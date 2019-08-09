package com.github.kmingulov.math.server

import com.github.kmingulov.math.ComputationId
import com.github.kmingulov.math.ComputationRequest
import com.github.kmingulov.math.ComputationResult
import com.github.kmingulov.math.worker.ComputationEvent
import com.github.kmingulov.math.worker.ComputationWorker
import io.grpc.stub.StreamObserver
import spock.lang.Specification

import java.util.function.Consumer

import static com.github.kmingulov.math.ComputationState.*
import static com.github.kmingulov.math.worker.ComputationEvent.*

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
            ComputationWorker worker = Mock(ComputationWorker) {
                submit(EXPRESSION, _ as Consumer) >> ID
            }

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
            ComputationWorker worker = Mock(ComputationWorker) {
                submit(EXPRESSION, _ as Consumer) >> { args ->
                    args[1].accept(event)
                    return ID
                }
            }

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

}
