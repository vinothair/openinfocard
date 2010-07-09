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
package com.awl.fc2.selector.session;

import javax.swing.JOptionPane;

import com.awl.fc2.plugin.session.IPlugInSession;
import com.awl.fc2.selector.Selector;
import com.awl.fc2.selector.exceptions.CardStore_Execption_FailedRetrieving;
import com.awl.fc2.selector.exceptions.Config_Exception_NotDone;
import com.awl.fc2.selector.exceptions.Config_Exeception_MalFormedConfigFile;
import com.awl.fc2.selector.exceptions.Config_Exeception_UnableToReadConfigFile;
import com.awl.fc2.selector.exceptions.CredentialStore_Execption_FailedRetrieving;
import com.awl.fc2.selector.launcher.Config;
import com.awl.fc2.selector.storage.ICardStoreV2;
import com.awl.fc2.selector.storage.ICredentialsStore;
import com.awl.fc2.selector.userinterface.lang.Lang;
import com.awl.logger.Logger;

public class SessionSelector {

	String username;
	public String getUsername(){
		return username;
	}
	public void setUsername(String username){
		this.username = username;
	}
	static Logger log = new Logger(SessionSelector.class);
	static public void trace(Object obj){
		log.trace(obj);
	}
	//
//	protected ICardStoreV2 cardStore = null;
//	protected ICredentialsStore credStore = null;
//	//FC2TrayIcon trayicon = null;
	public ICardStoreV2 getCardStore() {
		try {
			return Config.getInstance().getCardStorage();
		} catch (Config_Exception_NotDone e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	public ICredentialsStore getCredentialStore(){
		return getCardStore().getCredentialStore();
	}
//	public void configure() throws Config_Exeception_MalFormedConfigFile, Config_Exception_NotDone{
//		// Init cardstore
//		trace("Configure Session");
//		
//		cardStore = Config.getInstance().getCardStorage();
//		trace("Using Cardstore : " + cardStore);
//
//				 
//		if(cardStore != null){
//			cardStore.configure(this);
//		}
//		
//		// Init credentials store
//		credStore = new CredentialStore();
//		if(credStore != null){
//			credStore.configure(this);
//		}
//		
//		trayicon = new FC2TrayIcon();
//		trayicon.configure(this);
//		
//		
////		if (Config.getInstance().getUseWallet().equalsIgnoreCase("yes")) {
////		trace("using wallet : YES");
////		cardStore = new CardStore();
////	}
////	else {
////		trace("using wallet : NO");
////		cardStore = new LocalCardStore();
////	}
//	}

	public void configure() throws Config_Exeception_MalFormedConfigFile, Config_Exception_NotDone{
		// Init cardstore
		trace("Configure Session");
		for(IPlugInSession plg : Config.getInstance().getPlugInDB().getSessionElementPlugin()){
			plg.getSessionElement().configure(this);
		}
		if(getCredentialStore() != null){
			try {
				getCredentialStore().configure(this);
			} catch (Exception e) {
				trace("Failed in configuring Credential Store");
			}
		}
//		cardStore = Config.getInstance().getCardStorage();
//		trace("Using Cardstore : " + cardStore);
//
//				 
//		if(cardStore != null){
//			cardStore.configure(this);
//		}
//		
//		// Init credentials store
//		credStore = new CredentialStore();
//		if(credStore != null){
//			credStore.configure(this);
//		}
//		
//		trayicon = new FC2TrayIcon();
//		trayicon.configure(this);
		
		
//		if (Config.getInstance().getUseWallet().equalsIgnoreCase("yes")) {
//		trace("using wallet : YES");
//		cardStore = new CardStore();
//	}
//	else {
//		trace("using wallet : NO");
//		cardStore = new LocalCardStore();
//	}
	}

	boolean bOpen = false;
	public boolean isOpen(){
		return bOpen;
	}
	public void open() throws Config_Exeception_UnableToReadConfigFile, Config_Exeception_MalFormedConfigFile, Config_Exception_NotDone{
		if(!bOpen){
			trace("Opening Session");
			Selector.getInstance().getUI().wakeup();
//			credStore.destroy();
			try {
//				cardStore.reset();
//				credStore.reset();
//				trayicon.reset();
				for(IPlugInSession plg : Config.getInstance().getPlugInDB().getSessionElementPlugin()){
					plg.getSessionElement().reset();
				}
				if(getCredentialStore() != null){
					getCredentialStore().reset();
				}
				bOpen = true;
			} catch (CardStore_Execption_FailedRetrieving e) {
				bOpen = false;
			} catch (CredentialStore_Execption_FailedRetrieving e) {
				bOpen = false;
			}
			
			Selector.getInstance().getUI().sleep();
			
		}else{
			JOptionPane.showOptionDialog(null, Lang.get(Lang.ALLREADY_LOG), "JSS", JOptionPane.CLOSED_OPTION, JOptionPane.WARNING_MESSAGE, null, null, null);
			
		}
	}
	public void close(){
		trace("Closing Session");
		bOpen = false;
		getCredentialStore().destroy();
		getCardStore().destroy();
		
	}
	
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
