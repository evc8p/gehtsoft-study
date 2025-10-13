package com.evch.rrm;

import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

import static java.lang.System.out;

public class BankAtomic {
    private Map<Integer, Account> accounts = new HashMap<>();

    public static void main(String[] args) {
        BankAtomic bank = new BankAtomic(200, 0L, 1_000L);
        System.out.println("Initial total: " + bank.getSumOfAllAccounts());
        bank.startTransfers();
        System.out.println("Final total: " + bank.getSumOfAllAccounts());
    }

    public BankAtomic(int numberOfAccounts, long minBalance, long maxBalance) {
        if (numberOfAccounts < 1 || minBalance < 0 || minBalance > maxBalance) {
            throw new IllegalArgumentException("numberOfAccounts or minBalance are less than 0 or maxBalance is less than minBalance");
        }
        int newId = 0;
        for (int i = 0; i < numberOfAccounts; i++) {
            AtomicLong newBalance = new AtomicLong(new Random().nextLong(maxBalance - minBalance + 1) + minBalance);
            accounts.put(newId, new Account(newId, newBalance));
            newId++;
        }
    }

    public void startTransfers() {
        List<Thread> threads = new ArrayList<>();
        for (int i = 0; i < this.accounts.size(); i++) {
            threads.add(Thread.ofVirtual().start(this.transferFunds()));
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
    }

    public int pickRandomAccountId() {
        return new Random().nextInt(accounts.size());
    }

    public AtomicLong getAccountBalance(int accountId) {
        return accounts.get(accountId).getBalance();
    }

    public void setAccountBalance(int accountId, AtomicLong newBalance) {
        if (accountId < 0 || accountId >= accounts.size()) {
            throw new IllegalArgumentException("accountId is out of range");
        }
        accounts.get(accountId).setBalance(newBalance);
    }

    public BigInteger getSumOfAllAccounts() {
        BigInteger result = new BigInteger(String.valueOf(0));
        for (int i = 0; i < accounts.size(); i++) {
            result = result.add(BigInteger.valueOf(accounts.get(i).getBalance().longValue()));
        }
        return result;
    }

    private Runnable transferFunds() {
        return new Runnable() {
            @Override
            public void run() {
                int to, from = BankAtomic.this.pickRandomAccountId();
                do {
                    to = BankAtomic.this.pickRandomAccountId();
                } while (to == from);
                if (BankAtomic.this.getAccountBalance(from).longValue() > 0) {
                    long x = new Random().nextLong(Math.max(1, BankAtomic.this.getAccountBalance(from).longValue() + 1));
                    transferFunds(from, to, x);
                }
            }

            private void transferFunds(int from, int to, long x) {
                // withdraw
                int counter = 0;
                while (counter < 1000) {
                    counter++;
                    AtomicLong atomicLongBalance = BankAtomic.this.getAccountBalance(from);
                    long balance = atomicLongBalance.longValue();
                    if (balance < 0) {
                        return;
                    } else {
                        if (atomicLongBalance.compareAndSet(balance, balance - x)) {
                            BankAtomic.this.getAccountBalance(to).addAndGet(x);
                            return;
                        }
                    }
                }
                if (counter >= 1000) {
                    throw new RuntimeException("Race condition is reached");
                }
            }
        };
    }

    private class Account {
        private int id;
        @Setter
        @Getter
        private AtomicLong balance;

        public Account(int id, AtomicLong balance) {
            this.id = id;
            this.balance = balance;
        }
    }
}
