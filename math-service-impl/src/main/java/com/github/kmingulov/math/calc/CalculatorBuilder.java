package com.github.kmingulov.math.calc;

import com.github.kmingulov.math.op.binary.BinaryOperation;
import com.github.kmingulov.math.op.fun.Function;

import java.util.ArrayList;
import java.util.List;

public final class CalculatorBuilder {

    private final List<BinaryOperation> operations = new ArrayList<>();
    private final List<Function> functions = new ArrayList<>();

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

    public CalculatorBuilder trigonometricFunctions() {
        functions.add(Function.sin());
        functions.add(Function.cos());
        functions.add(Function.tan());
        return this;
    }

    public CalculatorBuilder function(Function function) {
        functions.add(function);
        return this;
    }

    public Calculator build() {
        return new DefaultCalculator(operations, functions);
    }

}
