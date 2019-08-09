package com.github.kmingulov.math.server;

import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;

public class MathServiceServer {

    private static final int PORT = 8888;

    private Server server;

    private void start() throws IOException {
        System.out.println("Starting the server on " + PORT + "...");

        server = ServerBuilder.forPort(PORT)
                .addService(new ComputationService())
                .build()
                .start();

        Runtime.getRuntime().addShutdownHook(new Thread(this::stop));

        System.out.println("Server started!");
    }

    private void stop() {
        if (server != null) {
            System.out.println("Stopping the server...");
            server.shutdown();
            System.out.println("Server stopped!");
        }
    }

    private void blockUntilShutdown() throws InterruptedException {
        if (server != null) {
            server.awaitTermination();
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        MathServiceServer server = new MathServiceServer();
        server.start();
        server.blockUntilShutdown();
    }

}
