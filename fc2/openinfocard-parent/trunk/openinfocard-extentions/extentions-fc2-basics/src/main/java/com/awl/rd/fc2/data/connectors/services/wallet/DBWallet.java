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
package com.awl.rd.fc2.data.connectors.services.wallet;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.xmldap.util.PropertiesManager;

import com.thoughtworks.xstream.XStream;

public class DBWallet {
	static Logger log = Logger.getLogger(DBWallet.class);
	public static void trace(Object msg){
		log.info(msg);
	}
	transient static DBWallet s_this = null;
	Hashtable<String, WalletUser> m_tblUserId_Wallet = null;
	protected DBWallet(){
		trace("Creating a new DBWallet");
		m_tblUserId_Wallet = new Hashtable<String, WalletUser>();
	}
	
	public static void main(String arg[]){
		DBWallet walletStore = DBWallet.getInstance();
		System.out.println(walletStore.listUsers());
		walletStore.addUser("stef");
		walletStore.addCardsForUserId("stef", "cardId", "THECARD");
		System.out.println(walletStore.listUsers());
		System.out.println(walletStore.getCardIDsFromUserId("stef"));
	}
	static String getStoragePath(){
		String l_strPath2XML = PropertiesManager.getInstance().getProperty("storageFile");
		if(l_strPath2XML==null){
			trace("CONFIG FILE ERROR");
			l_strPath2XML = "c:/DBWallet.xml";
			
		}
		l_strPath2XML= l_strPath2XML.replace(".xml", "_DBWallet.xml");
		return l_strPath2XML;
	}
	public void save(){
		String l_strPath2XML = getStoragePath();
		XStream xstream = new XStream();
		String destFile = null;
		try {

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
	static DBWallet load(DBWallet root){
		String destFile = getStoragePath();//PropertiesManager.getInstance().getProperty("storageFile");//"c:/dataConnector.xml";
		XStream xstream = new XStream();
		try {
			BufferedReader fichier = new BufferedReader(new FileReader(destFile));
			return (DBWallet) xstream.fromXML(fichier,new DBWallet());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}	
		return new DBWallet();
	}
	public static DBWallet getInstance(){
		if(s_this == null){
			s_this = load(null);
		}
		return s_this;
	}
	
	
	// Handle the Wallet Store
	public void addUser(String username){
		if(!(m_tblUserId_Wallet.containsKey(username))){
			WalletUser wallet = new WalletUser(username);
			m_tblUserId_Wallet.put(username, wallet);
			save();
		}
	}
	public void addPWDForCardIdAndUserId(String username,String cardId,String pwd){
		WalletUser wallet = null;
		if((m_tblUserId_Wallet.containsKey(username))){
			wallet = m_tblUserId_Wallet.get(username);
			wallet.addPWD(cardId, pwd);
		}
	}
	public void addCardsForUserId(String username,String cardId,String cardB64){
		WalletUser wallet = null;
		if(!(m_tblUserId_Wallet.containsKey(username))){
			trace("addCardsForUserId : No Wallet found for " + username);
			wallet = new WalletUser(username);
			m_tblUserId_Wallet.put(username, wallet);
			
		}else{
			trace("addCardsForUserId : Wallet found for " + username);
			wallet = m_tblUserId_Wallet.get(username);
		}
		wallet.addCard(cardId, cardB64);
		save();
	}
	public Vector<String> getCardIDsFromUserId(String username){
		trace("getCardIDsFromUserId : looking for " + username);
		if(m_tblUserId_Wallet.containsKey(username)){
			trace("getCardIDsFromUserId : Wallet found for " + username);
			return m_tblUserId_Wallet.get(username).listCardIds();
		}
		trace("getCardIDsFromUserId : No Wallet found for " + username);
		return null;
			
	}
	public void removeCardsFromUserIdAndCardId(String username,String CardId){
		trace("getCardIDsFromUserId : looking for " + username);
		if(m_tblUserId_Wallet.containsKey(username)){
			trace("getCardIDsFromUserId : Wallet found for " + username);
			m_tblUserId_Wallet.get(username).removeCard(CardId);
			save();
		}
		trace("getCardIDsFromUserId : No Wallet found for " + username);
	}
	public void removePWDFromUserIdAndCardId(String username,String CardId){
		trace("getCardIDsFromUserId : looking for " + username);
		if(m_tblUserId_Wallet.containsKey(username)){
			trace("getCardIDsFromUserId : Wallet found for " + username);
			m_tblUserId_Wallet.get(username).removePWD(CardId);
			save();
		}
		trace("getCardIDsFromUserId : No Wallet found for " + username);
	}
	public String getCardFromUserIdAndCardID(String username,String CardId){
		trace("getCardIDsFromUserId : looking for " + username);
		if(m_tblUserId_Wallet.containsKey(username)){
			trace("getCardIDsFromUserId : Wallet found for " + username);
			return m_tblUserId_Wallet.get(username).getCardFromCardId(CardId);
		}
		trace("getCardIDsFromUserId : No Wallet found for " + username);
		return "";
			
	}
	public String getPWDFromUserIdAndCardId(String username,String CardId){
		trace("getPWDFromUserIdAndCardId : looking for " + username);
		if(m_tblUserId_Wallet.containsKey(username)){
			trace("getPWDFromUserIdAndCardId : Wallet found for " + username);
			return m_tblUserId_Wallet.get(username).getPWDFromCardId(CardId);
		}
		trace("getPWDFromUserIdAndCardId : No Wallet found for " + username);
		return "";
	}
	public Vector<String> listUsers(){
		Vector<String> toRet = new Vector<String>();
		Set<String> keys = m_tblUserId_Wallet.keySet();
		Iterator<String> iter = keys.iterator();
		while (iter.hasNext())
		{
			String key = iter.next();
			toRet.add(key);
		}
		return toRet;
	}
}
class WalletUser{
	static Logger log = Logger.getLogger(WalletUser.class);
	public static void trace(Object msg){
		log.info(msg);
	}
	String userName;
	Hashtable<String, String> m_tblCardID_CardB64 = new Hashtable<String, String>();
	Hashtable<String, String> m_tblCardID_PWD = new Hashtable<String, String>();
	public WalletUser(String username){
		userName = username;
	}
	public void addCard(String cardId,String cardB64){
		m_tblCardID_CardB64.put(cardId, cardB64);
	}
	public void addPWD(String cardId,String pwd){
		trace("Add the pwd");
		m_tblCardID_PWD.put(cardId, pwd);
	}
	public Vector<String> listCardIds(){
		trace("ListCardIds : " + userName);
		Vector<String> toRet = new Vector<String>();
		Set<String> keys = m_tblCardID_CardB64.keySet();
		Iterator<String> iter = keys.iterator();
		while (iter.hasNext())
		{
			String key = iter.next();
			toRet.add(key);
		}
		return toRet;
	}
	public String getCardFromCardId(String cardId){
		String response = m_tblCardID_CardB64.get(cardId);
		if(response==null)return "";
		return response;
	}
	public String getPWDFromCardId(String cardId){
		String response = m_tblCardID_PWD.get(cardId);
		if(response==null)return "";
		return response;
	}
	public void removeCard(String cardId){
		m_tblCardID_CardB64.remove(cardId);
		m_tblCardID_PWD.remove(cardId);
	}
	public void removePWD(String cardId){	
		m_tblCardID_PWD.remove(cardId);
	}
}


