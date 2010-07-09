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

public class TelcosSupportedClaims extends SupportedClaims {
	
	static Logger log = Logger.getLogger(TelcosSupportedClaims.class);
	static public void trace(Object message){
		log.info(message);
	}
 
	/* Et pour le fun
	 *
	 http://schemas.xmlsoap.org/ws/2005/05/identity/claims/emailaddress
	http://schemas.xmlsoap.org/ws/2005/05/identity/claims/otherphone
	http://schemas.xmlsoap.org/ws/2005/05/identity/claims/privatepersonalidentifier
	http://www.fc2consortium.org/ws/2008/10/identity/claims/pseudo
	http://www.fc2consortium.org/ws/2008/10/identity/claims/language
	http://www.fc2consortium.org/ws/2008/10/identity/claims/homefax
	http://www.fc2consortium.org/ws/2008/10/identity/claims/workpostalcode
	
	http://www.fc2consortium.org/ws/2008/10/identity/claims/worklocality
	http://www.fc2consortium.org/ws/2008/10/identity/claims/workcountry
	http://www.fc2consortium.org/ws/2008/10/identity/claims/workfax
	http://www.fc2consortium.org/ws/2008/10/identity/claims/workmobilephone
	http://www.fc2consortium.org/ws/2008/10/identity/claims/workemailaddress
	http://www.fc2consortium.org/ws/2008/10/identity/claims/facade
	http://www.fc2consortium.org/ws/2008/10/identity/claims/numberofemails
	http://www.fc2consortium.org/ws/2008/10/identity/claims/numberofunreademails
	http://www.fc2consortium.org/ws/2008/10/identity/claims/detailsoflastemails
	http://www.fc2consortium.org/ws/2008/10/identity/claims/numberofsms
	  
	 */
	
	
	public final static DbDisplayTag[] numberofsmsIdOA = {new DbDisplayTag("en_US","numberofsms")};
    public final static DbSupportedClaim numberofsmsIdO = new DbSupportedClaim("http://www.fc2consortium.org/ws/2008/10/identity/claims/numberofsms",
    													"numberofsms", "varChar(50)", numberofsmsIdOA);
    
	public final static DbDisplayTag[] detailsoflastemailsIdOA = {new DbDisplayTag("en_US","detailsoflastemails")};
    public final static DbSupportedClaim detailsoflastemailsIdO = new DbSupportedClaim("http://www.fc2consortium.org/ws/2008/10/identity/claims/detailsoflastemails",
    													"detailsoflastemails", "varChar(50)", detailsoflastemailsIdOA);
    
	public final static DbDisplayTag[] numberofunreademailsIdOA = {new DbDisplayTag("en_US","numberofunreademails")};
    public final static DbSupportedClaim numberofunreademailsIdO = new DbSupportedClaim("http://www.fc2consortium.org/ws/2008/10/identity/claims/numberofunreademails",
    													"numberofunreademails", "varChar(50)", numberofunreademailsIdOA);
    
	public final static DbDisplayTag[] numberofemailsIdOA = {new DbDisplayTag("en_US","numberofemails")};
    public final static DbSupportedClaim numberofemailsIdO = new DbSupportedClaim("http://www.fc2consortium.org/ws/2008/10/identity/claims/numberofemails",
    													"numberofemails", "varChar(50)", numberofemailsIdOA);
    
	public final static DbDisplayTag[] facadeIdOA = {new DbDisplayTag("en_US","facade")};
    public final static DbSupportedClaim facadeIdO = new DbSupportedClaim("http://www.fc2consortium.org/ws/2008/10/identity/claims/facade",
    													"facade", "varChar(50)", facadeIdOA);
    
	public final static DbDisplayTag[] workemailaddressIdOA = {new DbDisplayTag("en_US","workemailaddress")};
    public final static DbSupportedClaim workemailaddressIdO = new DbSupportedClaim("http://www.fc2consortium.org/ws/2008/10/identity/claims/workemailaddress",
    													"workemailaddress", "varChar(50)", workemailaddressIdOA);
    
	public final static DbDisplayTag[] workmobilephoneIdOA = {new DbDisplayTag("en_US","workmobilephone")};
    public final static DbSupportedClaim workmobilephoneIdO = new DbSupportedClaim("http://www.fc2consortium.org/ws/2008/10/identity/claims/workmobilephone",
    													"workmobilephone", "varChar(50)", workmobilephoneIdOA);
    
	public final static DbDisplayTag[] workfaxIdOA = {new DbDisplayTag("en_US","workfax")};
    public final static DbSupportedClaim workfaxIdO = new DbSupportedClaim("http://www.fc2consortium.org/ws/2008/10/identity/claims/workfax",
    													"workfax", "varChar(50)", workfaxIdOA);
    
	public final static DbDisplayTag[] workcountryIdOA = {new DbDisplayTag("en_US","workcountry")};
    public final static DbSupportedClaim workcountryIdO = new DbSupportedClaim("http://www.fc2consortium.org/ws/2008/10/identity/claims/workcountry",
    													"workcountry", "varChar(50)", workcountryIdOA);
    
	public final static DbDisplayTag[] worklocalityIdOA = {new DbDisplayTag("en_US","worklocality")};
    public final static DbSupportedClaim worklocalityIdO = new DbSupportedClaim("http://www.fc2consortium.org/ws/2008/10/identity/claims/worklocality",
    													"worklocality", "varChar(50)", worklocalityIdOA);
    
	public final static DbDisplayTag[] workpostalcodeIdOA = {new DbDisplayTag("en_US","workpostalcode")};
    public final static DbSupportedClaim workpostalcodeIdO = new DbSupportedClaim("http://www.fc2consortium.org/ws/2008/10/identity/claims/workpostalcode",
    													"workpostalcode", "varChar(50)", workpostalcodeIdOA);
    
	public final static DbDisplayTag[] homefaxIdOA = {new DbDisplayTag("en_US","homefax")};
    public final static DbSupportedClaim homefaxIdO = new DbSupportedClaim("http://www.fc2consortium.org/ws/2008/10/identity/claims/homefax",
    													"homefax", "varChar(50)", homefaxIdOA);
    
	public final static DbDisplayTag[] languageIdOA = {new DbDisplayTag("en_US","language")};
    public final static DbSupportedClaim languageIdO = new DbSupportedClaim("http://www.fc2consortium.org/ws/2008/10/identity/claims/language",
    													"language", "varChar(50)", languageIdOA);
    
	public final static DbDisplayTag[] pseudoIdOA = {new DbDisplayTag("en_US","pseudo")};
    public final static DbSupportedClaim pseudoIdO = new DbSupportedClaim("http://www.fc2consortium.org/ws/2008/10/identity/claims/pseudo",
    													"pseudo", "varChar(50)", pseudoIdOA);
    
	public final static DbDisplayTag[] privatepersonalidentifierIdOA = {new DbDisplayTag("en_US","privatepersonalidentifier")};
    public final static DbSupportedClaim privatepersonalidentifierIdO = new DbSupportedClaim("http://schemas.xmlsoap.org/ws/2005/05/identity/claims/privatepersonalidentifier",
    													"privatepersonalidentifier", "varChar(50)", privatepersonalidentifierIdOA);
    
	public final static DbDisplayTag[] otherphoneIdOA = {new DbDisplayTag("en_US","otherphone")};
    public final static DbSupportedClaim otherphoneIdO = new DbSupportedClaim("http://schemas.xmlsoap.org/ws/2005/05/identity/claims/otherphone",
    													"otherphone", "varChar(50)", otherphoneIdOA);
    
	public final static DbDisplayTag[] emailaddressIdOA = {new DbDisplayTag("en_US","emailaddress")};
    public final static DbSupportedClaim emailaddressIdO = new DbSupportedClaim("http://schemas.xmlsoap.org/ws/2005/05/identity/claims/emailaddress",
    													"emailaddress", "varChar(50)", emailaddressIdOA);
    
	
	
    /*
    Gender : http://schemas.xmlsoap.org/ws/2005/05/identity/claims/gender
	Surname (Lastname) : http://schemas.xmlsoap.org/ws/2005/05/identity/claims/surname
	Givenname : http://schemas.xmlsoap.org/ws/2005/05/identity/claims/givenname
	DateOfBirth : http://schemas.xmlsoap.org/ws/2005/05/identity/claims/dateofbirth
	Streetaddress :	http://schemas.xmlsoap.org/ws/2005/05/identity/claims/streetaddress
	Postalcode : http://schemas.xmlsoap.org/ws/2005/05/identity/claims/postalcode
	Locality : http://schemas.xmlsoap.org/ws/2005/05/identity/claims/locality	
	Country	: http://schemas.xmlsoap.org/ws/2005/05/identity/claims/country
	Homephone : http://schemas.xmlsoap.org/ws/2005/05/identity/claims/homephone	
	Mobilephone : http://schemas.xmlsoap.org/ws/2005/05/identity/claims/mobilephone
     */
	
	public final static DbDisplayTag[] mobilephoneIdOA = {new DbDisplayTag("en_US","mobilephone")};
    public final static DbSupportedClaim mobilephoneIdO = new DbSupportedClaim("http://schemas.xmlsoap.org/ws/2005/05/identity/claims/mobilephone",
    													"mobilephone", "varChar(50)", mobilephoneIdOA);
    
	public final static DbDisplayTag[] homephoneIdOA = {new DbDisplayTag("en_US","homephone")};
    public final static DbSupportedClaim homephoneIdO = new DbSupportedClaim("http://schemas.xmlsoap.org/ws/2005/05/identity/claims/homephone",
    													"homephone", "varChar(50)", homephoneIdOA);
    
    
	public final static DbDisplayTag[] countryIdOA = {new DbDisplayTag("en_US","country")};
    public final static DbSupportedClaim countryIdO = new DbSupportedClaim("http://schemas.xmlsoap.org/ws/2005/05/identity/claims/country",
    													"country", "varChar(50)", countryIdOA);
    
    
    public final static DbDisplayTag[] genderIdOA = {new DbDisplayTag("en_US","gender")};
    public final static DbSupportedClaim genderIdO = new DbSupportedClaim("http://schemas.xmlsoap.org/ws/2005/05/identity/claims/gender",
    													"gender", "varChar(50)", genderIdOA);
    
    public final static DbDisplayTag[] surnameIdOA = {new DbDisplayTag("en_US","surname")};
    public final static DbSupportedClaim surnameIdO = new DbSupportedClaim("http://schemas.xmlsoap.org/ws/2005/05/identity/claims/surname",
    													"surname", "varChar(50)", surnameIdOA);
    
    public final static DbDisplayTag[] givennameIdOA = {new DbDisplayTag("en_US","givenname")};
    public final static DbSupportedClaim givennameIdO = new DbSupportedClaim("http://schemas.xmlsoap.org/ws/2005/05/identity/claims/givenname",
    													"givenname", "varChar(50)", surnameIdOA);
    
     public final static DbDisplayTag[] dateofbirthIdOA = {new DbDisplayTag("en_US","dateofbirth")};
    public final static DbSupportedClaim dateofbirthIdO = new DbSupportedClaim("http://schemas.xmlsoap.org/ws/2005/05/identity/claims/dateofbirth",
    													"dateofbirth", "varChar(50)", dateofbirthIdOA);
    
   
    public final static DbDisplayTag[] streetAddressIdOA = {new DbDisplayTag("en_US","streetAdress")};
    public final static DbSupportedClaim streetAddressIdO = new DbSupportedClaim("http://schemas.xmlsoap.org/ws/2005/05/identity/claims/streetaddress",
    													"streetaddress", "varChar(50)", streetAddressIdOA);
    
     public final static DbDisplayTag[] postalCodeIdOA = {new DbDisplayTag("en_US","postalCode")};
    public final static DbSupportedClaim postalCodeIdO = new DbSupportedClaim("http://schemas.xmlsoap.org/ws/2005/05/identity/claims/postalcode",
    													"postalcode", "varChar(50)", postalCodeIdOA);
    public final static DbDisplayTag[] localityIdOA = {new DbDisplayTag("en_US","locality")};
    public final static DbSupportedClaim localityIdO = new DbSupportedClaim("http://schemas.xmlsoap.org/ws/2005/05/identity/claims/locality",
    													"locality", "varChar(50)", localityIdOA);
    
	
    public static void main(String arf[]){
    	new TelcosSupportedClaims();
    }
    public  TelcosSupportedClaims() {
    	List<DbSupportedClaim> claim = new ArrayList<DbSupportedClaim>();
    	Field[] lstField = TelcosSupportedClaims.class.getFields();
    	for(int i=0;i<lstField.length;i++){
    		if(lstField[i].getName().endsWith("IdO")){
    			try {
					//trace("Adding the claims = " + ((DbSupportedClaim)lstField[i].get(null)).uri);
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
