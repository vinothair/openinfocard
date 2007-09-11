package org.xmldap.xmldsig;

import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Iterator;
import java.util.Vector;

import org.xmldap.exceptions.SerializationException;
import org.xmldap.exceptions.SigningException;
import org.xmldap.infocard.SelfIssuedToken;
import org.xmldap.saml.Attribute;
import org.xmldap.saml.AttributeStatement;
import org.xmldap.saml.Conditions;
import org.xmldap.saml.SAMLAssertion;
import org.xmldap.saml.Subject;
import org.xmldap.util.Base64;
import org.xmldap.util.XmldapCertsAndKeys;
import org.xmldap.ws.WSConstants;

//import nu.xom.Attribute;
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

	String samlAssertion = "<saml:Assertion xmlns:saml=\"urn:oasis:names:tc:SAML:1.0:assertion\" AssertionID=\"uuid-C42AAF6A-F9E3-517B-A112-3EE10E6B2446\" Issuer=\"http://schemas.microsoft.com/ws/2005/05/identity/issuer/self\" IssueInstant=\"2007-08-06T13:23:49Z\" MajorVersion=\"1\" MinorVersion=\"1\"><saml:Conditions NotBefore=\"2007-08-06T13:18:49Z\" NotOnOrAfter=\"2007-08-06T13:33:49Z\" /><saml:AttributeStatement><saml:Subject><saml:SubjectConfirmation><saml:ConfirmationMethod>urn:oasis:names:tc:SAML:1.0:cm:holder-of-key</saml:ConfirmationMethod><dsig:KeyInfo xmlns:dsig=\"http://www.w3.org/2000/09/xmldsig#\"><dsig:KeyName>Public Key for CN=firefox,OU=infocard selector,O=xmldap,L=San Francisco,ST=California,C=US</dsig:KeyName><dsig:KeyValue><dsig:RSAKeyValue><dsig:Modulus>ALZMhAYh+wSeCIW0zt3EIfHuRgseoh6U4jRx78B28zg7+otOD1PvQDOz6z+mkhtP2nL+vF/+hepvcCMn25iR3tZM60EUMppP8ITfaWAf+YNzUyCh9ALpcEenahziSIGms7QPVvuKIWxc4/Mf/MG/fnkY0hp4E1lionGHw2tOiWa9</dsig:Modulus><dsig:Exponent>AQAB</dsig:Exponent></dsig:RSAKeyValue></dsig:KeyValue><dsig:X509Data><dsig:X509Certificate>MIIDjzCCAvigAwIBAgIGARQ7SsIcMA0GCSqGSIb3DQEBBQUAMHkxEDAOBgNVBAMMB2ZpcmVmb3gxGjAYBgNVBAsMEWluZm9jYXJkIHNlbGVjdG9yMQ8wDQYDVQQKDAZ4bWxkYXAxFjAUBgNVBAcMDVNhbiBGcmFuY2lzY28xEzARBgNVBAgMCkNhbGlmb3JuaWExCzAJBgNVBAYTAlVTMB4XDTA3MDgwNjExMDg0OFoXDTEyMDgwNjExMDg0OFoweTEQMA4GA1UEAwwHZmlyZWZveDEaMBgGA1UECwwRaW5mb2NhcmQgc2VsZWN0b3IxDzANBgNVBAoMBnhtbGRhcDEWMBQGA1UEBwwNU2FuIEZyYW5jaXNjbzETMBEGA1UECAwKQ2FsaWZvcm5pYTELMAkGA1UEBhMCVVMwgZ8wDQYJKoZIhvcNAQEBBQADgY0AMIGJAoGBALZMhAYh+wSeCIW0zt3EIfHuRgseoh6U4jRx78B28zg7+otOD1PvQDOz6z+mkhtP2nL+vF/+hepvcCMn25iR3tZM60EUMppP8ITfaWAf+YNzUyCh9ALpcEenahziSIGms7QPVvuKIWxc4/Mf/MG/fnkY0hp4E1lionGHw2tOiWa9AgMBAAGjggEgMIIBHDAPBgNVHRMBAf8EBTADAQH/MA4GA1UdDwEB/wQEAwIBtjAWBgNVHSUBAf8EDDAKBggrBgEFBQcDATARBglghkgBhvhCAQEEBAMCAAQwHQYDVR0OBBYEFHzAIJQ8C3oBoZtSenofswadkwzqMB8GA1UdIwQYMBaAFHzAIJQ8C3oBoZtSenofswadkwzqMIGNBggrBgEFBQcBDASBgDB+oXygejB4MHYwdBYJaW1hZ2UvanBnMCEwHzAHBgUrDgMCGgQUltpa9g9Q8YSEOj8sLZpb846h0NQwRBZCaHR0cDovL3N0YXRpYy5mbGlja3IuY29tLzEwL2J1ZGR5aWNvbnMvMTgxMTkxOTZATjAwLmpwZz8xMTE1NTQ5NDg2MA0GCSqGSIb3DQEBBQUAA4GBAD0tkpEI2tlmV8wu5M1QsSC/lQm7yXXG6wJ+Tmxr+KRBE+RVbzV8Uj2sExtwGN6er1KCZNDRlxnvvRf8+ScX4DtaR0fn2kTlfavTYs2LmtCl2SkNw25Ni+SQy3vFjRoA+9nM1QHDtGl7sH5PNq+GxdZSmqAabS+7l2rtS77CtTw+</dsig:X509Certificate></dsig:X509Data></dsig:KeyInfo></saml:SubjectConfirmation></saml:Subject><saml:Attribute AttributeName=\"givenname\" AttributeNamespace=\"http://schemas.xmlsoap.org/ws/2005/05/identity/claims/givenname\"><saml:AttributeValue>Jochen</saml:AttributeValue></saml:Attribute><saml:Attribute AttributeName=\"surname\" AttributeNamespace=\"http://schemas.xmlsoap.org/ws/2005/05/identity/claims/surname\"><saml:AttributeValue>Klaffer</saml:AttributeValue></saml:Attribute><saml:Attribute AttributeName=\"emailaddress\" AttributeNamespace=\"http://schemas.xmlsoap.org/ws/2005/05/identity/claims/emailaddress\"><saml:AttributeValue>jochen.klaffer@t-systems.com</saml:AttributeValue></saml:Attribute><saml:Attribute AttributeName=\"privatepersonalidentifier\" AttributeNamespace=\"http://schemas.xmlsoap.org/ws/2005/05/identity/claims/privatepersonalidentifier\"><saml:AttributeValue>S21sSUN4VGRIN2VTSTB2M1c3Y085aEpsMnFIa3l4ZnF5SnhKS3g5RGkxZz0=</saml:AttributeValue></saml:Attribute></saml:AttributeStatement><dsig:Signature xmlns:dsig=\"http://www.w3.org/2000/09/xmldsig#\"><dsig:SignedInfo><dsig:CanonicalizationMethod Algorithm=\"http://www.w3.org/2001/10/xml-exc-c14n#\" /><dsig:SignatureMethod Algorithm=\"http://www.w3.org/2000/09/xmldsig#rsa-sha1\" /><dsig:Reference URI=\"#uuid-C42AAF6A-F9E3-517B-A112-3EE10E6B2446\"><dsig:Transforms><dsig:Transform Algorithm=\"http://www.w3.org/2000/09/xmldsig#enveloped-signature\" /><dsig:Transform Algorithm=\"http://www.w3.org/2001/10/xml-exc-c14n#\" /></dsig:Transforms><dsig:DigestMethod Algorithm=\"http://www.w3.org/2000/09/xmldsig#sha1\" /><dsig:DigestValue>uGy32/62i/1hZvDuXe3Az8DggTQ=</dsig:DigestValue></dsig:Reference></dsig:SignedInfo><dsig:SignatureValue>RRUQihmc9+8MkdOLPTGnQiQsnQPW28izPk1iSamgGOOpZ9GoqSrAGTe9ESWqMhoAHfQS7KK9vcf8e0PPb8J+7KqL/yO8bbX8ylCQqg+zakgvFuKMFWHmbcZ9S+ReiIHDqYMpkmvcr9ha2Ox7LV+hlppQHrwLdJpZjtV4C65i6+s=</dsig:SignatureValue><dsig:KeyInfo><dsig:KeyName>Public Key for CN=firefox,OU=infocard selector,O=xmldap,L=San Francisco,ST=California,C=US</dsig:KeyName><dsig:KeyValue><dsig:RSAKeyValue><dsig:Modulus>ALZMhAYh+wSeCIW0zt3EIfHuRgseoh6U4jRx78B28zg7+otOD1PvQDOz6z+mkhtP2nL+vF/+hepvcCMn25iR3tZM60EUMppP8ITfaWAf+YNzUyCh9ALpcEenahziSIGms7QPVvuKIWxc4/Mf/MG/fnkY0hp4E1lionGHw2tOiWa9</dsig:Modulus><dsig:Exponent>AQAB</dsig:Exponent></dsig:RSAKeyValue></dsig:KeyValue><dsig:X509Data><dsig:X509Certificate>MIIDjzCCAvigAwIBAgIGARQ7SsIcMA0GCSqGSIb3DQEBBQUAMHkxEDAOBgNVBAMMB2ZpcmVmb3gxGjAYBgNVBAsMEWluZm9jYXJkIHNlbGVjdG9yMQ8wDQYDVQQKDAZ4bWxkYXAxFjAUBgNVBAcMDVNhbiBGcmFuY2lzY28xEzARBgNVBAgMCkNhbGlmb3JuaWExCzAJBgNVBAYTAlVTMB4XDTA3MDgwNjExMDg0OFoXDTEyMDgwNjExMDg0OFoweTEQMA4GA1UEAwwHZmlyZWZveDEaMBgGA1UECwwRaW5mb2NhcmQgc2VsZWN0b3IxDzANBgNVBAoMBnhtbGRhcDEWMBQGA1UEBwwNU2FuIEZyYW5jaXNjbzETMBEGA1UECAwKQ2FsaWZvcm5pYTELMAkGA1UEBhMCVVMwgZ8wDQYJKoZIhvcNAQEBBQADgY0AMIGJAoGBALZMhAYh+wSeCIW0zt3EIfHuRgseoh6U4jRx78B28zg7+otOD1PvQDOz6z+mkhtP2nL+vF/+hepvcCMn25iR3tZM60EUMppP8ITfaWAf+YNzUyCh9ALpcEenahziSIGms7QPVvuKIWxc4/Mf/MG/fnkY0hp4E1lionGHw2tOiWa9AgMBAAGjggEgMIIBHDAPBgNVHRMBAf8EBTADAQH/MA4GA1UdDwEB/wQEAwIBtjAWBgNVHSUBAf8EDDAKBggrBgEFBQcDATARBglghkgBhvhCAQEEBAMCAAQwHQYDVR0OBBYEFHzAIJQ8C3oBoZtSenofswadkwzqMB8GA1UdIwQYMBaAFHzAIJQ8C3oBoZtSenofswadkwzqMIGNBggrBgEFBQcBDASBgDB+oXygejB4MHYwdBYJaW1hZ2UvanBnMCEwHzAHBgUrDgMCGgQUltpa9g9Q8YSEOj8sLZpb846h0NQwRBZCaHR0cDovL3N0YXRpYy5mbGlja3IuY29tLzEwL2J1ZGR5aWNvbnMvMTgxMTkxOTZATjAwLmpwZz8xMTE1NTQ5NDg2MA0GCSqGSIb3DQEBBQUAA4GBAD0tkpEI2tlmV8wu5M1QsSC/lQm7yXXG6wJ+Tmxr+KRBE+RVbzV8Uj2sExtwGN6er1KCZNDRlxnvvRf8+ScX4DtaR0fn2kTlfavTYs2LmtCl2SkNw25Ni+SQy3vFjRoA+9nM1QHDtGl7sH5PNq+GxdZSmqAabS+7l2rtS77CtTw+</dsig:X509Certificate></dsig:X509Data></dsig:KeyInfo></dsig:Signature></saml:Assertion>";
	
	String simpleSamlAssertion = "<saml:Assertion xmlns:saml=\"urn:oasis:names:tc:SAML:1.0:assertion\" AssertionID=\"uuid-7B20C5C0-9B85-35D1-590A-D1B3093451CF\" Issuer=\"http://schemas.microsoft.com/ws/2005/05/identity/issuer/self\" IssueInstant=\"2007-08-30T15:10:47Z\" MajorVersion=\"1\" MinorVersion=\"1\"><saml:Conditions NotBefore=\"2007-08-30T15:05:47Z\" NotOnOrAfter=\"2007-08-30T15:20:47Z\"><saml:AudienceRestrictionCondition><saml:Audience>https://w4de3esy0069028.gdc-bln01.t-systems.com:8443/relyingparty/</saml:Audience></saml:AudienceRestrictionCondition></saml:Conditions><saml:AttributeStatement><saml:Subject><saml:SubjectConfirmation><saml:ConfirmationMethod>urn:oasis:names:tc:SAML:1.0:cm:bearer</saml:ConfirmationMethod></saml:SubjectConfirmation></saml:Subject><saml:Attribute AttributeName=\"givenname\" AttributeNamespace=\"http://schemas.xmlsoap.org/ws/2005/05/identity/claims/givenname\"><saml:AttributeValue>Axel</saml:AttributeValue></saml:Attribute><saml:Attribute AttributeName=\"surname\" AttributeNamespace=\"http://schemas.xmlsoap.org/ws/2005/05/identity/claims/surname\"><saml:AttributeValue>Nennker</saml:AttributeValue></saml:Attribute><saml:Attribute AttributeName=\"emailaddress\" AttributeNamespace=\"http://schemas.xmlsoap.org/ws/2005/05/identity/claims/emailaddress\"><saml:AttributeValue>axel@nennker.de</saml:AttributeValue></saml:Attribute><saml:Attribute AttributeName=\"privatepersonalidentifier\" AttributeNamespace=\"http://schemas.xmlsoap.org/ws/2005/05/identity/claims/privatepersonalidentifier\"><saml:AttributeValue>bXRwZTJPZUhldWJKU1lydDMxWThodnB1cFpCRmd6MDVlaXViWWo3NzJaTT0=</saml:AttributeValue></saml:Attribute></saml:AttributeStatement><dsig:Signature xmlns:dsig=\"http://www.w3.org/2000/09/xmldsig#\"><dsig:SignedInfo><dsig:CanonicalizationMethod Algorithm=\"http://www.w3.org/2001/10/xml-exc-c14n#\" /><dsig:SignatureMethod Algorithm=\"http://www.w3.org/2000/09/xmldsig#rsa-sha1\" /><dsig:Reference URI=\"#uuid-7B20C5C0-9B85-35D1-590A-D1B3093451CF\"><dsig:Transforms><dsig:Transform Algorithm=\"http://www.w3.org/2000/09/xmldsig#enveloped-signature\" /><dsig:Transform Algorithm=\"http://www.w3.org/2001/10/xml-exc-c14n#\" /></dsig:Transforms><dsig:DigestMethod Algorithm=\"http://www.w3.org/2000/09/xmldsig#sha1\" /><dsig:DigestValue>P834/zjB6jZbz80UPkCJQ+IGoqk=</dsig:DigestValue></dsig:Reference></dsig:SignedInfo><dsig:SignatureValue>lg/8RNBJ2JsSwkPY8G4VU+mS89NhPKn0psIIwdD9uiMVknLxQk3+79kP46CzLfpczy6Azjv17sXMgHJDr7XFchfKArhoAgaVc+ulkUpSOJNW8f5cVLMHvEmD2Qo5/VcYOgrVS72+d0rK8A42twUublm+8TjxGPp/oVSFxtTmg4E=</dsig:SignatureValue><dsig:KeyInfo><dsig:KeyValue><dsig:RSAKeyValue><dsig:Modulus>ALgc5OE4nyN5TfZS6wa5LT4rEfAMMuoOWknZoRv4T6wZcoEh31g2haNcbcqq+5PXeB+hSMwL4XBfKqs+JK5a4/WyTVfJ+Zedutq5t6S5Rq5v2jwVuFy5ZuWVAl5629slvcPtNGg3LeHvkz7fcgbxLreAIk5ojE4YQRRpffmGWH4j</dsig:Modulus><dsig:Exponent>AQAB</dsig:Exponent></dsig:RSAKeyValue></dsig:KeyValue></dsig:KeyInfo></dsig:Signature></saml:Assertion>";
	
	public void setUp() throws Exception {
		super.setUp();
	}

	public void testToXMLWithElement() throws Exception {

		Element body = new Element("xmldap:Body", "http://www.xmldap.org");
		body.addNamespaceDeclaration(WSConstants.WSU_PREFIX,
				WSConstants.WSSE_OASIS_10_WSU_NAMESPACE);
		nu.xom.Attribute idAttr = new nu.xom.Attribute(WSConstants.WSU_PREFIX + ":Id",
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
		nu.xom.Attribute idAttr = new nu.xom.Attribute(WSConstants.WSU_PREFIX + ":Id",
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
	
	public void testSAMLAssertionString() throws Exception {
		assertTrue(EnvelopedSignature.validate(samlAssertion));
		assertTrue(EnvelopedSignature.validate(simpleSamlAssertion));
	}
	
	public void testSAMLAssertion() throws Exception {

		X509Certificate signingCert = org.xmldap.util.XmldapCertsAndKeys
				.getXmldapCert();
		RSAPrivateKey signingKey = org.xmldap.util.XmldapCertsAndKeys
				.getXmldapPrivateKey();

		KeyInfo keyInfo = new AsymmetricKeyInfo(signingCert);
		EnvelopedSignature signer = new EnvelopedSignature(keyInfo,
				signingKey);

        Conditions conditions = new Conditions(-5, 10);

        Subject subject = new Subject(keyInfo, Subject.HOLDER_OF_KEY);
        org.xmldap.saml.Attribute given = new org.xmldap.saml.Attribute("givenname", "http://schemas.microsoft.com/ws/2005/05/identity/claims/GivenName", "Chuck");
        org.xmldap.saml.Attribute sur = new org.xmldap.saml.Attribute("surname", "http://schemas.microsoft.com/ws/2005/05/identity/claims/SurName", "Mortimore");
        org.xmldap.saml.Attribute email = new org.xmldap.saml.Attribute("email", "http://schemas.microsoft.com/ws/2005/05/identity/claims/EmailAddress", "cmortspam@gmail.com");
        AttributeStatement statement = new AttributeStatement();
        statement.setSubject(subject);
        statement.addAttribute(given);
        statement.addAttribute(sur);
        statement.addAttribute(email);

        SAMLAssertion assertion = new SAMLAssertion();
        assertion.setConditions(conditions);
        assertion.setAttributeStatement(statement);

		assertTrue(EnvelopedSignature.validate(signer.sign(assertion.toXML())));

	}

	public void testSelfIssuedToken() throws Exception {

		X509Certificate xmldapCert = XmldapCertsAndKeys.getXmldapCert();
		RSAPrivateKey xmldapKey = XmldapCertsAndKeys.getXmldapPrivateKey();
		
//		KeyInfo keyInfo = new AsymmetricKeyInfo(xmldapCert);
//		EnvelopedSignature signer = new EnvelopedSignature(keyInfo,
//				xmldapKey);
//
		X509Certificate relyingPartyCert = xmldapCert;
		RSAPublicKey signingKey = (RSAPublicKey)xmldapCert.getPublicKey();
		SelfIssuedToken token = new SelfIssuedToken(relyingPartyCert,
				signingKey, xmldapKey);
	
		token.setPrivatePersonalIdentifier(Base64.encodeBytes("ppid".getBytes()));
		token.setValidityPeriod(-5, 10);
	
		String xml = token.toXML();
//		String signedToken = signer.sign(xml);
		assertTrue(EnvelopedSignature.validate(xml));
	}
	
	public void testSelfIssuedTokenInline() throws Exception {

		X509Certificate xmldapCert = XmldapCertsAndKeys.getXmldapCert();
		RSAPrivateKey xmldapKey = XmldapCertsAndKeys.getXmldapPrivateKey();
		
//		KeyInfo keyInfo = new AsymmetricKeyInfo(xmldapCert);
//		EnvelopedSignature signer = new EnvelopedSignature(keyInfo,
//				xmldapKey);
//
		RSAPublicKey signingKey = (RSAPublicKey)xmldapCert.getPublicKey();
		SelfIssuedToken token = new SelfIssuedToken(xmldapCert,
				signingKey, xmldapKey);
	
		token.setPrivatePersonalIdentifier(Base64.encodeBytes("ppid".getBytes()));
		token.setValidityPeriod(-5, 10);

		Conditions conditions = new Conditions(-5, 10);

		Subject subject = new Subject(Subject.BEARER);

		Vector attributes = new Vector();

		Attribute attr = new Attribute("emailaddress", org.xmldap.infocard.Constants.IC_NAMESPACE_PREFIX, "axel@nennker.de");
		attributes.add(attr);

		AttributeStatement statement = new AttributeStatement();
		statement.setSubject(subject);

		Iterator iter = attributes.iterator();
		while (iter.hasNext()) {
			statement.addAttribute((Attribute) iter.next());
		}

		SAMLAssertion assertion = new SAMLAssertion();
		assertion.setConditions(conditions);
		assertion.setAttributeStatement(statement);

		RsaPublicKeyInfo keyInfo = new RsaPublicKeyInfo((RSAPublicKey)xmldapCert.getPublicKey());
		BaseEnvelopedSignature signer = new BaseEnvelopedSignature(keyInfo,	xmldapKey);

		Element signedXML = null;
		try {
			Element signThisOne = (Element) assertion.serialize().copy();
			
			String idVal = signThisOne.getAttributeValue("Id", WSConstants.WSSE_OASIS_10_WSU_NAMESPACE);
			if (idVal == null) {
			    //let's see if its a SAML assertions
			    nu.xom.Attribute assertionID = signThisOne.getAttribute("AssertionID");
			    if (assertionID != null) {
			        idVal = assertionID.getValue();
			    }
			}
			if (idVal == null) {
				throw new IllegalArgumentException("BaseEnvelopedSignature: Element to sign does not have an id-ttribute");
			}
			Reference reference = new Reference(signThisOne, idVal);
			
			//Get SignedInfo for reference
			SignedInfo signedInfo = new SignedInfo(reference);
			
			Signature signature = signer.getSignatureValue(signedInfo);
			
			//Envelope it.
			try {
				signThisOne.appendChild(signature.serialize());
			} catch (SerializationException e) {
			    throw new SigningException("Could not create enveloped signature due to serialization error", e);
			}
			signedXML = signThisOne;
		} catch (SigningException e) {
			throw new SerializationException("Error signing assertion", e);
		}
		Element sit = signedXML;
	
		String xml = sit.toXML();
//		String signedToken = signer.sign(xml);
		assertTrue(EnvelopedSignature.validate(xml));
	}

	public void testStub() throws Exception {

		assertTrue(true);

	}

}
