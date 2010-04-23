package org.xmldap.crypto;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;

import net.sourceforge.lightcrypto.SafeObject;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.xmldap.exceptions.CryptoException;
import org.xmldap.util.Base64;
import org.xmldap.util.XmldapCertsAndKeys;

import junit.framework.TestCase;

public class CryptoUtilsTest extends TestCase {

	protected void setUp() throws Exception {
		super.setUp();
	}

	public void testByteDigest() {
		byte[] data = "test".getBytes();
		try {
			byte[] digest = CryptoUtils.byteDigest(data, "SHA");
			String digestB64 = Base64.encodeBytesNoBreaks(digest);
			assertEquals("qUqP5cyxm6YcTAhz05Hph5gvu9M=", digestB64);
		} catch (CryptoException e) {
			assertEquals("", e.getMessage());
		}
		
		data = new byte[1024];
		for (int i=0; i<data.length; i++) {
			data[i] = (byte)(i & 0xFF);
		}
		try {
			byte[] digest = CryptoUtils.byteDigest(data, "SHA");
			String digestB64 = Base64.encodeBytesNoBreaks(digest);
			assertEquals("WwBmnEgNXP+9+ovbqZVhFg8tG3c=", digestB64);
		} catch (CryptoException e) {
			assertEquals("", e.getMessage());
		}
	}

	public void testDigest() {
		byte[] data = "test".getBytes();
		try {
			String b64 = CryptoUtils.digest(data, "SHA");
			assertEquals("qUqP5cyxm6YcTAhz05Hph5gvu9M=", b64);
		} catch (CryptoException e) {
			assertEquals("", e.getMessage());
		}
		data = new byte[1024];
		for (int i=0; i<data.length; i++) {
			data[i] = (byte)(i & 0xFF);
		}
		try {
			String b64 = CryptoUtils.digest(data, "SHA");
			assertEquals("WwBmnEgNXP+9+ovbqZVhFg8tG3c=", b64);
		} catch (CryptoException e) {
			assertEquals("", e.getMessage());
		}
	}

	public void testencryptAESCBC() {
		byte[] secretKey = new byte[] {0xffffffa4, 0xffffffa2, 0x15, 0x38, 0xffffffd7, 
				0xffffffec, 0xffffffd4, 0xffffff84, 0xb, 0x0, 0x4c, 0x38, 0xffffffdc, 
				0xffffffea, 0x2, 0xffffffdc, 0x3b, 0x39, 0xffffffc0, 0xffffff8c, 0x73, 
				0x11, 0xffffffe8, 0xffffffe2, 0xfffffff1, 0x71, 0xffffffca, 0x2, 0x3a, 
				0xffffff87, 0x1f, 0xffffffd2};
//		byte[] secretKey = CryptoUtils.genKey(256);
//		StringBuffer egal = new StringBuffer("byte[] secretKey = new byte[] {");
//		for (int i=0; i<secretKey.length-1; i++) {
//			egal.append( "0x");
//			egal.append(Integer.toHexString(secretKey[i]));
//			egal.append( ", ");
//		}
//		egal.append( "0x");
//		egal.append(Integer.toHexString(secretKey[secretKey.length-1]));
//		egal.append( "};");
//		assertEquals("", egal.toString());
		
		StringBuffer text = new StringBuffer("test");
        SafeObject keyBytes = new SafeObject();
        try {
            keyBytes.setText(secretKey);
        } catch (Exception e) {
        	assertEquals("", e.getMessage());
        }

		try {
			StringBuffer sb = CryptoUtils.encryptAESCBC(text, keyBytes);
//			assertEquals("GCKZW/YkurwSmGui4IdUr8m7GJBcc9UHm+nvjh50TFA=", new String(sb));
    		StringBuffer sb2 = CryptoUtils.decryptAESCBC(sb, keyBytes);
    		assertEquals(new String(text), new String(sb2));
		} catch (CryptoException e) {
			assertEquals("", e.getMessage());
		}
	}

	public void testEncryptAESCBCgenKey() {
        try {
        	byte[] secretKey = CryptoUtils.genKey(256);
        	StringBuffer text = new StringBuffer("test");
        	SafeObject keyBytes = new SafeObject();
            keyBytes.setText(secretKey);
    		StringBuffer sb = CryptoUtils.encryptAESCBC(text, keyBytes);
    		StringBuffer sb2 = CryptoUtils.decryptAESCBC(sb, keyBytes);
    		assertEquals(new String(text), new String(sb2));
        } catch (Exception e) {
        	assertEquals("", e.getMessage());
        }
	}

	public void testRsaoaep() {
		String text = "test";
		 
		try {
			KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
			keyGen.initialize(1024, new SecureRandom());
			KeyPair kp = keyGen.generateKeyPair();
			RSAPublicKey rsaPublicKey = (RSAPublicKey)kp.getPublic();
			String b64EncodedData = CryptoUtils.rsaoaepEncrypt(text.getBytes(), rsaPublicKey);
			
			byte[] decrypted = CryptoUtils.decryptRSAOAEP(b64EncodedData, kp.getPrivate());
			assertEquals(text, new String(decrypted));
		} catch (NoSuchAlgorithmException e) {
			assertEquals("", e.getMessage());
		} catch (CryptoException e) {
			assertEquals("", e.getMessage());
		}
	}

	public void testSignVerify() {
		String text = "test";
		try {
			KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
			keyGen.initialize(1024, new SecureRandom());
			KeyPair kp = keyGen.generateKeyPair();
	        String signingAlgorithm = "SHA1withRSA";
			String b64signature = CryptoUtils.sign(text.getBytes(), kp.getPrivate(), signingAlgorithm);
			RSAPublicKey publicKey = (RSAPublicKey) kp.getPublic();
			byte[] signature = Base64.decode(b64signature);
			
			boolean verified = CryptoUtils.verify(text.getBytes(), signature, 
					publicKey.getModulus(), publicKey.getPublicExponent());
			assertTrue(verified);
		} catch (NoSuchAlgorithmException e) {
			assertEquals("", e.getMessage());
		} catch (CryptoException e) {
			assertEquals("", e.getMessage());
		}
	}

	public void testSignVerifyLength() {
		String text = "test";
		try {
			KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
			keyGen.initialize(1024, new SecureRandom());
			KeyPair kp = keyGen.generateKeyPair();
			RSAPrivateKey privateKey = (RSAPrivateKey)kp.getPrivate();
			assertEquals(1024, privateKey.getModulus().bitLength());
	        String signingAlgorithm = "SHA1withRSA";
			String b64signature = CryptoUtils.sign(text.getBytes(), privateKey, signingAlgorithm);
			assertEquals(172, b64signature.length());
			byte[] signature = Base64.decode(b64signature);
			assertEquals(1024 / 8, signature.length);

			RSAPublicKey publicKey = (RSAPublicKey) kp.getPublic();
			// the following must not always be true.
			// but must be true for CardSpace
			assertEquals(65537, publicKey.getPublicExponent().longValue());
			boolean verified = CryptoUtils.verify(text.getBytes(), signature, 
					publicKey.getModulus(), publicKey.getPublicExponent());
			assertTrue(verified);
		} catch (NoSuchAlgorithmException e) {
			assertEquals("", e.getMessage());
		} catch (CryptoException e) {
			assertEquals("", e.getMessage());
		}
	}

	public void testSignVerifyStatic() {
		String text = "test";
		try {
			PrivateKey privateKey = XmldapCertsAndKeys.getXmldapPrivateKey();
			String signingAlgorithm = "SHA1withRSA";
			String b64signature = CryptoUtils.sign(text.getBytes(), privateKey, signingAlgorithm);
			assertEquals("rxEfBXFsfPsb+cxYZZcn359NLeSPkN6BA+JKXe+VwRJVA96pV1gvshSe6e" +
					"/2uox14lO5UR6EugDGiDIH9Kx7MeINKOw6+bzMirPh3FGUnWVXDv/sxafd4/gYP" +
					"hcB+UlN184nUqQHUtc8ZV3l+p5yaMuLeVDYTsqgrHvdYroZH8nKAIWcmFC6QT21" +
					"ua+ibqzUn53JA0HTG9mrFJtwT9O8Vmgc0dOnUZ/Nn07JgwuQXi7sPKZdb1G2j7x" +
					"M1vGQUo6RXwuvJOqk9eoyC4Kxi75E/YGHAGqQssHvJ5UIcBPMI8PjokG3i2536n" +
					"LWQjkQcj4F0Mw8p1kZg0gP0IcTQ8sUIg==", b64signature);
			byte[] signature = Base64.decode(b64signature);
			X509Certificate cert = XmldapCertsAndKeys.getXmldapCert();
			RSAPublicKey publicKey = (RSAPublicKey) cert.getPublicKey();
			
			boolean verified = CryptoUtils.verify(text.getBytes(), signature, 
					publicKey.getModulus(), publicKey.getPublicExponent());
			assertTrue(verified);
		} catch (NoSuchAlgorithmException e) {
			assertEquals("", e.getMessage());
		} catch (CryptoException e) {
			assertEquals("", e.getMessage());
		} catch (InvalidKeySpecException e) {
			assertEquals("", e.getMessage());
		} catch (CertificateException e) {
			assertEquals("", e.getMessage());
		}
	}

    public void testX509fromB64() throws CertificateException, CryptoException {
    	X509Certificate certIn = XmldapCertsAndKeys.getXmldapCert();
    	String b64EncodedX509Certificate = Base64.encodeBytesNoBreaks(certIn.getEncoded());
    	X509Certificate certOut = CryptoUtils.X509fromB64(b64EncodedX509Certificate);
    	assertTrue(certIn.equals(certOut));
    }

}
