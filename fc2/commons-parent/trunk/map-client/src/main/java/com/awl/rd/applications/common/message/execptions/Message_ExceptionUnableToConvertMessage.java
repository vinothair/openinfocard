package com.awl.rd.applications.common.message.execptions;

public class Message_ExceptionUnableToConvertMessage extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7060490295918136030L;

	public Message_ExceptionUnableToConvertMessage(String reason){
		super(reason);
	}
}
