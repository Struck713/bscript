package com.noah.bscript.lang;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;

@AllArgsConstructor
@Getter
public class BToken {

    private Type type;
    private String lexeme;
    private Object literal;
    private int position;

    @Getter
    public enum Type {

        // SINGLES
        LEFT_PAREN, RIGHT_PAREN,
        LEFT_BRACE, RIGHT_BRACE,
        COMMA, DOT, MINUS, PLUS,
        SEMICOLON, SLASH, STAR,

        // LOGICAL
        NOT, NOT_EQUAL,
        EQUAL, EQUAL_EQUAL,
        GREATER, GREATER_EQUAL,
        LESS, LESS_EQUAL,

        // KEYWORDS
        AND("and"),
        OR("or"),
        DEF("def"),
        LET("let"),
        CLASS("class"),
        ELSE("else"),
        IF("if"),
        FOR("for"),
        WHILE("while"),
        NULL("null"),
        PRINT("print"),
        RETURN("return"),
        SUPER("super"),
        THIS("this"),
        TRUE("true"),
        FALSE("false"),

        // LITERALS
        STRING,
        NUMBER,
        IDENTIFIER,

        // OTHER
        EOF;

        public static Type getByText(String text) {
            for (Type type :  Type.values()) {
                if (Objects.equals(type.getText(), text)) return type;
            }
            return null;
        }

        private String text;

        Type(String text) { this.text = text; }
        Type() { this(null); }

    }
}
