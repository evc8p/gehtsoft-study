package com.evch.rrm;

import java.util.Collection;
import java.util.Iterator;
import java.util.Queue;

public class CustomQueue<E> extends CustomLinkedList<E> implements Queue<E> {
    public CustomQueue() {
    }

    public CustomQueue(Collection<? extends E> c) {
        addAll(c);
    }

    @Override
    public int size() {
        return super.size();
    }

    @Override
    public boolean isEmpty() {
        return super.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return super.contains(o);
    }

    @Override
    public Iterator<E> iterator() {
        return super.iterator();
    }

    @Override
    public Object[] toArray() {
        return super.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return super.toArray(a);
    }

    @Override
    public boolean add(E e) {
        return super.add(e);
    }

    @Override
    public boolean remove(Object o) {
        return super.remove(o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return super.containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        return super.addAll(c);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return super.removeAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return super.retainAll(c);
    }

    @Override
    public void clear() {
        super.clear();
    }

    @Override
    public boolean offer(E e) {
        return super.offer(e);
    }

    @Override
    public E remove() {
        return super.remove();
    }

    @Override
    public E poll() {
        return super.poll();
    }

    @Override
    public E element() {
        return super.element();
    }

    @Override
    public E peek() {
        return super.peek();
    }
}
