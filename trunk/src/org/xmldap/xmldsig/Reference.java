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

package org.xmldap.xmldsig;

import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.canonical.Canonicalizer;
import org.xmldap.crypto.CryptoUtils;
import org.xmldap.exceptions.SerializationException;
import org.xmldap.xml.Serializable;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class Reference implements Serializable {

    //TODO - fix URI handling
    private Element data = null;
    private String id = null;
    private boolean enveloped = true;


    public Reference(Element data, String id) {
    	if (data == null) {
    		throw new IllegalArgumentException("Parameter data must not be null");
    	}
        this.data = data;
    	if (id == null) {
    		throw new IllegalArgumentException("Parameter id must not be null");
    	}
        this.id = id;

    }

    //TODO - shouldn't be boolean if we want to support all types
    public boolean isEnveloped() {
        return enveloped;
    }

    public void setEnveloped(boolean enveloped) {
        this.enveloped = enveloped;
    }

    private Element getReference() throws SerializationException {

        Element reference = new Element("dsig:Reference", "http://www.w3.org/2000/09/xmldsig#");

        Attribute uriAttr = null;

        if (id.equals("")) {
            uriAttr = new Attribute("URI", "");
        } else {
            uriAttr = new Attribute("URI", "#" + id);
        }

        reference.addAttribute(uriAttr);

//        Attribute uriAttr = new Attribute("URI", "");
//        reference.addAttribute(uriAttr);


        Element transforms = new Element("dsig:Transforms", "http://www.w3.org/2000/09/xmldsig#");

        //Attribute transformDsigAlgorithm = new Attribute("Algorithm", Canonicalizer.CANONICAL_XML);
        if (enveloped) {

            Element transformEnveloped = new Element("dsig:Transform", "http://www.w3.org/2000/09/xmldsig#");
            Attribute transformEnvelopedAlgorithm = new Attribute("Algorithm", "http://www.w3.org/2000/09/xmldsig#enveloped-signature");
            transformEnveloped.addAttribute(transformEnvelopedAlgorithm);
            transforms.appendChild(transformEnveloped);


        }


        Element transformDsig = new Element("dsig:Transform", "http://www.w3.org/2000/09/xmldsig#");
        Attribute transformDsigAlgorithm = new Attribute("Algorithm", Canonicalizer.EXCLUSIVE_XML_CANONICALIZATION);
        transformDsig.addAttribute(transformDsigAlgorithm);
        transforms.appendChild(transformDsig);


        reference.appendChild(transforms);

        Element digestMethod = new Element("dsig:DigestMethod", "http://www.w3.org/2000/09/xmldsig#");
        Attribute digestAlgorithm = new Attribute("Algorithm", "http://www.w3.org/2000/09/xmldsig#sha1");
        digestMethod.addAttribute(digestAlgorithm);
        reference.appendChild(digestMethod);

        Element digestValue = new Element("dsig:DigestValue", "http://www.w3.org/2000/09/xmldsig#");
        byte[] dataBytes = null;

        ByteArrayOutputStream stream = new ByteArrayOutputStream();

        //Canonicalizer outputer = new Canonicalizer(stream, Canonicalizer.CANONICAL_XML);
        Canonicalizer outputer = new Canonicalizer(stream, Canonicalizer.EXCLUSIVE_XML_CANONICALIZATION);
        try {
            outputer.write(data);
        } catch (IOException e) {
            throw new SerializationException("Error canonicalizing data to be digested", e);
        }
        dataBytes = stream.toByteArray();


        try {
            String digest = CryptoUtils.digest(dataBytes);
            digestValue.appendChild(digest);
        } catch (org.xmldap.exceptions.CryptoException e) {
            e.printStackTrace();
        }

        reference.appendChild(digestValue);

        return reference;

    }


    public String toXML() throws SerializationException {
        Element reference = serialize();
        return reference.toXML();  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Element serialize() throws SerializationException {
        return getReference();
    }
}
