package net.sourceforge.lightcrypto.test;

import junit.framework.Assert;
import junit.framework.TestCase;
import net.sourceforge.lightcrypto.HMacs;

import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * A collection of HMAC tests
 * <P>
 * These tests can be run using JUnit (http://www.junit.org)
 *
 * @author Gert Van Ham
 * @author hamgert@users.sourceforge.net
 * @author http://jcetaglib.sourceforge.net
 * @version $Id: HMacTest.java,v 1.1 2003/10/05 11:41:29 hamgert Exp $
 */

public class HMacTest extends TestCase {
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
     * @throws java.io.IOException
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
     * test HMAC
     *
     * @throws Exception
     */
     public void testHmac() throws Exception {
        Assert.assertEquals(HMacs.mac(text1).toString(),"PhX3UR5UV8g2JSwzaIMhmbbGzcs=");
        Assert.assertEquals(HMacs.mac(text2).toString(),"xLlNhfjrcyl6rOmDUDDdaxYBVeA=");
        Assert.assertEquals(HMacs.mac(text3).toString(),"YBmWah8XC1DaMz6z+5XJIAfaU/A=");
        Assert.assertEquals(HMacs.mac(text4).toString(),"F3uuSWd1wy+nzed0i8/HU1Qw7iw=");
        Assert.assertEquals(HMacs.mac(text5).toString(),"cCa/XPXAEi1wlxgObcjiYkTxU+w=");
        Assert.assertEquals(HMacs.mac(text6).toString(),"6xkWTygLs3nyKLPkQqQeEXgBJ98=");
        Assert.assertEquals(HMacs.mac(text7).toString(),"ag9+ZfwJDzeYiXbSFrCGT/cTs3M=");
        Assert.assertEquals(HMacs.mac(text8).toString(),"AeC346vUfMYmD4v5LeMdbPvixkU=");
        Assert.assertEquals(HMacs.mac(text9).toString(),"w0Hajv1tjcN1xDX+QyLcx77GiCw=");
        Assert.assertEquals(HMacs.mac(text10).toString(),"2agRqn7w1PHDt52VVs3z/EK1s8M=");
     }

    /**
     * test file Hmac
     *
     * @throws Exception
     */
    public void testFileHmac() throws Exception {
        Assert.assertEquals(HMacs.macFromFile(RunTest.TEMPFOLDER + "readable.txt").toString(), "88L5GNtKuj/RgJCi+E9Cxv1YLOM=");
    }
}
