package com.github.kmingulov.math.worker;

import com.github.kmingulov.math.ComputationId;
import com.github.kmingulov.math.calc.Calculator;

import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import static com.github.kmingulov.math.worker.ComputationEvent.*;

public final class AsyncComputationWorker implements ComputationWorker, AutoCloseable {

    private final Calculator calculator;
    private final Thread workThread;

    private boolean running = true;

    private final BlockingQueue<WorkUnit> queue = new LinkedBlockingQueue<>();

    public AsyncComputationWorker(Calculator calculator) {
        this.calculator = calculator;
        this.workThread = new Thread(this::listenQueue);

        this.workThread.start();
    }

    @Override
    public void close() {
        if (!running) {
            throw new IllegalStateException("Cannot stop a not running worker.");
        }

        running = false;
    }

    @Override
    public ComputationId submit(String expression,
                                Consumer<ComputationEvent> progressListener) {
        if (!running) {
            throw new IllegalStateException("This worker isn't running.");
        }

        String stringId = UUID.randomUUID().toString();
        ComputationId id = ComputationId.newBuilder().setId(stringId).build();

        WorkUnit workUnit = new WorkUnit(id, expression, progressListener);
        queue.offer(workUnit);

        progressListener.accept(pending(id));

        return id;
    }

    private void listenQueue() {
        while (running) {
            try {
                WorkUnit unit = queue.poll(1, TimeUnit.SECONDS);
                if (unit != null) {
                    compute(unit);
                }
            } catch (InterruptedException e) {
                // ignored
            }
        }
    }

    private void compute(WorkUnit workUnit) {
        ComputationId id = workUnit.id;

        try {
            workUnit.progressListener.accept(running(id));
            double result = calculator.compute(workUnit.expression);
            workUnit.progressListener.accept(computed(id, result));
        } catch (Exception e) {
            workUnit.progressListener.accept(error(id, e));
        }
    }

    private static final class WorkUnit {
        private final ComputationId id;
        private final String expression;
        private final Consumer<ComputationEvent> progressListener;

        WorkUnit(ComputationId id,
                 String expression,
                 Consumer<ComputationEvent> progressListener) {
            this.id = id;
            this.expression = expression;
            this.progressListener = progressListener;
        }
    }

}
