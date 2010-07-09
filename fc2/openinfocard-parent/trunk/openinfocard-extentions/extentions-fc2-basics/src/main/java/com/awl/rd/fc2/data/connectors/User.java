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

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

import org.apache.log4j.Logger;

import com.awl.rd.fc2.claims.FC2ManagedCard;

public class User {
	static Logger log = Logger.getLogger(User.class);
	public static void trace(Object message){
		log.info(message);
	}
	String userId;
	String stspwd;
	Hashtable<String, Card> m_tblCards = new Hashtable<String, Card>();
	Hashtable<String,Service> m_tblServices = new Hashtable<String,Service>();
	
	public User() {
		
	}
	Vector<Service> getServices(){
		verif();
		Enumeration<Service> en = m_tblServices.elements();
		Vector<Service> toRet = new Vector<Service>();
		while(en.hasMoreElements()){
			toRet.add(en.nextElement());
		}
		return toRet;
	}
	public Vector<Card> getCards(){
		verif();
		Vector<Service> vecServices = getServices();
		Enumeration<Card> en = m_tblCards.elements();
		Vector<Card> toRet = new Vector<Card>();
		while(en.hasMoreElements()){
			Card curCard = en.nextElement();
			trace("Set The service");
			((FC2ManagedCard)curCard.getManagedCard()).setAssociatedServices(vecServices);
			((FC2ManagedCard)curCard.getManagedCard()).setSTSUserId(userId);
			toRet.add(curCard);
		}
		return toRet;
	}
	public User(String userId,String pwd) {
		this.userId = userId;
		this.stspwd = pwd;
	}
	public void verif(){
		if(m_tblCards == null) m_tblCards=new Hashtable<String, Card>();
		if(m_tblServices== null)m_tblServices = new Hashtable<String, Service>();
	}
	public void addCard(String cardId,Card theCard){
		verif();
		m_tblCards.put(cardId, theCard);
	}
	public Card getCardById(String cardId){
		trace("Looking for the card with cardID : " + cardId);
		verif();
		Vector<Service> vecServices = getServices();
		trace("Length of table : " + m_tblCards.size());
		Iterator<Card> it = m_tblCards.values().iterator();
		while(it.hasNext()){
			Card current = it.next();
			trace("cardID["+current.getCardId()+"]");
		}
		Card curCard = m_tblCards.get(cardId);
		if(curCard == null) return null;
		((FC2ManagedCard)curCard.getManagedCard()).setAssociatedServices(vecServices);
		((FC2ManagedCard)curCard.getManagedCard()).setSTSUserId(userId);
		return curCard;
	}
	public void addService(Service svc){
		verif();
		if(svc == null) return;
		if(m_tblServices.put(svc.getId(), svc)!= null){
			trace("Service allready present");
			getCards();
		}
	}
}

