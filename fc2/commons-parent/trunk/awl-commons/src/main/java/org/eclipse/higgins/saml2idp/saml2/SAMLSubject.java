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

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

public class SAMLSubject extends XMLElement {

	public SAMLSubject(Document document) {

		super(document, SAMLConstants.PREFIX_SAML_ASSERTION, SAMLConstants.NS_SAML_ASSERTION, "Subject");
	}

	public SAMLSubject() {

		this((Document) null);
	}

	public SAMLSubject(InputStream stream) throws SAXException, IOException {

		super(stream);
	}

	public SAMLSubject(Reader reader) throws SAXException, IOException {

		super(reader);
	}

	public SAMLSubject(Document document, Element element) {

		super(document, element);
	}

	private Element getNameIDElement() {

		Element element = (Element) this.element.getElementsByTagNameNS(SAMLConstants.NS_SAML_ASSERTION, "NameID").item(0);
		if (element == null) element = (Element) this.element.getElementsByTagName("NameID").item(0);
		
		return(element);
	}

	public String getNameIDFormat() {

		Element elementNameID = this.getNameIDElement();
		if (elementNameID == null) return(null); 
		
		Attr attrFormat = elementNameID.getAttributeNode("Format");
		if (attrFormat == null) return(null);
		
		return(attrFormat.getValue());
	}

	public void setNameIDFormat(String value) {

		Element elementNameID = this.getNameIDElement();
		if (elementNameID == null) {

			elementNameID = this.document.createElementNS(SAMLConstants.NS_SAML_ASSERTION, "NameID");
			elementNameID.setPrefix(SAMLConstants.PREFIX_SAML_ASSERTION);
			this.element.appendChild(elementNameID);
		}

		elementNameID.setAttribute("Format", value);
	}

	public String getNameID() {

		Element elementNameID = this.getNameIDElement();
		if (elementNameID == null) return(null); 
		
		return(getTextContent(elementNameID));
	}

	public void setNameID(String value) {

		Element elementNameID = this.getNameIDElement();
		if (elementNameID == null) {

			elementNameID = this.document.createElementNS(SAMLConstants.NS_SAML_ASSERTION, "NameID");
			elementNameID.setPrefix(SAMLConstants.PREFIX_SAML_ASSERTION);
			this.element.appendChild(elementNameID);
		}

		setTextContent(elementNameID, value);
	}

	private Element getSubjectConfirmationElement() {

		Element element = (Element) this.element.getElementsByTagNameNS(SAMLConstants.NS_SAML_ASSERTION, "SubjectConfirmation").item(0);
		if (element == null) element = (Element) this.element.getElementsByTagName("SubjectConfirmation").item(0);
		
		return(element);
	}

	public String getSubjectConfirmationMethod() {

		Element elementSubjectConfirmation = this.getSubjectConfirmationElement();
		if (elementSubjectConfirmation == null) return(null); 
		
		Attr attrFormat = elementSubjectConfirmation.getAttributeNode("Method");
		if (attrFormat == null) return(null);
		
		return(attrFormat.getValue());
	}

	public void setSubjectConfirmationMethod(String value) {

		Element elementSubjectConfirmation = this.getSubjectConfirmationElement();
		if (elementSubjectConfirmation == null) {

			elementSubjectConfirmation = this.document.createElementNS(SAMLConstants.NS_SAML_ASSERTION, "SubjectConfirmation");
			elementSubjectConfirmation.setPrefix(SAMLConstants.PREFIX_SAML_ASSERTION);
			this.element.appendChild(elementSubjectConfirmation);
		}

		elementSubjectConfirmation.setAttribute("Method", value);
	}
}
