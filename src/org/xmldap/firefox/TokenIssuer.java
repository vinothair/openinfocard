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
 *     * Neither the name of the University of California, Berkeley nor the
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

package org.xmldap.firefox;

import org.xmldap.util.KeystoreUtil;
import org.xmldap.util.Base64;
import org.xmldap.exceptions.KeyStoreException;
import org.xmldap.exceptions.SerializationException;
import org.xmldap.exceptions.TokenIssuanceException;
import org.xmldap.xmlenc.EncryptedData;
import org.xmldap.infocard.SelfIssuedToken;
import org.json.JSONObject;
import org.json.JSONException;

import java.net.URLDecoder;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.cert.CertificateException;
import java.io.ByteArrayInputStream;
import java.io.BufferedInputStream;
import java.io.IOException;

import nu.xom.*;


public class TokenIssuer {

    private String path;

    public TokenIssuer(String path){

        this.path = URLDecoder.decode(path.substring(7,path.length()));


    }


    public String init(String path){

        return "TokenIssuer initialized";

    }


    public String getToken(String serializedPolicy) throws TokenIssuanceException {

        //TODO - break up this rather large code block
        JSONObject policy = null;
        String card = null;
        String der = null;
        try {
            policy = new JSONObject(serializedPolicy);
            der = (String)policy.get("cert");
            card = (String)policy.get("card");
        } catch (JSONException e) {
            throw new TokenIssuanceException(e);
        }

        Builder parser = new Builder();
        Document infocard = null;
        try {
            infocard = parser.build(card,"");
        } catch (ParsingException e) {
            throw new TokenIssuanceException(e);
        } catch (IOException e) {
            throw new TokenIssuanceException(e);
        }

        Nodes dataNodes = infocard.query("/infocard/carddata/selfasserted");
        Element data = (Element) dataNodes.get(0);

        //TODO - support all elements including ppi
        String ppi = "";
        String givenName = "";
        String surName = "";
        String email = "";


        Nodes ppiNodes = infocard.query("/infocard/privatepersonalidentifier");
        Element ppiElm = (Element) ppiNodes.get(0);
        if (ppiElm != null) ppi = ppiElm.getValue();

        Element givenNameElm = data.getFirstChildElement("givenname");
        if ( givenNameElm != null) givenName = givenNameElm.getValue();

        Element surNameElm = data.getFirstChildElement("surname");
        if ( surNameElm != null) surName = surNameElm.getValue();

        Element emailElm = data.getFirstChildElement("emailaddress");
        if ( emailElm != null) email = emailElm.getValue();


        byte[] certBytes = Base64.decode(der);
        ByteArrayInputStream is = new ByteArrayInputStream(certBytes);
        BufferedInputStream bis = new BufferedInputStream(is);
        CertificateFactory cf = null;
        X509Certificate  cert = null;
        try {
            cf = CertificateFactory.getInstance("X.509");
            cert = (X509Certificate) cf.generateCertificate(bis);
        } catch (CertificateException e) {
            throw new TokenIssuanceException(e);
        }

        System.out.println("Server Cert: " + cert.getSubjectDN().toString());


        //TODO - get rid of keystore dependency - gen certs, store, and pass in.
        String keystorePath = path + "components/lib/firefox.jks";

        //Get my keystore
        KeystoreUtil keystore = null;
        try {
            keystore = new KeystoreUtil(keystorePath, "storepassword");
        } catch (KeyStoreException e) {

            throw new TokenIssuanceException(e);

        }

        String issuedToken="";
        EncryptedData encryptor = new EncryptedData(cert);
        SelfIssuedToken token = new SelfIssuedToken(keystore, cert, "firefox", "keypassword");


        token.setPrivatePersonalIdentifier(Base64.encodeBytes(ppi.getBytes()));
        token.setGivenName(givenName);
        token.setSurname(surName);
        token.setEmailAddress(email);
        token.setValidityPeriod(20);
        Element securityToken = null;
        try {
           securityToken = token.serialize();
           encryptor.setData(securityToken.toXML());
           issuedToken = encryptor.toXML();

        } catch (SerializationException e) {
            throw new TokenIssuanceException(e);
        }

        return issuedToken;
    }

}
