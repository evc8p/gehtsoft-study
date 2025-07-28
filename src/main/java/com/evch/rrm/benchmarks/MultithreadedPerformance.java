package com.evch.rrm.benchmarks;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.LongAdder;
import java.util.stream.IntStream;

import static java.lang.System.out;

public class MultithreadedPerformance {
    private static final int NUMBER_OF_ELEMENTS = 100_000_000;
    private static final short[] array = new short[NUMBER_OF_ELEMENTS];

    public static void main(String[] args) {
        MultithreadedPerformance multithreadedPerformance = new MultithreadedPerformance();
        IntStream.range(0, NUMBER_OF_ELEMENTS).parallel().forEach(e -> array[e] = 1);
        String format = "Sum with parallel %s with %d iteration%s: %d ms (%d mks)\n";

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(
                "src/main/java/com/evch/rrm/benchmarks/results/multithreaded_performance.txt"))) {
            for (int i : new int[]{1, 10, 100, 1000}) {
                if (i > array.length) i = array.length;
                long start = System.nanoTime();
                multithreadedPerformance.sumWithParallelStream(i);
                long end = System.nanoTime() - start;
                bw.write(String.format(format, "Stream", i, i == 1 ? "" : "s", end / 1_000_000L, end / 1_000L));

                start = System.nanoTime();
                multithreadedPerformance.sumWithParallelThreads(i);
                end = System.nanoTime() - start;
                bw.write(String.format(format, "Threads", i, i == 1 ? "" : "s", end / 1_000_000L, end / 1_000L));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private long sumWithParallelStream(int threadsCount) {
        int indexesInThread = NUMBER_OF_ELEMENTS / threadsCount;
        final LongAdder sum = new LongAdder();
        for (int startIndex = 0; startIndex < NUMBER_OF_ELEMENTS; startIndex += indexesInThread) {
            int endIndex = startIndex + indexesInThread;
            sum.add(IntStream.range(startIndex, endIndex).map(i -> array[i]).parallel().sum());
        }
        return sum.sum();
    }

    private long sumWithParallelThreads(int threadsCount) {
        int indexesInThread = NUMBER_OF_ELEMENTS / threadsCount;
        final LongAdder sum = new LongAdder();
        List<Thread> threads = new ArrayList<>(threadsCount);
        for (int startIndex = 0; startIndex < NUMBER_OF_ELEMENTS; startIndex += indexesInThread) {
            int endIndex = startIndex + indexesInThread;
            threads.add(Thread.startVirtualThread(getTaskOfSumByIndexes(startIndex, endIndex, sum)));
        }
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                for (Thread t : threads) {
                    if (t.isAlive()) {
                        t.interrupt();
                    }
                }
                out.println("All threads were interrupted.");
                e.printStackTrace();
                Thread.currentThread().interrupt();
            }
        }
        return sum.sum();
    }

    private Runnable getTaskOfSumByIndexes(int startIndex, int endIndex, LongAdder adder) {
        return () -> {
            int sum = 0;
            for (int i = startIndex; i < endIndex; i++) {
                sum += array[i];
            }
            adder.add(sum);
        };
    }
}
