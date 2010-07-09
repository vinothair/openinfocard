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
package com.awl.ws.v2.impl;

import java.io.IOException;
import java.util.Vector;

import javax.xml.soap.SOAPConstants;
import javax.xml.soap.SOAPException;
import javax.xml.transform.TransformerException;

import org.xmldap.crypto.CryptoUtils;
import org.xmldap.exceptions.CryptoException;

import com.awl.fc2.plugin.authenticationHandler.IPlugInAuthenticationHandler;
import com.awl.fc2.selector.Selector;
import com.awl.fc2.selector.authentication.AuthenticationConfig;
import com.awl.fc2.selector.exceptions.Config_Exception_NotDone;
import com.awl.fc2.selector.exceptions.Config_Exeception_MalFormedConfigFile;
import com.awl.fc2.selector.exceptions.Config_Exeception_UnableToReadConfigFile;
import com.awl.fc2.selector.exceptions.FC2Authentication_Exeception_AuthenticationFailed;
import com.awl.fc2.selector.exceptions.WSTrustProtocol_Exeception_IS_NOT_Configure;
import com.awl.fc2.selector.launcher.Config;
import com.awl.fc2.selector.session.SessionSelector;
import com.awl.fc2.selector.utils.Hexify;
import com.awl.logger.Logger;
import com.awl.rd.fc2.claims.CBSupportedClaims;
import com.awl.rd.fc2.claims.CardsSupportedClaims;
import com.awl.ws.messages.IMetaDataExchange;
import com.awl.ws.messages.IRequestSecurityToken;
import com.awl.ws.messages.IRequestSecurityTokenResponse;
import com.awl.ws.messages.impl.MetaDataExchange;
import com.awl.ws.messages.impl.RequestSecurityToken_1_2;
import com.awl.ws.messages.impl.RequestSecurityToken_1_3;
import com.utils.Base64;

public class WSTrust_Protocol {
	static Logger log = new Logger(WSTrust_Protocol.class);
	public static void trace(Object msg){
		log.trace(msg);
	}
	public WSTrust_Protocol(){
		
	}
	public WSTrust_Protocol(AuthenticationConfig config){
		this.config = config;
	}
	
	/**
	 * CONFIGURATION
	 */
	String strSOAPProcotolInUse;
	AuthenticationConfig config =null;
	String cardId;
	SessionSelector session = null;
	boolean cnfDone = false;
	String RST_Version ="1.1";
	String username;
	String selmac_protocol=null;//Just for Infocard with SmartCardToken
	String SSO_TrustChain = "NOT_DEFINED";
	/////
	/**
	 * DATA After MEX
	 */
	
	//String tokenInUse = null;
	Vector<String> serverPossibleTokens = new Vector<String>();
	boolean isProcessFinished = false;
	////
	
	//mex
//	public final String TOKEN_CAS = "SmartCardToken";
//	public final String TOKEN_SSO = "SSOToken";
	public final String TOKEN_PWD = "UsernameToken";
//	public final String TOKEN_PKI = "X509Token";
	
	//card
//	public final String CREDENTIAL_CAS = "CASAuthenticate";//"SmartCardCredential";
//	public final String CREDENTIAL_SSO = "MapAuthenticatice";//"SSOCredential";
//	public final String CREDENTIAL_PWD = "UserNamePasswordAuthenticate";//"UsernamePasswordCredential";
//	public final String CREDENTIAL_PKI = "X509V3Credential";
	IRequestSecurityTokenResponse rstrFinal = null;
	
//	//crré la rst
//	IToken createToken() throws FC2Authentication_Exeception_AuthenticationFailed{
//		trace("Dont forget to add CredentialStore");
//		trace("Looking for " + tokenInUse);
//		if(TOKEN_PKI.equalsIgnoreCase(tokenInUse)){
//			PKIToken token = new PKIToken("o");
//			
//			trace("FIND THE CERTIFICATE FROM THE HASH... WHERE TO WE GET THE HASH ??");
//			PKIHandler pki = session.getCredentialStore().getPKIHandlerAssociatedToTheCard(config.card);			
//			try {
//				token.setPKIHandler(pki);
//			} catch (PKIHandler_Exeception e) {
//				trace("PKI problem");
//			}
//			return token;
//		}
//		if(TOKEN_SSO.equalsIgnoreCase(tokenInUse)){			
//			try {
//				return session.getCredentialStore().createToken(cardId,"SSOToken",config.userName,SSO_TrustChain , this.stsURL);
//			} catch (Config_Exception_NotDone eConf) {
////				trace("Configuration problem");
////				IMapAuthenticationUI map = null;
////				try {
////					try {
////						map = Selector.getInstance().getUI().getMapUI();
////					} catch (Config_Exception_NotDone e) {
////						throw new FC2Authentication_Exeception_AuthenticationFailed("Configuration problem");
////					}
////					map.setUserName(username);
////					map.doAuthentication();
////				} catch (Config_Exeception_UnableToReadConfigFile e) {
////					// TODO Auto-generated catch block
////					e.printStackTrace();
////				} catch (Config_Exeception_MalFormedConfigFile e) {
////					// TODO Auto-generated catch block
////					e.printStackTrace();
////				}
////				
////				SSOToken token = new SSOToken("o");
////				token.setUserName(map.getUserName());
////				String theTicket = map.getTicket();
////				trace("Put the following ticket in the credentialstore" + theTicket);
////				token.setSSOToken(map.getTicket());
//				return null;
//			}
//		}
//		if(TOKEN_PWD.equalsIgnoreCase(tokenInUse)){
//
//			try {
//				trace("Ask to the credential store");
//				return session.getCredentialStore().createToken(cardId, "UsernameToken", config.userName, null, this.stsURL);
//			} catch (Config_Exception_NotDone e) {
//				trace("Configuration problem");
////				UserNameToken token = new UserNameToken("o");
////				String pwd = "";
////				try {
////					if(username==null)
////						username = Selector.getInstance().getUI().getUserNameTokenUI().getUserName();
//////					if(mapPwd.containsKey(cardId)) {
//////						pwd = mapPwd.get(cardId);
//////					}else
////					{
////						pwd = Selector.getInstance().getUI().getUserNameTokenUI().getPWD();
//////						curContext=new CurrentContext();
//////						curContext.setData(username, pwd, cardId);
////					}
////					
////				} catch (Config_Exeception_UnableToReadConfigFile e1) {
////					// TODO Auto-generated catch block
////					e1.printStackTrace();
////				} catch (Config_Exeception_MalFormedConfigFile e1) {
////					// TODO Auto-generated catch block
////					e.printStackTrace();
////				} catch (Config_Exception_NotDone e1) {
////					// TODO Auto-generated catch block
////					e.printStackTrace();
////				}
////					
////				token.setUserName(username);			
////			    token.setPWD(pwd);
//			}
//		    //return token; 
//		}
//		trace("No compatible token found");
//		return null;
//	}
	
	IRequestSecurityToken createEmptyRST(){
		if(IRequestSecurityToken.REQUEST_TYPE_ISSUING_1_2.equalsIgnoreCase(RST_Version)){
			return new RequestSecurityToken_1_2("wst", IRequestSecurityToken.REQUEST_TYPE_ISSUING_1_2);
		}
		if(IRequestSecurityToken.REQUEST_TYPE_ISSUING_1_3.equalsIgnoreCase(RST_Version)){
			return  new RequestSecurityToken_1_3("wst", IRequestSecurityToken.REQUEST_TYPE_ISSUING_1_3);
		}
	
		return null;
		
	}
	
	public void configure(String cardId,SessionSelector l_session){
		cnfDone = true;
		this.cardId = cardId;
		session = l_session;
		
		trace("Configuration");
		String strSOAPProtocol = SOAPConstants.SOAP_1_1_PROTOCOL;
		if(config.card == null){
			RST_Version =IRequestSecurityToken.REQUEST_TYPE_ISSUING_1_2;
		}else{
			if(config.card.getIssuer().contains("orange") &&
				!config.card.getIssuer().contains("Bandit")){
			trace("Activate WS-TRUST 1.3");
			RST_Version = IRequestSecurityToken.REQUEST_TYPE_ISSUING_1_3;
			strSOAPProtocol = SOAPConstants.SOAP_1_2_PROTOCOL;
			}else{
				RST_Version = IRequestSecurityToken.REQUEST_TYPE_ISSUING_1_2;
			}
		}
		strSOAPProcotolInUse = strSOAPProtocol;
		//selmac_protocol = "CAP";
		if(config != null) username = config.userName;
		trace("Negociating with the STS with the following parameters :");
		trace("- SOAPProcotol   : "+strSOAPProtocol);
		trace("- RST_VERSION    : "+RST_Version);
		trace("- Username       : "+ username);
		trace("- SELMAC_PROTOCOL: "+ selmac_protocol);
		
	}
	
	
	public void startProtocol() throws WSTrustProtocol_Exeception_IS_NOT_Configure{
		if(!cnfDone) throw new WSTrustProtocol_Exeception_IS_NOT_Configure("Please configure and give the session");
		trace("Starting protocol");
		doMEX();
	}
	public boolean isFinished(){
		return isProcessFinished;
	}
	
	/**
	 * HANDLING SESSION AND STUFF
	 * @throws FC2Authentication_Exeception_AuthenticationFailed 
	 * @throws Config_Exception_NotDone 
	 * 
	 */
	
	
	public void process() throws FC2Authentication_Exeception_AuthenticationFailed, Config_Exception_NotDone{
		trace("Process");
		Vector<IPlugInAuthenticationHandler> authHandlers = Config.getInstance().getPlugInDB().getAuthenticationHandlerPlugin();
		for(IPlugInAuthenticationHandler authHandler : authHandlers){
			trace("Trying handler : " + authHandler.getName());
			for(String tokenInUse : serverPossibleTokens){
				boolean goodHandler = false;
				/*if(config.card != null){
					authHandler.configureAuthentication(config, cardId);
					if(authHandler.getTokenType().equalsIgnoreCase(tokenInUse) && authHandler.isCardCompatible(config.card)){
						goodHandler = true;
					}	
				}else*/{
					if(authHandler.getAuthenticationURI().equalsIgnoreCase(config.uriAuthentication)){
						goodHandler = true;
					}else if(config.card != null){
						authHandler.configureAuthentication(config, cardId);
						if(authHandler.getTokenType().equalsIgnoreCase(tokenInUse) && authHandler.isCardCompatible(config.card)){
							goodHandler = true;
						}	
					}
				}
				if(goodHandler){
					authHandler.configureAuthentication(config, cardId);
					authHandler.configureRSTFactory(strSOAPProcotolInUse, RST_Version);
					authHandler.handleProtocol(stsURL,username);
					rstrFinal =authHandler.getFinalRSTR();
					if(authHandler.isStorableToken()){
						try {
							trace("Adding token into the credential store");
							Selector.getInstance().session.getCredentialStore().addToken(stsURL, authHandler.getTokenType(), authHandler.getToken());
						} catch (Config_Exeception_UnableToReadConfigFile e) {
							trace("Store Token impossible");
						} catch (Config_Exeception_MalFormedConfigFile e) {
							trace("Store Token impossible");
						}
					}
					
					return ;
				}
				
			}
			
		}
		isProcessFinished = true;
//		if(TOKEN_CAS.equalsIgnoreCase(tokenInUse)){
//			trace("DEAL WITH MULTIPLE RST");
//			SelMac selmac = new SelMac(selmac_protocol);
//			selmac.configureAuthentication(config, cardId);
//			selmac.configureRSTFactory(strSOAPProcotolInUse, RST_Version);
//			selmac.handleProtocol(stsURL,username);
//			rstrFinal =selmac.getFinalRSTR();
//			isProcessFinished = true;
//			return ;
//			
//		}else{
//			trace("Protocol that need an unique shot to the STS");
//			//IToken token =// getToken(session,cardID,mexResponse, theSTS);
//			IToken SecurityToken = createToken();
//			IRequestSecurityToken rst = createEmptyRST();
//			
//			
//			rst.setSOAPProtocol(this.strSOAPProcotolInUse);
//			rst.setEndPoint(stsURL);
//			rst.setTokenType(IRequestSecurityToken.TOKEN_TYPE_SAML11);
//			    
//			rst.setPPID(computePPID(config.urlRequestor, cardId));
//			rst.setRequestor(config.urlRequestor,config.certifRequestor);
//			rst.setCardId(cardId);
//			   
//		    for(String claim : config.requiredClaims){
//		    	rst.addClaims(claim);
//		    }
//			    
//			    //try {
//			rst.setAuthenticationHandler(SecurityToken);
//			trace("Sending RST");
//			try {
//				SOAPMessage soaprstr = rst.sendRST();
//				IRequestSecurityTokenResponse rstr = new RequestSecurityTokenResponse();				
//				rstr.setRSTR(soaprstr);
//				rstrFinal = rstr;
//			} catch (UnsupportedOperationException e) {
//				trace(e.getMessage());
//			} catch (SOAPException e) {
//				trace(e.getMessage());
//			} catch (IOException e) {
//				trace(e.getMessage());
//			}
//			isProcessFinished= true;
//			
//			
//		}
		
	}
	
	public boolean succeed(){
		
		return false;
	}
	
	public void run() throws WSTrustProtocol_Exeception_IS_NOT_Configure, FC2Authentication_Exeception_AuthenticationFailed{
		//configure();
		startProtocol();
		try{
			process();
		}catch (Config_Exception_NotDone e) {
				throw new WSTrustProtocol_Exeception_IS_NOT_Configure(e.getMessage());
		}
//		while(!isFinished()){
//			try {
//				process();
//			} catch (Config_Exception_NotDone e) {
//				throw new WSTrustProtocol_Exeception_IS_NOT_Configure(e.getMessage());
//			}
//		}
	}
	
	public IRequestSecurityTokenResponse getFinalResponse() throws FC2Authentication_Exeception_AuthenticationFailed{
		if (rstrFinal == null) throw new FC2Authentication_Exeception_AuthenticationFailed("Rstr null");
		else return rstrFinal;
	}
	
	String stsURL = null;
	///
	void doMEX(){
		trace("MetaData Exchange");
		IMetaDataExchange mex = new MetaDataExchange();
		mex.setSOAPProtocol(strSOAPProcotolInUse);
			trace("Configuring MEX for the url : " + config.urlMEX);
			mex.setMexURL(config.urlMEX);//.replace("https", "http"));
			
		try {
			mex.doRequest();
			if(mex.getSTSURL().size()==0){
				if(config.card==null) {
					throw(new FC2Authentication_Exeception_AuthenticationFailed("FC2Authentication.chooseSTS"));
				}
				stsURL =  config.card.getTokenServiceReference().get(0).getAddress();
				trace("We found on the card : " + stsURL);
				
			}else{
				stsURL =  mex.getSTSURL().get(0);
			}
			
			if(mex.getSupportedTokenURI().size()==0){
				if(config.card==null) {
					throw(new FC2Authentication_Exeception_AuthenticationFailed("FC2Authentication.chooseSTS"));
				}
				trace("A VERIFIER ICI");
//				
//				if(config.uriAuthentication.equalsIgnoreCase(CREDENTIAL_CAS)) tokenInUse = TOKEN_CAS;
//				else if (config.uriAuthentication.equalsIgnoreCase(CREDENTIAL_PWD)) tokenInUse = TOKEN_PWD;
//				else if (config.uriAuthentication.equalsIgnoreCase(CREDENTIAL_SSO)) tokenInUse = TOKEN_SSO;
//				else if (config.uriAuthentication.equalsIgnoreCase(CREDENTIAL_PKI)) tokenInUse = TOKEN_PKI;
//				//tokenInUse =  config.card.getTokenList().getSupportedTokens().get(0).toString();
//				
				trace("We found on the card : " + stsURL);
				
			}
			else
			{
//				if(config.uriAuthentication.equalsIgnoreCase(CREDENTIAL_CAS)) tokenInUse = TOKEN_CAS;
//				else if (config.uriAuthentication.equalsIgnoreCase(CREDENTIAL_PWD)) tokenInUse = TOKEN_PWD;
//				else if (config.uriAuthentication.equalsIgnoreCase(CREDENTIAL_SSO)) tokenInUse = TOKEN_SSO;
//				else if (config.uriAuthentication.equalsIgnoreCase(CREDENTIAL_PKI)) tokenInUse = TOKEN_PKI;
//				
//				if(mex.getSupportedTokenURI().get(0).equalsIgnoreCase(IMetaDataExchange.tokenType_X509)){
//					tokenInUse = TOKEN_PKI;
//				}
				//tokenInUse =  mex.getSupportedToken().get(mex.getSupportedToken().size()-1).getLocalName();
				serverPossibleTokens = mex.getSupportedTokenURI();
			}
			//Get extra  paramaters for SSOTOken
//			{
//				if(UserCredential.MAP.equalsIgnoreCase(config.uriAuthentication)){
//					Vector<Node> vecAuth = mex.getSupportedToken();
//					for(Node authMeth : vecAuth){
//						if("SSOToken".equalsIgnoreCase(authMeth.getLocalName())){
//							NodeList lst = authMeth.getChildNodes();
//							for(int i=0;i<lst.getLength();i++){
//								String subName = lst.item(i).getLocalName();
//								if("Policy".equalsIgnoreCase(subName)){
//									trace(subName);
//									for(int j=0;j<lst.item(i).getChildNodes().getLength();j++){
//										
//										String trustNode = lst.item(i).getChildNodes().item(j).getLocalName();
//										if("Trust".equalsIgnoreCase(trustNode)){
//											//String trustIn = lst.item(i).getChildNodes().item(j).getTextContent().trim();
//											String trustIn = "onsenfou";
//											trace("SSOToken, trust in  ["  +trustIn +"]");
//											SSO_TrustChain = trustIn;
//										}
//									}							
//								}
//							}
//						}
//						
//					
//					}
//				}
//			}
		} catch (UnsupportedOperationException e) {
			
		} catch (SOAPException e) {
			
		} catch (TransformerException e) {
			
		} catch (IOException e) {
			
		} catch (FC2Authentication_Exeception_AuthenticationFailed e) {
			
		}
		
		if(serverPossibleTokens.size() == 0){
			String authType = config.card.getTokenServiceReference().get(0).getUserCredential().getAuthType(); 
			trace("Authentication asked by the card : " + authType);
			serverPossibleTokens.add(TOKEN_PWD);
			stsURL =  config.card.getTokenServiceReference().get(0).getAddress();
		}
//		if(tokenInUse == null){
//			String authType = config.card.getTokenServiceReference().get(0).getUserCredential().getAuthType(); 
//			trace("Authentication asked by the card : " + authType);
//			tokenInUse = TOKEN_PWD;
//			stsURL =  config.card.getTokenServiceReference().get(0).getAddress();
//		}
		trace("MEX POST CONFIGURATION");
		trace("- URL ENDPOINT = " + stsURL);
		trace("- Protocols     = " +serverPossibleTokens);
		//System.exit(-1);
	}
	/**
	 * compute the PPID of for the actual RP.
	 * @param url the RP Url
	 * @param CardId the CardID of the choosen card
	 * @return the String representation of the PPID
	 */
	public static String computePPID(String url,String CardId){
		String res="";
		try {
			byte [] dig = CryptoUtils.byteDigest((url+CardId).getBytes(),Config.getDigestMethod());
			res =Hexify.encode(dig);
			return res;
		} catch (CryptoException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return  null;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			Config.getInstance("C:/tempp/cards/Config_Selecteur.xml",true);
			Selector.getInstance();
		} catch (Config_Exeception_UnableToReadConfigFile e) {
			trace("Not abble to locate the config file");
		} catch (Config_Exeception_MalFormedConfigFile e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Config_Exception_NotDone e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// TESTING WITH THE WALLET
		int TESTING_WALLET = 0;
		int TESTING_PKI_BANK_BANDIT = 1;
		int TESTING_PWD_BANK_BANDIT = 2;
		//int TESTING_CAS_WALLET = 3;
		int TESTING_CAS_PAYMENT=4;
		
		int curTest = TESTING_CAS_PAYMENT;
		if(curTest == TESTING_CAS_PAYMENT){
			trace("TESTING Payment");
			String username = "fjritaine";		
			Vector<String> lstOptionalClaims = new Vector<String>();
			String urlRequestor = "localhost";
			String certifRequestorB64 = "";
			Vector<String> lstRequiredClaims = new Vector<String>();
			lstRequiredClaims.add(CBSupportedClaims.paymentAmountO.uri+"?2");
			urlSTSWallet = "https://ip-bancaire.atosworldline.bancaire.test.fc2consortium.org/sts-payment-cas/mex/UserNamePasswordAuthenticate";//"http://ip-bancaire.atosworldline.bancaire.test.fc2consortium.org/sts-payment-cas/mex/UserNamePasswordAuthenticate";
			AuthenticationConfig choosenMethod = new AuthenticationConfig(username, null, urlSTSWallet, "CASAuthenticate");//selectorUI.onChooseAuthenticationConfig(l_vecConfigAuthentication);		
			choosenMethod.setQuery(lstRequiredClaims, lstOptionalClaims, urlRequestor, certifRequestorB64);				
			//----
			WSTrust_Protocol wstrust = new WSTrust_Protocol(choosenMethod);
			wstrust.configure(carIDPrefix+getCardIDFromUserId(username), null);
			try {
				wstrust.run();
			} catch (WSTrustProtocol_Exeception_IS_NOT_Configure e) {
				trace(e.getMessage());
			} catch (FC2Authentication_Exeception_AuthenticationFailed e) {
				trace(e.getMessage());
			}
			if(wstrust.succeed()){
				trace("Operation succeed");
			}
		}
		if(curTest == TESTING_WALLET){
			trace("TESTING WALLET");
			String username = "robert";		
			Vector<String> lstOptionalClaims = new Vector<String>();
			String urlRequestor = "localhost";
			String certifRequestorB64 = "";
			Vector<String> lstRequiredClaims = new Vector<String>();
			lstRequiredClaims.add(CardsSupportedClaims.listCardIdO.uri);	
			AuthenticationConfig choosenMethod = new AuthenticationConfig(username, null, urlSTSWallet, "UserNamePasswordAuthenticate");//selectorUI.onChooseAuthenticationConfig(l_vecConfigAuthentication);		
			choosenMethod.setQuery(lstRequiredClaims, lstOptionalClaims, urlRequestor, certifRequestorB64);				
			//----
			WSTrust_Protocol wstrust = new WSTrust_Protocol(choosenMethod);
			wstrust.configure(carIDPrefix+getCardIDFromUserId(username), null);
			try {
				wstrust.run();
			} catch (WSTrustProtocol_Exeception_IS_NOT_Configure e) {
				trace(e.getMessage());
			} catch (FC2Authentication_Exeception_AuthenticationFailed e) {
				trace(e.getMessage());
			}
			if(wstrust.succeed()){
				trace("Operation succeed");
			}
		}
		if(curTest == TESTING_WALLET){
			trace("TESTING WALLET");
			String username = "robert";		
			Vector<String> lstOptionalClaims = new Vector<String>();
			String urlRequestor = "localhost";
			String certifRequestorB64 = "";
			Vector<String> lstRequiredClaims = new Vector<String>();
			lstRequiredClaims.add(CardsSupportedClaims.listCardIdO.uri);	
			AuthenticationConfig choosenMethod = new AuthenticationConfig(username, null, urlSTSWallet, "UserNamePasswordAuthenticate");//selectorUI.onChooseAuthenticationConfig(l_vecConfigAuthentication);		
			choosenMethod.setQuery(lstRequiredClaims, lstOptionalClaims, urlRequestor, certifRequestorB64);				
			//----
			WSTrust_Protocol wstrust = new WSTrust_Protocol(choosenMethod);
			wstrust.configure(carIDPrefix+getCardIDFromUserId(username), null);
			try {
				wstrust.run();
			} catch (WSTrustProtocol_Exeception_IS_NOT_Configure e) {
				trace(e.getMessage());
			} catch (FC2Authentication_Exeception_AuthenticationFailed e) {
				trace(e.getMessage());
			}
			if(wstrust.succeed()){
				trace("Operation succeed");
			}
		}
		if(curTest == TESTING_PWD_BANK_BANDIT){
			trace("TESTING WALLET");
			String username = "fjritaine";		
			Vector<String> lstOptionalClaims = new Vector<String>();
			String urlRequestor = "localhost";
			String certifRequestorB64 = "";
			Vector<String> lstRequiredClaims = new Vector<String>();
			lstRequiredClaims.add("http://schemas.xmlsoap.org/ws/2005/05/identity/claims/surname");
			String urlSTS = "https://ip-bancaire.atosworldline.bancaire.test.fc2consortium.org/BanditIdP/services/MetadataUsernameToken";
			AuthenticationConfig choosenMethod = new AuthenticationConfig(username, null, urlSTS, "UserNamePasswordAuthenticate");//selectorUI.onChooseAuthenticationConfig(l_vecConfigAuthentication);		
			choosenMethod.setQuery(lstRequiredClaims, lstOptionalClaims, urlRequestor, certifRequestorB64);				
			//----
			WSTrust_Protocol wstrust = new WSTrust_Protocol(choosenMethod);
			String cardID  = "contextid:ip-bancaire.atosworldline.bancaire.test.fc2consortium.org:cfid:card_6013061182542822660:cardtype:RAC";
			wstrust.configure(cardID, null);
			try {
				wstrust.run();
			} catch (WSTrustProtocol_Exeception_IS_NOT_Configure e) {
				trace(e.getMessage());
			} catch (FC2Authentication_Exeception_AuthenticationFailed e) {
				trace(e.getMessage());
			}
			if(wstrust.succeed()){
				trace("Operation succeed");
			}
		}
		
		if(curTest == TESTING_PKI_BANK_BANDIT){
			trace("TESTING WALLET");
			String username = "fjritaine";		
			Vector<String> lstOptionalClaims = new Vector<String>();
			String urlRequestor = "localhost";
			String certifRequestorB64 = "";
			Vector<String> lstRequiredClaims = new Vector<String>();
			lstRequiredClaims.add("http://schemas.xmlsoap.org/ws/2005/05/identity/claims/surname");
			String urlSTS = "http://ip-bancaire.atosworldline.bancaire.test.fc2consortium.org/BanditIdP/services/MetadataX509Token";
			AuthenticationConfig choosenMethod = new AuthenticationConfig(username, null, urlSTS, "UserNamePasswordAuthenticate");//selectorUI.onChooseAuthenticationConfig(l_vecConfigAuthentication);		
			choosenMethod.setQuery(lstRequiredClaims, lstOptionalClaims, urlRequestor, certifRequestorB64);				
			//----
			WSTrust_Protocol wstrust = new WSTrust_Protocol(choosenMethod);
			String cardID  = "contextid:ip-bancaire.atosworldline.bancaire.test.fc2consortium.org:cfid:card_6013061182542822660:cardtype:RAC";
			wstrust.configure(cardID, null);
			try {
				wstrust.run();
			} catch (WSTrustProtocol_Exeception_IS_NOT_Configure e) {
				trace(e.getMessage());
			} catch (FC2Authentication_Exeception_AuthenticationFailed e) {
				trace(e.getMessage());
			}
			if(wstrust.succeed()){
				trace("Operation succeed");
			}
		}
		System.exit(0);
	}
	
	
	// FOR THE TEST WITH THE WALLET
	public static String getCardIDFromUserId(String userID) /*throws CryptoException*/{
		try {
			return Base64.encode(CryptoUtils.byteDigest(userID.getBytes(),Config.getDigestMethod()));
		} catch (CryptoException e) {
			trace("WTF");
			return "bla";
			
		}
	}
	static String carIDPrefix = "https://ip-bancaire.atosworldline.bancaire.test.fc2consortium.org/sts-wallet/card/";//"http://ip-bancaire.atosworldline.bancaire.test.fc2consortium.org/sts-wallet/";
	//static String carIDPrefix = "https://localhost:8080/sts/card/";//"http://ip-bancaire.atosworldline.bancaire.test.fc2consortium.org/sts-wallet/";
	static String urlSTSWallet=null;
	static {
		urlSTSWallet = "http://ip-bancaire.atosworldline.bancaire.test.fc2consortium.org/sts-wallet/mex/UserNamePasswordAuthenticate";
		//urlSTSWallet = "http://localhost:8080/sts/mex/UserNamePasswordAuthenticate";
	}

}
