package com.example.lexcorp.tableview.model;

import android.support.annotation.NonNull;

import java.util.AbstractCollection;
import java.util.AbstractSet;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiConsumer;

public class HashTable<K, V> implements Map<K, V>{
    private final int DEFAULT_CAPACITY = 3;
    private final int MAXIMUM_CAPACITY = 16;
    private final float LOAD_FACTOR = 0.75f;
    private final float loadFactor;
    private volatile int capacity;
    private volatile int size = 0;
    private SecondHash<K> hash;
    private SecondHash<K> defaultHash = Object::hashCode;

    private volatile Node[] hashTable;
    private volatile boolean[] deleted;

    Node[] getNodes(){
        return hashTable;
    }

    boolean[] getDeleted(){
        return deleted;
    }

    @SuppressWarnings({"WeakerAccess", "unused"})
    public HashTable() {
        loadFactor = LOAD_FACTOR;
        this.capacity = DEFAULT_CAPACITY;

        //noinspection unchecked
        hashTable = new HashTable.Node[capacity];
        entrySet = new EntrySet();
        deleted = new boolean[capacity];
        this.hash = defaultHash;
    }

    @SuppressWarnings("WeakerAccess")
    public HashTable(SecondHash<K> hash) {
        loadFactor = LOAD_FACTOR;
        this.capacity = DEFAULT_CAPACITY;
        //noinspection unchecked
        hashTable = new HashTable.Node[capacity];
        entrySet = new EntrySet();
        deleted = new boolean[capacity];
        this.hash = hash;
    }

    @SuppressWarnings("unused")
    public HashTable(int capacity) {
        loadFactor = LOAD_FACTOR;
        if (capacity >= DEFAULT_CAPACITY  && capacity <= MAXIMUM_CAPACITY) {
            this.capacity = capacity;
        } else {
            this.capacity = DEFAULT_CAPACITY;
        }
        //noinspection unchecked
        this.hashTable = new HashTable.Node[this.capacity];
        deleted = new boolean[this.capacity];
        this.hash = defaultHash;
    }

    @SuppressWarnings("unused")
    public HashTable(int capacity, SecondHash<K> hash) {
        loadFactor = LOAD_FACTOR;
        if (capacity >= DEFAULT_CAPACITY  && capacity <= MAXIMUM_CAPACITY) {
            this.capacity = capacity;
        } else {
            this.capacity = DEFAULT_CAPACITY;
        }
        //noinspection unchecked
        hashTable = new HashTable.Node[this.capacity];
        deleted = new boolean[this.capacity];
        this.hash = hash;
    }

    @SuppressWarnings("unused")
    public HashTable(float loadFactor, int capacity, SecondHash<K> hash) {
        if (loadFactor <= LOAD_FACTOR) {
            this.loadFactor = loadFactor;
        } else {
            this.loadFactor = LOAD_FACTOR;
        }

        if (capacity >= DEFAULT_CAPACITY  && capacity <= MAXIMUM_CAPACITY) {
            this.capacity = capacity;
        } else {
            this.capacity = DEFAULT_CAPACITY;
        }
        deleted = new boolean[this.capacity];
        //noinspection unchecked
        hashTable = new HashTable.Node[this.capacity];
        this.hash = hash;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

     public synchronized int contains(Object key) {
        int n = -1;
        @SuppressWarnings("unchecked") int hash1 = hashCode1((K)key);
        @SuppressWarnings("unchecked") int hash2 = hashCode2((K)key);

        while (n != capacity - 1) {
            n++;
            int index = (hash1 + n * hash2) % (capacity - 1);
            Map.Entry<K, V> node = hashTable[index];
            if (node != null && node.getKey().equals(key)) {
                if (!deleted[index]) {
                    return -1;
                } else {
                    return index;
                }
            }
        }
        return -1;
    }

    @Override
    public synchronized boolean containsKey(Object key) {
        if (key == null) {
            throw new NullPointerException();
        }
        return contains(key) >= 0;
    }

    @Override
    public synchronized boolean containsValue(Object value) {
        if (value == null) {
            throw new NullPointerException();
        }
        for (int i = 0; i < capacity; i++) {
            if (deleted[i]) {
                if (value.equals(hashTable[i].getValue())) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public synchronized V get(Object key) {
        int index = contains(key);
        if (index < 0) {
            return null;
        }
        return hashTable[index].value;
    }

    @Override
    public synchronized V put(@NonNull K key, @NonNull V value) {
        if (contains(key) >= 0) {
            return null;
        }

        float k = (float) ++size / capacity;
        if (k >= loadFactor) {
            rehash();
        }

        int index = getFreeIndex(key);
        Node newNode = new Node(key, value);
        hashTable[index] = newNode;
        deleted[index] = true;

        return value;
    }

    @Override
    public synchronized V remove(Object key) {
        int index = contains(key);

        if (index < 0) {
            return null;
        }
        size--;

        deleted[index] = false;
        return hashTable[index].getValue();
    }

    @Override
    public synchronized void putAll(@NonNull Map<? extends K, ? extends V> m) {
        for (Map.Entry<? extends K, ? extends V> elem : m.entrySet()) {
            put(elem.getKey(), elem.getValue());
        }
    }

    @Override
    public synchronized void clear() {
        for (int i = 0; i < capacity; i++) {
            hashTable[i] = null;
        }
        size = 0;
    }

    @Override
    public synchronized void forEach(@NonNull BiConsumer<? super K, ? super V> action) {
        Objects.requireNonNull(action);
        Iterator<Entry<K, V>> iterator = entrySet.iterator();

        for (; iterator.hasNext(); ) {
            Map.Entry<K, V> node = iterator.next();
            action.accept(node.getKey(), node.getValue());
        }
    }

    private synchronized int getFreeIndex(K key) {
        int n = -1;
        System.out.println(key);

        while (true) {
            n++;
            int index = (hashCode1(key) + n * hashCode2(key)) % (capacity - 1);
            if (index >= 0 && !deleted[index]) {
                return index;
            }
        }
    }

    private int hashCode1(K key) {
        return Math.abs(key.hashCode());
    }

    private int hashCode2(K key) {
        return 1 + Math.abs(hash.hashCode2(key)) % (capacity - 2);
    }

    @Override
    public synchronized boolean replace(K key, V oldValue, V newValue) {
        int index = contains(key);
        if (index < 0 || !hashTable[index].getValue().equals(oldValue)) {
            return false;
        }
        hashTable[index].setValue(newValue);
        return true;
    }

    @Override
    public synchronized V replace(@NonNull K key, @NonNull V value) {
        int index = contains(key);
        if (index < 0) {
            return null;
        }
        V old = hashTable[index].getValue();
        hashTable[index].setValue(value);
        return old;
    }

    private synchronized void rehash() {
        int old = capacity;
        capacity *= 2;
        HashTable<K, V>.Node[] between = Arrays.copyOf(hashTable, old);

        //noinspection unchecked
        hashTable = new HashTable.Node[capacity];
        deleted = new boolean[capacity];
        for (int i = 0; i < old; i++) {
            if (between[i] != null) {
                int index = getFreeIndex(between[i].getKey());
                hashTable[index] = between[i];
                deleted[index] = true;
            }
        }
    }

    @NonNull
    @Override
    public String toString() {
        return Arrays.toString(hashTable);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Map)) {
            return false;
        }
        if (((Map) obj).size() != this.size){
            return false;
        }
        Iterator iterator = ((Map) obj).entrySet().iterator();
        for (; iterator.hasNext(); ) {
            @SuppressWarnings("unchecked") Map.Entry<K, V> elem = (Map.Entry<K, V>)iterator.next();
            K key = elem.getKey();
            V value = elem.getValue();
            if (!containsKey(key) || get(key) != value) {
                return false;
            }
        }
        return true;
    }


    @Override
    public int hashCode() {
        int hash = 0;
        Iterator<Map.Entry<K, V>> iterator = entrySet.iterator();
        for (; iterator.hasNext(); ) {
            hash += iterator.next().hashCode();
        }
        return hash;
    }


    enum Type {ENTRY ,KEYS, VALUES}

    private Set<Map.Entry<K, V>> entrySet = new EntrySet();
    private Set<K> keySet = new KeySet();
    private Collection<V> values = new ValueCollection();

    private <T> java.util.Iterator<T> getIterator(Type type) {
        if (size == 0) {
            return Collections.emptyIterator();
        } else {
            return new MyIterator<>(type);
        }
    }

    @NonNull
    @Override
    public Collection<V> values() {
        return values;
    }

    private class ValueCollection extends AbstractCollection<V> {
        @NonNull
        public Iterator<V> iterator() {
            return getIterator(Type.VALUES);
        }

        public int size() {
            return size;
        }

        public boolean contains(Object o) {
            return containsValue(o);
        }

        public void clear() {
            HashTable.this.clear();
        }
    }

    @NonNull
    @Override
    public Set<K> keySet() {
        return keySet;
    }

    private class KeySet extends AbstractSet<K> {
        @NonNull
        public java.util.Iterator<K> iterator() {
            return getIterator(Type.KEYS);
        }

        public int size() {
            return size;
        }

        public boolean contains(Object o) {
            return containsKey(o);
        }

        public boolean remove(Object o) {
            return HashTable.this.remove(o) != null;
        }

        public void clear() {
            HashTable.this.clear();
        }
    }

    ////////////////////////////////////////////////////////////////////////
    @NonNull
    @Override
    public Set<Map.Entry<K, V>> entrySet() {
        return entrySet;
    }

    class EntrySet extends AbstractSet<Map.Entry<K, V>> {
        @NonNull
        public Iterator<Map.Entry<K, V>> iterator() {
            return getIterator(Type.ENTRY);
        }

        public boolean add(Map.Entry<K, V> o) {
            return super.add(o);
        }

        public boolean contains(Object o) {
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            Map.Entry<?, ?> entry = (Map.Entry<?, ?>) o;
            Object key = entry.getKey();
            return HashTable.this.containsKey(key);
        }

        public boolean remove(Object o) {
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            Map.Entry<?, ?> entry = (Map.Entry<?, ?>) o;
            Object key = entry.getKey();
            return HashTable.this.remove(key) != null;
        }

        public void clear() {
            HashTable.this.clear();
        }

        @Override
        public int size() {
            return size;
        }
    }

    ///////////////////////////////////////////////////////////////////////
    class MyIterator<T> implements java.util.Iterator<T> {
        Node[] table = HashTable.this.hashTable;
        boolean[] del = HashTable.this.deleted;
        Type type;
        int index = -1;
        int count = 0;
        int size1 = HashTable.this.size;

        MyIterator(Type type) {
            this.type = type;
        }

        @Override
        public boolean hasNext() {
            return count < size1;
        }

        //одправить
        @Override
        public T next() {
            index++;
            while (index < capacity) {
                if (del[index]) {
                    Map.Entry node = table[index];
                    count++;
                    //noinspection unchecked
                    return type == Type.KEYS ? (T) node.getKey() : (type == Type.VALUES ? (T) node.getValue() : (T) node);
                } else {
                    index++;
                }
            }
            return null;
        }
    }

    class Node implements Map.Entry<K, V> {
        private K key;
        private V value;

        public K getKey() {
            return key;
        }

        public V getValue() {
            return value;
        }

        @Override
        public V setValue(V newValue) {
            if (value == null)
                throw new NullPointerException();

            V old = value;
            value = newValue;
            return old;
        }

        Node(K key, V value) {
            this.key = key;
            this.value = value;
        }

        public final String toString() {
            return "" + key;
        }// + "=>" + value

        @Override
        public int hashCode() {
            return key.hashCode() ^ value.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof HashTable.Node) {
                HashTable.Node node = (HashTable.Node) obj;
                return Objects.equals(key, node.key) && Objects.equals(value, node.value);
            }
            return false;
        }
    }
}
