package org.lexasub.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

public class JsonDumper {
    public static boolean jsonize = false;
    public static boolean compact = false;
    public static boolean showEmptyChildList = false;

    private static String jsnz(String a, String b) {
        return (jsonize) ? a : b;
    }

    private static String cmpct(String a, String b) {
        return (compact) ? a : b;
    }
    private static String r(String a) {
        return jsnz('"' + a + '"', a);
    }

    private static StringBuilder append(StringBuilder sb, String t, String str) {
        return sb.append(t.concat(str));
    }
    private static BiFunction<StringBuilder, String, StringBuilder> v(Map.Entry<String, String> a){
        return (StringBuilder sb, String t) ->
                append(sb, cmpct("", t), r(a.getKey()) + ":" + r(a.getValue()) + jsnz(",", "") + cmpct(" ", "\n"));
    }

    private static void dumpContent(String t, StringBuilder sb, HashMap<String, String> items) {
        if (compact) sb.append(t);
        items.entrySet().stream().map(JsonDumper::v).forEach(i->i.apply(sb, t));
    }
    public static void dumpq(String t, StringBuilder sb, boolean hasntChilds, HashMap<String, String> items, StringBuilder sb1) {
        if(!hasntChilds) {
            sb1.setLength(sb1.length()-1);//remove ','
            if (jsonize) sb1.setLength(sb1.length()-1);
            sb1.append("\n");
        }

        if (jsonize) append(sb, t, "{").append("\n");
        dumpContent(t, sb, items);
        dumpChilds(t, sb, hasntChilds, sb1);
        if (jsonize) append(sb, t, "}").append(",").append("\n");
    }

    private static void dumpChilds(String t, StringBuilder sb, boolean hasntChilds, StringBuilder sb1) {
        if (!hasntChilds) {
            dumpChildsHeader(t, sb);
            sb.append(sb1);
            dumpChildsFooter(t, sb);
        } else {
            if(showEmptyChildList){
                append(sb, t, r("childs"))
                        .append(":").append(jsnz("[]", "{}")).append("\n");
            }
            else {
                if (jsonize) sb.setLength(sb.length() - 2);//remove ','
                if (compact || jsonize) append(sb, t, "\n");
            }
        }
    }

    private static void dumpChildsHeader(String t, StringBuilder sb) {
        append(sb, cmpct("", t), r("childs"))
                .append(":").append(jsnz("[", "{")).append("\n");
    }

    private static void dumpChildsFooter(String t, StringBuilder sb) {
        append(sb, t, jsnz("]", "}")).append("\n");
    }
}
