package org.lexasub.frontend.utils;

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
}
