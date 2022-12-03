package org.lexasub.IR1.IR1Block;

import org.lexasub.frontend.utils.FrontendBaseBlock;
import java.util.*;
import java.util.stream.Stream;

public class IR1BaseBlock {
    public String name;
    public String code;
    public String blockId;
    public FrontendBaseBlock.TYPE type;
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
        return makeFromFrontendBaseBlock(frontendBlock, new HashMap<>());
    }
    private static IR1BaseBlock makeFromFrontendBaseBlock(FrontendBaseBlock frontendBlock, HashMap<String, IR1BaseBlock> decls) {
        //clone for cancel local modification(mb scopes)(ex local variables)//TODO check
        IR1BaseBlock ir1BB = new IR1BaseBlock(frontendBlock);
        getIr1BaseBlock(frontendBlock, decls, ir1BB).forEach(ir1BaseBlock -> connectToChilds(ir1BaseBlock, ir1BB));
        return ir1BB;
    }


    private static LinkedList<IR1BaseBlock> getIr1BaseBlock(FrontendBaseBlock frontendBlock, HashMap<String, IR1BaseBlock> _decls, IR1BaseBlock ir1BB) {
        HashMap<String, IR1BaseBlock> decls = (HashMap<String, IR1BaseBlock>) _decls.clone();
        LinkedList<FrontendBaseBlock> fbChilds = frontendBlock.childs;
        int cnt = addFuncArgs(ir1BB, decls, fbChilds.iterator());
        autoDeclare(ir1BB, decls);
        Stream<IR1BaseBlock> ir1BaseBlockStream = fbChilds.stream().skip(cnt) //skip funcArgs
                .map(i -> {
                    IR1BaseBlock ir1BaseBlock = makeFromFrontendBaseBlock(i, decls);
                    if (Objects.equals(ir1BaseBlock.type, FrontendBaseBlock.TYPE.BLOCK))
                        decls.put("res_" + ir1BaseBlock.blockId, ir1BaseBlock);
                    return ir1BaseBlock;
                });
        //may be last expr - it's return if blockId != ""
        return new LinkedList<>(ir1BaseBlockStream.toList());
    }

    private static void autoDeclare(IR1BaseBlock ir1BB, HashMap<String, IR1BaseBlock> decls) {
        if (!Objects.equals(ir1BB.code, "")) {
            //TODO
            //extract from code ids and maybe add to nodesIn, nodesOut
            Arrays.stream(ir1BB.code.split(" |\\(|\\)|,")).forEach(i->{
                IR1BaseBlock ir1BaseBlock = decls.get(i);
                if(ir1BaseBlock==null)
                    return;
                //ok on read variable, on write to variable-it's wrong
                //if == "res_...." -> вроде всегда получаем read.
                // write на данном этапе в дереве не будет.(т.к.) auto-return(ex return last expr) not applyed
                connectTo(ir1BB, ir1BaseBlock);//mb_swap
            });
        }
    }

    private static int addFuncArgs(IR1BaseBlock ir1BB, HashMap<String, IR1BaseBlock> decls, Iterator<FrontendBaseBlock> childsIt) {
        int cnt = 0;
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
    private static void connectTo(IR1BaseBlock to, IR1BaseBlock from) {
        from.nodesOut.add(to);
        to.nodesIn.add(from);
    }
}
