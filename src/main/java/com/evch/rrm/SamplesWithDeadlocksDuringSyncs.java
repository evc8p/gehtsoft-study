package com.evch.rrm;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class SamplesWithDeadlocksDuringSyncs {
    private static final Object lock1 = new Object();
    private static final Object lock2 = new Object();
    private static final Lock lock = new ReentrantLock();
    private static final ReadWriteLock rwlock = new ReentrantReadWriteLock();
    private static final Lock readLock = rwlock.readLock();
    private static final Lock writeLock = rwlock.writeLock();

    public static void main(String[] args) throws InterruptedException {
        SamplesWithDeadlocksDuringSyncs samples = new SamplesWithDeadlocksDuringSyncs();

        // Deadlock by using two objects in synchronized
//        samples.deadlockWithTwoObjectsInSynchronized();

        // Deadlock Forgot to unlock in ReentrantLock (or unlock depends from if)
//        samples.deadlockForgetToUnlockInReentrantLock();

        // Deadlock try to lock the writeLock without unlock the readLock in ReadWriteDeadlock
//        samples.deadlockTryToLockWriteLockReadWriteDeadlock();
    }

    private void deadlockWithTwoObjectsInSynchronized() {
        Thread thread1 = new Thread(new Task1());
        Thread thread2 = new Thread(new Task2());
        thread1.start();
        thread2.start();

        try {
            Thread.sleep(3000);
            System.out.println("Deadlock reached.");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private class Task1 implements Runnable {
        @Override
        public void run() {
            synchronized (lock1) {
                System.out.println("lock1 is used in the Task1");
                try {
                    Thread.sleep(100); // Небольшая задержка
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("Trying to use lock2 in the Task1");
                synchronized (lock2) {
                    System.out.println("lock2 is used in the Task1");
                    try {
                        Thread.sleep(100); // Небольшая задержка
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private class Task2 implements Runnable {
        @Override
        public void run() {
            synchronized (lock2) {
                System.out.println("lock2 is used in the Task2");
                try {
                    Thread.sleep(100); // Небольшая задержка
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("Trying to use lock1 in the Task2");
                synchronized (lock1) {
                    System.out.println("lock1 is used in the Task2");
                    try {
                        Thread.sleep(100); // Небольшая задержка
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private void deadlockForgetToUnlockInReentrantLock() {
        Thread thread1 = new Thread(new Task3());
        Thread thread2 = new Thread(new Task4());
        thread1.start();
        thread2.start();

        try {
            Thread.sleep(3000);
            System.out.println("Deadlock reached.");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private class Task3 implements Runnable {
        @Override
        public void run() {
            lock.lock();
            System.out.println("Task3 used the lock");
            try {
                Thread.sleep(100); // Небольшая задержка
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("Task3 didn't unlock");
        }
    }

    private class Task4 implements Runnable {
        @Override
        public void run() {
            lock.lock();
            System.out.println("Task4 used the lock");
            try {
                Thread.sleep(100); // Небольшая задержка
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("Task4 didn't unlock");
        }
    }

    private void deadlockTryToLockWriteLockReadWriteDeadlock() {
        new Thread(() -> {
            System.out.println("Thread started");
            readLock.lock();
            System.out.println("readLock used");
            writeLock.lock();
            System.out.println("writeLock used");
            System.out.println("Thread stopped");
        }).start();
    }
}

