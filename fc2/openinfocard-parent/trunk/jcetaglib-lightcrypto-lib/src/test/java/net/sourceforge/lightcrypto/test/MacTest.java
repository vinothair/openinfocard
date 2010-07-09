package net.sourceforge.lightcrypto.test;

import junit.framework.TestCase;
import junit.framework.Assert;

import java.io.*;

import net.sourceforge.lightcrypto.Key;
import net.sourceforge.lightcrypto.SafeObject;
import net.sourceforge.lightcrypto.Macs;

/**
 * A collection of HMAC tests
 * <P>
 * These tests can be run using JUnit (http://www.junit.org)
 *
 * @author Gert Van Ham
 * @author hamgert@users.sourceforge.net
 * @author http://jcetaglib.sourceforge.net
 * @version $Id: MacTest.java,v 1.1 2003/10/05 11:41:29 hamgert Exp $
 */

public class MacTest extends TestCase {
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

    StringBuffer mac = null;
    StringBuffer plaintext = null;

    /**
     * setup test
     *
     * @throws java.io.IOException
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
     * test MAC
     *
     * @throws Exception
     */
    public void testMac() throws Exception {
        // generate a key
        Key.generatekey(RunTest.TEMPFOLDER + "tempkey.key", new StringBuffer("password"));

        SafeObject k = new SafeObject();
        k = Key.loadkey(RunTest.TEMPFOLDER + "tempkey.key", new StringBuffer("password"));

        mac = Macs.mac(text1, k);
        Assert.assertTrue(Macs.macEquals(text1, mac, k));

        mac = Macs.mac(text2, k);
        Assert.assertTrue(Macs.macEquals(text2, mac, k));

        mac = Macs.mac(text3, k);
        Assert.assertTrue(Macs.macEquals(text3, mac, k));

        mac = Macs.mac(text4, k);
        Assert.assertTrue(Macs.macEquals(text4, mac, k));

        mac = Macs.mac(text5, k);
        Assert.assertTrue(Macs.macEquals(text5, mac, k));

        mac = Macs.mac(text6, k);
        Assert.assertTrue(Macs.macEquals(text6, mac, k));

        mac = Macs.mac(text7, k);
        Assert.assertTrue(Macs.macEquals(text7, mac, k));

        mac = Macs.mac(text8, k);
        Assert.assertTrue(Macs.macEquals(text8, mac, k));

        mac = Macs.mac(text9, k);
        Assert.assertTrue(Macs.macEquals(text9, mac, k));

        mac = Macs.mac(text10, k);
        Assert.assertTrue(Macs.macEquals(text10, mac, k));

        mac = Macs.mac(text10, k);
        Assert.assertFalse(Macs.macEquals(text9, mac, k));
    }

    /**
    * test file encryption
    *
    * @throws Exception
    */
   public void testFileEncryption() throws Exception {
        SafeObject k = new SafeObject();
        k = Key.loadkey(RunTest.TEMPFOLDER + "tempkey.key",new StringBuffer("password"));

        mac = Macs.macFromFile(RunTest.TEMPFOLDER + "readable.txt",k);
        Assert.assertTrue(Macs.macEqualsFile(RunTest.TEMPFOLDER + "readable.txt",mac,k));

    }
}
