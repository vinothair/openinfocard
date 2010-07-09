/*******************************************************************************
 * Copyright (c) Google
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Markus Sabadello - Initial API and implementation
 *******************************************************************************/
package org.eclipse.higgins.saml2idp.saml2;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.text.ParseException;
import java.util.Date;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

public class SAMLConditions extends XMLElement {

	public SAMLConditions(Document document) {

		super(document, SAMLConstants.PREFIX_SAML_ASSERTION, SAMLConstants.NS_SAML_ASSERTION, "Conditions");
	}

	public SAMLConditions() {

		this((Document) null);
	}

	public SAMLConditions(InputStream stream) throws SAXException, IOException {

		super(stream);
	}

	public SAMLConditions(Reader reader) throws SAXException, IOException {

		super(reader);
	}

	public SAMLConditions(Document document, Element element) {

		super(document, element);
	}
	
	public Date getNotBefore() {

		try {
			
			return(fromXMLDate(this.element.getAttribute("NotBefore")));
		} catch (ParseException ex) {
			
			return(null);
		}
	}

	public void setNotBefore(Date value) {

		this.element.setAttribute("NotBefore", toXMLDate(value));
	}
	
	public Date getNotOnOrAfter() {

		try {

			return(fromXMLDate(this.element.getAttribute("NotOnOrAfter")));
		} catch (ParseException ex) {
			
			return(null);
		}
	}

	public void setNotOnOrAfter(Date value) {

		this.element.setAttribute("NotOnOrAfter", toXMLDate(value));
	}
}
