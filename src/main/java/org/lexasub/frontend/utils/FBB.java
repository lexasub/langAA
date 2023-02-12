package org.lexasub.frontend.utils;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class FBB {
    public FBB parent = null;
    public String name = "";
    public TYPE type = TYPE.BLOCK;
    public List<FBB> childs = new LinkedList<>();
    public String blockId = IdGenerator.id();

    public FBB() {

    }


    public FBB(String name, String blockId, String type) {
        setName(name);
        this.blockId = blockId;
        if (!Objects.equals(type, "null"))
            this.type = TYPE.valueOf(type);
        //may be - add childs
    }

    public static FBB spawnID(String id, FBB myBlock) {
        FBB fbb = new FBB();
        fbb.type = FBB.TYPE.ID;
        fbb.setName(id);
        fbb.setParent(myBlock);
        return fbb;
    }
 /*   public FrontendBaseBlock(FrontendBaseBlock obj) {//blockId
        this.parent = obj.parent;
        this.name = obj.name;
        this.type = obj.type;
        this.childs = new LinkedList<>(obj.childs.stream().map(i -> new FrontendBaseBlock(i)).toList());
    }*/

    public void setName(String name) {
        this.name = name;
    }

    public void fullLinkWith(FBB child) {
        addChild(child);
        child.setParent(this);
    }

    public void addChild(FBB child) {
        childs.add(child);
    }

    public void declareVariable(String type, String name) {
        addChild(spawnID(name, this));//todo add type
    }

    public FBB CONTINUE() {//TODO
        return null;
    }

    public FBB BREAK() {//TODO
        return null;
    }

    public void declareVariable(String i) {
        addChild(spawnID(i, this));
    }

    public void setParent(FBB parent) {
        this.parent = parent;
    }


    public enum TYPE {FUNC, BLOCK, CODE, ID, IF, WHILE, COND_JMP, JMP, PHI_PART, PHI, AFTER, LAMBDA}//ID or variable??//TODO
}
