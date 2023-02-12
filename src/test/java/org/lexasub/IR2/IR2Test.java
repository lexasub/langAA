package org.lexasub.IR2;

import com.ibm.icu.impl.Assert;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.lexasub.Generic.GenericTest;
import org.lexasub.IR1.IR1;
import org.lexasub.IR2.IR2.IR2;
import org.lexasub.frontend.utils.FBBView;
import org.lexasub.utils.graphDriver.GraphDumper;

import java.io.IOException;
import java.util.stream.Stream;

import static org.lexasub.Generic.GenericTest.testFiles;
import static org.lexasub.IR1.IR1Test.generateIr1TestRes;

public class IR2Test {

    @ParameterizedTest
    @MethodSource("argsProviderFactory")
    void visit(String filemame) {
        GraphDumper.dump(generateIr2TestRes(filemame), true, true, "/tmp/" + filemame + "_ir2.svg", "SVG");
        Assert.assrt(true);
    }

    public static IR1 generateIr2TestRes(String filemame) {
        return IR2.doJob(generateIr1TestRes(filemame));
    }

    static Stream<String> argsProviderFactory() {
        return testFiles();
    }
}