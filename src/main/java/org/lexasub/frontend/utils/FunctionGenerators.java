package org.lexasub.frontend.utils;

import java.util.Iterator;
import java.util.function.Function;
import java.util.stream.Stream;

public class FunctionGenerators {
    public static Function IF(FrontendBaseBlock myBlock) {
        return (s) -> {
            Iterator<FrontendBaseBlock> v = ((Stream<FrontendBaseBlock>)s).iterator();
            FrontendBaseBlock newIf = new FrontendBaseBlock();
            newIf.parent = myBlock;
            newIf.type = FrontendBaseBlock.TYPE.IF;
            FrontendBaseBlock expr = v.next().childs.get(0);//todo convert lambda to block
            FrontendBaseBlock trueBranch = v.next().childs.get(0);//todo convert lambda to block
            System.out.println(trueBranch.childs.size());
            expr.parent = newIf;
            newIf.addChild(expr);
            trueBranch.parent = newIf;
            newIf.addChild(trueBranch);
            if(v.hasNext()){
                FrontendBaseBlock falseBranch = v.next().childs.get(0);//todo convert lambda to block
                falseBranch.parent = newIf;
                newIf.addChild(falseBranch);
            }
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

    public static Function WHILE(FrontendBaseBlock myBlock) {
        return (s) -> {
            Iterator<FrontendBaseBlock> v = ((Stream<FrontendBaseBlock>)s).iterator();

            FrontendBaseBlock newWhile = new FrontendBaseBlock();
            newWhile.parent = myBlock;
            newWhile.type = FrontendBaseBlock.TYPE.WHILE;
            FrontendBaseBlock expr = v.next().childs.get(0);//todo convert lambda to block
            FrontendBaseBlock body = v.next();//todo convert lambda to block
            expr.parent = newWhile;
            newWhile.addChild(expr);
            body.parent = newWhile;
            newWhile.addChild(body);

            //  expr.type = FrontendBaseBlock.TYPE.BLOCK;
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

    public static Function ID(String funcName, FrontendBaseBlock myBlock) {
        return (s) -> {
            FrontendBaseBlock newFunCall = new FrontendBaseBlock();
            newFunCall.parent = myBlock;
            Asm.call(funcName, (Stream<FrontendBaseBlock>)s, newFunCall);
            return newFunCall;
        };
    }
}
