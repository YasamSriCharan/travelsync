# TextHack — Advanced Data Structures & Algorithms (DSA-3)
## The Algorithm Question Bank & Text-Analytics Engine

TextHack is a comprehensive, production-grade advanced algorithm laboratory and text-analytics system built entirely from scratch in Java with **zero reliance on `java.util.*` inside the engine**.

---

## 🏛️ Theoretical Canon & Textbook Mapping

TextHack is structured around the advanced algorithms canon from **CLRS Part VII**, **Kleinberg-Tardos (Chapters 6–13)**, and **Jeff Erickson (Chapters 4–12)**:

| Module | Algorithmic Family | Canonical Problems | Textbook Reference | TextHack Engine Implementation |
|---|---|---|---|---|
| **Module 1** | **System Architecture & Information Retrieval** | Inverted Indexing, Citation Graphs, Frequency Analysis | Manning et al. Ch. 1–2 | [`CorpusLoader.java`](file:///c:/Users/CHARAN/OneDrive/Desktop/DSA-3/DSA3-Project/src/texthack/module1/CorpusLoader.java) |
| **Module 2** | **Exact String Algorithms & Automata** | Sub-quadratic pattern search, Multi-pattern matching, Suffix arrays, Suffix automata | CLRS Ch. 32, Gusfield Ch. 1–7 | [`KMP.java`](file:///c:/Users/CHARAN/OneDrive/Desktop/DSA-3/DSA3-Project/src/texthack/module2/KMP.java), [`ZFunction.java`](file:///c:/Users/CHARAN/OneDrive/Desktop/DSA-3/DSA3-Project/src/texthack/module2/ZFunction.java), [`RabinKarp.java`](file:///c:/Users/CHARAN/OneDrive/Desktop/DSA-3/DSA3-Project/src/texthack/module2/RabinKarp.java), [`AhoCorasick.java`](file:///c:/Users/CHARAN/OneDrive/Desktop/DSA-3/DSA3-Project/src/texthack/module2/AhoCorasick.java), [`SuffixArray.java`](file:///c:/Users/CHARAN/OneDrive/Desktop/DSA-3/DSA3-Project/src/texthack/module2/SuffixArray.java), [`SuffixAutomaton.java`](file:///c:/Users/CHARAN/OneDrive/Desktop/DSA-3/DSA3-Project/src/texthack/module2/SuffixAutomaton.java) |
| **Module 3** | **Advanced Dynamic Programming** | Sequence Alignment, Interval DP, Bitmask DP, Tree DP, Sum-Over-Subsets | Kleinberg-Tardos Ch. 6, Erickson Ch. 3 | [`EditDistance.java`](file:///c:/Users/CHARAN/OneDrive/Desktop/DSA-3/DSA3-Project/src/texthack/module3/EditDistance.java), [`SequenceAlignment.java`](file:///c:/Users/CHARAN/OneDrive/Desktop/DSA-3/DSA3-Project/src/texthack/module3/SequenceAlignment.java), [`IntervalDP.java`](file:///c:/Users/CHARAN/OneDrive/Desktop/DSA-3/DSA3-Project/src/texthack/module3/IntervalDP.java), [`BitmaskDP.java`](file:///c:/Users/CHARAN/OneDrive/Desktop/DSA-3/DSA3-Project/src/texthack/module3/BitmaskDP.java), [`TreeDP.java`](file:///c:/Users/CHARAN/OneDrive/Desktop/DSA-3/DSA3-Project/src/texthack/module3/TreeDP.java), [`SumOverSubsets.java`](file:///c:/Users/CHARAN/OneDrive/Desktop/DSA-3/DSA3-Project/src/texthack/module3/SumOverSubsets.java) |
| **Module 4** | **Network Flow & Cut Duality** | Max-Flow Min-Cut, Level Graphs, Bipartite Matching, Min-Cost Flow | CLRS Ch. 26, Kleinberg-Tardos Ch. 7 | [`FlowNetwork.java`](file:///c:/Users/CHARAN/OneDrive/Desktop/DSA-3/DSA3-Project/src/texthack/module4/FlowNetwork.java), [`FordFulkerson.java`](file:///c:/Users/CHARAN/OneDrive/Desktop/DSA-3/DSA3-Project/src/texthack/module4/FordFulkerson.java), [`EdmondsKarp.java`](file:///c:/Users/CHARAN/OneDrive/Desktop/DSA-3/DSA3-Project/src/texthack/module4/EdmondsKarp.java), [`Dinic.java`](file:///c:/Users/CHARAN/OneDrive/Desktop/DSA-3/DSA3-Project/src/texthack/module4/Dinic.java), [`MinCut.java`](file:///c:/Users/CHARAN/OneDrive/Desktop/DSA-3/DSA3-Project/src/texthack/module4/MinCut.java), [`BipartiteMatching.java`](file:///c:/Users/CHARAN/OneDrive/Desktop/DSA-3/DSA3-Project/src/texthack/module4/BipartiteMatching.java), [`MinCostMaxFlow.java`](file:///c:/Users/CHARAN/OneDrive/Desktop/DSA-3/DSA3-Project/src/texthack/module4/MinCostMaxFlow.java) |
| **Module 5** | **Intractability, Reductions & Approximations** | Cook-Levin, Karp's 21 reductions, Constant-factor approximations, FPTAS, FPT | CLRS Ch. 34–35, Kleinberg-Tardos Ch. 8, 11 | [`SATSolver.java`](file:///c:/Users/CHARAN/OneDrive/Desktop/DSA-3/DSA3-Project/src/texthack/module5/SATSolver.java), [`ReductionDemo.java`](file:///c:/Users/CHARAN/OneDrive/Desktop/DSA-3/DSA3-Project/src/texthack/module5/ReductionDemo.java), [`VertexCoverApprox.java`](file:///c:/Users/CHARAN/OneDrive/Desktop/DSA-3/DSA3-Project/src/texthack/module5/VertexCoverApprox.java), [`TSPApprox.java`](file:///c:/Users/CHARAN/OneDrive/Desktop/DSA-3/DSA3-Project/src/texthack/module5/TSPApprox.java), [`KnapsackFPTAS.java`](file:///c:/Users/CHARAN/OneDrive/Desktop/DSA-3/DSA3-Project/src/texthack/module5/KnapsackFPTAS.java), [`FPTDemo.java`](file:///c:/Users/CHARAN/OneDrive/Desktop/DSA-3/DSA3-Project/src/texthack/module5/FPTDemo.java) |
| **Module 6** | **Randomised & Parallel Computing** | Las Vegas vs Monte Carlo, Universal Hashing, Reservoir Sampling, Work-Span Blelloch Scan | CLRS Ch. 5, 27, Motwani-Raghavan | [`MillerRabin.java`](file:///c:/Users/CHARAN/OneDrive/Desktop/DSA-3/DSA3-Project/src/texthack/module6/MillerRabin.java), [`UniversalHash.java`](file:///c:/Users/CHARAN/OneDrive/Desktop/DSA-3/DSA3-Project/src/texthack/module6/UniversalHash.java), [`ReservoirSampling.java`](file:///c:/Users/CHARAN/OneDrive/Desktop/DSA-3/DSA3-Project/src/texthack/module6/ReservoirSampling.java), [`RandomisedQuicksort.java`](file:///c:/Users/CHARAN/OneDrive/Desktop/DSA-3/DSA3-Project/src/texthack/module6/RandomisedQuicksort.java), [`ParallelReduce.java`](file:///c:/Users/CHARAN/OneDrive/Desktop/DSA-3/DSA3-Project/src/texthack/module6/ParallelReduce.java), [`ParallelPrefixSum.java`](file:///c:/Users/CHARAN/OneDrive/Desktop/DSA-3/DSA3-Project/src/texthack/module6/ParallelPrefixSum.java), [`ParallelMergeSort.java`](file:///c:/Users/CHARAN/OneDrive/Desktop/DSA-3/DSA3-Project/src/texthack/module6/ParallelMergeSort.java) |

---

## 📦 Project Structure

```
src/
├── texthack/
│   ├── TextHackEngine.java          # Public API facade uniting all 6 modules
│   ├── Main.java                    # Interactive terminal console UI & verification runner
│   ├── InputHelper.java             # Safe console input handler
│   │
│   ├── collections/                 # Hand-crafted Collections (NO java.util.*)
│   │   ├── SimpleList.java          # Dynamic resizing array with O(1) amortised append
│   │   ├── SimpleMap.java           # Separate-chaining hash table with resize & rehashing
│   │   ├── SimpleQueue.java         # Ring-buffer FIFO queue
│   │   ├── SimpleSet.java           # Hash set backed by SimpleMap
│   │   └── SimplePriorityQueue.java # Binary min-heap with bubble-up and bubble-down
│   │
│   ├── module1/                     # System & Indian-Language Wikipedia Corpus
│   │   └── CorpusLoader.java        # Document indexer & citation graph generator
│   │
│   ├── module2/                     # Exact String Algorithms
│   │   ├── KMP.java                 # Knuth-Morris-Pratt O(n + m) with prefix table
│   │   ├── ZFunction.java           # Gusfield Z-algorithm O(n + m)
│   │   ├── RabinKarp.java           # Rolling polynomial hash with Horner's rule
│   │   ├── AhoCorasick.java         # Multi-pattern matching automaton with failure links
│   │   ├── SuffixArray.java         # Suffix array + Kasai O(n) LCP computation
│   │   └── SuffixAutomaton.java     # Linear state automaton representing all substrings
│   │
│   ├── module3/                     # Advanced Dynamic Programming
│   │   ├── EditDistance.java        # Levenshtein & Damerau-Levenshtein distance
│   │   ├── SequenceAlignment.java   # Needleman-Wunsch & Smith-Waterman with traceback
│   │   ├── IntervalDP.java          # Matrix chain multiplication & Knuth-optimised OBST
│   │   ├── BitmaskDP.java           # Held-Karp TSP O(n² 2ⁿ) & Hamiltonian path
│   │   ├── TreeDP.java              # Tree Maximum Independent Set & tree diameter
│   │   └── SumOverSubsets.java      # Fast Möbius/Zeta transform over subset lattice
│   │
│   ├── module4/                     # Network Flow & Cut Duality
│   │   ├── FlowNetwork.java         # Residual graph with paired reverse edges
│   │   ├── FordFulkerson.java       # Augmenting paths via DFS
│   │   ├── EdmondsKarp.java         # Shortest augmenting paths via BFS O(V E²)
│   │   ├── Dinic.java               # Blocking flows on level graphs O(V² E)
│   │   ├── MinCut.java              # Residual reachability cut partition
│   │   ├── BipartiteMatching.java   # Hopcroft-Karp / Flow reduction + König's theorem
│   │   └── MinCostMaxFlow.java      # Successive Shortest Path (SSP) with SPFA
│   │
│   ├── module5/                     # NP-Completeness, Reductions & Approximation
│   │   ├── SATSolver.java           # 3-SAT Tseitin solver & Cook-Levin demonstration
│   │   ├── ReductionDemo.java       # Poly-time reduction zoo (SAT→CLIQUE→IS→VC, etc.)
│   │   ├── VertexCoverApprox.java   # 2-approximation via maximal matching (OPT guarantee)
│   │   ├── TSPApprox.java           # Metric TSP 2-approximation via MST doubling & shortcut
│   │   ├── KnapsackFPTAS.java       # Value-scaled (1-ε) FPTAS running in O(n²/ε)
│   │   └── FPTDemo.java             # O(2^k · n) bounded search tree & O(k²) kernelisation
│   │
│   └── module6/                     # Randomised & Parallel Computing
│       ├── MillerRabin.java         # Monte Carlo primality test & Carmichael numbers
│       ├── UniversalHash.java       # Carter-Wegman 2-universal hash family & collisions
│       ├── ReservoirSampling.java   # Knuth Algorithm R & Li's geometric-jump Algorithm L
│       ├── RandomisedQuicksort.java # Las Vegas quicksort vs adversarial inputs
│       ├── ParallelReduce.java      # Work-span model: Work O(n), Span O(log n)
│       ├── ParallelPrefixSum.java   # Guy Blelloch work-efficient parallel scan
│       └── ParallelMergeSort.java   # Parallel divide-and-conquer with parallel merge
```

---

## ⚡ How to Build & Run

### 1. Compile

From the workspace root directory:

```powershell
javac -d bin -cp src (Get-ChildItem -Recurse -Filter *.java src | ForEach-Object { $_.FullName })
```

### 2. Run Interactive Console

```powershell
java -cp bin texthack.Main
```

### 3. Automated Verification Run

To run all 6 modules end-to-end without manual menu navigation:

```powershell
"7`n`n0" | java -cp bin texthack.Main
```
