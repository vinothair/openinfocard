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

import nu.xom.Attribute;
import nu.xom.Element;
import org.xmldap.exceptions.KeyStoreException;
import org.xmldap.exceptions.SigningException;
import org.xmldap.util.KeystoreUtil;
import org.xmldap.util.RandomGUID;

/**
 * Created by IntelliJ IDEA.
 * User: cmort
 * Date: Mar 25, 2006
 * Time: 5:18:39 PM
 * To change this template use File | Settings | File Templates.
 */
public class EnvelopingTest {


    public static void main(String[] args) {

        //Create a new docuement to sign
        RandomGUID guidGen = new RandomGUID();
        String guid = guidGen.toURN();
        Element body = new Element("xmldap:Body", "http://www.xmldap.org");
        //body.addNamespaceDeclaration(WSConstants.WSU_PREFIX, WSConstants.WSSE_OASIS_10_WSU_NAMESPACE);
        //Attribute idAttr = new Attribute(WSConstants.WSU_PREFIX + ":Id", WSConstants.WSSE_OASIS_10_WSU_NAMESPACE, "urn:guid:58824342-832F-C99B-925B-CB0E858E5D65");
        Attribute idAttr = new Attribute("Id", "urn:guid:58824342-832F-C99B-925B-CB0E858E5D65");
        body.addAttribute(idAttr);
        Element child1 = new Element("xmldap:Child", "http://www.xmldap.org");
        //Attribute idAttr1 = new Attribute(WSConstants.WSU_PREFIX + ":Id", WSConstants.WSSE_OASIS_10_WSU_NAMESPACE, "1");
        Attribute idAttr1 = new Attribute("Id", "1");
        child1.addAttribute(idAttr1);
        Element child2 = new Element("xmldap:Child", "http://www.xmldap.org");
        //Attribute idAttr2 = new Attribute(WSConstants.WSU_PREFIX + ":Id", WSConstants.WSSE_OASIS_10_WSU_NAMESPACE, "2");
        Attribute idAttr2 = new Attribute("Id", "2");
        child2.addAttribute(idAttr2);

        body.appendChild(child1);
        body.appendChild(child2);

        //Get my keystore
        KeystoreUtil keystore = null;
        try {
            keystore = new KeystoreUtil("/Users/cmort/build/infocard/conf/xmldap.jks", "storepassword");
        } catch (KeyStoreException e) {
            e.printStackTrace();
        }

        //Get the signing util
        EnvelopingSignature signer = new EnvelopingSignature(keystore, "xmldap", "keypassword");
        try {

            Element signedElm = signer.sign(body);
            System.out.println(signedElm.toXML());

        } catch (SigningException e) {
            e.printStackTrace();
        }


    }
}
