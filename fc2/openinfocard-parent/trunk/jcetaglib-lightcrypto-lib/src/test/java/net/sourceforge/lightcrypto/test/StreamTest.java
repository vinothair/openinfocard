/*
  Name:         net.sourceforge.lightcrypto.test.StreamTest
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

package net.sourceforge.lightcrypto.test;

import junit.framework.TestCase;
import junit.framework.Assert;

import java.io.*;

import net.sourceforge.lightcrypto.SafeObject;
import net.sourceforge.lightcrypto.Key;
import net.sourceforge.lightcrypto.Stream;

/**
 * A collection of stream cipher tests
 * <P>
 * These tests can be run using JUnit (http://www.junit.org)
 *
 * @author Gert Van Ham
 * @author hamgert@users.sourceforge.net
 * @author http://jcetaglib.sourceforge.net
 * @version $Id: StreamTest.java,v 1.1 2003/10/28 20:12:54 hamgert Exp $
 */
public class StreamTest extends TestCase {
     /**
     * setup test
     *
     * @throws java.io.IOException
     */
    protected void setUp() throws IOException {
        // create text file
        FileOutputStream outStr = new FileOutputStream(RunTest.TEMPFOLDER + "readable.txt");
        DataOutputStream dataStr = new DataOutputStream(outStr);

        dataStr.writeBytes("This is a readable string inside a file");

        dataStr.flush();
        dataStr.close();

        outStr.close();
    }

    /**
    * test file encryption
    *
    * @throws Exception
    */
   public void testStream() throws Exception {
        // generate a key
        Key.generatekey(RunTest.TEMPFOLDER + "tempkey.key",new StringBuffer("password"));

        SafeObject k = new SafeObject();
        k = Key.loadkey(RunTest.TEMPFOLDER + "tempkey.key",new StringBuffer("password"));

        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        DataOutputStream dao = new DataOutputStream(bao);
        ByteArrayOutputStream bao2 = new ByteArrayOutputStream();
        DataOutputStream dao2 = new DataOutputStream(bao2);

        Stream.encrypt(new FileInputStream(RunTest.TEMPFOLDER + "readable.txt"),dao,k,64);
        Stream.decrypt(new ByteArrayInputStream(bao.toByteArray()),dao2,k,64);


        Assert.assertEquals("This is a readable string inside a file", new String(bao2.toByteArray()));

        dao.close();
        dao2.close();
    }

}
