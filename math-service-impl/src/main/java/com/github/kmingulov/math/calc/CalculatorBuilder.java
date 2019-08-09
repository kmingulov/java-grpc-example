package com.github.kmingulov.math.calc;

import com.github.kmingulov.math.op.BinaryOperation;

import java.util.ArrayList;
import java.util.List;

public final class CalculatorBuilder {

    private final List<BinaryOperation> operations = new ArrayList<>();

    CalculatorBuilder() {}

    public CalculatorBuilder arithmeticOperations() {
        operations.add(BinaryOperation.plus());
        operations.add(BinaryOperation.minus());
        operations.add(BinaryOperation.multiply());
        operations.add(BinaryOperation.divide());
        return this;
    }

    public CalculatorBuilder operation(BinaryOperation operation) {
        operations.add(operation);
        return this;
    }

    public Calculator build() {
        return new DefaultCalculator(operations);
    }

}
