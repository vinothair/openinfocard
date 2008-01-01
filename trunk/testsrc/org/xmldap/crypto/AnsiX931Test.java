package org.xmldap.crypto;

import java.math.BigInteger;
import java.security.cert.X509Certificate;

import org.bouncycastle.asn1.pkcs.RSAPrivateKeyStructure;
import org.xmldap.crypto.AnsiX931;
import org.xmldap.firefox.TokenIssuer;
import org.xmldap.util.XmldapCertsAndKeys;

import junit.framework.TestCase;

public class AnsiX931Test extends TestCase {
	byte[] masterkey = new byte[256];
	byte[] rpIdentifier = new byte[256];
	int requiredKeySize = 1024;
	X509Certificate relyingpartyCert; 
	X509Certificate[] chain = new X509Certificate[1];
	
	protected void setUp() throws Exception {
		super.setUp();
		
		for (int i=0; i<masterkey.length; i++) {
			masterkey[i] = (byte)i;
		}
		relyingpartyCert = XmldapCertsAndKeys.getXmldapCert();
		rpIdentifier = TokenIssuer.rpIdentifier(relyingpartyCert, chain);
		chain[0] = relyingpartyCert;
		
	}

	
	public void testGenerateRSAKeyPairFromMasterKey() throws Exception {
		RSAPrivateKeyStructure rsa = AnsiX931.generateRSAKeyPairFromMasterKey(
				masterkey, rpIdentifier, requiredKeySize);
		BigInteger coefficient = rsa.getCoefficient();
		assertEquals(null, coefficient);
		BigInteger exponent1 = rsa.getExponent1();
		assertEquals(null, exponent1);
		BigInteger exponent2 = rsa.getExponent2();
		assertEquals(null, exponent2);
		BigInteger modulus = rsa.getModulus();
		assertEquals(1024, modulus.bitLength());
		assertEquals(null, modulus);
		BigInteger prime1 = rsa.getPrime1();
		assertEquals(null, prime1);
		BigInteger prime2 = rsa.getPrime2();
		assertEquals(null, prime2);
		BigInteger privateExponent = rsa.getPrivateExponent();
		assertEquals(null, privateExponent);
		byte[] encoded = rsa.getEncoded();
		assertEquals(null, encoded);
	}

	public void testBn_x931_derive_pi() {
		fail("Not yet implemented");
	}

}
