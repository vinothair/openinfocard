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
package com.awl.rd.fc2.data.connectors;

import java.util.List;

import org.xmldap.sts.db.DbSupportedClaim;
import org.xmldap.sts.db.ManagedCard;
import org.xmldap.sts.db.SupportedClaims;
import org.xmldap.util.PropertiesManager;
import org.xmldap.util.XSDDateTime;

import com.awl.rd.fc2.claims.FC2ManagedCard;

public class Card {
	ManagedCard theCard;
	SupportedClaims claims;
	public SupportedClaims getSupportedClaims(){
		return claims;
	}
	public Card() {
	
	}
	public ManagedCard getManagedCard(){
		return theCard;
	}
	public String getCardId(){
		return theCard.getCardId();
	}
	public static Card getNewCard(SupportedClaims theClaims){
		return getNewCard(null,theClaims);
	}
	public static Card getNewCard(String cardId,SupportedClaims theClaims){
		Card toret = new Card();
		toret.claims = theClaims;
//		try {
//			toret.claims =  SupportedClaims.getInstance(PropertiesManager.getInstance().getProperty("supportedClaimsClass"));
//		} catch (InstantiationException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IllegalAccessException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (ClassNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		FC2ManagedCard mgrCard;
		if(cardId == null)
			mgrCard = new FC2ManagedCard();
		else
			mgrCard = new FC2ManagedCard(cardId);
		
		mgrCard.setCardName(PropertiesManager.getInstance().getProperty("cardname"));
		 String timeissued = new XSDDateTime().getDateTime();
		 mgrCard.setTimeIssued(timeissued);
		 List dbSupportedClaims = toret.claims.dbSupportedClaims();
		    for (int i=0; i<dbSupportedClaims.size(); i++) {
		    	DbSupportedClaim claim = (DbSupportedClaim)dbSupportedClaims.get(i);
		    	String key = claim.columnName;		    			    			    	
			    mgrCard.setClaim(claim.uri, "NOT_USED");
		    	
		    }
		 toret.theCard = mgrCard;
		
		return toret;
		
		
	}
}
