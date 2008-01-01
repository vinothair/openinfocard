package org.xmldap.crypto;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Random;

import org.bouncycastle.asn1.pkcs.RSAPrivateKeyStructure;

public class AnsiX931 {
	private static final BigInteger TWO = BigInteger.valueOf(2);

	static final int SHA1_BYTE_LENGTH = 160 / 8;

	private AnsiX931() {
	}

	public static RSAPrivateKeyStructure generateRSAKeyPairFromMasterKey(
			byte[] masterkey, byte[] rpIdentifier, int requiredKeySize)
			throws Exception {
		byte[][] digests = null;
		int numberOfDigests;

		if (requiredKeySize == 1024) {
			numberOfDigests = 10;
		} else if (requiredKeySize == 2048) {
			numberOfDigests = 16;
		} else {
			throw new IllegalAccessException(
					"required key size must be 1024 or 2048. It was: "
							+ requiredKeySize);
		}

		digests = new byte[numberOfDigests][SHA1_BYTE_LENGTH];

		{
			byte[] mtcBytes = new byte[32 + rpIdentifier.length + 4];
			System.arraycopy(masterkey, 0, mtcBytes, 0, 32);
			System
					.arraycopy(rpIdentifier, 0, mtcBytes, 32,
							rpIdentifier.length);
			mtcBytes[32 + rpIdentifier.length] = 0;
			mtcBytes[32 + rpIdentifier.length + 1] = 0;
			mtcBytes[32 + rpIdentifier.length + 2] = 0;
			mtcBytes[32 + rpIdentifier.length + 3] = 0;

			for (byte n = 0; n < numberOfDigests; n++) {
				MessageDigest md = MessageDigest.getInstance("SHA");
				mtcBytes[32 + rpIdentifier.length + 3] = n;
				md.update(mtcBytes);
				byte[] digest = md.digest();
				System.arraycopy(digest, 0, digests[n], 0, SHA1_BYTE_LENGTH);
			}
		}

		// compute Xp1, Xp2, Xq1, and Xq2
		byte[] xp1Bytes = new byte[SHA1_BYTE_LENGTH];
		byte[] xp2Bytes = new byte[SHA1_BYTE_LENGTH];
		byte[] xq1Bytes = new byte[SHA1_BYTE_LENGTH];
		byte[] xq2Bytes = new byte[SHA1_BYTE_LENGTH];
		byte[] xpBytes;
		byte[] xqBytes;
		if (requiredKeySize == 1024) {
			xpBytes = new byte[64];
			xqBytes = new byte[64];
		} else { // requiredKeySize == 2048
			xpBytes = new byte[128];
			xqBytes = new byte[128];
		}

		System.arraycopy(digests[0], 0, xp1Bytes, 0, 112 / 8);
		System.arraycopy(digests[1], 0, xp2Bytes, 0, 112 / 8);
		System.arraycopy(digests[2], 0, xq1Bytes, 0, 112 / 8);
		System.arraycopy(digests[3], 0, xq2Bytes, 0, 112 / 8);

		xp1Bytes[0] |= 0x80;
		xp2Bytes[0] |= 0x80;
		xq1Bytes[0] |= 0x80;
		xq2Bytes[0] |= 0x80;

		// Get the first 512 bits (64 bytes) for Xp and Xq

		// Xp = H4[1..160] + H5[1..160] + H6[1..160] + H0[129..160]
		System.arraycopy(digests[4], 0, xpBytes, 0, SHA1_BYTE_LENGTH);
		System.arraycopy(digests[5], 0, xpBytes, SHA1_BYTE_LENGTH, SHA1_BYTE_LENGTH);
		System.arraycopy(digests[6], 0, xpBytes, SHA1_BYTE_LENGTH * 2, SHA1_BYTE_LENGTH);
		System.arraycopy(digests[0], 0, xpBytes, SHA1_BYTE_LENGTH * 3, 4);

		// Xq = H7[1..160] + H8[1..160] + H9[1..160] + H1[129..160]
		System.arraycopy(digests[7], 0, xqBytes, 0, SHA1_BYTE_LENGTH);
		System.arraycopy(digests[8], 0, xqBytes, SHA1_BYTE_LENGTH, SHA1_BYTE_LENGTH);
		System.arraycopy(digests[9], 0, xqBytes, SHA1_BYTE_LENGTH * 2, SHA1_BYTE_LENGTH);
		System.arraycopy(digests[1], 0, xqBytes, SHA1_BYTE_LENGTH * 3, 4);

		// Get the next 512 bits (64 bytes) for Xp and Xq

		if (requiredKeySize == 2048) {
			// Xp += H10[1..160] + H11[1..160] + H12[1..160] + H2[129..160]
			System.arraycopy(digests[10], 0, xpBytes, 64, SHA1_BYTE_LENGTH);
			System.arraycopy(digests[11], 0, xpBytes, 64 + SHA1_BYTE_LENGTH, SHA1_BYTE_LENGTH);
			System.arraycopy(digests[12], 0, xpBytes, 64 + SHA1_BYTE_LENGTH * 2, SHA1_BYTE_LENGTH);
			System.arraycopy(digests[2],  0, xpBytes, 64 + SHA1_BYTE_LENGTH * 3, 4);

			// Xq += H13[1..160] + H14[1..160] + H15[1..160] + H3[129..160]
			System.arraycopy(digests[13], 0, xqBytes, 64, SHA1_BYTE_LENGTH);
			System.arraycopy(digests[14], 0, xqBytes, 64 + SHA1_BYTE_LENGTH, SHA1_BYTE_LENGTH);
			System.arraycopy(digests[15], 0, xqBytes, 64 + SHA1_BYTE_LENGTH * 2, SHA1_BYTE_LENGTH);
			System.arraycopy(digests[3],  0, xqBytes, 64 + SHA1_BYTE_LENGTH * 3, 4);
		}

		xpBytes[0] |= 0xC0;
		xqBytes[0] |= 0xC0;

		BigInteger xp1 = new BigInteger(xp1Bytes);
		BigInteger xp2 = new BigInteger(xp2Bytes);
		BigInteger xq1 = new BigInteger(xq1Bytes);
		BigInteger xq2 = new BigInteger(xq2Bytes);
		BigInteger xp = new BigInteger(xpBytes);
		BigInteger xq = new BigInteger(xqBytes);

		// 1024: |Xp - Xq| >= 2^412
		// 2048: |Xp - Xq| >= 2^924

		RSAPrivateKeyStructure rsa = RSA_X931_derive((BigInteger) null,
				(BigInteger) null, (BigInteger) null, (BigInteger) null, xp1,
				xp2, xp, xq1, xq2, xq, (BigInteger) null);
		return rsa;
	}

	private static RSAPrivateKeyStructure RSA_X931_derive(BigInteger p1,
			BigInteger p2, BigInteger q1, BigInteger q2, BigInteger Xp1,
			BigInteger Xp2, BigInteger Xp, BigInteger Xq1, BigInteger Xq2,
			BigInteger Xq, BigInteger e) throws Exception {
		BigInteger r0 = null;
		BigInteger r1 = null;
		BigInteger r2 = null;
		BigInteger r3 = null;

		BigInteger publicExponent = e;

		BigInteger prime1 = BN_X931_derive_prime(p1, p2, Xp, Xp1, Xp2, e);

		BigInteger prime2 = BN_X931_derive_prime(q1, q2, Xq, Xq1, Xq2, e);

		if ((prime1 == null) || (prime2 == null)) {
			throw new Exception();
		}

		/* Since both primes are set we can now calculate all remaining
		 * components.
		 */

		/* calculate n */
		BigInteger modulus = prime1.multiply(prime2);

		/* calculate d */
		r1 = prime1.subtract(BigInteger.ONE); /* p-1 */
		r2 = prime2.subtract(BigInteger.ONE); /* q-1 */
		r0 = r1.multiply(r2);
		r3 = r1.gcd(r2);
		r0 = r0.divide(r3);
		BigInteger privateExponent = publicExponent.modInverse(r0);

		/* calculate d mod (p-1) */
		BigInteger exponent1 = privateExponent.mod(r1);

		/* calculate d mod (q-1) */
		BigInteger exponent2 = privateExponent.mod(r2);

		/* calculate inverse of q mod p */
		BigInteger coefficient = prime2.modInverse(prime1);

		return new RSAPrivateKeyStructure(modulus, publicExponent,
				privateExponent, prime1, prime2, exponent1, exponent2,
				coefficient);
	}

	boolean isOdd(BigInteger bi) {
		return false; // TODO
	}

	static BigInteger bn_x931_derive_pi(BigInteger Xpi) {
		BigInteger pi = Xpi;

		if (!pi.testBit(0)) {
			pi.add(BigInteger.ONE);
		}

		SecureRandom rnd = new SecureRandom();
		do {
			/* NB 27 MR is specificed in X9.31 */
			if (passesMillerRabin(pi, 27, rnd))
				break;
			pi.add(TWO);
		} while (true);
		return pi;
	}

	private static BigInteger BN_X931_derive_prime(BigInteger p1,
			BigInteger p2, BigInteger Xp, BigInteger Xp1, BigInteger Xp2,
			BigInteger e) {
		if (!e.testBit(0)) {
			throw new IllegalArgumentException("exponent must be odd.");
		}

		p1 = bn_x931_derive_pi(Xp1);

		p2 = bn_x931_derive_pi(Xp2);

		BigInteger p1p2 = p1.multiply(p2);

		/* First set p to value of Rp */

		BigInteger p = p2.modInverse(p1);

		p = p.multiply(p2);

		BigInteger t = p1.modInverse(p2);

		t = t.multiply(p1);

		p = p.subtract(t);

		if (p.signum() == -1) {
			p = p.add(p1p2);
		}

		/* p now equals Rp */

		p = p.subtract(Xp);
		p = p.mod(p1p2);

		p = p.add(Xp);

		/* p now equals Yp0 */

		SecureRandom rnd = new SecureRandom();
		do {
			BigInteger pm1 = p;

			pm1 = pm1.subtract(BigInteger.ONE);

			t = pm1.gcd(e);

			if (BigInteger.ONE.equals(t) && passesMillerRabin(p, 50, rnd))
				break;
			p = p.add(p1p2);
		} while (true);
		return p;
	}

	/**
	 * Returns true iff this BigInteger passes the specified number of
	 * Miller-Rabin tests. This test is taken from the DSA spec (NIST FIPS
	 * 186-2).
	 *
	 * The following assumptions are made:
	 * This BigInteger is a positive, odd number greater than 2.
	 * iterations<=50.
	 */
	private static boolean passesMillerRabin(BigInteger p, int iterations,
			Random rnd) {
		// Find a and m such that m is odd and this == 1 + 2**a * m
		BigInteger thisMinusOne = p.subtract(BigInteger.ONE);
		BigInteger m = thisMinusOne;
		int a = m.getLowestSetBit();
		m = m.shiftRight(a);

		for (int i = 0; i < iterations; i++) {
			// Generate a uniform random on (1, this)
			BigInteger b;
			do {
				b = new BigInteger(p.bitLength(), rnd);
			} while (b.compareTo(BigInteger.ONE) <= 0 || b.compareTo(p) >= 0);

			int j = 0;
			BigInteger z = b.modPow(m, p);
			while (!((j == 0 && z.equals(BigInteger.ONE)) || z
					.equals(thisMinusOne))) {
				if (j > 0 && z.equals(BigInteger.ONE) || ++j == a)
					return false;
				z = z.modPow(TWO, p);
			}
		}
		return true;
	}

}
