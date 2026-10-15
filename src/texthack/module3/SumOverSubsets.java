package texthack.module3;

/**
 * Sum-Over-Subsets (SOS) DP.
 *
 * Given an array A of size 2ⁿ indexed by bitmasks, computes for each mask S:
 *   F[S] = Σ A[T] for all T ⊆ S   (the "subset sum transform")
 *
 * This is the Möbius/Zeta transform over the subset lattice.
 *
 * Time:  O(n · 2ⁿ) — much better than the naïve O(3ⁿ)
 * Space: O(2ⁿ)
 *
 * Applications: inclusion-exclusion, convolution over subsets,
 *               counting subsets with certain properties.
 */
public class SumOverSubsets {

    /**
     * Computes the subset sum (Zeta) transform.
     * After this, result[S] = Σ a[T] for all T ⊆ S.
     *
     * @param a  input array of size 2ⁿ
     * @param n  number of bits
     * @return transformed array
     */
    public static long[] zetaTransform(long[] a, int n) {
        int size = 1 << n;
        long[] f = new long[size];
        System.arraycopy(a, 0, f, 0, size);

        for (int bit = 0; bit < n; bit++) {
            for (int mask = 0; mask < size; mask++) {
                if ((mask & (1 << bit)) != 0) {
                    f[mask] += f[mask ^ (1 << bit)];
                }
            }
        }
        return f;
    }

    /**
     * Computes the Möbius (inverse Zeta) transform.
     * Undoes the subset sum transform via inclusion-exclusion.
     *
     * @param f  input array of size 2ⁿ (presumably the result of zetaTransform)
     * @param n  number of bits
     * @return inverse-transformed array
     */
    public static long[] moebiusTransform(long[] f, int n) {
        int size = 1 << n;
        long[] a = new long[size];
        System.arraycopy(f, 0, a, 0, size);

        for (int bit = 0; bit < n; bit++) {
            for (int mask = 0; mask < size; mask++) {
                if ((mask & (1 << bit)) != 0) {
                    a[mask] -= a[mask ^ (1 << bit)];
                }
            }
        }
        return a;
    }

    /**
     * Computes the superset sum transform.
     * After this, result[S] = Σ a[T] for all T ⊇ S.
     *
     * @param a  input array of size 2ⁿ
     * @param n  number of bits
     * @return transformed array
     */
    public static long[] supersetZetaTransform(long[] a, int n) {
        int size = 1 << n;
        long[] f = new long[size];
        System.arraycopy(a, 0, f, 0, size);

        for (int bit = 0; bit < n; bit++) {
            for (int mask = 0; mask < size; mask++) {
                if ((mask & (1 << bit)) == 0) {
                    f[mask] += f[mask | (1 << bit)];
                }
            }
        }
        return f;
    }

    /**
     * Subset convolution using ranked Möbius/Zeta transforms.
     * Given A and B of size 2ⁿ, computes:
     *   C[S] = Σ A[T] · B[S \ T] for all T ⊆ S
     *
     * Time: O(n² · 2ⁿ)
     */
    public static long[] subsetConvolution(long[] a, long[] b, int n) {
        int size = 1 << n;

        // Ranked versions: fA[popcount][mask], fB[popcount][mask]
        long[][] fA = new long[n + 1][size];
        long[][] fB = new long[n + 1][size];

        for (int mask = 0; mask < size; mask++) {
            int pc = Integer.bitCount(mask);
            fA[pc][mask] = a[mask];
            fB[pc][mask] = b[mask];
        }

        // Apply Zeta transform per rank
        for (int rank = 0; rank <= n; rank++) {
            for (int bit = 0; bit < n; bit++) {
                for (int mask = 0; mask < size; mask++) {
                    if ((mask & (1 << bit)) != 0) {
                        fA[rank][mask] += fA[rank][mask ^ (1 << bit)];
                        fB[rank][mask] += fB[rank][mask ^ (1 << bit)];
                    }
                }
            }
        }

        // Multiply per rank
        long[][] fC = new long[n + 1][size];
        for (int mask = 0; mask < size; mask++) {
            for (int k = 0; k <= n; k++) {
                for (int j = 0; j <= k; j++) {
                    fC[k][mask] += fA[j][mask] * fB[k - j][mask];
                }
            }
        }

        // Apply Möbius transform per rank
        for (int rank = 0; rank <= n; rank++) {
            for (int bit = 0; bit < n; bit++) {
                for (int mask = 0; mask < size; mask++) {
                    if ((mask & (1 << bit)) != 0) {
                        fC[rank][mask] -= fC[rank][mask ^ (1 << bit)];
                    }
                }
            }
        }

        // Extract result at the correct rank
        long[] c = new long[size];
        for (int mask = 0; mask < size; mask++) {
            c[mask] = fC[Integer.bitCount(mask)][mask];
        }
        return c;
    }

    /**
     * Counts the number of subsets T ⊆ S for which A[T] != 0, for each S.
     * Uses the Zeta transform on indicator values.
     */
    public static int[] countNonZeroSubsets(long[] a, int n) {
        int size = 1 << n;
        long[] indicator = new long[size];
        for (int i = 0; i < size; i++) indicator[i] = (a[i] != 0) ? 1 : 0;
        long[] result = zetaTransform(indicator, n);
        int[] counts = new int[size];
        for (int i = 0; i < size; i++) counts[i] = (int) result[i];
        return counts;
    }
}
