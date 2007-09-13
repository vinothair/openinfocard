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

package org.xmldap.infocard.roaming;

import org.xmldap.util.Base64;
import org.xmldap.util.XmlFileUtil;
import org.xmldap.util.XSDDateTime;
import org.xmldap.util.KeystoreUtil;
import org.xmldap.crypto.CryptoUtils;
import org.xmldap.crypto.EncryptedStoreKeys;
import org.xmldap.ws.WSConstants;
import org.xmldap.exceptions.*;
import org.xmldap.infocard.policy.SupportedToken;
import org.xmldap.infocard.policy.SupportedClaimList;
import org.xmldap.infocard.policy.SupportedTokenList;
import org.xmldap.infocard.policy.SupportedClaim;
import org.xmldap.infocard.TokenServiceReference;
import org.xmldap.infocard.InfoCard;
import org.xmldap.infocard.Constants;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.crypto.engines.AESLightEngine;
import org.bouncycastle.crypto.modes.CBCBlockCipher;
import org.bouncycastle.crypto.BufferedBlockCipher;
import org.bouncycastle.crypto.paddings.PaddedBufferedBlockCipher;

import java.io.*;
import java.util.Random;
import java.util.Arrays;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;

import net.sourceforge.lightcrypto.SafeObject;
import nu.xom.*;
import nu.xom.ParsingException;


public class EncryptedStore {

    private static byte[] bom = {(byte)0xEF, (byte)0xBB, (byte)0xBF};


    public Document decryptStore(String encryptedStoreString, String password) throws CryptoException, ParsingException{

        int index = encryptedStoreString.indexOf("<?xml");
        String data = encryptedStoreString.substring(index);

        Builder parser = new Builder();
        Document encryptedStore = null;
        try {
            encryptedStore = parser.build(data, "");
        } catch (IOException e) {
            e.printStackTrace();
            throw new ParsingException("Error parsing", e);
        }

        return decryptStore(encryptedStore, password);

    }


    public Document decryptStore(InputStream encryptedStoreStream, String password) throws CryptoException, ParsingException{

        Document encryptedStore = null;
        try {
            encryptedStore = XmlFileUtil.readXml(encryptedStoreStream);
        } catch (IOException e) {
            throw new ParsingException("Error parsing EncryptedStore", e);
        }

        return decryptStore(encryptedStore, password);


    }

    public Document decryptStore(Document encryptedStore, String password) throws CryptoException, ParsingException{



        XPathContext context = new XPathContext();
        context.addNamespace("id","http://schemas.xmlsoap.org/ws/2005/05/identity");
        context.addNamespace("enc","http://www.w3.org/2001/04/xmlenc#");

        Nodes saltNodes = encryptedStore.query("//id:StoreSalt",context);
        Element saltElm = (Element) saltNodes.get(0);

        Nodes cipherValueNodes = encryptedStore.query("//enc:CipherValue",context);
        Element cipherValueElm = (Element) cipherValueNodes.get(0);


        String roamingStoreString =  decrypt(cipherValueElm.getValue(), password, saltElm.getValue());

        //let's make a doc
        Builder parser = new Builder();
        Document roamingStore = null;
        try {
            roamingStore = parser.build(roamingStoreString, "");
        } catch (ParsingException e) {
            e.printStackTrace();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return roamingStore;

    }


    private String decrypt(String cipherText, String password, String salt) throws CryptoException{


        EncryptedStoreKeys keys = new EncryptedStoreKeys(password,Base64.decode(salt));

        byte[] cipherBytes = Base64.decode(cipherText);
        byte[] iv = new byte[16];
        byte[] integrityCode = new byte[32];
        byte[] data = new byte[cipherBytes.length - iv.length - integrityCode.length];
        byte[] ivPlusData = new byte[cipherBytes.length  - integrityCode.length];

        //Copy IV
        System.arraycopy(cipherBytes, 0, iv, 0, 16);

        //Copy integrityCode
        System.arraycopy(cipherBytes, 16, integrityCode, 0, 32);

        //Copy data
        System.arraycopy(cipherBytes, 48, data, 0, data.length);

        //Copy iv and data
        System.arraycopy(iv, 0, ivPlusData, 0, 16);
        System.arraycopy(data, 0, ivPlusData, 16, data.length);


        SafeObject keyBytes = new SafeObject();
        try {
            keyBytes.setText(keys.getEncryptionKey());
        } catch (Exception e) {
            throw new CryptoException("Error Parsing Roaming Store", e);
        }


        StringBuffer clearText = null;

        clearText = CryptoUtils.decryptAESCBC(new StringBuffer(Base64.encodeBytesNoBreaks(ivPlusData)), keyBytes);

        byte[] hashedIntegrityCode = getHashedIntegrityCode(iv, keys.getIntegrityKey(),  clearText.toString());
        boolean valid = Arrays.equals(integrityCode, hashedIntegrityCode);
        if (!valid) {
            throw new CryptoException("The cardstore did not pass the integrity check - it may have been tampered with");
        }

        //get rid of the byte order mark
        int start = clearText.indexOf("<RoamingStore");
        return clearText.substring(start);

    }


    public void encryptStore(RoamingStore roamingStore, String password, OutputStream output) throws CryptoException {


        Element encryptedStore = new Element("EncryptedStore", WSConstants.INFOCARD_NAMESPACE);
        Element storeSalt = new Element("StoreSalt", WSConstants.INFOCARD_NAMESPACE);
        Random rand = new Random();
        byte[] salt = new byte[16];
        rand.nextBytes(salt);

        storeSalt.appendChild(Base64.encodeBytesNoBreaks(salt));
        encryptedStore.appendChild(storeSalt);
        Element encryptedData = new Element("EncryptedData", WSConstants.ENC_NAMESPACE);
        Element cipherData = new Element("CipherData", WSConstants.ENC_NAMESPACE);
        Element cipherValue = new Element("CipherValue", WSConstants.ENC_NAMESPACE);

        encryptedStore.appendChild(encryptedData);
        encryptedData.appendChild(cipherData);
        cipherData.appendChild(cipherValue);
        cipherValue.appendChild(encrypt(roamingStore, password, salt));

        try {
            output.write(bom);
            output.write("<?xml version=\"1.0\" encoding=\"utf-8\"?>".getBytes("UTF8"));
            output.write(encryptedStore.toXML().getBytes("UTF8"));

        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    public String encrypt(RoamingStore roamingStore, String password, byte[] salt) throws CryptoException{


        EncryptedStoreKeys keys = new EncryptedStoreKeys(password,salt);

        Random rand = new Random();
        byte[] iv = new byte[16];
        rand.nextBytes(iv);

        String dataString = null;
        try {
            dataString = roamingStore.toXML();
        } catch (SerializationException e) {
            throw new CryptoException("Error getting RoamingStore XML", e);
        }

        byte[] integrityCode = getHashedIntegrityCode(iv, keys.getIntegrityKey(),dataString);

        ByteArrayOutputStream dataBytes = new ByteArrayOutputStream();
        try {
            dataBytes.write(bom);
            dataBytes.write(dataString.getBytes("UTF8"));
        } catch (IOException e) {
            e.printStackTrace();
        }


        //encrypt
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            AESLightEngine aes = new AESLightEngine();
            CBCBlockCipher cbc = new CBCBlockCipher(aes);
            BufferedBlockCipher cipher = new PaddedBufferedBlockCipher(cbc);
            KeyParameter key = new KeyParameter(keys.getEncryptionKey());
            ParametersWithIV paramWithIV = new ParametersWithIV(key, iv);
            byte inputBuffer[] = new byte[16];
            byte outputBuffer[] = new byte[16];
            int bytesProcessed = 0;
            cipher.init(true, paramWithIV);
            int bytesRead = 0;
            ByteArrayInputStream inputStream = new ByteArrayInputStream(dataBytes.toByteArray());
            while((bytesRead = inputStream.read(inputBuffer)) > 0)  {
                bytesProcessed = cipher.processBytes(inputBuffer, 0, bytesRead, outputBuffer, 0);
                if(bytesProcessed > 0)  outputStream.write(outputBuffer, 0, bytesProcessed);
            }
            bytesProcessed = cipher.doFinal(outputBuffer, 0);
            if(bytesProcessed > 0) outputStream.write(outputBuffer, 0, bytesProcessed);
        } catch(Exception e) {
            throw new CryptoException ("Error encrypting data", e);
        }

        byte[] cipherText = outputStream.toByteArray();


        //append iv + integrityCode + cipherText
        byte[] blob = new byte[ 48 + cipherText.length];
        System.arraycopy(iv, 0, blob, 0, 16);
        System.arraycopy(integrityCode, 0, blob, 16, 32);
        System.arraycopy(cipherText, 0, blob, 48, cipherText.length);

        //Base64 encode and return
        return Base64.encodeBytesNoBreaks(blob);
    }




    private byte[] getHashedIntegrityCode(byte[] iv, byte[] integrityKey,  String clearText) {

        byte[] clearBytes = new byte[0];
        try {
            clearBytes = clearText.getBytes("UTF8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        byte[] lastBlock = new byte[16];
        System.arraycopy(clearBytes,clearBytes.length - 16 ,lastBlock, 0, 16);


        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {

            e.printStackTrace();
        }


        byte[] integrityCheck = new byte[64];
        System.arraycopy(iv, 0, integrityCheck, 0, 16);
        System.arraycopy(integrityKey, 0, integrityCheck, 16, 32);
        System.arraycopy(lastBlock, 0, integrityCheck, 48, 16);
        digest.update(integrityCheck);
        return digest.digest();

    }


    public static void main(String[] args) {

        String  password = "password";

        EncryptedStore encryptedStore = new EncryptedStore();
        Document roamingStore = null;
        try {
            InputStream stream =  new FileInputStream("/Users/cmort/Desktop/Infocard/CardBackups/backup.crds");
            roamingStore = encryptedStore.decryptStore(stream, password);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(roamingStore.toXML());

        RoamingStore store = new RoamingStore();


        //Get my keystore
        KeystoreUtil keystore = null;
        try {
            keystore = new KeystoreUtil("/Users/cmort/xmldap_files/xmldap_org.jks", "password");
        } catch (KeyStoreException e) {
            e.printStackTrace();
        }

        X509Certificate cert = null;
        try {
            cert = keystore.getCertificate("tomcat");
        } catch (KeyStoreException e) {
            e.printStackTrace();
        }


        InfoCard card = new InfoCard();
        card.setCardId("https://xmldap.org/cards/123456");
        card.setCardName("Custom card");
        card.setCardVersion(1);
        card.setIssuerName("xmldap.org");
        card.setIssuer("http://xmldap.org/");
        XSDDateTime issued = new XSDDateTime();
        XSDDateTime expires = new XSDDateTime(525600);

        card.setTimeIssued(issued.getDateTime());
        card.setTimeExpires(expires.getDateTime());

        TokenServiceReference tsr = new TokenServiceReference("http://xmldap.org/sts/tokenservice", "https://xmldap.org/sts/mex", cert);
        tsr.setUserName("cmort");
        card.setTokenServiceReference(tsr);


        SupportedTokenList tokenList = new SupportedTokenList();
        SupportedToken token = new SupportedToken(SupportedToken.SAML11);
        tokenList.addSupportedToken(token);
        card.setTokenList(tokenList);

        SupportedClaimList claimList = new SupportedClaimList();
        SupportedClaim given = new SupportedClaim("GivenName", Constants.IC_NAMESPACE_PREFIX + "givenname", "Your given name.");
        SupportedClaim sur = new SupportedClaim("Surname", Constants.IC_NAMESPACE_PREFIX + "surname", "Your surname.");
        SupportedClaim email = new SupportedClaim("EmailAddress", Constants.IC_NAMESPACE_PREFIX + "emailaddress", "Your Email-Address");
        claimList.addSupportedClaim(given);
        claimList.addSupportedClaim(sur);
        claimList.addSupportedClaim(email);
        card.setClaimList(claimList);

        card.setPrivacyPolicy("https://xmldap.org/PrivacyPolicy.xml");

        //Don't want the sig
        card.setSignCard(false);


        RoamingInformationCard ric = new RoamingInformationCard(card);
        store.addRoamingInformationCard(ric);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream("/Users/cmort/Desktop/Infocard/CardBackups/ManualBackup.crds");
            encryptedStore.encryptStore(store, password, fos);
        } catch (Exception e) {
            e.printStackTrace();
        }


        EncryptedStore encryptedStore1 = new EncryptedStore();
        Document roamingStore1 = null;
        try {
            InputStream stream =  new FileInputStream("/Users/cmort/Desktop/Infocard/CardBackups/ManualBackup.crds");
            roamingStore1 = encryptedStore1.decryptStore(stream, password);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (ParsingException e) {
            e.printStackTrace();
        } catch (CryptoException e) {
            e.printStackTrace();
        }
        System.out.println(roamingStore1.toXML());

    }

}
