package texthack.module3;

/**
 * Interval DP algorithms.
 *
 * Implements:
 *   1. Matrix-chain multiplication — optimal parenthesisation O(n³)
 *   2. Optimal Binary Search Tree (Knuth's optimisation) — O(n²)
 */
public class IntervalDP {

    // =====================================================================
    //  Matrix Chain Multiplication
    // =====================================================================

    /**
     * Result of matrix-chain multiplication optimisation.
     */
    public static class MCMResult {
        public final long minCost;           // minimum scalar multiplications
        public final int[][] splitTable;     // s[i][j] = optimal split point
        public final String parenthesisation;

        MCMResult(long minCost, int[][] splitTable, String parenthesisation) {
            this.minCost = minCost;
            this.splitTable = splitTable;
            this.parenthesisation = parenthesisation;
        }
    }

    /**
     * Solves the matrix-chain multiplication problem.
     * @param dims array where matrix Aᵢ has dimensions dims[i] × dims[i+1]
     * @return MCMResult with minimum cost and optimal parenthesisation
     *
     * Time:  O(n³) where n = number of matrices
     * Space: O(n²)
     */
    public static MCMResult matrixChain(int[] dims) {
        int n = dims.length - 1; // number of matrices
        long[][] dp = new long[n][n];
        int[][] split = new int[n][n];

        // dp[i][j] = min cost of multiplying matrices i..j
        // base case: dp[i][i] = 0 (single matrix, no multiplication)

        for (int len = 2; len <= n; len++) {          // chain length
            for (int i = 0; i <= n - len; i++) {
                int j = i + len - 1;
                dp[i][j] = Long.MAX_VALUE;
                for (int k = i; k < j; k++) {
                    long cost = dp[i][k] + dp[k + 1][j]
                               + (long) dims[i] * dims[k + 1] * dims[j + 1];
                    if (cost < dp[i][j]) {
                        dp[i][j] = cost;
                        split[i][j] = k;
                    }
                }
            }
        }

        String paren = buildParenthesisation(split, 0, n - 1);
        return new MCMResult(dp[0][n - 1], split, paren);
    }

    private static String buildParenthesisation(int[][] split, int i, int j) {
        if (i == j) return "A" + (i + 1);
        return "(" + buildParenthesisation(split, i, split[i][j])
               + " × " + buildParenthesisation(split, split[i][j] + 1, j) + ")";
    }

    // =====================================================================
    //  Optimal Binary Search Tree
    // =====================================================================

    /**
     * Result of optimal BST computation.
     */
    public static class OBSTResult {
        public final double minCost;
        public final int[][] root;

        OBSTResult(double minCost, int[][] root) {
            this.minCost = minCost;
            this.root = root;
        }
    }

    /**
     * Computes the optimal BST given key probabilities and dummy key probabilities.
     *
     * @param p    p[i] = probability of searching for key i (1-indexed, p[0] unused)
     * @param q    q[i] = probability of searching between key i and key i+1 (0-indexed)
     * @param n    number of keys
     * @return OBSTResult with minimum expected search cost
     *
     * Uses Knuth's optimisation: root[i][j-1] ≤ root[i][j] ≤ root[i+1][j]
     * bringing the time from O(n³) down to O(n²).
     */
    public static OBSTResult optimalBST(double[] p, double[] q, int n) {
        double[][] e = new double[n + 2][n + 1]; // expected cost
        double[][] w = new double[n + 2][n + 1]; // weight (sum of probabilities)
        int[][] root = new int[n + 1][n + 1];

        // Base case: dummy keys
        for (int i = 1; i <= n + 1; i++) {
            e[i][i - 1] = q[i - 1];
            w[i][i - 1] = q[i - 1];
        }

        for (int len = 1; len <= n; len++) {
            for (int i = 1; i <= n - len + 1; i++) {
                int j = i + len - 1;
                e[i][j] = Double.MAX_VALUE;
                w[i][j] = w[i][j - 1] + p[j] + q[j];

                // Knuth's optimisation bounds
                int rLo = (len == 1) ? i : root[i][j - 1];
                int rHi = (j == n) ? j : root[i + 1][j];
                rHi = Math.min(rHi, j);

                for (int r = rLo; r <= rHi; r++) {
                    double cost = e[i][r - 1] + e[r + 1][j] + w[i][j];
                    if (cost < e[i][j]) {
                        e[i][j] = cost;
                        root[i][j] = r;
                    }
                }
            }
        }

        return new OBSTResult(e[1][n], root);
    }
}
