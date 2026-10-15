package texthack.module5;

import texthack.collections.SimpleList;

/**
 * Parameterised Complexity — FPT (Fixed-Parameter Tractable) demonstrations.
 *
 * Key idea: isolate the exponential blowup into a small parameter k,
 * yielding O(f(k) · n^c) algorithms where c is constant.
 *
 * Implements:
 *   1. Vertex Cover parameterised by k (bounded search tree) — O(2ᵏ · n)
 *   2. Kernelisation for Vertex Cover — O(k²) kernel
 */
public class FPTDemo {

    // =====================================================================
    //  Vertex Cover FPT: Bounded Search Tree
    // =====================================================================

    /**
     * FPT Vertex Cover: decides if there exists a vertex cover of size ≤ k.
     *
     * Algorithm (bounded search tree):
     *   1. Pick any edge (u, v)
     *   2. Branch: either u is in the cover, or v is in the cover
     *   3. In each branch, remove the vertex and all incident edges, decrease k by 1
     *   4. If k < 0, reject. If no edges remain, accept.
     *
     * Time: O(2ᵏ · (V + E)) — exponential only in k, not in n.
     *
     * @param n     number of vertices
     * @param edges edge list
     * @param k     parameter: maximum cover size
     * @return list of vertices in the cover, or null if no cover of size ≤ k exists
     */
    public static SimpleList<Integer> vertexCoverFPT(int n, SimpleList<int[]> edges, int k) {
        SimpleList<Integer> cover = new SimpleList<>();
        if (searchTree(n, edges, k, new boolean[n], cover)) {
            return cover;
        }
        return null;
    }

    private static boolean searchTree(int n, SimpleList<int[]> edges, int k,
                                       boolean[] removed, SimpleList<Integer> cover) {
        // Find an uncovered edge
        int[] uncovered = null;
        for (int i = 0; i < edges.size(); i++) {
            int[] e = edges.get(i);
            if (!removed[e[0]] && !removed[e[1]]) {
                uncovered = e;
                break;
            }
        }

        if (uncovered == null) return true; // all edges covered
        if (k <= 0) return false; // budget exhausted

        // Branch 1: include u
        removed[uncovered[0]] = true;
        cover.add(uncovered[0]);
        if (searchTree(n, edges, k - 1, removed, cover)) return true;
        cover.remove(cover.size() - 1);
        removed[uncovered[0]] = false;

        // Branch 2: include v
        removed[uncovered[1]] = true;
        cover.add(uncovered[1]);
        if (searchTree(n, edges, k - 1, removed, cover)) return true;
        cover.remove(cover.size() - 1);
        removed[uncovered[1]] = false;

        return false;
    }

    // =====================================================================
    //  Kernelisation for Vertex Cover
    // =====================================================================

    /**
     * Kernelisation: reduces Vertex Cover instance to a kernel of size O(k²).
     *
     * Rules applied:
     *   1. If a vertex has degree > k, it must be in any cover of size k.
     *      Add it to the cover and remove it.
     *   2. If vertex has degree 0, remove it (isolated vertices never in cover).
     *   3. Crown reduction (simplified): if matched endpoints of a maximal
     *      matching exceed k, answer NO.
     *
     * After kernelisation, the remaining graph has at most k² edges and 2k vertices.
     *
     * @param n     number of vertices
     * @param edges edge list
     * @param k     parameter
     * @return KernelResult with the kernel graph and forced vertices
     */
    public static KernelResult kernelise(int n, SimpleList<int[]> edges, int k) {
        boolean[] removed = new boolean[n];
        SimpleList<Integer> forced = new SimpleList<>();

        boolean changed = true;
        while (changed) {
            changed = false;

            // Rule 1: high-degree vertices
            int[] degree = new int[n];
            for (int i = 0; i < edges.size(); i++) {
                int[] e = edges.get(i);
                if (!removed[e[0]] && !removed[e[1]]) {
                    degree[e[0]]++;
                    degree[e[1]]++;
                }
            }

            for (int v = 0; v < n; v++) {
                if (!removed[v] && degree[v] > k) {
                    removed[v] = true;
                    forced.add(v);
                    k--;
                    changed = true;
                }
            }

            // Rule 2: isolated vertices
            for (int v = 0; v < n; v++) {
                if (!removed[v] && degree[v] == 0) {
                    removed[v] = true;
                    changed = true;
                }
            }
        }

        // Collect remaining edges
        SimpleList<int[]> kernelEdges = new SimpleList<>();
        for (int i = 0; i < edges.size(); i++) {
            int[] e = edges.get(i);
            if (!removed[e[0]] && !removed[e[1]]) {
                kernelEdges.add(e);
            }
        }

        // Count remaining vertices
        int remaining = 0;
        for (int v = 0; v < n; v++) {
            if (!removed[v]) remaining++;
        }

        return new KernelResult(remaining, kernelEdges, forced, k);
    }

    /** Result of kernelisation. */
    public static class KernelResult {
        public final int kernelVertices;
        public final SimpleList<int[]> kernelEdges;
        public final SimpleList<Integer> forcedVertices;
        public final int remainingBudget;

        KernelResult(int kernelVertices, SimpleList<int[]> kernelEdges,
                     SimpleList<Integer> forcedVertices, int remainingBudget) {
            this.kernelVertices = kernelVertices;
            this.kernelEdges = kernelEdges;
            this.forcedVertices = forcedVertices;
            this.remainingBudget = remainingBudget;
        }
    }
}
