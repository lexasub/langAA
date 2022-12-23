package org.lexasub.frontend.utils;

import org.antlr.v4.misc.OrderedHashMap;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;

public class FBBIO {
    public static boolean jsonize = false;

    private static String r(String a) {
        return (jsonize) ? ('"' + a + '"') : a;
    }

    public static void dump(String t, StringBuilder sb, FBB fbb) {
        BiFunction<String, String, StringBuilder> v = (String a, String b) ->
                append(sb, t, r(a) + ":" + r(b) + ((jsonize) ? "," : "") + "\n");
        if (jsonize) append(sb, t, "{" + "\n");
        v.apply("name", fbb.name);
        v.apply("blockId", fbb.blockId);
        v.apply("type", String.valueOf(fbb.type));
        // v.apply("parent:" + ((parent == null) ? null : parent.getBlockId()));
        if (!fbb.childs.isEmpty()) {
            if (jsonize)
                append(sb, t, "\"childs\":[\n");
            else
                append(sb, t, "childs:{\n");
            fbb.childs.forEach(i -> dump(t + "\t", sb, i));
            if (jsonize) append(sb, t, "\t" + "{}" + "\n");
            append(sb, t, ((jsonize) ? "]" : "}") + "\n");
        } else {
            if (jsonize)
                append(sb, t, "\"childs\":{}\n");
            else
                append(sb, t, "childs:{}\n");
        }
        if (jsonize) append(sb, t, "}" + "," + "\n");
    }

    private static StringBuilder append(StringBuilder sb, String t, String str) {
        return sb.append(t.concat(str));
    }

    public static void serialize(StringBuilder sb, FBB fbb) {
        Function<String, StringBuilder> v = (String s) -> sb.append(s + "\n");
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