package org.lexasub.frontend.utils;

import java.util.stream.Stream;

public class Asm {

    public static void call(String funcName, Stream<FBB> args, FBB newFunCall) {
        FBB name_ = new FBB();
        name_.type = FBB.TYPE.ID;
        name_.setName(funcName);
        FBB v = introduceCodeBlock(Stream.concat(Stream.of(name_), args), "call");
        newFunCall.fullLinkWith(v);

    }

    private static FBB introduceCodeBlock(Stream<FBB> args, String nameOp) {
        FBB fbb = new FBB();
        fbb.type = FBB.TYPE.CODE;
        FBB nameOP = new FBB();
        nameOP.type = FBB.TYPE.ID;
        nameOP.setName(nameOp);
        fbb.fullLinkWith(nameOP);
        args.forEach(fbb::fullLinkWith);
        return fbb;
    }

    public static FBB RETURN(String arg, FBB myBlock) {
        FBB name_ = new FBB();
        name_.type = FBB.TYPE.ID;
        name_.setName(arg);
        FBB v = introduceCodeBlock(Stream.of(name_), "ret");
        v.setParent(myBlock);
        return v;
    }

    public static FBB RETURN(FBB _parent) {
        FBB v = introduceCodeBlock(Stream.of(), "ret");
        v.setParent(_parent);
        return v;
    }

    public static FBB RETURN(FBB expr, FBB _parent) {
        FBB fbb = introduceCodeBlock(Stream.of(), "ret");
        FBB i = new FBB();
        i.type = FBB.TYPE.ID;
        i.setName("res_" + expr.blockId);//todo link with expr
        fbb.fullLinkWith(i);
        FBB newFbb = new FBB();
        fbb.setParent(newFbb);
        newFbb.fullLinkWith(fbb);
        newFbb.fullLinkWith(expr);
        return newFbb;

    }

}
