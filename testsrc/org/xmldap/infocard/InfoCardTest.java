package org.xmldap.infocard;


import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.junit.Before;
import org.xmldap.infocard.policy.SupportedClaim;
import org.xmldap.infocard.policy.SupportedClaimTypeList;
import org.xmldap.infocard.policy.SupportedToken;
import org.xmldap.infocard.policy.SupportedTokenList;
import org.xmldap.sts.db.SupportedClaims;
import org.xmldap.util.XmldapCertsAndKeys;
import org.xmldap.ws.WSConstants;

public class InfoCardTest extends TestCase {

	InfoCard card; 
	
	@Before
	public void setUp() throws Exception {
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
	
	public void testCard() {
		assertEquals("card1", card.getCardId());
		assertEquals("issuer", card.getIssuer());
		assertEquals("2006-09-28T12:58:26Z", card.getTimeIssued());
		assertNull(card.getTimeExpires());
		assertEquals(null, card.getBase64BinaryCardImage());
		assertEquals(null, card.getCardName());
		assertEquals(null, card.getLang());
		assertEquals("privacyPolicyUrl", card.getPrivacyPolicy());
		assertEquals(1, card.getPrivacyPolicyVersion());
		assertTrue(card.getRequireStrongRecipientIdentity());
		SupportedTokenList stl = card.getTokenList();
		assertEquals(1, stl.getSupportedTokens().size());
		SupportedClaimTypeList sctl = card.getClaimList();
		assertEquals(1, sctl.getSupportedClaims().size());
		assertEquals(1,card.getTokenServiceReference().size());
	}

}
