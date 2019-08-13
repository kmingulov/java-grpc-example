package com.github.kmingulov.math.server

import com.github.kmingulov.math.model.ComputationEvent
import com.github.kmingulov.math.model.ComputationId
import com.github.kmingulov.math.model.ComputationRequest
import com.github.kmingulov.math.model.ComputationResult
import com.github.kmingulov.math.model.StreamingStop
import com.github.kmingulov.math.worker.ComputationWorker
import io.grpc.stub.StreamObserver
import spock.lang.Specification

import java.util.function.Consumer

import static com.github.kmingulov.math.model.ComputationEvents.*
import static com.github.kmingulov.math.model.ComputationState.*

class ComputationServiceSpec extends Specification {

    private static final ComputationId ID = ComputationId.newBuilder()
            .setId('12345')
            .build()

    private static final String EXPRESSION = '1+2'

    private static final ComputationRequest REQUEST = ComputationRequest.newBuilder()
            .setExpression(EXPRESSION)
            .build()

    def 'submits expression'() {
        given:
            ComputationWorker worker = new DummyWorker(ID, pending(ID))
            ComputationService service = new ComputationService(worker)
            StreamObserver<ComputationId> observer = Mock(StreamObserver)

        when:
            service.computeExpression(REQUEST, observer)

        then:
            1 * observer.onNext(ID)
            1 * observer.onCompleted()
    }

    def 'returns error when asked for result for unknown computation'() {
        given:
            ComputationService service = new ComputationService(Mock(ComputationWorker))

            StreamObserver<ComputationResult> observer = Mock(StreamObserver)

        when:
            service.getComputationResult(ID, observer)

        then:
            1 * observer.onError(_ as Throwable)
    }

    def 'persists and returns event submitted by the worker'(ComputationEvent event, ComputationResult result) {
        given:
            ComputationWorker worker = new DummyWorker(ID, event)
            ComputationService service = new ComputationService(worker)

            service.computeExpression(REQUEST, Mock(StreamObserver))

            StreamObserver<ComputationResult> observer = Mock(StreamObserver)

        when:
            service.getComputationResult(ID, observer)

        then:
            1 * observer.onNext(result)
            1 * observer.onCompleted()

        where:
            event                                   || result
            pending(ID)                             || ComputationResult.newBuilder().setState(PENDING).build()
            running(ID)                             || ComputationResult.newBuilder().setState(RUNNING).build()
            error(ID, new Exception('Some error'))  || ComputationResult.newBuilder()
                    .setState(ERROR)
                    .setError('Some error')
                    .build()
            computed(ID, 42)                        || ComputationResult.newBuilder()
                    .setState(COMPUTED)
                    .setResult(42)
                    .build()
    }

    def 'streams events'() {
        given:
            ComputationWorker worker = new DummyWorker(ID, pending(ID))
            ComputationService service = new ComputationService(worker)
            StreamObserver<ComputationEvent> responseObserver = Mock()

        when:
            service.computeExpression(REQUEST, Mock(StreamObserver))
            StreamObserver<StreamingStop> requestObserver = service.streamComputationEvents(responseObserver)
            service.computeExpression(REQUEST, Mock(StreamObserver))
            requestObserver.onCompleted()

        then:
            1 * responseObserver.onNext(pending(ID))
    }

    def 'stops streaming when request stream is completed'() {
        given:
            ComputationWorker worker = new DummyWorker(ID, pending(ID))
            ComputationService service = new ComputationService(worker)
            StreamObserver<ComputationEvent> responseObserver = Mock()

        when:
            StreamObserver<StreamingStop> requestObserver = service.streamComputationEvents(responseObserver)
            service.computeExpression(REQUEST, Mock(StreamObserver))
            requestObserver.onCompleted()
            service.computeExpression(REQUEST, Mock(StreamObserver))

        then:
            1 * responseObserver.onNext(pending(ID))
    }

    def 'stops streaming when request stream contains StreamingStop'() {
        given:
            ComputationWorker worker = new DummyWorker(ID, pending(ID))
            ComputationService service = new ComputationService(worker)
            StreamObserver<ComputationEvent> responseObserver = Mock()

        when:
            StreamObserver<StreamingStop> requestObserver = service.streamComputationEvents(responseObserver)
            service.computeExpression(REQUEST, Mock(StreamObserver))
            requestObserver.onNext(StreamingStop.getDefaultInstance())
            service.computeExpression(REQUEST, Mock(StreamObserver))

        then:
            1 * responseObserver.onNext(pending(ID))
    }

    def 'stops streaming when request stream contains error'() {
        given:
            ComputationWorker worker = new DummyWorker(ID, pending(ID))
            ComputationService service = new ComputationService(worker)
            StreamObserver<ComputationEvent> responseObserver = Mock()

        when:
            StreamObserver<StreamingStop> requestObserver = service.streamComputationEvents(responseObserver)
            service.computeExpression(REQUEST, Mock(StreamObserver))
            requestObserver.onError(new NullPointerException())
            service.computeExpression(REQUEST, Mock(StreamObserver))

        then:
            1 * responseObserver.onNext(pending(ID))
    }

    private static final class DummyWorker implements ComputationWorker {

        private final ComputationId idToReturn
        private final ComputationEvent eventToTrigger

        DummyWorker(ComputationId idToReturn, ComputationEvent eventToTrigger) {
            this.idToReturn = idToReturn
            this.eventToTrigger = eventToTrigger
        }

        @Override
        ComputationId submit(String expression, Consumer<ComputationEvent> progressListener) {
            progressListener.accept(eventToTrigger)
            return idToReturn
        }

    }

}
