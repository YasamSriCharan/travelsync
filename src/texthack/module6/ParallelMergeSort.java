package texthack.module6;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveAction;

/**
 * Parallel Merge Sort — Parallel Divide & Conquer.
 *
 * Concepts demonstrated:
 *   1. Parallel divide-and-conquer: Left and right subproblems are independent and run concurrently.
 *   2. Sequential threshold: Once subproblems fall below a threshold (e.g. 4096),
 *      sequential sorting runs directly to avoid task spawning overhead.
 *   3. Parallel Merge: To achieve polylogarithmic span O(log² n) or O(log³ n), the merge itself
 *      can be parallelised using binary search splitting instead of sequential linear merge.
 *   4. Scalability benchmark: Measures speedup vs number of available worker threads.
 */
public class ParallelMergeSort {

    private static final int SEQ_SORT_THRESHOLD = 4096;
    private static final int SEQ_MERGE_THRESHOLD = 2048;
    private static final ForkJoinPool POOL = new ForkJoinPool();

    public static class SortMetrics {
        public final long elapsedNanos;
        public final boolean isSorted;
        public final int elements;

        public SortMetrics(long elapsedNanos, boolean isSorted, int elements) {
            this.elapsedNanos = elapsedNanos;
            this.isSorted = isSorted;
            this.elements = elements;
        }

        @Override
        public String toString() {
            return String.format("Sorted %d elements in %.3f ms (verified: %s)",
                    elements, elapsedNanos / 1e6, isSorted);
        }
    }

    /**
     * Parallel sort an array of integers.
     */
    public static SortMetrics sort(int[] array) {
        int[] copy = array.clone();
        int[] aux = new int[array.length];

        long start = System.nanoTime();
        POOL.invoke(new ParallelSortTask(copy, aux, 0, array.length));
        long duration = System.nanoTime() - start;

        // Copy back sorted result
        System.arraycopy(copy, 0, array, 0, array.length);

        boolean sorted = isSorted(array);
        return new SortMetrics(duration, sorted, array.length);
    }

    private static class ParallelSortTask extends RecursiveAction {
        private final int[] a;
        private final int[] aux;
        private final int low;
        private final int high;

        public ParallelSortTask(int[] a, int[] aux, int low, int high) {
            this.a = a;
            this.aux = aux;
            this.low = low;
            this.high = high;
        }

        @Override
        protected void compute() {
            int len = high - low;
            if (len <= SEQ_SORT_THRESHOLD) {
                sequentialMergeSort(a, aux, low, high);
                return;
            }

            int mid = low + len / 2;
            ParallelSortTask left = new ParallelSortTask(a, aux, low, mid);
            ParallelSortTask right = new ParallelSortTask(a, aux, mid, high);

            invokeAll(left, right);

            // Parallel merge of sorted halves
            parallelMerge(a, low, mid, mid, high, aux, low);
            System.arraycopy(aux, low, a, low, len);
        }
    }

    /**
     * Parallel merge using recursive divide-and-conquer with binary search:
     * Splits the larger subarray at its median, finds the corresponding insertion point
     * in the other subarray with binary search, and recursively merges both halves in parallel!
     */
    private static void parallelMerge(int[] src, int l1, int r1, int l2, int r2, int[] dest, int dOut) {
        int len1 = r1 - l1;
        int len2 = r2 - l2;

        if (len1 + len2 <= SEQ_MERGE_THRESHOLD) {
            sequentialMerge(src, l1, r1, l2, r2, dest, dOut);
            return;
        }

        // Ensure first subarray is at least as large as the second
        if (len1 < len2) {
            parallelMerge(src, l2, r2, l1, r1, dest, dOut);
            return;
        }

        // Split larger subarray at its median
        int mid1 = l1 + len1 / 2;
        int midVal = src[mid1];

        // Binary search for midVal in the second subarray
        int mid2 = binarySearch(src, l2, r2, midVal);

        // Position where midVal lands in destination
        int offset = (mid1 - l1) + (mid2 - l2);
        dest[dOut + offset] = midVal;

        // Recursively merge left pieces and right pieces concurrently
        final int finalMid1 = mid1;
        final int finalMid2 = mid2;
        final int finalOffset = offset;

        RecursiveAction leftMerge = new RecursiveAction() {
            @Override
            protected void compute() {
                parallelMerge(src, l1, finalMid1, l2, finalMid2, dest, dOut);
            }
        };

        RecursiveAction rightMerge = new RecursiveAction() {
            @Override
            protected void compute() {
                parallelMerge(src, finalMid1 + 1, r1, finalMid2, r2, dest, dOut + finalOffset + 1);
            }
        };

        ForkJoinTask.invokeAll(leftMerge, rightMerge);
    }

    private static int binarySearch(int[] arr, int low, int high, int key) {
        while (low < high) {
            int mid = low + (high - low) / 2;
            if (arr[mid] < key) {
                low = mid + 1;
            } else {
                high = mid;
            }
        }
        return low;
    }

    private static void sequentialMerge(int[] src, int l1, int r1, int l2, int r2, int[] dest, int dOut) {
        int i = l1, j = l2, k = dOut;
        while (i < r1 && j < r2) {
            if (src[i] <= src[j]) {
                dest[k++] = src[i++];
            } else {
                dest[k++] = src[j++];
            }
        }
        while (i < r1) dest[k++] = src[i++];
        while (j < r2) dest[k++] = src[j++];
    }

    private static void sequentialMergeSort(int[] a, int[] aux, int low, int high) {
        if (high - low <= 1) return;
        int mid = low + (high - low) / 2;
        sequentialMergeSort(a, aux, low, mid);
        sequentialMergeSort(a, aux, mid, high);
        sequentialMerge(a, low, mid, mid, high, aux, low);
        System.arraycopy(aux, low, a, low, high - low);
    }

    private static boolean isSorted(int[] arr) {
        for (int i = 1; i < arr.length; i++) {
            if (arr[i] < arr[i - 1]) return false;
        }
        return true;
    }
}
