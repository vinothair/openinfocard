package com.utils;

import org.apache.log4j.Logger;

public class InitBenchMarck {
	static Logger log = Logger.getLogger(InitBenchMarck.class);
	public static void trace(Object message){
		log.info(message);
	}
	public static void main(String arg[]){
		
		
		trace("Total intialization of the cards for (stef and fj)");
		ISTSConfiguration configurator = FactorySTSConfiguration.getInstance();
		configurator.configure();
		configurator.run();
		configurator.test();
		//new CreateCards().run();
		//CreateCRD.run();
		trace("Enroll succeed");
		
		trace("Don't forget to update the SDD database...");
	}
}
