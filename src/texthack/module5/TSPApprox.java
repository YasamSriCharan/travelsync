package texthack.module5;

/**
 * TSP Approximation algorithms.
 *
 * Implements:
 *   1. Nearest-neighbour heuristic — greedy, no guarantee but fast
 *   2. 2-approximation via MST (metric TSP)
 *
 * For metric TSP (triangle inequality holds), the MST-based algorithm
 * guarantees a tour at most 2× optimal. Christofides' 1.5-approximation
 * requires minimum-weight perfect matching (outlined at preview level).
 */
public class TSPApprox {

    private static final int INF = Integer.MAX_VALUE / 2;

    /**
     * Nearest-neighbour heuristic for TSP.
     * Greedily visits the nearest unvisited city.
     *
     * @param dist  n×n distance matrix
     * @param start starting city
     * @return tour as an array of city indices (length n+1, last = first)
     *
     * Time:  O(n²)
     * No approximation guarantee in general.
     */
    public static int[] nearestNeighbour(int[][] dist, int start) {
        int n = dist.length;
        boolean[] visited = new boolean[n];
        int[] tour = new int[n + 1];
        int totalCost = 0;

        tour[0] = start;
        visited[start] = true;

        for (int step = 1; step < n; step++) {
            int current = tour[step - 1];
            int nearest = -1;
            int nearestDist = INF;
            for (int j = 0; j < n; j++) {
                if (!visited[j] && dist[current][j] < nearestDist) {
                    nearest = j;
                    nearestDist = dist[current][j];
                }
            }
            tour[step] = nearest;
            visited[nearest] = true;
        }
        tour[n] = start; // return to start
        return tour;
    }

    /**
     * Computes the total tour cost.
     */
    public static int tourCost(int[][] dist, int[] tour) {
        int cost = 0;
        for (int i = 0; i < tour.length - 1; i++) {
            cost += dist[tour[i]][tour[i + 1]];
        }
        return cost;
    }

    /**
     * 2-approximation for metric TSP using MST.
     *
     * Algorithm:
     *   1. Build MST of the complete graph
     *   2. Perform a DFS traversal of the MST
     *   3. Visit cities in DFS pre-order (skip already visited — shortcutting)
     *
     * Guarantee: tour cost ≤ 2 × OPT (when triangle inequality holds)
     *
     * @param dist  n×n distance matrix (must satisfy triangle inequality)
     * @return tour as array of city indices
     */
    public static int[] mstApproximation(int[][] dist) {
        int n = dist.length;

        // Prim's MST
        boolean[] inMST = new boolean[n];
        int[] key = new int[n];
        int[] parent = new int[n];
        for (int i = 0; i < n; i++) { key[i] = INF; parent[i] = -1; }
        key[0] = 0;

        for (int iter = 0; iter < n; iter++) {
            int u = -1;
            for (int v = 0; v < n; v++) {
                if (!inMST[v] && (u == -1 || key[v] < key[u])) u = v;
            }
            inMST[u] = true;
            for (int v = 0; v < n; v++) {
                if (!inMST[v] && dist[u][v] < key[v]) {
                    key[v] = dist[u][v];
                    parent[v] = u;
                }
            }
        }

        // Build adjacency list from MST
        int[][] mstAdj = new int[n][];
        int[] degree = new int[n];
        for (int i = 1; i < n; i++) {
            degree[parent[i]]++;
            degree[i]++;
        }
        for (int i = 0; i < n; i++) {
            mstAdj[i] = new int[degree[i]];
            degree[i] = 0;
        }
        for (int i = 1; i < n; i++) {
            mstAdj[parent[i]][degree[parent[i]]++] = i;
            mstAdj[i][degree[i]++] = parent[i];
        }

        // DFS pre-order traversal
        int[] tour = new int[n + 1];
        boolean[] visited = new boolean[n];
        int[] stack = new int[n];
        int top = 0, tIdx = 0;
        stack[top++] = 0;

        while (top > 0) {
            int u = stack[--top];
            if (visited[u]) continue;
            visited[u] = true;
            tour[tIdx++] = u;
            // Push children in reverse for correct DFS order
            for (int i = mstAdj[u].length - 1; i >= 0; i--) {
                if (!visited[mstAdj[u][i]]) {
                    stack[top++] = mstAdj[u][i];
                }
            }
        }
        tour[n] = tour[0]; // close the tour
        return tour;
    }

    /**
     * 2-opt local search improvement.
     * Iteratively reverses segments to reduce tour cost.
     *
     * @param dist  distance matrix
     * @param tour  initial tour
     * @return improved tour
     */
    public static int[] twoOpt(int[][] dist, int[] tour) {
        int n = tour.length - 1;
        int[] best = new int[tour.length];
        System.arraycopy(tour, 0, best, 0, tour.length);
        boolean improved = true;

        while (improved) {
            improved = false;
            for (int i = 1; i < n - 1; i++) {
                for (int j = i + 1; j < n; j++) {
                    int oldDist = dist[best[i - 1]][best[i]] + dist[best[j]][best[j + 1]];
                    int newDist = dist[best[i - 1]][best[j]] + dist[best[i]][best[j + 1]];
                    if (newDist < oldDist) {
                        // Reverse the segment [i..j]
                        reverse(best, i, j);
                        improved = true;
                    }
                }
            }
        }
        return best;
    }

    private static void reverse(int[] arr, int i, int j) {
        while (i < j) {
            int temp = arr[i];
            arr[i] = arr[j];
            arr[j] = temp;
            i++; j--;
        }
    }
}
