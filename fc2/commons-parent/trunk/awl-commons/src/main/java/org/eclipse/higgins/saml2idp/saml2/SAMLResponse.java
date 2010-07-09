/*******************************************************************************
 * Copyright (c) 2007 Google
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

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

public class SAMLResponse extends XMLElement {

	public SAMLResponse(Document document) {

		super(document, SAMLConstants.PREFIX_SAML_PROTOCOL, SAMLConstants.NS_SAML_PROTOCOL, "Response");

		this.setVersion("2.0");
		this.setID(createID());
		this.setIssueInstant(new Date());
	}

	public SAMLResponse() {

		this((Document) null);
	}

	public SAMLResponse(InputStream stream) throws SAXException, IOException {

		super(stream);
	}

	public SAMLResponse(Reader reader) throws SAXException, IOException {

		super(reader);
	}

	public SAMLResponse(Document document, Element element) {

		super(document, element);
	}

	public String getVersion() {

		return(this.element.getAttribute("Version"));
	}

	public void setVersion(String value) {

		this.element.setAttribute("Version", value);
	}

	public Date getIssueInstant() {

		try {

			return(fromXMLDate(this.element.getAttribute("IssueInstant")));
		} catch (ParseException ex) {
			
			return(null);
		}
	}

	public void setIssueInstant(Date value) {

		this.element.setAttribute("IssueInstant", toXMLDate(value));
	}

	public String getID() {

		return(this.element.getAttribute("ID"));
	}

	public void setID(String value) {

		this.element.setAttribute("ID", value);
	}

	public String getDestination() {

		return(this.element.getAttribute("Destination"));
	}

	public void setDestination(String value) {

		this.element.setAttribute("Destination", value);
	}

	private Element getIssuerElement() {

		Element element = (Element) this.element.getElementsByTagNameNS(SAMLConstants.NS_SAML_PROTOCOL, "Issuer").item(0);
		if (element == null) element = (Element) this.element.getElementsByTagName("Issuer").item(0);

		return(element);
	}

	public String getIssuer() {

		Element elementIssuer = this.getIssuerElement();
		if (elementIssuer == null) return(null); 

		return(getTextContent(elementIssuer));
	}

	public void setIssuer(String value) {

		Element elementIssuer = this.getIssuerElement();
		if (elementIssuer == null) {

			elementIssuer = this.document.createElementNS(SAMLConstants.NS_SAML_ASSERTION, "Issuer");
			elementIssuer.setPrefix(SAMLConstants.PREFIX_SAML_ASSERTION);
			this.element.appendChild(elementIssuer);
		}

		setTextContent(elementIssuer, value);
	}

	private Element getStatusElement() {

		Element element = (Element) this.element.getElementsByTagNameNS(SAMLConstants.NS_SAML_PROTOCOL, "Status").item(0);
		if (element == null) element = (Element) this.element.getElementsByTagName("Status").item(0);

		return(element);
	}

	private Element getStatusCodeElement(Element elementStatus) {

		Element element = (Element) this.element.getElementsByTagNameNS(SAMLConstants.NS_SAML_PROTOCOL, "StatusCode").item(0);
		if (element == null) element = (Element) this.element.getElementsByTagName("StatusCode").item(0);

		return(element);
	}

	public String getStatusCodeValue() {

		Element elementStatus = this.getStatusElement();
		if (elementStatus == null) return(null);

		Element elementStatusCode = this.getStatusCodeElement(elementStatus);
		if (elementStatusCode == null) return(null); 

		Attr attrValue = elementStatusCode.getAttributeNode("Value");
		if (attrValue == null) return(null);

		return(attrValue.getValue());
	}

	public void setStatusCodeValue(String value) {

		Element elementStatus = this.getStatusElement();
		if (elementStatus == null) {

			elementStatus = this.document.createElementNS(SAMLConstants.NS_SAML_PROTOCOL, "Status");
			elementStatus.setPrefix(SAMLConstants.PREFIX_SAML_PROTOCOL);
			this.element.appendChild(elementStatus);
		}

		Element elementStatusCode = this.getStatusCodeElement(elementStatus);
		if (elementStatusCode == null) {

			elementStatusCode = this.document.createElementNS(SAMLConstants.NS_SAML_PROTOCOL, "StatusCode");
			elementStatusCode.setPrefix(SAMLConstants.PREFIX_SAML_PROTOCOL);
			elementStatus.appendChild(elementStatusCode);
		}

		elementStatusCode.setAttribute("Value", value);
	}

	private Element getAssertionElement() {

		Element element = (Element) this.element.getElementsByTagNameNS(SAMLConstants.NS_SAML_ASSERTION, "Assertion").item(0);
		if (element == null) element = (Element) this.element.getElementsByTagName("Assertion").item(0);

		return(element);
	}

	public SAMLAssertion getSAMLAssertion() {

		Element elementAssertion = this.getAssertionElement();
		if (elementAssertion == null) return(null);

		return(new SAMLAssertion(this.document, elementAssertion));
	}

	public void setSAMLAssertion(SAMLAssertion samlAssertion) {

		Element elementAssertion = this.getAssertionElement();

		if (elementAssertion != null) {

			this.element.replaceChild(elementAssertion, samlAssertion.getElement());
		} else {

			this.element.appendChild(samlAssertion.getElement());
		}
	}
}
