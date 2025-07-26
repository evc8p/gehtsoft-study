package com.evch.rrm.benchmarks;

import java.util.Arrays;

public class ArrayOperations {

    public static void main(String[] args) {
        System.out.println("------------------------------------");
        System.out.println("Test with arrays of different sizes");
        int[] sizes = new int[]{1_000, 10_000, 100_000, 1_000_000};
        for (int size : sizes) {
            System.out.printf("Size = %d.\n", size);
            Object[] array = new Object[size];
            Arrays.fill(array, 0);
            System.out.print("Using System.arraycopy. ");
            runAndMeasureTime(() -> shiftLeftSystemCopy(array, 1));
            System.out.print("Using manual loop. ");
            runAndMeasureTime(() -> shiftLeftManualLoop(array, 1));
        }
        System.out.println("------------------------------------");
        System.out.println("Test with different shift positions");
        int[] positions = new int[]{1, 100, 1_000, 100_000, 1_000_000};
        Object[] array = new Object[1_000_000];
        Arrays.fill(array, 0);
        for (int position : positions) {
            System.out.printf("Position to shift = %d.\n", position);
            System.out.print("Using System.arraycopy. ");
            runAndMeasureTime(() -> shiftLeftSystemCopy(array, position));
            System.out.print("Using manual loop. ");
            runAndMeasureTime(() -> shiftLeftManualLoop(array, position));
        }
    }

    /**
     * Shift array elements using System.arraycopy
     */
    public static void shiftLeftSystemCopy(Object[] array, int positions) {
        if (array == null || positions < 0 || positions > array.length) {
            throw new IllegalArgumentException();
        }
        for (int i = 0; i < 2000; i++) {
            System.arraycopy(array, positions, array, 0, array.length - positions);
        }
    }

    /**
     * Shift array elements using manual for loop
     */
    public static void shiftLeftManualLoop(Object[] array, int positions) {
        if (array == null || positions < 0 || positions > array.length) {
            throw new IllegalArgumentException();
        }
        for (int j = 0; j < 2000; j++) {
            for (int i = positions; i < array.length; i++) {
                array[i - positions] = array[i];
            }
        }
    }

    public static void runAndMeasureTime(Runnable task) {
        long t = System.currentTimeMillis();
        int numberOfIterations = 1;
        for (int i = 0; i < numberOfIterations; i++) {
            task.run();
        }
        System.out.printf("The mean time taken: %d ms\n", (System.currentTimeMillis() - t));
    }
}
