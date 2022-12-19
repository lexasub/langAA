package org.lexasub.IR3;


import org.lexasub.IR1.IR1Block.IR1BaseBlock;
import org.lexasub.IR2.IR2Block.IR2BaseBlock;
import org.lexasub.frontend.utils.FBBView;

import java.io.IOException;


public class IO {
    public static void main(String[] args) throws IOException {
        IR1BaseBlock newBlock = IR1BaseBlock.makeFromFrontendBaseBlock(FBBView.visit(FBBView.getParser("test")));
        IR3BaseBlock.doJob(IR2BaseBlock.doJob(newBlock));
    }

}
