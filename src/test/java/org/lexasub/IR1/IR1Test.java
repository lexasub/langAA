package org.lexasub.IR1;

import com.ibm.icu.impl.Assert;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.lexasub.frontend.utils.fbb.FBBViewTest;
import org.lexasub.utils.graphDriver.GraphDumper;

import java.io.IOException;
import java.util.stream.Stream;

import static org.lexasub.Generic.GenericTest.testFiles;

public class IR1Test {

    @ParameterizedTest
    @MethodSource("argsProviderFactory")
    void visit(String filemame) {
        IR1 block = null;
        try {
            block = generateIr1TestRes(filemame);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        GraphDumper.dump(block, true, true, "/tmp/" + filemame + "_ir1.svg", "SVG");
        Assert.assrt(true);
    }

    public static IR1 generateIr1TestRes(String filemame) throws IOException {
        return IR1.makeFromFBB(FBBViewTest.generateFBBTestRes(filemame));
    }

    static Stream<String> argsProviderFactory() {
        return testFiles();
    }

}