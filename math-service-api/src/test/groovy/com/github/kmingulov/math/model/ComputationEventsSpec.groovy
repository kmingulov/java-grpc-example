package com.github.kmingulov.math.model

import spock.lang.Specification

import static com.github.kmingulov.math.model.ComputationEvents.*
import static com.github.kmingulov.math.model.ComputationState.*

class ComputationEventsSpec extends Specification {

    private static final ComputationId ID = ComputationId.newBuilder()
            .setId('12345')
            .build()

    def 'creates PENDING event'() {
        given:
            ComputationEvent event = pending(ID)

        expect:
            event.getId() == ID
            event.getState() == PENDING
            event.getResult() == 0
            event.getError() == ''
    }

    def 'creates RUNNING event'() {
        given:
            ComputationEvent event = running(ID)

        expect:
            event.getId() == ID
            event.getState() == RUNNING
            event.getResult() == 0
            event.getError() == ''
    }

    def 'creates ERROR event when exception has a message'() {
        given:
            Exception e = new NullPointerException('some error')
            ComputationEvent event = error(ID, e)

        expect:
            event.getId() == ID
            event.getState() == ERROR
            event.getResult() == 0
            event.getError() == e.getMessage()
    }

    def 'creates ERROR event when exception has no message'() {
        given:
            Exception e = new NullPointerException()
            ComputationEvent event = error(ID, e)

        expect:
            event.getId() == ID
            event.getState() == ERROR
            event.getResult() == 0
            event.getError() == 'java.lang.NullPointerException'
    }

    def 'creates COMPUTED event'() {
        given:
            double result = 123
            ComputationEvent event = computed(ID, 123)

        expect:
            event.getId() == ID
            event.getState() == COMPUTED
            event.getResult() == result
            event.getError() == ''
    }
}
