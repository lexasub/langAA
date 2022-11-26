package org.lexasub.frontend;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.lexasub.frontend.langosVisitors.myLangosVisitor;
import org.lexasub.frontend.utils.FrontendBaseBlock;

import java.io.IOException;
import java.nio.charset.StandardCharsets;


public class IO {
    public static void main(String[] args) throws IOException {

        //Asm.pretty = true;//Set output with tabs
        //Asm.print(
        FrontendBaseBlock block = visit(getParser("test"));
        StringBuilder sb = new StringBuilder();
        block.dump("", sb);
        System.out.println(sb);

        StringBuilder sb1 = new StringBuilder();
        block.serialize(sb1);
        System.out.println(sb1);

        FrontendBaseBlock newBlock = FrontendBaseBlock.deserialize(sb1.toString().split("\n \n"));
        StringBuilder sb2 = new StringBuilder();
        newBlock.dump("", sb2);
        System.out.println(sb2);
    }

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
                .forEach(i->{
                    ((FrontendBaseBlock) i).parent = myBlock;
                    myBlock.addChild((FrontendBaseBlock) i);
                });
        return myBlock;
    }
}
