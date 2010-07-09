package com.utils.execeptions;

public class XMLParser_Exception_NO_ATTRIBUTE extends Exception {
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
	public XMLParser_Exception_NO_ATTRIBUTE(String reason){
		super(reason);
	}
}
