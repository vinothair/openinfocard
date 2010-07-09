package com.awl.rd.applications.common.message.types;

import com.awl.rd.applications.common.message.IMessage;
import com.awl.rd.applications.common.message.execptions.Message_ExceptionUnableToConvertMessage;
import com.utils.XMLParser;
import com.utils.execeptions.XMLParser_Exception_NO_ATTRIBUTE;

public class Message_AskQuestion implements IMessage {

	String m_strQuestion;
	String m_strResponse;
	public Message_AskQuestion(String question) {
		m_strQuestion = question;
	}
	public Message_AskQuestion() {
		// TODO Auto-generated constructor stub
	}
	public String getQuestion(){
		return m_strQuestion;
	}
	public String getResponse(){
		return m_strResponse;
	}
	public void setResponse(String response){
		m_strResponse = response;
	}
	public String toString(){
		return this.getClass().getName();
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	@Override
	public String toXML() {
		
		return "<MESSAGE>" +
				"<TYPE>Message_AskQuestion</TYPE>" +
				"<QUESTION>" + m_strQuestion+"</QUESTION>" +
				"<REPONSE>"+m_strResponse+"</REPONSE>"+
				"</MESSAGE>";
	}
	@Override
	public void constructFromXML(String xml)
			throws Message_ExceptionUnableToConvertMessage {
		XMLParser parser = new XMLParser(xml);
		try {
			m_strQuestion = parser.getFirstValue("QUESTION");
			m_strResponse = parser.getFirstValue("REPONSE");
		} catch (XMLParser_Exception_NO_ATTRIBUTE e) {
			throw(new Message_ExceptionUnableToConvertMessage(e.getMessage()));
		}
		
		
	}

}
