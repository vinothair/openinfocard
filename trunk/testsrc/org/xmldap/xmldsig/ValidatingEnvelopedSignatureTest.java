package org.xmldap.xmldsig;

import java.io.IOException;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPrivateKey;
import java.util.ArrayList;
import java.util.List;

import nu.xom.Document;
import nu.xom.Element;
import nu.xom.ParsingException;

import org.xmldap.exceptions.CryptoException;
import org.xmldap.exceptions.SerializationException;
import org.xmldap.infocard.InfoCard;
import org.xmldap.infocard.TokenServiceReference;
import org.xmldap.infocard.UserCredential;
import org.xmldap.infocard.policy.SupportedClaim;
import org.xmldap.infocard.policy.SupportedClaimTypeList;
import org.xmldap.infocard.policy.SupportedToken;
import org.xmldap.infocard.policy.SupportedTokenList;
import org.xmldap.ws.WSConstants;
import org.xmldap.xml.XmlUtils;

import junit.framework.TestCase;
import org.junit.Test;

public class ValidatingEnvelopedSignatureTest extends TestCase {
	InfoCard invalidCard = null;
	InfoCard validCard = null;

	protected void setUp() throws Exception {
		super.setUp();

		X509Certificate invalidCert = org.xmldap.util.XmldapCertsAndKeys.getXmldapCert();
		RSAPrivateKey privateKey = org.xmldap.util.XmldapCertsAndKeys.getXmldapPrivateKey();
		X509Certificate[] invalidCertChain = {invalidCert};
		
		invalidCard = new InfoCard(invalidCertChain, privateKey);

        invalidCard.setCardId("invalidCard", 1);
		invalidCard.setIssuer("issuer with invalid cert");
		invalidCard.setTimeIssued("2006-09-28T12:58:26Z");
		{
	        ArrayList<SupportedClaim> cl = new ArrayList<SupportedClaim>();
			String displayName = "displayName";
			String uri = "uri";
			String description = "description";
			SupportedClaim claim = new SupportedClaim(displayName, uri, description);
			cl.add(claim);
			SupportedClaimTypeList claimList = new SupportedClaimTypeList(cl);
			invalidCard.setClaimList(claimList);
		}
		{
			SupportedToken token = new SupportedToken(WSConstants.SAML11_NAMESPACE); // default is SAML11
			List<SupportedToken> list = new ArrayList<SupportedToken>();
			list.add(token);
			SupportedTokenList tokenList = new SupportedTokenList(list);
			invalidCard.setTokenList(tokenList);
		}

		X509Certificate validCert = org.xmldap.util.XmldapCertsAndKeys.getXmldapCert1();
		RSAPrivateKey privateKey1 = org.xmldap.util.XmldapCertsAndKeys.getXmldapPrivateKey1();
		X509Certificate[] validCertChain = {validCert};
		
		validCard = new InfoCard(validCertChain, privateKey1);

		validCard.setCardId("validCard", 1);
		validCard.setIssuer("issuer with valid cert");
		validCard.setTimeIssued("2006-09-28T12:58:26Z");
		{
	        ArrayList<SupportedClaim> cl = new ArrayList<SupportedClaim>();
			String displayName = "displayName";
			String uri = "uri";
			String description = "description";
			SupportedClaim claim = new SupportedClaim(displayName, uri, description);
			cl.add(claim);
			SupportedClaimTypeList claimList = new SupportedClaimTypeList(cl);
			validCard.setClaimList(claimList);
		}
		{
			SupportedToken token = new SupportedToken(WSConstants.SAML11_NAMESPACE); // default is SAML11
			List<SupportedToken> list = new ArrayList<SupportedToken>();
			list.add(token);
			SupportedTokenList tokenList = new SupportedTokenList(list);
			validCard.setTokenList(tokenList);
		}
    {
      ArrayList<TokenServiceReference> tokenServiceReferenceList = new ArrayList<TokenServiceReference>();
      String address = "http://sts.example.com/";
      String mexAddress = "https://mex.example.com/";
      X509Certificate cert = null;
      UserCredential userCredential = new UserCredential(UserCredential.USERNAME, "username");
      TokenServiceReference tsr = new TokenServiceReference(address, mexAddress, cert, userCredential);
      tokenServiceReferenceList.add(tsr);
      validCard.setTokenServiceReference(tokenServiceReferenceList);
    }
	}

	@Test(expected=java.security.cert.CertificateExpiredException.class)
	public void testSignatureWithInvalidCert() throws IOException, ParsingException, org.xmldap.exceptions.ParsingException, SerializationException {
		String invalidSignedCard = invalidCard.toXML();
		String expectedSignature = "<dsig:Signature xmlns:dsig=\"http://www.w3.org/2000/09/xmldsig#\"><dsig:SignedInfo><dsig:CanonicalizationMethod Algorithm=\"http://www.w3.org/2001/10/xml-exc-c14n#\" /><dsig:SignatureMethod Algorithm=\"http://www.w3.org/2000/09/xmldsig#rsa-sha1\" /><dsig:Reference URI=\"#_Object_InfoCard\"><dsig:Transforms><dsig:Transform Algorithm=\"http://www.w3.org/2001/10/xml-exc-c14n#\" /></dsig:Transforms><dsig:DigestMethod Algorithm=\"http://www.w3.org/2000/09/xmldsig#sha1\" /><dsig:DigestValue>lmYembabW/01mfqEapHj54siliM=</dsig:DigestValue></dsig:Reference></dsig:SignedInfo><dsig:SignatureValue>rJDObuTZ3l3pISkc2B6aSVnNLzcOMuDNAmIYcCh2eE8Ce8Zdx2GgJnmWyFv+YGhfIJr0r4I1aTeeLkCVobRf6CIzmVejDh3TQnV4YQo+9ZXhomc29Y+EkHNtwyOAkp0IIEaLgAQOcjYNNOCA/O+wI+dhQ4xa3KXcSCIk0iDHCK+8uS3ksBdQq/wOpuy8nhRDNajQb+v9AQr+nw6XVmjXgcmJL+INbhg+sl8tFWvG0iEESWT5HXxzWAQShNfEHMG2j9cQOci7Kbo/fEX6KiDSGt6uvJTxtBSoUddYXJv+eON2qIQAGPFrsd5c4F6rTKq0cL8MMZotwIJBiFUjtK0Fuw==</dsig:SignatureValue><dsig:KeyInfo><dsig:X509Data><dsig:X509Certificate>MIIDXTCCAkUCBEQd+4EwDQYJKoZIhvcNAQEEBQAwczELMAkGA1UEBhMCVVMxEzARBgNVBAgTCkNhbGlmb3JuaWExFjAUBgNVBAcTDVNhbiBGcmFuY2lzY28xDzANBgNVBAoTBnhtbGRhcDERMA8GA1UECxMIaW5mb2NhcmQxEzARBgNVBAMTCnhtbGRhcC5vcmcwHhcNMDYwMzIwMDA0NjU3WhcNMDYwNjE4MDA0NjU3WjBzMQswCQYDVQQGEwJVUzETMBEGA1UECBMKQ2FsaWZvcm5pYTEWMBQGA1UEBxMNU2FuIEZyYW5jaXNjbzEPMA0GA1UEChMGeG1sZGFwMREwDwYDVQQLEwhpbmZvY2FyZDETMBEGA1UEAxMKeG1sZGFwLm9yZzCCASIwDQYJKoZIhvcNAQEBBQADggEPADCCAQoCggEBANMnkVA4xfpG0bLos9FOpNBjHAdFahy2cJ7FUwuXd/IShnG+5qF/z1SdPWzRxTtpFFyodtXlBUEIbiT+IbYPZF1vCcBrcFa8Kz/4rBjrpPZgllgA/WSVKjnJvw8q4/tO6CQZSlRlj/ebNK9VyT1kN+MrKV1SGTqaIJ2l+7Rd05WHscwZMPdVWBbRrg76YTfy6H/NlQIArNLZanPvE0Vd5QfD4ZyG2hTh3y7ZlJAUndGJ/kfZw8sKuL9QSrh4eOTc280NQUmPGz6LP5MXNmu0RxEcomod1+ToKll90yEKFAUKuPYFgm9J+vYm4tzRequLy/njteRIkcfAdcAtt6PCYjUCAwEAATANBgkqhkiG9w0BAQQFAAOCAQEAURtxiA7qDSq/WlUpWpfWiZ7HvveQrwTaTwV/Fk3l/I9e9WIRN51uFLuiLtZMMwR02BX7Yva1KQ/Gl999cm/0b5hptJ+TU29rVPZIlI32c5vjcuSVoEda8+BRj547jlC0rNokyWm+YtBcDOwfHSPFFwVPPVxyQsVEebsiB6KazFq6iZ8A0F2HLEnpsdFnGrSwBBbH3I3PH65ofrTTgj1Mjk5kA6EVaeefDCtlkX2ogIFMlcS6ruihX2mlCLUSrlPs9TH+M4j/R/LV5QWJ93/X9gsxFrxVFGg3b75EKQP8MZ111/jaeKd80mUOAiTO06EtfjXZPrjPN4e2l05i2EGDUA==</dsig:X509Certificate></dsig:X509Data></dsig:KeyInfo><dsig:Object Id=\"_Object_InfoCard\"><ic:InformationCard xmlns:ds=\"http://www.w3.org/2000/09/xmldsig#\" xmlns:ic=\"http://schemas.xmlsoap.org/ws/2005/05/identity\" xmlns:mex=\"http://schemas.xmlsoap.org/ws/2004/09/mex\" xmlns:wsa=\"http://www.w3.org/2005/08/addressing\" xmlns:wsid=\"http://schemas.xmlsoap.org/ws/2006/02/addressingidentity\" xmlns:wst=\"http://schemas.xmlsoap.org/ws/2005/02/trust\" xml:lang=\"en-us\"><ic:InformationCardReference><ic:CardId>invalidCard</ic:CardId><ic:CardVersion>1</ic:CardVersion></ic:InformationCardReference><ic:Issuer>issuer with invalid cert</ic:Issuer><ic:TimeIssued>2006-09-28T12:58:26Z</ic:TimeIssued><ic:SupportedTokenTypeList><wst:TokenType>urn:oasis:names:tc:SAML:1.0:assertion</wst:TokenType></ic:SupportedTokenTypeList><ic:SupportedClaimTypeList><ic:SupportedClaimType Uri=\"uri\"><ic:DisplayTag>displayName</ic:DisplayTag><ic:Description>description</ic:Description></ic:SupportedClaimType></ic:SupportedClaimTypeList><ic07:RequireStrongRecipientIdentity xmlns:ic07=\"http://schemas.xmlsoap.org/ws/2007/01/identity\" /></ic:InformationCard></dsig:Object></dsig:Signature>";
		assertEquals(expectedSignature, invalidSignedCard);
		
		Document doc = XmlUtils.parse(invalidSignedCard);
		Element signatureElement = doc.getRootElement();
		ValidatingEnvelopedSignature signature = new ValidatingEnvelopedSignature(signatureElement);
		try {
			signature.validate();
		} catch (CryptoException e) {
			assertTrue(true);
			return;
		}
		fail("Expected CryptoException to be thrown");
	}

  public void testSignatureWithValidCert() throws IOException, ParsingException, org.xmldap.exceptions.ParsingException, SerializationException, CryptoException {
    String validSignedCard = validCard.toXML();
    String expectedSignature = "<dsig:Signature xmlns:dsig=\"http://www.w3.org/2000/09/xmldsig#\"><dsig:SignedInfo><dsig:CanonicalizationMethod Algorithm=\"http://www.w3.org/2001/10/xml-exc-c14n#\" /><dsig:SignatureMethod Algorithm=\"http://www.w3.org/2000/09/xmldsig#rsa-sha1\" /><dsig:Reference URI=\"#_Object_InfoCard\"><dsig:Transforms><dsig:Transform Algorithm=\"http://www.w3.org/2001/10/xml-exc-c14n#\" /></dsig:Transforms><dsig:DigestMethod Algorithm=\"http://www.w3.org/2000/09/xmldsig#sha1\" /><dsig:DigestValue>vMsq1niriwx43Z/SDwp/MgJEaAQ=</dsig:DigestValue></dsig:Reference></dsig:SignedInfo><dsig:SignatureValue>bpGqGtzCC0a5SKofsezUZGhLMUdCxxqzUtYvLLdI+ghLiPUWyvujMfbYq/HMtrk6UobdoQ//ihr3jCk+aIsRSgMHgT6a/71FvDdQobuSuPJD4x2vHKI88xM6tSQEqjA/4cOEvjsTLo4hMNAxUI1/zcfRrKGCjUUzFptONWB3X+s=</dsig:SignatureValue><dsig:KeyInfo><dsig:X509Data><dsig:X509Certificate>MIIDkDCCAvmgAwIBAgIJAO+Fcd4yj0h/MA0GCSqGSIb3DQEBBQUAMIGNMQswCQYDVQQGEwJVUzETMBEGA1UECBMKQ2FsaWZvcm5pYTEWMBQGA1UEBxMNU2FuIEZyYW5jaXNjbzEPMA0GA1UEChMGeG1sZGFwMScwJQYDVQQLFB5DaHVjayBNb3J0aW1vcmUgJiBBeGVsIE5lbm5rZXIxFzAVBgNVBAMTDnd3dy54bWxkYXAub3JnMB4XDTA3MDgxODIxMTIzMVoXDTE3MDgxNTIxMTIzMVowgY0xCzAJBgNVBAYTAlVTMRMwEQYDVQQIEwpDYWxpZm9ybmlhMRYwFAYDVQQHEw1TYW4gRnJhbmNpc2NvMQ8wDQYDVQQKEwZ4bWxkYXAxJzAlBgNVBAsUHkNodWNrIE1vcnRpbW9yZSAmIEF4ZWwgTmVubmtlcjEXMBUGA1UEAxMOd3d3LnhtbGRhcC5vcmcwgZ8wDQYJKoZIhvcNAQEBBQADgY0AMIGJAoGBAOKUn6/QqTZj/BWoQVxNFI0Z2AXI1azws+RyuJek60NiawQrFAKk0Ph+/YnUiQAnzbsT+juZV08UpaPa2IE3g0+RFZtODlqoGGGakSOd9NNnDuNhsdtXJWgQq8paM9Sc4nUue31iq7LvmjSGSL5w84NglT48AcqVGr+/5vy8CfT/AgMBAAGjgfUwgfIwHQYDVR0OBBYEFGcwQKLQtW8/Dql5t70BfXX66dmaMIHCBgNVHSMEgbowgbeAFGcwQKLQtW8/Dql5t70BfXX66dmaoYGTpIGQMIGNMQswCQYDVQQGEwJVUzETMBEGA1UECBMKQ2FsaWZvcm5pYTEWMBQGA1UEBxMNU2FuIEZyYW5jaXNjbzEPMA0GA1UEChMGeG1sZGFwMScwJQYDVQQLFB5DaHVjayBNb3J0aW1vcmUgJiBBeGVsIE5lbm5rZXIxFzAVBgNVBAMTDnd3dy54bWxkYXAub3JnggkA74Vx3jKPSH8wDAYDVR0TBAUwAwEB/zANBgkqhkiG9w0BAQUFAAOBgQAYQisGgrg1xw0TTgIZcz3JXr+ZtwjeKqEewoxCxBz1uki7hJYHIznEZq4fzSMtcBMgbKmOTzFNV0Yr/tnJ9rrljRf8EXci62ffzj+Kkny7JtM6Ltxq0BJuF3jrXogdbsc5J3W9uJ7C2+uJTHG1mApbOdJGvLAGLCaNw5NpP7+ZXQ==</dsig:X509Certificate></dsig:X509Data></dsig:KeyInfo><dsig:Object Id=\"_Object_InfoCard\"><ic:InformationCard xmlns:ds=\"http://www.w3.org/2000/09/xmldsig#\" xmlns:ic=\"http://schemas.xmlsoap.org/ws/2005/05/identity\" xmlns:mex=\"http://schemas.xmlsoap.org/ws/2004/09/mex\" xmlns:wsa=\"http://www.w3.org/2005/08/addressing\" xmlns:wsid=\"http://schemas.xmlsoap.org/ws/2006/02/addressingidentity\" xmlns:wst=\"http://schemas.xmlsoap.org/ws/2005/02/trust\" xml:lang=\"en-us\"><ic:InformationCardReference><ic:CardId>validCard</ic:CardId><ic:CardVersion>1</ic:CardVersion></ic:InformationCardReference><ic:Issuer>issuer with valid cert</ic:Issuer><ic:TimeIssued>2006-09-28T12:58:26Z</ic:TimeIssued><ic:TokenServiceList><ic:TokenService><wsa:EndpointReference><wsa:Address>http://sts.example.com/</wsa:Address><wsa:Metadata><mex:Metadata><mex:MetadataSection><mex:MetadataReference><wsa:Address>https://mex.example.com/</wsa:Address></mex:MetadataReference></mex:MetadataSection></mex:Metadata></wsa:Metadata></wsa:EndpointReference><ic:UserCredential><ic:DisplayCredentialHint>Please enter your username and password.</ic:DisplayCredentialHint><ic:UsernamePasswordCredential><ic:Username>username</ic:Username></ic:UsernamePasswordCredential></ic:UserCredential></ic:TokenService></ic:TokenServiceList><ic:SupportedTokenTypeList><wst:TokenType>urn:oasis:names:tc:SAML:1.0:assertion</wst:TokenType></ic:SupportedTokenTypeList><ic:SupportedClaimTypeList><ic:SupportedClaimType Uri=\"uri\"><ic:DisplayTag>displayName</ic:DisplayTag><ic:Description>description</ic:Description></ic:SupportedClaimType></ic:SupportedClaimTypeList><ic07:RequireStrongRecipientIdentity xmlns:ic07=\"http://schemas.xmlsoap.org/ws/2007/01/identity\" /></ic:InformationCard></dsig:Object></dsig:Signature>";
    assertEquals(expectedSignature, validSignedCard);
    
    Document doc = XmlUtils.parse(validSignedCard);
    Element signatureElement = doc.getRootElement();
    ValidatingEnvelopedSignature signature = new ValidatingEnvelopedSignature(signatureElement);
    Element elt = signature.validate();
    assertNotNull(elt);
    assertEquals("Object", elt.getLocalName());
    Element infocardElt = elt.getFirstChildElement("InformationCard", WSConstants.INFOCARD_NAMESPACE);
    assertNotNull(infocardElt);
    InfoCard card = new InfoCard(infocardElt);
  }
  
  public void testSignatureWithValidCert1() throws IOException, ParsingException, org.xmldap.exceptions.ParsingException, SerializationException, CryptoException {
    InfoCard vcard = new InfoCard(validCard);
    ArrayList<TokenServiceReference> tokenServiceReferenceList = new ArrayList<TokenServiceReference>();
    String address = "http://sts.example.com/";
    String mexAddress = "https://mex.example.com/";
    X509Certificate cert = null;
    UserCredential userCredential = new UserCredential(UserCredential.USERNAME, "username");
    TokenServiceReference tsr = new TokenServiceReference(address, mexAddress, cert, userCredential);
    tokenServiceReferenceList.add(tsr);
    vcard.setTokenServiceReference(tokenServiceReferenceList);
    String validSignedCard = vcard.toXML();
    String expectedSignature = "<dsig:Signature xmlns:dsig=\"http://www.w3.org/2000/09/xmldsig#\"><dsig:SignedInfo><dsig:CanonicalizationMethod Algorithm=\"http://www.w3.org/2001/10/xml-exc-c14n#\" /><dsig:SignatureMethod Algorithm=\"http://www.w3.org/2000/09/xmldsig#rsa-sha1\" /><dsig:Reference URI=\"#_Object_InfoCard\"><dsig:Transforms><dsig:Transform Algorithm=\"http://www.w3.org/2001/10/xml-exc-c14n#\" /></dsig:Transforms><dsig:DigestMethod Algorithm=\"http://www.w3.org/2000/09/xmldsig#sha1\" /><dsig:DigestValue>vMsq1niriwx43Z/SDwp/MgJEaAQ=</dsig:DigestValue></dsig:Reference></dsig:SignedInfo><dsig:SignatureValue>bpGqGtzCC0a5SKofsezUZGhLMUdCxxqzUtYvLLdI+ghLiPUWyvujMfbYq/HMtrk6UobdoQ//ihr3jCk+aIsRSgMHgT6a/71FvDdQobuSuPJD4x2vHKI88xM6tSQEqjA/4cOEvjsTLo4hMNAxUI1/zcfRrKGCjUUzFptONWB3X+s=</dsig:SignatureValue><dsig:KeyInfo><dsig:X509Data><dsig:X509Certificate>MIIDkDCCAvmgAwIBAgIJAO+Fcd4yj0h/MA0GCSqGSIb3DQEBBQUAMIGNMQswCQYDVQQGEwJVUzETMBEGA1UECBMKQ2FsaWZvcm5pYTEWMBQGA1UEBxMNU2FuIEZyYW5jaXNjbzEPMA0GA1UEChMGeG1sZGFwMScwJQYDVQQLFB5DaHVjayBNb3J0aW1vcmUgJiBBeGVsIE5lbm5rZXIxFzAVBgNVBAMTDnd3dy54bWxkYXAub3JnMB4XDTA3MDgxODIxMTIzMVoXDTE3MDgxNTIxMTIzMVowgY0xCzAJBgNVBAYTAlVTMRMwEQYDVQQIEwpDYWxpZm9ybmlhMRYwFAYDVQQHEw1TYW4gRnJhbmNpc2NvMQ8wDQYDVQQKEwZ4bWxkYXAxJzAlBgNVBAsUHkNodWNrIE1vcnRpbW9yZSAmIEF4ZWwgTmVubmtlcjEXMBUGA1UEAxMOd3d3LnhtbGRhcC5vcmcwgZ8wDQYJKoZIhvcNAQEBBQADgY0AMIGJAoGBAOKUn6/QqTZj/BWoQVxNFI0Z2AXI1azws+RyuJek60NiawQrFAKk0Ph+/YnUiQAnzbsT+juZV08UpaPa2IE3g0+RFZtODlqoGGGakSOd9NNnDuNhsdtXJWgQq8paM9Sc4nUue31iq7LvmjSGSL5w84NglT48AcqVGr+/5vy8CfT/AgMBAAGjgfUwgfIwHQYDVR0OBBYEFGcwQKLQtW8/Dql5t70BfXX66dmaMIHCBgNVHSMEgbowgbeAFGcwQKLQtW8/Dql5t70BfXX66dmaoYGTpIGQMIGNMQswCQYDVQQGEwJVUzETMBEGA1UECBMKQ2FsaWZvcm5pYTEWMBQGA1UEBxMNU2FuIEZyYW5jaXNjbzEPMA0GA1UEChMGeG1sZGFwMScwJQYDVQQLFB5DaHVjayBNb3J0aW1vcmUgJiBBeGVsIE5lbm5rZXIxFzAVBgNVBAMTDnd3dy54bWxkYXAub3JnggkA74Vx3jKPSH8wDAYDVR0TBAUwAwEB/zANBgkqhkiG9w0BAQUFAAOBgQAYQisGgrg1xw0TTgIZcz3JXr+ZtwjeKqEewoxCxBz1uki7hJYHIznEZq4fzSMtcBMgbKmOTzFNV0Yr/tnJ9rrljRf8EXci62ffzj+Kkny7JtM6Ltxq0BJuF3jrXogdbsc5J3W9uJ7C2+uJTHG1mApbOdJGvLAGLCaNw5NpP7+ZXQ==</dsig:X509Certificate></dsig:X509Data></dsig:KeyInfo><dsig:Object Id=\"_Object_InfoCard\"><ic:InformationCard xmlns:ds=\"http://www.w3.org/2000/09/xmldsig#\" xmlns:ic=\"http://schemas.xmlsoap.org/ws/2005/05/identity\" xmlns:mex=\"http://schemas.xmlsoap.org/ws/2004/09/mex\" xmlns:wsa=\"http://www.w3.org/2005/08/addressing\" xmlns:wsid=\"http://schemas.xmlsoap.org/ws/2006/02/addressingidentity\" xmlns:wst=\"http://schemas.xmlsoap.org/ws/2005/02/trust\" xml:lang=\"en-us\"><ic:InformationCardReference><ic:CardId>validCard</ic:CardId><ic:CardVersion>1</ic:CardVersion></ic:InformationCardReference><ic:Issuer>issuer with valid cert</ic:Issuer><ic:TimeIssued>2006-09-28T12:58:26Z</ic:TimeIssued><ic:TokenServiceList><ic:TokenService><wsa:EndpointReference><wsa:Address>http://sts.example.com/</wsa:Address><wsa:Metadata><mex:Metadata><mex:MetadataSection><mex:MetadataReference><wsa:Address>https://mex.example.com/</wsa:Address></mex:MetadataReference></mex:MetadataSection></mex:Metadata></wsa:Metadata></wsa:EndpointReference><ic:UserCredential><ic:DisplayCredentialHint>Please enter your username and password.</ic:DisplayCredentialHint><ic:UsernamePasswordCredential><ic:Username>username</ic:Username></ic:UsernamePasswordCredential></ic:UserCredential></ic:TokenService></ic:TokenServiceList><ic:SupportedTokenTypeList><wst:TokenType>urn:oasis:names:tc:SAML:1.0:assertion</wst:TokenType></ic:SupportedTokenTypeList><ic:SupportedClaimTypeList><ic:SupportedClaimType Uri=\"uri\"><ic:DisplayTag>displayName</ic:DisplayTag><ic:Description>description</ic:Description></ic:SupportedClaimType></ic:SupportedClaimTypeList><ic07:RequireStrongRecipientIdentity xmlns:ic07=\"http://schemas.xmlsoap.org/ws/2007/01/identity\" /></ic:InformationCard></dsig:Object></dsig:Signature>";
    assertEquals(expectedSignature, validSignedCard);
    
    Document doc = XmlUtils.parse(validSignedCard);
    Element signatureElement = doc.getRootElement();
    ValidatingEnvelopedSignature signature = new ValidatingEnvelopedSignature(signatureElement);
    Element objectElt = signature.validate();
    assertNotNull(objectElt);
    Element infocardElt = (Element)objectElt.getChild(0);
    InfoCard card = new InfoCard(infocardElt);
  }

}
