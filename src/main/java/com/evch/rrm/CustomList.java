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
        return indexOf(o) > -1;
    }

    @Override
    public Iterator<E> iterator() {
        return listIterator();
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
        Objects.requireNonNull(c, "The collection must not be null ");
        final Collection objects = c;
        for (Object object : objects) {
            if (Objects.isNull(object)) {
                add(null);
            } else {
                add((E) object);
            }
        }
        return true;
    }

    @Override
    public boolean addAll(int index, Collection c) {
        Objects.checkIndex(index, capacity);
        Objects.nonNull(c);
        final Object[] objects = Arrays.copyOf(c.toArray(), c.size(), Object[].class);
        if (objects.length != 0) {
            ensureCapacity(size + objects.length);
            System.arraycopy(elements, index, elements, index + objects.length, size - index);
            System.arraycopy(objects, 0, elements, index, objects.length);
            size += objects.length;
            return true;
        }
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
        final Collection objects = c;
        for (Object object : objects) {
            if (indexOf(object) == -1) {
                return false;
            }
        }
        return true;
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
        final Object[] objects = elements;
        for (int i = size - 1; i >= 0; i--) {
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
    public ListIterator<E> listIterator() {
        return new ListItr();
    }

    @Override
    public ListIterator<E> listIterator(int index) {
        return new ListItr(index);
    }

    @Override
    public List<E> subList(int fromIndex, int toIndex) {
        Objects.checkFromToIndex(fromIndex, toIndex, capacity);
        return (List<E>) List.of(Arrays.copyOfRange(elements, fromIndex, toIndex));
    }

    private void ensureCapacity(int newCapacity) {
        if (newCapacity > capacity) {
            grow((int) (newCapacity * multiplier));
        }
    }

    private void grow(int newCapacity) {
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
    }

    private class ListItr implements ListIterator<E> {
        private int cursor = 0;

        public ListItr() {
        }

        ListItr(int index) {
            Objects.checkIndex(index, capacity);
            cursor = index;
        }

        @Override
        public boolean hasNext() {
            return cursor < size();
        }

        @Override
        public E next() {
            return get(cursor++);
        }

        @Override
        public boolean hasPrevious() {
            return cursor != 0;
        }

        @Override
        public E previous() {
            return get(--cursor);
        }

        @Override
        public int nextIndex() {
            return cursor;
        }

        @Override
        public int previousIndex() {
            return cursor - 1;
        }

        @Override
        public void remove() {
            if (hasPrevious()) {
                CustomList.this.remove(previousIndex());
                cursor--;
            }
        }

        @Override
        public void set(E e) {
            if (hasPrevious()) {
                CustomList.this.set(previousIndex(), e);
            }
        }

        @Override
        public void add(E e) {
            if (hasPrevious()) {
                CustomList.this.add(previousIndex(), e);
                cursor++;
            }
        }
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }

        if (Objects.isNull(o) || !(o instanceof List)) {
            return false;
        }

        List<?> list = (List<?>) o;
        if (size != list.size()) {
            return false;
        }
        for (int i = 0; i < size; i++) {
            if (Objects.isNull(elements[i])) {
                if (Objects.nonNull(list.get(i))) {
                    return false;
                }
            } else {
                if (Objects.isNull(list.get(i))) {
                    return false;
                } else {
                    if (!elements[i].equals(list.get(i))) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public int hashCode() {
        final Object[] e = Arrays.copyOfRange(elements, 0, size);
        int hashCode = 1;
        for (Object o : e) {
            hashCode += 31 * hashCode + (o == null ? 0 : o.hashCode());
        }
        return hashCode;
    }
}
