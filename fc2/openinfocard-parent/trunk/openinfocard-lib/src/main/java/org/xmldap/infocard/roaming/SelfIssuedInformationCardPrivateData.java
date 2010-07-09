package org.xmldap.infocard.roaming;

import java.util.Collection;
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
	
	public SelfIssuedClaims getSelfIssuedClaims() {
		return selfIssuedClaims;
	}

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
		   				{
		   					Elements valueElements = claimValueElement.getChildElements("Value", WSConstants.INFOCARD_NAMESPACE);
			   				if (valueElements.size() != 1) {
			   					throw new ParsingException("Found " + valueElements.size() + " Value elements for Uri " + uri + " in  ic:InformationCardPrivateData");
			   				} else {
			   					if (selfIssuedClaims == null) {
			   						selfIssuedClaims = new SelfIssuedClaims();
			   					}
			   					Element valueElement = valueElements.get(0);
			   					String value = valueElement.getValue();
			   					selfIssuedClaims.setClaim(uri, value);
			   				}
		   				}
		   			}
		   		}
	   		}
	   	} else {
	   		throw new ParsingException("Expected ic:InformationCardPrivateData");
	   	}
	}
	
	private void addClaimValue(Element claimValueList, String uri, String value) {
        Element claimValueElt = new Element("ClaimValue", WSConstants.INFOCARD_NAMESPACE);
        Attribute uriElt = new Attribute("Uri", uri);
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
            Collection<String>keySet = selfIssuedClaims.getKeySet();
            if (keySet.size() > 0) {
		        Element claimValueList = new Element("ClaimValueList", WSConstants.INFOCARD_NAMESPACE);
		        informationCardPrivateData.appendChild(claimValueList);
	
		        for (String key : keySet) {
        			addClaimValue(claimValueList, key, selfIssuedClaims.getClaim(key));
		        }
            }
        }
        return informationCardPrivateData;
	}

	public String toXML() {
		Element elt = serialize();
		return elt.toXML();
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
