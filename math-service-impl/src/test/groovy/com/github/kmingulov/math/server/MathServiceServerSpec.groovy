package com.github.kmingulov.math.server

import io.grpc.Server
import spock.lang.Specification

class MathServiceServerSpec extends Specification {

    private Server grpcServer = Mock()
    private MathServiceServer server = new MathServiceServer(grpcServer)

    def 'starts the server'() {
        when:
            server.start()

        then:
            1 * grpcServer.start()
    }

    def 'stops the server'() {
        when:
            server.start()
            server.stop()

        then:
            1 * grpcServer.start()
            1 * grpcServer.shutdown()
    }

    def 'awaits the server termination'() {
        when:
            server.start()
            server.blockUntilShutdown()

        then:
            1 * grpcServer.start()
            1 * grpcServer.awaitTermination()
    }

}
