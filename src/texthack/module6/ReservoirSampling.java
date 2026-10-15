package texthack.module6;

import texthack.collections.SimpleList;

/**
 * Reservoir Sampling — Randomised Streaming Algorithms.
 *
 * Concepts demonstrated:
 *   1. Streaming model: The stream size N is unknown in advance, data arrives sequentially,
 *      and memory is strictly bounded (O(k) where k << N).
 *   2. Algorithm R (Alan Waterman / Knuth):
 *      - Store first k items.
 *      - For the i-th item (0-indexed, i >= k), pick random integer j in [0, i].
 *        If j < k, replace reservoir[j] with item i.
 *      - Invariant proof by induction: At step N, Pr[item in reservoir] = k / N.
 *   3. Algorithm L (Kim-Hung Li, 1994):
 *      - Optimised reservoir sampling that skips items using geometric random jumps.
 *      - Avoids calling the random number generator for every single incoming stream item.
 *   4. Uniformity test: Empirically verifies the uniform distribution over stream elements.
 */
public class ReservoirSampling {

    private static long rngState = 4147555123456789L;

    private static synchronized long nextLong() {
        rngState ^= (rngState << 13);
        rngState ^= (rngState >>> 7);
        rngState ^= (rngState << 17);
        return rngState;
    }

    private static synchronized double nextDouble() {
        return (Math.abs(nextLong()) % 1_000_000_000L) / 1_000_000_000.0;
    }

    /**
     * Algorithm R: Classic reservoir sampling of k items from a stream.
     */
    public static <T> SimpleList<T> sampleAlgorithmR(SimpleList<T> stream, int k) {
        SimpleList<T> reservoir = new SimpleList<>();
        int n = stream.size();
        if (k <= 0 || n == 0) return reservoir;

        int limit = Math.min(k, n);
        for (int i = 0; i < limit; i++) {
            reservoir.add(stream.get(i));
        }

        for (int i = k; i < n; i++) {
            long randIndex = Math.abs(nextLong()) % (i + 1);
            if (randIndex < k) {
                reservoir.set((int) randIndex, stream.get(i));
            }
        }

        return reservoir;
    }

    /**
     * Algorithm L: Fast reservoir sampling using geometric jumps.
     * Skips items without generating a random number for each stream element.
     */
    public static <T> SimpleList<T> sampleAlgorithmL(SimpleList<T> stream, int k) {
        SimpleList<T> reservoir = new SimpleList<>();
        int n = stream.size();
        if (k <= 0 || n == 0) return reservoir;

        int limit = Math.min(k, n);
        for (int i = 0; i < limit; i++) {
            reservoir.add(stream.get(i));
        }
        if (n <= k) return reservoir;

        double w = Math.exp(Math.log(Math.max(1e-12, nextDouble())) / k);
        int i = k;

        while (i < n) {
            double u = Math.max(1e-12, nextDouble());
            // Geometric skip distance
            int skip = (int) (Math.log(u) / Math.log(1.0 - w));
            i = i + skip + 1;

            if (i < n) {
                int replaceSlot = (int) (Math.abs(nextLong()) % k);
                reservoir.set(replaceSlot, stream.get(i));
                w = w * Math.exp(Math.log(Math.max(1e-12, nextDouble())) / k);
            }
        }

        return reservoir;
    }

    /**
     * Empirically checks uniformity by running M trials on stream of size N.
     * Returns an array of selection counts for each item index.
     * Expected count per element = (M * k) / N.
     */
    public static int[] testUniformity(int n, int k, int trials) {
        int[] counts = new int[n];
        SimpleList<Integer> stream = new SimpleList<>();
        for (int i = 0; i < n; i++) stream.add(i);

        for (int t = 0; t < trials; t++) {
            SimpleList<Integer> sample = sampleAlgorithmR(stream, k);
            for (int i = 0; i < sample.size(); i++) {
                counts[sample.get(i)]++;
            }
        }
        return counts;
    }
}
