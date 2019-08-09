package com.github.kmingulov.math.server;

import com.github.kmingulov.math.calc.Calculator;
import com.github.kmingulov.math.model.*;
import com.github.kmingulov.math.worker.AsyncComputationWorker;
import com.github.kmingulov.math.worker.ComputationEvent;
import com.github.kmingulov.math.worker.ComputationWorker;
import com.google.common.annotations.VisibleForTesting;
import io.grpc.stub.StreamObserver;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

final class ComputationService extends ComputationServiceGrpc.ComputationServiceImplBase {

    private final ComputationWorker worker;

    private final ConcurrentMap<ComputationId, ComputationEvent> latestEventById = new ConcurrentHashMap<>();

    ComputationService() {
        Calculator calculator = Calculator.builder()
                .arithmeticOperations()
                .trigonometricFunctions()
                .build();

        this.worker = new AsyncComputationWorker(calculator);
    }

    @VisibleForTesting
    ComputationService(ComputationWorker worker) {
        this.worker = worker;
    }

    @Override
    public void computeExpression(ComputationRequest request, StreamObserver<ComputationId> responseObserver) {
        ComputationId id = worker.submit(request.getExpression(), this::handleEvent);
        responseObserver.onNext(id);
        responseObserver.onCompleted();
    }

    @Override
    public void getComputationResult(ComputationId id, StreamObserver<ComputationResult> responseObserver) {
        ComputationEvent event = latestEventById.get(id);
        if (event == null) {
            responseObserver.onError(new NullPointerException("Not Found"));
        } else {
            ComputationResult.Builder builder = ComputationResult.newBuilder()
                    .setState(event.getState());

            if (event.getState() == ComputationState.ERROR) {
                builder.setError(event.getError().getMessage());
            }

            if (event.getState() == ComputationState.COMPUTED) {
                builder.setResult(event.getResult());
            }

            responseObserver.onNext(builder.build());
        }

        responseObserver.onCompleted();
    }

    private void handleEvent(ComputationEvent event) {
        latestEventById.put(event.getId(), event);
    }

}
