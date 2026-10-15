package texthack;

import texthack.collections.SimpleList;
import texthack.collections.SimpleMap;
import texthack.module1.CorpusLoader;
import texthack.module2.KMP;
import texthack.module2.ZFunction;
import texthack.module2.RabinKarp;
import texthack.module2.AhoCorasick;
import texthack.module2.SuffixArray;
import texthack.module2.SuffixAutomaton;
import texthack.module3.EditDistance;
import texthack.module3.SequenceAlignment;
import texthack.module3.IntervalDP;
import texthack.module3.BitmaskDP;
import texthack.module3.TreeDP;
import texthack.module3.SumOverSubsets;
import texthack.module4.FlowNetwork;
import texthack.module4.EdmondsKarp;
import texthack.module4.Dinic;
import texthack.module4.MinCut;
import texthack.module4.BipartiteMatching;
import texthack.module4.MinCostMaxFlow;
import texthack.module5.SATSolver;
import texthack.module5.ReductionDemo;
import texthack.module5.VertexCoverApprox;
import texthack.module5.TSPApprox;
import texthack.module5.KnapsackFPTAS;
import texthack.module5.FPTDemo;
import texthack.module6.MillerRabin;
import texthack.module6.UniversalHash;
import texthack.module6.ReservoirSampling;
import texthack.module6.RandomisedQuicksort;
import texthack.module6.ParallelReduce;
import texthack.module6.ParallelPrefixSum;
import texthack.module6.ParallelMergeSort;

/**
 * TextHackEngine — Unified Entry Point and Algorithmic Question Bank.
 *
 * Exposes queries across all 6 core modules of Advanced DSA:
 *   Module 1: Corpus & Inverted Indexing
 *   Module 2: Exact String Algorithms & Suffix Structures
 *   Module 3: Advanced Dynamic Programming & Alignment
 *   Module 4: Network Flow, Cuts, and Bipartite Matchings
 *   Module 5: Intractability, Reductions, and Approximation Schemes
 *   Module 6: Randomised Algorithms & Work-Span Parallel Computation
 *
 * Strictly zero usage of java.util.* within the engine architecture.
 */
public class TextHackEngine {

    private final CorpusLoader corpusLoader;

    public TextHackEngine() {
        this.corpusLoader = new CorpusLoader();
    }

    public CorpusLoader getCorpus() {
        return corpusLoader;
    }

    // ==========================================
    // MODULE 1: System & Corpus Queries
    // ==========================================
    public SimpleList<CorpusLoader.Document> getDocuments() {
        return corpusLoader.getDocuments();
    }

    public SimpleList<Integer> queryInvertedIndex(String word) {
        return corpusLoader.search(word);
    }

    // ==========================================
    // MODULE 2: String Algorithms
    // ==========================================
    public SimpleList<Integer> runKMP(String text, String pattern) {
        return KMP.search(text, pattern);
    }

    public SimpleList<Integer> runZ(String text, String pattern) {
        return ZFunction.search(text, pattern);
    }

    public SimpleList<Integer> runRabinKarp(String text, String pattern) {
        return RabinKarp.search(text, pattern);
    }

    public SimpleList<int[]> runAhoCorasick(String text, String[] patterns) {
        AhoCorasick ac = new AhoCorasick(patterns);
        return ac.search(text);
    }

    public SuffixArray buildSuffixArray(String text) {
        return new SuffixArray(text);
    }

    public SuffixAutomaton buildSuffixAutomaton(String text) {
        return new SuffixAutomaton(text);
    }

    // ==========================================
    // MODULE 3: Advanced DP
    // ==========================================
    public int computeLevenshtein(String s1, String s2) {
        return EditDistance.levenshtein(s1, s2);
    }

    public int computeDamerau(String s1, String s2) {
        return EditDistance.damerauLevenshtein(s1, s2);
    }

    public SequenceAlignment.AlignmentResult alignNeedlemanWunsch(String s1, String s2) {
        return SequenceAlignment.needlemanWunsch(s1, s2);
    }

    public SequenceAlignment.AlignmentResult alignSmithWaterman(String s1, String s2) {
        return SequenceAlignment.smithWaterman(s1, s2);
    }

    public IntervalDP.MCMResult runMatrixChain(int[] dims) {
        return IntervalDP.matrixChain(dims);
    }

    public int runBitmaskTSP(int[][] cost) {
        return BitmaskDP.tsp(cost, 0);
    }

    public int[] runBitmaskTSPWithPath(int[][] cost) {
        return BitmaskDP.tspWithPath(cost, 0);
    }

    public int runTreeMaxIndependentSet(SimpleList<Integer>[] adj, int n) {
        return TreeDP.maxIndependentSet(adj, n);
    }

    public long[] runSOSDP(long[] a, int n) {
        return SumOverSubsets.zetaTransform(a, n);
    }

    // ==========================================
    // MODULE 4: Network Flow
    // ==========================================
    public int runEdmondsKarp(FlowNetwork fn, int s, int t) {
        return EdmondsKarp.maxFlow(fn, s, t);
    }

    public int runDinic(FlowNetwork fn, int s, int t) {
        return Dinic.maxFlow(fn, s, t);
    }

    public MinCut.MinCutResult runMinCut(FlowNetwork fn, int s, int t) {
        return MinCut.compute(fn, s, t);
    }

    public BipartiteMatching.MatchingResult runBipartiteMatching(int uCount, int vCount, SimpleList<int[]> edges) {
        return BipartiteMatching.maxMatchingFlow(uCount, vCount, edges);
    }

    public MinCostMaxFlow.MCMFResult runMinCostMaxFlow(FlowNetwork fn, int s, int t) {
        return MinCostMaxFlow.compute(fn, s, t);
    }

    /**
     * Build flow network from the Wikipedia citation graph to analyze information bottlenecks.
     */
    public FlowNetwork buildCitationFlowNetwork() {
        SimpleList<CorpusLoader.Document> docs = corpusLoader.getDocuments();
        int n = docs.size();
        FlowNetwork fn = new FlowNetwork(n);
        SimpleMap<Integer, SimpleList<Integer>> graph = corpusLoader.getCitationGraph();

        for (int i = 0; i < n; i++) {
            SimpleList<Integer> targets = graph.get(i);
            if (targets != null) {
                for (int j = 0; j < targets.size(); j++) {
                    int to = targets.get(j);
                    // Capacity based on citation flow
                    fn.addEdge(i, to, 10);
                }
            }
        }
        return fn;
    }

    // ==========================================
    // MODULE 5: Intractability & Approximation
    // ==========================================
    public boolean solveSAT(SATSolver.Clause[] clauses, int numVars) {
        return SATSolver.isSatisfiable(clauses, numVars);
    }

    public VertexCoverApprox.VCResult runVertexCoverApprox(int n, SimpleList<int[]> edges) {
        return VertexCoverApprox.approximate(n, edges);
    }

    public int[] runMetricTSPApprox(int[][] dist) {
        return TSPApprox.mstApproximation(dist);
    }

    public int runKnapsackFPTAS(int[] weights, int[] values, int capacity, double epsilon) {
        return KnapsackFPTAS.fptas(weights, values, capacity, epsilon);
    }

    public int runKnapsackExact(int[] weights, int[] values, int capacity) {
        return KnapsackFPTAS.exactKnapsack(weights, values, capacity);
    }

    public SimpleList<Integer> runVertexCoverFPT(int n, SimpleList<int[]> edges, int k) {
        return FPTDemo.vertexCoverFPT(n, edges, k);
    }

    // ==========================================
    // MODULE 6: Randomised & Parallel
    // ==========================================
    public boolean testMillerRabin(long n, int k) {
        return MillerRabin.isProbablePrime(n, k);
    }

    public boolean testDeterministicPrime64(long n) {
        return MillerRabin.isPrime64(n);
    }

    public UniversalHash.CollisionReport testUniversalHash(SimpleList<String> words, int buckets, int trials) {
        return UniversalHash.evaluateCollisions(words, buckets, trials);
    }

    public <T> SimpleList<T> sampleReservoir(SimpleList<T> stream, int k) {
        return ReservoirSampling.sampleAlgorithmR(stream, k);
    }

    public RandomisedQuicksort.SortStats sortRandomised(int[] arr) {
        return RandomisedQuicksort.sortRandomised(arr);
    }

    public RandomisedQuicksort.SortStats sortDeterministic(int[] arr) {
        return RandomisedQuicksort.sortDeterministic(arr);
    }

    public ParallelReduce.WorkSpanMetrics parallelSum(long[] array) {
        return ParallelReduce.parallelSum(array);
    }

    public long[] parallelScan(long[] array) {
        return ParallelPrefixSum.blellochScanInclusive(array);
    }

    public ParallelMergeSort.SortMetrics parallelMergeSort(int[] array) {
        return ParallelMergeSort.sort(array);
    }
}
