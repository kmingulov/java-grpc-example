package com.github.kmingulov.math.client;

import com.github.kmingulov.math.model.ComputationServiceGrpc;
import com.github.kmingulov.math.model.ComputationServiceGrpc.ComputationServiceBlockingStub;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import java.util.concurrent.TimeUnit;

public final class Main {

    private static final String HOST = "localhost";
    private static final int PORT = 8888;

    public static void main(String[] args) throws InterruptedException {
        ManagedChannel channel = ManagedChannelBuilder.forAddress(HOST, PORT)
                .usePlaintext()
                .build();

        ComputationServiceBlockingStub computationService = ComputationServiceGrpc.newBlockingStub(channel);

        MathServiceClient client = new MathServiceClient(computationService, System.in, System.out);
        try {
            client.start();
        } finally {
            channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
        }
    }

}
