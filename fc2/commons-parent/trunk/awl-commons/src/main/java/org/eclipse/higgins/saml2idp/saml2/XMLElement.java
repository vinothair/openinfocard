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
import java.io.StringWriter;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.PublicKey;
import java.security.interfaces.DSAPrivateKey;
import java.security.interfaces.RSAPrivateKey;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.Random;
import java.util.TimeZone;

import javax.xml.crypto.MarshalException;
import javax.xml.crypto.dsig.CanonicalizationMethod;
import javax.xml.crypto.dsig.DigestMethod;
import javax.xml.crypto.dsig.Reference;
import javax.xml.crypto.dsig.SignatureMethod;
import javax.xml.crypto.dsig.SignedInfo;
import javax.xml.crypto.dsig.Transform;
import javax.xml.crypto.dsig.XMLSignature;
import javax.xml.crypto.dsig.XMLSignatureException;
import javax.xml.crypto.dsig.XMLSignatureFactory;
import javax.xml.crypto.dsig.dom.DOMSignContext;
import javax.xml.crypto.dsig.dom.DOMValidateContext;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import javax.xml.crypto.dsig.keyinfo.KeyInfoFactory;
import javax.xml.crypto.dsig.keyinfo.KeyValue;
import javax.xml.crypto.dsig.spec.C14NMethodParameterSpec;
import javax.xml.crypto.dsig.spec.TransformParameterSpec;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;

import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

@SuppressWarnings("deprecation")
public abstract class XMLElement {

	//private static final Log log = LogFactory.getLog(XMLElement.class);

	public static final String PROVIDER_JSR105 = "org.jcp.xml.dsig.internal.dom.XMLDSigRI";

	private static DocumentBuilderFactory documentBuilderFactory = null;
	private static DocumentBuilder documentBuilder = null;
	private static TransformerFactory transformerFactory = null;
	private static Transformer transformer = null;
	private static XMLSignatureFactory signatureFactory = null;
	private static KeyInfoFactory keyInfoFactory = null;

	private static Random random = null;

	static {

		try {

			documentBuilderFactory = DocumentBuilderFactory.newInstance();
			documentBuilderFactory.setNamespaceAware(true);
			documentBuilderFactory.setValidating(false);
			documentBuilder = documentBuilderFactory.newDocumentBuilder();
			transformerFactory = TransformerFactory.newInstance();
			transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.METHOD, "xml");
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
			transformer.setOutputProperty(OutputKeys.INDENT, "no");
			signatureFactory = XMLSignatureFactory.getInstance(
					"DOM",
					(Provider) Class.forName(System.getProperty("jsr105Provider", PROVIDER_JSR105)).newInstance());
			keyInfoFactory = signatureFactory.getKeyInfoFactory();

			random = new Random();
		} catch (Exception ex) {

			throw new RuntimeException("Cannot initialize XML.", ex);
		}
	}

	protected Document document;
	protected Element element;

	protected XMLElement(Document document, String prefix, String namespaceURI, String localName) {

		if (document == null) {

			this.document = documentBuilder.newDocument();
			this.element = this.document.createElementNS(namespaceURI, localName);
			this.element.setPrefix(prefix);
			this.document.appendChild(this.element);

			addNamespaceAttributes(this.document);
		} else {

			this.document = document;
			this.element = this.document.createElementNS(namespaceURI, localName);
			this.element.setPrefix(prefix);
		}
	}

	protected XMLElement(InputStream stream) throws SAXException, IOException {

		InputSource inputSource = new InputSource(stream);

		this.document = documentBuilder.parse(inputSource);
		this.element = this.document.getDocumentElement();
	}

	protected XMLElement(Reader reader) throws SAXException, IOException {

		InputSource inputSource = new InputSource(reader);

		this.document = documentBuilder.parse(inputSource);
		this.element = this.document.getDocumentElement();
	}

	protected XMLElement(Document document, Element element) {

		this.document = document;
		this.element = element;
	}

	public Document getDocument() {

		return(this.document);
	}

	public Element getElement() {

		return(this.element);
	}

	public String dump () throws IOException {

		StringWriter writer = new StringWriter();
		OutputFormat format = new OutputFormat("XML", "UTF-8", true);
		format.setIndenting(false);
		XMLSerializer serializer = new XMLSerializer(writer, format);
		serializer.asDOMSerializer();
		serializer.serialize(this.document);
		writer.close();

		String str = writer.getBuffer().toString();

		//log.info("DUMP: " + str);
		return(str);
	}

	public String toString() {

		try {
			return(this.dump());
		} catch (IOException ex) {

			return("[SERIALIZATION PROBLEM: " + ex.getMessage());
		}
	}

	private void sign(PrivateKey privateKey, KeyInfo keyInfo) throws NoSuchAlgorithmException, InvalidAlgorithmParameterException, MarshalException, XMLSignatureException, KeyException {

		DigestMethod digestMethod = signatureFactory.newDigestMethod(
				DigestMethod.SHA1, 
				null);

		Transform transform = signatureFactory.newTransform(
				Transform.ENVELOPED, 
				(TransformParameterSpec) null);

		Reference reference = signatureFactory.newReference(
				"", 
				digestMethod,
				Collections.singletonList(
						transform),
						null,
						null);

		CanonicalizationMethod canonicalizationMethod = signatureFactory.newCanonicalizationMethod(
				CanonicalizationMethod.INCLUSIVE_WITH_COMMENTS,
				(C14NMethodParameterSpec) null);

		SignatureMethod signatureMethod;

		if (privateKey instanceof DSAPrivateKey) {

			signatureMethod = signatureFactory.newSignatureMethod(SignatureMethod.DSA_SHA1, 
					null);
		} else if (privateKey instanceof RSAPrivateKey) {

			signatureMethod = signatureFactory.newSignatureMethod(SignatureMethod.RSA_SHA1, 
					null);
		} else {

			throw new IllegalArgumentException("Invalid key type (must be DSA or RSA).");
		}

		SignedInfo signedInfo = signatureFactory.newSignedInfo(
				canonicalizationMethod,
				signatureMethod,
				Collections.singletonList(reference));

		DOMSignContext signContext = new DOMSignContext(privateKey, this.element);
		signContext.setNextSibling(this.element.getFirstChild());

		XMLSignature signature = signatureFactory.newXMLSignature(signedInfo, keyInfo);

		signature.sign(signContext);
	}

	public void sign(PrivateKey privateKey, PublicKey publicKey) throws KeyException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, MarshalException, XMLSignatureException {

		KeyValue keyValue = keyInfoFactory.newKeyValue(publicKey);

		KeyInfo keyInfo = keyInfoFactory.newKeyInfo(Collections.singletonList(keyValue));

		this.sign(privateKey, keyInfo);
	}

	public void sign(PrivateKey privateKey) throws NoSuchAlgorithmException, InvalidAlgorithmParameterException, MarshalException, XMLSignatureException, KeyException {

		this.sign(privateKey, (KeyInfo) null);
	}

	@SuppressWarnings("unchecked")
	public void verify(PublicKey publicKey) throws MarshalException, XMLSignatureException, VerificationException {

		// Obtain signature from XML document.

		Element signatureElement = (Element) this.document.getElementsByTagNameNS(XMLSignature.XMLNS, "Signature").item(0);
		if (signatureElement == null) throw new IllegalStateException("No XML Signature found to verify.");

		DOMValidateContext validateContext = new DOMValidateContext(publicKey, signatureElement);

		XMLSignature signature = signatureFactory.unmarshalXMLSignature(validateContext);

		// Verify signature.

		if (! signature.getSignatureValue().validate(validateContext)) 
			throw new VerificationException("Invalid signature value.");

		for (Iterator i = signature.getSignedInfo().getReferences().iterator(); i.hasNext(); ) {

			Reference reference = (Reference) i.next();

			if (! reference.validate(validateContext))
				throw new VerificationException("Invalid reference: URI=" + reference.getURI() + ", Type=" + reference.getType() + ", ID=" + reference.getId());
		}

		if (! signature.validate(validateContext)) 
			throw new VerificationException("Invalid signature.");
	}

	public class VerificationException extends Exception {

		private static final long serialVersionUID = 1L;

		public VerificationException(String message) {

			super(message);
		}
	}

	public static String getTextContent(Element element) {

		StringBuffer buffer = new StringBuffer();
		NodeList nodeList = element.getChildNodes();

		for (int i=0; i<nodeList.getLength(); i++) {

			Node node = nodeList.item(i);
			if (node.getNodeType() == Node.TEXT_NODE) buffer.append(node.getNodeValue());
		}

		return(buffer.toString());
	}

	public static void setTextContent(Element element, String value) {

		if (element.hasChildNodes()) {

			NodeList nodeList = element.getChildNodes();
			for (int i=0; i<nodeList.getLength(); i++) {

				if (nodeList.item(i).getNodeType() == Node.TEXT_NODE) element.removeChild(nodeList.item(i));
			}
		}

		Text text = element.getOwnerDocument().createTextNode(value);
		element.appendChild(text);
	}

	public static String createID() {

		final char[] hex = { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p' };
		byte[] bytes = new byte[20];
		random.nextBytes(bytes);

		char[] chars = new char[40];

		int pos = 0;
		for (int i = 0; i < bytes.length; i++) {

			int hi = (bytes[i] >> 4) & 0x0f;
			int lo = bytes[i] & 0x0f;

			chars[pos++] = hex[hi];
			chars[pos++] = hex[lo];
		}

		return String.valueOf(chars);
	}

	public static Date fromXMLDate(String date) throws ParseException {

		int dot = date.indexOf('.');

		SimpleDateFormat format = (dot > 0) 
		? new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
		: new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

		format.setTimeZone(TimeZone.getTimeZone("GMT"));

		return(format.parse(date));
	}

	public static String toXMLDate(Date date) {

		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
		format.setTimeZone(TimeZone.getTimeZone("GMT"));

		return(format.format(date));
	}

	public static void addNamespaceAttributes(Document document) {

		if (SAMLConstants.PREFIX_SAML_PROTOCOL != null) {

			document.getDocumentElement().setAttribute("xmlns:" + SAMLConstants.PREFIX_SAML_PROTOCOL, SAMLConstants.NS_SAML_PROTOCOL);
		} else {

			document.getDocumentElement().setAttribute("xmlns", SAMLConstants.NS_SAML_PROTOCOL);
		}

		if (SAMLConstants.PREFIX_SAML_ASSERTION != null) {

			document.getDocumentElement().setAttribute("xmlns:" + SAMLConstants.PREFIX_SAML_ASSERTION, SAMLConstants.NS_SAML_ASSERTION);
		} else {

			document.getDocumentElement().setAttribute("xmlns", SAMLConstants.NS_SAML_ASSERTION);
		}

		if (SAMLConstants.PREFIX_SAML_XENC != null) {

			document.getDocumentElement().setAttribute("xmlns:" + SAMLConstants.PREFIX_SAML_XENC, SAMLConstants.NS_SAML_XENC);
		} else {

			document.getDocumentElement().setAttribute("xmlns", SAMLConstants.NS_SAML_XENC);
		}
	}
}
