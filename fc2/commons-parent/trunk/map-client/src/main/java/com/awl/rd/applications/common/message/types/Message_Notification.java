package com.awl.rd.applications.common.message.types;

import com.awl.rd.applications.common.message.IMessage;
import com.awl.rd.applications.common.message.execptions.Message_ExceptionUnableToConvertMessage;
import com.utils.XMLParser;
import com.utils.execeptions.XMLParser_Exception_NO_ATTRIBUTE;

public class Message_Notification implements IMessage {

	String m_strNotify;
	public void setNotification(String notif){
		m_strNotify = notif;
	}
	public String  getNotification(){
		return m_strNotify;
	}
	public Message_Notification() {
	
	}
	public Message_Notification(String notif) {
		setNotification(notif);
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
				"<TYPE>Message_Notification</TYPE>" +
				"<NOTIFICATION>"+m_strNotify+"</NOTIFICATION>" +
				"</MESSAGE>";
	}
	@Override
	public void constructFromXML(String xml) throws Message_ExceptionUnableToConvertMessage {
		try {
			m_strNotify = XMLParser.getFirstValue(xml, "MESSAGE/NOTIFICATION");
		} catch (XMLParser_Exception_NO_ATTRIBUTE e) {
			throw(new Message_ExceptionUnableToConvertMessage(e.getMessage()));
		}
		
		
		
	}

}
