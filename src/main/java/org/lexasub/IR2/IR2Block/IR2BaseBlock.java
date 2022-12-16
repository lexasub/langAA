package org.lexasub.IR2.IR2Block;

import org.lexasub.IR1.IR1Block.IR1BaseBlock;
import org.lexasub.IR2.IR2Block.Parts.IfConvert;
import org.lexasub.frontend.utils.FrontendBaseBlock.TYPE;

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


    public void doJob() {//root is not id, if
        IR2Walkers.removeTransitBlocksJob(block, new LinkedList<>());
        introducePhisJob(block, new LinkedList<>());
        IR2Walkers.removeTransitBlocksJob(block, new LinkedList<>());
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
        if (block.typeIs(TYPE.ID) && block.hasntDeps()) {//maybe TODO check ID && hasntDeps
           /* switch (block.name){
                case "call":
                case "set":
                    block.nodesOut.forEach(i->replaceVarsWith(i, visitedBlocks));
                    block.nodesOutChilds.forEach(i->replaceVarsWith(i, visitedBlocks));
                    return;
            }*/
            IR1BaseBlock phiPart = new IR1BaseBlock(TYPE.PHI_PART);
            ListIterator<IR1BaseBlock> it = block.nodesInParents.listIterator();
            while (it.hasNext()) {
                int id = it.nextIndex();
                IR1BaseBlock ir1BB = it.next();
                if(!Objects.equals(ir1BB, parent))
                    replaceVarArg(block, id, ir1BB, phiPart);
            }
            return true;
        }
        if (block.typeIs(TYPE.IF)) {
            relinkNodeIn(block, IfConvert.ifConvert(block, visitedBlocks));
        }
        return false;
    }

    private void replaceVarArg(IR1BaseBlock idNode, int id, IR1BaseBlock ch, IR1BaseBlock phiPart) {
        //id - it's number of phi reg
        IR1BaseBlock phi = new IR1BaseBlock(TYPE.PHI, idNode.name + "_" + id);

        ch.nodesOutChilds.set(ch.nodesOutChilds.indexOf(idNode), phi);
        phi.nodesInParents.add(ch);
        connectTo(phiPart, ch);

        idNode.nodesInParents.set(id, phi);
        phi.nodesOutChilds.add(idNode);
        /*idNode.nodesInParents.remove(id);
        connectTo(phi, idNode);*/
        connectTo(phiPart, phi);

    }


}
