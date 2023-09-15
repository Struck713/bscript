package com.noah.bscript;

import com.noah.bscript.exceptions.BScriptException;
import com.noah.bscript.tools.BAstPrinter;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.File;

public class TestBScriptEngine {

    public static final BScriptEngine ENGINE = new BScriptEngine();

    @Test
    void loadInvalidScripts() {
        assertThrows(BScriptException.class, () -> ENGINE.load(new File("")));
        assertThrows(BScriptException.class, () -> ENGINE.load(new File("scripts/loadfile")));
        assertThrows(BScriptException.class, () -> ENGINE.load(new File("scripts/loadscript.txt")));
        assertThrows(BScriptException.class, () -> ENGINE.load(new File("scripts/loadfile.bscript")));
    }

    @Test
    void testAssignment() {
        assertDoesNotThrow(() -> {
            BScript script = ENGINE.load(new File("scripts/assignment.bscript"));
            script.run();
        });
    }

    @Test
    void testScoping() {
        assertDoesNotThrow(() -> {
            BScript script = ENGINE.load(new File("scripts/scoping.bscript"));
            script.run();
        });
    }

    @Test
    void testDefiningVariable() {
        assertDoesNotThrow(() -> {
            BScript script = ENGINE.load(new File("scripts/defined.bscript"));
            script.define("DEFINED_IN_JAVA", "This string was defined in Java.");
            script.define("PI", Math.PI);
            script.run();
        });
    }

    @Test
    void testControlFlow() {
        assertDoesNotThrow(() -> {
            BScript script = ENGINE.load(new File("scripts/control.bscript"));
            script.run();
        });
    }

    @Test
    void testShortCircuit() {
        assertDoesNotThrow(() -> {
            BScript script = ENGINE.load(new File("scripts/short_circuit.bscript"));
            script.run();
        });
    }

    @Test
    void testLoops() {
        assertDoesNotThrow(() -> {
            BScript script = ENGINE.load(new File("scripts/loop.bscript"));
            script.run();
        });
    }

}
