package org.lexasub.frontend.utils;

import java.util.LinkedList;

public class FrontendBaseBlock  implements Cloneable {
    public FrontendBaseBlock parent;
    LinkedList<FrontendBaseBlock> childs = new LinkedList<>();
    public String name;
    public FrontendBaseBlock(){
        
    }
    public FrontendBaseBlock(FrontendBaseBlock obj){
        this.parent = obj.parent;
        this.name = obj.name;
        this.childs = (LinkedList<FrontendBaseBlock>) childs.stream().map(i->new FrontendBaseBlock(i)).toList();
    }
    public FrontendBaseBlock(FrontendBaseBlock parent, LinkedList<FrontendBaseBlock> childs, String name){
        this.parent = parent;
        this.name = name;
        this.childs = (LinkedList<FrontendBaseBlock>) childs.stream().map(i->new FrontendBaseBlock(i)).toList();
    }

    public void addChild(FrontendBaseBlock child) {
        childs.add(child);
    }

    public void declareVariable(String type, String name) {
    }

    public Object returnRes() {
        return null;
    }

    public Object begin() {
        return null;
    }

    public Object end() {
        return null;
    }

    public enum TYPE {FUNC, LAMBDA};
    public TYPE type;

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
}
