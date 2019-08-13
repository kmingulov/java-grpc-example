package com.github.kmingulov.math.client;

import com.github.kmingulov.math.model.*;
import com.github.kmingulov.math.model.ComputationServiceGrpc.ComputationServiceBlockingStub;
import com.github.kmingulov.math.model.ComputationServiceGrpc.ComputationServiceStub;
import io.grpc.stub.StreamObserver;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Scanner;

final class MathServiceClient {

    private final ComputationServiceBlockingStub blockingStub;
    private final ComputationServiceStub asyncStub;
    private final InputStream inputStream;
    private final PrintStream outputStream;

    MathServiceClient(ComputationServiceBlockingStub blockingStub,
                      ComputationServiceStub asyncStub,
                      InputStream inputStream,
                      OutputStream outputStream) {
        this.blockingStub = blockingStub;
        this.asyncStub = asyncStub;
        this.inputStream = inputStream;
        this.outputStream = new PrintStream(outputStream, true);
    }

    void start() throws InterruptedException {
        try (Scanner scanner = new Scanner(inputStream)) {
            while (true) {
                outputStream.print("> ");
                if (!scanner.hasNextLine()) {
                    break;
                }

                String line = scanner.nextLine();
                if ("quit".equals(line)) {
                    break;
                }

                if ("events".equals(line)) {
                    listenToEvents(scanner);
                    continue;
                }

                computeExpression(line);
            }
        }
    }

    private void listenToEvents(Scanner scanner) {
        outputStream.println("Listening to the server events. Type quit to terminate.");

        StreamObserver<StreamingStop> requestObserver = asyncStub.streamComputationEvents(new StreamObserver<>() {
            @Override
            public void onNext(ComputationEvent event) {
                outputStream.println(formatEvent(event));
            }

            @Override
            public void onError(Throwable t) {
                outputStream.println("ERROR: " + t.getMessage());
            }

            @Override
            public void onCompleted() {}
        });

        while (scanner.hasNextLine()) {
            if ("quit".equals(scanner.nextLine())) {
                break;
            }
        }

        requestObserver.onNext(StreamingStop.getDefaultInstance());
    }

    private String formatEvent(ComputationEvent event) {
        StringBuilder sb = new StringBuilder();
        sb
                .append(event.getId().getId())
                .append(' ')
                .append(event.getState())
                .append(' ');

        if (event.getState() == ComputationState.ERROR) {
            sb.append(event.getError());
        }

        if (event.getState() == ComputationState.COMPUTED) {
            sb.append(event.getResult());
        }

        return sb.toString();
    }

    private void computeExpression(String expression) throws InterruptedException {
        ComputationRequest request = ComputationRequest.newBuilder()
                .setExpression(expression)
                .build();

        ComputationId id = blockingStub.computeExpression(request);

        while (true) {
            ComputationResult result = blockingStub.getComputationResult(id);
            switch (result.getState()) {
                case PENDING:
                    outputStream.println("PENDING...");
                    break;

                case RUNNING:
                    outputStream.println("RUNNING...");
                    break;

                case ERROR:
                    outputStream.println("ERROR: " + result.getError());
                    return;

                case COMPUTED:
                    outputStream.println(result.getResult());
                    return;
            }

            Thread.sleep(1000);
        }
    }

}
