package com.noah.bscript.runtime;

import com.noah.bscript.BScript;
import com.noah.bscript.exceptions.BRuntimeException;
import com.noah.bscript.lang.BExpression;
import com.noah.bscript.lang.BStatement;
import com.noah.bscript.lang.BToken;
import lombok.Getter;

import java.util.List;

public class BInterpreter implements BExpression.Visitor<Object>, BStatement.Visitor<Void> {

    private List<BStatement> statements;
    private BScript script;

    @Getter private BEnvironment environment;

    public BInterpreter(BScript script, List<BStatement> statements) {
        this.script = script;
        this.statements = statements;
        this.environment = new BEnvironment();
    }

    public void interpret() {
        try {
            for (BStatement statement : this.statements) this.execute(statement);
        } catch (BRuntimeException exception) {
            this.script.error(exception.getToken(), exception.getMessage());
        }
    }

    ///////////////////////////////////////////////////
    // STATEMENTS
    ///////////////////////////////////////////////////

    @Override
    public Void visitIf(BStatement.If statement) {
        if (this.isTruthy(this.evaluate(statement.getExpression()))) this.execute(statement.getThenBranch());
        else if (statement.getElseBranch() != null) this.execute(statement.getElseBranch());
        return null;
    }

    @Override
    public Void visitWhile(BStatement.While statement) {
        while (isTruthy(this.evaluate(statement.getCondition())))
            this.execute(statement.getBody());
        return null;
    }

    @Override
    public Void visitBlock(BStatement.Block statement) {
        this.executeBlock(statement.getStatements(), new BEnvironment(this.environment));
        return null;
    }

    @Override
    public Void visitExpression(BStatement.Expression statement) {
        this.evaluate(statement.getExpression());
        return null;
    }

    @Override
    public Void visitPrint(BStatement.Print statement) {
        Object value = this.evaluate(statement.getExpression());
        System.out.println(value);
        return null;
    }

    @Override
    public Void visitLetStatement(BStatement.Let statement) {
        Object value = null;
        BExpression initializer = statement.getInitializer();
        if (initializer != null) {
            value = this.evaluate(initializer);
        }

        this.environment.define(statement.getName().getLexeme(), value);
        return null;
    }

    ///////////////////////////////////////////////////
    // EXPRESSIONS
    ///////////////////////////////////////////////////

    @Override
    public Object visitAssign(BExpression.Assign assign) {
        Object value = this.evaluate(assign.getValue());
        this.environment.redefine(assign.getName(), value);
        return value;
    }

    @Override
    public Object visitLetExpression(BExpression.Let expression) {
        return this.environment.get(expression.getName());
    }

    @Override
    public Object visitBinary(BExpression.Binary expression) {
        Object left = this.evaluate(expression.getLeft());
        Object right = this.evaluate(expression.getRight());

        switch (expression.getOperator().getType()) {
            case GREATER:
                this.checkNumberOperands(expression.getOperator(), left, right);
                return (double)left > (double)right;
            case GREATER_EQUAL:
                this.checkNumberOperands(expression.getOperator(), left, right);
                return (double)left >= (double)right;
            case LESS:
                this.checkNumberOperands(expression.getOperator(), left, right);
                return (double)left < (double)right;
            case LESS_EQUAL:
                this.checkNumberOperands(expression.getOperator(), left, right);
                return (double)left <= (double)right;
            case MINUS:
                this.checkNumberOperands(expression.getOperator(), left, right);
                return (double)left - (double)right;
            case SLASH:
                this.checkNumberOperands(expression.getOperator(), left, right);
                return (double)left / (double)right;
            case STAR:
                this.checkNumberOperands(expression.getOperator(), left, right);
                return (double)left * (double)right;
            case NOT_EQUAL: return !isEqual(left, right);
            case EQUAL_EQUAL: return isEqual(left, right);
            case PLUS:
                if (left instanceof Double) {
                    if (right instanceof Double) return (double)left + (double)right;
                    if (right instanceof String) return (double)left + (String)right;
                }
                if (left instanceof String) {
                    if (right instanceof String) return (String)left + (String)right;
                    if (right instanceof Double) return (String)left + (double)right;
                }
                throw new BRuntimeException(expression.getOperator(), "Operands must be string or number.");
        }

        return null;
    }

    @Override
    public Object visitGrouping(BExpression.Grouping expression) {
        return this.evaluate(expression.getExpression());
    }

    @Override
    public Object visitLiteral(BExpression.Literal expression) {
        return expression.getValue();
    }

    @Override
    public Object visitLogical(BExpression.Logical expression) {
        Object left = this.evaluate(expression.getLeft());

        if (expression.getOperator().getType() == BToken.Type.OR) {
            if (isTruthy(left)) return left;
        } else {
            if (!isTruthy(left)) return left;
        }

        return this.evaluate(expression.getRight());
    }

    @Override
    public Object visitUnary(BExpression.Unary expression) {
        Object right = this.evaluate(expression.getExpression());

        switch (expression.getOperator().getType()) {
            case MINUS: return -(double)right;
            case NOT: return !this.isTruthy(right);
        }

        return null;
    }

    ///////////////////////////////////////////////////
    // UTILITY
    ///////////////////////////////////////////////////

    private Object evaluate(BExpression expression) {
        return expression.accept(this);
    }

    private Object execute(BStatement statement) {
        return statement.accept(this);
    }

    private void executeBlock(List<BStatement> statements, BEnvironment environment) {
        BEnvironment previous = this.environment;
        try {
            this.environment = environment;
            for (BStatement statement : statements) this.execute(statement);
        } finally {
            this.environment = previous;
        }
    }

    public void checkNumberOperands(BToken token, Object left, Object right) {
        if (left instanceof Double && right instanceof Double) return;
        throw new BRuntimeException(token, "Operands must be numbers.");
    }

    private boolean isTruthy(Object object) {
        if (object == null) return false;
        if (object instanceof Boolean) return (boolean)object;
        return true;
    }

    private boolean isEqual(Object a, Object b) {
        if (a == null && b == null) return true;
        if (a == null) return false;

        return a.equals(b);
    }

}
