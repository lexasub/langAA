package org.lexasub.IR4;

import org.lexasub.IR3.IR3;

import java.util.LinkedList;
import java.util.ListIterator;
import java.util.stream.Stream;

public class IR4Asm {
    public static IR4 lbl(String s) {
        return new IR4(IR4.Type.LBL).setName(s);
    }

    public static IR4 call(String funcName, Stream<IR3> childs) {
        return thenConcatCode("call", spawnId("i32"))
                .addChild(spawnGlobalRegister(funcName))
                .addChild(spawnCodePart("("))
                .addChild(spawnComa().addChildsStream(childs.map(i -> i.name).map(i -> spawnTypedRegister("i32", i))))
                .addChild(spawnCodePart(")"));
    }

    private static IR4 funcHeader(String name, Stream<IR3> args) {
        return thenConcatCode("define", spawnId("i32")).addChild(spawnGlobalRegister(name))
                .addChild(spawnCodePart("("))
                //mb pairs, not string-concat
                .addChild(spawnComa().addChildsStream(args.map(i -> " i32 " + "%" + i.name).map(IR4Asm::spawnCodePart)))
                .addChild(spawnCodePart(")"));
    }

    public static IR4 Assign(String regName, IR4 expr) {
        //mb some part of expr before assign, and some part after '='
        return new IR4(IR4.Type.CODE).addTwoChilds(spawnLocalRegister(regName), spawnCodePart("=")).addChild(expr);
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
        IR4 coma = spawnComa();
        while (it.hasNext()) {
            //String bl = it.next().blockId;
            IR3 next = it.next();
            String bl = next.parent.blockId;//zzz
            String id = next.name;//mb get blockId from this it.next() ))
            coma.addChild(thenConcatCodePart("[",
                    spawnComa().addTwoChilds(spawnLocalRegister(id), spawnLocalRegister(bl)))
                    .addChild(spawnCodePart("]")));
        }
        return ir4.addChild(coma);
    }

    private static IR4 thenConcatCodePart(String child0, IR4 child1) {
        return new IR4(IR4.Type.CODE).addTwoChilds(spawnCodePart(child0), child1);
    }

    public static IR4 ret(IR4 arg) {
        return thenConcatCode("ret", transformToTypedRegister("i32", arg));//TODO , now ok on only ret id
    }

    private static IR4 transformToTypedRegister(String type, IR4 arg) {
        return thenConcatCode(type, spawnLocalRegister(arg.name));
    }

    public static IR4 JMPCond(IR4 truePart, IR4 falsePart, IR4 condRes) {
        return thenConcat(thenConcatCode("br",
                spawnId("i1")
        ).addChild(spawnComa().addChild(spawnLocalRegister(condRes.name))
                .addTwoChilds(spawnTypedRegister("label", truePart.name + "_begin"),
                        spawnTypedRegister("label", falsePart.name + "_begin"))
        ), truePart).addChild(falsePart);
    }

    private static IR4 spawnTypedRegister(String type, String name) {
        return thenConcatCode(type, spawnLocalRegister(name));
    }

    private static IR4 spawnLocalRegister(String name) {
        return spawnId("%" + name);
    }

    private static IR4 spawnGlobalRegister(String name) {
        return spawnId("@" + name);
    }

    public static IR4 JMP(IR3 to) {
        return thenConcatCode("br", spawnTypedRegister("label", to.blockId + "_end"));
    }

    private static IR4 spawnComa() {
        return new IR4(IR4.Type.COMA);
    }

}
