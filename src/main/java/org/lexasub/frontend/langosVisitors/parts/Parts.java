package org.lexasub.frontend.langosVisitors.parts;

import org.lexasub.frontend.langosParser;
import org.lexasub.frontend.langosVisitors.myLangosVisitor;
import org.lexasub.frontend.utils.FBB;

import java.util.Iterator;
import java.util.stream.Stream;

import static org.lexasub.frontend.langosVisitors.myLangosVisitor.visitElement;
import static org.lexasub.frontend.langosVisitors.myLangosVisitor.visitVar_name;

public class Parts {

    public static FBB visitFunction(langosParser.FunctionContext ctx, final FBB myBlock) {
        //func spec
        //type
        FBB newBlock = new FBB();
        newBlock.type = FBB.TYPE.FUNC;
        newBlock.setName(visitVar_name(ctx.var_name()));
        newBlock.setParent(myBlock);
        visitFunc_args(ctx.func_args(), newBlock);//result-insertedVariables
        visitBody(ctx.body(), newBlock).forEach(newBlock::fullLinkWith);
        return newBlock;
    }

    public static void visitFunc_args(langosParser.Func_argsContext ctx, FBB newBlock) {
        Iterator<String> names = ctx.var_name().stream().map(myLangosVisitor::visitVar_name).iterator();
        Iterator<String> types = ctx.type_name().stream().map(Parts::visitType_name).iterator();
        while (names.hasNext()) {
            newBlock.declareVariable(types.next(), names.next());
        }
    }

    public static String visitType_name(langosParser.Type_nameContext ctx) {
        return ctx.ID().getText();
    }

    public static Stream<FBB> visitBody(langosParser.BodyContext ctx, FBB newBlock) {
        return ctx.element().stream().map(i -> visitElement(i, newBlock));
    }
}
