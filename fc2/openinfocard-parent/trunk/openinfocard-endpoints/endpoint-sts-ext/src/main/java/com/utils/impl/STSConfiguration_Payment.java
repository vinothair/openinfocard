package com.utils.impl;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.log4j.Logger;
import org.xmldap.crypto.CryptoUtils;
import org.xmldap.exceptions.CryptoException;
import org.xmldap.sts.db.ManagedCard;

import com.awl.rd.fc2.claims.CBSupportedClaims;
import com.awl.rd.fc2.claims.CompositeSupportedClaims;
import com.awl.rd.fc2.claims.FC2ManagedCard;
import com.awl.rd.fc2.claims.SDDSupportedClaims;
import com.awl.rd.fc2.data.connectors.DataConnector;
import com.awl.rd.fc2.data.connectors.exceptions.CardNotFoundExecption;
import com.awl.rd.fc2.data.connectors.services.ServiceType;
import com.utils.Base64;
import com.utils.ISTSConfiguration;

public class STSConfiguration_Payment implements ISTSConfiguration {

	static Logger log = Logger.getLogger(STSConfiguration_Payment.class);
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
			/*createAccountForUserId("stef", "stef", "SDD.stef", "PC.stef");
			createAccountForUserId("fj", "fj", "SDD.fj", "PC.fj");*/
//			createAccountForUserId("youssef", "youssef", "SDD.youssef", "youssef");
//			createAccountForUserId("fjritaine", "fjritaine", "SDD.fjritaine", "fjritaine");
//			createAccountForUserId("ASP", "ASP", "SDD.ASP", "ASP");
//			createAccountForUserId("renaud.ninauve", "renaud.ninauve", "SDD.renaud.ninauve", "renaud.ninauve");
//			createAccountForUserId("adressedevincent", "adressedevincent", "SDD.adressedevincent", "adressedevincent");
//			createAccountForUserId("anne", "anne", "SDD.anne", "anne");
//			createAccountForUserId("jdupond", "jdupond", "SDD.jdupond", "jdupond");
//			createAccountForUserId("CRE_03_Bancaire", "CRE_03_Bancaire", "SDD.CRE_03_Bancaire", "CRE_03_Bancaire");
//			createAccountForUserId("CRE_05_Bancaire", "CRE_05_Bancaire", "SDD.CRE_05_Bancaire", "CRE_05_Bancaire");
//			createAccountForUserId("CRE_06_Bancaire", "CRE_06_Bancaire", "SDD.CRE_06_Bancaire", "CRE_06_Bancaire");
//			createAccountForUserId("CRE_07_Bancaire", "CRE_07_Bancaire", "SDD.CRE_07_Bancaire", "CRE_07_Bancaire");
//			createAccountForUserId("SF_01_Bancaire", "SF_01_Bancaire", "SDD.SF_01_Bancaire", "SF_01_Bancaire");
//			createAccountForUserId("SF_02_Bancaire", "SF_02_Bancaire", "SDD.SF_02_Bancaire", "SF_02_Bancaire");
//			createAccountForUserId("ASP_CB", "ASP_CB", "SDD.ASP_CB", "ASP_CB");
//			createAccountForUserId("vincent", "vincent", "SDD.vincent", "vincent");
//			createAccountForUserId("rninauve", "rninauve", "SDD.rninauve", "rninauve");
//			createAccountForUserId("alex09", "alex09", "SDD.alex09", "alex09");
//			createAccountForUserId("lfournie", "lfournie", "SDD.lfournie", "lfournie");
//			createAccountForUserId("ckuhn", "ckuhn", "SDD.ckuhn", "ckuhn");
//			createAccountForUserId("dgd986243517", "dgd986243517", "SDD.dgd986243517", "dgd986243517");
//			createAccountForUserId("EC_01_Bancaire", "EC_01_Bancaire", "SDD.EC_01_Bancaire", "EC_01_Bancaire");
//			createAccountForUserId("EC_02_Bancaire", "EC_02_Bancaire", "SDD.EC_02_Bancaire", "EC_02_Bancaire");
//			createAccountForUserId("testorange", "testorange", "SDD.testorange", "testorange");
//			createAccountForUserId("youssef", "youssef", "987654", "youssef");
//			createAccountForUserId("fjritaine", "fjritaine", "987654", "fjritaine");
//			createAccountForUserId("ASP", "ASP", "987654", "ASP");
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
			createAccountForUserId("fjritaine", "fjritaine", "987654", "fjritaine");
//			createAccountForUserId("alex09", "alex09", "987654", "alex09");
			createAccountForUserId("CRE_03_Bancaire", "CRE_03_Bancaire", null, "CRE_03_Bancaire");
			createAccountForUserId("CRE_05_Bancaire", "CRE_05_Bancaire", null, "CRE_05_Bancaire");
			createAccountForUserId("CRE_06_Bancaire", "CRE_06_Bancaire", null, "CRE_06_Bancaire");
			createAccountForUserId("CRE_07_Bancaire", "CRE_07_Bancaire", null, "CRE_07_Bancaire");
			createAccountForUserId("SF_01_Bancaire", "SF_01_Bancaire", "SF_01", "SF_01_Bancaire");
			createAccountForUserId("SF_02_Bancaire", "SF_02_Bancaire", "SF_02", "SF_02_Bancaire");
			createAccountForUserId("EC_01_Bancaire", "EC_01_Bancaire", "EC_01", "EC_01_Bancaire");
			createAccountForUserId("EC_02_Bancaire", "EC_02_Bancaire", "EC_02", "EC_02_Bancaire");
			createAccountForUserId("LV_01_Bancaire", "LV_01_Bancaire", "LV_01", "LV_01_Bancaire");
			createAccountForUserId("LV_02_Bancaire", "LV_02_Bancaire", "LV_02", "LV_02_Bancaire");
			
			createAccountForUserId("robert", "robert", "robert_sdd", "robert");
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
			FC2ManagedCard c = (FC2ManagedCard) mgrCard;
			System.out.println(c.stsUserId);
			System.out.println(c.m_vecServices);
			System.out.println("THE CLAIM : " + mgrCard.getClaim("http://www.fc2consortium.org/ws/2008/10/identity/claims/getCRD"));
		} catch (CardNotFoundExecption e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CryptoException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	public void createAccountForUserId(String userid,String pwd,String SDDuserID,String PCUserID) throws CryptoException{
		connect.addUser(userid,pwd);
		
		CompositeSupportedClaims theClaims = new CompositeSupportedClaims();
		if(SDDuserID!=null || !("".equalsIgnoreCase(SDDuserID))){
			theClaims.addSupportedClaims(new SDDSupportedClaims());
		}
		if(PCUserID!=null || !("".equalsIgnoreCase(PCUserID))){
			theClaims.addSupportedClaims(new CBSupportedClaims());
		}			
		connect.addNewCardToTheUser(userid,getCardIDFromUserId(userid),theClaims);	
		connect.configureService(userid, ServiceType.SDD,SDDuserID);
		connect.configureService(userid, ServiceType.PaymentCard,PCUserID);
		connect.configureService(userid, ServiceType.Wallet, userid);
		
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
