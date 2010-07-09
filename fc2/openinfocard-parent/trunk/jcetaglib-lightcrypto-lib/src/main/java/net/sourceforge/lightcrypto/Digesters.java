/*
  Name:         net.sourceforge.lightcrypto.Digesters
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

import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.digests.MD5Digest;
import org.bouncycastle.crypto.digests.SHA1Digest;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.util.encoders.Base64;

import java.io.*;

/**
 * digest and hash routines for use with the BouncyCastle lightweight API
 *
 * @author Gert Van Ham
 * @author hamgert@users.sourceforge.net
 * @author http://jcetaglib.sourceforge.net
 * @version $Id: Digesters.java,v 1.2 2003/10/05 11:41:29 hamgert Exp $
 */

public class Digesters {
    private static int BUFFERSIZE_TEXT = 64;
    private static int BUFFERSIZE_FILE = 8192;

    /**
     * Creates a digest from a stringbuffer
     *
     * @param text creates the digest from this text
     * @param algorithm the digest algorithm. Can be 'SHA1' or 'MD5' (default)
     * @return the generated digest string in BASE64
     * @exception CryptoException for all errors
     **/
    public static StringBuffer digest(
            StringBuffer text
            , String algorithm) throws CryptoException {
        return digest(new ByteArrayInputStream(text.toString().getBytes()), algorithm, BUFFERSIZE_TEXT);
    }

    /**
     * Creates a digest from an inputstream
     *
     * @param is inputstream
     * @param algorithm the digest algorithm. Can be 'SHA1' or 'MD5' (default)
     * @return the generated digest string in BASE64
     * @exception CryptoException for all errors
     **/
    public static StringBuffer digest(
            InputStream is
            , String algorithm
            , int buffersize
            ) throws CryptoException {
        Digest digest;

        try {
            if (algorithm != null) {
                if (algorithm.equalsIgnoreCase("SHA1")) {
                    digest = new SHA1Digest();
                } else if (algorithm.equalsIgnoreCase("SHA256")) {
                    digest = new SHA256Digest();
                } else {
                    digest = new MD5Digest();
                }
            } else {
                digest = new MD5Digest();
            }

            byte[] result = new byte[digest.getDigestSize()];
            byte[] buffer = new byte[buffersize];
            int length = 0;

            // read bytes into buffer and feed these bytes into the message digest object
            while ((length = is.read(buffer)) != -1) {
                digest.update(buffer, 0, length);
            }

            digest.doFinal(result, 0);

            return new StringBuffer(new String(Base64.encode(result)));
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new CryptoException(ex.getMessage());
        }
    }

     /**
     * Creates a digest from a file
     *
     * @param file creates the digest from this file
     * @param algorithm the digest algorithm. Can be 'SHA1' or 'MD5' (default)
     * @return the generated digest string in BASE64
     * @exception CryptoException for digest errors
     * @throws FileNotFoundException when the file was not found
     * @throws IOException when the file could not be opened (or closed)
     **/
     public static StringBuffer digestFromFile(
            String file
             , String algorithm
            ) throws CryptoException, FileNotFoundException, IOException {

        FileInputStream fis = new FileInputStream(file);
        StringBuffer res = digest(fis, algorithm, BUFFERSIZE_FILE);
        fis.close();
        return res;
    }
}
