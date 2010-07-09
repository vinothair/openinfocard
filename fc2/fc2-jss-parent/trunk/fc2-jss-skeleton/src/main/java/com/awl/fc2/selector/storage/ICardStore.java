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
 *     * Neither the names xmldap, xmldap.org, xmldap.com nor the
 *       names of its contributors may be used to endorse or promote products
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
 */
package com.awl.fc2.selector.storage;

import java.util.Vector;

import org.xmldap.infocard.InfoCard;

import com.awl.fc2.selector.exceptions.CardStore_Execption_FailedRetrieving;
import com.awl.fc2.selector.query.ClaimsQuery;
import com.awl.fc2.selector.query.CompatibleInfoCards;
import com.awl.fc2.selector.session.ISessionElement;

public interface ICardStore extends ISessionElement{

	public final int STORE_MEMORY =0;
	public final int STORE_PHYSICALLY=1;
	// Query requests
	public Vector<InfoCard> getCompatibleCards(ClaimsQuery query);	
	public Vector<CompatibleInfoCards> getSetCompatibleCards(Vector<ClaimsQuery> setOfQuery);
	
	
	//Infocard handler
	public void addCRD(String l_strPath);
	public void addInfoCard(InfoCard cardToAdd);
	public void removeInfoCard(InfoCard cardToRemove);
	public void removeInfoCard(String cardId);
	public Vector<InfoCard> listAllCards(int storeType);
	public void commit();
	public void commit(String cardId,String B64);
	public void update() throws CardStore_Execption_FailedRetrieving;
	//--

	
	
}
