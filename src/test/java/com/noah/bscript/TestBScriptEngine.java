package com.noah.bscript;

import com.noah.bscript.exceptions.BScriptException;
import com.noah.bscript.lang.BScript;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.File;

public class TestBScriptEngine {

    @Test
    void loadInvalidScripts() {
        BScriptEngine engine = new BScriptEngine();
        assertThrows(BScriptException.class, () -> engine.load(new File("")));
        assertThrows(BScriptException.class, () -> engine.load(new File("scripts/loadfile")));
        assertThrows(BScriptException.class, () -> engine.load(new File("scripts/loadscript.txt")));
        assertThrows(BScriptException.class, () -> engine.load(new File("scripts/loadfile.bscript")));
    }

    @Test
    void loadValidScript() {
        BScriptEngine engine = new BScriptEngine();
        assertDoesNotThrow(() -> {
            BScript script = engine.load(new File("scripts/test.bscript"));
        });
    }

}
