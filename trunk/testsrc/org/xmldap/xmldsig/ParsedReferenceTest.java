package org.xmldap.xmldsig;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import nu.xom.Document;
import nu.xom.Element;
import nu.xom.ParsingException;
import nu.xom.canonical.Canonicalizer;

import org.xmldap.exceptions.SerializationException;

import junit.framework.TestCase;

public class ParsedReferenceTest extends TestCase {

	protected void setUp() throws Exception {
		super.setUp();
	}

	public void test() {

        Element assertionElement = null;
        try {
    		String assertionStr = "<saml:Assertion xmlns:saml=\"urn:oasis:names:tc:SAML:1.0:assertion\" AssertionID=\"uuid-7B20C5C0-9B85-35D1-590A-D1B3093451CF\" Issuer=\"http://schemas.microsoft.com/ws/2005/05/identity/issuer/self\" IssueInstant=\"2007-08-30T15:10:47Z\" MajorVersion=\"1\" MinorVersion=\"1\"><saml:Conditions NotBefore=\"2007-08-30T15:05:47Z\" NotOnOrAfter=\"2007-08-30T15:20:47Z\"><saml:AudienceRestrictionCondition><saml:Audience>https://w4de3esy0069028.gdc-bln01.t-systems.com:8443/relyingparty/</saml:Audience></saml:AudienceRestrictionCondition></saml:Conditions><saml:AttributeStatement><saml:Subject><saml:SubjectConfirmation><saml:ConfirmationMethod>urn:oasis:names:tc:SAML:1.0:cm:bearer</saml:ConfirmationMethod></saml:SubjectConfirmation></saml:Subject><saml:Attribute AttributeName=\"givenname\" AttributeNamespace=\"http://schemas.xmlsoap.org/ws/2005/05/identity/claims/givenname\"><saml:AttributeValue>Axel</saml:AttributeValue></saml:Attribute><saml:Attribute AttributeName=\"surname\" AttributeNamespace=\"http://schemas.xmlsoap.org/ws/2005/05/identity/claims/surname\"><saml:AttributeValue>Nennker</saml:AttributeValue></saml:Attribute><saml:Attribute AttributeName=\"emailaddress\" AttributeNamespace=\"http://schemas.xmlsoap.org/ws/2005/05/identity/claims/emailaddress\"><saml:AttributeValue>axel@nennker.de</saml:AttributeValue></saml:Attribute><saml:Attribute AttributeName=\"privatepersonalidentifier\" AttributeNamespace=\"http://schemas.xmlsoap.org/ws/2005/05/identity/claims/privatepersonalidentifier\"><saml:AttributeValue>bXRwZTJPZUhldWJKU1lydDMxWThodnB1cFpCRmd6MDVlaXViWWo3NzJaTT0=</saml:AttributeValue></saml:Attribute></saml:AttributeStatement></saml:Assertion>";
        	Document doc = org.xmldap.xml.XmlUtils.parse(assertionStr);
    		assertionElement = doc.getRootElement();
        } catch (IOException e) {
        	assertEquals("", e.getMessage());
		} catch (ParsingException e) {
			assertEquals("", e.getMessage());
		}

		String canonicalAssertion = "<saml:Assertion xmlns:saml=\"urn:oasis:names:tc:SAML:1.0:assertion\" AssertionID=\"uuid-7B20C5C0-9B85-35D1-590A-D1B3093451CF\" IssueInstant=\"2007-08-30T15:10:47Z\" Issuer=\"http://schemas.microsoft.com/ws/2005/05/identity/issuer/self\" MajorVersion=\"1\" MinorVersion=\"1\"><saml:Conditions NotBefore=\"2007-08-30T15:05:47Z\" NotOnOrAfter=\"2007-08-30T15:20:47Z\"><saml:AudienceRestrictionCondition><saml:Audience>https://w4de3esy0069028.gdc-bln01.t-systems.com:8443/relyingparty/</saml:Audience></saml:AudienceRestrictionCondition></saml:Conditions><saml:AttributeStatement><saml:Subject><saml:SubjectConfirmation><saml:ConfirmationMethod>urn:oasis:names:tc:SAML:1.0:cm:bearer</saml:ConfirmationMethod></saml:SubjectConfirmation></saml:Subject><saml:Attribute AttributeName=\"givenname\" AttributeNamespace=\"http://schemas.xmlsoap.org/ws/2005/05/identity/claims/givenname\"><saml:AttributeValue>Axel</saml:AttributeValue></saml:Attribute><saml:Attribute AttributeName=\"surname\" AttributeNamespace=\"http://schemas.xmlsoap.org/ws/2005/05/identity/claims/surname\"><saml:AttributeValue>Nennker</saml:AttributeValue></saml:Attribute><saml:Attribute AttributeName=\"emailaddress\" AttributeNamespace=\"http://schemas.xmlsoap.org/ws/2005/05/identity/claims/emailaddress\"><saml:AttributeValue>axel@nennker.de</saml:AttributeValue></saml:Attribute><saml:Attribute AttributeName=\"privatepersonalidentifier\" AttributeNamespace=\"http://schemas.xmlsoap.org/ws/2005/05/identity/claims/privatepersonalidentifier\"><saml:AttributeValue>bXRwZTJPZUhldWJKU1lydDMxWThodnB1cFpCRmd6MDVlaXViWWo3NzJaTT0=</saml:AttributeValue></saml:Attribute></saml:AttributeStatement></saml:Assertion>";
		{
	        byte[] dataBytes = null;

	        ByteArrayOutputStream stream = new ByteArrayOutputStream();

	        //Canonicalizer outputer = new Canonicalizer(stream, Canonicalizer.CANONICAL_XML);
	        Canonicalizer outputer = new Canonicalizer(stream, Canonicalizer.EXCLUSIVE_XML_CANONICALIZATION);
	        try {
	            outputer.write(assertionElement);
	        } catch (IOException e) {
	        	assertEquals("", e.getMessage());
	        }
	        dataBytes = stream.toByteArray();
	        assertEquals(canonicalAssertion, new String(dataBytes));
		}

		Element referenceElement = null;
        try {
    		String referenceStr = "<dsig:Reference xmlns:dsig=\"http://www.w3.org/2000/09/xmldsig#\" URI=\"#uuid-7B20C5C0-9B85-35D1-590A-D1B3093451CF\"><dsig:Transforms><dsig:Transform Algorithm=\"http://www.w3.org/2000/09/xmldsig#enveloped-signature\" /><dsig:Transform Algorithm=\"http://www.w3.org/2001/10/xml-exc-c14n#\" /></dsig:Transforms><dsig:DigestMethod Algorithm=\"http://www.w3.org/2000/09/xmldsig#sha1\" /><dsig:DigestValue>P834/zjB6jZbz80UPkCJQ+IGoqk=</dsig:DigestValue></dsig:Reference>";
        	Document doc = org.xmldap.xml.XmlUtils.parse(referenceStr);
        	referenceElement = doc.getRootElement();

    		Reference ref = new Reference(assertionElement, "uuid-7B20C5C0-9B85-35D1-590A-D1B3093451CF");
    		try {
				assertEquals(referenceStr, ref.toXML());
			} catch (SerializationException e) {
				assertEquals("", e.getMessage());
			}

			ParsedReference parsedRef = new ParsedReference(referenceElement);
			assertEquals("P834/zjB6jZbz80UPkCJQ+IGoqk=", parsedRef.getDigestValue());
			
        } catch (IOException e) {
        	assertEquals("", e.getMessage());
		} catch (ParsingException e) {
			assertEquals("", e.getMessage());
		}

	}
}
