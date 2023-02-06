package org.lexasub.IR4;

import org.lexasub.IR3.IR3;

import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Objects;
import java.util.stream.Stream;

public class IR4Asm {
    public static IR4 lbl(String s) {
        return spawnCodePart(s + ":");
    }

    public static IR4 call(String funcName, Stream<IR3> childs) {
        return thenConcatCode("call", spawnId(funcName))
                .addChildsStream(childs.map(i -> i.name).map(IR4Asm::spawnId));
    }

    private static IR4 funcHeader(String name, Stream<IR3> args) {
        return thenConcatCode("define", spawnId("int"))
                .addTwoChilds(spawnId("@" + name), spawnCodePart("("))
                .addChildsStream(args.map(i -> "int %" + i.name).map(IR4Asm::spawnCodePart))//mb pairs, not string-concat
                .addChild(spawnCodePart(")"));
    }

    public static IR4 Assign(String regName, IR4 expr) {
        //mb some part of expr before assing, and some part after '='
        return thenConcatCode(regName, spawnCodePart("=")).addChild(expr);
    }

    public static IR4 func(String name, Stream<IR3> args, Stream<IR4> body) {
        return thenConcat(funcHeader(name, args), spawnCodePart("{"))
                .addChildsStream(body)
                .addChild(spawnCodePart("}"));//mb mark type as function
    }

    public static IR4 decorateBlock(String blockId, Stream<IR4> body) {
        return thenConcat(lbl(blockId + "_begin"), body)
                .addChild(lbl(blockId + "_end")).setName(blockId);
    }

    private static IR4 thenConcatCode(String id, IR4 child) {
        return new IR4(IR4.Type.CODE).addTwoChilds(spawnId(id), child);
    }

    private static IR4 thenConcat(IR4 child0, IR4 child1) {
        return new IR4(IR4.Type.SEQ).addTwoChilds(child0, child1);
    }

    private static IR4 thenConcat(IR4 child0, Stream<IR4> childs) {
        return new IR4(IR4.Type.SEQ).addChild(child0).addChildsStream(childs);
    }

    private static IR4 spawnCodePart(String code) {
        return new IR4(IR4.Type.CODEPART).setName(code);
    }

    private static IR4 spawnId(String idName) {
        return new IR4(IR4.Type.ID).setName(idName);
    }

    public static IR4 phi(String type, LinkedList<IR3> childs) {
        IR4 ir4 = new IR4(IR4.Type.CODE);
        ir4.addChild(spawnCodePart("phi"));
        ir4.addChild(spawnId(type));
        ListIterator<IR3> it = childs.listIterator();
        while (it.hasNext()) {
            //String bl = it.next().blockId;
            IR3 next = it.next();
            String bl = next.parent.blockId;//zzz
            String id = next.name;//mb get blockId from this it.next() ))
            ir4.addChild(spawnCodePart("[%" + id + ", " + "%" + bl + "], "));
        }
        return ir4;
    }

    public static IR4 ret(IR4 arg) {
        return thenConcatCode("ret", arg);
    }

    public static IR4 JMPCond(IR4 truePart, IR4 falsePart, IR4 condRes) {
        return thenConcat(thenConcatCode("br",
                spawnId("i1")
        ).addTwoChilds(spawnId("%" + condRes.name),
                spawnCodePart(", label")).addTwoChilds(
                spawnId("%" + truePart.name + "_begin"),//mb use not string
                spawnCodePart(", label")
        ).addChild(spawnId("%" + falsePart.name + "_begin")//mb use not string
        ),
                truePart).addChild(falsePart);
    }

    public static IR4 JMP(IR3 to) {
        return thenConcatCode("br", spawnId(to.blockId + "_end"));
    }
}
