package com.github.kmingulov.math.server;

import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;

class MathServiceServer {

    private final Server server;

    MathServiceServer(Server server) {
        this.server = server;
    }

    MathServiceServer(int port) {
        this.server = ServerBuilder.forPort(port)
                .addService(new ComputationService())
                .build();
    }

    void start() throws IOException {
        System.out.println("Starting the server on " + server.getPort() + "...");
        server.start();
        System.out.println("Server started!");
    }

    void stop() {
        System.out.println("Stopping the server...");
        server.shutdown();
        System.out.println("Server stopped!");
    }

    void blockUntilShutdown() throws InterruptedException {
        if (server != null) {
            server.awaitTermination();
        }
    }

}
