package org.xmldap.xmldsig;

import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPrivateKey;

import nu.xom.Attribute;
import nu.xom.Element;

import org.xmldap.ws.WSConstants;

import junit.framework.TestCase;

public class BaseEnvelopedSignatureTest extends TestCase {

	protected void setUp() throws Exception {
		super.setUp();
	}

	public void testToXMLWithElement() throws Exception {

		Element body = new Element("xmldap:Body", "http://www.xmldap.org");
		body.addNamespaceDeclaration(WSConstants.WSU_PREFIX,
				WSConstants.WSSE_OASIS_10_WSU_NAMESPACE);
		Attribute idAttr = new Attribute(WSConstants.WSU_PREFIX + ":Id",
				WSConstants.WSSE_OASIS_10_WSU_NAMESPACE,
				"urn:guid:58824342-832F-C99B-925B-CB0E858E5D65");
		body.addAttribute(idAttr);
		Element child = new Element("xmldap:Child", "http://www.xmldap.org");
		body.appendChild(child);

		X509Certificate signingCert = org.xmldap.util.XmldapCertsAndKeys
				.getXmldapCert();
		RSAPrivateKey signingKey = org.xmldap.util.XmldapCertsAndKeys
				.getXmldapPrivateKey();

		KeyInfo keyInfo = new AsymmetricKeyInfo(signingCert);
		BaseEnvelopedSignature signer = new BaseEnvelopedSignature(keyInfo,
				signingKey);
		Element signedXML = signer.sign(body);
		// System.out.println(signedXML.toXML());
		assertTrue(EnvelopedSignature.validate(signedXML.toXML()));

	}

	public void testDigestElement() throws Exception {

		Element body = new Element("xmldap:Body", "http://www.xmldap.org");
		body.addNamespaceDeclaration(WSConstants.WSU_PREFIX,
				WSConstants.WSSE_OASIS_10_WSU_NAMESPACE);
		Attribute idAttr = new Attribute(WSConstants.WSU_PREFIX + ":Id",
				WSConstants.WSSE_OASIS_10_WSU_NAMESPACE,
				"urn:guid:58824342-832F-C99B-925B-CB0E858E5D65");
		body.addAttribute(idAttr);
		Element child = new Element("xmldap:Child", "http://www.xmldap.org");
		body.appendChild(child);

		X509Certificate signingCert = org.xmldap.util.XmldapCertsAndKeys
				.getXmldapCert();
		RSAPrivateKey signingKey = org.xmldap.util.XmldapCertsAndKeys
				.getXmldapPrivateKey();

		KeyInfo keyInfo = new AsymmetricKeyInfo(signingCert);
		BaseEnvelopedSignature signer = new BaseEnvelopedSignature(keyInfo,
				signingKey);
		Element signedXML = signer.sign(body);

		String digest = BaseEnvelopedSignature.digestElement(signedXML);
		// System.out.println(signedXML.toXML());
		assertEquals("8xRvuAvdu90P9qcwm3kaUNSh4/c=", digest);

	}

	public void testGetAssertionCanonicalBytes() throws Exception {

		Element body = new Element("xmldap:Body", "http://www.xmldap.org");
		body.addNamespaceDeclaration(WSConstants.WSU_PREFIX,
				WSConstants.WSSE_OASIS_10_WSU_NAMESPACE);
		Attribute idAttr = new Attribute(WSConstants.WSU_PREFIX + ":Id",
				WSConstants.WSSE_OASIS_10_WSU_NAMESPACE,
				"urn:guid:58824342-832F-C99B-925B-CB0E858E5D65");
		body.addAttribute(idAttr);
		Element child = new Element("xmldap:Child", "http://www.xmldap.org");
		body.appendChild(child);

		X509Certificate signingCert = org.xmldap.util.XmldapCertsAndKeys
				.getXmldapCert();
		RSAPrivateKey signingKey = org.xmldap.util.XmldapCertsAndKeys
				.getXmldapPrivateKey();

		KeyInfo keyInfo = new AsymmetricKeyInfo(signingCert);
		BaseEnvelopedSignature signer = new BaseEnvelopedSignature(keyInfo,
				signingKey);
		Element signedXML = signer.sign(body);

		byte[] digest = BaseEnvelopedSignature.getAssertionCanonicalBytes(signedXML);
		// System.out.println(signedXML.toXML());
		String expected = "<xmldap:Body " +
		"xmlns:wsu=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd\" " +
		"xmlns:xmldap=\"http://www.xmldap.org\" " +
		"wsu:Id=\"urn:guid:58824342-832F-C99B-925B-CB0E858E5D65\">" +
		"<xmldap:Child></xmldap:Child></xmldap:Body>";
		assertEquals(expected, new String(digest));

	}

	public void testGetSignedInfoCanonicalBytes() throws Exception {

		Element body = new Element("xmldap:Body", "http://www.xmldap.org");
		body.addNamespaceDeclaration(WSConstants.WSU_PREFIX,
				WSConstants.WSSE_OASIS_10_WSU_NAMESPACE);
		Attribute idAttr = new Attribute(WSConstants.WSU_PREFIX + ":Id",
				WSConstants.WSSE_OASIS_10_WSU_NAMESPACE,
				"urn:guid:58824342-832F-C99B-925B-CB0E858E5D65");
		body.addAttribute(idAttr);
		Element child = new Element("xmldap:Child", "http://www.xmldap.org");
		body.appendChild(child);

		X509Certificate signingCert = org.xmldap.util.XmldapCertsAndKeys
				.getXmldapCert();
		RSAPrivateKey signingKey = org.xmldap.util.XmldapCertsAndKeys
				.getXmldapPrivateKey();

		KeyInfo keyInfo = new AsymmetricKeyInfo(signingCert);
		BaseEnvelopedSignature signer = new BaseEnvelopedSignature(keyInfo,
				signingKey);
		Element signedXML = signer.sign(body);

		byte[] digest = BaseEnvelopedSignature.getSignedInfoCanonicalBytes(signedXML);
		// System.out.println(signedXML.toXML());
		String expected = "<dsig:SignedInfo " +
		"xmlns:dsig=\"http://www.w3.org/2000/09/xmldsig#\">" +
		"<dsig:CanonicalizationMethod Algorithm=\"http://www.w3.org/2001/10/xml-exc-c14n#\">" +
		"</dsig:CanonicalizationMethod>" +
		"<dsig:SignatureMethod Algorithm=\"http://www.w3.org/2000/09/xmldsig#rsa-sha1\">" +
		"</dsig:SignatureMethod><dsig:Reference URI=\"#urn:guid:58824342-832F-C99B-925B-CB0E858E5D65\">" +
		"<dsig:Transforms>" +
		"<dsig:Transform Algorithm=\"http://www.w3.org/2000/09/xmldsig#enveloped-signature\">" +
		"</dsig:Transform><dsig:Transform Algorithm=\"http://www.w3.org/2001/10/xml-exc-c14n#\">" +
		"</dsig:Transform></dsig:Transforms>" +
		"<dsig:DigestMethod Algorithm=\"http://www.w3.org/2000/09/xmldsig#sha1\"></dsig:DigestMethod>" +
		"<dsig:DigestValue>8xRvuAvdu90P9qcwm3kaUNSh4/c=</dsig:DigestValue>" +
		"</dsig:Reference></dsig:SignedInfo>";
		assertEquals(expected, new String(digest));

	}

}
