package texthack.module6;

import texthack.collections.SimpleList;

/**
 * Universal Hashing — Carter-Wegman 2-Universal Hash Family.
 *
 * Concepts demonstrated:
 *   1. Deterministic hashing has adversarial worst-case inputs that force O(n) lookups.
 *   2. A 2-universal family H guarantees that for any two distinct keys x != y:
 *        Pr_{h in H} [ h(x) == h(y) ] <= 1 / m
 *   3. Carter-Wegman Linear Congruential Family:
 *        h_{a,b}(x) = ((a * x + b) mod p) mod m
 *      where p is a prime > max(x, m), a in [1, p-1], b in [0, p-1].
 *   4. String hashing via polynomial evaluation with randomised indeterminate x.
 *   5. Collision rate empirical verification comparing observed collisions to theoretical expectation.
 */
public class UniversalHash {

    // Mersenne prime 2^31 - 1 = 2147483647
    public static final long MERSENNE_PRIME_31 = 2147483647L;

    // Mersenne prime 2^61 - 1
    public static final long MERSENNE_PRIME_61 = 2305843009213693951L;

    private final long a;
    private final long b;
    private final long p;
    private final int m; // Number of buckets

    /**
     * Constructs a Carter-Wegman universal hash function with random parameters.
     */
    public UniversalHash(int numBuckets, long seed) {
        this.m = numBuckets;
        this.p = MERSENNE_PRIME_31;
        long s = seed == 0 ? 123456789L : seed;
        // XorShift parameters
        s ^= (s << 13);
        s ^= (s >>> 7);
        s ^= (s << 17);
        long candidateA = Math.abs(s) % (p - 1) + 1;
        s ^= (s << 13);
        s ^= (s >>> 7);
        s ^= (s << 17);
        long candidateB = Math.abs(s) % p;

        this.a = candidateA;
        this.b = candidateB;
    }

    /**
     * Constructs with explicit parameters (for reproducibility/testing).
     */
    public UniversalHash(int numBuckets, long a, long b, long prime) {
        this.m = numBuckets;
        this.a = a;
        this.b = b;
        this.p = prime;
    }

    /**
     * Hash integer key x using Carter-Wegman formula:
     * ((a * x + b) mod p) mod m
     */
    public int hash(long x) {
        long nonNegX = Math.abs(x) % p;
        long val = (MillerRabin.mulMod(a, nonNegX, p) + b) % p;
        return (int) (val % m);
    }

    /**
     * Hash a string using polynomial hashing with universal coefficients.
     * Evaluates sum_{i=0}^{n-1} str[i] * a^(n - 1 - i) + b mod p, then mod m.
     */
    public int hashString(String str) {
        if (str == null || str.isEmpty()) return 0;
        long h = 0;
        for (int i = 0; i < str.length(); i++) {
            h = (MillerRabin.mulMod(h, a, p) + str.charAt(i)) % p;
        }
        h = (h + b) % p;
        return (int) (h % m);
    }

    public long getA() { return a; }
    public long getB() { return b; }
    public long getPrime() { return p; }
    public int getBuckets() { return m; }

    /**
     * Demonstrates collision rate across a list of strings.
     * Returns the collision percentage vs the theoretical bound 1/m.
     */
    public static CollisionReport evaluateCollisions(SimpleList<String> words, int buckets, int trials) {
        int totalCollisions = 0;
        int n = words.size();
        if (n <= 1) {
            return new CollisionReport(0, 0, 1.0 / buckets);
        }

        long pairs = (long) n * (n - 1) / 2;

        for (int t = 0; t < trials; t++) {
            UniversalHash uh = new UniversalHash(buckets, 10007L * (t + 1) + 42);
            int[] bucketCounts = new int[buckets];
            for (int i = 0; i < n; i++) {
                int h = uh.hashString(words.get(i));
                bucketCounts[h]++;
            }
            int roundCollisions = 0;
            for (int count : bucketCounts) {
                if (count > 1) {
                    roundCollisions += count * (count - 1) / 2;
                }
            }
            totalCollisions += roundCollisions;
        }

        double avgCollisions = (double) totalCollisions / trials;
        double observedCollisionRate = avgCollisions / pairs;
        double theoreticalBound = 1.0 / buckets;

        return new CollisionReport(avgCollisions, observedCollisionRate, theoreticalBound);
    }

    public static class CollisionReport {
        public final double avgCollisions;
        public final double observedRate;
        public final double theoreticalRate;

        public CollisionReport(double avgCollisions, double observedRate, double theoreticalRate) {
            this.avgCollisions = avgCollisions;
            this.observedRate = observedRate;
            this.theoreticalRate = theoreticalRate;
        }

        @Override
        public String toString() {
            return String.format("Observed collision rate: %.5f | Theoretical bound (1/m): %.5f",
                    observedRate, theoreticalRate);
        }
    }
}
