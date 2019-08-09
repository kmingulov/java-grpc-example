package com.github.kmingulov.math.op.binary;

import com.github.kmingulov.math.op.Operation;

public interface BinaryOperation extends Operation {

    @Override
    default int operandsCount() {
        return 2;
    }

    @Override
    default double apply(double[] args) {
        return compute(args[0], args[1]);
    }

    double compute(double a, double b);

    char symbol();

    int precedence();

    static BinaryOperation plus() {
        return new AbstractBinaryOperation('+', 2, (a, b) -> a + b);
    }

    static BinaryOperation minus() {
        return new AbstractBinaryOperation('-', 2, (a, b) -> a - b);
    }

    static BinaryOperation multiply() {
        return new AbstractBinaryOperation('*', 3, (a, b) -> a * b);
    }

    static BinaryOperation divide() {
        return new AbstractBinaryOperation('/', 3, (a, b) -> a / b);
    }

}
