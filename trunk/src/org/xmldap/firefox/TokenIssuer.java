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

import org.bouncycastle.asn1.x509.X509Name;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;
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
import javax.security.auth.x500.X500Principal;

import java.io.*;
import java.net.URLDecoder;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.*;
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
				throw new TokenIssuanceException(e1);
			} catch (NoSuchProviderException e1) {
				throw new TokenIssuanceException(e1);
			} catch (InvalidKeyException e1) {
				throw new TokenIssuanceException(e1);
			} catch (SecurityException e1) {
				throw new TokenIssuanceException(e1);
			} catch (SignatureException e1) {
				throw new TokenIssuanceException(e1);
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

	public boolean isExtendedEvaluationCert(X509Certificate relyingpartyCert) {
		return false;
	}
	
	public byte[] rpIdentifier(
			X509Certificate relyingpartyCert, 
			X509Certificate[] chain)
	throws TokenIssuanceException {
		if (isExtendedEvaluationCert(relyingpartyCert)) {
			return rpIdentifierEV(relyingpartyCert);
		} else {
			return rpIdentifierNonEV(relyingpartyCert, chain);
		}
	}
	
	private String orgIdString(X509Certificate relyingpartyCert)
	throws TokenIssuanceException {
		X500Principal principal = relyingpartyCert.getSubjectX500Principal();
		String dn = principal.getName();
		if (dn == null) {
			PublicKey publicKey = relyingpartyCert.getPublicKey();
			return new String(publicKey.getEncoded());
		}
		X509Name x509Name = new X509Name(dn);
		Vector oids = x509Name.getOIDs();
		Vector values = x509Name.getValues();
		int index = 0;
		StringBuffer orgIdStringBuffer = new StringBuffer("|"); 
		for (Object oid : oids) {
			if ("O".equals(oid)) {
				String value = (String)values.get(index);
				if (value==null) {
					orgIdStringBuffer.append("O=\"\"|");
				} else {
					orgIdStringBuffer.append("O=\"" + value + "\"|");
				}
			} else if ("L".equals(oid)) {
				String value = (String)values.get(index);
				if (value==null) {
					orgIdStringBuffer.append("L=\"\"|");
				} else {
					orgIdStringBuffer.append("L=\"" + value + "\"|");
				}
			} else if ("S".equals(oid)) {
				String value = (String)values.get(index);
				if (value==null) {
					orgIdStringBuffer.append("S=\"\"|");
				} else {
					orgIdStringBuffer.append("S=\"" + value + "\"|");
				}
			} else if ("C".equals(oid)) {
				String value = (String)values.get(index);
				if (value==null) {
					orgIdStringBuffer.append("C=\"\"|");
				} else {
					orgIdStringBuffer.append("C=\"" + value + "\"|");
				}
			} else {
				System.out.println("unused oid (" + oid + "). Value=" + (String)values.get(index));
			}
			index += 1;
		}
		if (orgIdStringBuffer.length() == 1) { // none of OLSC were found
			PublicKey publicKey = relyingpartyCert.getPublicKey();
			return new String(publicKey.getEncoded());
		}
		return orgIdStringBuffer.toString();
	}
	
	public byte[] rpIdentifierNonEV(
			X509Certificate relyingpartyCert,
			X509Certificate[] chain)
	throws TokenIssuanceException {
		String orgIdString = orgIdString(relyingpartyCert);
		
		String qualifiedOrgIdString = qualifiedOrgIdString(chain, orgIdString);
		try {
			byte[] qualifiedOrgIdBytes = qualifiedOrgIdString.getBytes("UTF-8");
			byte[] rpIdentifier = sha256(qualifiedOrgIdBytes);
			return rpIdentifier;
		} catch (UnsupportedEncodingException e) {
			throw new TokenIssuanceException(e);
		}
	}

	/**
	 * @param chain
	 * @param orgIdString
	 */
	public String qualifiedOrgIdString(X509Certificate[] chain, String orgIdString) {
		StringBuffer qualifiedOrgIdString = new StringBuffer();
		for (int i=chain.length; i<0; i++) {
			X509Certificate parent = chain[i];
			X500Principal parentPrincipal = parent.getSubjectX500Principal();
			String subjectDN = parentPrincipal.getName(X500Principal.RFC2253);
			// append CertPathString
			qualifiedOrgIdString.append("|ChainElement=\"");
			qualifiedOrgIdString.append(subjectDN);
			qualifiedOrgIdString.append("\"");
		}
		qualifiedOrgIdString.append(orgIdString);
		return qualifiedOrgIdString.toString();
	}
	
	public byte[] rpIdentifierEV(X509Certificate relyingpartyCert)
	throws TokenIssuanceException {
		String rpIdentifier = null;
		String orgIdString = orgIdString(relyingpartyCert);
		
		byte[] digest;
		try {
			digest = sha256(orgIdString.getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e) {
			throw new TokenIssuanceException(e);
		}
		return digest;
	}
	
	private byte[] sha256(byte[] bytes) throws TokenIssuanceException {
		MessageDigest mdAlgorithm;
		try {
			mdAlgorithm = MessageDigest.getInstance("SHA-256");
		} catch (NoSuchAlgorithmException e) {
			throw new TokenIssuanceException(e); 
		}
		mdAlgorithm.update(bytes);
		byte[] digest = mdAlgorithm.digest();
		return digest;
	}
	
	private String generateRPPPID(
			String infoCardPpi, 
			X509Certificate relyingPartyCert,
			X509Certificate[] chain)
		throws TokenIssuanceException {
		try {
			byte[] rpIdentifierBytes = sha256(rpIdentifier(relyingPartyCert, chain));
			byte[] canonicalCardIdBytes = sha256(infoCardPpi.getBytes("UTF-8"));
			byte[] bytes = new byte[rpIdentifierBytes.length+canonicalCardIdBytes.length];
			System.arraycopy(rpIdentifierBytes, 0, bytes, 0, rpIdentifierBytes.length);
			System.arraycopy(canonicalCardIdBytes, 0, bytes, rpIdentifierBytes.length, canonicalCardIdBytes.length);
			byte[] ppidBytes = sha256(bytes);
			return Base64.encodeBytes(ppidBytes);
		} catch (UnsupportedEncodingException e) {
			throw new TokenIssuanceException(e);
		}
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

	public String getToken(String serializedPolicy)
			throws TokenIssuanceException {

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


        X509Certificate relyingPartyCert;
		try {
			relyingPartyCert = org.xmldap.util.CertsAndKeys.der2cert(der);
		} catch (CertificateException e) {
			throw new TokenIssuanceException(e);
		}

		int chainLength = 0;
		try {
			String chainLengthStr = (String) policy.get("chainLength");
			chainLength = Integer.parseInt(chainLengthStr);
		} catch (JSONException e) {
			throw new TokenIssuanceException(e);
		}
		
		X509Certificate[] chain = new X509Certificate[chainLength];
		for (int i=0; i<chainLength; i++) {
			try {
				String chainDer = (String) policy.get("certChain"+i);
				X509Certificate chainCert = org.xmldap.util.CertsAndKeys.der2cert(chainDer);
				chain[i] = chainCert;
			} catch (JSONException e) {
				throw new TokenIssuanceException(e);
			} catch (CertificateException e) {
				throw new TokenIssuanceException(e);
			}
		}
		
        if (type.equals("selfAsserted")) {
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

    		String card = null;
    		try {
    		    card = (String) policy.get("card");
    		} catch (JSONException e) {
    		    throw new TokenIssuanceException(e);
    		}

            issuedToken = getSelfAssertedToken(
            		card, relyingPartyCert, chain, requiredClaims, optionalClaims);

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

	/**
	 * @param policy
	 * @param issuedToken
	 * @param relyingPartyCert
	 * @return
	 * @throws TokenIssuanceException
	 */
	private String getSelfAssertedToken(
			String card, 
			X509Certificate relyingPartyCert,
			X509Certificate[] chain,
			String requiredClaims, String 
			optionalClaims) throws TokenIssuanceException {
		String issuedToken = null;

		Document infocard = getInfocard(card);

		Nodes dataNodes = infocard.query("/infocard/carddata/selfasserted");
		Element data = (Element) dataNodes.get(0);

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
		ppi = generateRPPPID(ppi, relyingPartyCert, chain);
//		ppi = generatePpiForThisRP(ppi, relyingPartyCert
//		        .getSubjectX500Principal().getName());

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

		if (requiredClaims == null) {
		    if (optionalClaims != null) {
		        token = org.xmldap.infocard.SelfIssuedToken.setTokenClaims(data, token, optionalClaims);
		    } else { // hm, lets throw everything we have at the RP
		        token = org.xmldap.infocard.SelfIssuedToken.setTokenClaims(data, token, ALL_CLAIMS);
		    }
		} else { // requiredClaim are present
		    token = org.xmldap.infocard.SelfIssuedToken.setTokenClaims(data, token, requiredClaims);
		    if (optionalClaims != null) {
		        token = org.xmldap.infocard.SelfIssuedToken.setTokenClaims(data, token, optionalClaims);
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
		return issuedToken;
	}

}
