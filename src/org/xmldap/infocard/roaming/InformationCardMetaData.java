package org.xmldap.infocard.roaming;

import java.util.Random;

import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.Elements;

import org.xmldap.exceptions.ParsingException;
import org.xmldap.exceptions.SerializationException;
import org.xmldap.infocard.InfoCard;
import org.xmldap.util.Base64;
import org.xmldap.ws.WSConstants;

public class InformationCardMetaData {
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

	String lang = null;
	boolean isSelfIssued;
	String pinDigest = null;			// optional
	String HashSalt = null;
	String timeLastUpdated = null;
	String issuerId = null;
	String issuerName = null;
	String backgroundColor = null;
	
    InfoCard card;

    public InformationCardMetaData(Element informationCardMetaDataElement) throws ParsingException {
    	if ("InformationCardMetaData".equals(informationCardMetaDataElement.getLocalName())) {
    		
    		Element elt;
    		elt = informationCardMetaDataElement.getFirstChildElement("InformationCard", WSConstants.INFOCARD_NAMESPACE);
    		if (elt != null) {
    			card = new InfoCard(elt);
    		} else {
    			throw new ParsingException("Expected IsSelfIssued");
    		}
    		elt = informationCardMetaDataElement.getFirstChildElement("IsSelfIssued", WSConstants.INFOCARD_NAMESPACE);
    		if (elt != null) {
    			String value = elt.getValue();
    			if ("true".equals(value) || ("1").equals(value)) {
    				isSelfIssued = true;
    			} else if("false".equals(value) || ("0").equals(value)) { 
    				isSelfIssued = false;
    			} else {
    				throw new ParsingException("The value of IsSelfIssued must be true,1,false or 0: " + value);
    			}
    		} else {
    			throw new ParsingException("Expected IsSelfIssued");
    		}
    		elt = informationCardMetaDataElement.getFirstChildElement("PinDigest", WSConstants.INFOCARD_NAMESPACE);
    		if (elt != null) {
    			this.pinDigest = elt.getValue();
    		} else {
    			// optional
    		}
    		elt = informationCardMetaDataElement.getFirstChildElement("HashSalt", WSConstants.INFOCARD_NAMESPACE);
    		if (elt != null) {
    			this.HashSalt = elt.getValue();
    		} else {
    			throw new ParsingException("Expected HashSalt");
    		}
    		elt = informationCardMetaDataElement.getFirstChildElement("TimeLastUpdated", WSConstants.INFOCARD_NAMESPACE);
    		if (elt != null) {
    			this.HashSalt = elt.getValue();
    		} else {
    			throw new ParsingException("Expected TimeLastUpdated");
    		}
    		elt = informationCardMetaDataElement.getFirstChildElement("IssuerId", WSConstants.INFOCARD_NAMESPACE);
    		if (elt != null) {
    			this.issuerId = elt.getValue();
    		} else {
    			throw new ParsingException("Expected IssuerId");
    		}
    		elt = informationCardMetaDataElement.getFirstChildElement("IssuerName", WSConstants.INFOCARD_NAMESPACE);
    		if (elt != null) {
    			this.issuerName = elt.getValue();
    		} else {
    			throw new ParsingException("Expected IssuerName");
    		}
    		elt = informationCardMetaDataElement.getFirstChildElement("BackgroundColor", WSConstants.INFOCARD_NAMESPACE);
    		if (elt != null) {
    			this.backgroundColor = elt.getValue();
    		} else {
    			throw new ParsingException("Expected BackgroundColor");
    		}
    	} else {
    		throw new ParsingException("Expected ic:InformationCardMetaData");
    	}
    }
    
    Element serialize() throws SerializationException {
        Element informationCardMetaData = new Element("InformationCardMetaData", WSConstants.INFOCARD_NAMESPACE);
        Attribute lang;
        if (this.lang != null) {
        	lang = new Attribute("xml:lang", "http://www.w3.org/XML/1998/namespace", this.lang);
        } else {
        	lang = new Attribute("xml:lang", "http://www.w3.org/XML/1998/namespace", "en");
        }
        informationCardMetaData.addAttribute(lang);
        Element cardElm = card.serialize();
        Elements children = cardElm.getChildElements();
        for (int i = 0; i < children.size(); i++) {
            Element child = children.get(i);
            child.detach();
            informationCardMetaData.appendChild(child);
        }


        Element isSelfIssued = new Element("IsSelfIssued", WSConstants.INFOCARD_NAMESPACE);
        if (org.xmldap.infocard.Constants.ISSUER_XMLSOAP.equals(card.getIssuer())) {
        	isSelfIssued.appendChild("true");
        } else {
        	isSelfIssued.appendChild("false");
        }
        informationCardMetaData.appendChild(isSelfIssued);
        
        if (this.HashSalt == null) {
        	Random random = new Random();
        	byte[] bytes = new byte[256];
        	random.nextBytes(bytes);
        	this.HashSalt = Base64.encodeBytesNoBreaks(bytes);
        }
        Element hashSaltElt = new Element("HashSalt", WSConstants.INFOCARD_NAMESPACE);
        informationCardMetaData.appendChild(hashSaltElt);
        hashSaltElt.appendChild(this.HashSalt);

        Element timeLastUpdated = new Element("TimeLastUpdated", WSConstants.INFOCARD_NAMESPACE);
        informationCardMetaData.appendChild(timeLastUpdated);
        timeLastUpdated.appendChild(card.getTimeIssued());

        Element issuerId = new Element("IssuerId", WSConstants.INFOCARD_NAMESPACE);
        informationCardMetaData.appendChild(issuerId);

        Element issuerNameElt = new Element("IssuerName", WSConstants.INFOCARD_NAMESPACE);
        informationCardMetaData.appendChild(issuerNameElt);
        String issuerNameStr = card.getIssuerName();
        if (issuerNameStr == null) {
        	issuerNameElt.appendChild(issuerNameStr);
        } else {
        	issuerNameElt.appendChild(card.getIssuer());
        }
        
        Element backgroundColor = new Element("BackgroundColor", WSConstants.INFOCARD_NAMESPACE);
        informationCardMetaData.appendChild(backgroundColor);
        if (this.backgroundColor == null) {
        	backgroundColor.appendChild("16777215");
        } else {
        	backgroundColor.appendChild(this.backgroundColor);
        }
        return informationCardMetaData;
    }
    public InformationCardMetaData(InfoCard card) {
    	this.card = card;
    }

	public String getLang() {
		return lang;
	}

	public void setLang(String lang) {
		this.lang = lang;
	}

	public String getPinDigest() {
		return pinDigest;
	}

	public void setPinDigest(String pinDigest) {
		this.pinDigest = pinDigest;
	}

	public String getHashSalt() {
		return HashSalt;
	}

	public void setHashSalt(String hashSalt) {
		HashSalt = hashSalt;
	}

	public String getTimeLastUpdated() {
		return timeLastUpdated;
	}

	public void setTimeLastUpdated(String timeLastUpdated) {
		this.timeLastUpdated = timeLastUpdated;
	}

	public String getIssuerId() {
		return issuerId;
	}

	public void setIssuerId(String issuerId) {
		this.issuerId = issuerId;
	}

	public String getIssuerName() {
		return issuerName;
	}

	public void setIssuerName(String issuerName) {
		this.issuerName = issuerName;
	}

	public String getBackgroundColor() {
		return backgroundColor;
	}

	public void setBackgroundColor(String backgroundColor) {
		this.backgroundColor = backgroundColor;
	}

	public void setCard(InfoCard card) {
		this.card = card;
	}

	public boolean getIsSelfIssued() {
		return isSelfIssued;
	}

	public void setIsSelfIssued(boolean isSelfIssued) {
		this.isSelfIssued = isSelfIssued;
	}
}
