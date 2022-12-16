package org.lexasub.IR2.IR2Block.Parts;

import org.lexasub.IR1.IR1Block.IR1BaseBlock;
import org.lexasub.frontend.utils.FrontendBaseBlock;

import java.util.Iterator;
import java.util.LinkedList;

import static org.lexasub.IR1.IR1Block.IR1BaseBlock.connectToChilds;

public class WhileConvert {
    private void whileConvert(IR1BaseBlock ir1Block, LinkedList<IR1BaseBlock> visitedBlocks) {
        Iterator<IR1BaseBlock> it = ir1Block.nodesOutChildsListIterator();
        IR1BaseBlock cond = it.next();
        //doJob(cond, visitedBlocks);
        IR1BaseBlock expr = it.next();
        //doJob(expr, visitedBlocks);
        //1)найти зависимости переменных в cond от переменных в expr
        //2)найти зависимости переменных в expr от переменных декларированных раньше
        //3)вставить phi функции в начала cond, expr и после while
        IR1BaseBlock whileScope = new IR1BaseBlock();
        IR1BaseBlock condScope = new IR1BaseBlock();
        IR1BaseBlock exprScope = new IR1BaseBlock();
        IR1BaseBlock condJmp = new IR1BaseBlock(FrontendBaseBlock.TYPE.COND_JMP);
        IR1BaseBlock jmp = new IR1BaseBlock(FrontendBaseBlock.TYPE.JMP);
        //condScope.add(phi's)
        connectToChilds(cond, condScope);
        //condJmp.add(cond_res, end_while, begin_expr)
        connectToChilds(condJmp, condScope);
        //exprScope.add(phi's)
        connectToChilds(expr, exprScope);
        connectToChilds(jmp, exprScope);//jmp to ...
        connectToChilds(condScope, whileScope);
        connectToChilds(exprScope, whileScope);
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
}
