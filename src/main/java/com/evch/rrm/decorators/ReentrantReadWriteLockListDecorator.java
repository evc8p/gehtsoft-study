package com.evch.rrm.decorators;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ReentrantReadWriteLockListDecorator<E> implements List<E> {
    private final List<E> decoratedList;
    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    private final Lock readLock = readWriteLock.readLock();
    private final Lock writeLock = readWriteLock.writeLock();

    public ReentrantReadWriteLockListDecorator(List<E> listToDecorate) {
        this.decoratedList = listToDecorate;
    }

    @Override
    public int size() {
        readLock.lock();
        try {
            return decoratedList.size();
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public boolean isEmpty() {
        readLock.lock();
        try {
            return decoratedList.isEmpty();
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public boolean contains(Object o) {
        readLock.lock();
        try {
            return decoratedList.contains(o);
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public Iterator<E> iterator() {
        readLock.lock();
        try {
            return decoratedList.iterator();
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public Object[] toArray() {
        readLock.lock();
        try {
            return decoratedList.toArray();
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public <T> T[] toArray(T[] a) {
        readLock.lock();
        try {
            return decoratedList.toArray(a);
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public boolean add(E e) {
        writeLock.lock();
        try {
            return decoratedList.add(e);
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public boolean remove(Object o) {
        writeLock.lock();
        try {
            return decoratedList.remove(o);
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        readLock.lock();
        try {
            return decoratedList.containsAll(c);
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        writeLock.lock();
        try {
            return decoratedList.addAll(c);
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public boolean addAll(int index, Collection<? extends E> c) {
        writeLock.lock();
        try {
            return decoratedList.addAll(index, c);
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        writeLock.lock();
        try {
            return decoratedList.removeAll(c);
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        writeLock.lock();
        try {
            return decoratedList.retainAll(c);
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public void clear() {
        writeLock.lock();
        try {
            decoratedList.clear();
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public E get(int index) {
        readLock.lock();
        try {
            return decoratedList.get(index);
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public E set(int index, E element) {
        writeLock.lock();
        try {
            return decoratedList.set(index, element);
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public void add(int index, E element) {
        writeLock.lock();
        try {
            decoratedList.add(index, element);
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public E remove(int index) {
        writeLock.lock();
        try {
            return decoratedList.remove(index);
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public int indexOf(Object o) {
        readLock.lock();
        try {
            return decoratedList.indexOf(o);
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public int lastIndexOf(Object o) {
        readLock.lock();
        try {
            return decoratedList.lastIndexOf(o);
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public ListIterator<E> listIterator() {
        writeLock.lock();
        try {
            return decoratedList.listIterator();
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public ListIterator<E> listIterator(int index) {
        writeLock.lock();
        try {
            return decoratedList.listIterator(index);
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public List<E> subList(int fromIndex, int toIndex) {
        readLock.lock();
        try {
            return decoratedList.subList(fromIndex, toIndex);
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public boolean equals(Object o) {
        readLock.lock();
        try {
            return decoratedList.equals(o);
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public int hashCode() {
        readLock.lock();
        try {
            return decoratedList.hashCode();
        } finally {
            readLock.unlock();
        }
    }
}
