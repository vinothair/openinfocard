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

import net.sourceforge.lightcrypto.SafeObject;
import nu.xom.*;
import org.xmldap.crypto.CryptoUtils;
import org.xmldap.exceptions.KeyStoreException;
import org.xmldap.exceptions.SerializationException;
import org.xmldap.exceptions.SigningException;
import org.xmldap.saml.AttributeStatement;
import org.xmldap.saml.Conditions;
import org.xmldap.saml.SAMLAssertion;
import org.xmldap.saml.Subject;
import org.xmldap.util.Base64;
import org.xmldap.util.KeystoreUtil;
import org.xmldap.ws.WSConstants;
import org.xmldap.xmldsig.AysmmetricKeyInfo;
import org.xmldap.xmldsig.EnvelopedSignature;
import org.xmldap.xmlenc.EncryptedData;

import java.io.IOException;
import java.security.interfaces.RSAPrivateKey;


public class DecryptUtil {

    private KeystoreUtil keystore;

    public DecryptUtil(KeystoreUtil keystore) {
        this.keystore = keystore;
    }


    private Document parse(String xml) {

        Builder parser = new Builder();
        Document doc = null;
        try {
            doc = parser.build(xml, "");

            //TODO - improve error handling
        } catch (ParsingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return doc;

    }


    public StringBuffer decryptXML(String encryptedXML, String alias, String password) {

        Document xml = parse(encryptedXML);

        XPathContext context = new XPathContext();
        context.addNamespace("enc", WSConstants.ENC_NAMESPACE);
        context.addNamespace("dsig", WSConstants.DSIG_NAMESPACE);
        context.addNamespace("wsse", WSConstants.WSSE_NAMESPACE_OASIS_10);

        //Nodes fingerprints = xml.query("/enc:EncryptedData/dsig:KeyInfo/enc:EncryptedKey/dsig:KeyInfo/wsse:SecurityTokenReference/wsse:KeyIdentifier",context);
        Nodes fingerprints = xml.query("//wsse:KeyIdentifier", context);
        Element fingerprintElm = (Element) fingerprints.get(0);
        String fingerprint = fingerprintElm.getValue();
        byte[] fingerPrintBytes = Base64.decode(fingerprint);
        //System.out.println("Fingerprint: " + CryptoUtils.byteArrayToHexString(fingerPrintBytes));


        Nodes keys = xml.query("/enc:EncryptedData/dsig:KeyInfo/enc:EncryptedKey/enc:CipherData/enc:CipherValue", context);
        Element cipherValue = (Element) keys.get(0);
        String keyCipherText = cipherValue.getValue();
        //System.out.println("Key Cipher Text: " + keyCipherText);
        //System.out.println("Key Bytes: " + Base64.decode(keyCipherText).length);


        Nodes dataNodes = xml.query("/enc:EncryptedData/enc:CipherData/enc:CipherValue", context);
        Element dataCipherValue = (Element) dataNodes.get(0);
        String dataCipherText = dataCipherValue.getValue();
        //System.out.println("Data Cipher Text: " + dataCipherText);


        RSAPrivateKey key = null;
        try {
            key = (RSAPrivateKey) keystore.getPrivateKey(alias, password);

        } catch (KeyStoreException e) {
            e.printStackTrace();
        }
        byte[] clearTextKey = null;
        try {
            clearTextKey = CryptoUtils.decryptRSAOAEP(keyCipherText, key);
        } catch (org.xmldap.exceptions.CryptoException e) {
            e.printStackTrace();
        }


        SafeObject keyBytes = new SafeObject();
        try {
            keyBytes.setText(clearTextKey);
        } catch (Exception e) {
            e.printStackTrace();
        }
        StringBuffer clearTextBuffer = new StringBuffer(dataCipherText);

        StringBuffer clearText = null;
        try {
            clearText = CryptoUtils.decryptAESCBC(clearTextBuffer, keyBytes);
        } catch (org.xmldap.exceptions.CryptoException e) {
            e.printStackTrace();
        }
        return clearText;

    }


    public static void main(String[] args) {

        //Get my keystore
        KeystoreUtil myKeystore = null;
        try {
            myKeystore = new KeystoreUtil("/Users/cmort/build/infocard/conf/xmldap.org.jks", "storepassword");
        } catch (KeyStoreException e) {
            e.printStackTrace();
        }


        Conditions conditions = new Conditions(10);

        org.xmldap.xmldsig.AysmmetricKeyInfo keyInfo = new AysmmetricKeyInfo(myKeystore, "Server-Cert");
        Subject subject = new Subject(keyInfo);
        org.xmldap.saml.Attribute given = new org.xmldap.saml.Attribute("GivenName", "http://schemas.microsoft.com/ws/2005/05/identity/claims/givenname", "Chuck");
        org.xmldap.saml.Attribute sur = new org.xmldap.saml.Attribute("Surname", "http://schemas.microsoft.com/ws/2005/05/identity/claims/surname", "Mortimore");
        org.xmldap.saml.Attribute email = new org.xmldap.saml.Attribute("EmailAddress", "http://schemas.microsoft.com/ws/2005/05/identity/claims/emailaddress", "cmortspam@gmail.com");
        AttributeStatement statement = new AttributeStatement();
        statement.setSubject(subject);
        statement.addAttribute(given);
        statement.addAttribute(sur);
        statement.addAttribute(email);

        SAMLAssertion assertion = new SAMLAssertion();
        assertion.setConditions(conditions);
        assertion.setAttributeStatement(statement);

        String encryptedXML = "";


        EnvelopedSignature signer = new EnvelopedSignature(myKeystore, "Server-Cert", "keypassword");
        EncryptedData encrypted = new EncryptedData(myKeystore, "Server-Cert");

        try {
            Element signedXML = signer.sign(assertion.serialize());
            encrypted.setData(signedXML.toXML());
            encryptedXML = encrypted.toXML();
        } catch (SigningException e) {
            e.printStackTrace();
        } catch (SerializationException e) {
            e.printStackTrace();
        }


        System.out.println("Encrypted: ");


        try {
            Serializer serializer = new Serializer(System.out, "ISO-8859-1");
            serializer.setIndent(4);
            serializer.setMaxLength(64);
            serializer.setPreserveBaseURI(true);
            serializer.write(new Document(encrypted.serialize()));
            serializer.flush();
        } catch (IOException ex) {
            System.out.println(
                    "Due to an IOException, the parser could not check "
                            + args[0]
            );
        } catch (SerializationException e) {
            e.printStackTrace();
        }


        DecryptUtil decrypt = new DecryptUtil(myKeystore);
        StringBuffer bufferClearText = decrypt.decryptXML(encryptedXML, "Server-Cert", "keypassword");
        System.out.println(bufferClearText);


    }


}
