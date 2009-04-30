package org.xmldap.infocard.roaming;

import java.util.Random;

import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.Elements;

import org.xmldap.exceptions.ParsingException;
import org.xmldap.infocard.SelfIssuedClaims;
import org.xmldap.util.Base64;
import org.xmldap.ws.WSConstants;

public class SelfIssuedInformationCardPrivateData implements InformationCardPrivateData, Comparable<SelfIssuedInformationCardPrivateData> {
//    <ic:InformationCardPrivateData> ?
//    <ic:MasterKey> xs:base64Binary </ic:MasterKey>
//    <ic:ClaimValueList> ?
//      <ic:ClaimValue Uri="xs:anyURI" ...> +
//        <ic:Value> xs:string </ic:Value>
//      </ic:ClaimValue>
//    </ic:ClaimValueList>
//  </ic:InformationCardPrivateData>

	String masterKey = null;
	SelfIssuedClaims selfIssuedClaims = null;
	
	public SelfIssuedInformationCardPrivateData() {
    	Random random = new Random();
    	byte[] bytes = new byte[256];
    	random.nextBytes(bytes);
    	this.masterKey = Base64.encodeBytesNoBreaks(bytes);
	}
	
	public SelfIssuedInformationCardPrivateData(byte[] masterKeyBytes) {
    	this.masterKey = Base64.encodeBytesNoBreaks(masterKeyBytes);
	}

	public SelfIssuedInformationCardPrivateData(SelfIssuedClaims selfIssuedClaims, byte[] masterKeyBytes) {
    	this.masterKey = Base64.encodeBytesNoBreaks(masterKeyBytes);
    	this.selfIssuedClaims = selfIssuedClaims;
	}

	public SelfIssuedInformationCardPrivateData(Element informationCardPrivateDataElement) throws ParsingException {
	   	if ("InformationCardPrivateData".equals(informationCardPrivateDataElement.getLocalName())) {
	   		Elements elts = informationCardPrivateDataElement.getChildElements("MasterKey", WSConstants.INFOCARD_NAMESPACE);
	   		if (elts.size() != 1) {
	   			throw new ParsingException("Found " + elts.size() + " MasterKey elements in  ic:InformationCardPrivateData");
	   		} else {
	   			Element masterkeyElement = elts.get(0);
	   			masterKey = masterkeyElement.getValue();
	   		}
	   		
	   		elts = informationCardPrivateDataElement.getChildElements("ClaimValueList", WSConstants.INFOCARD_NAMESPACE);
	   		if (elts.size() != 1) {
	   			throw new ParsingException("Found " + elts.size() + " ClaimValueList elements in  ic:InformationCardPrivateData");
	   		} else {
	   			Element claimValueList = elts.get(0);
	   			elts = claimValueList.getChildElements("ClaimValue", WSConstants.INFOCARD_NAMESPACE);
	   			if (elts.size() < 1) {
		   			throw new ParsingException("Found " + elts.size() + " ClaimValue elements in  ic:InformationCardPrivateData");
		   		} else {
		   			for (int i=0; i<elts.size(); i++) {
		   				Element claimValueElement = elts.get(i);
		   				String uri = claimValueElement.getAttributeValue("Uri");
		   				if (org.xmldap.infocard.Constants.IC_NS_GIVENNAME.equals(uri)) {
		   					if (selfIssuedClaims == null) {
		   						selfIssuedClaims = new SelfIssuedClaims();
		   					}
		   					Elements valueElements = claimValueElement.getChildElements("Value", WSConstants.INFOCARD_NAMESPACE);
			   				if (valueElements.size() != 1) {
			   					throw new ParsingException("Found " + valueElements.size() + " Value elements for Uri " + uri + " in  ic:InformationCardPrivateData");
			   				} else {
			   					Element valueElement = valueElements.get(0);
			   					String value = valueElement.getValue();
			   					selfIssuedClaims.setGivenName(value);
			   				}
		   				} else if (org.xmldap.infocard.Constants.IC_NS_SURNAME.equals(uri)) {
		   					if (selfIssuedClaims == null) {
		   						selfIssuedClaims = new SelfIssuedClaims();
		   					}
		   					Elements valueElements = claimValueElement.getChildElements("Value", WSConstants.INFOCARD_NAMESPACE);
			   				if (valueElements.size() != 1) {
			   					throw new ParsingException("Found " + valueElements.size() + " Value elements for Uri " + uri + " in  ic:InformationCardPrivateData");
			   				} else {
			   					Element valueElement = valueElements.get(0);
			   					String value = valueElement.getValue();
			   					selfIssuedClaims.setSurname(value);
			   				}
		   				} else if (org.xmldap.infocard.Constants.IC_NS_EMAILADDRESS.equals(uri)) {
		   					if (selfIssuedClaims == null) {
		   						selfIssuedClaims = new SelfIssuedClaims();
		   					}
		   					Elements valueElements = claimValueElement.getChildElements("Value", WSConstants.INFOCARD_NAMESPACE);
			   				if (valueElements.size() != 1) {
			   					throw new ParsingException("Found " + valueElements.size() + " Value elements for Uri " + uri + " in  ic:InformationCardPrivateData");
			   				} else {
			   					Element valueElement = valueElements.get(0);
			   					String value = valueElement.getValue();
			   					selfIssuedClaims.setEmailAddress(value);
			   				}
		   				} else if (org.xmldap.infocard.Constants.IC_NS_STREETADDRESS.equals(uri)) {
		   					if (selfIssuedClaims == null) {
		   						selfIssuedClaims = new SelfIssuedClaims();
		   					}
		   					Elements valueElements = claimValueElement.getChildElements("Value", WSConstants.INFOCARD_NAMESPACE);
			   				if (valueElements.size() != 1) {
			   					throw new ParsingException("Found " + valueElements.size() + " Value elements for Uri " + uri + " in  ic:InformationCardPrivateData");
			   				} else {
			   					Element valueElement = valueElements.get(0);
			   					String value = valueElement.getValue();
			   					selfIssuedClaims.setStreetAddress(value);
			   				}
		   				} else if (org.xmldap.infocard.Constants.IC_NS_STATEORPROVINCE.equals(uri)) {
		   					if (selfIssuedClaims == null) {
		   						selfIssuedClaims = new SelfIssuedClaims();
		   					}
		   					Elements valueElements = claimValueElement.getChildElements("Value", WSConstants.INFOCARD_NAMESPACE);
			   				if (valueElements.size() != 1) {
			   					throw new ParsingException("Found " + valueElements.size() + " Value elements for Uri " + uri + " in  ic:InformationCardPrivateData");
			   				} else {
			   					Element valueElement = valueElements.get(0);
			   					String value = valueElement.getValue();
			   					selfIssuedClaims.setStateOrProvince(value);
			   				}
		   				} else if (org.xmldap.infocard.Constants.IC_NS_POSTALCODE.equals(uri)) {
		   					if (selfIssuedClaims == null) {
		   						selfIssuedClaims = new SelfIssuedClaims();
		   					}
		   					Elements valueElements = claimValueElement.getChildElements("Value", WSConstants.INFOCARD_NAMESPACE);
			   				if (valueElements.size() != 1) {
			   					throw new ParsingException("Found " + valueElements.size() + " Value elements for Uri " + uri + " in  ic:InformationCardPrivateData");
			   				} else {
			   					Element valueElement = valueElements.get(0);
			   					String value = valueElement.getValue();
			   					selfIssuedClaims.setPostalCode(value);
			   				}
		   				} else if (org.xmldap.infocard.Constants.IC_NS_COUNTRY.equals(uri)) {
		   					if (selfIssuedClaims == null) {
		   						selfIssuedClaims = new SelfIssuedClaims();
		   					}
		   					Elements valueElements = claimValueElement.getChildElements("Value", WSConstants.INFOCARD_NAMESPACE);
			   				if (valueElements.size() != 1) {
			   					throw new ParsingException("Found " + valueElements.size() + " Value elements for Uri " + uri + " in  ic:InformationCardPrivateData");
			   				} else {
			   					Element valueElement = valueElements.get(0);
			   					String value = valueElement.getValue();
			   					selfIssuedClaims.setCountry(value);
			   				}
		   				} else if (org.xmldap.infocard.Constants.IC_NS_HOMEPHONE.equals(uri)) {
		   					if (selfIssuedClaims == null) {
		   						selfIssuedClaims = new SelfIssuedClaims();
		   					}
		   					Elements valueElements = claimValueElement.getChildElements("Value", WSConstants.INFOCARD_NAMESPACE);
			   				if (valueElements.size() != 1) {
			   					throw new ParsingException("Found " + valueElements.size() + " Value elements for Uri " + uri + " in  ic:InformationCardPrivateData");
			   				} else {
			   					Element valueElement = valueElements.get(0);
			   					String value = valueElement.getValue();
			   					selfIssuedClaims.setPrimaryPhone(value);
			   				}
		   				} else if (org.xmldap.infocard.Constants.IC_NS_OTHERPHONE.equals(uri)) {
		   					if (selfIssuedClaims == null) {
		   						selfIssuedClaims = new SelfIssuedClaims();
		   					}
		   					Elements valueElements = claimValueElement.getChildElements("Value", WSConstants.INFOCARD_NAMESPACE);
			   				if (valueElements.size() != 1) {
			   					throw new ParsingException("Found " + valueElements.size() + " Value elements for Uri " + uri + " in  ic:InformationCardPrivateData");
			   				} else {
			   					Element valueElement = valueElements.get(0);
			   					String value = valueElement.getValue();
			   					selfIssuedClaims.setOtherPhone(value);
			   				}
		   				} else if (org.xmldap.infocard.Constants.IC_NS_MOBILEPHONE.equals(uri)) {
		   					if (selfIssuedClaims == null) {
		   						selfIssuedClaims = new SelfIssuedClaims();
		   					}
		   					Elements valueElements = claimValueElement.getChildElements("Value", WSConstants.INFOCARD_NAMESPACE);
			   				if (valueElements.size() != 1) {
			   					throw new ParsingException("Found " + valueElements.size() + " Value elements for Uri " + uri + " in  ic:InformationCardPrivateData");
			   				} else {
			   					Element valueElement = valueElements.get(0);
			   					String value = valueElement.getValue();
			   					selfIssuedClaims.setMobilePhone(value);
			   				}
		   				} else if (org.xmldap.infocard.Constants.IC_NS_DATEOFBIRTH.equals(uri)) {
		   					if (selfIssuedClaims == null) {
		   						selfIssuedClaims = new SelfIssuedClaims();
		   					}
		   					Elements valueElements = claimValueElement.getChildElements("Value", WSConstants.INFOCARD_NAMESPACE);
			   				if (valueElements.size() != 1) {
			   					throw new ParsingException("Found " + valueElements.size() + " Value elements for Uri " + uri + " in  ic:InformationCardPrivateData");
			   				} else {
			   					Element valueElement = valueElements.get(0);
			   					String value = valueElement.getValue();
			   					selfIssuedClaims.setDateOfBirth(value);
			   				}
		   				} else if (org.xmldap.infocard.Constants.IC_NS_GENDER.equals(uri)) {
		   					if (selfIssuedClaims == null) {
		   						selfIssuedClaims = new SelfIssuedClaims();
		   					}
		   					Elements valueElements = claimValueElement.getChildElements("Value", WSConstants.INFOCARD_NAMESPACE);
			   				if (valueElements.size() != 1) {
			   					throw new ParsingException("Found " + valueElements.size() + " Value elements for Uri " + uri + " in  ic:InformationCardPrivateData");
			   				} else {
			   					Element valueElement = valueElements.get(0);
			   					String value = valueElement.getValue();
			   					selfIssuedClaims.setGender(value);
			   				}
		   				} else if (org.xmldap.infocard.Constants.IC_NS_LOCALITY.equals(uri)) {
		   					if (selfIssuedClaims == null) {
		   						selfIssuedClaims = new SelfIssuedClaims();
		   					}
		   					Elements valueElements = claimValueElement.getChildElements("Value", WSConstants.INFOCARD_NAMESPACE);
			   				if (valueElements.size() != 1) {
			   					throw new ParsingException("Found " + valueElements.size() + " Value elements for Uri " + uri + " in  ic:InformationCardPrivateData");
			   				} else {
			   					Element valueElement = valueElements.get(0);
			   					String value = valueElement.getValue();
			   					selfIssuedClaims.setLocality(value);
			   				}
		   				} else if (org.xmldap.infocard.Constants.IC_NS_WEBPAGE.equals(uri)) {
		   					if (selfIssuedClaims == null) {
		   						selfIssuedClaims = new SelfIssuedClaims();
		   					}
		   					Elements valueElements = claimValueElement.getChildElements("Value", WSConstants.INFOCARD_NAMESPACE);
			   				if (valueElements.size() != 1) {
			   					throw new ParsingException("Found " + valueElements.size() + " Value elements for Uri " + uri + " in  ic:InformationCardPrivateData");
			   				} else {
			   					Element valueElement = valueElements.get(0);
			   					String value = valueElement.getValue();
			   					selfIssuedClaims.setWebPage(value);
			   				}
		   				} else {
		   					throw new ParsingException("Found unknown Uri in self-issued ic:InformationCardPrivateData: " + uri);
		   				}
		   			}
		   		}
	   		}
	   	} else {
	   		throw new ParsingException("Expected ic:InformationCardPrivateData");
	   	}
	}
	
	private void addClaimValue(Element claimValueList, String claimName, String nameSpace, String value) {
        Element claimValueElt = new Element("ClaimValue", WSConstants.INFOCARD_NAMESPACE);
        Attribute uriElt = new Attribute("Uri", nameSpace+claimName);
        claimValueElt.addAttribute(uriElt);
        Element valueElt = new Element("Value", WSConstants.INFOCARD_NAMESPACE);
        claimValueElt.appendChild(valueElt);
        valueElt.appendChild(value);
        claimValueList.appendChild(claimValueElt);
	}

	public Element serialize() {
        Element informationCardPrivateData = new Element("InformationCardPrivateData", WSConstants.INFOCARD_NAMESPACE);

        Element masterKeyElt = new Element("MasterKey", WSConstants.INFOCARD_NAMESPACE);
        masterKeyElt.appendChild(masterKey);
        informationCardPrivateData.appendChild(masterKeyElt);

        if (selfIssuedClaims != null) {
	        Element claimValueList = new Element("ClaimValueList", WSConstants.INFOCARD_NAMESPACE);
	        informationCardPrivateData.appendChild(claimValueList);

	        String claimsNamespace = org.xmldap.infocard.Constants.IC_NAMESPACE_PREFIX;
			addClaimValue(claimValueList, "givenname", claimsNamespace, selfIssuedClaims.getGivenName());
			addClaimValue(claimValueList, "surname", claimsNamespace, selfIssuedClaims.getSurname());
			addClaimValue(claimValueList, "emailaddress", claimsNamespace, selfIssuedClaims.getEmailAddress());
			addClaimValue(claimValueList, "streetaddress", claimsNamespace, selfIssuedClaims.getStreetAddress());
			addClaimValue(claimValueList, "locality", claimsNamespace, selfIssuedClaims.getLocality());
			addClaimValue(claimValueList, "stateorprovince", claimsNamespace, selfIssuedClaims.getStateOrProvince());
			addClaimValue(claimValueList, "postalcode", claimsNamespace,	selfIssuedClaims.getPostalCode());
			addClaimValue(claimValueList, "country", claimsNamespace, selfIssuedClaims.getCountry());
			addClaimValue(claimValueList, "primaryphone", claimsNamespace, selfIssuedClaims.getPrimaryPhone());
			addClaimValue(claimValueList, "otherphone", claimsNamespace,selfIssuedClaims.getOtherPhone());
			addClaimValue(claimValueList, "mobilephone",	claimsNamespace, selfIssuedClaims.getMobilePhone());
			addClaimValue(claimValueList, "dateofbirth",	claimsNamespace, selfIssuedClaims.getDateOfBirth());
			addClaimValue(claimValueList, "privatepersonalidentifier", claimsNamespace,selfIssuedClaims.getPrivatePersonalIdentifier());
			addClaimValue(claimValueList, "gender", claimsNamespace, selfIssuedClaims.getGender());
			addClaimValue(claimValueList, "webpage", claimsNamespace, selfIssuedClaims.getWebPage());
        }
        return informationCardPrivateData;
	}

	@Override
	public int compareTo(SelfIssuedInformationCardPrivateData obj) {
	    	if (this == obj) return 0;
	    	
	    	SelfIssuedInformationCardPrivateData anObj = (SelfIssuedInformationCardPrivateData)obj;
	    	int comparison = masterKey.compareTo(anObj.masterKey);
	    	if (comparison != 0) return comparison;
	    	
	    	comparison = selfIssuedClaims.compareTo(obj.selfIssuedClaims);
	    	return comparison;
	}
}
