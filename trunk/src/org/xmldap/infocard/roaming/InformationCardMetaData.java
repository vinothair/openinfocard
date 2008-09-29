package org.xmldap.infocard.roaming;

import java.util.List;
import java.util.Random;

import nu.xom.Attribute;
import nu.xom.Element;

import org.xmldap.exceptions.ParsingException;
import org.xmldap.exceptions.SerializationException;
import org.xmldap.infocard.InfoCard;
import org.xmldap.infocard.TokenServiceReference;
import org.xmldap.infocard.UserCredential;
import org.xmldap.util.Base64;
import org.xmldap.ws.WSConstants;

public class InformationCardMetaData extends InfoCard {
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

	//String lang = null;
	boolean isSelfIssued;
	String pinDigest = null;			// optional
	String HashSalt = null;
	String timeLastUpdated = null;
	String issuerId = null;
	String issuerName = null;
	String backgroundColor = null;
	
    //InfoCard card;

    public InformationCardMetaData(Element informationCardMetaDataElement) throws ParsingException {
    	if ("InformationCardMetaData".equals(informationCardMetaDataElement.getLocalName())) {
    		
    		Element elt = informationCardMetaDataElement;
//    		elt = informationCardMetaDataElement.getFirstChildElement("InformationCard", WSConstants.INFOCARD_NAMESPACE);
//    		if (elt != null) {
//    			card = new InfoCard(elt);
    			super.createFromElement(elt);
//    		} else {
//    			throw new ParsingException("Expected InformationCard");
//    		}
    		elt = informationCardMetaDataElement.getFirstChildElement("IsSelfIssued", WSConstants.INFOCARD_NAMESPACE);
    		if (elt != null) {
    			super.bastards.remove(elt);
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
    			super.bastards.remove(elt);
    			this.pinDigest = elt.getValue();
    		} else {
    			// optional
    		}
    		elt = informationCardMetaDataElement.getFirstChildElement("HashSalt", WSConstants.INFOCARD_NAMESPACE);
    		if (elt != null) {
    			super.bastards.remove(elt);
    			this.HashSalt = elt.getValue();
    		} else {
    			throw new ParsingException("Expected HashSalt");
    		}
    		elt = informationCardMetaDataElement.getFirstChildElement("TimeLastUpdated", WSConstants.INFOCARD_NAMESPACE);
    		if (elt != null) {
    			super.bastards.remove(elt);
    			this.timeLastUpdated = elt.getValue();
    		} else {
    			throw new ParsingException("Expected TimeLastUpdated");
    		}
    		elt = informationCardMetaDataElement.getFirstChildElement("IssuerId", WSConstants.INFOCARD_NAMESPACE);
    		if (elt != null) {
    			super.bastards.remove(elt);
    			this.issuerId = elt.getValue();
    		} else {
    			throw new ParsingException("Expected IssuerId");
    		}
    		elt = informationCardMetaDataElement.getFirstChildElement("IssuerName", WSConstants.INFOCARD_NAMESPACE);
    		if (elt != null) {
    			super.bastards.remove(elt);
    			this.issuerName = elt.getValue();
    		} else {
    			throw new ParsingException("Expected IssuerName");
    		}
    		elt = informationCardMetaDataElement.getFirstChildElement("BackgroundColor", WSConstants.INFOCARD_NAMESPACE);
    		if (elt != null) {
    			super.bastards.remove(elt);
    			this.backgroundColor = elt.getValue();
    		} else {
    			throw new ParsingException("Expected BackgroundColor");
    		}
    	} else {
    		throw new ParsingException("Expected ic:InformationCardMetaData");
    	}
    }
    
    public Element serialize() throws SerializationException {
        Element informationCardMetaData = new Element("InformationCardMetaData", WSConstants.INFOCARD_NAMESPACE);
        Attribute lang;
        if (this.lang != null) {
        	lang = new Attribute("xml:lang", "http://www.w3.org/XML/1998/namespace", this.lang);
        } else {
        	lang = new Attribute("xml:lang", "http://www.w3.org/XML/1998/namespace", "en");
        }
        informationCardMetaData.addAttribute(lang);
        
        super.appendChildren(informationCardMetaData);
//        Element cardElm = super.serialize();
//        informationCardMetaData.appendChild(cardElm);
//        Elements children = cardElm.getChildElements();
//        for (int i = 0; i < children.size(); i++) {
//            Element child = children.get(i);
//            child.detach();
//            informationCardMetaData.appendChild(child);
//        }


        Element isSelfIssuedElt = new Element("IsSelfIssued", WSConstants.INFOCARD_NAMESPACE);
        if (isSelfIssued) {
        	isSelfIssuedElt.appendChild("true");
        } else {
        	isSelfIssuedElt.appendChild("false");
        }
        informationCardMetaData.appendChild(isSelfIssuedElt);
        
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
        timeLastUpdated.appendChild(super.getTimeIssued());

        Element issuerIdElt = new Element("IssuerId", WSConstants.INFOCARD_NAMESPACE);
        informationCardMetaData.appendChild(issuerIdElt);
        if (!isSelfIssued) {
        	boolean needIssuerId = false;
        	List<TokenServiceReference> tsrl = super.getTokenServiceReference();
        	for (TokenServiceReference tsr : tsrl) {
        		UserCredential userCredential = tsr.getUserCredential();
        		if (UserCredential.SELF_ISSUED.equals(userCredential.getAuthType())) {
        			needIssuerId = true;
        			break;
        		}
        	}
        	if (needIssuerId) {
        		if (issuerIdElt != null) {
        			issuerIdElt.appendChild(this.issuerId);
        		} else {
        			throw new SerializationException("IssuerId is unknown but card is backed by a self-issued card");
        		}
        	}
        } // else IssuerId is empty
        
        Element issuerNameElt = new Element("IssuerName", WSConstants.INFOCARD_NAMESPACE);
        informationCardMetaData.appendChild(issuerNameElt);
        if (!isSelfIssued) {
        	if (this.issuerName != null) {
        		issuerNameElt.appendChild(this.issuerName);
        	} else {
            	// ISIP says this SHOULD be the O-value from the EV-Cert or the CN-Value from the cert.
            	// Don't have the cert here...
            	// TODO
        		throw new SerializationException("required element IssuerName is null");
        	}
        } // else IssuerName is empty
        
        Element backgroundColor = new Element("BackgroundColor", WSConstants.INFOCARD_NAMESPACE);
        informationCardMetaData.appendChild(backgroundColor);
        if (this.backgroundColor == null) {
        	backgroundColor.appendChild("16777215");
        } else {
        	backgroundColor.appendChild(this.backgroundColor);
        }
        return informationCardMetaData;
    }
    
    public InformationCardMetaData(InfoCard card, String issuerName) {
    	super(card);
    	this.isSelfIssued = org.xmldap.infocard.Constants.ISSUER_XMLSOAP.equals(card.getIssuer());
    	this.issuerName = issuerName;
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

    public String getIssuerName() {
        return issuerName;
    }

    public void setIssuerName(String issuerName) {
        this.issuerName = issuerName;
    }

	public String getIssuerId() {
		return issuerId;
	}

	public void setIssuerId(String issuerId) {
		this.issuerId = issuerId;
	}

	public String getBackgroundColor() {
		return backgroundColor;
	}

	public void setBackgroundColor(String backgroundColor) {
		this.backgroundColor = backgroundColor;
	}

	public boolean getIsSelfIssued() {
		return isSelfIssued;
	}

	public void setIsSelfIssued(boolean isSelfIssued) {
		this.isSelfIssued = isSelfIssued;
	}
}
