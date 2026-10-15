package texthack.module6;

import java.math.BigInteger;

/**
 * Miller-Rabin Randomised Primality Test.
 *
 * Concepts demonstrated:
 *   1. Fermat's Little Theorem: a^(p-1) = 1 (mod p) for prime p and gcd(a, p) = 1.
 *   2. Carmichael numbers (e.g. 561, 1105) fool Fermat's test for ALL coprime bases.
 *   3. Miller-Rabin nontrivial square roots of 1: x^2 = 1 (mod p) has only x = 1 or x = p - 1.
 *      If we ever encounter a nontrivial square root of 1 modulo n, n MUST be composite.
 *   4. Error bound: For any composite odd n, at least 3/4 of all bases a in [2, n-2] are
 *      witnesses to compositeness. Repeating with k random bases reduces error to <= (1/4)^k.
 *   5. For 64-bit integers, deterministic testing is possible using small fixed witness sets
 *      (e.g., {2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37}).
 */
public class MillerRabin {

    // Simple custom pseudo-random number generator (64-bit XorShift) - no java.util.Random needed
    private static long rngState = 88172645463325252L;

    private static synchronized long nextRandomLong() {
        rngState ^= (rngState << 13);
        rngState ^= (rngState >>> 7);
        rngState ^= (rngState << 17);
        return rngState;
    }

    /**
     * Modular multiplication (a * b) % m avoiding 64-bit overflow using Russian Peasant algorithm.
     */
    public static long mulMod(long a, long b, long m) {
        long res = 0;
        a %= m;
        b %= m;
        while (b > 0) {
            if ((b & 1) == 1) {
                res = (res + a) % m;
            }
            a = (a + a) % m;
            b >>= 1;
        }
        return res;
    }

    /**
     * Modular exponentiation: (base^exp) % mod.
     */
    public static long powerMod(long base, long exp, long mod) {
        long res = 1;
        base %= mod;
        while (exp > 0) {
            if ((exp & 1) == 1) {
                res = mulMod(res, base, mod);
            }
            base = mulMod(base, base, mod);
            exp >>= 1;
        }
        return res;
    }

    /**
     * Fermat primality test for educational comparison with Miller-Rabin.
     * Carmichael numbers will fool this test if base is coprime!
     */
    public static boolean fermatTest(long n, int rounds) {
        if (n <= 1) return false;
        if (n <= 3) return true;
        if ((n & 1) == 0) return false;

        for (int i = 0; i < rounds; i++) {
            long a = 2 + (Math.abs(nextRandomLong()) % (n - 3));
            if (powerMod(a, n - 1, n) != 1) {
                return false; // Definitely composite
            }
        }
        return true; // Probably prime (could be a Carmichael number!)
    }

    /**
     * Single round of Miller-Rabin for odd n with base a:
     * n - 1 = d * 2^s where d is odd.
     * Returns true if n is composite (a is a witness), false if probable prime.
     */
    private static boolean isWitness(long a, long d, int s, long n) {
        long x = powerMod(a, d, n);
        if (x == 1 || x == n - 1) return false;

        for (int r = 1; r < s; r++) {
            x = mulMod(x, x, n);
            if (x == n - 1) return false;
            if (x == 1) return true; // Nontrivial square root of 1 found -> composite!
        }
        return true; // a^(n-1) != 1 -> composite!
    }

    /**
     * Probabilistic Miller-Rabin test with k independent random bases.
     * Probability of false positive <= (1/4)^k.
     */
    public static boolean isProbablePrime(long n, int k) {
        if (n <= 1) return false;
        if (n <= 3) return true;
        if ((n & 1) == 0) return false;

        // Write n - 1 as d * 2^s
        long d = n - 1;
        int s = 0;
        while ((d & 1) == 0) {
            d >>= 1;
            s++;
        }

        for (int i = 0; i < k; i++) {
            long a = 2 + (Math.abs(nextRandomLong()) % (n - 3));
            if (isWitness(a, d, s, n)) {
                return false; // Definitely composite
            }
        }
        return true; // Probably prime with confidence >= 1 - (1/4)^k
    }

    /**
     * Deterministic Miller-Rabin test for all 64-bit numbers (up to 2^64 - 1)
     * using Jim Sinclair's minimal witness bases set.
     */
    public static boolean isPrime64(long n) {
        if (n <= 1) return false;
        if (n <= 3) return true;
        if ((n & 1) == 0) return false;

        long d = n - 1;
        int s = 0;
        while ((d & 1) == 0) {
            d >>= 1;
            s++;
        }

        // Fixed basis set covering all 64-bit integers deterministically
        long[] bases = {2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37};
        for (long a : bases) {
            if (n == a) return true;
            if (n % a == 0) return false;
            if (isWitness(a, d, s, n)) return false;
        }
        return true;
    }

    /**
     * Test arbitrary-precision BigInteger using Miller-Rabin.
     */
    public static boolean isProbablePrimeBig(BigInteger n, int k) {
        if (n.compareTo(BigInteger.ONE) <= 0) return false;
        if (n.equals(BigInteger.TWO) || n.equals(BigInteger.valueOf(3))) return true;
        if (!n.testBit(0)) return false;

        BigInteger d = n.subtract(BigInteger.ONE);
        int s = 0;
        while (!d.testBit(0)) {
            d = d.shiftRight(1);
            s++;
        }

        for (int i = 0; i < k; i++) {
            // Pick pseudo-random base in [2, n-2] using custom RNG
            byte[] bytes = new byte[(n.bitLength() + 7) / 8];
            for (int b = 0; b < bytes.length; b++) {
                bytes[b] = (byte) (nextRandomLong() & 0xFF);
            }
            BigInteger a = new BigInteger(1, bytes).mod(n.subtract(BigInteger.valueOf(3))).add(BigInteger.TWO);

            BigInteger x = a.modPow(d, n);
            if (x.equals(BigInteger.ONE) || x.equals(n.subtract(BigInteger.ONE))) continue;

            boolean composite = true;
            for (int r = 1; r < s; r++) {
                x = x.modPow(BigInteger.TWO, n);
                if (x.equals(n.subtract(BigInteger.ONE))) {
                    composite = false;
                    break;
                }
                if (x.equals(BigInteger.ONE)) return false;
            }
            if (composite) return false;
        }
        return true;
    }
}
