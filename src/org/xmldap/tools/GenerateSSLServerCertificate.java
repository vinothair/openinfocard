/*
 * Copyright (c) 2006, Axel Nennker - http://axel.nennker.de/
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * The names of the contributors may NOT be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE REGENTS AND CONTRIBUTORS ``AS IS'' AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE REGENTS AND CONTRIBUTORS BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * 
 */package org.xmldap.tools;

import org.bouncycastle.asn1.x509.X509Name;
import org.xmldap.exceptions.TokenIssuanceException;
import org.xmldap.util.CertsAndKeys;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

public class GenerateSSLServerCertificate {

	/**
	 * @param args
	 * @throws NoSuchProviderException 
	 * @throws NoSuchAlgorithmException 
	 * @throws TokenIssuanceException 
	 * @throws KeyStoreException 
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 * @throws CertificateException 
	 * @throws SignatureException 
	 * @throws SecurityException 
	 * @throws InvalidKeyException 
	 */
	public static void main(String[] args) throws NoSuchAlgorithmException,
			NoSuchProviderException, KeyStoreException,
			CertificateException, FileNotFoundException, IOException, InvalidKeyException, SecurityException, SignatureException {
		KeyPair kp = CertsAndKeys.generateKeyPair();
		X509Name issuer = new X509Name(
				"CN=w4de3esy0069028.gdc-bln01.t-systems.com, OU=SSC ENPS, O=T-Systems, L=Berlin, ST=Berln, C=DE");
		X509Name subject = issuer;
		X509Certificate cert = CertsAndKeys.generateSSLServerCertificate(kp,
				issuer, subject);

		String keystorePath = "";
        String tmpdir = 
            System.getProperty("java.io.tmpdir");
        if (tmpdir != null) {
        	keystorePath = tmpdir + "keystore.jks";
        } else {
        	File[] roots = File.listRoots();
        	keystorePath = roots[0].getPath() + "keystore.jks";
        }
		String storePassword = "changeit";
		String cardCertNickname = "tomcat";
		String keyPassword = "changeit";
		KeyStore ks = KeyStore.getInstance("JKS");
		ks.load(null, storePassword.toCharArray());
		Certificate[] chain = { cert };
		ks.setKeyEntry(cardCertNickname, kp.getPrivate(), keyPassword
				.toCharArray(), chain);
		File file = new File(keystorePath);
		file.createNewFile();
		FileOutputStream fos = new java.io.FileOutputStream(file);
		ks.store(fos, storePassword.toCharArray());
		fos.close();
		
		System.out.println("saved keystore to: " + keystorePath);
	}

}
