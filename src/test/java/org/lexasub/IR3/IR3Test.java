package org.lexasub.IR3;

import com.ibm.icu.impl.Assert;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.util.stream.Stream;

import static org.lexasub.Generic.GenericTest.testFiles;
import static org.lexasub.IR2.IR2Test.generateIr2TestRes;

public class IR3Test {

    @ParameterizedTest
    @MethodSource("argsProviderFactory")
    void visit(String filemame) {
        IR3 block = null;
        try {
            block = generateIr3TestRes(filemame);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        IR3IO.dumpAsGraph(block, "/tmp/" + filemame + "_ir3.svg", "SVG");
        Assert.assrt(true);
    }

    public static IR3 generateIr3TestRes(String filemame) throws IOException {
        return IR3.doJob(generateIr2TestRes(filemame));
    }

    static Stream<String> argsProviderFactory() {
        return testFiles();
    }
}