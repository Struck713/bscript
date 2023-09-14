package com.noah.bscript.lang;

import com.noah.bscript.lang.tools.BAstPrinter;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

public class BScript {

    private File file;

    public BScript(File file) {
        this.file = file;
    }

    /**
     * Load the tokens of the tree
     */
    public void load() {

        String source;
        try {
            byte[] bytes = Files.readAllBytes(Paths.get(file.getPath()));
            source = new String(bytes, Charset.defaultCharset());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        BLexer lexer = new BLexer(this, source);
        BParser parser = new BParser(this, lexer.tokenize());
        BExpression expression = parser.parse();

        BInterpreter interpreter = new BInterpreter(this);
        interpreter.interpret(expression);

        System.out.println(new BAstPrinter().print(expression));

    }

    public void error(int line, String location, String message) {
        System.out.printf("In %s:%d - '%s' %s", file.getPath(), line, message, location);
        System.out.println();
    }

    public void error(int line, String message) {
        this.error(line, "", message);
    }

    public void error(BToken token, String message) {
        if (token.getType() == BToken.Type.EOF) {
            this.error(token.getPosition(), "at end ", message);
        } else {
            this.error(token.getPosition(), "at '" + token.getLexeme() + "'", message);
        }
    }

}
