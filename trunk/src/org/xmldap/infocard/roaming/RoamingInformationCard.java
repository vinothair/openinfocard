package org.xmldap.infocard.roaming;

import java.io.IOException;

import org.xmldap.exceptions.ParsingException;
import org.xmldap.exceptions.SerializationException;
import org.xmldap.ws.WSConstants;
import nu.xom.*;

public class RoamingInformationCard implements org.xmldap.xml.Serializable {
//	  <ic:RoamingInformationCard> +
//	    <ic:InformationCardMetaData>
//	      [Information Card]
//	      <ic:IsSelfIssued> xs:boolean </ic:IsSelfIssued>
//	      <ic:PinDigest> xs:base64Binary </ic:PinDigest> ?
//	      <ic:HashSalt> xs:base64Binary </ic:HashSalt>
//	      <ic:TimeLastUpdated> xs:dateTime </ic:TimeLastUpdated>
//	      <ic:IssuerId> xs:base64Binary </ic:IssuerId>
//	      <ic:IssuerName> xs:string </ic:IssuerName>
//	      <ic:BackgroundColor> xs:int </ic:BackgroundColor>
//	    </ic:InformationCardMetaData>
//	    <ic:InformationCardPrivateData> ?
//	      <ic:MasterKey> xs:base64Binary </ic:MasterKey>
//	      <ic:ClaimValueList> ?
//	        <ic:ClaimValue Uri="xs:anyURI" ...> +
//	          <ic:Value> xs:string </ic:Value>
//	        </ic:ClaimValue>
//	      </ic:ClaimValueList>
//	    </ic:InformationCardPrivateData>
//	  </ic:RoamingInformationCard>

	InformationCardMetaData informationCardMetaData = null;
	InformationCardPrivateData informationCardPrivateData = null;
	
//    public RoamingInformationCard(InfoCard card) {
//        this.informationCardMetaData.card = card;
//        informationCardPrivateData = new InformationCardPrivateData();
//    }

    public RoamingInformationCard(InformationCardMetaData informationCardMetaData, InformationCardPrivateData informationCardPrivateData) {
    	if (informationCardMetaData.getTokenList() == null) {
    		throw new IllegalArgumentException("SupportedTokenList in information card MUST not be null");
    	}
    	if (informationCardMetaData.getTokenServiceReference() == null) {
    		throw new IllegalArgumentException("TokenServiceList in information card MUST not be null");
    	}
    	
        this.informationCardMetaData = informationCardMetaData;
        this.informationCardPrivateData = informationCardPrivateData;
    }

    public RoamingInformationCard(Element roamingInformationCardElement) throws ValidityException, IOException, ParsingException, ParsingException {
    	Element informationCardMetaDataElement = roamingInformationCardElement.getFirstChildElement("InformationCardMetaData", WSConstants.INFOCARD_NAMESPACE);
    	informationCardMetaData = new InformationCardMetaData(informationCardMetaDataElement);
    	Element informationCardPrivateDataElement = roamingInformationCardElement.getFirstChildElement("InformationCardPrivateData", WSConstants.INFOCARD_NAMESPACE);
    	if (informationCardPrivateDataElement != null) {
    		if (informationCardMetaData.getIsSelfIssued()) {
    			informationCardPrivateData = new SelfIssuedInformationCardPrivateData(informationCardPrivateDataElement);
    		} else {
    			informationCardPrivateData = new ManagedInformationCardPrivateData(informationCardPrivateDataElement);
    		}
    	}
    }

    private Element getRoamingInformationCard() throws SerializationException {

        Element roamingInformationCard = new Element("RoamingInformationCard", WSConstants.INFOCARD_NAMESPACE);

        roamingInformationCard.appendChild(informationCardMetaData.serialize());
        if (informationCardPrivateData != null) {
        	roamingInformationCard.appendChild(informationCardPrivateData.serialize());
        }
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
