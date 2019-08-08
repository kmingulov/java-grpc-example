package com.github.kmingulov.math.op

import spock.lang.Specification

class BinaryOperationSpec extends Specification {

    def "creates plus operation" () {
        given:
        BinaryOperation op = BinaryOperation.plus();

        expect:
        op.symbol() == '+' as char
        op.precedence() == 2
        op.compute(1, 2) == 3
    }

    def "creates minus operation" () {
        given:
        BinaryOperation op = BinaryOperation.minus();

        expect:
        op.symbol() == '-' as char
        op.precedence() == 2
        op.compute(1, 2) == -1
    }

    def "creates multiply operation" () {
        given:
        BinaryOperation op = BinaryOperation.multiply();

        expect:
        op.symbol() == '*' as char
        op.precedence() == 3
        op.compute(2, 3) == 6
    }

    def "creates divide operation" () {
        given:
        BinaryOperation op = BinaryOperation.divide();

        expect:
        op.symbol() == '/' as char
        op.precedence() == 3
        op.compute(3, 2) == 1.5 as double
    }

}
