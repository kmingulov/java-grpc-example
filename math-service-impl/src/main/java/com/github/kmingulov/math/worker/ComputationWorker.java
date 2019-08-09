package com.github.kmingulov.math.worker;

import com.github.kmingulov.math.model.ComputationEvent;
import com.github.kmingulov.math.model.ComputationId;

import java.util.function.Consumer;

public interface ComputationWorker {

    ComputationId submit(String expression, Consumer<ComputationEvent> progressListener);

}
