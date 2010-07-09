package com.awl.rd.protocols.messagehandler.impl;

import java.security.InvalidKeyException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.Signature;
import java.security.SignatureException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.Scanner;

import org.apache.log4j.Logger;
import org.apache.xalan.xsltc.compiler.sym;
import org.w3c.dom.Document;


import com.awl.rd.applications.common.message.IMessage;
import com.awl.rd.applications.common.message.types.Message_APDU;
import com.awl.rd.applications.common.message.types.Message_AskQuestion;
import com.awl.rd.applications.common.message.types.Message_CSP;
import com.awl.rd.applications.common.message.types.Message_INVOKE_PCSC;
import com.awl.rd.applications.common.message.types.Message_Notification;
import com.awl.rd.protocols.messagehandler.IMapMessageHandler;
import com.awl.rd.protocols.messagehandler.IUI_BasicInterface;
import com.awl.rd.smartcard.FactoryReaders;
import com.awl.rd.smartcard.IAPDU_Bridge;
import com.awl.rd.smartcard.IReaders;
import com.awl.rd.smartcard.exception.SmartCard_Exception_NO_CARD;
import com.awl.rd.smartcard.exception.SmartCard_Exception_NO_READER;
import com.awl.rd.smartcard.exception.SmartCard_Exception_UNKNOW;
import com.utils.Base64;
import com.utils.CSPController;
import com.utils.Utils;


public class MapMessageHandler_FullAccess implements IMapMessageHandler{
	static Logger logger = Logger.getLogger(MapMessageHandler_FullAccess.class);
	static public void trace(Object obj){
		System.out.println("client : " + obj);
		logger.trace(obj);
	}
	
	public MapMessageHandler_FullAccess(IUI_BasicInterface ui) {
		basicUI = ui;
	}
	
	IUI_BasicInterface basicUI;
	
	static public void warning(Object msg){
		System.out.println(msg);
		logger.warn(msg);
	}
	static public void err(Object msg){
		System.err.println(msg);
		logger.error(msg);
	}
//	public IMessage processQuestion(Message_AskQuestion msg){
//		System.out.println(msg.getQuestion());
//		Scanner in = new Scanner(System.in);
//		String response = in.nextLine();
//		msg.setResponse(response);
//		return msg;
//	}
	

	/**
	 * This method handles the messages of the type question.<br/>
	 * We used {@link AirAppControler#sendQuestion(String)}
	 * @param msg message containing the question
	 * @return the message containing the response
	 */
	public IMessage processQuestion(Message_AskQuestion msg){
		String response = basicUI.sendQuestion("",msg.getQuestion(),false);//AirAppControler.getInstance().sendQuestion(msg.getQuestion());		
		msg.setResponse(response);		
		return msg;
	}
	
	
	public IMessage handleMessage(IMessage msg){
		trace("handleMessage : "  + msg);
		if(msg instanceof Message_AskQuestion){
			return processQuestion((Message_AskQuestion) msg);
		}
		if(msg instanceof Message_INVOKE_PCSC){
			return processPCSC((Message_INVOKE_PCSC) msg);
		}
		if(msg instanceof Message_APDU){
			return processAPDU((Message_APDU)msg);
		}
		if(msg instanceof Message_Notification){
			return processNotification((Message_Notification) msg);
		}
		if(msg instanceof Message_CSP) {
			return processCSP((Message_CSP)msg);
		}
		return null;
	}
	
	public static void main(String[] args) {
		MapMessageHandler_FullAccess client = new MapMessageHandler_FullAccess(new BasicUI_Console());
		
		Message_CSP req = new Message_CSP();
		req.setCmd(Message_CSP.CMD_SELECT_CERTIFICATE);
		req.setUserDN("C=FR, O=fc2consortium, OU=atosorigin, CN=François-Julien Ritaine");
//		System.out.println(req.toXML());
		Message_CSP reponse = (Message_CSP) client.handleMessage(req);
		System.out.println(reponse.toXML());
		if(reponse.getState() == Message_CSP.CERTIFICATE_OK) System.out.println("SELECT CERTIF = - OK -");
		else System.out.println("SELECT CERTIF = - KO -");
		
		System.out.println("\n");
		reponse.setData("testtesttesttest");
		reponse.setCmd(Message_CSP.CMD_REQUEST_SIGNATURE_DATA);
		Message_CSP reponse2 = (Message_CSP) client.handleMessage(reponse);
//		System.out.println(reponse2.toXML());
		
//		System.out.println(reponse2.);
		
		//verif
		String toSign = "testtesttesttest";
		
		byte[] signedData = Base64.decode(reponse2.getSignedData());
		
		
		try {
			Signature sign = Signature.getInstance("SHA1withRSA");
			sign.initVerify(CSPController.X509fromB64(reponse2.getCertificate()).getPublicKey());
			sign.update(toSign.getBytes());
			System.out.println("sign = "+sign.verify(signedData));
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SignatureException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		
//		System.out.println("\n");
//		reponse.setData("<data>testtest</data>");
//		reponse.setCmd(Message_CSP.CMD_REQUEST_SIGNATURE_XML);
//		Message_CSP reponse2 = (Message_CSP) client.handleMessage(reponse);
//		System.out.println(reponse2.toXML());
	}
	
	private CSPController cspController = null;
	
	private IMessage processCSP(Message_CSP msg) {
		if(msg.getCmd().equalsIgnoreCase(Message_CSP.CMD_SELECT_CERTIFICATE)) {
			trace("csp : select certificate");
			if (this.cspController == null) {
				this.cspController = new CSPController();
			}else{
				cspController.reset();
			}
			
			try {
				X509Certificate cert = this.cspController.getFirstSatisfyingCertificate(msg.getUserDN(), msg.getIssuerDN(), msg.getUsages());
				if(cert == null) msg.setState(Message_CSP.CERTIFICATE_KO);
				else {
					msg.setState(Message_CSP.CERTIFICATE_OK);
					msg.setCertificate(Base64.encode(cert.getEncoded()));
				}
			} catch (KeyStoreException e) {
				e.printStackTrace();
				msg.setState(Message_CSP.CERTIFICATE_KO);
			} catch (CertificateEncodingException e) {
				e.printStackTrace();
				msg.setState(Message_CSP.CERTIFICATE_KO);
			}
			return msg;
		}
		if(msg.getCmd().equalsIgnoreCase(Message_CSP.CMD_REQUEST_SIGNATURE_XML)) {
			trace("csp : sign xml");
			if(this.cspController == null || this.cspController.getCurrentCertificate()==null) {
				msg.setState(Message_CSP.SIGNATURE_KO);
				System.out.println(this.cspController);
				System.out.println(this.cspController.getCurrentCertificate());
				return msg;
			}
			
			try {
				Document signedDoc = this.cspController.signXml(msg.getData());
				msg.setSignedData(CSPController.xml2String(signedDoc));
				msg.setState(Message_CSP.SIGNATURE_OK);
			} catch (Exception e) {
				e.printStackTrace();
				msg.setState(Message_CSP.SIGNATURE_KO);
			}
			return msg;
		}
		if(msg.getCmd().equalsIgnoreCase(Message_CSP.CMD_REQUEST_SIGNATURE_DATA)) {
			trace("csp : sign raw data");
			
			if(this.cspController == null || this.cspController.getCurrentCertificate()==null) {
				msg.setState(Message_CSP.SIGNATURE_KO);
				System.out.println(this.cspController);
				System.out.println(this.cspController.getCurrentCertificate());
				return msg;
			}
			
			try {
				byte[] signedData = this.cspController.signData(msg.getData());
				msg.setSignedData(Base64.encode(signedData));
				msg.setState(Message_CSP.SIGNATURE_OK);
			} catch (Exception e) {
				e.printStackTrace();
				msg.setState(Message_CSP.SIGNATURE_KO);
			}
			return msg;
		}
		return null;
	}
//	private IMessage processNotification(Message_Notification msg) {
//		System.out.println("NOTIFICATION ===>>>>>" + msg.getNotification() + "<<<<<<");
//		return msg;
//	}
	/**
	 * This method handles the messages of the type notification.<br/>
	 * We used {@link AirAppControler#sendNotification(String)}
	 * @param msg message containing the question
	 * @return the message containing the response
	 */
	private IMessage processNotification(Message_Notification msg) {
		String strMsg = msg.getNotification();
		if("Please insert your pin".equalsIgnoreCase(strMsg)){
			strMsg = "Saisissez votre code confidentiel";
		}
		
		basicUI.sendNotification("",strMsg,false);
		return msg;
	}
	public String getXMLContextForID(String id){
		return  "<CONTEXT>"+
		"<TRANSACTION_BANCAIRE>"+
		"</TRANSACTION_BANCAIRE>"+
		"<MESSAGES_UNDERSTANDING>"+
		//"<MESSAGE_TYPE>INVOKE_READER</MESSAGE_TYPE>"+
		"<MESSAGE_TYPE>INVOKE_PCSC</MESSAGE_TYPE>"+
		"<MESSAGE_TYPE>APDUCommand</MESSAGE_TYPE>"+
		"<MESSAGE_TYPE>APDUResponse</MESSAGE_TYPE>"+
		"<MESSAGE_TYPE>NOTIFICATION</MESSAGE_TYPE>"+
		"<MESSAGE_TYPE>DISPLAY_QUESTION</MESSAGE_TYPE>"+
		"<MESSAGE_TYPE>Message_CSP</MESSAGE_TYPE>" +
		"</MESSAGES_UNDERSTANDING>"+
		"<USER>"+
		"<ID>"+id+"</ID> "+
		"</USER>"+
		"</CONTEXT>";
	}
	
	IReaders m_terminals = FactoryReaders.createReaders(FactoryReaders.READER_JRE_EMBEDDED);
	IAPDU_Bridge m_apdu;
	IMessage processPCSC(Message_INVOKE_PCSC msg){
		if(Message_INVOKE_PCSC.CMD_LISTTERMINAL.equalsIgnoreCase(msg.getCommandType())){
			trace("list the terminals");
			try {
				msg.m_vecResponse = m_terminals.getVectorOfAccessibleReaders();
			} catch (SmartCard_Exception_NO_READER e) {
				msg.setStatus(Message_INVOKE_PCSC.STATUS_NOREADER);
			}
			return msg;
		}
		if(Message_INVOKE_PCSC.CMD_SELECTTERMINAL.equalsIgnoreCase(msg.getCommandType())){
			trace("select a terminal");
			boolean success = m_terminals.selectTerminal(msg.getReaderName());
			if(success )
				msg.setStatus(Message_INVOKE_PCSC.TERMINAL_SELECTED);
			else
				msg.setStatus(Message_INVOKE_PCSC.UNKNOW_ERROR);
			return msg;
		}
		if(Message_INVOKE_PCSC.CMD_CREATEAPDU.equalsIgnoreCase(msg.getCommandType())){
			trace("Create apdu");
			try {
				m_apdu = m_terminals.createCompatibleAPDUBridge(true);
			} catch (SmartCard_Exception_NO_CARD e) {
				msg.setStatus(Message_INVOKE_PCSC.NO_CARD);
				return msg;
			}
			
			msg.setStatus(Message_INVOKE_PCSC.APDU_CREATED);
			return msg;
		}
		return null;
	}
	
	IMessage processAPDU(Message_APDU msg){
		
		if(msg.getState()==Message_APDU.SENDAPDU){
			trace("Sending the apdu");
			try {
				m_apdu.sendAPDUString(msg.getM_strAPDUInHex());
				msg.setM_strDataInHex(Utils.getHexString(m_apdu.getResData()));
				msg.setM_strSWInHex(Utils.getHexString(m_apdu.getResStatus()));
				return msg;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
			
		}
		if(msg.getState() == Message_APDU.SENDATR){
			trace("ATR Request");
			byte [] response = m_apdu.getATR();
			String strResponse = "";
			if(response != null){
				
				strResponse=bytes2String(response);
				msg.setM_strSWInHex("9000");
			}else{
				msg.setM_strSWInHex("8080");
				msg.setM_strDataInHex(strResponse);
			}
			
			return msg;
		}
		if(msg.getState() == Message_APDU.SENDCARDCTROL){
			trace("Card Control Command");
			int code = Integer.valueOf(msg.getM_iCCCode());
			byte [] control = string2Bytes(msg.getM_strCCControl());
			byte[] response;
			if(msg.getM_iCCCode().equalsIgnoreCase("1107299656"))
			{
				trace("CORRECTION DE MERDE");
				code = 16606712;
				control = string2Bytes("0A008947040C0402000A0C000000000D00000000200001082FFFFFFFFFFFFFFF");
			}
			
			
			trace("Common : " +msg.getM_iCCCode() +", " + msg.getM_strCCControl());
			String strResponse = "";
			try {
				response = m_apdu.transmitControlCommand(code, control);
				trace("Response from CodeControl : " + bytes2String(response));
				if(response != null){					
					strResponse=bytes2String(response);
					msg.setM_strDataInHex(strResponse);
					msg.setM_strSWInHex("9000");
				}else{
					msg.setM_strSWInHex("8080");
					msg.setM_strDataInHex(strResponse);
				}
			} catch (SmartCard_Exception_UNKNOW e) {
				msg.setM_strSWInHex("8080");
				msg.setM_strDataInHex(strResponse);
			}
			
			
			return msg;
		}
		
		return null;
	}
	public static String bytes2String(byte[] b) {
		String result = "";
		for (int i = 0; i < b.length; i++) {
			String s = Integer.toHexString(b[i] & (0xff)).toUpperCase();
			if (s.length() == 1)
				s = "0" + s;
			result += s;
		}
		return result;
	}


	public static byte[] string2Bytes(String s) {
		byte b[] = new byte[s.length() / 2];
		for (int i = 0; i < b.length; i++) {
			int j = i * 2;
			b[i] = Integer.valueOf(s.substring(j, j + 2), 16).byteValue();
		}
		return b;
	}
	@Override
	public void setBasicUI(IUI_BasicInterface ui) {
		basicUI = ui;
		
	}
}
