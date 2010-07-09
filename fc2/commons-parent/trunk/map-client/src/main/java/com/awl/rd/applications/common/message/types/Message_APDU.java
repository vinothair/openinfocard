package com.awl.rd.applications.common.message.types;

import com.awl.rd.applications.common.message.IMessage;
import com.awl.rd.applications.common.message.execptions.Message_ExceptionUnableToConvertMessage;
import com.utils.XMLParser;
import com.utils.execeptions.XMLParser_Exception_NO_ATTRIBUTE;

public class Message_APDU implements IMessage {

	public final static int SENDAPDU = 0;
	public final static int SENDATR = 1;
	public final static int UNKNOWN = 2;
	public final static int SENDCARDCTROL=3;
	
	public void setState(int state){
		this.state = state;
	}
	public int getState(){
		return state;
	}
	public int state = UNKNOWN;
	String m_strAPDUInHex="";
	String m_strSWInHex="";
	String m_strDataInHex="";
	
	String m_iCCCode="";
	String m_strCCControl="";
	
	
	public String getM_strAPDUInHex() {
		return m_strAPDUInHex;
	}
	public void setM_strAPDUInHex(String inHex) {
		m_strAPDUInHex = inHex;
	}
	public String getM_strSWInHex() {
		return m_strSWInHex;
	}
	public void setM_strSWInHex(String inHex) {
		m_strSWInHex = inHex;
	}
	public String getM_strDataInHex() {
		return m_strDataInHex;
	}
	public void setM_strDataInHex(String dataInHex) {
		m_strDataInHex = dataInHex;
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
				"<TYPE>Message_APDU</TYPE>" +
				"<STATE>"+state+"</STATE>"+
				"<APDU>"+m_strAPDUInHex+"</APDU>" +
				"<CODE>"+m_iCCCode+"</CODE>"+
				"<CONTROL>"+m_strCCControl+"</CONTROL>"+
				"<SW>"+m_strSWInHex+"</SW>" +
				"<DATA>"+m_strDataInHex+"</DATA>"+
				"</MESSAGE>";
	}
	@Override
	public void constructFromXML(String xml)
			throws Message_ExceptionUnableToConvertMessage {
		XMLParser parser = new XMLParser(xml);
		try {
			m_strAPDUInHex = parser.getFirstValue("APDU");
			m_strSWInHex = parser.getFirstValue("SW");
			m_strDataInHex = parser.getFirstValue("DATA");
			m_iCCCode = parser.getFirstValue("CODE");
			m_strCCControl = parser.getFirstValue("CONTROL");
			state = Integer.valueOf(parser.getFirstValue("STATE"));
			
		} catch (XMLParser_Exception_NO_ATTRIBUTE e) {
			throw(new Message_ExceptionUnableToConvertMessage(e.getMessage()));
		}
		
	}
	public String getM_iCCCode() {
		return m_iCCCode;
	}
	public void setM_iCCCode(String mICCCode) {
		m_iCCCode = mICCCode;
	}
	public String getM_strCCControl() {
		return m_strCCControl;
	}
	public void setM_strCCControl(String mStrCCControl) {
		m_strCCControl = mStrCCControl;
	}

}
