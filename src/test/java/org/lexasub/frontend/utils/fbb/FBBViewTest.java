package org.lexasub.frontend.utils.fbb;

import com.ibm.icu.impl.Assert;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.lexasub.Generic.GenericTest;
import org.lexasub.frontend.utils.FBB;
import org.lexasub.frontend.utils.FBBIO;
import org.lexasub.frontend.utils.FBBView;

import java.io.IOException;
import java.util.stream.Stream;

import static org.lexasub.Generic.GenericTest.testFiles;

public class FBBViewTest {

    public static FBB generateFBBTestRes(String filemame) {
        try {
            return FBBView.visit(FBBView.getParser(GenericTest.addPath(filemame)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    static Stream<String> argsProviderFactory() {
        return testFiles();
    }

    @ParameterizedTest
    @MethodSource("argsProviderFactory")
    void visit(String filemame) {
        System.out.println(FBBIO.dumpAsText("", generateFBBTestRes(filemame)));
        Assert.assrt(true);
    }
}