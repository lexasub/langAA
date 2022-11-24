package org.lexasub.frontend;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.lexasub.frontend.langosVisitors.myLangosVisitor;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.stream.Stream;

public class IO {
    public static void main(String[] args) throws IOException {

        //Asm.pretty = true;//Set output with tabs
        //Asm.print(
        visit(getParser("test"));
        System.out.println("test");
    }

    public static langosParser getParser(String filemame) throws IOException {
        CharStream stream = CharStreams.fromFileName(filemame, StandardCharsets.UTF_8);
        langosLexer lexer = new langosLexer(stream);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        langosParser parser = new langosParser(tokens);
        return parser;
    }

    public static Stream visit(langosParser parser) {
        myLangosVisitor visitor = new myLangosVisitor();
        return visitor.visitEntry_point(parser.entry_point());
    }
}
