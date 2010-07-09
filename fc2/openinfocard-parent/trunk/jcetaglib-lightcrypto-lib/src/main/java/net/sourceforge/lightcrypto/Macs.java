/*
  Name:         net.sourceforge.lightcrypto.Macs
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

import org.bouncycastle.util.encoders.Base64;
import org.bouncycastle.crypto.macs.CBCBlockCipherMac;
import org.bouncycastle.crypto.engines.AESLightEngine;
import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;

import java.io.*;
import java.security.SecureRandom;

/**
 * Create CBC Block Cipher MAC (Message Authentication Code) with IV from text and files
 *
 * @author Gert Van Ham
 * @author hamgert@users.sourceforge.net
 * @author http://jcetaglib.sourceforge.net
 * @version $Id: Macs.java,v 1.1 2003/10/05 11:41:29 hamgert Exp $
 */
public class Macs {
    private static int BUFFERSIZE_TEXT = 64;
    private static int BUFFERSIZE_FILE = 8192;

    /**
     * Creates a MAC from a stringbuffer
     *
     * @param text creates MAC from this text
     * @param keybytes the symmetric key
     * @return MAC in BASE64 format
     * @throws CryptoException for MAC errors
     */
    public static StringBuffer mac(
            StringBuffer text
            , SafeObject keybytes
            ) throws CryptoException {
        return mac(new ByteArrayInputStream(text.toString().getBytes()), keybytes, null, BUFFERSIZE_TEXT);
    }

    /**
     * Creates a MAC from a stringbuffer
     *
     * @param text creates MAC from this text
     * @param keybytes the symmetric key
     * @param seed the seed for SecureRandom
     * @return MAC in BASE64 format
     * @throws CryptoException for MAC errors
     */
    public static StringBuffer mac(
            StringBuffer text
            , SafeObject keybytes
            , StringBuffer seed
            ) throws CryptoException {
        return mac(new ByteArrayInputStream(text.toString().getBytes()), keybytes, seed, BUFFERSIZE_TEXT);
    }

    /**
     * Creates a MAC from a file
     *
     * @param file filename
     * @param keybytes the symmetric key
     * @return MAC in BASE64 format
     * @throws CryptoException for MAC errors
     * @throws FileNotFoundException when the file was not found
     * @throws IOException when the file could not be opened (or closed)
     */
    public static StringBuffer macFromFile(
            String file
            , SafeObject keybytes
            ) throws CryptoException, FileNotFoundException, IOException {

        FileInputStream fis = new FileInputStream(file);
        StringBuffer res = mac(fis, keybytes, null, BUFFERSIZE_FILE);
        fis.close();
        return res;
    }

    /**
     * Creates a MAC from a file
     *
     * @param file filename
     * @param keybytes the symmetric key
     * @param seed the seed for SecureRandom
     * @return MAC in BASE64 format
     * @throws CryptoException for MAC errors
     * @throws FileNotFoundException when the file was not found
     * @throws IOException when the file could not be opened (or closed)
     */
    public static StringBuffer macFromFile(
            String file
            , SafeObject keybytes
            , StringBuffer seed
            ) throws CryptoException, FileNotFoundException, IOException {

        FileInputStream fis = new FileInputStream(file);
        StringBuffer res = mac(fis, keybytes, seed, BUFFERSIZE_FILE);
        fis.close();
        return res;
    }

    /**
     * Creates a MAC from an inputstream
     *
     * @param is any inputstream
     * @param buffersize the buffersize in number of bytes
     * @return MAC in BASE64 format
     * @throws CryptoException for MAC errors
     */
    public static StringBuffer mac(
            InputStream is
            , SafeObject keybytes
            , StringBuffer seed
            , int buffersize
            ) throws CryptoException {

        KeyParameter key = null;

        try {
            SecureRandom sr = new SecureRandom();

            // set seed if available
            if (seed != null && !seed.equals("")) {
                sr.setSeed(seed.toString().getBytes());
            }

            BlockCipher cipher = new AESLightEngine();
            CBCBlockCipherMac mac = new CBCBlockCipherMac(cipher);

            // Create an IV of random data.
            byte[] iv = new byte[cipher.getBlockSize()];
            sr.nextBytes(iv);

            // use the keybytes to create a key
            key = new KeyParameter(keybytes.getBytes());
            // use the IV and key to create cipherparameters
            ParametersWithIV ivparam = new ParametersWithIV(key, iv);

            mac.init(ivparam);

            byte[] result = new byte[mac.getMacSize() + iv.length];
            byte[] buffer = new byte[buffersize];
            int length = 0;

            // write the IV
            System.arraycopy(iv, 0, result, 0, iv.length);

            // read bytes into buffer and feed these bytes into Hmac object
            while ((length = is.read(buffer)) != -1) {
                mac.update(buffer, 0, length);
            }

            mac.doFinal(result, iv.length);

            return new StringBuffer(new String(Base64.encode(result)));
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
     * Check if a MAC is valid
     *
     * @param text the plain text
     * @param mac the MAC (in BASE64 format)
     * @param keybytes the MAC (in BASE64 format)
     * @return true if MAC is valid, false if not
     * @throws CryptoException for MAC errors
     */
    public static boolean macEquals(
            StringBuffer text
            , StringBuffer mac
            , SafeObject keybytes
            ) throws CryptoException {
        return macEquals(new ByteArrayInputStream(text.toString().getBytes()), mac, keybytes, BUFFERSIZE_TEXT);
    }

    /**
     * Check if a file MAC is valid
     *
     * @param file the filename
     * @param mac the MAC (in BASE64 format)
     * @param keybytes the MAC (in BASE64 format)
     * @return true if MAC is valid, false if not
     * @throws CryptoException for MAC errors
     * @throws FileNotFoundException when the file was not found
     * @throws IOException when the file could not be opened (or closed)
     */
    public static boolean macEqualsFile(
            String file
            , StringBuffer mac
            , SafeObject keybytes
            ) throws CryptoException, FileNotFoundException, IOException {

        FileInputStream fis = new FileInputStream(file);
        boolean res = macEquals(fis, mac, keybytes, BUFFERSIZE_FILE);
        fis.close();
        return res;
    }

    /**
     * Check if a MAC is valid
     *
     * @param is any inputstream containing the plain text
     * @param mac the MAC (in BASE64 format)
     * @param keybytes the symmetric key
     * @param buffersize the buffersize in number of bytes
     * @return true if MAC is valid, false if not
     * @throws CryptoException for MAC errors
     */
    public static boolean macEquals(
            InputStream is
            , StringBuffer mac
            , SafeObject keybytes
            , int buffersize
            ) throws CryptoException {

        KeyParameter key = null;

        try {
            // use the keybytes to create a key
            key = new KeyParameter(keybytes.getBytes());

            BlockCipher cipher = new AESLightEngine();
            CBCBlockCipherMac cbcmac = new CBCBlockCipherMac(cipher);

            // read the IV from the MAC
            byte[] iv = new byte[cipher.getBlockSize()];
            byte[] decodedMac = new byte[cbcmac.getMacSize() + iv.length];
            decodedMac = Base64.decode(new String(mac));

            System.arraycopy(decodedMac, 0, iv, 0, iv.length);

            // use the IV and key to create cipherparameters
            ParametersWithIV ivparam = new ParametersWithIV(key, iv);

            cbcmac.init(ivparam);

            byte[] result = new byte[cbcmac.getMacSize() + iv.length];
            byte[] buffer = new byte[buffersize];
            int length = 0;

            // write the IV
            System.arraycopy(iv, 0, result, 0, iv.length);

            // read bytes into buffer and feed these bytes into MAC object
            while ((length = is.read(buffer)) != -1) {
                cbcmac.update(buffer, 0, length);
            }

            cbcmac.doFinal(result, iv.length);

            String rs = new String(Base64.encode(result));

            if (rs.equals(mac.toString())) {
                return true;
            } else {
                return false;
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
