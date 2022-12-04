package org.lexasub.IR2;

import org.lexasub.IR1.IR1Block.IR1BaseBlock;
import org.lexasub.frontend.utils.FrontendBaseBlock;

import java.util.*;

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


    private ListIterator findAndGetIterator(LinkedList<IR1BaseBlock> nodesOutChilds, IR1BaseBlock ir1Block) {
        ListIterator<IR1BaseBlock> it = nodesOutChilds.listIterator();
        while(!Objects.equals(it.next(), ir1Block));
        return it;
    }

    /*private Object iteratePrevAndCheck(ListIterator i, IR1BaseBlock ir1Block) {
        while (i.hasPrevious()){
            //name or "res_baseblockid

        }
    }*/


    private IR1BaseBlock eeee(IR1BaseBlock parent, IR1BaseBlock child) {
        String text;
        if(parent.type == FrontendBaseBlock.TYPE.ID) {//variable
            text = parent.name;
            //else == block || code
            //TODO if each from nondesO is only reader - then no phi func
            //also check parent (may ~ while(set(a, add(a,1))) )
            //nodesO.stream().map(i->i.blockId) -> phi res, name0 bid0, name1 bid1, ..
            List<String> blocks = new ArrayList<>(parent.nodesOut.stream().map(i -> i.blockId).toList());
            for(ListIterator<String> it = blocks.listIterator(); it.hasNext();){
                int id = it.nextIndex();
                it.next();
                blocks.set(id, text  + "_" + id + " " + blocks.get(id));
            }
            for(int id = 0; id<parent.nodesOut.size(); ++id)
                if(parent.nodesOut.contains(child)){
                    System.out.println(parent.nodesOut.size());

                    IR1BaseBlock blockScope = new IR1BaseBlock(FrontendBaseBlock.TYPE.BLOCK);
                    IR1BaseBlock phiBlock = new IR1BaseBlock(FrontendBaseBlock.TYPE.CODE);
                    String newReplacement = " " + text + "_" + id + " ";
                    phiBlock.code = "phi " + newReplacement + blocks.stream().map(i -> " , " + i).reduce("", String::concat);
                    blockScope.blockId = child.blockId + "_scope";
                    //remove link parent , child
                    if(child.type != FrontendBaseBlock.TYPE.CODE)
                        System.out.println("bad");
                    child.code = child.code.replaceAll(" " + text + " ", newReplacement);//smart replace(with spaces)
                    parent.nodesOut.remove(child);
                    child.nodesIn.remove(parent);
                    //child.nodesInParents -> blockScope.nodesInParents
                    moveParentToOtherBlock(child, blockScope);
                    moveDependenceToOtherBlock(child, blockScope);

                    IR1BaseBlock.connectTo(child,blockScope);//connectToChilds
                    IR1BaseBlock.connectTo(phiBlock,blockScope);//connectToChilds
                    IR1BaseBlock.connectTo(blockScope, parent);
               }
         //  IR1BaseBlockIO.dump(ir1Block);
        }
        else
            text = "res_" + parent.blockId;//пока тут ниче не делаем
        return null;
    }

    private static void moveParentToOtherBlock(IR1BaseBlock child, IR1BaseBlock blockScope) {
        child.nodesInParents.forEach(i->{
            i.nodesOutChilds.add(i.nodesOutChilds.indexOf(child), blockScope);//relink,mov(copy stage)
            i.nodesOutChilds.remove(child);//rm stage
            blockScope.nodesInParents.add(i);// а тут порядок вроде не важен))
        });
        child.nodesInParents.clear();//rm stage
    }
    private static void moveDependenceToOtherBlock(IR1BaseBlock child, IR1BaseBlock blockScope) {
        child.nodesIn.forEach(i->{
            i.nodesOut.add(i.nodesOut.indexOf(child), blockScope);//relink,mov(copy stage)
            i.nodesOut.remove(child);//rm stage
            blockScope.nodesIn.add(i);// а тут порядок вроде не важен))
        });
        child.nodesIn.clear();//rm stage
    }

    private void eee(IR1BaseBlock ir1Block) {
       // Stream<ListIterator> its = ir1Block.nodesInParents.stream().map(i -> findAndGetIterator(i.nodesOutChilds, ir1Block));//get iterators to me from parents
       // its.map(i -> iteratePrevAndCheck(i, ir1Block));
        //ir1Block.nodesIn;//if no one - it's strange
        if(ir1Block.type != FrontendBaseBlock.TYPE.CODE) {
            ir1Block.nodesOutChilds.forEach(this::eee);
            return;
        }
        //генерация для каждой переменной фи функций??
        for (int id = 0; id < ir1Block.nodesIn.size(); ++id) {
            eeee(ir1Block.nodesIn.get(0), ir1Block);
        }
    }
    private void ifConvert(IR1BaseBlock ir1Block, LinkedList<IR1BaseBlock> visitedBlocks) {
        eee(ir1Block);
        Iterator<IR1BaseBlock> it = ir1Block.nodesOutChilds.iterator();
        IR1BaseBlock cond = it.next();
        doJob(cond, visitedBlocks);
        IR1BaseBlock trueExpr = it.next();
        doJob(trueExpr, visitedBlocks);
      //  Object trueDeps = findDependences(trueExpr); //1)найти зависимости переменных в trueExpr от переменных декларированных раньше
        IR1BaseBlock falseExpr = null;
        if(it.hasNext()){
            falseExpr = it.next();
            doJob(falseExpr, visitedBlocks);
        }
      //  Object falseDeps = findDependences(falseExpr);//2)найти зависимости переменных в falseExpr от переменных декларированных раньше
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
