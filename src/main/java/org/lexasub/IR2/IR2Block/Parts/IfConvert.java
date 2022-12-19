package org.lexasub.IR2.IR2Block.Parts;

import org.lexasub.IR1.IR1Block.IR1BaseBlock;
import org.lexasub.frontend.utils.FrontendBaseBlock;

import java.util.Iterator;
import java.util.LinkedList;

import static org.lexasub.IR1.IR1Block.IR1BaseBlock.connectTo;
import static org.lexasub.IR1.IR1Block.IR1BaseBlock.connectToChilds;

public class IfConvert {
    public static IR1BaseBlock ifConvert(IR1BaseBlock ir1Block, LinkedList<IR1BaseBlock> visitedBlocks) {
        Iterator<IR1BaseBlock> it = ir1Block.nodesOutChildsListIterator();
        IR1BaseBlock cond = it.next();
        cond.type = FrontendBaseBlock.TYPE.BLOCK;
        IR1BaseBlock trueExpr = it.next();
        trueExpr.type = FrontendBaseBlock.TYPE.BLOCK;
        //1)найти зависимости переменных в trueExpr от переменных декларированных раньше
        IR1BaseBlock falseExpr = null;
        if (it.hasNext()) {
            falseExpr = it.next();
            falseExpr.type = FrontendBaseBlock.TYPE.BLOCK;
        }
        //2)найти зависимости переменных в falseExpr от переменных декларированных раньше
        //3)сгенерить phi-функции после выполнения if(ну и новых переменных создать для phi)
        IR1BaseBlock ifScope = new IR1BaseBlock();
        IR1BaseBlock condJmp = new IR1BaseBlock(FrontendBaseBlock.TYPE.COND_JMP);
        ifPart(cond, trueExpr, ir1Block, ifScope, condJmp);

        //; , falseExpr(or endIf))on outer of func
        if (falseExpr != null) {
            connectTo(falseExpr, condJmp);
            ifPartFalseExpr(falseExpr, ifScope);
        } else connectTo(ifScope.after(), condJmp);
        return ifScope;
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

    private static void ifPartFalseExpr(IR1BaseBlock falseExpr, IR1BaseBlock ifScope) {
        IR1BaseBlock jmp = new IR1BaseBlock(FrontendBaseBlock.TYPE.JMP);
        IR1BaseBlock falseScope = new IR1BaseBlock();
        connectToChilds(falseExpr, falseScope);
        connectToChilds(jmp, falseScope);//jmp to ...
        connectTo(jmp, ifScope.after());//jmp to ...
        connectToChilds(falseScope, ifScope);
    }

    private static void ifPart(IR1BaseBlock cond, IR1BaseBlock trueExpr, IR1BaseBlock ir1Block, IR1BaseBlock ifScope, IR1BaseBlock condJmp) {
        IR1BaseBlock jmp = new IR1BaseBlock(FrontendBaseBlock.TYPE.JMP);
        IR1BaseBlock trueScope = new IR1BaseBlock();
        IR1BaseBlock condScope = new IR1BaseBlock();
        connectToChilds(cond, condScope);
        //condJmp.add(cond_res//TODO
        //trueExpr
        connectTo(trueExpr, condJmp);
        //; , falseExpr(or endIf))on outer of func
        connectToChilds(condJmp, condScope);
        connectToChilds(trueExpr, trueScope);
        connectToChilds(jmp, trueScope);//jmp to ...
        connectTo(jmp, ifScope.after());//jmp to ...
        ifScope.copyNodesInsFrom(ir1Block);
        connectToChilds(condScope, ifScope);
        connectToChilds(trueScope, ifScope);
    }
}
