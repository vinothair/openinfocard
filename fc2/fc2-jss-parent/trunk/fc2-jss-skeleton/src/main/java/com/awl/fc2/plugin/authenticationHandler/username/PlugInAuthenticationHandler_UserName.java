package com.awl.fc2.plugin.authenticationHandler.username;

import java.io.IOException;

import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;

import org.xmldap.infocard.InfoCard;
import org.xmldap.infocard.TokenServiceReference;
import org.xmldap.infocard.UserCredential;

import com.awl.fc2.plugin.authenticationHandler.IPlugInAuthenticationHandler;
import com.awl.fc2.selector.Selector;
import com.awl.fc2.selector.authentication.AuthenticationConfig;
import com.awl.fc2.selector.exceptions.Config_Exception_NotDone;
import com.awl.fc2.selector.exceptions.Config_Exeception_MalFormedConfigFile;
import com.awl.fc2.selector.exceptions.Config_Exeception_UnableToReadConfigFile;
import com.awl.fc2.selector.exceptions.FC2Authentication_Exeception_AuthenticationFailed;
import com.awl.fc2.selector.launcher.Config;

import com.awl.fc2.selector.userinterface.lang.Lang;
import com.awl.logger.Logger;
import com.awl.rd.applications.common.exceptions.APP_Exception_InternalError;
import com.awl.rd.protocols.messagehandler.IUI_BasicInterface;
import com.awl.ws.messages.IRequestSecurityToken;
import com.awl.ws.messages.IRequestSecurityTokenResponse;
import com.awl.ws.messages.authentication.IToken;
import com.awl.ws.messages.impl.RequestSecurityTokenResponse;
import com.awl.ws.v2.impl.FactoryHelpers;
import com.utils.execeptions.XMLParser_Exception_NO_ATTRIBUTE;

public class PlugInAuthenticationHandler_UserName implements
		IPlugInAuthenticationHandler {

	//public final String URI_CREDENTIAL_SSO = "MapAuthenticatice";//"SSOCredential";
	public final String URI_CREDENTIAL_PWD = "UserNamePasswordAuthenticate";
	public static Logger log = new Logger(PlugInAuthenticationHandler_UserName.class);
	public static void trace(Object msg){
		log.trace(msg);

	}
	@Override
	public String getAuthenticationURI() {
		// TODO Auto-generated method stub
		return URI_CREDENTIAL_PWD;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "UserNamePassword";
	}

	@Override
	public String getType() {
		// TODO Auto-generated method stub
		return PLG_AUTHENTICATION_HANDLER;
	}

	@Override
	public void install(Config cnf) {
		// TODO Auto-generated method stub
		
	}

	
	public String rstVersion;
	public String strSOAPProtocolInUse;
	public AuthenticationConfig config;
	String cardId;
	private IRequestSecurityTokenResponse rstrFinal;
	
	
	
	
	@Override
	public void configureAuthentication(AuthenticationConfig config,
			String cardid) {
		this.config = config;
		this.cardId = cardid;
	
		
	}

	@Override
	public void configureRSTFactory(String strSOAPProcotolInUse,
			String RST_Version) {
		rstVersion = RST_Version;
		this.strSOAPProtocolInUse = strSOAPProcotolInUse;
		
	}

	@Override
	public IRequestSecurityTokenResponse getFinalRSTR() {
		return rstrFinal;
	}

	IToken usedToken = null;
	private String pwd;
	public String getPWD(){
		return pwd;
	}
	public IToken createToken(String stsURL, String username,String password) throws APP_Exception_InternalError, Config_Exeception_UnableToReadConfigFile, Config_Exeception_MalFormedConfigFile, Config_Exception_NotDone, XMLParser_Exception_NO_ATTRIBUTE, FC2Authentication_Exeception_AuthenticationFailed{
	
		
		IUI_BasicInterface basicUI = Selector.getInstance().getUI().getBasicInterface();
		if(usedToken == null){
			UserNameToken token = new UserNameToken("o");
			pwd = "";
			if(username==null)
				//username = Selector.getInstance().getUI().getUserNameTokenUI().getUserName();
				username = basicUI.sendQuestion("",Lang.get(Lang.ASK_USERNAME),false);
//				if(mapPwd.containsKey(cardId)) {
//					pwd = mapPwd.get(cardId);
//				}else
			{
				//pwd = Selector.getInstance().getUI().getUserNameTokenUI().getPWD();
				if(password == null){
					pwd =  basicUI.sendQuestion("",Lang.get(Lang.ASKPWD),true);
				}else{
					pwd = password;
				}
				
				
//					curContext=new CurrentContext();
//					curContext.setData(username, pwd, cardId);
			}
				
			token.setUserName(username);			
		    token.setPWD(pwd);
		    usedToken = token;
		    return token;
		}else{
			return usedToken;
		}
	     
		
	}
	@Override
	public void handleProtocol(String stsURL, String username) throws FC2Authentication_Exeception_AuthenticationFailed {
		IToken SecurityToken;
		try {
			usedToken = Selector.getInstance().session.getCredentialStore().getToken(cardId, "UsernameToken", username, null, stsURL);
			if(usedToken == null){
				SecurityToken = createToken(stsURL,username,null);
			}else{
				SecurityToken = usedToken;
			}
			
		} catch (APP_Exception_InternalError e1) {
			throw new FC2Authentication_Exeception_AuthenticationFailed(e1.getMessage());
		} catch (Config_Exeception_UnableToReadConfigFile e1) {
			throw new FC2Authentication_Exeception_AuthenticationFailed(e1.getMessage());
		} catch (Config_Exeception_MalFormedConfigFile e1) {
			throw new FC2Authentication_Exeception_AuthenticationFailed(e1.getMessage());
		} catch (Config_Exception_NotDone e1) {
			throw new FC2Authentication_Exeception_AuthenticationFailed(e1.getMessage());
		} catch (XMLParser_Exception_NO_ATTRIBUTE e1) {
			throw new FC2Authentication_Exeception_AuthenticationFailed(e1.getMessage());
		}
		IRequestSecurityToken rst = FactoryHelpers.createEmptyRST(rstVersion);
		
		
		rst.setSOAPProtocol(this.strSOAPProtocolInUse);
		rst.setEndPoint(stsURL);
		rst.setTokenType(IRequestSecurityToken.TOKEN_TYPE_SAML11);
		    
		rst.setPPID(FactoryHelpers.computePPID(config.urlRequestor, cardId));
		rst.setRequestor(config.urlRequestor,config.certifRequestor);
		rst.setCardId(cardId);
		   
	    for(String claim : config.requiredClaims){
	    	rst.addClaims(claim);
	    }
		    
		    //try {
		rst.setAuthenticationHandler(SecurityToken);
		trace("Sending RST");
		try {
			SOAPMessage soaprstr = rst.sendRST();
			IRequestSecurityTokenResponse rstr = new RequestSecurityTokenResponse();				
			rstr.setRSTR(soaprstr);
			rstrFinal = rstr;
		} catch (UnsupportedOperationException e) {
			trace(e.getMessage());
		} catch (SOAPException e) {
			trace(e.getMessage());
		} catch (IOException e) {
			trace(e.getMessage());
		}
		
	}
	@Override
	public String getTokenType() {
		// TODO Auto-generated method stub
		return "UsernameToken";
	}
	@Override
	public boolean isCardCompatible(InfoCard card) {
		for(TokenServiceReference tok : card.getTokenServiceReference()){
			if(tok.getUserCredential().getAuthType().equalsIgnoreCase(UserCredential.USERNAME)){
				return true;
			}
		}
		return false;
	}
	@Override
	public IToken getToken() {
		// TODO Auto-generated method stub
		return usedToken;
	}
	@Override
	public boolean isStorableToken() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public int getPriority() {
		
		return 0;
	}
	
	@Override
	public void uninstall() {
		// TODO Auto-generated method stub
		
	}

	

}
