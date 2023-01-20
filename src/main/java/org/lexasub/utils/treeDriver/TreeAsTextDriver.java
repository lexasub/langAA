package org.lexasub.utils.treeDriver;


import java.util.Map;
import java.util.stream.Stream;

public class TreeAsTextDriver implements TreeDriver {
    public TreeDumper td;

    public TreeAsTextDriver(String t) {
        td = new TreeDumper(t);
    }

    @Override
    public void init() {
        td.initChilds();
    }

    @Override
    public void finit() {
        td.finitChilds();
    }

    @Override
    public void addChild(StringBuilder sb) {
        td.addChild(sb);
    }

    @Override
    public StringBuilder getRes() {
        return td.finalAppend();
    }

    @Override
    public void fromEntrySetStream(Stream<Map.Entry<String, String>> stream) {
        td.from(stream);
    }

    @Override
    public StringBuilder emptyChilds() {
        return td.emptyChilds().finalAppend();
    }
}
