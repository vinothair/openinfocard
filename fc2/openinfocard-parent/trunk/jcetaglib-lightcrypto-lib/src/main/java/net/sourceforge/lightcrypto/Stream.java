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

import org.bouncycastle.crypto.engines.RC4Engine;
import org.bouncycastle.crypto.params.KeyParameter;

import java.io.DataOutputStream;
import java.io.InputStream;

/**
 * Streamcipher routines for use with the BouncyCastle lightweight API
 *
 * @author Gert Van Ham
 * @author hamgert@users.sourceforge.net
 * @author http://jcetaglib.sourceforge.net
 * @version $Id: Stream.java,v 1.1 2003/10/28 20:12:53 hamgert Exp $
 */

public class Stream {
    /**
     * Encrypts any inputstream with an RC4 stream cipher and returns
     * the ciphered inputstream as a DataOutputStream
     *
     * @param is the inputstream to encrypt
     * @param daos outputstream for the ciphertext
     * @param keybytes the symmetric key (which generated with the net.sourceforge.lightcrypto.Key object)
     * @param bufferlength buffer length in bytes
     * @exception CryptoException for all encryption errors
     **/
    public static void encrypt(
            InputStream is
            , DataOutputStream daos
            , SafeObject keybytes
            , int bufferlength
            ) throws CryptoException {

        KeyParameter key = null;

        try {
            // Initialize the RC4 cipher
            RC4Engine streamCipher = new RC4Engine();

            // use the keybytes to create a key
            key = new KeyParameter(keybytes.getBytes());

            byte[] buffer = new byte[bufferlength];
            int length = bufferlength;
            byte[] result = new byte[length];

            // initialize the cipher for encrypting with the key
            streamCipher.init(true, key);

            // read bytes into buffer and feed these bytes into the cipher
            while ((length = is.read(buffer)) != -1) {
                streamCipher.processBytes(buffer, 0, length, result, 0);
                daos.write(result, 0, length);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            throw new CryptoException(ex.getMessage());
        } finally {
            // clean sensitive information from memory
            key = null;
        }
    }

    /**
     * Decrypts a ciphered inputstream with an RC4 stream cipher
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

            // Initialize the RC4 cipher
            RC4Engine streamCipher = new RC4Engine();

            byte[] buffer = new byte[bufferlength];
            int length = bufferlength;
            byte[] result = new byte[length];

            // initialize the cipher for decrypting with the key
            streamCipher.init(false, key);

            // read bytes into buffer and feed these bytes into the cipher
            while ((length = is.read(buffer)) != -1) {
                streamCipher.processBytes(buffer, 0, length, result, 0);
                daos.write(result, 0, length);
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

