package org.lexasub.LLVMIR;

import org.lexasub.IR4.IR4;

public class LLVMIR {
    public StringBuilder ir = new StringBuilder();
    public LLVMIR(String name) {
        ir.append(name);
    }

    public static LLVMIR doJob(IR4 block) {
        if(block == null) return null;
        if(block.typeIs(IR4.Type.CODE)) return transformCode(block);
        if(block.typeIs(IR4.Type.CODEPART)) return transformCodePart(block);
        if(block.typeIs(IR4.Type.SEQ)) return transformSeq(block);
        if(block.typeIs(IR4.Type.ID)) return transformID(block);
        return null;
    }

    private static LLVMIR transformCodePart(IR4 block) {
        return new LLVMIR(block.name);
    }

    private static LLVMIR transformID(IR4 block) {
        return new LLVMIR(block.name);
    }

    private static LLVMIR transformSeq(IR4 block) {
        return LLVMIRasm.concatSplitter(block.childs.stream().map(LLVMIR::doJob), '\n');//.type=seq?
    }

    private static LLVMIR transformCode(IR4 block) {
        return LLVMIRasm.concatSplitter(block.childs.stream().map(LLVMIR::doJob), ' ');//.type=code?
    }
}
