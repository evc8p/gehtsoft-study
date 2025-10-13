package com.evch.rrm.benchmarks;

import com.evch.rrm.CustomList;
import com.evch.rrm.decorators.ReentrantReadWriteLockListDecorator;
import com.evch.rrm.decorators.SynchronizedListDecorator;

import java.util.List;
import java.util.stream.IntStream;

public class CustomListLocksPerformanceTest {
    public static final int NUMBER_OF_ELEMENTS_TO_ADD = 500_000;

    public static void main(String[] args) throws InterruptedException {
        CustomListLocksPerformanceTest synchronizedCustomListPerformanceTest = new CustomListLocksPerformanceTest();

        CustomList<Integer> customList = new CustomList<>();
        System.out.printf("CustomList. Adding a million elements with two threads took %d ms\n",
                synchronizedCustomListPerformanceTest.timeInMsForAddingElements(customList));

        SynchronizedListDecorator<Integer> synchronizedListDecorator =
                new SynchronizedListDecorator<>(new CustomList<>());
        System.out.printf("SynchronizedListDecorator. Adding a million elements with two threads took %d ms\n",
                synchronizedCustomListPerformanceTest.timeInMsForAddingElements(synchronizedListDecorator));

        ReentrantReadWriteLockListDecorator<Integer> reentrantReadWriteLockListDecorator =
                new ReentrantReadWriteLockListDecorator<>(new CustomList<>());
        System.out.printf("ReentrantReadWriteLockListDecorator. Adding a million elements with two threads took %d ms\n",
                synchronizedCustomListPerformanceTest.timeInMsForAddingElements(reentrantReadWriteLockListDecorator));
    }

    private long timeInMsForAddingElements(List<Integer> list) throws InterruptedException {
        long time = System.nanoTime();
        Thread thread1 = Thread.startVirtualThread(addElementTask(list, NUMBER_OF_ELEMENTS_TO_ADD));
        if (!(list instanceof CustomList)) {
            Thread thread2 = Thread.startVirtualThread(addElementTask(list, NUMBER_OF_ELEMENTS_TO_ADD));
            thread2.join();
        }
        thread1.join();
        return (System.nanoTime() - time) / 1_000_000;
    }

    private Runnable addElementTask(List<Integer> list, int quantity) {
        return () -> {
            if (!(list instanceof CustomList)) {
                IntStream.range(0, quantity).parallel().forEach(n -> list.add(1));
            } else {
                IntStream.range(0, quantity).forEach(n -> list.add(1));
            }
        };
    }
}
