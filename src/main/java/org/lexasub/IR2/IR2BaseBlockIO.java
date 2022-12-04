package org.lexasub.IR2;

import org.lexasub.IR1.IR1Block.IR1BaseBlock;
import org.lexasub.IR1.IR1Block.IR1BaseBlockIO;

public class IR2BaseBlockIO {
    public static void dump(IR2BaseBlock ir2BaseBlock) {
        IR1BaseBlockIO.dump(ir2BaseBlock.ir1Block);
    }
}
