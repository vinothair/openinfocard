/*
 * XmldapCertsAndKeys.java
 *
 * Created on 6. September 2006, 11:08
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.xmldap.util;

import org.bouncycastle.asn1.DERObject;
import org.bouncycastle.asn1.DERObjectIdentifier;
import org.bouncycastle.asn1.x509.*;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.x509.X509V3CertificateGenerator;
import org.xmldap.asn1.*;
import org.xmldap.exceptions.TokenIssuanceException;

import java.io.*;
import java.math.BigInteger;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Calendar;
import java.util.Date;
import java.util.Vector;

/**
 * 
 * @author Axel Nennker
 */
public class CertsAndKeys {

	/** Creates a new instance of XmldapCertsAndKeys */
	private CertsAndKeys() {
	}

	public static KeyPair generateKeyPair() throws NoSuchAlgorithmException,
			NoSuchProviderException {
		KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
		keyGen.initialize(1024, new SecureRandom());
		return keyGen.generateKeyPair();
	}

	public static X509Certificate infocard2Certificate(KeyPair kp)
			throws TokenIssuanceException {
		X509Certificate cert = null;
		return cert;
	}

	public static X509Certificate generateCaCertificate(KeyPair kp)
			throws TokenIssuanceException {
		String issuerStr = "CN=firefox, OU=infocard selector, O=xmldap, L=San Francisco, ST=California, C=US";
		X509Name issuer = new X509Name(issuerStr);
		return generateCaCertificate(kp, issuer, issuer);
	}

	static public X509V3CertificateGenerator addClientExtensions(
			X509V3CertificateGenerator gen)
			throws UnsupportedEncodingException {
		gen.addExtension(X509Extensions.BasicConstraints, true,
				new BasicConstraints(false));
		gen.addExtension(X509Extensions.KeyUsage, true, new KeyUsage(
				KeyUsage.digitalSignature | KeyUsage.keyEncipherment
						| KeyUsage.dataEncipherment | KeyUsage.keyCertSign));
		gen.addExtension(X509Extensions.ExtendedKeyUsage, true,
				new ExtendedKeyUsage(KeyPurposeId.id_kp_clientAuth));

		return gen;
	}
	
	static public X509V3CertificateGenerator addLogotype(
			X509V3CertificateGenerator gen) {
		String mediaType = "image/jpg";
		AlgorithmIdentifier algId = new AlgorithmIdentifier("1.3.14.3.2.26");
		byte[] digest = { (byte) 0x96, (byte) 0xda, (byte) 0x5a, (byte) 0xf6,
				(byte) 0x0f, (byte) 0x50, (byte) 0xf1, (byte) 0x84,
				(byte) 0x84, (byte) 0x3a, (byte) 0x3f, (byte) 0x2c,
				(byte) 0x2d, (byte) 0x9a, (byte) 0x5b, (byte) 0xf3,
				(byte) 0x8e, (byte) 0xa1, (byte) 0xd0, (byte) 0xd4 };
		DigestInfo digestInfo = new DigestInfo(algId, digest);
		DigestInfo[] logotypeHash = { digestInfo };
		String[] logotypeURI = { "http://static.flickr.com/10/buddyicons/18119196@N00.jpg?1115549486" };
		LogotypeDetails imageDetails = new LogotypeDetails(mediaType,
				logotypeHash, logotypeURI);
		// LogotypeImageInfo imageInfo = null;
		// LogotypeImage image = new LogotypeImage(imageDetails, imageInfo);
		// LogotypeImage[] images = { image };
		LogotypeDetails[] images = { imageDetails };
		LogotypeAudio[] audio = null;
		LogotypeData direct = new LogotypeData(images, audio);
		LogotypeInfo[] communityLogos = null;
		LogotypeInfo issuerLogo = new LogotypeInfo(direct);
		LogotypeInfo subjectLogo = null;
		OtherLogotypeInfo[] otherLogos = null;
		Logotype logotype = new Logotype(communityLogos, issuerLogo,
				subjectLogo, otherLogos);
		DERObject obj = logotype.toASN1Object();
		byte[] logotypeBytes = obj.getDEREncoded();
		gen.addExtension(Logotype.id_pe_logotype, false, logotypeBytes);
		return gen;
	}	

	static public X509V3CertificateGenerator addCaExtensions(
			X509V3CertificateGenerator gen) {
		gen.addExtension(X509Extensions.BasicConstraints, true,
				new BasicConstraints(true));
		gen.addExtension(X509Extensions.KeyUsage, true, new KeyUsage(
				KeyUsage.digitalSignature | KeyUsage.keyEncipherment
						| KeyUsage.dataEncipherment | KeyUsage.keyCertSign));
		gen.addExtension(X509Extensions.ExtendedKeyUsage, true,
				new ExtendedKeyUsage(KeyPurposeId.id_kp_clientAuth));
		// gen.addExtension(X509Extensions.SubjectAlternativeName, false,
		// new GeneralNames(new GeneralName(GeneralName.rfc822Name,
		// "test@test.test")));
		return gen;
	}

	static public X509V3CertificateGenerator addSSLServerExtensions(
			X509V3CertificateGenerator gen) {
		gen.addExtension(X509Extensions.BasicConstraints, true,
				new BasicConstraints(false));
		gen.addExtension(X509Extensions.KeyUsage, true, new KeyUsage(
				KeyUsage.keyEncipherment|KeyUsage.digitalSignature));
		Vector extendedKeyUsageV = new Vector();
		extendedKeyUsageV.add(KeyPurposeId.id_kp_serverAuth);
		extendedKeyUsageV.add(KeyPurposeId.id_kp_clientAuth);
		// Netscape Server Gated Crypto
		extendedKeyUsageV.add(new DERObjectIdentifier("2.16.840.1.113730.4.1"));
		// Microsoft Server Gated Crypto
		extendedKeyUsageV.add(new DERObjectIdentifier("1.3.6.1.4.1.311.10.3.3"));
		gen.addExtension(X509Extensions.ExtendedKeyUsage, true,
				new ExtendedKeyUsage(extendedKeyUsageV));
		// gen.addExtension(X509Extensions.SubjectAlternativeName, false,
		// new GeneralNames(new GeneralName(GeneralName.rfc822Name,
		// "test@test.test")));
		return gen;
	}

	/**
	 * generates an X509 certificate which is used to sign the xmlTokens in the
	 * firefox infocard selector
	 * 
	 * @param kp
	 * @param issuer
	 * @param subject
	 * @return
	 * @throws TokenIssuanceException
	 * @throws UnsupportedEncodingException
	 */
	public static X509Certificate generateClientCertificate(KeyPair kp,
			X509Name issuer, X509Name subject, String gender,
			Date dateOfBirth, String streetAddress, String telephoneNumber)
			throws TokenIssuanceException, UnsupportedEncodingException {
		if (Security.getProvider("BC") == null) {
			Security.addProvider(new BouncyCastleProvider());
		}

		X509Certificate cert = null;

		X509V3CertificateGenerator gen = new X509V3CertificateGenerator();
		gen.setIssuerDN(issuer);
		Calendar rightNow = Calendar.getInstance();
		rightNow.add(Calendar.MINUTE, -2); // 2 minutes
		gen.setNotBefore(rightNow.getTime());
		rightNow.add(Calendar.YEAR, 5);
		gen.setNotAfter(rightNow.getTime());
		gen.setSubjectDN(subject);
		gen.setPublicKey(kp.getPublic());
		gen.setSignatureAlgorithm("MD5WithRSAEncryption");
		gen.setSerialNumber(BigInteger.valueOf(System.currentTimeMillis()));
		gen = addClientExtensions(gen);
		SubjectDirectoryAttributes sda = new SubjectDirectoryAttributes(
				gender, dateOfBirth, streetAddress, telephoneNumber);
		if (sda.size() > 0) {
			gen.addExtension(X509Extensions.SubjectDirectoryAttributes, false,
				sda);
		}


		try {
			cert = gen.generateX509Certificate(kp.getPrivate());
		} catch (InvalidKeyException e) {
			throw new TokenIssuanceException(e);
		} catch (SecurityException e) {
			throw new TokenIssuanceException(e);
		} catch (SignatureException e) {
			throw new TokenIssuanceException(e);
		}
		return cert;
	}

	/**
	 * generates an X509 certificate which is used to sign the xmlTokens in the
	 * firefox infocard selector
	 * 
	 * @param kp
	 * @param issuer
	 * @param subject
	 * @return
	 * @throws TokenIssuanceException
	 */
	public static X509Certificate generateCaCertificate(KeyPair kp,
			X509Name issuer, X509Name subject) throws TokenIssuanceException {
		if (Security.getProvider("BC") == null) {
			Security.addProvider(new BouncyCastleProvider());
		}

		X509Certificate cert = null;

		X509V3CertificateGenerator gen = new X509V3CertificateGenerator();
		gen.setIssuerDN(issuer);
		Calendar rightNow = Calendar.getInstance();
		rightNow.add(Calendar.MINUTE, -2); // 2 minutes
		gen.setNotBefore(rightNow.getTime());
		rightNow.add(Calendar.YEAR, 5);
		gen.setNotAfter(rightNow.getTime());
		gen.setSubjectDN(subject);
		gen.setPublicKey(kp.getPublic());
		gen.setSignatureAlgorithm("MD5WithRSAEncryption");
		gen.setSerialNumber(BigInteger.valueOf(System.currentTimeMillis()));
		gen = addCaExtensions(gen);
		gen = addLogotype(gen);
		try {
			cert = gen.generateX509Certificate(kp.getPrivate());
		} catch (InvalidKeyException e) {
			throw new TokenIssuanceException(e);
		} catch (SecurityException e) {
			throw new TokenIssuanceException(e);
		} catch (SignatureException e) {
			throw new TokenIssuanceException(e);
		}
		return cert;
	}

	/**
	 * generates an X509 certificate 
	 * 
	 * @param kp
	 * @param issuer
	 * @param subject
	 * @return
	 * @throws TokenIssuanceException
	 */
	public static X509Certificate generateSSLServerCertificate(KeyPair kp,
			X509Name issuer, X509Name subject) throws TokenIssuanceException {
		if (Security.getProvider("BC") == null) {
			Security.addProvider(new BouncyCastleProvider());
		}

		X509Certificate cert = null;

		X509V3CertificateGenerator gen = new X509V3CertificateGenerator();
		gen.setIssuerDN(issuer);
		Calendar rightNow = Calendar.getInstance();
		rightNow.add(Calendar.MINUTE, -2); // 2 minutes
		gen.setNotBefore(rightNow.getTime());
		rightNow.add(Calendar.YEAR, 5);
		gen.setNotAfter(rightNow.getTime());
		gen.setSubjectDN(subject);
		gen.setPublicKey(kp.getPublic());
		gen.setSignatureAlgorithm("MD5WithRSAEncryption");
		gen.setSerialNumber(BigInteger.valueOf(System.currentTimeMillis()));
		gen = addSSLServerExtensions(gen);
		gen = addLogotype(gen);
		try {
			cert = gen.generateX509Certificate(kp.getPrivate());
		} catch (InvalidKeyException e) {
			throw new TokenIssuanceException(e);
		} catch (SecurityException e) {
			throw new TokenIssuanceException(e);
		} catch (SignatureException e) {
			throw new TokenIssuanceException(e);
		}
		return cert;
	}

	public static KeyPair bytesToKeyPair(byte[] bytes)
			throws TokenIssuanceException {
		ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
		try {
			ObjectInputStream ois = new ObjectInputStream(bis);
			return (KeyPair) ois.readObject();
		} catch (IOException e) {
			throw new TokenIssuanceException(e);
		} catch (ClassNotFoundException e) {
			throw new TokenIssuanceException(e);
		}

	}

	public static byte[] keyPairToBytes(KeyPair kp)
			throws TokenIssuanceException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		try {
			ObjectOutputStream oos = new ObjectOutputStream(bos);
			oos.writeObject(kp);
			oos.close();
			return bos.toByteArray();
		} catch (IOException e) {
			throw new TokenIssuanceException(e);
		}
	}

	public static X509Certificate der2cert(String der) throws TokenIssuanceException {
		byte[] certBytes = Base64.decode(der);
		ByteArrayInputStream is = new ByteArrayInputStream(certBytes);
		BufferedInputStream bis = new BufferedInputStream(is);
		CertificateFactory cf = null;
		X509Certificate cert = null;
		try {
			cf = CertificateFactory.getInstance("X.509");
			cert = (X509Certificate) cf.generateCertificate(bis);
		} catch (CertificateException e) {
			throw new TokenIssuanceException(e);
		}
		return cert;
	}

}
