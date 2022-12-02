package org.lexasub.IR2;


import org.lexasub.IR1.IR1Block.IR1BaseBlock;
import org.lexasub.frontend.utils.FBBView;

import java.io.IOException;


public class IO {
    public static void main(String[] args) throws IOException {

        IR1BaseBlock newBlock = IR1BaseBlock.makeFromFrontendBaseBlock(FBBView.visit(FBBView.getParser("test")));
        IR2BaseBlock ir2BaseBlock = new IR2BaseBlock(newBlock);
        ir2BaseBlock.doJob();
        IR2BaseBlockIO.dump(ir2BaseBlock);
    }

}
