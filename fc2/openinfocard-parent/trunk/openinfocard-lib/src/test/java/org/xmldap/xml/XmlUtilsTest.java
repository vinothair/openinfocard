package org.xmldap.xml;

import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;
import nu.xom.Document;
import nu.xom.Element;

import org.json.JSONObject;
import org.xmldap.infocard.InfoCard;
import org.xmldap.infocard.TokenServiceReference;
import org.xmldap.infocard.UserCredential;
import org.xmldap.infocard.policy.SupportedClaim;
import org.xmldap.infocard.policy.SupportedClaimTypeList;
import org.xmldap.infocard.policy.SupportedToken;
import org.xmldap.infocard.policy.SupportedTokenList;
import org.xmldap.util.Base64;
import org.xmldap.util.XmldapCertsAndKeys;
import org.xmldap.ws.WSConstants;


public class XmlUtilsTest extends TestCase {
  public void setUp() throws Exception {
    super.setUp();
  }

  public void testSimpleToJSON() throws Exception {
    Document doc = XmlUtils.parse("<root><a>root</a></root>");
    Element elt = doc.getRootElement();
    JSONObject json = XmlUtils.toJSON(elt);
    String expected = "{\"QualifiedName\":\"root\",\"Children\":[{\"QualifiedName\":\"a\",\"Children\":[\"root\"],\"LocalName\":\"a\"}],\"LocalName\":\"root\"}";
    assertEquals(expected, json.toString());
  }
  public void testNamespaceToJSON() throws Exception {
    Document doc = XmlUtils.parse("<RNS:root xmlns:RNS=\"urn:rns\"><ANS:a xmlns:ANS=\"urn:ans\">root</ANS:a></RNS:root>");
    Element elt = doc.getRootElement();
    JSONObject json = XmlUtils.toJSON(elt);
    String expected = "{\"NamespaceDeclarations\":[[\"RNS\",\"urn:rns\"]],\"NamespacePrefix\":\"RNS\",\"QualifiedName\":\"RNS:root\",\"NamespaceURI\":\"urn:rns\",\"Children\":[{\"NamespaceDeclarations\":[[\"ANS\",\"urn:ans\"]],\"NamespacePrefix\":\"ANS\",\"QualifiedName\":\"ANS:a\",\"NamespaceURI\":\"urn:ans\",\"Children\":[\"root\"],\"LocalName\":\"a\"}],\"LocalName\":\"root\"}";
    assertEquals(expected, json.toString());
  }
  
  public void testInfocardToJSON() throws Exception {
    InfoCard card = new InfoCard();
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

    JSONObject json = XmlUtils.toJSON(card.serialize());
    String expected = "{\"NamespaceDeclarations\":[[\"ds\",\"http://www.w3.org/2000/09/xmldsig#\"],[\"ic\",\"http://schemas.xmlsoap.org/ws/2005/05/identity\"],[\"mex\",\"http://schemas.xmlsoap.org/ws/2004/09/mex\"],[\"wsa\",\"http://www.w3.org/2005/08/addressing\"],[\"wsid\",\"http://schemas.xmlsoap.org/ws/2006/02/addressingidentity\"],[\"wst\",\"http://schemas.xmlsoap.org/ws/2005/02/trust\"]],\"NamespacePrefix\":\"ic\",\"Attributes\":[{\"NamespacePrefix\":\"xml\",\"Value\":\"en-us\",\"QualifiedName\":\"xml:lang\",\"NamespaceURI\":\"http://www.w3.org/XML/1998/namespace\",\"LocalName\":\"lang\"}],\"QualifiedName\":\"ic:InformationCard\",\"NamespaceURI\":\"http://schemas.xmlsoap.org/ws/2005/05/identity\",\"Children\":[{\"NamespaceDeclarations\":[[\"ic\",\"http://schemas.xmlsoap.org/ws/2005/05/identity\"]],\"NamespacePrefix\":\"ic\",\"QualifiedName\":\"ic:InformationCardReference\",\"NamespaceURI\":\"http://schemas.xmlsoap.org/ws/2005/05/identity\",\"Children\":[{\"NamespaceDeclarations\":[[\"ic\",\"http://schemas.xmlsoap.org/ws/2005/05/identity\"]],\"NamespacePrefix\":\"ic\",\"QualifiedName\":\"ic:CardId\",\"NamespaceURI\":\"http://schemas.xmlsoap.org/ws/2005/05/identity\",\"Children\":[\"card1\"],\"LocalName\":\"CardId\"},{\"NamespaceDeclarations\":[[\"ic\",\"http://schemas.xmlsoap.org/ws/2005/05/identity\"]],\"NamespacePrefix\":\"ic\",\"QualifiedName\":\"ic:CardVersion\",\"NamespaceURI\":\"http://schemas.xmlsoap.org/ws/2005/05/identity\",\"Children\":[\"1\"],\"LocalName\":\"CardVersion\"}],\"LocalName\":\"InformationCardReference\"},{\"NamespaceDeclarations\":[[\"ic\",\"http://schemas.xmlsoap.org/ws/2005/05/identity\"]],\"NamespacePrefix\":\"ic\",\"QualifiedName\":\"ic:Issuer\",\"NamespaceURI\":\"http://schemas.xmlsoap.org/ws/2005/05/identity\",\"Children\":[\"issuer\"],\"LocalName\":\"Issuer\"},{\"NamespaceDeclarations\":[[\"ic\",\"http://schemas.xmlsoap.org/ws/2005/05/identity\"]],\"NamespacePrefix\":\"ic\",\"QualifiedName\":\"ic:TimeIssued\",\"NamespaceURI\":\"http://schemas.xmlsoap.org/ws/2005/05/identity\",\"Children\":[\"2006-09-28T12:58:26Z\"],\"LocalName\":\"TimeIssued\"},{\"NamespaceDeclarations\":[[\"ic\",\"http://schemas.xmlsoap.org/ws/2005/05/identity\"]],\"NamespacePrefix\":\"ic\",\"QualifiedName\":\"ic:TokenServiceList\",\"NamespaceURI\":\"http://schemas.xmlsoap.org/ws/2005/05/identity\",\"Children\":[{\"NamespaceDeclarations\":[[\"ic\",\"http://schemas.xmlsoap.org/ws/2005/05/identity\"]],\"NamespacePrefix\":\"ic\",\"QualifiedName\":\"ic:TokenService\",\"NamespaceURI\":\"http://schemas.xmlsoap.org/ws/2005/05/identity\",\"Children\":[{\"NamespaceDeclarations\":[[\"wsa\",\"http://www.w3.org/2005/08/addressing\"]],\"NamespacePrefix\":\"wsa\",\"QualifiedName\":\"wsa:EndpointReference\",\"NamespaceURI\":\"http://www.w3.org/2005/08/addressing\",\"Children\":[{\"NamespaceDeclarations\":[[\"wsa\",\"http://www.w3.org/2005/08/addressing\"]],\"NamespacePrefix\":\"wsa\",\"QualifiedName\":\"wsa:Address\",\"NamespaceURI\":\"http://www.w3.org/2005/08/addressing\",\"Children\":[\"sts\"],\"LocalName\":\"Address\"},{\"NamespaceDeclarations\":[[\"wsa\",\"http://www.w3.org/2005/08/addressing\"]],\"NamespacePrefix\":\"wsa\",\"QualifiedName\":\"wsa:Metadata\",\"NamespaceURI\":\"http://www.w3.org/2005/08/addressing\",\"Children\":[{\"NamespaceDeclarations\":[[\"mex\",\"http://schemas.xmlsoap.org/ws/2004/09/mex\"]],\"NamespacePrefix\":\"mex\",\"QualifiedName\":\"mex:Metadata\",\"NamespaceURI\":\"http://schemas.xmlsoap.org/ws/2004/09/mex\",\"Children\":[{\"NamespaceDeclarations\":[[\"mex\",\"http://schemas.xmlsoap.org/ws/2004/09/mex\"]],\"NamespacePrefix\":\"mex\",\"QualifiedName\":\"mex:MetadataSection\",\"NamespaceURI\":\"http://schemas.xmlsoap.org/ws/2004/09/mex\",\"Children\":[{\"NamespaceDeclarations\":[[\"mex\",\"http://schemas.xmlsoap.org/ws/2004/09/mex\"]],\"NamespacePrefix\":\"mex\",\"QualifiedName\":\"mex:MetadataReference\",\"NamespaceURI\":\"http://schemas.xmlsoap.org/ws/2004/09/mex\",\"Children\":[{\"NamespaceDeclarations\":[[\"wsa\",\"http://www.w3.org/2005/08/addressing\"]],\"NamespacePrefix\":\"wsa\",\"QualifiedName\":\"wsa:Address\",\"NamespaceURI\":\"http://www.w3.org/2005/08/addressing\",\"Children\":[\"mex\"],\"LocalName\":\"Address\"}],\"LocalName\":\"MetadataReference\"}],\"LocalName\":\"MetadataSection\"}],\"LocalName\":\"Metadata\"}],\"LocalName\":\"Metadata\"},{\"NamespaceDeclarations\":[[\"wsid\",\"http://schemas.xmlsoap.org/ws/2006/02/addressingidentity\"]],\"NamespacePrefix\":\"wsid\",\"QualifiedName\":\"wsid:Identity\",\"NamespaceURI\":\"http://schemas.xmlsoap.org/ws/2006/02/addressingidentity\",\"Children\":[{\"NamespaceDeclarations\":[[\"ds\",\"http://www.w3.org/2000/09/xmldsig#\"]],\"NamespacePrefix\":\"ds\",\"QualifiedName\":\"ds:KeyInfo\",\"NamespaceURI\":\"http://www.w3.org/2000/09/xmldsig#\",\"Children\":[{\"NamespaceDeclarations\":[[\"ds\",\"http://www.w3.org/2000/09/xmldsig#\"]],\"NamespacePrefix\":\"ds\",\"QualifiedName\":\"ds:X509Data\",\"NamespaceURI\":\"http://www.w3.org/2000/09/xmldsig#\",\"Children\":[{\"NamespaceDeclarations\":[[\"ds\",\"http://www.w3.org/2000/09/xmldsig#\"]],\"NamespacePrefix\":\"ds\",\"QualifiedName\":\"ds:X509Certificate\",\"NamespaceURI\":\"http://www.w3.org/2000/09/xmldsig#\",\"Children\":[\"MIIDkDCCAvmgAwIBAgIJAO+Fcd4yj0h/MA0GCSqGSIb3DQEBBQUAMIGNMQswCQYDVQQGEwJVUzETMBEGA1UECBMKQ2FsaWZvcm5pYTEWMBQGA1UEBxMNU2FuIEZyYW5jaXNjbzEPMA0GA1UEChMGeG1sZGFwMScwJQYDVQQLFB5DaHVjayBNb3J0aW1vcmUgJiBBeGVsIE5lbm5rZXIxFzAVBgNVBAMTDnd3dy54bWxkYXAub3JnMB4XDTA3MDgxODIxMTIzMVoXDTE3MDgxNTIxMTIzMVowgY0xCzAJBgNVBAYTAlVTMRMwEQYDVQQIEwpDYWxpZm9ybmlhMRYwFAYDVQQHEw1TYW4gRnJhbmNpc2NvMQ8wDQYDVQQKEwZ4bWxkYXAxJzAlBgNVBAsUHkNodWNrIE1vcnRpbW9yZSAmIEF4ZWwgTmVubmtlcjEXMBUGA1UEAxMOd3d3LnhtbGRhcC5vcmcwgZ8wDQYJKoZIhvcNAQEBBQADgY0AMIGJAoGBAOKUn6/QqTZj/BWoQVxNFI0Z2AXI1azws+RyuJek60NiawQrFAKk0Ph+/YnUiQAnzbsT+juZV08UpaPa2IE3g0+RFZtODlqoGGGakSOd9NNnDuNhsdtXJWgQq8paM9Sc4nUue31iq7LvmjSGSL5w84NglT48AcqVGr+/5vy8CfT/AgMBAAGjgfUwgfIwHQYDVR0OBBYEFGcwQKLQtW8/Dql5t70BfXX66dmaMIHCBgNVHSMEgbowgbeAFGcwQKLQtW8/Dql5t70BfXX66dmaoYGTpIGQMIGNMQswCQYDVQQGEwJVUzETMBEGA1UECBMKQ2FsaWZvcm5pYTEWMBQGA1UEBxMNU2FuIEZyYW5jaXNjbzEPMA0GA1UEChMGeG1sZGFwMScwJQYDVQQLFB5DaHVjayBNb3J0aW1vcmUgJiBBeGVsIE5lbm5rZXIxFzAVBgNVBAMTDnd3dy54bWxkYXAub3JnggkA74Vx3jKPSH8wDAYDVR0TBAUwAwEB/zANBgkqhkiG9w0BAQUFAAOBgQAYQisGgrg1xw0TTgIZcz3JXr+ZtwjeKqEewoxCxBz1uki7hJYHIznEZq4fzSMtcBMgbKmOTzFNV0Yr/tnJ9rrljRf8EXci62ffzj+Kkny7JtM6Ltxq0BJuF3jrXogdbsc5J3W9uJ7C2+uJTHG1mApbOdJGvLAGLCaNw5NpP7+ZXQ==\"],\"LocalName\":\"X509Certificate\"}],\"LocalName\":\"X509Data\"}],\"LocalName\":\"KeyInfo\"}],\"LocalName\":\"Identity\"}],\"LocalName\":\"EndpointReference\"},{\"NamespaceDeclarations\":[[\"ic\",\"http://schemas.xmlsoap.org/ws/2005/05/identity\"]],\"NamespacePrefix\":\"ic\",\"QualifiedName\":\"ic:UserCredential\",\"NamespaceURI\":\"http://schemas.xmlsoap.org/ws/2005/05/identity\",\"Children\":[{\"NamespaceDeclarations\":[[\"ic\",\"http://schemas.xmlsoap.org/ws/2005/05/identity\"]],\"NamespacePrefix\":\"ic\",\"QualifiedName\":\"ic:DisplayCredentialHint\",\"NamespaceURI\":\"http://schemas.xmlsoap.org/ws/2005/05/identity\",\"Children\":[\"Please enter your username and password.\"],\"LocalName\":\"DisplayCredentialHint\"},{\"NamespaceDeclarations\":[[\"ic\",\"http://schemas.xmlsoap.org/ws/2005/05/identity\"]],\"NamespacePrefix\":\"ic\",\"QualifiedName\":\"ic:UsernamePasswordCredential\",\"NamespaceURI\":\"http://schemas.xmlsoap.org/ws/2005/05/identity\",\"Children\":[{\"NamespaceDeclarations\":[[\"ic\",\"http://schemas.xmlsoap.org/ws/2005/05/identity\"]],\"NamespacePrefix\":\"ic\",\"QualifiedName\":\"ic:Username\",\"NamespaceURI\":\"http://schemas.xmlsoap.org/ws/2005/05/identity\",\"Children\":[\"username\"],\"LocalName\":\"Username\"}],\"LocalName\":\"UsernamePasswordCredential\"}],\"LocalName\":\"UserCredential\"}],\"LocalName\":\"TokenService\"}],\"LocalName\":\"TokenServiceList\"},{\"NamespaceDeclarations\":[[\"ic\",\"http://schemas.xmlsoap.org/ws/2005/05/identity\"]],\"NamespacePrefix\":\"ic\",\"QualifiedName\":\"ic:SupportedTokenTypeList\",\"NamespaceURI\":\"http://schemas.xmlsoap.org/ws/2005/05/identity\",\"Children\":[{\"NamespaceDeclarations\":[[\"wst\",\"http://schemas.xmlsoap.org/ws/2005/02/trust\"]],\"NamespacePrefix\":\"wst\",\"QualifiedName\":\"wst:TokenType\",\"NamespaceURI\":\"http://schemas.xmlsoap.org/ws/2005/02/trust\",\"Children\":[\"urn:oasis:names:tc:SAML:1.0:assertion\"],\"LocalName\":\"TokenType\"}],\"LocalName\":\"SupportedTokenTypeList\"},{\"NamespaceDeclarations\":[[\"ic\",\"http://schemas.xmlsoap.org/ws/2005/05/identity\"]],\"NamespacePrefix\":\"ic\",\"QualifiedName\":\"ic:SupportedClaimTypeList\",\"NamespaceURI\":\"http://schemas.xmlsoap.org/ws/2005/05/identity\",\"Children\":[{\"NamespaceDeclarations\":[[\"ic\",\"http://schemas.xmlsoap.org/ws/2005/05/identity\"]],\"NamespacePrefix\":\"ic\",\"Attributes\":[{\"NamespacePrefix\":\"\",\"Value\":\"uri\",\"QualifiedName\":\"Uri\",\"NamespaceURI\":\"\",\"LocalName\":\"Uri\"}],\"QualifiedName\":\"ic:SupportedClaimType\",\"NamespaceURI\":\"http://schemas.xmlsoap.org/ws/2005/05/identity\",\"Children\":[{\"NamespaceDeclarations\":[[\"ic\",\"http://schemas.xmlsoap.org/ws/2005/05/identity\"]],\"NamespacePrefix\":\"ic\",\"QualifiedName\":\"ic:DisplayTag\",\"NamespaceURI\":\"http://schemas.xmlsoap.org/ws/2005/05/identity\",\"Children\":[\"displayName\"],\"LocalName\":\"DisplayTag\"},{\"NamespaceDeclarations\":[[\"ic\",\"http://schemas.xmlsoap.org/ws/2005/05/identity\"]],\"NamespacePrefix\":\"ic\",\"QualifiedName\":\"ic:Description\",\"NamespaceURI\":\"http://schemas.xmlsoap.org/ws/2005/05/identity\",\"Children\":[\"description\"],\"LocalName\":\"Description\"}],\"LocalName\":\"SupportedClaimType\"}],\"LocalName\":\"SupportedClaimTypeList\"},{\"NamespaceDeclarations\":[[\"ic\",\"http://schemas.xmlsoap.org/ws/2005/05/identity\"]],\"NamespacePrefix\":\"ic\",\"Attributes\":[{\"NamespacePrefix\":\"\",\"Value\":\"1\",\"QualifiedName\":\"Version\",\"NamespaceURI\":\"\",\"LocalName\":\"Version\"}],\"QualifiedName\":\"ic:PrivacyNotice\",\"NamespaceURI\":\"http://schemas.xmlsoap.org/ws/2005/05/identity\",\"Children\":[\"privacyPolicyUrl\"],\"LocalName\":\"PrivacyNotice\"},{\"NamespaceDeclarations\":[[\"ic07\",\"http://schemas.xmlsoap.org/ws/2007/01/identity\"]],\"NamespacePrefix\":\"ic07\",\"QualifiedName\":\"ic07:RequireStrongRecipientIdentity\",\"NamespaceURI\":\"http://schemas.xmlsoap.org/ws/2007/01/identity\",\"LocalName\":\"RequireStrongRecipientIdentity\"}],\"LocalName\":\"InformationCard\"}";
    assertEquals(expected, json.toString());
  }

  public void testParsedInfocardToJSON() throws Exception {
    String bceidCardStr = "<i:InformationCard xml:lang=\"en\" xmlns:i=\"http://schemas.xmlsoap.org/ws/2005/05/identity\"><i:InformationCardReference><i:CardId>urn:GUID:6d6693c1-6b1a-df11-b009-00143851d232</i:CardId><i:CardVersion>4</i:CardVersion></i:InformationCardReference><i:CardName>BCeID Information Card</i:CardName><i:CardImage MimeType=\"image/jpeg\">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</i:CardImage><i:Issuer>http://stsip.systestv2.bceid.ca/adfs/services/trust</i:Issuer><i:TimeIssued>2010-04-15T17:52:07.341Z</i:TimeIssued><i:TokenServiceList><i:TokenService><EndpointReference xmlns=\"http://www.w3.org/2005/08/addressing\"><Address>https://stsip.systestv2.bceid.ca/adfs/services/trust/2005/usernamemixed</Address><Metadata><Metadata xmlns=\"http://schemas.xmlsoap.org/ws/2004/09/mex\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:wsx=\"http://schemas.xmlsoap.org/ws/2004/09/mex\"><wsx:MetadataSection Dialect=\"http://schemas.xmlsoap.org/ws/2004/09/mex\" xmlns=\"\"><wsx:MetadataReference><Address xmlns=\"http://www.w3.org/2005/08/addressing\">https://stsip.systestv2.bceid.ca/adfs/services/trust/mex</Address></wsx:MetadataReference></wsx:MetadataSection></Metadata></Metadata></EndpointReference><i:UserCredential><i:UsernamePasswordCredential><i:Username>SBCEID\\pwiebe10i</i:Username></i:UsernamePasswordCredential></i:UserCredential></i:TokenService><i:TokenService><EndpointReference xmlns=\"http://www.w3.org/2005/08/addressing\"><Address>https://stsip.systestv2.bceid.ca/adfs/services/trust/13/usernamemixed</Address><Metadata><Metadata xmlns=\"http://schemas.xmlsoap.org/ws/2004/09/mex\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:wsx=\"http://schemas.xmlsoap.org/ws/2004/09/mex\"><wsx:MetadataSection Dialect=\"http://schemas.xmlsoap.org/ws/2004/09/mex\" xmlns=\"\"><wsx:MetadataReference><Address xmlns=\"http://www.w3.org/2005/08/addressing\">https://stsip.systestv2.bceid.ca/adfs/services/trust/mex</Address></wsx:MetadataReference></wsx:MetadataSection></Metadata></Metadata></EndpointReference><i:UserCredential><i:UsernamePasswordCredential><i:Username>SBCEID\\pwiebe10i</i:Username></i:UsernamePasswordCredential></i:UserCredential></i:TokenService></i:TokenServiceList><i:SupportedTokenTypeList><t:TokenType xmlns:t=\"http://schemas.xmlsoap.org/ws/2005/02/trust\">urn:oasis:names:tc:SAML:1.0:assertion</t:TokenType><t:TokenType xmlns:t=\"http://schemas.xmlsoap.org/ws/2005/02/trust\">http://docs.oasis-open.org/wss/oasis-wss-saml-token-profile-1.1#SAMLV1.1</t:TokenType><t:TokenType xmlns:t=\"http://schemas.xmlsoap.org/ws/2005/02/trust\">http://docs.oasis-open.org/wss/oasis-wss-saml-token-profile-1.1#SAMLV2.0</t:TokenType></i:SupportedTokenTypeList><i:SupportedClaimTypeList><i:SupportedClaimType Uri=\"http://idmanagement.gov/icam/2009/09/imi_1.0_profile#assurancelevel1\"><i:DisplayTag>ICAM Assurance Level 1</i:DisplayTag><i:Description>Level of Assurance achieved according to the rules of the ICAM IMI 1.0 profile located at http://www.idmanagement.gov/</i:Description></i:SupportedClaimType><i:SupportedClaimType Uri=\"http://www.cio.gov.bc.ca/standards/claims/2009/11/useridentifier\"><i:DisplayTag>User Identifier</i:DisplayTag></i:SupportedClaimType><i:SupportedClaimType Uri=\"http://www.ocio.gov.bc.ca/standards/claims/2009/06/userdisplayname\"><i:DisplayTag>User Display Name</i:DisplayTag></i:SupportedClaimType><i:SupportedClaimType Uri=\"http://www.ocio.gov.bc.ca/standards/claims/2009/09/identityassurancelevel\"><i:DisplayTag>Identity Assurance Level</i:DisplayTag></i:SupportedClaimType><i:SupportedClaimType Uri=\"http://www.ocio.gov.bc.ca/standards/claims/2009/09/authoritativepartyidentifier\"><i:DisplayTag>AP Identifier</i:DisplayTag></i:SupportedClaimType><i:SupportedClaimType Uri=\"http://www.ocio.gov.bc.ca/standards/claims/2009/09/authoritativepartyname\"><i:DisplayTag>AP Name</i:DisplayTag></i:SupportedClaimType><i:SupportedClaimType Uri=\"http://www.cio.gov.bc.ca/standards/claims/2009/09/identityassurancelevel1\"><i:DisplayTag>Identity Assurance Level 1</i:DisplayTag></i:SupportedClaimType><i:SupportedClaimType Uri=\"http://schemas.xmlsoap.org/ws/2005/05/identity/claims/emailaddress\"><i:DisplayTag>E-Mail Address</i:DisplayTag><i:Description>The e-mail address of the user</i:Description></i:SupportedClaimType><i:SupportedClaimType Uri=\"http://schemas.xmlsoap.org/ws/2005/05/identity/claims/givenname\"><i:DisplayTag>Given Name</i:DisplayTag><i:Description>The given name of the user</i:Description></i:SupportedClaimType><i:SupportedClaimType Uri=\"http://schemas.xmlsoap.org/ws/2005/05/identity/claims/name\"><i:DisplayTag>Name</i:DisplayTag><i:Description>The unique name of the user</i:Description></i:SupportedClaimType><i:SupportedClaimType Uri=\"http://schemas.xmlsoap.org/ws/2005/05/identity/claims/upn\"><i:DisplayTag>UPN</i:DisplayTag><i:Description>The user principal name (UPN) of the user</i:Description></i:SupportedClaimType><i:SupportedClaimType Uri=\"http://schemas.xmlsoap.org/ws/2005/05/identity/claims/surname\"><i:DisplayTag>Surname</i:DisplayTag><i:Description>The surname of the user</i:Description></i:SupportedClaimType><i:SupportedClaimType Uri=\"http://schemas.xmlsoap.org/ws/2005/05/identity/claims/privatepersonalidentifier\"><i:DisplayTag>PPID</i:DisplayTag><i:Description>The private identifier of the user</i:Description></i:SupportedClaimType><i:SupportedClaimType Uri=\"http://schemas.xmlsoap.org/ws/2005/05/identity/claims/nameidentifier\"><i:DisplayTag>Name ID</i:DisplayTag><i:Description>The SAML name identifier of the user</i:Description></i:SupportedClaimType><i:SupportedClaimType Uri=\"http://schemas.microsoft.com/ws/2008/06/identity/claims/authenticationinstant\"><i:DisplayTag>Authentication time stamp</i:DisplayTag><i:Description>Used to display the time and date that the user was authenticated</i:Description></i:SupportedClaimType><i:SupportedClaimType Uri=\"http://schemas.microsoft.com/ws/2008/06/identity/claims/authenticationmethod\"><i:DisplayTag>Authentication method</i:DisplayTag><i:Description>The method used to authenticate the user</i:Description></i:SupportedClaimType></i:SupportedClaimTypeList><i:RequireAppliesTo Optional=\"false\"></i:RequireAppliesTo><ic09:IssuerName xmlns:ic09=\"http://docs.oasis-open.org/imi/ns/identity-200903\">stsip.systestv2.bceid.ca</ic09:IssuerName><ic09:CardType xmlns:ic09=\"http://docs.oasis-open.org/imi/ns/identity-200903\">urn:GUID:6d6693c1-6b1a-df11-b009-00143851d232</ic09:CardType></i:InformationCard>";
    Document doc = XmlUtils.parse(bceidCardStr);
    
    InfoCard card = new InfoCard(doc.getRootElement());
    JSONObject json = card.toJSON();
    String expected = "ewogICJDYXJkSWQiOiAidXJuOkdVSUQ6NmQ2NjkzYzEtNmIxYS1kZjExLWIwMDktMDAxNDM4NTFk\n"+
"MjMyIiwKICAiQ2FyZEltYWdlIjogIi85ai80QUFRU2taSlJnQUJBUUVBZUFCNEFBRC8yd0JEQUFn\n"+
"R0JnY0dCUWdIQndjSkNRZ0tEQlFOREFzTERCa1NFdzhVSFJvZkhoMGFIQndnSkM0bklDSXNJeHdj\n"+
"S0RjcExEQXhORFEwSHljNVBUZ3lQQzR6TkRMLzJ3QkRBUWtKQ1F3TERCZ05EUmd5SVJ3aE1qSXlN\n"+
"akl5TWpJeU1qSXlNakl5TWpJeU1qSXlNakl5TWpJeU1qSXlNakl5TWpJeU1qSXlNakl5TWpJeU1q\n"+
"SXlNakwvd0FBUkNBQlFBSGdEQVNJQUFoRUJBeEVCLzhRQUh3QUFBUVVCQVFFQkFRRUFBQUFBQUFB\n"+
"QUFBRUNBd1FGQmdjSUNRb0wvOFFBdFJBQUFnRURBd0lFQXdVRkJBUUFBQUY5QVFJREFBUVJCUklo\n"+
"TVVFR0UxRmhCeUp4RkRLQmthRUlJMEt4d1JWUzBmQWtNMkp5Z2drS0ZoY1lHUm9sSmljb0tTbzBO\n"+
"VFkzT0RrNlEwUkZSa2RJU1VwVFZGVldWMWhaV21Oa1pXWm5hR2xxYzNSMWRuZDRlWHFEaElXR2g0\n"+
"aUppcEtUbEpXV2w1aVptcUtqcEtXbXA2aXBxckt6dExXMnQ3aTV1c0xEeE1YR3g4akp5dExUMU5Y\n"+
"VzE5aloydUhpNCtUbDV1Zm82ZXJ4OHZQMDlmYjMrUG42LzhRQUh3RUFBd0VCQVFFQkFRRUJBUUFB\n"+
"QUFBQUFBRUNBd1FGQmdjSUNRb0wvOFFBdFJFQUFnRUNCQVFEQkFjRkJBUUFBUUozQUFFQ0F4RUVC\n"+
"U0V4QmhKQlVRZGhjUk1pTW9FSUZFS1JvYkhCQ1NNelV2QVZZbkxSQ2hZa05PRWw4UmNZR1JvbUp5\n"+
"Z3BLalUyTnpnNU9rTkVSVVpIU0VsS1UxUlZWbGRZV1ZwalpHVm1aMmhwYW5OMGRYWjNlSGw2Z29P\n"+
"RWhZYUhpSW1La3BPVWxaYVhtSm1hb3FPa3BhYW5xS21xc3JPMHRiYTN1TG02d3NQRXhjYkh5TW5L\n"+
"MHRQVTFkYlgyTm5hNHVQazVlYm42T25xOHZQMDlmYjMrUG42LzlvQURBTUJBQUlSQXhFQVB3RDM0\n"+
"MFpOQnJnL0ZYeEFHaTMwMWxid0dSNHhobUNiaG45UHk5cXlyVm9VbzgwakN2aUtkQ1BOVVpyNmw0\n"+
"NzBUU2J5TzN1N3BVM01GTForN2s4RWowNS9MdFhTNU5mSmV2WGt1dStJWVlWTExOY3pxaUJqbFJ1\n"+
"SUErdk5mVjBUQ09PT0tTUlRLRkFQYkp4NlY2ZU5vMGFNS2M2Y3I4eXYrVmdvVlhWVGZRbXp6UzAw\n"+
"ZGFkWENiaFJSUlFBVVVVVUFGRkZGQUJSUlJRQVVVVVVBSWE4TStLRVRXZmlLNmRKQVk1Z3J0M0tz\n"+
"VkhhdlVQR3Q1cU5wb3lmMmFzeGtrbENPMEtrc3E0UFRISTdjMTVMZTZacUY0R011blhqc2V1NkZq\n"+
"bjYxNWVQcXliVkpVMit0K3g0ZWJWWnlhb3hwdVd6dlk0YTkwMzdUNUZ5a3NpeXFBWW1Wc0ZUMUdE\n"+
"MTROZTBhdGYzY050QW91cFdFWVdOc3ZsbktqQkpQVW5JSno2MTU5WmVCTDNVOVVXTm9adFBqUWVZ\n"+
"WjVZeU51Q01iUjNPU0s3UFV0R2tXS05HdjVaQ2plWTVjQUdSdlVnZE1uc0s0dUlzMGhpYVZDZ2x5\n"+
"T0c2MTdLMzVNK200WWduZVZTTFcxcm5xV216dGM2WmFUdWN0SkVyTWZVa2MxYnJtdkIrc1JYMWdM\n"+
"SHlXaW1zMFZTQzI0TVBVSCtsZExYdDRlckdyU2pPTHVtWjRpbEtsVmxDU3NGRkZGYkdJVVVVVUFG\n"+
"RkZGQUJSUlJRQVVVVVVBSWFNR2xvb0FxM2xoYlg4YXBkUkNSVk9RQ1NNSDhLb253dm94NjJFWi80\n"+
"RTMrTlRlSWJ1YXc4TmFyZVc3Qlo3ZXpsbGpZak9HVkNRY0gzRmNrYjN4SkQ0RWg4U1E2eUo1bHRS\n"+
"ZFMyOXhieCtXd3hrZ0ZRQ09QYzBLaXBhdElPZHgyT3hzTkhzTk1kM3M3V09GbkdHSzU1RlhxNW1i\n"+
"eGxhV2ZnbTM4UjNjYklzMFNzc0NuNW1kaHdvL0g5T2Frc2JMWHRTdGt1dFQxSjdGNUJ1RnBab243\n"+
"b0hzenNDU2ZYR0JUVU9WZGhPVjJkRlJYTzJ0dnJ1bmVJNFlwZFFrdjlKbmlmSmxpVVBESU1FWlpR\n"+
"TWdqTmJsemRRMmtYbVR5QkY2Yzl6N1VwTlJWMnhxN0pxS3kyMVJMaVlXUUZ4YXpUSVRISVZYSTl3\n"+
"RG45UlhPK0R0UjFuVk5jMXVHLzFScFlOTnVqQkhHc01hK1lQbTVZaGMrblRGS25LTlJOeGV3U1Rp\n"+
"MG1kdFJYQnJlYTljZkVXNjBEKzNKSXJPTzBGMHJKYlJiK1NCdHlWSXh5ZWExTlVqOFQ2UmF2ZTJG\n"+
"L0hxaXhEYzlyY3dxanVvNjdXUUFaK29yVGsxdGNubU9vb3JKOE4rSUxUeE5vMFdwV21WVnNxOGJk\n"+
"WTNIVlRXdFV0Tk96S1R1RkZGRklBb29vb0F4UEdMK1g0SzF3NHpteG1INW9SL1d1UXQvRG1yNng4\n"+
"T2ROanR0WllvYlNOL3Nja1NpT1VBWkNGbHcyTzNXdTE4UzZkYzZ2NGN2OU90SGlTYTVpTVFhWE8w\n"+
"QThIcDdackgwblN2RkdtK0hZTklFK2xxWVl2S1c2QmRtQTZBN01BRWo2MXJDVm8vTWlTdXpndGU4\n"+
"UUxybmhQd3ZxTDJxMjFyYWFtc04xQ2crUkNvR01lMjNQOHE5cEJCQUlPUWE1dXk4RWFWYmVFVzhP\n"+
"ekswOXZKbHBaRzRablBPLzJQVEgwcG1sYWY0azBHM1N4U2EwMVN6aUcyRjUzYUdaVjdBa0JnMlBY\n"+
"aW5OeGtyTG9LS2EzT25aZ3FrbmdDdVh0R3V0WTFHVytTT05vNGlVaDh4dmxUM3dPcHFlUFRkWjFE\n"+
"V1lyelZKYmFHMGdSeEZhV3pNNUxzTnU1bUlHY0FuQUE3MDdUdE0xWFRsa3Q0cHJmeVdiSWRnU3cv\n"+
"Q3ZNeGNKT2NGcTQ5YmR6cW95U2pKOVMvYWFVSXJzM2x4S1o3a2pBWWpBVWVnRmN0NEIvNUdIeGgv\n"+
"MkVqL0FGcnNoSE5CWmxJV0Uwd0JLbVppQXplNUFPQjlCWE1lRnZEMnRhSnJHcTNWM0pZU3c2bGNH\n"+
"ZHhFN2hvenp3TXJ6MUhwWFpRaEdGTnBhWE1adHlrbVZMVC9BSkxWZi84QVlKWC9BTkRXdTVKQVVr\n"+
"OEFkYTRkdkR2aWFQeHZKNGtnZlMvbmdGdTFzMGttQ25CKzl0NjVIcFd2ZjJHdjZ6Yk5aejNGcHAx\n"+
"dEtOc3JXck5MS3lucUF6QlF1ZlhCcmFWbmJVaFhWem5QaEtoK3g2N1BHQ0xTWFVITVBvUVBUOUs5\n"+
"RnFucGVtV21qYWJEWVdNUWl0NFZ3cWorWjlTYXVWRTVjMG15b3F5c0ZGRkZTTUtLS0tBQ2lpaWdB\n"+
"b29vb0FLS0tLQUNpaWlnQW9vb29BS0tLS0FDaWlpZ0QvL1oiLAogICJDYXJkTmFtZSI6ICJCQ2VJ\n"+
"RCBJbmZvcm1hdGlvbiBDYXJkIiwKICAiQ2FyZFR5cGUiOiAidXJuOkdVSUQ6NmQ2NjkzYzEtNmIx\n"+
"YS1kZjExLWIwMDktMDAxNDM4NTFkMjMyIiwKICAiQ2FyZFZlcnNpb24iOiA0LAogICJJc3N1ZXIi\n"+
"OiAiaHR0cDovL3N0c2lwLnN5c3Rlc3R2Mi5iY2VpZC5jYS9hZGZzL3NlcnZpY2VzL3RydXN0IiwK\n"+
"ICAiSXNzdWVyTmFtZSI6ICJzdHNpcC5zeXN0ZXN0djIuYmNlaWQuY2EiLAogICJNaW1lVHlwZSI6\n"+
"ICJpbWFnZS9qcGVnIiwKICAiUmVxdWlyZUFwcGxpZXNUbyI6IGZhbHNlLAogICJTdXBwb3J0ZWRD\n"+
"bGFpbVR5cGVMaXN0IjogWwogICAgewogICAgICAiRGVzY3JpcHRpb24iOiAiTGV2ZWwgb2YgQXNz\n"+
"dXJhbmNlIGFjaGlldmVkIGFjY29yZGluZyB0byB0aGUgcnVsZXMgb2YgdGhlIElDQU0gSU1JIDEu\n"+
"MCBwcm9maWxlIGxvY2F0ZWQgYXQgaHR0cDovL3d3dy5pZG1hbmFnZW1lbnQuZ292LyIsCiAgICAg\n"+
"ICJEaXNwbGF5VGFnIjogIklDQU0gQXNzdXJhbmNlIExldmVsIDEiLAogICAgICAiVXJpIjogImh0\n"+
"dHA6Ly9pZG1hbmFnZW1lbnQuZ292L2ljYW0vMjAwOS8wOS9pbWlfMS4wX3Byb2ZpbGUjYXNzdXJh\n"+
"bmNlbGV2ZWwxIgogICAgfSwKICAgIHsKICAgICAgIkRpc3BsYXlUYWciOiAiVXNlciBJZGVudGlm\n"+
"aWVyIiwKICAgICAgIlVyaSI6ICJodHRwOi8vd3d3LmNpby5nb3YuYmMuY2Evc3RhbmRhcmRzL2Ns\n"+
"YWltcy8yMDA5LzExL3VzZXJpZGVudGlmaWVyIgogICAgfSwKICAgIHsKICAgICAgIkRpc3BsYXlU\n"+
"YWciOiAiVXNlciBEaXNwbGF5IE5hbWUiLAogICAgICAiVXJpIjogImh0dHA6Ly93d3cub2Npby5n\n"+
"b3YuYmMuY2Evc3RhbmRhcmRzL2NsYWltcy8yMDA5LzA2L3VzZXJkaXNwbGF5bmFtZSIKICAgIH0s\n"+
"CiAgICB7CiAgICAgICJEaXNwbGF5VGFnIjogIklkZW50aXR5IEFzc3VyYW5jZSBMZXZlbCIsCiAg\n"+
"ICAgICJVcmkiOiAiaHR0cDovL3d3dy5vY2lvLmdvdi5iYy5jYS9zdGFuZGFyZHMvY2xhaW1zLzIw\n"+
"MDkvMDkvaWRlbnRpdHlhc3N1cmFuY2VsZXZlbCIKICAgIH0sCiAgICB7CiAgICAgICJEaXNwbGF5\n"+
"VGFnIjogIkFQIElkZW50aWZpZXIiLAogICAgICAiVXJpIjogImh0dHA6Ly93d3cub2Npby5nb3Yu\n"+
"YmMuY2Evc3RhbmRhcmRzL2NsYWltcy8yMDA5LzA5L2F1dGhvcml0YXRpdmVwYXJ0eWlkZW50aWZp\n"+
"ZXIiCiAgICB9LAogICAgewogICAgICAiRGlzcGxheVRhZyI6ICJBUCBOYW1lIiwKICAgICAgIlVy\n"+
"aSI6ICJodHRwOi8vd3d3Lm9jaW8uZ292LmJjLmNhL3N0YW5kYXJkcy9jbGFpbXMvMjAwOS8wOS9h\n"+
"dXRob3JpdGF0aXZlcGFydHluYW1lIgogICAgfSwKICAgIHsKICAgICAgIkRpc3BsYXlUYWciOiAi\n"+
"SWRlbnRpdHkgQXNzdXJhbmNlIExldmVsIDEiLAogICAgICAiVXJpIjogImh0dHA6Ly93d3cuY2lv\n"+
"Lmdvdi5iYy5jYS9zdGFuZGFyZHMvY2xhaW1zLzIwMDkvMDkvaWRlbnRpdHlhc3N1cmFuY2VsZXZl\n"+
"bDEiCiAgICB9LAogICAgewogICAgICAiRGVzY3JpcHRpb24iOiAiVGhlIGUtbWFpbCBhZGRyZXNz\n"+
"IG9mIHRoZSB1c2VyIiwKICAgICAgIkRpc3BsYXlUYWciOiAiRS1NYWlsIEFkZHJlc3MiLAogICAg\n"+
"ICAiVXJpIjogImh0dHA6Ly9zY2hlbWFzLnhtbHNvYXAub3JnL3dzLzIwMDUvMDUvaWRlbnRpdHkv\n"+
"Y2xhaW1zL2VtYWlsYWRkcmVzcyIKICAgIH0sCiAgICB7CiAgICAgICJEZXNjcmlwdGlvbiI6ICJU\n"+
"aGUgZ2l2ZW4gbmFtZSBvZiB0aGUgdXNlciIsCiAgICAgICJEaXNwbGF5VGFnIjogIkdpdmVuIE5h\n"+
"bWUiLAogICAgICAiVXJpIjogImh0dHA6Ly9zY2hlbWFzLnhtbHNvYXAub3JnL3dzLzIwMDUvMDUv\n"+
"aWRlbnRpdHkvY2xhaW1zL2dpdmVubmFtZSIKICAgIH0sCiAgICB7CiAgICAgICJEZXNjcmlwdGlv\n"+
"biI6ICJUaGUgdW5pcXVlIG5hbWUgb2YgdGhlIHVzZXIiLAogICAgICAiRGlzcGxheVRhZyI6ICJO\n"+
"YW1lIiwKICAgICAgIlVyaSI6ICJodHRwOi8vc2NoZW1hcy54bWxzb2FwLm9yZy93cy8yMDA1LzA1\n"+
"L2lkZW50aXR5L2NsYWltcy9uYW1lIgogICAgfSwKICAgIHsKICAgICAgIkRlc2NyaXB0aW9uIjog\n"+
"IlRoZSB1c2VyIHByaW5jaXBhbCBuYW1lIChVUE4pIG9mIHRoZSB1c2VyIiwKICAgICAgIkRpc3Bs\n"+
"YXlUYWciOiAiVVBOIiwKICAgICAgIlVyaSI6ICJodHRwOi8vc2NoZW1hcy54bWxzb2FwLm9yZy93\n"+
"cy8yMDA1LzA1L2lkZW50aXR5L2NsYWltcy91cG4iCiAgICB9LAogICAgewogICAgICAiRGVzY3Jp\n"+
"cHRpb24iOiAiVGhlIHN1cm5hbWUgb2YgdGhlIHVzZXIiLAogICAgICAiRGlzcGxheVRhZyI6ICJT\n"+
"dXJuYW1lIiwKICAgICAgIlVyaSI6ICJodHRwOi8vc2NoZW1hcy54bWxzb2FwLm9yZy93cy8yMDA1\n"+
"LzA1L2lkZW50aXR5L2NsYWltcy9zdXJuYW1lIgogICAgfSwKICAgIHsKICAgICAgIkRlc2NyaXB0\n"+
"aW9uIjogIlRoZSBwcml2YXRlIGlkZW50aWZpZXIgb2YgdGhlIHVzZXIiLAogICAgICAiRGlzcGxh\n"+
"eVRhZyI6ICJQUElEIiwKICAgICAgIlVyaSI6ICJodHRwOi8vc2NoZW1hcy54bWxzb2FwLm9yZy93\n"+
"cy8yMDA1LzA1L2lkZW50aXR5L2NsYWltcy9wcml2YXRlcGVyc29uYWxpZGVudGlmaWVyIgogICAg\n"+
"fSwKICAgIHsKICAgICAgIkRlc2NyaXB0aW9uIjogIlRoZSBTQU1MIG5hbWUgaWRlbnRpZmllciBv\n"+
"ZiB0aGUgdXNlciIsCiAgICAgICJEaXNwbGF5VGFnIjogIk5hbWUgSUQiLAogICAgICAiVXJpIjog\n"+
"Imh0dHA6Ly9zY2hlbWFzLnhtbHNvYXAub3JnL3dzLzIwMDUvMDUvaWRlbnRpdHkvY2xhaW1zL25h\n"+
"bWVpZGVudGlmaWVyIgogICAgfSwKICAgIHsKICAgICAgIkRlc2NyaXB0aW9uIjogIlVzZWQgdG8g\n"+
"ZGlzcGxheSB0aGUgdGltZSBhbmQgZGF0ZSB0aGF0IHRoZSB1c2VyIHdhcyBhdXRoZW50aWNhdGVk\n"+
"IiwKICAgICAgIkRpc3BsYXlUYWciOiAiQXV0aGVudGljYXRpb24gdGltZSBzdGFtcCIsCiAgICAg\n"+
"ICJVcmkiOiAiaHR0cDovL3NjaGVtYXMubWljcm9zb2Z0LmNvbS93cy8yMDA4LzA2L2lkZW50aXR5\n"+
"L2NsYWltcy9hdXRoZW50aWNhdGlvbmluc3RhbnQiCiAgICB9LAogICAgewogICAgICAiRGVzY3Jp\n"+
"cHRpb24iOiAiVGhlIG1ldGhvZCB1c2VkIHRvIGF1dGhlbnRpY2F0ZSB0aGUgdXNlciIsCiAgICAg\n"+
"ICJEaXNwbGF5VGFnIjogIkF1dGhlbnRpY2F0aW9uIG1ldGhvZCIsCiAgICAgICJVcmkiOiAiaHR0\n"+
"cDovL3NjaGVtYXMubWljcm9zb2Z0LmNvbS93cy8yMDA4LzA2L2lkZW50aXR5L2NsYWltcy9hdXRo\n"+
"ZW50aWNhdGlvbm1ldGhvZCIKICAgIH0KICBdLAogICJTdXBwb3J0ZWRUb2tlblR5cGVMaXN0Ijog\n"+
"WyJodHRwOi8vZG9jcy5vYXNpcy1vcGVuLm9yZy93c3Mvb2FzaXMtd3NzLXNhbWwtdG9rZW4tcHJv\n"+
"ZmlsZS0xLjEjU0FNTFYyLjAiXSwKICAiVGltZUlzc3VlZCI6ICIyMDEwLTA0LTE1VDE3OjUyOjA3\n"+
"LjM0MVoiLAogICJUb2tlblNlcnZpY2VMaXN0IjogWwogICAgewogICAgICAiQWRkcmVzcyI6ICJo\n"+
"dHRwczovL3N0c2lwLnN5c3Rlc3R2Mi5iY2VpZC5jYS9hZGZzL3NlcnZpY2VzL3RydXN0L21leCIs\n"+
"CiAgICAgICJVc2VyQ3JlZGVudGlhbCI6IHsKICAgICAgICAiVHlwZSI6ICJVc2VyTmFtZVBhc3N3\n"+
"b3JkQXV0aGVudGljYXRlIiwKICAgICAgICAiVXNlcm5hbWUiOiAiU0JDRUlEXFxwd2llYmUxMGki\n"+
"CiAgICAgIH0KICAgIH0sCiAgICB7CiAgICAgICJBZGRyZXNzIjogImh0dHBzOi8vc3RzaXAuc3lz\n"+
"dGVzdHYyLmJjZWlkLmNhL2FkZnMvc2VydmljZXMvdHJ1c3QvbWV4IiwKICAgICAgIlVzZXJDcmVk\n"+
"ZW50aWFsIjogewogICAgICAgICJUeXBlIjogIlVzZXJOYW1lUGFzc3dvcmRBdXRoZW50aWNhdGUi\n"+
"LAogICAgICAgICJVc2VybmFtZSI6ICJTQkNFSURcXHB3aWViZTEwaSIKICAgICAgfQogICAgfQog\n"+
"IF0KfQ==";
   
    assertEquals(expected, Base64.encodeBytes(json.toString(2).getBytes()));
  }
}
