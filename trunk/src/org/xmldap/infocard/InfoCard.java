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

import nu.xom.Attribute;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Serializer;
import org.xmldap.exceptions.KeyStoreException;
import org.xmldap.exceptions.SerializationException;
import org.xmldap.exceptions.SigningException;
import org.xmldap.infocard.policy.SupportedClaim;
import org.xmldap.infocard.policy.SupportedClaimList;
import org.xmldap.infocard.policy.SupportedToken;
import org.xmldap.infocard.policy.SupportedTokenList;
import org.xmldap.util.KeystoreUtil;
import org.xmldap.util.XSDDateTime;
import org.xmldap.ws.WSConstants;
import org.xmldap.xml.Serializable;
import org.xmldap.xmldsig.EnvelopingSignature;

import java.io.IOException;
import java.security.cert.X509Certificate;

/**
 * InfoCard allows you to create an InfoCard, and serialize to XML.
 *
 * Almost works...
 *
 * @author charliemortimore at gmail.com
 */
public class InfoCard implements Serializable {

    private String cardName;
    private String cardId;
    private int cardVersion = 1;
    private String base64BinaryCardImage;
    private String issuerName;
    private String timeIssued;
    private String timeExpires;
    private TokenServiceReference tokenServiceReference;
    private SupportedTokenList tokenList;
    private SupportedClaimList claimList;


    private boolean requireAppliesTo = false;

    public void setRequireAppliesTo(boolean requireAppliesTo) {
        this.requireAppliesTo = requireAppliesTo;
    }


    private String userName;

    public String getCardName() {
        return cardName;
    }

    public void setCardName(String cardName) {
        this.cardName = cardName;
    }

    public String getCardId() {
        return cardId;
    }

    public void setCardId(String cardId) {
        this.cardId = cardId;
    }

    public int getCardVersion() {
        return cardVersion;
    }

    public void setCardVersion(int cardVersion) {
        this.cardVersion = cardVersion;
    }

    public String getBase64BinaryCardImage() {
        return base64BinaryCardImage;
    }

    public void setBase64BinaryCardImage(String base64BinaryCardImage) {
        this.base64BinaryCardImage = base64BinaryCardImage;
    }

    public String getIssuerName() {
        return issuerName;
    }

    public void setIssuerName(String issuerName) {
        this.issuerName = issuerName;
    }

    public String getTimeIssued() {
        return timeIssued;
    }

    public void setTimeIssued(String timeIssued) {
        this.timeIssued = timeIssued;
    }

    public String getTimeExpires() {
        return timeExpires;
    }

    public void setTimeExpires(String timeExpires) {
        this.timeExpires = timeExpires;
    }

    public TokenServiceReference getTokenServiceReference() {
        return tokenServiceReference;
    }

    public void setTokenServiceReference(TokenServiceReference tokenServiceReference) {
        this.tokenServiceReference = tokenServiceReference;
    }

    public SupportedTokenList getTokenList() {
        return tokenList;
    }

    public void setTokenList(SupportedTokenList tokenList) {
        this.tokenList = tokenList;
    }

    public SupportedClaimList getClaimList() {
        return claimList;
    }

    public void setClaimList(SupportedClaimList claimList) {
        this.claimList = claimList;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }


    private Element getInfoCard() throws SerializationException {

        Element infoCard = new Element(WSConstants.INFOCARD_PREFIX + ":InfoCard", WSConstants.INFOCARD_NAMESPACE);
        Attribute lang = new Attribute("xml:lang", "http://www.w3.org/XML/1998/namespace", "en-us");
        infoCard.addAttribute(lang);

        Element infoCardReference = new Element(WSConstants.INFOCARD_PREFIX + ":InfoCardReference", WSConstants.INFOCARD_NAMESPACE);
        Element cardIdElm = new Element(WSConstants.INFOCARD_PREFIX + ":CardId", WSConstants.INFOCARD_NAMESPACE);
        cardIdElm.appendChild(cardId);
        infoCardReference.appendChild(cardIdElm);
        Element cardVersionElm = new Element(WSConstants.INFOCARD_PREFIX + ":CardVersion", WSConstants.INFOCARD_NAMESPACE);
        Integer ver = new Integer(cardVersion);
        cardVersionElm.appendChild(ver.toString());
        infoCardReference.appendChild(cardVersionElm);
        infoCard.appendChild(infoCardReference);

        Element cardNameElm = new Element(WSConstants.INFOCARD_PREFIX + ":CardName", WSConstants.INFOCARD_NAMESPACE);
        cardNameElm.appendChild(cardName);
        infoCard.appendChild(cardNameElm);

        Element issuerNameElm = new Element(WSConstants.INFOCARD_PREFIX + ":IssuerName", WSConstants.INFOCARD_NAMESPACE);
        issuerNameElm.appendChild(issuerName);
        infoCard.appendChild(issuerNameElm);

        Element timeIssuedElm = new Element(WSConstants.INFOCARD_PREFIX + ":TimeIssued", WSConstants.INFOCARD_NAMESPACE);
        timeIssuedElm.appendChild(timeIssued);
        infoCard.appendChild(timeIssuedElm);

        Element timeExpiresElm = new Element(WSConstants.INFOCARD_PREFIX + ":TimeExpires", WSConstants.INFOCARD_NAMESPACE);
        timeExpiresElm.appendChild(timeExpires);
        infoCard.appendChild(timeExpiresElm);

        infoCard.appendChild(tokenServiceReference.serialize());
        infoCard.appendChild(tokenList.serialize());
        infoCard.appendChild(claimList.serialize());


        if (requireAppliesTo) {

            Element requireAppliesToElm = new Element(WSConstants.INFOCARD_PREFIX + ":RequireAppliesTo", WSConstants.INFOCARD_NAMESPACE);
            infoCard.appendChild(requireAppliesToElm);

        }


        return infoCard;


    }


    public String toXML() throws SerializationException {

        Element infoCard = serialize();
        return infoCard.toXML();

    }

    public Element serialize() throws SerializationException {
        return getInfoCard();
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


        InfoCard card = new InfoCard();
        card.setCardId("http://xmldap.org/cards/12345");
        card.setCardName("Cmort's first card");
        card.setCardVersion(1);
        card.setIssuerName("http://xmldap.org");
        XSDDateTime issued = new XSDDateTime();
        XSDDateTime expires = new XSDDateTime(525600);

        card.setTimeIssued(issued.getDateTime());
        card.setTimeExpires(expires.getDateTime());

        TokenServiceReference tsr = new TokenServiceReference("http://xmldap.org/sts", cert);
        tsr.setUserName("cmort");
        card.setTokenServiceReference(tsr);


        SupportedTokenList tokenList = new SupportedTokenList();
        SupportedToken token = new SupportedToken(SupportedToken.SAML11);
        tokenList.addSupportedToken(token);
        card.setTokenList(tokenList);

        SupportedClaimList claimList = new SupportedClaimList();
        SupportedClaim given = new SupportedClaim("GivenName", "http://schemas.microsoft.com/ws/2005/05/identity/claims/givenname");
        SupportedClaim sur = new SupportedClaim("Surname", "http://schemas.microsoft.com/ws/2005/05/identity/claims/surname");
        SupportedClaim email = new SupportedClaim("EmailAddress", "http://schemas.microsoft.com/ws/2005/05/identity/claims/emailaddress");
        claimList.addSupportedClaim(given);
        claimList.addSupportedClaim(sur);
        claimList.addSupportedClaim(email);
        card.setClaimList(claimList);

        //Get the signing util
        EnvelopingSignature signer = new EnvelopingSignature(keystore, "xmldap", "keypassword");

        try {

            Element signedCard = signer.sign(card.serialize());
            System.out.println(signedCard.toXML());


            Document doc = new Document(signedCard);
            Serializer serializer = new Serializer(System.out);
            serializer.setIndent(4);
            serializer.setMaxLength(64);
            try {
                serializer.write(doc);
            }
            catch (IOException ex) {
                System.err.println(ex);
            }


        } catch (SerializationException e) {
            e.printStackTrace();
        } catch (SigningException e) {
            e.printStackTrace();
        }


    }

}
