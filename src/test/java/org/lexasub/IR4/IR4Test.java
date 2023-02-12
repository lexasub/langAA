package org.lexasub.IR4;

import com.ibm.icu.impl.Assert;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.util.stream.Stream;

import static org.lexasub.Generic.GenericTest.testFiles;
import static org.lexasub.IR3.IR3Test.generateIr3TestRes;

public class IR4Test {

    @ParameterizedTest
    @MethodSource("argsProviderFactory")
    void visit(String filemame) {
        IR4 block = null;
        try {
            block = generateIr4TestRes(filemame);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        IR4IO.dumpAsGraph(block, "/tmp/" + filemame + "_ir4.svg", "SVG");
        Assert.assrt(true);
    }

    public static IR4 generateIr4TestRes(String filemame) throws IOException {
        return IR4.doJob(generateIr3TestRes(filemame));
    }

    static Stream<String> argsProviderFactory() {
        return testFiles();
    }
}