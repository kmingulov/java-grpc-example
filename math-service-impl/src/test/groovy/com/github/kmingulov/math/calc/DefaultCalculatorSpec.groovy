package com.github.kmingulov.math.calc

import spock.lang.Specification

class DefaultCalculatorSpec extends Specification {

    def 'computes 5'() {
        expect:
            arithmeticCalc().compute('5') == 5 as double
    }

    def 'computes 3.1415'() {
        expect:
            arithmeticCalc().compute('3.1415') == 3.1415 as double
    }

    def 'computes 5+3'() {
        expect:
            arithmeticCalc().compute('5+3') == 8 as double
    }

    def 'computes 3.14+3.15'() {
        expect:
            arithmeticCalc().compute('3.14+3.15') == 6.29 as double
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

    def 'computes sin(1)'() {
        expect:
            trigonometricCalc().compute('sin(1)') == Math.sin(1)
    }

    def 'computes sin1'() {
        expect:
            trigonometricCalc().compute('sin1') == Math.sin(1)
    }

    def 'computes sin(1+2)'() {
        expect:
            trigonometricCalc().compute('sin(1+2)') == Math.sin(3)
    }

    def 'computes sin(1+2*(1+2))'() {
        expect:
            trigonometricCalc().compute('sin(1+2*(1+2))') == Math.sin(7)
    }

    def 'computes sin(1+2*(1+2))/3'() {
        expect:
            trigonometricCalc().compute('sin(1+2*(1+2))/3') == Math.sin(7) / 3 as double
    }

    def 'computes sin(3)+tan(2/5)'() {
        expect:
            trigonometricCalc().compute('sin(3)+tan(2/5)') == Math.sin(3) + Math.tan(0.4)
    }

    def 'computes sin(sin(3))'() {
        expect:
            trigonometricCalc().compute('sin(sin(3))') == Math.sin(Math.sin(3))
    }

    def 'throws for invalid number'() {
        when:
            arithmeticCalc().compute('3.14.15')

        then:
            thrown IllegalArgumentException
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

    def 'throws for unknown function calls'() {
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
        return Calculator.builder()
                .arithmeticOperations()
                .build()
    }

    private static Calculator trigonometricCalc() {
        return Calculator.builder()
                .arithmeticOperations()
                .trigonometricFunctions()
                .build()
    }

}
