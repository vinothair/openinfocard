/*
  Name:         net.sourceforge.lightcrypto.test.RunTest
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

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * A collection of tests for net.sourceforge.lightcrypto package
 * <P>
 * These tests can be run using JUnit (http://www.junit.org)
 *
 * @author Gert Van Ham
 * @author hamgert@users.sourceforge.net
 * @author http://jcetaglib.sourceforge.net
 * @version $Id: RunTest.java,v 1.2 2003/10/05 11:41:29 hamgert Exp $
 */

public class RunTest {

     public final static String TEMPFOLDER = "c:/temp/";

     /**
      * Returns a test suite to the JUnit test runner.
      *
      * @return suite of tests
      */
     public static Test suite() {
        TestSuite suite = new TestSuite("net.sourceforge.lightcrypto");
        suite.addTestSuite(DigestTest.class);
        suite.addTestSuite(KeyTest.class);
        suite.addTestSuite(CryptTest.class);
        suite.addTestSuite(HMacTest.class);
        suite.addTestSuite(MacTest.class);
        suite.addTestSuite(HsqldbTest.class);
        return suite;
    }

    /**
     * Main method to run the tests from the command line
     *
     * @param args
     */
    public static void main(String args[]) {
        junit.textui.TestRunner.run(suite());
    }
}
