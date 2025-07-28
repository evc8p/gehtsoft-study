package com.evch.rrm.benchmarks;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static java.lang.System.gc;
import static java.lang.System.out;

public class PlatformVsVirtualThread {
    private static final int NUMBER_OF_THREADS = 8000;

    public static void main(String[] args) throws IOException, InterruptedException {
        PlatformVsVirtualThread platformVsVirtualThread = new PlatformVsVirtualThread();
        long timeVirtual = platformVsVirtualThread.runVirtualThreads(NUMBER_OF_THREADS);
        long timePlatform = platformVsVirtualThread.runPlatformThreads(NUMBER_OF_THREADS);

        Thread.sleep(20000);

        out.printf("The execution of virtual threads took %d ms.\n", timeVirtual / 1_000_000);
        out.println("----------------------------");
        out.printf("The execution of platform threads took %d ms.\n", timePlatform / 1_000_000);
    }

    private long runPlatformThreads(int threadsCount) throws IOException, InterruptedException {
        gc();
        gc();
        Thread.yield();
        createBaseLine();
        long start = System.nanoTime();
        List<Thread> threads = new ArrayList<>(threadsCount);
        for (int i = 0; i < threadsCount; i++) {
            Thread thread = new Thread(getSleepingTask());
            threads.add(thread);
            thread.start();
        }
        showMemoryDiff();
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
        return System.nanoTime() - start;
    }

    private long runVirtualThreads(int threadsCount) throws IOException, InterruptedException {
        gc();
        gc();
        Thread.yield();
        createBaseLine();
        long start = System.nanoTime();
        List<Thread> threads = new ArrayList<>(threadsCount);
        for (int i = 0; i < threadsCount; i++) {
            threads.add(Thread.startVirtualThread(getSleepingTask()));
        }
        showMemoryDiff();
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
        return System.nanoTime() - start;
    }

    private Runnable getSleepingTask() {
        return () -> {
            try {
                TimeUnit.MILLISECONDS.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        };
    }

    private void createBaseLine() throws IOException, InterruptedException {
        long pid = ProcessHandle.current().pid();
        ProcessBuilder pb = new ProcessBuilder("jcmd", String.valueOf(pid), "VM.native_memory", "baseline");
        Process process = pb.start();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        }
        process.waitFor();
    }

    private void showMemoryDiff() throws IOException, InterruptedException {
        long pid = ProcessHandle.current().pid();
        ProcessBuilder pb = new ProcessBuilder("jcmd", String.valueOf(pid), "VM.native_memory", "summary.diff");
        Process process = pb.start();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        }
        process.waitFor();
    }
}
