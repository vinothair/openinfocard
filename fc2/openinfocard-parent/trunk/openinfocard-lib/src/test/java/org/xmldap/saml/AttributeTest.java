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
