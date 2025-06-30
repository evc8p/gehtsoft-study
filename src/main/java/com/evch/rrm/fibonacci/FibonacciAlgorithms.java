package com.evch.rrm.fibonacci;

import java.util.HashMap;
import java.util.Map;

public class FibonacciAlgorithms {
    /**
     * Recursive implementation of Fibonacci sequence
     * Time Complexity: {todo: specify. explain}
     * Space Complexity: {todo: specify.explain }
     * <p>
     * Explanation: Each call branches into two recursive calls, creating
     * a binary tree of calls with height n. The same subproblems are
     * solved multiple times, leading to exponential time complexity.
     */

    private static Map<Integer, Long> resultsCacheForMemoized = new HashMap<>();
    private static Map<Integer, Long> resultsCacheForIterative = new HashMap<>(Map.of(-1, 0L, 0, 0L, 1, 1L));

    public static void main(String[] args) {
        // Recursive (O(2^n))
        System.out.println("Recursive:");
        runAndMeasure(() -> fibonacciRecursive(20));
        runAndMeasure(() -> fibonacciRecursive(25));
        runAndMeasure(() -> fibonacciRecursive(40));
        runAndMeasure(() -> fibonacciRecursive(43));
        runAndMeasure(() -> fibonacciRecursive(45));

        // Memoized (O(1))
        System.out.println("Memoized:");
        runAndMeasure(() -> fibonacciMemoized(3000));
        runAndMeasure(() -> fibonacciMemoized(6730)); // max. if > 6730 then StackOverflow

        // Iterative (O(n))
        System.out.println("Iterative:");
        runAndMeasure(() -> fibonacciIterative(6730));
        runAndMeasure(() -> fibonacciIterative(10000));
        runAndMeasure(() -> fibonacciIterative(20000));
        runAndMeasure(() -> fibonacciIterative(40000));
        runAndMeasure(() -> fibonacciIterative(80000));
        runAndMeasure(() -> fibonacciIterative(160000));
        runAndMeasure(() -> fibonacciIterative(320000));
        runAndMeasure(() -> fibonacciIterative(640000));
        runAndMeasure(() -> fibonacciIterative(1280000));
        runAndMeasure(() -> fibonacciIterative(2560000));
        runAndMeasure(() -> fibonacciIterative(5120000));
    }

    public static long fibonacciRecursive(int n) {
        if (n < 2) {
            return n;
        }
        return fibonacciRecursive(n - 1) + fibonacciRecursive(n - 2);
    }

    public static long fibonacciMemoized(int n) {
        if (n < 2) {
            return n;
        }
        if (resultsCacheForMemoized.get(n) != null) {
            return resultsCacheForMemoized.get(n);
        }
        long result = fibonacciMemoized(n - 1) + fibonacciMemoized(n - 2);
        resultsCacheForMemoized.put(n, result);
        return result;
    }

    public static long fibonacciIterative(int n) {
        long result = 0;
        for (int i = 1; i < n; i++) {
            result = resultsCacheForIterative.get(i) + resultsCacheForIterative.get(i - 1);
            resultsCacheForIterative.putIfAbsent(i + 1, result);
        }
        return n < 2 ? n : result;
    }

    public static void runAndMeasure(Runnable task) {
        long t = System.currentTimeMillis();
        task.run();
        System.out.printf("Time taken: %d ms\n", (System.currentTimeMillis() - t));
    }
}
