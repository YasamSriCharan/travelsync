package texthack.module4;

import texthack.collections.SimpleList;
import texthack.collections.SimpleQueue;

/**
 * Dinic's algorithm for maximum flow.
 *
 * Uses blocking flows on level graphs (constructed via BFS) to achieve
 * O(V²E) running time — near-optimal for most practical inputs.
 *
 * Steps:
 *   1. Build a level graph using BFS from source
 *   2. Find blocking flows using DFS with the "iter" (current-arc) optimisation
 *   3. Repeat until no path exists from source to sink in the level graph
 */
public class Dinic {

    private static int[] level;
    private static int[] iter; // current-arc optimisation

    /**
     * Computes maximum flow from source to sink.
     *
     * Time:  O(V²E)
     * Space: O(V + E)
     */
    public static int maxFlow(FlowNetwork network, int source, int sink) {
        int n = network.getVertexCount();
        int totalFlow = 0;

        while (true) {
            // Build level graph via BFS
            level = new int[n];
            for (int i = 0; i < n; i++) level[i] = -1;
            if (!bfs(network, source, sink)) break;

            // Reset current-arc pointers
            iter = new int[n];

            // Find blocking flows via DFS
            int pushed;
            while ((pushed = dfs(network, source, sink, Integer.MAX_VALUE)) > 0) {
                totalFlow += pushed;
            }
        }
        return totalFlow;
    }

    /**
     * BFS to build the level graph.
     * @return true if sink is reachable from source
     */
    private static boolean bfs(FlowNetwork network, int source, int sink) {
        SimpleQueue<Integer> queue = new SimpleQueue<>();
        level[source] = 0;
        queue.enqueue(source);

        while (!queue.isEmpty()) {
            int u = queue.dequeue();
            SimpleList<FlowNetwork.Edge> edges = network.getEdges(u);
            for (int i = 0; i < edges.size(); i++) {
                FlowNetwork.Edge e = edges.get(i);
                if (level[e.to] < 0 && e.residual() > 0) {
                    level[e.to] = level[u] + 1;
                    queue.enqueue(e.to);
                }
            }
        }
        return level[sink] >= 0;
    }

    /**
     * DFS to find a blocking flow.
     * Uses the current-arc (iter) optimisation: once an edge is saturated
     * or leads to a dead end, we never try it again in this phase.
     */
    private static int dfs(FlowNetwork network, int u, int sink, int pushed) {
        if (u == sink) return pushed;

        SimpleList<FlowNetwork.Edge> edges = network.getEdges(u);
        for (; iter[u] < edges.size(); iter[u]++) {
            FlowNetwork.Edge e = edges.get(iter[u]);
            if (level[e.to] == level[u] + 1 && e.residual() > 0) {
                int flow = dfs(network, e.to, sink, Math.min(pushed, e.residual()));
                if (flow > 0) {
                    e.addFlow(flow);
                    return flow;
                }
            }
        }
        return 0;
    }
}
