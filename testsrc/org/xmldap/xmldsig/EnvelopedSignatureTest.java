package org.xmldap.xmldsig;

import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPrivateKey;

import org.xmldap.ws.WSConstants;

import nu.xom.Attribute;
import nu.xom.Document;
import nu.xom.Element;

import junit.framework.TestCase;

/**
 * SignatureUtil Tester.
 * 
 * @author <Authors name>
 * @since
 * 
 * <pre>
 * 03 / 21 / 2006
 * </pre>
 * 
 * @version 1.0
 */
public class EnvelopedSignatureTest extends TestCase {

	private static final String XML = "<xmldap:Body xmlns:wsu=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd\" xmlns:xmldap=\"http://www.xmldap.org\" wsu:Id=\"urn:guid:58824342-832F-C99B-925B-CB0E858E5D65\"><xmldap:Child /></xmldap:Body>";

	// TODO - refactor to support non-wrapped B64
	private static final String SIGNED_XML_DOC = 
		"<?xml version=\"1.0\"?>\n" +
		"<xmldap:Body " +
		"xmlns:wsu=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd\" " +
		"xmlns:xmldap=\"http://www.xmldap.org\" " +
		"wsu:Id=\"urn:guid:58824342-832F-C99B-925B-CB0E858E5D65\">" +
		"<xmldap:Child /><ds:Signature xmlns:ds=\"http://www.w3.org/2000/09/xmldsig#\">" +
		"<ds:SignedInfo><ds:CanonicalizationMethod Algorithm=\"http://www.w3.org/2001/10/xml-exc-c14n#\" />" +
		"<ds:SignatureMethod Algorithm=\"http://www.w3.org/2000/09/xmldsig#rsa-sha1\" />" +
		"<ds:Reference URI=\"\"><ds:Transforms><ds:Transform " +
		"Algorithm=\"http://www.w3.org/2000/09/xmldsig#enveloped-signature\" />" +
		"<ds:Transform Algorithm=\"http://www.w3.org/2001/10/xml-exc-c14n#\" /></ds:Transforms>" +
		"<ds:DigestMethod Algorithm=\"http://www.w3.org/2000/09/xmldsig#sha1\" /><ds:DigestValue>" +
		"8xRvuAvdu90P9qcwm3kaUNSh4/c=</ds:DigestValue></ds:Reference></ds:SignedInfo><ds:SignatureValue>" +
		"udWT9QYo6X/Z0YVIN/deUJxGU3CYiDV6OLYxSo/8xjqEKXYzhpwaGEZMytWRBLM/clcd1PViJCdQZRbHnLzBeXk193c5c06" +
		"FJC56sruoWg8WoqkABd0ZwY4TxxKxQx2+EHqIJMLV06zPOqkBbR9/l14TPIv1E7aRR5S+hjycvCTvZ5rkG4HWHo0czdvE0b" +
		"4T10LN6+aokr5SYDRrouQcU9gDpAOJ9r2l7xTc39wNk/d4iGJLqbj0X+9S6GqldDMrBFXnTe8tdyoLjCdZpgwMa9Ml0Cx6H" +
		"BF8CBL8U9R0vlfMQuyd59ZRHPSlyClf9cYzz7eE3srrMQATImkDS1I0LA==</ds:SignatureValue><ds:KeyInfo>" +
		"<ds:KeyName>Public Key for CN=xmldap.org, OU=infocard, O=xmldap, L=San Francisco, ST=California, " +
		"C=US</ds:KeyName><ds:KeyValue><ds:RSAKeyValue><ds:Modulus>ANMnkVA4xfpG0bLos9FOpNBjHAdFahy2cJ7FU" +
		"wuXd/IShnG+5qF/z1SdPWzRxTtpFFyodtXlBUEIbiT+IbYPZF1vCcBrcFa8Kz/4rBjrpPZgllgA/WSVKjnJvw8q4/tO6CQZ" +
		"SlRlj/ebNK9VyT1kN+MrKV1SGTqaIJ2l+7Rd05WHscwZMPdVWBbRrg76YTfy6H/NlQIArNLZanPvE0Vd5QfD4ZyG2hTh3y7" +
		"ZlJAUndGJ/kfZw8sKuL9QSrh4eOTc280NQUmPGz6LP5MXNmu0RxEcomod1+ToKll90yEKFAUKuPYFgm9J+vYm4tzRequLy/" +
		"njteRIkcfAdcAtt6PCYjU=</ds:Modulus><ds:Exponent>AQAB</ds:Exponent></ds:RSAKeyValue></ds:KeyValu" +
		"e><ds:X509Data><ds:X509Certificate>MIIDXTCCAkUCBEQd+4EwDQYJKoZIhvcNAQEEBQAwczELMAkGA1UEBhMCVVMx" +
		"EzARBgNVBAgTCkNhbGlmb3JuaWExFjAUBgNVBAcTDVNhbiBGcmFuY2lzY28xDzANBgNVBAoTBnhtbGRhcDERMA8GA1UECxM" +
		"IaW5mb2NhcmQxEzARBgNVBAMTCnhtbGRhcC5vcmcwHhcNMDYwMzIwMDA0NjU3WhcNMDYwNjE4MDA0NjU3WjBzMQswCQYDVQ" +
		"QGEwJVUzETMBEGA1UECBMKQ2FsaWZvcm5pYTEWMBQGA1UEBxMNU2FuIEZyYW5jaXNjbzEPMA0GA1UEChMGeG1sZGFwMREwD" +
		"wYDVQQLEwhpbmZvY2FyZDETMBEGA1UEAxMKeG1sZGFwLm9yZzCCASIwDQYJKoZIhvcNAQEBBQADggEPADCCAQoCggEBANMn" +
		"kVA4xfpG0bLos9FOpNBjHAdFahy2cJ7FUwuXd/IShnG+5qF/z1SdPWzRxTtpFFyodtXlBUEIbiT+IbYPZF1vCcBrcFa8Kz/" +
		"4rBjrpPZgllgA/WSVKjnJvw8q4/tO6CQZSlRlj/ebNK9VyT1kN+MrKV1SGTqaIJ2l+7Rd05WHscwZMPdVWBbRrg76YTfy6H" +
		"/NlQIArNLZanPvE0Vd5QfD4ZyG2hTh3y7ZlJAUndGJ/kfZw8sKuL9QSrh4eOTc280NQUmPGz6LP5MXNmu0RxEcomod1+ToK" +
		"ll90yEKFAUKuPYFgm9J+vYm4tzRequLy/njteRIkcfAdcAtt6PCYjUCAwEAATANBgkqhkiG9w0BAQQFAAOCAQEAURtxiA7q" +
		"DSq/WlUpWpfWiZ7HvveQrwTaTwV/Fk3l/I9e9WIRN51uFLuiLtZMMwR02BX7Yva1KQ/Gl999cm/0b5hptJ+TU29rVPZIlI3" +
		"2c5vjcuSVoEda8+BRj547jlC0rNokyWm+YtBcDOwfHSPFFwVPPVxyQsVEebsiB6KazFq6iZ8A0F2HLEnpsdFnGrSwBBbH3I" +
		"3PH65ofrTTgj1Mjk5kA6EVaeefDCtlkX2ogIFMlcS6ruihX2mlCLUSrlPs9TH+M4j/R/LV5QWJ93/X9gsxFrxVFGg3b75EK" +
		"QP8MZ111/jaeKd80mUOAiTO06EtfjXZPrjPN4e2l05i2EGDUA==</ds:X509Certificate></ds:X509Data>" +
		"</ds:KeyInfo></ds:Signature></xmldap:Body>\n";

	private static final String SIGNED_XML =
			"<xmldap:Body " +
			"xmlns:wsu=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd\" " +
			"xmlns:xmldap=\"http://www.xmldap.org\" " +
			"wsu:Id=\"urn:guid:58824342-832F-C99B-925B-CB0E858E5D65\">" +
			"<xmldap:Child /><ds:Signature xmlns:ds=\"http://www.w3.org/2000/09/xmldsig#\">" +
			"<ds:SignedInfo><ds:CanonicalizationMethod " +
			"Algorithm=\"http://www.w3.org/2001/10/xml-exc-c14n#\" />" +
			"<ds:SignatureMethod Algorithm=\"http://www.w3.org/2000/09/xmldsig#rsa-sha1\" />" +
			"<ds:Reference URI=\"\"><ds:Transforms>" +
			"<ds:Transform Algorithm=\"http://www.w3.org/2000/09/xmldsig#enveloped-signature\" />" +
			"<ds:Transform Algorithm=\"http://www.w3.org/2001/10/xml-exc-c14n#\" />" +
			"</ds:Transforms><ds:DigestMethod " +
			"Algorithm=\"http://www.w3.org/2000/09/xmldsig#sha1\" />" +
			"<ds:DigestValue>8xRvuAvdu90P9qcwm3kaUNSh4/c=</ds:DigestValue></ds:Reference>" +
			"</ds:SignedInfo>" +
			"<ds:SignatureValue>udWT9QYo6X/Z0YVIN/deUJxGU3CYiDV6OLYxSo/8xjqEKXYzhpwaGEZMytWRBL" +
			"M/clcd1PViJCdQZRbHnLzBeXk193c5c06FJC56sruoWg8WoqkABd0ZwY4TxxKxQx2+EHqIJMLV06zPOqk" +
			"BbR9/l14TPIv1E7aRR5S+hjycvCTvZ5rkG4HWHo0czdvE0b4T10LN6+aokr5SYDRrouQcU9gDpAOJ9r2l" +
			"7xTc39wNk/d4iGJLqbj0X+9S6GqldDMrBFXnTe8tdyoLjCdZpgwMa9Ml0Cx6HBF8CBL8U9R0vlfMQuyd5" +
			"9ZRHPSlyClf9cYzz7eE3srrMQATImkDS1I0LA==</ds:SignatureValue>" +
			"<ds:KeyInfo><ds:KeyName>" +
			"Public Key for CN=xmldap.org, OU=infocard, O=xmldap, L=San Francisco, " +
			"ST=California, C=US</ds:KeyName><ds:KeyValue><ds:RSAKeyValue>" +
			"<ds:Modulus>ANMnkVA4xfpG0bLos9FOpNBjHAdFahy2cJ7FUwuXd/IShnG+5qF/z1SdPWzRxTtpFFyod" +
			"tXlBUEIbiT+IbYPZF1vCcBrcFa8Kz/4rBjrpPZgllgA/WSVKjnJvw8q4/tO6CQZSlRlj/ebNK9VyT1kN+" +
			"MrKV1SGTqaIJ2l+7Rd05WHscwZMPdVWBbRrg76YTfy6H/NlQIArNLZanPvE0Vd5QfD4ZyG2hTh3y7ZlJA" +
			"UndGJ/kfZw8sKuL9QSrh4eOTc280NQUmPGz6LP5MXNmu0RxEcomod1+ToKll90yEKFAUKuPYFgm9J+vYm" +
			"4tzRequLy/njteRIkcfAdcAtt6PCYjU=</ds:Modulus><ds:Exponent>AQAB</ds:Exponent></ds:" +
			"RSAKeyValue></ds:KeyValue><ds:X509Data><ds:X509Certificate>MIIDXTCCAkUCBEQd+4EwDQ" +
			"YJKoZIhvcNAQEEBQAwczELMAkGA1UEBhMCVVMxEzARBgNVBAgTCkNhbGlmb3JuaWExFjAUBgNVBAcTDVN" +
			"hbiBGcmFuY2lzY28xDzANBgNVBAoTBnhtbGRhcDERMA8GA1UECxMIaW5mb2NhcmQxEzARBgNVBAMTCnht" +
			"bGRhcC5vcmcwHhcNMDYwMzIwMDA0NjU3WhcNMDYwNjE4MDA0NjU3WjBzMQswCQYDVQQGEwJVUzETMBEGA" +
			"1UECBMKQ2FsaWZvcm5pYTEWMBQGA1UEBxMNU2FuIEZyYW5jaXNjbzEPMA0GA1UEChMGeG1sZGFwMREwDw" +
			"YDVQQLEwhpbmZvY2FyZDETMBEGA1UEAxMKeG1sZGFwLm9yZzCCASIwDQYJKoZIhvcNAQEBBQADggEPADC" +
			"CAQoCggEBANMnkVA4xfpG0bLos9FOpNBjHAdFahy2cJ7FUwuXd/IShnG+5qF/z1SdPWzRxTtpFFyodtXl" +
			"BUEIbiT+IbYPZF1vCcBrcFa8Kz/4rBjrpPZgllgA/WSVKjnJvw8q4/tO6CQZSlRlj/ebNK9VyT1kN+MrK" +
			"V1SGTqaIJ2l+7Rd05WHscwZMPdVWBbRrg76YTfy6H/NlQIArNLZanPvE0Vd5QfD4ZyG2hTh3y7ZlJAUnd" +
			"GJ/kfZw8sKuL9QSrh4eOTc280NQUmPGz6LP5MXNmu0RxEcomod1+ToKll90yEKFAUKuPYFgm9J+vYm4tz" +
			"RequLy/njteRIkcfAdcAtt6PCYjUCAwEAATANBgkqhkiG9w0BAQQFAAOCAQEAURtxiA7qDSq/WlUpWpfW" +
			"iZ7HvveQrwTaTwV/Fk3l/I9e9WIRN51uFLuiLtZMMwR02BX7Yva1KQ/Gl999cm/0b5hptJ+TU29rVPZIl" +
			"I32c5vjcuSVoEda8+BRj547jlC0rNokyWm+YtBcDOwfHSPFFwVPPVxyQsVEebsiB6KazFq6iZ8A0F2HLE" +
			"npsdFnGrSwBBbH3I3PH65ofrTTgj1Mjk5kA6EVaeefDCtlkX2ogIFMlcS6ruihX2mlCLUSrlPs9TH+M4j" +
			"/R/LV5QWJ93/X9gsxFrxVFGg3b75EKQP8MZ111/jaeKd80mUOAiTO06EtfjXZPrjPN4e2l05i2EGDUA==" +
			"</ds:X509Certificate></ds:X509Data></ds:KeyInfo></ds:Signature></xmldap:Body>";

	public void setUp() throws Exception {
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
		EnvelopedSignature signer = new EnvelopedSignature(keyInfo,
				signingKey);
		Element signedXML = signer.sign(body);
//		System.out.println(signedXML.toXML());
		assertTrue(EnvelopedSignature.validate(signedXML.toXML()));

	}

	public void testToXMLWithDoc() throws Exception {

		Element body = new Element("xmldap:Body", "http://www.xmldap.org");
		body.addNamespaceDeclaration(WSConstants.WSU_PREFIX,
				WSConstants.WSSE_OASIS_10_WSU_NAMESPACE);
		Attribute idAttr = new Attribute(WSConstants.WSU_PREFIX + ":Id",
				WSConstants.WSSE_OASIS_10_WSU_NAMESPACE,
				"urn:guid:58824342-832F-C99B-925B-CB0E858E5D65");
		body.addAttribute(idAttr);
		Element child = new Element("xmldap:Child", "http://www.xmldap.org");
		body.appendChild(child);
		Document bodyDoc = new Document(body);

		X509Certificate signingCert = org.xmldap.util.XmldapCertsAndKeys
				.getXmldapCert();
		RSAPrivateKey signingKey = org.xmldap.util.XmldapCertsAndKeys
				.getXmldapPrivateKey();

		KeyInfo keyInfo = new AsymmetricKeyInfo(signingCert);
		EnvelopedSignature signer = new EnvelopedSignature(keyInfo,
				signingKey);
		Document signedXML = signer.sign(bodyDoc);
//		String test = signedXML.toXML();
//		System.out.println(test);
//		System.out.println(test.length() + ":" + SIGNED_XML_DOC.length());

//		System.out.println(signedXML.toXML());
		assertTrue(EnvelopedSignature.validate(signedXML.toXML()));

	}

	public void testToXMLWithString() throws Exception {

		X509Certificate signingCert = org.xmldap.util.XmldapCertsAndKeys
				.getXmldapCert();
		RSAPrivateKey signingKey = org.xmldap.util.XmldapCertsAndKeys
				.getXmldapPrivateKey();

		KeyInfo keyInfo = new AsymmetricKeyInfo(signingCert);
		EnvelopedSignature signer = new EnvelopedSignature(keyInfo,
				signingKey);

		assertTrue(EnvelopedSignature.validate(signer.sign(XML)));

	}

	public void testStub() throws Exception {

		assertTrue(true);

	}

}
