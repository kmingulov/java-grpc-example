package com.github.kmingulov.math.op.fun;

import java.util.ArrayList;
import java.util.List;

public final class PrimeFunction implements Function {

    @Override
    public String name() {
        return "prime";
    }

    @Override
    public int operandsCount() {
        return 1;
    }

    @Override
    public double apply(double[] args) {
        int n = (int) args[0];

        if (n <= 0) {
            throw new IllegalArgumentException("Expected a positive integer number.");
        }

        List<Long> primes = new ArrayList<>();
        primes.add(2L);
        primes.add(3L);

        long currentNum = 4;
        while (primes.size() < n) {
            boolean isPrime = true;

            for (long prime: primes) {
                if (currentNum % prime == 0) {
                    isPrime = false;
                    break;
                }
            }

            if (isPrime) {
                primes.add(currentNum);
            }

            currentNum++;
        }

        return primes.get(n - 1);
    }

}
