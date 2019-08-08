package com.github.kmingulov.math.token

import spock.lang.Specification

class TokenSpec extends Specification {

    def 'creates number token' () {
        given:
        Token token = Token.number('123')

        expect:
        token.isNumber()
        token.getType() == TokenType.NUMBER
        token.getData() == '123'
    }

    def 'creates left parenthesis token' () {
        given:
        Token token = Token.leftParenthesis()

        expect:
        token.isLeftParenthesis()
        token.getType() == TokenType.LEFT_PARENTHESIS
        token.getData() == '('
    }

    def 'creates right parenthesis token' () {
        given:
        Token token = Token.rightParenthesis()

        expect:
        token.isRightParenthesis()
        token.getType() == TokenType.RIGHT_PARENTHESIS
        token.getData() == ')'
    }

    def 'creates binary operation token' () {
        given:
        Token token = Token.binaryOperation('?')

        expect:
        token.isBinaryOperation()
        token.getType() == TokenType.BINARY_OPERATION
        token.getData() == '?'
    }

    def 'creates function token' () {
        given:
        Token token = Token.function('sin')

        expect:
        token.isFunction()
        token.getType() == TokenType.FUNCTION
        token.getData() == 'sin'
    }

}
