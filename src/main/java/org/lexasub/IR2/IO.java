package org.lexasub.IR2;


import org.lexasub.IR1.IR1;
import org.lexasub.IR2.IR2.IR2;
import org.lexasub.frontend.utils.FBBView;
import org.lexasub.utils.IR1IO;

import java.io.IOException;


public class IO {
    public static void main(String[] args) throws IOException {
        IR1 newBlock = IR1.makeFromFBB(FBBView.visit(FBBView.getParser("test")));
        IR1IO.dump(IR2.doJob(newBlock), true, true);
    }

}
