package com.utils;

import java.beans.PropertyChangeSupport;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.log4j.Logger;
import org.xmldap.crypto.CryptoUtils;
import org.xmldap.exceptions.CryptoException;
import org.xmldap.sts.db.ManagedCard;
import org.xmldap.sts.db.SupportedClaims;
import org.xmldap.util.PropertiesManager;

import com.awl.rd.fc2.claims.CBSupportedClaims;
import com.awl.rd.fc2.claims.CompositeSupportedClaims;
import com.awl.rd.fc2.claims.FC2ManagedCard;
import com.awl.rd.fc2.claims.SDDSupportedClaims;
import com.awl.rd.fc2.data.connectors.DataConnector;
import com.awl.rd.fc2.data.connectors.exceptions.CardNotFoundExecption;
import com.awl.rd.fc2.data.connectors.services.ServiceType;

public class CreateCards {
	static Logger log = Logger.getLogger(CreateCards.class);
	static public void trace(Object message){
		log.info(message);
	}
	static public void err(Object message){
		log.error(message);
	}
	DataConnector connect = null;
	public CreateCards() {
		trace("Initializer of dataconnector construction");
		connect = DataConnector.getInstance();
	}
	
	
	public void run(){
		trace("Reset of all profiles");
		connect.reset();
		try {
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
		} catch (CryptoException e) {
			err("Error in creating the account (due to cardid generation)");
		}
		
		connect.save();
		trace("Committed all profiles");
	}
	public static void main(String arg[]){
	
		CreateCards init = new CreateCards();
		init.run();
		init.test();
		
	
	}
	public void test(){
		
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
		};// connect.getCardsByUserId("fjritaine").get(0).getManagedCard();
		
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
