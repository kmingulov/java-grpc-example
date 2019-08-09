package com.github.kmingulov.math.calc;

import com.github.kmingulov.math.op.Operation;
import com.github.kmingulov.math.op.binary.BinaryOperation;
import com.github.kmingulov.math.op.fun.Function;
import com.github.kmingulov.math.token.Token;
import com.github.kmingulov.math.token.Tokenizer;
import com.google.common.collect.ImmutableMap;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.Map;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

final class DefaultCalculator implements Calculator {

    private final ImmutableMap<String, Operation> operationBySymbol;

    DefaultCalculator(List<BinaryOperation> operations, List<Function> functions) {
        Map<String, Operation> binaryOperationBySymbol = operations
                .stream()
                .collect(toMap(op -> String.valueOf(op.symbol()), identity()));

        Map<String, Operation> functionByName = functions
                .stream()
                .collect(toMap(Function::name, identity()));

        this.operationBySymbol = ImmutableMap.<String, Operation>builder()
                .putAll(binaryOperationBySymbol)
                .putAll(functionByName)
                .build();
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
                    Operation operation = getOperation(token.getData());
                    processOperation(token, (BinaryOperation) operation, numberStack, opStack);
                    break;

                case LEFT_PARENTHESIS:
                case FUNCTION:
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

            Operation op = getOperation(token.getData());
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

    private void processOperation(Token opToken, BinaryOperation op, Deque<Double> numberStack, Deque<Token> opStack) {
        while (!opStack.isEmpty()) {
            Token otherToken = opStack.getLast();
            if (otherToken.isLeftParenthesis()) {
                break;
            }

            Operation otherOp = getOperation(otherToken.getData());
            if (otherToken.isBinaryOperation()) {
                BinaryOperation otherBinaryOp = (BinaryOperation) otherOp;
                if (otherBinaryOp.precedence() < op.precedence()) {
                    break;
                }
            }

            opStack.removeLast();
            doOperation(otherOp, numberStack);
        }

        opStack.addLast(opToken);
    }

    private void processRightParenthesis(Deque<Double> numberStack, Deque<Token> opStack) {
        while (!opStack.isEmpty() && !opStack.getLast().isLeftParenthesis()) {
            Operation op = getOperation(opStack.removeLast().getData());
            doOperation(op, numberStack);
        }

        if (opStack.isEmpty()) {
            throw new IllegalArgumentException("Parenthesis aren't balanced in the given expression.");
        }

        opStack.removeLast();
    }

    private Operation getOperation(String data) {
        if (operationBySymbol.containsKey(data)) {
            return operationBySymbol.get(data);
        }

        throw new IllegalArgumentException(data + " is not a valid operation or function.");
    }

    private void doOperation(Operation op, Deque<Double> numberStack) {
        if (numberStack.size() < op.operandsCount()) {
            throw new IllegalArgumentException("Incomplete expression, not enough arguments for " + op.getClass().getName());
        }

        double[] args = new double[op.operandsCount()];
        for (int i = 0; i < op.operandsCount(); i++) {
            args[op.operandsCount() - i - 1] = numberStack.removeLast();
        }

        numberStack.addLast(op.apply(args));
    }

}
