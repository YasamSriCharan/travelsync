package texthack.module4;

import texthack.collections.SimpleList;
import texthack.collections.SimpleQueue;

/**
 * Edmonds-Karp algorithm — Ford-Fulkerson with BFS for augmenting paths.
 *
 * By always choosing the shortest augmenting path (BFS), the algorithm
 * guarantees O(VE²) running time, independent of the max-flow value.
 *
 * This is the practical reference implementation for max-flow.
 */
public class EdmondsKarp {

    /**
     * Computes maximum flow from source to sink.
     * @return maximum flow value
     *
     * Time:  O(VE²)
     * Space: O(V + E)
     */
    public static int maxFlow(FlowNetwork network, int source, int sink) {
        int n = network.getVertexCount();
        int totalFlow = 0;

        while (true) {
            // BFS to find shortest augmenting path
            FlowNetwork.Edge[] parent = new FlowNetwork.Edge[n];
            boolean[] visited = new boolean[n];
            SimpleQueue<Integer> queue = new SimpleQueue<>();

            visited[source] = true;
            queue.enqueue(source);

            while (!queue.isEmpty() && !visited[sink]) {
                int u = queue.dequeue();
                SimpleList<FlowNetwork.Edge> edges = network.getEdges(u);
                for (int i = 0; i < edges.size(); i++) {
                    FlowNetwork.Edge e = edges.get(i);
                    if (!visited[e.to] && e.residual() > 0) {
                        visited[e.to] = true;
                        parent[e.to] = e;
                        queue.enqueue(e.to);
                    }
                }
            }

            if (!visited[sink]) break; // no augmenting path found

            // Find bottleneck capacity
            int pathFlow = Integer.MAX_VALUE;
            for (FlowNetwork.Edge e = parent[sink]; e != null; e = parent[e.from]) {
                pathFlow = Math.min(pathFlow, e.residual());
            }

            // Push flow along the path
            for (FlowNetwork.Edge e = parent[sink]; e != null; e = parent[e.from]) {
                e.addFlow(pathFlow);
            }

            totalFlow += pathFlow;
        }
        return totalFlow;
    }

    /**
     * Computes max-flow and returns both the flow value and the min-cut.
     * @return int[0] = max flow, subsequent values = vertices on the source side of the min-cut
     */
    public static int[] maxFlowWithCut(FlowNetwork network, int source, int sink) {
        int flow = maxFlow(network, source, sink);

        // BFS from source in residual graph to find min-cut
        int n = network.getVertexCount();
        boolean[] reachable = new boolean[n];
        SimpleQueue<Integer> queue = new SimpleQueue<>();
        reachable[source] = true;
        queue.enqueue(source);

        int count = 0;
        while (!queue.isEmpty()) {
            int u = queue.dequeue();
            count++;
            SimpleList<FlowNetwork.Edge> edges = network.getEdges(u);
            for (int i = 0; i < edges.size(); i++) {
                FlowNetwork.Edge e = edges.get(i);
                if (!reachable[e.to] && e.residual() > 0) {
                    reachable[e.to] = true;
                    queue.enqueue(e.to);
                }
            }
        }

        int[] result = new int[count + 1];
        result[0] = flow;
        int idx = 1;
        for (int i = 0; i < n; i++) {
            if (reachable[i]) result[idx++] = i;
        }
        return result;
    }
}
