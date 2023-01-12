package org.lexasub.IR4;

import org.lexasub.IR3.IR3;

import java.util.stream.Stream;

public class IR4Asm {
    public static IR4 lbl(String s) {
        return spawnCode(s + ":");
    }

    public static IR4 call(String funcName, Stream<IR3> childs) {
        return thenConcatCode("call", spawnId(funcName))
                .addChildsStream(childs.map(i-> i.name).map(IR4Asm::spawnId));
    }

    private static IR4 funcHeader(String name, Stream<IR3> args) {
        return thenConcatCode("define", spawnId("int"))
                .addChild(spawnId("@" + name))
                .addChild(spawnCode("("))
                .addChildsStream(args.map(i->"int %" + i.name).map(IR4Asm::spawnCode))//mb pairs, not string-concat
                .addChild(spawnCode(")"));
    }

    public static IR4 Assign(String regName, IR4 expr) {
        return thenConcatCode(regName, spawnCode("="))
                .addChild(expr);//mb some part of expr before assing, and some part after '='
    }

    public static IR4 func(String name, Stream<IR3> args, Stream<IR4> body) {
        return thenConcat(funcHeader(name, args), spawnCode("{"))
                .addChildsStream(body)
                .addChild(spawnCode("}"));//mb mark type as function
    }
    public static IR4 decorateBlock(String blockId, Stream<IR4> body) {
        return thenConcat(lbl(blockId + "_begin"), body)
                .addChild(lbl(blockId + "_end"));
    }

    private static IR4 thenConcatCode(String id, IR4 child) {
        return new IR4(IR4.Type.CODE).addChild(spawnId(id)).addChild(child);
    }

    private static IR4 thenConcat(IR4 child0, IR4 child1) {
        return new IR4(IR4.Type.SEQ).addChild(child0).addChild(child1);
    }

    private static IR4 thenConcat(IR4 child0, Stream<IR4> childs) {
        return new IR4(IR4.Type.SEQ).addChild(child0).addChildsStream(childs);
    }

    private static IR4 spawnCode(String code) {
        return new IR4(IR4.Type.CODE).setName(code);
    }

    private static IR4 spawnId(String idName) {
        return new IR4(IR4.Type.ID).setName(idName);
    }
}
