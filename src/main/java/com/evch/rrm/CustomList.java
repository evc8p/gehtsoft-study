package com.evch.rrm;

import java.util.*;

public class CustomList<E> implements List<E> {
    private static final int DEFAULT_CAPACITY = 10;
    private int capacity = DEFAULT_CAPACITY;
    private int size = 0;
    private float multiplier = 2.0f;
    private Object[] elements;

    public CustomList() {
        this.elements = new Object[DEFAULT_CAPACITY];
    }

    public CustomList(int initialCapacity) {
        if (initialCapacity < 0) {
            throw new IllegalArgumentException("Initial capacity is less than 0");
        }
        this.capacity = initialCapacity;
        this.elements = new Object[initialCapacity];
    }

    public CustomList(int initialCapacity, float multiplier) {
        if (initialCapacity < 0 || multiplier < 1.0f) {
            throw new IllegalArgumentException("Initial capacity is less than 0 or multiplier < 1");
        }
        this.capacity = initialCapacity;
        this.multiplier = multiplier;
        this.elements = new Object[initialCapacity];
    }

    public CustomList(E[] elements) {
        if (Objects.isNull(elements)) {
            throw new IllegalArgumentException("Array is null");
        }
        this.size = elements.length;
        if (size > DEFAULT_CAPACITY) {
            this.capacity = size;
            this.elements = elements;
        } else {
            this.elements = new Object[this.capacity];
            System.arraycopy(elements, 0, this.elements, 0, size);
        }
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public boolean contains(Object o) {
        return false;
    }

    @Override
    public Iterator iterator() {
        return null;
    }

    @Override
    public Object[] toArray() {
        return new Object[0];
    }

    @Override
    public Object[] toArray(Object[] a) {
        return new Object[0];
    }

    @Override
    public boolean add(E o) {
        ensureCapacity(size + 1);
        elements[size++] = o;
        return true;
    }

    @Override
    public boolean remove(Object o) {
        int index = indexOf(o);
        if (index > -1) {
            remove(index);
        }
        return true;
    }

    @Override
    public boolean addAll(Collection c) {
        return false;
    }

    @Override
    public boolean addAll(int index, Collection c) {
        return false;
    }

    @Override
    public boolean retainAll(Collection c) {
        return false;
    }

    @Override
    public boolean removeAll(Collection c) {
        return false;
    }

    @Override
    public boolean containsAll(Collection c) {
        return false;
    }

    @Override
    public void clear() {
        final Object[] removeElements = elements;
        for (int i = 0; i < size; i++) {
            removeElements[i] = null;
        }
        size = 0;
    }

    @Override
    public E get(int index) {
        Objects.checkIndex(index, size);
        return (E) elements[index];
    }

    @Override
    public E set(int index, E element) {
        Objects.checkIndex(index, size);
        Object previousElement = elements[index];
        elements[index] = element;
        return (E) previousElement;
    }

    @Override
    public void add(int index, E element) {
        Objects.checkIndex(index, capacity);
        ensureCapacity(size + 1);
        System.arraycopy(elements, index, elements, index + 1, size - index);
        elements[index] = element;
        size++;
    }

    @Override
    public E remove(int index) {
        Objects.checkIndex(index, size);
        Object removedElement = elements[index];
        if ((index + 1) < size) {
            System.arraycopy(elements, index + 1, elements, index, size - index - 1);
        }
        elements[size - 1] = null;
        size--;
        return (E) removedElement;
    }

    @Override
    public int indexOf(Object o) {
        final Object[] objects = elements;
        for (int i = 0; i < size; i++) {
            if (Objects.isNull(o)) {
                if (Objects.isNull(objects[i])) {
                    return i;
                }
            } else {
                if (o.equals(objects[i])) {
                    return i;
                }
            }
        }
        return -1;
    }

    @Override
    public int lastIndexOf(Object o) {
        return 0;
    }

    @Override
    public ListIterator listIterator() {
        return null;
    }

    @Override
    public ListIterator listIterator(int index) {
        return null;
    }

    @Override
    public List subList(int fromIndex, int toIndex) {
        return List.of();
    }

    private void ensureCapacity(int newCapacity) {
        if (newCapacity > capacity) {
            grow((int) (capacity * multiplier));
        }
    }

    private Object[] grow(int newCapacity) {
        if (newCapacity == capacity) {
            newCapacity++;
        }
        if (newCapacity < capacity) { // overflow has occurred
            throw new ArrayIndexOutOfBoundsException("Array index is out of bounds. The upper possible limit of the array has been exceeded ( > " + Integer.MAX_VALUE + ")");
        }
        Object[] newElements = new Object[newCapacity];
        System.arraycopy(elements, 0, newElements, 0, elements.length);
        elements = newElements;
        capacity = newCapacity;
        return elements;
    }
}
