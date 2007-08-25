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

package org.xmldap.rp;

import nu.xom.*;

import java.util.Calendar;
import java.util.Map;
import java.util.HashMap;
import java.io.IOException;
import java.io.ByteArrayInputStream;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.CertificateFactory;
import java.security.cert.CertificateException;

import org.xmldap.crypto.CryptoUtils;
import org.xmldap.exceptions.InfoCardProcessingException;
import org.xmldap.exceptions.CryptoException;
import org.xmldap.xmldsig.ValidatingBaseEnvelopedSignature;
import org.xmldap.ws.WSConstants;
import org.xmldap.saml.Conditions;
import org.xmldap.util.XSDDateTime;



public class Token {


    private boolean haveValidatedAll = false;
    private boolean valid = false;

    private boolean haveValidatedConditions = false;
    private boolean conditionsValid = false;
    private Conditions conditions = null;
    private String	audience = null;

    private boolean haveValidatedCertificate = false;
    private boolean certificateValid = true;
    private X509Certificate certificate = null;

    private boolean haveValidatedSignature = false;
    private boolean signatureValid = false;

    private String encryptedToken = null;
    private String decryptedToken = null;
    private Document doc = null;

    private HashMap claims = null;

    public Token(String encryptedToken, PrivateKey privateKey) throws InfoCardProcessingException{

        this.encryptedToken = encryptedToken;

        DecryptUtil decryptUtil = new DecryptUtil();
        try {
           decryptedToken = decryptUtil.decryptToken(encryptedToken, privateKey);
        } catch (CryptoException e) {
           throw new InfoCardProcessingException("Error decrypting encrypted token", e);
        }

        if (decryptedToken == null) {
           throw new InfoCardProcessingException("Result of token decryption was null");
        }


    }

    public Document getDoc() throws InfoCardProcessingException{

        if (doc == null ) {

            try {
                doc = org.xmldap.xml.XmlUtils.parse(decryptedToken);
            } catch (ParsingException e) {
                throw new InfoCardProcessingException("Unable to parse decrypted token into a XOM document", e);
            } catch (IOException e) {
                throw new InfoCardProcessingException("Unable to parse decrypted token into a XOM document", e);
            }

        }

        return doc;

    }

    public String getEncryptedToken() {
        return encryptedToken;
    }

    public String getDecryptedToken() {
        return decryptedToken;
    }

    public String getAudience() {
    	return audience;
    }
    
    public boolean isConditionsValid() throws InfoCardProcessingException {
        if ( ! haveValidatedConditions ) validateConditions();
        return conditionsValid;
    }

    public Calendar getStartValidityPeriod() throws InfoCardProcessingException {
        if ( ! haveValidatedConditions ) validateConditions();
        return conditions.getNotBefore();
    }

    public Calendar getEndValidityPeriod() throws InfoCardProcessingException {
        if ( ! haveValidatedConditions ) validateConditions();
        return conditions.getNotOnOrAfter();
    }

    public X509Certificate getCertificate() throws InfoCardProcessingException {
        if (certificate == null) parseCertificate();
        return certificate;
    }


    public Map getClaims() throws InfoCardProcessingException {

        if ( claims == null ) parseClaims();
        return claims;

    }

    public boolean isCertificateValid() throws InfoCardProcessingException {

        if (! haveValidatedCertificate ) {

            try {
            	certificate = getCertificate();
            	if (certificate != null) {
            		certificate.checkValidity();
            	} else {
            		throw new InfoCardProcessingException("This token does not have a certificate");
            	}
            } catch (CertificateExpiredException e) {
                System.out.println("Certificate expired");
                certificateValid = false;
            } catch (CertificateNotYetValidException e) {
                System.out.println("Certificate not yet valid");
                certificateValid = false;
            }
        }

        return certificateValid;

    }

    public boolean isSignatureValid() throws InfoCardProcessingException {

        if ( ! haveValidatedSignature ) {

            try {
                signatureValid = ValidatingBaseEnvelopedSignature.validate(getDoc());
            } catch (CryptoException e) {
                throw new InfoCardProcessingException("Error validating signature", e);
            }

        }

        return signatureValid;

    }

    public boolean isValid() throws InfoCardProcessingException {

        if ( ! haveValidatedAll ) {

            if ( isConditionsValid() && isCertificateValid() && isSignatureValid() ) valid = true;
            haveValidatedAll = true;

        }

        return valid;

    }


    private void parseConditions() throws InfoCardProcessingException {
        //Get the conditions
    	//  <saml:Conditions 
    	//   NotBefore="2007-08-21T07:18:50.605Z" 
    	//   NotOnOrAfter="2007-08-21T08:18:50.605Z">
    	//   <saml:AudienceRestrictionCondition>
    	//    <saml:Audience>https://w4de3esy0069028.gdc-bln01.t-systems.com:8443/relyingparty/</saml:Audience>
    	//   </saml:AudienceRestrictionCondition>
    	//  </saml:Conditions>

        XPathContext thisContext = new XPathContext();
        thisContext.addNamespace("saml", WSConstants.SAML11_NAMESPACE);
        Nodes nodes = getDoc().query("//saml:Conditions", thisContext);
        Element element = (Element) nodes.get(0);

        //Get the values
        String notBeforeVal = element.getAttribute("NotBefore").getValue();
        String notOnOrAfterVal = element.getAttribute("NotOnOrAfter").getValue();

        conditions = new Conditions(
        		XSDDateTime.parse(notBeforeVal), XSDDateTime.parse(notOnOrAfterVal));
        
        Elements elements = element.getChildElements("AudienceRestrictionCondition", WSConstants.SAML11_NAMESPACE);
        if (elements.size() == 1) {
        	Element audienceRestrictionCondition = elements.get(0);
        	elements = audienceRestrictionCondition.getChildElements("Audience", WSConstants.SAML11_NAMESPACE);
        	if (elements.size() == 1) {
        		audience = elements.get(0).getValue();
        	} else {
        		throw new InfoCardProcessingException("Expected the element AudienceRestrictionCondition to have one child Audience, but found" + elements.size());
        	}
        } else {
        	audience = null;
        }
    }

    public String getConfirmationMethod() {
        XPathContext thisContext = new XPathContext();
        thisContext.addNamespace("saml", WSConstants.SAML11_NAMESPACE);
        Nodes nodes = null;
		try {
			nodes  = getDoc().query("saml:Assertion/saml:AttributeStatement/saml:Subject/saml:SubjectConfirmation/saml:ConfirmationMethod", thisContext);;
		} catch (InfoCardProcessingException e) {
			return e.getMessage();
		}
		if (nodes.size() == 1) {
			Element element = (Element) nodes.get(0);
			return element.getValue();
		} else {
			return "Expected one saml:ConfirmationMethod, but found " + nodes.size();
		}
    }


    private void validateConditions() throws InfoCardProcessingException {

        Calendar now = XSDDateTime.parse(new XSDDateTime().getDateTime());

        parseConditions();
        
        //inbetween - we could handle on, but since this is milli percision, it seems like a useless edge case.
        conditionsValid = conditions.validate(now);

        //tell the class not to do this work again
        haveValidatedConditions = true;

    }

    public String getClientDigest() throws InfoCardProcessingException, CryptoException {
    	X509Certificate cert = getCertificateOrNull();
    	if (cert != null) {
    		certificate = cert;
    		PublicKey publicKey = certificate.getPublicKey();
    		String sha1 = CryptoUtils.digest(publicKey.getEncoded());
    		return sha1;
    	} else {
    		String modulus = getModulusOrNull();
    		if (modulus != null) {
    			String sha1 = CryptoUtils.digest(modulus.getBytes());
    			return sha1;
    		} else {
    			throw new InfoCardProcessingException("could not find neither certificate nor modulus");
    		}
    	}
    }

	public String getModulusOrNull() throws InfoCardProcessingException {
//	    XPathContext thisContext = new XPathContext();
//	    thisContext.addNamespace("saml", WSConstants.SAML11_NAMESPACE);
//	    thisContext.addNamespace("dsig", WSConstants.DSIG_NAMESPACE);
//	    Nodes nodes = getDoc().query("//dsig:KeyValue/dsig:RSAKeyValue/dsig:Modulus", thisContext);
//	    if ((nodes != null) && (nodes.size() > 0)) {
//	        String element = nodes.get(0).getValue();
//	        return element;
//	    } 
//	    return null;
		return ValidatingBaseEnvelopedSignature.getModulusOrNull(getDoc());
	}

	public X509Certificate getCertificateOrNull() throws InfoCardProcessingException {
		X509Certificate certificate = null;
	    XPathContext thisContext = new XPathContext();
	    thisContext.addNamespace("saml", WSConstants.SAML11_NAMESPACE);
	    thisContext.addNamespace("dsig", WSConstants.DSIG_NAMESPACE);
	    Nodes nodes = getDoc().query("//dsig:X509Data/dsig:X509Certificate", thisContext);
	    if ((nodes != null) && (nodes.size() > 0)) {
	        String element = nodes.get(0).getValue();
	        StringBuffer sb = new StringBuffer("-----BEGIN CERTIFICATE-----\n");
	        sb.append(element);
	        sb.append("\n-----END CERTIFICATE-----\n");
	
	        ByteArrayInputStream bis = new ByteArrayInputStream(sb.toString().getBytes());
	        CertificateFactory cf;
	        try {
	            cf = CertificateFactory.getInstance("X509");
	            certificate = (X509Certificate)cf.generateCertificate(bis);
	            return certificate;
	        } catch (CertificateException e) {
	            throw new InfoCardProcessingException("Error creating X509Certificate from Token", e);
	        }
	
	    } 
	    return null;
	}

	private void parseCertificate() throws InfoCardProcessingException {
		certificate = getCertificateOrNull();
	    if (certificate == null) {
	    } else {
	        throw new InfoCardProcessingException("No X509Certificate provided in Token");
	    }
	}


    //TODO - improve claims
    private void parseClaims() throws InfoCardProcessingException {

        claims = new HashMap();
        XPathContext context = new XPathContext();
        context.addNamespace("saml", WSConstants.SAML11_NAMESPACE);
        Nodes claimNodeList = getDoc().query("/saml:Assertion/saml:AttributeStatement/saml:Attribute", context);

        for (int i = 0; i < claimNodeList.size(); i++) {

            Element claim = (Element) claimNodeList.get(i);
            Attribute nameAttr = claim.getAttribute("AttributeName");
            String name = nameAttr.getValue();
            Element valueElm = claim.getFirstChildElement("AttributeValue", WSConstants.SAML11_NAMESPACE);
            String value = valueElm.getValue();
            claims.put(name,value);

        }

    }


}
