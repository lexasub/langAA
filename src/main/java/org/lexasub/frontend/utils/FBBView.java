package org.lexasub.frontend.utils;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.lexasub.frontend.langosLexer;
import org.lexasub.frontend.langosParser;
import org.lexasub.frontend.langosVisitors.myLangosVisitor;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class FBBView {
    public static langosParser getParser(String filemame) throws IOException {
        CharStream stream = CharStreams.fromFileName(filemame, StandardCharsets.UTF_8);
        langosLexer lexer = new langosLexer(stream);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        langosParser parser = new langosParser(tokens);
        return parser;
    }

    public static FrontendBaseBlock visit(langosParser parser) {
        myLangosVisitor visitor = new myLangosVisitor();
        FrontendBaseBlock myBlock = new FrontendBaseBlock();
        visitor.visitEntry_point(parser.entry_point(), myBlock)
                .forEach(i -> myBlock.fullLinkWith((FrontendBaseBlock) i));
        return myBlock;
    }
}