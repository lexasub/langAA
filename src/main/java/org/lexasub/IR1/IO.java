package org.lexasub.IR1;


import org.lexasub.IR1.IR1Block.IR1BaseBlock;
import org.lexasub.IR1.IR1Block.IR1BaseBlockIO;
import org.lexasub.frontend.utils.FBBView;

import java.io.IOException;


public class IO {
    public static void main(String[] args) throws IOException {

        //Asm.pretty = true;//Set output with tabs
        //Asm.print(
        IR1BaseBlock newBlock = IR1BaseBlock.makeFromFrontendBaseBlock(FBBView.visit(FBBView.getParser("test")));
        IR1BaseBlockIO.dump(newBlock);
        // newBlock.serialize(sb);
        // System.out.println(sb);


        // StringBuilder sb1 = new StringBuilder();
        // newBlock.serialize(sb1);
        //System.out.println(sb1);

        //newBlock.deserialize()
    }

}
