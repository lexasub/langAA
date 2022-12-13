package org.lexasub.frontend.utils;

import java.util.*;

public class FrontendBaseBlock {
    public FrontendBaseBlock parent = null;
    public String name = "";
    public String code = "";
    public FrontendBaseBlock.TYPE type = TYPE.BLOCK;
    public LinkedList<FrontendBaseBlock> childs = new LinkedList<>();
    public String blockId = IdGenerator.id();

    public FrontendBaseBlock() {

    }


    public FrontendBaseBlock(String name, String code, String blockId, String type) {
        this.name = name;
        this.code = code;
        this.blockId = blockId;
        if (!Objects.equals(type, "null"))
            this.type = TYPE.valueOf(type);
        //may be - add childs
    }
 /*   public FrontendBaseBlock(FrontendBaseBlock obj) {//blockId
        this.parent = obj.parent;
        this.name = obj.name;
        this.code = obj.code;
        this.type = obj.type;
        this.childs = new LinkedList<>(obj.childs.stream().map(i -> new FrontendBaseBlock(i)).toList());
    }*/

    public static FrontendBaseBlock spawnID(String id, FrontendBaseBlock myBlock) {
        FrontendBaseBlock fbb = new FrontendBaseBlock();
        fbb.type = FrontendBaseBlock.TYPE.ID;
        fbb.name = id;
        fbb.setParent(myBlock);
        return fbb;
    }

    public void fullLinkWith(FrontendBaseBlock child) {
        addChild(child);
        child.setParent(this);
    }

    public void addChild(FrontendBaseBlock child) {
        childs.add(child);
    }

    public void declareVariable(String type, String name) {
        addChild(spawnID(name, this));//todo add type
    }

    /*
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
    */
    public FrontendBaseBlock CONTINUE() {//TODO
        return null;
    }

    public FrontendBaseBlock BREAK() {//TODO
        return null;
    }

    public void declareVariable(String i) {
        addChild(spawnID(i, this));
    }

    public void setParent(FrontendBaseBlock parent) {
        this.parent = parent;
    }


    public enum TYPE {FUNC, BLOCK, CODE, ID, IF, WHILE, COND_JMP, JMP, PHI_PART, PHI, LAMBDA}//ID or variable??//TODO
}
