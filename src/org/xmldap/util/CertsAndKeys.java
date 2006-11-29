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
			X509V3CertificateGenerator gen) throws UnsupportedEncodingException {
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
				KeyUsage.keyEncipherment | KeyUsage.digitalSignature));
		Vector extendedKeyUsageV = new Vector();
		extendedKeyUsageV.add(KeyPurposeId.id_kp_serverAuth);
		extendedKeyUsageV.add(KeyPurposeId.id_kp_clientAuth);
		// Netscape Server Gated Crypto
		extendedKeyUsageV.add(new DERObjectIdentifier("2.16.840.1.113730.4.1"));
		// Microsoft Server Gated Crypto
		extendedKeyUsageV
				.add(new DERObjectIdentifier("1.3.6.1.4.1.311.10.3.3"));
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
			X509Name issuer, X509Name subject, String gender, Date dateOfBirth,
			String streetAddress, String telephoneNumber)
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
		SubjectDirectoryAttributes sda = new SubjectDirectoryAttributes(gender,
				dateOfBirth, streetAddress, telephoneNumber);
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
			throws IOException, ClassNotFoundException {
		ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
			ObjectInputStream ois = new ObjectInputStream(bis);
			return (KeyPair) ois.readObject();
	}

	public static byte[] keyPairToBytes(KeyPair kp)
			throws IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(bos);
		oos.writeObject(kp);
		oos.close();
		return bos.toByteArray();
	}

	public static X509Certificate der2cert(String der)
			throws CertificateException {
		byte[] certBytes = Base64.decode(der);
		ByteArrayInputStream is = new ByteArrayInputStream(certBytes);
		BufferedInputStream bis = new BufferedInputStream(is);
		CertificateFactory cf = null;
		X509Certificate cert = null;
		cf = CertificateFactory.getInstance("X.509");
		cert = (X509Certificate) cf.generateCertificate(bis);
		return cert;
	}

	// private void storeInfoCardAsCertificate(String nickname, Document
	// infocard)
	// throws TokenIssuanceException { // temporary hack to store infocards
	// // as certificates
	// try {
	// X509Certificate cardAsCert = infocard2Certificate(infocard);
	// // store in firefox.jks
	// storeCardCertKeystore(nickname, cardAsCert, false);
	// // store in <ppi>.pem
	// storeCardCertPem(nickname, cardAsCert);
	// // store in <ppi>.p12
	// // storeCardCertP12(token.getPrivatePersonalIdentifier(),
	// // cardAsCert);
	// } catch (UnsupportedEncodingException e) {
	// e.printStackTrace();
	// } catch (ParseException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// } catch (CertificateEncodingException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// } catch (IOException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// }

	// private X509Name claims2X509Name(Element data)
	// throws TokenIssuanceException {
	//
	// Vector oids = new Vector();
	// Vector values = new Vector();
	//
	// String value = getDataValue(data, "givenname");
	// if ((value != null) && !value.equals("")) {
	// oids.add(X509Name.GIVENNAME);
	// values.add(value);
	// }
	// value = getDataValue(data, "surname");
	// if ((value != null) && !value.equals("")) {
	// oids.add(X509Name.SURNAME);
	// values.add(value);
	// }
	// value = getDataValue(data, "emailaddress");
	// if ((value != null) && !value.equals("")) {
	// oids.add(X509Name.E);
	// values.add(value);
	// }
	// // value = getDataValue(data, "streetladdress");
	// // if ((value != null) && !value.equals("")) {
	// // sb.append(" streetladdress=");
	// // sb.append(value);
	// // }
	// value = getDataValue(data, "locality");
	// if ((value != null) && !value.equals("")) {
	// oids.add(X509Name.L);
	// values.add(value);
	// }
	// value = getDataValue(data, "stateorprovince");
	// if ((value != null) && !value.equals("")) {
	// oids.add(X509Name.ST);
	// values.add(value);
	// }
	// // value = getDataValue(data, "postalcode");
	// // if ((value != null) && !value.equals("")) {
	// // sb.append("postalcode=");
	// // sb.append(value);
	// // }
	// value = getDataValue(data, "country");
	// if ((value != null) && !value.equals("")) {
	// oids.add(X509Name.C);
	// values.add(value);
	// }
	// // value = getDataValue(data, "primaryphone");
	// // if ((value != null) && !value.equals("")) {
	// // sb.append(" primaryphone=");
	// // sb.append(value);
	// // }
	// // value = getDataValue(data, "otherphone");
	// // if ((value != null) && !value.equals("")) {
	// // sb.append(" otherphone=");
	// // sb.append(value);
	// // }
	// // value = getDataValue(data, "mobilephone");
	// // if ((value != null) && !value.equals("")) {
	// // sb.append(" mobilephone=");
	// // sb.append(value);
	// // }
	// // value = getDataValue(data, "dateofbirth");
	// // if ((value != null) && !value.equals("")) {
	// // sb.append(" dateofbirth=");
	// // sb.append(value);
	// // }
	// // value = getDataValue(data, "gender");
	// // if ((value != null) && !value.equals("")) {
	// // sb.append(" gender=");
	// // sb.append(value);
	// // }
	//
	// return new X509Name(oids, values);
	// }

	// public X509Certificate infocard2Certificate(Document infocard, KeyPair
	// kp)
	// throws UnsupportedEncodingException, ParseException {
	// X509Certificate cert = null;
	// // KeyPair kp = new KeyPair(signingCert.getPublicKey(), signingKey);
	// X509Name issuer = new X509Name(
	// "CN=firefox, OU=infocard selector, O=xmldap, L=San Francisco,
	// ST=California, C=US");
	// Nodes dataNodes = infocard.query("/infocard/carddata/selfasserted");
	// Element data = (Element) dataNodes.get(0);
	// X509Name subject = claims2X509Name(data);
	//
	// DateFormat df = DateFormat.getDateInstance();
	// Date dateOfBirth = df.parse(getDataValue(data, "dateofbirth"));
	// cert = CertsAndKeys.generateClientCertificate(kp, issuer, subject,
	// getDataValue(data, "gender"), dateOfBirth, getDataValue(data,
	//						"streetladdress"), getDataValue(data, "primaryphone"));
	//		return cert;
	//	}

}
