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

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

public class SAMLAssertion extends XMLElement {

	public SAMLAssertion(Document document) {

		super(document, SAMLConstants.PREFIX_SAML_ASSERTION, SAMLConstants.NS_SAML_ASSERTION, "Assertion");

		this.setVersion("2.0");
		this.setID(createID());
		this.setIssueInstant(new Date());
	}

	public SAMLAssertion() {

		this((Document) null);
	}

	public SAMLAssertion(InputStream stream) throws SAXException, IOException {

		super(stream);
	}

	public SAMLAssertion(Reader reader) throws SAXException, IOException {

		super(reader);
	}

	public SAMLAssertion(Document document, Element element) {

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

	private Element getAuthnStatementElement() {

		Element element = (Element) this.element.getElementsByTagNameNS(SAMLConstants.NS_SAML_ASSERTION, "AuthnStatement").item(0);
		if (element == null) element = (Element) this.element.getElementsByTagName("AuthnStatement").item(0);

		return(element);
	}

	public SAMLAuthnStatement getSAMLAuthnStatement() {

		Element elementAuthnStatement = this.getAuthnStatementElement();
		if (elementAuthnStatement == null) return(null);

		return(new SAMLAuthnStatement(this.document, elementAuthnStatement));
	}

	public void setSAMLAuthnStatement(SAMLAuthnStatement samlAuthnStatement) {

		Element elementAuthnStatement = this.getAuthnStatementElement();

		if (elementAuthnStatement != null) {

			this.element.replaceChild(elementAuthnStatement, samlAuthnStatement.getElement());
		} else {

			this.element.appendChild(samlAuthnStatement.getElement());
		}
	}

	private Element getSubjectElement() {

		Element element = (Element) this.element.getElementsByTagNameNS(SAMLConstants.NS_SAML_ASSERTION, "Subject").item(0);
		if (element == null) element = (Element) this.element.getElementsByTagName("Subject").item(0);

		return(element);
	}

	public SAMLSubject getSubject() {

		Element elementSubject = this.getSubjectElement();
		if (elementSubject == null) return(null);

		return(new SAMLSubject(this.document, elementSubject));
	}

	public void setSAMLSubject(SAMLSubject samlSubject) {

		Element elementSubject = this.getAuthnStatementElement();

		if (elementSubject != null) {

			this.element.replaceChild(elementSubject, samlSubject.getElement());
		} else {

			this.element.appendChild(samlSubject.getElement());
		}
	}

	private Element getConditionsElement() {

		Element element = (Element) this.element.getElementsByTagNameNS(SAMLConstants.NS_SAML_ASSERTION, "Conditions").item(0);
		if (element == null) element = (Element) this.element.getElementsByTagName("Conditions").item(0);

		return(element);
	}

	public SAMLConditions getConditions() {

		Element elementConditions = this.getConditionsElement();
		if (elementConditions == null) return(null);

		return(new SAMLConditions(this.document, elementConditions));
	}

	public void setSAMLConditions(SAMLConditions samlConditions) {

		Element elementConditions = this.getConditionsElement();

		if (elementConditions != null) {

			this.element.replaceChild(elementConditions, samlConditions.getElement());
		} else {

			this.element.appendChild(samlConditions.getElement());
		}
	}
}
