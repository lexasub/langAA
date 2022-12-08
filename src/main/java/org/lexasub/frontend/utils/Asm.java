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

    public static void call(String funcName, Stream<FrontendBaseBlock> args, FrontendBaseBlock newFunCall) {
        FrontendBaseBlock name_ = new FrontendBaseBlock();
        name_.type = FrontendBaseBlock.TYPE.ID;
        name_.name = funcName;
        FrontendBaseBlock v = introduceCodeBlock(Stream.concat(Stream.of(name_), args), "call");
        v.parent = newFunCall;
        newFunCall.addChild(v);
    }

    private static FrontendBaseBlock introduceCodeBlock(Stream<FrontendBaseBlock> args, String nameOp) {
        FrontendBaseBlock fbb = new FrontendBaseBlock();
        fbb.type = FrontendBaseBlock.TYPE.CODE;
        FrontendBaseBlock nameOP = new FrontendBaseBlock();
        nameOP.type = FrontendBaseBlock.TYPE.ID;
        nameOP.name = nameOp;
        fbb.childs.add(nameOP);
        nameOP.parent = fbb;
        args.forEach(i -> {
            fbb.childs.add(i);
            i.parent = fbb;
        });
        return fbb;
    }

    public static FrontendBaseBlock RETURN(String arg, FrontendBaseBlock myBlock) {
        FrontendBaseBlock name_ = new FrontendBaseBlock();
        name_.type = FrontendBaseBlock.TYPE.ID;
        name_.name = arg;
        FrontendBaseBlock v = introduceCodeBlock(Stream.of(name_), "ret");
        v.parent = myBlock;
        return v;
    }

    public static FrontendBaseBlock RETURN(FrontendBaseBlock _parent) {
        FrontendBaseBlock fbb = new FrontendBaseBlock();
        fbb.type = FrontendBaseBlock.TYPE.CODE;
        fbb.code = "ret";
        fbb.parent = _parent;
        return fbb;
    }

    public static FrontendBaseBlock RETURN(FrontendBaseBlock expr, FrontendBaseBlock _parent) {
        FrontendBaseBlock fbb = introduceCodeBlock(Stream.of(), "ret");
        FrontendBaseBlock i = new FrontendBaseBlock();
        i.type = FrontendBaseBlock.TYPE.ID;
        i.name = "res_" + expr.blockId;//todo link with expr
        fbb.childs.add(i);
        i.parent = fbb;
        FrontendBaseBlock newFbb = new FrontendBaseBlock();
        fbb.parent = newFbb;
        expr.parent = newFbb;
        newFbb.addChild(expr);
        newFbb.addChild(fbb);
        newFbb.parent = _parent;
        return newFbb;

    }
}
