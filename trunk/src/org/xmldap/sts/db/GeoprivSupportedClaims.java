package org.xmldap.sts.db;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class GeoprivSupportedClaims extends SupportedClaims {
    public final static DbDisplayTag[] countryOA = {new DbDisplayTag("en_US","Country"), new DbDisplayTag("de_DE","Staat")};
    public final static DbSupportedClaim countryO = new DbSupportedClaim("urn:ietf:params:xml:ns:pidf:geopriv10:civicLoc:country", "country", "varChar(50)", countryOA);
    
    public final static DbDisplayTag[] a1OA = {new DbDisplayTag("en_US","state"), new DbDisplayTag("de_DE","Bundesland")};
    public final static DbSupportedClaim a1O = new DbSupportedClaim("urn:ietf:params:xml:ns:pidf:geopriv10:civicLoc:A1", "A1", "varChar(50)", a1OA);
    
    public final static DbDisplayTag[] a2OA = {new DbDisplayTag("en_US","county"), new DbDisplayTag("de_DE","Bezirk")};
    public final static DbSupportedClaim a2O = new DbSupportedClaim("urn:ietf:params:xml:ns:pidf:geopriv10:civicLoc:A2", "A2", "varChar(50)", a2OA);
    
    public final static DbDisplayTag[] a3OA = {new DbDisplayTag("en_US","city"), new DbDisplayTag("de_DE","Stadt")};
    public final static DbSupportedClaim a3O = new DbSupportedClaim("urn:ietf:params:xml:ns:pidf:geopriv10:civicLoc:A3", "A3", "varChar(50)", a3OA);
    
    public final static DbDisplayTag[] a4OA = {new DbDisplayTag("en_US","city division"), new DbDisplayTag("de_DE","Stadtteil")};
    public final static DbSupportedClaim a4O = new DbSupportedClaim("urn:ietf:params:xml:ns:pidf:geopriv10:civicLoc:A4", "A4", "varChar(50)", a4OA);
    
    public final static DbDisplayTag[] a5OA = {new DbDisplayTag("en_US","neighborhood"), new DbDisplayTag("de_DE","Kiez")};
    public final static DbSupportedClaim a5O = new DbSupportedClaim("urn:ietf:params:xml:ns:pidf:geopriv10:civicLoc:A5", "A5", "varChar(50)", a5OA);
    
    public final static DbDisplayTag[] a6OA = {new DbDisplayTag("en_US","street"), new DbDisplayTag("de_DE","Strasse")};
    public final static DbSupportedClaim a6O = new DbSupportedClaim("urn:ietf:params:xml:ns:pidf:geopriv10:civicLoc:A6", "A6", "varChar(50)", a6OA);
    
    public final static DbDisplayTag[] prdOA = {new DbDisplayTag("en_US","Leading street direction"), new DbDisplayTag("de_DE","Strassenrichtung")};
    public final static DbSupportedClaim prdO = new DbSupportedClaim("urn:ietf:params:xml:ns:pidf:geopriv10:civicLoc:PRD", "PRD", "varChar(50)", prdOA);
    
    public final static DbDisplayTag[] podOA = {new DbDisplayTag("en_US","Trailing street suffix"), new DbDisplayTag("de_DE","Strassensuffix")};
    public final static DbSupportedClaim podO = new DbSupportedClaim("urn:ietf:params:xml:ns:pidf:geopriv10:civicLoc:POD", "POD", "varChar(50)", podOA);
    
    public final static DbDisplayTag[] stsOA = {new DbDisplayTag("en_US","Street suffix"), new DbDisplayTag("de_DE","Strasse2")};
    public final static DbSupportedClaim stsO = new DbSupportedClaim("urn:ietf:params:xml:ns:pidf:geopriv10:civicLoc:STS", "STS", "varChar(50)", stsOA);
    
    public final static DbDisplayTag[] hnoOA = {new DbDisplayTag("en_US","House number"), new DbDisplayTag("de_DE","Hausnummer")};
    public final static DbSupportedClaim hnoO = new DbSupportedClaim("urn:ietf:params:xml:ns:pidf:geopriv10:civicLoc:HNO", "HNO", "varChar(50)", hnoOA);
    
    public final static DbDisplayTag[] hnsOA = {new DbDisplayTag("en_US","House number suffix"), new DbDisplayTag("de_DE","Hausnummersuffix")};
    public final static DbSupportedClaim hnsO = new DbSupportedClaim("urn:ietf:params:xml:ns:pidf:geopriv10:civicLoc:HNS", "HNS", "varChar(50)", hnsOA);
    
    public final static DbDisplayTag[] lmkOA = {new DbDisplayTag("en_US","Landmark or vanity address"), new DbDisplayTag("de_DE","Alternative Adressbezeichnung")};
    public final static DbSupportedClaim lmkO = new DbSupportedClaim("urn:ietf:params:xml:ns:pidf:geopriv10:civicLoc:LMK", "LMK", "varChar(50)", lmkOA);
    
    public final static DbDisplayTag[] locOA = {new DbDisplayTag("en_US","Additional location information"), new DbDisplayTag("de_DE","Zusatzinformation")};
    public final static DbSupportedClaim locO = new DbSupportedClaim("urn:ietf:params:xml:ns:pidf:geopriv10:civicLoc:LOC", "LOC", "varChar(50)", locOA);
    
    public final static DbDisplayTag[] flrOA = {new DbDisplayTag("en_US","Floor"), new DbDisplayTag("de_DE","Etage")};
    public final static DbSupportedClaim flrO = new DbSupportedClaim("urn:ietf:params:xml:ns:pidf:geopriv10:civicLoc:FLR", "FLR", "varChar(50)", flrOA);
    
    public final static DbDisplayTag[] namOA = {new DbDisplayTag("en_US","Name (residence, business or office occupant)"), new DbDisplayTag("de_DE","Raumbezeichnung")};
    public final static DbSupportedClaim namO = new DbSupportedClaim("urn:ietf:params:xml:ns:pidf:geopriv10:civicLoc:NAM", "NAM", "varChar(50)", namOA);
    
    public final static DbDisplayTag[] pcOA = {new DbDisplayTag("en_US","Postal code"), new DbDisplayTag("de_DE","Postleitzahl")};
    public final static DbSupportedClaim pcO = new DbSupportedClaim("urn:ietf:params:xml:ns:pidf:geopriv10:civicLoc:PC", "PC", "varChar(50)", pcOA);

    protected GeoprivSupportedClaims() {
		List<DbSupportedClaim> claim = new ArrayList<DbSupportedClaim>();
		claim.add(countryO);
		claim.add(a1O);
		claim.add(a2O);
		claim.add(a3O);
		claim.add(a4O);
		claim.add(a5O);
		claim.add(a6O);
		claim.add(prdO);
		claim.add(podO);
		claim.add(stsO);
		claim.add(hnoO);
		claim.add(hnsO);
		claim.add(lmkO);
		claim.add(locO);
		claim.add(flrO);
		claim.add(namO);
		claim.add(pcO);
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
