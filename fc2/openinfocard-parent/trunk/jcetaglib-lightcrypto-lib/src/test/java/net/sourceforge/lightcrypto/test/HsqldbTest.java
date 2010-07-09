/*
  Name:         net.sourceforge.lightcrypto.test.HsqldbTest
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
import net.sourceforge.lightcrypto.*;

import java.sql.*;

/**
 * A collection of encryption tests for HSQLDB (http://hsqldb.sourceforge.net)
 * <P>
 * These tests can be run using JUnit (http://www.junit.org)
 *
 * @author Gert Van Ham
 * @author hamgert@users.sourceforge.net
 * @author http://jcetaglib.sourceforge.net
 * @version $Id: HsqldbTest.java,v 1.3 2003/10/28 20:12:54 hamgert Exp $
 */

public class HsqldbTest extends TestCase {
     private String text1;
     private String text2;
     private String text3;
     private String text4;
     private String text5;
     private String text6;
     private String text7;
     private String text8;
     private String text9;
     private String text10;

    Connection conn = null;
    Statement st = null;
    ResultSet rs = null;

    static boolean setup = false; 
    int i;

    /**
     * setup test (create database)
     *
     * @throws ClassNotFoundException
     * @throws SQLException
     */
    protected void setUp() throws ClassNotFoundException, SQLException {
    	
        text1= "The Bouncy Castle Crypto package is a Java implementation of cryptographic algorithms";
        text2= "This software is distributed under a license based on the MIT X Consortium license";
        text3= "found in $JAVA_HOME/jre/lib/security/java.security, where $JAVA_HOME is the location of your JDK/JRE distribution";
        text4= "Mit Project 2002 zum erfolgreichen Projektmanagement Damit Sie in Zukunft Ihre Projekte präzise und komfortabel steuern können";
        text5="En av de största nyheterna är att det finns en .NET Enterprise Server-lösning för stora företagsomspännade projekt";
        text6= "Lees de productinformatie en ontdek alles over de krachtige tools binnen Visual Studio .NET";
        text7= "Vergeet even die oude tovenaars met puntige hoeden en rondborstige jonkvrouwen in nood... oké, vergeet in ieder geval even die tovenaars, want Lionheart komt met een ambitieuze rollenspelvariant";
        text8= "An implementation of ECIES (stream mode) as described in IEEE P 1363a.";
        text9= "This makes the entire keystore resistant to tampering and inspection, and forces verification";
        text10= "application/pkcs7-signature;; x-java-content-handler=org.bouncycastle.mail.smime.handlers.pkcs7_signature";

        // create an in-process, in-memory database
        Class.forName("org.hsqldb.jdbcDriver");
        conn = DriverManager.getConnection("jdbc:hsqldb:.","sa","");

       // if(!setup){
	        try {
				st = conn.createStatement();				
		        rs = st.executeQuery("DROP TABLE sample_table");
		        st = conn.createStatement();
		        rs = st.executeQuery("DROP TABLE sample_table_2");
		        st = conn.createStatement();
		        rs = st.executeQuery("DROP TABLE sample_table_3");
			} catch (SQLException e) {
				
			}
	     
        	 // create the table        
            st = conn.createStatement();
            rs = st.executeQuery("CREATE TABLE sample_table ( id INTEGER IDENTITY, str_col VARCHAR(500))");

            // create second table
            rs = st.executeQuery("CREATE TABLE sample_table_2 ( id INTEGER IDENTITY, str_col VARCHAR(500))");

            // create third table
            rs = st.executeQuery("CREATE TABLE sample_table_3 ( id INTEGER IDENTITY, str_col VARCHAR(500))");


            st.close();
//            setup = true;
//        }
       

        // fill table
        st = conn.createStatement();
        i = st.executeUpdate("INSERT INTO sample_table(str_col) VALUES('" + text1 + "')");
        i = st.executeUpdate("INSERT INTO sample_table(str_col) VALUES('" + text2 + "')");
        i = st.executeUpdate("INSERT INTO sample_table(str_col) VALUES('" + text3 + "')");
        i = st.executeUpdate("INSERT INTO sample_table(str_col) VALUES('" + text4 + "')");
        i = st.executeUpdate("INSERT INTO sample_table(str_col) VALUES('" + text5 + "')");
        i = st.executeUpdate("INSERT INTO sample_table(str_col) VALUES('" + text6 + "')");
        i = st.executeUpdate("INSERT INTO sample_table(str_col) VALUES('" + text7 + "')");
        i = st.executeUpdate("INSERT INTO sample_table(str_col) VALUES('" + text8 + "')");
        i = st.executeUpdate("INSERT INTO sample_table(str_col) VALUES('" + text9 + "')");
        i = st.executeUpdate("INSERT INTO sample_table(str_col) VALUES('" + text10 + "')");

        st.close();
    }

    /**
     * test digest
     *
     * @throws Exception
     */
    public void testDigest() throws Exception {
        // query table
        st = conn.createStatement();
        rs = st.executeQuery("SELECT \"net.sourceforge.lightcrypto.Hsqldb.digest\"(str_col) as digest FROM sample_table ORDER BY id");

        rs.next();
        Assert.assertEquals(rs.getString(1),"GPMnLEpblugEcs2kmFkg3Q==");

        rs.next();
        Assert.assertEquals(rs.getString(1),"LPcC8hTqwv/qBcDUQdpx4w==");

        rs.next();
        Assert.assertEquals(rs.getString(1),"1pWkqCss4OvVPCv0fcSBgQ==");

        rs.next();
        Assert.assertEquals(rs.getString(1),"6kqKaRJHN1an+j+u2fUvHA==");

        rs.next();
        Assert.assertEquals(rs.getString(1),"KJ+Ve5/s8HDv/xu49lsf3g==");

        rs.next();
        Assert.assertEquals(rs.getString(1),"a7syvGijznXELm/sOctixw==");

        rs.next();
        Assert.assertEquals(rs.getString(1),"Pw3X59NMqZEYaOyKDPXn1g==");

        rs.next();
        Assert.assertEquals(rs.getString(1),"uA+eNem45Shm+4SWImwFDw==");

        rs.next();
        Assert.assertEquals(rs.getString(1),"jMU70cRMB3LnWkWrWhczBg==");

        rs.next();
        Assert.assertEquals(rs.getString(1),"BytgzY4DVd4pFgyQLK1rMw==");

        st.close();

        // try another algorithm
        st = conn.createStatement();
        rs = st.executeQuery("SELECT \"net.sourceforge.lightcrypto.Hsqldb.digest\"(str_col,'SHA1') as digest FROM sample_table WHERE id = 5");

        rs.next();
        st.close();
        Assert.assertEquals(rs.getString(1),"Y15d+lCFfNBbucduGH+rvoweQqw=");

    }

    /**
     * test encryption
     *
     * @throws Exception
     */
    public void testEncryption() throws Exception {
        // generate a key
        Key.generatekey(RunTest.TEMPFOLDER  + "tempkey.key",new StringBuffer("password"));

        StringBuffer mykey = Hsqldb.loadkey(RunTest.TEMPFOLDER  + "tempkey.key",new StringBuffer("password"));

        // fill table
        st = conn.createStatement();
        i = st.executeUpdate("INSERT INTO sample_table_2(str_col) VALUES(\"net.sourceforge.lightcrypto.Hsqldb.encrypt\"('" + text1 + "','" + mykey + "'))");
        i = st.executeUpdate("INSERT INTO sample_table_2(str_col) VALUES(\"net.sourceforge.lightcrypto.Hsqldb.encrypt\"('" + text2 + "','" + mykey + "'))");
        i = st.executeUpdate("INSERT INTO sample_table_2(str_col) VALUES(\"net.sourceforge.lightcrypto.Hsqldb.encrypt\"('" + text3 + "','" + mykey + "'))");
        i = st.executeUpdate("INSERT INTO sample_table_2(str_col) VALUES(\"net.sourceforge.lightcrypto.Hsqldb.encrypt\"('" + text4 + "','" + mykey + "'))");
        i = st.executeUpdate("INSERT INTO sample_table_2(str_col) VALUES(\"net.sourceforge.lightcrypto.Hsqldb.encrypt\"('" + text5 + "','" + mykey + "'))");
        i = st.executeUpdate("INSERT INTO sample_table_2(str_col) VALUES(\"net.sourceforge.lightcrypto.Hsqldb.encrypt\"('" + text6 + "','" + mykey + "'))");
        i = st.executeUpdate("INSERT INTO sample_table_2(str_col) VALUES(\"net.sourceforge.lightcrypto.Hsqldb.encrypt\"('" + text7 + "','" + mykey + "'))");
        i = st.executeUpdate("INSERT INTO sample_table_2(str_col) VALUES(\"net.sourceforge.lightcrypto.Hsqldb.encrypt\"('" + text8 + "','" + mykey + "'))");
        i = st.executeUpdate("INSERT INTO sample_table_2(str_col) VALUES(\"net.sourceforge.lightcrypto.Hsqldb.encrypt\"('" + text9 + "','" + mykey + "'))");
        i = st.executeUpdate("INSERT INTO sample_table_2(str_col) VALUES(\"net.sourceforge.lightcrypto.Hsqldb.encrypt\"('" + text10 + "','" + mykey + "'))");

        st.close();

        // clean key after usage
        Clean.blank(mykey);

        // load key again
        StringBuffer mykey2 = Hsqldb.loadkey(RunTest.TEMPFOLDER  + "tempkey.key",new StringBuffer("password"));

        st = conn.createStatement();
        rs = st.executeQuery("SELECT \"net.sourceforge.lightcrypto.Hsqldb.decrypt\"(str_col,'" + mykey2 + "') FROM sample_table_2 ORDER BY id");

        // clean key after usage
        Clean.blank(mykey2);

        rs.next();
        Assert.assertEquals(rs.getString(1),text1);

        rs.next();
        Assert.assertEquals(rs.getString(1),text2);

        rs.next();
        Assert.assertEquals(rs.getString(1),text3);

        rs.next();
        Assert.assertEquals(rs.getString(1),text4);

        rs.next();
        Assert.assertEquals(rs.getString(1),text5);

        rs.next();
        Assert.assertEquals(rs.getString(1),text6);

        rs.next();
        Assert.assertEquals(rs.getString(1),text7);

        rs.next();
        Assert.assertEquals(rs.getString(1),text8);

        rs.next();
        Assert.assertEquals(rs.getString(1),text9);

        rs.next();
        Assert.assertEquals(rs.getString(1),text10);

        st.close();
    }

     /**
     * test encryption
     *
     * @throws Exception
     */
    public void testPBEEncryption() throws Exception {

        // fill table
        st = conn.createStatement();
        i = st.executeUpdate("INSERT INTO sample_table_3(str_col) VALUES(\"net.sourceforge.lightcrypto.Hsqldb.PBEEncrypt\"('" + text1 + "','mypassword'))");
        i = st.executeUpdate("INSERT INTO sample_table_3(str_col) VALUES(\"net.sourceforge.lightcrypto.Hsqldb.PBEEncrypt\"('" + text2 + "','mypassword'))");
        i = st.executeUpdate("INSERT INTO sample_table_3(str_col) VALUES(\"net.sourceforge.lightcrypto.Hsqldb.PBEEncrypt\"('" + text3 + "','mypassword'))");
        i = st.executeUpdate("INSERT INTO sample_table_3(str_col) VALUES(\"net.sourceforge.lightcrypto.Hsqldb.PBEEncrypt\"('" + text4 + "','mypassword'))");
        i = st.executeUpdate("INSERT INTO sample_table_3(str_col) VALUES(\"net.sourceforge.lightcrypto.Hsqldb.PBEEncrypt\"('" + text5 + "','mypassword'))");
        i = st.executeUpdate("INSERT INTO sample_table_3(str_col) VALUES(\"net.sourceforge.lightcrypto.Hsqldb.PBEEncrypt\"('" + text6 + "','mypassword'))");
        i = st.executeUpdate("INSERT INTO sample_table_3(str_col) VALUES(\"net.sourceforge.lightcrypto.Hsqldb.PBEEncrypt\"('" + text7 + "','mypassword'))");
        i = st.executeUpdate("INSERT INTO sample_table_3(str_col) VALUES(\"net.sourceforge.lightcrypto.Hsqldb.PBEEncrypt\"('" + text8 + "','mypassword'))");
        i = st.executeUpdate("INSERT INTO sample_table_3(str_col) VALUES(\"net.sourceforge.lightcrypto.Hsqldb.PBEEncrypt\"('" + text9 + "','mypassword'))");
        i = st.executeUpdate("INSERT INTO sample_table_3(str_col) VALUES(\"net.sourceforge.lightcrypto.Hsqldb.PBEEncrypt\"('" + text10 + "','mypassword'))");

        st.close();

        st = conn.createStatement();
        rs = st.executeQuery("SELECT \"net.sourceforge.lightcrypto.Hsqldb.PBEDecrypt\"(str_col,'mypassword') FROM sample_table_3 ORDER BY id");

        rs.next();
        Assert.assertEquals(rs.getString(1),text1);

        rs.next();
        Assert.assertEquals(rs.getString(1),text2);

        rs.next();
        Assert.assertEquals(rs.getString(1),text3);

        rs.next();
        Assert.assertEquals(rs.getString(1),text4);

        rs.next();
        Assert.assertEquals(rs.getString(1),text5);

        rs.next();
        Assert.assertEquals(rs.getString(1),text6);

        rs.next();
        Assert.assertEquals(rs.getString(1),text7);

        rs.next();
        Assert.assertEquals(rs.getString(1),text8);

        rs.next();
        Assert.assertEquals(rs.getString(1),text9);

        rs.next();
        Assert.assertEquals(rs.getString(1),text10);

        st.close();
    }

    /**
     * test HMAC
     *
     * @throws Exception
     */
    public void testHMAC() throws Exception {

        // query table
        st = conn.createStatement();
        rs = st.executeQuery("SELECT \"net.sourceforge.lightcrypto.Hsqldb.hmac\"(str_col) as mac FROM sample_table ORDER BY id");

        rs.next();
        Assert.assertEquals(rs.getString(1),"PhX3UR5UV8g2JSwzaIMhmbbGzcs=");

        st.close();
    }

    /**
     * test MAC
     *
     * @throws Exception
     */
    public void testMAC() throws Exception {
        StringBuffer mykey = Hsqldb.loadkey(RunTest.TEMPFOLDER  + "tempkey.key",new StringBuffer("password"));

        SafeObject k = new SafeObject();
        k = Key.loadkey(RunTest.TEMPFOLDER + "tempkey.key", new StringBuffer("password"));


        // query table
        st = conn.createStatement();
        rs = st.executeQuery("SELECT \"net.sourceforge.lightcrypto.Hsqldb.mac\"(str_col,'" + mykey + "') as mac FROM sample_table ORDER BY id");

        rs.next();

        Assert.assertTrue(Macs.macEquals(new StringBuffer(text1), new StringBuffer(rs.getString(1)), k));
        rs.close();
        st.close();
    }

    /**
     * runs a the end of the tests (close database)
     *
     * @throws SQLException
     */
    protected void tearDown() throws SQLException {
        conn.close();
    }
}
