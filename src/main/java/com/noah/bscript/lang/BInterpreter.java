package com.noah.bscript.lang;

import com.noah.bscript.exceptions.BRuntimeException;

public class BInterpreter implements BExpression.Visitor<Object> {

    private BScript script;

    public BInterpreter(BScript script) {
        this.script = script;
    }

    public void interpret(BExpression expression) {
        try {
            Object value = this.evaluate(expression);
            System.out.println(value);
        } catch (BRuntimeException exception) {
            this.script.error(exception.getToken(), exception.getMessage());
        }
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
    public Object visitUnary(BExpression.Unary expression) {
        Object right = this.evaluate(expression.getExpression());

        switch (expression.getOperator().getType()) {
            case MINUS: return -(double)right;
            case NOT: return !this.isTruthy(right);
        }

        return null;
    }

    private Object evaluate(BExpression expression) {
        return expression.accept(this);
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
