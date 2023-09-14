package com.noah.bscript.lang;

import lombok.Getter;

public abstract class BExpression {

    @Getter
    public static class Binary extends BExpression {

        final BExpression left;
        final BToken operator;
        final BExpression right;

        Binary(BExpression left, BToken operator, BExpression right) {
            this.left = left;
            this.operator = operator;
            this.right = right;
        }

        @Override
        public Object accept(Visitor<Object> visitor) {
            return visitor.visitBinary(this);
        }
    }

    @Getter
    public static class Grouping extends BExpression {

        final BExpression expression;

        Grouping(BExpression expression) {
            this.expression = expression;
        }

        @Override
        public Object accept(Visitor<Object> visitor) {
            return visitor.visitGrouping(this);
        }
    }

    @Getter
    public static class Literal extends BExpression {

        final Object value;

        Literal(Object value) {
            this.value = value;
        }

        @Override
        public Object accept(Visitor<Object> visitor) {
            return visitor.visitLiteral(this);
        }
    }

    @Getter
    public static class Unary extends BExpression {

        final BToken operator;
        final BExpression expression;

        Unary(BToken operator, BExpression expression) {
            this.operator = operator;
            this.expression = expression;
        }

        @Override
        public Object accept(Visitor<Object> visitor) {
            return visitor.visitUnary(this);
        }
    }

    public interface Visitor<T> {

        T visitBinary(Binary expression);
        T visitGrouping(Grouping expression);
        T visitLiteral(Literal expression);
        T visitUnary(Unary expression);

    }

    public abstract Object accept(Visitor<Object> visitor);

}
