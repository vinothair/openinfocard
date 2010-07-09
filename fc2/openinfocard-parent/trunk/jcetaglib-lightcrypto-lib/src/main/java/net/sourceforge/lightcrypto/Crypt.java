/*
  Name:         net.sourceforge.lightcrypto.Crypt
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

import org.bouncycastle.crypto.BufferedBlockCipher;
import org.bouncycastle.crypto.engines.AESLightEngine;
import org.bouncycastle.crypto.modes.CBCBlockCipher;
import org.bouncycastle.crypto.paddings.PaddedBufferedBlockCipher;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.util.encoders.Base64;

import java.io.*;
import java.security.SecureRandom;

/**
 * Encryption & decryption routines for use with the BouncyCastle lightweight API
 *
 * @author Gert Van Ham
 * @author hamgert@users.sourceforge.net
 * @author http://jcetaglib.sourceforge.net
 * @version $Id: Crypt.java,v 1.2 2003/10/05 11:41:29 hamgert Exp $
 */

public class Crypt {
    private static int BUFFERSIZE_TEXT = 64;
    private static int BUFFERSIZE_FILE = 8192;

    /**
     * Encrypts a string with a symmetric key (AES light engine, CBC mode, PKCS7 padding) and returns
     * the ciphered text in BASE64 format.
     *
     * @param text the text to encrypt
     * @param keybytes the symmetric key (which generated with the net.sourceforge.lightcrypto.Key object)
     * @return the cipherstring in BASE64 format
     * @exception IOException I/O errors
     * @exception CryptoException for all encryption errors
     **/
    public static StringBuffer encrypt(
            StringBuffer text
            , SafeObject keybytes
            ) throws CryptoException, IOException {

        return encrypt(text, keybytes, null);
    }

    /**
     * Encrypts a string with a symmetric key (AES light engine, CBC mode, PKCS7 padding) and returns
     * the ciphered text in BASE64 format.
     *
     * @param text the text to encrypt
     * @param keybytes the symmetric key (which generated with the net.sourceforge.lightcrypto.Key object)
     * @param seed the seed for SecureRandom
     * @return the cipherstring in BASE64 format
     * @exception IOException I/O errors
     * @exception CryptoException for all encryption errors
     **/
    public static StringBuffer encrypt(
            StringBuffer text
            , SafeObject keybytes
            , StringBuffer seed
            ) throws CryptoException, IOException {

        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        DataOutputStream dao = new DataOutputStream(bao);

        // encrypt text
        encrypt(new ByteArrayInputStream(text.toString().getBytes()), dao, keybytes, seed, BUFFERSIZE_TEXT);

        StringBuffer result = new StringBuffer(new String(Base64.encode(bao.toByteArray())));

        // close outputstream
        dao.flush();
        dao.close();

        return result;
    }

    /**
     * Encrypts a file with a symmetric key (AES light engine, CBC mode, PKCS7 padding) and creates
     * a new file with the result.
     *
     * @param file the file to encrypt
     * @param file the encrypted file
     * @param keybytes the symmetric key (which generated with the net.sourceforge.lightcrypto.Key object)
     * @exception IOException I/O errors
     * @exception CryptoException for all encryption errors
     **/
    public static void encryptFile(
            String file
            , String newfile
            , SafeObject keybytes
            ) throws CryptoException, IOException {
        encryptFile(file, newfile, keybytes, null);
    }

    /**
     * Encrypts a file with a symmetric key (AES light engine, CBC mode, PKCS7 padding) and creates
     * a new file with the result.
     *
     * @param file the file to encrypt
     * @param file the encrypted file
     * @param keybytes the symmetric key (which generated with the net.sourceforge.lightcrypto.Key object)
     * @param seed the seed for SecureRandom
     * @exception IOException I/O errors
     * @exception CryptoException for all encryption errors
     **/
    public static void encryptFile(
            String file
            , String newfile
            , SafeObject keybytes
            , StringBuffer seed
            ) throws CryptoException, IOException {

        FileInputStream fis = new FileInputStream(file);
        FileOutputStream fos = new FileOutputStream(newfile);

        DataOutputStream dao = new DataOutputStream(fos);

        // encrypt file
        encrypt(fis, dao, keybytes, seed, BUFFERSIZE_FILE);

        // close outputstream
        dao.flush();
        dao.close();

        // close inputstream
        fis.close();
        fos.close();
    }

    /**
     * Encrypts any inputstream with a symmetric key (AES light engine, CBC mode, PKCS7 padding) and returns
     * the ciphered inputstream as a ByteArrayOutputStream
     *
     * @param is the inputstream to encrypt
     * @param daos outputstream for the ciphertext
     * @param keybytes the symmetric key (which generated with the net.sourceforge.lightcrypto.Key object)
     * @param seed the seed for SecureRandom
     * @param bufferlength buffer length in bytes
     * @exception CryptoException for all encryption errors
     **/
    public static void encrypt(
            InputStream is
            , DataOutputStream daos
            , SafeObject keybytes
            , StringBuffer seed
            , int bufferlength
            ) throws CryptoException {

        KeyParameter key = null;

        try {
            SecureRandom sr = new SecureRandom();

            // set seed if available
            if (seed != null && !seed.equals("")) {
                sr.setSeed(seed.toString().getBytes());
            }

            // Initialize the AES cipher ("light" engine) in CBC mode with PKCS7 padding
            AESLightEngine blockCipher = new AESLightEngine();
            CBCBlockCipher cbcCipher = new CBCBlockCipher(blockCipher);
            BufferedBlockCipher cipher = new PaddedBufferedBlockCipher(cbcCipher);

            // Create an IV of random data.
            byte[] iv = new byte[blockCipher.getBlockSize()];
            sr.nextBytes(iv);

            // use the keybytes to create a key
            key = new KeyParameter(keybytes.getBytes());
            // use the IV and key to create cipherparameters
            ParametersWithIV ivparam = new ParametersWithIV(key, iv);

            // write the IV to the outputstream
            daos.write(iv, 0, iv.length);

            // Concatenate the IV and the message.
            byte[] buffer = new byte[bufferlength];
            int length = cipher.getOutputSize(bufferlength);
            byte[] result = new byte[length];
            int outputLen = 0;

            // initialize the cipher for encrypting with the key and IV
            cipher.init(true, ivparam);

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
        } finally {
            // clean sensitive information from memory
            key = null;
            if (seed != null) {
                Clean.blank(seed);
                seed = null;
            }
        }
    }

    /**
     * Decrypts a ciphered BASE64 string with a symmetric key (AES light engine, CBC mode, PKCS7 padding)
     *
     * @param text the text to decipher
     * @param keybytes the symmetric key (which generated with the net.sourceforge.lightcrypto.Key object)
     * @return the decipher string (plaintext)
     * @exception CryptoException for all encryption errors
     * @exception IOException I/O errors
     **/
    public static StringBuffer decrypt(
            StringBuffer text
            , SafeObject keybytes
            ) throws CryptoException, IOException {

        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        DataOutputStream dao = new DataOutputStream(bao);

        // decrypt
        decrypt(new ByteArrayInputStream(Base64.decode(text.toString())), dao, keybytes, BUFFERSIZE_TEXT);

        //close outputstream
        dao.flush();
        dao.close();

        return new StringBuffer(new String(bao.toByteArray()));
    }

    /**
     * Decrypts a ciphered file with a symmetric key (AES light engine, CBC mode, PKCS7 padding)
     *
     * @param file the file to decrypt
     * @param file the deciphered file
     * @param keybytes the symmetric key (which generated with the net.sourceforge.lightcrypto.Key object)
     * @exception CryptoException for all encryption errors
     * @exception IOException I/O errors
     **/
    public static void decryptFile(
            String file
            , String newfile
            , SafeObject keybytes
            ) throws CryptoException, IOException {

        FileInputStream fis = new FileInputStream(file);
        FileOutputStream fos = new FileOutputStream(newfile);
        DataOutputStream dao = new DataOutputStream(fos);

        // decrypt file
        decrypt(fis, dao, keybytes, BUFFERSIZE_FILE);

        // close outputstream
        dao.flush();
        dao.close();

        // close inputstream
        fis.close();
        fos.close();
    }

    /**
     * Decrypts a ciphered inputstream with a symmetric key (AES light engine, CBC mode, PKCS7 padding)
     *
     * @param is the inputstream to decipher
     * @param daos outputstream for the plaintext
     * @param keybytes the symmetric key (which generated with the net.sourceforge.lightcrypto.Key object)
     * @param bufferlength buffer length in bytes
     * @exception CryptoException for all encryption errors
     **/
    public static void decrypt(
            InputStream is
            , DataOutputStream daos
            , SafeObject keybytes
            , int bufferlength
            ) throws CryptoException {
        KeyParameter key = null;

        try {
            // use the keybytes to create a key
            key = new KeyParameter(keybytes.getBytes());

            AESLightEngine blockCipher = new AESLightEngine();
            CBCBlockCipher cbcCipher = new CBCBlockCipher(blockCipher);
            BufferedBlockCipher cipher = new PaddedBufferedBlockCipher(cbcCipher);

            // read the IV from the inputstream
            byte[] iv = new byte[blockCipher.getBlockSize()];
            is.read(iv);

            // use the IV and key to create cipherparameters
            ParametersWithIV ivparam = new ParametersWithIV(key, iv);

            byte[] buffer = new byte[bufferlength];
            int length = cipher.getOutputSize(buffer.length);
            byte[] result = new byte[length];
            int outputLen = 0;

            // initialize the cipher for decrypting ith the key and IV
            cipher.init(false, ivparam);

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

        } finally {
            // clean sensitive information from memory
            key = null;
        }
    }
}
