package org.lexasub.IR2.IR2.Parts;

import org.lexasub.IR1.IR1;
import org.lexasub.frontend.utils.FBB;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Optional;

import static org.lexasub.IR1.IR1.connectDependence;
import static org.lexasub.IR1.IR1.connectToChilds;

public class IfConvert {
    public static IR1 ifConvert(IR1 ir1Block, LinkedList<IR1> visitedBlocks) {
        Iterator<IR1> it = ir1Block.nodesOutChildsListIterator();
        IR1 ifScope = new IR1().copyNodesInsFrom(ir1Block);
        IR1 cond = it.next().setType(FBB.TYPE.BLOCK);
        IR1 trueExpr = it.next().setType(FBB.TYPE.BLOCK);
        Optional<IR1> falseExpr = Optional.of(it.hasNext() ? it.next().setType(FBB.TYPE.BLOCK) : null);
        //1)найти зависимости переменных в trueExpr от переменных декларированных раньше
        //2)найти зависимости переменных в falseExpr от переменных декларированных раньше
        //3)сгенерить phi-функции после выполнения if(ну и новых переменных создать для phi)
        IR1 condJmp = new IR1(FBB.TYPE.COND_JMP);
        ifCondConnect(cond, ifScope, condJmp);
        ifBodyConnect(trueExpr, falseExpr, condJmp, ifScope.after());
        return ifScope;
    }

    private static void ifCondConnect(IR1 cond, IR1 ifScope, IR1 condJmp) {//TODO?? get Res of cond and send to ifBodyConnect
        IR1 condScope = new IR1();
        //condJmp.add(cond_res//TODO
        connectToChilds(cond, condScope);
        connectToChilds(condJmp, condScope);//jump - is last element of block
        connectToChilds(condScope, ifScope);
    }

    private static void ifBodyConnect(IR1 trueExpr, Optional<IR1> falseExprO, IR1 condJmp, IR1 endIf) {
        ifJumper(trueExpr, condJmp, endIf);
        falseExprO.ifPresentOrElse(falseExpr->ifJumper(falseExpr, condJmp, endIf), ()->connectDependence(endIf, condJmp));
    }

    private static void ifJumper(IR1 body, IR1 condJmp, IR1 endIf) {
        IR1 jmp = new IR1(FBB.TYPE.JMP);
        connectDependence(jmp, endIf);//jump to ...
        IR1 localScope = new IR1();
        //body
        connectToChilds(body, localScope);
        connectToChilds(jmp, localScope);//add jump code//jump - is last of block
        connectToChilds(localScope, condJmp);
    }

}
