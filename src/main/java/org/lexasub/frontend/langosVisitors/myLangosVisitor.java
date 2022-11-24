package org.lexasub.frontend.langosVisitors;

import org.lexasub.frontend.langosParser;
import org.lexasub.frontend.utils.FrontendBaseBlock;

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
    public Object visitFun_name(langosParser.Fun_nameContext ctx) {
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
    public Object visitFunction(langosParser.FunctionContext ctx) {
        return null;
    }

    @Override
    public Object visitExpr(langosParser.ExprContext ctx, FrontendBaseBlock myblock) {
        //with_
        if(ctx.flow_control() != null) return visitFlow_control(ctx.flow_control(), myblock);
        if(ctx.function_call_() != null) return  visitFunction_call_(ctx.function_call_(), myblock);
        //class_
        if(ctx.lambda() != null) return visitLambda(ctx.lambda(), myblock);
        //get_member
        //char, string, id
        return null;
    }

    @Override
    public Object visitGet_member(langosParser.Get_memberContext ctx) {
        return null;
    }

    @Override
    public Object visitBody(langosParser.BodyContext ctx, FrontendBaseBlock myblock) {
        return ctx.element().stream().map(ctx1 -> visitElement(ctx1, myblock));
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
    public Object visitFunction_call(langosParser.Function_callContext ctx) {
        return null;
    }

    @Override
    public Object visitFunction_call_helper(langosParser.Function_call_helperContext ctx) {
        return null;
    }

    @Override
    public Object visitFunction_call2(langosParser.Function_call2Context ctx) {
        return null;
    }

    @Override
    public Object visitFunction_call_(langosParser.Function_call_Context ctx, FrontendBaseBlock myblock) {
        return null;
    }

    @Override
    public Object visitFlow_control(langosParser.Flow_controlContext ctx, FrontendBaseBlock myblock) {
        if(ctx.CONTINUE() != null) return myblock.CONTINUE();
        if(ctx.BREAK() != null) return myblock.BREAK();
        //else return
        return visitReturn_expr(ctx.return_expr(), myblock);
    }

    @Override
    public Object visitLambda(langosParser.LambdaContext ctx, FrontendBaseBlock myblock) {
        Object args = visitLambdaArgs(ctx.lambdaArgs());
        Object body;
        FrontendBaseBlock newblock = new FrontendBaseBlock();
        newblock.type = FrontendBaseBlock.TYPE.LAMBDA;
        newblock.parent = myblock;
        if(ctx.body() != null) body = visitBody(ctx.body(), newblock);
        else body = visitExpr(ctx.expr(), newblock);
        myblock.addChild(newblock);
        //createLambda()
        return null;
    }

    @Override
    public Object visitReturn_expr(langosParser.Return_exprContext ctx, FrontendBaseBlock myblock) {
        if (ctx == null) return myblock.RETURN();
        //getMember, char,string, id
        if (ctx.function_call_() != null) return visitFunction_call_(ctx.function_call_(), myblock);
        if (ctx.lambda() != null) return visitLambda(ctx.lambda(), myblock);
        return null;
    }

    @Override
    public Object visitElement(langosParser.ElementContext ctx, FrontendBaseBlock myblock) {
        if(ctx.function() != null) return visitFunction(ctx.function());
        return visitExpr(ctx.expr(), myblock);
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
    public Object visitEntry_point(langosParser.Entry_pointContext ctx) {
        ctx.program().stream().map(this::visitProgram);//TODO
        return null;
    }

    @Override
    public Object visitParened_expr_list(langosParser.Parened_expr_listContext ctx) {
        return null;
    }

    @Override
    public Object visitLambdaArgs(langosParser.LambdaArgsContext ctx) {
        return null;
    }
}
