package texthack.module6;

/**
 * Randomised Quicksort with Empirical Analysis.
 *
 * Concepts demonstrated:
 *   1. Las Vegas algorithm: Output is ALWAYS correctly sorted, runtime is a random variable.
 *   2. Deterministic Quicksort (e.g. first element as pivot) degrades to O(n²) comparisons
 *      and O(n) recursion depth on already-sorted or reverse-sorted inputs (adversarial input).
 *   3. Randomised Quicksort guarantees E[C] = 2n ln(n) ~ 1.386 n log2(n) comparisons for ANY input,
 *      eliminating adversarial worst-cases without needing input shuffling.
 *   4. Metrics collected: comparison count, max recursion depth, and wall-clock execution time.
 */
public class RandomisedQuicksort {

    public static class SortStats {
        public long comparisons = 0;
        public int maxDepth = 0;
        public long timeNanos = 0;
        public boolean sortedCorrectly = false;
    }

    private static long rngState = 98765432109876543L;

    private static synchronized int randomInt(int min, int max) {
        if (min >= max) return min;
        rngState ^= (rngState << 13);
        rngState ^= (rngState >>> 7);
        rngState ^= (rngState << 17);
        int range = max - min + 1;
        return min + (int) (Math.abs(rngState) % range);
    }

    // ==========================================
    // 1. Deterministic Quicksort (First element pivot)
    // ==========================================
    public static SortStats sortDeterministic(int[] arr) {
        SortStats stats = new SortStats();
        int[] copy = arr.clone();
        long start = System.nanoTime();
        quicksortDet(copy, 0, copy.length - 1, 1, stats);
        stats.timeNanos = System.nanoTime() - start;
        stats.sortedCorrectly = isSorted(copy);
        return stats;
    }

    private static void quicksortDet(int[] arr, int low, int high, int depth, SortStats stats) {
        if (depth > stats.maxDepth) stats.maxDepth = depth;
        if (low < high) {
            int p = partitionDet(arr, low, high, stats);
            quicksortDet(arr, low, p - 1, depth + 1, stats);
            quicksortDet(arr, p + 1, high, depth + 1, stats);
        }
    }

    private static int partitionDet(int[] arr, int low, int high, SortStats stats) {
        int pivot = arr[low];
        int i = low + 1;
        int j = high;

        while (true) {
            while (i <= high) {
                stats.comparisons++;
                if (arr[i] > pivot) break;
                i++;
            }
            while (j >= low + 1) {
                stats.comparisons++;
                if (arr[j] <= pivot) break;
                j--;
            }
            if (i >= j) break;
            swap(arr, i, j);
        }
        swap(arr, low, j);
        return j;
    }

    // ==========================================
    // 2. Randomised Quicksort (Uniform random pivot)
    // ==========================================
    public static SortStats sortRandomised(int[] arr) {
        SortStats stats = new SortStats();
        int[] copy = arr.clone();
        long start = System.nanoTime();
        quicksortRand(copy, 0, copy.length - 1, 1, stats);
        stats.timeNanos = System.nanoTime() - start;
        stats.sortedCorrectly = isSorted(copy);
        return stats;
    }

    private static void quicksortRand(int[] arr, int low, int high, int depth, SortStats stats) {
        if (depth > stats.maxDepth) stats.maxDepth = depth;
        if (low < high) {
            // Pick random pivot and swap with low
            int randPivotIndex = randomInt(low, high);
            swap(arr, low, randPivotIndex);

            int p = partitionDet(arr, low, high, stats);
            quicksortRand(arr, low, p - 1, depth + 1, stats);
            quicksortRand(arr, p + 1, high, depth + 1, stats);
        }
    }

    private static void swap(int[] arr, int i, int j) {
        int temp = arr[i];
        arr[i] = arr[j];
        arr[j] = temp;
    }

    public static boolean isSorted(int[] arr) {
        for (int i = 1; i < arr.length; i++) {
            if (arr[i] < arr[i - 1]) return false;
        }
        return true;
    }

    /**
     * Theoretical expectation for quicksort comparisons on n distinct elements:
     * 2 * n * ln(n)
     */
    public static double theoreticalExpectedComparisons(int n) {
        if (n <= 1) return 0;
        return 2.0 * n * Math.log(n);
    }
}
