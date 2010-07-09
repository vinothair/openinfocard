package com.awl.rd.applications.common.message;

import com.awl.rd.applications.common.message.execptions.Message_ExceptionUnableToConvertMessage;

public interface IMessage {
	public String toXML();
	public void constructFromXML(String xml) throws Message_ExceptionUnableToConvertMessage;
	
}
