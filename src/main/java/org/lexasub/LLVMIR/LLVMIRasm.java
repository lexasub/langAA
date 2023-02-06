package org.lexasub.LLVMIR;

import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class LLVMIRasm {
    public static LLVMIR concatSplitter(Stream<LLVMIR> llvmirStream, char s) {
        return new LLVMIR(llvmirStream.filter(Objects::nonNull)
                .map(i->i.ir).filter(ir -> !ir.isEmpty())
                .map(smartConcatWithSplitter(s))
                .collect(Collectors.joining()));
    }

    private static Function<StringBuilder, StringBuilder> smartConcatWithSplitter(char s) {
        return ir -> Objects.equals(ir.charAt(ir.length() - 1), s) ? ir : ir.append(s);
    }
}