package com.awl.fc2.plugin.authenticationHandler.pki;

import java.io.IOException;

import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;

import org.xmldap.infocard.InfoCard;
import org.xmldap.infocard.TokenServiceReference;
import org.xmldap.infocard.UserCredential;

import com.awl.fc2.plugin.authenticationHandler.IPlugInAuthenticationHandler;
import com.awl.fc2.selector.Selector;
import com.awl.fc2.selector.authentication.AuthenticationConfig;
import com.awl.fc2.selector.authentication.PKIHandler;
import com.awl.fc2.selector.exceptions.Config_Exception_NotDone;
import com.awl.fc2.selector.exceptions.Config_Exeception_MalFormedConfigFile;
import com.awl.fc2.selector.exceptions.Config_Exeception_UnableToReadConfigFile;
import com.awl.fc2.selector.exceptions.FC2Authentication_Exeception_AuthenticationFailed;
import com.awl.fc2.selector.exceptions.PKIHandler_Exeception;
import com.awl.fc2.selector.launcher.Config;
import com.awl.logger.Logger;
import com.awl.rd.applications.common.exceptions.APP_Exception_InternalError;
import com.awl.ws.messages.IRequestSecurityToken;
import com.awl.ws.messages.IRequestSecurityTokenResponse;
import com.awl.ws.messages.authentication.IToken;
import com.awl.ws.messages.impl.RequestSecurityTokenResponse;
import com.awl.ws.v2.impl.FactoryHelpers;
import com.utils.execeptions.XMLParser_Exception_NO_ATTRIBUTE;

public class PlugInAuthenticationHandler_X509 implements
		IPlugInAuthenticationHandler {

	//public final String URI_CREDENTIAL_SSO = "MapAuthenticatice";//"SSOCredential";
	public final String URI_CREDENTIAL_PKI = "X509V3Credential";
	public static Logger log = new Logger(PlugInAuthenticationHandler_X509.class);
	public static void trace(Object msg){
		log.trace(msg);

	}
	@Override
	public String getAuthenticationURI() {
		// TODO Auto-generated method stub
		return URI_CREDENTIAL_PKI;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "X509V3Credential";
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

		
	IToken createToken(String stsURL, String username) throws APP_Exception_InternalError, Config_Exeception_UnableToReadConfigFile, Config_Exeception_MalFormedConfigFile, Config_Exception_NotDone, XMLParser_Exception_NO_ATTRIBUTE, FC2Authentication_Exeception_AuthenticationFailed{
	
		IToken res = null;//Selector.getInstance().session.getCredentialStore().getToken(cardId, "X509Token", config.userName, null, stsURL);
	//	IUI_BasicInterface basicUI = Selector.getInstance().getUI().getBasicInterface();
		if(res == null){
			PKIToken token = new PKIToken("o");
			
			trace("FIND THE CERTIFICATE FROM THE HASH... WHERE TO WE GET THE HASH ??");
			String hashCErtificate = config.card.getTokenServiceReference().get(0).getUserCredential().getX509Hash();
			PKIHandler pki =  new PKIHandler(hashCErtificate);			
			try {
				token.setPKIHandler(pki);
			} catch (PKIHandler_Exeception e) {
				trace("PKI problem");
			}
				
			
		    return token;
		}else{
			return res;
		}
	     
		
	}
	@Override
	public void handleProtocol(String stsURL, String username) throws FC2Authentication_Exeception_AuthenticationFailed {
		IToken SecurityToken;
		try {
			SecurityToken = createToken(stsURL,username);
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
		return "X509Token";
	}
	@Override
	public boolean isCardCompatible(InfoCard card) {
		for(TokenServiceReference tok : card.getTokenServiceReference()){
			if(tok.getUserCredential().getAuthType().equalsIgnoreCase(UserCredential.X509)){
				return true;
			}
		}
		return false;
	}
	@Override
	public IToken getToken() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public boolean isStorableToken() {
		// TODO Auto-generated method stub
		return false;
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
