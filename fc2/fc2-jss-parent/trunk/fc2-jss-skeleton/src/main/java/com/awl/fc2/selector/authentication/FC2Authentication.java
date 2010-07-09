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
package com.awl.fc2.selector.authentication;

import java.io.IOException;
import java.util.Vector;

import javax.swing.JOptionPane;
import javax.xml.soap.SOAPException;
import javax.xml.transform.TransformerException;

import com.awl.fc2.selector.Selector;
import com.awl.fc2.selector.exceptions.Config_Exception_NotDone;
import com.awl.fc2.selector.exceptions.Config_Exeception_MalFormedConfigFile;
import com.awl.fc2.selector.exceptions.Config_Exeception_UnableToReadConfigFile;
import com.awl.fc2.selector.exceptions.FC2Authentication_Exeception_AuthenticationFailed;
import com.awl.fc2.selector.exceptions.WSTrustProtocol_Exeception_IS_NOT_Configure;
import com.awl.fc2.selector.session.SessionSelector;
import com.awl.logger.Logger;
import com.awl.ws.messages.DisplayTokenElement;
import com.awl.ws.messages.IMetaDataExchange;
import com.awl.ws.messages.IRequestSecurityTokenResponse;
import com.awl.ws.messages.impl.MetaDataExchange;
import com.awl.ws.v2.impl.WSTrust_Protocol;
/**
 * This object performs the authentication with the STS.<br/>
 * In order to perform an authentication, this object should be constructed with a {@link AuthenticationConfig}. 
 * @author Cauchie st�phane
 * @author Fran�ois julien ritaine
 */
public class FC2Authentication {
	String stsSAMLAssertion ="";
	Vector<DisplayTokenElement> vecDisplayToken;
	final static public int STATUS_FAILED =0;
	final static public int STATUS_SUCCEED=1;
	final static public int STATUS_PROGRESS=2;
	int status= STATUS_PROGRESS;
	static Logger log = new Logger(FC2Authentication.class);
	AuthenticationConfig config;
	static public void trace(Object ob){
		log.trace(ob);
	}
	public Vector<DisplayTokenElement> getDisplayToken(){
		return vecDisplayToken;
	}
	/**
	 * The possible status are : <br/>
	 * - FC2Authentication.STATUS_FAILED <br/>
	 * - FC2AuthenticationSTATUS_SUCCEED  <br/>
	 * - FC2Authentication.STATUS_PROGRESS  <br/>
	 * @return the authentication status
	 */
	public int getStatus(){
		return status;
	}
	/**
	 * FC2Authentication can only be constructed with an {@link AuthenticationConfig}
	 * @param config 
	 */
	public FC2Authentication(AuthenticationConfig config) {
		this.config = config;
	}
	
	
	/** 
	 * @return the string representation of the SAML assertion returned by the STS (if the getStatus()==STATUS_SUCCEED)
	 */
	public String getTicket(){		
		return stsSAMLAssertion;
	}
	
	public void trace2UI(String msg){
		try {
			Selector.getInstance().getUI().traceConsole("-"+msg);
		} catch (Config_Exeception_UnableToReadConfigFile e) {
			trace("Problem of tracing to UI");
		} catch (Config_Exeception_MalFormedConfigFile e) {
			trace("Problem of tracing to UI");
		} catch (Config_Exception_NotDone e) {
			trace("Problem of tracing to UI");
		}
	}
	
	/**
	 * Internal function that perform the request to the STS. <br/>
	 * 1) Proceed the MEX<br/>
	 * 2) Choose the STS {@link FC2Authentication#chooseSTS(IMetaDataExchange)}<br/>
	 * 3) From the Mex response get the corresponding authenticator token {@link FC2Authentication#getToken(IMetaDataExchange, String)}<br/>
	 * 4) Send the RST.<br/>
	 * @throws FC2Authentication_Exeception_AuthenticationFailed
	 * @throws Config_Exception_NotDone if the config object is not initialized (call {@link Config#getInstance(String)})
	 */
//	public void innerAuthent(SessionSelector session, String cardID,String strSOAPProtocol,IRequestSecurityToken rst) throws FC2Authentication_Exeception_AuthenticationFailed, Config_Exception_NotDone{
//		
//		IMetaDataExchange mexResponse = processMetaDataExchange(strSOAPProtocol);
//		
//		
//		trace2UI("Negociating the protocol");
//		String theSTS = chooseSTS(mexResponse);		
//		IToken token = getToken(session,cardID,mexResponse, theSTS);
//		System.out.println("MEX Finished");		
//		
////		trace("List of supported Token");
////		for(int i=0;i<config.card.getTokenList().getSupportedTokens().size();i++){
////			try {
////				trace("- " + config.card.getTokenList().getSupportedTokens().get(i).toXML());
////			} catch (SerializationException e) {
////				// TODO Auto-generated catch block
////				e.printStackTrace();
////			}
////		}
//		
//	    rst.setSOAPProtocol(strSOAPProtocol);
//		rst.setEndPoint(theSTS);
//	    rst.setTokenType(IRequestSecurityToken.TOKEN_TYPE_SAML11);
//	    
//	    rst.setPPID(computePPID(config.urlRequestor, cardID));
//	    rst.setRequestor(config.urlRequestor,config.certifRequestor);
//	    rst.setCardId(cardID);
//	   
//	    for(String claim : config.requiredClaims){
//	    	rst.addClaims(claim);
//	    }
//	    
//	    //try {
//	    	rst.setAuthenticationHandler(token);
//	    	try {
//	    		trace2UI("Sending Authentication Message");
//				SOAPMessage soaprstr = rst.sendRST();
//				IRequestSecurityTokenResponse rstr = new RequestSecurityTokenResponse();				
//				rstr.setRSTR(soaprstr);
//				trace2UI("Decoding response");
//				stsSAMLAssertion = rstr.getSAMLAssertionXML();
//				vecDisplayToken = rstr.getDisplayToken();				
//				trace("Get the display token = " + vecDisplayToken);
//				status = rstr.getStatus();
//				session.getCredentialStore().saveCurrentToken();
//			} catch (UnsupportedOperationException e) {
//				trace2UI("Failed decoding response");
//				status = STATUS_FAILED;
//				throw(new FC2Authentication_Exeception_AuthenticationFailed(e.getMessage()));
//			} catch (SOAPException e) {
//				trace2UI("Failed decoding response");
//				status = STATUS_FAILED;
//				throw(new FC2Authentication_Exeception_AuthenticationFailed(e.getMessage()));
//			} catch (IOException e) {
//				trace2UI("Failed decoding response");
//				status = STATUS_FAILED;
//				throw(new FC2Authentication_Exeception_AuthenticationFailed(e.getMessage()));
//			}
//	    	
//			
//	    
//	}

	/**
	 * Perform the authentication and try to get the SAML response from the STS.
	 * @param cardId the cardId (if null we take the one in the infocard)
	 * @throws FC2Authentication_Exeception_AuthenticationFailed
	 * @throws Config_Exception_NotDone
	 */
	public void doAuthentication(String cardId,SessionSelector l_session) throws FC2Authentication_Exeception_AuthenticationFailed, Config_Exception_NotDone{		
		stsSAMLAssertion = "";
		if(cardId == null){
			cardId =  config.card.getCardId().replace(":443", "");
		}
		
		
		FC2Authentication_Exeception_AuthenticationFailed lastError=null;
		
		
		WSTrust_Protocol wstrust = new WSTrust_Protocol(config);
		wstrust.configure(cardId,l_session);
		try {
			wstrust.run();
			IRequestSecurityTokenResponse rstr = wstrust.getFinalResponse();
			stsSAMLAssertion = rstr.getSAMLAssertionXML();
			vecDisplayToken = rstr.getDisplayToken();
			trace("Get the display token = " + vecDisplayToken);
			status = rstr.getStatus();
			l_session.getCredentialStore().saveCurrentToken();
		} catch (WSTrustProtocol_Exeception_IS_NOT_Configure e) {			
			status = STATUS_FAILED;
		} catch (FC2Authentication_Exeception_AuthenticationFailed e) {
			lastError = e;
			status = STATUS_FAILED;
		}
//		trace("doAuthentication, checking the Issuer to deduce the right version");
//		//trace("-->" + config.card.getIssuer());
//		IRequestSecurityToken rst;
//		String strSOAPProtocol = SOAPConstants.SOAP_1_1_PROTOCOL;
//		if(config.card == null){
//			rst = new RequestSecurityToken_1_2("wst", IRequestSecurityToken.REQUEST_TYPE_ISSUING_1_2);
//		}else{
//			if(config.card.getIssuer().contains("orange") &&
//				!config.card.getIssuer().contains("Bandit")){
//			trace("Activate WS-TRUST 1.3");
//			rst = new RequestSecurityToken_1_3("wst", IRequestSecurityToken.REQUEST_TYPE_ISSUING_1_3);
//			strSOAPProtocol = SOAPConstants.SOAP_1_2_PROTOCOL;
//			}else{
//				rst = new RequestSecurityToken_1_2("wst", IRequestSecurityToken.REQUEST_TYPE_ISSUING_1_2);
//			}
//		}
//		
//		FC2Authentication_Exeception_AuthenticationFailed lastError=null;
//		for(int nbTry=0;nbTry<3 && status != STATUS_SUCCEED;nbTry++){
//			try {
//				innerAuthent(l_session,cardId, strSOAPProtocol, rst);
//			} catch (FC2Authentication_Exeception_AuthenticationFailed e) {
//				lastError = e;
//				trace("Impossible to authenticate the user");
//				status = STATUS_FAILED;
//			}
//			
//			if(status == STATUS_FAILED){
//				String info = "You have failed your authentication \n you have " + (3-nbTry) +"tries left";
//				JOptionPane.showConfirmDialog(null,info,"Authentication process",JOptionPane.OK_CANCEL_OPTION);
//
//			}
//		}
		

		if(status==STATUS_FAILED){
			String info = "You have failed your authentication.";
			JOptionPane.showConfirmDialog(null,info,"Authentication process",JOptionPane.OK_CANCEL_OPTION);
				throw(lastError);
		}
		
	}
	
//	/**
//	 * compute the PPID of for the actual RP.
//	 * @param url the RP Url
//	 * @param CardId the CardID of the choosen card
//	 * @return the String representation of the PPID
//	 */
//	public static String computePPID(String url,String CardId){
//		String res="";
//		try {
//			byte [] dig = CryptoUtils.byteDigest((url+CardId).getBytes());
//			res =Hexify.encode(dig);
//			return res;
//		} catch (CryptoException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		return  null;
//	}
	
	/**
	 * Proceed to the MetaDataExchange
	 * @return {@link IMetaDataExchange}
	 * @throws FC2Authentication_Exeception_AuthenticationFailed if a problem occurs
	 */
	public IMetaDataExchange processMetaDataExchange(String strSOAPProtocol) throws FC2Authentication_Exeception_AuthenticationFailed{
		IMetaDataExchange mex = new MetaDataExchange();
		mex.setSOAPProtocol(strSOAPProtocol);
			trace("Configuring MEX for the url : " + config.urlMEX);
			mex.setMexURL(config.urlMEX);//.replace("https", "http"));
		try {
			mex.doRequest();
			return mex;
			
		} catch (UnsupportedOperationException e) {
			return mex;
			//status = STATUS_FAILED;
			//throw(new FC2Authentication_Exeception_AuthenticationFailed(e.getMessage()));
		} catch (SOAPException e) {
			return mex;
			//status = STATUS_FAILED;
			//throw(new FC2Authentication_Exeception_AuthenticationFailed(e.getMessage()));
		} catch (TransformerException e) {
			return mex;
			//status = STATUS_FAILED;
			//throw(new FC2Authentication_Exeception_AuthenticationFailed(e.getMessage()));
		} catch (IOException e) {
			return mex;
			//status = STATUS_FAILED;
			//throw(new FC2Authentication_Exeception_AuthenticationFailed(e.getMessage()));
		}
	}
	
	/**
	 * Parse the MEX XML response and then call the credential store in order to get the token. 
	 * @param mexResponse the MEX response containing the actual authentication methods that can handle the STS and the corresponding endpoint
	 * @param theSTS the URL of the STS
	 * @return the authenticator Token {@link IToken};
	 * @throws FC2Authentication_Exeception_AuthenticationFailed
	 * @throws Config_Exception_NotDone
	 * @see CredentialStore
	 * {@link CredentialStore#createToken(String, String, String, String)}
	 */
//	public IToken getToken(SessionSelector session,String cardId,IMetaDataExchange mexResponse, String theSTS) throws FC2Authentication_Exeception_AuthenticationFailed, Config_Exception_NotDone{
////		return CredentialStore.getInstance().createToken("UsernameToken", config.userName, null, theSTS);
//		if(UserCredential.MAP.equalsIgnoreCase(config.uriAuthentication)){
//			Vector<Node> vecAuth = mexResponse.getSupportedToken();
//			for(Node authMeth : vecAuth){
//				if("SSOToken".equalsIgnoreCase(authMeth.getLocalName())){
//					NodeList lst = authMeth.getChildNodes();
//					for(int i=0;i<lst.getLength();i++){
//						String subName = lst.item(i).getLocalName();
//						if("Policy".equalsIgnoreCase(subName)){
//							trace(subName);
//							for(int j=0;j<lst.item(i).getChildNodes().getLength();j++){
//								
//								String trustNode = lst.item(i).getChildNodes().item(j).getLocalName();
//								if("Trust".equalsIgnoreCase(trustNode)){
//									//String trustIn = lst.item(i).getChildNodes().item(j).getTextContent().trim();
//									String trustIn = "onsenfou";
//									trace("SSOToken, trust in  ["  +trustIn +"]");
//									//return CredentialStore.getInstance().createToken("SSOToken",config.userName, trustIn, theSTS);
//									//return CredentialStore.createToken("SSOToken",config.userName, trustIn, theSTS);
//									//return config.theSession.getCredentialStore().createToken("SSOToken",config.userName, trustIn, theSTS);
//									return session.getCredentialStore().createToken(cardId,"SSOToken",config.userName, trustIn, theSTS);
//								}
//							}							
//						}
//					}
//				}
//				
//			
//			}
//		}
//		
//		if(UserCredential.USERNAME.equalsIgnoreCase(config.uriAuthentication)){
//			Vector<Node> vecAuth = mexResponse.getSupportedToken();
//			for(Node authMeth : vecAuth){
//				if("UsernameToken".equalsIgnoreCase(authMeth.getLocalName())){
//					trace("UserNameToken ");
//					//return CredentialStore.getInstance().createToken("UsernameToken", config.userName, null, theSTS);
//					return session.getCredentialStore().createToken(cardId, "UsernameToken", config.userName, null, theSTS);
//				}							
//			}
//		}
//		trace("MEX does not respond correctly, we take the default user/password");
//		//return CredentialStore.getInstance().createToken("UsernameToken", config.userName, null, theSTS);
//		return session.getCredentialStore().createToken(cardId, "UsernameToken", config.userName, null, theSTS);
//		//throw(new FC2Authentication_Exeception_AuthenticationFailed("STS does not support Authentication Method"));
//	}
	/**
	 * Actually return the first STS found.
	 * @param mexResponse
	 * @return the STS url (String representation)
	 * @throws FC2Authentication_Exeception_AuthenticationFailed if no STS is found.
	 */
//	public String chooseSTS(IMetaDataExchange mexResponse) throws FC2Authentication_Exeception_AuthenticationFailed{
//		//return "https://ip-telecom.orange.telecom.test.fc2consortium.org/STSOrangeCartesV2/Service.svc";
//		//return "https://ip-telecom.orange.telecom.test.fc2consortium.org/CustomUserNameCardStsHostFactory/Service.svc";//"http://idp.ds.interieur.test.fc2consortium.org:7000/sample/trust/usernamepassword/sts";
//		if(mexResponse.getSTSURL().size()==0){
//			if(config.card==null) {
//				throw(new FC2Authentication_Exeception_AuthenticationFailed("FC2Authentication.chooseSTS"));
//			}
//			String stsURL =  config.card.getTokenServiceReference().get(0).getAddress();
//			trace("We found on the card : " + stsURL);
//			return stsURL;
//		}
//		//	throw(new FC2Authentication_Exeception_AuthenticationFailed("No STS found"));
//		return mexResponse.getSTSURL().get(0);
//	}
	
}
