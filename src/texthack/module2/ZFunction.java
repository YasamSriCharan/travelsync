package texthack.module2;

import texthack.collections.SimpleList;

/**
 * Z-function algorithm — the modern alternative to KMP.
 *
 * For a string S of length n, Z[i] = length of the longest substring
 * starting at index i that is also a prefix of S.
 *
 * Time:  O(n) using the Z-box technique
 * Space: O(n)
 */
public class ZFunction {

    /**
     * Computes the Z-array for the given string.
     * Z[0] is defined as 0 (or n, by convention — we use 0).
     */
    public static int[] compute(String s) {
        int n = s.length();
        int[] z = new int[n];
        if (n == 0) return z;

        int left = 0, right = 0;
        for (int i = 1; i < n; i++) {
            if (i < right) {
                z[i] = Math.min(right - i, z[i - left]);
            }
            while (i + z[i] < n && s.charAt(z[i]) == s.charAt(i + z[i])) {
                z[i]++;
            }
            if (i + z[i] > right) {
                left = i;
                right = i + z[i];
            }
        }
        return z;
    }

    /**
     * Finds all occurrences of pattern in text using the Z-function.
     * Constructs the string pattern + "$" + text and uses the Z-array.
     *
     * @return list of starting indices in text where pattern occurs
     */
    public static SimpleList<Integer> search(String text, String pattern) {
        SimpleList<Integer> result = new SimpleList<>();
        if (pattern.isEmpty() || text.isEmpty() || pattern.length() > text.length()) {
            return result;
        }

        String combined = pattern + "$" + text;
        int[] z = compute(combined);
        int m = pattern.length();

        for (int i = m + 1; i < combined.length(); i++) {
            if (z[i] == m) {
                result.add(i - m - 1); // position in original text
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
     * Counts occurrences of pattern in text.
     */
    public static int count(String text, String pattern) {
        return search(text, pattern).size();
    }
}
