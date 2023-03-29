package org.lexasub.frontend.langosVisitors;

import org.lexasub.frontend.langosParser;
import org.lexasub.frontend.utils.FBB;

public interface myLangosVisitorInterface {
    static Object visitImport_(langosParser.Import_Context ctx) {
        return null;
    }

    static String visitVar_name(langosParser.Var_nameContext ctx) {
        return null;
    }

    static Object visitClass_name(langosParser.Class_nameContext ctx) {
        return null;
    }

    static Object visitMember_name(langosParser.Member_nameContext ctx) {
        return null;
    }

    static Object visitNamspce_obj(langosParser.Namspce_objContext ctx) {
        return null;
    }

    static Object visitFunction_specifier(langosParser.Function_specifierContext ctx) {
        return null;
    }

    static Object visitExpr(langosParser.ExprContext ctx, FBB myblock) {
        return null;
    }

    static Object visitGet_member(langosParser.Get_memberContext ctx) {
        return null;
    }

    static Object visitMethod_call_(langosParser.Method_call_Context ctx) {
        return null;
    }

    static Object visitFunction_call3(langosParser.Function_call3Context ctx) {
        return null;
    }

    static Object visitFunction_call_helper_method(langosParser.Function_call_helper_methodContext ctx) {
        return null;
    }

    static Object visitMethod_call(langosParser.Method_callContext ctx) {
        return null;
    }

    static Object visitFunction_call(langosParser.Function_callContext ctx, FBB myblock) {
        return null;
    }

    static Object visitFunction_call_helper(langosParser.Function_call_helperContext ctx) {
        return null;
    }

    static Object visitFunction_call2(langosParser.Function_call2Context ctx, FBB myblock) {
        return null;
    }

    static Object visitFunction_call_(langosParser.Function_call_Context ctx, FBB myblock) {
        return null;
    }

    static Object visitFlow_control(langosParser.Flow_controlContext ctx, FBB myscope) {
        return null;
    }

    static Object visitLambda(langosParser.LambdaContext ctx, FBB myblock) {
        return null;
    }

    static Object visitReturn_expr(langosParser.Return_exprContext ctx, FBB myblock) {
        return null;
    }

    static Object visitElement(langosParser.ElementContext ctx, FBB myblock) {
        return null;
    }

    static Object visitWith_body(langosParser.With_bodyContext ctx) {
        return null;
    }

    static Object visitWith_synonym(langosParser.With_synonymContext ctx) {
        return null;
    }

    static Object visitWith_(langosParser.With_Context ctx) {
        return null;
    }


    static Object visitWithArg(langosParser.WithArgContext ctx, FBB myblock) {
        return null;
    }

    ;

    static Object visitDeclare_member(langosParser.Declare_memberContext ctx) {
        return null;
    }

    static Object visitClass_(langosParser.Class_Context ctx) {
        return null;
    }

    static Object visitProgram(langosParser.ProgramContext ctx, FBB myBlock) {
        return null;
    }

    static Object visitEntry_point(langosParser.Entry_pointContext ctx, FBB myBlock) {
        return null;
    }

    static Object visitCallArg(langosParser.CallArgContext ctx, FBB myblock) {
        return null;
    }

    static Object visitCallArgs(langosParser.CallArgsContext ctx, FBB myblock) {
        return null;
    }

    static Object visitLambdaArgs(langosParser.LambdaArgsContext ctx) {
        return null;
    }
}
