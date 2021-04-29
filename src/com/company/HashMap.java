package com.company;

import java.util.*;
import java.util.function.BiConsumer;
import static java.util.Map.Entry;

public class HashMap<K, V> {
    static class Node<K, V> implements Map.Entry<K, V> {
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

        public K getKey() {
            return key;
        }

        public V getValue() {
            return value;
        }

        public V setValue(V newValue) {
            V oldValue = value;
            value = newValue;
            return oldValue;
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            } else if (o instanceof Node) {
                Node<?, ?> node = (Node<?, ?>) o;
                return eq(key, node.key) && eq(value, node.value);
            }
            return false;
        }

        public int hashCode() {
            return h(key) ^ h(value);
        }

        public String toString() {
            return key + "=" + value;
        }
    }

    private static int h(Object o) {
        return o == null ? 0 : o.hashCode();
    }
    private static boolean eq(Object o1, Object o2) {
        return o1 == null ? o2 == null : o1.equals(o2);
    }

    private static final int DEFAULT_CAPACITY = 16;
    private static final float DEFAULT_LOAD_FACTOR = 0.75F;

    private final float loadFactor;
    private int capacity;

    private Node<K, V>[] buckets;
    private int size = 0;

    public HashMap() {
        this(DEFAULT_CAPACITY, DEFAULT_LOAD_FACTOR);
    }
    public HashMap(int initialCapacity) {
        this(initialCapacity, DEFAULT_LOAD_FACTOR);
    }
    @SuppressWarnings("unchecked")
    public HashMap(int countBuckets, float loadFactor) {
        if (countBuckets < 0)
            throw new IllegalArgumentException("Illegal capacity: " + countBuckets);
        if (loadFactor <= 0)
            throw new IllegalArgumentException("Illegal load factor: " + loadFactor);
        this.loadFactor = loadFactor;
        this.buckets = (Node<K, V>[]) new Node[countBuckets];
        this.capacity = calculateCapacity(countBuckets);
    }

    private int calculateCapacity(int numBuckets) {
        return (int) (numBuckets * loadFactor);
    }
    private int indexOf(int hash, int nemBuckets) {
        return hash % nemBuckets;
    }
    private static int hash(Object o) {
        int h;
        return o == null ? 0 : ((h = o.hashCode()) ^ (h >> 16));
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    private Node<K, V> getNode(Object key) {
        int index = indexOf(hash(key), buckets.length);
        Node<K, V> node = buckets[index];
        if (key == null) {
            while (node != null) {
                if (null == node.key)
                    return node;
                node = node.next;
            }
        } else {
            while (node != null) {
                if (key.equals(node.key))
                    return node;
                node = node.next;
            }
        }
        return null;
    }

    public boolean containsKey(Object key) {
        return getNode(key) != null;
    }

    public boolean containsValue(Object value) {
        if (value == null) {
            for (Node<K, V> node : buckets) {
                while (node != null) {
                    if (null == node.value)
                        return true;
                    node = node.next;
                }
            }
        } else {
            for (Node<K, V> node : buckets) {
                while (node != null) {
                    if (value.equals(node.value))
                        return true;
                    node = node.next;
                }
            }
        }
        return false;
    }

    public V get(Object key) {
        Node<K, V> node = getNode(key);
        return node == null ? null : node.value;
    }

    public V put(K key, V value) {
        ensureCapacity();
        final int hash = hash(key);
        final int index = indexOf(hash, buckets.length);
        Node<K, V> node = buckets[index];
        if (key == null) {
            while (node != null) {
                if (null == node.key)
                    return node.setValue(value);
                node = node.next;
            }
        } else {
            while (node != null) {
                if (key.equals(node.key))
                    return node.setValue(value);
                node = node.next;
            }
        }
        Node<K, V> next = buckets[index];
        Node<K, V> newNode = new Node<>(hash, key, value, next);
        buckets[index] = newNode;
        size++;
        return null;
    }
    private void ensureCapacity() {
        int c = capacity;
        if (size >= c) {
            int jump = c < 64 ? c + 2 : c >> 1;
            int newSize = buckets.length + jump;

            @SuppressWarnings("unchecked")
            final Node<K, V>[] nodes = (Node<K, V>[]) new Node[newSize];
            this.capacity = calculateCapacity(newSize);

            //copy elements
            Node<K, V> next;
            for (Node<K, V> node : buckets)
                while (node != null) {
                    next = node.next;
                    int index = indexOf(node.hash, newSize);
                    node.next = nodes[index];
                    nodes[index] = node;
                    node = next;
                }
            buckets = nodes;
        }
    }

    public V remove(Object key) {
        int index = indexOf(hash(key), buckets.length);
        Node<K, V> prev = null;
        Node<K, V> node = buckets[index];
        if (key == null) {
            while (node != null) {
                if (null == node.key)
                    return removeNode(index, prev, node);
                prev = node;
                node = node.next;
            }
        } else {
            while (node != null) {
                if (key.equals(node.key))
                    return removeNode(index, prev, node);
                prev = node;
                node = node.next;
            }
        }
        return null;
    }
    private V removeNode(int index, Node<K, V> prev, Node<K, V> removed) {
        if (prev == null) {
            buckets[index] = null;
        } else {
            prev.next = removed.next;
        }
        removed.next = null;
        return removed.setValue(null);
    }

    public void clear() {
        Node<K, V> node, next;
        for (int i = 0; i < buckets.length; i++) {
            node = buckets[i];
            buckets[i] = null;
            while (node != null) {
                next = node.next;

                node.next = null;
                node.value = null;
                node = next;
            }
        }
        size = 0;
    }

    public void putAll(java.util.Map<? extends K, ? extends V> m) {
        for (Entry<? extends K, ? extends V> entry : m.entrySet())
            put(entry.getKey(), entry.getValue());
    }

    public void forEach(BiConsumer<? super K, ? super V> consumer) {
        for (Node<K, V> temp : buckets) {
            while (temp != null) {
                consumer.accept(temp.key, temp.value);
                temp = temp.next;
            }
        }
    }

    public Set<Entry<K, V>> entrySet() {
        return new EntrySet();
    }

    class EntrySet extends AbstractSet<Entry<K, V>> {
        public int size() {
            return size;
        }

        public Iterator<Entry<K, V>> iterator() {
            return new EntryIterator();
        }

        class EntryIterator implements Iterator<Entry<K, V>> {
            Node<K, V> next;
            Node<K, V> lastRet;
            int i = 0;

            EntryIterator() {
                setNext();
            }

            private void setNext() {
                if (next == null) {
                    searchInArray();
                } else {
                    next = next.next;
                    if (next == null)
                        searchInArray();
                }
            }

            private void searchInArray() {
                while (i < buckets.length) {
                    Node<K, V> node = buckets[i++];
                    if (node != null) {
                        next = node;
                        break;
                    }
                }
            }

            public boolean hasNext() {
                return next != null;
            }

            public Entry<K, V> next() {
                if (next == null)
                    throw new NoSuchElementException();
                lastRet = next;
                setNext();
                return lastRet;
            }
        }
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o instanceof HashMap) {
            HashMap<?, ?> hashMap = (HashMap<?, ?>) o;
            if (size == hashMap.size) {
                for (Entry<K, V> e : entrySet())
                    if (!e.equals(hashMap.getNode(e.getKey())))
                        return false;
                return true;
            }
        }
        return false;
    }

    public int hashCode() {
        long bits = size + 17;
        for (Entry<K, V> kvEntry : entrySet())
            bits = bits * 32 + h(kvEntry);
        return (int) (bits ^ (bits >> 32));
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append('[');
        forEach((k, v) -> stringBuilder.append('[').append(k).append('-').append('>').append(v).append(']'));
        return stringBuilder.append(']').toString();
    }
}