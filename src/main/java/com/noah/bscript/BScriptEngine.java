package com.noah.bscript;

import com.noah.bscript.exceptions.BScriptException;

import java.io.*;

public class BScriptEngine {

    public static final String FILE_EXTENSION = ".bscript";

    /**
     * Load a bScript from a file path
     *
     * @param file the File path
     */
    public BScript load(File file) {
        if (!isValid(file)) throw new BScriptException("Invalid BScript file: " + file);

        BScript script = new BScript(file);
        script.load();

        if (script.isFailed()) {
            System.out.println("Script could not load. Parsing failed with errors.");
            return null;
        }

        return script;
    }

    /**
     * Check if a file is a valid BScript
     *
     * @param file the {@link File} that we are checking
     * @return if the {@link File} is valid or not
     */
    public boolean isValid(File file) {
        return file.exists()
                && file.isFile()
                && file.getName()
                       .endsWith(BScriptEngine.FILE_EXTENSION);
    }

}
