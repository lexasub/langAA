package org.lexasub.IR3;

import org.lexasub.IR1.IR1;
import org.lexasub.frontend.utils.FBB;
import org.lexasub.frontend.utils.IdGenerator;

import java.util.*;
import java.util.stream.Stream;

public class IR3 {
    public Type type;
    public String name;
    public LinkedList<IR3> childs = new LinkedList<>();
    public String blockId = IdGenerator.id();
    public IR3 parent;

    public IR3(Type type) {
        setType(type);
    }

    public IR3(Type type, String blockId) {
        this(type);
        this.blockId = blockId;
    }

    public IR3(IR1 i) {//for only type==ID
        //readFromVar - else write to var//TODO
        //if readFromVar == true -> use phi
        this(Type.ID, i.blockId);
        setName(i.name);
    }

    //Make more CODEBLOCKS
    public static IR3 doJob(IR1 block) {
        return doJob_(block.nodesOutChilds.get(0));
    }

    private static IR3 doJob_(IR1 block) {
        if (block.typeIs(FBB.TYPE.BLOCK)) return BlockPart(block);
        if (block.typeIs(FBB.TYPE.FUNC)) return FunctionPart(block);
        if (block.typeIs(FBB.TYPE.CODE)) return CodePart(block);
        if (block.typeIs(FBB.TYPE.JMP)) return JmpPart(block);
        if (block.typeIs(FBB.TYPE.COND_JMP)) return jmpCondPart(block);

        if (block.typeIs(FBB.TYPE.ID)) return new IR3(Type.ID, block.blockId).setName(block.name);//TODO
        if (block.typeIs(FBB.TYPE.PHI)) return new IR3(Type.BLOCK, block.blockId);//TODO
        return null;
    }

    private static IR3 jmpCondPart(IR1 block) {//TODO
        return new IR3(Type.COND_JMP, block.blockId)
                .addChild(doJob_(block.nodesOutChilds.get(0)))
                .addChild(doJob_(block.nodesOutChilds.get(1)));
    }

    public static IR3 FunctionPart(IR1 block) {
        System.out.println("func: " + block.blockId);
        LinkedList<IR3> args = getFuncArgs(block.nodesOutChilds.iterator());//transform ids
        return new IR3(Type.FUNC, block.blockId)
                .setName(block.name)
                .addChildsStream(args.stream())//add functionArgs
               // .addChild(new IR3(Type.SPLITTER))//splitter//а нужен ли он??
                .addChildsStream(block.nodesOutChilds.stream().skip(args.size()).map(IR3::doJob_));//then add parts
    }

    private static LinkedList<IR3> getFuncArgs(Iterator<IR1> ir1ChildsIterator) {
        LinkedList<IR3> args = new LinkedList<>();
        while (ir1ChildsIterator.hasNext()) {
            IR1 next = ir1ChildsIterator.next();
            if (!next.typeIs(FBB.TYPE.ID)) break;
            args.add(new IR3(next));//boolean-hack for normal create
        }
        return args;
    }

    private static IR3 BlockPart(IR1 block) {

        //need nodesInParents.size() == 1??
        System.out.println("block: " + block.blockId);
        IR3 newBlock = new IR3(Type.BLOCK, block.blockId);
        return newBlock.addChildsStream(block.nodesOutChilds.stream().map(IR3::doJob_));
    }

    private static IR3 JmpPart(IR1 block) {//uncond JMP
        return new IR3(Type.JMP, block.blockId)
                .addChild(new IR3(block.nodesIn.get(0).nodesIn.get(0)));//TODO change//replace last nodesIN.get(0) to good block after another block
    }

    private static IR3 CodePart(IR1 block) {
        System.out.println("code: " + block.blockId);
        //may be having phi in nodesOutChilds and create with phi
        List<IR1> childs = block.nodesOutChilds;
        if (Objects.equals(childs.get(0).name, "call")) {//почти всегда call, else ret
            return tryCheckSetFunc(childs).orElseGet(() -> modifyCallFunc(childs));
        }
        if (!Objects.equals(childs.get(0).name, "ret")) {
            System.err.println("bad code part");
            return null;
        }
        //else it's ret
        IR3 retBlock = new IR3(Type.RET, block.blockId);
        if (childs.get(1).typeIs(FBB.TYPE.PHI))
            return transformPhi(retBlock, childs.get(1));
        IR3 newBlock = doJob_(childs.get(1));
        //args = BLOCK || CODE
        return IR3Asm.thenConcat(newBlock, retBlock.addChild(newBlock.getRes()));
    }

    private static IR3 modifyCallFunc(List<IR1> childs) {
        //else it's userFunc
        // may be TODO force seqence strong
        LinkedList<IR3> argsExt = new LinkedList<>();
        //.name - сейчас так, может в будущем какая-то служебная инфа кроме имени функции будет добавляться
        IR3 call = IR3Asm.CALL(childs.get(1).name,
                childs.stream().skip(2).map(i -> {
                    // if (i.typeIs(FBB.TYPE.ID))//  that ok //ID-> PHI??
                    //   return new IR3(i, true);
                    if (i.typeIs(FBB.TYPE.PHI)) {
                        IR3 id = new IR3(i);
                        argsExt.add(generatePhiPart(i.nodesOut.get(0), id));
                        return id;
                    }
                    if (i.typeIs(FBB.TYPE.ID)) {
                        return new IR3(i);
                    }
                    IR3 arg = doJob_(i);
                    argsExt.add(arg);
                    return arg.getRes();
                }));
        if (argsExt.isEmpty()) return call;
        return IR3Asm.thenConcat(argsExt.stream(), call);
        //args - (PHI || BLOCK || CODE)*
    }

    private static Optional<IR3> tryCheckSetFunc(List<IR1> childs) {
        if (!Objects.equals(childs.get(1).name, "set")) return Optional.empty();
        //  if (childs.get(3).typeIs(FBB.TYPE.ID))//arg1 is PHI //TODO check!!!!!
        //     return Optional.of(IR3Asm.SET(childs.get(2), modifyPhiPart(childs.get(3))));
        IR3 arg1 = doJob_(childs.get(3));
        //arg0 - PHI
        //arg1 - BLOCK || CODE
        assert arg1 != null;
        return Optional.of(IR3Asm.thenConcat(arg1, IR3Asm.SET(childs.get(2), arg1.getRes())));
    }

    static private IR3 transformPhi(IR3 retBlock, IR1 i) {//TODO edit//FBB.type.PHI

        //System.out.println(i.nodesOut.get(0).type);//TYPE need PHI_PART
        /*
        name=v4
        blockid=".."
        dependences...
         */
        //addChild(new_i).addChild(new IR3(i, false));
        IR3 phiPart = generatePhiPart(i.nodesOut.get(0), new IR3(i));//mb add types
        IR3 reg = new IR3(i);
        //todo plan for "changing" blockid//linkage from IR1BB to IR3BB
        return IR3Asm.thenConcat(phiPart, retBlock.addChild(reg));//reg;
    }
    private static IR3 generatePhiPart1(IR1 ir1, IR3 reg) {
        IR3 phi = new IR3(Type.PHI);
        ir1.nodesIn.stream()//NodesOut->in
                .filter(i->!i.typeIs(FBB.TYPE.PHI)).map(i -> new IR3(Type.ID, i.blockId).setName(i.name))
                //   .map(i -> new IR3((i.typeIs(FBB.TYPE.PHI) ? Type.ID : Type.BLOCK), i.blockId).setName(i.name))//type BLOCK??mb
                .map(i->Objects.equals(i.name, reg.name)?i: reg)//partiotional kostyl'
                .forEach(phi::addChild);
        return IR3Asm.SET(reg, phi);
    }

    private static IR3 generatePhiPart(IR1 ir1, IR3 reg) {
        return generatePhiPart1(ir1, reg);
        /*IR3 phi = new IR3(Type.PHI);
        IR3 rrr = new IR3(reg);
        ir1.nodesIn.stream()//NodesOut->in
                //.filter(i->!i.typeIs(FBB.TYPE.PHI)).map(i -> new IR3(Type.ID, i.blockId).setName(i.name))
                .map(i -> new IR3((i.typeIs(FBB.TYPE.PHI) ? Type.ID : Type.BLOCK), i.blockId).setName(i.name))//type BLOCK??mb
                .map(i->Objects.equals(i.name, rrr.name)?i:rrr)//partiotional kostyl'
                .forEach(phi::addChild);
        return IR3Asm.SET(rrr, phi);*/
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

    public IR3 getRes() {
        if(typeIs(Type.BLOCK)) return getResForBlock();
        if(typeIs(Type.CALL)) return getResForCall();
        if(typeIs(Type.ID)) return this;
        return null;
    }

    private IR3 getResForCall() {
        assert parent == null;
        IR3 child = new IR3(type, blockId)
                .setName(name).moveChildsFrom(this);
        assignGen(child, this).setName(null);
        return childs.get(0);
    }

    private IR3 getResForBlock() {
        IR3 cur = this;
        while(cur.childs.getLast().typeIs(Type.BLOCK))//не учитываем пока jmps
            cur = cur.childs.getLast();
        IR3 child = cur.childs.getLast();
        return getResPart(child, cur, cur.childs.size() - 1);
    }

    private static IR3 getResPart(IR3 child, IR3 cur, int childId) {
        IR3 assign = assignGen(child, new IR3(Type.ASSIGN));
        cur.childs.set(childId, assign);
        return assign.childs.get(0);
    }

    private IR3 moveChildsFrom(IR3 ir3) {
        childs = ir3.childs;//copy childs
        ir3.childs = new LinkedList<>();//nulling prevowner child's
        childs.forEach(i->i.parent = this);//link
        return this;
    }

    private static IR3 assignGen(IR3 child, IR3 obj) {
        return obj.setType(Type.ASSIGN).addChild(new IR3(Type.ID).setName(IdGenerator.id())).addChild(child);
    }

    private IR3 setType(Type _type) {
        type = _type;
        return this;
    }


    public boolean typeIs(Type _type) {
        return type == _type;
    }

    public enum Type {ASSIGN, BLOCK, CALL, FUNC, ID, PHI, RET, COND_JMP, JMP}
}
