package com.evch.rrm;

import java.util.*;

public class CustomLinkedHashMap<K, V> extends CustomHashMap<K, V> {
    private Entry<K, V> head = null;
    private Entry<K, V> tail = null;
    private final boolean accessOrder;
    private boolean isAddingAtHead = false;

    public CustomLinkedHashMap() {
        super();
        accessOrder = false;
    }

    public CustomLinkedHashMap(int capacity) {
        super(capacity);
        accessOrder = false;
    }

    public CustomLinkedHashMap(int capacity, float loadFactor) {
        super(capacity, loadFactor);
        accessOrder = false;
    }

    public CustomLinkedHashMap(Map<? extends K, ? extends V> m) {
        super(m);
        accessOrder = false;
    }

    public CustomLinkedHashMap(int capacity, float loadFactor, boolean accessOrder) {
        super(capacity, loadFactor);
        this.accessOrder = accessOrder;
    }

    public V putFirst(K k, V v) {
        isAddingAtHead = true;
        V value = put(k, v);
        isAddingAtHead = false;
        return value;
    }

    public V putLast(K k, V v) {
        return put(k, v);
    }

    public V getFirst() {
        return get(head.key);
    }

    public V getLast() {
        return get(tail.key);
    }

    public Set<K> getLinkedKeySet() {
        Set<K> linkedKeys = new LinkedHashSet<>();
        for (Entry<K, V> entry = head; entry != null; entry = entry.next) {
            linkedKeys.add(entry.key);
        }
        return linkedKeys;
    }

    public Set<V> getLinkedValues() {
        Set<V> values = new LinkedHashSet<>();
        for (Entry<K, V> entry = head; entry != null; entry = entry.next) {
            values.add(entry.value);
        }
        return values;
    }

    public Set<Entry<K, V>> getLinkedEntries() {
        Set<Entry<K, V>> entries = new LinkedHashSet<>();
        for (Entry<K, V> entry = head; entry != null; entry = entry.next) {
            entries.add(entry);
        }
        return entries;
    }

    @Override
    public void clear() {
        super.clear();
        head = tail = null;
    }

    @Override
    public V remove(Object key) {
        V oldValue = super.remove(key);
        removeLinkedEntry(getLinkedEntry((K) key));
        return oldValue;
    }

    public V removeFirst() {
        V oldValue = super.remove(head.key);
        if (head != null) {
            head.value = null;
            if (head.next != null) {
                head.next.prev = null;
                head = head.next;
            } else {
                head = null;
            }
        }
        return oldValue;
    }

    private void removeLinkedEntry(Entry<K, V> entry) {
        if (entry.prev != null) {
            entry.prev.next = entry.next;
        } else {
            head = entry.next;
        }
        if (entry.next != null) {
            entry.next.prev = entry.prev;
        } else {
            tail = entry.prev;
        }
        entry.prev = null;
        entry.next = null;
        entry.value = null;
    }

    @Override
    protected Node<K,V> newNode(int hash, K key, V value, Node<K,V> next) {
        Node<K,V> newNode = new Entry<>(hash, key, value, next);
        return newNode;
    }

    @Override
    protected Node<K,V> afterNodePut(Node<K, V> node) {
        if (super.getIsAddedNode()) {
            if (isAddingAtHead) {
                linkNodeAtHead((Entry<K, V>) node);
            } else {
                linkNodeAtTail((Entry<K, V>) node);
            }
        } else {
            Entry<K, V> linkedEntry = getLinkedEntry(node.key);
            linkedEntry.value = node.value;
            if (accessOrder) {
                removeLinkedEntry(linkedEntry);
                linkNodeAtTail(linkedEntry);
            }
        }
        return node;
    }

    private void linkNodeAtTail(Entry<K,V> entry) {
        if (head == null) {
            head = entry;
            tail = entry;
            head.prev = null;
        } else {
            tail.next = entry;
            entry.prev = tail;
            tail = entry;
        }
    }

    private void linkNodeAtHead(Entry<K,V> entry) {
        if (head == null) {
            head = entry;
            tail = entry;
            head.prev = null;
        } else {
            head.prev = entry;
            entry.next = head;
            head = entry;
        }
    }

    public static class Entry<K, V> extends Node<K, V> {
        Entry<K, V> prev;
        Entry<K, V> next;

        Entry(int hash, K key, V value, Node<K, V> next) {
            super(hash, key, value, next);
        }
    }

    private Entry<K, V> getLinkedEntry(K key) {
        for (Entry<K, V> entry = head; entry != null; entry = entry.next) {
            if (key == null && entry.key == null || key != null && key.equals(entry.key)) {
                return entry;
            }
        }
        return null;
    }
}
