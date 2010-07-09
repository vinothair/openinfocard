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
package com.awl.rd.fc2.storage;

import org.apache.log4j.Logger;
import org.xmldap.sts.db.CardStorage;
import org.xmldap.sts.db.DbSupportedClaim;
import org.xmldap.sts.db.ManagedCard;
import org.xmldap.sts.db.SupportedClaims;
import org.xmldap.exceptions.StorageException;

import com.awl.rd.fc2.data.connectors.Card;
import com.awl.rd.fc2.data.connectors.DataConnector;
import com.awl.rd.fc2.data.connectors.exceptions.CardNotFoundExecption;

import java.sql.*;
import java.util.List;
import java.util.Properties;
import java.util.Vector;


public class FC2CardStorage implements CardStorage {

	Logger log = Logger
			.getLogger(FC2CardStorage.class);

	static final int defaultVersion = 2; // since 200809
	int version = 0;

	SupportedClaims supportedClaimsImpl = null;

	public FC2CardStorage(SupportedClaims supportedClaimsImpl) {
		this.supportedClaimsImpl = supportedClaimsImpl;
	}

	private String claimsDefinition() {
		List<DbSupportedClaim> dbSupportedClaims = supportedClaimsImpl
				.dbSupportedClaims();
		if (dbSupportedClaims.size() > 0) {
			StringBuffer claimsDefinition = new StringBuffer();
			claimsDefinition.append(",");
			for (int i = 0; i < dbSupportedClaims.size() - 1; i++) {
				claimsDefinition.append(" ");
				DbSupportedClaim claim = dbSupportedClaims.get(i);
				claimsDefinition.append(claim.columnName);
				claimsDefinition.append(" ");
				claimsDefinition.append(claim.columnType);
				claimsDefinition.append(",");
			}
			claimsDefinition.append(" ");
			claimsDefinition.append(dbSupportedClaims.get(dbSupportedClaims
					.size() - 1).columnName);
			claimsDefinition.append(" ");
			claimsDefinition.append(dbSupportedClaims.get(dbSupportedClaims
					.size() - 1).columnType);
			return claimsDefinition.toString();
		} else {
			System.out.println("STS supported claims list is empty!!!");
			return "";
		}
	}

	private void createTableCards(Statement s) throws SQLException {
		String claimsDefinition = claimsDefinition();
		// " givenName varChar(50)," +
		// " surname varChar(50)," +
		// " emailAddress varChar(150)," +
		// " streetAddress varChar(50)," +
		// " locality varChar(50)," +
		// " stateOrProvince varChar(50)," +
		// " postalCode varChar(10)," +
		// " country varChar(50)," +
		// " primaryPhone varChar(50)," +
		// " dateOfBirth varChar(50)," +
		// " gender  varChar(10))";

		String query = "create table cards("
				+ "cardid varchar(255) NOT NULL CONSTRAINT CARD_PK PRIMARY KEY,"
				+ " cardName varchar(48) NOT NULL," + " cardVersion int,"
				+ " timeIssued varChar(50) NOT NULL," + " timeExpires varChar(50),"
				+ " requireStrongRecipientIdentity int,"
				+ " requireAppliesTo int" + claimsDefinition + ")";
		System.out.println(query);
		s.execute(query);
		System.out.println("Created table cards");
	}

	public void startup() {

	
	}

	public void addAccount(String username, String password)
			throws StorageException {
	
	}

	public boolean authenticate(String uid, String password) {
		return DataConnector.getInstance().authenticate(uid, password);		
	}

	public void addCard(String username, ManagedCard card) throws StorageException {
		

	}

	public List<String> getCards(String username) {
		
		Vector<String> cardIds = new Vector<String>();
		Vector<Card> vecCard = DataConnector.getInstance().getCardsByUserId(username);
		for(Card cur:vecCard){
			cardIds.add(cur.getCardId());
		}					
		return cardIds;

	}

	public ManagedCard getCard(String cardid) {
		
		try {
			return DataConnector.getInstance().getCardByCardID(cardid).getManagedCard();
		} catch (CardNotFoundExecption e) {
			return null;
		}

	}
	public SupportedClaims getSupportedClaimsByCard(String cardId){
		try {
			return DataConnector.getInstance().getCardByCardID(cardId).getSupportedClaims();
		} catch (CardNotFoundExecption e) {
			return null;
		}
	}

	public void shutdown() {
		

	}

	public int getVersion() {
		
		return version;
	}

	/*
	 * public static void main(String[] args) {
	 * 
	 * CardStorage storage = new CardStorageEmbeddedDBImpl(); storage.startup();
	 * storage.addAccount("cmort1", "password"); boolean authn =
	 * storage.authenticate("cmort", "password1"); System.out.println(authn);
	 * 
	 * 
	 * XSDDateTime issued = new XSDDateTime(); XSDDateTime expires = new
	 * XSDDateTime(525600);
	 * 
	 * 
	 * ManagedCard card = new ManagedCard(); card.setGivenName("cmort");
	 * card.setSurname("motimore"); card.setEmailAddress("cmort@xmldap.org");
	 * card.setCardName("My Card"); card.setTimeIssued(issued.getDateTime());
	 * card.setTimeExpires(expires.getDateTime()); storage.addCard("cmort",
	 * card);
	 * 
	 * List cardIds = storage.getCards("cmort"); Iterator ids =
	 * cardIds.iterator(); while (ids.hasNext()){ String cardId = (String)
	 * ids.next(); ManagedCard thisCard = storage.getCard(cardId);
	 * System.out.println(thisCard.getCardId()); }
	 * 
	 * 
	 * 
	 * storage.shutdown();
	 * 
	 * }
	 */

}
