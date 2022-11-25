package org.lexasub.frontend.utils;

import java.util.LinkedList;
import java.util.Objects;
import java.util.Random;

public class IdGenerator {
    private static final Random random = new Random(25);
    static LinkedList<String> lambdas = new LinkedList<>();
    static LinkedList<String> elems = new LinkedList<>();
    static LinkedList<String> function_name = new LinkedList<>();
    static LinkedList<String> lbl_name = new LinkedList<>();
    static LinkedList<String> regs = new LinkedList<>();

    public static String randomString() {
        int leftLimit = 48; // numeral '0'
        int rightLimit = 122; // letter 'z'
        int targetStringLength = 10;
        return random.ints(leftLimit, rightLimit + 1)
                .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                .limit(targetStringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }

    public static String lambda() {
        return getString(lambdas, "lambda_");
    }

    public static String element() {
        return getString(elems, "element_");
    }

    public static String functionCall() {
        return getString(function_name, "function_call_");
    }

    private static String getString(LinkedList<String> e, String text) {
        while (true) {
            String s = randomString();
            if (e.stream().filter(i -> Objects.equals(i, s)).findFirst().isEmpty())
                return text + s;
        }
    }

    public static String label() {
        return getString(lbl_name, "lbl_");
    }
    public static String id() {
        return getString(lbl_name, "");
    }
    public static String lblCollBegin() {
        return "MAP_ARGUMENT_" + label();
    }

    public static String lbl() {
        return "lbl_" + label();
    }

    public static String lblMapEnd() {
        return "ENDMAP_" + label();
    }

    public static String lblWhileEnd() {
        return "ENDWHILE_" + label();
    }

    public static String lblIfEnd() {
        return "ENDIF_" + label();
    }

    public static String reg() {
        return getString(regs, "gr_");
    }

    public static String type() {
        return getString(regs, "type_");
    }
}
