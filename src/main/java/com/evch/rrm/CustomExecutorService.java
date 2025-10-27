package com.evch.rrm;

import lombok.Getter;
import lombok.Setter;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class CustomExecutorService implements ExecutorService {
    @Getter
    @Setter
    private long waitingWorkersTimeoutMs = 900_000L; // 15 minutes
    @Getter
    @Setter
    private long waitingForNewTasksTimeoutMs = 20;
    private final List<Thread> workers;
    private final BlockingQueue<Runnable> tasks;
    private final int maxCorePoolSize;
    private final boolean useVirtualThreads;
    private volatile boolean isShutdown = false;
    private volatile boolean isShutdownNow = false;
    private volatile boolean isAwaitTermination = false;
    private final Lock terminationLock = new ReentrantLock();
    private final Lock addWorkersLock = new ReentrantLock();

    public CustomExecutorService(int corePoolSize, boolean useVirtualThreads) {
        if (corePoolSize < 1) {
            throw new IllegalArgumentException("The number of core pools must be greater than 0");
        }
        this.maxCorePoolSize = corePoolSize;
        this.workers = new LinkedList<>();
        this.tasks = new LinkedBlockingQueue<>();
        this.useVirtualThreads = useVirtualThreads;

        startThreadsExecutor();
    }

    public static void main(String[] args) {
        CustomExecutorService ces = new CustomExecutorService(1, false);
        ces.testPerformanceComparison();
        ces.testConcurrentExecution();
        ces.testShutdownBehavior();
    }

    private void startThreadsExecutor() {
        Thread.ofVirtual().start(() -> {
            long time = System.currentTimeMillis();
            while (!isTerminated() || isAwaitTermination) {
                if (isShutdownNow) {
                    workers.forEach(Thread::interrupt);
                    workers.clear();
                    tasks.clear();
                    break;
                }
                try {
                    if (workers.size() < maxCorePoolSize) {
                        Runnable task = tasks.poll(waitingForNewTasksTimeoutMs, TimeUnit.MILLISECONDS);
                        if (task != null) {
                            addWorkersLock.lock();
                            workers.addLast(useVirtualThreads ? Thread.ofVirtual().start(task) : Thread.ofPlatform().start(task));
                            addWorkersLock.unlock();
                        }
                    }
                    workers.removeIf(worker -> !worker.isAlive() || worker.isInterrupted());
                    if (workers.size() >= maxCorePoolSize) {
                        if ((System.currentTimeMillis() - time) >= waitingWorkersTimeoutMs) {
                            workers.removeFirst().interrupt();
                        }
                    } else {
                        time = System.currentTimeMillis();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        });
    }

    @Override
    public void shutdown() {
        isShutdown = true;
    }

    @Override
    public List<Runnable> shutdownNow() {
        isShutdown = true;
        List<Runnable> outstandingTasks = tasks.stream().toList();
        isShutdownNow = true;
        return outstandingTasks;
    }

    @Override
    public boolean isShutdown() {
        return isShutdown;
    }

    @Override
    public boolean isTerminated() {
        return isShutdown && tasks.isEmpty() && workers.isEmpty();
    }

    @Override
    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        isAwaitTermination = true;
        Condition terminationCondition = terminationLock.newCondition();
        terminationLock.lock();
        try {
            long timeoutNs = unit.toNanos(timeout);
            while (!isTerminated()) {
                if (timeoutNs <= 0L) {
                    return false;
                }
                timeoutNs = terminationCondition.awaitNanos(timeoutNs);
            }
        } finally {
            terminationLock.unlock();
            isAwaitTermination = false;
        }
        return true;
    }

    @Override
    public <T> Future<T> submit(Callable<T> task) {
        FutureTask<T> future = null;
        if (canAddTask() && task != null) {
            future = new FutureTask<>(task);
            tasks.add(future);
        }
        return future;
    }

    @Override
    public <T> Future<T> submit(Runnable task, T result) {
        FutureTask<T> future = null;
        if (canAddTask() && task != null) {
            future = new FutureTask<T>(task, result);
            tasks.add(future);
        }
        return future;
    }

    @Override
    public Future<?> submit(Runnable task) {
        FutureTask<Object> future = null;
        if (canAddTask() && task != null) {
            future = new FutureTask<>(task, 0);
            tasks.add(future);
        }
        return future;
    }

    @Override
    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks) throws InterruptedException {
        return List.of();
    }

    @Override
    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException {
        return List.of();
    }

    @Override
    public <T> T invokeAny(Collection<? extends Callable<T>> tasks) throws InterruptedException, ExecutionException {
        return null;
    }

    @Override
    public <T> T invokeAny(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return null;
    }

    @Override
    public void execute(Runnable command) {
        if (canAddTask() && command != null) {
            tasks.add(new FutureTask<>(command, 0));
        }
    }

    private boolean canAddTask() {
        return !isShutdown && tasks.size() < Integer.MAX_VALUE * 0.95;
    }

    private void testPerformanceComparison() {
        int[] poolSizes = new int[]{10, 50, 100, 500};
        int numberOfTasks = 10000;
        System.out.println("Performance comparison");
        for (int poolSize : poolSizes) {
            ExecutorService tpe = Executors.newFixedThreadPool(poolSize);
            ExecutorService cesPlatform = new CustomExecutorService(poolSize, false);
            ExecutorService cesVirtual = new CustomExecutorService(poolSize, true);

            System.out.printf("Time to add/shutdown now tasks to Executors.newFixedThreadPool(pool size: %d): %d ms\n", poolSize,
                    submitAndShutdownNowTasksWithSleep(numberOfTasks, tpe, 10));
            System.out.printf("Time to add/shutdown now tasks to CustomExecutorService (platform tasks)(pool size: %d): %d ms\n",
                    poolSize, submitAndShutdownNowTasksWithSleep(numberOfTasks, cesPlatform, 10));
            System.out.printf("Time to add/shutdown now tasks to CustomExecutorService (virtual tasks)(pool size: %d): %d ms\n",
                    poolSize, submitAndShutdownNowTasksWithSleep(numberOfTasks, cesVirtual, 10));
        }
        System.out.println("\n---------------------------------------------------------\n");
    }

    private void testConcurrentExecution() {
        int poolSize = 1;
        ExecutorService tpe = Executors.newFixedThreadPool(poolSize);
        ExecutorService cesPlatform = new CustomExecutorService(poolSize, false);
        ExecutorService cesVirtual = new CustomExecutorService(poolSize, true);

        System.out.println("Concurrent task execution");
        System.out.printf("Count 1000 with Executors.newFixedThreadPool(pool size: %d). Result: %d\n", poolSize,
                submit1000TasksAndCount1000(tpe));
        System.out.printf("Count 1000 with CustomExecutorService (platform tasks)(pool size: %d). Result: %d\n", poolSize,
                submit1000TasksAndCount1000(cesPlatform));
        System.out.printf("Count 1000 with CustomExecutorService (virtual tasks)(pool size: %d). Result: %d\n", poolSize,
                submit1000TasksAndCount1000(cesVirtual));
        System.out.println("\n---------------------------------------------------------\n");
    }

    private void testShutdownBehavior() {
        int numberOfWorkers = 100;
        ExecutorService tpe = Executors.newFixedThreadPool(numberOfWorkers);
        ExecutorService cesPlatform = new CustomExecutorService(numberOfWorkers, false);
        ExecutorService cesVirtual = new CustomExecutorService(numberOfWorkers, true);

        int numberOfTasks = 5000;
        System.out.println("Shutdown behavior test");
        System.out.printf("Time to add+shutdown tasks to Executors.newFixedThreadPool: %d ms\n",
                submitAndShutdownTasksWithSleep5S(numberOfTasks, tpe));
        System.out.printf("Time to add+shutdown tasks to CustomExecutorService (platform tasks): %d ms\n",
                submitAndShutdownTasksWithSleep5S(numberOfTasks, cesPlatform));
        System.out.printf("Time to add+shutdown tasks to CustomExecutorService (virtual tasks): %d ms\n",
                submitAndShutdownTasksWithSleep5S(numberOfTasks, cesVirtual));
    }

    private long submitAndShutdownNowTasksWithSleep(int numberOfTasks, ExecutorService es, long sleepTime) {
        long time = System.nanoTime();
        for (int i = 0; i < numberOfTasks; i++) {
            es.submit(() -> {
                try {
                    TimeUnit.MILLISECONDS.sleep(sleepTime);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });
        }
        es.shutdownNow();
        try {
            es.awaitTermination(10, java.util.concurrent.TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return (System.nanoTime() - time) / 1_000_000;
    }

    private int submit1000TasksAndCount1000(ExecutorService es) {
        AtomicInteger sum = new AtomicInteger();
        for (int i = 0; i < 1000; i++) {
            es.submit(() -> {
                while (true) {
                    int s = sum.get();
                    if (s >= 1000 || sum.compareAndSet(s, s + 1)) {
                        break;
                    }
                }
            });
        }
        try {
            TimeUnit.MILLISECONDS.sleep(500);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        es.shutdown();
        try {
            es.awaitTermination(1, java.util.concurrent.TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return sum.get();
    }

    private long submitAndShutdownTasksWithSleep5S(int numberOfTasks, ExecutorService es) {
        long time = System.nanoTime();
        for (int i = 0; i < numberOfTasks; i++) {
            es.submit(() -> {
                try {
                    TimeUnit.MILLISECONDS.sleep(5);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });
        }
        es.shutdown();
        try {
            es.awaitTermination(20, java.util.concurrent.TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return (System.nanoTime() - time) / 1_000_000;
    }

    public int getWorkersSize() {
        return workers.size();
    }
}
