package org.lexasub.IR1.utils;


public class CustomEdge {

    public String src, tgt;
    CustomEdge(String s, String t){
        src = s;
        tgt = t;
    }
    protected Object getSource() {
        return src;
    }

    protected Object getTarget() {
        return tgt;
    }

    public String toString() {
        return /*"(" + this.getSource() + " : " + this.getTarget() + ")"*/ "";
    }
}
