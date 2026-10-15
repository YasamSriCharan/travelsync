package texthack.module4;

import texthack.collections.SimpleList;
import texthack.collections.SimplePriorityQueue;

/**
 * Min-Cost Max-Flow using the Successive Shortest Paths (SSP) algorithm.
 *
 * Finds the maximum flow with minimum total cost by repeatedly finding
 * the shortest (minimum-cost) augmenting path using Bellman-Ford (or
 * SPFA — Shortest Path Faster Algorithm) on the residual graph.
 *
 * Time:  O(V · E · flow) — practical for moderate inputs
 * Space: O(V + E)
 *
 * Applications:
 *   - Optimal assignment (each job to a worker at minimum cost)
 *   - Transportation problems
 *   - Minimum-cost bipartite matching
 */
public class MinCostMaxFlow {

    /** Result containing both the maximum flow and its minimum cost. */
    public static class MCMFResult {
        public final int maxFlow;
        public final int minCost;

        MCMFResult(int maxFlow, int minCost) {
            this.maxFlow = maxFlow;
            this.minCost = minCost;
        }

        @Override
        public String toString() {
            return "MaxFlow=" + maxFlow + ", MinCost=" + minCost;
        }
    }

    private static final int INF = Integer.MAX_VALUE / 2;

    /**
     * Computes min-cost max-flow from source to sink.
     */
    public static MCMFResult compute(FlowNetwork network, int source, int sink) {
        int n = network.getVertexCount();
        int totalFlow = 0, totalCost = 0;

        while (true) {
            // SPFA (Bellman-Ford variant) to find shortest path by cost
            int[] dist = new int[n];
            boolean[] inQueue = new boolean[n];
            FlowNetwork.Edge[] parent = new FlowNetwork.Edge[n];

            for (int i = 0; i < n; i++) dist[i] = INF;
            dist[source] = 0;

            // Use a simple array-based queue for SPFA
            int[] queue = new int[n * 4]; // oversized to handle re-enqueuing
            int front = 0, back = 0;
            queue[back++ % queue.length] = source;
            inQueue[source] = true;

            while (front != back) {
                int u = queue[front++ % queue.length];
                inQueue[u] = false;

                SimpleList<FlowNetwork.Edge> edges = network.getEdges(u);
                for (int i = 0; i < edges.size(); i++) {
                    FlowNetwork.Edge e = edges.get(i);
                    if (e.residual() > 0 && dist[u] + e.cost < dist[e.to]) {
                        dist[e.to] = dist[u] + e.cost;
                        parent[e.to] = e;
                        if (!inQueue[e.to]) {
                            queue[back++ % queue.length] = e.to;
                            inQueue[e.to] = true;
                        }
                    }
                }
            }

            if (dist[sink] == INF) break; // no more augmenting paths

            // Find bottleneck
            int pathFlow = INF;
            for (FlowNetwork.Edge e = parent[sink]; e != null; e = parent[e.from]) {
                pathFlow = Math.min(pathFlow, e.residual());
            }

            // Push flow and accumulate cost
            for (FlowNetwork.Edge e = parent[sink]; e != null; e = parent[e.from]) {
                e.addFlow(pathFlow);
                totalCost += pathFlow * e.cost;
            }
            totalFlow += pathFlow;
        }

        return new MCMFResult(totalFlow, totalCost);
    }

    /**
     * Optimal assignment: n workers, n jobs, cost[i][j] = cost of assigning worker i to job j.
     * @return MCMFResult with total cost and assignments
     */
    public static MCMFResult optimalAssignment(int[][] cost) {
        int n = cost.length;
        int source = 2 * n, sink = 2 * n + 1;
        FlowNetwork network = new FlowNetwork(2 * n + 2);

        // Source → workers
        for (int i = 0; i < n; i++) {
            network.addEdge(source, i, 1, 0);
        }

        // Workers → jobs
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                network.addEdge(i, n + j, 1, cost[i][j]);
            }
        }

        // Jobs → sink
        for (int j = 0; j < n; j++) {
            network.addEdge(n + j, sink, 1, 0);
        }

        return compute(network, source, sink);
    }
}
