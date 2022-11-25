package org.lexasub.frontend.langosVisitors;

import org.lexasub.frontend.langosParser;
import org.lexasub.frontend.utils.FrontendBaseBlock;
import org.lexasub.frontend.utils.FunctionGenerators;

import java.util.Iterator;
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
        if(ctx.IF() != null) return FunctionGenerators.IF(myBlock);
        if(ctx.WHILE() != null) return FunctionGenerators.WHILE(myBlock);
        if(ctx.ID() != null) return FunctionGenerators.ID(ctx.ID().getText(), myBlock);
        return null;
    }

    @Override
    public Object visitId_list(langosParser.Id_listContext ctx) {
        return null;
    }

    @Override
    public String visitType_name(langosParser.Type_nameContext ctx) {
        return ctx.ID().getText();
    }

    @Override
    public String visitVar_name(langosParser.Var_nameContext ctx) {
        return ctx.ID().getText();
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
        FrontendBaseBlock newBlock = new FrontendBaseBlock();
        newBlock.type = FrontendBaseBlock.TYPE.FUNC;
        newBlock.name = visitVar_name(ctx.var_name());
        newBlock.parent = myBlock;
        visitFunc_args(ctx.func_args(), newBlock);//result-insertedVariables
        Stream body = visitBody(ctx.body(), newBlock);
        body.count();
        myBlock.addChild(newBlock);
        //createFunc
        return newBlock;
    }

    @Override
    public FrontendBaseBlock visitExpr(langosParser.ExprContext ctx, FrontendBaseBlock myBlock) {
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
    public void visitFunc_args(langosParser.Func_argsContext ctx, FrontendBaseBlock newBlock) {
        Iterator<String> names = ctx.var_name().stream().map(this::visitVar_name).iterator();
        Iterator<String> types = ctx.type_name().stream().map(this::visitType_name).iterator();
        while (names.hasNext()){
            newBlock.declareVariable(types.next(), names.next());
        }
    }
    @Override
    public Stream visitBody(langosParser.BodyContext ctx, FrontendBaseBlock newBlock) {
        return ctx.element().stream().map(i->visitElement(i, newBlock));
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
    public FrontendBaseBlock visitFunction_call(langosParser.Function_callContext ctx, FrontendBaseBlock myBlock) {
        Function funName = visitFun_name(ctx.fun_name(), myBlock);//->lambda
        Stream args = visitCallArgs(ctx.callArgs(), myBlock);
        return (FrontendBaseBlock) funName.apply(args);
    }

    @Override
    public Object visitFunction_call_helper(langosParser.Function_call_helperContext ctx) {
        return null;
    }

    @Override
    public FrontendBaseBlock visitFunction_call2(langosParser.Function_call2Context ctx, FrontendBaseBlock myBlock) {
        //function_call_helper
        if(ctx.function_call() != null) return visitFunction_call(ctx.function_call(), myBlock);
        return null;
    }

    @Override
    public FrontendBaseBlock visitFunction_call_(langosParser.Function_call_Context ctx, FrontendBaseBlock myBlock) {
        //method_Call
        if(ctx.function_call2() != null) return visitFunction_call2(ctx.function_call2(), myBlock);
        return null;
    }

    @Override
    public FrontendBaseBlock visitFlow_control(langosParser.Flow_controlContext ctx, FrontendBaseBlock myBlock) {
        if(ctx.CONTINUE() != null) return myBlock.CONTINUE();
        if(ctx.BREAK() != null) return myBlock.BREAK();
        //else return
        return visitReturn_expr(ctx.return_expr(), myBlock);
    }

    @Override
    public FrontendBaseBlock visitLambda(langosParser.LambdaContext ctx, FrontendBaseBlock myBlock) {
        Stream args = visitLambdaArgs(ctx.lambdaArgs());
        Stream body;
        FrontendBaseBlock newBlock = new FrontendBaseBlock();
        newBlock.type = FrontendBaseBlock.TYPE.LAMBDA;
        args.map(i->newBlock.declareVariable(i));
        newBlock.parent = myBlock;
        if(ctx.body() != null)
            body = ctx.body().element().stream().map(ctx1 -> visitElement(ctx1, newBlock));//visitElem - visitExpr || visitFunc
        else body = Stream.of(visitExpr(ctx.expr(), newBlock));
        myBlock.addChild(newBlock);
        return myBlock;
    }

    public Object visitLambdaAsArgumentOfCall(langosParser.LambdaContext ctx, FrontendBaseBlock myBlock) {
        Stream args = visitLambdaArgs(ctx.lambdaArgs());
        Stream body;
        FrontendBaseBlock newBlock = new FrontendBaseBlock();
        newBlock.type = FrontendBaseBlock.TYPE.LAMBDA;
        args.map(i->newBlock.declareVariable(i));
        newBlock.parent = myBlock;
        if(ctx.body() != null)
            body = ctx.body().element().stream().map(ctx1 -> visitElement(ctx1, newBlock));//visitElem - visitExpr || visitFunc
        else body = Stream.of(visitExpr(ctx.expr(), newBlock));
        //TODO
        return body;
    }

    @Override
    public FrontendBaseBlock visitReturn_expr(langosParser.Return_exprContext ctx, FrontendBaseBlock myBlock) {
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
    public Object visitProgram(langosParser.ProgramContext ctx, FrontendBaseBlock myBlock) {
        if(ctx.import_() != null) return visitImport_(ctx.import_());
        return visitElement(ctx.element(), myBlock);
    }

    @Override
    public Stream visitEntry_point(langosParser.Entry_pointContext ctx) {
        FrontendBaseBlock myBlock = new FrontendBaseBlock();
        return ctx.program().stream().map(ctx1 -> visitProgram(ctx1, myBlock));//TODO
    }
    @Override
    public Object visitCallArg(langosParser.CallArgContext ctx, FrontendBaseBlock myBlock) {
        //getMember, char,string, id
        if (ctx.function_call_() != null) return visitFunction_call_(ctx.function_call_(), myBlock);
        if (ctx.lambda() != null) return visitLambdaAsArgumentOfCall(ctx.lambda(), myBlock);
        return null;
    }
    @Override
    public Stream visitCallArgs(langosParser.CallArgsContext ctx, FrontendBaseBlock myBlock) {
        return ctx.callArg().stream().map(ctx1 -> visitCallArg(ctx1, myBlock));
    }

    @Override
    public Stream visitLambdaArgs(langosParser.LambdaArgsContext ctx) {
        if(ctx.ID() != null) return Stream.of(ctx.ID().getText());
        return ctx.id_list().ID().stream().map(i->i.getText());
    }
}
