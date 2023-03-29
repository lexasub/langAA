package org.lexasub.IR4;

import org.lexasub.IR3.IR3;
import org.lexasub.utils.IdGenerator;

import java.util.LinkedList;
import java.util.stream.Stream;

public class IR4 {
    public Type type;
    public LinkedList<IR4> childs = new LinkedList<>();
    public String name = IdGenerator.id();//kostyl'

    public IR4(Type _type) {
        type = _type;
    }

    public static IR4 doJob(IR3 block) {
        if (block.typeIs(IR3.Type.BLOCK)) return BlockPart(block);
        if (block.typeIs(IR3.Type.ASSIGN)) return AssignPart(block);
        if (block.typeIs(IR3.Type.CALL)) return CallPart(block);
        if (block.typeIs(IR3.Type.FUNC)) return FuncPart(block);
        if (block.typeIs(IR3.Type.ID)) return IdPart(block);
        if (block.typeIs(IR3.Type.PHI)) return PhiPart(block);
        if (block.typeIs(IR3.Type.RET)) return RetPart(block);
        if (block.typeIs(IR3.Type.COND_JMP)) return JMPCondPart(block);
        if (block.typeIs(IR3.Type.JMP)) return JMPPart(block);
        //splitter
        return null;
    }

    private static IR4 JMPPart(IR3 block) {
        return IR4Asm.JMP(IR4AsmUtils.endOf(block.childsGet(0)));
    }

    private static IR4 JMPCondPart(IR3 block) {//todo add condition in ir3
        return IR4Asm.JMPCond(doJob(block.childsGet(0)), doJob(block.childsGet(1)),
                doJob(new IR3(IR3.Type.ID)));//<-replace to condition res
    }

    private static IR4 BlockPart(IR3 block) {
        return IR4Asm.decorateBlock(block.blockId, block.childs.stream().map(IR4::doJob));
    }


    private static IR4 AssignPart(IR3 block) {//first child - it's id
        return IR4Asm.Assign(block.childsGet(0).name, doJob(block.childsGet(1))).setName(block.blockId);
    }

    private static IR4 CallPart(IR3 block) {// childs only id's?
        return IR4Asm.call(block.name, block.childs.stream());
    }

    private static IR4 FuncPart(IR3 block) {//TODO
        return IR4Asm.func(block.name, block.childs.stream().filter(i -> i.typeIs(IR3.Type.ID)),//mb reduce refinding over object by stream
                block.childs.stream().filter(i -> !i.typeIs(IR3.Type.ID)).map(IR4::doJob));
    }

    private static IR4 IdPart(IR3 block) {
        return new IR4(Type.ID).setName(block.name);
    }

    private static IR4 PhiPart(IR3 block) {
        return IR4Asm.phi("i32", block.childs);
    }

    private static IR4 RetPart(IR3 block) {
        return IR4Asm.ret(doJob(block.childsGet(0).getRes()));
    }

    public IR4 addChild(IR4 child) {
        childs.add(child);
        return this;
    }

    public IR4 addTwoChilds(IR4 child0, IR4 child1) {
        return addChild(child0).addChild(child1);
    }

    public IR4 addChildsStream(Stream<IR4> args) {
        args.forEach(this::addChild);
        return this;
    }

    public IR4 setName(String _name) {
        name = _name;
        return this;
    }

    public boolean typeIs(Type _type) {
        return type == _type;
    }

    public enum Type {CODE, ID, CODEPART, COMA, LBL, SEQ}
}
