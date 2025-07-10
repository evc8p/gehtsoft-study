package com.evch.rrm.benchmarks;

import com.evch.rrm.CustomLinkedList;
import com.evch.rrm.CustomList;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Benchmark {
    public static void main(String[] args) {
        Benchmark benchmark = new Benchmark();
        List<Integer> arrayList = new ArrayList<>();
        List<Integer> linkedList = new LinkedList<>();
        List<Integer> customList = new CustomList<>();
        List<Integer> customLinkedList = new CustomLinkedList<>();

        // sleep to start and prepare jconsole
        try {
            Thread.sleep(25000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        // Bulk Addition Tests
        int iterations = 1_000_000;
        System.out.println("\nBulk Addition Tests. " + iterations + " iterations");
        System.out.println("\t\t\t\t\tmin\t\tmax\t\taverage");
        benchmark.bulkAdditionTest("linkedList", linkedList, iterations);
        benchmark.bulkAdditionTest("customLinkedList", customLinkedList, iterations);
        benchmark.bulkAdditionTest("arrayList", arrayList, iterations);
        benchmark.bulkAdditionTest("customList", customList, iterations);

        // Add/Remove Tests
        iterations = 10_000;
        System.out.println("\n\nAdd/Remove Tests. " + iterations + " iterations");
        System.out.println("\t\t\t\t\tmin\t\tmax\t\taverage");
        benchmark.addRemoveTest("linkedList", linkedList, iterations);
        benchmark.addRemoveTest("customLinkedList", customLinkedList, iterations);
        benchmark.addRemoveTest("arrayList", arrayList, iterations);
        benchmark.addRemoveTest("customList", customList, iterations);
    }

    public void bulkAdditionTest(String text, List<Integer> list, int iterations) {
        long min = 0, max = 0, t, t1;
        for (int j = 0; j < 20; j++) {
            t = System.currentTimeMillis();
            for (int i = 0; i < iterations; i++) {
                list.add(i);
            }
            t1 = System.currentTimeMillis() - t;
            list.clear();
            if (j == 0) {
                min = t1;
                max = t1;
            } else {
                if (min > t1) {
                    min = t1;
                }
                if (max < t1) {
                    max = t1;
                }
            }
        }
        System.out.printf(text + "\t\t\t%dms\t\t%dms\t\t%dms\n", min, max, (max + min) / 2);
    }

    public void addRemoveTest(String text, List<Integer> list, int iterations) {
        long min = 0, max = 0, t, t1;
        for (int j = 0; j < 20; j++) {
            t = System.currentTimeMillis();
            for (int i = 0; i < iterations; i++) {
                list.add(i);
            }
            for (int i = 0; i < iterations; i++) {
                list.remove(0);
            }
            t1 = System.currentTimeMillis() - t;
            if (j == 0) {
                min = t1;
                max = t1;
            } else {
                if (min > t1) {
                    min = t1;
                }
                if (max < t1) {
                    max = t1;
                }
            }
        }
        System.out.printf(text + "\t\t\t%dms\t\t%dms\t\t%dms\n", min, max, (max + min) / 2);
    }
}

