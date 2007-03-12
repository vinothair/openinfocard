package org.xmldap.saml;

import java.io.IOException;
import java.util.Calendar;

import org.xmldap.exceptions.SerializationException;
import org.xmldap.util.XSDDateTime;

import junit.framework.TestCase;
import nu.xom.ParsingException;
import nu.xom.ValidityException;

public class ConditionsTest extends TestCase {

	String c1E = null;
	Calendar notBeforeCal = null;
	Calendar notOnOrAfterCal = null;
	Calendar when = null;
	Conditions c2 = null;
	String notBeforeString = "2006-09-27T12:58:26Z";
	String notOnOrAfterString = "2006-09-29T12:58:26Z";
	
	public void setUp() throws Exception {
		super.setUp();
		notBeforeCal = XSDDateTime.parse(notBeforeString);
		when = XSDDateTime.parse("2006-09-28T12:58:26Z");
		notOnOrAfterCal = XSDDateTime.parse(notOnOrAfterString);
		Conditions c1 = new Conditions(-10, 10);
		c1E = c1.toXML();
		c2 = new Conditions(notBeforeCal, notOnOrAfterCal);
	}
	
	public void testConditionsElement() {
		Conditions c1;
		try {
			c1 = new Conditions(c1E);
			try {
				c1.toXML();
			} catch (SerializationException e) {
				assertTrue(false);
			}
		} catch (ValidityException e1) {
			assertTrue(false);
		} catch (ParsingException e1) {
			assertTrue(false);
		} catch (IOException e1) {
			assertTrue(false);
		}
	}

	public void testToXML() {
		String xml;
		try {
			xml = c2.toXML();
			assertEquals("<saml:Conditions xmlns:saml=\"urn:oasis:names:tc:SAML:1.0:assertion\" NotBefore=\"2006-09-27T12:58:26Z\" NotOnOrAfter=\"2006-09-29T12:58:26Z\" />", xml);
		} catch (SerializationException e) {
			assertTrue(false);
		}
	}

	public void testValidate() {
		assertFalse(c2.validate(notBeforeCal));
		assertTrue(c2.validate(when));
		assertTrue(c2.validate(notOnOrAfterCal));
	}

	public void testGet() {
		Calendar notBefore = c2.getNotBefore();
		assertEquals(notBeforeString, XSDDateTime.getDateTime(notBefore));
		Calendar notOnOrAfter = c2.getNotOnOrAfter();
		assertEquals(notOnOrAfterString, XSDDateTime.getDateTime(notOnOrAfter));
	}
}
