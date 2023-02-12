package org.lexasub.IR3;

import com.ibm.icu.impl.Assert;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.lexasub.Generic.GenericTest.testFiles;
import static org.lexasub.IR2.IR2Test.generateIr2TestRes;

public class IR3Test {

    @ParameterizedTest
    @MethodSource("argsProviderFactory")
    void visit(String filemame) {
        IR3IO.dumpAsGraph(generateIr3TestRes(filemame), "/tmp/" + filemame + "_ir3.svg", "SVG");
        Assert.assrt(true);
    }

    public static IR3 generateIr3TestRes(String filemame) {
        return IR3.doJob(generateIr2TestRes(filemame));
    }

    static Stream<String> argsProviderFactory() {
        return testFiles();
    }
}