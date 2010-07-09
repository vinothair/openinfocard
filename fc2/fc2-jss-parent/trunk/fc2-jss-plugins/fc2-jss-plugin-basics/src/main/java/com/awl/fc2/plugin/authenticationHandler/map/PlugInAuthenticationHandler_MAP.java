package com.awl.fc2.plugin.authenticationHandler.map;

import java.io.IOException;

import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;

import org.xmldap.exceptions.ParsingException;
import org.xmldap.infocard.InfoCard;

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
import com.awl.rd.applications.common.message.Constant_XML;
import com.awl.rd.applications.common.message.IMessage;
import com.awl.rd.applications.common.message.MessageAdaptater;
import com.awl.rd.applications.common.message.execptions.Message_ExceptionIsErrorMessage;
import com.awl.rd.applications.common.message.execptions.Message_ExceptionUnableToConvertMessage;
import com.awl.rd.applications.map.orchestror.IAPP_Orchestror_ExportedFunctions;
import com.awl.rd.applications.map.orchestror.impl.Orchestror_ExportedFunctions_PostVersion;
import com.awl.rd.fc2.plugin.infocard.usercredentials.map.UserCredentialExt_MAP;
import com.awl.rd.protocols.messagehandler.IMapMessageHandler;
import com.awl.rd.protocols.messagehandler.IUI_BasicInterface;
import com.awl.rd.protocols.messagehandler.impl.MapMessageHandler_FullAccess;
import com.awl.ws.messages.IRequestSecurityToken;
import com.awl.ws.messages.IRequestSecurityTokenResponse;
import com.awl.ws.messages.authentication.IToken;
import com.awl.ws.messages.impl.RequestSecurityTokenResponse;
import com.awl.ws.v2.impl.FactoryHelpers;
import com.utils.XMLParser;
import com.utils.execeptions.XMLParser_Exception_NO_ATTRIBUTE;
import com.utils.execeptions.XMLParser_Exception_NoNextValue;

public class PlugInAuthenticationHandler_MAP implements
		IPlugInAuthenticationHandler {

	public final String URI_CREDENTIAL_SSO = "MapAuthenticatice";//"SSOCredential";
	public static Logger log = new Logger(PlugInAuthenticationHandler_MAP.class);
	public static void trace(Object msg){
		log.trace(msg);

	}
	@Override
	public String getAuthenticationURI() {
		// TODO Auto-generated method stub
		return URI_CREDENTIAL_SSO;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "MAPAuthentication";
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
	UserCredentialExt_MAP extMap ;
	private String SSO_TrustChain;
	
	
	@Override
	public void configureAuthentication(AuthenticationConfig config,
			String cardid) {
		this.config = config;
		this.cardId = cardid;
		extMap = new UserCredentialExt_MAP();
		for(int i=0;i<config.card.getTokenServiceReference().size();i++){
			try {
				extMap.fromUserCredential(config.card.getTokenServiceReference().get(i).getUserCredential());
				break;
			} catch (ParsingException e) {
				
			}			
		}
		trace("configure authentication, Get Username for MAP :  " + extMap.getUsername());
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

	String getMapTicket(String userID) throws APP_Exception_InternalError, Config_Exeception_UnableToReadConfigFile, Config_Exeception_MalFormedConfigFile, Config_Exception_NotDone, XMLParser_Exception_NO_ATTRIBUTE, FC2Authentication_Exeception_AuthenticationFailed{
			IAPP_Orchestror_ExportedFunctions map = new Orchestror_ExportedFunctions_PostVersion();
			trace("run for " + userID);
			String sessionId =  map.startSession();
			sessionId = XMLParser.getSessionIDFromResponse(sessionId);
			System.out.println("Server said: " + sessionId);
		
			IMapMessageHandler client;
			IUI_BasicInterface basicUI = Selector.getInstance().getUI().getBasicInterface();
			client = new MapMessageHandler_FullAccess(basicUI);

			String initContext = client.getXMLContextForID(userID);
			
			
			String response = map.initConnections(sessionId);
			trace("REPONSE = " + response);
			response = map.initTransaction(sessionId,initContext);
			trace("REPONSE = " + response);
			
			
			String lstOfAuthenticators = XMLParser.getValueFromResponseXML(map.getAuthenticationMethods(sessionId));
			XMLParser parser = new XMLParser(lstOfAuthenticators);
			XMLParser parserAuth = new XMLParser(lstOfAuthenticators);
			parser.query("URI_CONFIG");
			parserAuth.query("URI_AUTHENT");
			trace("Quelle méthode ? ");
			int cpt=0;
			String xmlAuth =  "<?xml version=\"1.0\"?>"+
			"<methods>";
				try {
				while(parser.hasNext()){
					String URI_Config;
				
						URI_Config = parser.getNextValue();
					
					String URI_Auth = parserAuth.getNextValue();
					trace(cpt + ") : " + URI_Config);
					String tmplCard = 	"<item>"+
					"<url>"+"</url>"+
					"<info>"+URI_Config+"</info>"+
					"<title> "+URI_Auth+" </title>"+
					"<sendBack>"+cpt+"</sendBack>"+
					"</item>";		
					xmlAuth += tmplCard;
					cpt++;
				}
			} catch (XMLParser_Exception_NoNextValue e) {
					throw(new APP_Exception_InternalError(e.getMessage()));
				}							
			//Scanner in = new Scanner(System.in);
			//response = in.nextLine();
			xmlAuth += "</methods>";
			//AirAppControler.getInstance().sendOpen();
			//response = AirAppControler.getInstance().sendChooseMethod(xmlAuth);
			int idx = basicUI.sendChooseMethod(xmlAuth);
			//AirAppControler.getInstance().sendNotification(Lang.get(Lang.AUTHENTICATION_INPROGRESS));
			basicUI.sendNotification("", Lang.get(Lang.AUTHENTICATION_INPROGRESS),false);
			//int idx = Integer.valueOf(response).intValue();
			parser.query(Constant_XML.XML_BALISE_INITIALIZATION_CONTEXT);
			String xmlInit = "";
			try {
				for(int i=0;i<idx;i++,parser.getNextValue());		
				xmlInit = parser.getNextXML();
				trace("Authentication with " +xmlInit);
			} catch (XMLParser_Exception_NoNextValue e) {
				throw(new APP_Exception_InternalError(e.getMessage()));
			}
			
			//---- INITIALIZATION DU CONTEXT A VOIR
			xmlInit = xmlInit.replace("{OTP,CHALLENGE}","OTP");
			xmlInit = xmlInit.replace("\n", "");
			//----
			map.initAuthentication(sessionId,xmlInit);
			IMessage toRet=null;
			do{
				System.out.println("CPT =" +cpt);											
				System.out.println("CLIENT PROCESS");
				IMessage recievedMessage = (client.handleMessage(toRet));//protocol.clientGetMessage()));
				System.out.println("SERVER PROCESS");
				//toRet = map.processMessage(recievedMessage);
				response = map.processMessage(sessionId,MessageAdaptater.Msg2xml(recievedMessage));
				trace("RE¨-PROCESS" +  response);
				try {
					try {
						toRet = MessageAdaptater.xml2Msg(XMLParser.getValueFromResponseXML(response));
					} catch (Message_ExceptionIsErrorMessage e) {
						trace("ERROR Message = " + e.theError.toString());
						System.exit(0);
					}
				} catch (Message_ExceptionUnableToConvertMessage e) {
					trace("Can not load the message");
					System.exit(0);
				}
				
				response = map.isComplete(sessionId);
				response = XMLParser.getValueFromResponseValue(response);
			}while(!"TRUE".equalsIgnoreCase(response));
			
			//
			response = map.getAuthenticatorResult(sessionId);
			String strResult = XMLParser.getValueFromResponseValue(response);
			if(strResult.equalsIgnoreCase(Constant_XML.XML_BALISE_PROTOCOL_STATUS_SUCCEED)){
				trace("Authentication succeed");
				
			}else{
				trace("Authentication failed");
				throw(new FC2Authentication_Exeception_AuthenticationFailed("map authentication failed"));
			}
			
			
			response = map.getTicket(sessionId,"SAML2");
			trace("The reponse");
			trace("TICKET = " + response);
			String theTicket = XMLParser.getValueFromResponseValue(response);
			trace("the ticket : ");
			trace(new String(com.utils.Base64.decode(theTicket)));
			
			return theTicket;
			//System.exit(0);
		
	}
	IToken usedToken = null;
	IToken createToken(String stsURL, String username) throws APP_Exception_InternalError, Config_Exeception_UnableToReadConfigFile, Config_Exeception_MalFormedConfigFile, Config_Exception_NotDone, XMLParser_Exception_NO_ATTRIBUTE, FC2Authentication_Exeception_AuthenticationFailed{
		usedToken = Selector.getInstance().session.getCredentialStore().getToken(cardId,"SSOToken",config.userName,SSO_TrustChain , stsURL);
		if(usedToken == null){
			trace("No token found in the credential store");
			
			
			SSOToken token = new SSOToken("o");
			token.setUserName(username);
			String theTicket = getMapTicket(username);
			trace("Put the following ticket in the credentialstore" + theTicket);
			token.setSSOToken(theTicket);
			usedToken = token;
			return token;
		}else{
			return usedToken;
		}
	}
	@Override
	public void handleProtocol(String stsURL, String username) throws FC2Authentication_Exeception_AuthenticationFailed {
		if(username == null) username = extMap.getUsername();
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
		return "SSOToken";
	}
	@Override
	public boolean isCardCompatible(InfoCard card) {
		extMap = new UserCredentialExt_MAP();
		for(int i=0;i<config.card.getTokenServiceReference().size();i++){
			try {
				extMap.fromUserCredential(card.getTokenServiceReference().get(i).getUserCredential());
				return true;
			} catch (ParsingException e) {
				
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
