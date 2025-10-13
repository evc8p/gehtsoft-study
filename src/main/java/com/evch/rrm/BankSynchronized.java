package com.evch.rrm;

import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;
import java.util.*;

import static java.lang.System.out;

public class BankSynchronized {
    private Map<Integer, Account> accounts = new HashMap<>();

    public static void main(String[] args) {
        BankSynchronized bank = new BankSynchronized(200, 0L, 1_000L);
        System.out.println("Initial total: " + bank.getSumOfAllAccounts());
        bank.startTransfers();
        System.out.println("Final total: " + bank.getSumOfAllAccounts());
    }

    public BankSynchronized(int numberOfAccounts, long minBalance, long maxBalance) {
        if (numberOfAccounts < 1 || minBalance < 0 || minBalance > maxBalance) {
            throw new IllegalArgumentException("numberOfAccounts or minBalance are less than 0 or maxBalance is less than minBalance");
        }
        int newId = 0;
        for (int i = 0; i < numberOfAccounts; i++) {
            long newBalance = new Random().nextLong(maxBalance - minBalance + 1) + minBalance;
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

    public long getAccountBalance(int accountId) {
        return accounts.get(accountId).getBalance();
    }

    public void setAccountBalance(int accountId, long newBalance) {
        if (accountId < 0 || accountId >= accounts.size()) {
            throw new IllegalArgumentException("accountId is out of range");
        }
        accounts.get(accountId).setBalance(newBalance);
    }

    public BigInteger getSumOfAllAccounts() {
        BigInteger result = new BigInteger(String.valueOf(0));
        for (int i = 0; i < accounts.size(); i++) {
            result = result.add(BigInteger.valueOf(accounts.get(i).getBalance()));
        }
        return result;
    }

    private Runnable transferFunds() {
        return new Runnable() {
            @Override
            public void run() {
                int to, from = BankSynchronized.this.pickRandomAccountId();
                do {
                    to = BankSynchronized.this.pickRandomAccountId();
                } while (to == from);
                synchronized (BankSynchronized.this) {
                    if (BankSynchronized.this.getAccountBalance(from) > 0) {
                        long x = new Random().nextLong(BankSynchronized.this.getAccountBalance(from) + 1);
                        transferFunds(from, to, x);
                    }
                }
            }

            private void transferFunds(int from, int to, long x) {
                // withdraw
                long balFrom = BankSynchronized.this.getAccountBalance(from) - x;
                BankSynchronized.this.setAccountBalance(from, balFrom);
                // deposit
                long balTo = BankSynchronized.this.getAccountBalance(to) + x;
                BankSynchronized.this.setAccountBalance(to, balTo);
            }
        };
    }

    private class Account {
        private int id;
        @Setter
        @Getter
        private long balance;

        public Account(int id, long balance) {
            this.id = id;
            this.balance = balance;
        }
    }
}
