package com.awl.rd.applications.common.message.types;

import com.awl.rd.applications.common.message.IMessage;
import com.awl.rd.applications.common.message.execptions.Message_ExceptionUnableToConvertMessage;
import com.utils.XMLParser;
import com.utils.execeptions.XMLParser_Exception_NO_ATTRIBUTE;

public class Message_Error implements IMessage {

	String m_strError;
	public Message_Error() {
	
	}
	public Message_Error(String error) {
		m_strError = error;
	}
	public String toString(){
		return "ERROR MESSAGE :  " + m_strError;
	}
	@Override
	public void constructFromXML(String xml)
			throws Message_ExceptionUnableToConvertMessage {
		try {
			m_strError = XMLParser.getFirstValue(xml, "MESSAGE/ERROR");
		} catch (XMLParser_Exception_NO_ATTRIBUTE e) {
			throw(new Message_ExceptionUnableToConvertMessage(e.getMessage()));
		}

	}

	@Override
	public String toXML() {
		String toRet ="<MESSAGE>" +
		  "<TYPE>Message_Error</TYPE>" +
		  "<ERROR>"+m_strError+"</ERROR>"+
		  "</MESSAGE>";

			return toRet;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
