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

package org.xmldap.crypto;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.xmldap.exceptions.CryptoException;


public class EncryptedStoreKeys {

    private static byte[] encKeyEntropy =  { (byte)0xd9, (byte)0x59, (byte)0x7b, (byte)0x26, (byte)0x1e, (byte)0xd8, (byte)0xb3, (byte)0x44, (byte)0x93, (byte)0x23, (byte)0xb3, (byte)0x96, (byte)0x85, (byte)0xde, (byte)0x95, (byte)0xfc };
    private static byte[] integrityKeyEntropy = {(byte)0xc4, (byte)0x01, (byte)0x7b, (byte)0xf1, (byte)0x6b, (byte)0xad, (byte)0x2f, (byte)0x42, (byte)0xaf, (byte)0xf4, (byte)0x97, (byte)0x7d, (byte)0x4, (byte)0x68, (byte)0x3, (byte)0xdb};


    private byte[] encryptionKey;
    private byte[] integrityKey;

    public EncryptedStoreKeys(String password, byte[] salt) throws CryptoException {


        byte[] key = null;
        try {
            key = password.getBytes("UTF-16LE");
        } catch (Exception e) {
            throw new CryptoException("Error getting bytes for password", e);
        }

        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new CryptoException("Error generating SHA256 Digest", e);
        }

        byte[] derivedKey = generateDerivedKey(digest, key, salt, 1000);

        digest.reset();

        byte[] encKeyBytes = new byte[encKeyEntropy.length + derivedKey.length];
        System.arraycopy(encKeyEntropy, 0, encKeyBytes, 0, encKeyEntropy.length);
        System.arraycopy(derivedKey, 0, encKeyBytes, encKeyEntropy.length ,derivedKey.length);
        digest.update(encKeyBytes);
        encryptionKey = digest.digest();

        digest.reset();

        byte[] integrityKeyBytes = new byte[integrityKeyEntropy.length + derivedKey.length];
        System.arraycopy(integrityKeyEntropy, 0, integrityKeyBytes, 0, integrityKeyEntropy.length);
        System.arraycopy(derivedKey, 0, integrityKeyBytes, integrityKeyEntropy.length ,derivedKey.length);
        digest.update(integrityKeyBytes);
        integrityKey = digest.digest();


    }


    private byte[] generateDerivedKey(MessageDigest digest, byte[] password, byte[] salt, int iterationCount) {

        digest.update(password);
        digest.update(salt);
        byte[] digestBytes = digest.digest();
        for (int i = 1; i < iterationCount; i++) {
            digest.update(digestBytes);
            digestBytes = digest.digest();
        }
        return digestBytes;
    }

    public byte[] getEncryptionKey() {
        return encryptionKey;
    }

    public byte[] getIntegrityKey() {
        return integrityKey;
    }


}
