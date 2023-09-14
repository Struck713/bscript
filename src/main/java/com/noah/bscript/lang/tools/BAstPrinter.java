package com.noah.bscript.lang.tools;

import com.noah.bscript.lang.BExpression;

public class BAstPrinter implements BExpression.Visitor<Object> {

    public String print(BExpression expression) {
        return expression.accept(this).toString();
    }

    @Override
    public Object visitBinary(BExpression.Binary expression) {
        return this.parenthesize(expression.getOperator().getLexeme(), expression.getLeft(), expression.getRight());
    }

    @Override
    public Object visitGrouping(BExpression.Grouping expression) {
        return this.parenthesize("group", expression.getExpression());
    }

    @Override
    public Object visitLiteral(BExpression.Literal expression) {
        if (expression.getValue() == null) return "null";
        return expression.getValue().toString();
    }

    @Override
    public Object visitUnary(BExpression.Unary expression) {
        return this.parenthesize(expression.getOperator().getLexeme(), expression.getExpression());
    }

    private Object parenthesize(String name, BExpression... expressions) {
        StringBuilder builder = new StringBuilder();

        builder.append("(").append(name);
        for (BExpression expression : expressions) {
            builder.append(" ");
            builder.append(expression.accept(this));
        }
        builder.append(")");

        return builder.toString();
    }

}
