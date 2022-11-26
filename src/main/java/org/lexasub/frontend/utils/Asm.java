package org.lexasub.frontend.utils;

import java.util.stream.Stream;

public class Asm {
    public static FrontendBaseBlock jmp(String cond, String lblTrue, String lblFalse) {
        FrontendBaseBlock fbb = new FrontendBaseBlock();
        fbb.type = FrontendBaseBlock.TYPE.CODE;
        fbb.code = "jmp " + cond + ", " + lblTrue + ", " + lblFalse;
        return fbb;
    }

    public static FrontendBaseBlock jmp(String lbl) {
        FrontendBaseBlock fbb = new FrontendBaseBlock();
        fbb.type = FrontendBaseBlock.TYPE.CODE;
        fbb.code = "jmp " + lbl;
        return fbb;
    }

    public static FrontendBaseBlock call(String funcName, Stream<String> args) {
        FrontendBaseBlock fbb = new FrontendBaseBlock();
        fbb.type = FrontendBaseBlock.TYPE.CODE;
        String args_ = args.map(i -> i + ", ").reduce("", String::concat);
        fbb.code = "call " + funcName + "(" + args_.substring(0, args_.length()-2) + ")";
        return fbb;
    }

    public static FrontendBaseBlock RETURN(String arg) {
        FrontendBaseBlock fbb = new FrontendBaseBlock();
        fbb.type = FrontendBaseBlock.TYPE.CODE;
        fbb.code = "ret " + arg;
        return fbb;
    }

    public static FrontendBaseBlock RETURN() {
        FrontendBaseBlock fbb = new FrontendBaseBlock();
        fbb.type = FrontendBaseBlock.TYPE.CODE;
        fbb.code = "ret";
        return fbb;
    }
}
