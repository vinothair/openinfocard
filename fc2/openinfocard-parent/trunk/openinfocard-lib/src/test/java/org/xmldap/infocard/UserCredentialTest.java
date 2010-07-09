package org.xmldap.infocard;


import java.io.IOException;

import junit.framework.TestCase;

import nu.xom.Document;
import nu.xom.Element;
import nu.xom.ParsingException;

import org.junit.Before;
import org.xmldap.exceptions.SerializationException;
import org.xmldap.xml.XmlUtils;

public class UserCredentialTest extends TestCase {

	@Before
	public void setUp() throws Exception {
	}

	public void testUsernamePasswordCredential() throws SerializationException, IOException, ParsingException, org.xmldap.exceptions.ParsingException {
		UserCredential uc = new UserCredential(UserCredential.USERNAME, "username");
		String ucXmlStr = uc.serialize().toXML();
		String expected = "<ic:UserCredential xmlns:ic=\"http://schemas.xmlsoap.org/ws/2005/05/identity\"><ic:DisplayCredentialHint>Please enter your username and password.</ic:DisplayCredentialHint><ic:UsernamePasswordCredential><ic:Username>username</ic:Username></ic:UsernamePasswordCredential></ic:UserCredential>";
		assertEquals(expected, ucXmlStr);
		Document doc = XmlUtils.parse(expected);
		Element root = doc.getRootElement();
		UserCredential parsedUc = new UserCredential(root);
		String parsedUcXmlStr = parsedUc.serialize().toXML();
		assertEquals(parsedUcXmlStr, ucXmlStr);
	}

	public void testUsernamePasswordCredentialWithHint() throws SerializationException, IOException, ParsingException, org.xmldap.exceptions.ParsingException {
		UserCredential uc = new UserCredential(UserCredential.USERNAME, "username");
		uc.setHint("hint");
		String ucXmlStr = uc.serialize().toXML();
		String expected = "<ic:UserCredential xmlns:ic=\"http://schemas.xmlsoap.org/ws/2005/05/identity\"><ic:DisplayCredentialHint>hint</ic:DisplayCredentialHint><ic:UsernamePasswordCredential><ic:Username>username</ic:Username></ic:UsernamePasswordCredential></ic:UserCredential>";
		assertEquals(expected, ucXmlStr);
		Document doc = XmlUtils.parse(expected);
		Element root = doc.getRootElement();
		UserCredential parsedUc = new UserCredential(root);
		String parsedUcXmlStr = parsedUc.serialize().toXML();
		assertEquals(parsedUcXmlStr, ucXmlStr);
	}

	public void testKerberosV5Credential() throws SerializationException, IOException, ParsingException, org.xmldap.exceptions.ParsingException {
		UserCredential uc = new UserCredential(UserCredential.KERB, "kerberos");
		String ucXmlStr = uc.serialize().toXML();
		String expected = "<ic:UserCredential xmlns:ic=\"http://schemas.xmlsoap.org/ws/2005/05/identity\"><ic:DisplayCredentialHint>Enter your kerberos credentials</ic:DisplayCredentialHint><ic:KerberosV5Credential /></ic:UserCredential>";
		assertEquals(expected, ucXmlStr);
		Document doc = XmlUtils.parse(expected);
		Element root = doc.getRootElement();
		UserCredential parsedUc = new UserCredential(root);
		String parsedUcXmlStr = parsedUc.serialize().toXML();
		assertEquals(parsedUcXmlStr, ucXmlStr);
	}

	public void testKerberosV5CredentialWithHint() throws SerializationException, IOException, ParsingException, org.xmldap.exceptions.ParsingException {
		UserCredential uc = new UserCredential(UserCredential.KERB, "kerberos");
		uc.setHint("hint");
		String ucXmlStr = uc.serialize().toXML();
		String expected = "<ic:UserCredential xmlns:ic=\"http://schemas.xmlsoap.org/ws/2005/05/identity\"><ic:DisplayCredentialHint>hint</ic:DisplayCredentialHint><ic:KerberosV5Credential /></ic:UserCredential>";
		assertEquals(expected, ucXmlStr);
		Document doc = XmlUtils.parse(expected);
		Element root = doc.getRootElement();
		UserCredential parsedUc = new UserCredential(root);
		String parsedUcXmlStr = parsedUc.serialize().toXML();
		assertEquals(parsedUcXmlStr, ucXmlStr);
	}

	public void testX509V3Credential() throws SerializationException, IOException, ParsingException, org.xmldap.exceptions.ParsingException {
		UserCredential uc = new UserCredential(UserCredential.X509, "x509");
		String ucXmlStr = uc.serialize().toXML();
		String expected = "<ic:UserCredential xmlns:ic=\"http://schemas.xmlsoap.org/ws/2005/05/identity\"><ic:DisplayCredentialHint>Choose a certificate</ic:DisplayCredentialHint><ic:X509V3Credential><ds:X509Data xmlns:ds=\"http://www.w3.org/2000/09/xmldsig#\"><wsse:KeyIdentifier xmlns:wsse=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd\" ValueType=\"http://docs.oasis-open.org/wss/2004/xx/oasis-2004xx-wss-soap-message-security-1.1#ThumbprintSHA1\" EncodingType=\"http://docs.oasis-open.org/wss/2004/xx/oasis-2004xx-wss-soap-message-security-1.1#Base64Binary\">x509</wsse:KeyIdentifier></ds:X509Data></ic:X509V3Credential></ic:UserCredential>";
		assertEquals(expected, ucXmlStr);
		Document doc = XmlUtils.parse(expected);
		Element root = doc.getRootElement();
		UserCredential parsedUc = new UserCredential(root);
		String parsedUcXmlStr = parsedUc.serialize().toXML();
		assertEquals(parsedUcXmlStr, ucXmlStr);
	}

	public void testX509V3CredentialWithHint() throws SerializationException, IOException, ParsingException, org.xmldap.exceptions.ParsingException {
		UserCredential uc = new UserCredential(UserCredential.X509, "x509");
		uc.setHint("hint");
		String ucXmlStr = uc.serialize().toXML();
		String expected = "<ic:UserCredential xmlns:ic=\"http://schemas.xmlsoap.org/ws/2005/05/identity\"><ic:DisplayCredentialHint>hint</ic:DisplayCredentialHint><ic:X509V3Credential><ds:X509Data xmlns:ds=\"http://www.w3.org/2000/09/xmldsig#\"><wsse:KeyIdentifier xmlns:wsse=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd\" ValueType=\"http://docs.oasis-open.org/wss/2004/xx/oasis-2004xx-wss-soap-message-security-1.1#ThumbprintSHA1\" EncodingType=\"http://docs.oasis-open.org/wss/2004/xx/oasis-2004xx-wss-soap-message-security-1.1#Base64Binary\">x509</wsse:KeyIdentifier></ds:X509Data></ic:X509V3Credential></ic:UserCredential>";
		assertEquals(expected, ucXmlStr);
		Document doc = XmlUtils.parse(expected);
		Element root = doc.getRootElement();
		UserCredential parsedUc = new UserCredential(root);
		String parsedUcXmlStr = parsedUc.serialize().toXML();
		assertEquals(parsedUcXmlStr, ucXmlStr);
	}

	public void testSelfIssuedCredential() throws SerializationException, IOException, ParsingException, org.xmldap.exceptions.ParsingException {
		UserCredential uc = new UserCredential(UserCredential.SELF_ISSUED, "self");
		String ucXmlStr = uc.serialize().toXML();
		String expected = "<ic:UserCredential xmlns:ic=\"http://schemas.xmlsoap.org/ws/2005/05/identity\"><ic:DisplayCredentialHint>Choose a self-asserted card</ic:DisplayCredentialHint><ic:SelfIssuedCredential><ic:PrivatePersonalIdentifier>self</ic:PrivatePersonalIdentifier></ic:SelfIssuedCredential></ic:UserCredential>";
		assertEquals(expected, ucXmlStr);
		Document doc = XmlUtils.parse(expected);
		Element root = doc.getRootElement();
		UserCredential parsedUc = new UserCredential(root);
		String parsedUcXmlStr = parsedUc.serialize().toXML();
		assertEquals(parsedUcXmlStr, ucXmlStr);
	}

	public void testSelfIssuedCredentialWithHint() throws SerializationException, IOException, ParsingException, org.xmldap.exceptions.ParsingException {
		UserCredential uc = new UserCredential(UserCredential.SELF_ISSUED, "self");
		uc.setHint("hint");
		String ucXmlStr = uc.serialize().toXML();
		String expected = "<ic:UserCredential xmlns:ic=\"http://schemas.xmlsoap.org/ws/2005/05/identity\"><ic:DisplayCredentialHint>hint</ic:DisplayCredentialHint><ic:SelfIssuedCredential><ic:PrivatePersonalIdentifier>self</ic:PrivatePersonalIdentifier></ic:SelfIssuedCredential></ic:UserCredential>";
		assertEquals(expected, ucXmlStr);
		Document doc = XmlUtils.parse(expected);
		Element root = doc.getRootElement();
		UserCredential parsedUc = new UserCredential(root);
		String parsedUcXmlStr = parsedUc.serialize().toXML();
		assertEquals(parsedUcXmlStr, ucXmlStr);
	}

}
