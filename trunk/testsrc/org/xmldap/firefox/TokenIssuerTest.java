package org.xmldap.firefox;

import java.io.IOException;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.Map;

import junit.framework.TestCase;
import nu.xom.Document;

import org.xmldap.exceptions.InfoCardProcessingException;
import org.xmldap.exceptions.TokenIssuanceException;
import org.xmldap.infocard.SelfIssuedClaims;
import org.xmldap.infocard.roaming.SelfIssuedInformationCardPrivateData;
import org.xmldap.rp.Token;
import org.xmldap.saml.Subject;
import org.xmldap.xml.XmlUtils;

public class TokenIssuerTest extends TestCase {
    X509Certificate cert = null;
    PrivateKey privateKey = null;
    X509Certificate[] chain = null;
    
    static final String cardId = "45925";
    
    static final String anyClaimSelfAssertedCardStr = "<infocard>" +
    "<name>testkarte</name>" +
    "<type>selfAsserted</type>" +
    "<version>1</version>" +
    "<id>22392</id>" +
    "<privatepersonalidentifier>81dcac6be7a51ca322062cdc4d6712651dadc1ab</privatepersonalidentifier>" +
    "<ic:InformationCardPrivateData xmlns:ic=\"http://schemas.xmlsoap.org/ws/2005/05/identity\">" +
      "<ic:MasterKey>81dcac6be7a51ca322062cdc4d6712651dadc1ab</ic:MasterKey>" +
      "<ic:ClaimValueList>" +
        "<ic:ClaimValue Uri=\"http://schemas.t-labs.de/ws/2010/10/identity/claims/cardnumber\">" +
          "<ic:Value>1234</ic:Value>" +
        "</ic:ClaimValue>" +
        "<ic:ClaimValue Uri=\"http://schemas.t-labs.de/ws/2010/10/identity/claims/carddomain\">" +
          "<ic:Value>payment</ic:Value>" +
        "</ic:ClaimValue>" +
        "<ic:ClaimValue Uri=\"http://schemas.t-labs.de/ws/2010/10/identity/claims/cardname\">" +
          "<ic:Value>Jochen's Testkarte</ic:Value>" +
        "</ic:ClaimValue>" +
      "</ic:ClaimValueList>" +
    "</ic:InformationCardPrivateData>" +
    "<ic:SupportedClaimTypeList xmlns:ic=\"http://schemas.xmlsoap.org/ws/2005/05/identity\">" +
      "<ic:SupportedClaimType Uri=\"http://schemas.t-labs.de/ws/2010/10/identity/claims/cardnumber\">" +
        "<ic:DisplayTag>http://schemas.t-labs.de/ws/2010/10/identity/claims/cardnumber</ic:DisplayTag>" +
      "</ic:SupportedClaimType>" +
      "<ic:SupportedClaimType Uri=\"http://schemas.t-labs.de/ws/2010/10/identity/claims/carddomain\">" +
        "<ic:DisplayTag>http://schemas.t-labs.de/ws/2010/10/identity/claims/carddomain</ic:DisplayTag>" +
      "</ic:SupportedClaimType>" +
      "<ic:SupportedClaimType Uri=\"http://schemas.t-labs.de/ws/2010/10/identity/claims/cardname\">" +
        "<ic:DisplayTag>http://schemas.t-labs.de/ws/2010/10/identity/claims/cardname</ic:DisplayTag>" +
      "</ic:SupportedClaimType>" +
      "<ic:SupportedClaimType Uri=\"http://schemas.xmlsoap.org/ws/2005/05/identity/claims/privatepersonalidentifier\"/>" +
    "</ic:SupportedClaimTypeList>" +
    "<rpIds>a68741b47ba43b77979e905ba43097e877687d42</rpIds>" +
  "</infocard>";

    static final String selfAssertedCardStr = 
     "<infocard>" +
     "   <name>nessus</name>" +
     "   <type>selfAsserted</type>" +
     "   <version>1</version>" +
     "   <id>" + cardId + "</id>" +
     "   <privatepersonalidentifier>5601ad61a8d29f0c464f45c9fc1ebb014acd060e</privatepersonalidentifier>" +
     "   <supportedclaim>givenname</supportedclaim>" +
     "   <supportedclaim>surname</supportedclaim>" +
     "   <supportedclaim>emailaddress</supportedclaim>" +
     "   <supportedclaim>imgurl</supportedclaim>" +
     "<ic:InformationCardPrivateData xmlns:ic=\"http://schemas.xmlsoap.org/ws/2005/05/identity\">" +
     "<ic:MasterKey>81dcac6be7a51ca322062cdc4d6712651dadc1ab</ic:MasterKey>" +
     "<ic:ClaimValueList>" +
       "<ic:ClaimValue Uri=\"http://schemas.xmlsoap.org/ws/2005/05/identity/claims/givenname\">" +
         "<ic:Value>Axel</ic:Value>" +
       "</ic:ClaimValue>" +
       "<ic:ClaimValue Uri=\"http://schemas.xmlsoap.org/ws/2005/05/identity/claims/surname\">" +
         "<ic:Value>Nennker</ic:Value>" +
       "</ic:ClaimValue>" +
       "<ic:ClaimValue Uri=\"http://schemas.xmlsoap.org/ws/2005/05/identity/claims/emailaddress\">" +
         "<ic:Value>axel@nennker.de</ic:Value>" +
       "</ic:ClaimValue>" +
     "</ic:ClaimValueList>" +
   "</ic:InformationCardPrivateData>" +
     " </infocard>";
    
  protected void setUp() throws Exception {
    super.setUp();

    cert = org.xmldap.util.XmldapCertsAndKeys.getXmldapCert1();
        privateKey = org.xmldap.util.XmldapCertsAndKeys.getXmldapPrivateKey1();

    chain = new X509Certificate[1];
    chain[0] = cert;
  }

  public final void testQualifiedOrgIdString() throws TokenIssuanceException
  {
    String orgIdString = TokenIssuer.orgIdString(cert);
    String qualifiedOrgIdString = TokenIssuer.qualifiedOrgIdString(chain, orgIdString);
    assertEquals("", qualifiedOrgIdString);
  }
  
  public final void testOrgIdString() throws TokenIssuanceException
  {
    String orgIdString = TokenIssuer.orgIdString(cert);
    assertEquals("", orgIdString);
  }
  
  public final void testRpIdentifier() throws TokenIssuanceException
  {
    byte[] rpIdentifier =  TokenIssuer.rpIdentifier(
        cert, 
        chain);
    assertEquals("", new String(rpIdentifier));
  }
  
  public final void testGenerateRPPPID() 
    throws Exception 
  {
    String rpName = "https://xmldap.org/relyingparty/";
    String rpPPID = TokenIssuer.generateRPPPID(
        cardId,
        cert,
        chain, 
        rpName);
    assertEquals("", new String(rpPPID));
  }

  public final void testGetSelfAssertedTokenStringX509CertificateX509CertificateArrayStringString() 
    throws Exception 
  {

        X509Certificate relyingPartyCert = cert;
    String requiredClaims = "http://schemas.xmlsoap.org/ws/2005/05/identity/claims/privatepersonalidentifier http://schemas.xmlsoap.org/ws/2005/05/identity/claims/givenname http://schemas.xmlsoap.org/ws/2005/05/identity/claims/surname http://schemas.xmlsoap.org/ws/2005/05/identity/claims/emailaddress"; 
    String optionalClaims = "http://schemas.xmlsoap.org/ws/2005/05/identity/claims/streetaddress http://schemas.xmlsoap.org/ws/2005/05/identity/claims/locality http://schemas.xmlsoap.org/ws/2005/05/identity/claims/stateorprovince http://schemas.xmlsoap.org/ws/2005/05/identity/claims/postalcode http://schemas.xmlsoap.org/ws/2005/05/identity/claims/country http://schemas.xmlsoap.org/ws/2005/05/identity/claims/homephone http://schemas.xmlsoap.org/ws/2005/05/identity/claims/otherphone http://schemas.xmlsoap.org/ws/2005/05/identity/claims/mobilephone http://schemas.xmlsoap.org/ws/2005/05/identity/claims/dateofbirth http://schemas.xmlsoap.org/ws/2005/05/identity/claims/gender";
    X509Certificate signingCert = cert;
    PrivateKey signingKey = privateKey;
    Document infocard;
    try {
      infocard = XmlUtils.parse(selfAssertedCardStr);
    } catch (IOException e) {
      throw new TokenIssuanceException(e);
    } catch (nu.xom.ParsingException e) {
      throw new TokenIssuanceException(e);    
    }
    String audience = "https://relyingparty.example.com/AudienceRestriction";
    String ppi = TokenIssuer.generateRPPPID(relyingPartyCert, chain, audience, infocard);

    String encryptedXML =   TokenIssuer.getSelfAssertedToken(
        infocard, 
        relyingPartyCert,
        chain,
        requiredClaims,
        optionalClaims,
        signingCert,
        signingKey,
        audience, 
        Subject.HOLDER_OF_KEY,
        ppi);
    Token token = new Token(encryptedXML, privateKey);
    assertTrue(token.isSignatureValid());
    assertTrue(token.isConditionsValid());
//    X509Certificate tokenCert = token.getCertificateOrNull();
//    assertNotNull(tokenCert);
//    tokenCert.checkValidity(); // date
//    assertTrue(token.isCertificateValid());
    Map<String,String> claims = token.getClaims();
    assertTrue(claims.containsKey("givenname"));
    assertEquals(claims.get("givenname"), "Axel");
    assertTrue(claims.containsKey("surname"));
    assertEquals(claims.get("surname"), "Nennker");
    assertTrue(claims.containsKey("emailaddress"));
    assertEquals(claims.get("emailaddress"), "axel@nennker.de");
    assertTrue(claims.containsKey("privatepersonalidentifier"));
    String ppid = (String)claims.get("privatepersonalidentifier");
    assertEquals("OG1lcHcreGJIZVNBbWUxZkJJUDFGNjc3bWhaT1NLRmJSNml4RTcxV3lmQT0=", ppid);
    
  }
  
  public void testGetSelfAssertedToken() throws TokenIssuanceException, InfoCardProcessingException {
    X509Certificate relyingPartyCert = null; // unencrypted token 
    String requiredClaims = "http://schemas.example.com/2010/foo http://schemas.xmlsoap.org/ws/2005/05/identity/claims/givenname";
    String optionalClaims = null; 
    SelfIssuedClaims selfIssuedClaims = new SelfIssuedClaims();
    selfIssuedClaims.setClaim("http://schemas.xmlsoap.org/ws/2005/05/identity/claims/givenname", "Jonathan");
    selfIssuedClaims.setClaim("http://schemas.example.com/2010/foo", "bar");
    String masterkey = "masterkey";
    SelfIssuedInformationCardPrivateData siicpd = new SelfIssuedInformationCardPrivateData(selfIssuedClaims, masterkey.getBytes());
    String ppi = "ppi";
    X509Certificate signingCert = cert;
    PrivateKey signingKey = privateKey;
    String audience = "http://rp.example.com/";
    String confirmationMethod = Subject.BEARER;
    String tokenStr = TokenIssuer.getSelfAssertedToken(
        relyingPartyCert, 
        requiredClaims, optionalClaims, 
        siicpd, ppi,
        signingCert,
        signingKey,
        audience,
        confirmationMethod);
    Token token = new Token(tokenStr, null);
    assertFalse(token.isEncrypted());
    assertTrue(token.isSignatureValid());
    
    Map<String,String> claims = token.getClaims();

    assertTrue(claims.containsKey("givenname"));
    assertEquals("Jonathan", claims.get("givenname"));
    assertTrue(claims.containsKey("http://schemas.example.com/2010/foo"));
    assertEquals("bar", claims.get("http://schemas.example.com/2010/foo"));

    assertTrue(claims.containsKey("privatepersonalidentifier"));
    assertEquals("cHBp", claims.get("privatepersonalidentifier"));
    
    assertEquals(audience, token.getAudience());
  }
  

//  public final void testGetSelfAssertedTokenX509CertificateStringStringStringElementString() {
//    fail("Not yet implemented"); // TODO
//  }

}
