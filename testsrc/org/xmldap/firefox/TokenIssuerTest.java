package org.xmldap.firefox;

import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.Map;

import org.xmldap.rp.Token;
import org.xmldap.saml.Subject;

import junit.framework.TestCase;

public class TokenIssuerTest extends TestCase {

	protected void setUp() throws Exception {
		super.setUp();
	}

	public final void testGetSelfAssertedTokenStringX509CertificateX509CertificateArrayStringString() 
		throws Exception 
	{
		String card =
 "<infocard>" +
 "   <name>nessus</name>" +
 "   <type>selfAsserted</type>" +
 "   <version>1</version>" +
 "   <id>45925</id>" +
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
        X509Certificate cert = null;
        cert = org.xmldap.util.XmldapCertsAndKeys.getXmldapCert1();
        PrivateKey privateKey = null;
        privateKey = org.xmldap.util.XmldapCertsAndKeys.getXmldapPrivateKey1();

        X509Certificate relyingPartyCert = cert;
		X509Certificate[] chain = new X509Certificate[1];
		chain[0] = cert;
		String requiredClaims = "http://schemas.xmlsoap.org/ws/2005/05/identity/claims/privatepersonalidentifier http://schemas.xmlsoap.org/ws/2005/05/identity/claims/givenname http://schemas.xmlsoap.org/ws/2005/05/identity/claims/surname http://schemas.xmlsoap.org/ws/2005/05/identity/claims/emailaddress"; 
		String optionalClaims = "http://schemas.xmlsoap.org/ws/2005/05/identity/claims/streetaddress http://schemas.xmlsoap.org/ws/2005/05/identity/claims/locality http://schemas.xmlsoap.org/ws/2005/05/identity/claims/stateorprovince http://schemas.xmlsoap.org/ws/2005/05/identity/claims/postalcode http://schemas.xmlsoap.org/ws/2005/05/identity/claims/country http://schemas.xmlsoap.org/ws/2005/05/identity/claims/homephone http://schemas.xmlsoap.org/ws/2005/05/identity/claims/otherphone http://schemas.xmlsoap.org/ws/2005/05/identity/claims/mobilephone http://schemas.xmlsoap.org/ws/2005/05/identity/claims/dateofbirth http://schemas.xmlsoap.org/ws/2005/05/identity/claims/gender";
		X509Certificate signingCert = cert;
		PrivateKey signingKey = privateKey;
		String encryptedXML = 	TokenIssuer.getSelfAssertedToken(
				card, 
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
