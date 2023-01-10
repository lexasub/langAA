package org.lexasub.IR4;

import java.util.stream.Stream;

public class IR4Asm {
    public static IR4 lbl(String s) {
        IR4 code = new IR4(IR4.Type.CODE);
        //todo
        return code;
    }

    public static IR4 thenConcat(IR4 arg0, Stream<IR4> args) {
        return new IR4(IR4.Type.SEQ).addChild(arg0).addChildsStream(args);
    }
}
