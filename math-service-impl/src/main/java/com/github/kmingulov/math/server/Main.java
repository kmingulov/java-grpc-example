package com.github.kmingulov.math.server;

import java.io.IOException;

public final class Main {

    private static final int PORT = 8888;

    public static void main(String[] args) throws IOException, InterruptedException {
        MathServiceServer server = new MathServiceServer(PORT);
        Runtime.getRuntime().addShutdownHook(new Thread(server::stop));

        server.start();
        server.blockUntilShutdown();
    }

}
