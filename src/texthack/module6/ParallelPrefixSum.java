package texthack.module6;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

/**
 * Parallel Prefix Sum — Guy Blelloch's Work-Efficient Parallel Scan Algorithm.
 *
 * Concepts demonstrated:
 *   1. Naive parallel prefix sum: O(n log n) work — work-inefficient!
 *   2. Blelloch algorithm achieves WORK-EFFICIENCY:
 *      - Work: O(n) total additions
 *      - Span: O(log n) tree depth
 *   3. Two distinct phases on a complete binary tree:
 *      Phase 1: Up-Sweep (Reduce Phase)
 *        - Computes sums of subtrees going bottom-up.
 *        - At step d (stride = 2^(d+1)), array[i] += array[i - 2^d].
 *      Phase 2: Down-Sweep Phase
 *        - Sets root (array[n - 1]) to identity (0).
 *        - Passes sums down, swapping and accumulating.
 *        - Left child gets parent's old value.
 *        - Right child gets parent's old value + left child's old value.
 *   4. Results in an exclusive prefix sum in O(n) work and O(log n) span.
 */
public class ParallelPrefixSum {

    private static final int SEQUENTIAL_CUTOFF = 4096;
    private static final ForkJoinPool POOL = new ForkJoinPool();

    /**
     * Compute exclusive prefix sum in-place using Blelloch's work-efficient scan.
     * Output array[i] = sum_{j=0}^{i-1} input[j], with array[0] = 0.
     */
    public static long[] blellochScanExclusive(long[] input) {
        int n = input.length;
        if (n == 0) return new long[0];

        // Pad to next power of 2
        int m = 1;
        while (m < n) m <<= 1;
        long[] tree = new long[m];
        System.arraycopy(input, 0, tree, 0, n);

        // Phase 1: Up-sweep (Reduce)
        for (int d = 0; (1 << d) < m; d++) {
            int stride = 1 << (d + 1);
            int half = 1 << d;
            int numOps = m / stride;

            if (numOps <= 16) {
                // Small number of ops: execute directly
                for (int i = 0; i < m; i += stride) {
                    tree[i + stride - 1] += tree[i + half - 1];
                }
            } else {
                final int currentStride = stride;
                final int currentHalf = half;
                final int total = m;
                POOL.invoke(new RecursiveAction() {
                    @Override
                    protected void compute() {
                        parallelUpSweep(tree, 0, total, currentStride, currentHalf);
                    }
                });
            }
        }

        // Set the root to zero (identity element for addition)
        tree[m - 1] = 0;

        // Phase 2: Down-sweep
        for (int d = (int) (Math.log(m) / Math.log(2)) - 1; d >= 0; d--) {
            int stride = 1 << (d + 1);
            int half = 1 << d;
            int numOps = m / stride;

            if (numOps <= 16) {
                for (int i = 0; i < m; i += stride) {
                    long t = tree[i + half - 1];
                    tree[i + half - 1] = tree[i + stride - 1];
                    tree[i + stride - 1] += t;
                }
            } else {
                final int currentStride = stride;
                final int currentHalf = half;
                final int total = m;
                POOL.invoke(new RecursiveAction() {
                    @Override
                    protected void compute() {
                        parallelDownSweep(tree, 0, total, currentStride, currentHalf);
                    }
                });
            }
        }

        long[] result = new long[n];
        System.arraycopy(tree, 0, result, 0, n);
        return result;
    }

    private static void parallelUpSweep(long[] tree, int start, int end, int stride, int half) {
        int count = (end - start) / stride;
        if (count <= 32) {
            for (int i = start; i < end; i += stride) {
                tree[i + stride - 1] += tree[i + half - 1];
            }
            return;
        }

        int midIndex = start + (count / 2) * stride;
        RecursiveAction left = new RecursiveAction() {
            @Override protected void compute() { parallelUpSweep(tree, start, midIndex, stride, half); }
        };
        RecursiveAction right = new RecursiveAction() {
            @Override protected void compute() { parallelUpSweep(tree, midIndex, end, stride, half); }
        };
        left.fork();
        right.fork();
        left.join();
        right.join();
    }

    private static void parallelDownSweep(long[] tree, int start, int end, int stride, int half) {
        int count = (end - start) / stride;
        if (count <= 32) {
            for (int i = start; i < end; i += stride) {
                long t = tree[i + half - 1];
                tree[i + half - 1] = tree[i + stride - 1];
                tree[i + stride - 1] += t;
            }
            return;
        }

        int midIndex = start + (count / 2) * stride;
        RecursiveAction left = new RecursiveAction() {
            @Override protected void compute() { parallelDownSweep(tree, start, midIndex, stride, half); }
        };
        RecursiveAction right = new RecursiveAction() {
            @Override protected void compute() { parallelDownSweep(tree, midIndex, end, stride, half); }
        };
        left.fork();
        right.fork();
        left.join();
        right.join();
    }

    /**
     * Compute inclusive prefix sum: output[i] = sum_{j=0}^{i} input[j].
     */
    public static long[] blellochScanInclusive(long[] input) {
        long[] exclusive = blellochScanExclusive(input);
        long[] inclusive = new long[input.length];
        for (int i = 0; i < input.length; i++) {
            inclusive[i] = exclusive[i] + input[i];
        }
        return inclusive;
    }

    /**
     * Sequential prefix sum reference for verification.
     */
    public static long[] sequentialPrefixSum(long[] input) {
        long[] res = new long[input.length];
        long sum = 0;
        for (int i = 0; i < input.length; i++) {
            sum += input[i];
            res[i] = sum;
        }
        return res;
    }
}
