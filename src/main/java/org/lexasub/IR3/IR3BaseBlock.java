package org.lexasub.IR3;

import org.lexasub.IR1.IR1Block.IR1BaseBlock;
import org.lexasub.frontend.utils.FrontendBaseBlock;

import java.util.*;
import java.util.stream.Stream;

public class IR3BaseBlock {
    Type type;
    private String name;
    private IR3BaseBlock parent;
    private LinkedList<IR3BaseBlock> childs = new LinkedList<>();
    public IR3BaseBlock(Type type) {
        this.type = type;
    }

    public IR3BaseBlock(IR1BaseBlock i, boolean readFromVar) {//for only type==ID
        //readFromVar - else write to var//TODO
        this.name = i.name;
    }

    //Make more CODEBLOCKS
    public static IR3BaseBlock doJob(IR1BaseBlock block) {
        return doJob_(block.nodesOutChilds.get(0));
    }

    public static IR3BaseBlock doJob_(IR1BaseBlock block) {
        if (block.typeIs(FrontendBaseBlock.TYPE.BLOCK)) return BlockPart(block);
        if (block.typeIs(FrontendBaseBlock.TYPE.FUNC)) return FunctionPart(block);
        if (block.typeIs(FrontendBaseBlock.TYPE.JMP)) return JmpPart(block);//TODO cond_jmp
        if (block.typeIs(FrontendBaseBlock.TYPE.CODE)) return CodePart(block);
        //TODO ADD PHI
        return null;
    }

    public static IR3BaseBlock FunctionPart(IR1BaseBlock block) {
        System.out.println("func: " + block.blockId);
        IR3BaseBlock newBlock = new IR3BaseBlock(Type.Func);
        //add ids
        //then add to childs
        block.nodesOutChilds.stream().map(IR3BaseBlock::doJob_).toList();//TODO use result
        //nodesOutChilds.filter(type==ID) to args
        return null;
    }

    public static IR3BaseBlock BlockPart(IR1BaseBlock block) {

        //need nodesInParents.size() == 1??
        System.out.println("block: " + block.blockId);
        IR3BaseBlock newBlock = new IR3BaseBlock(Type.Block);//TODO more
        return newBlock.addChildsStream(block.nodesOutChilds.stream().map(IR3BaseBlock::doJob_));
    }

    public static IR3BaseBlock JmpPart(IR1BaseBlock block) {//uncond JMP
        System.out.println("jmp: " + block.blockId);
        block.nodesIn.get(0);//куда прыгаем
        return null;
    }

    public static IR3BaseBlock CodePart(IR1BaseBlock block) {
        System.out.println("code: " + block.blockId);
        //may be having phi in nodesOutChilds and create with phi
        List<IR1BaseBlock> childs = block.nodesOutChilds;
        if (Objects.equals(childs.get(0).name, "call")) {//почти всегда call, else ret
            if (Objects.equals(childs.get(1).name, "set")) {
                if (childs.get(3).typeIs(FrontendBaseBlock.TYPE.ID))//arg1 is PHI
                    return IR3Asm.SET(childs.get(2), new IR3BaseBlock(childs.get(3), true));
                IR3BaseBlock arg1 = doJob_(childs.get(3));
                //arg0 - PHI
                //arg1 - BLOCK || CODE
                return IR3Asm.thenConcat(arg1, IR3Asm.SET(childs.get(2), arg1.getRes()));
            }
            //else it's userFunc
            LinkedList<IR3BaseBlock> argsExt = new LinkedList<>();//may be TODO force seqence strong
            //.name - сейчас так, может в будущем какая-то служебная инфа кроме имени функции будет добавляться
            IR3BaseBlock call = IR3Asm.CALL(childs.get(1).name,
                    childs.stream().skip(2).map(i -> {
                        if (i.typeIs(FrontendBaseBlock.TYPE.ID))// that ok
                            return new IR3BaseBlock(i, true);
                        IR3BaseBlock arg = doJob_(i);
                        argsExt.add(arg);
                        return arg.getRes();
                    }));
            return IR3Asm.thenConcat(argsExt.stream(), call);
            //args - (PHI || BLOCK || CODE)*
        }
        if (!Objects.equals(childs.get(0).name, "ret")) {
            System.err.println("bad code part");
            return null;
        }
        //else it's ret
        IR3BaseBlock retBlock = new IR3BaseBlock(Type.Ret);
        if(childs.get(1).typeIs(FrontendBaseBlock.TYPE.PHI))
            return retBlock.addChild(new IR3BaseBlock(childs.get(1), true));
        IR3BaseBlock newBlock = doJob_(childs.get(1));
        //args = BLOCK || CODE
        return IR3Asm.thenConcat(newBlock, retBlock.addChild(newBlock.getRes()));
    }

    public IR3BaseBlock addChild(IR3BaseBlock to) {
        to.parent = this;
        childs.add(to);
        return this;
    }

    public IR3BaseBlock setName(String name) {
        this.name = name;
        return this;
    }

    public IR3BaseBlock addChildsStream(Stream<IR3BaseBlock> argsIds) {
        argsIds.forEach(this::addChild);
        return this;
    }

    private IR3BaseBlock getRes() {

        //TODO
        return null;
    }

    public enum Type {Block, Call, Func, Ret, Assign}
}
