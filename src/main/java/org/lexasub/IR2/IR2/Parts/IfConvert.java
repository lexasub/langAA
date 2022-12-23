package org.lexasub.IR2.IR2.Parts;

import org.lexasub.IR1.IR1;
import org.lexasub.frontend.utils.FBB;

import java.util.Iterator;
import java.util.LinkedList;

import static org.lexasub.IR1.IR1.connectTo;
import static org.lexasub.IR1.IR1.connectToChilds;

public class IfConvert {
    public static IR1 ifConvert(IR1 ir1Block, LinkedList<IR1> visitedBlocks) {
        Iterator<IR1> it = ir1Block.nodesOutChildsListIterator();
        IR1 cond = it.next();
        cond.type = FBB.TYPE.BLOCK;
        IR1 trueExpr = it.next();
        trueExpr.type = FBB.TYPE.BLOCK;
        //1)найти зависимости переменных в trueExpr от переменных декларированных раньше
        IR1 falseExpr = null;
        if (it.hasNext()) {
            falseExpr = it.next();
            falseExpr.type = FBB.TYPE.BLOCK;
        }
        //2)найти зависимости переменных в falseExpr от переменных декларированных раньше
        //3)сгенерить phi-функции после выполнения if(ну и новых переменных создать для phi)
        IR1 ifScope = new IR1();
        IR1 condJmp = new IR1(FBB.TYPE.COND_JMP);
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

    private static void ifPartFalseExpr(IR1 falseExpr, IR1 ifScope) {
        IR1 jmp = new IR1(FBB.TYPE.JMP);
        IR1 falseScope = new IR1();
        connectToChilds(falseExpr, falseScope);
        connectToChilds(jmp, falseScope);//jmp to ...
        connectTo(jmp, ifScope.after());//jmp to ...
        connectToChilds(falseScope, ifScope);
    }

    private static void ifPart(IR1 cond, IR1 trueExpr, IR1 ir1Block, IR1 ifScope, IR1 condJmp) {
        IR1 jmp = new IR1(FBB.TYPE.JMP);
        IR1 trueScope = new IR1();
        IR1 condScope = new IR1();
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
