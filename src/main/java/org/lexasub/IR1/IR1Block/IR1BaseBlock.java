package org.lexasub.IR1.IR1Block;

import org.lexasub.frontend.utils.FrontendBaseBlock;
import org.lexasub.frontend.utils.IdGenerator;

import java.util.*;
import java.util.stream.Stream;

public class IR1BaseBlock {
    public String name;
    public String blockId = IdGenerator.id();
    public FrontendBaseBlock.TYPE type = FrontendBaseBlock.TYPE.BLOCK;
    public List<IR1BaseBlock> nodesIn = new LinkedList<>();
    public List<IR1BaseBlock> nodesOut = new LinkedList<>();
    public List<IR1BaseBlock> nodesInParents = new LinkedList<>();
    public List<IR1BaseBlock> nodesOutChilds = new LinkedList<>();
    private IR1BaseBlock after_;

    public IR1BaseBlock() {
    }


    public IR1BaseBlock(FrontendBaseBlock v) {
        name = v.name;
        type = v.type;
        blockId = v.blockId;
    }

    public IR1BaseBlock(FrontendBaseBlock.TYPE type) {
        this.type = type;
    }

    public IR1BaseBlock(FrontendBaseBlock.TYPE type, String name) {
        this.type = type;
        this.name = name;
    }

    public IR1BaseBlock(FrontendBaseBlock.TYPE afterOrBefore, IR1BaseBlock block) {
        type = afterOrBefore;
        connectTo(this, block);
    }

    public static IR1BaseBlock makeFromFrontendBaseBlock(FrontendBaseBlock frontendBlock) {
        return makeFromFrontendBaseBlock(frontendBlock, new HashMap<>());
    }

    private static IR1BaseBlock makeFromFrontendBaseBlock(FrontendBaseBlock frontendBlock, HashMap<String, IR1BaseBlock> _decls) {
        //clone for cancel local modification(mb scopes)(ex local variables)//TODO check
        IR1BaseBlock ir1BB = new IR1BaseBlock(frontendBlock);
        HashMap<String, IR1BaseBlock> decls = (HashMap<String, IR1BaseBlock>) _decls.clone();
        List<FrontendBaseBlock> fbChilds = frontendBlock.childs;
        int cnt = 0;
        /* if (Objects.equals(frontendBlock.blockId, "zlJ5kusc6q")) {
            System.out.println(frontendBlock.name);
        }*/
        if (ir1BB.typeIs(FrontendBaseBlock.TYPE.CODE)) {
            codeBlock(ir1BB, decls, fbChilds);
            return ir1BB;
        }
        if (ir1BB.typeIs(FrontendBaseBlock.TYPE.FUNC))
            cnt = addFuncArgs(ir1BB, decls, fbChilds.iterator());//cnt - how much skip blocks
        getIr1BaseBlock(decls, fbChilds.stream().skip(cnt)).forEach(i -> connectToChilds(i, ir1BB));
        return ir1BB;
    }

    private static IR1BaseBlock findOrCreateBlock(HashMap<String, IR1BaseBlock> decls, FrontendBaseBlock i) {
        IR1BaseBlock ir1BaseBlock = decls.get(i.name);
        if (ir1BaseBlock == null)
            ir1BaseBlock = makeFromFrontendBaseBlock(i, decls);
        return ir1BaseBlock;
        //may be dependence with orig var
    }

    private static LinkedList<IR1BaseBlock> getIr1BaseBlock(HashMap<String, IR1BaseBlock> decls, Stream<FrontendBaseBlock> blocks) {
        return new LinkedList<>(
                blocks.map(i -> {
                    IR1BaseBlock ir1BB = findOrCreateBlock(decls, i);
                    if (ir1BB.typeIs(FrontendBaseBlock.TYPE.BLOCK))
                        decls.put("res_" + ir1BB.blockId, ir1BB);
                    return ir1BB;
                }).toList());
        //may be last expr - it's return if blockId != ""
    }

    private static void codeBlock(IR1BaseBlock ir1BB, HashMap<String, IR1BaseBlock> decls, List<FrontendBaseBlock> args) {
        //TODO
        //extract ids and maybe add to nodesIn, nodesOut
        FrontendBaseBlock v = args.get(0);
        connectToChilds(new IR1BaseBlock(v), ir1BB);
        int n;
        if (Objects.equals(v.name, "call")) {
            connectToChilds(new IR1BaseBlock(args.get(1)), ir1BB);
            n = 2;
        } else n = 1;
        //skip (ex: call, name)

        args.stream().skip(n).forEach(i -> {
            IR1BaseBlock ir1BB_ = findOrCreateBlock(decls, i);
            if (ir1BB_.typeIs(FrontendBaseBlock.TYPE.BLOCK))
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

    private static int addFuncArgs(IR1BaseBlock ir1BB, HashMap<String, IR1BaseBlock> decls, Iterator<FrontendBaseBlock> childsIt) {
        int cnt = 0;
        //System.out.println(ir1BB.name + " " + ir1BB.blockId + " " + ir1BB.blockId);
        while (childsIt.hasNext()) {
            FrontendBaseBlock v = childsIt.next();
            if (v.type != FrontendBaseBlock.TYPE.ID) break;
            ++cnt;
            IR1BaseBlock vv = new IR1BaseBlock(v);
            connectToChilds(vv, ir1BB);
            decls.put(v.name, vv);
            //childs должен при type=ID быть пустым
        }
        return cnt;
    }

    public static void connectToChilds(IR1BaseBlock to, IR1BaseBlock from) {
        from.nodesOutChilds.add(to);
        to.nodesInParents.add(from);
    }

    public static void connectTo(IR1BaseBlock to, IR1BaseBlock from) {
        from.nodesOut.add(to);
        to.nodesIn.add(from);
    }

    public ListIterator<IR1BaseBlock> nodesOutChildsListIterator() {
        return nodesOutChilds.listIterator();
    }

    public ListIterator<IR1BaseBlock> nodesOutListIterator() {
        return nodesOut.listIterator();
    }

    public void copyNodesInsFrom(IR1BaseBlock ir1Block) {
        nodesIn = ir1Block.nodesIn;
        nodesInParents = ir1Block.nodesInParents;
    }

    public boolean typeIs(FrontendBaseBlock.TYPE type_) {
        return type == type_;
    }

    public boolean hasntDeps() {
        return nodesIn.isEmpty();
    }

    public IR1BaseBlock after() {
        if (after_ == null)
            after_ = new IR1BaseBlock(FrontendBaseBlock.TYPE.AFTER, this);//temporary kostyl'
        return after_;
    }
}
