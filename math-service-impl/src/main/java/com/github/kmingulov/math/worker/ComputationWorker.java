package com.github.kmingulov.math.worker;

import com.github.kmingulov.math.ComputationId;

import java.util.function.Consumer;

public interface ComputationWorker {

    ComputationId submit(String expression, Consumer<ComputationEvent> progressListener);

}
