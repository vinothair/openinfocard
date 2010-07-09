/*
  Name:         net.sourceforge.lightcrypto.Hsqldb
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

import java.io.ByteArrayInputStream;
import java.io.IOException;

/**
 * High-level methods for use with HSQLDB database engine (http://hsqldb.sourceforge.net)
 * These methods encapsulate functionality (use String instead of StringBuffer) so it
 * can be used as a function inside SQL statements.
 *
 * @author Gert Van Ham
 * @author hamgert@users.sourceforge.net
 * @author http://jcetaglib.sourceforge.net
 * @version $Id: Hsqldb.java,v 1.3 2003/10/28 20:12:53 hamgert Exp $
 */

public class Hsqldb {
    private static int BUFFERSIZE_TEXT = 64;

    /**
     * Creates a digest from a string
     *
     * @param text creates the digest from this text
     * @param algorithm the digest algorithm. Can be 'SHA1' or 'MD5' (default)
     * @return the generated digest string in BASE64
     * @exception CryptoException for all encryption errors
     **/
    public static String digest(
            String text
            , String algorithm) throws CryptoException {
        return Digesters.digest(new ByteArrayInputStream(text.getBytes()), algorithm, BUFFERSIZE_TEXT).toString();
    }

    /**
     * Load a symmetric key from the file, unwrap it and return the key as a Base64 string
     *
     * @param file the filename where the symmetric key is store
     * @param passphrase the passphrase for the symmetric key
     * @return the key as a BASE64 string
     * @throws Exception for all errors
     * @throws CryptoException for all encryption errors
     * @throws KeyException when the key could not be loaded
     */
    public static StringBuffer loadkey(
            String file
            , StringBuffer passphrase) throws Exception, CryptoException, KeyException {

        SafeObject so = new SafeObject();
        so = Key.loadkey(file, passphrase);

        StringBuffer key = new StringBuffer(so.getBase64());
        so.clearText();

        return key;
    }

    /**
     * Encrypts a string with a symmetric key (AES light engine, CBC mode, PKCS7 padding) and returns
     * the ciphered text in BASE64 format.
     *
     * @param text the plain text
     * @param key the key (in BASE64 format)
     * @return the cipherstring in BASE64 format
     * @throws Exception for all errors
     * @throws CryptoException for all encryption errors
     * @throws IOException I/O errors
     */
    public static String encrypt(
            String text
            , String key
            ) throws Exception, CryptoException, IOException {

        SafeObject k = new SafeObject();
        k.setText(Base64.decode(key));

        String cipher = new String(Crypt.encrypt(new StringBuffer(text), k));
        k.clearText();

        return cipher;
    }

    /**
     * Decrypts a ciphered BASE64 string with a symmetric key (AES light engine, CBC mode, PKCS7 padding)
     *
     * @param text the text to decipher
     * @param key key (in BASE64 format)
     * @return the plain text
     * @throws Exception for all errors
     * @throws CryptoException for all encryption errors
     * @throws IOException I/O errors
     */
    public static String decrypt(
            String text
            , String key
            ) throws Exception, CryptoException, IOException {

        SafeObject k = new SafeObject();
        k.setText(Base64.decode(key));

        String decipher = new String(Crypt.decrypt(new StringBuffer(text), k));
        k.clearText();

        return decipher;
    }

    /**
     * Encrypts a string with PBE and returns
     * the ciphered text in BASE64 format.
     *
     * @param text the plain text
     * @param passphrase the password or passphrase
     * @return the cipherstring in BASE64 format
     * @throws Exception for all errors
     * @throws CryptoException for all encryption errors
     * @throws IOException I/O errors
     */
    public static String PBEEncrypt(
            String text
            , String passphrase
            ) throws Exception, CryptoException, IOException {

        String cipher = new String(PBECrypt.encrypt(new StringBuffer(text), new StringBuffer(passphrase)));

        return cipher;
    }

     /**
     * Decrypts a ciphered BASE64 string with PBE
     *
     * @param text the text to decipher
     * @param passphrase password or passphrase
     * @return the plain text
     * @throws Exception for all errors
     * @throws CryptoException for all encryption errors
     * @throws IOException I/O errors
     */
    public static String PBEDecrypt(
            String text
            , String passphrase
            ) throws Exception, CryptoException, IOException {

        String decipher = new String(PBECrypt.decrypt(new StringBuffer(text), new StringBuffer(passphrase)));

        return decipher;
    }

    /**
     * Create HMAC (Hash Message Authentication Code)
     *
     * @param text creates HMAC from this text
     * @return HMAC in BASE64 format
     * @throws CryptoException for HMAC errors
     */
    public static String hmac(
            String text
            ) throws CryptoException {

        String hmac = new String(HMacs.mac(new StringBuffer(text)));
        return hmac;
    }

     /**
     * Create MAC (Message Authentication Code)
     *
     * @param text creates MAC from this text
     * @param key key (in BASE64 format)
     * @return MAC in BASE64 format
     * @throws CryptoException for MAC errors
     */
    public static String mac(
               String text
               ,String key
               ) throws Exception, CryptoException {

        SafeObject k = new SafeObject();
        k.setText(Base64.decode(key));

        String mac = new String(Macs.mac(new StringBuffer(text), k));
        k.clearText();

        return mac;
    }
}
