package org.lexasub.frontend.utils;

import org.antlr.v4.misc.OrderedHashMap;
import org.lexasub.utils.graphDriver.GraphDriver;
import org.lexasub.utils.treeDriver.TreeDriver;
import org.lexasub.utils.graphDriver.graphiz.graphizDriver;
import org.lexasub.utils.treeDriver.TreeAsGraphDriver;
import org.lexasub.utils.treeDriver.TreeAsTextDriver;

import java.util.*;
import java.util.function.Function;

public class FBBIO {
    private static final String tb = "    ";//""\t";

    public static StringBuilder dumpAsText(String t, FBB ll) {
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

    private static void dumpAsGraph(FBB ll, String fileName, String fileFormat) {
        GraphDriver graph = new graphizDriver("");
        dumpAsGraph(ll, graph);
        graph.write(fileName, fileFormat);
    }

    private static void dumpAsGraph(FBB ll, GraphDriver graph) {
        if (ll == null) return;
        TreeDriver p = new TreeAsGraphDriver(graph, getMyDumpConcated(ll));
        ll.childs.stream().map(FBBIO::getMyDumpConcated).forEach(p::addChild);
        ll.childs.forEach(i -> dumpAsGraph(i, graph));//recursive
    }

    private static String getMyDumpConcated(FBB ll) {
        return getMyDump(ll).entrySet().stream().map(i -> i.getKey() + ":" + i.getValue() + "\n").reduce("", String::concat);
    }

    private static HashMap<String, String> getMyDump(FBB fbb) {
        HashMap<String, String> items = new HashMap<>();
        items.put("name", fbb.name);
        items.put("blockId", fbb.blockId);
        items.put("type", String.valueOf(fbb.type));
        /*v.apply("name", fbb.name);
        v.apply("blockId", fbb.blockId);
        v.apply("type", String.valueOf(fbb.type));*/
        // v.apply("parent:" + ((parent == null) ? null : parent.getBlockId()));
        return items;
    }

    public static void serialize(StringBuilder sb, FBB fbb) {
        Function<String, StringBuilder> v = (String s) -> sb.append(s).append("\n");
        List<FBB> childs = fbb.childs;
        childs.forEach(i -> {
            v.apply(i.name);
            v.apply(i.blockId);
            v.apply(String.valueOf(i.type));
            v.apply((i.parent == null) ? "" : i.parent.blockId);
            v.apply(" ");
        });
        childs.forEach(i -> serialize(sb, i));
    }

    public static FBB deserialize(String[] split) {
        OrderedHashMap<String, LinkedList<String>> parentChild = new OrderedHashMap<>();
        List<FBB> blocks = Arrays.stream(split)
                .map(i -> i.split("\n"))
                .map(i ->
                        {
                            parentChild.computeIfAbsent(i[3], k -> new LinkedList<>());
                            parentChild.get(i[3]).add(i[1]);
                            return new FBB(i[0], i[1], i[2]);
                        }
                ).toList();
        blocks.forEach(
                i -> {
                    LinkedList<String> l = parentChild.get(i.blockId);
                    if (l != null)
                        l.forEach(j ->
                                i.fullLinkWith(blocks.stream().filter(k -> Objects.equals(k.blockId, j))
                                        .findFirst().get())//TODO findFirst check
                        );
                }
        );

        FBB myBlock = new FBB();
        blocks.stream()
                .filter(i -> i.parent == null)
                .forEach(myBlock::fullLinkWith);
        return myBlock;
    }
}