package org.lexasub.IR4;

import com.ibm.icu.impl.Assert;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.lexasub.Generic.GenericTest.testFiles;
import static org.lexasub.IR3.IR3Test.generateIr3TestRes;

public class IR4Test {

    public static IR4 generateIr4TestRes(String filemame) {
        return IR4.doJob(generateIr3TestRes(filemame));
    }

    static Stream<String> argsProviderFactory() {
        return testFiles();
    }

    @ParameterizedTest
    @MethodSource("argsProviderFactory")
    void visit(String filemame) {
        IR4IO.dumpAsGraph(generateIr4TestRes(filemame), "/tmp/" + filemame + "_ir4.svg", "SVG");
        Assert.assrt(true);
    }
}