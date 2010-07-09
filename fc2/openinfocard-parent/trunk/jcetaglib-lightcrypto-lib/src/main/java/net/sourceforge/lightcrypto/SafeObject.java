/*
  Name:         net.sourceforge.lightcrypto.SafeObject
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

/**
 * Object containing text that can be removed from memory by calling clearText() 
 * method
 *
 * @author Gert Van Ham
 * @author hamgert@users.sourceforge.net
 * @author http://jcetaglib.sourceforge.net
 * @version $Id: SafeObject.java,v 1.2 2003/10/05 11:41:29 hamgert Exp $
 */

public class SafeObject {
    private byte[] safetext;

   /**
    * Set text
    *
    * @param s byte[] byte array of text
    * @exception Exception for all errors 
    **/  
    public void setText (byte[] s)
     throws Exception {   
    	this.safetext = s;
    }
    
   /**
    * Retrieve text
    *
    * @return text
    * @exception Exception for all errors 
    **/  
    public StringBuffer getText () 
     throws Exception { 
    	return new StringBuffer(new String(safetext));
    }

    /**
    * Retrieve text as a BASE64 string
    *
    * @return text in BASE64 format
    * @exception Exception for all errors
    **/
    public String getBase64 ()
     throws Exception {
    	return new String(Base64.encode(this.safetext));
    }

    /**
    * Get text length
    *
    * @return text length
    * @exception Exception for all errors
    **/
    public int getLength ()
     throws Exception {
    	return safetext.length;
    }

    /**
    * Retrieve bytes
    *
    * @return text
    * @exception Exception for all errors
    **/
    public byte[] getBytes ()
     throws Exception {
    	return safetext;
    }

   /**
    * Wipe text from memory   
    *
    * @exception Exception for all errors  
    **/  
    public void clearText () 
     throws Exception {
    	for (int i = 0; i < safetext.length; i++)
      {
      	safetext[i] = 0;
      }
    }   	        	
}    
	