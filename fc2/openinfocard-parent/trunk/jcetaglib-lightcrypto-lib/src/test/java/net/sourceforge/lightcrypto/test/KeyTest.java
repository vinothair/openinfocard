/*
  Name:         net.sourceforge.lightcrypto.test.KeyTest
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
import net.sourceforge.lightcrypto.Key;
import net.sourceforge.lightcrypto.SafeObject;

/**
 * A collection of key tests
 * <P>
 * These tests can be run using JUnit (http://www.junit.org)
 *
 * @author Gert Van Ham
 * @author hamgert@users.sourceforge.net
 * @author http://jcetaglib.sourceforge.net
 * @version $Id: KeyTest.java,v 1.2 2003/10/05 11:41:29 hamgert Exp $
 */

public class KeyTest extends TestCase {
    /**
     * test key generation
     *
     * @throws Exception
     */
    public void testGenerateKey() throws Exception {
        Key.generatekey(RunTest.TEMPFOLDER + "tempkey.key",new StringBuffer("password"), new StringBuffer("123456789"));
    }

    /**
     * test key loading
     *
     * @throws Exception
     */
    public void testLoadKey() throws Exception {
        SafeObject k = new SafeObject();
        k = Key.loadkey(RunTest.TEMPFOLDER + "tempkey.key",new StringBuffer("password"));
    }
}
