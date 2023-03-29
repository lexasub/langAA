package org.lexasub.IR3;


import org.lexasub.IR1.IR1;

import java.io.IOException;

import static org.lexasub.IR2.IO.generateIr2;


public class IO {
    public static void main(String[] args) throws IOException {
        IR3 ll = generateIr3("test");
        // IR3IO.jsonize = true;
        IR3IO.compact = true;
        IR3IO.dumpAsText(ll);
        IR3IO.dumpAsGraph(ll, "/tmp/IR3.svg", "SVG");
        System.out.println("s");
    }

    public static IR3 generateIr3(String filename) throws IOException {
        return IR3.doJob(generateIr2(filename));
    }

}
