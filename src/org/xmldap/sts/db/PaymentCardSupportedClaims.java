/*
 * Copyright (c) 2007, Axel Nennker - http://ignisvulpis.blogspot.com/
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
package org.xmldap.sts.db;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class PaymentCardSupportedClaims extends SupportedClaims {
	static private final String prefix = "http://schemas.xmlsoap.org/PaymentCard/";
    public final static DbDisplayTag[] accountNumberOA = {new DbDisplayTag("en_US","Account Number"), new DbDisplayTag("de_DE","Kontonummer")};
    public final static DbSupportedClaim accountNumberO = new DbSupportedClaim(prefix + "account", "account", "varChar(50)", accountNumberOA);
    
    public final static DbDisplayTag[] verificationValueOA = {new DbDisplayTag("en_US","Verification Value"), new DbDisplayTag("de_DE","Sicherheitswert")};
    public final static DbSupportedClaim verificationValueO = new DbSupportedClaim(prefix + "VV", "VV", "varChar(50)", verificationValueOA);
    
    public final static DbDisplayTag[] expirationDateOA = {new DbDisplayTag("en_US","Expiration Date"), new DbDisplayTag("de_DE","Gültigikeitsendedatum")};
    public final static DbSupportedClaim expirationDateO = new DbSupportedClaim(prefix + "expiry", "expiry", "varChar(50)", expirationDateOA);
    
    public final static DbDisplayTag[] transactionDetailsOA = {new DbDisplayTag("en_US","Transaction Details"), new DbDisplayTag("de_DE","Transaktionsdetails")};
    public final static DbSupportedClaim transactionDetailsO = new DbSupportedClaim(prefix + "trandata?", "TransactionDetails", "varChar(50)", transactionDetailsOA);
    

    protected PaymentCardSupportedClaims() {
		List<DbSupportedClaim> claim = new ArrayList<DbSupportedClaim>();
		claim.add(accountNumberO);
		claim.add(verificationValueO);
		claim.add(expirationDateO);
		claim.add(transactionDetailsO);
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
