package com.noah.bscript.runtime;

import com.noah.bscript.BScript;
import com.noah.bscript.exceptions.BParseException;
import com.noah.bscript.lang.BExpression;
import com.noah.bscript.lang.BStatement;
import com.noah.bscript.lang.BToken;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BParser {

    private BScript script;
    private final List<BToken> tokens;
    private int current = 0;

    public BParser(BScript script, List<BToken> tokens) {
        this.script = script;
        this.tokens = tokens;
    }

    public List<BStatement> parse() {
        List<BStatement> statements = new ArrayList<>();
        while (!isEnd()) statements.add(this.declaration());
        return statements;
    }

    ///////////////////////////////////////////////////
    // STATEMENTS
    ///////////////////////////////////////////////////

    // block → "{" declaration* "}"
    private List<BStatement> block() {
        List<BStatement> statements = new ArrayList<>();
        while (!check(BToken.Type.RIGHT_BRACE) && !isEnd()) statements.add(this.declaration());

        this.consume(BToken.Type.RIGHT_BRACE, "Expected a '}' after block.");
        return statements;
    }

    // declaration    → letDecl
    //                | statement ;
    private BStatement declaration() {
        try {
            if (this.match(BToken.Type.LET)) return this.letDeclaration();
            return this.statement();
        } catch (BParseException exception) {
            this.synchronize();
            return null;
        }
    }

    // letDecl        → "let" IDENTIFIER ( "=" expression )? ";" ;
    private BStatement letDeclaration() {
        BToken name = this.consume(BToken.Type.IDENTIFIER, "Expected variable name.");

        BExpression initializer = null;
        if (this.match(BToken.Type.EQUAL)) initializer = this.expression();

        this.consume(BToken.Type.SEMICOLON, "Expected ';' after variable declaration.");
        return new BStatement.Let(name, initializer);
    }

    // statement      → exprStmt
    //                | forStatement
    //                | ifStatement
    //                | printStmt
    //                | whileStatement
    //                | block ;
    private BStatement statement() {
        if (this.match(BToken.Type.FOR)) return this.forStatement();
        if (this.match(BToken.Type.IF)) return this.ifStatement();
        if (this.match(BToken.Type.WHILE)) return this.whileStatement();
        if (this.match(BToken.Type.PRINT)) return this.printStatement();
        if (this.match(BToken.Type.LEFT_BRACE)) return new BStatement.Block(this.block());
        return this.expressionStatement();
    }

    // ifStmt         → "if" "(" expression ")" statement
    //                ( "else" statement )? ;
    private BStatement ifStatement() {
        this.consume(BToken.Type.LEFT_PAREN, "Expected '(' after 'if'.");
        BExpression expression = this.expression();
        this.consume(BToken.Type.RIGHT_PAREN, "Expected ')' after if condition.");

        BStatement thenBranch = this.statement();
        BStatement elseBranch = null;
        if (this.match(BToken.Type.ELSE)) elseBranch = this.statement();
        return new BStatement.If(expression, thenBranch, elseBranch);
    }

    // whileStatement      → "while" "(" expression ")" statement;
    private BStatement whileStatement() {
        this.consume(BToken.Type.LEFT_PAREN, "Expected '(' after 'while'.");
        BExpression expression = this.expression();
        this.consume(BToken.Type.RIGHT_PAREN, "Expected ')' after while condition.");
        return new BStatement.While(expression, this.statement());
    }

    // forStmt        → "for" "(" ( varDecl | exprStmt | ";" )
    //                  expression? ";"
    //                  expression? ")" statement ;
    private BStatement forStatement() {
        this.consume(BToken.Type.LEFT_PAREN, "Expected '(' after 'for'.");

        // grab the initializer variable if there is one
        BStatement initializer;
        if (this.match(BToken.Type.SEMICOLON)) initializer = null;
        else if (this.match(BToken.Type.LET)) initializer = this.letDeclaration();
        else initializer = this.expressionStatement();

        // grab the conditional if there is one
        BExpression condition = null;
        if (!this.check(BToken.Type.SEMICOLON)) condition = this.expression();
        this.consume(BToken.Type.SEMICOLON, "Expect ';' after loop condition.");

        // grab the increment if there is one
        BExpression increment = null;
        if (!this.check(BToken.Type.RIGHT_PAREN)) increment = this.expression();
        this.consume(BToken.Type.RIGHT_PAREN, "Expected ')' after for clause.");

        // grab the body as a statement
        BStatement body = this.statement();
        if (increment != null) {
            body = new BStatement.Block(Arrays.asList(body, new BStatement.Expression(increment))); // add the increment as a statement (at the end of the body)
                                                                                                    // if it exists
        }

        // if there is no condition, loop forever basically
        if (condition == null) condition = new BExpression.Literal(true);
        body = new BStatement.While(condition, body);

        // add the initializer in a new block if it exists
        if (initializer != null) body = new BStatement.Block(Arrays.asList(initializer, body));

        return body;
    }


    // printStatement      → "print" expressionStatement
    private BStatement printStatement() {
        BExpression value = this.expression();
        this.consume(BToken.Type.SEMICOLON, "Expect ';' after value.");
        return new BStatement.Print(value);
    }

    // expressionStatement → expression ";"
    private BStatement expressionStatement() {
        BExpression value = this.expression();
        this.consume(BToken.Type.SEMICOLON, "Expect ';' after expression.");
        return new BStatement.Expression(value);
    }

    ///////////////////////////////////////////////////
    // EXPRESSIONS
    ///////////////////////////////////////////////////

    // expression     → assignment
    private BExpression expression() {
        return this.assignment();
    }

    // assignment     → IDENTIFIER "=" assignment
    //                | equality ;
    private BExpression assignment() {
        BExpression expression = this.or();

        if (this.match(BToken.Type.EQUAL)) {
            BToken equals = this.previous();
            BExpression value = this.assignment();

            if (expression instanceof BExpression.Let) {
                BToken name = ((BExpression.Let)expression).getName();
                return new BExpression.Assign(name, value);
            }

            this.script.error(equals, "Invalid target for assignment.");
        }

        return expression;
    }

    private BExpression or() {
        BExpression expression = this.and();

        while (this.match(BToken.Type.OR)) {
            BToken operator = this.previous();
            BExpression right = this.and();
            expression = new BExpression.Logical(expression, operator, right);
        }

        return expression;
    }

    private BExpression and() {
        BExpression expression = this.equality();

        while (this.match(BToken.Type.AND)) {
            BToken operator = this.previous();
            BExpression right = this.equality();
            expression = new BExpression.Logical(expression, operator, right);
        }

        return expression;
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
        if (this.match(BToken.Type.NUMBER, BToken.Type.STRING)) return new BExpression.Literal(this.previous().getLiteral());
        if (this.match(BToken.Type.IDENTIFIER)) return new BExpression.Let(this.previous());

        if (this.match(BToken.Type.LEFT_PAREN)) {
            BExpression expression = this.expression();
            this.consume(BToken.Type.RIGHT_PAREN, "Expected ')' after expression");
            return new BExpression.Grouping(expression);
        }

        this.script.error(peek(), "Expected expression.");
        throw new BParseException();
    }

    ///////////////////////////////////////////////////
    // UTILITIES
    ///////////////////////////////////////////////////

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
