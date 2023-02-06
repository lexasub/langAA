package org.lexasub.frontend.utils.fbb;

import com.ibm.icu.impl.Assert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.lexasub.frontend.utils.FBB;
import org.lexasub.frontend.utils.FBBIO;
import org.lexasub.frontend.utils.FBBView;

import java.io.IOException;
import java.util.stream.Stream;

class FBBViewTest {

    @ParameterizedTest
    @MethodSource("argsProviderFactory")
    void visit(String filemame) {
        FBB block = null;
        try {
            block = FBBView.visit(FBBView.getParser("src/test/java/org/lexasub/frontend/utils/fbb/"+filemame));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println(FBBIO.dumpAsText("", block));
        Assert.assrt(true);
    }
    static Stream<String> argsProviderFactory() {
        return Stream.of("test1.txt", "test2.txt", "test3.txt" );
    }
}