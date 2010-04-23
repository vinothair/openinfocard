package org.xmldap.xmldsig;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPrivateKey;
import java.util.Calendar;

import nu.xom.Attribute;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Node;
import nu.xom.Nodes;
import nu.xom.XPathContext;

import org.xmldap.crypto.CryptoUtils;
import org.xmldap.exceptions.CryptoException;
import org.xmldap.saml.AttributeStatement;
import org.xmldap.saml.Conditions;
import org.xmldap.saml.SAMLAssertion;
import org.xmldap.saml.Subject;
import org.xmldap.util.Base64;
import org.xmldap.util.RandomGUID;
import org.xmldap.util.XSDDateTime;
import org.xmldap.ws.WSConstants;
import org.xmldap.xml.Canonicalizable;
import org.xmldap.xml.XmlUtils;

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
    String signingAlgorithm = "SHA1withRSA";
    BaseEnvelopedSignature signer = new BaseEnvelopedSignature(keyInfo,
        signingKey, signingAlgorithm);
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
    String signingAlgorithm = "SHA1withRSA";
    BaseEnvelopedSignature signer = new BaseEnvelopedSignature(keyInfo,
        signingKey, signingAlgorithm);
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
    String signingAlgorithm = "SHA1withRSA";
    BaseEnvelopedSignature signer = new BaseEnvelopedSignature(keyInfo,
        signingKey, signingAlgorithm);
    Element signedXML = signer.sign(body);

    byte[] digest = BaseEnvelopedSignature
        .getAssertionCanonicalBytes(signedXML);
    // System.out.println(signedXML.toXML());
    String expected = "<xmldap:Body "
        + "xmlns:wsu=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd\" "
        + "xmlns:xmldap=\"http://www.xmldap.org\" "
        + "wsu:Id=\"urn:guid:58824342-832F-C99B-925B-CB0E858E5D65\">"
        + "<xmldap:Child></xmldap:Child></xmldap:Body>";
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
    String signingAlgorithm = "SHA1withRSA";
    BaseEnvelopedSignature signer = new BaseEnvelopedSignature(keyInfo,
        signingKey, signingAlgorithm);
    Element signedXML = signer.sign(body);

    Element signature = signedXML.getFirstChildElement("Signature",
        WSConstants.DSIG_NAMESPACE);
    ParsedSignature parsedSignature = new ParsedSignature(signature);
    String signatureValueB64 = parsedSignature.getSignatureValue();
    String expectedSignatureValueB64 = "AELmgznEGzXVB7482qIddFzl+gUEbmV+mEdgu1w0S9DYNATiG4FB5xkbCMrZR2Z5nynqFL42bpsjtsTZZsDNXx5Lr1LwQY00tcMamRBqkPxLLz9QKb4mpdv5Z1QOLz+d7IXPpWxI09egMAF52HQ1LMuNeam9kP928K8VznLLwOKiqzrz/6ZXdSZsfWPS5viG1kp3osO6xQVBWQ3jN9xSCKbzWPxB+f+DkFYSLswtFQE/k4sbCVeC54i7aBpT6K1J6c/BWe9EwzJvLfxRjN4iWoVZd4iztWdjfGSe9DGbHJpFfgfDF13msfDhXAUYvbFdQXK/DrUS+nrYwXXBAoI2HQ==";
    assertEquals(expectedSignatureValueB64, signatureValueB64);
    assertEquals(344, signatureValueB64.length());
    byte[] signatureValue = Base64.decode(expectedSignatureValueB64);
    assertEquals(signingKey.getModulus().bitLength(), signatureValue.length * 8);

    ParsedKeyInfo parsedKeyInfo = parsedSignature.getParsedKeyInfo();
    ParsedKeyValue parsedKeyValue = parsedKeyInfo.getParsedKeyValue();
    String modulusB64 = parsedKeyValue.getModulus();
    assertEquals(344, modulusB64.length());

    byte[] digest = parsedSignature.getParsedSignedInfo().getCanonicalBytes();
    // System.out.println(signedXML.toXML());
    String expected = "<dsig:SignedInfo xmlns:dsig=\"http://www.w3.org/2000/09/xmldsig#\"><dsig:CanonicalizationMethod Algorithm=\"http://www.w3.org/2001/10/xml-exc-c14n#\"></dsig:CanonicalizationMethod><dsig:SignatureMethod Algorithm=\"http://www.w3.org/2000/09/xmldsig#rsa-sha1\"></dsig:SignatureMethod><dsig:Reference URI=\"#urn:guid:58824342-832F-C99B-925B-CB0E858E5D65\"><dsig:Transforms><dsig:Transform Algorithm=\"http://www.w3.org/2000/09/xmldsig#enveloped-signature\"></dsig:Transform><dsig:Transform Algorithm=\"http://www.w3.org/2001/10/xml-exc-c14n#\"><ec:InclusiveNamespaces xmlns:ec=\"http://www.w3.org/2001/10/xml-exc-c14n#\" PrefixList=\"wsu xmldap\"></ec:InclusiveNamespaces></dsig:Transform></dsig:Transforms><dsig:DigestMethod Algorithm=\"http://www.w3.org/2000/09/xmldsig#sha1\"></dsig:DigestMethod><dsig:DigestValue>8xRvuAvdu90P9qcwm3kaUNSh4/c=</dsig:DigestValue></dsig:Reference></dsig:SignedInfo>";
    assertEquals(expected, new String(digest));

  }

  public void test1() throws Exception {
    Calendar notBeforeCal = null;
    Calendar notOnOrAfterCal = null;
    Conditions conditions = null;
    String notBeforeString = "2006-09-27T12:58:26Z";
    String notOnOrAfterString = "2006-09-29T12:58:26Z";
    notBeforeCal = XSDDateTime.parse(notBeforeString);
    notOnOrAfterCal = XSDDateTime.parse(notOnOrAfterString);
    conditions = new Conditions(notBeforeCal, notOnOrAfterCal);
    X509Certificate cert = null;
    try {
      cert = org.xmldap.util.XmldapCertsAndKeys.getXmldapCert();
    } catch (CertificateException e) {
      assertTrue(false);
    }
    AsymmetricKeyInfo keyInfo = null;
    keyInfo = new AsymmetricKeyInfo(cert);

    Subject subject = new Subject(keyInfo, Subject.HOLDER_OF_KEY);
    org.xmldap.saml.Attribute given = new org.xmldap.saml.Attribute(
        "givenname",
        "http://schemas.microsoft.com/ws/2005/05/identity/claims/GivenName",
        "Chuck");
    org.xmldap.saml.Attribute sur = new org.xmldap.saml.Attribute("surname",
        "http://schemas.microsoft.com/ws/2005/05/identity/claims/SurName",
        "Mortimore");
    org.xmldap.saml.Attribute email = new org.xmldap.saml.Attribute("email",
        "http://schemas.microsoft.com/ws/2005/05/identity/claims/EmailAddress",
        "cmortspam@gmail.com");
    AttributeStatement statement = new AttributeStatement();
    statement.setSubject(subject);
    statement.addAttribute(given);
    statement.addAttribute(sur);
    statement.addAttribute(email);

    RandomGUID guidGen = new RandomGUID();
    SAMLAssertion assertion = new SAMLAssertion(guidGen);
    assertion.setConditions(conditions);
    assertion.setAttributeStatement(statement);
    assertion.setIssueInstant(XSDDateTime.parse("2007-03-12T12:19:01Z"));

    RSAPrivateKey signingKey = org.xmldap.util.XmldapCertsAndKeys
        .getXmldapPrivateKey();

    String signingAlgorithm = "SHA1withRSA";
    BaseEnvelopedSignature signer = new BaseEnvelopedSignature(keyInfo,
        signingKey, signingAlgorithm);
    Element signedXML = signer.sign(assertion.serialize());

    /*
     * <saml:Assertion xmlns:saml="urn:oasis:names:tc:SAML:1.0:assertion"
     * AssertionID="uuid-243B903B-5919-3579-A483-6176822A0422"
     * Issuer="http://schemas.microsoft.com/ws/2005/05/identity/issuer/self"
     * IssueInstant="2007-03-12T12:19:01Z" MajorVersion="1"
     * MinorVersion="1"><saml:Conditions NotBefore="2006-09-27T12:58:26Z"
     * NotOnOrAfter="2006-09-29T12:58:26Z"
     * /><saml:AttributeStatement><saml:Subject
     * ><saml:SubjectConfirmation><saml:ConfirmationMethod
     * >urn:oasis:names:tc:SAML
     * :1.0:cm:holder-of-key</saml:ConfirmationMethod><dsig:KeyInfo
     * xmlns:dsig="http://www.w3.org/2000/09/xmldsig#"><dsig:KeyName>Public Key
     * for CN=xmldap.org, OU=infocard, O=xmldap, L=San Francisco, ST=California,
     * C=US</dsig:KeyName><dsig:KeyValue><dsig:RSAKeyValue><dsig:Modulus>
     * ANMnkVA4xfpG0bLos9FOpNBjHAdFahy2cJ7FUwuXd
     * /IShnG+5qF/z1SdPWzRxTtpFFyodtXlBUEIbiT
     * +IbYPZF1vCcBrcFa8Kz/4rBjrpPZgllgA/WSVKjnJvw8q4
     * /tO6CQZSlRlj/ebNK9VyT1kN+MrKV1SGTqaIJ2l
     * +7Rd05WHscwZMPdVWBbRrg76YTfy6H/NlQIArNLZanPvE0Vd5QfD4ZyG2hTh3y7ZlJAUndGJ
     * /kfZw8sKuL9QSrh4eOTc280NQUmPGz6LP5MXNmu0RxEcomod1
     * +ToKll90yEKFAUKuPYFgm9J+vYm4tzRequLy
     * /njteRIkcfAdcAtt6PCYjU=</dsig:Modulus>
     * <dsig:Exponent>AQAB</dsig:Exponent><
     * /dsig:RSAKeyValue></dsig:KeyValue><dsig
     * :X509Data><dsig:X509Certificate>MIIDXTCCAkUCBEQd+
     * 4EwDQYJKoZIhvcNAQEEBQAwczELMAkGA1UEBhMCVVMxEzARBgNVBAgTCkNhbGlmb3JuaWExFjAUBgNVBAcTDVNhbiBGcmFuY2lzY28xDzANBgNVBAoTBnhtbGRhcDERMA8GA1UECxMIaW5mb2NhcmQxEzARBgNVBAMTCnhtbGRhcC5vcmcwHhcNMDYwMzIwMDA0NjU3WhcNMDYwNjE4MDA0NjU3WjBzMQswCQYDVQQGEwJVUzETMBEGA1UECBMKQ2FsaWZvcm5pYTEWMBQGA1UEBxMNU2FuIEZyYW5jaXNjbzEPMA0GA1UEChMGeG1sZGFwMREwDwYDVQQLEwhpbmZvY2FyZDETMBEGA1UEAxMKeG1sZGFwLm9yZzCCASIwDQYJKoZIhvcNAQEBBQADggEPADCCAQoCggEBANMnkVA4xfpG0bLos9FOpNBjHAdFahy2cJ7FUwuXd/IShnG+5qF/z1SdPWzRxTtpFFyodtXlBUEIbiT+IbYPZF1vCcBrcFa8Kz/4rBjrpPZgllgA/WSVKjnJvw8q4/tO6CQZSlRlj/ebNK9VyT1kN+MrKV1SGTqaIJ2l+7Rd05WHscwZMPdVWBbRrg76YTfy6H/NlQIArNLZanPvE0Vd5QfD4ZyG2hTh3y7ZlJAUndGJ/kfZw8sKuL9QSrh4eOTc280NQUmPGz6LP5MXNmu0RxEcomod1+ToKll90yEKFAUKuPYFgm9J+vYm4tzRequLy/njteRIkcfAdcAtt6PCYjUCAwEAATANBgkqhkiG9w0BAQQFAAOCAQEAURtxiA7qDSq/WlUpWpfWiZ7HvveQrwTaTwV/Fk3l/I9e9WIRN51uFLuiLtZMMwR02BX7Yva1KQ/Gl999cm/0b5hptJ+TU29rVPZIlI32c5vjcuSVoEda8+BRj547jlC0rNokyWm+YtBcDOwfHSPFFwVPPVxyQsVEebsiB6KazFq6iZ8A0F2HLEnpsdFnGrSwBBbH3I3PH65ofrTTgj1Mjk5kA6EVaeefDCtlkX2ogIFMlcS6ruihX2mlCLUSrlPs9TH+M4j/R/LV5QWJ93/X9gsxFrxVFGg3b75EKQP8MZ111/jaeKd80mUOAiTO06EtfjXZPrjPN4e2l05i2EGDUA==</dsig:X509Certificate></dsig:X509Data></dsig:KeyInfo></saml:SubjectConfirmation></saml:Subject><saml:Attribute
     * AttributeName="givenname"AttributeNamespace=
     * "http://schemas.microsoft.com/ws/2005/05/identity/claims/GivenName"
     * ><saml:
     * AttributeValue>Chuck</saml:AttributeValue></saml:Attribute><saml:Attribute
     * AttributeName="surname"AttributeNamespace=
     * "http://schemas.microsoft.com/ws/2005/05/identity/claims/SurName"
     * ><saml:AttributeValue
     * >Mortimore</saml:AttributeValue></saml:Attribute><saml:Attribute
     * AttributeName="email"AttributeNamespace=
     * "http://schemas.microsoft.com/ws/2005/05/identity/claims/EmailAddress"
     * ><saml
     * :AttributeValue>cmortspam@gmail.com</saml:AttributeValue></saml:Attribute
     * ></saml:AttributeStatement><dsig:Signature
     * xmlns:dsig="http://www.w3.org/2000/09/xmldsig#"
     * ><dsig:SignedInfo><dsig:CanonicalizationMethod
     * Algorithm="http://www.w3.org/2001/10/xml-exc-c14n#"
     * /><dsig:SignatureMethod
     * Algorithm="http://www.w3.org/2000/09/xmldsig#rsa-sha1" /><dsig:Reference
     * URI="#uuid-243B903B-5919-3579-A483-6176822A0422"><dsig:Transforms><dsig:
     * Transform
     * Algorithm="http://www.w3.org/2000/09/xmldsig#enveloped-signature"
     * /><dsig:Transform Algorithm="http://www.w3.org/2001/10/xml-exc-c14n#"
     * /></dsig:Transforms><dsig:DigestMethod
     * Algorithm="http://www.w3.org/2000/09/xmldsig#sha1"
     * /><dsig:DigestValue>rusgiCaSNXammM1p
     * +30Zriq5yVs=</dsig:DigestValue></dsig:
     * Reference></dsig:SignedInfo><dsig:SignatureValue>S4o94Gt/
     * PXUF2Lfdfo0aIn3PsKlN21lFWtyiwruosLty0bErUIQo4XksbqBxKT8M9xmYn57DZUyuS2om5HiR09pH6O133TisD0cfS3
     * /F5g212nG8Wy5zg/zVnwldf93lLBmEBME/Hj04GxlT7Sy9qMWm35LmGN5Pz/tlQ4bC8F4CD/
     * S8r3tqoZPuHb6AVZ93aEZSgWAK9XsU8r+Ppy9GWf+4D7tLGU0LO/
     * K2iJ6954s4j31brlv8ONFmaReFog47CyzV1cIH1qH1o8TnI765uwxY8NtPmXdAfU38ZJs4kNyzLB4ZVUXmQ
     * /
     * 6Nczh3gRKAL0LoBQ3yEfU45G55IdZJNA==</dsig:SignatureValue><dsig:KeyInfo><dsig
     * :KeyName>Public Key for CN=xmldap.org, OU=infocard, O=xmldap, L=San
     * Francisco, ST=California,
     * C=US</dsig:KeyName><dsig:KeyValue><dsig:RSAKeyValue
     * ><dsig:Modulus>ANMnkVA4xfpG0bLos9FOpNBjHAdFahy2cJ7FUwuXd
     * /IShnG+5qF/z1SdPWzRxTtpFFyodtXlBUEIbiT
     * +IbYPZF1vCcBrcFa8Kz/4rBjrpPZgllgA/WSVKjnJvw8q4
     * /tO6CQZSlRlj/ebNK9VyT1kN+MrKV1SGTqaIJ2l
     * +7Rd05WHscwZMPdVWBbRrg76YTfy6H/NlQIArNLZanPvE0Vd5QfD4ZyG2hTh3y7ZlJAUndGJ
     * /kfZw8sKuL9QSrh4eOTc280NQUmPGz6LP5MXNmu0RxEcomod1
     * +ToKll90yEKFAUKuPYFgm9J+vYm4tzRequLy
     * /njteRIkcfAdcAtt6PCYjU=</dsig:Modulus>
     * <dsig:Exponent>AQAB</dsig:Exponent><
     * /dsig:RSAKeyValue></dsig:KeyValue><dsig
     * :X509Data><dsig:X509Certificate>MIIDXTCCAkUCBEQd+
     * 4EwDQYJKoZIhvcNAQEEBQAwczELMAkGA1UEBhMCVVMxEzARBgNVBAgTCkNhbGlmb3JuaWExFjAUBgNVBAcTDVNhbiBGcmFuY2lzY28xDzANBgNVBAoTBnhtbGRhcDERMA8GA1UECxMIaW5mb2NhcmQxEzARBgNVBAMTCnhtbGRhcC5vcmcwHhcNMDYwMzIwMDA0NjU3WhcNMDYwNjE4MDA0NjU3WjBzMQswCQYDVQQGEwJVUzETMBEGA1UECBMKQ2FsaWZvcm5pYTEWMBQGA1UEBxMNU2FuIEZyYW5jaXNjbzEPMA0GA1UEChMGeG1sZGFwMREwDwYDVQQLEwhpbmZvY2FyZDETMBEGA1UEAxMKeG1sZGFwLm9yZzCCASIwDQYJKoZIhvcNAQEBBQADggEPADCCAQoCggEBANMnkVA4xfpG0bLos9FOpNBjHAdFahy2cJ7FUwuXd/IShnG+5qF/z1SdPWzRxTtpFFyodtXlBUEIbiT+IbYPZF1vCcBrcFa8Kz/4rBjrpPZgllgA/WSVKjnJvw8q4/tO6CQZSlRlj/ebNK9VyT1kN+MrKV1SGTqaIJ2l+7Rd05WHscwZMPdVWBbRrg76YTfy6H/NlQIArNLZanPvE0Vd5QfD4ZyG2hTh3y7ZlJAUndGJ/kfZw8sKuL9QSrh4eOTc280NQUmPGz6LP5MXNmu0RxEcomod1+ToKll90yEKFAUKuPYFgm9J+vYm4tzRequLy/njteRIkcfAdcAtt6PCYjUCAwEAATANBgkqhkiG9w0BAQQFAAOCAQEAURtxiA7qDSq/WlUpWpfWiZ7HvveQrwTaTwV/Fk3l/I9e9WIRN51uFLuiLtZMMwR02BX7Yva1KQ/Gl999cm/0b5hptJ+TU29rVPZIlI32c5vjcuSVoEda8+BRj547jlC0rNokyWm+YtBcDOwfHSPFFwVPPVxyQsVEebsiB6KazFq6iZ8A0F2HLEnpsdFnGrSwBBbH3I3PH65ofrTTgj1Mjk5kA6EVaeefDCtlkX2ogIFMlcS6ruihX2mlCLUSrlPs9TH+M4j/R/LV5QWJ93/X9gsxFrxVFGg3b75EKQP8MZ111/jaeKd80mUOAiTO06EtfjXZPrjPN4e2l05i2EGDUA==</dsig:X509Certificate></dsig:X509Data></dsig:KeyInfo></dsig:Signature></saml:Assertion>
     */
    // assertEquals(signedXML.toXML(), "");
    Element signature = signedXML.getFirstChildElement("Signature",
        WSConstants.DSIG_NAMESPACE);
    ParsedSignature parsedSignature = new ParsedSignature(signature);
    ParsedKeyInfo parsedKeyInfo = parsedSignature.getParsedKeyInfo();
    ParsedKeyValue parsedKeyValue = parsedKeyInfo.getParsedKeyValue();
    String modulusB64 = parsedKeyValue.getModulus();
    // assertEquals(344, modulusB64.length());
    String signatureValueB64 = parsedSignature.getSignatureValue();
    // assertEquals(344, signatureValueB64.length());
    assertEquals(modulusB64.length(), signatureValueB64.length());

    byte[] digest = parsedSignature.getParsedSignedInfo().getCanonicalBytes();
    // System.out.println(signedXML.toXML());
    Document doc = new Document(signedXML);

    XPathContext xPathContext = new XPathContext();
    xPathContext.addNamespace("dsig", WSConstants.DSIG_NAMESPACE);
    xPathContext.addNamespace(WSConstants.SAML_PREFIX,
        WSConstants.SAML11_NAMESPACE);

    Nodes nodes0 = doc.query("*[@AssertionID]", xPathContext);
    assertEquals(1, nodes0.size());
    Element node = (Element) nodes0.get(0);
    String uuid = node.getAttribute("AssertionID").getValue();
    assertEquals("uuid-" + guidGen.toString(), uuid);
    // Nodes nodes1 = doc.query("*[@AssertionID = uuid-"+guidGen.toString()+"]",
    // xPathContext);
    // this currently fails. Please correct the above query. Axel
    // assertEquals(1, nodes1.size());

  }

}
