package texthack.module2;

import texthack.collections.SimpleList;
import texthack.collections.SimpleQueue;

/**
 * Aho-Corasick algorithm — simultaneous multi-pattern matching.
 *
 * Builds a trie with failure links (suffix links) that enable
 * matching all patterns against the text in a single pass.
 *
 * Time:  O(n + m + z) where n = |text|, m = total pattern length, z = number of matches
 * Space: O(m · |Σ|) for the trie
 */
public class AhoCorasick {

    private static final int ALPHABET_SIZE = 128; // ASCII

    /** Internal trie node. */
    private static class Node {
        int[] children = new int[ALPHABET_SIZE];
        int failureLink = 0;
        SimpleList<Integer> output = new SimpleList<>(); // pattern indices that end here
        int depth = 0;

        Node() {
            for (int i = 0; i < ALPHABET_SIZE; i++) children[i] = -1;
        }
    }

    private final Node[] nodes;
    private int nodeCount;
    private final String[] patterns;

    /**
     * Constructs the Aho-Corasick automaton from the given patterns.
     */
    public AhoCorasick(String[] patterns) {
        this.patterns = patterns;
        int totalLen = 0;
        for (String p : patterns) totalLen += p.length();
        nodes = new Node[totalLen + patterns.length + 1];
        nodes[0] = new Node();
        nodeCount = 1;
        buildTrie();
        buildFailureLinks();
    }

    /**
     * Searches the text for all pattern occurrences.
     * @return list of (patternIndex, startPosition) pairs
     */
    public SimpleList<int[]> search(String text) {
        SimpleList<int[]> results = new SimpleList<>();
        int state = 0;

        for (int i = 0; i < text.length(); i++) {
            int c = text.charAt(i);
            if (c >= ALPHABET_SIZE) c = 0; // map non-ASCII to 0

            while (state != 0 && nodes[state].children[c] == -1) {
                state = nodes[state].failureLink;
            }
            if (nodes[state].children[c] != -1) {
                state = nodes[state].children[c];
            }

            // Collect all patterns ending at this position
            int temp = state;
            while (temp != 0) {
                SimpleList<Integer> out = nodes[temp].output;
                for (int j = 0; j < out.size(); j++) {
                    int patIdx = out.get(j);
                    int startPos = i - patterns[patIdx].length() + 1;
                    results.add(new int[]{patIdx, startPos});
                }
                temp = nodes[temp].failureLink;
            }
        }
        return results;
    }

    /**
     * Returns just the count of total matches.
     */
    public int countMatches(String text) {
        return search(text).size();
    }

    // ----- Trie construction -----
    private void buildTrie() {
        for (int p = 0; p < patterns.length; p++) {
            int current = 0;
            for (int i = 0; i < patterns[p].length(); i++) {
                int c = patterns[p].charAt(i);
                if (c >= ALPHABET_SIZE) c = 0;
                if (nodes[current].children[c] == -1) {
                    nodes[nodeCount] = new Node();
                    nodes[nodeCount].depth = nodes[current].depth + 1;
                    nodes[current].children[c] = nodeCount;
                    nodeCount++;
                }
                current = nodes[current].children[c];
            }
            nodes[current].output.add(p);
        }
    }

    // ----- BFS to build failure links -----
    private void buildFailureLinks() {
        SimpleQueue<Integer> queue = new SimpleQueue<>();
        // Root's children: set failure link to root
        for (int c = 0; c < ALPHABET_SIZE; c++) {
            if (nodes[0].children[c] != -1) {
                nodes[nodes[0].children[c]].failureLink = 0;
                queue.enqueue(nodes[0].children[c]);
            }
        }

        while (!queue.isEmpty()) {
            int u = queue.dequeue();
            for (int c = 0; c < ALPHABET_SIZE; c++) {
                int v = nodes[u].children[c];
                if (v == -1) continue;

                int f = nodes[u].failureLink;
                while (f != 0 && nodes[f].children[c] == -1) {
                    f = nodes[f].failureLink;
                }
                nodes[v].failureLink = (nodes[f].children[c] != -1 && nodes[f].children[c] != v)
                        ? nodes[f].children[c] : 0;

                // Merge output from failure link (suffix link output)
                SimpleList<Integer> fOut = nodes[nodes[v].failureLink].output;
                for (int j = 0; j < fOut.size(); j++) {
                    nodes[v].output.add(fOut.get(j));
                }
                queue.enqueue(v);
            }
        }
    }
}
