package org.lexasub.IR4;

import org.lexasub.utils.graphDriver.GraphDriver;
import org.lexasub.utils.graphDriver.graphiz.graphizDriver;
import org.lexasub.utils.graphDriver.jgrapht.jgraphtDriver;
import org.lexasub.utils.treeDriver.TreeAsGraphDriver;
import org.lexasub.utils.treeDriver.TreeAsTextDriver;
import org.lexasub.utils.treeDriver.TreeDriver;
import org.lexasub.utils.treeDriver.TreeDumper;

import java.util.HashMap;
import java.util.Objects;

public class IR4IO {
    private static final String tb = "    ";//""\t";
    public static boolean jsonize = false;
    public static boolean compact = false;

    public static void dumpAsText(IR4 ll) {
        TreeDumper.compact = compact;
        TreeDumper.jsonize = jsonize;
        StringBuilder sb = dumpAsText("", ll);
        if (jsonize) sb.setLength(sb.length() - 2);
        System.out.println(sb);
    }

    private static StringBuilder dumpAsText(String t, IR4 ll) {
        if (ll == null) return new StringBuilder();

        TreeDriver p = new TreeAsTextDriver(t);
        p.fromEntrySetStream(getMyDump(ll).entrySet().stream());
        if (ll.childs.isEmpty())
            return p.emptyChilds();
        p.init();
        ll.childs.stream().map(i -> dumpAsText(t + tb, i)).forEach(p::addChild);
        p.finit();
        return p.getRes();
    }

    public static void dumpAsGraph(IR4 ll, String fileName, String fileFormat) {
        GraphDriver graph = new graphizDriver("");
        dumpAsGraph(ll, graph);
        graph.write(fileName, fileFormat);
    }

    private static void dumpAsGraph(IR4 ll, GraphDriver graph) {
        if (ll == null) return;
        TreeDriver p = new TreeAsGraphDriver(graph, getMyDumpConcated(ll));
        ll.childs.stream().filter(Objects::nonNull).map(IR4IO::getMyDumpConcated).forEach(p::addChild);
        ll.childs.forEach(i -> dumpAsGraph(i, graph));//recursive
    }

    private static String getMyDumpConcated(IR4 ll) {
        return getMyDump(ll).entrySet().stream().map(i -> i.getKey() + ":" + i.getValue() + "\n").reduce("", String::concat);
    }

    private static HashMap<String, String> getMyDump(IR4 ll) {
        HashMap<String, String> items = new HashMap<>();
        items.put("name", ll.name);
        items.put("type", String.valueOf(ll.type));
        /*v.apply("name", ll.name);
        //v.apply("blockId", ll.blockId);
        v.apply("type", String.valueOf(ll.type));*/
        // v.apply("parent:" + ((parent == null) ? null : parent.getBlockId()));
        return items;
    }
}
