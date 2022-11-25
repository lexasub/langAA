package org.lexasub.frontend.utils;

import java.util.Iterator;
import java.util.function.Function;
import java.util.stream.Stream;

public class FunctionGenerators {
    public static Function IF(FrontendBaseBlock myBlock) {
        return (s) -> {
            Iterator<FrontendBaseBlock> v = ((Stream<FrontendBaseBlock>)s).iterator();
            FrontendBaseBlock newIf = new FrontendBaseBlock();
            newIf.type = FrontendBaseBlock.TYPE.BLOCK;
            FrontendBaseBlock expr = v.next();
            expr.parent = newIf;
            FrontendBaseBlock trueBranch = v.next();
            trueBranch.parent = newIf;
            FrontendBaseBlock falseBranch = null;
            if(v.hasNext()){
                falseBranch = v.next();
                falseBranch.parent = newIf;
            }
            newIf.addChild(expr);
            if(falseBranch != null)
                newIf.addChild(Asm.jmp(expr.returnRes(), trueBranch.begin(), falseBranch.begin()));
            newIf.addChild(trueBranch);
            if(falseBranch != null){
                newIf.addChild(Asm.jmp(newIf.end()));
                newIf.addChild(falseBranch);
            }
            return newIf;
            /*
            expr Asm.jmp(expr.returnRes(), trueBranch.begin(), falseBranch.begin()) trueBranch Asm.jmp(endIf) falseBranch (virtual Asm.lbl(endIf))
            expr Asm.jmp(expr.returnRes(), trueBranch.begin(), endIf) trueBranch (virtual Asm.lbl(endIf))
             */
        };
    }

    public static Function WHILE(FrontendBaseBlock myBlock) {
        return (s) -> {
            Iterator<FrontendBaseBlock> v = ((Stream<FrontendBaseBlock>)s).iterator();

            FrontendBaseBlock newWhile = new FrontendBaseBlock();
            newWhile.type = FrontendBaseBlock.TYPE.BLOCK;
            FrontendBaseBlock expr = v.next();
            expr.type = FrontendBaseBlock.TYPE.BLOCK;
            expr.parent = newWhile;
            FrontendBaseBlock expr1 = new FrontendBaseBlock(expr);//TODO check deep copy
            expr1.parent = newWhile;
            expr1.type = FrontendBaseBlock.TYPE.BLOCK;
            FrontendBaseBlock body = v.next();
            body.parent = newWhile;
            body.type = FrontendBaseBlock.TYPE.BLOCK;

            newWhile.addChild(expr);
            newWhile.addChild(Asm.jmp(expr.returnRes(), body.begin(), newWhile.end()));
            newWhile.addChild(body);
            newWhile.addChild(expr1);
            newWhile.addChild(Asm.jmp(expr1.returnRes(), body.begin(), newWhile.end()));

            return newWhile;
            /*
            (virtual Asm.lbl(beginWhile))
                expr
                Asm.jmp(expr.returnRes(), body.begin(), endWhile)
                body
                expr1
                Asm.jmp(expr1.returnRes(), body.begin(), endWhile)
            (virtual Asm.lbl(endWhile))
             */
        };
    }

    public static Function ID(String text, FrontendBaseBlock myBlock) {
        return (s) -> {
            Iterator<FrontendBaseBlock> v = ((Stream<FrontendBaseBlock>)s).iterator();
            FrontendBaseBlock newFunCall = new FrontendBaseBlock();
            return newFunCall;
        };
    }
}
