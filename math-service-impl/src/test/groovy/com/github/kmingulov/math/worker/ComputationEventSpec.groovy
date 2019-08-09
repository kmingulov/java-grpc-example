package com.github.kmingulov.math.worker

import com.github.kmingulov.math.ComputationId
import spock.lang.Specification

import static com.github.kmingulov.math.ComputationState.*
import static com.github.kmingulov.math.worker.ComputationEvent.*

class ComputationEventSpec extends Specification {

    private static final ComputationId ID = ComputationId.newBuilder()
            .setId('12345')
            .build();

    def 'creates PENDING event' () {
        given:
        ComputationEvent event = pending(ID)

        expect:
        event.getId() == ID
        event.getState() == PENDING
        event.getResult() == null
        event.getError() == null
    }

    def 'creates RUNNING event' () {
        given:
        ComputationEvent event = running(ID)

        expect:
        event.getId() == ID
        event.getState() == RUNNING
        event.getResult() == null
        event.getError() == null
    }

    def 'creates ERROR event' () {
        given:
        Exception e = new NullPointerException()
        ComputationEvent event = error(ID, e)

        expect:
        event.getId() == ID
        event.getState() == ERROR
        event.getResult() == null
        event.getError() == e
    }

    def 'creates COMPUTED event' () {
        given:
        double result = 123
        ComputationEvent event = computed(ID, 123)

        expect:
        event.getId() == ID
        event.getState() == COMPUTED
        event.getResult() == result
        event.getError() == null
    }

}
