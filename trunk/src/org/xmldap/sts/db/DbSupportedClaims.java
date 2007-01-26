package org.xmldap.sts.db;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class DbSupportedClaims {
    public final static DbDisplayTag[] givenNameDisplayTagsOA = {new DbDisplayTag("en-us","Given Name"), new DbDisplayTag("de-DE","Vorname")};
    public final static DbSupportedClaim givenNameO = new DbSupportedClaim("http://schemas.xmlsoap.org/ws/2005/05/identity/givenname", "givenName", "varChar(50)", givenNameDisplayTagsOA);
    
    public final static DbDisplayTag[] surnammeDisplayTagsOA = {new DbDisplayTag("en-us","Surname"), new DbDisplayTag("de-DE","Nachname")};
    public final static DbSupportedClaim surnammeO = new DbSupportedClaim("http://schemas.xmlsoap.org/ws/2005/05/identity/claims/surname", "surname", "varChar(50)", surnammeDisplayTagsOA);
    
    public final static DbDisplayTag[] emailaddressDisplayTagsOA = {new DbDisplayTag("en-us","Email"), new DbDisplayTag("de-DE","Email")};
    public final static DbSupportedClaim emailAddressO = new DbSupportedClaim("http://schemas.xmlsoap.org/ws/2005/05/identity/claims/emailaddress", "emailAddress", "varChar(150)", emailaddressDisplayTagsOA);
    
    public final static DbDisplayTag[] streetAddressDisplayTagsOA = {new DbDisplayTag("en-us","Street"), new DbDisplayTag("de-DE","Straﬂe")};
    public final static DbSupportedClaim streetAddressO = new DbSupportedClaim("http://schemas.xmlsoap.org/ws/2005/05/identity/claims/streetaddress", "streetAddress", "varChar(50)", streetAddressDisplayTagsOA);
    
    public final static DbDisplayTag[] localityDisplayTagsOA = {new DbDisplayTag("en-us","City"), new DbDisplayTag("de-DE","Ort")};
    public final static DbSupportedClaim localityNameO = new DbSupportedClaim("http://schemas.xmlsoap.org/ws/2005/05/identity/claims/locality", "locality", "varChar(50)", localityDisplayTagsOA);
    
    public final static DbDisplayTag[] stateOrProvinceDisplayTagsOA = {new DbDisplayTag("en-us","State"), new DbDisplayTag("de-DE","Bundesland")};
    public final static DbSupportedClaim stateOrProvinceO = new DbSupportedClaim("http://schemas.xmlsoap.org/ws/2005/05/identity/claims/stateorprovince", "stateOrProvince", "varChar(50)", stateOrProvinceDisplayTagsOA);
    
    public final  static DbDisplayTag[] postalCodeDisplayTagsOA = {new DbDisplayTag("en-us","Postalcode"), new DbDisplayTag("de-DE","Postleitzahl")};
    public final static DbSupportedClaim postalCodeO = new DbSupportedClaim("http://schemas.xmlsoap.org/ws/2005/05/identity/claims/postalcode", "postalCode", "varChar(10)", postalCodeDisplayTagsOA);
    
    public final static DbDisplayTag[] countryDisplayTagsOA = {new DbDisplayTag("en-us","Country"), new DbDisplayTag("de-DE","Staat")};
    public final static DbSupportedClaim countryO = new DbSupportedClaim("http://schemas.xmlsoap.org/ws/2005/05/identity/claims/country", "country", "varChar(50)", countryDisplayTagsOA);
    
    public final static DbDisplayTag[] primaryPhoneDisplayTagsOA = {new DbDisplayTag("en-us","Telephone"), new DbDisplayTag("de-DE","Telefon")};
    public final static DbSupportedClaim primaryPhoneO = new DbSupportedClaim("http://schemas.xmlsoap.org/ws/2005/05/identity/claims/homephone", "primaryPhone", "varChar(50)", primaryPhoneDisplayTagsOA);
    
    public final static DbDisplayTag[] dateOfBirthDisplayTagsOA = {new DbDisplayTag("en-us","Date of Birth"), new DbDisplayTag("de-DE","Geburtsdatum")};
    public final static DbSupportedClaim dateOfBirthO = new DbSupportedClaim("http://schemas.xmlsoap.org/ws/2005/05/identity/claims/dateofbirth", "dateOfBirth", "varChar(50)", dateOfBirthDisplayTagsOA);
    
    public final static DbDisplayTag[] genderDisplayTagsOA = {new DbDisplayTag("en-us","Gender"), new DbDisplayTag("de-DE","Geschlecht")};
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
    
    public static List<DbSupportedClaim> dbSupportedClaims() {
    	return Collections.unmodifiableList(Arrays.asList(dbSupportedClaims));
    }
    public static Iterator<DbSupportedClaim> iterator() {
    	return dbSupportedClaims().iterator();
    }

}
