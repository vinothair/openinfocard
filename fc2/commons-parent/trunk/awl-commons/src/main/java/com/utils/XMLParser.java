package com.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Nodes;
import nu.xom.ParsingException;
import nu.xom.XPathContext;



import com.awl.rd.applications.common.exceptions.APP_Exception_InternalError;

import com.utils.execeptions.XMLParser_Exception_NO_ATTRIBUTE;
import com.utils.execeptions.XMLParser_Exception_NoNextValue;

public class XMLParser {

	Builder m_parser = new Builder();
	Document m_mex = null;
	public XMLParser(File file) throws FileNotFoundException{
		
			FileReader fin = new FileReader(file);
			StringBuffer buf = new StringBuffer();
			char buffer[] = new char[50];
			int read = 1;
			while(read!= -1){
				try {
					read = fin.read(buffer);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if(read != -1){
					buf.append(buffer,0,read);
				}
				
			}
			initFromStringXML(buf.toString());
			
			
			
		
	}
	private void initFromStringXML(String xml){
		
		try {
		    m_mex = m_parser.build(xml, "");
		} catch (ParsingException e) {
		    //throw new ServletException(e);
			e.printStackTrace();
		} catch (IOException e) {
		   // throw new ServletException(e);
			e.printStackTrace();
		}
	}
	public XMLParser(String xml) {
		initFromStringXML(xml);
		
	}
	Nodes m_messageIDs = null;
	int curPos;
	public void query(String query) throws XMLParser_Exception_NO_ATTRIBUTE{
		XPathContext context = new XPathContext();		
		m_messageIDs = m_mex.query("//"+query, context);
		curPos=0;
		//throw(new XMLParser_Exception_NO_ATTRIBUTE(query));
	}
	
	public void query(String query,String prefix,String uri) throws XMLParser_Exception_NO_ATTRIBUTE{
		XPathContext context = new XPathContext(prefix,query);		
		m_messageIDs = m_mex.query("//"+query, context);
		curPos=0;
		//throw(new XMLParser_Exception_NO_ATTRIBUTE(query));
	}
	
	public void resetIterator(){
		curPos=0;
	}
	public String getNextValue() throws XMLParser_Exception_NoNextValue{
		if(!hasNext()) throw( new XMLParser_Exception_NoNextValue("getNextValue"));
		String res = m_messageIDs.get(curPos).getValue();
		curPos++;
		return res;
	}
	public String getNextXML() throws XMLParser_Exception_NoNextValue{
		if(!hasNext()) throw(new XMLParser_Exception_NoNextValue("getNextXML"));
		String res = m_messageIDs.get(curPos).toXML();
		curPos++;
		return res;
	}
	public boolean hasNext(){
		return (curPos<m_messageIDs.size());
		
	}
	public String getFirstValue(String query) throws XMLParser_Exception_NO_ATTRIBUTE{
		XPathContext context = new XPathContext();		
		Nodes messageIDs = m_mex.query("//"+query, context);
		for(int i=0;i<messageIDs.size();){
			Element messageID = (Element) messageIDs.get(i);
			//System.out.println(messageID.getValue());
			return messageID.getValue();
		}
		
		throw(new XMLParser_Exception_NO_ATTRIBUTE(query));
	}
	
	public static String getFirstValue(String xml,String query) throws XMLParser_Exception_NO_ATTRIBUTE{
		Builder parser = new Builder();
		Document mex = null;
		try {
		    mex = parser.build(xml, "");
		} catch (ParsingException e) {
		    //throw new ServletException(e);
			e.printStackTrace();
		} catch (IOException e) {
		   // throw new ServletException(e);
			e.printStackTrace();
		}
		//XPathContext context = new XPathContext("")
		XPathContext context = new XPathContext();		
		Nodes messageIDs = mex.query("//"+query, context);
		for(int i=0;i<messageIDs.size();){
			Element messageID = (Element) messageIDs.get(i);
			//System.out.println(messageID.getValue());
			return messageID.getValue();
		}
		throw(new XMLParser_Exception_NO_ATTRIBUTE(query));
	}
	public static String getFirstXML(String xml,String query) throws XMLParser_Exception_NO_ATTRIBUTE{
		Builder parser = new Builder();
		Document mex = null;
		try {
		    mex = parser.build(xml, "");
		} catch (ParsingException e) {
		    //throw new ServletException(e);
			e.printStackTrace();
		} catch (IOException e) {
		   // throw new ServletException(e);
			e.printStackTrace();
		}
		//XPathContext context = new XPathContext("")
		XPathContext context = new XPathContext();		
		Nodes messageIDs = mex.query("//"+query, context);
		for(int i=0;i<messageIDs.size();){
			Element messageID = (Element) messageIDs.get(i);
			//System.out.println(messageID.getValue());
			return messageID.toXML();
		}
		throw(new XMLParser_Exception_NO_ATTRIBUTE(query));
	}
	public static String getFirstXML(String xml,String query,String prefix,String uri) throws XMLParser_Exception_NO_ATTRIBUTE{
		Builder parser = new Builder();
		Document mex = null;
		try {
		    mex = parser.build(xml, "");
		} catch (ParsingException e) {
		    //throw new ServletException(e);
			e.printStackTrace();
		} catch (IOException e) {
		   // throw new ServletException(e);
			e.printStackTrace();
		}
		//XPathContext context = new XPathContext("")
		XPathContext context = new XPathContext(prefix,uri);		
		Nodes messageIDs = mex.query("//"+query, context);
		for(int i=0;i<messageIDs.size();){
			Element messageID = (Element) messageIDs.get(i);
			//System.out.println(messageID.getValue());
			return messageID.toXML();
		}
		throw(new XMLParser_Exception_NO_ATTRIBUTE(query));
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
	/*	IAPP_Identification_Exported_Functions test = new APP_Identification_Exported_Functions();
		String xml = test.startSession();
		System.out.println(xml);
		try {
			System.out.println(XMLParser.getFirstValue(xml, "COMPLETE"));
		} catch (Exception e) {
			// TODO: handle exception
		}
		*/
		
	}
	
	
	//SPECIFIC TO APPS
	public static  String getSessionIDFromResponse(String xml) throws APP_Exception_InternalError{
		try {
			return XMLParser.getFirstValue(xml,Constant_XML.XML_BALISE_SESSIONID);
		} catch (XMLParser_Exception_NO_ATTRIBUTE e) {
			throw(new APP_Exception_InternalError(xml));
		}
	}
	public static  String getValueFromResponseXML(String xml) throws APP_Exception_InternalError{
		try {
			return XMLParser.getFirstXML(xml,Constant_XML.XML_BALISE_RETURN_VALUE);
		} catch (XMLParser_Exception_NO_ATTRIBUTE e) {
			throw(new APP_Exception_InternalError(xml));
		}
	}
	public static  String getValueFromResponseValue(String xml) throws APP_Exception_InternalError{
		try {
			return XMLParser.getFirstValue(xml,Constant_XML.XML_BALISE_RETURN_VALUE);
		} catch (XMLParser_Exception_NO_ATTRIBUTE e) {
			throw(new APP_Exception_InternalError(xml));
		}
	}
	public static  String getErrorFromResponse(String xml) throws APP_Exception_InternalError{
		try {
			return XMLParser.getFirstValue(xml,Constant_XML.XML_BALISE_ERROR);
		} catch (XMLParser_Exception_NO_ATTRIBUTE e) {
			throw(new APP_Exception_InternalError(xml));
		}
	}
	public static  boolean isCallCompleted(String xml){
		try {
			if("OK".equalsIgnoreCase(XMLParser.getFirstValue(xml, Constant_XML.XML_BALISE_COMPLETE)))
				return true;
		} catch (XMLParser_Exception_NO_ATTRIBUTE e) {
			//warning("Error in parsing, assuming false completion");
		}
		return false;
	}
	public void checkifError(String xml) throws APP_Exception_InternalError{
		try {
			if("NOT_OK".equalsIgnoreCase(XMLParser.getFirstValue(xml, Constant_XML.XML_BALISE_COMPLETE))){
				throw(new APP_Exception_InternalError(XMLParser.getFirstValue(xml, Constant_XML.XML_BALISE_ERROR)));
			}
		} catch (XMLParser_Exception_NO_ATTRIBUTE e) {
			throw(new APP_Exception_InternalError(xml));
		}
	}

}
