package com.utils.impl;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.log4j.Logger;
import org.xmldap.crypto.CryptoUtils;
import org.xmldap.exceptions.CryptoException;
import org.xmldap.sts.db.ManagedCard;

import com.awl.rd.fc2.claims.CompositeSupportedClaims;
import com.awl.rd.fc2.claims.EIDSupportedClaims;
import com.awl.rd.fc2.data.connectors.DataConnector;
import com.awl.rd.fc2.data.connectors.exceptions.CardNotFoundExecption;
import com.awl.rd.fc2.data.connectors.services.ServiceType;
import com.awl.rd.fc2.data.connectors.services.dbutils.DBUtils;
import com.awl.rd.fc2.data.connectors.services.eid.EIDData;
import com.utils.Base64;
import com.utils.ISTSConfiguration;


public class STSConfiguration_EID implements ISTSConfiguration {

	static Logger log = Logger.getLogger(STSConfiguration_EID.class);
	static public void trace(Object message){
		log.info(message);
	}
	static public void err(Object message){
		log.error(message);
	}
	DataConnector connect = null;
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	@Override
	public void configure() {
		
		connect = DataConnector.getInstance();
	}

	@Override
	public void run() {
		trace("Reset of all profiles");
		connect.reset();
		try {

			createAccountForUserId(EIDData.USER_STEF, "scauchie");
			createAccountForUserId(EIDData.USER_FJ, "fjritaine");
			createAccountForUserId(EIDData.USER_ROBERT, "robert");
//			createAccountForUserId("renaud.ninauve", "renaud.ninauve", "987654", "renaud.ninauve");
//			createAccountForUserId("adressedevincent", "adressedevincent", "987654", "adressedevincent");
//			createAccountForUserId("anne", "anne", "987654", "anne");
//			createAccountForUserId("jdupond", "jdupond", "987654", "jdupond");
//			createAccountForUserId("CRE_03_Bancaire", "CRE_03_Bancaire", "987654", "CRE_03_Bancaire");
//			createAccountForUserId("CRE_05_Bancaire", "CRE_05_Bancaire", "987654", "CRE_05_Bancaire");
//			createAccountForUserId("CRE_06_Bancaire", "CRE_06_Bancaire", "987654", "CRE_06_Bancaire");
//			createAccountForUserId("CRE_07_Bancaire", "CRE_07_Bancaire", "987654", "CRE_07_Bancaire");
//			createAccountForUserId("SF_01_Bancaire", "SF_01_Bancaire", "987654", "SF_01_Bancaire");
//			createAccountForUserId("SF_02_Bancaire", "SF_02_Bancaire", "987654", "SF_02_Bancaire");
//			createAccountForUserId("ASP_CB", "ASP_CB", "987654", "ASP_CB");
//			createAccountForUserId("vincent", "vincent", "987654", "vincent");
//			createAccountForUserId("rninauve", "rninauve", "987654", "rninauve");
//			createAccountForUserId("alex09", "alex09", "987654", "alex09");
//			createAccountForUserId("lfournie", "lfournie", "987654", "lfournie");
//			createAccountForUserId("ckuhn", "ckuhn", "987654", "ckuhn");
//			createAccountForUserId("dgd986243517", "dgd986243517", "987654", "dgd986243517");
//			createAccountForUserId("EC_01_Bancaire", "EC_01_Bancaire", "987654", "EC_01_Bancaire");
//			createAccountForUserId("EC_02_Bancaire", "EC_02_Bancaire", "987654", "EC_02_Bancaire");
//			createAccountForUserId("testorange", "testorange", "987654", "testorange");
		} catch (CryptoException e) {
			err("Error in creating the account (due to cardid generation)");
		}
		
		connect.save();
		trace("Committed all profiles");
		
	}

	@Override
	public void test() {
		ManagedCard mgrCard;
		
		try {
			mgrCard = DataConnector.getInstance().getCardByCardID(getCardIDFromUserId("fjritaine")).getManagedCard();
			//FC2ManagedCard c = (FC2ManagedCard) mgrCard;						
			System.out.println("GET SURNAME : " + mgrCard.getClaim(EIDSupportedClaims.surnameIdO.uri));
									
		} catch (CardNotFoundExecption e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CryptoException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	public void createAccountForUserId(String userid,String pwd) throws CryptoException{
		trace("Creating User EID Account - "+userid);
		connect.addUser(userid,pwd);		
		CompositeSupportedClaims theClaims = new CompositeSupportedClaims();	
		theClaims.addSupportedClaims(new EIDSupportedClaims());		
		connect.addNewCardToTheUser(userid,getCardIDFromUserId(userid),theClaims);			
		connect.configureService(userid, ServiceType.EID, userid);
		DBUtils.getInstance().addUser(userid, EIDData.getDefaultUser(userid));
		
	}
	public String getCardIDFromUserId(String userID) throws CryptoException{
		return Base64.encode(byteDigest(userID.getBytes()));
	}
	public static byte[] byteDigest(byte[] data) throws CryptoException {


        MessageDigest md;
        try {
            md = MessageDigest.getInstance("SHA");
        } catch (NoSuchAlgorithmException e) {
            throw new CryptoException(e);
        }
        md.reset();
        md.update(data);
        return md.digest();

    }
}
