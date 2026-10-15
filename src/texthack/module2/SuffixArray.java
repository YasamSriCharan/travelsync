package texthack.module2;

import texthack.collections.SimpleList;

/**
 * Suffix Array with LCP (Kasai algorithm).
 *
 * Suffix array:  a sorted array of all suffixes of a string, represented as
 *                starting indices. Enables O(m log n) pattern matching via binary search.
 *
 * Construction:  O(n log² n) using the prefix-doubling approach.
 *                (The O(n) SA-IS algorithm is conceptual; this practical variant is used.)
 *
 * LCP array:     Kasai's algorithm computes the Longest Common Prefix array in O(n).
 *                LCP[i] = length of the longest common prefix between suffix sa[i] and sa[i-1].
 */
public class SuffixArray {

    private final String text;
    private final int[] sa;      // suffix array
    private int[] lcp;           // LCP array (computed on demand)
    private int[] rank;          // rank array (inverse of sa)

    /**
     * Constructs the suffix array for the given text in O(n log² n).
     */
    public SuffixArray(String text) {
        this.text = text;
        int n = text.length();
        this.sa = new int[n];
        this.rank = new int[n];
        buildSuffixArray();
    }

    /** Returns the suffix array. */
    public int[] getSuffixArray() {
        return sa;
    }

    /** Returns the rank (inverse SA) array. */
    public int[] getRank() {
        return rank;
    }

    /**
     * Computes and returns the LCP array using Kasai's algorithm in O(n).
     * LCP[i] = length of the longest common prefix between sa[i] and sa[i-1].
     * LCP[0] = 0 by definition.
     */
    public int[] getLCP() {
        if (lcp != null) return lcp;
        int n = text.length();
        lcp = new int[n];

        // Build rank from sa
        rank = new int[n];
        for (int i = 0; i < n; i++) rank[sa[i]] = i;

        int h = 0;
        for (int i = 0; i < n; i++) {
            if (rank[i] > 0) {
                int j = sa[rank[i] - 1];
                while (i + h < n && j + h < n && text.charAt(i + h) == text.charAt(j + h)) {
                    h++;
                }
                lcp[rank[i]] = h;
                if (h > 0) h--;
            } else {
                h = 0;
            }
        }
        return lcp;
    }

    /**
     * Searches for pattern in text using the suffix array (binary search).
     * Time: O(m log n) where m = |pattern|, n = |text|
     * @return list of starting positions where pattern occurs
     */
    public SimpleList<Integer> search(String pattern) {
        SimpleList<Integer> results = new SimpleList<>();
        int n = text.length(), m = pattern.length();
        if (m == 0 || n == 0) return results;

        // Find the lower bound
        int lo = 0, hi = n - 1;
        int lowerBound = n;
        while (lo <= hi) {
            int mid = lo + (hi - lo) / 2;
            int cmp = compareAt(sa[mid], pattern);
            if (cmp >= 0) {
                if (cmp == 0) lowerBound = mid;
                hi = mid - 1;
            } else {
                lo = mid + 1;
            }
        }

        if (lowerBound == n) return results;

        // Find the upper bound
        lo = lowerBound;
        hi = n - 1;
        int upperBound = lowerBound;
        while (lo <= hi) {
            int mid = lo + (hi - lo) / 2;
            int cmp = compareAt(sa[mid], pattern);
            if (cmp == 0) {
                upperBound = mid;
                lo = mid + 1;
            } else {
                hi = mid - 1;
            }
        }

        // Collect all matches
        for (int i = lowerBound; i <= upperBound; i++) {
            results.add(sa[i]);
        }
        return results;
    }

    /**
     * Finds the longest repeated substring using the LCP array.
     * @return the longest substring that appears at least twice
     */
    public String longestRepeatedSubstring() {
        int[] lcpArr = getLCP();
        int maxLen = 0, maxIdx = 0;
        for (int i = 1; i < lcpArr.length; i++) {
            if (lcpArr[i] > maxLen) {
                maxLen = lcpArr[i];
                maxIdx = i;
            }
        }
        if (maxLen == 0) return "";
        return text.substring(sa[maxIdx], sa[maxIdx] + maxLen);
    }

    /**
     * Counts the number of distinct substrings.
     * Total substrings = n(n+1)/2. Subtract sum of LCP values.
     */
    public long countDistinctSubstrings() {
        int n = text.length();
        long total = (long) n * (n + 1) / 2;
        int[] lcpArr = getLCP();
        for (int i = 1; i < n; i++) {
            total -= lcpArr[i];
        }
        return total;
    }

    // ----- O(n log² n) prefix-doubling construction -----
    private void buildSuffixArray() {
        int n = text.length();
        if (n == 0) return;

        // Initial ranking by single character
        Integer[] order = new Integer[n];
        int[] tempRank = new int[n];
        for (int i = 0; i < n; i++) {
            order[i] = i;
            rank[i] = text.charAt(i);
        }

        for (int gap = 1; gap < n; gap <<= 1) {
            final int g = gap;
            final int[] r = rank;

            // Sort by (rank[i], rank[i + gap])
            sort(order, (a, b) -> {
                if (r[a] != r[b]) return r[a] - r[b];
                int ra = a + g < n ? r[a + g] : -1;
                int rb = b + g < n ? r[b + g] : -1;
                return ra - rb;
            });

            // Recompute ranks
            tempRank[order[0]] = 0;
            for (int i = 1; i < n; i++) {
                tempRank[order[i]] = tempRank[order[i - 1]];
                if (r[order[i]] != r[order[i - 1]]
                        || (order[i] + g < n ? r[order[i] + g] : -1)
                           != (order[i - 1] + g < n ? r[order[i - 1] + g] : -1)) {
                    tempRank[order[i]]++;
                }
            }
            System.arraycopy(tempRank, 0, rank, 0, n);
        }

        for (int i = 0; i < n; i++) sa[i] = order[i];
    }

    /** Comparator interface to avoid java.util.Comparator. */
    @FunctionalInterface
    private interface Cmp {
        int compare(int a, int b);
    }

    /** Merge sort using a custom comparator. */
    private void sort(Integer[] arr, Cmp cmp) {
        if (arr.length <= 1) return;
        Integer[] aux = new Integer[arr.length];
        mergeSort(arr, aux, 0, arr.length - 1, cmp);
    }

    private void mergeSort(Integer[] arr, Integer[] aux, int lo, int hi, Cmp cmp) {
        if (lo >= hi) return;
        int mid = lo + (hi - lo) / 2;
        mergeSort(arr, aux, lo, mid, cmp);
        mergeSort(arr, aux, mid + 1, hi, cmp);
        merge(arr, aux, lo, mid, hi, cmp);
    }

    private void merge(Integer[] arr, Integer[] aux, int lo, int mid, int hi, Cmp cmp) {
        System.arraycopy(arr, lo, aux, lo, hi - lo + 1);
        int i = lo, j = mid + 1;
        for (int k = lo; k <= hi; k++) {
            if (i > mid) arr[k] = aux[j++];
            else if (j > hi) arr[k] = aux[i++];
            else if (cmp.compare(aux[i], aux[j]) <= 0) arr[k] = aux[i++];
            else arr[k] = aux[j++];
        }
    }

    /** Compares text starting at pos with the full pattern. */
    private int compareAt(int pos, String pattern) {
        int n = text.length(), m = pattern.length();
        for (int i = 0; i < m; i++) {
            if (pos + i >= n) return -1;
            if (text.charAt(pos + i) < pattern.charAt(i)) return -1;
            if (text.charAt(pos + i) > pattern.charAt(i)) return 1;
        }
        return 0;
    }
}
