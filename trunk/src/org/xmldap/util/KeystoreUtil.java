/*
 * Copyright (c) 2006, Chuck Mortimore - xmldap.org
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
 *     * Neither the names xmldap, xmldap.org, xmldap.com nor the
 *       names of its contributors may be used to endorse or promote products
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
 */

package org.xmldap.util;

import org.xmldap.exceptions.KeyStoreException;
import org.xmldap.exceptions.TokenIssuanceException;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;


public class KeystoreUtil {

    private KeyStore keystore = null;
    private String keystorePath = null;
    private String keystorePassword = null;

    public KeystoreUtil(String keystorePath, String keystorePassword) throws KeyStoreException {
        load(keystorePath, keystorePassword, "JKS");
        this.keystorePath = keystorePath;
        this.keystorePassword = keystorePassword;
    }

    //TODO - fix this to use types in constructor
    public KeystoreUtil(String keystorePath, String keystorePassword, String type) throws KeyStoreException {
        load(keystorePath, keystorePassword, type);
    }

	public void storeCert(
			String cardCertNickname,
			X509Certificate cardCert,
			PrivateKey signingKey,
			String keyPassword)
			throws KeyStoreException {
			try {
				if (keystore.containsAlias(cardCertNickname)) {
					throw new KeyStoreException("duplicate certAlias");
				}
				Certificate[] chain = { cardCert };
				keystore.setKeyEntry(cardCertNickname, signingKey, keyPassword
						.toCharArray(), chain);
				FileOutputStream fos = new java.io.FileOutputStream(keystorePath);
				keystore.store(fos, keystorePassword.toCharArray());
				fos.close();
			} catch (java.security.KeyStoreException e) {
				throw new KeyStoreException(e);
			} catch (FileNotFoundException e) {
				throw new KeyStoreException(e);
			} catch (NoSuchAlgorithmException e) {
				throw new KeyStoreException(e);
			} catch (CertificateException e) {
				throw new KeyStoreException(e);
			} catch (IOException e) {
				throw new KeyStoreException(e);
			}

	}

	private void load(String keystorePath, String keystorePassword, String type) throws KeyStoreException {
		try {
            this.keystore = KeyStore.getInstance(type);
            keystore.load(new FileInputStream(keystorePath), keystorePassword.toCharArray());
        } catch (IOException e) {
            throw new KeyStoreException(e);
        } catch (NoSuchAlgorithmException e) {
            throw new KeyStoreException(e);
        } catch (CertificateException e) {
            throw new KeyStoreException(e);
        } catch (java.security.KeyStoreException e) {
            throw new KeyStoreException(e);
        }
	}

    public X509Certificate getCertificate(String alias) throws KeyStoreException {

        X509Certificate cert = null;
        try {
            cert = (X509Certificate) keystore.getCertificate(alias);
        } catch (java.security.KeyStoreException e) {
            throw new KeyStoreException("Error fetching Cert: " + alias, e);
        }
        return cert;

    }


    public PrivateKey getPrivateKey(String alias, String password) throws KeyStoreException {

        PrivateKey privateKey = null;
        try {
            privateKey = (PrivateKey) keystore.getKey(alias, password.toCharArray());
        } catch (java.security.KeyStoreException e) {
            throw new KeyStoreException("Error fetching PrivateKey: " + alias, e);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnrecoverableKeyException e) {
            e.printStackTrace();
        }

        return privateKey;

    }
    
}
