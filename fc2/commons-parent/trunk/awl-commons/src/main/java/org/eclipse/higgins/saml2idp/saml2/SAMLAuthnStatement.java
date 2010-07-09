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

public class SAMLAuthnStatement extends XMLElement {

	public SAMLAuthnStatement(Document document) {

		super(document, SAMLConstants.PREFIX_SAML_ASSERTION, SAMLConstants.NS_SAML_ASSERTION, "AuthnStatement");

		this.setAuthnInstant(new Date());
	}

	public SAMLAuthnStatement() {

		this((Document) null);
	}

	public SAMLAuthnStatement(InputStream stream) throws SAXException, IOException {

		super(stream);
	}

	public SAMLAuthnStatement(Reader reader) throws SAXException, IOException {

		super(reader);
	}

	public SAMLAuthnStatement(Document document, Element element) {

		super(document, element);
	}

	public Date getAuthnInstant() {

		try {

			return(fromXMLDate(this.element.getAttribute("AuthnInstant")));
		} catch (ParseException ex) {

			return(null);
		}
	}

	public void setAuthnInstant(Date value) {

		this.element.setAttribute("AuthnInstant", toXMLDate(value));
	}

	private Element getAuthnContextElement() {

		Element element = (Element) this.element.getElementsByTagNameNS(SAMLConstants.NS_SAML_ASSERTION, "AuthnContext").item(0);
		if (element == null) element = (Element) this.element.getElementsByTagName("AuthnContext").item(0);

		return(element);
	}

	private Element getAuthnContextClassRefElement(Element elementAuthnContext) {

		Element element = (Element) this.element.getElementsByTagNameNS(SAMLConstants.NS_SAML_PROTOCOL, "AuthnContextClassRef").item(0);
		if (element == null) element = (Element) this.element.getElementsByTagName("AuthnContextClassRef").item(0);

		return(element);
	}

	public String getAuthnContextClassRef() {

		Element elementAuthnContext = this.getAuthnContextElement();
		if (elementAuthnContext == null) return(null);

		Element elementAuthnContextClassRef = this.getAuthnContextClassRefElement(elementAuthnContext);
		if (elementAuthnContextClassRef == null) return(null); 

		return(getTextContent(elementAuthnContextClassRef));
	}

	public void setAuthnContextClassRef(String value) {

		Element elementAuthnContext = this.getAuthnContextElement();
		if (elementAuthnContext == null) {

			elementAuthnContext = this.document.createElementNS(SAMLConstants.NS_SAML_ASSERTION, "AuthnContext");
			elementAuthnContext.setPrefix(SAMLConstants.PREFIX_SAML_ASSERTION);
			this.element.appendChild(elementAuthnContext);
		}

		Element elementAuthnContextClassRef = this.getAuthnContextClassRefElement(elementAuthnContext);
		if (elementAuthnContextClassRef == null) {

			elementAuthnContextClassRef = this.document.createElementNS(SAMLConstants.NS_SAML_ASSERTION, "AuthnContextClassRef");
			elementAuthnContextClassRef.setPrefix(SAMLConstants.PREFIX_SAML_ASSERTION);
			elementAuthnContext.appendChild(elementAuthnContextClassRef);
		}

		setTextContent(elementAuthnContextClassRef, value);
	}
}
