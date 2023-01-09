package org.lexasub.IR2.IR2.Parts;

import org.lexasub.IR1.IR1;
import org.lexasub.frontend.utils.FBB;

import java.util.Iterator;
import java.util.LinkedList;

import static org.lexasub.IR1.IR1.connectDependence;
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
        ifScope.copyNodesInsFrom(ir1Block);
        ifCondPart(cond, ifScope, condJmp);
        IR1 endIf = ifScope.after();
        ifJumper(trueExpr, ifScope, condJmp, endIf);
        if (falseExpr != null) //check exist of else(falseExpr)
            ifJumper(falseExpr, ifScope, condJmp, endIf);
        else connectDependence(endIf, condJmp);
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

    private static void ifCondPart(IR1 cond, IR1 ifScope, IR1 condJmp) {
        IR1 condScope = new IR1();
        //condJmp.add(cond_res//TODO
        connectToChilds(cond, condScope);
        connectToChilds(condJmp, condScope);//jump - is last element of block
        connectToChilds(condScope, ifScope);
    }

    private static void ifJumper(IR1 body, IR1 ifScope, IR1 condJmp, IR1 endIf) {
        IR1 jmp = new IR1(FBB.TYPE.JMP);
        connectDependence(jmp, endIf);//jump to ...
        IR1 localScope = new IR1();
        //body
        connectDependence(body, condJmp);
        connectToChilds(body, localScope);
        connectToChilds(jmp, localScope);//add jump code//jump - is last of block
        connectToChilds(localScope, ifScope);
    }

}
