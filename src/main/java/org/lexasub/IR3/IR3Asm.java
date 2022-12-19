package org.lexasub.IR3;

import org.lexasub.IR1.IR1Block.IR1BaseBlock;

import java.util.stream.Stream;

public class IR3Asm {
    public static IR3BaseBlock SET(IR1BaseBlock to, IR3BaseBlock from) {
        IR3BaseBlock to_ = new IR3BaseBlock(to, false);//don't use dependences
        return new IR3BaseBlock(IR3BaseBlock.Type.Assign).addChild(to_).addChild(from);
    }

    public static IR3BaseBlock thenConcat(IR3BaseBlock conc, IR3BaseBlock body) {
        return new IR3BaseBlock(IR3BaseBlock.Type.Block).addChild(conc).addChild(body);
    }

    public static IR3BaseBlock thenConcat(Stream<IR3BaseBlock> argsExt, IR3BaseBlock call) {
        return new IR3BaseBlock(IR3BaseBlock.Type.Block).addChildsStream(argsExt).addChild(call);
    }

    public static IR3BaseBlock CALL(String funName, Stream<IR3BaseBlock> argsIds) {
        return new IR3BaseBlock(IR3BaseBlock.Type.Call).setName(funName).addChildsStream(argsIds);
    }

}
