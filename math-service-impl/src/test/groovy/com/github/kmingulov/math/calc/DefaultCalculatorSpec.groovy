package com.github.kmingulov.math.calc

import spock.lang.Specification

import static com.github.kmingulov.math.op.BinaryOperation.*

class DefaultCalculatorSpec extends Specification {

    def 'computes 5'() {
        expect:
            arithmeticCalc().compute('5') == 5 as double
    }

    def 'computes 5+3'() {
        expect:
            arithmeticCalc().compute('5+3') == 8 as double
    }

    def 'computes 5*3+2'() {
        expect:
            arithmeticCalc().compute('5*3+2') == 17 as double
    }

    def 'computes 5+3*2'() {
        expect:
            arithmeticCalc().compute('5+3*2') == 11 as double
    }

    def 'computes (5+3)*2'() {
        expect:
            arithmeticCalc().compute('(5+3)*2') == 16 as double
    }

    def 'computes (5+3)*(2)'() {
        expect:
            arithmeticCalc().compute('(5+3)*(2)') == 16 as double
    }

    def 'computes (5+3)*(6-7)'() {
        expect:
            arithmeticCalc().compute('(5+3)*(6-7)') == -8 as double
    }

    def 'computes (1+2*(1+2*(1+2)))'() {
        expect:
            arithmeticCalc().compute('(1+2*(1+2*(1+2)))') == 15 as double
    }

    def 'computes 1+2+3+4'() {
        expect:
            arithmeticCalc().compute('1+2+3+4') == 10 as double
    }

    def 'computes 1/2/4'() {
        expect:
            arithmeticCalc().compute('1/2/4') == 0.125 as double
    }

    def 'throws for unbalanced left parenthesis'() {
        when:
            arithmeticCalc().compute('(5+3')

        then:
            thrown IllegalArgumentException
    }

    def 'throws for unbalanced right parenthesis'() {
        when:
            arithmeticCalc().compute('5+3)')

        then:
            thrown IllegalArgumentException
    }

    def 'throws for function calls'() {
        when:
            arithmeticCalc().compute('sin(5)')

        then:
            thrown IllegalArgumentException
    }

    def 'throws for unknown operations'() {
        when:
            arithmeticCalc().compute('5^3')

        then:
            thrown IllegalArgumentException
    }

    def 'throws for incomplete expression'() {
        when:
            arithmeticCalc().compute('5+')

        then:
            thrown IllegalArgumentException
    }

    def 'throws for incomplete expression in parenthesis'() {
        when:
            arithmeticCalc().compute('(5+)')

        then:
            thrown IllegalArgumentException
    }

    private static Calculator arithmeticCalc() {
        return new DefaultCalculator([ minus(), plus(), multiply(), divide() ])
    }

}
