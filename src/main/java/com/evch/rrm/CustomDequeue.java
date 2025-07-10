package com.evch.rrm;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class CustomDequeue<E> implements Deque<E> {
    CustomList<E> dequeue = new CustomList<>();

    public CustomDequeue() {
    }

    public CustomDequeue(Collection<? extends E> c) {
        addAll(c);
    }

    @Override
    public void addFirst(E e) {
        dequeue.add(0, e);
    }

    @Override
    public void addLast(E e) {
        dequeue.add(size() - 1, e);
    }

    @Override
    public boolean offerFirst(E e) {
        dequeue.add(0, e);
        return true;
    }

    @Override
    public boolean offerLast(E e) {
        dequeue.add(size() - 1, e);
        return true;
    }

    @Override
    public E removeFirst() {
        if (size() == 0) {
            throw new NoSuchElementException();
        }
        return dequeue.remove(0);
    }

    @Override
    public E removeLast() {
        if (size() == 0) {
            throw new NoSuchElementException();
        }
        return dequeue.remove(size() - 1);
    }

    @Override
    public E pollFirst() {
        if (isEmpty()) {
            return null;
        }
        return dequeue.remove(0);
    }

    @Override
    public E pollLast() {
        if (isEmpty()) {
            return null;
        }
        return dequeue.remove(size() - 1);
    }

    @Override
    public E getFirst() {
        if (size() == 0) {
            throw new NoSuchElementException();
        }
        return dequeue.get(0);
    }

    @Override
    public E getLast() {
        if (size() == 0) {
            throw new NoSuchElementException();
        }
        return dequeue.get(size() - 1);
    }

    @Override
    public E peekFirst() {
        if (isEmpty()) {
            return null;
        }
        return dequeue.get(0);
    }

    @Override
    public E peekLast() {
        if (isEmpty()) {
            return null;
        }
        return dequeue.get(size() - 1);
    }

    @Override
    public boolean removeFirstOccurrence(Object o) {
        return dequeue.remove(o);
    }

    @Override
    public boolean removeLastOccurrence(Object o) {
        int index = dequeue.lastIndexOf((E) o);
        if (index == -1) {
            return false;
        }
        dequeue.remove(index);
        return true;
    }

    @Override
    public boolean add(Object o) {
        return dequeue.add((E) o);
    }

    @Override
    public boolean offer(Object o) {
        return dequeue.add((E) o);
    }

    @Override
    public E remove() {
        return dequeue.remove(0);
    }

    @Override
    public E poll() {
        if (isEmpty()) {
            return null;
        }
        return dequeue.remove(0);
    }

    @Override
    public E element() {
        return getFirst();
    }

    @Override
    public E peek() {
        if (isEmpty()) {
            return null;
        }
        return dequeue.get(0);
    }

    @Override
    public boolean addAll(Collection c) {
        return dequeue.addAll(c);
    }

    @Override
    public boolean removeIf(Predicate filter) {
        return Deque.super.removeIf(filter);
    }

    @Override
    public void clear() {
        dequeue.clear();
    }

    @Override
    public Spliterator spliterator() {
        return Deque.super.spliterator();
    }

    @Override
    public Stream stream() {
        return Deque.super.stream();
    }

    @Override
    public Stream parallelStream() {
        return Deque.super.parallelStream();
    }

    @Override
    public boolean retainAll(Collection c) {
        return dequeue.retainAll(c);
    }

    @Override
    public boolean removeAll(Collection c) {
        return dequeue.removeAll(c);
    }

    @Override
    public void push(E e) {
        addFirst(e);
    }

    @Override
    public E pop() {
        return removeFirst();
    }

    @Override
    public boolean remove(Object o) {
        return dequeue.remove(o);
    }

    @Override
    public boolean containsAll(Collection c) {
        for (Object element : c) {
            if (!dequeue.contains(element)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean contains(Object o) {
        return dequeue.contains(o);
    }

    @Override
    public int size() {
        return dequeue.size();
    }

    @Override
    public boolean isEmpty() {
        return dequeue.size() > 0;
    }

    @Override
    public Iterator iterator() {
        return null;
    }

    @Override
    public void forEach(Consumer action) {
        Deque.super.forEach(action);
    }

    @Override
    public Object[] toArray() {
        return dequeue.toArray();
    }

    @Override
    public Object[] toArray(IntFunction generator) {
        return Deque.super.toArray(generator);
    }

    @Override
    public Object[] toArray(Object[] a) {
        return dequeue.toArray(a);
    }

    @Override
    public Iterator descendingIterator() {
        return null;
    }

    @Override
    public Deque reversed() {
        return Deque.super.reversed();
    }
}
