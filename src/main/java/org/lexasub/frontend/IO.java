package org.lexasub.frontend;

import org.lexasub.frontend.utils.FBBView;
import org.lexasub.frontend.utils.FrontendBaseBlock;
import org.lexasub.frontend.utils.FrontendBaseBlockIO;

import java.io.IOException;


public class IO {
    public static void main(String[] args) throws IOException {

        FrontendBaseBlockIO.jsonize = true;
        //Asm.print(
        FrontendBaseBlock block = FBBView.visit(FBBView.getParser("test"));
        StringBuilder sb = new StringBuilder();
        FrontendBaseBlockIO.dump("", sb, block);
        sb.setLength(sb.length() - 2);
        //System.out.println(sb);

        StringBuilder sb1 = new StringBuilder();
        FrontendBaseBlockIO.serialize(sb1, block);
        //System.out.println(sb1);

        FrontendBaseBlock newBlock = FrontendBaseBlockIO.deserialize(sb1.toString().split("\n \n"));
        StringBuilder sb2 = new StringBuilder();
        FrontendBaseBlockIO.dump("", sb2, newBlock);
        sb2.setLength(sb2.length() - 2);
        System.out.println(sb2);
    }

}
