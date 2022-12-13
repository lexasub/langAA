package org.lexasub.IR2.IR2Block;

import org.lexasub.IR1.IR1Block.IR1BaseBlock;
import org.lexasub.frontend.utils.FrontendBaseBlock;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Objects;

import static org.lexasub.IR1.IR1Block.IR1BaseBlock.connectTo;
import static org.lexasub.IR1.IR1Block.IR1BaseBlock.connectToChilds;

public class IR2BaseBlock {
    public IR1BaseBlock block;

    public IR2BaseBlock(IR1BaseBlock block) {
        this.block = block;
    }

    private static void relinkNodeIn(IR1BaseBlock ir1Block, IR1BaseBlock ifScope) {
        ifScope.nodesIn.forEach(i -> {
            ListIterator<IR1BaseBlock> il = i.nodesOutListIterator();
            while (il.hasNext())
                if (il.next() == ir1Block) {
                    il.previous();
                    il.set(ifScope);
                }
        });
        ifScope.nodesInParents.forEach(i -> {
            ListIterator<IR1BaseBlock> il = i.nodesOutChildsListIterator();
            while (il.hasNext())
                if (il.next() == ir1Block) {
                    il.previous();
                    il.set(ifScope);
                }
        });
    }

    private static void ifPart(IR1BaseBlock cond, IR1BaseBlock trueExpr, IR1BaseBlock ir1Block, IR1BaseBlock ifScope) {
        IR1BaseBlock jmp1 = new IR1BaseBlock(FrontendBaseBlock.TYPE.JMP);
        IR1BaseBlock condJmp = new IR1BaseBlock(FrontendBaseBlock.TYPE.COND_JMP);
        IR1BaseBlock trueScope = new IR1BaseBlock();
        IR1BaseBlock condScope = new IR1BaseBlock();
        connectToChilds(cond, condScope);
        //condJmp.add(cond_res, trueExpr, falseExpr(or endIf))
        connectToChilds(condJmp, condScope);
        connectToChilds(trueExpr, trueScope);
        connectToChilds(jmp1, trueScope);//jmp to ...
        ifScope.copyNodesInsFrom(ir1Block);
        connectToChilds(condScope, ifScope);
        connectToChilds(trueScope, ifScope);
    }

    public void doJob() {//root is not id, if
        IR2Walkers.removeTransitBlocksJob(block, new LinkedList<>());
        introducePhisJob(block, new LinkedList<>());
        IR2Walkers.removeTransitBlocksJob(block, new LinkedList<>());
    }

    private void introducePhisJob(IR1BaseBlock block, LinkedList<IR1BaseBlock> visitedBlocks) {
        block.nodesOut.forEach(i -> replaceVarsWith(block, i, visitedBlocks));
        block.nodesOutChilds.forEach(i -> replaceVarsWith(block, i, visitedBlocks));
    }

    private void replaceVarsWith(IR1BaseBlock parent, IR1BaseBlock block, LinkedList<IR1BaseBlock> visitedBlocks) {
        if (visitedBlocks.contains(block)) return;
        visitedBlocks.add(block);
        if (checkType(parent, block, visitedBlocks)) return;
        introducePhisJob(block, visitedBlocks);
    }
    private boolean checkType(IR1BaseBlock parent, IR1BaseBlock block, LinkedList<IR1BaseBlock> visitedBlocks) {
        if (block.typeIs(FrontendBaseBlock.TYPE.ID) && block.hasntDeps()) {//maybe TODO check ID && hasntDeps
           /* switch (block.name){
                case "call":
                case "set":
                    block.nodesOut.forEach(i->replaceVarsWith(i, visitedBlocks));
                    block.nodesOutChilds.forEach(i->replaceVarsWith(i, visitedBlocks));
                    return;
            }*/
            IR1BaseBlock phiPart = new IR1BaseBlock(FrontendBaseBlock.TYPE.PHI_PART);
            ListIterator<IR1BaseBlock> it = block.nodesInParents.listIterator();
            while (it.hasNext()) {
                int id = it.nextIndex();
                IR1BaseBlock ir1BB = it.next();
                if(!Objects.equals(ir1BB, parent))
                    replaceVarArg(block, id, ir1BB, phiPart);
            }
            return true;
        }
        if (block.typeIs(FrontendBaseBlock.TYPE.IF)) {
            ifConvert(block, visitedBlocks);
        }
        return false;
    }

    private void replaceVarArg(IR1BaseBlock idNode, int id, IR1BaseBlock ch, IR1BaseBlock phiPart) {
        //id - it's number of phi reg
        IR1BaseBlock phi = new IR1BaseBlock(FrontendBaseBlock.TYPE.PHI, idNode.name + "_" + id);

        ch.nodesOutChilds.set(ch.nodesOutChilds.indexOf(idNode), phi);
        phi.nodesInParents.add(ch);
        connectTo(phiPart, ch);

        idNode.nodesInParents.set(id, phi);
        phi.nodesOutChilds.add(idNode);
        /*idNode.nodesInParents.remove(id);
        connectTo(phi, idNode);*/
        connectTo(phiPart, phi);

    }

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

    private void ifConvert(IR1BaseBlock ir1Block, LinkedList<IR1BaseBlock> visitedBlocks) {
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
        ifPart(cond, trueExpr, ir1Block, ifScope);
        if (falseExpr != null) {
            ifPartFalseExpr(falseExpr, ifScope);
        }
        relinkNodeIn(ir1Block, ifScope);
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
        IR1BaseBlock jmp2 = new IR1BaseBlock(FrontendBaseBlock.TYPE.JMP);
        IR1BaseBlock falseScope = new IR1BaseBlock();
        connectToChilds(falseExpr, falseScope);
        connectToChilds(jmp2, falseScope);//jmp to ...
        connectToChilds(falseScope, ifScope);
    }
}
