package com.awl.rd.applications.common.message;

import com.awl.rd.applications.common.message.execptions.Message_ExceptionIsErrorMessage;
import com.awl.rd.applications.common.message.execptions.Message_ExceptionUnableToConvertMessage;
import com.awl.rd.applications.common.message.types.Message_APDU;
import com.awl.rd.applications.common.message.types.Message_AskQuestion;
import com.awl.rd.applications.common.message.types.Message_CSP;
import com.awl.rd.applications.common.message.types.Message_Error;
import com.awl.rd.applications.common.message.types.Message_INVOKE_PCSC;
import com.awl.rd.applications.common.message.types.Message_Notification;
import com.awl.rd.applications.common.message.types.Message_Start;
import com.utils.XMLParser;
import com.utils.execeptions.XMLParser_Exception_NO_ATTRIBUTE;
import com.utils.execeptions.XMLParser_Exception_NoNextValue;

public class MessageAdaptater {

	public static IMessage xml2Msg(String xmlMessage) throws Message_ExceptionUnableToConvertMessage, Message_ExceptionIsErrorMessage{
		if(xmlMessage == null || xmlMessage.length()==0 ) return null;
		XMLParser parser = new XMLParser(xmlMessage);
		IMessage toret = null;
		try {
			parser.query("MESSAGE/TYPE");
		} catch (XMLParser_Exception_NO_ATTRIBUTE e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(parser.hasNext()){
			String value;
			try {
				value = parser.getNextValue();
				if("Message_APDU".equalsIgnoreCase(value)){
					toret =new Message_APDU();
					toret.constructFromXML(xmlMessage);
					return toret;
				}
				if("Message_Notification".equalsIgnoreCase(value)){
					toret =new Message_Notification();
					toret.constructFromXML(xmlMessage);
					return toret;
				}
				if("Message_AskQuestion".equalsIgnoreCase(value)){
					toret =new Message_AskQuestion();
					toret.constructFromXML(xmlMessage);
					return toret;
				}
				if("Message_INVOKE_PCSC".equalsIgnoreCase(value)){
					toret =new Message_INVOKE_PCSC();
					toret.constructFromXML(xmlMessage);
					return toret;
				}
				if("Message_Start".equalsIgnoreCase(value)){
					toret =new Message_Start();
					toret.constructFromXML(xmlMessage);
					return toret;
				}
				if("Message_CSP".equalsIgnoreCase(value)){
					toret =new Message_CSP();
					toret.constructFromXML(xmlMessage);
					return toret;
				}
				if("Message_Error".equalsIgnoreCase(value)){
					toret =new Message_Error();
					toret.constructFromXML(xmlMessage);
					throw(new Message_ExceptionIsErrorMessage((Message_Error)toret));
					//return toret;
				}
			} catch (XMLParser_Exception_NoNextValue e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
						
		}
		
		throw(new Message_ExceptionUnableToConvertMessage(xmlMessage));
	}
	public static String Msg2xml(IMessage msg){
		if(msg == null) return "";
		return msg.toXML();
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
