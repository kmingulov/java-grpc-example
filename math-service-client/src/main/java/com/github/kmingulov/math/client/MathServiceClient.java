package com.github.kmingulov.math.client;

import com.github.kmingulov.math.model.ComputationId;
import com.github.kmingulov.math.model.ComputationRequest;
import com.github.kmingulov.math.model.ComputationResult;
import com.github.kmingulov.math.model.ComputationServiceGrpc.ComputationServiceBlockingStub;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Scanner;

final class MathServiceClient {

    private final ComputationServiceBlockingStub computationService;
    private final InputStream inputStream;
    private final PrintStream outputStream;

    MathServiceClient(ComputationServiceBlockingStub computationService,
                      InputStream inputStream,
                      OutputStream outputStream) {
        this.computationService = computationService;
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

                computeExpression(line);
            }
        }
    }

    private void computeExpression(String expression) throws InterruptedException {
        ComputationRequest request = ComputationRequest.newBuilder()
                .setExpression(expression)
                .build();

        ComputationId id = computationService.computeExpression(request);

        while (true) {
            ComputationResult result = computationService.getComputationResult(id);
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
