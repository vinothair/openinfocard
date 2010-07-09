/*
  Name:         net.sourceforge.lightcrypto.Key
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

import org.bouncycastle.crypto.*;
import org.bouncycastle.crypto.digests.SHA1Digest;
import org.bouncycastle.crypto.engines.TwofishEngine;
import org.bouncycastle.crypto.generators.PKCS12ParametersGenerator;
import org.bouncycastle.crypto.modes.CBCBlockCipher;
import org.bouncycastle.crypto.paddings.PaddedBufferedBlockCipher;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.security.SecureRandom;

/**
 * Class to generate and read symmetric (shared) keys with the Bouncycastle lightweight API.
 * Keys are wrapped with PBE (Password-Based Encryption)
 *
 * @author Gert Van Ham
 * @author hamgert@users.sourceforge.net
 * @author http://jcetaglib.sourceforge.net
 * @version $Id: Key.java,v 1.2 2003/10/05 11:41:29 hamgert Exp $
 */

public class Key {
    private static int SecKeystoreCount = 100;
    private static int SecKeystoreKeylength = 256;
    private static int SecKeylength = 128;

    /**
     * Constructor
     **/
    public Key() {
    }

    /**
     * Generates a symmetric key, wraps it up and stores it in a file
     *
     * @param file the filename to store the symmetric key
     * @param passphrase the passphrase for the symmetric key
     * @exception CryptoException for all errors
     **/
    public static void generatekey(
            String file
            , StringBuffer passphrase) throws CryptoException {
        generatekey(file, passphrase, null);
    }

    /**
     * Generates a symmetric key, wraps it up and stores it in a file
     *
     * @param file the filename to store the symmetric key
     * @param passphrase the passphrase for the symmetric key
     * @param seed the seed for SecureRandom (used for generating the PBE salt)
     * @exception CryptoException for all errors
     **/
    public static void generatekey(
            String file
            , StringBuffer passphrase
            , StringBuffer seed
            ) throws CryptoException {

        FileOutputStream fos = null;
        byte[] key = null;
        byte[] wrappedkey = null;
        byte[] newkey = null;

        try {
            SecureRandom sr = new SecureRandom();

            // set seed if available
            if (seed != null && !seed.equals("")) {
                sr.setSeed(seed.toString().getBytes());
                // clean the seed from memory
                Clean.blank(seed);
            }

            KeyGenerationParameters keygen = new KeyGenerationParameters(sr, SecKeylength);
            CipherKeyGenerator cipherkey = new CipherKeyGenerator();
            cipherkey.init(keygen);

            // generate the key
            key = cipherkey.generateKey();

            // create random salt
            byte[] randomsalt = new byte[8];
            sr.nextBytes(randomsalt);

            // create the PBE cipher
            PBEParametersGenerator generator = new PKCS12ParametersGenerator(new SHA1Digest());
            generator.init(PBEParametersGenerator.PKCS12PasswordToBytes(passphrase.toString().toCharArray()), randomsalt, SecKeystoreCount);

            // clean the passphrase from memory
            Clean.blank(passphrase);

            TwofishEngine blockCipher = new TwofishEngine();
            CBCBlockCipher cbcCipher = new CBCBlockCipher(blockCipher);
            BufferedBlockCipher cipher = new PaddedBufferedBlockCipher(cbcCipher);

            CipherParameters param = generator.generateDerivedParameters(SecKeystoreKeylength,128);

            // encrypt the symmetric key with PBE
            cipher.init(true, param);

            int outputLen = 0;
            wrappedkey = new byte[cipher.getOutputSize(key.length)];
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            // process cipher
            outputLen = cipher.processBytes(key, 0, key.length, wrappedkey, 0);

            if (outputLen > 0) {
                baos.write(wrappedkey, 0, outputLen);
            }

            // process last bytes
            outputLen = cipher.doFinal(wrappedkey, 0);

            if (outputLen > 0) {
                baos.write(wrappedkey, 0, outputLen);
            }

            // save the wrapped key to disk
            fos = new FileOutputStream(file);
            fos.write(randomsalt);
            fos.write(baos.toByteArray());
            fos.close();

            baos.close();
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new CryptoException(ex.getMessage());
        } finally {
            // close the outputstream
            if (fos != null) {
                try {
                    fos.close();
                } catch (Exception e) {
                    ;
                }
                fos = null;
            }

            // clean sensitive information from memory
            Clean.blank(passphrase);
            if (seed != null) {
                Clean.blank(seed);
                seed = null;
            }
            if (key != null) {
                Clean.blank(key);
                key = null;
            }
            if (wrappedkey != null) {
                Clean.blank(wrappedkey);
                wrappedkey = null;
            }
            if (newkey != null) {
                Clean.blank(wrappedkey);
                wrappedkey = null;
            }
        }
    }

    /**
     * Load a symmetric key from the file, unwrap it and return the key bytes
     *
     * @param file the filename where the symmetric key is stored
     * @param passphrase the passphrase for the symmetric key
     * @return the key as a SafeObject
     * @exception KeyException when the key could not be loaded
     * @exception CryptoException for all errors
     **/
    public static SafeObject loadkey(
            String file
            , StringBuffer passphrase) throws CryptoException, KeyException {

        FileInputStream fInput = null;
        byte[] keybytes = null;

        try {
            fInput = new FileInputStream(file);

            // read the salt
            byte[] randomsalt = new byte[8];
            fInput.read(randomsalt);

            // read the wrapped key
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            int i = 0;
            while ((i = fInput.read()) != -1) {
                baos.write(i);
            }
            fInput.close();
            byte[] wrappedkey = baos.toByteArray();
            baos.close();

            // Create the PBE cipher
            PBEParametersGenerator generator = new PKCS12ParametersGenerator(new SHA1Digest());
            generator.init(PBEParametersGenerator.PKCS12PasswordToBytes(passphrase.toString().toCharArray()), randomsalt, SecKeystoreCount);

            // clean the passphrase from memory
            Clean.blank(passphrase);

            TwofishEngine blockCipher = new TwofishEngine();
            CBCBlockCipher cbcCipher = new CBCBlockCipher(blockCipher);
            BufferedBlockCipher cipher = new PaddedBufferedBlockCipher(cbcCipher);

            CipherParameters param = generator.generateDerivedParameters(SecKeystoreKeylength,128);

            // decrypt the wrapped key with PBE
            cipher.init(false, param);

            int outputLen = 0;
            keybytes = new byte[cipher.getOutputSize(wrappedkey.length)];

            ByteArrayOutputStream baos2 = new ByteArrayOutputStream();

            // process cipher
            outputLen = cipher.processBytes(wrappedkey, 0, wrappedkey.length, keybytes, 0);

            if (outputLen > 0) {
                baos2.write(keybytes, 0, outputLen);
            }

            // process last bytes
            outputLen = cipher.doFinal(keybytes, 0);

            if (outputLen > 0) {
                baos2.write(keybytes, 0, outputLen);
            }

            SafeObject sf = new SafeObject();
            sf.setText(baos2.toByteArray());

            baos2.close();

            return sf;
        } catch (FileNotFoundException fnfe) {
            throw new KeyException("Unable to load key from keystore \"" + file + "\" - keystore could not be found");
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new CryptoException(ex.getMessage());
        } finally {
            // close the inputstream
            if (fInput != null) {
                try {
                    fInput.close();
                } catch (Exception e) {
                    ;
                }
                fInput = null;
            }

            // clean sensitive information from memory
            Clean.blank(passphrase);
        }
    }

    /**
     * Sets the PBE iteration count (default = 100)
     *
     * @param secKeystoreCount iteration count
     **/
    public void setSecKeystoreCount(int secKeystoreCount) {
        SecKeystoreCount = secKeystoreCount;
    }

    /**
     * Sets the PBE key length (default = 256)
     *
     * @param secKeystoreKeylength PBE key length in bits
     **/
    public static void setSecKeystoreKeylength(int secKeystoreKeylength) {
        SecKeystoreKeylength = secKeystoreKeylength;
    }

    /**
     * Sets the AES keybits (default = 256)
     *
     * @param secKeylength key length in bits
     **/
    public void setSecKeylength(int secKeylength) {
        SecKeylength = secKeylength;
    }
}