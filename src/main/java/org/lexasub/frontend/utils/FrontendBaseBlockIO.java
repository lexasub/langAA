package org.lexasub.frontend.utils;

import org.antlr.v4.misc.OrderedHashMap;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;

public class FrontendBaseBlockIO {
    public static boolean jsonize = false;

    private static String r(String a) {
        return (jsonize) ? ('"' + a + '"') : a;
    }

    public static void dump(String t, StringBuilder sb, FrontendBaseBlock fbb) {
        BiFunction<String, String, StringBuilder> v = (String a, String b) ->
                append(sb, t, r(a) + ":" + r(b) + ((jsonize) ? "," : "") + "\n");
        if (jsonize) append(sb, t, "{" + "\n");
        v.apply("name", fbb.name);
        v.apply("code", fbb.code);
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

    public static void serialize(StringBuilder sb, FrontendBaseBlock fbb) {
        Function<String, StringBuilder> v = (String s) -> sb.append(s + "\n");
        LinkedList<FrontendBaseBlock> childs = fbb.childs;
        childs.forEach(i -> {
            v.apply(i.name);
            v.apply(i.code);
            v.apply(i.blockId);
            v.apply(String.valueOf(i.type));
            v.apply((i.parent == null) ? "" : i.parent.blockId);
            v.apply(" ");
        });
        childs.forEach(i -> serialize(sb, i));
    }

    public static FrontendBaseBlock deserialize(String[] split) {
        OrderedHashMap<String, LinkedList<String>> parentChild = new OrderedHashMap<String, LinkedList<String>>();
        List<FrontendBaseBlock> blocks = Arrays.stream(split)
                .map(i -> i.split("\n"))
                .map(i ->
                        {
                            if (parentChild.get(i[4]) == null)
                                parentChild.put(i[4], new LinkedList());
                            parentChild.get(i[4]).add(i[2]);
                            return new FrontendBaseBlock(i[0], i[1], i[2], i[3]);
                        }
                ).toList();
        blocks.forEach(
                i -> {
                    LinkedList<String> l = parentChild.get(i.blockId);
                    if (l != null)
                        l.forEach(j -> {
                            FrontendBaseBlock v = blocks.stream().filter(k -> Objects.equals(k.blockId, j)).findFirst().get();
                            i.addChild(v);
                            v.parent = i;
                        });
                }
        );

        FrontendBaseBlock myBlock = new FrontendBaseBlock();
        blocks.stream()
                .filter(i -> i.parent == null)
                .forEach(i -> {
                    i.parent = myBlock;
                    myBlock.addChild(i);
                });
        return myBlock;
    }
}