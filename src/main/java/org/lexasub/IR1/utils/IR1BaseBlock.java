package org.lexasub.IR1.utils;

import org.lexasub.frontend.utils.FrontendBaseBlock;

import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.Stream;

public class IR1BaseBlock {
    private String name;
    private String code;
    private String blockId;
    private FrontendBaseBlock.TYPE type;
    LinkedList<IR1BaseBlock> nodesIn = new LinkedList<>();
    LinkedList<IR1BaseBlock> nodesOut = new LinkedList<>();

    static boolean jsonize = false;
    public static IR1BaseBlock makeFromFrontendBaseBlock(FrontendBaseBlock frontendBlock) {
        return makeFromFrontendBaseBlock(frontendBlock, new HashMap<>());
    }
    private static IR1BaseBlock makeFromFrontendBaseBlock(FrontendBaseBlock frontendBlock, final HashMap<String, IR1BaseBlock> _decls) {
        //clone for cancel local modification(mb scopes)(ex local variables)//TODO check
        IR1BaseBlock ir1BB = new IR1BaseBlock();
        LinkedList<IR1BaseBlock> childs = getIr1BaseBlock(frontendBlock, _decls, ir1BB);
        childs.forEach(i->{
            ir1BB.nodesOut.add(i);
            i.nodesIn.add(ir1BB);
        });
        return ir1BB;
    }

    private static LinkedList<IR1BaseBlock> getIr1BaseBlock(FrontendBaseBlock frontendBlock, final HashMap<String, IR1BaseBlock> _decls, IR1BaseBlock ir1BB) {
        HashMap<String, IR1BaseBlock> decls = (HashMap<String, IR1BaseBlock>) _decls.clone();
        ir1BB.name = frontendBlock.name;
        ir1BB.code = frontendBlock.code;
        ir1BB.type = frontendBlock.type;
        ir1BB.blockId = frontendBlock.blockId;
        // name -> addSome
        int cnt = addFuncArgs(ir1BB, decls, frontendBlock.childs.iterator());
        autoDeclare(ir1BB, decls);
        Stream<IR1BaseBlock> ir1BaseBlockStream = frontendBlock.childs.stream().skip(cnt) //skip funcArgs
                .map(i -> {
                    IR1BaseBlock ir1BaseBlock = makeFromFrontendBaseBlock(i, decls);
                    if (!Objects.equals(i.blockId, ""))
                        decls.put("res_" + i.blockId, ir1BaseBlock);
                    return ir1BaseBlock;
                });
        //may be last expr - it's return if blockId != ""
        return new LinkedList<>(ir1BaseBlockStream.toList());
    }

    private static void autoDeclare(IR1BaseBlock ir1BB, HashMap<String, IR1BaseBlock> decls) {
        if(!Objects.equals(ir1BB.name, ""))
            decls.put(ir1BB.name, ir1BB);
        else if (!Objects.equals(ir1BB.code, "")) {
            //TODO
            //extract from code ids and maybe add to nodesIn, nodesOut
            Arrays.stream(ir1BB.code.split(" |\\(|\\)")).forEach(i->{
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
    }

    private static int addFuncArgs(IR1BaseBlock ir1BB, HashMap<String, IR1BaseBlock> decls, Iterator<FrontendBaseBlock> childsIt) {
        int cnt = 0;
        if(ir1BB.type == FrontendBaseBlock.TYPE.FUNC) {
            while (childsIt.hasNext()){
                FrontendBaseBlock v = childsIt.next();
                if(v.type != FrontendBaseBlock.TYPE.ID) break;
                ++cnt;
                IR1BaseBlock vv = new IR1BaseBlock();
                vv.nodesIn.add(ir1BB);
                ir1BB.nodesOut.add(vv);
                vv.name = v.name;
                vv.code = v.code;
                vv.type = v.type;
                vv.blockId = v.blockId;
                decls.put(v.name, vv);
                //childs должен при type=ID быть пустым
            }
        }
        return cnt;
    }

    private String r(String a){
        return (jsonize)?('"' + a + '"'):a;
    }
    public void dump(String t, StringBuilder sb) {//TODO
        BiFunction<String, String, StringBuilder> v = (String a, String b) -> sb.append(
                t.concat(r(a) + ":" + r(b) + ((jsonize)?",":"") + "\n")
        );
/*
        v.apply("name", name);
        v.apply("code", code);
        v.apply("blockId", blockId);
        v.apply("type", String.valueOf(type));
        // v.apply("parent:" + ((parent == null) ? null : parent.getBlockId()));
        if (!nodesOut.isEmpty()) {
            if(jsonize)
                sb.append(t.concat("\"nodesOut\":{\n"));
            else
                sb.append(t.concat("nodesOut:{\n"));
            nodesOut.forEach(i -> i.dump(t + "\t", sb));
            sb.append(t.concat("}"+((jsonize)?",":"")+"\n"));
        } else {
            if(jsonize)
                sb.append(t.concat("\"childs\":{},\n"));
            else
                sb.append(t.concat("childs:{}\n"));
        }

 */
    }

    public void serialize(StringBuilder sb1) {
    }

}
