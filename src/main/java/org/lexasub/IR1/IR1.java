package org.lexasub.IR1;

import org.lexasub.frontend.utils.FBB;
import org.lexasub.frontend.utils.IdGenerator;

import java.util.*;
import java.util.stream.Stream;

public class IR1 {
    public String name;
    public String blockId = IdGenerator.id();
    public FBB.TYPE type = FBB.TYPE.BLOCK;
    public List<IR1> nodesIn = new LinkedList<>();
    public List<IR1> nodesOut = new LinkedList<>();
    public List<IR1> nodesInParents = new LinkedList<>();
    public List<IR1> nodesOutChilds = new LinkedList<>();
    private IR1 after_;

    public IR1() {
    }


    public IR1(FBB v) {
        name = v.name;
        type = v.type;
        blockId = v.blockId;
    }

    public IR1(FBB.TYPE type) {
        this.type = type;
    }

    public IR1(FBB.TYPE type, String name) {
        this.type = type;
        this.name = name;
    }

    public IR1(FBB.TYPE afterOrBefore, IR1 block) {
        type = afterOrBefore;
        connectTo(this, block);
    }

    public static IR1 makeFromFBB(FBB frontendBlock) {
        return makeFromFBB(frontendBlock, new HashMap<>());
    }

    private static IR1 makeFromFBB(FBB frontendBlock, HashMap<String, IR1> _decls) {
        //clone for cancel local modification(mb scopes)(ex local variables)//TODO check
        IR1 ir1BB = new IR1(frontendBlock);
        HashMap<String, IR1> decls = (HashMap<String, IR1>) _decls.clone();
        List<FBB> fbChilds = frontendBlock.childs;
        int cnt = 0;
        /* if (Objects.equals(frontendBlock.blockId, "zlJ5kusc6q")) {
            System.out.println(frontendBlock.name);
        }*/
        if (ir1BB.typeIs(FBB.TYPE.CODE)) {
            codeBlock(ir1BB, decls, fbChilds);
            return ir1BB;
        }
        if (ir1BB.typeIs(FBB.TYPE.FUNC))
            cnt = addFuncArgs(ir1BB, decls, fbChilds.iterator());//cnt - how much skip blocks
        getIr1BaseBlock(decls, fbChilds.stream().skip(cnt)).forEach(i -> connectToChilds(i, ir1BB));
        return ir1BB;
    }

    private static IR1 findOrCreateBlock(HashMap<String, IR1> decls, FBB i) {
        IR1 ir1BaseBlock = decls.get(i.name);
        if (ir1BaseBlock == null)
            ir1BaseBlock = makeFromFBB(i, decls);
        return ir1BaseBlock;
        //may be dependence with orig var
    }

    private static LinkedList<IR1> getIr1BaseBlock(HashMap<String, IR1> decls, Stream<FBB> blocks) {
        return new LinkedList<>(
                blocks.map(i -> {
                    IR1 ir1BB = findOrCreateBlock(decls, i);
                    if (ir1BB.typeIs(FBB.TYPE.BLOCK))
                        decls.put("res_" + ir1BB.blockId, ir1BB);
                    return ir1BB;
                }).toList());
        //may be last expr - it's return if blockId != ""
    }

    private static void codeBlock(IR1 ir1BB, HashMap<String, IR1> decls, List<FBB> args) {
        //TODO
        //extract ids and maybe add to nodesIn, nodesOut
        FBB v = args.get(0);
        connectToChilds(new IR1(v), ir1BB);
        int n;
        if (Objects.equals(v.name, "call")) {
            connectToChilds(new IR1(args.get(1)), ir1BB);
            n = 2;
        } else n = 1;
        //skip (ex: call, name)

        args.stream().skip(n).forEach(i -> {
            IR1 ir1BB_ = findOrCreateBlock(decls, i);
            if (ir1BB_.typeIs(FBB.TYPE.BLOCK))
                decls.put("res_" + ir1BB_.blockId, ir1BB_);
            //connectTo(ir1BB, ir1BB_);
            connectToChilds(ir1BB_, ir1BB);
            //System.out.println(i.name + " " + i.blockId + " " + ir1BB_.blockId);
            //ok on read variable, on write to variable-it's wrong
            //if == "res_...." -> вроде всегда получаем read.
            // write на данном этапе в дереве не будет.(т.к.) auto-return(ex return last expr) not applyed
            // ir1BB.nodesOutChilds.remove(ir1BB_);
        });
    }

    private static int addFuncArgs(IR1 ir1BB, HashMap<String, IR1> decls, Iterator<FBB> childsIt) {
        int cnt = 0;
        //System.out.println(ir1BB.name + " " + ir1BB.blockId + " " + ir1BB.blockId);
        while (childsIt.hasNext()) {
            FBB v = childsIt.next();
            if (v.type != FBB.TYPE.ID) break;
            ++cnt;
            IR1 vv = new IR1(v);
            connectToChilds(vv, ir1BB);
            decls.put(v.name, vv);
            //childs должен при type=ID быть пустым
        }
        return cnt;
    }

    public static void connectToChilds(IR1 to, IR1 from) {
        from.nodesOutChilds.add(to);
        to.nodesInParents.add(from);
    }

    public static void connectTo(IR1 to, IR1 from) {
        from.nodesOut.add(to);
        to.nodesIn.add(from);
    }

    public ListIterator<IR1> nodesOutChildsListIterator() {
        return nodesOutChilds.listIterator();
    }

    public ListIterator<IR1> nodesOutListIterator() {
        return nodesOut.listIterator();
    }

    public void copyNodesInsFrom(IR1 ir1Block) {
        nodesIn = ir1Block.nodesIn;
        nodesInParents = ir1Block.nodesInParents;
    }

    public boolean typeIs(FBB.TYPE type_) {
        return type == type_;
    }

    public boolean hasntDeps() {
        return nodesIn.isEmpty();
    }

    public IR1 after() {
        if (after_ == null)
            after_ = new IR1(FBB.TYPE.AFTER, this);//temporary kostyl'
        return after_;
    }
}
