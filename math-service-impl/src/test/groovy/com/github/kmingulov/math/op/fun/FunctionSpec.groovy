package com.github.kmingulov.math.op.fun

import spock.lang.Specification

class FunctionSpec extends Specification {

    def 'creates sin function'() {
        given:
            Function fun = Function.sin()

        expect:
            fun.name() == 'sin'
            fun.operandsCount() == 1
            fun.apply(1) == Math.sin(1)
    }

    def 'creates cos function'() {
        given:
            Function fun = Function.cos()

        expect:
            fun.name() == 'cos'
            fun.operandsCount() == 1
            fun.apply(1) == Math.cos(1)
    }

    def 'creates tan function'() {
        given:
            Function fun = Function.tan()

        expect:
            fun.name() == 'tan'
            fun.operandsCount() == 1
            fun.apply(1) == Math.tan(1)
    }

    def 'throws for wrong number of arguments'() {
        given:
            Function fun = Function.sin()

        when:
            fun.apply(1, 2, 3)

        then:
            thrown IllegalArgumentException
    }

}
