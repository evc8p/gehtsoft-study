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
        for (int i = 0; i < 36; i++) {
            int j = i;
            runAndMeasureTime(() -> fibonacciRecursive(j));
        }

        // Memoized (O(nlogn))
        System.out.println("Memoized:");
        runAndMeasureTime(() -> fibonacciMemoized(350000));
        runAndMeasureTime(() -> fibonacciMemoized(700000));
        runAndMeasureTime(() -> fibonacciMemoized(1400000));
        runAndMeasureTime(() -> fibonacciMemoized(2800000));
        runAndMeasureTime(() -> fibonacciMemoized(5600000));

        // Iterative (O(n))
        System.out.println("Iterative:");
        runAndMeasureTime(() -> fibonacciIterative(350000));
        runAndMeasureTime(() -> fibonacciIterative(700000));
        runAndMeasureTime(() -> fibonacciIterative(1400000));
        runAndMeasureTime(() -> fibonacciIterative(2800000));
        runAndMeasureTime(() -> fibonacciIterative(5600000));
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

    public static void runAndMeasureTime(Runnable task) {
        long t = System.currentTimeMillis();
        int numberOfIterations = 1;
        for (int i = 0; i < numberOfIterations; i++) {
            resultsCacheForMemoized = new HashMap<>();
            resultsCacheForIterative = new HashMap<>(Map.of(-1, 0L, 0, 0L, 1, 1L));
            task.run();
        }
        System.out.printf("The mean time taken: %d ms\n", (System.currentTimeMillis() - t) / numberOfIterations);
    }

    public static void runAndCheckMemory(Runnable task) {
        System.gc();
        int numberOfIterations = 1;
        long m = Runtime.getRuntime().freeMemory();
        for (int i = 0; i < numberOfIterations; i++) {
            resultsCacheForMemoized = new HashMap<>();
            resultsCacheForIterative = new HashMap<>(Map.of(-1, 0L, 0, 0L, 1, 1L));
            task.run();
        }
        System.out.printf("Used memory %d Mb\n", (Math.abs(m - Runtime.getRuntime().freeMemory()) / 1024));
    }
}
