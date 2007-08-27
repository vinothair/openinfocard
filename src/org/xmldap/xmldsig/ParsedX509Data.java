package org.xmldap.xmldsig;

import java.security.InvalidParameterException;

import nu.xom.Element;

import org.xmldap.ws.WSConstants;

public class ParsedX509Data {
//  <dsig:X509Data>
//  <dsig:X509Certificate>MIIDjzCCAvigAwIBAgIG ... RPo71Qg6ApCinzllSDoga5zFbSS8pzX</dsig:X509Certificate>
// </dsig:X509Data>
	String certificateB64 = null;
	public ParsedX509Data(Element element) {
		Element child = element.getFirstChildElement("X509Certificate", WSConstants.DSIG_NAMESPACE);
		if (child != null) {
			certificateB64 = child.getValue();
		} else {
			throw new InvalidParameterException("Expected child element 'X509Certificate'");
		}

	}
	public String getCertificateB64() {
		return certificateB64;
	}
}
