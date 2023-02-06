package org.lexasub.IR3;

import org.lexasub.IR1.IR1;

import java.util.stream.Stream;

public class IR3Asm {
    public static IR3 SET(IR1 to, IR3 from) {
        IR3 to_ = new IR3(to);//don't use dependences
        return new IR3(IR3.Type.ASSIGN).addChild(to_).addChild(from);
    }
    public static IR3 SET(IR3 to, IR3 from) {
        return new IR3(IR3.Type.ASSIGN).addChild(to).addChild(from);
    }
    public static IR3 thenConcat(IR3 conc, IR3 body) {
        return new IR3(IR3.Type.BLOCK).addChild(conc).addChild(body);
    }

    public static IR3 thenConcat(Stream<IR3> argsExt, IR3 call) {
        return new IR3(IR3.Type.BLOCK).addChildsStream(argsExt).addChild(call);
    }

    public static IR3 CALL(String funName, Stream<IR3> argsIds) {
        return new IR3(IR3.Type.CALL).addChildsStream(argsIds).setName(funName);
    }

}
