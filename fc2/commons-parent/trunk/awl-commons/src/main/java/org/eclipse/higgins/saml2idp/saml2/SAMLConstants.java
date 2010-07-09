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

public class SAMLConstants {

	public static final String PREFIX_SAML_PROTOCOL = "samlp";
	public static final String PREFIX_SAML_ASSERTION = null;
	public static final String PREFIX_SAML_XENC = "xenc";
	
	public static final String NS_SAML_PROTOCOL = "urn:oasis:names:tc:SAML:2.0:protocol";
	public static final String NS_SAML_ASSERTION = "urn:oasis:names:tc:SAML:2.0:assertion";
	public static final String NS_SAML_XENC = "http://www.w3.org/2001/04/xmlenc#";

	public static final String STATUSCCODE_SUCCESS = "urn:oasis:names:tc:SAML:2.0:status:Success";

	public static final String PROTOCOLBINDING_HTTP_REDIRECT = "urn:oasis:names.tc:SAML:2.0:bindings:HTTP-Redirect";
	public static final String PROTOCOLBINDING_HTTP_POST = "urn:oasis:names:tc:SAML:2.0:bindings:HTTP-POST";
	
	public static final String AUTHNCONTEXTCLASSREF_PASSWORD = "urn:oasis:names:tc:SAML:2.0:ac:classes:Password";

	public static final String NAMEIDFORMAT_EMAILADDRESS = "urn:oasis:names:tc:SAML:1.1:nameid-format:emailAddress";
	public static final String NAMEIDFORMAT_ENTITY = "urn:oasis:names:tc:SAML:1.1:nameid-format:entity";

	public static final String NAMEIDPOLICY_UNSPECIFIED = "urn:oasis:names:tc:SAML:1.1:nameid-format:unspecified";
	
	public static final String SUBJECTCONFIRMATIONMETHOD_BEARER = "urn:oasis:names:tc:SAML:2.0:cm:bearer";
	
	private SAMLConstants() {

	}
}
