package com.github.kmingulov.math.op.fun;

class AbstractFunction implements Function {

    private final String name;

    private final int operandsCount;

    private final java.util.function.Function<double[], Double> function;

    public AbstractFunction(String name,
                            int operandsCount,
                            java.util.function.Function<double[], Double> function) {
        this.name = name;
        this.operandsCount = operandsCount;
        this.function = function;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public int operandsCount() {
        return operandsCount;
    }

    @Override
    public double apply(double[] args) {
        if (args.length != operandsCount) {
            throw new IllegalArgumentException("Expected exactly " + operandsCount + " arguments, "
                    + args.length + " received.");
        }

        return function.apply(args);
    }

}
