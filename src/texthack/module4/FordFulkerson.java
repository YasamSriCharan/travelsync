package texthack.module4;

import texthack.collections.SimpleList;

/**
 * Ford-Fulkerson method for maximum flow using DFS augmenting paths.
 *
 * The method repeatedly finds augmenting paths in the residual graph
 * and pushes flow along them until no more augmenting paths exist.
 *
 * Correctness: guaranteed by the Max-Flow Min-Cut theorem.
 * Time: O(E · maxFlow) with DFS — can be slow with large capacities.
 *       Use Edmonds-Karp or Dinic's for polynomial-time guarantees.
 */
public class FordFulkerson {

    /**
     * Computes maximum flow from source to sink using DFS augmenting paths.
     * @return maximum flow value
     */
    public static int maxFlow(FlowNetwork network, int source, int sink) {
        int totalFlow = 0;
        while (true) {
            boolean[] visited = new boolean[network.getVertexCount()];
            int pathFlow = dfs(network, source, sink, Integer.MAX_VALUE, visited);
            if (pathFlow == 0) break;
            totalFlow += pathFlow;
        }
        return totalFlow;
    }

    /**
     * DFS to find an augmenting path and push flow.
     * @return flow pushed along this path (0 if no path found)
     */
    private static int dfs(FlowNetwork network, int u, int sink, int pushed, boolean[] visited) {
        if (u == sink) return pushed;
        visited[u] = true;
        SimpleList<FlowNetwork.Edge> edges = network.getEdges(u);
        for (int i = 0; i < edges.size(); i++) {
            FlowNetwork.Edge e = edges.get(i);
            if (!visited[e.to] && e.residual() > 0) {
                int flow = dfs(network, e.to, sink, Math.min(pushed, e.residual()), visited);
                if (flow > 0) {
                    e.addFlow(flow);
                    return flow;
                }
            }
        }
        return 0;
    }
}
