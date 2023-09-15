package com.noah.bscript.tools;

import com.noah.bscript.lang.BExpression;

public class BAstPrinter implements BExpression.Visitor<String> {

    public String print(BExpression expression) {
        return expression.accept(this);
    }

    @Override
    public String visitBinary(BExpression.Binary expression) {
        return this.parenthesize(expression.getOperator().getLexeme(), expression.getLeft(), expression.getRight());
    }

    @Override
    public String visitGrouping(BExpression.Grouping expression) {
        return this.parenthesize("group", expression.getExpression());
    }

    @Override
    public String visitLiteral(BExpression.Literal expression) {
        if (expression.getValue() == null) return "null";
        return expression.getValue().toString();
    }

    @Override
    public String visitLogical(BExpression.Logical expression) {
        return this.parenthesize("logical", expression.getLeft(), expression.getRight());
    }

    @Override
    public String visitUnary(BExpression.Unary expression) {
        return this.parenthesize(expression.getOperator().getLexeme(), expression.getExpression());
    }

    @Override
    public String visitLetExpression(BExpression.Let expression) {
        return this.parenthesize("let", expression);
    }

    @Override
    public String visitAssign(BExpression.Assign expression) {
        return this.parenthesize("assign", expression);
    }

    private String parenthesize(String name, BExpression... expressions) {
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
