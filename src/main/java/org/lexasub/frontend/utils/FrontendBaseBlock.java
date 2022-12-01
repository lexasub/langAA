package org.lexasub.frontend.utils;

import org.antlr.v4.misc.OrderedHashMap;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;

public class FrontendBaseBlock {

    public FrontendBaseBlock parent = null;
    public String name = "";
    public String code = "";
    public FrontendBaseBlock.TYPE type = null;
    public LinkedList<FrontendBaseBlock> childs = new LinkedList<>();
    public String blockId = IdGenerator.id();
    static boolean jsonize = false;
    public FrontendBaseBlock() {

    }

    public FrontendBaseBlock(FrontendBaseBlock obj) {//blockId
        this.parent = obj.parent;
        this.name = obj.name;
        this.code = obj.code;
        this.type = obj.type;
        this.childs = new LinkedList<>(childs.stream().map(i -> new FrontendBaseBlock(i)).toList());
    }

    public FrontendBaseBlock(String name, String code, String blockId, String type) {
        this.name = name;
        this.code = code;
        this.blockId = blockId;
        if(!Objects.equals(type, "null"))
            this.type = TYPE.valueOf(type);
    }

    public static FrontendBaseBlock spawnID(String id, FrontendBaseBlock myBlock) {
        FrontendBaseBlock fbb = new FrontendBaseBlock();
        fbb.type = FrontendBaseBlock.TYPE.ID;
        fbb.name = id;
        fbb.parent = myBlock;
        return fbb;
    }

    private String r(String a){
        return (jsonize)?('"' + a + '"'):a;
    }
    public void dump(String t, StringBuilder sb) {
        BiFunction<String, String, StringBuilder> v = (String a, String b) -> sb.append(
                t.concat(r(a) + ":" + r(b) + ((jsonize)?",":"") + "\n")
        );

        v.apply("name", name);
        v.apply("code", code);
        v.apply("blockId", blockId);
        v.apply("type", String.valueOf(type));
       // v.apply("parent:" + ((parent == null) ? null : parent.getBlockId()));
        if (!childs.isEmpty()) {
            if(jsonize)
                sb.append(t.concat("\"childs\":{\n"));
            else
                sb.append(t.concat("childs:{\n"));
            childs.forEach(i -> i.dump(t + "\t", sb));
            sb.append(t.concat("}"+((jsonize)?",":"")+"\n"));
        } else {
           if(jsonize)
               sb.append(t.concat("\"childs\":{},\n"));
           else
               sb.append(t.concat("childs:{}\n"));
        }
    }

    public void addChild(FrontendBaseBlock child) {
        childs.add(child);
    }

    public void declareVariable(String type, String name) {
        addChild(spawnID(name, this));//todo add type
    }

    public String returnRes() {
        if (type == TYPE.ID) return name;//MayBeBad
        return "res_" + blockId;
    }

    public String begin() {
        return "begin_" + blockId;
    }

    public String end() {
        return "end_" + blockId;
    }

    public FrontendBaseBlock CONTINUE() {//TODO
        return null;
    }

    public FrontendBaseBlock BREAK() {//TODO
        return null;
    }

    public void declareVariable(String i) {
        addChild(spawnID(i, this));
    }

    public void serialize(StringBuilder sb) {
        Function<String, StringBuilder> v = (String s) -> sb.append(s + "\n");
        childs.forEach(i -> {
            v.apply(i.name);
            v.apply(i.code);
            v.apply(i.blockId);
            v.apply(String.valueOf(i.type));
            v.apply((i.parent == null) ? "" : i.parent.blockId);
            v.apply(" ");
        });
        childs.forEach(i -> i.serialize(sb));
    }

    public static FrontendBaseBlock deserialize(String[] split) {
        OrderedHashMap<String, LinkedList<String>> parentChild = new OrderedHashMap<>();
        List<FrontendBaseBlock> blocks = Arrays.stream(split)
                .map(i -> i.split("\n"))
                .map(i ->
                {
                    if (parentChild.get(i[4]) == null)
                        parentChild.put(i[4], new LinkedList());
                    parentChild.get(i[4]).add(i[2]);
                    return new FrontendBaseBlock(i[0], i[1], i[2], i[3]);
                }
        ).toList();
        blocks.forEach(
                i -> {
                    LinkedList<String> l = parentChild.get(i.blockId);
                    if(l != null)
                        l.forEach(j -> {
                            FrontendBaseBlock v = blocks.stream().filter(k -> Objects.equals(k.blockId, j)).findFirst().get();
                            i.addChild(v);
                            v.parent = i;
                        });
                }
        );

        FrontendBaseBlock myBlock = new FrontendBaseBlock();
        blocks.stream()
                .filter(i->i.parent == null)
                .forEach(i->{
                    i.parent = myBlock;
                    myBlock.addChild(i);
                });
        return myBlock;
    }

    public enum TYPE {FUNC, BLOCK, CODE, ID, LAMBDA}//ID or variable??//TODO
}
