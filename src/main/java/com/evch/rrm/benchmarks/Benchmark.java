package com.evch.rrm.benchmarks;

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

        // Bulk Addition Tests
        System.out.println("\nBulk Addition Tests. 10_000_000 iterations");
        benchmark.bulkAdditionTest("linkedList", linkedList, 1_000_000);
        benchmark.bulkAdditionTest("customList", customList, 1_000_000);
        benchmark.bulkAdditionTest("arrayList", arrayList, 1_000_000);

        // Add/Remove Tests
        System.out.println("\n\nAdd/Remove Tests. 100_000 iterations");
        benchmark.addRemoveTest("linkedList", linkedList, 100_00);
        benchmark.addRemoveTest("customList", customList, 100_00);
        benchmark.addRemoveTest("arrayList", arrayList, 100_00);
    }

    public void bulkAdditionTest(String text, List<Integer> list, int iterations) {
        long min = 0, max = 0, t, t1;
        System.out.printf("%" + text.length() + "s\t\tmin\t\tmax\t\taverage\n", "");
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
        System.out.printf(text + "\t\t%dms\t\t%dms\t\t%dms\n", min, max, (max + min) / 2);
    }

    public void addRemoveTest(String text, List<Integer> list, int iterations) {
        long min = 0, max = 0, t, t1;
        System.out.printf("%" + text.length() + "s\t\tmin\t\tmax\t\taverage\n", "");
        for (int j = 0; j < 10; j++) {
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
        System.out.printf(text + "\t\t%dms\t\t%dms\t\t%dms\n", min, max, (max + min) / 2);
    }
}

