/*
 * Copyright (c) 2010, Atos Worldline - http://www.atosworldline.com/
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * The names of the contributors may NOT be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE REGENTS AND CONTRIBUTORS ``AS IS'' AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE REGENTS AND CONTRIBUTORS BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * 
 */
package com.awl.rd.fc2.claims;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.xmldap.sts.db.DbDisplayTag;
import org.xmldap.sts.db.DbSupportedClaim;
import org.xmldap.sts.db.SupportedClaims;

public class RACSupportedClaims extends SupportedClaims {
	
	static Logger log = Logger.getLogger(RACSupportedClaims.class);
	static public void trace(Object message){
		log.info(message);
	}
//  
   /**
    * RAC :
=====
 String Civility = "M.";
        String Nom = "Cauchie";
        String prenom = "Stephane";
        String streetAddress = "25 rue de la pointe";
        String CP ="59000";
        String city ="Lille";
Civility : http://www.fc2consortium.org/ws/2008/10/identity/claims/civility
Surname (Lastname) : http://schemas.xmlsoap.org/ws/2005/05/identity/claims/surname
Givenname : http://schemas.xmlsoap.org/ws/2005/05/identity/claims/givenname
Streetaddress : http://schemas.xmlsoap.org/ws/2005/05/identity/claims/streetaddress
Postalcode : http://schemas.xmlsoap.org/ws/2005/05/identity/claims/postalcode
city :http://schemas.xmlsoap.org/ws/2005/05/identity/claims/city
	
	String dateofbirth;
	String emailaddress;
	String password;
	String drivinglicencenumber;
	String drivinglicenceissuingdate;
	String streetaddress;
	String streetaddress_suite;***
	
	String locality;
	String country;
	String homephone;
	String fax;
	String RACUserID="RACID";
    */
	
	
	
	public final static DbDisplayTag[] dateofbirthIdOA = {new DbDisplayTag("en_US","dateofbirth")};
    public final static DbSupportedClaim dateofbirthIdO = new DbSupportedClaim("http://www.fc2consortium.org/ws/2008/10/identity/claims/dateofbirth",
    													"dateofbirth", "varChar(50)", dateofbirthIdOA);
    
	
	public final static DbDisplayTag[] emailaddressIdOA = {new DbDisplayTag("en_US","emailaddress")};
    public final static DbSupportedClaim emailaddressIdO = new DbSupportedClaim("http://www.fc2consortium.org/ws/2008/10/identity/claims/emailaddress",
    													"emailaddress", "varChar(50)", emailaddressIdOA);
    
	
	public final static DbDisplayTag[] passwordIdOA = {new DbDisplayTag("en_US","password")};
    public final static DbSupportedClaim passwordIdO = new DbSupportedClaim("http://www.fc2consortium.org/ws/2008/10/identity/claims/password",
    													"password", "varChar(50)", passwordIdOA);
    
	
//	public final static DbDisplayTag[] drivinglicencenumberIdOA = {new DbDisplayTag("en_US","drivinglicencenumber")};
//    public final static DbSupportedClaim drivinglicencenumberIdO = new DbSupportedClaim("http://www.fc2consortium.org/ws/2008/10/identity/claims/drivinglicencenumber",
//    													"drivinglicencenumber", "varChar(50)", drivinglicencenumberIdOA);
//    
//	
//	public final static DbDisplayTag[] drivinglicenceissuingdateIdOA = {new DbDisplayTag("en_US","drivinglicenceissuingdate")};
//    public final static DbSupportedClaim drivinglicenceissuingdateIdO = new DbSupportedClaim("http://www.fc2consortium.org/ws/2008/10/identity/claims/drivinglicenceissuingdate",
//    													"drivinglicenceissuingdate", "varChar(50)", drivinglicenceissuingdateIdOA);
//    

//	public final static DbDisplayTag[] streetaddressIdOA = {new DbDisplayTag("en_US","streetaddress")};
//    public final static DbSupportedClaim streetaddressIdO = new DbSupportedClaim("http://www.fc2consortium.org/ws/2008/10/identity/claims/streetaddress",
//    													"streetaddress", "varChar(50)", streetaddressIdOA);
//    
	public final static DbDisplayTag[] localityIdOA = {new DbDisplayTag("en_US","locality")};
    public final static DbSupportedClaim localityIdO = new DbSupportedClaim("http://www.fc2consortium.org/ws/2008/10/identity/claims/locality",
    													"locality", "varChar(50)", localityIdOA);
    
	public final static DbDisplayTag[] countryIdOA = {new DbDisplayTag("en_US","country")};
    public final static DbSupportedClaim countryIdO = new DbSupportedClaim("http://www.fc2consortium.org/ws/2008/10/identity/claims/country",
    													"country", "varChar(50)", countryIdOA);
    
	
	public final static DbDisplayTag[] homephoneIdOA = {new DbDisplayTag("en_US","homephone")};
    public final static DbSupportedClaim homephoneIdO = new DbSupportedClaim("http://www.fc2consortium.org/ws/2008/10/identity/claims/homephone",
    													"homephone", "varChar(50)", homephoneIdOA);
    
	
	public final static DbDisplayTag[] faxIdOA = {new DbDisplayTag("en_US","fax")};
    public final static DbSupportedClaim faxIdO = new DbSupportedClaim("http://www.fc2consortium.org/ws/2008/10/identity/claims/fax",
    													"fax", "varChar(50)", faxIdOA);
    

	
	public final static DbDisplayTag[] RACUserIDIdOA = {new DbDisplayTag("en_US","RACUserID")};
    public final static DbSupportedClaim RACUserIDIdO = new DbSupportedClaim("http://www.fc2consortium.org/ws/2008/10/identity/claims/RACUserID",
    													"RACUserID", "varChar(50)", RACUserIDIdOA);
    

		
	public final static DbDisplayTag[] cityIdOA = {new DbDisplayTag("en_US","city")};
    public final static DbSupportedClaim cityIdO = new DbSupportedClaim("http://www.fc2consortium.org/ws/2008/10/identity/claims/civility",
    													"city", "varChar(50)", cityIdOA);
    

    public final static DbDisplayTag[] civilityIdOA = {new DbDisplayTag("en_US","civility")};
    public final static DbSupportedClaim civilityIdO = new DbSupportedClaim("http://www.fc2consortium.org/ws/2008/10/identity/claims/civility",
    													"civility", "varChar(50)", civilityIdOA);
    
   
    public final static DbDisplayTag[] surnameIdOA = {new DbDisplayTag("en_US","surname")};
    public final static DbSupportedClaim surnameIdO = new DbSupportedClaim("http://schemas.xmlsoap.org/ws/2005/05/identity/claims/surname",
    													"surname", "varChar(50)", surnameIdOA);
    
    public final static DbDisplayTag[] givennameIdOA = {new DbDisplayTag("en_US","givenname")};
    public final static DbSupportedClaim givennameIdO = new DbSupportedClaim("http://schemas.xmlsoap.org/ws/2005/05/identity/claims/givenname",
    													"givenname", "varChar(50)", surnameIdOA);
    
  											
    
    public final static DbDisplayTag[] streetAddressIdOA = {new DbDisplayTag("en_US","streetAdress")};
    public final static DbSupportedClaim streetAddressIdO = new DbSupportedClaim("http://schemas.xmlsoap.org/ws/2005/05/identity/claims/streetaddress",
    													"streetaddress", "varChar(50)", streetAddressIdOA);
    
     public final static DbDisplayTag[] postalCodeIdOA = {new DbDisplayTag("en_US","postalCode")};
    public final static DbSupportedClaim postalCodeIdO = new DbSupportedClaim("http://schemas.xmlsoap.org/ws/2005/05/identity/claims/postalcode",
    													"postalcode", "varChar(50)", postalCodeIdOA);
    
    
    public static void main(String arf[]){
    	new RACSupportedClaims();
    }
    public  RACSupportedClaims() {
    	List<DbSupportedClaim> claim = new ArrayList<DbSupportedClaim>();
    	Field[] lstField = RACSupportedClaims.class.getFields();
    	for(int i=0;i<lstField.length;i++){
    		if(lstField[i].getName().endsWith("IdO")){
    			try {
					System.out.println("public String " + ((DbSupportedClaim)lstField[i].get(null)).columnName +";");
					claim.add(((DbSupportedClaim)lstField[i].get(null)));
				} catch (IllegalArgumentException e) {
					trace("Error Adding Claims (IArE)");
						
				} catch (IllegalAccessException e) {
					trace("Error Adding Claims (IAcE");
				}
    			
    		}
    	}
    	
		
		
		/*claim.add(expirationYearO);		
		claim.add(paymentCardVerificationO);*/	
    	dbSupportedClaims = claim.toArray(new DbSupportedClaim[claim.size()]);
    }    

    public DbSupportedClaim getClaimByUri(String uri) {
    	for (DbSupportedClaim claim : dbSupportedClaims) {
    		if (claim.uri.equals(uri)) {
    			return claim;
    		}
    	}
    	throw new IllegalArgumentException("This URI is not supported:" + uri);
    }
    
    public List<DbSupportedClaim> dbSupportedClaims() {
    	return Collections.unmodifiableList(Arrays.asList(dbSupportedClaims));
    }
    public Iterator<DbSupportedClaim> iterator() {
    	return dbSupportedClaims().iterator();
    }

}
