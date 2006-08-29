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

package org.xmldap.crypto;

import net.sourceforge.lightcrypto.Crypt;
import net.sourceforge.lightcrypto.SafeObject;
import org.bouncycastle.crypto.AsymmetricBlockCipher;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.encodings.OAEPEncoding;
import org.bouncycastle.crypto.engines.RSAEngine;
import org.bouncycastle.crypto.params.RSAKeyParameters;
import org.xmldap.exceptions.CryptoException;
import org.xmldap.util.Base64;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.io.IOException;
import java.math.BigInteger;
import java.security.*;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;


/**
 * CryptUtils provides a number of general purpose crypto utilities.  Digests, encrypt, decrupt, etc
 *
 * @author charliemortimore at gmail.com
 */
public class CryptoUtils {


    /**
     * Creates a Base64 encoded SHA Digest of a byte[]
     *
     * @param data the data to digest
     * @return Base64 encoded digest of the data
     * @throws CryptoException
     */
    public static String digest(byte[] data) throws CryptoException {


        MessageDigest md;
        try {
            md = MessageDigest.getInstance("SHA");
        } catch (NoSuchAlgorithmException e) {
            throw new CryptoException(e);
        }
        md.reset();
        md.update(data);
        byte[] digest = md.digest();


        return Base64.encodeBytesNoBreaks(digest);

    }

    /**
     * Encrypts data using AES with a Chained Block Cipber.   Got rid of my own impl, and wrapped lightcrypto
     *
     * @param text     The data to encrypt
     * @param keybytes the key
     * @return the cipherText in a Stringbuffer
     * @throws CryptoException
     */
    public static StringBuffer encryptAESCBC(StringBuffer text, SafeObject keybytes) throws CryptoException {

        StringBuffer cipherText;

        try {
            cipherText = Crypt.encrypt(text, keybytes);
        } catch (net.sourceforge.lightcrypto.CryptoException e) {

            throw new CryptoException(e);
        } catch (IOException e) {

            throw new CryptoException(e);
        }

        return cipherText;

    }


    /**
     * Decryptes AES with a Chained Block Cipher - wraps lightcrypto
     *
     * @param text     The cipher text
     * @param keybytes the decryptiong key
     * @return clearText in a StringBuffer
     * @throws CryptoException
     */
    public static StringBuffer decryptAESCBC(StringBuffer text, SafeObject keybytes) throws CryptoException {

        StringBuffer clearText;

        try {
            clearText = Crypt.decrypt(text, keybytes);
        } catch (net.sourceforge.lightcrypto.CryptoException e) {

            throw new CryptoException(e);
        } catch (IOException e) {

            throw new CryptoException(e);
        }

        return clearText;

    }


    /**
     * Encrypts using RSA with OAEP
     *
     * @param input the clear text to encrypt
     * @param cert  the certificate to use for encryption
     * @return the base64 encoded cipher text
     * @throws CryptoException
     */
    public static String rsaoaepEncrypt(byte[] input, X509Certificate cert) throws CryptoException {



        AsymmetricBlockCipher engine = new RSAEngine();
        OAEPEncoding cipher = new OAEPEncoding(engine);

        //populate modulus
        RSAPublicKey key = (RSAPublicKey) cert.getPublicKey();
        BigInteger mod = key.getModulus();
        BigInteger exp = key.getPublicExponent();
        RSAKeyParameters keyParams = new RSAKeyParameters(false, mod, exp);
        cipher.init(true, keyParams);

        int inputBlockSize = cipher.getInputBlockSize();
        int outputBlockSize = cipher.getOutputBlockSize();

        byte[] cipherText;
        try {
            cipherText = cipher.processBlock(input, 0, input.length);
        } catch (InvalidCipherTextException e) {

            throw new CryptoException(e);
        }

        return Base64.encodeBytesNoBreaks(cipherText);

    }


    /**
     * Decrypts base 64 encoded data using RSA OAEP and the provided Key
     *
     * @param b64EncodedData the base 64 encoded ciphertext
     * @param inputKey            the private key to use for decryption
     * @return a byte[] of clear text
     * @throws CryptoException
     */
    public static byte[] decryptRSAOAEP(String b64EncodedData, PrivateKey inputKey) throws CryptoException {

        byte[] cipherText = Base64.decode(b64EncodedData);


        RSAPrivateKey key =  (RSAPrivateKey) inputKey;
        RSAEngine engine = new RSAEngine();
        OAEPEncoding cipher = new OAEPEncoding(engine);
        BigInteger mod = key.getModulus();
        BigInteger exp = key.getPrivateExponent();
        RSAKeyParameters keyParams = new RSAKeyParameters(true, mod, exp);
        cipher.init(false, keyParams);

        int inputBlockSize = cipher.getInputBlockSize();
        int outputBlockSize = cipher.getOutputBlockSize();


        byte[] clearText;

        try {
            clearText = cipher.processBlock(cipherText, 0, cipherText.length);
        } catch (InvalidCipherTextException e) {

            throw new CryptoException(e);
        }

        return clearText;

    }

    /**
     * Generates a SecretKey of a specified bit length
     *
     * @param bitSize length of key
     * @return the key as a byte[]
     * @throws CryptoException
     */
    public static byte[] genKey(int bitSize) throws CryptoException {


        KeyGenerator keygen;
        try {
            keygen = KeyGenerator.getInstance("AES");
        } catch (NoSuchAlgorithmException e) {

            throw new CryptoException(e);
        }

        keygen.init(bitSize);
        SecretKey key = keygen.generateKey();
        return key.getEncoded();

    }


    /**
     * Verifies a public signature
     *
     * @param data      the signed data
     * @param signature the signature
     * @param mod       modulus
     * @param exp       exponent
     * @return valid or invalid
     * @throws CryptoException
     */
    public static boolean verify(byte[] data, byte[] signature, BigInteger mod, BigInteger exp) throws CryptoException {



        boolean verified;

        try {

            RSAPublicKeySpec rsaKeySpec = new RSAPublicKeySpec(mod, exp);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PublicKey pubKey = keyFactory.generatePublic(rsaKeySpec);
            Signature sig = Signature.getInstance("SHA1withRSA");
            sig.initVerify(pubKey);
            sig.update(data);
            verified = sig.verify(signature);

        } catch (NoSuchAlgorithmException e) {

            throw new CryptoException(e);
        } catch (SignatureException e) {

            throw new CryptoException(e);
        } catch (InvalidKeyException e) {

            throw new CryptoException(e);
        } catch (InvalidKeySpecException e) {

            throw new CryptoException(e);
        }

        return verified;

    }

    /**
     * Signs data
     *
     * @param data the data to sign
     * @param key  the private key to use for signing
     * @return sgined data
     * @throws CryptoException
     */
    public static String sign(byte[] data, PrivateKey key) throws CryptoException {


        String signedData;

        try {

            Signature signature = Signature.getInstance("SHA1withRSA");
            signature.initSign(key);
            signature.update(data);
            signedData = Base64.encodeBytesNoBreaks(signature.sign());

        } catch (NoSuchAlgorithmException e) {

            throw new CryptoException(e);
        } catch (SignatureException e) {

            throw new CryptoException(e);
        } catch (InvalidKeyException e) {

            throw new CryptoException(e);
        }

        return signedData;

    }


}
