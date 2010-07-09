package org.xmldap.infocard.roaming;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;
import nu.xom.ParsingException;
import nu.xom.ValidityException;

import org.xmldap.exceptions.CryptoException;
import org.xmldap.infocard.InfoCard;
import org.xmldap.infocard.TokenServiceReference;
import org.xmldap.infocard.UserCredential;
import org.xmldap.infocard.policy.SupportedClaim;
import org.xmldap.infocard.policy.SupportedClaimTypeList;
import org.xmldap.infocard.policy.SupportedToken;
import org.xmldap.infocard.policy.SupportedTokenList;
import org.xmldap.util.XmldapCertsAndKeys;
import org.xmldap.ws.WSConstants;

public class TestEncryptedStore extends TestCase {

	EncryptedStore es = null;
	static String password = "test";
	byte[] esXmlBytes = null;
	static String readTestXML(){
		InputStream is = TestEncryptedStore.class.getClassLoader().getResourceAsStream("roamingTest.xml");
		StringWriter writer=new StringWriter();
		InputStreamReader streamReader=new InputStreamReader(is);
		//le buffer permet le readline
		BufferedReader buffer=new BufferedReader(streamReader);
		String line="";
		try {
			while ( null!=(line=buffer.readLine())){
			writer.write(line);
			}
		} catch (IOException e) {
			return null;
		}
		// Sortie finale dans le String
		return writer.toString();
	}
	String interopRoamingStoreString = readTestXML();
	RoamingStore interopRoamingStore;
	
//	String selfissued_interop_card_crds = null;
	static final String password_interop = "password";
	
	RoamingStore roamingStore = null; 
	
	protected void setUp() throws Exception {
		super.setUp();
		roamingStore = new RoamingStore();
		InfoCard card = new InfoCard();
		card.setCardId("card1", 1);
		card.setIssuer("issuer");
		card.setTimeIssued("2006-09-28T12:58:26Z");
		{
	        ArrayList<SupportedClaim> cl = new ArrayList<SupportedClaim>();
			String displayName = "displayName";
			String uri = "uri";
			String description = "description";
			SupportedClaim claim = new SupportedClaim(displayName, uri, description);
			cl.add(claim);
			SupportedClaimTypeList claimList = new SupportedClaimTypeList(cl);
			card.setClaimList(claimList);
		}
		{
			List<SupportedToken> supportedTokenList = new ArrayList<SupportedToken>();
			SupportedToken token = new SupportedToken(WSConstants.SAML11_NAMESPACE); 
			supportedTokenList.add(token);
			SupportedTokenList tokenList = new SupportedTokenList(supportedTokenList);
			tokenList.addSupportedToken(token);
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
		
		String issuerName = "issuerName = DN from issuer cert";
    	String hashSalt = "cardHashSalt";
    	String timeLastUpdated = "2009-04-27T19:40:19.6053152Z";
    	String issuerId = "";
    	String backgroundColor = "16777215";

		InformationCardMetaData informationCardMetaData = 
			new InformationCardMetaData(card, false, null, hashSalt, timeLastUpdated, issuerId, issuerName, backgroundColor);
		ManagedInformationCardPrivateData informationCardPrivateData = new ManagedInformationCardPrivateData("masterkeybytes");
		RoamingInformationCard ric = new RoamingInformationCard(informationCardMetaData, informationCardPrivateData);
		roamingStore.addRoamingInformationCard(ric);
		byte[] salt = "01234567890123456789".getBytes();
		byte[] iv = "1234567890123456".getBytes();
		es = new EncryptedStore(roamingStore, password, salt, iv);
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		es.toStream(baos);
		esXmlBytes = baos.toByteArray();
		
		interopRoamingStore = new RoamingStore(interopRoamingStoreString);
	}

	public void testDecryptFromStream() throws CryptoException, ParsingException, IOException, org.xmldap.exceptions.ParsingException {
		ByteArrayInputStream bais = new ByteArrayInputStream(esXmlBytes);
		EncryptedStore storeFromStream = new EncryptedStore(bais, password);
		String roamingStoreString = storeFromStream.getRoamingStoreString();
		RoamingStore aRoamingStore = new RoamingStore(roamingStoreString);
		assertTrue(roamingStore.equals(aRoamingStore));
	}
	
	public void testToStream() throws CryptoException, ValidityException, IOException, ParsingException, org.xmldap.exceptions.ParsingException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		es.toStream(baos);
		String xml = baos.toString();
		ByteArrayInputStream bais = new ByteArrayInputStream(xml.getBytes());
		EncryptedStore encStore = new EncryptedStore(bais, password);
		String roamingStoreString = encStore.getRoamingStoreString();
		RoamingStore aRoamingStore = new RoamingStore(roamingStoreString);
		assertTrue(roamingStore.equals(aRoamingStore));
	}
	
	public void testDecryptInteropCrds() throws CryptoException, ParsingException, IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
//		byte[] bom = {(byte)0xEF, (byte)0xBB, (byte)0xBF}; // utf-8
//		baos.write(bom);
		baos.write(Xaa.x);
		baos.write(Xab.x);
		baos.write(Xac.x);
		baos.write(Xad.x);
		baos.write(Xae.x);
		baos.write(Xaf.x);
		baos.write(Xag.x);
		baos.write(Xah.x);
		baos.write(Xai.x);
		baos.write(Xaj.x);
		baos.write(Xak.x);
		baos.write(Xal.x);
//		String defaultEncoding = java.nio.charset.Charset.defaultCharset().name();
		EncryptedStore storeFromStream = new EncryptedStore(baos.toByteArray(), password_interop);
		String roamingStore = storeFromStream.getRoamingStoreString();
		assertEquals(interopRoamingStoreString, roamingStore);
	}
	
}
