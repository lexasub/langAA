package org.lexasub.frontend.langosVisitors;

import org.lexasub.frontend.langosParser;
import org.lexasub.frontend.utils.FrontendBaseBlock;

public interface myLangosVisitorInterface {
    default Object visitImport_(langosParser.Import_Context ctx) {
        return null;
    }

    default Object visitId_strong(langosParser.Id_strongContext ctx) {
        return null;
    }

    default Object visitFun_name(langosParser.Fun_nameContext ctx) {
        return null;
    }

    default Object visitId_list(langosParser.Id_listContext ctx) {
        return null;
    }

    default Object visitType_name(langosParser.Type_nameContext ctx) {
        return null;
    }

    default Object visitVar_name(langosParser.Var_nameContext ctx) {
        return null;
    }

    default Object visitClass_name(langosParser.Class_nameContext ctx) {
        return null;
    }

    default Object visitMember_name(langosParser.Member_nameContext ctx) {
        return null;
    }

    default Object visitNamspce_obj(langosParser.Namspce_objContext ctx) {
        return null;
    }

    default Object visitFunction_specifier(langosParser.Function_specifierContext ctx) {
        return null;
    }

    default Object visitFunction(langosParser.FunctionContext ctx) {
        return null;
    }

    default Object visitExpr(langosParser.ExprContext ctx, FrontendBaseBlock myblock) {
        return null;
    }

    default Object visitGet_member(langosParser.Get_memberContext ctx) {
        return null;
    }

    default Object visitBody(langosParser.BodyContext ctx, FrontendBaseBlock myblock) {
        return null;
    }

    default Object visitExpr_list(langosParser.Expr_listContext ctx) {
        return null;
    }

    default Object visitFunc_args(langosParser.Func_argsContext ctx) {
        return null;
    }

    default Object visitMethod_call_(langosParser.Method_call_Context ctx) {
        return null;
    }

    default Object visitFunction_call3(langosParser.Function_call3Context ctx) {
        return null;
    }

    default Object visitFunction_call_helper_method(langosParser.Function_call_helper_methodContext ctx) {
        return null;
    }

    default Object visitMethod_call(langosParser.Method_callContext ctx) {
        return null;
    }

    default Object visitFunction_call(langosParser.Function_callContext ctx) {
        return null;
    }

    default Object visitFunction_call_helper(langosParser.Function_call_helperContext ctx) {
        return null;
    }

    default Object visitFunction_call2(langosParser.Function_call2Context ctx) {
        return null;
    }

    default Object visitFunction_call_(langosParser.Function_call_Context ctx, FrontendBaseBlock myblock) {
        return null;
    }

    default Object visitFlow_control(langosParser.Flow_controlContext ctx, FrontendBaseBlock myscope) {
        return null;
    }

    default Object visitLambda(langosParser.LambdaContext ctx, FrontendBaseBlock myblock) {
        return null;
    }

    default Object visitReturn_expr(langosParser.Return_exprContext ctx, FrontendBaseBlock myblock) {
        return null;
    }

    default Object visitElement(langosParser.ElementContext ctx, FrontendBaseBlock myblock) {
        return null;
    }

    default Object visitWith_body(langosParser.With_bodyContext ctx) {
        return null;
    }

    default Object visitWith_synonym(langosParser.With_synonymContext ctx) {
        return null;
    }

    default Object visitWith_(langosParser.With_Context ctx) {
        return null;
    }

    default Object visitWithArg(langosParser.WithArgContext ctx, FrontendBaseBlock myblock) {
        return null;
    }

    default Object visitDeclare_member(langosParser.Declare_memberContext ctx) {
        return null;
    }

    default Object visitClass_(langosParser.Class_Context ctx) {
        return null;
    }

    default Object visitProgram(langosParser.ProgramContext ctx) {
        return null;
    }

    default Object visitEntry_point(langosParser.Entry_pointContext ctx) {
        return null;
    }

    default Object visitParened_expr_list(langosParser.Parened_expr_listContext ctx) {
        return null;
    }

    default Object visitLambdaArgs(langosParser.LambdaArgsContext ctx) {
        return null;
    }
}
