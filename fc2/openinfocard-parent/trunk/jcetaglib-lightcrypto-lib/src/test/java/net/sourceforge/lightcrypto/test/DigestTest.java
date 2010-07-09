/*
  Name:         net.sourceforge.lightcrypto.test.DigestTest
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

import junit.framework.Assert;
import junit.framework.TestCase;
import net.sourceforge.lightcrypto.Digesters;

import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * A collection of digest tests
 * <P>
 * These tests can be run using JUnit (http://www.junit.org)
 *
 * @author Gert Van Ham
 * @author hamgert@users.sourceforge.net
 * @author http://jcetaglib.sourceforge.net
 * @version $Id: DigestTest.java,v 1.2 2003/10/05 11:41:29 hamgert Exp $
 */

public class DigestTest extends TestCase {
    private StringBuffer text1;
    private StringBuffer text2;
    private StringBuffer text3;
    private StringBuffer text4;
    private StringBuffer text5;
    private StringBuffer text6;
    private StringBuffer text7;
    private StringBuffer text8;
    private StringBuffer text9;
    private StringBuffer text10;

    /**
     * setup test
     *
     * @throws IOException
     */
    protected void setUp() throws IOException {
        text1 = new StringBuffer("The Bouncy Castle Crypto package is a Java implementation of cryptographic algorithms");
        text2 = new StringBuffer("This software is distributed under a license based on the MIT X Consortium license");
        text3 = new StringBuffer("found in $JAVA_HOME/jre/lib/security/java.security, where $JAVA_HOME is the location of your JDK/JRE distribution");
        text4 = new StringBuffer("Mit Project 2002 zum erfolgreichen Projektmanagement Damit Sie in Zukunft Ihre Projekte präzise und komfortabel steuern können");
        text5 = new StringBuffer("En av de största nyheterna är att det finns en .NET Enterprise Server-lösning för stora företagsomspännade projekt");
        text6 = new StringBuffer("Lees de productinformatie en ontdek alles over de krachtige tools binnen Visual Studio .NET");
        text7 = new StringBuffer("Vergeet even die oude tovenaars met puntige hoeden en rondborstige jonkvrouwen in nood... oké, vergeet in ieder geval even die tovenaars, want Lionheart komt met een ambitieuze rollenspelvariant");
        text8 = new StringBuffer("An implementation of ECIES (stream mode) as described in IEEE P 1363a.");
        text9 = new StringBuffer("This makes the entire keystore resistant to tampering and inspection, and forces verification");
        text10 = new StringBuffer("application/pkcs7-signature;; x-java-content-handler=org.bouncycastle.mail.smime.handlers.pkcs7_signature");

        // create text file
        FileOutputStream outStr = new FileOutputStream(RunTest.TEMPFOLDER + "readable.txt");
        DataOutputStream dataStr = new DataOutputStream(outStr);

        dataStr.writeBytes("This is a readable string inside a file");

        dataStr.flush();
        dataStr.close();

        outStr.close();
    }

    /**
     * test digest
     *
     * @throws Exception
     */
    public void testDigest() throws Exception {
        Assert.assertEquals(Digesters.digest(text1, null).toString(), "GPMnLEpblugEcs2kmFkg3Q==");
        Assert.assertEquals(Digesters.digest(text2, null).toString(), "LPcC8hTqwv/qBcDUQdpx4w==");
        Assert.assertEquals(Digesters.digest(text3, null).toString(), "1pWkqCss4OvVPCv0fcSBgQ==");
        Assert.assertEquals(Digesters.digest(text4, null).toString(), "6kqKaRJHN1an+j+u2fUvHA==");
        Assert.assertEquals(Digesters.digest(text5, null).toString(), "KJ+Ve5/s8HDv/xu49lsf3g==");
        Assert.assertEquals(Digesters.digest(text6, null).toString(), "a7syvGijznXELm/sOctixw==");
        Assert.assertEquals(Digesters.digest(text7, null).toString(), "Pw3X59NMqZEYaOyKDPXn1g==");
        Assert.assertEquals(Digesters.digest(text8, null).toString(), "uA+eNem45Shm+4SWImwFDw==");
        Assert.assertEquals(Digesters.digest(text9, null).toString(), "jMU70cRMB3LnWkWrWhczBg==");
        Assert.assertEquals(Digesters.digest(text10, null).toString(), "BytgzY4DVd4pFgyQLK1rMw==");

        // try some other algorithms
        Assert.assertEquals(Digesters.digest(text8, "SHA1").toString(), "U81M41/RyigieCM/7xnIfO9cDyY=");
    }

    /**
     * test file digest
     *
     * @throws Exception
     */
    public void testFileDigest() throws Exception {
        Assert.assertEquals(Digesters.digestFromFile(RunTest.TEMPFOLDER + "readable.txt", null).toString(), "oHHF8dn23j348n/jrII7nA==");
    }
}
