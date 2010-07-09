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
package com.awl.fc2.plugin.store.wallet;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import com.awl.fc2.plugin.authenticationHandler.username.PlugInAuthenticationHandler_UserName;
import com.awl.fc2.selector.Selector;
import com.awl.fc2.selector.exceptions.Config_Exception_NotDone;
import com.awl.fc2.selector.exceptions.Config_Exeception_MalFormedConfigFile;
import com.awl.fc2.selector.exceptions.Config_Exeception_UnableToReadConfigFile;
import com.awl.fc2.selector.exceptions.CredentialStore_Execption_FailedRetrieving;
import com.awl.fc2.selector.exceptions.FC2Authentication_Exeception_AuthenticationFailed;
import com.awl.fc2.selector.launcher.Config;
import com.awl.fc2.selector.session.SessionSelector;
import com.awl.fc2.selector.storage.ICredentialsStore;
import com.awl.fc2.selector.storage.utils.Utils;
import com.awl.fc2.selector.userinterface.lang.Lang;
import com.awl.logger.Logger;
import com.awl.rd.applications.common.exceptions.APP_Exception_InternalError;
import com.awl.rd.fc2.claims.CardsSupportedClaims;
import com.awl.ws.messages.authentication.IToken;
import com.utils.execeptions.XMLParser_Exception_NO_ATTRIBUTE;

public class CredentialStore implements ICredentialsStore {

	HashMap<String, IToken> mapSSOTokens = new HashMap<String, IToken>();
	HashMap<String, String> mapPwd= new HashMap<String, String>();
	SessionSelector theSession = null;
	static Logger log = new Logger(CredentialStore.class);
	static public void trace(Object obj){
		log.trace(obj);
	}
	boolean loadPassword = true;
	@Override
	public void configure(SessionSelector theSession) {
		this.theSession = theSession;
		try {
			loadPassword = Config.getInstance().distributedWeakCredentials();
		} catch (Config_Exception_NotDone e) {
			loadPassword = true;
		}
		
	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		mapSSOTokens.clear();		
		mapPwd.clear();
	}

	@Override
	public void reset() throws CredentialStore_Execption_FailedRetrieving {
		trace("Reset the Credentials Store");		
		/*if(loadPassword)*/
		
		loadPassword();

		

	}
	public void traceUI(String msg){
		try {
			Selector.getInstance().getUI().traceConsole(msg);
		} catch (Config_Exeception_UnableToReadConfigFile e) {
			trace("Cannot console");
		} catch (Config_Exeception_MalFormedConfigFile e) {
			trace("Cannot console");
		} catch (Config_Exception_NotDone e) {
			trace("Cannot console");
		}
	}
	public void cleanUI(){
		try {
			Selector.getInstance().getUI().clearConsole();
		} catch (Config_Exeception_UnableToReadConfigFile e) {
			trace("Cannot console");
		} catch (Config_Exeception_MalFormedConfigFile e) {
			trace("Cannot console");
		} catch (Config_Exception_NotDone e) {
			trace("Cannot console");
		}
	}
	public void loadPassword() throws CredentialStore_Execption_FailedRetrieving{
		trace("Loading Password");
		Vector<String> lstRequiredClaims = new Vector<String>();
		lstRequiredClaims.add(CardsSupportedClaims.listCardIdO.uri);		
		Map<String, String> response;
		try {
			response = Utils.getSTSResponse(theSession,theSession.getUsername(),lstRequiredClaims);
		} catch (FC2Authentication_Exeception_AuthenticationFailed e) {
			throw(new CredentialStore_Execption_FailedRetrieving(e.getMessage()));
		}
		cleanUI();
		if(response != null){
			String lstCards = (String)response.get(CardsSupportedClaims.listCardIdO.columnName);
			if(lstCards != null){
				String tabCardIds [] = Utils.String2Tab(lstCards);
				System.out.println("----------------------------");
				for(int i=0;i<tabCardIds.length;i++){
					trace("|||||||||||||| "+ tabCardIds[i]);
				}
				trace("----------------------------");
				for(int i=0;i<tabCardIds.length;i++){
					lstRequiredClaims.clear();
					traceUI(Lang.get(Lang.CHECK_REMEMBER_ME) + " " + (i+1) +"/" +tabCardIds.length);
					String request = CardsSupportedClaims.pwdCRDO.uri+"?"+tabCardIds[i].trim();
					trace("loading password : "+ request);
//					try {
//						System.in.read();
//					} catch (IOException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
					lstRequiredClaims.add(request);
					try {
						response = Utils.getSTSResponse(theSession,theSession.getUsername(),lstRequiredClaims);
					} catch (FC2Authentication_Exeception_AuthenticationFailed e) {
						throw(new CredentialStore_Execption_FailedRetrieving(e.getMessage()));
					}
					if(response != null){
						String pwd = (String) response.get(CardsSupportedClaims.pwdCRDO.columnName);
						if(pwd != null){														
							trace("Adding the passeword");
							mapPwd.put(tabCardIds[i].trim(), pwd);		
									
						}
					}
				}
			}
		}
		for(String key : mapPwd.keySet()){
			trace(">> " + key  +" | " + mapPwd.get(key));
		}
	}
	CurrentContext curContext = null;
//	@Override
//	public IToken createToken(String cardId,
//			String supportedToken, String username,
//			String param, String theSTS) throws Config_Exception_NotDone,
//			FC2Authentication_Exeception_AuthenticationFailed {
//		trace("createToken called for the STS : "+theSTS);
//		trace("Supported Token : " + supportedToken);
//		cardId = cardId.trim();
//		curContext = null;
//		if("UsernameToken".equalsIgnoreCase(supportedToken)){
//			UserNameToken token = new UserNameToken("o");
//			String pwd = "";
//			try {
//				if(username==null)
//					username = Selector.getInstance().getUI().getUserNameTokenUI().getUserName();
//				if(mapPwd.containsKey(cardId)) {
//					pwd = mapPwd.get(cardId);
//				}else
//				{
//					pwd = Selector.getInstance().getUI().getUserNameTokenUI().getPWD();
//					curContext=new CurrentContext();
//					curContext.setData(username, pwd, cardId);
//				}
//				
//			} catch (Config_Exeception_UnableToReadConfigFile e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			} catch (Config_Exeception_MalFormedConfigFile e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//				
//			token.setUserName(username);			
//		    token.setPWD(pwd);
//		    return token;
//		}
//		if("SSOToken".equalsIgnoreCase(supportedToken)){	
//			String trustedAS = param;
//			if(mapSSOTokens.containsKey(trustedAS)) {
//			//if(m_vecSSOToken.size() >= 1) {
//				trace("Suitable SSO Token has been found in CredentialStore, retrieving it");
//				SSOToken theToken = mapSSOTokens.get(trustedAS);
//				return theToken;
//			}
//			
//			//trace("Trust in = " + supportedToken.getFirstChild().getNextSibling().getFirstChild().getNodeValue());
//			IMapAuthenticationUI map = null;
//			try {
//				map = Selector.getInstance().getUI().getMapUI();
//				map.setUserName(username);
//				map.doAuthentication();
//			} catch (Config_Exeception_UnableToReadConfigFile e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			} catch (Config_Exeception_MalFormedConfigFile e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			
//			SSOToken token = new SSOToken("o");
//			token.setUserName(map.getUserName());
//			String theTicket = map.getTicket();
//			trace("Put the following ticket in the credentialstore" + theTicket);
//			token.setSSOToken(map.getTicket());
//			//m_vecSSOToken.add(token);trace("SSO Token added to credential store (" + m_vecSSOToken.size() + " items)");
//			addSSOToken(trustedAS, token);
//			trace("SSO Token added to credential store");
//			return token;
//		}
//		return null;
//	}

	public void addIToken(String uriAuthenticationServer,IToken token){
		mapSSOTokens.put(uriAuthenticationServer, token);
		configureTimer();
	}
	@Override
	public void saveCurrentToken() {
		trace("Saving current Context");
		if(curContext !=null){
			mapPwd.put(curContext.cardId, curContext.pwd);
//			Vector<String> lstRequiredClaims = new Vector<String>();
//			lstRequiredClaims.add(CardsSupportedClaims.pwdCRDO.uri+"?"+curContext.cardId+"--"+curContext.pwd);
//			Utils.getSTSResponse(theSession, curContext.username, lstRequiredClaims);
		}
		
	}

	@Override
	public void addPwdForCardId(String cardId, String pwd) {
		mapPwd.put(cardId, pwd);
		
	}

	@Override
	public void removePassword() {
		mapPwd.clear();
		
	}

	@Override
	public void removeSSOToken() {
		mapSSOTokens.clear();
		
	}

	public final int seconds = 15;
	public void configureTimer(){
		trace("Configuring timer in order to remove SSOToken in " + seconds +"s.");
		if(timer!=null){
			timer.cancel();
			timer = null;
		}
		 timer = new Timer();
		 timer.schedule(new RemoveTokenTask(), seconds * 1000);
	}
	 Timer timer=null;
	class RemoveTokenTask extends TimerTask {
	    public void run() {
	      trace("-----------------------------------------");
	      trace("|-- REMOVING SSO AUTHENTICATION TOKEN --|");
	      trace("-----------------------------------------");
	      removeSSOToken();
	      timer.cancel(); //Not necessary because we call System.exit
	      //System.exit(0); //Stops the AWT thread (and everything else)
	    }
	  }
//	@Override
//	public PKIHandler getPKIHandlerAssociatedToTheCard(InfoCard card) {
//		String hashCErtificate = card.getTokenServiceReference().get(0).getUserCredential().getX509Hash();
//		return new PKIHandler(hashCErtificate);
//	}

	@Override
	public IToken getToken(String cardId, String supportedToken,
			String username, String param, String theSTS)
			throws Config_Exception_NotDone,
			FC2Authentication_Exeception_AuthenticationFailed {
		IToken token = mapSSOTokens.get(theSTS+supportedToken);
		if(!"UsernameToken".equalsIgnoreCase(supportedToken)){
			token = mapSSOTokens.get(/*theSTS+*/supportedToken);
		}
		
		
		if(token == null && "UsernameToken".equalsIgnoreCase(supportedToken)){
			PlugInAuthenticationHandler_UserName authUser = new PlugInAuthenticationHandler_UserName();			
			authUser.configureAuthentication(null, cardId);
			String pwd = mapPwd.get(cardId);
			
			try {
				token = authUser.createToken(theSTS, username,pwd);
				curContext = new CurrentContext();
				curContext.pwd = authUser.getPWD();
				curContext.username = username;
				curContext.cardId = cardId;
			} catch (APP_Exception_InternalError e) {
				return null;
			} catch (Config_Exeception_UnableToReadConfigFile e) {
				return null;
			} catch (Config_Exeception_MalFormedConfigFile e) {
				return null;
			} catch (XMLParser_Exception_NO_ATTRIBUTE e) {
				return null;
			}
		}
		return token;
//		trace("createToken called for the STS : "+theSTS);
//		trace("Supported Token : " + supportedToken);
//		cardId = cardId.trim();
//		curContext = null;
//		if("UsernameToken".equalsIgnoreCase(supportedToken)){
////			UserNameToken token = new UserNameToken("o");
////			String pwd = "";
//			try {
//				if(username==null)
//					username = Selector.getInstance().getUI().getUserNameTokenUI().getUserName();
//				if(mapPwd.containsKey(cardId)) {
//					pwd = mapPwd.get(cardId);
//				}else
//				{
//					pwd = Selector.getInstance().getUI().getUserNameTokenUI().getPWD();
//					curContext=new CurrentContext();
//					curContext.setData(username, pwd, cardId);
//				}
//				
//			} catch (Config_Exeception_UnableToReadConfigFile e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			} catch (Config_Exeception_MalFormedConfigFile e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//				
//			token.setUserName(username);			
//		    token.setPWD(pwd);
//		    return token;
//		}
//		if("SSOToken".equalsIgnoreCase(supportedToken)){	
//			String trustedAS = param;
//			if(mapSSOTokens.containsKey(trustedAS)) {
//			//if(m_vecSSOToken.size() >= 1) {
//				trace("Suitable SSO Token has been found in CredentialStore, retrieving it");
//				SSOToken theToken = mapSSOTokens.get(trustedAS);
//				return theToken;
//			}
//			
//			//trace("Trust in = " + supportedToken.getFirstChild().getNextSibling().getFirstChild().getNodeValue());
//			IMapAuthenticationUI map = null;
//			try {
//				map = Selector.getInstance().getUI().getMapUI();
//				map.setUserName(username);
//				map.doAuthentication();
//			} catch (Config_Exeception_UnableToReadConfigFile e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			} catch (Config_Exeception_MalFormedConfigFile e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			
//			SSOToken token = new SSOToken("o");
//			token.setUserName(map.getUserName());
//			String theTicket = map.getTicket();
//			trace("Put the following ticket in the credentialstore" + theTicket);
//			token.setSSOToken(map.getTicket());
//			//m_vecSSOToken.add(token);trace("SSO Token added to credential store (" + m_vecSSOToken.size() + " items)");
//			addSSOToken(trustedAS, token);
//			trace("SSO Token added to credential store");
//			return token;
//		}
		
	}

	@Override
	public void addToken(String theSTS, String tokenType, IToken token) {
		String key =theSTS+tokenType;
		if(!"UsernameToken".equalsIgnoreCase(tokenType)){
			key=tokenType;
		}
		mapSSOTokens.put(key, token);
		configureTimer();
		
	}
	
}



class CurrentContext{
	public String username;
	public String pwd;
	public String cardId;
	public void setData(String username,String pwd,String cardId){
		this.username = username;
		this.pwd = pwd;
		this.cardId = cardId;
		
	}
}
