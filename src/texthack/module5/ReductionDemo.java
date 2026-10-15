package texthack.module5;

import texthack.collections.SimpleList;
import texthack.collections.SimpleMap;

/**
 * Reduction Zoo — demonstrates polynomial-time reductions between NP-complete problems.
 *
 * The reduction chain:
 *   3-SAT → CLIQUE → INDEPENDENT-SET → VERTEX-COVER
 *   SUBSET-SUM and KNAPSACK
 *   HAM-CYCLE → TSP
 *
 * Each reduction is implemented as a function that transforms one problem instance
 * into another, preserving the answer.
 */
public class ReductionDemo {

    // =====================================================================
    //  3-SAT → CLIQUE reduction
    // =====================================================================

    /**
     * Reduces a 3-SAT instance to a CLIQUE instance.
     *
     * For each clause with k literals, create k vertices (one per literal).
     * Add edges between vertices from different clauses unless they are
     * contradictory (xᵢ and ¬xᵢ).
     *
     * The 3-SAT formula is satisfiable iff the graph has a clique of size = #clauses.
     *
     * @return adjacency matrix of the constructed graph + the required clique size
     */
    public static boolean[][] satToClique(SATSolver.Clause[] clauses) {
        // Count total vertices
        int n = 0;
        for (SATSolver.Clause c : clauses) n += c.literals.length;

        boolean[][] adj = new boolean[n][n];
        int[] clauseOf = new int[n];
        SATSolver.Literal[] literalOf = new SATSolver.Literal[n];

        int idx = 0;
        for (int ci = 0; ci < clauses.length; ci++) {
            for (SATSolver.Literal lit : clauses[ci].literals) {
                clauseOf[idx] = ci;
                literalOf[idx] = lit;
                idx++;
            }
        }

        // Connect vertices from different clauses unless contradictory
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                if (clauseOf[i] == clauseOf[j]) continue; // same clause
                if (literalOf[i].variable == literalOf[j].variable
                        && literalOf[i].negated != literalOf[j].negated) continue; // contradictory
                adj[i][j] = true;
                adj[j][i] = true;
            }
        }

        return adj;
    }

    /**
     * Brute-force CLIQUE detection (for small graphs).
     * @return true if there exists a clique of the given size
     */
    public static boolean hasClique(boolean[][] adj, int size) {
        int n = adj.length;
        return findClique(adj, n, size, 0, new int[size], 0);
    }

    private static boolean findClique(boolean[][] adj, int n, int size, int start, int[] clique, int depth) {
        if (depth == size) return true;
        for (int v = start; v < n; v++) {
            boolean canAdd = true;
            for (int i = 0; i < depth; i++) {
                if (!adj[v][clique[i]]) { canAdd = false; break; }
            }
            if (canAdd) {
                clique[depth] = v;
                if (findClique(adj, n, size, v + 1, clique, depth + 1)) return true;
            }
        }
        return false;
    }

    // =====================================================================
    //  CLIQUE → INDEPENDENT-SET reduction
    // =====================================================================

    /**
     * Reduces CLIQUE to INDEPENDENT-SET by complementing the graph.
     * A clique in G = an independent set in the complement of G.
     */
    public static boolean[][] cliqueToIndependentSet(boolean[][] adj) {
        int n = adj.length;
        boolean[][] complement = new boolean[n][n];
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                if (i != j) complement[i][j] = !adj[i][j];
        return complement;
    }

    /**
     * Brute-force maximum independent set.
     */
    public static int maxIndependentSet(boolean[][] adj) {
        int n = adj.length;
        int maxSize = 0;
        for (int mask = 0; mask < (1 << n); mask++) {
            if (isIndependentSet(adj, n, mask)) {
                maxSize = Math.max(maxSize, Integer.bitCount(mask));
            }
        }
        return maxSize;
    }

    private static boolean isIndependentSet(boolean[][] adj, int n, int mask) {
        for (int i = 0; i < n; i++) {
            if ((mask & (1 << i)) == 0) continue;
            for (int j = i + 1; j < n; j++) {
                if ((mask & (1 << j)) == 0) continue;
                if (adj[i][j]) return false;
            }
        }
        return true;
    }

    // =====================================================================
    //  INDEPENDENT-SET → VERTEX-COVER reduction
    // =====================================================================

    /**
     * S is an independent set iff V \ S is a vertex cover.
     * So max independent set = n - min vertex cover.
     */
    public static int minVertexCover(boolean[][] adj) {
        return adj.length - maxIndependentSet(adj);
    }

    // =====================================================================
    //  SUBSET-SUM
    // =====================================================================

    /**
     * Brute-force subset sum.
     * @return true if there exists a subset of values that sums to target
     */
    public static boolean subsetSum(int[] values, int target) {
        int n = values.length;
        for (int mask = 0; mask < (1 << n); mask++) {
            int sum = 0;
            for (int i = 0; i < n; i++) {
                if ((mask & (1 << i)) != 0) sum += values[i];
            }
            if (sum == target) return true;
        }
        return false;
    }

    /**
     * Subset sum via DP — pseudo-polynomial O(n · target).
     */
    public static boolean subsetSumDP(int[] values, int target) {
        boolean[] dp = new boolean[target + 1];
        dp[0] = true;
        for (int v : values) {
            for (int j = target; j >= v; j--) {
                if (dp[j - v]) dp[j] = true;
            }
        }
        return dp[target];
    }

    // =====================================================================
    //  0/1 KNAPSACK
    // =====================================================================

    /**
     * 0/1 Knapsack — classic DP solution.
     * @param weights item weights
     * @param values  item values
     * @param capacity knapsack capacity
     * @return maximum value achievable
     */
    public static int knapsack(int[] weights, int[] values, int capacity) {
        int n = weights.length;
        int[][] dp = new int[n + 1][capacity + 1];
        for (int i = 1; i <= n; i++) {
            for (int w = 0; w <= capacity; w++) {
                dp[i][w] = dp[i - 1][w]; // don't take item i
                if (weights[i - 1] <= w) {
                    dp[i][w] = Math.max(dp[i][w], dp[i - 1][w - weights[i - 1]] + values[i - 1]);
                }
            }
        }
        return dp[n][capacity];
    }

    // =====================================================================
    //  HAM-CYCLE → TSP reduction
    // =====================================================================

    /**
     * Reduces Hamiltonian Cycle to TSP (decision version).
     * Given an adjacency matrix, construct a distance matrix:
     *   dist[i][j] = 1 if edge exists, else n+1
     *
     * The graph has a Hamiltonian cycle iff TSP tour cost ≤ n.
     */
    public static int[][] hamCycleToTSP(boolean[][] adj) {
        int n = adj.length;
        int[][] dist = new int[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                dist[i][j] = adj[i][j] ? 1 : n + 1;
            }
        }
        return dist;
    }
}
