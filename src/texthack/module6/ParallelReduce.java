package texthack.module6;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

/**
 * Parallel Reduce — Work-Span Model of Parallel Computing.
 *
 * Concepts demonstrated:
 *   1. Work T_1: Total operations = O(n) additions.
 *   2. Span T_infinity (critical path length): Tree depth = O(log n).
 *   3. Parallelism: T_1 / T_infinity = O(n / log n).
 *   4. Brent's Theorem: On P processors, runtime T_P <= (T_1 / P) + T_infinity.
 *   5. Sequential threshold cutoff: Leaves beneath a threshold (e.g. 1000) are computed
 *      sequentially to amortize thread dispatch and fork-join overhead.
 */
public class ParallelReduce {

    private static final int SEQUENTIAL_THRESHOLD = 2048;
    private static final ForkJoinPool POOL = new ForkJoinPool();

    public static class WorkSpanMetrics {
        public final long result;
        public final int work;       // O(n)
        public final int span;       // O(log n)
        public final double parallelism;
        public final long executionTimeNanos;

        public WorkSpanMetrics(long result, int work, int span, double parallelism, long executionTimeNanos) {
            this.result = result;
            this.work = work;
            this.span = span;
            this.parallelism = parallelism;
            this.executionTimeNanos = executionTimeNanos;
        }

        @Override
        public String toString() {
            return String.format("Result: %d | Work (T1): %d ops | Span (T_inf): %d depth | Parallelism: %.2f | Time: %.3f ms",
                    result, work, span, parallelism, executionTimeNanos / 1e6);
        }
    }

    private static class SumTask extends RecursiveTask<Long> {
        private final long[] array;
        private final int start;
        private final int end;

        public SumTask(long[] array, int start, int end) {
            this.array = array;
            this.start = start;
            this.end = end;
        }

        @Override
        protected Long compute() {
            int length = end - start;
            if (length <= SEQUENTIAL_THRESHOLD) {
                long sum = 0;
                for (int i = start; i < end; i++) {
                    sum += array[i];
                }
                return sum;
            }

            int mid = start + length / 2;
            SumTask left = new SumTask(array, start, mid);
            SumTask right = new SumTask(array, mid, end);

            left.fork();
            long rightResult = right.compute();
            long leftResult = left.join();

            return leftResult + rightResult;
        }
    }

    private static class MaxTask extends RecursiveTask<Long> {
        private final long[] array;
        private final int start;
        private final int end;

        public MaxTask(long[] array, int start, int end) {
            this.array = array;
            this.start = start;
            this.end = end;
        }

        @Override
        protected Long compute() {
            int length = end - start;
            if (length <= SEQUENTIAL_THRESHOLD) {
                long max = Long.MIN_VALUE;
                for (int i = start; i < end; i++) {
                    if (array[i] > max) max = array[i];
                }
                return max;
            }

            int mid = start + length / 2;
            MaxTask left = new MaxTask(array, start, mid);
            MaxTask right = new MaxTask(array, mid, end);

            left.fork();
            long rightVal = right.compute();
            long leftVal = left.join();

            return Math.max(leftVal, rightVal);
        }
    }

    /**
     * Parallel sum reduction with full work-span analysis.
     */
    public static WorkSpanMetrics parallelSum(long[] array) {
        int n = array.length;
        if (n == 0) return new WorkSpanMetrics(0, 0, 0, 1.0, 0);

        long start = System.nanoTime();
        long sum = POOL.invoke(new SumTask(array, 0, n));
        long duration = System.nanoTime() - start;

        int work = n - 1; // n-1 additions
        int span = (int) Math.ceil(Math.log(n) / Math.log(2));
        if (span == 0) span = 1;
        double parallelism = (double) work / span;

        return new WorkSpanMetrics(sum, work, span, parallelism, duration);
    }

    /**
     * Parallel max reduction.
     */
    public static WorkSpanMetrics parallelMax(long[] array) {
        int n = array.length;
        if (n == 0) return new WorkSpanMetrics(0, 0, 0, 1.0, 0);

        long start = System.nanoTime();
        long max = POOL.invoke(new MaxTask(array, 0, n));
        long duration = System.nanoTime() - start;

        int work = n - 1;
        int span = (int) Math.ceil(Math.log(n) / Math.log(2));
        if (span == 0) span = 1;
        double parallelism = (double) work / span;

        return new WorkSpanMetrics(max, work, span, parallelism, duration);
    }

    /**
     * Sequential sum baseline for speedup benchmarking.
     */
    public static long sequentialSum(long[] array) {
        long sum = 0;
        for (long v : array) {
            sum += v;
        }
        return sum;
    }
}
