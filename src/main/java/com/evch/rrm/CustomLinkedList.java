package com.evch.rrm;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

public class CustomLinkedList<E> implements List<E>, Deque<E> {
    private int size = 0;
    private Node<E> first = null;
    private Node<E> last = null;

    public CustomLinkedList() {
    }

    public CustomLinkedList(Collection<? extends E> c) {
        addAll(c);
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
    public boolean add(E e) {
        final Node<E> l = last;
        final Node<E> f = first;
        Node<E> newNode = new Node<>(l, e, null);
        if (f == null) {
            first = last = newNode;
        } else {
            last = l.next = newNode;
        }
        size++;
        return true;
    }

    @Override
    public boolean offer(E e) {
        return add(e);
    }

    @Override
    public E remove() {
        return removeFirst();
    }

    @Override
    public E poll() {
        final Node<E> f = first;
        return f == null ? null : removeFirst();
    }

    @Override
    public E element() {
        return getFirst();
    }

    @Override
    public E peek() {
        final Node<E> f = first;
        return (f == null) ? null : f.item;
    }

    @Override
    public void add(int index, E element) {
        checkPositionIndex(index);
        if (index == size) {
            add(element);
            return;
        }
        final Node<E> oldNode = getNode(index);
        Node<E> newNode = new Node<>(oldNode.prev, element, oldNode);
        oldNode.prev = newNode;
        if (newNode.prev == null) {
            first = newNode;
        } else {
            newNode.prev.next = newNode;
        }
        if (newNode.next == null) {
            last = newNode;
        }
        size++;
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        c.forEach(this::add);
        return false;
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
    public boolean addAll(int index, Collection<? extends E> c) {
        checkPositionIndex(index);
        Iterator<E> iterator = (Iterator<E>) c.iterator();
        for (int i = index; i < index + c.size(); i++) {
            add(i, iterator.next());
        }
        return true;
    }

    @Override
    public boolean remove(Object o) {
        Node<E> node = getNode(o);
        if (node == null) {
            return false;
        }
        if (node.prev == null) {
            first = node.next;
        } else {
            node.prev.next = node.next;
        }
        if (node.next == null) {
            last = node.prev;
        } else {
            node.next.prev = node.prev;
        }
        size--;
        return true;
    }

    @Override
    public E remove(int index) {
        Node<E> node = getNode(index);
        if (node == null) {
            return null;
        }
        E oldItem = node.item;
        if (node.prev == null) {
            first = node.next;
        } else {
            node.prev.next = node.next;
        }
        if (node.next == null) {
            last = node.prev;
        } else {
            node.next.prev = node.prev;
        }
        size--;
        return oldItem;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        for (Object o : c) {
            int index = indexOf(o);
            while (index > -1) {
                remove(index);
                index = indexOf(o);
            }
        }
        return true;
    }

    @Override
    public boolean removeIf(Predicate<? super E> filter) {
        return List.super.removeIf(filter);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        for (Node<E> node = first; node != null; node = node.next) {
            if (!c.contains(node.item)) {
                Node<E> prev = node.prev;
                remove(node.item);
                if (prev == null) {
                    node = first;
                } else {
                    node = prev;
                }
            }
        }
        return false;
    }

    @Override
    public void replaceAll(UnaryOperator<E> operator) {
        List.super.replaceAll(operator);
    }

    @Override
    public void sort(Comparator<? super E> c) {
        List.super.sort(c);
    }

    @Override
    public void clear() {
        for (Node<E> node = first; node != null; ) {
            Node<E> next = node.next;
            node.prev = null;
            node.item = null;
            node.next = null;
            node = next;
        }
        first = last = null;
        size = 0;
    }

    @Override
    public E get(int index) {
        checkPositionIndex(index);
        if (size == 0) {
            return null;
        }
        return getNode(index).item;
    }

    @Override
    public E set(int index, E element) {
        final Node<E> node = getNode(index);
        E oldElement = node.item;
        node.item = element;
        return oldElement;
    }

    @Override
    public boolean contains(Object o) {
        return indexOf(o) > -1;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        for (Object element : c) {
            if (indexOf(element) == -1) {
                return false;
            }
        }
        return true;
    }

    @Override
    public Object[] toArray() {
        Object[] array = new Object[size];
        int i = 0;
        for (Node<E> node = first; node != null; node = node.next) {
            array[i++] = node.item;
        }
        return array;
    }

    @Override
    public <T> T[] toArray(T[] a) {
        Object[] array = toArray();
        if (a.length <= size) {
            return (T[]) array;
        }
        System.arraycopy(array, 0, a, 0, size);
        Arrays.fill(a, size, a.length - 1, null);
        return a;
    }

    @Override
    public <T> T[] toArray(IntFunction<T[]> generator) {
        return List.super.toArray(generator);
    }

    @Override
    public int indexOf(Object o) {
        int index = 0;
        if (o == null) {
            for (Node<E> node = first; node != null; node = node.next) {
                if (node.item == null) {
                    return index;
                }
                index++;
            }
        } else {
            for (Node<E> node = first; node != null; node = node.next) {
                if (o.equals(node.item)) {
                    return index;
                }
                index++;
            }
        }
        return -1;
    }

    @Override
    public int lastIndexOf(Object o) {
        int index = size - 1;
        if (o == null) {
            for (Node<E> node = last; node != null; node = node.prev) {
                if (node.item == null) {
                    return index;
                }
                index--;
            }
        } else {
            for (Node<E> node = last; node != null; node = node.prev) {
                if (o.equals(node.item)) {
                    return index;
                }
                index--;
            }
        }
        return -1;
    }

    @Override
    public Iterator<E> iterator() {
        return listIterator();
    }

    @Override
    public void forEach(Consumer<? super E> action) {
        List.super.forEach(action);
    }

    @Override
    public Iterator<E> descendingIterator() {
        return new DescListItr();
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
        List<E> list = new ArrayList<>(toIndex - fromIndex);
        for (Node<E> node = getNode(indexOf(fromIndex)); node != getNode(indexOf(toIndex)); node = node.next) {
            list.add(node.item);
        }
        return list;
    }

    @Override
    public Spliterator<E> spliterator() {
        return List.super.spliterator();
    }

    @Override
    public Stream<E> stream() {
        return List.super.stream();
    }

    @Override
    public Stream<E> parallelStream() {
        return List.super.parallelStream();
    }

    @Override
    public void addFirst(E e) {
        add(0, e);
    }

    @Override
    public void addLast(E e) {
        add(size, e);
    }

    @Override
    public boolean offerFirst(E e) {
        addFirst(e);
        return true;
    }

    @Override
    public boolean offerLast(E e) {
        addLast(e);
        return true;
    }

    @Override
    public E getFirst() {
        if (first == null) {
            return null;
        }
        return first.item;
    }

    @Override
    public E getLast() {
        if (last == null) {
            return null;
        }
        return last.item;
    }

    @Override
    public E peekFirst() {
        final Node<E> f = first;
        return (f == null) ? null : f.item;
    }

    @Override
    public E peekLast() {
        final Node<E> l = last;
        return (l == null) ? null : l.item;
    }

    @Override
    public boolean removeFirstOccurrence(Object o) {
        return remove(o);
    }

    @Override
    public boolean removeLastOccurrence(Object o) {
        int index = lastIndexOf(o);
        if (index > -1) {
            remove(index);
            return true;
        }
        return false;
    }

    @Override
    public E removeFirst() {
        if (first == null) {
            return null;
        }
        E oldItem = first.item;
        remove(getFirst());
        return oldItem;
    }

    @Override
    public E removeLast() {
        if (last == null) {
            return null;
        }
        E oldItem = last.item;
        remove(getLast());
        return oldItem;
    }

    @Override
    public CustomLinkedList<E> reversed() {
        return new ReverseOrderCustomLinkedListView();
    }

    @Override
    public E pollFirst() {
        final Node<E> f = first;
        return (f == null) ? null : removeFirst();
    }

    @Override
    public E pollLast() {
        final Node<E> l = last;
        return (l == null) ? null : removeLast();
    }

    private static class Node<E> {
        E item;
        CustomLinkedList.Node<E> next;
        CustomLinkedList.Node<E> prev;

        Node(CustomLinkedList.Node<E> prev, E element, CustomLinkedList.Node<E> next) {
            this.item = element;
            this.next = next;
            this.prev = prev;
        }
    }

    private Node<E> getNode(int index) {
        checkElementIndex(index);
        Node<E> node = first;
        for (int i = 0; i < index; i++) {
            node = node.next;
        }
        return node;
    }

    private Node<E> getNode(Object o) {
        if (o == null) {
            for (Node<E> node = first; node != null; node = node.next) {
                if (node.item == null) {
                    return node;
                }
            }
        } else {
            for (Node<E> node = first; node != null; node = node.next) {
                if (o.equals(node.item)) {
                    return node;
                }
            }
        }
        return null;
    }

    private void checkElementIndex(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("The index is out of range " + index);
        }
    }

    private void checkPositionIndex(int index) {
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException("The index is out of range " + index);
        }
    }

    private class ListItr implements ListIterator<E> {
        private int cursor = 0;

        public ListItr() {
        }

        public ListItr(int index) {
            checkElementIndex(index);
            this.cursor = index;
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
            return cursor > 0;
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
                CustomLinkedList.this.remove(previousIndex());
                cursor--;
            }
        }

        @Override
        public void set(E e) {
            if (hasPrevious()) {
                CustomLinkedList.this.set(previousIndex(), e);
            }
        }

        @Override
        public void add(E e) {
            if (hasPrevious()) {
                CustomLinkedList.this.add(previousIndex(), e);
                cursor++;
            }
        }
    }

    private class DescListItr implements ListIterator<E> {
        private int cursor = size - 1;

        public DescListItr() {
        }

        public DescListItr(int index) {
            checkElementIndex(index);
            this.cursor = index;
        }

        @Override
        public boolean hasNext() {
            return cursor > -1;
        }

        @Override
        public E next() {
            return get(cursor--);
        }

        @Override
        public boolean hasPrevious() {
            return cursor < size - 1;
        }

        @Override
        public E previous() {
            return get(++cursor);
        }

        @Override
        public int nextIndex() {
            return cursor;
        }

        @Override
        public int previousIndex() {
            return cursor + 1;
        }

        @Override
        public void remove() {
            if (hasPrevious()) {
                CustomLinkedList.this.remove(previousIndex());
                cursor++;
            }
        }

        @Override
        public void set(E e) {
            if (hasPrevious()) {
                CustomLinkedList.this.set(previousIndex(), e);
            }
        }

        @Override
        public void add(E e) {
            if (hasPrevious()) {
                CustomLinkedList.this.add(previousIndex(), e);
                cursor--;
            }
        }
    }

    // Stub class. Only the list is returned in reverse order
    class ReverseOrderCustomLinkedListView extends CustomLinkedList {
        public CustomLinkedList<E> getReversedList() {
            CustomLinkedList<E> reversedList = new CustomLinkedList<>();
            for (Node<E> node = last; node != null; node = node.prev) {
                reversedList.addFirst(node.item);
            }
            return reversedList;
        }
    }
}
