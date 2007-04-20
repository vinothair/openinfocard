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

package org.xmldap.xmldsig;

import nu.xom.*;
import nu.xom.canonical.Canonicalizer;
import org.xmldap.crypto.CryptoUtils;
import org.xmldap.exceptions.CryptoException;
import org.xmldap.exceptions.SerializationException;
import org.xmldap.exceptions.SigningException;
import org.xmldap.ws.WSConstants;
import org.xmldap.xml.Canonicalizable;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.PrivateKey;


public class BaseEnvelopedSignature {

//    private X509Certificate signingCert;
    protected PrivateKey privateKey;
	protected KeyInfo keyInfo;
	
//    public EnvelopedSignature(X509Certificate signingCert, PrivateKey privateKey) {
//        this.privateKey = privateKey;
//        this.signingCert = signingCert;
//    }
    public BaseEnvelopedSignature(KeyInfo keyInfo, PrivateKey privateKey) {
        this.keyInfo = keyInfo;
        this.privateKey = privateKey;
    }

    protected BaseEnvelopedSignature() {}
    
	/**
	 * @param xml
	 * @param nodesToReference
	 * @throws SigningException
	 */
	public Element sign(Element xml) throws SigningException {
        Element signThisOne = (Element) xml.copy();

        String idVal = signThisOne.getAttributeValue("Id", WSConstants.WSSE_OASIS_10_WSU_NAMESPACE);
		Reference reference = new Reference(signThisOne, idVal);
		
        //Get SignedInfo for reference
        SignedInfo signedInfo = new SignedInfo(reference);

        Signature signature = getSignatureValue(signedInfo);

        //Envelope it.
        try {
        	signThisOne.appendChild(signature.serialize());
        } catch (SerializationException e) {
            throw new SigningException("Could not create enveloped signature due to serialization error", e);
        }
        return signThisOne;
	}

	/**
	 * @param signedInfo
	 * @return
	 */
	protected Signature getSignatureValue(SignedInfo signedInfo) {
		//Get sigvalue for the signedInfo
        SignatureValue signatureValue = new SignatureValue(signedInfo, privateKey);

        //Get KeyInfo
//        KeyInfo keyInfo = new AsymmetricKeyInfo(signingCert);


        //Create the signature block
        Signature signature = new Signature(signedInfo, signatureValue, keyInfo);
		return signature;
	}

	public static byte[] getSignedInfoCanonicalBytes(Element root) throws IOException {
//		Nodes signedInfoVals = assertion.query("//dsig:Signature/dsig:SignedInfo", thisContext);
//		Element signedInfo = (Element) signedInfoVals.get(0);
		// Axel Nennker: removed the dependency to saml:Assertion
		// The following lines get the "SignedInfo". 
		// This way the ValidationUtils can be used to validate other signed XML too.
		Element signature = root.getFirstChildElement("Signature", WSConstants.DSIG_NAMESPACE);
		Element signedInfo = signature.getFirstChildElement("SignedInfo", WSConstants.DSIG_NAMESPACE);

		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		Canonicalizer outputter = new Canonicalizer(stream,
				Canonicalizable.EXCLUSIVE_CANONICAL_XML);
		outputter.write(signedInfo);
		byte[] bytes = stream.toByteArray();
		stream.close();

		return bytes;
	}

	protected static byte[] getAssertionCanonicalBytes(Element root) throws IOException {
		// REMOVE the siganture element
		Element signature = root.getFirstChildElement("Signature",
				WSConstants.DSIG_NAMESPACE);
		// System.out.println(signature.toXML());
		root.removeChild(signature);

		// GET the canonical bytes of the assertion
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		Canonicalizer outputter = new Canonicalizer(stream,
				Canonicalizable.EXCLUSIVE_CANONICAL_XML);
		outputter.write(root);
		byte[] assertionCanonicalBytes = stream.toByteArray();
		stream.close();
		return assertionCanonicalBytes;
	}

	/**
	 * @param root
	 * @return
	 * @throws CryptoException
	 */
	protected static String digestElement(Element root) throws CryptoException {
		String b64EncodedDigest = null;

		byte[] assertionCanonicalBytes;
		try {
			assertionCanonicalBytes = getAssertionCanonicalBytes(root);
		} catch (IOException e) {
			throw new CryptoException(e);
		}

		// WE've got the canonical without the signature.
		// Let's calculate the Digest to validate the references
		try {
			b64EncodedDigest = CryptoUtils.digest(assertionCanonicalBytes);
		} catch (CryptoException e) {
			throw new CryptoException(e);
		}
		return b64EncodedDigest;
	}

}
