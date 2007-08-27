package org.xmldap.ws.trust;

import java.util.List;

import nu.xom.Attribute;
import nu.xom.Element;

import org.xmldap.exceptions.SerializationException;
import org.xmldap.infocard.Constants;
import org.xmldap.ws.WSConstants;
import org.xmldap.xml.Serializable;

public class Claims implements Serializable {
	List<String> claimsUris = null;
	public Claims(List<String> claimsUris) {
		this.claimsUris = claimsUris;
	}
	
	/*
    *  <wst:Claims
    *   wst:Dialect=”http://schemas.xmlsoap.org/ws/2005/05/identity”>
    *   <ic:ClaimType Uri=”http://.../identity/claims/givenname”/>
    *   <ic:ClaimType Uri=”http://.../identity/claims/surname”/>
    *  </wst:Claims>
    */
	public Element serialize() throws SerializationException {
        Element claims = new Element(WSConstants.TRUST_PREFIX + ":Claims", WSConstants.TRUST_NAMESPACE_04_04);    	        	
        Attribute dialect = new Attribute(WSConstants.TRUST_PREFIX + ":Dialect", WSConstants.TRUST_NAMESPACE_04_04);
        claims.addAttribute(dialect);

        for (String uri : claimsUris) {
            Element ic = new Element("ic" + ":ClaimType", Constants.IC_NAMESPACE);    	        	
            Attribute uriAttr = new Attribute("Uri", uri);
            ic.addAttribute(uriAttr);
            claims.appendChild(ic);
        }

		return claims;
	}

	public String toXML() throws SerializationException {
		return serialize().toXML();
	}

}
