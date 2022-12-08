package org.lexasub.IR1.IR1Block.utils;


import java.util.Objects;

public class CustomEdge {

    public String src, tgt;

    public CustomEdge(String s, String t) {
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
        return (Objects.equals(src, "s")) ? "before" : "part"/*"(" + this.getSource() + " : " + this.getTarget() + ")"*/;
    }
}
