package com.github.kmingulov.math.client

import com.github.kmingulov.math.model.ComputationEvent
import com.github.kmingulov.math.model.ComputationId
import com.github.kmingulov.math.model.ComputationRequest
import com.github.kmingulov.math.model.ComputationResult
import com.github.kmingulov.math.model.ComputationServiceGrpc
import com.github.kmingulov.math.model.ComputationServiceGrpc.ComputationServiceBlockingStub
import com.github.kmingulov.math.model.ComputationServiceGrpc.ComputationServiceImplBase
import com.github.kmingulov.math.model.ComputationServiceGrpc.ComputationServiceStub
import com.github.kmingulov.math.model.StreamingStop
import io.grpc.ManagedChannel
import io.grpc.Server
import io.grpc.inprocess.InProcessChannelBuilder
import io.grpc.inprocess.InProcessServerBuilder
import io.grpc.stub.StreamObserver
import spock.lang.Specification

import static com.github.kmingulov.math.model.ComputationEvents.*
import static com.github.kmingulov.math.model.ComputationState.*

class MathServiceClientSpec extends Specification {

    private static final ComputationId ID = ComputationId.newBuilder()
            .setId(UUID.randomUUID().toString())
            .build()

    private ComputationServiceImplStub serviceImplStub
    private Server server
    private ManagedChannel channel
    private ComputationServiceBlockingStub blockingStub
    private ComputationServiceStub asyncStub

    private ByteArrayOutputStream outputStream = new ByteArrayOutputStream()

    def setup() {
        String serverName = this.getClass().getName()

        serviceImplStub = new ComputationServiceImplStub()

        server = InProcessServerBuilder.forName(serverName)
                .directExecutor()
                .addService(serviceImplStub)
                .build()
                .start()

        channel = InProcessChannelBuilder.forName(serverName)
                .directExecutor()
                .build()

        blockingStub = ComputationServiceGrpc.newBlockingStub(channel)
        asyncStub = ComputationServiceGrpc.newStub(channel)
    }

    void cleanup() {
        channel.shutdown()
        server.shutdown()
    }

    def 'quits on quit command'() {
        given:
            ByteArrayInputStream inputStream = new ByteArrayInputStream('quit'.getBytes())
            MathServiceClient client = new MathServiceClient(blockingStub, asyncStub, inputStream, outputStream)

        when:
            client.start()

        then:
            outputStream.toString() == '> '
    }

    def 'quits on input end'() {
        given:
            ByteArrayInputStream inputStream = new ByteArrayInputStream()
            MathServiceClient client = new MathServiceClient(blockingStub, asyncStub, inputStream, outputStream)

        when:
            client.start()

        then:
            outputStream.toString() == '> '
    }

    def 'computes expression'() {
        given:
            ByteArrayInputStream inputStream = new ByteArrayInputStream('1+2'.getBytes())
            MathServiceClient client = new MathServiceClient(blockingStub, asyncStub, inputStream, outputStream)

        when:
            client.start()

        then:
            outputStream.toString() == '> PENDING...\nRUNNING...\n1.0\n> '
    }

    def 'prints error'() {
        given:
            serviceImplStub.setReturnError(true)

            ByteArrayInputStream inputStream = new ByteArrayInputStream('1+2'.getBytes())
            MathServiceClient client = new MathServiceClient(blockingStub, asyncStub, inputStream, outputStream)

        when:
            client.start()

        then:
            outputStream.toString() == '> PENDING...\nRUNNING...\nERROR: Some Error\n> '
    }

    def 'listens to events'() {
        given:
            ByteArrayInputStream inputStream = new ByteArrayInputStream('events\nquit'.getBytes())
            MathServiceClient client = new MathServiceClient(blockingStub, asyncStub, inputStream, outputStream)

        when:
            client.start()

        then:
            outputStream.toString() == '> Listening to the server events. Type quit to terminate.\n' +
                    ID.getId() + ' PENDING \n' +
                    ID.getId() + ' RUNNING \n' +
                    ID.getId() + ' COMPUTED 1.0\n' +
                    '> '
    }

    private static final class ComputationServiceImplStub extends ComputationServiceImplBase {

        private int computationResultCallCount = 0
        private boolean returnError = false

        @Override
        void computeExpression(ComputationRequest request, StreamObserver<ComputationId> responseObserver) {
            responseObserver.onNext(ID)
            responseObserver.onCompleted()
        }

        @Override
        void getComputationResult(ComputationId request, StreamObserver<ComputationResult> responseObserver) {
            ComputationResult.Builder resultBuilder = ComputationResult.newBuilder()

            if (computationResultCallCount == 0) {
                resultBuilder.setState(PENDING)
            } else if (computationResultCallCount == 1) {
                resultBuilder.setState(RUNNING)
            } else if (returnError) {
                resultBuilder.setState(ERROR).setError('Some Error')
            } else {
                resultBuilder.setState(COMPUTED).setResult(1)
            }

            responseObserver.onNext(resultBuilder.build())
            responseObserver.onCompleted()
            computationResultCallCount++
        }

        @Override
        StreamObserver<StreamingStop> streamComputationEvents(StreamObserver<ComputationEvent> responseObserver) {
            responseObserver.onNext(pending(ID))
            responseObserver.onNext(running(ID))
            responseObserver.onNext(computed(ID, 1))

            return new StreamObserver<StreamingStop>() {

                @Override
                void onNext(StreamingStop value) {}

                @Override
                void onError(Throwable t) {}

                @Override
                void onCompleted() {}

            }
        }

        void setReturnError(boolean returnError) {
            this.returnError = returnError
        }

    }

}
