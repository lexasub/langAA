package org.lexasub.IR1;

import com.ibm.icu.impl.Assert;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.lexasub.frontend.utils.fbb.FBBViewTest;
import org.lexasub.utils.graphDriver.GraphDumper;

import java.util.stream.Stream;

import static org.lexasub.Generic.GenericTest.testFiles;

public class IR1Test {

    public static IR1 generateIr1TestRes(String filemame) {
        return IR1.makeFromFBB(FBBViewTest.generateFBBTestRes(filemame));
    }

    static Stream<String> argsProviderFactory() {
        return testFiles();
    }

    @ParameterizedTest
    @MethodSource("argsProviderFactory")
    void visit(String filemame) {
        GraphDumper.dump(generateIr1TestRes(filemame), true, true, "/tmp/" + filemame + "_ir1.svg", "SVG");
        Assert.assrt(true);
    }

}