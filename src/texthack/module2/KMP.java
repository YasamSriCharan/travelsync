package texthack.module2;

import texthack.collections.SimpleList;

/**
 * Knuth-Morris-Pratt (KMP) algorithm for exact pattern matching.
 *
 * Time:  O(n + m) where n = |text|, m = |pattern|
 * Space: O(m) for the failure function
 *
 * The failure function π[i] gives the length of the longest proper
 * prefix of pattern[0..i] that is also a suffix. This allows the
 * algorithm to skip redundant comparisons after a mismatch.
 */
public class KMP {

    /**
     * Computes the failure function (also called partial match table).
     * π[i] = length of the longest proper prefix of pattern[0..i]
     *        that is also a suffix of pattern[0..i].
     */
    public static int[] computeFailure(String pattern) {
        int m = pattern.length();
        int[] failure = new int[m];
        failure[0] = 0;
        int k = 0; // length of the previous longest prefix suffix

        for (int i = 1; i < m; i++) {
            while (k > 0 && pattern.charAt(k) != pattern.charAt(i)) {
                k = failure[k - 1];
            }
            if (pattern.charAt(k) == pattern.charAt(i)) {
                k++;
            }
            failure[i] = k;
        }
        return failure;
    }

    /**
     * Finds all occurrences of pattern in text using KMP.
     * @return list of starting indices where pattern occurs in text
     */
    public static SimpleList<Integer> search(String text, String pattern) {
        SimpleList<Integer> result = new SimpleList<>();
        if (pattern.isEmpty() || text.isEmpty() || pattern.length() > text.length()) {
            return result;
        }

        int n = text.length();
        int m = pattern.length();
        int[] failure = computeFailure(pattern);
        int q = 0; // number of characters matched

        for (int i = 0; i < n; i++) {
            while (q > 0 && pattern.charAt(q) != text.charAt(i)) {
                q = failure[q - 1];
            }
            if (pattern.charAt(q) == text.charAt(i)) {
                q++;
            }
            if (q == m) {
                result.add(i - m + 1);
                q = failure[q - 1]; // look for next match
            }
        }
        return result;
    }

    /**
     * Returns the first occurrence of pattern in text, or -1 if not found.
     */
    public static int searchFirst(String text, String pattern) {
        SimpleList<Integer> matches = search(text, pattern);
        return matches.isEmpty() ? -1 : matches.get(0);
    }

    /**
     * Counts the number of (possibly overlapping) occurrences.
     */
    public static int count(String text, String pattern) {
        return search(text, pattern).size();
    }
}
