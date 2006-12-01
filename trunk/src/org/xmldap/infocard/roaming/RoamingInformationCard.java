package org.xmldap.infocard.roaming;

import org.xmldap.exceptions.SerializationException;
import org.xmldap.ws.WSConstants;
import org.xmldap.infocard.InfoCard;
import nu.xom.*;

import java.io.IOException;


public class RoamingInformationCard implements org.xmldap.xml.Serializable {

    private InfoCard card;

    public RoamingInformationCard(InfoCard card) {
        this.card = card;
    }

    private Element getRoamingInformationCard() throws SerializationException {

        Element roamingInformationCard = new Element("RoamingInformationCard", WSConstants.INFOCARD_NAMESPACE);
        Element informationCardMetaData = new Element("InformationCardMetaData", WSConstants.INFOCARD_NAMESPACE);
        Attribute lang = new Attribute("xml:lang", "http://www.w3.org/XML/1998/namespace", "en");
        informationCardMetaData.addAttribute(lang);
        roamingInformationCard.appendChild(informationCardMetaData);
        Element cardElm = card.serialize();
        Elements children = cardElm.getChildElements();
        for (int i = 0; i < children.size(); i++) {
            Element child = children.get(i);
            child.detach();
            informationCardMetaData.appendChild(child);
        }


        //TODO - make this all dynamic
        Element isSelfIssued = new Element("IsSelfIssued", WSConstants.INFOCARD_NAMESPACE);
        informationCardMetaData.appendChild(isSelfIssued);
        isSelfIssued.appendChild("true");

        Element hashSalt = new Element("HashSalt", WSConstants.INFOCARD_NAMESPACE);
        informationCardMetaData.appendChild(hashSalt);
        hashSalt.appendChild("610+M5O09oikUhLQ3n324g==");

        Element timeLastUpdated = new Element("TimeLastUpdated", WSConstants.INFOCARD_NAMESPACE);
        informationCardMetaData.appendChild(timeLastUpdated);
        timeLastUpdated.appendChild(card.getTimeIssued());

        Element issuerId = new Element("IssuerId", WSConstants.INFOCARD_NAMESPACE);
        informationCardMetaData.appendChild(issuerId);

        Element issuerName = new Element("IssuerName", WSConstants.INFOCARD_NAMESPACE);
        informationCardMetaData.appendChild(issuerName);
        issuerName.appendChild("Self");

        Element backgroundColor = new Element("BackgroundColor", WSConstants.INFOCARD_NAMESPACE);
        informationCardMetaData.appendChild(backgroundColor);
        backgroundColor.appendChild("16777215");

        Element informationCardPrivateData = new Element("InformationCardPrivateData", WSConstants.INFOCARD_NAMESPACE);
        roamingInformationCard.appendChild(informationCardPrivateData);

        Element masterKey = new Element("MasterKey", WSConstants.INFOCARD_NAMESPACE);
        informationCardPrivateData.appendChild(masterKey);
        masterKey.appendChild("iJzz3+thvV6wWdFQCFADcHbNaOasZpt0qTaC7Brvbfc=");

        Element claimValueList = new Element("ClaimValueList", WSConstants.INFOCARD_NAMESPACE);
        informationCardPrivateData.appendChild(claimValueList);

        Element givenNameCV = new Element("ClaimValue", WSConstants.INFOCARD_NAMESPACE);
        Attribute givenNameUri = new Attribute("Uri", "http://schemas.xmlsoap.org/ws/2005/05/identity/claims/givenname");
        givenNameCV.addAttribute(givenNameUri);
        Element givenNameValue = new Element("Value", WSConstants.INFOCARD_NAMESPACE);
        givenNameCV.appendChild(givenNameValue);
        givenNameValue.appendChild("Chuck");
        claimValueList.appendChild(givenNameCV);


        Element surNameCV = new Element("ClaimValue", WSConstants.INFOCARD_NAMESPACE);
        Attribute surNameUri = new Attribute("Uri", "http://schemas.xmlsoap.org/ws/2005/05/identity/claims/surname");
        surNameCV.addAttribute(surNameUri);
        Element surNameValue = new Element("Value", WSConstants.INFOCARD_NAMESPACE);
        surNameCV.appendChild(surNameValue);
        surNameValue.appendChild("Mortimore");
        claimValueList.appendChild(surNameCV);


        Element emailCV = new Element("ClaimValue", WSConstants.INFOCARD_NAMESPACE);
        Attribute emailUri = new Attribute("Uri", "http://schemas.xmlsoap.org/ws/2005/05/identity/claims/emailaddress");
        emailCV.addAttribute(emailUri);
        Element emailValue = new Element("Value", WSConstants.INFOCARD_NAMESPACE);
        emailCV.appendChild(emailValue);
        emailValue.appendChild("cmort@xmldap.org");
        claimValueList.appendChild(emailCV);


        //TODO - I need to come up with a strategy to merge all the card serialization formats into one or two

        return roamingInformationCard;
    }

    
    public String toXML() throws SerializationException {
        Element ric = serialize();
        return ric.toXML();
    }

    public Element serialize() throws SerializationException {
        return getRoamingInformationCard();
    }
}
