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

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.xmldap.sts.db.DbSupportedClaim;
import org.xmldap.sts.db.SupportedClaims;

public class CompositeSupportedClaims extends SupportedClaims {
	  
    /*
     * Data
		http://www.fc2consortium.org/ws/2008/10/identity/claims/paymentdata
			BIC
http://www.fc2consortium.org/ws/2008/10/identity/claims/paymentbic
			IBAN
http://www.fc2consortium.org/ws/2008/10/identity/claims/paymentiban
     */
    public  CompositeSupportedClaims() {
	
    //	dbSupportedClaims = claim.toArray(new DbSupportedClaim[claim.size()]);
    } 
    public void addSupportedClaims(SupportedClaims setOfClaims){
    	if(dbSupportedClaims==null || dbSupportedClaims.length == 0){
    		int size = setOfClaims.getSupportedClaims().length;
    		dbSupportedClaims = new DbSupportedClaim[size];
    		for(int i=0;i<size;i++){
    			dbSupportedClaims[i] = setOfClaims.getSupportedClaims()[i];    			
    		}    		
    	}else{
    		int size = setOfClaims.getSupportedClaims().length;
    		int Gsize =size + dbSupportedClaims.length;;
    		DbSupportedClaim[] tmp = new DbSupportedClaim[Gsize];
    		int i=0;
    		for(;i<size;i++){
    			tmp[i] = setOfClaims.getSupportedClaims()[i];    			
    		}
    		for(int cpt=0;i<Gsize;i++){
    			tmp[i] = dbSupportedClaims[cpt];    			
    		}
    		dbSupportedClaims = tmp;
    	}
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
