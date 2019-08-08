package com.github.kmingulov.math.token;

import java.util.Objects;

public class Token {

    private final TokenType type;
    private final String data;

    Token(TokenType type, String data) {
        this.type = type;
        this.data = data;
    }

    public TokenType getType() {
        return type;
    }

    public String getData() {
        return data;
    }

    public boolean isNumber() {
        return type == TokenType.NUMBER;
    }

    public boolean isLeftParenthesis() {
        return type == TokenType.LEFT_PARENTHESIS;
    }

    public boolean isRightParenthesis() {
        return type == TokenType.RIGHT_PARENTHESIS;
    }

    public boolean isBinaryOperation() {
        return type == TokenType.BINARY_OPERATION;
    }

    public boolean isFunction() {
        return type == TokenType.FUNCTION;
    }

    @Override
    public String toString() {
        return type + "(" + data + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Token token = (Token) o;
        return type == token.type && Objects.equals(data, token.data);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, data);
    }

    public static Token number(String num) {
        return new Token(TokenType.NUMBER, num);
    }

    public static Token leftParenthesis() {
        return new Token(TokenType.LEFT_PARENTHESIS, "(");
    }

    public static Token rightParenthesis() {
        return new Token(TokenType.RIGHT_PARENTHESIS, ")");
    }

    public static Token binaryOperation(String op) {
        return new Token(TokenType.BINARY_OPERATION, op);
    }

    public static Token function(String fun) {
        return new Token(TokenType.FUNCTION, fun);
    }

}
