package org.lexasub.IR4;


import org.lexasub.IR1.IR1;
import org.lexasub.IR2.IR2.IR2;
import org.lexasub.IR3.IR3;
import org.lexasub.frontend.utils.FBBView;

import java.io.IOException;

import static org.lexasub.IR1.IR1.makeFromFBB;


public class IO {
    public static void main(String[] args) throws IOException {
        IR1 ir1Block = makeFromFBB(FBBView.visit(FBBView.getParser("test")));
        IR4 ll = IR4.doJob(IR3.doJob(IR2.doJob(ir1Block)));
        System.out.println("ss");
    }

}
