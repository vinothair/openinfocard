package com.awl.rd.applications.common.message.types;

import com.awl.rd.applications.common.message.IMessage;
import com.awl.rd.applications.common.message.execptions.Message_ExceptionUnableToConvertMessage;

public class Message_Start implements IMessage {

	public String toString(){
		return this.getClass().getName();
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	@Override
	public String toXML() {
		return "<MESSAGE><TYPE>Message_Start</TYPE></MESSAGE>";
	}
	@Override
	public void constructFromXML(String xml) throws Message_ExceptionUnableToConvertMessage {
		
		
	}

}
