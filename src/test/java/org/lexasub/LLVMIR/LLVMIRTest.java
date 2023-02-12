package org.lexasub.LLVMIR;

import com.ibm.icu.impl.Assert;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.lexasub.Generic.GenericTest.testFiles;
import static org.lexasub.IR4.IR4Test.generateIr4TestRes;

class LLVMIRTest {

    static Stream<String> argsProviderFactory() {
        return testFiles();
    }

    @ParameterizedTest
    @MethodSource("argsProviderFactory")
    void visit(String filemame) {
        System.out.println(LLVMIR.doJob(generateIr4TestRes(filemame)).ir);
        Assert.assrt(true);
    }
}