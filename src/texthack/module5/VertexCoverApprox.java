package texthack.module5;

import texthack.collections.SimpleList;

/**
 * Vertex Cover 2-Approximation via Maximal Matching.
 *
 * The 2-approximation algorithm:
 *   1. Find any maximal matching (greedy: take edges, remove endpoints)
 *   2. The endpoints of the matching form a vertex cover
 *   3. This cover is at most 2× the optimal
 *
 * Proof: Every edge in the matching must be covered by at least one endpoint
 * in any optimal cover. The matching has k edges → OPT ≥ k.
 * Our cover has 2k vertices → APX ≤ 2 · OPT.
 *
 * Time:  O(V + E)
 * Space: O(V)
 */
public class VertexCoverApprox {

    /**
     * Result of the approximation algorithm.
     */
    public static class VCResult {
        public final SimpleList<Integer> cover;       // vertices in the cover
        public final SimpleList<int[]> matching;      // edges in the maximal matching
        public final int coverSize;

        VCResult(SimpleList<Integer> cover, SimpleList<int[]> matching) {
            this.cover = cover;
            this.matching = matching;
            this.coverSize = cover.size();
        }
    }

    /**
     * Computes a 2-approximate vertex cover.
     *
     * @param n     number of vertices
     * @param edges list of [u, v] edges
     * @return VCResult with the cover and the maximal matching used
     */
    public static VCResult approximate(int n, SimpleList<int[]> edges) {
        boolean[] covered = new boolean[n];
        SimpleList<int[]> matching = new SimpleList<>();
        SimpleList<Integer> cover = new SimpleList<>();

        for (int i = 0; i < edges.size(); i++) {
            int[] edge = edges.get(i);
            int u = edge[0], v = edge[1];
            if (!covered[u] && !covered[v]) {
                // Add this edge to the matching
                matching.add(edge);
                covered[u] = true;
                covered[v] = true;
                cover.add(u);
                cover.add(v);
            }
        }

        return new VCResult(cover, matching);
    }

    /**
     * Exact minimum vertex cover (brute-force for small graphs).
     * @return minimum vertex cover size
     */
    public static int exactMinCover(int n, SimpleList<int[]> edges) {
        for (int k = 0; k <= n; k++) {
            if (canCover(n, edges, k, 0, new boolean[n], 0)) return k;
        }
        return n;
    }

    private static boolean canCover(int n, SimpleList<int[]> edges, int k,
                                     int start, boolean[] inCover, int chosen) {
        if (chosen == k) {
            // Check if all edges are covered
            for (int i = 0; i < edges.size(); i++) {
                int[] e = edges.get(i);
                if (!inCover[e[0]] && !inCover[e[1]]) return false;
            }
            return true;
        }
        if (start >= n) return false;

        // Try including vertex 'start' in cover
        inCover[start] = true;
        if (canCover(n, edges, k, start + 1, inCover, chosen + 1)) return true;
        inCover[start] = false;

        // Try not including it
        return canCover(n, edges, k, start + 1, inCover, chosen);
    }

    /**
     * Verifies that the approximate cover is valid (covers all edges).
     */
    public static boolean verifyCover(int n, SimpleList<int[]> edges, SimpleList<Integer> cover) {
        boolean[] inCover = new boolean[n];
        for (int i = 0; i < cover.size(); i++) inCover[cover.get(i)] = true;
        for (int i = 0; i < edges.size(); i++) {
            int[] e = edges.get(i);
            if (!inCover[e[0]] && !inCover[e[1]]) return false;
        }
        return true;
    }
}
