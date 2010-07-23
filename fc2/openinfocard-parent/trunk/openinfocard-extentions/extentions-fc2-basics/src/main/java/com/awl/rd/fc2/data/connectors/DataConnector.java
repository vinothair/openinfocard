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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

import org.xmldap.sts.db.SupportedClaims;
import org.xmldap.util.PropertiesManager;

import com.awl.rd.fc2.data.connectors.exceptions.CardNotFoundExecption;
import com.awl.rd.fc2.data.connectors.services.ServiceType;
import com.thoughtworks.xstream.XStream;

public class DataConnector {
	
	public Hashtable<String, User> m_tblUser = new Hashtable<String, User>();
	transient static DataConnector s_this = null;
	
	public boolean authenticate(String userId,String pwd){
		User curUser = m_tblUser.get(userId);
		if(curUser==null)
			return false;
		
		if(curUser.stspwd.equalsIgnoreCase(pwd)){
			return true;
		}
		return false;
	}
	
	public static void trace(Object obj){
		System.out.println("DATAConnector : " +  obj);
	}
	
	public DataConnector() {
		trace("Creating a new dataconnector");
	}
	public static XStream setAlias(XStream xstream){
//		xstream.alias("DataConnector", DataConnector.class);
//		xstream.alias("User", User.class);
//		xstream.alias("Service", Service.class);
//		xstream.alias("Card", Card.class);
		return xstream;
	}
	public void save(){
		String l_strPath2XML = PropertiesManager.getInstance().getProperty("storageFile");
		XStream xstream = new XStream();
		//Setting the alias
		
	//	String xml = xstream.toXML(this);
		setAlias(xstream);
		String destFile = null;
		try {
			//Check if directory exists
			//File directory = new File(loc.m_strDBConnection+"/"+loc.m_strObjectLocation+"/");
			//if(!directory.exists()){
			//	directory.mkdirs();
			//}
			//--
			destFile = l_strPath2XML;
			destFile = destFile.replace("//", "/");
			System.out.println("Writing to : " + destFile);
			BufferedWriter out = new BufferedWriter(new FileWriter(destFile));
			xstream.toXML(this, out);
			
			out.flush();
			out.close(); 
		} catch (IOException e) {
			e.printStackTrace()		;	
		}
	}
	
	
	static DataConnector load(DataConnector root){
		String destFile = PropertiesManager.getInstance().getProperty("storageFile");//"c:/dataConnector.xml";
		XStream xstream = new XStream();
		xstream = setAlias(xstream);
		try {
			System.out.println(destFile);
			File file = new File(destFile);
			if(!file.exists()){
				trace("No dataconnector creating a new one");
				return new DataConnector();
			}
			BufferedReader fichier = new BufferedReader(new FileReader(destFile));
			return (DataConnector) xstream.fromXML(fichier,new DataConnector());
		} catch (FileNotFoundException e) {
			trace("does not work");
		}	
		return new DataConnector();
	}
	public static DataConnector getInstance(){
		if(s_this == null){
			s_this = load(null);
		}
		return s_this;
	}
	public void addUser(String userId,String stspwd){
		if(m_tblUser.get(userId)!=null){
			trace("User Allready present");
		}else{
			trace("Add user");
			m_tblUser.put(userId, new User(userId,stspwd));
		}
		
	}
	public void addNewCardToTheUser(String userId,String cardID,SupportedClaims theClaims){
		User user = m_tblUser.get(userId);
		if(user!=null){
			trace("User found");
			Card newCard = null;
			if(cardID == null) 
				newCard = Card.getNewCard(theClaims);
			else
				newCard = Card.getNewCard(cardID,theClaims);
			user.addCard(newCard.getCardId(), newCard);
		}
	}
	public void addNewCardToTheUser(String userId,SupportedClaims theClaims){
		addNewCardToTheUser(userId,null,theClaims);
	}
	public void configureService(String userId,Service svc){
		User user = m_tblUser.get(userId);
		if(user!=null){
			user.addService(svc);
		}else{
			trace("No user found");
		}
	}
	public void configureService(String userId, ServiceType type ,String ServiceuserId){
		User user = m_tblUser.get(userId);
		if(user!=null){
			trace("User found");
			Service svc = null;
			if(type == ServiceType.SDD)
				svc = Service.createSDDServiceForUser(userId, ServiceuserId);
			if(type == ServiceType.PaymentCard)
				svc = Service.createPaymentCard(userId, ServiceuserId);
			if(type == ServiceType.Wallet){
				svc = Service.createWalletServiceForUser(userId, ServiceuserId);
			}
			if(type == ServiceType.DRIVERLICENCE){
				svc = Service.createDriverLicenceService(userId, ServiceuserId);
			}
			if(type == ServiceType.EID){
				svc = Service.createEIDService(userId, ServiceuserId);			
			}
			if(type == ServiceType.TELCOS){
				svc = Service.createTelcosService(userId, ServiceuserId); 
			}
			if(type==ServiceType.RAC){
				svc = Service.createRACService(userId, ServiceuserId);
			}
			user.addService(svc);
		}
	}
	public Card getCardByCardID(String cardId) throws CardNotFoundExecption{
		Iterator<User> it = m_tblUser.values().iterator();
		while(it.hasNext()){
			User current = it.next();
			Card card = current.getCardById(cardId);
			if(card!= null) {trace("Data connector found the card");return card;}
		}
		throw new CardNotFoundExecption(cardId);
	}
	public Vector<Card> getCardsByUserId(String userId){
		
		return m_tblUser.get(userId).getCards();
		
	}
	public void reset(){
		m_tblUser.clear();
	}
	
}

