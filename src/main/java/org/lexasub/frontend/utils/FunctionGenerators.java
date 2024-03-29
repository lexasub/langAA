package org.lexasub.frontend.utils;

import org.lexasub.frontend.langosParser;

import java.util.Iterator;
import java.util.function.Function;
import java.util.stream.Stream;

public class FunctionGenerators {
    public static Function<Stream<FBB>, FBB> IF(FBB myBlock, FBB newIf) {
        return (s) -> {
            Iterator<FBB> v = s.iterator();
            newIf.setParent(myBlock);
            newIf.type = FBB.TYPE.IF;
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

    public static Function<Stream<FBB>, FBB> WHILE(FBB myBlock, FBB newWhile) {
        return (s) -> {
            Iterator<FBB> v = s.iterator();

            newWhile.setParent(myBlock);
            newWhile.type = FBB.TYPE.WHILE;
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

    public static Function<Stream<FBB>, FBB> ID(String funcName, FBB myBlock, FBB newFunCall) {
        return (s) -> {
            newFunCall.setParent(myBlock);
            Asm.call(funcName, s, newFunCall);
            return newFunCall;
        };
    }

    public static Function<Stream<FBB>, FBB> visitFun_name(langosParser.Fun_nameContext ctx, FBB myBlock, FBB newPart) {
        //pairmap,map,set,swap
        if (ctx.IF() != null) return FunctionGenerators.IF(myBlock, newPart);
        if (ctx.WHILE() != null) return FunctionGenerators.WHILE(myBlock, newPart);
        if (ctx.ID() != null) return FunctionGenerators.ID(ctx.ID().getText(), myBlock, newPart);
        if (ctx.SET() != null) return FunctionGenerators.ID(ctx.SET().getText(), myBlock, newPart);//kostyl'
        if (ctx.SWAP() != null) return FunctionGenerators.ID(ctx.SWAP().getText(), myBlock, newPart);//kostyl'
        return null;
    }
}
