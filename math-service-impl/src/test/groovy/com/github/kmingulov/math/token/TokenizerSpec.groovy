package com.github.kmingulov.math.token

import spock.lang.Specification

import static com.github.kmingulov.math.token.Token.*

class TokenizerSpec extends Specification {

    def 'tokenizes 3' () {
        expect:
        Tokenizer.tokenize('3') == [ number('3') ]
    }

    def 'tokenizes *' () {
        expect:
        Tokenizer.tokenize('*') == [ binaryOperation('*') ]
    }

    def 'tokenizes (' () {
        expect:
        Tokenizer.tokenize('(') == [ leftParenthesis() ]
    }

    def 'tokenizes )' () {
        expect:
        Tokenizer.tokenize(')') == [ rightParenthesis() ]
    }

    def 'tokenizes sin' () {
        expect:
        Tokenizer.tokenize('sin') == [ function('sin') ]
    }

    def 'tokenizes 3+5' () {
        expect:
        Tokenizer.tokenize('3+5') == [ number('3'), binaryOperation('+'), number('5') ]
    }

    def 'tokenizes 3 + 5' () {
        expect:
        Tokenizer.tokenize('3 + 5') == [ number('3'), binaryOperation('+'), number('5') ]
    }

    def 'tokenizes 3+5*8' () {
        expect:
        Tokenizer.tokenize('3+5*8') \
                == [ number('3'), binaryOperation('+'), number('5'), binaryOperation('*'), number('8') ]
    }

    def 'tokenizes 3 + 5  *  8' () {
        expect:
        Tokenizer.tokenize('3 + 5  *  8') \
                == [ number('3'), binaryOperation('+'), number('5'), binaryOperation('*'), number('8') ]
    }

    def 'tokenizes sin5' () {
        expect:
        Tokenizer.tokenize('sin5') == [ function('sin'), number('5') ]
    }

    def 'tokenizes sin(5)' () {
        expect:
        Tokenizer.tokenize('sin(5)') == [ function('sin'), leftParenthesis(), number('5'), rightParenthesis() ]
    }

    def 'tokenizes sin(5+3)' () {
        expect:
        Tokenizer.tokenize('sin(5+3)') \
                == [ function('sin'), leftParenthesis(), number('5'), binaryOperation('+'), number('3'), rightParenthesis() ]
    }

    def 'tokenizes (((' () {
        expect:
        Tokenizer.tokenize('(((') == [ leftParenthesis(), leftParenthesis(), leftParenthesis() ]
    }

    def 'tokenizes ++' () {
        expect:
        Tokenizer.tokenize('++') == [ binaryOperation('+'), binaryOperation('+') ]
    }

}
