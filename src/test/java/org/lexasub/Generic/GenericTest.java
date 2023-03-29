package org.lexasub.Generic;

import java.util.stream.Stream;

public class GenericTest {
    public static Stream<String> testFiles() {
        return Stream.of("test1.txt", "test2.txt", "test3.txt");
    }

    public static String addPath(String filemame) {
        return "src/test/java/org/lexasub/frontend/utils/fbb/" + filemame;
    }
}
