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
import java.security.cert.X509Certificate;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.CertificateFactory;
import java.security.cert.CertificateException;

import org.xmldap.exceptions.InfoCardProcessingException;
import org.xmldap.exceptions.CryptoException;
import org.xmldap.xmldsig.EnvelopedSignature;
import org.xmldap.ws.WSConstants;
import org.xmldap.util.XSDDateTime;



public class Token {


    private boolean haveValidatedAll = false;
    private boolean valid = false;

    private boolean haveValidatedConditions = false;
    private boolean conditionsValid = false;
    private Calendar startValidityPeriod = null;
    private Calendar endValidityPeriod = null;

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
           throw new InfoCardProcessingException("Eror Decrypting encrypted token", e);
        }

        if (decryptedToken == null) {
           throw new InfoCardProcessingException("Result of token decryption was null");
        }


    }

    public Document getDoc() throws InfoCardProcessingException{

        if (doc == null ) {

            Builder parser = new Builder();
            try {
                doc = parser.build(decryptedToken, "");
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

    public boolean isConditionsValid() throws InfoCardProcessingException {
        if ( ! haveValidatedConditions ) validateConditions();
        return conditionsValid;
    }

    public Calendar getStartValidityPeriod() throws InfoCardProcessingException {
        if ( ! haveValidatedConditions ) validateConditions();
        return startValidityPeriod;
    }

    public Calendar getEndValidityPeriod() throws InfoCardProcessingException {
        if ( ! haveValidatedConditions ) validateConditions();
        return endValidityPeriod;
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
                getCertificate().checkValidity();
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
                signatureValid = EnvelopedSignature.validate(getDoc());
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


    private void validateConditions() throws InfoCardProcessingException {

        //Get the conditions
        XPathContext thisContext = new XPathContext();
        thisContext.addNamespace("saml", WSConstants.SAML11_NAMESPACE);
        Nodes nodes = getDoc().query("//saml:Conditions", thisContext);
        Element element = (Element) nodes.get(0);

        //Get the values
        String notBeforeVal = element.getAttribute("NotBefore").getValue();
        String notOnOrAfterVal = element.getAttribute("NotOnOrAfter").getValue();
        Calendar now = XSDDateTime.parse(new XSDDateTime().getDateTime());

        startValidityPeriod = XSDDateTime.parse(notBeforeVal);
        endValidityPeriod = XSDDateTime.parse(notOnOrAfterVal);

        //inbetween - we could handle on, but since this is milli percision, it seems like a useless edge case.
        if ((startValidityPeriod.before(now)) && (endValidityPeriod.after(now))) conditionsValid = true;

        //tell the class not to do this work again
        haveValidatedConditions = true;

    }

    private void parseCertificate() throws InfoCardProcessingException {
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
            } catch (CertificateException e) {
                throw new InfoCardProcessingException("Error creating X509Certificate from Token");
            }

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
