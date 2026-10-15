package texthack.module3;

import texthack.collections.SimpleList;

/**
 * Bitmask DP algorithms.
 *
 * Implements:
 *   1. Travelling Salesman Problem (TSP) — O(2ⁿ · n²)
 *   2. Hamiltonian Path detection — O(2ⁿ · n²)
 *   3. Set Cover via bitmask DP — O(2ⁿ · m)
 *
 * Bitmask DP uses an integer bitmask to represent subsets of n elements,
 * enabling enumeration of all 2ⁿ subsets efficiently.
 */
public class BitmaskDP {

    private static final int INF = Integer.MAX_VALUE / 2;

    /**
     * Solves TSP exactly using bitmask DP.
     * @param dist  n×n distance matrix; dist[i][j] = cost from city i to j
     * @param start starting city index
     * @return minimum cost of a tour visiting all cities and returning to start
     *
     * Time:  O(2ⁿ · n²)
     * Space: O(2ⁿ · n)
     */
    public static int tsp(int[][] dist, int start) {
        int n = dist.length;
        int totalMask = 1 << n;
        int[][] dp = new int[totalMask][n];

        for (int mask = 0; mask < totalMask; mask++) {
            for (int i = 0; i < n; i++) dp[mask][i] = INF;
        }
        dp[1 << start][start] = 0;

        for (int mask = 0; mask < totalMask; mask++) {
            for (int u = 0; u < n; u++) {
                if (dp[mask][u] == INF) continue;
                if ((mask & (1 << u)) == 0) continue;
                for (int v = 0; v < n; v++) {
                    if ((mask & (1 << v)) != 0) continue; // already visited
                    int newMask = mask | (1 << v);
                    int newCost = dp[mask][u] + dist[u][v];
                    if (newCost < dp[newMask][v]) {
                        dp[newMask][v] = newCost;
                    }
                }
            }
        }

        // Close the tour back to start
        int fullMask = totalMask - 1;
        int minCost = INF;
        for (int u = 0; u < n; u++) {
            if (dp[fullMask][u] != INF && dist[u][start] != INF) {
                minCost = Math.min(minCost, dp[fullMask][u] + dist[u][start]);
            }
        }
        return minCost;
    }

    /**
     * TSP with path reconstruction.
     * @return int[] where [0] is the tour cost and [1..n] are the city indices in order
     */
    public static int[] tspWithPath(int[][] dist, int start) {
        int n = dist.length;
        int totalMask = 1 << n;
        int[][] dp = new int[totalMask][n];
        int[][] parent = new int[totalMask][n];

        for (int mask = 0; mask < totalMask; mask++) {
            for (int i = 0; i < n; i++) {
                dp[mask][i] = INF;
                parent[mask][i] = -1;
            }
        }
        dp[1 << start][start] = 0;

        for (int mask = 0; mask < totalMask; mask++) {
            for (int u = 0; u < n; u++) {
                if (dp[mask][u] == INF) continue;
                if ((mask & (1 << u)) == 0) continue;
                for (int v = 0; v < n; v++) {
                    if ((mask & (1 << v)) != 0) continue;
                    int newMask = mask | (1 << v);
                    int newCost = dp[mask][u] + dist[u][v];
                    if (newCost < dp[newMask][v]) {
                        dp[newMask][v] = newCost;
                        parent[newMask][v] = u;
                    }
                }
            }
        }

        int fullMask = totalMask - 1;
        int minCost = INF;
        int lastCity = -1;
        for (int u = 0; u < n; u++) {
            if (dp[fullMask][u] != INF && dist[u][start] != INF) {
                int cost = dp[fullMask][u] + dist[u][start];
                if (cost < minCost) {
                    minCost = cost;
                    lastCity = u;
                }
            }
        }

        // Reconstruct path
        int[] path = new int[n + 2]; // cost + n cities + back to start
        path[0] = minCost;
        int mask = fullMask;
        int idx = n;
        int cur = lastCity;
        while (cur != -1 && mask != 0) {
            path[idx--] = cur;
            int prev = parent[mask][cur];
            mask ^= (1 << cur);
            cur = prev;
        }
        path[idx] = start; // should be redundant but ensures correctness
        path[n + 1] = start; // return to start
        return path;
    }

    /**
     * Detects whether a Hamiltonian path exists in the graph.
     * @param adj  adjacency matrix; adj[i][j] = true if edge exists
     * @return true if a Hamiltonian path exists
     *
     * Time:  O(2ⁿ · n²)
     */
    public static boolean hasHamiltonianPath(boolean[][] adj) {
        int n = adj.length;
        int totalMask = 1 << n;
        boolean[][] dp = new boolean[totalMask][n];

        // Base: start from each vertex
        for (int i = 0; i < n; i++) {
            dp[1 << i][i] = true;
        }

        for (int mask = 0; mask < totalMask; mask++) {
            for (int u = 0; u < n; u++) {
                if (!dp[mask][u]) continue;
                for (int v = 0; v < n; v++) {
                    if ((mask & (1 << v)) != 0) continue;
                    if (adj[u][v]) {
                        dp[mask | (1 << v)][v] = true;
                    }
                }
            }
        }

        int fullMask = totalMask - 1;
        for (int i = 0; i < n; i++) {
            if (dp[fullMask][i]) return true;
        }
        return false;
    }

    /**
     * Minimum set cover using bitmask DP.
     * @param universe   number of elements (0 to universe-1)
     * @param sets       each sets[i] is a bitmask of elements covered by set i
     * @param costs      cost of each set
     * @return minimum cost to cover all elements
     *
     * Time: O(2ⁿ · m) where n = universe size, m = number of sets
     */
    public static int minSetCover(int universe, int[] sets, int[] costs) {
        int totalMask = 1 << universe;
        int[] dp = new int[totalMask];
        for (int i = 1; i < totalMask; i++) dp[i] = INF;

        for (int mask = 0; mask < totalMask; mask++) {
            if (dp[mask] == INF) continue;
            for (int s = 0; s < sets.length; s++) {
                int newMask = mask | sets[s];
                if (dp[mask] + costs[s] < dp[newMask]) {
                    dp[newMask] = dp[mask] + costs[s];
                }
            }
        }
        return dp[totalMask - 1];
    }
}
