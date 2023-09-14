package com.noah.bscript.lang;

import com.noah.bscript.exceptions.BParseException;
import com.noah.bscript.exceptions.BScriptException;

import java.util.List;

public class BParser {

    private BScript script;
    private final List<BToken> tokens;
    private int current = 0;

    public BParser(BScript script, List<BToken> tokens) {
        this.script = script;
        this.tokens = tokens;
    }

    public BExpression parse() {
        return this.expression();
    }

    // expression     → equality
    private BExpression expression() {
        return this.equality();
    }

    // equality       → comparison ( ( "!=" | "==" ) comparison )* ;
    private BExpression equality() {
        BExpression expression = this.comparison();

        while (this.match(BToken.Type.NOT_EQUAL, BToken.Type.EQUAL_EQUAL)) {
            BToken operator = this.previous();
            BExpression right = this.comparison();
            expression = new BExpression.Binary(expression, operator, right);
        }

        return expression;
    }

    // comparison     → term ( ( ">" | ">=" | "<" | "<=" ) term )* ;
    private BExpression comparison() {
        BExpression expression = this.term();

        while (this.match(BToken.Type.GREATER, BToken.Type.GREATER_EQUAL, BToken.Type.LESS, BToken.Type.LESS_EQUAL)) {
            BToken operator = this.previous();
            BExpression right = this.term();
            expression = new BExpression.Binary(expression, operator, right);
        }

        return expression;
    }

    // comparison     → term ( ( ">" | ">=" | "<" | "<=" ) term )* ;
    private BExpression term() {
        BExpression expression = this.factor();

        while (this.match(BToken.Type.MINUS, BToken.Type.PLUS)) {
            BToken operator = this.previous();
            BExpression right = this.factor();
            expression = new BExpression.Binary(expression, operator, right);
        }

        return expression;
    }

    //factor         → factor ( "/" | "*" ) unary
    //               | unary ;
    private BExpression factor() {
        BExpression expression = this.unary();

        while (this.match(BToken.Type.SLASH, BToken.Type.STAR)) {
            BToken operator = this.previous();
            BExpression right = this.unary();
            expression = new BExpression.Binary(expression, operator, right);
        }

        return expression;
    }

    // unary          → ( "!" | "-" ) unary
    //                | primary ;
    private BExpression unary() {
        if (this.match(BToken.Type.NOT, BToken.Type.MINUS)) {
            BToken operator = this.previous();
            BExpression right = this.unary();
            return new BExpression.Unary(operator, right);
        }

        return this.primary();
    }

    private BExpression primary() {
        if (this.match(BToken.Type.TRUE)) return new BExpression.Literal(true);
        if (this.match(BToken.Type.FALSE)) return new BExpression.Literal(false);
        if (this.match(BToken.Type.NULL)) return new BExpression.Literal(null);

        if (this.match(BToken.Type.NUMBER, BToken.Type.STRING)) {
            return new BExpression.Literal(this.previous().getLiteral());
        }

        if (this.match(BToken.Type.LEFT_PAREN)) {
            BExpression expression = this.expression();
            this.consume(BToken.Type.RIGHT_PAREN, "Expected ')' after expression");
            return new BExpression.Grouping(expression);
        }

        this.script.error(peek(), "Expected expression.");
        throw new BParseException();
    }

    private boolean match(BToken.Type... types) {
        for (BToken.Type type : types) {
            if (this.check(type)) {
                this.advance();
                return true;
            }
        }
        return false;
    }

    private boolean check(BToken.Type type) {
        if (isEnd()) return false;
        return peek().getType() == type;
    }

    private BToken advance() {
        if (!isEnd()) this.current++;
        return previous();
    }

    private BToken peek() {
        return this.tokens.get(this.current);
    }

    private BToken previous() {
        return this.tokens.get(this.current - 1);
    }

    private BToken consume(BToken.Type type, String message) {
        if (this.check(type)) return this.advance();

        this.script.error(peek(), message);
        throw new BParseException();
    }

    private void synchronize() {
        this.advance();

        while (!isEnd()) {
            if (this.previous().getType() == BToken.Type.SEMICOLON) return;

            switch (this.peek().getType()) {
                case CLASS:
                case DEF:
                case LET:
                case FOR:
                case IF:
                case WHILE:
                case PRINT:
                case RETURN:
                    return;
            }

            this.advance();
        }
    }

    private boolean isEnd() {
        return this.peek().getType() == BToken.Type.EOF;
    }


}
