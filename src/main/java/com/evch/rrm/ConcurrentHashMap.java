package com.evch.rrm;

import java.util.*;

public class ConcurrentHashMap<K, V> implements Map<K, V> {
    private static final int DEFAULT_CAPACITY = 16;
    private static final float DEFAULT_LOAD_FACTOR = 0.75f;
    private int capacity;
    private float loadFactor;
    private int size = 0;
    private Node[] buckets;
    private int modCount = 0;
    protected Object monitorDuringChanges = new Object();
    private boolean isResizing = false;

    public ConcurrentHashMap() {
        this(DEFAULT_CAPACITY, DEFAULT_LOAD_FACTOR);
    }

    public ConcurrentHashMap(int capacity) {
        this(capacity, DEFAULT_LOAD_FACTOR);
    }

    public ConcurrentHashMap(int capacity, float loadFactor) {
        if (capacity < 0) {
            throw new IllegalArgumentException("Illegal initial capacity: " + capacity);
        }
        if (loadFactor <= 0 || Float.isNaN(loadFactor)) {
            throw new IllegalArgumentException("Illegal load factor: " + loadFactor);
        }
        this.capacity = capacity;
        this.loadFactor = loadFactor;
        this.buckets = new Node[capacity];
    }

    public ConcurrentHashMap(Map<? extends K, ? extends V> m) {
        this.capacity = Math.max(m.size(), DEFAULT_CAPACITY);
        this.loadFactor = DEFAULT_LOAD_FACTOR;
        this.buckets = new Node[this.capacity];
        putAll(m);
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
    public boolean containsKey(Object key) {
        int hashKey = hash(key);
        int index = hashKey & (capacity - 1);
        if (isEmpty() || buckets[index] == null) return false;
        for (Node<K, V> node = buckets[index]; node != null; node = node.next) {
            if (node.key == null && key == null || node.key != null && node.hash == hashKey && node.key.equals(key)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean containsValue(Object value) {
        if (!isEmpty()) {
            for (Node<K, V> node : buckets) {
                for (; node != null; node = node.next) {
                    if (value == null && node.value == null || value != null && value.equals(node.value)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public V get(Object key) {
        if (!isEmpty()) {
            int index = hash(key) & (capacity - 1);
            for (Node<K, V> node = buckets[index]; node != null; node = node.next) {
                if (key == null && node.key == null || key != null && key.equals(node.key)) {
                    return node.value;
                }
            }
        }
        return null;
    }

    @Override
    public V put(K key, V value) {
        synchronized (monitorDuringChanges) {
            if (((float) size) >= capacity * loadFactor) {
                resize();
            }
            V oldValue = null;
            int hashKey = hash(key);
            Node<K, V> newNode = newNode(hashKey, key, value, null);
            int index = hashKey & (capacity - 1);
            if (buckets[index] == null) {
                buckets[index] = newNode;
                size++;
            } else {
                for (Node<K, V> node = buckets[index]; node != null; node = node.next) {
                    if (node.key == null && key == null || node.key != null && node.hash == hashKey && node.key.equals(key)) {
                        oldValue = node.value;
                        node.value = value;
                        break;
                    }
                    if (node.next == null) {
                        node.next = newNode;
                        size++;
                        break;
                    }
                }
            }
            modCount++;
            return oldValue;
        }
    }

    private void resize() {
        isResizing = true;
        this.capacity *= 2;
        Node<K, V>[] oldBuckets = buckets;
        this.buckets = new Node[capacity];
        for (Node<K, V> oldNode : oldBuckets) {
            for (; oldNode != null; oldNode = oldNode.next) {
                int hashKey = hash(oldNode.key);
                Node<K, V> newNode = newNode(hashKey, oldNode.key, oldNode.value, null);
                int index = hashKey & (capacity - 1);
                if (buckets[index] == null) {
                    buckets[index] = newNode;
                } else {
                    for (Node<K, V> node = buckets[index]; node != null; node = node.next) {
                        if (node.key == null && oldNode.key == null || node.key != null && node.hash == hashKey && node.key.equals(oldNode.key)) {
                            node.value = oldNode.value;
                            break;
                        }
                        if (node.next == null) {
                            node.next = newNode;
                            break;
                        }
                    }
                }
            }
        }
        isResizing = false;
    }

    protected Node<K,V> newNode(int hash, K key, V value, Node<K,V> next) {
        synchronized (monitorDuringChanges) {
            Node<K, V> newNode = new Node<>(hash, key, value, next);
            return afterNodeCreate(newNode);
        }
    }

    protected Node<K,V> afterNodeCreate(Node<K, V> node) {
        synchronized (monitorDuringChanges) {
            return node;
        }
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        for (Map.Entry<? extends K, ? extends V> entry : m.entrySet()) {
            put(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public V remove(Object key) {
        synchronized (monitorDuringChanges) {
            V oldValue = null;
            if (!isEmpty()) {
                int index = hash(key) & (capacity - 1);
                Node<K, V> prevNode = null;
                for (Node<K, V> node = buckets[index]; node != null; node = node.next) {
                    if (key == null && node.key == null || key != null && key.equals(node.key)) {
                        oldValue = node.value;
                        if (node.next == null) {
                            buckets[index] = null;
                        } else {
                            if (prevNode == null) {
                                buckets[index] = node.next;
                            } else {
                                prevNode.next = node.next;
                            }
                        }
                        size--;
                        modCount++;
                        return oldValue;
                    }
                    prevNode = node;
                }
            }
            return null;
        }
    }

    @Override
    public void clear() {
        synchronized (monitorDuringChanges) {
            for (int i = 0; i < buckets.length; i++) {
                buckets[i] = null;
            }
            size = 0;
            modCount++;
        }
    }

    @Override
    public Set<K> keySet() {
        Set<K> keySet = new HashSet<>();
        if (!isEmpty()) {
            for (Node<K, V> node : buckets) {
                for (; node != null; node = node.next) {
                    keySet.add(node.key);
                }
            }
        }
        return keySet;
    }

    @Override
    public Collection<V> values() {
        List<V> values = new ArrayList<>(size);
        if (!isEmpty()) {
            for (Node<K, V> node : buckets) {
                for (; node != null; node = node.next) {
                    values.add(node.value);
                }
            }
        }
        return values;
    }

    @Override
    public Set<Map.Entry<K, V>> entrySet() {
        Set<Map.Entry<K, V>> entries = new HashSet<>(size);
        if (!isEmpty()) {
            for (Node<K, V> node : buckets) {
                for (; node != null; node = node.next) {
                    entries.add(new Entry<>(node.key, node.value));
                }
            }
        }
        return entries;
    }

    class Node<K, V> implements Map.Entry<K, V> {
        final int hash;
        final K key;
        V value;
        Node<K, V> next;

        Node(int hash, K key, V value, Node<K, V> next) {
            this.hash = hash;
            this.key = key;
            this.value = value;
            this.next = next;
        }

        public final K getKey() {
            return key;
        }

        public final V getValue() {
            return value;
        }

        public final String toString() {
            return key + "=" + value;
        }

        public final int hashCode() {
            return Objects.hashCode(key) ^ Objects.hashCode(value);
        }

        public final V setValue(V newValue) {
            synchronized (monitorDuringChanges) {
                V oldValue = value;
                value = newValue;
                return oldValue;
            }
        }

        public final boolean equals(Object o) {
            if (o == this)
                return true;

            return o instanceof Map.Entry<?, ?> e
                    && Objects.equals(key, e.getKey())
                    && Objects.equals(value, e.getValue());
        }
    }

    private int hash(Object key) {
        int h;
        return (key == null) ? 0 : (h = key.hashCode()) ^ (h >>> 16);
    }

    public final class Entry<K, V> implements Map.Entry<K, V> {
        K key;
        V value;

        public Entry(K key, V value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public K getKey() {
            return key;
        }

        @Override
        public V getValue() {
            return value;
        }

        @Override
        public V setValue(V value) {
            synchronized (monitorDuringChanges) {
                V oldValue = value;
                this.value = value;
                return oldValue;
            }
        }
    }
}
