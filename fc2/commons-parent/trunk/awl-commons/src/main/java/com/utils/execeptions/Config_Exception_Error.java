package com.utils.execeptions;

public class Config_Exception_Error extends Exception {
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
	public Config_Exception_Error(String reason){
		super(reason);
	}
}
