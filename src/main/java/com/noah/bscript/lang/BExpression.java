package com.noah.bscript.lang;

import lombok.Getter;

public abstract class BExpression {

    public interface Visitor<T> {

        T visitBinary(Binary expression);
        T visitGrouping(Grouping expression);
        T visitLiteral(Literal expression);
        T visitLogical(Logical expression);
        T visitUnary(Unary expression);
        T visitLetExpression(Let expression);
        T visitAssign(Assign expression);

    }

    @Getter
    public static class Assign extends BExpression {

        final BToken name;
        final BExpression value;

        public Assign(BToken name, BExpression value) {
            this.name = name;
            this.value = value;
        }

        @Override
        public <T> T accept(Visitor<T> visitor) {
            return visitor.visitAssign(this);
        }
    }

    @Getter
    public static class Binary extends BExpression {

        final BExpression left;
        final BToken operator;
        final BExpression right;

        public Binary(BExpression left, BToken operator, BExpression right) {
            this.left = left;
            this.operator = operator;
            this.right = right;
        }

        @Override
        public <T> T accept(Visitor<T> visitor) {
            return visitor.visitBinary(this);
        }
    }

    @Getter
    public static class Grouping extends BExpression {

        final BExpression expression;

        public Grouping(BExpression expression) {
            this.expression = expression;
        }

        @Override
        public <T> T accept(Visitor<T> visitor) {
            return visitor.visitGrouping(this);
        }
    }

    @Getter
    public static class Literal extends BExpression {

        final Object value;

        public Literal(Object value) {
            this.value = value;
        }

        @Override
        public <T> T accept(Visitor<T> visitor) {
            return visitor.visitLiteral(this);
        }
    }

    @Getter
    public static class Logical extends BExpression {

        private final BExpression left;
        private final BToken operator;
        private final BExpression right;

        public Logical(BExpression left, BToken operator, BExpression right) {
            this.left = left;
            this.operator = operator;
            this.right = right;
        }

        @Override
        public <T> T accept(Visitor<T> visitor) {
            return visitor.visitLogical(this);
        }
    }

    @Getter
    public static class Unary extends BExpression {

        final BToken operator;
        final BExpression expression;

        public Unary(BToken operator, BExpression expression) {
            this.operator = operator;
            this.expression = expression;
        }

        @Override
        public <T> T accept(Visitor<T> visitor) {
            return visitor.visitUnary(this);
        }
    }

    @Getter
    public static class Let extends BExpression {

        final BToken name;

        public Let(BToken name) {
            this.name = name;
        }

        @Override
        public <T> T accept(Visitor<T> visitor) {
            return visitor.visitLetExpression(this);
        }
    }

    public abstract <T> T accept(Visitor<T> visitor);

}
