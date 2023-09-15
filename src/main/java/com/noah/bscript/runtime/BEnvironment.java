package com.noah.bscript.runtime;

import com.noah.bscript.exceptions.BRuntimeException;
import com.noah.bscript.lang.BToken;

import java.util.HashMap;
import java.util.Map;

public class BEnvironment {

    private BEnvironment enclosing = null;
    private final Map<String, Object> values = new HashMap<>();

    public BEnvironment() {}
    public BEnvironment(BEnvironment enclosing) { this.enclosing = enclosing; }

    public void define(String name, Object value) {
        this.values.put(name, value);
    }

    public void redefine(BToken name, Object value) {
        String lexeme = name.getLexeme();
        if (this.values.containsKey(lexeme)) {
            this.values.put(lexeme, value);
            return;
        }

        if (this.enclosing != null) {
            this.enclosing.redefine(name, value);
            return;
        }

        throw new BRuntimeException(name, "Invalid assignment: '" + lexeme + "'.");
    }

    public Object get(BToken name) {
        String lexeme = name.getLexeme();
        if (this.values.containsKey(lexeme)) return this.values.get(lexeme);
        if (this.enclosing != null) return this.enclosing.get(name);
        throw new BRuntimeException(name, "Undefined variable: '" + lexeme + "'.");
    }

}
