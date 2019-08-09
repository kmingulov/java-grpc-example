package com.github.kmingulov.math.op.fun

import spock.lang.Specification

class PrimeFunctionSpec extends Specification {

    private final static Function FUNCTION = new PrimeFunction()

    def 'returns the first prime'() {
        expect:
            FUNCTION.apply(1) == 2
    }

    def 'returns the second prime'() {
        expect:
            FUNCTION.apply(2) == 3
    }

    def 'returns the 10th prime'() {
        expect:
            FUNCTION.apply(10) == 29
    }

    def 'throws for 0'() {
        when:
            FUNCTION.apply(0)

        then:
            thrown IllegalArgumentException
    }

    def 'throws for negative numbers'() {
        when:
            FUNCTION.apply(-1)

        then:
            thrown IllegalArgumentException
    }

}
