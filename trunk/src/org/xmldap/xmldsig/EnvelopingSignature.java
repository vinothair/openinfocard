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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmldap.exceptions.SerializationException;
import org.xmldap.exceptions.SigningException;
import org.xmldap.util.KeystoreUtil;
import org.xmldap.util.RandomGUID;
import org.xmldap.ws.WSConstants;

import java.util.Vector;

/**
 * Created by IntelliJ IDEA.
 * User: cmort
 * Date: Mar 25, 2006
 * Time: 4:41:33 PM
 * To change this template use File | Settings | File Templates.
 */
public class EnvelopingSignature {


    private final Logger logger = LoggerFactory.getLogger(EnvelopedSignature.class);
    private final boolean DEBUG = logger.isDebugEnabled();

    private KeystoreUtil keystore = null;
    private String alias = null;
    private String keyPassword = null;


    public EnvelopingSignature(KeystoreUtil keystore, String alias, String keyPassword) {
        this.keystore = keystore;
        this.alias = alias;
        this.keyPassword = keyPassword;
    }


    public Element sign(Element xml) throws SigningException {

        /*  Commenting out - this really needs to go on object
        //TODO - figure out something to do with irregular IDs
        Attribute id = xml.getAttribute("Id");
        if ( id == null ) {

            RandomGUID guidGen = new RandomGUID();
            id = new Attribute("Id",guidGen.toURN());
            xml.addAttribute(id);

        } */


        Element object = new Element(WSConstants.DSIG_PREFIX + ":Object", WSConstants.DSIG_NAMESPACE);
        RandomGUID guidGen = new RandomGUID();
        Attribute id = new Attribute("Id", guidGen.toURN());
        object.addAttribute(id);
        object.appendChild(xml);

        Reference referenceElm = new Reference(object, id.getValue());
        referenceElm.setEnveloped(false);

        /*
        try {
            System.out.println("Reference: " + referenceElm.toXML());
        } catch (SerializationException e) {
            e.printStackTrace();
        }
        */

        Vector references = new Vector();
        references.add(referenceElm);

        //Get SignedInfo for reference
        SignedInfo signedInfo = new SignedInfo(references);

        //Get sigvalue for the signedInfo
        SignatureValue signatureValue = new SignatureValue(signedInfo, keystore, alias, keyPassword);

        //Get AysmmetricKeyInfo
        AysmmetricKeyInfo keyInfo = new AysmmetricKeyInfo(keystore, alias);

        //Create the signature block
        Signature signature = new Signature(signedInfo, signatureValue, keyInfo);

        Element signatureElement = null;

        //Envelope it.
        try {

            signatureElement = signature.serialize();

        } catch (SerializationException e) {
            throw new SigningException("Could not create signature due to serialization error", e);
        }

        signatureElement.appendChild(object);

        return signatureElement;

    }


}
