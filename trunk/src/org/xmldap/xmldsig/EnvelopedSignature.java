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

import nu.xom.*;
import org.xmldap.exceptions.SerializationException;
import org.xmldap.exceptions.SigningException;

import java.io.IOException;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.Vector;


public class EnvelopedSignature {

    private X509Certificate signingCert;
    private PrivateKey privateKey;

    public EnvelopedSignature(X509Certificate signingCert, PrivateKey privateKey) {
        this.privateKey = privateKey;
        this.signingCert = signingCert;
    }


    public Element sign(Element xml) throws SigningException {

        return sign(xml, "/*", null);

    }

    public Element sign(Element xml, String xpath, XPathContext context) throws SigningException {

        Element signThisOne = (Element) xml.copy();
        return signXML(signThisOne, xpath, context);

    }


    public Document sign(Document xml) throws SigningException {

        return sign(xml, "/*", null);

    }

    public String sign(String xml) throws SigningException {

        return sign(xml, "/*", null);

    }

    public Document sign(Document xml, String xpath, XPathContext context) throws SigningException {

        Document signThisDoc = (Document) xml.copy();
        signXML(signThisDoc.getRootElement(), xpath, context);
        return signThisDoc;

    }


    public String sign(String xml, String xpath, XPathContext context) throws SigningException {

        Document doc;
        try {
            doc = parse(xml);
        } catch (org.xmldap.exceptions.ParsingException e) {
            throw new SigningException("Unable to parse input", e);
        }
        Document signedDoc = sign(doc, xpath, context);
        return signedDoc.toXML();

    }


    private Document parse(String doc) throws org.xmldap.exceptions.ParsingException {

        Builder parser = new Builder();
        Document parsedDoc;

        try {

            parsedDoc = parser.build(doc, "");


        } catch (nu.xom.ParsingException e) {
            throw new org.xmldap.exceptions.ParsingException("Unable to parse XML", e);
        } catch (IOException e) {
            throw new org.xmldap.exceptions.ParsingException("Unable to parse XML", e);
        }

        return parsedDoc;

    }


    private Element signXML(Element xml, String xpath, XPathContext context) throws SigningException {

        //Get Reference
        Nodes nodesToReference = new Nodes();


        if (xpath.equals("/*")) {

            //Just put in the base document - skip the xpath
            nodesToReference.append(xml);

        } else {

            try {

                if (context != null) {
                    nodesToReference = xml.query(xpath, context);
                } else {
                    nodesToReference = xml.query(xpath);
                }

            } catch (XPathException e) {

                throw new SigningException("Error in signing XPath: " + xpath, e);

            }

        }

        if (nodesToReference.size() == 0) throw new SigningException("XPath returned no results");

        Vector references = new Vector();

        for (int i = 0; i < nodesToReference.size(); i++) {

            try {


                Element referenceThis = (Element) nodesToReference.get(i);
                //Check for root
                boolean isRoot = false;
                Document thisDoc = referenceThis.getDocument();

                //TODO - common code
                //it isn't a doc, let's check if it's the top of the tree.
                if (thisDoc == null) {

                    ParentNode parent = referenceThis.getParent();
                    if (parent == null) isRoot = true;

                } else {

                    //It is a doc - let's see if it's root.
                    Element root = thisDoc.getRootElement();
                    if (root.equals(referenceThis)) isRoot = true;

                }

                //Attribute id = referenceThis.getAttribute("Id",WSConstants.WSSE_OASIS_10_WSU_NAMESPACE);

                String idVal = "";

                if (!isRoot) {
                    //TODO - support multiple ID id Ids
                    Attribute id = referenceThis.getAttribute("id");
                    if (id == null)
                        throw new SigningException("XPath returned Element with no wsu:Id attribute. Id is required");
                    idVal = id.getValue();
                    //System.out.println("Building reference for ID " + id.getValue() + ": " + referenceThis);
                } else {

                    //let's see if its a SAML assertions
                    Attribute assertionID = referenceThis.getAttribute("AssertionID");
                    if (assertionID != null) {

                        idVal = assertionID.getValue();

                    }

                }

                Reference referenceElm = new Reference(referenceThis, idVal);


                references.add(referenceElm);

            } catch (ClassCastException e) {

                throw new SigningException("XPath returned an item which was not an element. Signing only allowed on elements.", e);

            }

        }

        //Get SignedInfo for reference
        SignedInfo signedInfo = new SignedInfo(references);

        //Get sigvalue for the signedInfo
        SignatureValue signatureValue = new SignatureValue(signedInfo, privateKey);

        //Get KeyInfo
        KeyInfo keyInfo = new AsymmetricKeyInfo(signingCert);


        //Create the signature block
        Signature signature = new Signature(signedInfo, signatureValue, keyInfo);

        //Envelope it.
        try {
            //Element rootElement = xml.getRootElement();
            //rootElement.appendChild(signature.serialize());
            xml.appendChild(signature.serialize());
        } catch (SerializationException e) {
            throw new SigningException("Could not create enveloped signature due to serialization error", e);
        }

        return xml;

    }


}
