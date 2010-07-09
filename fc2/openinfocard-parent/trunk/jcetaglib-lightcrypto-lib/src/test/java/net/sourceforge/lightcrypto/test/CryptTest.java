/*
  Name:         net.sourceforge.lightcrypto.test.CryptTest
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
import net.sourceforge.lightcrypto.Crypt;
import net.sourceforge.lightcrypto.Key;
import net.sourceforge.lightcrypto.SafeObject;

import java.io.*;

/**
 * A collection of encryption tests
 * <P>
 * These tests can be run using JUnit (http://www.junit.org)
 *
 * @author Gert Van Ham
 * @author hamgert@users.sourceforge.net
 * @author http://jcetaglib.sourceforge.net
 * @version $Id: CryptTest.java,v 1.2 2003/10/05 11:41:29 hamgert Exp $
 */

public class CryptTest extends TestCase {
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

    StringBuffer ciphertext = null;
    StringBuffer plaintext = null;

    /**
     * setup test
     *
     * @throws IOException
     */
    protected void setUp() throws IOException {
        text1= new StringBuffer("The Bouncy Castle Crypto package is a Java implementation of cryptographic algorithms");
        text2= new StringBuffer("This software is distributed under a license based on the MIT X Consortium license");
        text3= new StringBuffer("found in $JAVA_HOME/jre/lib/security/java.security, where $JAVA_HOME is the location of your JDK/JRE distribution");
        text4= new StringBuffer("Mit Project 2002 zum erfolgreichen Projektmanagement Damit Sie in Zukunft Ihre Projekte präzise und komfortabel steuern können");
        text5= new StringBuffer("En av de största nyheterna är att det finns en .NET Enterprise Server-lösning för stora företagsomspännade projekt");
        text6= new StringBuffer("Lees de productinformatie en ontdek alles over de krachtige tools binnen Visual Studio .NET");
        text7= new StringBuffer("Vergeet even die oude tovenaars met puntige hoeden en rondborstige jonkvrouwen in nood... oké, vergeet in ieder geval even die tovenaars, want Lionheart komt met een ambitieuze rollenspelvariant");
        text8= new StringBuffer("An implementation of ECIES (stream mode) as described in IEEE P 1363a.");
        text9= new StringBuffer("This makes the entire keystore resistant to tampering and inspection, and forces verification");
        text10= new StringBuffer("application/pkcs7-signature;; x-java-content-handler=org.bouncycastle.mail.smime.handlers.pkcs7_signature");

        // create text file
        FileOutputStream outStr = new FileOutputStream(RunTest.TEMPFOLDER + "readable.txt");
        DataOutputStream dataStr = new DataOutputStream(outStr);

        dataStr.writeBytes("This is a readable string inside a file");

        dataStr.flush();
        dataStr.close();

        outStr.close();
    }

    /**
     * test encryption
     *
     * @throws Exception
     */
    public void testEncryption() throws Exception {
        // generate a key
        Key.generatekey(RunTest.TEMPFOLDER + "tempkey.key",new StringBuffer("password"));

        SafeObject k = new SafeObject();
        k = Key.loadkey(RunTest.TEMPFOLDER + "tempkey.key",new StringBuffer("password"));

        // encrypt & decrypt
        ciphertext = Crypt.encrypt(text1,k);
        plaintext = Crypt.decrypt(ciphertext,k);

        Assert.assertEquals(text1.toString(), plaintext.toString());

        ciphertext = Crypt.encrypt(text2,k);
        plaintext = Crypt.decrypt(ciphertext,k);
        Assert.assertEquals(text2.toString(), plaintext.toString());

        ciphertext = Crypt.encrypt(text3,k);
        plaintext = Crypt.decrypt(ciphertext,k);
        Assert.assertEquals(text3.toString(), plaintext.toString());

        ciphertext = Crypt.encrypt(text4,k);
        plaintext = Crypt.decrypt(ciphertext,k);
        Assert.assertEquals(text4.toString(), plaintext.toString());

        ciphertext = Crypt.encrypt(text5,k);
        plaintext = Crypt.decrypt(ciphertext,k);
        Assert.assertEquals(text5.toString(), plaintext.toString());

        ciphertext = Crypt.encrypt(text6,k);
        plaintext = Crypt.decrypt(ciphertext,k);
        Assert.assertEquals(text6.toString(), plaintext.toString());

        ciphertext = Crypt.encrypt(text7,k);
        plaintext = Crypt.decrypt(ciphertext,k);
        Assert.assertEquals(text7.toString(), plaintext.toString());

        ciphertext = Crypt.encrypt(text8,k);
        plaintext = Crypt.decrypt(ciphertext,k);
        Assert.assertEquals(text8.toString(), plaintext.toString());

        ciphertext = Crypt.encrypt(text9,k);
        plaintext = Crypt.decrypt(ciphertext,k);
        Assert.assertEquals(text9.toString(), plaintext.toString());

        ciphertext = Crypt.encrypt(text10,k);
        plaintext = Crypt.decrypt(ciphertext,k);
        Assert.assertEquals(text10.toString(), plaintext.toString());

    }

   /**
    * test file encryption
    *
    * @throws Exception
    */
   public void testFileEncryption() throws Exception {
        SafeObject k = new SafeObject();
        k = Key.loadkey(RunTest.TEMPFOLDER + "tempkey.key",new StringBuffer("password"));

        Crypt.encryptFile(RunTest.TEMPFOLDER + "readable.txt",RunTest.TEMPFOLDER + "readable.txt.encrypted",k);
        Crypt.decryptFile(RunTest.TEMPFOLDER + "readable.txt.encrypted",RunTest.TEMPFOLDER + "readable.txt.decrypted",k);

        // read the file
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream("c:/temp/readable.txt.decrypted")));

        //int n = 0; // number of valid read characters
        StringBuffer line = new StringBuffer();
        int c;

        while ((c = reader.read()) != -1) {
            //n++;
            line.append((char) c);
        }

        reader.close();

        String t = line.toString();

        Assert.assertEquals("This is a readable string inside a file", t);
    }
}
