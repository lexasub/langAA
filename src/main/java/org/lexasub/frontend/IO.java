package org.lexasub.frontend;

import org.lexasub.frontend.utils.FBBView;
import org.lexasub.frontend.utils.FBB;
import org.lexasub.frontend.utils.FBBIO;

import java.io.IOException;


public class IO {
    public static void main(String[] args) throws IOException {

        FBBIO.jsonize = true;
        //Asm.print(
        FBB block = FBBView.visit(FBBView.getParser("test"));
        StringBuilder sb = new StringBuilder();
        FBBIO.dump("", sb, block);
        sb.setLength(sb.length() - 2);
        //System.out.println(sb);

        StringBuilder sb1 = new StringBuilder();
        FBBIO.serialize(sb1, block);
        //System.out.println(sb1);

        FBB newBlock = FBBIO.deserialize(sb1.toString().split("\n \n"));
        StringBuilder sb2 = new StringBuilder();
        FBBIO.dump("", sb2, newBlock);
        sb2.setLength(sb2.length() - 2);
        System.out.println(sb2);
    }

}
