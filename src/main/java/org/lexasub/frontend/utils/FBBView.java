package org.lexasub.frontend.utils;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.lexasub.frontend.langosLexer;
import org.lexasub.frontend.langosParser;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.lexasub.frontend.langosVisitors.myLangosVisitor.visitEntry_point;

public class FBBView {
    public static langosParser getParser(String filemame) throws IOException {
        CharStream stream = CharStreams.fromFileName(filemame, StandardCharsets.UTF_8);
        langosLexer lexer = new langosLexer(stream);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        langosParser parser = new langosParser(tokens);
        return parser;
    }

    public static FBB visit(langosParser parser) {
        FBB myBlock = new FBB();
        visitEntry_point(parser.entry_point(), myBlock).forEach(myBlock::fullLinkWith);
        return myBlock;
    }
}