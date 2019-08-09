package com.github.kmingulov.math.op.fun;

import com.github.kmingulov.math.op.Operation;

public interface Function extends Operation {

    String name();

    static Function sin() {
        return new AbstractFunction("sin", 1, args -> Math.sin(args[0]));
    }

    static Function cos() {
        return new AbstractFunction("cos", 1, args -> Math.cos(args[0]));
    }

    static Function tan() {
        return new AbstractFunction("tan", 1, args -> Math.tan(args[0]));
    }

}
