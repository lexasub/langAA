package org.lexasub.IR3;

import org.lexasub.IR1.IR1;
import org.lexasub.frontend.utils.FBB;

import java.util.*;
import java.util.stream.Stream;

public class IR3 {
    private Type type;
    private String name;
    private IR3 parent;
    private LinkedList<IR3> childs = new LinkedList<>();
    private String blockId;

    public IR3(Type type) {
        this.type = type;
    }
    public IR3(Type type, String blockId) {
        this(type);
        this.blockId = blockId;
    }

    public IR3(IR1 i, boolean readFromVar) {//for only type==ID
        //readFromVar - else write to var//TODO
        //if readFromVar == true -> use phi
        if(!readFromVar) {
            setName(i.name);
            this.type = Type.ID;
            this.blockId = i.blockId;
        }
        else {
            //System.out.println(i.nodesOut.get(0).type);//TYPE need PHI_PART
            this.type = Type.BLOCK;
            addChild(transformPhiPart(i)).addChild(new IR3(i, false));
        }

    }

    private IR3 transformPhiPart(IR1 i) {
        IR1 phiPart = i.nodesOut.get(0);
        IR3 newBlock = new IR3(Type.PHI_PART);
        //todo plan for "changing" blockid//linkage from IR1BB to IR3BB

        return newBlock;
    }

    //Make more CODEBLOCKS
    public static IR3 doJob(IR1 block) {
        return doJob_(block.nodesOutChilds.get(0));
    }

    public static IR3 doJob_(IR1 block) {
        if (block.typeIs(FBB.TYPE.BLOCK)) return BlockPart(block);
        if (block.typeIs(FBB.TYPE.FUNC)) return FunctionPart(block);
        if (block.typeIs(FBB.TYPE.CODE)) return CodePart(block);
        if (block.typeIs(FBB.TYPE.JMP)) return JmpPart(block);//TODO cond_jmp
        if (block.typeIs(FBB.TYPE.ID)) return  new IR3(Type.BLOCK);//TODO
        if (block.typeIs(FBB.TYPE.PHI)) return  new IR3(Type.BLOCK);//TODO
        if (block.typeIs(FBB.TYPE.COND_JMP)) return  new IR3(Type.BLOCK);//TODO
        return null;
    }

    public static IR3 FunctionPart(IR1 block) {
        System.out.println("func: " + block.blockId);
        LinkedList<IR3> args = getFuncArgs(block.nodesOutChilds.iterator());//transform ids
        return new IR3(Type.FUNC, block.blockId)
                .setName(block.name)
                .addChildsStream(args.stream())//add functionArgs
                .addChild(new IR3(Type.SPLITTER))//splitter//а нужен ли он??
                .addChildsStream(block.nodesOutChilds.stream().skip(args.size()).map(IR3::doJob_));//then add parts
    }

    private static LinkedList<IR3> getFuncArgs(Iterator<IR1> ir1ChildsIterator) {
        LinkedList<IR3> args = new LinkedList<>();
        while(ir1ChildsIterator.hasNext()){
            IR1 next = ir1ChildsIterator.next();
            if(!next.typeIs(FBB.TYPE.ID)) break;
            args.add(new IR3(next, false));//boolean-hack for normal create
        }
        return args;
    }

    public static IR3 BlockPart(IR1 block) {

        //need nodesInParents.size() == 1??
        System.out.println("block: " + block.blockId);
        IR3 newBlock = new IR3(Type.BLOCK, block.blockId);
        return newBlock.addChildsStream(block.nodesOutChilds.stream().map(IR3::doJob_));
    }

    public static IR3 JmpPart(IR1 block) {//uncond JMP
        System.out.println("jmp: " + block.blockId);
        block.nodesIn.get(0);//куда прыгаем
        return new IR3(Type.BLOCK, block.blockId);//TODO change
    }

    public static IR3 CodePart(IR1 block) {
        System.out.println("code: " + block.blockId);
        //may be having phi in nodesOutChilds and create with phi
        List<IR1> childs = block.nodesOutChilds;
        if (Objects.equals(childs.get(0).name, "call")) {//почти всегда call, else ret
            return tryCheckSetFunc(childs).orElseGet(() -> modifyUserFunc(childs));
        }
        if (!Objects.equals(childs.get(0).name, "ret")) {
            System.err.println("bad code part");
            return null;
        }
        //else it's ret
        IR3 retBlock = new IR3(Type.RET, block.blockId);
        if(childs.get(1).typeIs(FBB.TYPE.PHI))
            return retBlock.addChild(new IR3(childs.get(1), true));
        IR3 newBlock = doJob_(childs.get(1));
        //args = BLOCK || CODE
        return IR3Asm.thenConcat(newBlock, retBlock.addChild(newBlock.getRes()));
    }

    private static IR3 modifyUserFunc(List<IR1> childs) {
        //else it's userFunc
        // may be TODO force seqence strong
        LinkedList<IR3> argsExt = new LinkedList<>();
        //.name - сейчас так, может в будущем какая-то служебная инфа кроме имени функции будет добавляться
        IR3 call = IR3Asm.CALL(childs.get(1).name,
                childs.stream().skip(2).map(i -> {
                    if (i.typeIs(FBB.TYPE.ID))// that ok
                        return new IR3(i, true);
                    IR3 arg = doJob_(i);
                    argsExt.add(arg);
                    return arg.getRes();
                }));
        return IR3Asm.thenConcat(argsExt.stream(), call);
        //args - (PHI || BLOCK || CODE)*
    }

    private static Optional<IR3> tryCheckSetFunc(List<IR1> childs) {
        if (!Objects.equals(childs.get(1).name, "set")) return Optional.empty();
        if (childs.get(3).typeIs(FBB.TYPE.ID))//arg1 is PHI
            return Optional.of(IR3Asm.SET(childs.get(2), new IR3(childs.get(3), true)));
        IR3 arg1 = doJob_(childs.get(3));
        //arg0 - PHI
        //arg1 - BLOCK || CODE
        return Optional.of(IR3Asm.thenConcat(arg1, IR3Asm.SET(childs.get(2), arg1.getRes())));
    }

    public IR3 addChild(IR3 to) {
        to.parent = this;
        childs.add(to);
        return this;
    }

    public IR3 addChildsStream(Stream<IR3> argsIds) {
        argsIds.forEach(this::addChild);
        return this;
    }

    public IR3 setName(String name) {
        this.name = name;
        return this;
    }

    private IR3 getRes() {

        //TODO
        return this;
    }

    public enum Type {ASSIGN, BLOCK, CALL, FUNC, ID, PHI_PART, RET, SPLITTER}
}
