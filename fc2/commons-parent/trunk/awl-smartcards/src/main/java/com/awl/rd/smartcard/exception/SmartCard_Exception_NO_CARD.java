package com.awl.rd.smartcard.exception;

public class SmartCard_Exception_NO_CARD extends Exception {
	static public void trace(Object obj){
		System.out.println(obj);
	}
	static public void warning(Object msg){
		System.out.println(msg);
	}
	static public void err(Object msg){
		System.err.println(msg);
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public SmartCard_Exception_NO_CARD(String reason){
		super(reason);
	}
}
