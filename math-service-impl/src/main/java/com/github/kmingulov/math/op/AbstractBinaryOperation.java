package com.github.kmingulov.math.op;

import java.util.function.BiFunction;

class AbstractBinaryOperation implements BinaryOperation {

    private final char symbol;
    private final int precedence;
    private final BiFunction<Double, Double, Double> function;

    AbstractBinaryOperation(char symbol,
                            int precedence,
                            BiFunction<Double, Double, Double> function) {
        this.symbol = symbol;
        this.precedence = precedence;
        this.function = function;
    }

    @Override
    public char symbol() {
        return symbol;
    }

    @Override
    public int precedence() {
        return precedence;
    }

    @Override
    public double compute(double a, double b) {
        return function.apply(a, b);
    }

}
