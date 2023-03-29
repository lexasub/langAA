package org.lexasub.IR4;


import org.lexasub.IR3.IR3;

import java.io.IOException;

import static org.lexasub.IR3.IO.generateIr3;


public class IO {
    public static void main(String[] args) throws IOException {
        IR4 ll = generateIr4("test");
        //  IR4IO.jsonize = true;
        IR4IO.compact = true;
        IR4IO.dumpAsText(ll);
        IR4IO.dumpAsGraph(ll, "/tmp/IR4.svg", "SVG");
        System.out.println("ss");
    }

    public static IR4 generateIr4(String filename) throws IOException {
        return IR4.doJob(generateIr3(filename));
    }

}
