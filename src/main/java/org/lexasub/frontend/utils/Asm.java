package org.lexasub.frontend.utils;

import java.util.stream.Stream;

public class Asm {
    /*public static FrontendBaseBlock jmp(String cond, String lblTrue, String lblFalse) {
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
    }*/

    public static FrontendBaseBlock call(String funcName, Stream<String> args) {
        FrontendBaseBlock fbb = new FrontendBaseBlock();
        fbb.type = FrontendBaseBlock.TYPE.CODE;
        String args_ = args.map(i -> i + ", ").reduce("", String::concat);
        fbb.code = "call " + funcName + "(" + args_.substring(0, args_.length()-2) + ")";
        return fbb;
    }

    public static FrontendBaseBlock RETURN(String arg, FrontendBaseBlock myBlock) {
        FrontendBaseBlock fbb = new FrontendBaseBlock();
        fbb.type = FrontendBaseBlock.TYPE.CODE;
        fbb.code = "ret " + arg;
        fbb.parent = myBlock;
        return fbb;
    }

    public static FrontendBaseBlock RETURN(FrontendBaseBlock _parent) {
        FrontendBaseBlock fbb = new FrontendBaseBlock();
        fbb.type = FrontendBaseBlock.TYPE.CODE;
        fbb.code = "ret";
        fbb.parent = _parent;
        return fbb;
    }

    public static FrontendBaseBlock RETURN(FrontendBaseBlock expr, FrontendBaseBlock _parent) {
        FrontendBaseBlock fbb = new FrontendBaseBlock();
        fbb.type = FrontendBaseBlock.TYPE.CODE;
        fbb.code = "ret " + "res_" + expr.blockId;
        FrontendBaseBlock newFbb = new FrontendBaseBlock();
        fbb.parent = newFbb;
        expr.parent = newFbb;
        newFbb.addChild(expr);
        newFbb.addChild(fbb);
        newFbb.parent = _parent;
        return newFbb;
    }
}
