package texthack.module4;

import texthack.collections.SimpleList;

/**
 * Flow network representation using adjacency lists with edge objects.
 * Supports residual graph operations needed by all flow algorithms.
 *
 * Each edge stores: source, destination, capacity, and flow.
 * Reverse edges are paired for O(1) residual updates.
 */
public class FlowNetwork {

    /** An edge in the flow network with a pointer to its reverse edge. */
    public static class Edge {
        public final int from;
        public final int to;
        public int capacity;
        public int flow;
        public int cost;     // used by min-cost max-flow
        public Edge reverse; // paired reverse edge

        public Edge(int from, int to, int capacity) {
            this.from = from;
            this.to = to;
            this.capacity = capacity;
            this.flow = 0;
            this.cost = 0;
        }

        public Edge(int from, int to, int capacity, int cost) {
            this.from = from;
            this.to = to;
            this.capacity = capacity;
            this.flow = 0;
            this.cost = cost;
        }

        /** Residual capacity of this edge. */
        public int residual() {
            return capacity - flow;
        }

        /** Push flow along this edge (and update reverse). */
        public void addFlow(int amount) {
            flow += amount;
            reverse.flow -= amount;
        }

        @Override
        public String toString() {
            return from + " -> " + to + " (" + flow + "/" + capacity + ")";
        }
    }

    private final int vertexCount;
    private final SimpleList<Edge>[] adj;

    @SuppressWarnings("unchecked")
    public FlowNetwork(int vertexCount) {
        this.vertexCount = vertexCount;
        adj = new SimpleList[vertexCount];
        for (int i = 0; i < vertexCount; i++) {
            adj[i] = new SimpleList<>();
        }
    }

    /**
     * Adds a directed edge with the given capacity (and a reverse edge with capacity 0).
     * Returns the forward edge.
     */
    public Edge addEdge(int from, int to, int capacity) {
        Edge forward = new Edge(from, to, capacity);
        Edge backward = new Edge(to, from, 0);
        forward.reverse = backward;
        backward.reverse = forward;
        adj[from].add(forward);
        adj[to].add(backward);
        return forward;
    }

    /**
     * Adds a directed edge with capacity and cost (for min-cost max-flow).
     */
    public Edge addEdge(int from, int to, int capacity, int cost) {
        Edge forward = new Edge(from, to, capacity, cost);
        Edge backward = new Edge(to, from, 0, -cost);
        forward.reverse = backward;
        backward.reverse = forward;
        adj[from].add(forward);
        adj[to].add(backward);
        return forward;
    }

    /** Returns the adjacency list for vertex u. */
    public SimpleList<Edge> getEdges(int u) {
        return adj[u];
    }

    /** Returns the number of vertices. */
    public int getVertexCount() {
        return vertexCount;
    }

    /** Returns all edges (forward only, not reverse edges). */
    public SimpleList<Edge> getAllEdges() {
        SimpleList<Edge> all = new SimpleList<>();
        for (int u = 0; u < vertexCount; u++) {
            for (int i = 0; i < adj[u].size(); i++) {
                Edge e = adj[u].get(i);
                if (e.capacity > 0) all.add(e); // forward edges only
            }
        }
        return all;
    }

    /** Resets all flows to zero. */
    public void resetFlows() {
        for (int u = 0; u < vertexCount; u++) {
            for (int i = 0; i < adj[u].size(); i++) {
                adj[u].get(i).flow = 0;
            }
        }
    }
}
