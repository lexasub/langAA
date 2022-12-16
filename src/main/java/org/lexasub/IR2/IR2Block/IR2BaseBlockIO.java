package org.lexasub.IR2.IR2Block;

import org.lexasub.IR1.IR1Block.IR1BaseBlock;
import org.lexasub.utils.IR1BaseBlockIO;

public class IR2BaseBlockIO {
    public static void dump(IR2BaseBlock ir2BaseBlock) {
        IR1BaseBlock newBlock = ir2BaseBlock.block.nodesOutChilds.get(0);
        IR1BaseBlockIO.dump(newBlock, true, true);
    }
}
