package org.lexasub.LLVMIR;

import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class LLVMIRasm {
    public static LLVMIR concatSplitter(Stream<LLVMIR> llvmirStream, char s) {
        return new LLVMIR(getIrs(llvmirStream, s)
                .collect(Collectors.joining()));
    }

    public static LLVMIR concatSplitterComa(Stream<LLVMIR> llvmirStream) {
        return new LLVMIR(getIrs(llvmirStream, ',').map(i -> i.append(' '))
                .collect(Collectors.joining()))
                .truncateIrFromEnd(2);
    }

    private static Function<StringBuilder, StringBuilder> smartConcatWithSplitter(char s) {
        return ir -> Objects.equals(ir.charAt(ir.length() - 1), s) ? ir : ir.append(s);
    }

    private static Stream<StringBuilder> getIrs(Stream<LLVMIR> llvmirStream, char s) {
        return llvmirStream.filter(Objects::nonNull)
                .map(i -> i.ir).filter(ir -> !ir.isEmpty())
                .map(smartConcatWithSplitter(s));
    }

    public static LLVMIR spawnLBL(String name) {
        return new LLVMIR(name + ":");
    }
}