package texthack.module4;

import texthack.collections.SimpleList;
import texthack.collections.SimpleQueue;

/**
 * Min-Cut as the dual of Max-Flow.
 *
 * By the Max-Flow Min-Cut theorem, the minimum cut equals the maximum flow.
 * After computing max-flow, the min-cut is the set of edges crossing from
 * the source-reachable partition to the non-reachable partition in the
 * residual graph.
 *
 * Applications:
 *   - Image segmentation (foreground/background partition)
 *   - Project selection (selecting profitable projects with dependencies)
 *   - Network reliability (finding bottleneck edges)
 */
public class MinCut {

    /** Result of a min-cut computation. */
    public static class MinCutResult {
        public final int cutValue;
        public final boolean[] sourcePartition;  // true = source side
        public final SimpleList<FlowNetwork.Edge> cutEdges;

        MinCutResult(int cutValue, boolean[] sourcePartition, SimpleList<FlowNetwork.Edge> cutEdges) {
            this.cutValue = cutValue;
            this.sourcePartition = sourcePartition;
            this.cutEdges = cutEdges;
        }
    }

    /**
     * Computes the minimum cut using Edmonds-Karp max-flow.
     */
    public static MinCutResult compute(FlowNetwork network, int source, int sink) {
        int maxFlow = EdmondsKarp.maxFlow(network, source, sink);
        int n = network.getVertexCount();

        // BFS in residual graph from source to find source-side partition
        boolean[] reachable = new boolean[n];
        SimpleQueue<Integer> queue = new SimpleQueue<>();
        reachable[source] = true;
        queue.enqueue(source);

        while (!queue.isEmpty()) {
            int u = queue.dequeue();
            SimpleList<FlowNetwork.Edge> edges = network.getEdges(u);
            for (int i = 0; i < edges.size(); i++) {
                FlowNetwork.Edge e = edges.get(i);
                if (!reachable[e.to] && e.residual() > 0) {
                    reachable[e.to] = true;
                    queue.enqueue(e.to);
                }
            }
        }

        // Cut edges: edges from reachable to non-reachable with positive capacity
        SimpleList<FlowNetwork.Edge> cutEdges = new SimpleList<>();
        for (int u = 0; u < n; u++) {
            if (!reachable[u]) continue;
            SimpleList<FlowNetwork.Edge> edges = network.getEdges(u);
            for (int i = 0; i < edges.size(); i++) {
                FlowNetwork.Edge e = edges.get(i);
                if (!reachable[e.to] && e.capacity > 0) {
                    cutEdges.add(e);
                }
            }
        }

        return new MinCutResult(maxFlow, reachable, cutEdges);
    }

    /**
     * Image segmentation demonstration:
     * Given pixel affinities and source/sink weights, finds optimal partition.
     *
     * @param rows     number of rows
     * @param cols     number of columns
     * @param fgWeight foreground weight for each pixel (source → pixel capacity)
     * @param bgWeight background weight for each pixel (pixel → sink capacity)
     * @param hEdge    horizontal adjacency penalty (similarity between neighbours)
     * @param vEdge    vertical adjacency penalty
     * @return boolean grid: true = foreground, false = background
     */
    public static boolean[][] imageSegmentation(int rows, int cols,
                                                 int[][] fgWeight, int[][] bgWeight,
                                                 int[][] hEdge, int[][] vEdge) {
        int n = rows * cols;
        int source = n, sink = n + 1;
        FlowNetwork network = new FlowNetwork(n + 2);

        // Source and sink connections
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                int idx = r * cols + c;
                network.addEdge(source, idx, fgWeight[r][c]);
                network.addEdge(idx, sink, bgWeight[r][c]);
            }
        }

        // Horizontal edges
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols - 1; c++) {
                int idx1 = r * cols + c;
                int idx2 = r * cols + c + 1;
                network.addEdge(idx1, idx2, hEdge[r][c]);
                network.addEdge(idx2, idx1, hEdge[r][c]);
            }
        }

        // Vertical edges
        for (int r = 0; r < rows - 1; r++) {
            for (int c = 0; c < cols; c++) {
                int idx1 = r * cols + c;
                int idx2 = (r + 1) * cols + c;
                network.addEdge(idx1, idx2, vEdge[r][c]);
                network.addEdge(idx2, idx1, vEdge[r][c]);
            }
        }

        MinCutResult result = compute(network, source, sink);

        boolean[][] segmentation = new boolean[rows][cols];
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                segmentation[r][c] = result.sourcePartition[r * cols + c];
            }
        }
        return segmentation;
    }
}
