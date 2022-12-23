package org.lexasub.frontend.langosVisitors;

import org.antlr.v4.runtime.tree.ParseTree;
import org.lexasub.frontend.langosParser;
import org.lexasub.frontend.langosVisitors.parts.Parts;
import org.lexasub.frontend.utils.Asm;
import org.lexasub.frontend.utils.FBB;
import org.lexasub.frontend.utils.FunctionGenerators;

import java.util.LinkedList;
import java.util.function.Function;
import java.util.stream.Stream;

public class myLangosVisitor implements myLangosVisitorInterface {

    static public Object visitClass_name(langosParser.Class_nameContext ctx) {
        return null;
    }

    static public Object visitMember_name(langosParser.Member_nameContext ctx) {
        return null;
    }

    static public Object visitNamspce_obj(langosParser.Namspce_objContext ctx) {
        return null;
    }

    static public Object visitFunction_specifier(langosParser.Function_specifierContext ctx) {
        return null;
    }


    static public Object visitGet_member(langosParser.Get_memberContext ctx) {
        return null;
    }

    static public Object visitMethod_call_(langosParser.Method_call_Context ctx) {
        return null;
    }

    static public Object visitFunction_call3(langosParser.Function_call3Context ctx) {
        return null;
    }

    static public Object visitFunction_call_helper_method(langosParser.Function_call_helper_methodContext ctx) {
        return null;
    }

    static public Object visitMethod_call(langosParser.Method_callContext ctx) {
        return null;
    }

    static public Object visitFunction_call_helper(langosParser.Function_call_helperContext ctx) {
        return null;
    }


    static public Object visitWith_body(langosParser.With_bodyContext ctx) {
        return null;
    }

    static public Object visitWith_synonym(langosParser.With_synonymContext ctx) {
        return null;
    }

    static public Object visitWith_(langosParser.With_Context ctx) {
        return null;
    }

    static public Object visitDeclare_member(langosParser.Declare_memberContext ctx) {
        return null;
    }

    static public Object visitClass_(langosParser.Class_Context ctx) {
        return null;
    }
    static public Object visitWithArg(langosParser.WithArgContext ctx, FBB myBlock) {
        return visitExpr(ctx.expr(), myBlock);
    }

    static public FBB visitImport_(langosParser.Import_Context ctx) {
        return null;
    }

    public static String visitVar_name(langosParser.Var_nameContext ctx) {
        return ctx.ID().getText();
    }

    static public FBB visitExpr(langosParser.ExprContext ctx, final FBB myBlock) {
        //with_
        if (ctx.flow_control() != null) return visitFlow_control(ctx.flow_control(), myBlock);
        if (ctx.function_call_() != null) return visitFunction_call_(ctx.function_call_(), myBlock);
        //class_
        if (ctx.lambda() != null) return visitLambda(ctx.lambda(), myBlock);
        //get_member
        //char, string, id
        return null;
    }

    static public FBB visitFunction_call(langosParser.Function_callContext ctx, FBB myBlock) {
        FBB newPart = new FBB();
        Function funName = FunctionGenerators.visitFun_name(ctx.fun_name(), myBlock, newPart);//->lambda
        Stream<FBB> args = visitCallArgs(ctx.callArgs(), newPart);//newPart or myBlock??
        return (FBB) funName.apply(args);
    }

    static public FBB visitFunction_call2(langosParser.Function_call2Context ctx, FBB myBlock) {
        //function_call_helper
        if (ctx.function_call() != null) return visitFunction_call(ctx.function_call(), myBlock);
        return null;
    }

    static public FBB visitFunction_call_(langosParser.Function_call_Context ctx, FBB myBlock) {
        //method_Call
        if (ctx.function_call2() != null) return visitFunction_call2(ctx.function_call2(), myBlock);
        return null;
    }

    static public FBB visitFlow_control(langosParser.Flow_controlContext ctx, FBB myBlock) {
        if (ctx.CONTINUE() != null) return myBlock.CONTINUE();
        if (ctx.BREAK() != null) return myBlock.BREAK();
        //else return
        return visitReturn_expr(ctx.return_expr(), myBlock);
    }

    static public FBB visitLambda(langosParser.LambdaContext ctx, FBB myBlock) {
        FBB newBlock = new FBB();
        newBlock.type = FBB.TYPE.LAMBDA;
        Stream<String> args = visitLambdaArgs(ctx.lambdaArgs());
        args.forEach(newBlock::declareVariable);
        newBlock.setParent(myBlock);
        Stream<FBB> body =
                ctx.body() == null
                        ? Stream.of(visitExpr(ctx.expr(), newBlock))
                        : ctx.body().element().stream().map(ctx1 -> visitElement(ctx1, newBlock));
        //visitElem - visitExpr || visitFunc
        LinkedList<FBB> bodyList = new LinkedList<>(body.toList());
        bodyList.forEach(newBlock::fullLinkWith);
        return newBlock;
    }


    static public FBB visitReturn_expr(langosParser.Return_exprContext ctx, FBB myBlock) {
        if (ctx == null) return Asm.RETURN(myBlock);
        //getMember, char,string
        if (ctx.function_call_() != null)
            return Asm.RETURN(visitFunction_call_(ctx.function_call_(), myBlock), myBlock);
        if (ctx.lambda() != null) return Asm.RETURN(visitLambda(ctx.lambda(), myBlock), myBlock);//thenReturn
        if (ctx.ID() != null) return Asm.RETURN(ctx.ID().getText(), myBlock);
        return null;
    }

    public static FBB visitElement(langosParser.ElementContext ctx, final FBB myBlock) {
        if (ctx.function() != null) return Parts.visitFunction(ctx.function(), myBlock);
        return visitExpr(ctx.expr(), myBlock);
    }


    static public FBB visitProgram(langosParser.ProgramContext ctx, FBB myBlock) {
        if (ctx.import_() != null) return visitImport_(ctx.import_());
        return visitElement(ctx.element(), myBlock);
    }

    static public Stream visitEntry_point(langosParser.Entry_pointContext ctx, FBB myBlock) {
        return ctx.program().stream().map(ctx1 -> visitProgram(ctx1, myBlock));
    }

    static public FBB visitCallArg(langosParser.CallArgContext ctx, FBB myBlock) {
        //getMember, char,string
        if (ctx.function_call_() != null) return visitFunction_call_(ctx.function_call_(), myBlock);
        if (ctx.lambda() != null) return visitLambda(ctx.lambda(), myBlock);
        if (ctx.ID() != null) return FBB.spawnID(ctx.ID().getText(), myBlock);//or find??
        return null;
    }

    static public Stream<FBB> visitCallArgs(langosParser.CallArgsContext ctx, FBB myBlock) {
        return ctx.callArg().stream().map(ctx1 -> visitCallArg(ctx1, myBlock));
    }

    static public Stream<String> visitLambdaArgs(langosParser.LambdaArgsContext ctx) {
        if (ctx.ID() != null) return Stream.of(ctx.ID().getText());
        if (ctx.id_list() != null) return ctx.id_list().ID().stream().map(ParseTree::getText);
        return Stream.of();
    }
}
