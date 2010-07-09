/*
 * Copyright (c) 2006, Axel Nennker - http://axel.nennker.de/
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * The names of the contributors may NOT be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE REGENTS AND CONTRIBUTORS ``AS IS'' AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE REGENTS AND CONTRIBUTORS BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * 
 */
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
