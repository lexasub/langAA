package org.lexasub.IR1;


import org.lexasub.utils.graphDriver.GraphDumper;

import java.io.IOException;

import static org.lexasub.frontend.IO.generateFBB;


public class IO {
    public static void main(String[] args) throws IOException {

        //Asm.pretty = true;//Set output with tabs
        //Asm.print(
        IR1 newBlock = generateIR1("test");
        GraphDumper.dump(newBlock, true, true, "graph.svg", "SVG");
        // newBlock.serialize(sb);
        // System.out.println(sb);


        // StringBuilder sb1 = new StringBuilder();
        // newBlock.serialize(sb1);
        //System.out.println(sb1);

        //newBlock.deserialize()
    }

    public static IR1 generateIR1(String filename) throws IOException {
        return IR1.makeFromFBB(generateFBB(filename));
    }

}
