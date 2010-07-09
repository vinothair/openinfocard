/*
  Name:         net.sourceforge.lightcrypto.Clean
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

/**
 * Utitlity to clean byte arrays & stringbuffers
 *
 * @author Gert Van Ham
 * @author hamgert@users.sourceforge.net
 * @author http://jcetaglib.sourceforge.net
 * @version $Id: Clean.java,v 1.2 2003/10/05 11:41:29 hamgert Exp $
 */

public class Clean {
    /**
    * zero out the passed in byte array
    *
    * @param bytes common byte array
    **/
    static public void blank(
        byte[]   bytes)
    {
        for (int t = 0; t < bytes.length; t++)
        {
            bytes[t] = 0;
        }
    } // blank()

   /**
    * zero out the passed in StringBuffer
    *
    * @param stringbuffer StringBuffer[]
    **/
    static public void blank(
        StringBuffer   stringbuffer)
    {
    		if (stringbuffer != null) {
    			stringbuffer.delete(0,stringbuffer.length());
    		}
    } // blank()
}
