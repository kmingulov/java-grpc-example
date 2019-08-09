package com.github.kmingulov.math.server

import io.grpc.Server
import spock.lang.Specification

class MathServiceServerSpec extends Specification {

    def 'starts the server'() {
        given:
            Server grpcServer = Mock()
            MathServiceServer server = new MathServiceServer(grpcServer)

        when:
            server.start()

        then:
            1 * grpcServer.start()
    }

    def 'stops the server'() {
        given:
            Server grpcServer = Mock()
            MathServiceServer server = new MathServiceServer(grpcServer)

        when:
            server.start()
            server.stop()

        then:
            1 * grpcServer.start()
            1 * grpcServer.shutdown()
    }

    def 'awaits the server termination'() {
        given:
            Server grpcServer = Mock()
            MathServiceServer server = new MathServiceServer(grpcServer)

        when:
            server.start()
            server.blockUntilShutdown()

        then:
            1 * grpcServer.start()
            1 * grpcServer.awaitTermination()
    }

}
