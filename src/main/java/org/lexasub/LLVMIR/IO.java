package org.lexasub.LLVMIR;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

import static org.lexasub.IR4.IO.generateIr4;


public class IO {
    public static void main(String[] args) throws IOException {
        LLVMIR ll = generateLLVMIR();
        //  IR4IO.jsonize = true;
        //   IR4IO.compact = true;
        // IR4IO.dumpAsText(ll);
        //  IR4IO.dumpAsGraph(ll, "/tmp/IR4.svg", "SVG");
        printBaseCode();
        System.out.println(ll.ir);
    }

    private static void printBaseCode() throws FileNotFoundException {
        try (Scanner input = new Scanner(new File("src/main/java/org/lexasub/LLVMIR/preCode.txt"))) {
            while (input.hasNextLine()) {
                System.out.println(input.nextLine());
            }
        }
    }

    private static LLVMIR generateLLVMIR() throws IOException {
        return LLVMIR.doJob(generateIr4("test"));
    }

}
