package com.awl.rd.protocols.messagehandler;

import com.awl.rd.applications.common.message.IMessage;

public interface IMapMessageHandler {
	IMessage handleMessage(IMessage msg);
	String getXMLContextForID(String userId);
	public void setBasicUI(IUI_BasicInterface ui);
	
}
