package org.lexasub.IR2;


import org.lexasub.IR1.IR1Block.IR1BaseBlock;
import org.lexasub.IR2.IR2Block.IR2BaseBlock;
import org.lexasub.frontend.utils.FBBView;
import org.lexasub.utils.IR1BaseBlockIO;

import java.io.IOException;


public class IO {
    public static void main(String[] args) throws IOException {
        IR1BaseBlock newBlock = IR1BaseBlock.makeFromFrontendBaseBlock(FBBView.visit(FBBView.getParser("test")));
        IR1BaseBlockIO.dump(IR2BaseBlock.doJob(newBlock), true, true);
    }

}
