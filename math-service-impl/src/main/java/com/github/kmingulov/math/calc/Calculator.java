package com.github.kmingulov.math.calc;

public interface Calculator {

    double compute(String expression);

    static CalculatorBuilder builder() {
        return new CalculatorBuilder();
    }

}
