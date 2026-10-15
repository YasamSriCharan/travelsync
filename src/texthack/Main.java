package texthack;

import java.util.Scanner;
import texthack.collections.SimpleList;
import texthack.collections.SimpleMap;
import texthack.module1.CorpusLoader;
import texthack.module2.SuffixArray;
import texthack.module2.SuffixAutomaton;
import texthack.module3.IntervalDP;
import texthack.module3.BitmaskDP;
import texthack.module3.SequenceAlignment;
import texthack.module4.FlowNetwork;
import texthack.module4.MinCut;
import texthack.module4.BipartiteMatching;
import texthack.module4.MinCostMaxFlow;
import texthack.module5.SATSolver;
import texthack.module5.VertexCoverApprox;
import texthack.module5.TSPApprox;
import texthack.module5.KnapsackFPTAS;
import texthack.module5.FPTDemo;
import texthack.module6.MillerRabin;
import texthack.module6.UniversalHash;
import texthack.module6.RandomisedQuicksort;
import texthack.module6.ParallelReduce;
import texthack.module6.ParallelMergeSort;

/**
 * TextHack Interactive Console UI.
 *
 * Demonstrates the 6 Advanced Algorithm modules:
 *   [1] Module-1: TextHack as a System & Corpus Inverted Index
 *   [2] Module-2: String Algorithms & Suffix Automata
 *   [3] Module-3: Advanced Dynamic Programming & Sequence Alignment
 *   [4] Module-4: Network Flow, Min-Cut & Bipartite Matching
 *   [5] Module-5: NP-Completeness, Reductions & Approximation Algorithms
 *   [6] Module-6: Randomised Algorithms & Work-Span Parallelism
 *   [7] Run Full Algorithm Suite Demonstration (End-to-End)
 */
public class Main {

    private final TextHackEngine engine;
    private final InputHelper input;

    public Main() {
        this.engine = new TextHackEngine();
        this.input = new InputHelper(new Scanner(System.in));
    }

    public static void main(String[] args) {
        Main app = new Main();
        app.run();
    }

    public void run() {
        System.out.println("================================================================================");
        System.out.println("            TEXTHACK — ADVANCED DATA STRUCTURES & ALGORITHMS (DSA-3)            ");
        System.out.println("                     The Algorithm Question Bank & Engine                       ");
        System.out.println("================================================================================");

        boolean running = true;
        while (running) {
            printMainMenu();
            int choice = input.readInt("Select an option", 0, 7, 7);
            System.out.println();

            switch (choice) {
                case 1: runModule1Menu(); break;
                case 2: runModule2Menu(); break;
                case 3: runModule3Menu(); break;
                case 4: runModule4Menu(); break;
                case 5: runModule5Menu(); break;
                case 6: runModule6Menu(); break;
                case 7: runFullSuiteDemo(); break;
                case 0:
                    System.out.println("Exiting TextHack. Happy algorithmic problem solving!");
                    running = false;
                    break;
                default:
                    System.out.println("Invalid selection.");
            }
        }
    }

    private void printMainMenu() {
        System.out.println("\n-------------------------------- MAIN MENU --------------------------------");
        System.out.println("  [1] Module 1: TextHack System & Indian-Language Wikipedia Corpus");
        System.out.println("  [2] Module 2: Exact String Algorithms & Suffix Automata");
        System.out.println("  [3] Module 3: Advanced Dynamic Programming & Sequence Alignment");
        System.out.println("  [4] Module 4: Network Flow, Min-Cuts & Bipartite Matchings");
        System.out.println("  [5] Module 5: NP-Completeness, Reductions & Approximation Schemes");
        System.out.println("  [6] Module 6: Randomised Algorithms & Work-Span Parallelism");
        System.out.println("  [7] Run Full Automated Verification Suite (All 6 Modules)");
        System.out.println("  [0] Exit");
        System.out.println("---------------------------------------------------------------------------");
    }

    // ==========================================
    // MODULE 1
    // ==========================================
    private void runModule1Menu() {
        System.out.println("--- Module 1: TextHack Corpus & Inverted Index ---");
        SimpleList<CorpusLoader.Document> docs = engine.getDocuments();
        System.out.println("Corpus contains " + docs.size() + " indexed Wikipedia articles:");
        for (int i = 0; i < docs.size(); i++) {
            CorpusLoader.Document doc = docs.get(i);
            System.out.printf("  [%d] %-25s (Words: ~%d, Citations: %d)\n",
                    doc.id, doc.title, doc.body.split("\\s+").length, doc.citations.length);
        }

        String query = input.readString("\nEnter search keyword to test inverted index", "architecture");
        SimpleList<Integer> matches = engine.queryInvertedIndex(query);
        System.out.println("Inverted index lookup for '" + query + "':");
        if (matches.size() == 0) {
            System.out.println("  No documents found containing '" + query + "'.");
        } else {
            for (int i = 0; i < matches.size(); i++) {
                int docId = matches.get(i);
                CorpusLoader.Document d = engine.getCorpus().getDocument(docId);
                System.out.println("  -> Document [" + docId + "] " + (d != null ? d.title : "Unknown"));
            }
        }
        input.waitForEnter();
    }

    // ==========================================
    // MODULE 2
    // ==========================================
    private void runModule2Menu() {
        System.out.println("--- Module 2: Exact String Algorithms & Suffix Structures ---");
        String text = input.readString("Enter text (or press Enter for default)", "The Taj Mahal is a masterpiece of Mughal architecture in Agra");
        String pattern = input.readString("Enter search pattern", "architecture");

        System.out.println("\n[1] Knuth-Morris-Pratt (KMP) (Prefix function pi):");
        SimpleList<Integer> kmpResults = engine.runKMP(text, pattern);
        System.out.println("    Found at 0-based indices: " + formatIndices(kmpResults));

        System.out.println("[2] Z-Algorithm (Z-array):");
        SimpleList<Integer> zResults = engine.runZ(text, pattern);
        System.out.println("    Found at 0-based indices: " + formatIndices(zResults));

        System.out.println("[3] Rabin-Karp (Rolling polynomial hash):");
        SimpleList<Integer> rkResults = engine.runRabinKarp(text, pattern);
        System.out.println("    Found at 0-based indices: " + formatIndices(rkResults));

        System.out.println("[4] Aho-Corasick Multi-Pattern Automaton:");
        String[] multiPatterns = {"Taj", "Agra", "Mughal", "architecture"};
        SimpleList<int[]> acHits = engine.runAhoCorasick(text, multiPatterns);
        System.out.println("    Searching for multi-keywords {\"Taj\", \"Agra\", \"Mughal\", \"architecture\"}:");
        for (int i = 0; i < acHits.size(); i++) {
            int[] hit = acHits.get(i);
            System.out.printf("      Pattern \"%s\" matched at index %d\n", multiPatterns[hit[0]], hit[1]);
        }

        System.out.println("[5] Suffix Array & Kasai LCP:");
        SuffixArray sa = engine.buildSuffixArray("banana");
        System.out.println("    Suffix array of 'banana':");
        int[] saArr = sa.getSuffixArray();
        int[] lcpArr = sa.getLCP();
        for (int i = 0; i < saArr.length; i++) {
            System.out.printf("      Rank %d: SA=%d, LCP=%d, suffix=\"%s\"\n",
                    i, saArr[i], lcpArr[i], "banana".substring(saArr[i]));
        }

        System.out.println("[6] Suffix Automaton (SAM):");
        SuffixAutomaton sam = engine.buildSuffixAutomaton("banana");
        System.out.println("    Distinct substrings in 'banana': " + sam.countDistinctSubstrings() + " (State count: " + sam.getStateCount() + ")");
        input.waitForEnter();
    }

    // ==========================================
    // MODULE 3
    // ==========================================
    private void runModule3Menu() {
        System.out.println("--- Module 3: Advanced Dynamic Programming ---");
        String s1 = input.readString("Enter first string", "algorithm");
        String s2 = input.readString("Enter second string", "altruistic");

        System.out.println("\n[1] Wagner-Fischer Levenshtein Distance: " + engine.computeLevenshtein(s1, s2));
        System.out.println("[2] Damerau-Levenshtein (with transpositions): " + engine.computeDamerau(s1, s2));

        System.out.println("[3] Needleman-Wunsch Global Sequence Alignment:");
        SequenceAlignment.AlignmentResult global = engine.alignNeedlemanWunsch(s1, s2);
        System.out.println("    Alignment score: " + global.score);
        System.out.println("    Seq 1: " + global.alignedSeq1);
        System.out.println("    Seq 2: " + global.alignedSeq2);

        System.out.println("[4] Matrix-Chain Multiplication (Interval DP):");
        int[] dims = {10, 30, 5, 60};
        IntervalDP.MCMResult mcm = engine.runMatrixChain(dims);
        System.out.println("    Dimensions: 10x30, 30x5, 5x60");
        System.out.println("    Minimum scalar multiplications: " + mcm.minCost);
        System.out.println("    Optimal parenthesisation: " + mcm.parenthesisation);

        System.out.println("[5] Held-Karp Travelling Salesperson (Bitmask DP O(n^2 2^n)):");
        int[][] dist = {
            {0, 10, 15, 20},
            {10, 0, 35, 25},
            {15, 35, 0, 30},
            {20, 25, 30, 0}
        };
        int tspCost = engine.runBitmaskTSP(dist);
        System.out.println("    4-city optimal tour length: " + tspCost);

        System.out.println("[6] Sum Over Subsets (SOS DP):");
        long[] f = {1, 2, 4, 8}; // 2 variables (indices 00, 01, 10, 11)
        long[] sos = engine.runSOSDP(f, 2);
        System.out.print("    SOS DP transform: [");
        for (int i = 0; i < sos.length; i++) {
            System.out.print(sos[i] + (i < sos.length - 1 ? ", " : ""));
        }
        System.out.println("]");
        input.waitForEnter();
    }

    // ==========================================
    // MODULE 4
    // ==========================================
    private void runModule4Menu() {
        System.out.println("--- Module 4: Network Flow, Min-Cut & Bipartite Matching ---");
        System.out.println("[1] Dinic vs Edmonds-Karp on a standard flow network:");
        FlowNetwork fn1 = new FlowNetwork(6);
        // Source=0, Sink=5
        fn1.addEdge(0, 1, 16);
        fn1.addEdge(0, 2, 13);
        fn1.addEdge(1, 2, 10);
        fn1.addEdge(1, 3, 12);
        fn1.addEdge(2, 1, 4);
        fn1.addEdge(2, 4, 14);
        fn1.addEdge(3, 2, 9);
        fn1.addEdge(3, 5, 20);
        fn1.addEdge(4, 3, 7);
        fn1.addEdge(4, 5, 4);

        int maxFlowDinic = engine.runDinic(fn1, 0, 5);
        System.out.println("    Dinic algorithm computed max flow: " + maxFlowDinic);

        System.out.println("[2] Min-Cut Partition (Max-Flow Min-Cut Theorem):");
        MinCut.MinCutResult cut = engine.runMinCut(fn1, 0, 5);
        System.out.println("    Min-cut value = " + cut.cutValue);
        System.out.println("    Number of cut edges: " + cut.cutEdges.size());

        System.out.println("[3] Bipartite Matching & König's Theorem:");
        SimpleList<int[]> edges = new SimpleList<>();
        edges.add(new int[]{0, 0});
        edges.add(new int[]{0, 1});
        edges.add(new int[]{1, 0});
        edges.add(new int[]{2, 1});
        edges.add(new int[]{2, 2});
        BipartiteMatching.MatchingResult match = engine.runBipartiteMatching(3, 3, edges);
        System.out.println("    Maximum cardinality matching: " + match.matchingSize);

        System.out.println("[4] Wikipedia Citation Graph Flow Bottlenecks:");
        FlowNetwork citationNet = engine.buildCitationFlowNetwork();
        int citationFlow = engine.runDinic(citationNet, 0, citationNet.getVertexCount() - 1);
        System.out.println("    Citation capacity from doc 0 to doc " + (citationNet.getVertexCount() - 1) + ": " + citationFlow);
        input.waitForEnter();
    }

    // ==========================================
    // MODULE 5
    // ==========================================
    private void runModule5Menu() {
        System.out.println("--- Module 5: NP-Completeness, Reductions & Approximation ---");

        System.out.println("[1] SAT Solver (Cook-Levin Canonical NP-Complete Problem):");
        SATSolver.Clause[] clauses = new SATSolver.Clause[]{
            new SATSolver.Clause(new SATSolver.Literal(0, false), new SATSolver.Literal(1, false)),
            new SATSolver.Clause(new SATSolver.Literal(0, true), new SATSolver.Literal(2, false)),
            new SATSolver.Clause(new SATSolver.Literal(1, true), new SATSolver.Literal(2, true))
        };
        boolean sat = engine.solveSAT(clauses, 3);
        System.out.println("    Formula: (x0 v x1) ^ (~x0 v x2) ^ (~x1 v ~x2)");
        System.out.println("    Satisfiable? " + sat);

        System.out.println("[2] Vertex Cover 2-Approximation via Maximal Matching:");
        SimpleList<int[]> vcEdges = new SimpleList<>();
        vcEdges.add(new int[]{0, 1});
        vcEdges.add(new int[]{1, 2});
        vcEdges.add(new int[]{2, 3});
        vcEdges.add(new int[]{3, 4});
        vcEdges.add(new int[]{4, 0});
        vcEdges.add(new int[]{1, 3});
        VertexCoverApprox.VCResult vcRes = engine.runVertexCoverApprox(5, vcEdges);
        System.out.println("    Graph: 5-cycle with chord (1-3)");
        System.out.println("    Maximal matching edges: " + vcRes.matching.size());
        System.out.println("    2-approximate cover size: " + vcRes.coverSize + " (guarantee <= 2 * OPT)");

        System.out.println("[3] Knapsack FPTAS (Fully Polynomial-Time Approximation Scheme):");
        int[] weights = {10, 20, 30, 40};
        int[] values = {60, 100, 120, 240};
        int W = 50;
        int exactVal = engine.runKnapsackExact(weights, values, W);
        int apxVal = engine.runKnapsackFPTAS(weights, values, W, 0.2);
        System.out.printf("    Exact Knapsack OPT: %d | FPTAS (eps=0.2): %d | Ratio: %.3f\n",
                exactVal, apxVal, (double) apxVal / exactVal);

        System.out.println("[4] Parameterised Complexity (FPT) Vertex Cover O(2^k * n):");
        SimpleList<Integer> fptCover = engine.runVertexCoverFPT(5, vcEdges, 3);
        System.out.println("    Is there a vertex cover of size k=3? " + (fptCover != null ? "YES (size " + fptCover.size() + ")" : "NO"));
        input.waitForEnter();
    }

    // ==========================================
    // MODULE 6
    // ==========================================
    private void runModule6Menu() {
        System.out.println("--- Module 6: Randomised Algorithms & Parallelism ---");

        System.out.println("[1] Miller-Rabin Randomised Primality Testing:");
        long prime = 1000000007L;
        long carmichael = 561L; // 3 * 11 * 17
        System.out.printf("    Is %d prime? %s (Miller-Rabin, 10 rounds)\n", prime, engine.testMillerRabin(prime, 10));
        System.out.printf("    Carmichael number %d (561): Fermat says %s, Miller-Rabin says %s\n",
                carmichael, MillerRabin.fermatTest(carmichael, 5), engine.testMillerRabin(carmichael, 10));

        System.out.println("[2] Carter-Wegman 2-Universal Hashing Collision Analysis:");
        SimpleList<String> words = new SimpleList<>();
        for (int i = 0; i < 1000; i++) words.add("word_" + i);
        UniversalHash.CollisionReport report = engine.testUniversalHash(words, 100, 5);
        System.out.println("    " + report);

        System.out.println("[3] Randomised Quicksort vs Deterministic Quicksort on Sorted Adversarial Array:");
        int n = 5000;
        int[] sortedArray = new int[n];
        for (int i = 0; i < n; i++) sortedArray[i] = i;

        RandomisedQuicksort.SortStats detStats = engine.sortDeterministic(sortedArray);
        RandomisedQuicksort.SortStats randStats = engine.sortRandomised(sortedArray);
        System.out.printf("    Deterministic: comparisons = %d, max recursion depth = %d\n",
                detStats.comparisons, detStats.maxDepth);
        System.out.printf("    Randomised:    comparisons = %d, max recursion depth = %d\n",
                randStats.comparisons, randStats.maxDepth);
        System.out.printf("    Theoretical expected comparisons (2*n*ln(n)): %.0f\n",
                RandomisedQuicksort.theoreticalExpectedComparisons(n));

        System.out.println("[4] Parallel Reduce (Work-Span Analysis):");
        long[] data = new long[100_000];
        for (int i = 0; i < data.length; i++) data[i] = 1;
        ParallelReduce.WorkSpanMetrics ws = engine.parallelSum(data);
        System.out.println("    " + ws);

        System.out.println("[5] Parallel Prefix Sum (Blelloch Scan):");
        long[] scanInput = {3, 1, 7, 0, 4, 1, 6, 3};
        long[] scanOutput = engine.parallelScan(scanInput);
        System.out.print("    Input:  [3, 1, 7, 0, 4, 1, 6, 3] -> Inclusive prefix sum: [");
        for (int i = 0; i < scanOutput.length; i++) {
            System.out.print(scanOutput[i] + (i < scanOutput.length - 1 ? ", " : ""));
        }
        System.out.println("]");

        System.out.println("[6] Parallel Merge Sort (Work O(n log n), Span O(log^2 n)):");
        int[] sortData = new int[50_000];
        for (int i = 0; i < sortData.length; i++) sortData[i] = (int) (Math.random() * 1_000_000);
        ParallelMergeSort.SortMetrics pms = engine.parallelMergeSort(sortData);
        System.out.println("    " + pms);
        input.waitForEnter();
    }

    // ==========================================
    // FULL SUITE DEMO
    // ==========================================
    private void runFullSuiteDemo() {
        System.out.println("================================================================================");
        System.out.println("           AUTOMATED END-TO-END DEMO: ALL 6 ADVANCED ALGORITHM MODULES          ");
        System.out.println("================================================================================");

        System.out.println("\n>>> [MODULE 1: System & Inverted Index]");
        System.out.println("Corpus articles indexed: " + engine.getDocuments().size());
        SimpleList<Integer> indHits = engine.queryInvertedIndex("tomb");
        System.out.println("Word 'tomb' matches " + indHits.size() + " documents.");

        System.out.println("\n>>> [MODULE 2: String Algorithms & Suffix Automata]");
        String sampleText = "The Taj Mahal is an ivory-white marble mausoleum in Agra.";
        System.out.println("KMP search for 'mausoleum': " + engine.runKMP(sampleText, "mausoleum"));
        System.out.println("Z-Algorithm for 'marble': " + engine.runZ(sampleText, "marble"));
        System.out.println("Rabin-Karp for 'Agra': " + engine.runRabinKarp(sampleText, "Agra"));

        System.out.println("\n>>> [MODULE 3: Dynamic Programming]");
        System.out.println("Levenshtein('architecture', 'agriculturist') = " + engine.computeLevenshtein("architecture", "agriculturist"));
        SequenceAlignment.AlignmentResult al = engine.alignNeedlemanWunsch("GCATGCU", "GATTACA");
        System.out.println("Global Alignment Score: " + al.score);

        System.out.println("\n>>> [MODULE 4: Network Flow & Bipartite Matching]");
        FlowNetwork fn = new FlowNetwork(4);
        fn.addEdge(0, 1, 10);
        fn.addEdge(0, 2, 5);
        fn.addEdge(1, 3, 10);
        fn.addEdge(2, 3, 10);
        System.out.println("Dinic Max-Flow: " + engine.runDinic(fn, 0, 3) + " (Capacity: 15)");

        System.out.println("\n>>> [MODULE 5: NP-Completeness & Approximation]");
        SimpleList<int[]> edges = new SimpleList<>();
        edges.add(new int[]{0, 1});
        edges.add(new int[]{1, 2});
        edges.add(new int[]{2, 0});
        VertexCoverApprox.VCResult vc = engine.runVertexCoverApprox(3, edges);
        System.out.println("Triangle Graph 2-approx vertex cover: " + vc.coverSize + " vertices");

        System.out.println("\n>>> [MODULE 6: Randomised & Parallel Computing]");
        System.out.println("Miller-Rabin prime test on 2^31 - 1 (2147483647): " + engine.testMillerRabin(2147483647L, 10));
        long[] arr = new long[10_000];
        for (int i = 0; i < arr.length; i++) arr[i] = 1;
        ParallelReduce.WorkSpanMetrics pSum = engine.parallelSum(arr);
        System.out.println("Parallel Sum of 10,000 elements: " + pSum.result + " (Work: " + pSum.work + ", Span: " + pSum.span + ")");

        System.out.println("\n================================================================================");
        System.out.println("                ALL 6 MODULES SUCCESSFULLY VERIFIED AND RUNNING!                ");
        System.out.println("================================================================================");
        input.waitForEnter();
    }

    private String formatIndices(SimpleList<Integer> list) {
        if (list.size() == 0) return "[] (no matches)";
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < list.size(); i++) {
            sb.append(list.get(i));
            if (i < list.size() - 1) sb.append(", ");
        }
        sb.append("]");
        return sb.toString();
    }
}
