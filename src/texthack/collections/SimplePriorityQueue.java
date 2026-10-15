package texthack.collections;

/**
 * A hand-built min-heap priority queue (replaces java.util.PriorityQueue).
 * Elements must be Comparable. Smallest element is dequeued first.
 */
public class SimplePriorityQueue<T extends Comparable<T>> {
    private Object[] heap;
    private int size;

    public SimplePriorityQueue() {
        heap = new Object[16];
        size = 0;
    }

    public SimplePriorityQueue(int capacity) {
        heap = new Object[Math.max(1, capacity)];
        size = 0;
    }

    public void insert(T element) {
        ensureCapacity(size + 1);
        heap[size] = element;
        siftUp(size);
        size++;
    }

    @SuppressWarnings("unchecked")
    public T extractMin() {
        if (size == 0) throw new RuntimeException("Priority queue is empty");
        T min = (T) heap[0];
        size--;
        heap[0] = heap[size];
        heap[size] = null;
        if (size > 0) siftDown(0);
        return min;
    }

    @SuppressWarnings("unchecked")
    public T peekMin() {
        if (size == 0) throw new RuntimeException("Priority queue is empty");
        return (T) heap[0];
    }

    public boolean isEmpty() { return size == 0; }
    public int size() { return size; }

    @SuppressWarnings("unchecked")
    private void siftUp(int idx) {
        while (idx > 0) {
            int parent = (idx - 1) / 2;
            if (((T) heap[idx]).compareTo((T) heap[parent]) < 0) {
                swap(idx, parent);
                idx = parent;
            } else break;
        }
    }

    @SuppressWarnings("unchecked")
    private void siftDown(int idx) {
        while (true) {
            int left = 2 * idx + 1;
            int right = 2 * idx + 2;
            int smallest = idx;
            if (left < size && ((T) heap[left]).compareTo((T) heap[smallest]) < 0)
                smallest = left;
            if (right < size && ((T) heap[right]).compareTo((T) heap[smallest]) < 0)
                smallest = right;
            if (smallest != idx) {
                swap(idx, smallest);
                idx = smallest;
            } else break;
        }
    }

    private void swap(int i, int j) {
        Object temp = heap[i];
        heap[i] = heap[j];
        heap[j] = temp;
    }

    private void ensureCapacity(int minCapacity) {
        if (minCapacity > heap.length) {
            int newCapacity = heap.length * 2;
            if (newCapacity < minCapacity) newCapacity = minCapacity;
            Object[] newHeap = new Object[newCapacity];
            System.arraycopy(heap, 0, newHeap, 0, size);
            heap = newHeap;
        }
    }
}
