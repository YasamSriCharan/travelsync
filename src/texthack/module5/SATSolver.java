package texthack.module5;

import texthack.collections.SimpleList;

/**
 * SAT Solver — brute-force satisfiability with 3-SAT reduction demonstrations.
 *
 * SAT is the canonical NP-complete problem (Cook-Levin theorem).
 * This implementation:
 *   1. Brute-force SAT for small instances (up to ~20 variables)
 *   2. 3-SAT clause representation
 *   3. Demonstrates the NP-completeness framework
 *
 * A clause is a disjunction of literals. A formula is a conjunction of clauses (CNF).
 * A literal is a variable (positive) or its negation.
 */
public class SATSolver {

    /**
     * A literal: variable index + sign (positive or negated).
     */
    public static class Literal {
        public final int variable;   // 0-indexed variable
        public final boolean negated;

        public Literal(int variable, boolean negated) {
            this.variable = variable;
            this.negated = negated;
        }

        /** Evaluates this literal under the given assignment. */
        public boolean evaluate(boolean[] assignment) {
            return negated ? !assignment[variable] : assignment[variable];
        }

        @Override
        public String toString() {
            return (negated ? "¬x" : "x") + variable;
        }
    }

    /** A clause is a disjunction (OR) of literals. */
    public static class Clause {
        public final Literal[] literals;

        public Clause(Literal... literals) {
            this.literals = literals;
        }

        /** A clause is satisfied if at least one literal is true. */
        public boolean evaluate(boolean[] assignment) {
            for (Literal lit : literals) {
                if (lit.evaluate(assignment)) return true;
            }
            return false;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder("(");
            for (int i = 0; i < literals.length; i++) {
                if (i > 0) sb.append(" ∨ ");
                sb.append(literals[i]);
            }
            sb.append(")");
            return sb.toString();
        }
    }

    /**
     * Brute-force SAT solver.
     * Tries all 2ⁿ assignments and returns the first satisfying one, or null.
     *
     * @param clauses the CNF formula
     * @param numVars number of variables
     * @return satisfying assignment, or null if unsatisfiable
     *
     * Time: O(2ⁿ · m) where n = variables, m = total literals
     */
    public static boolean[] solve(Clause[] clauses, int numVars) {
        int totalAssignments = 1 << numVars;
        for (int mask = 0; mask < totalAssignments; mask++) {
            boolean[] assignment = maskToAssignment(mask, numVars);
            if (evaluateFormula(clauses, assignment)) {
                return assignment;
            }
        }
        return null; // UNSAT
    }

    /**
     * Counts the number of satisfying assignments.
     */
    public static int countSatisfying(Clause[] clauses, int numVars) {
        int totalAssignments = 1 << numVars;
        int count = 0;
        for (int mask = 0; mask < totalAssignments; mask++) {
            boolean[] assignment = maskToAssignment(mask, numVars);
            if (evaluateFormula(clauses, assignment)) count++;
        }
        return count;
    }

    /**
     * Checks if the formula is satisfiable.
     */
    public static boolean isSatisfiable(Clause[] clauses, int numVars) {
        return solve(clauses, numVars) != null;
    }

    /**
     * Evaluates a CNF formula (conjunction of clauses).
     */
    public static boolean evaluateFormula(Clause[] clauses, boolean[] assignment) {
        for (Clause clause : clauses) {
            if (!clause.evaluate(assignment)) return false;
        }
        return true;
    }

    /**
     * Creates a 3-SAT instance from a given formula.
     * Each clause with k > 3 literals is split into multiple 3-literal clauses
     * using the Tseitin transformation (introducing auxiliary variables).
     */
    public static Clause[] convertTo3SAT(Clause[] clauses, int numVars) {
        SimpleList<Clause> result = new SimpleList<>();
        int nextVar = numVars;

        for (Clause clause : clauses) {
            if (clause.literals.length <= 3) {
                result.add(clause);
            } else {
                // Tseitin transformation
                Literal[] lits = clause.literals;
                int auxStart = nextVar;
                int numAux = lits.length - 3;
                nextVar += numAux;

                // First clause: (l₁ ∨ l₂ ∨ y₁)
                result.add(new Clause(lits[0], lits[1], new Literal(auxStart, false)));

                // Middle clauses: (¬yᵢ ∨ lᵢ₊₂ ∨ yᵢ₊₁)
                for (int i = 0; i < numAux - 1; i++) {
                    result.add(new Clause(
                        new Literal(auxStart + i, true),
                        lits[i + 2],
                        new Literal(auxStart + i + 1, false)
                    ));
                }

                // Last clause: (¬yₖ ∨ lₙ₋₁ ∨ lₙ)
                result.add(new Clause(
                    new Literal(auxStart + numAux - 1, true),
                    lits[lits.length - 2],
                    lits[lits.length - 1]
                ));
            }
        }

        Clause[] arr = new Clause[result.size()];
        for (int i = 0; i < result.size(); i++) arr[i] = result.get(i);
        return arr;
    }

    private static boolean[] maskToAssignment(int mask, int numVars) {
        boolean[] assignment = new boolean[numVars];
        for (int i = 0; i < numVars; i++) {
            assignment[i] = ((mask >> i) & 1) == 1;
        }
        return assignment;
    }
}
