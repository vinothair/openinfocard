package com.awl.rd.protocols.messagehandler.impl;

import java.util.Scanner;


import com.awl.logger.Logger;
import com.awl.rd.protocols.messagehandler.IUI_BasicInterface;
import com.utils.XMLParser;
import com.utils.execeptions.XMLParser_Exception_NO_ATTRIBUTE;
import com.utils.execeptions.XMLParser_Exception_NoNextValue;

public class BasicUI_Console implements IUI_BasicInterface {
	static Logger log = new Logger(BasicUI_Console.class);
	public static void trace(Object msg){
		log.trace(msg);
	}
	
	@Override
	public int sendChooseMethod(String xmlAuth) {
		XMLParser parser = new XMLParser(xmlAuth);
		
		try {
			parser.query("URI_CONFIG");
		} catch (XMLParser_Exception_NO_ATTRIBUTE e) {
			trace("Failed parsing xmlAuth return 0");
			return 0;
		}
		trace("Quelle m�thode ? ");
		int cpt=0;
		while(parser.hasNext()){
			try {
				trace(cpt++ + ") : " + parser.getNextValue());
			} catch (XMLParser_Exception_NoNextValue e) {
				trace("Failed getting method return 0");
				return 0;
			}
		}							
		Scanner in = new Scanner(System.in);
		String response = in.nextLine();
		int idx = Integer.valueOf(response).intValue();
		return idx;
	}

	@Override
	public void sendNotification(String Title, String caption, boolean modal) {
		trace("["+Title+"]="+caption);
		if(modal)
		{
			System.out.println("Press a key.");
			Scanner in = new Scanner(System.in);
			in.nextLine();
		}
	}

	@Override
	public String sendQuestion(String Title, String question,boolean dspAsPwd) {
		System.out.println("["+Title+"]"+question);
		Scanner in = new Scanner(System.in);
		String response = in.nextLine();
		return response;
	}

}
