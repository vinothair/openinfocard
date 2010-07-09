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
 *     * The names of the contributors may NOT be used to endorse or promote products
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
 * 
 */
package com.utils;

import org.apache.log4j.Logger;
import org.xmldap.util.PropertiesManager;

import com.utils.impl.STSConfiguration_DriverLicence;
import com.utils.impl.STSConfiguration_EID;
import com.utils.impl.STSConfiguration_Payment;
import com.utils.impl.STSConfiguration_Telcos;
import com.utils.impl.STSConfiguration_Wallet;

public class FactorySTSConfiguration {


	static final String PROP_KEY= "STSConfigurator";
	static final String CONFIG_PAYMENT = "ConfigPayment";
	static final String CONFIG_WALLET = "ConfigWallet";
	static final String CONFIG_EID = "ConfigEID";
	static final String CONFIG_DriverLicence = "ConfigDriverLicence";
	static final String CONFIG_TELCOS = "ConfigTelcos";
	
	static  Logger log = Logger.getLogger(FactorySTSConfiguration.class);
	static public void trace(Object msg){
		log.info(msg);
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	public static ISTSConfiguration getInstance(String className){
		trace("GetInstance on "+ className);
		if(CONFIG_PAYMENT.equalsIgnoreCase(className)){
			trace("Configuration : PAYMENT");
			return new STSConfiguration_Payment();
		}
		
		if(CONFIG_WALLET.equalsIgnoreCase(className)){
			trace("Configuration : WALLET");
			return new STSConfiguration_Wallet();
		}
		if(CONFIG_EID.equalsIgnoreCase(className)){
			trace("Configuration : EID");
			return new STSConfiguration_EID();
		}
		if(CONFIG_DriverLicence.equalsIgnoreCase(className)){
			trace("Configuration : DriverLicence");
			return new STSConfiguration_DriverLicence();
		}
		if(CONFIG_TELCOS.equalsIgnoreCase(className)){
			trace("Configuration : Telcos");
			return new STSConfiguration_Telcos();
		}
		return null;
	}
	public static ISTSConfiguration getInstance(){
		String className = PropertiesManager.get(PROP_KEY);
		return getInstance(className);
		
	}
}
