package org.lexasub.IR1.IR1Block;

import org.lexasub.frontend.utils.FrontendBaseBlock;
import org.lexasub.frontend.utils.IdGenerator;

import java.util.*;
import java.util.stream.Stream;

public class IR1BaseBlock {
    public String name;
    public String code;
    public String blockId = IdGenerator.id();
    public FrontendBaseBlock.TYPE type = FrontendBaseBlock.TYPE.BLOCK;
    public LinkedList<IR1BaseBlock> nodesIn = new LinkedList<>();
    public LinkedList<IR1BaseBlock> nodesOut = new LinkedList<>();
    public LinkedList<IR1BaseBlock> nodesInParents = new LinkedList<>();
    public LinkedList<IR1BaseBlock> nodesOutChilds = new LinkedList<>();

    public IR1BaseBlock() {
    }

    public IR1BaseBlock(FrontendBaseBlock v) {
        name = v.name;
        code = v.code;
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

    public static IR1BaseBlock makeFromFrontendBaseBlock(FrontendBaseBlock frontendBlock) {
        return makeFromFrontendBaseBlock(frontendBlock, new HashMap<>());
    }

    private static IR1BaseBlock makeFromFrontendBaseBlock(FrontendBaseBlock frontendBlock, HashMap<String, IR1BaseBlock> _decls) {
        //clone for cancel local modification(mb scopes)(ex local variables)//TODO check
        IR1BaseBlock ir1BB = new IR1BaseBlock(frontendBlock);
        HashMap<String, IR1BaseBlock> decls = (HashMap<String, IR1BaseBlock>) _decls.clone();
        LinkedList<FrontendBaseBlock> fbChilds = frontendBlock.childs;
        int cnt = 0;
        /* if (Objects.equals(frontendBlock.blockId, "zlJ5kusc6q")) {
            System.out.println(frontendBlock.name);
        }*/
        if (ir1BB.type == FrontendBaseBlock.TYPE.FUNC)
            cnt = addFuncArgs(ir1BB, decls, fbChilds.iterator());
        if (ir1BB.type == FrontendBaseBlock.TYPE.CODE)
            codeBlock(ir1BB, decls, fbChilds);
        else
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
        return new LinkedList<>(blocks //skip blocks
                .map(i -> {
                    IR1BaseBlock ir1BaseBlock = findOrCreateBlock(decls, i);
                    if (Objects.equals(ir1BaseBlock.type, FrontendBaseBlock.TYPE.BLOCK))
                        decls.put("res_" + ir1BaseBlock.blockId, ir1BaseBlock);
                    return ir1BaseBlock;
                }).toList());
        //may be last expr - it's return if blockId != ""
    }


    private static void codeBlock(IR1BaseBlock ir1BB, HashMap<String, IR1BaseBlock> decls, LinkedList<FrontendBaseBlock> args) {
        //TODO
        //extract from code ids and maybe add to nodesIn, nodesOut
        FrontendBaseBlock v = args.get(0);
        connectToChilds(new IR1BaseBlock(v), ir1BB);
        int n;
        if (Objects.equals(v.name, "call")) {
            connectToChilds(new IR1BaseBlock(args.get(1)), ir1BB);
            n = 2;
        } else n = 1;
        //skip (ex: call, name)

        args.stream().skip(n).forEach(i -> {
            IR1BaseBlock ir1BaseBlock = findOrCreateBlock(decls, i);
            if (Objects.equals(ir1BaseBlock.type, FrontendBaseBlock.TYPE.BLOCK))
                decls.put("res_" + ir1BaseBlock.blockId, ir1BaseBlock);
            connectTo(ir1BB, ir1BaseBlock);
            //System.out.println(i.name + " " + i.blockId + " " + ir1BaseBlock.blockId);
            //ok on read variable, on write to variable-it's wrong
            //if == "res_...." -> вроде всегда получаем read.
            // write на данном этапе в дереве не будет.(т.к.) auto-return(ex return last expr) not applyed
            // ir1BB.nodesOutChilds.remove(ir1BaseBlock);
            //connectToChilds(ir1BB, ir1BaseBlock);//mb_swap
            //connectTo(ir1BB, ir1BaseBlock);//mb_swap
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
}
