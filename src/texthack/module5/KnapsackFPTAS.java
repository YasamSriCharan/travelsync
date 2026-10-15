package texthack.module5;

/**
 * Knapsack FPTAS — Fully Polynomial-Time Approximation Scheme.
 *
 * For any ε > 0, achieves a (1 − ε)-approximation in time O(n² / ε).
 *
 * The key idea: scale down the profits by a factor that makes the DP table
 * size polynomial, then solve the scaled problem exactly.
 *
 * This demonstrates problems where (1+ε) approximations exist (contrast
 * with problems like MAX-CLIQUE where they don't unless P = NP).
 */
public class KnapsackFPTAS {

    /**
     * Exact 0/1 Knapsack (baseline comparison).
     * Time: O(n · W) — pseudo-polynomial
     */
    public static int exactKnapsack(int[] weights, int[] values, int capacity) {
        int n = weights.length;
        int[][] dp = new int[n + 1][capacity + 1];
        for (int i = 1; i <= n; i++) {
            for (int w = 0; w <= capacity; w++) {
                dp[i][w] = dp[i - 1][w];
                if (weights[i - 1] <= w) {
                    dp[i][w] = Math.max(dp[i][w], dp[i - 1][w - weights[i - 1]] + values[i - 1]);
                }
            }
        }
        return dp[n][capacity];
    }

    /**
     * FPTAS for 0/1 Knapsack.
     *
     * @param weights  item weights
     * @param values   item values
     * @param capacity knapsack capacity
     * @param epsilon  approximation parameter (0 < ε < 1)
     * @return approximate value ≥ (1 − ε) × OPT
     *
     * Time: O(n² / ε)
     */
    public static int fptas(int[] weights, int[] values, int capacity, double epsilon) {
        int n = weights.length;
        if (n == 0) return 0;

        // Find maximum value
        int maxVal = 0;
        for (int v : values) maxVal = Math.max(maxVal, v);

        if (maxVal == 0) return 0;

        // Scaling factor: K = (ε · maxVal) / n
        double K = (epsilon * maxVal) / n;
        if (K < 1) K = 1; // don't scale up

        // Scale down values
        int[] scaledValues = new int[n];
        for (int i = 0; i < n; i++) {
            scaledValues[i] = (int) Math.floor(values[i] / K);
        }

        // Solve scaled problem exactly
        // DP by value: dp[v] = minimum weight to achieve value v
        int maxScaledSum = 0;
        for (int sv : scaledValues) maxScaledSum += sv;

        long[] dp = new long[maxScaledSum + 1];
        for (int i = 0; i <= maxScaledSum; i++) dp[i] = Long.MAX_VALUE;
        dp[0] = 0;

        for (int i = 0; i < n; i++) {
            for (int v = maxScaledSum; v >= scaledValues[i]; v--) {
                if (dp[v - scaledValues[i]] != Long.MAX_VALUE) {
                    dp[v] = Math.min(dp[v], dp[v - scaledValues[i]] + weights[i]);
                }
            }
        }

        // Find maximum achievable scaled value within capacity
        int bestScaled = 0;
        for (int v = maxScaledSum; v >= 0; v--) {
            if (dp[v] <= capacity) {
                bestScaled = v;
                break;
            }
        }

        // Reconstruct: use original values with items chosen for scaled solution
        // For simplicity, return the scaled value mapped back
        // We redo the DP with original values for the items in the solution
        return reconstructOriginalValue(weights, values, scaledValues, capacity, bestScaled);
    }

    /**
     * Reconstructs the actual value by solving again with item tracking.
     */
    private static int reconstructOriginalValue(int[] weights, int[] values,
                                                  int[] scaledValues, int capacity,
                                                  int targetScaled) {
        int n = weights.length;

        // Trace which items achieve targetScaled with minimum weight
        long[] dp = new long[targetScaled + 1];
        int[][] choice = new int[n][targetScaled + 1]; // 0 = not taken, 1 = taken

        for (int i = 0; i <= targetScaled; i++) dp[i] = Long.MAX_VALUE;
        dp[0] = 0;

        for (int i = 0; i < n; i++) {
            for (int v = targetScaled; v >= scaledValues[i]; v--) {
                if (dp[v - scaledValues[i]] != Long.MAX_VALUE
                        && dp[v - scaledValues[i]] + weights[i] < dp[v]) {
                    dp[v] = dp[v - scaledValues[i]] + weights[i];
                    choice[i][v] = 1;
                }
            }
        }

        // Sum original values of chosen items
        int totalValue = 0;
        int v = targetScaled;
        for (int i = n - 1; i >= 0; i--) {
            if (choice[i][v] == 1) {
                totalValue += values[i];
                v -= scaledValues[i];
            }
        }
        return totalValue;
    }

    /**
     * Demonstrates the approximation quality by comparing FPTAS vs exact.
     */
    public static String compareResults(int[] weights, int[] values, int capacity, double epsilon) {
        int exact = exactKnapsack(weights, values, capacity);
        int approx = fptas(weights, values, capacity, epsilon);
        double ratio = exact > 0 ? (double) approx / exact : 1.0;

        return "Exact: " + exact + "\n"
             + "FPTAS (ε=" + epsilon + "): " + approx + "\n"
             + "Ratio: " + String.format("%.4f", ratio) + "\n"
             + "Guaranteed ≥ " + String.format("%.4f", 1.0 - epsilon);
    }
}
