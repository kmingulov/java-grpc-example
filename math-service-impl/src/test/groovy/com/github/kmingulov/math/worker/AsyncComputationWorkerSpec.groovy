package com.github.kmingulov.math.worker

import com.github.kmingulov.math.model.ComputationId
import com.github.kmingulov.math.calc.Calculator
import spock.lang.Specification

import java.util.concurrent.ConcurrentHashMap
import java.util.function.Consumer

import static com.github.kmingulov.math.worker.ComputationEvent.*

class AsyncComputationWorkerSpec extends Specification {

    private static final String EXPRESSION = '1+2'

    private static final Calculator SLOW_CALCULATOR = { expression ->
        Thread.sleep(100)
        return 0 as double
    }

    private static final Exception THROWN_EXCEPTION = new IllegalArgumentException()
    private static final Calculator THROWING_CALCULATOR = { expression ->
        Thread.sleep(100)
        throw THROWN_EXCEPTION
    }

    def 'executes a task successfully'() {
        given:
            ComputationWorker worker = new AsyncComputationWorker(SLOW_CALCULATOR)
            EventCaptor eventCaptor = new EventCaptor()

        when:
            ComputationId id = worker.submit(EXPRESSION, eventCaptor)

        then:
            Thread.sleep(500)
            eventCaptor.getEvents() == [ pending(id), running(id), computed(id, 0) ].toSet()

        cleanup:
            worker.close()
    }

    def 'executes a task with an error'() {
        given:
            ComputationWorker worker = new AsyncComputationWorker(THROWING_CALCULATOR)
            EventCaptor eventCaptor = new EventCaptor()

        when:
            ComputationId id = worker.submit(EXPRESSION, eventCaptor)

        then:
            Thread.sleep(500)
            eventCaptor.getEvents() == [ pending(id), running(id), error(id, THROWN_EXCEPTION) ].toSet()

        cleanup:
            worker.close()
    }

    def 'doesn\'t accept expressions when closed'() {
        given:
            ComputationWorker worker = new AsyncComputationWorker(SLOW_CALCULATOR)
            worker.close()

        when:
            worker.submit(EXPRESSION, new EventCaptor())

        then:
            thrown IllegalStateException
    }

    def 'throws when closed twice'() {
        given:
            ComputationWorker worker = new AsyncComputationWorker(SLOW_CALCULATOR)
            worker.close()

        when:
            worker.close()

        then:
            thrown IllegalStateException
    }

    private static final class EventCaptor implements Consumer<ComputationEvent> {

        private final ConcurrentHashMap<ComputationEvent, Boolean> events = [ ]

        Set<ComputationEvent> getEvents() {
            return new HashSet<>(events.keySet())
        }

        @Override
        void accept(ComputationEvent event) {
            events.put(event, true)
        }

    }

}
