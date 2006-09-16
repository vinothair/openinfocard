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

package org.xmldap.infocard;

import nu.xom.Element;
import org.xmldap.exceptions.KeyStoreException;
import org.xmldap.exceptions.SerializationException;
import org.xmldap.util.KeystoreUtil;
import org.xmldap.ws.WSConstants;
import org.xmldap.ws.soap.headers.addressing.IdentityEnabledEndpointReference;
import org.xmldap.xml.Serializable;

import java.security.cert.X509Certificate;


public class TokenServiceReference implements Serializable {

    private static final String USERNAME = "UserNamePasswordAuthenticate";
    private static final String SELF_ISSUED = "SelfIssuedAuthenticate";
    private static final String X509 = "X509V3Authenticate";
    private static final String KERB = "KerberosV5Authenticate";


    private String authType = USERNAME;
    private String address;
    private String mexAddress;
    private String userName;
    private X509Certificate cert;

    public TokenServiceReference() {
    }

    public TokenServiceReference(String address, String mexAddress) {
        this.address = address;
        this.mexAddress = mexAddress;
    }

    public TokenServiceReference(String address, String mexAddress, X509Certificate cert) {
        this.address = address;
        this.mexAddress = mexAddress;
        this.cert = cert;
    }

    public String getMexAddress() {
        return mexAddress;
    }

    public void setMexAddress(String mexAddress) {
        this.mexAddress = mexAddress;
    }

    public String getAuthType() {
        return authType;
    }

    public void setAuthType(String authType) {
        this.authType = authType;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public X509Certificate getCert() {
        return cert;
    }

    public void setCert(X509Certificate cert) {
        this.cert = cert;
    }


    private Element getTokenServiceReference() throws SerializationException {

        //TODO - support all the reference types
        Element tokenServiceList = new Element(WSConstants.INFOCARD_PREFIX + ":TokenServiceList", WSConstants.INFOCARD_NAMESPACE);
        Element tokenService = new Element(WSConstants.INFOCARD_PREFIX + ":TokenService", WSConstants.INFOCARD_NAMESPACE);
        IdentityEnabledEndpointReference iepr = new IdentityEnabledEndpointReference(address, mexAddress, cert);
        tokenService.appendChild(iepr.serialize());

        Element userCredential = new Element(WSConstants.INFOCARD_PREFIX + ":UserCredential", WSConstants.INFOCARD_NAMESPACE);
        Element displayCredentialHint = new Element(WSConstants.INFOCARD_PREFIX + ":DisplayCredentialHint", WSConstants.INFOCARD_NAMESPACE);
        displayCredentialHint.appendChild("Enter your username and password");
        userCredential.appendChild(displayCredentialHint);

        Element usernamePasswordCredential = new Element(WSConstants.INFOCARD_PREFIX + ":UsernamePasswordCredential", WSConstants.INFOCARD_NAMESPACE);
        Element username = new Element(WSConstants.INFOCARD_PREFIX + ":Username", WSConstants.INFOCARD_NAMESPACE);
        username.appendChild(userName);
        usernamePasswordCredential.appendChild(username);
        userCredential.appendChild(usernamePasswordCredential);

        tokenService.appendChild(userCredential);
        tokenServiceList.appendChild(tokenService);
        return tokenServiceList;

    }


    public String toXML() throws SerializationException {

        Element tsr = getTokenServiceReference();
        return tsr.toXML();

    }

    public Element serialize() throws SerializationException {
        return getTokenServiceReference();
    }


}
