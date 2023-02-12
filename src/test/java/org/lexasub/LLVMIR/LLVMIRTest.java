package org.lexasub.LLVMIR;

import com.ibm.icu.impl.Assert;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.lexasub.IR4.IR4;

import java.io.IOException;
import java.util.stream.Stream;

import static org.lexasub.Generic.GenericTest.testFiles;
import static org.lexasub.IR4.IR4Test.generateIr4TestRes;

class LLVMIRTest {

    @ParameterizedTest
    @MethodSource("argsProviderFactory")
    void visit(String filemame) {
        IR4 block = null;
        try {
            block = generateIr4TestRes(filemame);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println(LLVMIR.doJob(block).ir);
        //IR4IO.dumpAsGraph(block, "/tmp/" + filemame + "_ir4.svg", "SVG");
        Assert.assrt(true);
    }

    static Stream<String> argsProviderFactory() {
        return testFiles();
    }
}