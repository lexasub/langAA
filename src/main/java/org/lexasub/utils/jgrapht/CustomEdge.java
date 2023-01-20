package org.lexasub.utils.jgrapht;


import java.util.Objects;

public class CustomEdge {

    public String src, tgt;

    public CustomEdge(String s, String t) {
        src = s;
        tgt = t;
    }

    protected String getSource() {
        return src;
    }

    protected String getTarget() {
        return tgt;
    }

    public String toString() {
        return (Objects.equals(src, "s")) ? "before" : "part"/*"(" + this.getSource() + " : " + this.getTarget() + ")"*/;
    }
}
