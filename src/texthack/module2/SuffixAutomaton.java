package texthack.module2;

import texthack.collections.SimpleList;

/**
 * Suffix Automaton (SAM) — a preview-level implementation.
 *
 * A suffix automaton for string S is the smallest DFA that recognises exactly
 * all substrings of S. It has at most 2n−1 states and 3n−4 transitions.
 *
 * Key operations enabled beyond suffix arrays:
 *   - Online construction (characters added one by one)
 *   - O(|P|) substring checks (no binary search needed)
 *   - Counting distinct substrings
 *   - Finding the shortest non-occurring string
 *
 * Time:  O(n) construction
 * Space: O(n · |Σ|) transitions
 */
public class SuffixAutomaton {

    private static final int ALPHABET_SIZE = 128;

    /** A state in the suffix automaton. */
    public static class State {
        int len;        // length of the longest string in this endpos-equivalence class
        int link;       // suffix link
        int[] next;     // transitions
        long cnt;       // number of occurrences (computed after construction)

        State() {
            len = 0;
            link = -1;
            next = new int[ALPHABET_SIZE];
            for (int i = 0; i < ALPHABET_SIZE; i++) next[i] = -1;
            cnt = 0;
        }

        State(State other) {
            this.len = other.len;
            this.link = other.link;
            this.next = new int[ALPHABET_SIZE];
            System.arraycopy(other.next, 0, this.next, 0, ALPHABET_SIZE);
            this.cnt = 0;
        }
    }

    private State[] states;
    private int stateCount;
    private int last;
    private final String text;

    /**
     * Constructs the suffix automaton for the given text.
     */
    public SuffixAutomaton(String text) {
        this.text = text;
        int n = text.length();
        states = new State[2 * n + 2];
        stateCount = 0;

        // Initial state
        states[0] = new State();
        states[0].link = -1;
        states[0].len = 0;
        stateCount = 1;
        last = 0;

        for (int i = 0; i < n; i++) {
            extend(text.charAt(i));
        }
    }

    /**
     * Checks whether pattern is a substring of text in O(|pattern|).
     */
    public boolean contains(String pattern) {
        int cur = 0;
        for (int i = 0; i < pattern.length(); i++) {
            int c = pattern.charAt(i);
            if (c >= ALPHABET_SIZE || states[cur].next[c] == -1) return false;
            cur = states[cur].next[c];
        }
        return true;
    }

    /**
     * Finds the longest substring of text that is also a substring of query.
     */
    public String longestCommonSubstring(String query) {
        int cur = 0, curLen = 0;
        int bestLen = 0, bestPos = 0;

        for (int i = 0; i < query.length(); i++) {
            int c = query.charAt(i);
            if (c >= ALPHABET_SIZE) {
                cur = 0;
                curLen = 0;
                continue;
            }
            while (cur != 0 && states[cur].next[c] == -1) {
                cur = states[cur].link;
                curLen = states[cur].len;
            }
            if (states[cur].next[c] != -1) {
                cur = states[cur].next[c];
                curLen++;
            }
            if (curLen > bestLen) {
                bestLen = curLen;
                bestPos = i;
            }
        }
        if (bestLen == 0) return "";
        return query.substring(bestPos - bestLen + 1, bestPos + 1);
    }

    /**
     * Counts distinct substrings using the suffix automaton.
     * Each state contributes (len - len(link)) distinct substrings.
     */
    public long countDistinctSubstrings() {
        long count = 0;
        for (int i = 1; i < stateCount; i++) {
            int linkLen = (states[i].link >= 0) ? states[states[i].link].len : 0;
            count += states[i].len - linkLen;
        }
        return count;
    }

    /** Returns the number of states in the automaton. */
    public int getStateCount() {
        return stateCount;
    }

    // ----- Online construction: extend by one character -----
    private void extend(char ch) {
        int c = ch;
        if (c >= ALPHABET_SIZE) c = 0;

        int cur = stateCount;
        states[cur] = new State();
        states[cur].len = states[last].len + 1;
        states[cur].cnt = 1;
        stateCount++;

        int p = last;
        while (p != -1 && states[p].next[c] == -1) {
            states[p].next[c] = cur;
            p = states[p].link;
        }

        if (p == -1) {
            states[cur].link = 0;
        } else {
            int q = states[p].next[c];
            if (states[p].len + 1 == states[q].len) {
                states[cur].link = q;
            } else {
                int clone = stateCount;
                states[clone] = new State(states[q]);
                states[clone].len = states[p].len + 1;
                stateCount++;

                while (p != -1 && states[p].next[c] == q) {
                    states[p].next[c] = clone;
                    p = states[p].link;
                }
                states[q].link = clone;
                states[cur].link = clone;
            }
        }
        last = cur;
    }
}
