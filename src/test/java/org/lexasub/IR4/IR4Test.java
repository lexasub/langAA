package org.lexasub.IR4;

import com.ibm.icu.impl.Assert;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.lexasub.IR1.IR1;
import org.lexasub.IR2.IR2.IR2;
import org.lexasub.IR3.IR3;
import org.lexasub.IR4.IR4;
import org.lexasub.IR4.IR4IO;
import org.lexasub.frontend.utils.FBBView;

import java.io.IOException;
import java.util.stream.Stream;

class IR4Test {

    @ParameterizedTest
    @MethodSource("argsProviderFactory")
    void visit(String filemame) {
        IR4 block = null;
        try {
            block = IR4.doJob(IR3.doJob(IR2.doJob(IR1.makeFromFBB(FBBView.visit(FBBView.getParser("src/test/java/org/lexasub/frontend/utils/fbb/"+filemame))))));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        IR4IO.dumpAsGraph(block, "/tmp/" + filemame + "_ir4.svg", "SVG");
        Assert.assrt(true);
    }
    static Stream<String> argsProviderFactory() {
        return Stream.of("test1.txt", "test2.txt", "test3.txt" );
    }
}