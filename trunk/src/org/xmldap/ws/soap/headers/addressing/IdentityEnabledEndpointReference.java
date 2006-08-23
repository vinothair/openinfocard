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

package org.xmldap.ws.soap.headers.addressing;

import nu.xom.Element;
import org.xmldap.exceptions.KeyStoreException;
import org.xmldap.exceptions.SerializationException;
import org.xmldap.util.KeystoreUtil;
import org.xmldap.ws.WSConstants;
import org.xmldap.xml.Serializable;
import org.xmldap.xmldsig.AysmmetricKeyInfo;

import java.security.cert.X509Certificate;

/**
 * Created by IntelliJ IDEA.
 * User: cmort
 * Date: Mar 25, 2006
 * Time: 2:34:45 PM
 * To change this template use File | Settings | File Templates.
 */
public class IdentityEnabledEndpointReference extends EndpointReference implements Serializable {

    private X509Certificate cert;

    public IdentityEnabledEndpointReference(String address) {
        super(address);
    }

    public IdentityEnabledEndpointReference(String address, X509Certificate cert) {
        super(address);
        this.cert = cert;
    }


    /*
<wsid:Identity>
      <ds:KeyInfo>
        <ds:X509Data>
          <ds:X509Certificate>...</ds:X509Certificate>
        </ds:X509Data>
      </ds:KeyInfo>
    </wsid:Identity>

    */
    private Element getIEPR() throws SerializationException {

        Element ref = getEPR();


        Element identity = new Element(WSConstants.WSA_ID_PREFIX + ":Identity", WSConstants.WSA_ID_NAMESPACE);

        /*
        Saving this for later - reuse keyinfo!

        Element keyInfo = new Element(WSConstants.DSIG_PREFIX + ":KeyInfo", WSConstants.DSIG_NAMESPACE);
        Element x509Data = new Element(WSConstants.DSIG_PREFIX + ":X509Data", WSConstants.DSIG_NAMESPACE);
        Element x509Certificate = new Element(WSConstants.DSIG_PREFIX + ":X509Certificate", WSConstants.DSIG_NAMESPACE);

        try {
            x509Certificate.appendChild(Base64.encodeBytes(cert.getEncoded()));
        } catch (CertificateEncodingException e) {
            throw new SerializationException("Error serializing certificate", e);
        }

        x509Data.appendChild(x509Certificate);
        keyInfo.appendChild(x509Data);

        */

        AysmmetricKeyInfo keyInfo = new AysmmetricKeyInfo(cert);
        identity.appendChild(keyInfo.serialize());
        ref.appendChild(identity);
        return ref;

    }


    public String toXML() throws SerializationException {

        Element iepr = serialize();
        return iepr.toXML();

    }


    public Element serialize() throws SerializationException {


        return getIEPR();

    }


    public static void main(String[] args) {

        //Get my keystore
        KeystoreUtil keystore = null;
        try {
            keystore = new KeystoreUtil("/Users/cmort/build/infocard/conf/xmldap.jks", "storepassword");
        } catch (KeyStoreException e) {
            e.printStackTrace();
        }

        X509Certificate cert = null;
        try {
            cert = keystore.getCertificate("xmldap");
        } catch (KeyStoreException e) {
            e.printStackTrace();
        }

        IdentityEnabledEndpointReference iepr = new IdentityEnabledEndpointReference("http://test", cert);

        try {
            System.out.println(iepr.toXML());
        } catch (SerializationException e) {
            e.printStackTrace();
        }


    }


}
