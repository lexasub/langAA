package org.lexasub.frontend.langosVisitors;

import org.lexasub.frontend.langosParser;
import org.lexasub.frontend.utils.FrontendBaseBlock;
import org.lexasub.frontend.utils.FunctionGenerators;

import java.util.function.Function;
import java.util.stream.Stream;

public class myLangosVisitor  implements myLangosVisitorInterface {
    @Override
    public Object visitImport_(langosParser.Import_Context ctx) {
        return null;
    }

    @Override
    public Object visitId_strong(langosParser.Id_strongContext ctx) {
        return null;
    }

    @Override
    public Function visitFun_name(langosParser.Fun_nameContext ctx, FrontendBaseBlock myBlock) {
        //pairmap,map,set,swap
        if(ctx.IF() != null) return FunctionGenerators.IF();
        if(ctx.WHILE() != null) return FunctionGenerators.WHILE();
        if(ctx.ID() != null) return FunctionGenerators.ID(ctx.ID().getText());
        return null;
    }

    @Override
    public Object visitId_list(langosParser.Id_listContext ctx) {
        return null;
    }

    @Override
    public Object visitType_name(langosParser.Type_nameContext ctx) {
        return null;
    }

    @Override
    public Object visitVar_name(langosParser.Var_nameContext ctx) {
        return null;
    }

    @Override
    public Object visitClass_name(langosParser.Class_nameContext ctx) {
        return null;
    }

    @Override
    public Object visitMember_name(langosParser.Member_nameContext ctx) {
        return null;
    }

    @Override
    public Object visitNamspce_obj(langosParser.Namspce_objContext ctx) {
        return null;
    }

    @Override
    public Object visitFunction_specifier(langosParser.Function_specifierContext ctx) {
        return null;
    }

    @Override
    public Object visitFunction(langosParser.FunctionContext ctx, FrontendBaseBlock myBlock) {
        //func spec
        //type
        Object funName = visitVar_name(ctx.var_name());
        FrontendBaseBlock newBlock = new FrontendBaseBlock();
        newBlock.type = FrontendBaseBlock.TYPE.FUNC;
        newBlock.parent = myBlock;
        Object funcArgs = visitFunc_args(ctx.func_args());
        Object body = visitBody(ctx.body(), newBlock);
        myBlock.addChild(newBlock);
        //createFunc
        return null;
    }

    @Override
    public Object visitExpr(langosParser.ExprContext ctx, FrontendBaseBlock myBlock) {
        //with_
        if(ctx.flow_control() != null) return visitFlow_control(ctx.flow_control(), myBlock);
        if(ctx.function_call_() != null) return  visitFunction_call_(ctx.function_call_(), myBlock);
        //class_
        if(ctx.lambda() != null) return visitLambda(ctx.lambda(), myBlock);
        //get_member
        //char, string, id
        return null;
    }

    @Override
    public Object visitGet_member(langosParser.Get_memberContext ctx) {
        return null;
    }

    @Override
    public Object visitExpr_list(langosParser.Expr_listContext ctx) {
        return null;
    }

    @Override
    public Object visitFunc_args(langosParser.Func_argsContext ctx) {
        return null;
    }

    @Override
    public Object visitMethod_call_(langosParser.Method_call_Context ctx) {
        return null;
    }

    @Override
    public Object visitFunction_call3(langosParser.Function_call3Context ctx) {
        return null;
    }

    @Override
    public Object visitFunction_call_helper_method(langosParser.Function_call_helper_methodContext ctx) {
        return null;
    }

    @Override
    public Object visitMethod_call(langosParser.Method_callContext ctx) {
        return null;
    }

    @Override
    public Object visitFunction_call(langosParser.Function_callContext ctx, FrontendBaseBlock myBlock) {
        Function funName = visitFun_name(ctx.fun_name(), myBlock);//->lambda
        Stream args = visitCallArgs(ctx.callArgs(), myBlock);
        return funName.apply(args);
    }

    @Override
    public Object visitFunction_call_helper(langosParser.Function_call_helperContext ctx) {
        return null;
    }

    @Override
    public Object visitFunction_call2(langosParser.Function_call2Context ctx, FrontendBaseBlock myBlock) {
        //function_call_helper
        if(ctx.function_call() != null) return visitFunction_call(ctx.function_call(), myBlock);
        return null;
    }

    @Override
    public Object visitFunction_call_(langosParser.Function_call_Context ctx, FrontendBaseBlock myBlock) {
        //method_Call
        if(ctx.function_call2() != null) return visitFunction_call2(ctx.function_call2(), myBlock);
        return null;
    }

    @Override
    public Object visitFlow_control(langosParser.Flow_controlContext ctx, FrontendBaseBlock myBlock) {
        if(ctx.CONTINUE() != null) return myBlock.CONTINUE();
        if(ctx.BREAK() != null) return myBlock.BREAK();
        //else return
        return visitReturn_expr(ctx.return_expr(), myBlock);
    }

    @Override
    public Object visitLambda(langosParser.LambdaContext ctx, FrontendBaseBlock myBlock) {
        Stream args = visitLambdaArgs(ctx.lambdaArgs());
        Stream body;
        FrontendBaseBlock newBlock = new FrontendBaseBlock();
        newBlock.type = FrontendBaseBlock.TYPE.LAMBDA;
        newBlock.parent = myBlock;
        if(ctx.body() != null)
            body = ctx.body().element().stream().map(ctx1 -> visitElement(ctx1, newBlock));//visitElem - visitExpr || visitFunc
        else body = Stream.of(visitExpr(ctx.expr(), newBlock));

        //createLambda()
        return null;
    }

    @Override
    public Object visitReturn_expr(langosParser.Return_exprContext ctx, FrontendBaseBlock myBlock) {
        if (ctx == null) return myBlock.RETURN();
        //getMember, char,string, id
        if (ctx.function_call_() != null) return visitFunction_call_(ctx.function_call_(), myBlock);
        if (ctx.lambda() != null) return visitLambda(ctx.lambda(), myBlock);
        return null;
    }

    @Override
    public Object visitElement(langosParser.ElementContext ctx, FrontendBaseBlock myBlock) {
        if(ctx.function() != null) return visitFunction(ctx.function(), myBlock);
        return visitExpr(ctx.expr(), myBlock);
    }

    @Override
    public Object visitWith_body(langosParser.With_bodyContext ctx) {
        return null;
    }

    @Override
    public Object visitWith_synonym(langosParser.With_synonymContext ctx) {
        return null;
    }

    @Override
    public Object visitWith_(langosParser.With_Context ctx) {
        return null;
    }

    @Override
    public Object visitWithArg(langosParser.WithArgContext ctx, FrontendBaseBlock myblock) {
        return visitExpr(ctx.expr(), myblock);
    }

    @Override
    public Object visitDeclare_member(langosParser.Declare_memberContext ctx) {
        return null;
    }

    @Override
    public Object visitClass_(langosParser.Class_Context ctx) {
        return null;
    }

    @Override
    public Object visitProgram(langosParser.ProgramContext ctx) {
        if(ctx.import_() != null) return visitImport_(ctx.import_());
        return visitElement(ctx.element(), null);
    }

    @Override
    public Stream visitEntry_point(langosParser.Entry_pointContext ctx) {
        return ctx.program().stream().map(this::visitProgram);//TODO
    }

    @Override
    public Stream visitCallArgs(langosParser.CallArgsContext ctx, FrontendBaseBlock myblock) {
        return null;
    }

    @Override
    public Stream visitLambdaArgs(langosParser.LambdaArgsContext ctx) {
        return null;
    }
}
