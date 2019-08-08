package com.github.kmingulov.math.calc;

import com.github.kmingulov.math.op.BinaryOperation;
import com.github.kmingulov.math.token.Token;
import com.github.kmingulov.math.token.Tokenizer;
import com.google.common.collect.ImmutableMap;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

class DefaultCalculator implements Calculator {

    private final ImmutableMap<Character, BinaryOperation> operationBySymbol;

    DefaultCalculator(List<BinaryOperation> operations) {
        this.operationBySymbol = ImmutableMap.copyOf(
                operations
                        .stream()
                        .collect(toMap(BinaryOperation::symbol, identity()))
        );
    }

    @Override
    public double compute(String expression) {
        List<Token> tokens = Tokenizer.tokenize(expression);

        Deque<Double> numberStack = new ArrayDeque<>();
        Deque<Token> opStack = new ArrayDeque<>();

        for (Token token : tokens) {
            String data = token.getData();

            switch (token.getType()) {
                case NUMBER:
                    numberStack.addLast(parseNumber(data));
                    break;

                case BINARY_OPERATION:
                    processOperation(token, numberStack, opStack);
                    break;

                case LEFT_PARENTHESIS:
                    opStack.addLast(token);
                    break;

                case RIGHT_PARENTHESIS:
                    processRightParenthesis(numberStack, opStack);
                    break;

                default:
                    throw new IllegalArgumentException("Unsupported token " + token.getType());
            }
        }

        while (!opStack.isEmpty()) {
            Token token = opStack.removeLast();
            if (token.isLeftParenthesis()) {
                throw new IllegalArgumentException("Parenthesis aren't balanced in the given expression.");
            }

            BinaryOperation op = getOperation(token.getData());
            doOperation(op, numberStack);
        }

        return numberStack.removeLast();
    }

    private double parseNumber(String data) {
        try {
            return Double.parseDouble(data);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(data + " is not a valid number.", e);
        }
    }

    private void processOperation(Token opToken, Deque<Double> numberStack, Deque<Token> opStack) {
        BinaryOperation op = getOperation(opToken.getData());

        while (!opStack.isEmpty()) {
            Token otherToken = opStack.getLast();
            if (otherToken.isLeftParenthesis()) {
                break;
            }

            BinaryOperation otherOp = getOperation(otherToken.getData());
            if (otherOp.precedence() < op.precedence()) {
                break;
            }

            opStack.removeLast();
            doOperation(otherOp, numberStack);
        }

        opStack.addLast(opToken);
    }

    private void processRightParenthesis(Deque<Double> numberStack, Deque<Token> opStack) {
        while (!opStack.isEmpty() && !opStack.getLast().isLeftParenthesis()) {
            BinaryOperation op = getOperation(opStack.removeLast().getData());
            doOperation(op, numberStack);
        }

        if (opStack.isEmpty()) {
            throw new IllegalArgumentException("Parenthesis aren't balanced in the given expression.");
        }

        opStack.removeLast();
    }

    private BinaryOperation getOperation(String data) {
        if (data.length() == 1) {
            char c = data.charAt(0);
            if (operationBySymbol.containsKey(c)) {
                return operationBySymbol.get(c);
            }
        }

        throw new IllegalArgumentException(data + " is not a valid operation.");
    }

    private void doOperation(BinaryOperation op, Deque<Double> numberStack) {
        if (numberStack.size() < 2) {
            throw new IllegalArgumentException("Incomplete expression, not enough arguments for " + op.symbol());
        }

        double b = numberStack.removeLast();
        double a = numberStack.removeLast();
        numberStack.addLast(op.compute(a, b));
    }

}
