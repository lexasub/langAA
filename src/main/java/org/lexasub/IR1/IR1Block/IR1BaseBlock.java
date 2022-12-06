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
    public IR1BaseBlock(){}
    public IR1BaseBlock(FrontendBaseBlock v) {
        name = v.name;
        code = v.code;
        type = v.type;
        blockId = v.blockId;
    }

    public IR1BaseBlock(FrontendBaseBlock.TYPE type) {
        this.type = type;
    }


    public static IR1BaseBlock makeFromFrontendBaseBlock(FrontendBaseBlock frontendBlock) {
        return makeFromFrontendBaseBlock(frontendBlock, new HashMap<>(), new IR1BaseBlock(frontendBlock));
    }
    private static IR1BaseBlock makeFromFrontendBaseBlock(FrontendBaseBlock frontendBlock, HashMap<String, IR1BaseBlock> decls, IR1BaseBlock ir1BB) {
        //clone for cancel local modification(mb scopes)(ex local variables)//TODO check
        getIr1BaseBlock(frontendBlock, decls, ir1BB).forEach(ir1BaseBlock -> connectToChilds(ir1BaseBlock, ir1BB));
        return ir1BB;
    }


    private static LinkedList<IR1BaseBlock> getIr1BaseBlock(FrontendBaseBlock frontendBlock, HashMap<String, IR1BaseBlock> _decls, IR1BaseBlock ir1BB) {
        HashMap<String, IR1BaseBlock> decls = (HashMap<String, IR1BaseBlock>) _decls.clone();
        LinkedList<FrontendBaseBlock> fbChilds = frontendBlock.childs;
        int cnt = addFuncArgs(ir1BB, decls, fbChilds.iterator());
        autoDeclare(ir1BB, decls, fbChilds);
     //   if (ir1BB.type == FrontendBaseBlock.TYPE.CODE) cnt+=2;
            Stream<IR1BaseBlock> ir1BaseBlockStream = fbChilds.stream().skip(cnt) //skip funcArgs
                .map(i -> {
                    IR1BaseBlock ir1BaseBlock;
                    ir1BaseBlock = makeFromFrontendBaseBlock(i, decls, new IR1BaseBlock(i));
                    if (Objects.equals(ir1BaseBlock.type, FrontendBaseBlock.TYPE.BLOCK))
                        decls.put("res_" + ir1BaseBlock.blockId, ir1BaseBlock);
                    return ir1BaseBlock;
                });
        //may be last expr - it's return if blockId != ""
        return new LinkedList<>(ir1BaseBlockStream.toList());
    }

    private static void autoDeclare(IR1BaseBlock ir1BB, HashMap<String, IR1BaseBlock> decls, LinkedList<FrontendBaseBlock> args) {
          if (ir1BB.type == FrontendBaseBlock.TYPE.CODE) {
            //TODO
            //extract from code ids and maybe add to nodesIn, nodesOut
            connectTo(ir1BB, new IR1BaseBlock(args.get(0)));
            connectTo(ir1BB, new IR1BaseBlock(args.get(1)));
            //skip (ex: call, name)
            args.stream().skip(2).forEach(i->{
                IR1BaseBlock ir1BaseBlock = decls.get(i.name);

                if(ir1BaseBlock==null)
                    return;
                System.out.println(i.name + " " + i.blockId + " " + ir1BaseBlock.blockId);
                //ok on read variable, on write to variable-it's wrong
                //if == "res_...." -> вроде всегда получаем read.
                // write на данном этапе в дереве не будет.(т.к.) auto-return(ex return last expr) not applyed
                // ir1BB.nodesOutChilds.remove(ir1BaseBlock);
                //connectToChilds(ir1BB, ir1BaseBlock);//mb_swap
                //connectTo(ir1BB, ir1BaseBlock);//mb_swap
            });
        }
    }

    private static int addFuncArgs(IR1BaseBlock ir1BB, HashMap<String, IR1BaseBlock> decls, Iterator<FrontendBaseBlock> childsIt) {
        int cnt = 0;
        //System.out.println(ir1BB.name + " " + ir1BB.blockId + " " + ir1BB.blockId);
        if(ir1BB.type == FrontendBaseBlock.TYPE.FUNC) {
            while (childsIt.hasNext()){
                FrontendBaseBlock v = childsIt.next();
                if(v.type != FrontendBaseBlock.TYPE.ID) break;
                ++cnt;
                IR1BaseBlock vv = new IR1BaseBlock(v);
                connectToChilds(vv, ir1BB);
                decls.put(v.name, vv);
                //childs должен при type=ID быть пустым
            }
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
