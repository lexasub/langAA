package org.lexasub.frontend.utils;

import java.util.Iterator;
import java.util.function.Function;
import java.util.stream.Stream;

public class FunctionGenerators {
    public static Function IF(FrontendBaseBlock myBlock, FrontendBaseBlock newIf) {
        return (s) -> {
            Iterator<FrontendBaseBlock> v = ((Stream<FrontendBaseBlock>) s).iterator();
            newIf.parent = myBlock;
            newIf.type = FrontendBaseBlock.TYPE.IF;
            newIf.fullLinkWith(v.next());//expr
            newIf.fullLinkWith(v.next());//trueBranch
            if (v.hasNext())
                newIf.fullLinkWith(v.next());//falseBranch
            //FrontendBaseBlock trueBranch = v.next();
           /* if(falseBranch != null) {
                FrontendBaseBlock jmp = Asm.jmp(expr.returnRes(), trueBranch.begin(), falseBranch.begin());
                jmp.parent = newIf;
                newIf.addChild(jmp);
            }*/
            /*if(falseBranch != null){
                FrontendBaseBlock jmp = Asm.jmp(newIf.end());
                jmp.parent = newIf;
                newIf.addChild(jmp);
                newIf.addChild(falseBranch);
            }*/
            return newIf;
            /*
            expr Asm.jmp(expr.returnRes(), trueBranch.begin(), falseBranch.begin()) trueBranch Asm.jmp(endIf) falseBranch (virtual Asm.lbl(endIf))
            expr Asm.jmp(expr.returnRes(), trueBranch.begin(), endIf) trueBranch (virtual Asm.lbl(endIf))
             */
        };
    }

    public static Function WHILE(FrontendBaseBlock myBlock, FrontendBaseBlock newWhile) {
        return (s) -> {
            Iterator<FrontendBaseBlock> v = ((Stream<FrontendBaseBlock>) s).iterator();

            newWhile.parent = myBlock;
            newWhile.type = FrontendBaseBlock.TYPE.WHILE;
            newWhile.fullLinkWith(v.next());//expr
            newWhile.fullLinkWith(v.next());//body

           /* FrontendBaseBlock expr1 = new FrontendBaseBlock(expr);//TODO check deep copy
            expr1.parent = newWhile;
            expr1.type = FrontendBaseBlock.TYPE.BLOCK;*/
            // body.type = FrontendBaseBlock.TYPE.BLOCK;
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

    public static Function ID(String funcName, FrontendBaseBlock myBlock, FrontendBaseBlock newFunCall) {
        return (s) -> {
            newFunCall.parent = myBlock;
            Asm.call(funcName, (Stream<FrontendBaseBlock>) s, newFunCall);
            return newFunCall;
        };
    }
}
