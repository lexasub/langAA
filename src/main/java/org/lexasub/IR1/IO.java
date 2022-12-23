package org.lexasub.IR1;


import org.lexasub.frontend.utils.FBBView;
import org.lexasub.utils.IR1IO;

import java.io.IOException;


public class IO {
    public static void main(String[] args) throws IOException {

        //Asm.pretty = true;//Set output with tabs
        //Asm.print(
        IR1 newBlock = IR1.makeFromFBB(FBBView.visit(FBBView.getParser("test")));
        IR1IO.dump(newBlock, true, true);
        // newBlock.serialize(sb);
        // System.out.println(sb);


        // StringBuilder sb1 = new StringBuilder();
        // newBlock.serialize(sb1);
        //System.out.println(sb1);

        //newBlock.deserialize()
    }

}
