package org.lexasub.IR4;

import org.lexasub.IR3.IR3;

import static org.lexasub.IR4.IR4Asm.spawnId;
import static org.lexasub.IR4.IR4Asm.thenConcatCode;

public class IR4AsmUtils {

    public static String beginOf(String blockId) {
        return blockId + "_begin";
    }

    public static String endOf(String blockId) {
        return blockId + "_end";
    }

    public static String beginOf(IR4 obj) {
        return beginOf(obj.name);
    }

    public static String endOf(IR3 obj) {
        return endOf(obj.name);
    }

    static IR4 spawnLocalRegister(String name) {
        return spawnId("%" + name);
    }

    public static IR4 spawnGlobalRegister(String name) {
        return spawnId("@" + name);
    }
    public static IR4 spawnTypedRegister(String type, String name) {
        return thenConcatCode(type, spawnLocalRegister(name));
    }

    static IR4 spawnTypedLabelOfObject(String lblName) {
        return spawnTypedRegister("label", lblName);
    }
}
