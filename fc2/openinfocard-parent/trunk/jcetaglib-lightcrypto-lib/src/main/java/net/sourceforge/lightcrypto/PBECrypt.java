/*
  Name:         net.sourceforge.lightcrypto.PBECrypt
  Licensing:    LGPL (lesser GNU Public License)
  API:          Bouncy Castle (http://www.bouncycastle.org) lightweight API

  Disclaimer:

  COVERED CODE IS PROVIDED UNDER THIS LICENSE ON AN "AS IS" BASIS, WITHOUT WARRANTY OF ANY KIND,
  EITHER EXPRESSED OR IMPLIED, INCLUDING, WITHOUT LIMITATION, WARRANTIES THAT THE COVERED CODE
  IS FREE OF DEFECTS, MERCHANTABLE, FIT FOR A PARTICULAR PURPOSE OR NON-INFRINGING. THE ENTIRE
  RISK AS TO THE QUALITY AND PERFORMANCE OF THE COVERED CODE IS WITH YOU. SHOULD ANY COVERED CODE
  PROVE DEFECTIVE IN ANY RESPECT, YOU (NOT THE INITIAL DEVELOPER OR ANY OTHER CONTRIBUTOR)
  ASSUME THE COST OF ANY NECESSARY SERVICING, REPAIR OR CORRECTION. THIS DISCLAIMER OF WARRANTY
  CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE. NO USE OF ANY COVERED CODE IS AUTHORIZED
  HEREUNDER EXCEPT UNDER THIS DISCLAIMER.

  (C) Copyright 2003 Gert Van Ham

*/

package net.sourceforge.lightcrypto;

import org.bouncycastle.crypto.PBEParametersGenerator;
import org.bouncycastle.crypto.BufferedBlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.paddings.PaddedBufferedBlockCipher;
import org.bouncycastle.crypto.modes.CBCBlockCipher;
import org.bouncycastle.crypto.engines.TwofishEngine;
import org.bouncycastle.crypto.digests.SHA1Digest;
import org.bouncycastle.crypto.generators.PKCS12ParametersGenerator;
import org.bouncycastle.util.encoders.Base64;

import java.io.*;
import java.security.SecureRandom;

/**
 * PBE (Password-based) encryption & decryption routines for use with the BouncyCastle lightweight API
 *
 * @author Gert Van Ham
 * @author hamgert@users.sourceforge.net
 * @author http://jcetaglib.sourceforge.net
 * @version $Id: PBECrypt.java,v 1.1 2003/10/28 20:12:53 hamgert Exp $
 */

public class PBECrypt {
    private static int BUFFERSIZE_TEXT = 64;
    private static int BUFFERSIZE_FILE = 8192;

    private static int PBECount = 20;
    private static int PBEKeyLength = 256;

    /**
     * Encrypts a string with PBE and returns
     * the ciphered text in BASE64 format.
     *
     * @param text the text to encrypt
     * @param passphrase password or passphrase
     * @return the cipherstring in BASE64 format
     * @exception IOException I/O errors
     * @exception CryptoException for all encryption errors
     **/
    public static StringBuffer encrypt(
            StringBuffer text
            , StringBuffer passphrase
            ) throws CryptoException, IOException {

        return encrypt(text, passphrase, null);
    }

    /**
     * Encrypts a string with PBE and returns
     * the ciphered text in BASE64 format.
     *
     * @param text the text to encrypt
     * @param passphrase password or passphrase
     * @param seed the seed for SecureRandom
     * @return the cipherstring in BASE64 format
     * @exception IOException I/O errors
     * @exception CryptoException for all encryption errors
     **/
    public static StringBuffer encrypt(
            StringBuffer text
            , StringBuffer passphrase
            , StringBuffer seed
            ) throws CryptoException, IOException {

        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        DataOutputStream dao = new DataOutputStream(bao);

        // encrypt text
        encrypt(new ByteArrayInputStream(text.toString().getBytes()), dao, passphrase, seed, BUFFERSIZE_TEXT);

        StringBuffer result = new StringBuffer(new String(Base64.encode(bao.toByteArray())));

        // close outputstream
        dao.flush();
        dao.close();

        return result;
    }

    /**
     * Encrypts any inputstream with PBE and returns
     * the ciphered inputstream as a DataOutputStream.
     *
     * @param is the inputstream to encrypt
     * @param daos outputstream for the ciphertext
     * @param passphrase password or passphrase
     * @param bufferlength buffer length in bytes
     * @param seed the seed for SecureRandom
     * @exception CryptoException for all encryption errors
     **/
    public static void encrypt(
            InputStream is
            , DataOutputStream daos
            , StringBuffer passphrase
            , StringBuffer seed
            , int bufferlength
            ) throws CryptoException {

        try {
            SecureRandom sr = new SecureRandom();

            // set seed if available
            if (seed != null && !seed.equals("")) {
                sr.setSeed(seed.toString().getBytes());
            }

            // create random salt
            byte[] randomsalt = new byte[8];
            sr.nextBytes(randomsalt);

            // write the randomsalt
            daos.write(randomsalt);

            // create the PBE cipher
            PBEParametersGenerator generator = new PKCS12ParametersGenerator(new SHA1Digest());
            generator.init(PBEParametersGenerator.PKCS12PasswordToBytes(passphrase.toString().toCharArray()), randomsalt, PBECount);

            TwofishEngine blockCipher = new TwofishEngine();
            CBCBlockCipher cbcCipher = new CBCBlockCipher(blockCipher);
            BufferedBlockCipher cipher = new PaddedBufferedBlockCipher(cbcCipher);

            CipherParameters param = generator.generateDerivedParameters(PBEKeyLength,128);

            // encrypt with PBE
            cipher.init(true, param);

            byte[] buffer = new byte[bufferlength];
            int length = cipher.getOutputSize(bufferlength);
            byte[] result = new byte[length];
            int outputLen = 0;

            // read bytes into buffer and feed these bytes into the cipher
            while ((length = is.read(buffer)) != -1) {
                outputLen = cipher.processBytes(buffer, 0, length, result, 0);

                if (outputLen > 0) {
                    daos.write(result, 0, outputLen);
                }
            }

            // doFinal for encrypting last bytes
            outputLen = cipher.doFinal(result, 0);
            if (outputLen > 0) {
                daos.write(result, 0, outputLen);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            throw new CryptoException(ex.getMessage());
        }
    }

    /**
     * Encrypts a file with PBE and creates
     * a new file with the result.
     *
     * @param file the file to encrypt
     * @param file the encrypted file
     * @param passphrase password or passphrase
     * @exception IOException I/O errors
     * @exception CryptoException for all encryption errors
     **/
    public static void encryptFile(
            String file
            , String newfile
            , StringBuffer passphrase
            ) throws CryptoException, IOException {
        encryptFile(file, newfile, passphrase, null);
    }

    /**
     * Encrypts a file with PBE and creates
     * a new file with the result.
     *
     * @param file the file to encrypt
     * @param file the encrypted file
     * @param passphrase password or passphrase
     * @param seed the seed for SecureRandom
     * @exception IOException I/O errors
     * @exception CryptoException for all encryption errors
     **/
    public static void encryptFile(
            String file
            , String newfile
            , StringBuffer passphrase
            , StringBuffer seed
            ) throws CryptoException, IOException {

        FileInputStream fis = new FileInputStream(file);
        FileOutputStream fos = new FileOutputStream(newfile);

        DataOutputStream dao = new DataOutputStream(fos);

        // encrypt file
        encrypt(fis, dao, passphrase, seed, BUFFERSIZE_FILE);

        // close outputstream
        dao.flush();
        dao.close();

        // close inputstream
        fis.close();
        fos.close();
    }

    /**
     * Decrypts a ciphered BASE64 string with PBE
     *
     * @param text the text to decipher
     * @param passphrase the password or passphrase
     * @return the decipher string (plaintext)
     * @exception CryptoException for all encryption errors
     * @exception IOException I/O errors
     **/
    public static StringBuffer decrypt(
            StringBuffer text
            , StringBuffer passphrase
            ) throws CryptoException, IOException {

        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        DataOutputStream dao = new DataOutputStream(bao);

        // decrypt
        decrypt(new ByteArrayInputStream(Base64.decode(text.toString())), dao, passphrase, BUFFERSIZE_TEXT);

        //close outputstream
        dao.flush();
        dao.close();

        return new StringBuffer(new String(bao.toByteArray()));
    }

    /**
     * Decrypts a ciphered file with PBE
     *
     * @param file the file to decrypt
     * @param file the deciphered file
     * @param passphrase the password or passphrase
     * @exception CryptoException for all encryption errors
     * @exception IOException I/O errors
     **/
    public static void decryptFile(
            String file
            , String newfile
            , StringBuffer passphrase
            ) throws CryptoException, IOException {

        FileInputStream fis = new FileInputStream(file);
        FileOutputStream fos = new FileOutputStream(newfile);
        DataOutputStream dao = new DataOutputStream(fos);

        // decrypt file
        decrypt(fis, dao, passphrase, BUFFERSIZE_FILE);

        // close outputstream
        dao.flush();
        dao.close();

        // close inputstream
        fis.close();
        fos.close();
    }

    /**
     * Decrypts a ciphered inputstream with PBE
     *
     * @param is the inputstream to decipher
     * @param daos outputstream for the plaintext
     * @param passphrase the password or passphrase
     * @param bufferlength buffer length in bytes
     * @exception CryptoException for all encryption errors
     **/
    public static void decrypt(
            InputStream is
            , DataOutputStream daos
            , StringBuffer passphrase
            , int bufferlength
            ) throws CryptoException {

        try {
            // read the salt
            byte[] randomsalt = new byte[8];
            is.read(randomsalt);

            // create the PBE cipher
            PBEParametersGenerator generator = new PKCS12ParametersGenerator(new SHA1Digest());
            generator.init(PBEParametersGenerator.PKCS12PasswordToBytes(passphrase.toString().toCharArray()), randomsalt, PBECount);

            TwofishEngine blockCipher = new TwofishEngine();
            CBCBlockCipher cbcCipher = new CBCBlockCipher(blockCipher);
            BufferedBlockCipher cipher = new PaddedBufferedBlockCipher(cbcCipher);

            CipherParameters param = generator.generateDerivedParameters(PBEKeyLength,128);

            // decrypt with PBE
            cipher.init(false, param);

            byte[] buffer = new byte[bufferlength];
            int length = cipher.getOutputSize(bufferlength);
            byte[] result = new byte[length];
            int outputLen = 0;

            // read bytes into buffer and feed these bytes into the cipher
            while ((length = is.read(buffer)) != -1) {
                outputLen = cipher.processBytes(buffer, 0, length, result, 0);

                if (outputLen > 0) {
                    daos.write(result, 0, outputLen);
                }
            }

            // doFinal for encrypting last bytes
            outputLen = cipher.doFinal(result, 0);
            if (outputLen > 0) {
                daos.write(result, 0, outputLen);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            throw new CryptoException(ex.getMessage());
        }
    }
}
