package texthack.module2;

import texthack.collections.SimpleList;

/**
 * Rabin-Karp algorithm with polynomial rolling hash.
 *
 * Average case: O(n + m)
 * Worst case:   O(nm) — but double-hashing makes collisions extremely rare.
 *
 * Uses two independent hash functions to virtually eliminate false positives.
 * The modular arithmetic discipline: all hash computations use (value % MOD + MOD) % MOD
 * to handle negative numbers correctly.
 */
public class RabinKarp {

    // Two large primes for double hashing
    private static final long MOD1 = 1_000_000_007L;
    private static final long MOD2 = 998_244_353L;
    private static final long BASE1 = 31L;
    private static final long BASE2 = 37L;

    /**
     * Finds all occurrences of pattern in text using Rabin-Karp with double hashing.
     * @return list of starting indices where pattern occurs
     */
    public static SimpleList<Integer> search(String text, String pattern) {
        SimpleList<Integer> result = new SimpleList<>();
        int n = text.length(), m = pattern.length();
        if (m == 0 || n < m) return result;

        // Compute hash of pattern and the first window of text
        long patHash1 = 0, patHash2 = 0;
        long textHash1 = 0, textHash2 = 0;
        long power1 = 1, power2 = 1;

        for (int i = 0; i < m; i++) {
            patHash1 = (patHash1 * BASE1 + pattern.charAt(i)) % MOD1;
            patHash2 = (patHash2 * BASE2 + pattern.charAt(i)) % MOD2;
            textHash1 = (textHash1 * BASE1 + text.charAt(i)) % MOD1;
            textHash2 = (textHash2 * BASE2 + text.charAt(i)) % MOD2;
            if (i > 0) {
                power1 = (power1 * BASE1) % MOD1;
                power2 = (power2 * BASE2) % MOD2;
            }
        }

        for (int i = 0; i <= n - m; i++) {
            // Check both hashes match to avoid false positives
            if (textHash1 == patHash1 && textHash2 == patHash2) {
                // Verify character by character (belt-and-suspenders)
                if (verifyMatch(text, i, pattern)) {
                    result.add(i);
                }
            }

            // Roll the hash: remove leading character, add trailing character
            if (i + m < n) {
                textHash1 = ((textHash1 - text.charAt(i) * power1 % MOD1 + MOD1) * BASE1
                            + text.charAt(i + m)) % MOD1;
                textHash2 = ((textHash2 - text.charAt(i) * power2 % MOD2 + MOD2) * BASE2
                            + text.charAt(i + m)) % MOD2;
            }
        }
        return result;
    }

    /**
     * Multi-pattern search: finds occurrences of any pattern from the set.
     * Uses rolling hash for each pattern length group.
     * @return map-like result: pattern → list of starting positions
     */
    public static SimpleList<int[]> searchMultiple(String text, String[] patterns) {
        SimpleList<int[]> results = new SimpleList<>();
        for (int p = 0; p < patterns.length; p++) {
            SimpleList<Integer> matches = search(text, patterns[p]);
            for (int j = 0; j < matches.size(); j++) {
                results.add(new int[]{p, matches.get(j)});
            }
        }
        return results;
    }

    /**
     * Computes the polynomial rolling hash of a string.
     * Useful for document fingerprinting.
     */
    public static long hash(String s) {
        long h = 0;
        for (int i = 0; i < s.length(); i++) {
            h = (h * BASE1 + s.charAt(i)) % MOD1;
        }
        return h;
    }

    private static boolean verifyMatch(String text, int start, String pattern) {
        for (int j = 0; j < pattern.length(); j++) {
            if (text.charAt(start + j) != pattern.charAt(j)) return false;
        }
        return true;
    }
}
