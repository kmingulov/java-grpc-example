package com.github.kmingulov.math;

import com.github.kmingulov.math.server.ComputationService;
import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;
import java.util.logging.Logger;

public class MathServiceServer {

    private static final int PORT = 8888;

    private Server server;
    private Logger logger = Logger.getLogger(MathServiceServer.class.getName());

    private void start() throws IOException {
        logger.info("Starting the server on " + PORT + "...");

        server = ServerBuilder.forPort(PORT)
                .addService(new ComputationService())
                .build()
                .start();

        Runtime.getRuntime().addShutdownHook(new Thread(this::stop));

        logger.info("Server started!");
    }

    private void stop() {
        if (server != null) {
            logger.info("Stopping the server...");
            server.shutdown();
            logger.info("Server stopped!");
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
