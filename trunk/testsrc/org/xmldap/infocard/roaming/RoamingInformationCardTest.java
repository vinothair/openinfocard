package org.xmldap.infocard.roaming;


import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.junit.Before;
import org.xmldap.exceptions.ParsingException;
import org.xmldap.exceptions.SerializationException;
import org.xmldap.infocard.InfoCard;
import org.xmldap.infocard.TokenServiceReference;
import org.xmldap.infocard.UserCredential;
import org.xmldap.infocard.policy.SupportedClaim;
import org.xmldap.infocard.policy.SupportedClaimTypeList;
import org.xmldap.infocard.policy.SupportedToken;
import org.xmldap.infocard.policy.SupportedTokenList;
import org.xmldap.util.XmldapCertsAndKeys;
import org.xmldap.ws.WSConstants;

public class RoamingInformationCardTest extends TestCase {
	InfoCard card; 
	RoamingInformationCard ric;
	
	@Before
	public void setUp() throws Exception {

		{
			card = new InfoCard();
			card.setCardId("card1", 1);
			card.setIssuer("issuer");
			card.setTimeIssued("2006-09-28T12:58:26Z");
			{
				String displayName = "displayName";
				String uri = "uri";
				String description = "description";
				SupportedClaim claim = new SupportedClaim(displayName, uri, description);
				ArrayList<SupportedClaim> cl = new ArrayList<SupportedClaim>();
				cl.add(claim);
				SupportedClaimTypeList claimList = new SupportedClaimTypeList(cl);
				card.setClaimList(claimList);
			}
			{
				SupportedToken token = new SupportedToken(WSConstants.SAML11_NAMESPACE); // default is SAML11
				List<SupportedToken> list = new ArrayList<SupportedToken>();
				list.add(token);
				SupportedTokenList tokenList = new SupportedTokenList(list);
				card.setTokenList(tokenList);
			}
			{
				X509Certificate cert = XmldapCertsAndKeys.getXmldapCert1();
				UserCredential usercredential = new UserCredential(UserCredential.USERNAME, "username");
				TokenServiceReference tsr = new TokenServiceReference("sts", "mex", cert, usercredential);
				List<TokenServiceReference> tokenServiceReference = new ArrayList<TokenServiceReference>();
				tokenServiceReference.add(tsr);
				card.setTokenServiceReference(tokenServiceReference);
			}
			card.setPrivacyPolicy("privacyPolicyUrl", 1);
		}
		{
			String issuerName = card.getIssuer();
        	String hashSalt = "hashsalt";
        	String timeLastUpdated = "2009-04-27T19:39:19.6053152Z";
        	String issuerId = "";
        	String backgroundColor = "16777215";

			InformationCardMetaData informationCardMetaData = 
				new InformationCardMetaData(card, false, null, hashSalt, timeLastUpdated, issuerId, issuerName, backgroundColor);
			String masterKeyBase64 ="masterkeybytes";
			InformationCardPrivateData informationCardPrivateData = new ManagedInformationCardPrivateData(masterKeyBase64);
			ric = new RoamingInformationCard(informationCardMetaData, informationCardPrivateData);
		}
	}

	public void testRoamingInformationCardToXml() throws SerializationException {
		String expected = "<RoamingInformationCard xmlns=\"http://schemas.xmlsoap.org/ws/2005/05/identity\"><InformationCardMetaData xmlns:ds=\"http://www.w3.org/2000/09/xmldsig#\" xmlns:ic=\"http://schemas.xmlsoap.org/ws/2005/05/identity\" xmlns:mex=\"http://schemas.xmlsoap.org/ws/2004/09/mex\" xmlns:wsa=\"http://www.w3.org/2005/08/addressing\" xmlns:wsid=\"http://schemas.xmlsoap.org/ws/2006/02/addressingidentity\" xmlns:wst=\"http://schemas.xmlsoap.org/ws/2005/02/trust\" xml:lang=\"en\"><ic:InformationCardReference><ic:CardId>card1</ic:CardId><ic:CardVersion>1</ic:CardVersion></ic:InformationCardReference><ic:Issuer>issuer</ic:Issuer><ic:TimeIssued>2006-09-28T12:58:26Z</ic:TimeIssued><ic:TokenServiceList><ic:TokenService><wsa:EndpointReference><wsa:Address>sts</wsa:Address><wsa:Metadata><mex:Metadata><mex:MetadataSection><mex:MetadataReference><wsa:Address>mex</wsa:Address></mex:MetadataReference></mex:MetadataSection></mex:Metadata></wsa:Metadata><wsid:Identity><ds:KeyInfo><ds:X509Data><ds:X509Certificate>MIIDkDCCAvmgAwIBAgIJAO+Fcd4yj0h/MA0GCSqGSIb3DQEBBQUAMIGNMQswCQYDVQQGEwJVUzETMBEGA1UECBMKQ2FsaWZvcm5pYTEWMBQGA1UEBxMNU2FuIEZyYW5jaXNjbzEPMA0GA1UEChMGeG1sZGFwMScwJQYDVQQLFB5DaHVjayBNb3J0aW1vcmUgJiBBeGVsIE5lbm5rZXIxFzAVBgNVBAMTDnd3dy54bWxkYXAub3JnMB4XDTA3MDgxODIxMTIzMVoXDTE3MDgxNTIxMTIzMVowgY0xCzAJBgNVBAYTAlVTMRMwEQYDVQQIEwpDYWxpZm9ybmlhMRYwFAYDVQQHEw1TYW4gRnJhbmNpc2NvMQ8wDQYDVQQKEwZ4bWxkYXAxJzAlBgNVBAsUHkNodWNrIE1vcnRpbW9yZSAmIEF4ZWwgTmVubmtlcjEXMBUGA1UEAxMOd3d3LnhtbGRhcC5vcmcwgZ8wDQYJKoZIhvcNAQEBBQADgY0AMIGJAoGBAOKUn6/QqTZj/BWoQVxNFI0Z2AXI1azws+RyuJek60NiawQrFAKk0Ph+/YnUiQAnzbsT+juZV08UpaPa2IE3g0+RFZtODlqoGGGakSOd9NNnDuNhsdtXJWgQq8paM9Sc4nUue31iq7LvmjSGSL5w84NglT48AcqVGr+/5vy8CfT/AgMBAAGjgfUwgfIwHQYDVR0OBBYEFGcwQKLQtW8/Dql5t70BfXX66dmaMIHCBgNVHSMEgbowgbeAFGcwQKLQtW8/Dql5t70BfXX66dmaoYGTpIGQMIGNMQswCQYDVQQGEwJVUzETMBEGA1UECBMKQ2FsaWZvcm5pYTEWMBQGA1UEBxMNU2FuIEZyYW5jaXNjbzEPMA0GA1UEChMGeG1sZGFwMScwJQYDVQQLFB5DaHVjayBNb3J0aW1vcmUgJiBBeGVsIE5lbm5rZXIxFzAVBgNVBAMTDnd3dy54bWxkYXAub3JnggkA74Vx3jKPSH8wDAYDVR0TBAUwAwEB/zANBgkqhkiG9w0BAQUFAAOBgQAYQisGgrg1xw0TTgIZcz3JXr+ZtwjeKqEewoxCxBz1uki7hJYHIznEZq4fzSMtcBMgbKmOTzFNV0Yr/tnJ9rrljRf8EXci62ffzj+Kkny7JtM6Ltxq0BJuF3jrXogdbsc5J3W9uJ7C2+uJTHG1mApbOdJGvLAGLCaNw5NpP7+ZXQ==</ds:X509Certificate></ds:X509Data></ds:KeyInfo></wsid:Identity></wsa:EndpointReference><ic:UserCredential><ic:DisplayCredentialHint>Please enter your username and password.</ic:DisplayCredentialHint><ic:UsernamePasswordCredential><ic:Username>username</ic:Username></ic:UsernamePasswordCredential></ic:UserCredential></ic:TokenService></ic:TokenServiceList><ic:SupportedTokenTypeList><wst:TokenType>urn:oasis:names:tc:SAML:1.0:assertion</wst:TokenType></ic:SupportedTokenTypeList><ic:SupportedClaimTypeList><ic:SupportedClaimType Uri=\"uri\"><ic:DisplayTag>displayName</ic:DisplayTag><ic:Description>description</ic:Description></ic:SupportedClaimType></ic:SupportedClaimTypeList><ic:PrivacyNotice Version=\"1\">privacyPolicyUrl</ic:PrivacyNotice><ic07:RequireStrongRecipientIdentity xmlns:ic07=\"http://schemas.xmlsoap.org/ws/2007/01/identity\" /><IsSelfIssued>false</IsSelfIssued><HashSalt>hashsalt</HashSalt><TimeLastUpdated>2009-04-27T19:39:19.6053152Z</TimeLastUpdated><IssuerId /><IssuerName>issuer</IssuerName><BackgroundColor>16777215</BackgroundColor></InformationCardMetaData><InformationCardPrivateData><MasterKey>masterkeybytes</MasterKey></InformationCardPrivateData></RoamingInformationCard>";
		assertEquals(expected, ric.toXML());
	}
	
	public void testRoamingInformationCardToInfocard() throws ParsingException, SerializationException {
	  InfoCard cc = new InfoCard(ric);
//	  assertEquals(card.toXML(), cc.toXML());
	  
    assertEquals(card.getCardId(), cc.getCardId());
    assertEquals(card.getCardName(), cc.getCardName());
    assertEquals(card.getCardType(), cc.getCardType());
    assertEquals(card.getCardVersion(), cc.getCardVersion());
    assertEquals(card.getIssuer(), cc.getIssuer());
    assertEquals(card.getPrivacyPolicyVersion(), cc.getPrivacyPolicyVersion());
    assertEquals(card.getRequireStrongRecipientIdentity(), cc.getRequireStrongRecipientIdentity());
    assertEquals(card.getRequireAppliesTo(), cc.getRequireAppliesTo());
    assertEquals(card.getMimeType(), cc.getMimeType());
    assertEquals(card.getRequireStrongRecipientIdentity(), cc.getRequireStrongRecipientIdentity());
    assertEquals(card.getRequireStrongRecipientIdentity(), cc.getRequireStrongRecipientIdentity());
    assertEquals(card.getRequireStrongRecipientIdentity(), cc.getRequireStrongRecipientIdentity());
	}
	
}
