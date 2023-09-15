package com.noah.bscript;

import com.noah.bscript.lang.BToken;
import com.noah.bscript.runtime.BEnvironment;
import com.noah.bscript.runtime.BInterpreter;
import com.noah.bscript.runtime.BLexer;
import com.noah.bscript.runtime.BParser;
import lombok.Getter;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

public class BScript {

    private File file;
    @Getter private boolean failed;

    private BInterpreter interpreter;

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
        this.interpreter = new BInterpreter(this, parser.parse());
    }

    public void run() {
        if (this.failed) return;
        this.interpreter.interpret();
    }

    public void define(String name, Object value) {
        BEnvironment environment = this.interpreter.getEnvironment();
        environment.define(name, value);
    }

    public void error(int line, String location, String message) {
        this.failed = true;
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
