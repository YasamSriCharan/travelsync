package texthack.module3;

import texthack.collections.SimpleList;

/**
 * Tree DP algorithms.
 *
 * Implements:
 *   1. Maximum independent set on a tree
 *   2. Tree diameter via DP
 *   3. Tree centroid decomposition
 *   4. Rerooting technique — computing a rooted DP answer for every root in O(n)
 *
 * Trees are represented as adjacency lists: adj[u] = list of neighbours.
 * All DPs use iterative stack-based traversal to avoid stack overflow on deep trees.
 */
public class TreeDP {

    // =====================================================================
    //  Maximum Independent Set on a Tree
    // =====================================================================

    /**
     * Finds the maximum independent set size on a tree.
     * An independent set has no two adjacent vertices.
     *
     * @param adj  adjacency list of the tree (0-indexed, undirected)
     * @param n    number of vertices
     * @return maximum size of an independent set
     *
     * DP: dp[v][0] = max IS in subtree rooted at v, not including v
     *     dp[v][1] = max IS in subtree rooted at v, including v
     */
    public static int maxIndependentSet(SimpleList<Integer>[] adj, int n) {
        if (n == 0) return 0;
        int[][] dp = new int[n][2];
        boolean[] visited = new boolean[n];
        int[] parent = new int[n];
        int[] order = new int[n]; // topological order (leaves first via reverse post-order)
        for (int i = 0; i < n; i++) parent[i] = -1;

        // BFS to determine order
        int front = 0, back = 0;
        order[back++] = 0;
        visited[0] = true;
        while (front < back) {
            int u = order[front++];
            SimpleList<Integer> neighbours = adj[u];
            for (int i = 0; i < neighbours.size(); i++) {
                int v = neighbours.get(i);
                if (!visited[v]) {
                    visited[v] = true;
                    parent[v] = u;
                    order[back++] = v;
                }
            }
        }

        // Process in reverse BFS order (leaves first)
        for (int idx = n - 1; idx >= 0; idx--) {
            int v = order[idx];
            dp[v][0] = 0;
            dp[v][1] = 1;
            SimpleList<Integer> neighbours = adj[v];
            for (int i = 0; i < neighbours.size(); i++) {
                int child = neighbours.get(i);
                if (child == parent[v]) continue;
                dp[v][0] += Math.max(dp[child][0], dp[child][1]);
                dp[v][1] += dp[child][0];
            }
        }

        return Math.max(dp[0][0], dp[0][1]);
    }

    // =====================================================================
    //  Tree Diameter
    // =====================================================================

    /**
     * Computes the diameter of a tree (longest path between any two vertices).
     * Uses two BFS passes.
     *
     * @param adj adjacency list
     * @param n   number of vertices
     * @return diameter (number of edges on the longest path)
     */
    public static int diameter(SimpleList<Integer>[] adj, int n) {
        if (n <= 1) return 0;
        int[] dist = bfs(adj, n, 0);
        int farthest = 0;
        for (int i = 1; i < n; i++) {
            if (dist[i] > dist[farthest]) farthest = i;
        }
        dist = bfs(adj, n, farthest);
        int diam = 0;
        for (int i = 0; i < n; i++) diam = Math.max(diam, dist[i]);
        return diam;
    }

    /**
     * Computes the diameter using DP (single-pass) from root 0.
     * dp[v] = depth of deepest leaf in subtree of v.
     * The diameter passes through the vertex where the sum of the two
     * deepest subtrees is maximised.
     */
    public static int diameterDP(SimpleList<Integer>[] adj, int n) {
        if (n <= 1) return 0;
        int[] depth = new int[n];
        int[] parent = new int[n];
        int[] order = new int[n];
        boolean[] visited = new boolean[n];
        for (int i = 0; i < n; i++) parent[i] = -1;

        int front = 0, back = 0;
        order[back++] = 0;
        visited[0] = true;
        while (front < back) {
            int u = order[front++];
            for (int i = 0; i < adj[u].size(); i++) {
                int v = adj[u].get(i);
                if (!visited[v]) {
                    visited[v] = true;
                    parent[v] = u;
                    order[back++] = v;
                }
            }
        }

        int result = 0;
        for (int idx = n - 1; idx >= 0; idx--) {
            int u = order[idx];
            int max1 = 0, max2 = 0;
            for (int i = 0; i < adj[u].size(); i++) {
                int v = adj[u].get(i);
                if (v == parent[u]) continue;
                int d = depth[v] + 1;
                if (d > max1) { max2 = max1; max1 = d; }
                else if (d > max2) { max2 = d; }
            }
            depth[u] = max1;
            result = Math.max(result, max1 + max2);
        }
        return result;
    }

    // =====================================================================
    //  Tree Centroid
    // =====================================================================

    /**
     * Finds the centroid(s) of a tree.
     * A centroid is a vertex whose removal creates no component larger than n/2.
     *
     * @return list of centroid vertices (1 or 2 centroids)
     */
    public static SimpleList<Integer> findCentroids(SimpleList<Integer>[] adj, int n) {
        int[] subtreeSize = new int[n];
        int[] parent = new int[n];
        int[] order = new int[n];
        boolean[] visited = new boolean[n];
        for (int i = 0; i < n; i++) parent[i] = -1;

        int front = 0, back = 0;
        order[back++] = 0;
        visited[0] = true;
        while (front < back) {
            int u = order[front++];
            for (int i = 0; i < adj[u].size(); i++) {
                int v = adj[u].get(i);
                if (!visited[v]) {
                    visited[v] = true;
                    parent[v] = u;
                    order[back++] = v;
                }
            }
        }

        // Compute subtree sizes bottom-up
        for (int i = 0; i < n; i++) subtreeSize[i] = 1;
        for (int idx = n - 1; idx > 0; idx--) {
            subtreeSize[parent[order[idx]]] += subtreeSize[order[idx]];
        }

        SimpleList<Integer> centroids = new SimpleList<>();
        for (int u = 0; u < n; u++) {
            int maxComponent = n - subtreeSize[u];
            for (int i = 0; i < adj[u].size(); i++) {
                int v = adj[u].get(i);
                if (v != parent[u]) {
                    maxComponent = Math.max(maxComponent, subtreeSize[v]);
                }
            }
            if (maxComponent <= n / 2) centroids.add(u);
        }
        return centroids;
    }

    // =====================================================================
    //  Rerooting Technique
    // =====================================================================

    /**
     * Rerooting DP: computes the sum of distances from each vertex to all others.
     * First root at 0, then reroot in O(n).
     *
     * @return ans[v] = sum of distances from v to all other vertices
     */
    public static long[] sumOfDistances(SimpleList<Integer>[] adj, int n) {
        int[] subtreeSize = new int[n];
        long[] subtreeDistSum = new long[n];
        int[] parent = new int[n];
        int[] order = new int[n];
        boolean[] visited = new boolean[n];
        for (int i = 0; i < n; i++) parent[i] = -1;

        // BFS order
        int front = 0, back = 0;
        order[back++] = 0;
        visited[0] = true;
        while (front < back) {
            int u = order[front++];
            for (int i = 0; i < adj[u].size(); i++) {
                int v = adj[u].get(i);
                if (!visited[v]) {
                    visited[v] = true;
                    parent[v] = u;
                    order[back++] = v;
                }
            }
        }

        // Pass 1: bottom-up — compute subtreeSize and subtreeDistSum rooted at 0
        for (int i = 0; i < n; i++) subtreeSize[i] = 1;
        for (int idx = n - 1; idx > 0; idx--) {
            int v = order[idx];
            int p = parent[v];
            subtreeSize[p] += subtreeSize[v];
            subtreeDistSum[p] += subtreeDistSum[v] + subtreeSize[v];
        }

        // Pass 2: top-down — reroot
        long[] ans = new long[n];
        ans[0] = subtreeDistSum[0];
        for (int idx = 1; idx < n; idx++) {
            int v = order[idx];
            int p = parent[v];
            // When rerooting from p to v:
            // subtreeSize[v] nodes get 1 closer, (n - subtreeSize[v]) nodes get 1 farther
            ans[v] = ans[p] - subtreeSize[v] + (n - subtreeSize[v]);
        }
        return ans;
    }

    // ----- Utility: BFS distance -----
    private static int[] bfs(SimpleList<Integer>[] adj, int n, int source) {
        int[] dist = new int[n];
        for (int i = 0; i < n; i++) dist[i] = -1;
        int[] queue = new int[n];
        int front = 0, back = 0;
        dist[source] = 0;
        queue[back++] = source;
        while (front < back) {
            int u = queue[front++];
            for (int i = 0; i < adj[u].size(); i++) {
                int v = adj[u].get(i);
                if (dist[v] == -1) {
                    dist[v] = dist[u] + 1;
                    queue[back++] = v;
                }
            }
        }
        return dist;
    }
}
