package org.xmldap.sts.servlet;

import java.util.ArrayList;
import java.util.List;

import org.xmldap.infocard.policy.SupportedClaim;
import org.xmldap.infocard.policy.SupportedClaimTypeList;
import org.xmldap.sts.db.DbSupportedClaim;
import org.xmldap.sts.db.ManagedCard;

public class IPLocationCardServlet extends CardServlet {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected SupportedClaimTypeList getSupportedClaimList(ManagedCard managedCard) {
    	List<DbSupportedClaim> supportedClaims = supportedClaimsImpl.dbSupportedClaims();
        SupportedClaim supportedClaim = new SupportedClaim("PPID", org.xmldap.infocard.Constants.IC_NS_PRIVATEPERSONALIDENTIFIER, "your personal private identitfier");
        ArrayList<SupportedClaim> cl = new ArrayList<SupportedClaim>();
        cl.add(supportedClaim);
    	for (DbSupportedClaim claim : supportedClaims) {
    		supportedClaim = new SupportedClaim(claim.displayTags[0].displayTag, claim.uri, "A Description");
    		cl.add(supportedClaim);
    	}
        SupportedClaimTypeList claimList = new SupportedClaimTypeList(cl);
        return claimList;
    }


}
