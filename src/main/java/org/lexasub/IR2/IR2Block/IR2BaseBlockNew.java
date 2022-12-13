package org.lexasub.IR2.IR2Block;

import org.lexasub.IR1.IR1Block.IR1BaseBlock;
import org.lexasub.frontend.utils.FrontendBaseBlock;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;

import static org.lexasub.IR1.IR1Block.IR1BaseBlock.connectTo;
import static org.lexasub.IR1.IR1Block.IR1BaseBlock.connectToChilds;

public class IR2BaseBlockNew {
    public IR1BaseBlock block;

    public IR2BaseBlockNew(IR1BaseBlock block) {
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

    public void doJob() {
        LinkedList<IR1BaseBlock> visitedBlocks = new LinkedList<>();
        replaceVarsWith(block, visitedBlocks);
    }

    private void replaceVarsWith(IR1BaseBlock block, LinkedList<IR1BaseBlock> visitedBlocks) {
        if (visitedBlocks.contains(block)) return;
        visitedBlocks.add(block);
        if (block.typeIs(FrontendBaseBlock.TYPE.ID) && block.nodesIn.size() == 0) {//maybe TODO check ID && size=0
           /* switch (block.name){
                case "call":
                case "set":
                    block.nodesOut.forEach(i->replaceVarsWith(i, visitedBlocks));
                    block.nodesOutChilds.forEach(i->replaceVarsWith(i, visitedBlocks));
                    return;
            }*/
            IR1BaseBlock phiPart = new IR1BaseBlock(FrontendBaseBlock.TYPE.PHI_PART);
            ListIterator<IR1BaseBlock> it = block.nodesOutListIterator();
            while (it.hasNext()) {
                int id = it.nextIndex();
                replaceVarArg(block, id, it.next(), phiPart);
            }
            //  block.nodesOut.forEach(i->i.nodesOut.add(0, phiPart));
            return;
        } else if (block.typeIs(FrontendBaseBlock.TYPE.IF)) {
            ifConvert(block, visitedBlocks);
        }
        block.nodesOut.forEach(i -> replaceVarsWith(i, visitedBlocks));
        block.nodesOutChilds.forEach(i -> replaceVarsWith(i, visitedBlocks));
    }

    private void replaceVarArg(IR1BaseBlock parent, int id, IR1BaseBlock ch, IR1BaseBlock phiPart) {
        IR1BaseBlock phi = new IR1BaseBlock(FrontendBaseBlock.TYPE.PHI, parent.name + "_" + id);
       /* ch.nodesIn.set(ch.nodesIn.indexOf(parent), phi);//mb change to nodesInParent??
        phi.nodesOut.add(ch);*/
        ch.nodesIn.set(ch.nodesIn.indexOf(parent), phi);
        ch.nodesIn.remove(parent);

        connectToChilds(phi, ch);

        parent.nodesOut.set(id, phi);

        connectTo(phiPart, phi);
        connectTo(phiPart, ch);
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
        // replaceVarArg(ir1Block);
        //return;
        Iterator<IR1BaseBlock> it = ir1Block.nodesOutChildsListIterator();
        IR1BaseBlock cond = it.next();
        //  replaceVarsWith(cond, visitedBlocks);
        IR1BaseBlock trueExpr = it.next();
        //   replaceVarsWith(trueExpr, visitedBlocks);
        //  Object trueDeps = findDependences(trueExpr); //1)найти зависимости переменных в trueExpr от переменных декларированных раньше
        IR1BaseBlock falseExpr = null;
        if (it.hasNext()) {
            falseExpr = it.next();
            //  doJob(falseExpr, visitedBlocks);
        }
        //  Object falseDeps = findDependences(falseExpr);//2)найти зависимости переменных в falseExpr от переменных декларированных раньше
        //3)сгенерить phi-функции после выполнения if(ну и новых переменных создать для phi)
        IR1BaseBlock ifScope = new IR1BaseBlock();
        ifPart(cond, trueExpr, ir1Block, ifScope);

        IR1BaseBlock falseScope = new IR1BaseBlock();
        IR1BaseBlock jmp2 = new IR1BaseBlock(FrontendBaseBlock.TYPE.JMP);
        if (falseExpr != null) {
            connectToChilds(falseExpr, falseScope);
            connectToChilds(jmp2, falseScope);//jmp to ...
            connectToChilds(falseScope, ifScope);
        }
        relinkNodeIn(ir1Block, ifScope);
        //TODO ifScope->ir1Block
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
