package org.lexasub.utils.treeDriver;

import java.util.Map;
import java.util.function.BiFunction;
import java.util.stream.Stream;

public class TreeDumper {
    public static final boolean showEmptyChildList = false;
    public static boolean jsonize = false;
    public static boolean compact = false;
    private final String t;
    Stream<BiFunction<StringBuilder, String, StringBuilder>> items = null;
    private StringBuilder buf;

    public TreeDumper(String t) {
        this.t = t;
    }

    private static String jsnz(String a, String b) {
        return (jsonize) ? a : b;
    }

    private static String cmpct(String a, String b) {
        return (compact) ? a : b;
    }

    private static String r(String a) {
        return jsnz('"' + a + '"', a);
    }

    private StringBuilder append(StringBuilder sb, String str) {
        return sb.append(t.concat(str));
    }

    private StringBuilder append(StringBuilder sb, String str, String t1) {
        return sb.append(t1.concat(str));
    }

    private BiFunction<StringBuilder, String, StringBuilder> v(Map.Entry<String, String> a) {
        return (StringBuilder sb, String t) ->
                append(sb, r(a.getKey()) + ":" + r(a.getValue()) + jsnz(",", "") + cmpct(" ", "\n"), cmpct("", t));
    }

    public StringBuilder finalAppend() {
        if (jsonize) append(buf, "}").append(",").append("\n");
        return buf;
    }

    private StringBuilder applyItems(Stream<BiFunction<StringBuilder, String, StringBuilder>> items) {
        StringBuilder sb = new StringBuilder();
        if (jsonize) append(sb, "{").append("\n");
        if (compact) sb.append(t);
        items.forEach(i -> i.apply(sb, t));
        return sb;
    }

    private StringBuilder truncateChilds() {
        buf.setLength(buf.length() - 1);//remove ','
        if (jsonize) buf.setLength(buf.length() - 1);
        buf.append("\n");
        return buf;
    }

    private StringBuilder dumpChildsHeader() {
        StringBuilder sb = new StringBuilder();
        append(sb, r("childs"), cmpct("", t))
                .append(":").append(jsnz("[", "{")).append("\n");
        return sb;
    }

    private StringBuilder dumpChildsFooter() {
        StringBuilder sb = new StringBuilder();
        append(sb, jsnz("]", "}")).append("\n");
        return sb;
    }

    private void withoutChilds() {
        if (showEmptyChildList) {
            append(buf, r("childs"))
                    .append(":").append(jsnz("[]", "{}")).append("\n");
        } else {
            if (jsonize) buf.setLength(buf.length() - 2);//remove ','
            if (compact || jsonize) append(buf, "\n");
        }
    }

    public void from(Stream<Map.Entry<String, String>> itemsStream) {
        items = itemsStream.map(this::v);
        buf = applyItems(items);
    }

    public void initChilds() {
        buf.append(dumpChildsHeader());
    }

    public void addChild(StringBuilder sb) {
        buf.append(sb);
    }

    public void finitChilds() {
        truncateChilds();
        buf.append(dumpChildsFooter());
    }

    public TreeDumper emptyChilds() {
        withoutChilds();
        return this;
    }
}
