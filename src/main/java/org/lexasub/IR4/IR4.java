package org.lexasub.IR4;

import org.lexasub.IR3.IR3;

import java.util.LinkedList;
import java.util.stream.Stream;

public class IR4 {
    private Type type;
    private LinkedList<IR4> childs = new LinkedList<>();

    public IR4(Type _type) {
        type = _type;
    }

    public static IR4 doJob(IR3 block) {
        // public enum Type {ASSIGN, BLOCK, CALL, FUNC, ID, PHI_PART, RET, SPLITTER}
        if (block.typeIs(IR3.Type.BLOCK)) return BlockPart(block);
        if (block.typeIs(IR3.Type.ASSIGN)) return AssignPart(block);
        if (block.typeIs(IR3.Type.CALL)) return CallPart(block);
        if (block.typeIs(IR3.Type.FUNC)) return FuncPart(block);
        if (block.typeIs(IR3.Type.ID)) return IdPart(block);
        if (block.typeIs(IR3.Type.PHI_PART)) return PhiPart(block);
        if (block.typeIs(IR3.Type.RET)) return RetPart(block);
        //splitter
        return null;
    }

    private static IR4 BlockPart(IR3 block) {
        return IR4Asm.thenConcat(IR4Asm.lbl(block.blockId + "_begin"), block.childs.stream().map(IR4::doJob))
                .addChild(IR4Asm.lbl(block.blockId + "_end"));
    }

    private static IR4 AssignPart(IR3 block) {//first child - it's id
        return null;
    }

    private static IR4 CallPart(IR3 block) {
        return null;
    }

    private static IR4 FuncPart(IR3 block) {
        return null;
    }

    private static IR4 IdPart(IR3 block) {
        return null;
    }

    private static IR4 PhiPart(IR3 block) {
        return null;
    }

    private static IR4 RetPart(IR3 block) {
        return null;
    }

    public IR4 addChild(IR4 child) {
        childs.add(child);
        return this;
    }

    public IR4 addChildsStream(Stream<IR4> args) {
        args.forEach(this::addChild);
        return this;
    }

    public enum Type {CODE, SEQ}

    ;
}
