package com.utils;


import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;



public class Logger {
	//org.apache.log4j.Logger log = null;//org.apache.log4j.Logger.getLogger(T.getName());
	static {
		URL configFile = Logger.class.getResource("log4j.properties");
		System.out.println(configFile);
		//PropertyConfigurator.configure(configFile);
		configureIfPossible(configFile);
		
	}
	Method methTRACE=null;
	Method methWARN=null;
	Method methINFO=null;
	Method methERR=null;
	Object log = null;
	Object createLogger(String name){
		System.out.println("Creating logger");
		String clsToLoad = "org.apache.log4j.Logger";//.getLogger(cls.getName());
		//org.apache.log4j.Logger.getLogger(this.getClass())
		
		try {
			Class<?> cls = Class.forName(clsToLoad);
			
			Method tabMethod[] = cls.getDeclaredMethods();
			for(Method cur:tabMethod){
				System.out.println(">> -- " + cur.getName());
				if(cur.getName().contains("getLogger")){
					
					if(cur.getParameterTypes().length == 1){
						//System.out.println(cur.getParameterTypes()[0]);
						if(cur.getParameterTypes()[0].equals(String.class)){
							System.out.println("Found the getLogger method");
							try {
								log = cur.invoke(null, name);
								
							} catch (IllegalArgumentException e) {
								System.out.println("Failed invoking getLogger");
								return null;
							} catch (IllegalAccessException e) {
								System.out.println("Failed invoking getLogger");
								return null;
							} catch (InvocationTargetException e) {
								System.out.println("Failed invoking getLogger");
								return null;
							}
							//return null;
						}
					}
					
				}
				
			
				try {
					methTRACE = cls.getMethod("trace", Object.class);
				} catch (SecurityException e) {
					methTRACE=null;
				} catch (NoSuchMethodException e) {
					methTRACE=null;
				}
				try {
					methINFO = cls.getMethod("info", Object.class);
				} catch (SecurityException e) {
					methINFO=null;
				} catch (NoSuchMethodException e) {
					methINFO=null;
				}
				try {
					methWARN = cls.getMethod("warn", Object.class);
				} catch (SecurityException e) {
					methWARN=null;
				} catch (NoSuchMethodException e) {
					methWARN=null;
				}
				try {
					methERR = cls.getMethod("error", Object.class);
				} catch (SecurityException e) {
					methERR=null;
				} catch (NoSuchMethodException e) {
					methERR=null;
				}
				
				
			}
		} catch (ClassNotFoundException e) {
			System.out.println("Impossible to find the property logger");
		}
		
		
		return log;
	}
	
	static void configureIfPossible(URL url){
		String clsToLoad = "org.apache.log4j.PropertyConfigurator";
		try {
			Class<?> cls = Class.forName(clsToLoad);
			Method tabMethod[] = cls.getDeclaredMethods();
			
			for(Method cur:tabMethod){
				System.out.println(">> -- " + cur.getName());
				if(cur.getName().contains("configure")){
					
					if(cur.getParameterTypes().length == 1){
						//System.out.println(cur.getParameterTypes()[0]);
						if(cur.getParameterTypes()[0].equals(URL.class)){
							System.out.println("Found the right method");
							try {
								cur.invoke(null, url);
							} catch (IllegalArgumentException e) {
								System.out.println("Failed invoking configure");
							} catch (IllegalAccessException e) {
								System.out.println("Failed invoking configure");
							} catch (InvocationTargetException e) {
								System.out.println("Failed invoking configure");
							}
						}
					}
					
				}
			}
		} catch (ClassNotFoundException e) {
			System.out.println("Impossible to find the property logger");
		}
		
	}
	/**
	 * Display the message {@code msg} in the log as INFO level
	 * @param msg
	 */
	public  void trace(Object msg){		
		//log.trace(msg);
		if(methTRACE != null){
			try {
				methTRACE.invoke(log, msg);
				return;
			} catch (IllegalArgumentException e) {
				System.err.println("Logger does not work");
			} catch (IllegalAccessException e) {
				System.err.println("Logger does not work");
			} catch (InvocationTargetException e) {
				System.err.println("Logger does not work");
			}
			methTRACE = null;
		}else{
			System.out.println("NOLOGGER : " + msg);
		}
	}
	/**
	 * Display the message {@code msg} in the log as ERROR level
	 * @param msg
	 */
	public  void err(Object msg){
		//log.error(msg);
		if(methERR != null){
			try {
				methERR.invoke(log, msg);
				return;
			} catch (IllegalArgumentException e) {
				System.err.println("Logger does not work");
			} catch (IllegalAccessException e) {
				System.err.println("Logger does not work");
			} catch (InvocationTargetException e) {
				System.err.println("Logger does not work");
			}
			methERR = null;
		}else{
			System.out.println("NOLOGGER : " + msg);
		}
	}
	
	/**
	 * Display the message {@code msg} in the log as WARNING level
	 * @param msg
	 */
	public  void warn(Object msg){
		//log.warn(msg);
		if(methWARN != null){
			try {
				methWARN.invoke(log, msg);
				return;
			} catch (IllegalArgumentException e) {
				System.err.println("Logger does not work");
			} catch (IllegalAccessException e) {
				System.err.println("Logger does not work");
			} catch (InvocationTargetException e) {
				System.err.println("Logger does not work");
			}
			methWARN = null;
		}else{
			System.out.println("NOLOGGER : " + msg);
		}
		
	}
	
	public Logger(Class<?> cls) {
		log =  createLogger(cls.getName());//org.apache.log4j.Logger.getLogger(cls.getName());
	} 
	public static void main(String arg[]){
		System.out.println("IO");
	}
}
