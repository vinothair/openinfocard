/*
 * Copyright (c) 2006 Informed Control Inc. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of Informed Control Inc. or the
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

import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.security.cert.CertificateEncodingException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

/**
 * Extracts configuration parameters from a ServletConfig. 
 */
public class ServletUtil {
    public static final String PARAM_KEYSTORE = "keystore";
    public static final String PARAM_KEYSTORE_PASSWORD = "keystore-password";
    public static final String PARAM_KEY_PASSWORD = "key-password";
    public static final String PARAM_KEYNAME = "key";
    public static final String PARAM_DOMAIN = "domain";
    public static final String PARAM_CARDSTORE = "cards-file";
    public static final String PARAM_ISSUE_FILE = "issue-file";

    public static final String VAL_KEYSTORE_DEFAULT = "/home/cmort/apps/apache-tomcat-5.5.17/conf/xmldap_org.jks";
    public static final String VAL_KEYSTORE_PASSWORD_DEFAULT = "password";
    public static final String VAL_KEY_PASSWORD_DEFAULT = "password";
    public static final String VAL_KEYNAME_DEFAULT = "xmldap";
    public static final String VAL_DOMAIN_DEFAULT = "xmldap.org";
    
    private ServletConfig _config;
    private KeystoreUtil _keystore;

    public ServletUtil (ServletConfig config) {
	_config = config;
    }
    
    public synchronized KeystoreUtil getKeystore() throws KeyStoreException {
	if (_keystore == null) {
	    String path = _config.getInitParameter(PARAM_KEYSTORE);
	    if (path == null) path = VAL_KEYSTORE_DEFAULT;
	    
	    String pass = _config.getInitParameter(PARAM_KEYSTORE_PASSWORD);
	    if (pass == null) pass = VAL_KEYSTORE_PASSWORD_DEFAULT;
	    

	    KeystoreUtil keystore = new KeystoreUtil(path, pass);
	    _keystore = keystore;
	}
	return _keystore;
    }

    public PrivateKey getPrivateKey() throws KeyStoreException {
	if (_keystore == null) {
	    if (getKeystore() == null) return null;
	}
	
	String keyname = _config.getInitParameter(PARAM_KEYNAME);
	if (keyname == null) keyname = VAL_KEYNAME_DEFAULT;
	String pass = _config.getInitParameter(PARAM_KEY_PASSWORD);
	if (pass == null) pass = VAL_KEY_PASSWORD_DEFAULT;
	
	return _keystore.getPrivateKey(keyname,pass);
    }

    public X509Certificate getCertificate() throws KeyStoreException {
	if (_keystore == null) {
	    if (getKeystore() == null) return null;
	}
	
	String keyname = _config.getInitParameter(PARAM_KEYNAME);
	if (keyname == null) keyname = VAL_KEYNAME_DEFAULT;

	return _keystore.getCertificate(keyname);
    }

    public String getDomainName() {
	String dn = _config.getInitParameter(PARAM_DOMAIN);
	if (dn == null) dn = VAL_DOMAIN_DEFAULT;
	return dn;
    }

    public String getManagedCardPathString() {
	return _config.getInitParameter(PARAM_CARDSTORE);
    }

    public String getIssueFilePathString() {
	return _config.getInitParameter(PARAM_ISSUE_FILE);
    }

}
