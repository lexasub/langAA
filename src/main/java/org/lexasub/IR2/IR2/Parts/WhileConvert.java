package org.lexasub.IR2.IR2.Parts;

import org.lexasub.IR1.IR1;
import org.lexasub.frontend.utils.FBB;

import java.util.Iterator;
import java.util.LinkedList;

import static org.lexasub.IR1.IR1.connectToChilds;

public class WhileConvert {
    private void whileConvert(IR1 ir1Block, LinkedList<IR1> visitedBlocks) {
        Iterator<IR1> it = ir1Block.nodesOutChildsListIterator();
        IR1 cond = it.next();
        //doJob(cond, visitedBlocks);
        IR1 expr = it.next();
        //doJob(expr, visitedBlocks);
        //1)найти зависимости переменных в cond от переменных в expr
        //2)найти зависимости переменных в expr от переменных декларированных раньше
        //3)вставить phi функции в начала cond, expr и после while
        IR1 whileScope = new IR1();
        IR1 condScope = new IR1();
        IR1 exprScope = new IR1();
        IR1 condJmp = new IR1(FBB.TYPE.COND_JMP);
        IR1 jmp = new IR1(FBB.TYPE.JMP);
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
