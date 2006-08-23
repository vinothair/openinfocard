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

package org.xmldap.saml;

import nu.xom.Element;
import org.xmldap.exceptions.KeyStoreException;
import org.xmldap.exceptions.SerializationException;
import org.xmldap.util.KeystoreUtil;
import org.xmldap.ws.WSConstants;
import org.xmldap.xml.Serializable;
import org.xmldap.xmldsig.AysmmetricKeyInfo;
import org.xmldap.xmldsig.KeyInfo;


public class Subject implements Serializable {


    private String confirmationMethod = "urn:oasis:names:tc:SAML:1.0:cm:holder-of-key";
    private KeyInfo keyInfo;

    public Subject(KeyInfo keyInfo) {
        this.keyInfo = keyInfo;
    }

    private Element getSubject() throws SerializationException {

        Element subject = new Element(WSConstants.SAML_PREFIX + ":Subject", WSConstants.SAML11_NAMESPACE);
        Element subjectConfirmation = new Element(WSConstants.SAML_PREFIX + ":SubjectConfirmation", WSConstants.SAML11_NAMESPACE);
        Element confirmationMethodElm = new Element(WSConstants.SAML_PREFIX + ":ConfirmationMethod", WSConstants.SAML11_NAMESPACE);
        confirmationMethodElm.appendChild(confirmationMethod);
        subjectConfirmation.appendChild(confirmationMethodElm);
        subjectConfirmation.appendChild(keyInfo.serialize());
        subject.appendChild(subjectConfirmation);
        return subject;

    }

    public String toXML() throws SerializationException {

        Element subject = serialize();
        return subject.toXML();
    }

    public Element serialize() throws SerializationException {

        return getSubject();

    }

    public static void main(String[] args) {

        //Get my keystore
        KeystoreUtil keystore = null;
        try {
            keystore = new KeystoreUtil("/Users/cmort/build/infocard/conf/xmldap.jks", "storepassword");
        } catch (KeyStoreException e) {
            e.printStackTrace();
        }


        AysmmetricKeyInfo keyInfo = new AysmmetricKeyInfo(keystore, "xmldap");

        Subject subject = new Subject(keyInfo);
        try {
            System.out.println(subject.toXML());
        } catch (SerializationException e) {
            e.printStackTrace();
        }


    }
}
