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

package org.xmldap.infocard.roaming;

import org.xmldap.exceptions.SerializationException;
import org.xmldap.infocard.InfoCard;
import org.xmldap.infocard.SelfIssuedClaims;
import org.xmldap.util.Base64;
import org.xmldap.util.XmlFileUtil;
import org.xmldap.ws.WSConstants;

import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Elements;
import nu.xom.ParsingException;
import nu.xom.ValidityException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Vector;
import java.util.Iterator;

//<ic:RoamingStore>
//<ic:RoamingInformationCard> +
//  <ic:InformationCardMetaData>
//    [Information Card]
//    <ic:IsSelfIssued> xs:boolean </ic:IsSelfIssued>
//    <ic:PinDigest> xs:base64Binary </ic:PinDigest> ?
//    <ic:HashSalt> xs:base64Binary </ic:HashSalt>
//    <ic:TimeLastUpdated> xs:dateTime </ic:TimeLastUpdated>
//    <ic:IssuerId> xs:base64Binary </ic:IssuerId>
//    <ic:IssuerName> xs:string </ic:IssuerName>
//    <ic:BackgroundColor> xs:int </ic:BackgroundColor>
//  </ic:InformationCardMetaData>
//  <ic:InformationCardPrivateData> ?
//    <ic:MasterKey> xs:base64Binary </ic:MasterKey>
//    <ic:ClaimValueList> ?
//      <ic:ClaimValue Uri="xs:anyURI" ...> +
//        <ic:Value> xs:string </ic:Value>
//      </ic:ClaimValue>
//    </ic:ClaimValueList>
//  </ic:InformationCardPrivateData>
//</ic:RoamingInformationCard>
//</ic:RoamingStore>

public class RoamingStore implements org.xmldap.xml.Serializable {

    private List<RoamingInformationCard> roamingInformationCards = null;

    public RoamingStore() {
        roamingInformationCards = new Vector<RoamingInformationCard>();
    }

    public RoamingStore(List<RoamingInformationCard> roamingInformationCards) {
        this.roamingInformationCards = roamingInformationCards;
    }

    public RoamingStore(String roamingStoreStr) throws ValidityException, IOException, ParsingException, org.xmldap.exceptions.ParsingException {
    	this(XmlFileUtil.readXml(new ByteArrayInputStream(roamingStoreStr.getBytes())));
    }

//    public RoamingStore(Infocards infocards) throws org.xmldap.exceptions.ParsingException {
//    	Collection<JsInfocard> infocardsList = infocards.getInfocards();
//    	for (JsInfocard jsInfocard : infocardsList) {
//			if (roamingInformationCards == null) {
//				roamingInformationCards = new Vector<RoamingInformationCard>();
//			}
//			InformationCardPrivateData informationCardPrivateData;
//			if ("selfAsserted".equals(jsInfocard.type)) {
//				JsCardDataSelfAsserted cardData = (JsCardDataSelfAsserted)jsInfocard.cardData.foo;
//				SelfIssuedClaims sic = new SelfIssuedClaims();
//				for (String key : cardData.avps.keySet()) {
//					if ("givenname".equals(key)) {
//						sic.setGivenName(cardData.avps.get(key));
//					} else if ("surname".equals(key)) {
//						sic.setSurname(cardData.avps.get(key));
//					} else if ("email".equals(key)) {
//						sic.setEmailAddress(cardData.avps.get(key));
//					} else if ("streetAddress".equals(key)) {
//						sic.setStreetAddress(cardData.avps.get(key));
//					} else if ("locality".equals(key)) {
//						sic.setLocality(cardData.avps.get(key));
//					} else if ("stateOrProvince".equals(key)) {
//						sic.setStateOrProvince(cardData.avps.get(key));
//					} else if ("postalCode".equals(key)) {
//						sic.setPostalCode(cardData.avps.get(key));
//					} else if ("country".equals(key)) {
//						sic.setCountry(cardData.avps.get(key));
//					} else if ("primaryPhone".equals(key)) {
//						sic.setPrimaryPhone(cardData.avps.get(key));
//					} else if ("otherPhone".equals(key)) {
//						sic.setOtherPhone(cardData.avps.get(key));
//					} else if ("mobilePhone".equals(key)) {
//						sic.setMobilePhone(cardData.avps.get(key));
//					} else if ("dateOfBirth".equals(key)) {
//						sic.setDateOfBirth(cardData.avps.get(key));
//					} else if ("gender".equals(key)) {
//						sic.setGender(cardData.avps.get(key));
//					} else {
//						throw new IllegalArgumentException("unsupported self-issued attribute:"  + key);
//					}
//					byte[] ppid = Base64.decode(jsInfocard.privatepersonalidentifier);
//					informationCardPrivateData = new SelfIssuedInformationCardPrivateData(sic, ppid);
//				}
//			} else if ("managedCard".equals(jsInfocard.type)) {
//				informationCardPrivateData = new ManagedInformationCardPrivateData();
//			} else {
//				throw new IllegalArgumentException("Information Card type is not supported:" + jsInfocard.type);
//			}
//			InfoCard infocard = new InfoCard();
//			{
//				infocard.setCardId(jsInfocard.getId(), 1);
//				infocard.setCardName(jsInfocard.getName());
//				if ("managedCard".equals(jsInfocard.type)) {
//					infocard.setIssuer(jsInfocard.getIssuer());
//					infocard.setRequireAppliesTo(jsInfocard.getRequireApplisTo());
//					infocard.set
//				}
//			}
//			InformationCardMetaData informationCardMetaData = new InformationCardMetaData(infocard);
//			RoamingInformationCard roamingInformationCard = new RoamingInformationCard(informationCardMetaData, informationCardPrivateData);
//			roamingInformationCards.add(roamingInformationCard);
//    	}
//    }

    public RoamingStore(Document roamingStoreDoc) throws IOException, org.xmldap.exceptions.ParsingException, ParsingException {
    	Element root = roamingStoreDoc.getRootElement();
    	if ("RoamingStore".equals(root.getLocalName())) {
    		Elements roamingCardsElts = root.getChildElements("RoamingInformationCard", WSConstants.INFOCARD_NAMESPACE);
    		for (int i=0; i<roamingCardsElts.size(); i++) {
    			Element roamingCardElt = roamingCardsElts.get(i);
    			if (roamingInformationCards == null) {
    				roamingInformationCards = new Vector<RoamingInformationCard>();
    			}
    			RoamingInformationCard roamingInformationCard = new RoamingInformationCard(roamingCardElt);
    			roamingInformationCards.add(roamingInformationCard);
    		}
    	} else {
    		throw new ParsingException("RoamingStore expected: " + root.getLocalName());
    	}
    }

    public void addRoamingInformationCard(RoamingInformationCard ric) {

        roamingInformationCards.add(ric);

    }

    public List<RoamingInformationCard> getRoamingInformationCards() {

        return roamingInformationCards;

    }

    private Element getRoamingStore() throws SerializationException {

        Element roamingStore = new Element("RoamingStore", WSConstants.INFOCARD_NAMESPACE);
        Iterator<RoamingInformationCard> cards = roamingInformationCards.iterator();
        while (cards.hasNext()) {
            RoamingInformationCard card  = (RoamingInformationCard) cards.next();
            roamingStore.appendChild(card.serialize());
        }

        return roamingStore;
    }


    public String toXML() throws SerializationException {

        Element keyInfo = serialize();
        return keyInfo.toXML();

    }

    public Element serialize() throws SerializationException {

        return getRoamingStore();

    }
}
