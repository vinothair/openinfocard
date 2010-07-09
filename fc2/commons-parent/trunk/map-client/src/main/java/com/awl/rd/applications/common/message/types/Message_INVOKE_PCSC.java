package com.awl.rd.applications.common.message.types;

import java.util.Vector;

import com.awl.rd.applications.common.message.IMessage;
import com.awl.rd.applications.common.message.execptions.Message_ExceptionUnableToConvertMessage;
import com.utils.XMLParser;
import com.utils.execeptions.XMLParser_Exception_NO_ATTRIBUTE;

public class Message_INVOKE_PCSC implements IMessage {

	public final static String CMD_LISTTERMINAL = "LIST_TERMINALS";
	public final static String CMD_SELECTTERMINAL = "SELECT_TERMINAL";
	public final static String CMD_CREATEAPDU ="CREATEAPDU";
	public final static String CMD_UNKNOWN = "UNKNOW";
	public String cmd="";
	public String m_strReaderName="";
	public Vector<String> m_vecResponse =  new Vector<String>();
	public int Status;
	
	public final static int STATUS_NOREADER =-1;
	public final static int LIST_OF_READERS =0;
	public final static int TERMINAL_SELECTED = 1;
	public final static int NO_CARD=2;
	public final static int UNKNOW_ERROR=3;
	public final static int APDU_CREATED = 4;
	public void setType(String type){
		cmd = type;
	}
	public String getCommandType(){
		return cmd;		
	}
	public Vector<String> getResponse(){
		return m_vecResponse;
	}
	public String getReaderName(){return m_strReaderName;}
	public void setReaderName(String readername){m_strReaderName = readername;}
	public int getStatus(){return Status;}
	public void setStatus(int status){Status = status;};
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
		String toRet ="<MESSAGE>" +
					  "<TYPE>Message_INVOKE_PCSC</TYPE>" +
					  "<COMMAND>"+cmd+"</COMMAND>" +
					  "<READER_NAME>"+m_strReaderName+"</READER_NAME>" +					  
					  "<STATUS>"+Status+"</STATUS>"+
					  "<RESPONSE>"+m_vecResponse+"</RESPONSE>"+
					  "</MESSAGE>";
		
		return toRet;
	}
	@Override
	public void constructFromXML(String xml)
			throws Message_ExceptionUnableToConvertMessage {
		XMLParser parser = new XMLParser(xml);
		try {
			cmd = parser.getFirstValue("COMMAND");
			m_strReaderName = parser.getFirstValue("READER_NAME");
			Status = Integer.valueOf(parser.getFirstValue("STATUS")).intValue();
			String response = parser.getFirstValue("RESPONSE");
			response = response.replace("[", "");
			response = response.replace("]", "");
			String lst[] = response.split(",");
			m_vecResponse.clear();
			for(int i=0;i<lst.length;i++){
				m_vecResponse.add(lst[i].trim());
			}
		} catch (XMLParser_Exception_NO_ATTRIBUTE e) {
			throw(new Message_ExceptionUnableToConvertMessage(e.getMessage()));
		}					
	}

}
