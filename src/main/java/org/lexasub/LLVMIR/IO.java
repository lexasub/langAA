package org.lexasub.LLVMIR;


import org.lexasub.IR1.IR1;
import org.lexasub.IR2.IR2.IR2;
import org.lexasub.IR3.IR3;
import org.lexasub.IR4.IR4;
import org.lexasub.IR4.IR4IO;
import org.lexasub.frontend.utils.FBBView;

import java.io.IOException;

import static org.lexasub.IR1.IR1.makeFromFBB;


public class IO {
    public static void main(String[] args) throws IOException {
        IR1 ir1Block = makeFromFBB(FBBView.visit(FBBView.getParser("test")));
        LLVMIR ll = LLVMIR.doJob(IR4.doJob(IR3.doJob(IR2.doJob(ir1Block))));
        //  IR4IO.jsonize = true;
     //   IR4IO.compact = true;
       // IR4IO.dumpAsText(ll);
      //  IR4IO.dumpAsGraph(ll, "/tmp/IR4.svg", "SVG");
        System.out.println(ll.ir);
    }

}
