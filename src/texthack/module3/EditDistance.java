package texthack.module3;

/**
 * Edit Distance algorithms — the core of fuzzy matching in TextHack.
 *
 * Implements:
 *   1. Levenshtein distance (insert, delete, substitute)
 *   2. Damerau-Levenshtein distance (adds transposition)
 *   3. Weighted edit distance (custom operation costs)
 *
 * All use the Wagner-Fischer bottom-up DP approach.
 * Time:  O(nm) where n = |s1|, m = |s2|
 * Space: O(nm) for full matrix, O(min(n,m)) for space-optimised
 */
public class EditDistance {

    /**
     * Standard Levenshtein distance.
     * Operations: insert (cost 1), delete (cost 1), substitute (cost 1).
     */
    public static int levenshtein(String s1, String s2) {
        int n = s1.length(), m = s2.length();
        int[][] dp = new int[n + 1][m + 1];

        for (int i = 0; i <= n; i++) dp[i][0] = i;
        for (int j = 0; j <= m; j++) dp[0][j] = j;

        for (int i = 1; i <= n; i++) {
            for (int j = 1; j <= m; j++) {
                int cost = (s1.charAt(i - 1) == s2.charAt(j - 1)) ? 0 : 1;
                dp[i][j] = min3(
                    dp[i - 1][j] + 1,       // deletion
                    dp[i][j - 1] + 1,        // insertion
                    dp[i - 1][j - 1] + cost  // substitution
                );
            }
        }
        return dp[n][m];
    }

    /**
     * Damerau-Levenshtein distance.
     * Adds transposition of two adjacent characters as a fourth operation.
     */
    public static int damerauLevenshtein(String s1, String s2) {
        int n = s1.length(), m = s2.length();

        // We need dp[i-2][j-2] so a simple 2D array is used
        int[][] dp = new int[n + 1][m + 1];

        for (int i = 0; i <= n; i++) dp[i][0] = i;
        for (int j = 0; j <= m; j++) dp[0][j] = j;

        for (int i = 1; i <= n; i++) {
            for (int j = 1; j <= m; j++) {
                int cost = (s1.charAt(i - 1) == s2.charAt(j - 1)) ? 0 : 1;
                dp[i][j] = min3(
                    dp[i - 1][j] + 1,
                    dp[i][j - 1] + 1,
                    dp[i - 1][j - 1] + cost
                );
                // Transposition
                if (i > 1 && j > 1
                        && s1.charAt(i - 1) == s2.charAt(j - 2)
                        && s1.charAt(i - 2) == s2.charAt(j - 1)) {
                    dp[i][j] = Math.min(dp[i][j], dp[i - 2][j - 2] + cost);
                }
            }
        }
        return dp[n][m];
    }

    /**
     * Weighted edit distance with custom costs.
     * @param insertCost   cost of inserting a character
     * @param deleteCost   cost of deleting a character
     * @param substituteCost cost of substituting a character
     */
    public static int weighted(String s1, String s2,
                               int insertCost, int deleteCost, int substituteCost) {
        int n = s1.length(), m = s2.length();
        int[][] dp = new int[n + 1][m + 1];

        for (int i = 0; i <= n; i++) dp[i][0] = i * deleteCost;
        for (int j = 0; j <= m; j++) dp[0][j] = j * insertCost;

        for (int i = 1; i <= n; i++) {
            for (int j = 1; j <= m; j++) {
                if (s1.charAt(i - 1) == s2.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1];
                } else {
                    dp[i][j] = min3(
                        dp[i - 1][j] + deleteCost,
                        dp[i][j - 1] + insertCost,
                        dp[i - 1][j - 1] + substituteCost
                    );
                }
            }
        }
        return dp[n][m];
    }

    /**
     * Space-optimised Levenshtein using only two rows.
     * Space: O(min(n, m))
     */
    public static int levenshteinOptimised(String s1, String s2) {
        // Make sure s2 is the shorter string for space optimisation
        if (s1.length() < s2.length()) {
            String temp = s1; s1 = s2; s2 = temp;
        }
        int n = s1.length(), m = s2.length();
        int[] prev = new int[m + 1];
        int[] curr = new int[m + 1];

        for (int j = 0; j <= m; j++) prev[j] = j;

        for (int i = 1; i <= n; i++) {
            curr[0] = i;
            for (int j = 1; j <= m; j++) {
                int cost = (s1.charAt(i - 1) == s2.charAt(j - 1)) ? 0 : 1;
                curr[j] = min3(
                    prev[j] + 1,
                    curr[j - 1] + 1,
                    prev[j - 1] + cost
                );
            }
            int[] temp = prev; prev = curr; curr = temp;
        }
        return prev[m];
    }

    /**
     * Returns the full DP matrix for visualisation / traceback.
     */
    public static int[][] levenshteinMatrix(String s1, String s2) {
        int n = s1.length(), m = s2.length();
        int[][] dp = new int[n + 1][m + 1];
        for (int i = 0; i <= n; i++) dp[i][0] = i;
        for (int j = 0; j <= m; j++) dp[0][j] = j;
        for (int i = 1; i <= n; i++) {
            for (int j = 1; j <= m; j++) {
                int cost = (s1.charAt(i - 1) == s2.charAt(j - 1)) ? 0 : 1;
                dp[i][j] = min3(dp[i - 1][j] + 1, dp[i][j - 1] + 1, dp[i - 1][j - 1] + cost);
            }
        }
        return dp;
    }

    /**
     * Computes edit distance similarity as a ratio in [0, 1].
     * 1.0 = identical, 0.0 = completely different.
     */
    public static double similarity(String s1, String s2) {
        if (s1.isEmpty() && s2.isEmpty()) return 1.0;
        int maxLen = Math.max(s1.length(), s2.length());
        int dist = levenshtein(s1, s2);
        return 1.0 - (double) dist / maxLen;
    }

    private static int min3(int a, int b, int c) {
        return Math.min(a, Math.min(b, c));
    }
}
