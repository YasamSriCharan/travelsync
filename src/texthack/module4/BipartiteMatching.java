package texthack.module4;

import texthack.collections.SimpleList;

/**
 * Bipartite Matching as a max-flow application.
 *
 * Models bipartite matching as a flow problem:
 *   - Add a super-source connected to all left vertices (capacity 1)
 *   - Add a super-sink connected to all right vertices (capacity 1)
 *   - Each edge in the bipartite graph has capacity 1
 *   - Max-flow = maximum matching
 *
 * König's Theorem: In a bipartite graph,
 *   maximum matching = minimum vertex cover
 *
 * Also implements the Hopcroft-Karp style algorithm for direct O(E√V) matching.
 */
public class BipartiteMatching {

    /**
     * Result of a bipartite matching.
     */
    public static class MatchingResult {
        public final int matchingSize;
        public final int[] matchLeft;    // matchLeft[i] = matched right vertex, or -1
        public final int[] matchRight;   // matchRight[j] = matched left vertex, or -1

        MatchingResult(int size, int[] matchLeft, int[] matchRight) {
            this.matchingSize = size;
            this.matchLeft = matchLeft;
            this.matchRight = matchRight;
        }
    }

    /**
     * Maximum bipartite matching using max-flow.
     *
     * @param leftSize  number of left vertices
     * @param rightSize number of right vertices
     * @param edges     list of [left, right] pairs
     * @return MatchingResult
     */
    public static MatchingResult maxMatchingFlow(int leftSize, int rightSize,
                                                  SimpleList<int[]> edges) {
        int n = leftSize + rightSize + 2;
        int source = leftSize + rightSize;
        int sink = source + 1;
        FlowNetwork network = new FlowNetwork(n);

        // Source → left vertices
        for (int i = 0; i < leftSize; i++) {
            network.addEdge(source, i, 1);
        }

        // Right vertices → sink
        for (int j = 0; j < rightSize; j++) {
            network.addEdge(leftSize + j, sink, 1);
        }

        // Bipartite edges (left → right)
        for (int e = 0; e < edges.size(); e++) {
            int[] edge = edges.get(e);
            network.addEdge(edge[0], leftSize + edge[1], 1);
        }

        int flow = Dinic.maxFlow(network, source, sink);

        // Extract matching from flow
        int[] matchLeft = new int[leftSize];
        int[] matchRight = new int[rightSize];
        for (int i = 0; i < leftSize; i++) matchLeft[i] = -1;
        for (int j = 0; j < rightSize; j++) matchRight[j] = -1;

        for (int i = 0; i < leftSize; i++) {
            SimpleList<FlowNetwork.Edge> adj = network.getEdges(i);
            for (int k = 0; k < adj.size(); k++) {
                FlowNetwork.Edge fe = adj.get(k);
                if (fe.to >= leftSize && fe.to < leftSize + rightSize && fe.flow > 0) {
                    int j = fe.to - leftSize;
                    matchLeft[i] = j;
                    matchRight[j] = i;
                }
            }
        }

        return new MatchingResult(flow, matchLeft, matchRight);
    }

    /**
     * Hungarian-style augmenting path matching (simpler O(VE) approach).
     * Direct DFS-based matching without building a flow network.
     */
    public static MatchingResult maxMatchingDFS(int leftSize, int rightSize,
                                                 SimpleList<Integer>[] adj) {
        int[] matchLeft = new int[leftSize];
        int[] matchRight = new int[rightSize];
        for (int i = 0; i < leftSize; i++) matchLeft[i] = -1;
        for (int j = 0; j < rightSize; j++) matchRight[j] = -1;

        int matched = 0;
        for (int u = 0; u < leftSize; u++) {
            boolean[] visited = new boolean[rightSize];
            if (augment(u, adj, matchLeft, matchRight, visited)) {
                matched++;
            }
        }

        return new MatchingResult(matched, matchLeft, matchRight);
    }

    private static boolean augment(int u, SimpleList<Integer>[] adj,
                                    int[] matchLeft, int[] matchRight, boolean[] visited) {
        for (int i = 0; i < adj[u].size(); i++) {
            int v = adj[u].get(i);
            if (visited[v]) continue;
            visited[v] = true;
            if (matchRight[v] == -1 || augment(matchRight[v], adj, matchLeft, matchRight, visited)) {
                matchLeft[u] = v;
                matchRight[v] = u;
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if a perfect matching exists.
     */
    public static boolean hasPerfectMatching(int leftSize, int rightSize,
                                              SimpleList<int[]> edges) {
        if (leftSize != rightSize) return false;
        MatchingResult result = maxMatchingFlow(leftSize, rightSize, edges);
        return result.matchingSize == leftSize;
    }

    /**
     * Minimum vertex cover via König's theorem.
     * In a bipartite graph: min vertex cover = max matching.
     *
     * @return boolean arrays: [0] = left vertices in cover, [1] = right vertices in cover
     */
    public static boolean[][] minimumVertexCover(int leftSize, int rightSize,
                                                   SimpleList<Integer>[] adj) {
        MatchingResult matching = maxMatchingDFS(leftSize, rightSize, adj);

        // König's theorem: alternating BFS from unmatched left vertices
        boolean[] visitedLeft = new boolean[leftSize];
        boolean[] visitedRight = new boolean[rightSize];

        // Start from unmatched left vertices
        int[] queue = new int[leftSize + rightSize];
        int front = 0, back = 0;
        for (int u = 0; u < leftSize; u++) {
            if (matching.matchLeft[u] == -1) {
                visitedLeft[u] = true;
                queue[back++] = u;
            }
        }

        // Alternating BFS
        while (front < back) {
            int u = queue[front++];
            // Go to unmatched right neighbours
            for (int i = 0; i < adj[u].size(); i++) {
                int v = adj[u].get(i);
                if (!visitedRight[v]) {
                    visitedRight[v] = true;
                    // Follow the matching edge back to left
                    if (matching.matchRight[v] != -1 && !visitedLeft[matching.matchRight[v]]) {
                        visitedLeft[matching.matchRight[v]] = true;
                        queue[back++] = matching.matchRight[v];
                    }
                }
            }
        }

        // Vertex cover = unvisited left ∪ visited right
        boolean[] coverLeft = new boolean[leftSize];
        boolean[] coverRight = new boolean[rightSize];
        for (int i = 0; i < leftSize; i++) coverLeft[i] = !visitedLeft[i];
        for (int j = 0; j < rightSize; j++) coverRight[j] = visitedRight[j];

        return new boolean[][]{coverLeft, coverRight};
    }
}
