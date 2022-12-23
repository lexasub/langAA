package org.lexasub.IR2.IR2;

import org.lexasub.IR1.IR1;
import org.lexasub.IR2.IR2.Parts.IfConvert;
import org.lexasub.frontend.utils.FBB.TYPE;

import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Objects;

import static org.lexasub.IR1.IR1.connectTo;

public class IR2 {
    public IR1 block;

    public IR2(IR1 block) {
        this.block = block;
    }


    public static IR1 doJob(IR1 block) {//root is not id, if
        IR2Walkers.removeTransitBlocksJob(block, new LinkedList<>());
        introducePhisJob(block, new LinkedList<>());
        IR2Walkers.removeTransitBlocksJob(block, new LinkedList<>());
        return block;
    }

    private static void relinkNodeIn(IR1 ir1Block, IR1 ifScope) {
        ifScope.nodesIn.forEach(i -> {
            ListIterator<IR1> il = i.nodesOutListIterator();
            while (il.hasNext())
                if (il.next() == ir1Block) {
                    il.previous();
                    il.set(ifScope);
                }
        });
        ifScope.nodesInParents.forEach(i -> {
            ListIterator<IR1> il = i.nodesOutChildsListIterator();
            while (il.hasNext())
                if (il.next() == ir1Block) {
                    il.previous();
                    il.set(ifScope);
                }
        });
    }

    private static void introducePhisJob(IR1 block, LinkedList<IR1> visitedBlocks) {
        block.nodesOut.forEach(i -> replaceVarsWith(block, i, visitedBlocks));
        block.nodesOutChilds.forEach(i -> replaceVarsWith(block, i, visitedBlocks));
    }

    private static void replaceVarsWith(IR1 parent, IR1 block, LinkedList<IR1> visitedBlocks) {
        if (visitedBlocks.contains(block)) return;
        visitedBlocks.add(block);
        if (!checkType(parent, block, visitedBlocks)) introducePhisJob(block, visitedBlocks);
    }

    private static boolean checkType(IR1 parent, IR1 block, LinkedList<IR1> visitedBlocks) {
        if (block.typeIs(TYPE.ID) && block.hasntDeps()) {//maybe TODO check ID && hasntDeps
           /* switch (block.name){
                case "call":
                case "set":
                    block.nodesOut.forEach(i->replaceVarsWith(i, visitedBlocks));
                    block.nodesOutChilds.forEach(i->replaceVarsWith(i, visitedBlocks));
                    return;
            }*/
            IR1 phiPart = new IR1(TYPE.PHI_PART);
            ListIterator<IR1> it = block.nodesInParents.listIterator();
            while (it.hasNext()) {
                int id = it.nextIndex();
                IR1 ir1BB = it.next();
                if (!Objects.equals(ir1BB, parent))
                    replaceVarArg(block, id, ir1BB, phiPart);
            }
            return true;
        }
        if (block.typeIs(TYPE.IF)) {
            relinkNodeIn(block, IfConvert.ifConvert(block, visitedBlocks));
        }
        return false;
    }

    private static void replaceVarArg(IR1 idNode, int id, IR1 ch, IR1 phiPart) {
        //id - it's number of phi reg
        IR1 phi = new IR1(TYPE.PHI, idNode.name + "_" + id);

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
