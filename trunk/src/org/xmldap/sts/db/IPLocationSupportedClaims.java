package org.xmldap.sts.db;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class IPLocationSupportedClaims extends SupportedClaims {
    public final static DbDisplayTag[] countryOA = {new DbDisplayTag("en_US","Country"), new DbDisplayTag("de_DE","Staat")};
    public final static DbSupportedClaim countryO = new DbSupportedClaim("urn:ietf:params:xml:ns:pidf:geopriv10:civicLoc:country", "country", "varChar(50)", countryOA);
    
    public final static DbDisplayTag[] a3OA = {new DbDisplayTag("en_US","city"), new DbDisplayTag("de_DE","Stadt")};
    public final static DbSupportedClaim a3O = new DbSupportedClaim("urn:ietf:params:xml:ns:pidf:geopriv10:civicLoc:A3", "A3", "varChar(50)", a3OA);
    
    public final static DbDisplayTag[] a2OA = {new DbDisplayTag("en_US","county"), new DbDisplayTag("de_DE","Bezirk")};
    public final static DbSupportedClaim a2O = new DbSupportedClaim("urn:ietf:params:xml:ns:pidf:geopriv10:civicLoc:A2", "A2", "varChar(50)", a2OA);
    
    public final static DbDisplayTag[] locOA = {new DbDisplayTag("en_US","Additional location information"), new DbDisplayTag("de_DE","Zusatzinformation")};
    public final static DbSupportedClaim locO = new DbSupportedClaim("urn:ietf:params:xml:ns:pidf:geopriv10:civicLoc:LOC", "LOC", "varChar(50)", locOA);
    
    protected IPLocationSupportedClaims() {
		List<DbSupportedClaim> claim = new ArrayList<DbSupportedClaim>();
		claim.add(countryO);
		claim.add(a2O);
		claim.add(a3O);
		claim.add(locO);
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
