package texthack.collections;

/**
 * A hand-built hash map with separate chaining (replaces java.util.HashMap).
 * Uses polynomial rolling hash. Resizes at 75% load factor.
 */
public class SimpleMap<K, V> {

    /** A key-value entry in the map. */
    public static class Entry<K, V> {
        public final K key;
        public V value;
        Entry<K, V> next;

        Entry(K key, V value, Entry<K, V> next) {
            this.key = key;
            this.value = value;
            this.next = next;
        }
    }

    private Entry<K, V>[] buckets;
    private int size;
    private static final int DEFAULT_CAPACITY = 16;
    private static final double LOAD_FACTOR = 0.75;

    @SuppressWarnings("unchecked")
    public SimpleMap() {
        buckets = new Entry[DEFAULT_CAPACITY];
        size = 0;
    }

    @SuppressWarnings("unchecked")
    public SimpleMap(int capacity) {
        buckets = new Entry[Math.max(1, capacity)];
        size = 0;
    }

    private int bucketIndex(K key) {
        if (key == null) return 0;
        int h = key.hashCode();
        h ^= (h >>> 16);
        return (h & 0x7FFFFFFF) % buckets.length;
    }

    public void put(K key, V value) {
        int idx = bucketIndex(key);
        for (Entry<K, V> e = buckets[idx]; e != null; e = e.next) {
            if (keysEqual(e.key, key)) {
                e.value = value;
                return;
            }
        }
        buckets[idx] = new Entry<>(key, value, buckets[idx]);
        size++;
        if (size > buckets.length * LOAD_FACTOR) resize();
    }

    public V get(K key) {
        int idx = bucketIndex(key);
        for (Entry<K, V> e = buckets[idx]; e != null; e = e.next) {
            if (keysEqual(e.key, key)) return e.value;
        }
        return null;
    }

    public V getOrDefault(K key, V defaultValue) {
        V val = get(key);
        return (val != null || containsKey(key)) ? val : defaultValue;
    }

    public boolean containsKey(K key) {
        int idx = bucketIndex(key);
        for (Entry<K, V> e = buckets[idx]; e != null; e = e.next) {
            if (keysEqual(e.key, key)) return true;
        }
        return false;
    }

    public V remove(K key) {
        int idx = bucketIndex(key);
        Entry<K, V> prev = null;
        for (Entry<K, V> e = buckets[idx]; e != null; prev = e, e = e.next) {
            if (keysEqual(e.key, key)) {
                if (prev == null) buckets[idx] = e.next;
                else prev.next = e.next;
                size--;
                return e.value;
            }
        }
        return null;
    }

    public int size() { return size; }
    public boolean isEmpty() { return size == 0; }

    /** Returns all keys as a SimpleList. */
    public SimpleList<K> keys() {
        SimpleList<K> result = new SimpleList<>();
        for (Entry<K, V> bucket : buckets) {
            for (Entry<K, V> e = bucket; e != null; e = e.next) {
                result.add(e.key);
            }
        }
        return result;
    }

    /** Returns all values as a SimpleList. */
    public SimpleList<V> values() {
        SimpleList<V> result = new SimpleList<>();
        for (Entry<K, V> bucket : buckets) {
            for (Entry<K, V> e = bucket; e != null; e = e.next) {
                result.add(e.value);
            }
        }
        return result;
    }

    /** Returns all entries as a SimpleList. */
    public SimpleList<Entry<K, V>> entries() {
        SimpleList<Entry<K, V>> result = new SimpleList<>();
        for (Entry<K, V> bucket : buckets) {
            for (Entry<K, V> e = bucket; e != null; e = e.next) {
                result.add(e);
            }
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    private void resize() {
        Entry<K, V>[] oldBuckets = buckets;
        buckets = new Entry[oldBuckets.length * 2];
        size = 0;
        for (Entry<K, V> bucket : oldBuckets) {
            for (Entry<K, V> e = bucket; e != null; e = e.next) {
                put(e.key, e.value);
            }
        }
    }

    private boolean keysEqual(K a, K b) {
        if (a == b) return true;
        if (a == null || b == null) return false;
        return a.equals(b);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("{");
        boolean first = true;
        for (Entry<K, V> bucket : buckets) {
            for (Entry<K, V> e = bucket; e != null; e = e.next) {
                if (!first) sb.append(", ");
                sb.append(e.key).append("=").append(e.value);
                first = false;
            }
        }
        sb.append("}");
        return sb.toString();
    }
}
