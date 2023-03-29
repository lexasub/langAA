package org.lexasub.IR2;


import org.lexasub.IR1.IR1;
import org.lexasub.IR2.IR2.IR2;
import org.lexasub.utils.graphDriver.GraphDumper;

import java.io.IOException;

import static org.lexasub.IR1.IO.generateIR1;


public class IO {
    public static void main(String[] args) throws IOException {
        GraphDumper.dump(generateIr2("test"), true, true, "/tmp/IR2.svg", "SVG");
    }

    public static IR1 generateIr2(String filename) throws IOException {
        return IR2.doJob(generateIR1(filename));
    }

}
