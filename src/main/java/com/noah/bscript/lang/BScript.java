package com.noah.bscript.lang;

import com.noah.bscript.exceptions.BScriptException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

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
        lexer.tokenize();
        lexer.print();

    }

    public void error(int line, int position, String message) {
        System.out.printf("%s:%d:%d %s", file.getPath(), line, position, message);
        System.out.println();
    }

}
