package org.xmldap.sts.db;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class DbSupportedClaims {
    public final static DbDisplayTag[] givenNameDisplayTagsOA = {new DbDisplayTag("en_US","Given Name"), new DbDisplayTag("de_DE","Vorname")};
    public final static DbSupportedClaim givenNameO = new DbSupportedClaim("http://schemas.xmlsoap.org/ws/2005/05/identity/claims/givenname", "givenName", "varChar(50)", givenNameDisplayTagsOA);
    
    public final static DbDisplayTag[] surnammeDisplayTagsOA = {new DbDisplayTag("en_US","Surname"), new DbDisplayTag("de_DE","Nachname")};
    public final static DbSupportedClaim surnammeO = new DbSupportedClaim("http://schemas.xmlsoap.org/ws/2005/05/identity/claims/surname", "surname", "varChar(50)", surnammeDisplayTagsOA);
    
    public final static DbDisplayTag[] emailaddressDisplayTagsOA = {new DbDisplayTag("en_US","Email"), new DbDisplayTag("de_DE","Email")};
    public final static DbSupportedClaim emailAddressO = new DbSupportedClaim("http://schemas.xmlsoap.org/ws/2005/05/identity/claims/emailaddress", "emailAddress", "varChar(150)", emailaddressDisplayTagsOA);
    
    public final static DbDisplayTag[] streetAddressDisplayTagsOA = {new DbDisplayTag("en_US","Street"), new DbDisplayTag("de_DE","Straﬂe")};
    public final static DbSupportedClaim streetAddressO = new DbSupportedClaim("http://schemas.xmlsoap.org/ws/2005/05/identity/claims/streetaddress", "streetAddress", "varChar(50)", streetAddressDisplayTagsOA);
    
    public final static DbDisplayTag[] localityDisplayTagsOA = {new DbDisplayTag("en_US","City"), new DbDisplayTag("de_DE","Ort")};
    public final static DbSupportedClaim localityNameO = new DbSupportedClaim("http://schemas.xmlsoap.org/ws/2005/05/identity/claims/locality", "locality", "varChar(50)", localityDisplayTagsOA);
    
    public final static DbDisplayTag[] stateOrProvinceDisplayTagsOA = {new DbDisplayTag("en_US","State"), new DbDisplayTag("de_DE","Bundesland")};
    public final static DbSupportedClaim stateOrProvinceO = new DbSupportedClaim("http://schemas.xmlsoap.org/ws/2005/05/identity/claims/stateorprovince", "stateOrProvince", "varChar(50)", stateOrProvinceDisplayTagsOA);
    
    public final  static DbDisplayTag[] postalCodeDisplayTagsOA = {new DbDisplayTag("en_US","Postalcode"), new DbDisplayTag("de_DE","Postleitzahl")};
    public final static DbSupportedClaim postalCodeO = new DbSupportedClaim("http://schemas.xmlsoap.org/ws/2005/05/identity/claims/postalcode", "postalCode", "varChar(10)", postalCodeDisplayTagsOA);
    
    public final static DbDisplayTag[] countryDisplayTagsOA = {new DbDisplayTag("en_US","Country"), new DbDisplayTag("de_DE","Staat")};
    public final static DbSupportedClaim countryO = new DbSupportedClaim("http://schemas.xmlsoap.org/ws/2005/05/identity/claims/country", "country", "varChar(50)", countryDisplayTagsOA);
    
    public final static DbDisplayTag[] primaryPhoneDisplayTagsOA = {new DbDisplayTag("en_US","Telephone"), new DbDisplayTag("de_DE","Telefon")};
    public final static DbSupportedClaim primaryPhoneO = new DbSupportedClaim("http://schemas.xmlsoap.org/ws/2005/05/identity/claims/homephone", "primaryPhone", "varChar(50)", primaryPhoneDisplayTagsOA);
    
    public final static DbDisplayTag[] dateOfBirthDisplayTagsOA = {new DbDisplayTag("en_US","Date of Birth"), new DbDisplayTag("de_DE","Geburtsdatum")};
    public final static DbSupportedClaim dateOfBirthO = new DbSupportedClaim("http://schemas.xmlsoap.org/ws/2005/05/identity/claims/dateofbirth", "dateOfBirth", "varChar(50)", dateOfBirthDisplayTagsOA);
    
    public final static DbDisplayTag[] genderDisplayTagsOA = {new DbDisplayTag("en_US","Gender"), new DbDisplayTag("de_DE","Geschlecht")};
    public final static DbSupportedClaim genderO = new DbSupportedClaim("http://schemas.xmlsoap.org/ws/2005/05/identity/claims/gender", "gender", "varChar(10)", genderDisplayTagsOA);
 
    private final static DbSupportedClaim[] dbSupportedClaims = {
    		givenNameO,
    		surnammeO,
    		emailAddressO,
    		streetAddressO,
    		localityNameO,
    		stateOrProvinceO,
    		postalCodeO,
    		countryO,
    		primaryPhoneO,
    		dateOfBirthO,
    		genderO
    		};
    
    public static DbSupportedClaim getClaimByUri(String uri) {
    	for (DbSupportedClaim claim : dbSupportedClaims) {
    		if (claim.uri.equals(uri)) {
    			return claim;
    		}
    	}
    	throw new IllegalArgumentException("This URI is not supported:" + uri);
    }
    
    public static List<DbSupportedClaim> dbSupportedClaims() {
    	return Collections.unmodifiableList(Arrays.asList(dbSupportedClaims));
    }
    public static Iterator<DbSupportedClaim> iterator() {
    	return dbSupportedClaims().iterator();
    }

}
