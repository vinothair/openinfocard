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

public class SAMLAuthnRequest extends XMLElement {

	public SAMLAuthnRequest(Document document) {

		super(document, SAMLConstants.PREFIX_SAML_PROTOCOL, SAMLConstants.NS_SAML_PROTOCOL, "AuthnRequest");

		this.setVersion("2.0");
		this.setID(createID());
		this.setIssueInstant(new Date());
	}

	public SAMLAuthnRequest() {

		this((Document) null);
	}

	public SAMLAuthnRequest(InputStream stream) throws SAXException, IOException {

		super(stream);
	}

	public SAMLAuthnRequest(Reader reader) throws SAXException, IOException {

		super(reader);
	}

	public SAMLAuthnRequest(Document document, Element element) {

		super(document, element);
	}

	public String getDestination() {

		return(this.element.getAttribute("Destination"));
	}

	public void setDestination(String value) {

		this.element.setAttribute("Destination", value);
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

	public String getProtocolBinding() {

		return(this.element.getAttribute("ProtocolBinding"));
	}

	public void setProtocolBinding(String value) {

		this.element.setAttribute("ProtocolBinding", value);
	}

	public String getProviderName() {

		return(this.element.getAttribute("ProviderName"));
	}

	public void setProviderName(String value) {

		this.element.setAttribute("ProviderName", value);
	}

	public String getAssertionConsumerServiceURL() {

		return(this.element.getAttribute("AssertionConsumerServiceURL"));
	}

	public void setAssertionConsumerServiceURL(String value) {

		this.element.setAttribute("AssertionConsumerServiceURL", value);
	}

	private Element getIssuerElement() {

		Element element = (Element) this.element.getElementsByTagNameNS(SAMLConstants.NS_SAML_ASSERTION, "Issuer").item(0);
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

	private Element getNameIDPolicyElement() {

		Element element = (Element) this.element.getElementsByTagNameNS(SAMLConstants.NS_SAML_ASSERTION, "NameIDPolicy").item(0);
		if (element == null) element = (Element) this.element.getElementsByTagName("NameIDPolicy").item(0);

		return(element);
	}

	public Boolean getNameIDPolicyAllowCreate() {

		Element elementNameIDPolicy = this.getNameIDPolicyElement();
		if (elementNameIDPolicy == null) return(null); 

		Attr attrAllowCreate = elementNameIDPolicy.getAttributeNode("AllowCreate");
		if (attrAllowCreate == null) return(null);

		return(new Boolean(attrAllowCreate.getValue()));
	}

	public void setNameIDPolicyAllowCreate(boolean value) {

		Element elementNameIDPolicy = this.getNameIDPolicyElement();
		if (elementNameIDPolicy == null) {

			elementNameIDPolicy = this.document.createElementNS(SAMLConstants.NS_SAML_ASSERTION, "NameIDPolicy");
			elementNameIDPolicy.setPrefix(SAMLConstants.PREFIX_SAML_ASSERTION);
			this.element.appendChild(elementNameIDPolicy);
		}

		elementNameIDPolicy.setAttribute("AllowCreate", Boolean.toString(value));
	}

	public String getNameIDPolicyFormat() {

		Element elementNameIDPolicy = this.getNameIDPolicyElement();
		if (elementNameIDPolicy == null) return(null); 

		Attr attrFormat = elementNameIDPolicy.getAttributeNode("Format");
		if (attrFormat == null) return(null);

		return(attrFormat.getValue());
	}

	public void setNameIDPolicyFormat(String value) {

		Element elementNameIDPolicy = this.getNameIDPolicyElement();
		if (elementNameIDPolicy == null) {

			elementNameIDPolicy = this.document.createElementNS(SAMLConstants.NS_SAML_ASSERTION, "NameIDPolicy");
			elementNameIDPolicy.setPrefix(SAMLConstants.PREFIX_SAML_ASSERTION);
			this.element.appendChild(elementNameIDPolicy);
		}

		elementNameIDPolicy.setAttribute("Format", value);
	}
}
