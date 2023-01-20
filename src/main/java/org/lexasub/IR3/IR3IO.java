package org.lexasub.IR3;

import java.util.HashMap;
import org.lexasub.utils.JsonDumper;

public class IR3IO {
    private static final String tb = "    ";//"\t";
    public static boolean jsonize = false;
    public static boolean compact = false;

    public static void dump(IR3 ll) {
        StringBuilder sb = new StringBuilder();
        JsonDumper.compact = compact;
        JsonDumper.jsonize = jsonize;
        dumpq("", sb, ll);
        if (jsonize) sb.setLength(sb.length()-2);
        System.out.println(sb);
    }
    private static void dumpq(String t, StringBuilder sb, IR3 ll) {
        //их тут много создается(пустых stringBUilders). Как вариант foreach() -> map().collect()
        StringBuilder sb1 = new StringBuilder();
        ll.childs.forEach(i -> dumpq(t + tb, sb1, i));

        HashMap<String, String> items = new HashMap<>();
        items.put("name", ll.name);
        items.put("blockId", ll.blockId);
        items.put("type", String.valueOf(ll.type));
        /*v.apply("name", ll.name);
        v.apply("blockId", ll.blockId);
        v.apply("type", String.valueOf(ll.type));*/
        // v.apply("parent:" + ((parent == null) ? null : parent.getBlockId()));
        JsonDumper.dumpq(t, sb, ll.childs.isEmpty(), items, sb1);
    }
}
