package org.lexasub.frontend.utils;

import java.util.LinkedList;
import java.util.Objects;
import java.util.function.Function;

public class FrontendBaseBlock {
    public FrontendBaseBlock parent;
    public String name = "";
    public String code = "";
    public TYPE type;
    LinkedList<FrontendBaseBlock> childs = new LinkedList<>();
    private String blockId = "";

    public FrontendBaseBlock() {

    }

    public FrontendBaseBlock(FrontendBaseBlock obj) {
        this.parent = obj.parent;
        this.name = obj.name;
        this.childs = new LinkedList<>(childs.stream().map(i -> new FrontendBaseBlock(i)).toList());
    }

    public void dump(String t, StringBuilder sb) {
        Function<String, StringBuilder> v = (String s) -> sb.append(t.concat(s + "\n"));

        v.apply("name:" + name);
        v.apply("code:" + code);
        v.apply("blockId:" + blockId);
        v.apply("{");
        childs.forEach(i -> i.dump(t + "\t", sb));
        v.apply("}");
    }

    public void addChild(FrontendBaseBlock child) {
        childs.add(child);
    }

    public void declareVariable(String type, String name) {
    }

    public String returnRes() {
        return "res_" + getBlockId();
    }

    public String begin() {
        return "begin_" + getBlockId();
    }

    public String end() {
        return "end_" + getBlockId();
    }

    private String getBlockId() {
        if (Objects.equals(blockId, "")) blockId = IdGenerator.id();
        return blockId;
    }

    ;

    public FrontendBaseBlock CONTINUE() {
        return null;
    }

    public Object declareVariable(Object i) {
        return null;
    }

    public FrontendBaseBlock BREAK() {
        return null;
    }

    public FrontendBaseBlock RETURN() {
        return null;
    }

    public enum TYPE {FUNC, BLOCK, CODE, LAMBDA}
}
