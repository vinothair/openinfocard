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

import org.bouncycastle.crypto.BufferedBlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.PBEParametersGenerator;
import org.bouncycastle.crypto.digests.MD2Digest;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.engines.DESEngine;
import org.bouncycastle.crypto.engines.DESedeEngine;
import org.bouncycastle.crypto.generators.PKCS5S1ParametersGenerator;
import org.bouncycastle.crypto.generators.PKCS5S2ParametersGenerator;
import org.bouncycastle.crypto.modes.CBCBlockCipher;
import org.bouncycastle.crypto.paddings.PaddedBufferedBlockCipher;


/**
 * Attempt to decrypt EncryptedStore for InfoCards.   Doesn't work.
 */
public class PKCS5 {

    private PKCS5() {
    }

    public static byte[] PBKDF1(String password, byte[] salt) {
        return PBKDF1(password, salt, 32, 1024);
    }


    public static byte[] PBKDF1(String password, byte[] salt, int keySize) {
        return PBKDF1(password, salt, keySize, 1024);
    }

    public static byte[] PBKDF1(String password, byte[] salt, int keySize, int iterationCount) {
        if (keySize > 32) keySize = 32;
        if (iterationCount <= 0) iterationCount = 1;

        //TODO - switch to array copy if I save this...don't think there is any need give BC support

        byte[] keyBytes = password.getBytes();

        //concat the keys together
        byte[] concatKey = new byte[keyBytes.length + salt.length];


        int curs = 0;
        for (int i = 0; i < keyBytes.length; i++) {

            concatKey[curs] = keyBytes[i];
            curs++;
        }


        for (int i = 0; i < salt.length; i++) {

            concatKey[curs] = salt[i];
            curs++;
        }


        byte[] derivedKeyData = new byte[32];
        for (int i = 0; i < iterationCount; i++) {

            derivedKeyData = sha256(derivedKeyData);
        }


        byte[] derivedKey = new byte[keySize];

        System.arraycopy(derivedKeyData, 0, derivedKey, 0, keySize);

        return derivedKey;
    }


    public static byte[] sha256(byte[] data) {

        SHA256Digest digest = new SHA256Digest();
        byte[] hash = new byte[digest.getDigestSize()];
        digest.update(data, 0, data.length);
        digest.doFinal(hash, 0);
        return hash;

    }


    public static byte[] PBKDF2(String password, byte[] salt) {
        return PBKDF2(password, salt, 32, 1024);
    }


    public static byte[] PBKDF2(String password, byte[] salt, int keySize) {
        return PBKDF2(password, salt, keySize, 1024);
    }

    public static byte[] PBKDF2(String password, byte[] salt, int keySize, int iterationCount) {


        return null;
    }


    public static byte[] decryptPBES1(byte[] cipherText, byte[] key) {


        return null;

    }

    public static byte[] encryptPBES2(byte[] cipherText, byte[] key) {


        return null;

    }

    public static byte[] decryptPBES1(byte[] cipherText, String password, byte[] salt, int count) {

        MD2Digest digest = new MD2Digest();
        PKCS5S1ParametersGenerator paramGenerator = new PKCS5S1ParametersGenerator(digest);
        paramGenerator.init(PBEParametersGenerator.PKCS5PasswordToBytes(password.toCharArray()), salt, count);

        DESEngine blockCipher = new DESEngine();
        CBCBlockCipher cbcCipher = new CBCBlockCipher(blockCipher);
        BufferedBlockCipher cipher = new PaddedBufferedBlockCipher(cbcCipher);
        CipherParameters param = paramGenerator.generateDerivedParameters(64);
        cipher.init(false, param);

        byte[] clearText = new byte[cipher.getOutputSize(cipherText.length)];
        int outputLen = cipher.processBytes(cipherText, 0, cipherText.length, clearText, 0);
        try {
            cipher.doFinal(clearText, outputLen);
        } catch (InvalidCipherTextException e) {
            e.printStackTrace();
        }
        return clearText;

    }


    public static byte[] decryptPBES2(byte[] cipherText, String password, byte[] salt, int count) {

        PKCS5S2ParametersGenerator paramGenerator = new PKCS5S2ParametersGenerator();
        paramGenerator.init(PBEParametersGenerator.PKCS5PasswordToBytes(password.toCharArray()), salt, count);

        DESedeEngine blockCipher = new DESedeEngine();
        CBCBlockCipher cbcCipher = new CBCBlockCipher(blockCipher);
        BufferedBlockCipher cipher = new PaddedBufferedBlockCipher(cbcCipher);
        CipherParameters param = paramGenerator.generateDerivedParameters(128);
        cipher.init(false, param);

        byte[] clearText = new byte[cipher.getOutputSize(cipherText.length)];
        int outputLen = cipher.processBytes(cipherText, 0, cipherText.length, clearText, 0);
        try {
            cipher.doFinal(clearText, outputLen);
        } catch (InvalidCipherTextException e) {
            e.printStackTrace();
        }

        return clearText;

    }


}
