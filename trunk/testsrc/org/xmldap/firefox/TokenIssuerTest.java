package org.xmldap.firefox;

import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.Map;

import org.xmldap.exceptions.TokenIssuanceException;
import org.xmldap.rp.Token;
import org.xmldap.saml.Subject;

import junit.framework.TestCase;

public class TokenIssuerTest extends TestCase {
    X509Certificate cert = null;
    PrivateKey privateKey = null;
    X509Certificate[] chain = null;
    
    static final String cardId = "45925";
    
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
		 "   <carddata>" +
		 "     <selfasserted>" +
		 "       <givenname>Axel</givenname>" +
		 "       <surname>Nennker</surname>" +
		 "       <emailaddress>axel@nennker.de</emailaddress>" +
		 "       <imgurl>file:///D:/Dokumente/nessus.png</imgurl>" +
		 "     </selfasserted>" +
		 "   </carddata>" +
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
		String encryptedXML = 	TokenIssuer.getSelfAssertedToken(
				selfAssertedCardStr, 
				relyingPartyCert,
				chain,
				requiredClaims,
				optionalClaims,
				signingCert,
				signingKey,
				"https://relyingparty.example.com/AudienceRestriction", 
				Subject.HOLDER_OF_KEY);
		Token token = new Token(encryptedXML, privateKey);
		assertTrue(token.isSignatureValid());
		assertTrue(token.isConditionsValid());
//		X509Certificate tokenCert = token.getCertificateOrNull();
//		assertNotNull(tokenCert);
//		tokenCert.checkValidity(); // date
//		assertTrue(token.isCertificateValid());
		Map claims = token.getClaims();
		assertTrue(claims.containsKey("givenname"));
		assertEquals(claims.get("givenname"), "Axel");
		assertTrue(claims.containsKey("surname"));
		assertEquals(claims.get("surname"), "Nennker");
		assertTrue(claims.containsKey("emailaddress"));
		assertEquals(claims.get("emailaddress"), "axel@nennker.de");
		assertTrue(claims.containsKey("privatepersonalidentifier"));
		assertEquals(claims.get("privatepersonalidentifier"), "dm9rQzFIQm1uMUlabXd0L09vVzRPMmlMOVZIWHVJaFRoaWxvdGNFMG04RT0=");
		
        StringBuffer sb = new StringBuffer("-----BEGIN CERTIFICATE-----\n");
        
	}

//	public final void testGetSelfAssertedTokenX509CertificateStringStringStringElementString() {
//		fail("Not yet implemented"); // TODO
//	}

}
