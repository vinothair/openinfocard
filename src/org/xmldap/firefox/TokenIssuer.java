/*
 * Copyright (c) 2006, Chuck Mortimore - xmldap.org
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the names xmldap, xmldap.org, xmldap.com nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE REGENTS AND CONTRIBUTORS ``AS IS'' AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE REGENTS AND CONTRIBUTORS BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.xmldap.firefox;

import nu.xom.*;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.x509.X509Name;
import org.bouncycastle.x509.extension.X509ExtensionUtil;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;
import org.xmldap.asn1.Logotype;
import org.xmldap.asn1.LogotypeData;
import org.xmldap.asn1.LogotypeDetails;
import org.xmldap.asn1.LogotypeInfo;
import org.xmldap.exceptions.KeyStoreException;
import org.xmldap.exceptions.SerializationException;
import org.xmldap.exceptions.TokenIssuanceException;
import org.xmldap.infocard.SelfIssuedToken;
import org.xmldap.util.Base64;
import org.xmldap.util.CertsAndKeys;
import org.xmldap.util.Crds;
import org.xmldap.util.KeystoreUtil;
import org.xmldap.xmlenc.EncryptedData;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.net.URLDecoder;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.Vector;

public class TokenIssuer {

	static final String storePassword = "storepassword";

	static final String keyPassword = "keypassword";

	static final String nickname = "firefox";

	String keystorePath = null;

	X509Certificate signingCert = null;

	PrivateKey signingKey = null;

	private String initExtensionPath(String path) throws TokenIssuanceException {
		try {
			path = URLDecoder.decode(path.substring(7, path.length()), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new TokenIssuanceException(e);
		}
		return path + "components/lib/firefox.jks";
	}

	private String initProfilePath(String path) throws TokenIssuanceException {
		return path + "firefox.jks";
	}

	private void storeCardCertKeystore(String cardCertNickname,
			X509Certificate cardCert, boolean overwrite)
			throws TokenIssuanceException {
		try {
			KeyStore ks = KeyStore.getInstance("JKS");
			ks.load(new FileInputStream(keystorePath), storePassword
					.toCharArray());
			if (ks.containsAlias(cardCertNickname)) {
				if (!overwrite) {
					return;
				}
				ks.deleteEntry(cardCertNickname);
			}
			Certificate[] chain = { cardCert };
			ks.setKeyEntry(cardCertNickname, signingKey, keyPassword
					.toCharArray(), chain);
			FileOutputStream fos = new java.io.FileOutputStream(keystorePath);
			ks.store(fos, storePassword.toCharArray());
			fos.close();

		} catch (java.security.KeyStoreException e) {
			throw new TokenIssuanceException(e);
		} catch (NoSuchAlgorithmException e) {
			throw new TokenIssuanceException(e);
		} catch (CertificateException e) {
			throw new TokenIssuanceException(e);
		} catch (IOException e) {
			throw new TokenIssuanceException(e);
		}
	}

	// private void storeCardCertP12(String cardCertNickname,
	// X509Certificate cardCert) throws TokenIssuanceException {
	// try {
	// String dirName = dirname(keystorePath);
	//
	// KeyStore ks = KeyStore.getInstance("P12");
	// ks.load(null, storePassword.toCharArray());
	// Certificate[] chain = { cardCert };
	// ks.setKeyEntry(cardCertNickname, signingKey, keyPassword
	// .toCharArray(), chain);
	// FileOutputStream fos = new java.io.FileOutputStream(dirName
	// + cardCertNickname + ".p12");
	// ks.store(fos, storePassword.toCharArray());
	// fos.close();
	//
	// } catch (java.security.KeyStoreException e) {
	// throw new TokenIssuanceException(e);
	// } catch (NoSuchAlgorithmException e) {
	// throw new TokenIssuanceException(e);
	// } catch (CertificateException e) {
	// throw new TokenIssuanceException(e);
	// } catch (IOException e) {
	// throw new TokenIssuanceException(e);
	// }
	// }
	//
	private String dirname(String path) {
		int i = path.lastIndexOf("firefox.jks");
		return path.substring(0, i);
	}

	private void storeCardCertPem(String cardCertNickname,
			X509Certificate cardCert) throws TokenIssuanceException,
			CertificateEncodingException, IOException {

		String dirName = dirname(keystorePath);
		FileOutputStream fos = new FileOutputStream(dirName + cardCertNickname
				+ ".pem");
		String certb64 = Base64.encodeBytes(cardCert.getEncoded());
		fos.write("-----BEGIN CERTIFICATE-----\n".getBytes());
		fos.write(certb64.getBytes());
		fos.write("\n-----END CERTIFICATE-----\n".getBytes());

		String keyb64 = Base64.encodeBytes(signingKey.getEncoded());

		fos.write("-----BEGIN PRIVATE KEY-----\n".getBytes());
		fos.write(keyb64.getBytes());
		fos.write("\n-----END PRIVATE KEY-----\n".getBytes());
		fos.close();
	}

	/**
	 * Store the signingCert and the signingKey into firefox.jks This is called
	 * only once Create firefox.jks
	 * 
	 * @param keystorePath
	 * @throws TokenIssuanceException
	 */
	private void storeCertKey(String keystorePath)
			throws TokenIssuanceException {
		try {
			KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
			ks.load(null, storePassword.toCharArray());
			Certificate[] chain = { signingCert };
			ks.setKeyEntry(nickname, signingKey, keyPassword.toCharArray(),
					chain);
			// store away the keystore
			FileOutputStream fos = new java.io.FileOutputStream(keystorePath);
			ks.store(fos, storePassword.toCharArray());
			fos.close();
		} catch (java.security.KeyStoreException e) {
			throw new TokenIssuanceException(e);
		} catch (NoSuchAlgorithmException e) {
			throw new TokenIssuanceException(e);
		} catch (CertificateException e) {
			throw new TokenIssuanceException(e);
		} catch (IOException e) {
			throw new TokenIssuanceException(e);
		}
	}

	private void initCertKey(String keystorePath) throws TokenIssuanceException {
		// Get my keystore
		KeystoreUtil keystore = null;

		try {
			keystore = new KeystoreUtil(keystorePath, storePassword);
			signingCert = keystore.getCertificate(nickname);
			signingKey = keystore.getPrivateKey(nickname, keyPassword);

		} catch (KeyStoreException e) {
			throw new TokenIssuanceException(e);
		}
	}

	public TokenIssuer(String path) throws TokenIssuanceException {
		keystorePath = initExtensionPath(path);
		try {
			initCertKey(keystorePath);
		} catch (TokenIssuanceException e) {
			// first try to init the cert and key failed
			// now try to generate them
			KeyPair kp = null;
			try {
				kp = CertsAndKeys.generateKeyPair();
				signingCert = CertsAndKeys.generateCaCertificate(kp);
				signingKey = kp.getPrivate();
				storeCertKey(keystorePath);
			} catch (NoSuchAlgorithmException e1) {
				throw new TokenIssuanceException(e);
			} catch (NoSuchProviderException e1) {
				throw new TokenIssuanceException(e);
			}
		}
	}

	public String init(String path) {
		return "TokenIssuer initialized";
	}

	private Document getInfocard(String card) throws TokenIssuanceException {
		Builder parser = new Builder();
		Document infocard = null;
		try {
			infocard = parser.build(card, "");
		} catch (ParsingException e) {
			throw new TokenIssuanceException(e);
		} catch (IOException e) {
			throw new TokenIssuanceException(e);
		}
		return infocard;
	}

	public String getDataValue(Element data, String claim)
			throws TokenIssuanceException {
		Element nameElm = data.getFirstChildElement(claim);
		if (nameElm != null)
			return nameElm.getValue();
		return "";
	}

	private SelfIssuedToken setTokenClaims(Element data, SelfIssuedToken token,
			String claims) throws TokenIssuanceException {
		// the argument to indexOf is a kind of shorthand...
		// should we use the complete string?
		if (claims.indexOf("givenname") != -1) {
			String value = getDataValue(data, "givenname");
			if ((value != null) && !value.equals("")) {
				token.setGivenName(value);
			}
		}
		if (claims.indexOf("surname") != -1) {
			String value = getDataValue(data, "surname");
			if ((value != null) && !value.equals("")) {
				token.setSurname(value);
			}
		}
		if (claims.indexOf("emailaddress") != -1) {
			String value = getDataValue(data, "emailaddress");
			if ((value != null) && !value.equals("")) {
				token.setEmailAddress(value);
			}
		}
		if (claims.indexOf("streetladdress") != -1) {
			String value = getDataValue(data, "streetladdress");
			if ((value != null) && !value.equals("")) {
				token.setStreetAddress(value);
			}
		}
		if (claims.indexOf("locality") != -1) {
			String value = getDataValue(data, "locality");
			if ((value != null) && !value.equals("")) {
				token.setLocality(value);
			}
		}
		if (claims.indexOf("stateorprovince") != -1) {
			String value = getDataValue(data, "stateorprovince");
			if ((value != null) && !value.equals("")) {
				token.setStateOrProvince(value);
			}
		}
		if (claims.indexOf("postalcode") != -1) {
			String value = getDataValue(data, "postalcode");
			if ((value != null) && !value.equals("")) {
				token.setPostalCode(value);
			}
		}
		if (claims.indexOf("country") != -1) {
			String value = getDataValue(data, "country");
			if ((value != null) && !value.equals("")) {
				token.setCountry(value);
			}
		}
		if (claims.indexOf("primaryphone") != -1) {
			String value = getDataValue(data, "primaryphone");
			if ((value != null) && !value.equals("")) {
				token.setPrimaryPhone(value);
			}
		}
		if (claims.indexOf("otherphone") != -1) {
			String value = getDataValue(data, "otherphone");
			if ((value != null) && !value.equals("")) {
				token.setOtherPhone(value);
			}
		}
		if (claims.indexOf("mobilephone") != -1) {
			String value = getDataValue(data, "mobilephone");
			if ((value != null) && !value.equals("")) {
				token.setMobilePhone(value);
			}
		}
		if (claims.indexOf("dateofbirth") != -1) {
			String value = getDataValue(data, "dateofbirth");
			if ((value != null) && !value.equals("")) {
				token.setDateOfBirth(value);
			}
		}
		if (claims.indexOf("gender") != -1) {
			String value = getDataValue(data, "gender");
			if ((value != null) && !value.equals("")) {
				token.setGender(value);
			}
		}
		return token;
	}

	private X509Name claims2X509Name(Element data)
			throws TokenIssuanceException {

		Vector oids = new Vector();
		Vector values = new Vector();

		String value = getDataValue(data, "givenname");
		if ((value != null) && !value.equals("")) {
			oids.add(X509Name.GIVENNAME);
			values.add(value);
		}
		value = getDataValue(data, "surname");
		if ((value != null) && !value.equals("")) {
			oids.add(X509Name.SURNAME);
			values.add(value);
		}
		value = getDataValue(data, "emailaddress");
		if ((value != null) && !value.equals("")) {
			oids.add(X509Name.E);
			values.add(value);
		}
		// value = getDataValue(data, "streetladdress");
		// if ((value != null) && !value.equals("")) {
		// sb.append(" streetladdress=");
		// sb.append(value);
		// }
		value = getDataValue(data, "locality");
		if ((value != null) && !value.equals("")) {
			oids.add(X509Name.L);
			values.add(value);
		}
		value = getDataValue(data, "stateorprovince");
		if ((value != null) && !value.equals("")) {
			oids.add(X509Name.ST);
			values.add(value);
		}
		// value = getDataValue(data, "postalcode");
		// if ((value != null) && !value.equals("")) {
		// sb.append("postalcode=");
		// sb.append(value);
		// }
		value = getDataValue(data, "country");
		if ((value != null) && !value.equals("")) {
			oids.add(X509Name.C);
			values.add(value);
		}
		// value = getDataValue(data, "primaryphone");
		// if ((value != null) && !value.equals("")) {
		// sb.append(" primaryphone=");
		// sb.append(value);
		// }
		// value = getDataValue(data, "otherphone");
		// if ((value != null) && !value.equals("")) {
		// sb.append(" otherphone=");
		// sb.append(value);
		// }
		// value = getDataValue(data, "mobilephone");
		// if ((value != null) && !value.equals("")) {
		// sb.append(" mobilephone=");
		// sb.append(value);
		// }
		// value = getDataValue(data, "dateofbirth");
		// if ((value != null) && !value.equals("")) {
		// sb.append(" dateofbirth=");
		// sb.append(value);
		// }
		// value = getDataValue(data, "gender");
		// if ((value != null) && !value.equals("")) {
		// sb.append(" gender=");
		// sb.append(value);
		// }

		return new X509Name(oids, values);
	}

	// private String claims2String(Element data) throws TokenIssuanceException
	// {
	// StringBuffer sb = new StringBuffer("");
	//
	// String value = getDataValue(data, "givenname");
	// if ((value != null) && !value.equals("")) {
	// sb.append("givenanme=");
	// sb.append(value);
	// }
	// value = getDataValue(data, "surname");
	// if ((value != null) && !value.equals("")) {
	// sb.append(" surname=");
	// sb.append(value);
	// }
	// value = getDataValue(data, "emailaddress");
	// if ((value != null) && !value.equals("")) {
	// sb.append(" E=");
	// sb.append(value);
	// }
	// value = getDataValue(data, "streetladdress");
	// if ((value != null) && !value.equals("")) {
	// sb.append(" streetladdress=");
	// sb.append(value);
	// }
	// value = getDataValue(data, "locality");
	// if ((value != null) && !value.equals("")) {
	// sb.append(" l=");
	// sb.append(value);
	// }
	// value = getDataValue(data, "stateorprovince");
	// if ((value != null) && !value.equals("")) {
	// sb.append(" ST=");
	// sb.append(value);
	// }
	// // value = getDataValue(data, "postalcode");
	// // if ((value != null) && !value.equals("")) {
	// // sb.append("postalcode=");
	// // sb.append(value);
	// // }
	// value = getDataValue(data, "country");
	// if ((value != null) && !value.equals("")) {
	// sb.append(" C=");
	// sb.append(value);
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
	// return sb.toString();
	// }

	public X509Certificate infocard2Certificate(Document infocard)
			throws TokenIssuanceException, UnsupportedEncodingException,
			ParseException {
		X509Certificate cert = null;
		KeyPair kp = new KeyPair(signingCert.getPublicKey(), signingKey);
		X509Name issuer = new X509Name(
				"CN=firefox, OU=infocard selector, O=xmldap, L=San Francisco, ST=California, C=US");
		Nodes dataNodes = infocard.query("/infocard/carddata/selfasserted");
		Element data = (Element) dataNodes.get(0);
		X509Name subject = claims2X509Name(data);

		DateFormat df = DateFormat.getDateInstance();
		Date dateOfBirth = df.parse(getDataValue(data, "dateofbirth"));
		cert = CertsAndKeys.generateClientCertificate(kp, issuer, subject,
				getDataValue(data, "gender"), dateOfBirth, getDataValue(data,
						"streetladdress"), getDataValue(data, "primaryphone"));
		return cert;
	}

	/**
	 * Uses the first bytes of the infoCardPpi to encrypt a String which is
	 * fixed for the relying party using AES. Remember: The input parameter
	 * infoCardPpi is generated in the Firefox Identity Selector as a SHA-1 hash
	 * of the string "cardname + random-numer + cardversion" once the card is
	 * issued. This hash is 160 bit long. For the AES encryption we need a 128
	 * bit key. This function just truncates the hash and uses the result as the
	 * key for the AES encryption. As long as neither the infoCardPpi and the
	 * rpSignature don't change this yields the same PPI for this RP everytime.
	 * The result is then SHA-1 hashed again (to get a short ppi) and Base64
	 * encoded to make it printable. The schema http://xmldap.org/Infocard.xsd
	 * defines it as <xs:element name="PrivatePersonalIdentifier"
	 * type="tns:Base64BinaryMaxSize1K"/>.
	 * 
	 * @param infoCardPpi
	 * @param rpSignature
	 * @return the new PPI which is unique for this relying party
	 * @throws TokenIssuanceException
	 */
	public String generatePpiForThisRP(String infoCardPpi, String rpData)
			throws TokenIssuanceException {
		byte[] keyBytes = new byte[16];
		byte[] b;
		try {
			b = infoCardPpi.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new TokenIssuanceException(e);
		}
		int len = b.length;
		if (len > keyBytes.length)
			len = keyBytes.length;
		System.arraycopy(b, 0, keyBytes, 0, len);
		SecretKeySpec skeySpec = new SecretKeySpec(keyBytes, "AES");
		try {
			Cipher cipher = Cipher.getInstance("AES");
			cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
			byte[] encrypted = cipher.doFinal(rpData.getBytes("UTF-8"));
			MessageDigest mdAlgorithm = MessageDigest.getInstance("SHA-1");
			mdAlgorithm.update(encrypted);
			byte[] digest = mdAlgorithm.digest();
			return Base64.encodeBytes(digest);
		} catch (NoSuchAlgorithmException e) {
			throw new TokenIssuanceException(e);
		} catch (NoSuchPaddingException e) {
			throw new TokenIssuanceException(e);
		} catch (InvalidKeyException e) {
			throw new TokenIssuanceException(e);
		} catch (IllegalBlockSizeException e) {
			throw new TokenIssuanceException(e);
		} catch (BadPaddingException e) {
			throw new TokenIssuanceException(e);
		} catch (UnsupportedEncodingException e) {
			throw new TokenIssuanceException(e);
		}
	}

	private void storeInfoCardAsCertificate(String nickname, Document infocard)
			throws TokenIssuanceException { // temporary hack to store infocards
		// as certificates
		try {
			X509Certificate cardAsCert = infocard2Certificate(infocard);
			// store in firefox.jks
			storeCardCertKeystore(nickname, cardAsCert, false);
			// store in <ppi>.pem
			storeCardCertPem(nickname, cardAsCert);
			// store in <ppi>.p12
			// storeCardCertP12(token.getPrivatePersonalIdentifier(),
			// cardAsCert);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CertificateEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public String getAllCards(String dirName, String password)
			throws TokenIssuanceException {
		Crds crdStore = Crds.getInstance();
		String cardXml = crdStore.getAllCards(dirName, password);
		return cardXml;
//		System.out.println(cardXml);
//		try {
//			JSONObject json = XML.toJSONObject(cardXml);
//			return json.toString();
//		} catch (JSONException e) {
//			throw new TokenIssuanceException(e);
//		}
	}

	public String getCard(String dirName, String password, String cardId)
			throws TokenIssuanceException {
		Crds crdStore = Crds.getInstance();
		String cardXml = crdStore.getCard(dirName, password, cardId);
		JSONObject json;
		try {
			json = XML.toJSONObject(cardXml);
		} catch (JSONException e) {
			throw new TokenIssuanceException(e);
		}
		return json.toString();
	}

	public String newCard(String dirName, String password, String card)
			throws TokenIssuanceException {

		Document infocard = getInfocard(card);

		Crds crdStore = Crds.getInstance();
		return crdStore.newCard(dirName, password, infocard);
	}

	public String getIssuerLogoURL(String der) throws JSONException,
			TokenIssuanceException, IOException 
	{
		String url = null;
		X509Certificate relyingPartyCert = der2cert(der);
		byte[] fromExtensionValue = relyingPartyCert
				.getExtensionValue(Logotype.id_pe_logotype.getId());
		if ((fromExtensionValue == null) || (fromExtensionValue.length == 0)) {
			// certificat does not contain a logotype extension
			return null;
		}
		ASN1Encodable extVal = X509ExtensionUtil.fromExtensionValue(fromExtensionValue);
		Logotype logotype = Logotype.getInstance((ASN1Sequence)extVal);
		LogotypeInfo logotypeInfo = logotype.getIssuerLogo();
		if (logotypeInfo == null) {
			// logotype contains no issuer logo
			return null;
		}
		if (logotypeInfo.getTagNo() == LogotypeInfo.direct) {
			LogotypeData direct = logotypeInfo.getLogotypeData();
			LogotypeDetails[] images = direct.getImages();
			LogotypeDetails logotypeDetails = images[0];
			// String mediaType = logotypeDetails.getMediaType();
			// assertEquals("image/gif", mediaType);
			// DigestInfo[] dis = logotypeDetails.getLogotypeHash();
			// assertEquals(1, dis.length);
			// DigestInfo di = dis[0];
			// AlgorithmIdentifier algId = di.getAlgorithmId();
			// assertEquals("1.3.14.3.2.26", algId.getObjectId().getId());
			// byte[] digest = di.getDigest();
			// byte[] expected = { -113, -27, -45, 26, -122, -84, -115,
			// -114, 107, -61, -49, -128, 106, -44, 72, 24, 44, 123, 25,
			// 46};
			// assertEquals(expected.length , digest.length);
			// for (int i=0; i<digest.length; i++) {
			// assertEquals(expected[i], digest[i]);
			// }
			String[] uris = logotypeDetails.getLogotypeURI();
			// assertEquals(1, uris.length);
			url = uris[0];
			// assertEquals("http://logo.verisign.com/vslogo.gif", uri);
		} else if (logotypeInfo.getTagNo() == LogotypeInfo.indirect) {
			// LogotypeReference indirect = logotypeInfo
			// .getLogotypeReference();
			// TODO
			// LogotypeReference indirect =
			// LogotypeReference.getInstance((ASN1TaggedObject)issuerLogo.toASN1Object(),
			// false);
			// assertNotNull(indirect);
		}
		return url;
	}
	
	public String getToken(String serializedPolicy)
			throws TokenIssuanceException {

		// TODO - break up this rather large code block
		JSONObject policy = null;
		String der = null;
        String issuedToken = "";
        String type = null;

        try {

            policy = new JSONObject(serializedPolicy);
            type = (String) policy.get("type");
            der = (String) policy.get("cert");

        } catch (JSONException e) {
            throw new TokenIssuanceException(e);
        }


        X509Certificate relyingPartyCert = der2cert(der);

        if (type.equals("selfAsserted")) {

            String card = null;

            try {
                card = (String) policy.get("card");
            } catch (JSONException e) {
                throw new TokenIssuanceException(e);
            }

            Document infocard = getInfocard(card);

            Nodes dataNodes = infocard.query("/infocard/carddata/selfasserted");
            Element data = (Element) dataNodes.get(0);

            // TODO - support all elements including ppi
            String ppi = "";


            Nodes ppiNodes = infocard.query("/infocard/privatepersonalidentifier");
            Element ppiElm = (Element) ppiNodes.get(0);
            if (ppiElm != null) {
                ppi = ppiElm.getValue();
            } else {
                throw new TokenIssuanceException(
                        "Error: This infocard has no privatepersonalidentifier!");
            }

            // storeInfoCardAsCertificate(ppi, infocard);

            ppi = generatePpiForThisRP(ppi, relyingPartyCert
                    .getSubjectX500Principal().getName());

    //		System.out.println("Server Cert: "
    //				+ relyingPartyCert.getSubjectDN().toString());

            EncryptedData encryptor = new EncryptedData(relyingPartyCert);
            SelfIssuedToken token = new SelfIssuedToken(relyingPartyCert,
                    signingCert, signingKey);

            token.setPrivatePersonalIdentifier(Base64.encodeBytes(ppi.getBytes()));
            token.setValidityPeriod(-5, 10);

            final String ALL_CLAIMS = "givenname"
                    + " "
                    + "emailaddress"
                    + " "
                    + "surname"
                    + " "
                    + "streetaddress"
                    + " "
                    + "locality"
                    + " "
                    + "stateorprovince"
                    + " "
                    + "postalcode"
                    + " "
                    + "country"
                    + " "
                    + "homephone"
                    + " "
                    + "otherphone"
                    + " "
                    + "mobilephone"
                    + " "
                    + "dateofbirth"
                    + " "
                    + "gender";

            String requiredClaims = null;
            String optionalClaims = null;
            try {
                requiredClaims = (String) policy.get("requiredClaims");
            } catch (JSONException e) {
                // throw new TokenIssuanceException(e); // requiredClaims not found
            }
            try {
                optionalClaims = (String) policy.get("optionalClaims");
            } catch (JSONException e) {
                // throw new TokenIssuanceException(e); // optionalClaims not found
            }
            if (requiredClaims == null) {
                if (optionalClaims != null) {
                    token = setTokenClaims(data, token, optionalClaims);
                } else { // hm, lets throw everything we have at the RP
                    token = setTokenClaims(data, token, ALL_CLAIMS);
                }
            } else { // requiredClaim are present
                token = setTokenClaims(data, token, requiredClaims);
                if (optionalClaims != null) {
                    token = setTokenClaims(data, token, optionalClaims);
                }
            }

            Element securityToken = null;
            try {
                securityToken = token.serialize();
                encryptor.setData(securityToken.toXML());
                issuedToken = encryptor.toXML();

            } catch (SerializationException e) {
                throw new TokenIssuanceException(e);
            }

        } else {

            String assertion = null;

            try {
                assertion = (String) policy.get("assertion");
            } catch (JSONException e) {
                throw new TokenIssuanceException(e);
            }

            EncryptedData encryptor = new EncryptedData(relyingPartyCert);

            try {
                
                encryptor.setData(assertion);
                issuedToken = encryptor.toXML();

            } catch (SerializationException e) {
                throw new TokenIssuanceException(e);
            }

        }

        return issuedToken;

    }

	private X509Certificate der2cert(String der) throws TokenIssuanceException {
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
