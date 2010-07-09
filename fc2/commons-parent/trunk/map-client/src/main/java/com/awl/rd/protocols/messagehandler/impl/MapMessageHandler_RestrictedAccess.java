package com.awl.rd.protocols.messagehandler.impl;

import java.util.Scanner;

import com.awl.rd.applications.common.message.IMessage;
import com.awl.rd.applications.common.message.types.Message_AskQuestion;
import com.awl.rd.protocols.messagehandler.IMapMessageHandler;
import com.awl.rd.protocols.messagehandler.IUI_BasicInterface;

public class MapMessageHandler_RestrictedAccess implements IMapMessageHandler{
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

	IUI_BasicInterface basicUI;
	
	public IMessage handleMessage(IMessage msg){
		System.out.println("handleMesse : "  + msg);
		if(msg instanceof Message_AskQuestion){
			return processQuestion((Message_AskQuestion) msg);
		}
		return null;
	}
	public String getXMLContextForID(String id){
		return  "<CONTEXT>"+
		"<TRANSACTION_BANCAIRE>"+
		"</TRANSACTION_BANCAIRE>"+
		"<MESSAGES_UNDERSTANDING>"+
		/*"<MESSAGE_TYPE>INVOKE_READER</MESSAGE_TYPE>"+
		"<MESSAGE_TYPE>INVOKE_PCSC</MESSAGE_TYPE>"+
		"<MESSAGE_TYPE>APDUCommand</MESSAGE_TYPE>"+
		"<MESSAGE_TYPE>APDUResponse</MESSAGE_TYPE>"+*/
		"<MESSAGE_TYPE>NOTIFICATION</MESSAGE_TYPE>"+
		"<MESSAGE_TYPE>DISPLAY_QUESTION</MESSAGE_TYPE>"+
		"</MESSAGES_UNDERSTANDING>"+
		"<USER>"+
		"<ID>"+id+"</ID> "+
		"</USER>"+
		"</CONTEXT>";
	}
	@Override
	public void setBasicUI(IUI_BasicInterface ui) {
		basicUI = ui;
		
	}
}
