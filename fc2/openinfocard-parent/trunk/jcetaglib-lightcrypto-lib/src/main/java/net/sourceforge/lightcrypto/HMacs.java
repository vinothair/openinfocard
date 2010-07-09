/*
  Name:         net.sourceforge.lightcrypto.HMacs
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

import org.bouncycastle.crypto.digests.SHA1Digest;
import org.bouncycastle.crypto.macs.HMac;
import org.bouncycastle.util.encoders.Base64;

import java.io.*;

/**
 * Create HMAC (Hash Message Authentication Code) from text and files
 *
 * @author Gert Van Ham
 * @author hamgert@users.sourceforge.net
 * @author http://jcetaglib.sourceforge.net
 * @version $Id: HMacs.java,v 1.1 2003/10/05 11:41:29 hamgert Exp $
 */

public class HMacs {
    private static int BUFFERSIZE_TEXT = 64;
    private static int BUFFERSIZE_FILE = 8192;

    /**
     * Creates an HMAC from a stringbuffer
     *
     * @param text creates HMAC from this text
     * @return HMAC in BASE64 format
     * @throws CryptoException for HMAC errors
     */
    public static StringBuffer mac(
            StringBuffer text
            ) throws CryptoException {
        return mac(new ByteArrayInputStream(text.toString().getBytes()), BUFFERSIZE_TEXT);
    }

    /**
     * Creates an HMAC from an inputstream
     *
     * @param is any inputstream
     * @param buffersize the buffersize in number of bytes
     * @return HMAC in BASE64 format
     * @throws CryptoException for HMAC errors
     */
    public static StringBuffer mac(
            InputStream is
            , int buffersize
            ) throws CryptoException {

        try {
            HMac hmac = new HMac(new SHA1Digest());

            byte[] result = new byte[hmac.getMacSize()];
            byte[] buffer = new byte[buffersize];
            int length = 0;

            // read bytes into buffer and feed these bytes into Hmac object
            while ((length = is.read(buffer)) != -1) {
                hmac.update(buffer, 0, length);
            }

            hmac.doFinal(result, 0);

            return new StringBuffer(new String(Base64.encode(result)));
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new CryptoException(ex.getMessage());
        }
    }

    /**
     * Creates an HMAC from a file
     *
     * @param file filename
     * @return HMAC in BASE64 format
     * @throws CryptoException for HMAC errors
     * @throws FileNotFoundException when the file was not found
     * @throws IOException when the file could not be opened (or closed)
     */
    public static StringBuffer macFromFile(
            String file
            ) throws CryptoException, FileNotFoundException, IOException {

        FileInputStream fis = new FileInputStream(file);
        StringBuffer res = mac(fis, BUFFERSIZE_FILE);
        fis.close();
        return res;
    }
}
