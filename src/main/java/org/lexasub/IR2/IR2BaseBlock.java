package org.lexasub.IR2;

import org.lexasub.IR1.IR1Block.IR1BaseBlock;
import org.lexasub.frontend.utils.FrontendBaseBlock;

import java.util.Iterator;
import java.util.LinkedList;

public class IR2BaseBlock {
    IR1BaseBlock ir1Block;
    public IR2BaseBlock(IR1BaseBlock newBlock) {
        ir1Block = newBlock;
    }


    public void doJob() {
       doJob(ir1Block, new LinkedList<>());
    }

    private void doJob(IR1BaseBlock ir1Block, LinkedList<IR1BaseBlock> visitedBlocks) {
        if(visitedBlocks.contains(ir1Block)) return;
        visitedBlocks.add(ir1Block);
        if(ir1Block.type == FrontendBaseBlock.TYPE.IF) {
            ifConvert(ir1Block, visitedBlocks);
            return;
        }
        if(ir1Block.type == FrontendBaseBlock.TYPE.WHILE) {
            whileConvert(ir1Block, visitedBlocks);
            return;
        }
        ir1Block.nodesOut.forEach(i->doJob(i, visitedBlocks));
        ir1Block.nodesOutChilds.forEach(i->doJob(i, visitedBlocks));
    }

    private void whileConvert(IR1BaseBlock ir1Block, LinkedList<IR1BaseBlock> visitedBlocks) {
        Iterator<IR1BaseBlock> it = ir1Block.nodesOutChilds.iterator();
        IR1BaseBlock cond = it.next();
        IR1BaseBlock expr = it.next();
        //1)найти зависимости переменных в cond от переменных в expr
        //2)найти зависимости переменных в expr от переменных декларированных раньше
        //3)вставить phi функции в начала cond, expr и после while
        IR1BaseBlock whileScope = new IR1BaseBlock();
        IR1BaseBlock condScope = new IR1BaseBlock();
        IR1BaseBlock exprScope = new IR1BaseBlock();
        IR1BaseBlock condJmp = new IR1BaseBlock(FrontendBaseBlock.TYPE.COND_JMP);
        IR1BaseBlock jmp = new IR1BaseBlock(FrontendBaseBlock.TYPE.JMP);
        //condScope.add(phi's)
        IR1BaseBlock.connectToChilds(condScope, cond);
        //condJmp.add(cond_res, end_while, begin_expr)
        IR1BaseBlock.connectToChilds(condScope, condJmp);
        //exprScope.add(phi's)
        IR1BaseBlock.connectToChilds(exprScope, expr);
        IR1BaseBlock.connectToChilds(exprScope, jmp);//jmp to ...
        IR1BaseBlock.connectToChilds(whileScope, condScope);
        IR1BaseBlock.connectToChilds(whileScope, exprScope);
        //end_while:
        //whileScope.add(phi's)
        /*
        phi's
        cond
        TYPE::JMP_COND -> end_while, begin_expr
        begin_expr:
        phi's
        expr
        TYPE::JMP -> cond
        end_while:
        phi's
         */
    }

    private void ifConvert(IR1BaseBlock ir1Block, LinkedList<IR1BaseBlock> visitedBlocks) {
        Iterator<IR1BaseBlock> it = ir1Block.nodesOutChilds.iterator();
        IR1BaseBlock cond = it.next();
        IR1BaseBlock trueExpr = it.next();
        //1)найти зависимости переменных в trueExpr от переменных декларированных раньше
        IR1BaseBlock falseExpr = null;
        if(it.hasNext()) falseExpr = it.next();
        //2)найти зависимости переменных в falseExpr от переменных декларированных раньше
        //3)сгенерить phi-функции после выполнения if(ну и новых переменных создать для phi)
        IR1BaseBlock condScope = new IR1BaseBlock();
        IR1BaseBlock ifScope = new IR1BaseBlock();
        IR1BaseBlock trueScope = new IR1BaseBlock();
        IR1BaseBlock falseScope = new IR1BaseBlock();
        IR1BaseBlock condJmp = new IR1BaseBlock(FrontendBaseBlock.TYPE.COND_JMP);
        IR1BaseBlock jmp1 = new IR1BaseBlock(FrontendBaseBlock.TYPE.JMP);
        IR1BaseBlock jmp2 = new IR1BaseBlock(FrontendBaseBlock.TYPE.JMP);
        IR1BaseBlock.connectToChilds(condScope, cond);
        //condJmp.add(cond_res, trueExpr, falseExpr(or endIf))
        IR1BaseBlock.connectToChilds(condScope, condJmp);
        IR1BaseBlock.connectToChilds(trueScope, trueExpr);
        IR1BaseBlock.connectToChilds(trueScope, jmp1);//jmp to ...
        IR1BaseBlock.connectToChilds(ifScope, condScope);
        IR1BaseBlock.connectToChilds(ifScope, trueScope);
        if(falseExpr != null) {
            IR1BaseBlock.connectToChilds(falseScope, trueExpr);
            IR1BaseBlock.connectToChilds(falseScope, jmp2);//jmp to ...
            IR1BaseBlock.connectToChilds(ifScope, falseScope);
        }
        //end_if:
        //ifScope.add(phi's)
        /*
        cond
        TYPE::JMP_COND -> trueExpr, falseExpr(or endIf)
        trueExpr
        TYPE::JMP -> endIf
        falseExpr
        TYPE::JMP -> endIf
        endIF:
        phi's
        */
    }
}
