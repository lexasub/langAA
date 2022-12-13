package org.lexasub.IR2.IR2Block;

import org.lexasub.IR1.IR1Block.IR1BaseBlock;
import org.lexasub.frontend.utils.FrontendBaseBlock;

import java.util.*;

import static org.lexasub.IR1.IR1Block.IR1BaseBlock.connectTo;
import static org.lexasub.IR1.IR1Block.IR1BaseBlock.connectToChilds;

public class IR2BaseBlock {
    IR1BaseBlock ir1Block;

    public IR2BaseBlock(IR1BaseBlock newBlock) {
        ir1Block = newBlock;
    }

    private static void introducePhis(IR1BaseBlock parent, IR1BaseBlock _child, String text) {
        LinkedList<Map.Entry<IR1BaseBlock, IR1BaseBlock>> blockList = getPhiDependences(parent, text);
        for (int id = 0; id < parent.nodesOut.size(); ++id) {
            IR1BaseBlock child = parent.nodesOut.get(0);
            //if (parent.nodesOut.contains(child)) {
            System.out.println(child.blockId);

            IR1BaseBlock blockScope = new IR1BaseBlock(FrontendBaseBlock.TYPE.BLOCK);
            if (child.nodesInParents.size() != 1)
                System.out.println("bad " + child.nodesInParents.size() + " " + child.blockId);
            blockScope.nodesInParents.add(child.nodesInParents.get(0));
            ListIterator<IR1BaseBlock> it = child.nodesInParents.get(0).nodesOutChilds.listIterator();
            while (it.next() != child) ;
            it.previous();
            it.set(blockScope);
            child.nodesInParents.remove(0);
            IR1BaseBlock phiBlock = new IR1BaseBlock(FrontendBaseBlock.TYPE.PHI);
            IR1BaseBlock phiTo = new IR1BaseBlock(FrontendBaseBlock.TYPE.ID);//change to find
            phiTo.name = text + "_" + id;
            Map.Entry<IR1BaseBlock, IR1BaseBlock> blockEntry = blockList.get(id);
            connectToChilds(blockEntry.getKey(), phiBlock);
            connectToChilds(phiTo, phiBlock);
            connectToChilds(blockEntry.getValue(), blockScope);

            blockScope.blockId = child.blockId + "_scope";
            //remove link parent , child
            if (child.type != FrontendBaseBlock.TYPE.CODE)
                System.out.println("bad");
            //TODO   child.code = child.code.replaceAll(" " + text + " ", newReplacement);//smart replace(with spaces)
            parent.nodesOut.remove(child);
            child.nodesIn.remove(parent);
            connectToChilds(phiTo, child);
            //child.nodesInParents -> blockScope.nodesInParents
            //moveParentToOtherBlock(child, blockScope);
            //  moveDependenceToOtherBlock(child, blockScope);

            IR1BaseBlock.connectToChilds(phiBlock, blockScope);//connectToChilds
            IR1BaseBlock.connectToChilds(child, blockScope);//connectToChilds
            IR1BaseBlock.connectToChilds(blockScope, parent);
            //  }
        }
    }

    private static void moveParentToOtherBlock(IR1BaseBlock child, IR1BaseBlock blockScope) {
        child.nodesInParents.forEach(i -> {
            i.nodesOutChilds.add(i.nodesOutChilds.indexOf(child), blockScope);//relink,mov(copy stage)
            i.nodesOutChilds.remove(child);//rm stage
            blockScope.nodesInParents.add(i);// а тут порядок вроде не важен))
        });
        child.nodesInParents.clear();//rm stage
    }

    private static void moveDependenceToOtherBlock(IR1BaseBlock child, IR1BaseBlock blockScope) {
        child.nodesIn.forEach(i -> {
            i.nodesOut.add(i.nodesOut.indexOf(child), blockScope);//relink,mov(copy stage)
            i.nodesOut.remove(child);//rm stage
            blockScope.nodesIn.add(i);// а тут порядок вроде не важен))
        });
        child.nodesIn.clear();//rm stage
    }

    private static LinkedList<Map.Entry<IR1BaseBlock, IR1BaseBlock>> getPhiDependences(IR1BaseBlock parent, String text) {
        LinkedList<Map.Entry<IR1BaseBlock, IR1BaseBlock>> phis = new LinkedList<>();
        for (ListIterator<IR1BaseBlock> it = parent.nodesOut.listIterator(); it.hasNext(); ) {
            String newVarName = text + "_" + it.nextIndex();
            IR1BaseBlock block = it.next();
            IR1BaseBlock newVar = new IR1BaseBlock(FrontendBaseBlock.TYPE.ID);
            newVar.name = newVarName;
            IR1BaseBlock phi = new IR1BaseBlock(FrontendBaseBlock.TYPE.PHI_PART);
            connectToChilds(newVar, phi);
            connectTo(block, phi);
            phis.add(Map.entry(phi, block));
        }
        return phis;
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
        ifScope.nodesIn = ir1Block.nodesIn;
        ifScope.nodesInParents = ir1Block.nodesInParents;
        connectToChilds(condScope, ifScope);
        connectToChilds(trueScope, ifScope);
    }

    private static void relinkNodeIn(IR1BaseBlock ir1Block, IR1BaseBlock ifScope) {
        ifScope.nodesIn.forEach(i -> {
            ListIterator<IR1BaseBlock> il = i.nodesOut.listIterator();
            while (il.hasNext())
                if (il.next() == ir1Block) {
                    il.previous();
                    il.set(ifScope);
                }
        });
        ifScope.nodesInParents.forEach(i -> {
            ListIterator<IR1BaseBlock> il = i.nodesOutChilds.listIterator();
            while (il.hasNext())
                if (il.next() == ir1Block) {
                    il.previous();
                    il.set(ifScope);
                }
        });
    }

    public void doJob() {
        doJob(ir1Block, new LinkedList<>());
    }

    private void doJob(IR1BaseBlock ir1Block, LinkedList<IR1BaseBlock> visitedBlocks) {
        if (visitedBlocks.contains(ir1Block)) return;
        visitedBlocks.add(ir1Block);
        if (ir1Block.type == FrontendBaseBlock.TYPE.IF) {
            ifConvert(ir1Block, visitedBlocks);
            return;
        }
        if (ir1Block.type == FrontendBaseBlock.TYPE.WHILE) {
            whileConvert(ir1Block, visitedBlocks);
            return;
        }
        ir1Block.nodesOut.forEach(i -> doJob(i, visitedBlocks));
        ir1Block.nodesOutChilds.forEach(i -> doJob(i, visitedBlocks));
    }

    private void whileConvert(IR1BaseBlock ir1Block, LinkedList<IR1BaseBlock> visitedBlocks) {
        Iterator<IR1BaseBlock> it = ir1Block.nodesOutChilds.iterator();
        IR1BaseBlock cond = it.next();
        doJob(cond, visitedBlocks);
        IR1BaseBlock expr = it.next();
        doJob(expr, visitedBlocks);
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

    private void eee(IR1BaseBlock ir1Block) {
        // Stream<ListIterator> its = ir1Block.nodesInParents.stream().map(i -> findAndGetIterator(i.nodesOutChilds, ir1Block));//get iterators to me from parents
        // its.map(i -> iteratePrevAndCheck(i, ir1Block));
        //ir1Block.nodesIn;//if no one - it's strange
        if (ir1Block.type != FrontendBaseBlock.TYPE.CODE) {
            ir1Block.nodesOutChilds.forEach(this::eee);
            return;
        }
        //генерация для каждой переменной фи функций??
        for (int id = 0; id < ir1Block.nodesIn.size(); ++id) {
            vvvv(ir1Block.nodesIn.get(0), ir1Block);
        }
    }

    private void vvvv(IR1BaseBlock parent, IR1BaseBlock child) {
        String text;
        if (parent.type == FrontendBaseBlock.TYPE.ID) {//variable
            text = parent.name;
            if (Objects.equals(text, "")) return;
            if (Objects.equals(text, "call")) return;
            if (Objects.equals(text, "ret")) return;
            //else == block || code
            //TODO if each from nondesO is only reader - then no phi func
            //also check parent (may ~ while(set(a, add(a,1))) )
            //nodesO.stream().map(i->i.blockId) -> phi res, name0 bid0, name1 bid1, ..
            introducePhis(parent, child, text);
        } else
            text = "res_" + parent.blockId;//пока тут ниче не делаем
    }

    private void ifConvert(IR1BaseBlock ir1Block, LinkedList<IR1BaseBlock> visitedBlocks) {
        eee(ir1Block);
        return;/*
        Iterator<IR1BaseBlock> it = ir1Block.nodesOutChilds.iterator();
        IR1BaseBlock cond = it.next();
        doJob(cond, visitedBlocks);
        IR1BaseBlock trueExpr = it.next();
        doJob(trueExpr, visitedBlocks);
        //  Object trueDeps = findDependences(trueExpr); //1)найти зависимости переменных в trueExpr от переменных декларированных раньше
        IR1BaseBlock falseExpr = null;
        if (it.hasNext()) {
            falseExpr = it.next();
            doJob(falseExpr, visitedBlocks);
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
