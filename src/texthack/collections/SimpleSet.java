package texthack.collections;

/**
 * A hand-built hash set backed by SimpleMap (replaces java.util.HashSet).
 */
public class SimpleSet<T> {
    private final SimpleMap<T, Boolean> map;

    public SimpleSet() {
        map = new SimpleMap<>();
    }

    public SimpleSet(int capacity) {
        map = new SimpleMap<>(capacity);
    }

    public void add(T element) {
        map.put(element, Boolean.TRUE);
    }

    public boolean contains(T element) {
        return map.containsKey(element);
    }

    public void remove(T element) {
        map.remove(element);
    }

    public int size() {
        return map.size();
    }

    public boolean isEmpty() {
        return map.isEmpty();
    }

    public SimpleList<T> toList() {
        return map.keys();
    }

    @Override
    public String toString() {
        return map.keys().toString();
    }
}
