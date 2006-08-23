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

package org.xmldap.xmldsig;

import nu.xom.Element;
import org.xmldap.crypto.CryptoUtils;
import org.xmldap.exceptions.SerializationException;
import org.xmldap.exceptions.CryptoException;
import org.xmldap.util.KeystoreUtil;
import org.xmldap.xml.Serializable;

import java.security.PrivateKey;

public class SignatureValue implements Serializable {


    private SignedInfo signedInfo = null;
    private KeystoreUtil keystore = null;
    private String alias = null;
    private String keyPassword = null;

    public SignatureValue(SignedInfo signedInfo, KeystoreUtil keystore, String alias, String keyPassword) {
        this.signedInfo = signedInfo;
        this.keystore = keystore;
        this.alias = alias;
        this.keyPassword = keyPassword;
    }


    private Element getSignatureValue() throws SerializationException {

        Element signatureValue = new Element("ds:SignatureValue", "http://www.w3.org/2000/09/xmldsig#");
        PrivateKey key = null;
        try {
            key = keystore.getPrivateKey(alias, keyPassword);
        } catch (org.xmldap.exceptions.KeyStoreException e) {
            throw new SerializationException(e);
        }

        byte[] bytes = signedInfo.canonicalize();
        try {
            signatureValue.appendChild(CryptoUtils.sign(bytes, key));
        } catch (CryptoException e) {
            throw new SerializationException(e);
        }
        return signatureValue;

    }


    public String toXML() throws SerializationException {
        Element sigVal = serialize();
        return sigVal.toXML();

    }

    public Element serialize() throws SerializationException {
        return getSignatureValue();  //To change body of implemented methods use File | Settings | File Templates.
    }
}
