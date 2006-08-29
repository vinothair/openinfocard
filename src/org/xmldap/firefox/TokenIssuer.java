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
 *     * Neither the name of the University of California, Berkeley nor the
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
import org.json.JSONException;
import org.json.JSONObject;
import org.xmldap.exceptions.KeyStoreException;
import org.xmldap.exceptions.SerializationException;
import org.xmldap.exceptions.TokenIssuanceException;
import org.xmldap.infocard.SelfIssuedToken;
import org.xmldap.util.Base64;
import org.xmldap.util.KeystoreUtil;
import org.xmldap.xmlenc.EncryptedData;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URLDecoder;
import java.security.PrivateKey;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

public class TokenIssuer {

	private String path;


    public TokenIssuer(String path) {

		this.path = URLDecoder.decode(path.substring(7, path.length()));

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
		// should  we use the complete string?
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

	public String getToken(String serializedPolicy)  throws TokenIssuanceException {

//TODO - get rid of keystore dependency - gen certs, store, and pass in.
        String keystorePath = path + "components/lib/firefox.jks";

        //Get my keystore
        KeystoreUtil keystore = null;
        X509Certificate signingCert = null;
        PrivateKey signingKey = null;

        try {
            keystore = new KeystoreUtil(keystorePath, "storepassword");
            signingCert = keystore.getCertificate("firefox");
            signingKey = keystore.getPrivateKey("firefox", "keypassword");

        } catch (KeyStoreException e) {
            throw new TokenIssuanceException(e);
        }




        //TODO - break up this rather large code block
		JSONObject policy = null;
		String card = null;
		String der = null;
		try {
			policy = new JSONObject(serializedPolicy);
			der = (String) policy.get("cert");
			card = (String) policy.get("card");
		} catch (JSONException e) {
			throw new TokenIssuanceException(e);
		}

		Document infocard = getInfocard(card);

		Nodes dataNodes = infocard.query("/infocard/carddata/selfasserted");
		Element data = (Element) dataNodes.get(0);

		//TODO - support all elements including ppi
		String ppi = "";

		Nodes ppiNodes = infocard.query("/infocard/privatepersonalidentifier");
		Element ppiElm = (Element) ppiNodes.get(0);
		if (ppiElm != null)
			ppi = ppiElm.getValue();

		X509Certificate relyingPartyCert = der2cert(der);

		System.out.println("Server Cert: " + relyingPartyCert.getSubjectDN().toString());



		String issuedToken = "";
		EncryptedData encryptor = new EncryptedData(relyingPartyCert);
		SelfIssuedToken token = new SelfIssuedToken(relyingPartyCert, signingCert, signingKey);

		token.setPrivatePersonalIdentifier(Base64.encodeBytes(ppi.getBytes()));
		token.setValidityPeriod(20);

		String ALL_CLAIMS = "http://schemas.microsoft.com/ws/2005/05/identity/claims/givenname"
				+ " "
				+ "http://schemas.microsoft.com/ws/2005/05/identity/claims/emailaddress"
				+ " "
				+ "http://schemas.microsoft.com/ws/2005/05/identity/claims/surname"
				+ " "
				+ "http://schemas.microsoft.com/ws/2005/05/identity/claims/streetaddress"
				+ " "
				+ "http://schemas.microsoft.com/ws/2005/05/identity/claims/locality"
				+ " "
				+ "http://schemas.microsoft.com/ws/2005/05/identity/claims/stateorprovince"
				+ " "
				+ "http://schemas.microsoft.com/ws/2005/05/identity/claims/postalcode"
				+ " "
				+ "http://schemas.microsoft.com/ws/2005/05/identity/claims/country"
				+ " "
				+ "http://schemas.microsoft.com/ws/2005/05/identity/claims/homephone"
				+ " "
				+ "http://schemas.microsoft.com/ws/2005/05/identity/claims/otherphone"
				+ " "
				+ "http://schemas.microsoft.com/ws/2005/05/identity/claims/mobilephone"
				+ " "
				+ "http://schemas.microsoft.com/ws/2005/05/identity/claims/dateofbirth"
				+ " "
				+ "http://schemas.microsoft.com/ws/2005/05/identity/claims/gender";

		String requiredClaims = null;
		String optionalClaims = null;
		try {
			requiredClaims = (String) policy.get("requiredClaims");
		} catch (JSONException e) {
			//throw new TokenIssuanceException(e); // requiredClaims not found
		}
		try {
			optionalClaims = (String) policy.get("optionalClaims");
		} catch (JSONException e) {
			//throw new TokenIssuanceException(e); // optionalClaims not found
		}
		if (requiredClaims == null) { 
			if (optionalClaims != null) {
				token = setTokenClaims(data, token, optionalClaims);
			} else { // hm, lets throw everything we have at the RP
				token = setTokenClaims(data, token, ALL_CLAIMS);
			}
		}
		else { // requiredClaim are present
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
