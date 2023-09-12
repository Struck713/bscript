package com.noah.bscript.lang;

import lombok.Getter;

@Getter
public enum BType {

    // PRIMATIVES
    NUMBER,

    // LOGICAL
    ADD,
    SUBTRACT,
    DIVIDE,
    MULTIPLY,

    // BOOL
    TRUE,
    FALSE,
    IDENTIFIER,

    // OTHER
    EOF;

    public static BType decodeLogicalType(char character) {
        switch (character) {
            case '+': return BType.ADD;
            case '-': return BType.SUBTRACT;
            case '/': return BType.DIVIDE;
            case '*': return BType.MULTIPLY;
        }
        return null;
    }

}
