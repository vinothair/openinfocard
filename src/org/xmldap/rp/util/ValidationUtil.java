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

package org.xmldap.rp.util;

import nu.xom.*;
import nu.xom.canonical.Canonicalizer;
import org.xmldap.crypto.CryptoUtils;
import org.xmldap.exceptions.CryptoException;
import org.xmldap.util.Base64;
import org.xmldap.util.XSDDateTime;
import org.xmldap.ws.WSConstants;
import org.xmldap.xml.Canonicalizable;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.FileInputStream;
import java.math.BigInteger;
import java.security.cert.CertificateException;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateFactory;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.X509Certificate;
import java.text.DateFormat;
import java.util.Calendar;


public class ValidationUtil {


    public static boolean validate(String toValidate) throws CryptoException {

        Builder parser = new Builder();
        Document assertion = null;
        try {
            assertion = parser.build(toValidate, "");
            return validate(assertion);
        } catch (ParsingException e) {
            throw new CryptoException(e);
        } catch (IOException e) {
            throw new CryptoException(e);
        }

    }

    /**
     * 
     * @param assertion
     * 
     * @return String (NotBefore<now) && (now<NotOnOrAfter) 
     * 	if the notbefore and the notOnOrAfter dates fit to the current date
     * or !(NotBefore<now) && (now<NotOnOrAfter)
     * or (NotBefore<now) && !(now<NotOnOrAfter)
     *  
     * 
     */
    public static String validateConditions(Document assertion) {
//    	<saml:Conditions NotBefore="2006-09-27T13:26:59Z" NotOnOrAfter="2006-09-27T13:46:59Z" />
        XPathContext thisContext = new XPathContext();
        thisContext.addNamespace("saml", WSConstants.SAML11_NAMESPACE);
        Nodes nodes = assertion.query("//saml:Conditions", thisContext);
        Element element = (Element)nodes.get(0);
        String notBefore = (String)element.getAttribute("NotBefore").getValue();
        String notOnOrAfter = (String)element.getAttribute("NotOnOrAfter").getValue();
        Calendar now = XSDDateTime.parse(new XSDDateTime().getDateTime());
        Calendar nb = XSDDateTime.parse(notBefore);
        Calendar na = XSDDateTime.parse(notOnOrAfter);
        // TODO take care of "on"
        StringBuffer res = null;
		DateFormat df = DateFormat.getTimeInstance();
    	if (now.after(nb)) {
    		res = new StringBuffer("("+df.format(nb.getTime())+"<"+df.format(now.getTime())+")");
    	} else {
    		res = new StringBuffer("!("+df.format(nb.getTime())+"<"+df.format(now.getTime())+")");
    	}
    	if (now.before(na)) {
    		res = res.append(" && ("+df.format(now.getTime())+"<"+df.format(na.getTime())+")");
    	} else {
    		res = res.append(" && !("+df.format(now.getTime())+"<"+df.format(na.getTime())+")");
    	}
    	return res.toString();
    }

    public static X509Certificate getCertificate(Document assertion) throws CertificateException {
        XPathContext thisContext = new XPathContext();
        thisContext.addNamespace("saml", WSConstants.SAML11_NAMESPACE);
        thisContext.addNamespace("dsig", WSConstants.DSIG_NAMESPACE);
        Nodes nodes = assertion.query("//dsig:X509Data/dsig:X509Certificate", thisContext);
        if ((nodes != null) && (nodes.size() > 0)) {
        	String element = nodes.get(0).getValue();
            StringBuffer sb = new StringBuffer("-----BEGIN CERTIFICATE-----\n");
            sb.append(element);
            sb.append("\n-----END CERTIFICATE-----\n");

        	ByteArrayInputStream bis = new ByteArrayInputStream(sb.toString().getBytes());
			CertificateFactory cf = CertificateFactory.getInstance("X509");
			X509Certificate cert = (X509Certificate)cf.generateCertificate(bis);
        	return cert;
        } else {
        	return null;
        }
    }

    public static String validateCertificate(Document assertion) 
     throws CertificateException
    {
    	try {
    		X509Certificate cert = getCertificate(assertion);
    		if (cert != null) {
    			cert.checkValidity();
    			return "is valid";
    		} else {
    			return "is missing"; // if it is not there then it is invalid
    		}
    	} catch (CertificateExpiredException e) {
    		return "has expired";
    	} catch (CertificateNotYetValidException e) {
    		return "is not yet valid";
    	}
    }

    public static boolean validate(Document assertion) throws CryptoException {

        //OK - on to signature validation - we need to get the SignedInfo Element, and the Signature Element
        XPathContext thisContext = new XPathContext();
        thisContext.addNamespace("saml", WSConstants.SAML11_NAMESPACE);
        thisContext.addNamespace("dsig", WSConstants.DSIG_NAMESPACE);

        Nodes signedInfoVals = assertion.query("/saml:Assertion/dsig:Signature/dsig:SignedInfo", thisContext);
        Element signedInfo = (Element) signedInfoVals.get(0);
        byte[] signedInfoCanonicalBytes = null;

        try {

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            Canonicalizer outputter = new Canonicalizer(stream, Canonicalizable.EXCLUSIVE_CANONICAL_XML);
            outputter.write(signedInfo);
            signedInfoCanonicalBytes = stream.toByteArray();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

        Nodes signatureValues = assertion.query("//dsig:SignatureValue", thisContext);
        Element signatureValueElm = (Element) signatureValues.get(0);
        String signatureValue = signatureValueElm.getValue();


        thisContext = new XPathContext();
        thisContext.addNamespace("saml", WSConstants.SAML11_NAMESPACE);
        thisContext.addNamespace("dsig", WSConstants.DSIG_NAMESPACE);

        //And we need to fetch the modulus
        //Nodes modVals = assertion.query("/saml:Assertion/dsig:Signature/dsig:KeyInfo/disg:KeyValue/dsig:RSAKeyValue/dsig:Modulus", thisContext);
        Nodes modVals = assertion.query("//dsig:Modulus", thisContext);
        Element modulusElm = (Element) modVals.get(0);
        String mod = modulusElm.getValue();
        //System.out.println("Modulus: " + mod);

        //And we need to fetch the exponent
        Nodes expVals = assertion.query("//dsig:Exponent", thisContext);
        Element expElm = (Element) expVals.get(0);
        String exp = expElm.getValue();
        //System.out.println("Exponent: " + exp);

        //GET THE KEY CIPHERTEXT and DECRYPT
        XPathContext encContext = new XPathContext();
        encContext.addNamespace("enc", WSConstants.ENC_NAMESPACE);
        encContext.addNamespace("dsig", WSConstants.DSIG_NAMESPACE);
        Nodes digestValues = assertion.query("//dsig:DigestValue", encContext);
        Element digestValue = (Element) digestValues.get(0);
        String digest = digestValue.getValue();

        // WEVE GOT:
        // byte[] signedInfoCanonicalBytes
        // String signatureValue
        // byte[] digestBytes
        // String digest
        // String mod
        // String exp

        //WE now have the digest, and the signing key.   Let's validate the REFERENCES:

        //REMOVE the siganture element
        Element root = assertion.getRootElement();
        Element signature = root.getFirstChildElement("Signature", WSConstants.DSIG_NAMESPACE);
        //System.out.println(signature.toXML());
        root.removeChild(signature);

        //GET the canonical bytes of the assertion
        byte[] assertionCanonicalBytes = null;

        try {

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            Canonicalizer outputter = new Canonicalizer(stream, Canonicalizable.EXCLUSIVE_CANONICAL_XML);
            outputter.write(root);
            assertionCanonicalBytes = stream.toByteArray();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

        //WE've got the canonical without the signature.
        // Let's calculate the Digest to validate the references
        String b64EncodedDigest = null;
        try {
            b64EncodedDigest = CryptoUtils.digest(assertionCanonicalBytes);
        } catch (org.xmldap.exceptions.CryptoException e) {
            e.printStackTrace();
        }

        if (!digest.equals(b64EncodedDigest)) {

            System.out.println("Digest of the Reference did not match the provided Digest.  Exiting.");
            return false;

        }

        // WEVE GOT:
        // byte[] signedInfoCanonicalBytes
        // String signatureValue
        // byte[] clearTextKey
        // byte[] digestBytes
        // String digest
        // String mod
        // String exp

        BigInteger modulus = new BigInteger(Base64.decode(mod));
        BigInteger exponent = new BigInteger(Base64.decode(exp));
        return CryptoUtils.verify(signedInfoCanonicalBytes, Base64.decode(signatureValue), modulus, exponent);

    }

    /**
     * test ValidationUtil by validating a digest and signature in a SAML assertion contained in a file.
     * @param args an array of Strings, in which arg[0] is a filename of an input file
     */
    public static void main(String []args) throws Exception {
	String fn = args[0];
	FileInputStream fis = new FileInputStream(fn);
	int avail = fis.available();
	byte []b = new byte[avail];
	fis.read(b);
	fis.close();
	String decstr = new String(b,"UTF-8");

	Builder parser = new Builder();
	Document assertion = parser.build(decstr,"");
	
	ValidationUtil validator = new ValidationUtil();
	boolean verified = validator.validate(assertion);
	if (!verified) {
	    System.err.println("FAIL");
	    System.exit(1);
	}
	System.exit(0);
    }
}
