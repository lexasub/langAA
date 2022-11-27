package org.lexasub.IR1;


import org.lexasub.IR1.utils.IR1BaseBlock;
import org.lexasub.frontend.utils.FrontendBaseBlock;

import java.io.IOException;
import java.util.LinkedList;

import static org.lexasub.frontend.IO.getParser;
import static org.lexasub.frontend.IO.visit;


public class IO {
    public static void main(String[] args) throws IOException {

        //Asm.pretty = true;//Set output with tabs
        //Asm.print(
        FrontendBaseBlock block = visit(getParser("test"));
        IR1BaseBlock newBlock = IR1BaseBlock.makeFromFrontendBaseBlock(block);
        StringBuilder sb = new StringBuilder();
        newBlock.dump("", sb);
        System.out.println(sb);


        StringBuilder sb1 = new StringBuilder();
       // newBlock.serialize(sb1);
        System.out.println(sb1);

        //newBlock.deserialize()
    }

}
