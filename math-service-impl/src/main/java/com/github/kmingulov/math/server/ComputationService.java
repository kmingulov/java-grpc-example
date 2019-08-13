package com.github.kmingulov.math.server;

import com.github.kmingulov.math.calc.Calculator;
import com.github.kmingulov.math.model.*;
import com.github.kmingulov.math.op.fun.PrimeFunction;
import com.github.kmingulov.math.worker.AsyncComputationWorker;
import com.github.kmingulov.math.worker.ComputationWorker;
import com.google.common.annotations.VisibleForTesting;
import io.grpc.stub.StreamObserver;

import java.util.concurrent.*;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

final class ComputationService extends ComputationServiceGrpc.ComputationServiceImplBase {

    private static final Logger LOGGER = Logger.getLogger(ComputationService.class.getName());

    private final ComputationWorker worker;

    private final ConcurrentMap<ComputationId, ComputationEvent> latestEventById = new ConcurrentHashMap<>();

    private final CopyOnWriteArraySet<Consumer<ComputationEvent>> eventListeners = new CopyOnWriteArraySet<>();

    ComputationService() {
        Calculator calculator = Calculator.builder()
                .arithmeticOperations()
                .trigonometricFunctions()
                .function(new PrimeFunction())
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
                builder.setError(event.getError());
            }

            if (event.getState() == ComputationState.COMPUTED) {
                builder.setResult(event.getResult());
            }

            responseObserver.onNext(builder.build());
        }

        responseObserver.onCompleted();
    }

    @Override
    public StreamObserver<StreamingStop> streamComputationEvents(StreamObserver<ComputationEvent> responseObserver) {
        Consumer<ComputationEvent> eventListener = event -> {
            synchronized (responseObserver) {
                responseObserver.onNext(event);
            }
        };

        LOGGER.log(Level.INFO, "Adding the listener " + eventListener);
        eventListeners.add(eventListener);

        return new StreamObserver<>() {
            @Override
            public void onNext(StreamingStop value) {
                stopStreaming();
            }

            @Override
            public void onError(Throwable t) {
                LOGGER.log(Level.SEVERE, "Encountered error in streamComputationEvents.", t);
                stopStreaming();
            }

            @Override
            public void onCompleted() {
                stopStreaming();
            }

            private void stopStreaming() {
                LOGGER.log(Level.INFO, "Removing the listener " + eventListener);
                eventListeners.remove(eventListener);
                responseObserver.onCompleted();
            }
        };
    }

    private void handleEvent(ComputationEvent event) {
        latestEventById.put(event.getId(), event);

        for (Consumer<ComputationEvent> eventListener : eventListeners) {
            eventListener.accept(event);
        }
    }

}
