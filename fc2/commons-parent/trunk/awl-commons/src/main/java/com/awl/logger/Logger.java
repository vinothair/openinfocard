/*
 * Copyright (c) 2010, Atos Worldline - http://www.atosworldline.com/
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the names xmldap, xmldap.org, xmldap.com nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE REGENTS AND CONTRIBUTORS ``AS IS'' AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE REGENTS AND CONTRIBUTORS BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.awl.logger;

import java.net.URL;

import org.apache.log4j.PropertyConfigurator;

/**
 * Handle the log over all the project. <br/>
 * We are using Log4j.<br/>
 * {@code Example of use :}<br/>
 * {@code com.awl.logger.Logger log = new com.awl.logger.Logger(Object.class);}<br/>
 * {@code log.trace("nani");}
 * @author A168594 
 */
public class Logger {
	
	
	org.apache.log4j.Logger log = null;//org.apache.log4j.Logger.getLogger(T.getName());
	static {
		URL configFile = Logger.class.getResource("log4j.properties");
		System.out.println(configFile);
		PropertyConfigurator.configure(configFile);
		
	}
	/**
	 * Display the message {@code msg} in the log as INFO level
	 * @param msg
	 */
	public  void trace(Object msg){		
		log.info(msg);
	}
	/**
	 * Display the message {@code msg} in the log as ERROR level
	 * @param msg
	 */
	public  void err(Object msg){
		log.error(msg);
	}
	
	/**
	 * Display the message {@code msg} in the log as WARNING level
	 * @param msg
	 */
	public  void warn(Object msg){
		log.warn(msg);
	}
	
	public Logger(Class<?> cls) {
		log =  org.apache.log4j.Logger.getLogger(cls.getName());
	} 
	
}
