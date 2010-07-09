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
 *     * Neither the names xmldap, xmldap.org, xmldap.com nor the
 *       names of its contributors may be used to endorse or promote products
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
 */
package com.awl.ws.messages.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Vector;

import javax.xml.soap.MessageFactory;
import javax.xml.soap.Node;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPConstants;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFactory;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.transform.TransformerException;

import org.w3c.dom.NodeList;

import com.awl.fc2.selector.exceptions.Config_Exception_NotDone;
import com.awl.fc2.selector.exceptions.Config_Exeception_MalFormedConfigFile;
import com.awl.fc2.selector.exceptions.FC2Authentication_Exeception_AuthenticationFailed;
import com.awl.logger.Logger;
import com.awl.ws.messages.IMetaDataExchange;

public class MetaDataExchange implements IMetaDataExchange {

	public String currentProtocol = SOAPConstants.DEFAULT_SOAP_PROTOCOL;
	static Logger log = new Logger(MetaDataExchange.class);
	
	public static void trace(Object obj){
		System.out.println(obj);
		
	}
	String mexURL;
	/* (non-Javadoc)
	 * @see com.awl.ws.messages.IMetaDataExchange#setMexURL(java.lang.String)
	 */
	public void setMexURL(String url){
		mexURL = url;
	}
	Vector<Node> m_vecsupportedToken = new Vector<Node>();
	Vector<String> m_vecSTSUrl = new Vector<String>();
	
	/* (non-Javadoc)
	 * @see com.awl.ws.messages.IMetaDataExchange#getSupportedToken()
	 */
	public Vector<Node> getSupportedToken(){
		return m_vecsupportedToken;
	}
	
	Vector<String> vecSupportedTokenURI = new Vector<String>();
	//public final String tokenType_X509 = "X509Token";
	public Vector<String> getSupportedTokenURI(){
		return vecSupportedTokenURI;
	}
	/* (non-Javadoc)
	 * @see com.awl.ws.messages.IMetaDataExchange#getSTSURL()
	 */
	public Vector<String> getSTSURL(){
		return m_vecSTSUrl;
	}
	/* (non-Javadoc)
	 * @see com.awl.ws.messages.IMetaDataExchange#doRequest()
	 */
	public void doRequest() throws UnsupportedOperationException, SOAPException, TransformerException, IOException, FC2Authentication_Exeception_AuthenticationFailed{
		//String destination = "";
        //First create the connection
         SOAPConnectionFactory soapConnFactory = 
                            SOAPConnectionFactory.newInstance();
         SOAPConnection connection = 
                             soapConnFactory.createConnection();

         
        //Send the message
        // mexURL = mexURL.replace("https", "http");
         NodeList lst = null;
         //try {
        	 
        	 SOAPMessage reply = connection.call(createSOAPMessage(), mexURL);
        	 System.out.println("SOAP REPLY ==");
        	 reply.writeTo(System.out);
             writeDebug("replyMEX.xml", reply);
             lst = reply.getSOAPBody().getElementsByTagNameNS("*", "*");
		/*} catch (SOAPException e) {
			try {
				HTTPTransport http = new HTTPTransport(mexURL,true);
				http.send("");
				String response = http.response;
				trace("HTTP RESPONSE = " + response);
			} catch (TransportException e1) {
				throw(new FC2Authentication_Exeception_AuthenticationFailed("Impossible to contact the server"));
			}
			
		}         */  	   
        
        trace("Lookin in response");
        for(int i=0;i<lst.getLength();i++){
        	Node node = ((Node)lst.item(i));
        
        	if(node.getLocalName().equalsIgnoreCase("SignedSupportingTokens") ||
        	   node.getLocalName().equalsIgnoreCase("EndorsingSupportingTokens"))
        	{
        		trace(i + " : " + 	node.getPrefix() +" Name = " + node.getLocalName());
        		NodeList SupportedTokenConfiguration = node.getChildNodes();
        		
        		for(int j=0;j<SupportedTokenConfiguration.getLength();j++){
        			Node tokenType = ((Node)SupportedTokenConfiguration.item(j));
        			for(int x=0;x<tokenType.getChildNodes().getLength();x++){
        				Node supportedToken = (Node) tokenType.getChildNodes().item(x);
        				if("http://schemas.xmlsoap.org/ws/2005/07/securitypolicy".equalsIgnoreCase(supportedToken.getNamespaceURI())){
        					String strSP = supportedToken.getLocalName();
        					trace("SP : " + strSP);
        					vecSupportedTokenURI.add(strSP);
        					m_vecsupportedToken.add(supportedToken);
        					
        					
        				}
        				
        			}
        		}
        	}
        	//EndpointReference
        	if(node.getLocalName().equalsIgnoreCase("EndpointReference"))
        	{
        		trace(i + " : " + 	node.getPrefix() +" Name = " + node.getLocalName());
        		NodeList STSEndPoints = node.getChildNodes();	        		
        		for(int j=0;j<STSEndPoints.getLength();j++){
        			Node endPoint = ((Node)STSEndPoints.item(j));
        			if("Address".equalsIgnoreCase(endPoint.getLocalName())){
        				String urlSTS = endPoint.getFirstChild().getNodeValue();
         				trace("Endpoint = " + urlSTS);
         				m_vecSTSUrl.add(urlSTS);
        			}
        				
        		}
        	}
        }
        trace("Response ended");
	}
	public SOAPMessage createSOAPMessage() throws SOAPException, TransformerException, IOException{
		SOAPMessage soapMessage=null;		
		String urn = "urn:uuid:10";
		soapMessage = MessageFactory.newInstance(currentProtocol).createMessage();//SOAPConstants.SOAP_1_2_PROTOCOL).createMessage();
		//soapMessage = MessageFactory.newInstance(SOAPConstants.SOAP_1_2_PROTOCOL).createMessage();
		SOAPPart soapPart = soapMessage.getSOAPPart();
	    SOAPEnvelope soapEnvelope = soapPart.getEnvelope();
	    soapEnvelope.addNamespaceDeclaration("a", "http://www.w3.org/2005/08/addressing");
	    soapEnvelope.addNamespaceDeclaration("env", "http://schemas.xmlsoap.org/soap/envelope/");
	    SOAPHeader soapHeader = soapEnvelope.getHeader();
	    //SOAPHeaderElement headerElement = soapHeader.addHeaderElement(soapEnvelope.createName(
	    //    "Signature", "SOAP-SEC", "http://schemas.xmlsoap.org/soap/security/2000-12"));
	    
	    
	   // soapHeader.addHeaderElement(soapEnvelope.createName(
		//        "Action", "a", "http://www.w3.org/2005/08/addressing"));
	    SOAPElement elem2= soapHeader.addHeaderElement(soapEnvelope.createName(
	    		"Action", "a", "http://www.w3.org/2005/08/addressing"));
	    elem2.setAttribute("env:mustUnderstand", "1");
	    elem2.addTextNode("http://schemas.xmlsoap.org/ws/2004/09/transfer/Get");
	   
		

	    soapHeader.addHeaderElement(soapEnvelope.createName(
		        "MessageID", "a", "http://www.w3.org/2005/08/addressing")).addTextNode(urn);
		        //.setAttribute("NAME", "VALUE");
	    SOAPElement element = SOAPFactory.newInstance().createElement("Address", "a", "http://www.w3.org/2005/08/addressing");
	    //soapHeader.addHeaderElement(soapEnvelope.createName(
		//        "ReplyTo", "a", "http://www.w3.org/2005/08/addressing")).addChildElement(element).addTextNode("http://www.w3.org/2005/08/addressing/anonymous");
	   soapHeader.addHeaderElement(soapEnvelope.createName(
		        "ReplyTo", "a", "http://www.w3.org/2005/08/addressing")).addChildElement(element).addTextNode("http://schemas.xmlsoap.org/ws/2004/08/addressing/role/anonymous");
	  
	    //<a:To s:mustUnderstand="1">URL_TO</a:To> 
	    SOAPElement elem= soapHeader.addHeaderElement(soapEnvelope.createName(
		        "To", "a", "http://www.w3.org/2005/08/addressing"));
	    elem.setAttribute("env:mustUnderstand", "1");
	    elem.addTextNode(mexURL);
	    

	    
	    //  SOAPBody soapBody = soapEnvelope.getBody();
	  //  soapBody.addAttribute(soapEnvelope.createName("id", "SOAP-SEC",
	   //     "http://schemas.xmlsoap.org/soap/security/2000-12"), "Body");
	  //  Name bodyName = soapEnvelope.createName("FooBar", "z", "http://example.com");
	  //  SOAPBodyElement gltp = soapBody.addBodyElement(bodyName);

	    //Source source = soapPart.getContent();
	    //writeDebug("requestMEX.xml", soapMessage);
	    
	    System.out.println("SOAP REQUEST ==");
	    soapMessage.writeTo(System.out);
	    writeDebug("mexRequest.xml", soapMessage);
	   return soapMessage;
	}
	
	public void writeDebug(String filename,SOAPMessage msg){
		File file;
		try {
			file = new File(com.awl.fc2.selector.launcher.Config.getInstance().getDebugFolder()+ filename);
			FileOutputStream fout = new FileOutputStream(file); 
			msg.writeTo(fout);
		    fout.close();
		} catch (Config_Exeception_MalFormedConfigFile e) {		
			log.trace("Unable to write debug files");
		} catch (Config_Exception_NotDone e) {
			log.trace("Unable to write debug files");
		} catch (SOAPException e) {
			log.trace("Unable to write debug files");
		} catch (IOException e) {
			log.trace("Unable to write debug files");
		}
	}
	@Override
	public void setSOAPProtocol(String protocol) {
		this.currentProtocol = protocol;
		
	}
}
