package com.github.kmingulov.math.token;

import com.google.common.collect.ImmutableSet;

import java.util.ArrayList;
import java.util.List;

public final class Tokenizer {

    private static final ImmutableSet<Character> DIGITS = ImmutableSet.of(
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9'
    );

    private Tokenizer() {}

    public static List<Token> tokenize(String expression) {
        List<Token> tokens = new ArrayList<>();

        TokenType currentTokenType = null;
        int start = 0;
        for (int end = 0; end < expression.length(); end++) {
            char c = expression.charAt(end);
            TokenType tokenType = charCategory(c);

            if (tokenType == currentTokenType && (tokenType == TokenType.FUNCTION || tokenType == TokenType.NUMBER)) {
                continue;
            }

            if (currentTokenType != null) {
                String token = expression.substring(start, end);
                tokens.add(new Token(currentTokenType, token));
            }

            currentTokenType = tokenType;
            start = end;
        }

        if (currentTokenType != null) {
            String token = expression.substring(start);
            tokens.add(new Token(currentTokenType, token));
        }

        return tokens;
    }

    private static TokenType charCategory(char c) {
        if (c == '(') {
            return TokenType.LEFT_PARENTHESIS;
        }

        if (c == ')') {
            return TokenType.RIGHT_PARENTHESIS;
        }

        if (DIGITS.contains(c)) {
            return TokenType.NUMBER;
        }

        if (Character.isWhitespace(c)) {
            return null;
        }

        if (Character.isAlphabetic(c)) {
            return TokenType.FUNCTION;
        }

        return TokenType.BINARY_OPERATION;
    }

}
