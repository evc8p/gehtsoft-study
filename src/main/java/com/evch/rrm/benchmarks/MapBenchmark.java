package com.evch.rrm.benchmarks;

import com.evch.rrm.ConcurrentHashMap;
import com.evch.rrm.CustomHashMap;

import java.util.HashMap;
import java.util.Map;

public class MapBenchmark {
    public static void main(String[] args) {
        MapBenchmark benchmark = new MapBenchmark();
        Map<Integer, Integer> hashmap;
        Map<Integer, Integer> customHashMap;
        Map<Integer, Integer> concurrentHashMap;

        // sleep to start and prepare jconsole
//        try {
//            Thread.sleep(25000);
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        }

        // Bulk Addition Tests
        int iterations = 10_000_000;
        hashmap = new HashMap<>();
        customHashMap = new CustomHashMap<>();
        concurrentHashMap = new ConcurrentHashMap<>();
        System.out.println("\nBulk Addition Tests. " + iterations + " iterations");
        System.out.println("\t\t\t\t\tmin\t\tmax\t\taverage");
        benchmark.bulkAdditionTest("hashmap\t\t\t", hashmap, iterations);
        benchmark.bulkAdditionTest("customHashMap\t", customHashMap, iterations);
        benchmark.bulkAdditionTest("concurrentHashMap", concurrentHashMap, iterations);

        // Add/Remove Tests
        iterations = 10_000_000;
        hashmap = new HashMap<>();
        customHashMap = new CustomHashMap<>();
        concurrentHashMap = new ConcurrentHashMap<>();
        System.out.println("\n\nAdd/Remove Tests. " + iterations + " iterations");
        System.out.println("\t\t\t\t\tmin\t\tmax\t\taverage");
        benchmark.addRemoveTest("hashmap\t\t\t", hashmap, iterations);
        benchmark.addRemoveTest("customHashMap\t", customHashMap, iterations);
        benchmark.addRemoveTest("concurrentHashMap", concurrentHashMap, iterations);
    }

    public void bulkAdditionTest(String text, Map<Integer, Integer> map, int iterations) {
        long min = 0, max = 0, t, t1;
        for (int j = 0; j < 20; j++) {
            t = System.currentTimeMillis();
            for (int i = 0; i < iterations; i++) {
                map.put(i, (int) System.currentTimeMillis());
            }
            t1 = System.currentTimeMillis() - t;
            map.clear();
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
        System.out.printf(text + "\t%dms\t\t%dms\t\t%dms\n", min, max, (max + min) / 2);
    }

    public void addRemoveTest(String text, Map<Integer, Integer> map, int iterations) {
        long min = 0, max = 0, t, t1;
        for (int j = 0; j < 20; j++) {
            t = System.currentTimeMillis();
            for (int i = 0; i < iterations; i++) {
                map.put(i, (int) System.currentTimeMillis());
                map.clear();
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
        System.out.printf(text + "\t%dms\t\t%dms\t\t%dms\n", min, max, (max + min) / 2);
    }
}
