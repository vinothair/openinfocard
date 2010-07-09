package com.utils.impl;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.log4j.Logger;
import org.xmldap.crypto.CryptoUtils;
import org.xmldap.exceptions.CryptoException;
import org.xmldap.sts.db.ManagedCard;

import com.awl.rd.fc2.claims.CompositeSupportedClaims;
import com.awl.rd.fc2.claims.DriverLicenceSupportedClaims;
import com.awl.rd.fc2.data.connectors.DataConnector;
import com.awl.rd.fc2.data.connectors.exceptions.CardNotFoundExecption;
import com.awl.rd.fc2.data.connectors.services.ServiceType;
import com.awl.rd.fc2.data.connectors.services.dbutils.DBUtils;
import com.awl.rd.fc2.data.connectors.services.driverlicence.DriverLicenceData;
import com.awl.rd.fc2.data.connectors.services.eid.EIDData;
import com.utils.Base64;
import com.utils.ISTSConfiguration;

public class STSConfiguration_DriverLicence implements ISTSConfiguration {

	static Logger log = Logger.getLogger(STSConfiguration_DriverLicence.class);
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
			System.out.println("GET SURNAME : " + mgrCard.getClaim(DriverLicenceSupportedClaims.surnameIdO.uri));
									
		} catch (CardNotFoundExecption e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CryptoException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	public void createAccountForUserId(String userid,String pwd) throws CryptoException{
		trace("Creating User Driver Licence Account - "+userid);
		connect.addUser(userid,pwd);		
		CompositeSupportedClaims theClaims = new CompositeSupportedClaims();	
		theClaims.addSupportedClaims(new DriverLicenceSupportedClaims());		
		connect.addNewCardToTheUser(userid,getCardIDFromUserId(userid),theClaims);			
		connect.configureService(userid, ServiceType.DRIVERLICENCE, userid);
		DBUtils.getInstance().addUser(userid, DriverLicenceData.getDefaultUser(userid));
		
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
