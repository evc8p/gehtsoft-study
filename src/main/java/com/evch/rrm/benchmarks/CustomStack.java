package com.evch.rrm.benchmarks;

import com.evch.rrm.CustomLinkedList;

import java.util.Collection;
import java.util.EmptyStackException;

public class CustomStack<E> {
    CustomLinkedList<E> stack = new CustomLinkedList<>();

    public CustomStack() {
    }

    public CustomStack(Collection<? extends E> c) {
        c.forEach(this::push);
    }

    public E push(E item) {
        stack.addFirst(item);
        return item;
    }

    public E pop() {
        if (stack.isEmpty()) {
            throw new EmptyStackException();
        }
        return stack.pollFirst();
    }

    public E peek() {
        if (stack.isEmpty()) {
            throw new EmptyStackException();
        }
        return stack.peekFirst();
    }

    public boolean empty() {
        return stack.isEmpty();
    }

    public boolean isEmpty() {
        return stack.isEmpty();
    }

    public int search(Object o) {
        int index = stack.indexOf(o);
        return index == -1 ? -1 : index + 1;
    }

    public int size() {
        return stack.size();
    }
}
