package com.github.kmingulov.math.model;

import static com.github.kmingulov.math.model.ComputationState.*;

public class ComputationEvents {

    private ComputationEvents() {}

    public static ComputationEvent pending(ComputationId id) {
        return ComputationEvent.newBuilder()
                .setId(id)
                .setState(PENDING)
                .build();
    }

    public static ComputationEvent running(ComputationId id) {
        return ComputationEvent.newBuilder()
                .setId(id)
                .setState(RUNNING)
                .build();
    }

    public static ComputationEvent error(ComputationId id, Exception e) {
        return ComputationEvent.newBuilder()
                .setId(id)
                .setState(ERROR)
                .setError(e.getMessage() != null ? e.getMessage() : e.getClass().getName())
                .build();
    }

    public static ComputationEvent computed(ComputationId id, double result) {
        return ComputationEvent.newBuilder()
                .setId(id)
                .setState(COMPUTED)
                .setResult(result)
                .build();
    }

}
