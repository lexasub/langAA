package org.lexasub.IR3;

import java.util.function.BiFunction;

public class IR3IO {
    public static boolean jsonize = false;

    private static String r(String a) {
        return (jsonize) ? ('"' + a + '"') : a;
    }

    private static StringBuilder append(StringBuilder sb, String t, String str) {
        return sb.append(t.concat(str));
    }

    public static void dump(IR3 ll) {
        StringBuilder sb = new StringBuilder();
        dump("", sb, ll);
        System.out.println(sb);
    }

    private static void dump(String t, StringBuilder sb, IR3 ll) {
        BiFunction<String, String, StringBuilder> v = (String a, String b) ->
                append(sb, t, r(a) + ":" + r(b) + ((jsonize) ? "," : "") + "\n");
        if (jsonize) append(sb, t, "{" + "\n");
        v.apply("name", ll.name);
        v.apply("blockId", ll.blockId);
        v.apply("type", String.valueOf(ll.type));
        // v.apply("parent:" + ((parent == null) ? null : parent.getBlockId()));
        if (!ll.childs.isEmpty()) {
            if (jsonize)
                append(sb, t, "\"childs\":[\n");
            else
                append(sb, t, "childs:{\n");
            ll.childs.forEach(i -> dump(t + "\t", sb, i));
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
}
