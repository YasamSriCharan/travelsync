package texthack.collections;

/**
 * A hand-built linked-list queue (replaces java.util.LinkedList / Queue).
 * O(1) enqueue and dequeue.
 */
public class SimpleQueue<T> {
    private static class Node<T> {
        T data;
        Node<T> next;
        Node(T data) { this.data = data; }
    }

    private Node<T> head;
    private Node<T> tail;
    private int size;

    public SimpleQueue() {
        head = null;
        tail = null;
        size = 0;
    }

    public void enqueue(T element) {
        Node<T> node = new Node<>(element);
        if (tail != null) tail.next = node;
        tail = node;
        if (head == null) head = node;
        size++;
    }

    @SuppressWarnings("unchecked")
    public T dequeue() {
        if (head == null) throw new RuntimeException("Queue is empty");
        T data = head.data;
        head = head.next;
        if (head == null) tail = null;
        size--;
        return data;
    }

    public T peek() {
        if (head == null) throw new RuntimeException("Queue is empty");
        return head.data;
    }

    public boolean isEmpty() { return size == 0; }
    public int size() { return size; }

    public void clear() {
        head = null;
        tail = null;
        size = 0;
    }
}
