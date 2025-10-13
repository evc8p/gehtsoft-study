package com.evch.rrm.decorators;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class SynchronizedListDecorator<E> implements List<E> {
    private final List<E> decoratedList;

    public SynchronizedListDecorator(List<E> listToDecorate) {
        this.decoratedList = listToDecorate;
    }

    @Override
    public int size() {
        return decoratedList.size();
    }

    @Override
    public boolean isEmpty() {
        return decoratedList.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return decoratedList.contains(o);
    }

    @Override
    public Iterator<E> iterator() {
        return decoratedList.iterator();
    }

    @Override
    public Object[] toArray() {
        return decoratedList.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return decoratedList.toArray(a);
    }

    @Override
    public boolean add(E e) {
        synchronized (decoratedList) {
            return decoratedList.add(e);
        }
    }

    @Override
    public boolean remove(Object o) {
        synchronized (decoratedList) {
            return decoratedList.remove(o);
        }
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return decoratedList.containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        synchronized (decoratedList) {
            return decoratedList.addAll(c);
        }
    }

    @Override
    public boolean addAll(int index, Collection<? extends E> c) {
        synchronized (decoratedList) {
            return decoratedList.addAll(index, c);
        }
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        synchronized (decoratedList) {
            return decoratedList.removeAll(c);
        }
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        synchronized (decoratedList) {
            return decoratedList.retainAll(c);
        }
    }

    @Override
    public void clear() {
        synchronized (decoratedList) {
            decoratedList.clear();
        }
    }

    @Override
    public E get(int index) {
        return decoratedList.get(index);
    }

    @Override
    public E set(int index, E element) {
        synchronized (decoratedList) {
            return decoratedList.set(index, element);
        }
    }

    @Override
    public void add(int index, E element) {
        synchronized (decoratedList) {
            decoratedList.add(index, element);
        }
    }

    @Override
    public E remove(int index) {
        synchronized (decoratedList) {
            return decoratedList.remove(index);
        }
    }

    @Override
    public int indexOf(Object o) {
        return decoratedList.indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return decoratedList.lastIndexOf(o);
    }

    @Override
    public ListIterator<E> listIterator() {
        synchronized (decoratedList) {
            return decoratedList.listIterator();
        }
    }

    @Override
    public ListIterator<E> listIterator(int index) {
        synchronized (decoratedList) {
            return decoratedList.listIterator(index);
        }
    }

    @Override
    public List<E> subList(int fromIndex, int toIndex) {
        return decoratedList.subList(fromIndex, toIndex);
    }

    @Override
    public boolean equals(Object o) {
        return decoratedList.equals(o);
    }

    @Override
    public int hashCode() {
        return decoratedList.hashCode();
    }
}
