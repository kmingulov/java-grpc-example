package com.github.kmingulov.math.op;

public interface BinaryOperation {

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
