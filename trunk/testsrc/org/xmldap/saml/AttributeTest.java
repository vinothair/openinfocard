package org.xmldap.saml;

import org.xmldap.exceptions.SerializationException;

import nu.xom.Element;
import junit.framework.TestCase;

public class AttributeTest extends TestCase {

	public void testToXML() {
		Attribute given = new Attribute(
				"givenname",
				"http://schemas.microsoft.com/ws/2005/05/identity/claims/GivenName",
				"Chuck");
		Element givenE = null;
		try {
			givenE = given.serialize();
		} catch (SerializationException e) {
			assertTrue(false);
		}

		assertEquals(
				"<saml:Attribute xmlns:saml=\"urn:oasis:names:tc:SAML:1.0:assertion\" " +
				"AttributeName=\"givenname\" " +
				"AttributeNamespace=\"http://schemas.microsoft.com/ws/2005/05/identity/claims/GivenName\">" +
				"<saml:AttributeValue>" +
				"Chuck" +
				"</saml:AttributeValue></saml:Attribute>",
				givenE.toXML());
	}

}
