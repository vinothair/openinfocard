package com.awl.rd.applications.map.orchestror;

import java.util.Scanner;

import org.apache.log4j.Logger;


import com.awl.rd.applications.common.exceptions.APP_Exception_InternalError;
import com.awl.rd.applications.common.message.Constant_XML;
import com.awl.rd.applications.common.message.IMessage;
import com.awl.rd.applications.common.message.MessageAdaptater;
import com.awl.rd.applications.common.message.execptions.Message_ExceptionIsErrorMessage;
import com.awl.rd.applications.common.message.execptions.Message_ExceptionUnableToConvertMessage;
import com.awl.rd.applications.map.orchestror.exceptions.APP_Orchestror_Exception_InternalError;
import com.awl.rd.applications.map.orchestror.impl.Orchestror_ExportedFunctions_PostVersion;
//import com.awl.rd.applications.map.orchestror.impl.APP_Orchestror_ExportedFunctions;
//import com.awl.rd.entities.tokens.TokenFactory;
import com.awl.rd.protocols.messagehandler.IMapMessageHandler;
import com.awl.rd.protocols.messagehandler.IUI_BasicInterface;
import com.awl.rd.protocols.messagehandler.impl.BasicUI_Console;
import com.awl.rd.protocols.messagehandler.impl.MapMessageHandler_FullAccess;
import com.utils.XMLParser;
import com.utils.execeptions.XMLParser_Exception_NO_ATTRIBUTE;
import com.utils.execeptions.XMLParser_Exception_NoNextValue;



public class MAPRunner {
	static Logger logger = Logger.getLogger(MAPRunner.class);
	static public void trace(Object obj){		
		System.out.println("Client : " + obj);
		logger.info(obj);
	}
	static public void warning(Object msg){
		System.out.println(msg);
		logger.warn(msg);
	}
	
	IUI_BasicInterface ui;
	public MAPRunner(IUI_BasicInterface ui) {
		this.ui =ui;
	}
	static public void err(Object msg){
		System.err.println(msg);
		logger.error(msg);
	}
	IAPP_Orchestror_ExportedFunctions map;
	public void setOrchestror(IAPP_Orchestror_ExportedFunctions map){
		this.map = map;
	}
	public void throwIfFailed(String xmlResponse) throws APP_Exception_InternalError{
		try {
			String error = XMLParser.getErrorFromResponse(xmlResponse);
			if(!("_THE_ERROR_".equalsIgnoreCase(error))){
				throw(new APP_Exception_InternalError("Authentication failure : "+error));
			}
			
		} catch (Exception e) {
			throw(new APP_Exception_InternalError("Authentication failure"));
		}
	}
	public String run(String userID) throws APP_Exception_InternalError, XMLParser_Exception_NO_ATTRIBUTE, XMLParser_Exception_NoNextValue{
		String sessionId =  map.startSession();
		System.out.println("Response " + sessionId);
		sessionId = XMLParser.getSessionIDFromResponse(sessionId);
		System.out.println("Server said: " + sessionId);
		/*
		sessionId =  map.startSession();
		sessionId = XMLParser.getSessionIDFromResponse(sessionId);
		System.out.println("Server said: " + sessionId);
		
		sessionId =  map.startSession();
		sessionId = XMLParser.getSessionIDFromResponse(sessionId);
		System.out.println("Server said: " + sessionId);
		*/
		IMapMessageHandler client;
		client = new MapMessageHandler_FullAccess(ui);
		
		String initContext = client.getXMLContextForID(userID);
		
		
		String response = map.initConnections(sessionId);
		trace("REPONSE = " + response);
		throwIfFailed(response);
		response = map.initTransaction(sessionId,initContext);
		trace("REPONSE = " + response);
		throwIfFailed(response);
		response = map.getAuthenticationMethods(sessionId);
		trace("REPONSE = " + response);
		throwIfFailed(response);
		String lstOfAuthenticators = XMLParser.getValueFromResponseXML(response);
		XMLParser parser = new XMLParser(lstOfAuthenticators);
		
		parser.query("URI_CONFIG");
		trace("Quelle m�thode ? ");
		int cpt=0;
		while(parser.hasNext()){
			trace(cpt++ + ") : " + parser.getNextValue());
		}							
		Scanner in = new Scanner(System.in);
		response = in.nextLine();
		int idx = Integer.valueOf(response).intValue();
		parser.query(Constant_XML.XML_BALISE_INITIALIZATION_CONTEXT);
		for(int i=0;i<idx;i++,parser.getNextValue());
		String xmlInit = parser.getNextXML();
		trace("Authentication with " +xmlInit);
		
		
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
			trace("RE�-PROCESS" +  response);
			try {
				try {
					toRet = MessageAdaptater.xml2Msg(XMLParser.getValueFromResponseXML(response));
				} catch (Message_ExceptionIsErrorMessage e) {
					warning("ERROR Message = " + e.theError.toString());
					System.exit(0);
				}
			} catch (Message_ExceptionUnableToConvertMessage e) {
				warning("Can not load the message");
				System.exit(0);
			}
			
			response = map.isComplete(sessionId);
			response = XMLParser.getValueFromResponseValue(response);
		}while(!"TRUE".equalsIgnoreCase(response));
		
		//
		response = map.getAuthenticatorResult(sessionId);
		throwIfFailed(response);
		String strResult = XMLParser.getValueFromResponseValue(response);
		if(strResult.equalsIgnoreCase(Constant_XML.XML_BALISE_PROTOCOL_STATUS_SUCCEED)){
			trace("Authentication succeed");
			
		}else{
			trace("Authentication failed");
			throw(new APP_Exception_InternalError("Authentication failure"));
		}
		
		
		response = map.getTicket(sessionId,"SAML2");
		trace("The reponse");
		trace("TICKET = " + response);
		String theTicket = XMLParser.getValueFromResponseValue(response);
		return theTicket;
		/*trace("the ticket : ");
		trace(new String(com.utils.Base64.decode(theTicket)));*/
		
		
		//System.exit(0);
	}
	/**
	 * @param args
	 * @throws APP_Orchestror_Exception_InternalError 
	 * @throws XMLParser_Exception_NO_ATTRIBUTE 
	 * @throws Message_ExceptionUnableToConvertMessage 
	 */
	public static void main(String[] args) throws APP_Orchestror_Exception_InternalError, XMLParser_Exception_NO_ATTRIBUTE {
		String username = "stef";
		String theTicket;
		MAPRunner client = new MAPRunner(new BasicUI_Console());
		try {
			IAPP_Orchestror_ExportedFunctions map = new Orchestror_ExportedFunctions_PostVersion();
			client.setOrchestror(map);
			theTicket = client.run(username);
			System.out.println("The ticket = " + theTicket);
		} catch (APP_Exception_InternalError e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (XMLParser_Exception_NO_ATTRIBUTE e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (XMLParser_Exception_NoNextValue e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
		/*IAPP_Orchestror_ExportedFunctions map = new APP_Orchestror_ExportedFunctions();

		Client client = new Client();
		client.setOrchestror(map);
		try {
			client.run("fj");
		} catch (XMLParser_Exception_NoNextValue e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/

	}

}
