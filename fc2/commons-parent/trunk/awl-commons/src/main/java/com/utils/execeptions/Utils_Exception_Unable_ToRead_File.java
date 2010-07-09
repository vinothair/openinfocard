package com.utils.execeptions;

public class Utils_Exception_Unable_ToRead_File extends Exception {
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
	public Utils_Exception_Unable_ToRead_File(String reason){
		super(reason);
	}
}
