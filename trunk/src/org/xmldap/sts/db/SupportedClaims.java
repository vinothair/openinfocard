package org.xmldap.sts.db;

import java.util.Iterator;
import java.util.List;

public class SupportedClaims {
	protected static DbSupportedClaim[] dbSupportedClaims;

	public static SupportedClaims getInstance(String name) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		SupportedClaims supportedClaims = (SupportedClaims)Class.forName(name).newInstance();
		return supportedClaims;
	}
	
	public DbSupportedClaim getClaimByUri(String uri) {
		return null;
	}
   
	public List<DbSupportedClaim> dbSupportedClaims() {
		return null;
	}

	public Iterator<DbSupportedClaim> iterator() {
		return null;
	}


}
