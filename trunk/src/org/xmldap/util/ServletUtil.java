/*
 * Copyright (c) 2006 Informed Control Inc. All rights reserved.
 *
 * Contributors: cmort@xmldap.org
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

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.Map;
import java.util.Enumeration;
import java.util.HashMap;


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
    public static final String PARAM_MEX_FILE = "mex-file";


    //Changing defaults to be for the xmldap.org RP
    public static final String VAL_KEYSTORE_DEFAULT = "/Users/cmort/xmldap_files/xmldap_org.jks";
    public static final String VAL_MEX_DEFAULT = "/Users/cmort/xmldap_files/mex.xml";
    public static final String VAL_KEYSTORE_PASSWORD_DEFAULT = "password";
    public static final String VAL_KEY_PASSWORD_DEFAULT = "password";
    public static final String VAL_KEYNAME_DEFAULT = "xmldap";
    public static final String VAL_DOMAIN_DEFAULT = "xmldap.org";

    private KeystoreUtil _keystore;
    private Map<String, String> props = new HashMap<String, String>();


    public ServletUtil (ServletContext servletContext) {
        if (servletContext != null) initProperties(servletContext);
    }

    public ServletUtil (ServletConfig config) {
        if (config != null) initProperties(config);
    }


    /**
     * Init parameters from servlet context.
     *
     * @param servletContext
     */
    private void initProperties(ServletContext servletContext) {
        Enumeration enumerationOfParameternames = servletContext.getInitParameterNames();
        while (enumerationOfParameternames.hasMoreElements()) {
            String parameterName = (String) enumerationOfParameternames.nextElement();
            props.put(parameterName, servletContext.getInitParameter(parameterName));
        }
    }


    /**
     * Init parameters from servlet config.
     *
     * @param servletConfig
     */
    private void initProperties(ServletConfig servletConfig) {
        Enumeration enumerationOfParameternames = servletConfig.getInitParameterNames();
        while (enumerationOfParameternames.hasMoreElements()) {
            String parameterName = (String) enumerationOfParameternames.nextElement();
            props.put(parameterName, servletConfig.getInitParameter(parameterName));
        }
    }




    public synchronized KeystoreUtil getKeystore() throws KeyStoreException {
        if (_keystore == null) {
            String path = props.get(PARAM_KEYSTORE);
            //_config.getInitParameter(PARAM_KEYSTORE);
            System.out.println("path = " + path);
            if (path == null) path = VAL_KEYSTORE_DEFAULT;

            String pass = props.get(PARAM_KEYSTORE_PASSWORD);
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

        String keyname = props.get(PARAM_KEYNAME);
        if (keyname == null) keyname = VAL_KEYNAME_DEFAULT;
        String pass = props.get(PARAM_KEY_PASSWORD);
        if (pass == null) pass = VAL_KEY_PASSWORD_DEFAULT;

        return _keystore.getPrivateKey(keyname,pass);
    }

    public X509Certificate getCertificate() throws KeyStoreException {
        if (_keystore == null) {
            if (getKeystore() == null) return null;
        }

        String keyname = props.get(PARAM_KEYNAME);
        if (keyname == null) keyname = VAL_KEYNAME_DEFAULT;
        return _keystore.getCertificate(keyname);
    }

    public String getDomainName() {
        String dn = props.get(PARAM_DOMAIN);
        if (dn == null) dn = VAL_DOMAIN_DEFAULT;
        return dn;
    }

    public String getManagedCardPathString() {
        return props.get(PARAM_CARDSTORE);
    }

    public String getIssueFilePathString() {
        return props.get(PARAM_ISSUE_FILE);
    }

    public String getMexFilePathString() {
        String mex =  props.get(PARAM_MEX_FILE);
        if (mex == null) mex = VAL_MEX_DEFAULT;
        return mex;
    }

}
