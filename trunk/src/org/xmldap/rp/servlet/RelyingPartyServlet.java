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

package org.xmldap.rp.servlet;

import nu.xom.*;
import nu.xom.canonical.Canonicalizer;
import org.xmldap.crypto.CryptoUtils;
import org.xmldap.exceptions.KeyStoreException;
import org.xmldap.util.Base64;
import org.xmldap.util.KeystoreUtil;
import org.xmldap.ws.WSConstants;
import org.xmldap.xml.Canonicalizable;
import org.xmldap.xmlenc.DecryptUtil;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.interfaces.RSAPrivateKey;


public class RelyingPartyServlet extends HttpServlet {

    private KeystoreUtil keystore = null;

    public void init(ServletConfig config) throws ServletException {

        keystore = null;
        try {
            keystore = new KeystoreUtil("/export/home/cmort/xmldap.org.jks", "storepassword");
            //keystore = new KeystoreUtil("/Users/cmort/build/infocard/conf/xmldap.org.jks", "storepassword");
        } catch (KeyStoreException e) {
            throw new ServletException(e);
        }


    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException {


        try {
            response.setContentType("text/html");
            ServletOutputStream out = response.getOutputStream();

            String encryptedXML = request.getParameter("xmlToken");


            if (encryptedXML == null) {

                out.println("Sorry - you'll need to POST a security token.");
                return;

            }

            //We've got the XML
            out.println("<div  style=\"font-family: Helvetica;\"><h2>Here's what you posted:</h2>");

            //First, let's get the posted data:
            out.println("<p><textarea rows='10' cols='150'>" + encryptedXML + "</textarea></p>");

            //Now decrypt it.
            DecryptUtil decrypter = new DecryptUtil(keystore);
            StringBuffer decryptedXML = decrypter.decryptXML(encryptedXML, "Server-Cert", "keypassword");

            //System.out.println("-----BEGIN POST------");
            //System.out.println(decryptedXML.toString());
            //System.out.println("----- END POST ------");

            out.println("<h2>And here's the decrypted token:</h2>");

            //Turn it into a doc
            Builder parser = new Builder();
            Document assertion = parser.build(decryptedXML.toString(), "");
            out.println("<p><textarea rows='10' cols='150'>" + decryptedXML + "</textarea></p>");

            //PRETTY PRINT IT
            Serializer serializer = new Serializer(System.out);
            serializer.setIndent(4);
            serializer.setMaxLength(64);
            try {
                serializer.write(assertion);
            }
            catch (IOException ex) {
                System.err.println(ex);
            }

            //System.out.println("----- END POST ------");

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

            //System.out.println("Canonical SignedInfo: " + new String(signedInfoCanonicalBytes));

            Nodes signatureValues = assertion.query("//dsig:SignatureValue", thisContext);
            Element signatureValueElm = (Element) signatureValues.get(0);
            String signatureValue = signatureValueElm.getValue();
            //System.out.println("Signature Value B64: " + signatureValue);
            //System.out.println("Signature Value Hex: " + CryptoUtils.byteArrayToHexString(Base64.decode(signatureValue)));


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

            Nodes cipherValues = assertion.query("//enc:CipherValue", encContext);
            Element cipherValue = (Element) cipherValues.get(0);
            String cipherText = cipherValue.getValue();


            RSAPrivateKey key = null;
            try {
                key = (RSAPrivateKey) keystore.getPrivateKey("Server-Cert", "keypassword");

            } catch (KeyStoreException e) {
                e.printStackTrace();
            }
            byte[] clearTextKey = CryptoUtils.decryptRSAOAEP(cipherText, key);

            //System.out.println("Encrypted Key from Assertion: " + CryptoUtils.byteArrayToHexString(clearTextKey) + " : " + 8 * clearTextKey.length);

            //GET THE DIGEST VALUE
            XPathContext dsigContext = new XPathContext();
            encContext.addNamespace("dsig", WSConstants.DSIG_NAMESPACE);

            Nodes digestValues = assertion.query("//dsig:DigestValue", encContext);
            Element digestValue = (Element) digestValues.get(0);
            String digest = digestValue.getValue();
            //System.out.println("Base64 encoded digest: " + digest);
            byte[] digestBytes = Base64.decode(digest);
            //System.out.println("Digest: " + CryptoUtils.byteArrayToHexString(digestBytes));

            // WEVE GOT:
            // byte[] signedInfoCanonicalBytes
            // String signatureValue
            // byte[] clearTextKey
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

            //System.out.println("canonical: " + new String(assertionCanonicalBytes));

            //WE've got the canonical without the signature.
            // Let's calculate the Digest to validate the references
            String b64EncodedDigest = null;
            try {
                b64EncodedDigest = CryptoUtils.digest(assertionCanonicalBytes);
            } catch (org.xmldap.exceptions.CryptoException e) {
                e.printStackTrace();
            }

            if (!digest.equals(b64EncodedDigest)) {

                //System.out.println("FINAL DIGEST----->" + digest);
                //System.out.println("FINAL BYTES ----->" + digestBytes);
                //System.out.println("Calcu DIGEST----->" + b64EncodedDigest);

                out.println("Digest of the Reference did not match the provided Digest.  Exiting.");
                return;

            }

            //System.out.println("Base64 encoded calculated digest: " +  b64EncodedDigest);
            //System.out.println("Calculated digest: " +  CryptoUtils.byteArrayToHexString(Base64.decode(b64EncodedDigest)));

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
            boolean verified = CryptoUtils.verify(signedInfoCanonicalBytes, Base64.decode(signatureValue), modulus, exponent);

            if (verified) {


                out.println("<h2>Your signature was verified</h2>");


            } else {

                out.println("Signature validation failed!   Exiting");
                return;

            }


            out.println("<h2>You provided the following claims:</h2>");
            XPathContext context = new XPathContext();
            context.addNamespace("saml", WSConstants.SAML11_NAMESPACE);
            Nodes claims = assertion.query("/saml:Assertion/saml:AttributeStatement/saml:Attribute", context);
            for (int i = 0; i < claims.size(); i++) {

                Element claim = (Element) claims.get(i);
                Attribute nameAttr = claim.getAttribute("AttributeName");
                String name = nameAttr.getValue();
                Element valueElm = claim.getFirstChildElement("AttributeValue", WSConstants.SAML11_NAMESPACE);
                String value = valueElm.getValue();

                //if (name.equals("PrivatePersonalIdentifier")) value = new String(Base64.decode(value));
                out.println(name + ": " + value + "<br>");

            }

            out.println("<br><a href='mailto:charliemortimore@gmail.com'>Please drop me a line!</a></div>");


        } catch (IOException e) {
            throw new ServletException(e);
        } catch (ValidityException e) {
            e.printStackTrace();
        } catch (ParsingException e) {
            e.printStackTrace();
        } catch (org.xmldap.exceptions.CryptoException e) {
            e.printStackTrace();
        }


    }


}
