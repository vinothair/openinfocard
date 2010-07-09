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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.xmldap.sts.db.DbDisplayTag;
import org.xmldap.sts.db.DbSupportedClaim;
import org.xmldap.sts.db.SupportedClaims;

public class SDDSupportedClaims extends SupportedClaims {
	static private final String prefix = "http://www.fc2consortium.org/ws/2008/10/identity/claims/";
//    public final static DbDisplayTag[] cardNumberOA = {new DbDisplayTag("en_US","Payment Card Number")};
//    public final static DbSupportedClaim cardNumberO = new DbSupportedClaim(prefix + "paymentcardnumber", "paymentcardnumber", "varChar(50)", cardNumberOA);
//    
//    public final static DbDisplayTag[] expirationMonthOA = {new DbDisplayTag("en_US","Expiration Date Month")};
//    public final static DbSupportedClaim expirationMonthO = new DbSupportedClaim(prefix + "paymentcardexpdatemonth", "paymentcardexpdatemonth", "varChar(50)", expirationMonthOA);
//    
//    public final static DbDisplayTag[] expirationYearOA = {new DbDisplayTag("en_US","Expiration Date Year")};
//    public final static DbSupportedClaim expirationYearO = new DbSupportedClaim(prefix + "paymentcardexpdateyear", "paymentcardexpdateyear", "varChar(50)", expirationYearOA);
//    
//    public final static DbDisplayTag[] paymentCardVerificationOA = {new DbDisplayTag("en_US","Verification")};
//    public final static DbSupportedClaim paymentCardVerificationO = new DbSupportedClaim(prefix + "paymentcardverification", "paymentcardverification", "varChar(50)", paymentCardVerificationOA);
//    
//    public final static DbDisplayTag[] paymentbicOA = {new DbDisplayTag("en_US","paymentbic")};
//    public final static DbSupportedClaim paymentbictO = new DbSupportedClaim(prefix + "paymentbic", "paymentbic", "varChar(50)", paymentbicOA);
//    
//    public final static DbDisplayTag[] paymentibanOA = {new DbDisplayTag("en_US","paymentiban")};
//    public final static DbSupportedClaim paymentibanO = new DbSupportedClaim(prefix + "paymentiban", "paymentiban", "varChar(50)", paymentibanOA);
//    
    public final static DbDisplayTag[] paymentdataSDDOA = {new DbDisplayTag("en_US","payment-sdd-emandate")};
    public final static DbSupportedClaim paymentdataSDDO = new DbSupportedClaim(prefix + "payment-sdd-emandate", "payment-sdd-emandate", "varChar(50)", paymentdataSDDOA);
//    
//    public final static DbDisplayTag[] paymentAmountSDDOA = {new DbDisplayTag("en_US","payment-amount")};
//    public final static DbSupportedClaim paymentAmountSDDO = new DbSupportedClaim(prefix + "payment-amount", "payment-amount", "varChar(50)", paymentAmountSDDOA);
//    
    /*
     * Data
		http://www.fc2consortium.org/ws/2008/10/identity/claims/paymentdata
			BIC http://www.fc2consortium.org/ws/2008/10/identity/claims/paymentbic
			IBAN http://www.fc2consortium.org/ws/2008/10/identity/claims/paymentiban
     */
    public  SDDSupportedClaims() {
		List<DbSupportedClaim> claim = new ArrayList<DbSupportedClaim>();	
		claim.add(paymentdataSDDO);
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
