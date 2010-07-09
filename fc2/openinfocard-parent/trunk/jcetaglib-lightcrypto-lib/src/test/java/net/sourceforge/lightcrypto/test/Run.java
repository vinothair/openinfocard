package net.sourceforge.lightcrypto.test;

import net.sourceforge.lightcrypto.*;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.ByteArrayInputStream;

import org.bouncycastle.util.encoders.Base64;

/**
 * Created by IntelliJ IDEA.
 * User: u997879
 * Date: Aug 28, 2003
 * Time: 9:38:00 AM
 * To change this template use Options | File Templates.
 */
public class Run {
      public static void main(String args[]) {
         try  {



             System.out.println("ciphertext:" + PBECrypt.decrypt(new StringBuffer("DW5WQMZuGkfKEg80QqDRrG1kOZ9gI/Pd"),new StringBuffer("mypassword")));


             System.out.println("plain:" + PBECrypt.encrypt(new StringBuffer("this is A test"),new StringBuffer("mypassword")));

             /*Key k = new Key();

             //Key.generatekey("c:/temp/tempkey.key",new StringBuffer("mypassword"));


             SafeObject sf = k.loadkey("c:/temp/AES_128.key",new StringBuffer("mypassword"));

             StringBuffer plaintext = Crypt.decrypt(new StringBuffer("MxXSSQireZV7BEl2FyTMzsY9dQz9GvtHdhRkmmNMl2M="), sf);

             System.out.println("ciphertext:" + plaintext);




        /*ByteArrayOutputStream bao = new ByteArrayOutputStream();
        DataOutputStream dao = new DataOutputStream(bao);

        // encrypt text
        Stream.encrypt(new ByteArrayInputStream("blabla".getBytes()), dao, sf, 8);

        StringBuffer result = new StringBuffer(new String(Base64.encode(bao.toByteArray())));

        System.out.println("cipher: " + result);



        // close outputstream
        dao.flush();
        dao.close();

        bao = new ByteArrayOutputStream();
        dao = new DataOutputStream(bao);

        // decrypt
        Stream.decrypt(new ByteArrayInputStream(Base64.decode(result.toString())), dao, sf, 8);

        System.out.println("decipher: " + new String(bao.toByteArray()));

        //close outputstream
        dao.flush();
        dao.close();



             //System.out.println(HMacs.mac(new StringBuffer("mytext")));


             //Key k = new Key();
             //Key.generatekey("c:/temp/tempkey.key",new StringBuffer("mypassword"));




             //System.out.println("Load key...");
             //SafeObject sf = k.loadkey("c:/temp/tempkey.key",new StringBuffer("mypassword"));


             //System.gc();

             //Thread.currentThread().sleep(5 * 1000);

             //System.out.println(sf.getBase64());

             //StringBuffer cipher = PBECrypt.encrypt(new StringBuffer("The Bouncy Castle Crypto package is a Java implementation of cryptographic algorithms"),new StringBuffer("mypassword"));

             //StringBuffer plaintext = Crypt.decrypt(new StringBuffer("7SDEw/aYBsZ0FnVyi+JbKGGS22kwPO+LBESAb1DPHdE="), sf);

             //System.out.println("ciphertext:" + cipher);

             //StringBuffer plain = PBECrypt.decrypt(cipher,new StringBuffer("mypassword"));

               //System.out.println("plain:" + plain);

             //Thread.currentThread().sleep(30 * 1000);

             //sf.clearText();
             //sf = null;

             //System.gc();


             //sf.clearText();

             //Thread.currentThread().sleep(5 * 1000);


             /*System.out.println("key:" + sf.getBase64());

             StringBuffer m = Macs.mac(new StringBuffer("mytext"),sf);

             System.out.println("mac:" + m);

              System.out.println("mac equals?:" + Macs.macEquals(new StringBuffer("mytext"), m,sf));

             // encrypt
             /*StringBuffer ciphertext = Crypt.encrypt(new StringBuffer("mytest"), sf);
             System.out.println("cipher: " + ciphertext);

             StringBuffer plaintext = Crypt.decrypt(ciphertext,sf);
             System.out.println("plaintext: " + plaintext);        */

             /*

             SafeObject sf = k.loadkey("c:/temp/tempkey.key",new StringBuffer("password"));

             System.out.println("key:" + sf.getBase64());

              System.out.println("key2:" +  Hsqldb.loadkey("c:/temp/tempkey.key",new StringBuffer("password")));
                                   */

            //System.out.println(Crypt.Digesters(new StringBuffer("mytextmytextmytextmytextmytextmytextmytextmytextmytextmytextmytextmytextmytextmytextmytextmytextmytext"),"SHA512"));
            //System.out.println(Digesters.digest("mytextmytextmytextmytextmytextmytextmytextmytextmytextmytextmytextmytextmytextmytextmytextmytextmytext","SHA512"));

            //System.out.println(Crypt.Digesters(new FileInputStream("c:/javabooks/j-jstl0415.pdf"),null));

            //Key.generatekey("c:/temp/test1.key",new StringBuffer("mypassword"), new StringBuffer("sdkdffddiejjdjd"));

            //String sqlkey = Hsqldb.loadkey("c:/temp/test1.key",new StringBuffer("mypassword"));

            //String cipher = Hsqldb.encrypt("mytest",sqlkey);

            //System.out.println(cipher);

            //String decipher = Hsqldb.decrypt(cipher,sqlkey);

            //System.out.println(decipher);

            //Key.generatekey("c:/temp/tempkey.key",new StringBuffer("mypassword"));

            //SafeObject k = new SafeObject();
            //k = Key.loadkey("c:/temp/AES_128.key",new StringBuffer("mypassword"));

            //System.out.println(k.getBase64());

            // encrypt
            //StringBuffer ciphertext = Crypt.encrypt(new StringBuffer("test"),k);
            //System.out.println("cipher: " + ciphertext);

            //SafeObject k2 = new SafeObject();
            //k2 = Key.loadkey("c:/temp/test1.key",new StringBuffer("mypassword"));


            //StringBuffer plaintext = Crypt.decrypt(ciphertext,k);
            //System.out.println("plaintext: " + plaintext);

             /*// encrypt file
             Crypt.encryptFile("c:/javabooks/jstl3.pdf", "c:/javabooks/jstl3.encrypted", k);

              // decrypt file
             Crypt.decryptFile("c:/javabooks/jstl3.encrypted", "c:/javabooks/jstl3.decrypted.pdf", k);
             */

             //System.out.println(Digesters.digestFromFile("c:/javabooks/jstl3.pdf",null));
             //System.out.println(Digesters.digest(new StringBuffer("balbal"),"SHA512"));

         } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println(ex.getMessage());
         }
     }
}
