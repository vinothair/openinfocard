package org.xmldap.ws.trust;

import nu.xom.Attribute;
import nu.xom.Element;

import org.xmldap.exceptions.SerializationException;
import org.xmldap.ws.WSConstants;
import org.xmldap.xml.Serializable;
import org.xmldap.xmldsig.KeyInfo;

public class UseKey implements Serializable {
	String sig;
	KeyInfo keyInfo;
	public UseKey(String sig, KeyInfo keyInfo) {
		this.sig = sig;
		this.keyInfo = keyInfo;
	}
	public Element serialize() throws SerializationException {
        Element useKey = new Element(WSConstants.TRUST_PREFIX + ":UseKey", WSConstants.TRUST_NAMESPACE_04_04);    	        	
        Attribute sigAttr = new Attribute("Sig", WSConstants.TRUST_NAMESPACE_04_04, sig);
        useKey.addAttribute(sigAttr);
        useKey.appendChild(keyInfo.serialize());
		return useKey;
	}

	public String toXML() throws SerializationException {
		return serialize().toXML();
	}

}
