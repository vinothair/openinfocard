package com.awl.rd.applications.common.message.execptions;

import com.awl.rd.applications.common.message.types.Message_Error;

public class Message_ExceptionIsErrorMessage extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7060490295918136030L;
	public Message_Error theError;
	
	public Message_ExceptionIsErrorMessage(Message_Error err){
		super(err.toXML());
		theError = err;		
	}
}
