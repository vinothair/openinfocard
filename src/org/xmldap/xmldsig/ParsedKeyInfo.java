package org.xmldap.xmldsig;

import org.xmldap.ws.WSConstants;

import nu.xom.Element;

// <dsig:KeyInfo>
//  <dsig:KeyName>Public Key for C=US, ST=California, L=San Francisco, O=xmldap, OU=infocard selector, CN=firefox</dsig:KeyName>
//  <dsig:KeyValue>
//   <dsig:RSAKeyValue>
//    <dsig:Modulus>ALcns4ngVsyotUe ... 31OAmEtdnF7MZ</dsig:Modulus>
//    <dsig:Exponent>AQAB</dsig:Exponent>
//   </dsig:RSAKeyValue>
//  </dsig:KeyValue>
//  <dsig:X509Data>
//   <dsig:X509Certificate>MIIDjzCCAvigAwIBAgIG ... RPo71Qg6ApCinzllSDoga5zFbSS8pzX</dsig:X509Certificate>
//  </dsig:X509Data>
// </dsig:KeyInfo>

public class ParsedKeyInfo {
	String certificate = null;
	String keyName = null;
	ParsedKeyValue keyValue = null;
	ParsedX509Data x509Data = null;
	public ParsedKeyInfo(Element element) {
		Element child = element.getFirstChildElement("KeyName", WSConstants.DSIG_NAMESPACE);
		if (child != null) {
			keyName = child.getValue();
		}
		child = element.getFirstChildElement("KeyValue", WSConstants.DSIG_NAMESPACE);
		if (child != null) {
			keyValue = new ParsedKeyValue(child);
		}
		child = element.getFirstChildElement("X509Data", WSConstants.DSIG_NAMESPACE);
		if (child != null) {
			x509Data = new ParsedX509Data(child);
		}
	}
	
	public ParsedX509Data getParsedX509Data() {
		return x509Data;
	}
	
	public ParsedKeyValue getParsedKeyValue() {
		return keyValue;
	}
}
