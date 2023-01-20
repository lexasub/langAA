package org.lexasub.frontend.utils;

import org.antlr.v4.misc.OrderedHashMap;
import org.lexasub.utils.JsonDumper;

import java.util.*;
import java.util.function.Function;

public class FBBIO {
    private static final String tb = "    ";//""\t";

    public static void dump(String t, StringBuilder sb, FBB fbb) {
        //их тут много создается(пустых stringBUilders). Как вариант foreach() -> map().collect()
        StringBuilder sb1 = new StringBuilder();
        fbb.childs.forEach(i -> dump(t + tb, sb1, i));

        HashMap<String, String> items = new HashMap<>();
        items.put("name", fbb.name);
        items.put("blockId", fbb.blockId);
        items.put("type", String.valueOf(fbb.type));
        /*v.apply("name", fbb.name);
        v.apply("blockId", fbb.blockId);
        v.apply("type", String.valueOf(fbb.type));*/
        // v.apply("parent:" + ((parent == null) ? null : parent.getBlockId()));
        JsonDumper.dumpq(t, sb, fbb.childs.isEmpty(), items, sb1);
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
        OrderedHashMap<String, LinkedList<String>> parentChild = new OrderedHashMap<String, LinkedList<String>>();
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