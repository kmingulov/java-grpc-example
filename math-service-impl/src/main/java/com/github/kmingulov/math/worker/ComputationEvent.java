package com.github.kmingulov.math.worker;

import com.github.kmingulov.math.ComputationId;
import com.github.kmingulov.math.ComputationState;

import java.util.Objects;

public class ComputationEvent {

    private final ComputationId id;
    private final ComputationState state;
    private final Double result;
    private final Exception error;

    private ComputationEvent(ComputationId id, ComputationState state, Double result, Exception error) {
        this.id = id;
        this.state = state;
        this.result = result;
        this.error = error;
    }

    public ComputationId getId() {
        return id;
    }

    public ComputationState getState() {
        return state;
    }

    public Double getResult() {
        return result;
    }

    public Exception getError() {
        return error;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ComputationEvent event = (ComputationEvent) o;
        return Objects.equals(id, event.id)
                && state == event.state
                && Objects.equals(result, event.result)
                && Objects.equals(error, event.error);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, state, result, error);
    }

    @Override
    public String toString() {
        return state + "(id=" + id + ", result=" + result + ", error=" + error + ")";
    }

    static ComputationEvent pending(ComputationId id) {
        return new ComputationEvent(id, ComputationState.PENDING, null, null);
    }

    static ComputationEvent running(ComputationId id) {
        return new ComputationEvent(id, ComputationState.RUNNING, null, null);
    }

    static ComputationEvent error(ComputationId id, Exception e) {
        return new ComputationEvent(id, ComputationState.ERROR, null, e);
    }

    static ComputationEvent computed(ComputationId id, double result) {
        return new ComputationEvent(id, ComputationState.COMPUTED, result, null);
    }

}
