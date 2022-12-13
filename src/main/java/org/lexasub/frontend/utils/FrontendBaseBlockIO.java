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

    public static void dump(String t, StringBuilder sb, FrontendBaseBlock frontendBaseBlock) {
        BiFunction<String, String, StringBuilder> v = (String a, String b) -> sb.append(
                t.concat(r(a) + ":" + r(b) + ((jsonize) ? "," : "") + "\n")
        );
        if (jsonize) sb.append(t.concat("{" + "\n"));
        v.apply("name", frontendBaseBlock.name);
        v.apply("code", frontendBaseBlock.code);
        v.apply("blockId", frontendBaseBlock.blockId);
        v.apply("type", String.valueOf(frontendBaseBlock.type));
        // v.apply("parent:" + ((parent == null) ? null : parent.getBlockId()));
        if (!frontendBaseBlock.childs.isEmpty()) {
            if (jsonize)
                sb.append(t.concat("\"childs\":[\n"));
            else
                sb.append(t.concat("childs:{\n"));
            frontendBaseBlock.childs.forEach(i -> dump(t + "\t", sb, i));
            if (jsonize) sb.append(t.concat( "\t" + "{}" + "\n"));
            if (jsonize)  sb.append(t.concat( "]" + "\n"));
            if (!jsonize) sb.append(t.concat("}" + "\n"));
        } else {
            if (jsonize)
                sb.append(t.concat("\"childs\":{}\n"));
            else
                sb.append(t.concat("childs:{}\n"));
        }
        if (jsonize) sb.append(t.concat("}" +  ((jsonize) ? "," : "") + "\n"));
    }

    public static void serialize(StringBuilder sb, FrontendBaseBlock frontendBaseBlock) {
        Function<String, StringBuilder> v = (String s) -> sb.append(s + "\n");
        frontendBaseBlock.childs.forEach(i -> {
            v.apply(i.name);
            v.apply(i.code);
            v.apply(i.blockId);
            v.apply(String.valueOf(i.type));
            v.apply((i.parent == null) ? "" : i.parent.blockId);
            v.apply(" ");
        });
        frontendBaseBlock.childs.forEach(i -> serialize(sb, i));
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