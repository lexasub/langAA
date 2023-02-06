package org.lexasub.IR1;

import com.ibm.icu.impl.Assert;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.lexasub.IR1.IR1;
import org.lexasub.frontend.utils.FBBView;
import org.lexasub.utils.graphDriver.GraphDumper;

import java.io.IOException;
import java.util.stream.Stream;

class IR1Test {

    @ParameterizedTest
    @MethodSource("argsProviderFactory")
    void visit(String filemame) {
        IR1 block = null;
        try {
            block = IR1.makeFromFBB(FBBView.visit(FBBView.getParser("src/test/java/org/lexasub/frontend/utils/fbb/"+filemame)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        GraphDumper.dump(block, true, true, "/tmp/" + filemame + "_ir1.svg", "SVG");
        Assert.assrt(true);
    }
    static Stream<String> argsProviderFactory() {
        return Stream.of("test1.txt", "test2.txt", "test3.txt" );
    }
}