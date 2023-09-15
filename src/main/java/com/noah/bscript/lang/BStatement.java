package com.noah.bscript.lang;

import lombok.Getter;

import java.util.List;

public abstract class BStatement {

    public interface Visitor<T> {

        T visitIf(If statement);
        T visitWhile(While statement);

        T visitBlock(Block statement);
        T visitExpression(Expression statement);
        T visitLetStatement(Let statement);
        T visitPrint(Print statement);

    }

    @Getter
    public static class If extends BStatement {

        private final BExpression expression;
        private final BStatement thenBranch;
        private final BStatement elseBranch;

        public If(BExpression expression, BStatement thenBranch, BStatement elseBranch) {
            this.expression = expression;
            this.thenBranch = thenBranch;
            this.elseBranch = elseBranch;
        }

        @Override
        public <T> T accept(Visitor<T> visitor) {
            return visitor.visitIf(this);
        }

    }

    @Getter
    public static class While extends BStatement {

        private final BExpression condition;
        private final BStatement body;

        public While(BExpression condition, BStatement body) {
            this.condition = condition;
            this.body = body;
        }

        @Override
        public <T> T accept(Visitor<T> visitor) {
            return visitor.visitWhile(this);
        }
    }

    @Getter
    public static class Block extends BStatement {

        private final List<BStatement> statements;

        public Block(List<BStatement> statements) {
            this.statements = statements;
        }

        @Override
        public <T> T accept(Visitor<T> visitor) {
            return visitor.visitBlock(this);
        }
    }

    @Getter
    public static class Expression extends BStatement {

        final BExpression expression;

        public Expression(BExpression expression) {
            this.expression = expression;
        }

        @Override
        public <T> T accept(Visitor<T> visitor) { return visitor.visitExpression(this); }
    }

    @Getter
    public static class Let extends BStatement {

        final BToken name;
        final BExpression initializer;

        public Let(BToken name, BExpression initializer) {
            this.name = name;
            this.initializer = initializer;
        }

        @Override
        public <T> T accept(Visitor<T> visitor) { return visitor.visitLetStatement(this); }
    }

    @Getter
    public static class Print extends BStatement {

        final BExpression expression;

        public Print(BExpression expression) {
            this.expression = expression;
        }

        @Override
        public <T> T accept(Visitor<T> visitor) { return visitor.visitPrint(this); }
    }

    public abstract <T> T accept(Visitor<T> visitor);

}
