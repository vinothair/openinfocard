package org.xmldap.xmldsig;

import java.util.ArrayList;
import java.util.List;

import nu.xom.Element;
import nu.xom.Elements;

public class ParsedReference {
	String uri = null;
	String digestAlgorithm = null;
	String digestValue = null;
	List<String> transformAlgorithms = new ArrayList<String>();
	
	public ParsedReference(Element reference) {
		uri = reference.getAttribute("URI").getValue();
		Elements transforms = reference.getChildElements("Transforms", "http://www.w3.org/2000/09/xmldsig#");
		
		for (int t=0; t<transforms.size(); t++) {
			Element transform = transforms.get(t);
			String tranformAlgorithm = transform.getAttributeValue("Algorithm");
			transformAlgorithms.add(tranformAlgorithm);
		}
		Element digestMethod = reference.getFirstChildElement("DigestMethod", "http://www.w3.org/2000/09/xmldsig#");
		digestAlgorithm = digestMethod.getAttributeValue("Algorithm");
		digestValue = reference.getFirstChildElement("DigestValue", "http://www.w3.org/2000/09/xmldsig#").getValue();
	}
	
	public String getDigestValue() {
		return digestValue;
	}
	
	public String getDigestAlgorithm() {
		return digestAlgorithm;
	}
	
	public List<String> getTransformsAlgorithms() {
		return transformAlgorithms;
	}

	public String getUri() {
		return uri;
	}
}
