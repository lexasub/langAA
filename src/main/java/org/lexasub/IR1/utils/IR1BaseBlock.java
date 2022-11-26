package org.lexasub.IR1.utils;

import org.lexasub.frontend.utils.FrontendBaseBlock;

import java.util.*;
import java.util.stream.Stream;

public class IR1BaseBlock {
    private String name;
    private String code;
    private String blockId;
    private FrontendBaseBlock.TYPE type;
    LinkedList<IR1BaseBlock> nodesIn = new LinkedList<>();
    LinkedList<IR1BaseBlock> nodesOut = new LinkedList<>();
    public static IR1BaseBlock makeFromFrontendBaseBlock(FrontendBaseBlock frontendBlock) {
        return makeFromFrontendBaseBlock(frontendBlock,  new HashMap<>());
    }
    public static IR1BaseBlock makeFromFrontendBaseBlock(FrontendBaseBlock frontendBlock, final HashMap<String, IR1BaseBlock> _decls) {
        //clone for cancel local modification(mb scopes)(ex local variables)//TODO check
        HashMap<String, IR1BaseBlock> decls = (HashMap<String, IR1BaseBlock>) _decls.clone();
        IR1BaseBlock ir1BB = new IR1BaseBlock();
        ir1BB.name = frontendBlock.name;
        ir1BB.code = frontendBlock.code;
        ir1BB.type = frontendBlock.type;
        ir1BB.blockId = frontendBlock.blockId;
        // name -> addSome
        if(!Objects.equals(ir1BB.name, ""))
            decls.put(ir1BB.name, ir1BB);
        else if (!Objects.equals(ir1BB.code, "")) {
            //TODO
            //extract from code ids and maybe add to nodesIn, nodesOut
            Arrays.stream(ir1BB.code.split(" ")).forEach(i->{
                IR1BaseBlock ir1BaseBlock = decls.get(i);
                if(ir1BaseBlock==null)
                    return;
                else {//ok on read variable, on write to variable-it's wrong
                    //if == "res_...." -> вроде всегда получаем read.
                    // write на данном этапе в дереве не будет.(т.к.) auto-return(ex return last expr) not applyed
                    ir1BB.nodesIn.add(ir1BaseBlock);
                    ir1BaseBlock.nodesOut.add(ir1BB);
                }
            });
        }
        Stream<IR1BaseBlock> ir1BaseBlockStream = frontendBlock.childs.stream()
                .map(i -> {
                    IR1BaseBlock ir1BaseBlock = makeFromFrontendBaseBlock(i, decls);
                    if (!Objects.equals(i.blockId, ""))
                        decls.put("res_" + i.blockId, ir1BaseBlock);
                    return ir1BaseBlock;
                });
        LinkedList<IR1BaseBlock> childs = new LinkedList<>(ir1BaseBlockStream.toList());
        //may be last expr - it's return if blockId != ""
        return ir1BB;
    }

    public void dump(String s, StringBuilder sb) {
    }

    public void serialize(StringBuilder sb1) {
    }

}
