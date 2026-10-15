package texthack.module3;

/**
 * Sequence Alignment algorithms for genome data.
 *
 * Implements:
 *   1. Needleman-Wunsch — global alignment (aligns entire sequences)
 *   2. Smith-Waterman   — local alignment (finds best matching subsequence)
 *
 * Both support configurable match/mismatch/gap penalties.
 * Time:  O(nm)
 * Space: O(nm) for full matrix with traceback
 */
public class SequenceAlignment {

    /** Alignment result with score, aligned sequences, and the DP matrix. */
    public static class AlignmentResult {
        public final int score;
        public final String alignedSeq1;
        public final String alignedSeq2;
        public final int[][] matrix;

        AlignmentResult(int score, String alignedSeq1, String alignedSeq2, int[][] matrix) {
            this.score = score;
            this.alignedSeq1 = alignedSeq1;
            this.alignedSeq2 = alignedSeq2;
            this.matrix = matrix;
        }

        @Override
        public String toString() {
            return "Score: " + score + "\n" + alignedSeq1 + "\n" + alignedSeq2;
        }
    }

    // Default scoring
    private static final int MATCH = 2;
    private static final int MISMATCH = -1;
    private static final int GAP = -2;

    /**
     * Needleman-Wunsch global alignment.
     * Aligns the entire length of both sequences.
     */
    public static AlignmentResult needlemanWunsch(String s1, String s2) {
        return needlemanWunsch(s1, s2, MATCH, MISMATCH, GAP);
    }

    /**
     * Needleman-Wunsch with configurable scoring.
     */
    public static AlignmentResult needlemanWunsch(String s1, String s2,
                                                   int matchScore, int mismatchPenalty, int gapPenalty) {
        int n = s1.length(), m = s2.length();
        int[][] dp = new int[n + 1][m + 1];

        // Initialise borders
        for (int i = 0; i <= n; i++) dp[i][0] = i * gapPenalty;
        for (int j = 0; j <= m; j++) dp[0][j] = j * gapPenalty;

        // Fill matrix
        for (int i = 1; i <= n; i++) {
            for (int j = 1; j <= m; j++) {
                int score = (s1.charAt(i - 1) == s2.charAt(j - 1)) ? matchScore : mismatchPenalty;
                dp[i][j] = max3(
                    dp[i - 1][j - 1] + score,  // match/mismatch
                    dp[i - 1][j] + gapPenalty,  // gap in s2
                    dp[i][j - 1] + gapPenalty   // gap in s1
                );
            }
        }

        // Traceback
        StringBuilder aligned1 = new StringBuilder();
        StringBuilder aligned2 = new StringBuilder();
        int i = n, j = m;

        while (i > 0 || j > 0) {
            if (i > 0 && j > 0) {
                int score = (s1.charAt(i - 1) == s2.charAt(j - 1)) ? matchScore : mismatchPenalty;
                if (dp[i][j] == dp[i - 1][j - 1] + score) {
                    aligned1.insert(0, s1.charAt(i - 1));
                    aligned2.insert(0, s2.charAt(j - 1));
                    i--; j--;
                    continue;
                }
            }
            if (i > 0 && dp[i][j] == dp[i - 1][j] + gapPenalty) {
                aligned1.insert(0, s1.charAt(i - 1));
                aligned2.insert(0, '-');
                i--;
            } else {
                aligned1.insert(0, '-');
                aligned2.insert(0, s2.charAt(j - 1));
                j--;
            }
        }

        return new AlignmentResult(dp[n][m], aligned1.toString(), aligned2.toString(), dp);
    }

    /**
     * Smith-Waterman local alignment.
     * Finds the highest-scoring local alignment (best matching subsequence).
     */
    public static AlignmentResult smithWaterman(String s1, String s2) {
        return smithWaterman(s1, s2, MATCH, MISMATCH, GAP);
    }

    /**
     * Smith-Waterman with configurable scoring.
     */
    public static AlignmentResult smithWaterman(String s1, String s2,
                                                 int matchScore, int mismatchPenalty, int gapPenalty) {
        int n = s1.length(), m = s2.length();
        int[][] dp = new int[n + 1][m + 1];

        int maxScore = 0, maxI = 0, maxJ = 0;

        // Fill matrix (borders are 0 — local alignment allows starting anywhere)
        for (int i = 1; i <= n; i++) {
            for (int j = 1; j <= m; j++) {
                int score = (s1.charAt(i - 1) == s2.charAt(j - 1)) ? matchScore : mismatchPenalty;
                dp[i][j] = Math.max(0, max3(
                    dp[i - 1][j - 1] + score,
                    dp[i - 1][j] + gapPenalty,
                    dp[i][j - 1] + gapPenalty
                ));
                if (dp[i][j] > maxScore) {
                    maxScore = dp[i][j];
                    maxI = i;
                    maxJ = j;
                }
            }
        }

        // Traceback from the maximum score cell until we hit 0
        StringBuilder aligned1 = new StringBuilder();
        StringBuilder aligned2 = new StringBuilder();
        int i = maxI, j = maxJ;

        while (i > 0 && j > 0 && dp[i][j] > 0) {
            int score = (s1.charAt(i - 1) == s2.charAt(j - 1)) ? matchScore : mismatchPenalty;
            if (dp[i][j] == dp[i - 1][j - 1] + score) {
                aligned1.insert(0, s1.charAt(i - 1));
                aligned2.insert(0, s2.charAt(j - 1));
                i--; j--;
            } else if (dp[i][j] == dp[i - 1][j] + gapPenalty) {
                aligned1.insert(0, s1.charAt(i - 1));
                aligned2.insert(0, '-');
                i--;
            } else {
                aligned1.insert(0, '-');
                aligned2.insert(0, s2.charAt(j - 1));
                j--;
            }
        }

        return new AlignmentResult(maxScore, aligned1.toString(), aligned2.toString(), dp);
    }

    private static int max3(int a, int b, int c) {
        return Math.max(a, Math.max(b, c));
    }
}
