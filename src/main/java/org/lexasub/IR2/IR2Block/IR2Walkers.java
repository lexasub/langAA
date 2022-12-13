package org.lexasub.IR2.IR2Block;

import org.lexasub.IR1.IR1Block.IR1BaseBlock;
import org.lexasub.frontend.utils.FrontendBaseBlock;

import java.util.LinkedList;

public class IR2Walkers {

    public static void removeTransitBlocksJob(IR1BaseBlock block, LinkedList<IR1BaseBlock> visitedBlocks) {
        block.nodesOut.forEach(i -> removeTransitBlocksWrapper(block, i, visitedBlocks));
        block.nodesOutChilds.forEach(i -> removeTransitBlocksWrapper(block, i, visitedBlocks));
    }

    private static void removeTransitBlocksWrapper(IR1BaseBlock parent, IR1BaseBlock child, LinkedList<IR1BaseBlock> visitedBlocks) {
        if (visitedBlocks.contains(child)) return;
        visitedBlocks.add(child);
        if(parent.nodesOutChilds.contains(child) && child.hasntDeps() && child.nodesOut.isEmpty())
            removeTransitBlocks(parent, child);
        child.nodesOut.forEach(i -> removeTransitBlocksWrapper(child, i, visitedBlocks));
        child.nodesOutChilds.forEach(i -> removeTransitBlocksWrapper(child, i, visitedBlocks));
    }

    private static void removeTransitBlocks(IR1BaseBlock parent, IR1BaseBlock child) {
        if(!child.typeIs(FrontendBaseBlock.TYPE.BLOCK)) return;
        if(child.nodesOutChilds.size() == 1 && child.nodesInParents.size() == 1){//then it's transit block
            IR1BaseBlock nextChild = child.nodesOutChilds.get(0);
            parent.nodesOutChilds.set(parent.nodesOutChilds.indexOf(child), nextChild);
            nextChild.nodesInParents.set(nextChild.nodesInParents.indexOf(child), parent);
        }
    }
}
