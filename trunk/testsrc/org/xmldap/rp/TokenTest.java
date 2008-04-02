package org.xmldap.rp;

import java.math.BigInteger;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

import org.xmldap.exceptions.CryptoException;
import org.xmldap.exceptions.InfoCardProcessingException;
import org.xmldap.infocard.SelfIssuedToken;
import org.xmldap.util.Base64;
import org.xmldap.util.XmldapCertsAndKeys;
import org.xmldap.xmldsig.ValidatingBaseEnvelopedSignature;

import junit.framework.TestCase;

public class TokenTest extends TestCase {

	String selfIssuedTokenStr = null;
	RSAPrivateKey xmldapKey = null;
	
	protected void setUp() throws Exception {
		super.setUp();
		
		X509Certificate xmldapCert = XmldapCertsAndKeys.getXmldapCert();
		xmldapKey = XmldapCertsAndKeys.getXmldapPrivateKey();
		
		X509Certificate relyingPartyCert = xmldapCert;
		RSAPublicKey signingKey = (RSAPublicKey)xmldapCert.getPublicKey();
		SelfIssuedToken token = new SelfIssuedToken(signingKey, xmldapKey);
	
		token.setPrivatePersonalIdentifier(Base64.encodeBytesNoBreaks("ppid".getBytes()));
		token.setValidityPeriod(-5, 10);
		token.setConfirmationMethodBEARER();
		
		selfIssuedTokenStr = token.toXML();
		// e.g.: 
		// <saml:Assertion xmlns:saml="urn:oasis:names:tc:SAML:1.0:assertion" MajorVersion="1" MinorVersion="1" 
		// AssertionID="uuid-8A443583-6887-6A21-D5D8-811EEF95AE32" 
		// Issuer="http://schemas.xmlsoap.org/ws/2005/05/identity/issuer/self"  
		// IssueInstant="2007-09-13T09:05:03Z"> 
		// <saml:Conditions NotBefore="2007-09-13T09:00:02Z" NotOnOrAfter="2007-09-13T09:15:02Z" /> 
		// <saml:AttributeStatement><saml:Subject> 
		// <saml:SubjectConfirmation><saml:ConfirmationMethod>urn:oasis:names:tc:SAML:1.0:cm:bearer</saml:ConfirmationMethod> 
		// </saml:SubjectConfirmation></saml:Subject> 
		// <saml:Attribute AttributeName="privatepersonalidentifier"  
		// AttributeNamespace="http://schemas.xmlsoap.org/ws/2005/05/identity/claims/"> 
		// <saml:AttributeValue>cHBpZA==</saml:AttributeValue></saml:Attribute></saml:AttributeStatement> 
		// <dsig:Signature xmlns:dsig="http://www.w3.org/2000/09/xmldsig#"><dsig:SignedInfo> 
		// <dsig:CanonicalizationMethod Algorithm="http://www.w3.org/2001/10/xml-exc-c14n#" /> 
		// <dsig:SignatureMethod Algorithm="http://www.w3.org/2000/09/xmldsig#rsa-sha1" /> 
		// <dsig:Reference URI="#uuid-8A443583-6887-6A21-D5D8-811EEF95AE32"><dsig:Transforms> 
		// <dsig:Transform Algorithm="http://www.w3.org/2000/09/xmldsig#enveloped-signature" /> 
		// <dsig:Transform Algorithm="http://www.w3.org/2001/10/xml-exc-c14n#" /></dsig:Transforms> 
		// <dsig:DigestMethod Algorithm="http://www.w3.org/2000/09/xmldsig#sha1" /> 
		// <dsig:DigestValue>mqNBJacR2OJcr2UTHf4oGU6xYg4=</dsig:DigestValue></dsig:Reference> 
		// </dsig:SignedInfo> 
		// <dsig:SignatureValue>xydYzGbfpdGPA0KIUCVn/UHsekDF67X/a7yAUxaae9T5XeGeiFXv4Mb/GGG41c4J 
		// Su7eA1/5Wcz4a0Wl/woArL7z812SFubyVeKqCDDXTOus38Me5CCHfKdAqVNQi2nTDPF4g4plc8JeZNpAF8ATA 
		// GaCPU8O4vwr6SfueFILMOBrOUc9DKzi8i0Bc7uJ1niODoUBgBn+OmGAdCX1lZgwGmXpid1WoiCzBkJ+luihF7 
		// GZ757Xys7CgH389eBO560fXMG9eHdDy4cw3x71ozq8XglcegJkxfLD5cNolsMIuj7ufxi/x6Wp0fkhRyC3V9O 
		// M2tbxH+kIKltMQQrN4OcLVw==</dsig:SignatureValue><dsig:KeyInfo><dsig:KeyValue> 
		// <dsig:RSAKeyValue><dsig:Modulus>ANMnkVA4xfpG0bLos9FOpNBjHAdFahy2cJ7FUwuXd/IShnG+5qF/z 
		// 1SdPWzRxTtpFFyodtXlBUEIbiT+IbYPZF1vCcBrcFa8Kz/4rBjrpPZgllgA/WSVKjnJvw8q4/tO6CQZSlRlj/ 
		// ebNK9VyT1kN+MrKV1SGTqaIJ2l+7Rd05WHscwZMPdVWBbRrg76YTfy6H/NlQIArNLZanPvE0Vd5QfD4ZyG2hT 
		// h3y7ZlJAUndGJ/kfZw8sKuL9QSrh4eOTc280NQUmPGz6LP5MXNmu0RxEcomod1+ToKll90yEKFAUKuPYFgm9J 
		// +vYm4tzRequLy/njteRIkcfAdcAtt6PCYjU=</dsig:Modulus><dsig:Exponent>AQAB</dsig:Exponent> 
		// </dsig:RSAKeyValue></dsig:KeyValue></dsig:KeyInfo></dsig:Signature></saml:Assertion>

	}

	public void testGetClientDigest() throws InfoCardProcessingException, CryptoException {
		Token token = new Token(selfIssuedTokenStr, xmldapKey);
		String digest = token.getClientDigest();
		
		assertEquals("Di8/RScn6chI6xtbQhLXZQNJTiE=",digest);
	}
	
	public void testSelfIssuedToken() throws InfoCardProcessingException {
		Token token = new Token(selfIssuedTokenStr, xmldapKey);
		assertFalse(token.isEncrypted());
		
		assertTrue(token.isSignatureValid());
	}
	
	public void testLength() {
		String sv = "xydYzGbfpdGPA0KIUCVn/UHsekDF67X/a7yAUxaae9T5XeGeiFXv4Mb/GGG41c4JSu7eA1/5Wcz4a0Wl/woArL7z812SFubyVeKqCDDXTOus38Me5CCHfKdAqVNQi2nTDPF4g4plc8JeZNpAF8ATAGaCPU8O4vwr6SfueFILMOBrOUc9DKzi8i0Bc7uJ1niODoUBgBn+OmGAdCX1lZgwGmXpid1WoiCzBkJ+luihF7GZ757Xys7CgH389eBO560fXMG9eHdDy4cw3x71ozq8XglcegJkxfLD5cNolsMIuj7ufxi/x6Wp0fkhRyC3V9OM2tbxH+kIKltMQQrN4OcLVw==";
		String mo = "ANMnkVA4xfpG0bLos9FOpNBjHAdFahy2cJ7FUwuXd/IShnG+5qF/z1SdPWzRxTtpFFyodtXlBUEIbiT+IbYPZF1vCcBrcFa8Kz/4rBjrpPZgllgA/WSVKjnJvw8q4/tO6CQZSlRlj/ebNK9VyT1kN+MrKV1SGTqaIJ2l+7Rd05WHscwZMPdVWBbRrg76YTfy6H/NlQIArNLZanPvE0Vd5QfD4ZyG2hTh3y7ZlJAUndGJ/kfZw8sKuL9QSrh4eOTc280NQUmPGz6LP5MXNmu0RxEcomod1+ToKll90yEKFAUKuPYFgm9J+vYm4tzRequLy/njteRIkcfAdcAtt6PCYjU=";
		assertEquals(sv.length(), mo.length());
		BigInteger modulus = new BigInteger(1, Base64.decode(mo));
		assertEquals(2048, modulus.bitLength());
		assertEquals(Base64.decode(sv).length, Base64.decode(mo).length);
	}
	
	public void testLengthA() throws CryptoException {
		String a = "<saml:Assertion xmlns:saml=\"urn:oasis:names:tc:SAML:1.0:assertion\" AssertionID=\"uuid-7B20C5C0-9B85-35D1-590A-D1B3093451CF\" Issuer=\"http://schemas.microsoft.com/ws/2005/05/identity/issuer/self\" IssueInstant=\"2007-08-30T15:10:47Z\" MajorVersion=\"1\" MinorVersion=\"1\"><saml:Conditions NotBefore=\"2007-08-30T15:05:47Z\" NotOnOrAfter=\"2007-08-30T15:20:47Z\"><saml:AudienceRestrictionCondition><saml:Audience>https://w4de3esy0069028.gdc-bln01.t-systems.com:8443/relyingparty/</saml:Audience></saml:AudienceRestrictionCondition></saml:Conditions><saml:AttributeStatement><saml:Subject><saml:SubjectConfirmation><saml:ConfirmationMethod>urn:oasis:names:tc:SAML:1.0:cm:bearer</saml:ConfirmationMethod></saml:SubjectConfirmation></saml:Subject><saml:Attribute AttributeName=\"givenname\" AttributeNamespace=\"http://schemas.xmlsoap.org/ws/2005/05/identity/claims/givenname\"><saml:AttributeValue>Axel</saml:AttributeValue></saml:Attribute><saml:Attribute AttributeName=\"surname\" AttributeNamespace=\"http://schemas.xmlsoap.org/ws/2005/05/identity/claims/surname\"><saml:AttributeValue>Nennker</saml:AttributeValue></saml:Attribute><saml:Attribute AttributeName=\"emailaddress\" AttributeNamespace=\"http://schemas.xmlsoap.org/ws/2005/05/identity/claims/emailaddress\"><saml:AttributeValue>axel@nennker.de</saml:AttributeValue></saml:Attribute><saml:Attribute AttributeName=\"privatepersonalidentifier\" AttributeNamespace=\"http://schemas.xmlsoap.org/ws/2005/05/identity/claims/privatepersonalidentifier\"><saml:AttributeValue>bXRwZTJPZUhldWJKU1lydDMxWThodnB1cFpCRmd6MDVlaXViWWo3NzJaTT0=</saml:AttributeValue></saml:Attribute></saml:AttributeStatement><dsig:Signature xmlns:dsig=\"http://www.w3.org/2000/09/xmldsig#\"><dsig:SignedInfo><dsig:CanonicalizationMethod Algorithm=\"http://www.w3.org/2001/10/xml-exc-c14n#\" /><dsig:SignatureMethod Algorithm=\"http://www.w3.org/2000/09/xmldsig#rsa-sha1\" /><dsig:Reference URI=\"#uuid-7B20C5C0-9B85-35D1-590A-D1B3093451CF\"><dsig:Transforms><dsig:Transform Algorithm=\"http://www.w3.org/2000/09/xmldsig#enveloped-signature\" /><dsig:Transform Algorithm=\"http://www.w3.org/2001/10/xml-exc-c14n#\" /></dsig:Transforms><dsig:DigestMethod Algorithm=\"http://www.w3.org/2000/09/xmldsig#sha1\" /><dsig:DigestValue>P834/zjB6jZbz80UPkCJQ+IGoqk=</dsig:DigestValue></dsig:Reference></dsig:SignedInfo><dsig:SignatureValue>lg/8RNBJ2JsSwkPY8G4VU+mS89NhPKn0psIIwdD9uiMVknLxQk3+79kP46CzLfpczy6Azjv17sXMgHJDr7XFchfKArhoAgaVc+ulkUpSOJNW8f5cVLMHvEmD2Qo5/VcYOgrVS72+d0rK8A42twUublm+8TjxGPp/oVSFxtTmg4E=</dsig:SignatureValue><dsig:KeyInfo><dsig:KeyValue><dsig:RSAKeyValue><dsig:Modulus>ALgc5OE4nyN5TfZS6wa5LT4rEfAMMuoOWknZoRv4T6wZcoEh31g2haNcbcqq+5PXeB+hSMwL4XBfKqs+JK5a4/WyTVfJ+Zedutq5t6S5Rq5v2jwVuFy5ZuWVAl5629slvcPtNGg3LeHvkz7fcgbxLreAIk5ojE4YQRRpffmGWH4j</dsig:Modulus><dsig:Exponent>AQAB</dsig:Exponent></dsig:RSAKeyValue></dsig:KeyValue></dsig:KeyInfo></dsig:Signature></saml:Assertion>";
		String sv = "lg/8RNBJ2JsSwkPY8G4VU+mS89NhPKn0psIIwdD9uiMVknLxQk3+79kP46CzLfpczy6Azjv17sXMgHJDr7XFchfKArhoAgaVc+ulkUpSOJNW8f5cVLMHvEmD2Qo5/VcYOgrVS72+d0rK8A42twUublm+8TjxGPp/oVSFxtTmg4E=";
		String mo = "ALgc5OE4nyN5TfZS6wa5LT4rEfAMMuoOWknZoRv4T6wZcoEh31g2haNcbcqq+5PXeB+hSMwL4XBfKqs+JK5a4/WyTVfJ+Zedutq5t6S5Rq5v2jwVuFy5ZuWVAl5629slvcPtNGg3LeHvkz7fcgbxLreAIk5ojE4YQRRpffmGWH4j";
		assertEquals(sv.length(), mo.length());
		BigInteger modulus = new BigInteger(1, Base64.decode(mo));
		assertEquals(1024, modulus.bitLength());
		
		assertTrue(ValidatingBaseEnvelopedSignature.validate(a));
		assertEquals(Base64.decode(sv).length, Base64.decode(mo).length);
	}
}
