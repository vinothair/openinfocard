package org.xmldap.sts.db;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.xmldap.infocard.Constants;

public class Loa1SupportedClaims extends SupportedClaims {
    public final static DbDisplayTag[] ppidOA = {new DbDisplayTag("en_US","Private Personal Identifier"), 
        new DbDisplayTag("de_DE","Persönliches kryptisches Pseudonym")};
    public final static DbSupportedClaim ppidO = new DbSupportedClaim(Constants.IC_NS_PRIVATEPERSONALIDENTIFIER, "PPID", "varChar(50)", ppidOA);
    public final static DbDisplayTag[] loa1OA = {new DbDisplayTag("en_US","Level of Assurance One"), 
        new DbDisplayTag("de_DE","Zusicherungsgewissheit")};
    public final static DbSupportedClaim loa1O = new DbSupportedClaim(Constants.LOA1_URL, "LOA1", "varChar(256)", loa1OA);

    protected Loa1SupportedClaims() {
		List<DbSupportedClaim> claim = new ArrayList<DbSupportedClaim>();
		claim.add(ppidO);
		claim.add(loa1O);
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
