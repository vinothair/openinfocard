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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Vector;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPConstants;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;

import org.xml.sax.SAXException;
import org.xmldap.crypto.CryptoUtils;
import org.xmldap.exceptions.CryptoException;

import com.awl.fc2.selector.exceptions.Config_Exception_NotDone;
import com.awl.fc2.selector.exceptions.Config_Exeception_MalFormedConfigFile;
import com.awl.logger.Logger;
import com.awl.ws.messages.IRequestSecurityToken;
import com.awl.ws.messages.authentication.IToken;
import com.awl.ws.messages.namespaces.SOAPUtility;
import com.awl.ws.messages.namespaces.WSAddressing;
import com.awl.ws.messages.namespaces.WSCardIdentity;
import com.awl.ws.messages.namespaces.WSPolicy;
import com.awl.ws.messages.namespaces.WSSecurityUtility;
import com.awl.ws.messages.utilities.XMLNameSpaceAbstract;


public class RequestSecurityToken_1_3 extends XMLNameSpaceAbstract implements IRequestSecurityToken {
	static Logger log = new Logger(RequestSecurityToken_1_3.class);
	String prefix;
	public String requestType;
	public String tokenType;
	public String curSOAPProtocol = SOAPConstants.DEFAULT_SOAP_PROTOCOL;
	/* (non-Javadoc)
	 * @see com.awl.ws.messages.IRST#setTokenType(java.lang.String)
	 */
	public void setTokenType(String tt){
		tokenType = tt;
	}
	public String getRequestType(){return requestType;}
	@Override
	public String getPrefix() {
		// TODO Auto-generated method stub
		return prefix;
	}
	@Override
	public String getURI() {
		// TODO Auto-generated method stub
		return URI_1_3;
	} 
	public RequestSecurityToken_1_3(String prefix,String requestType) {
		this.prefix = prefix;
		this.requestType = requestType;
	}
	String m_strURLEndPoint;
	/* (non-Javadoc)
	 * @see com.awl.ws.messages.IRST#setEndPoint(java.lang.String)
	 */
	public void setEndPoint(String urlEndPoint){
		m_strURLEndPoint = urlEndPoint;
	}
	String requestorURL;
	String certificateB64;
	/* (non-Javadoc)
	 * @see com.awl.ws.messages.IRST#setRequestor(java.lang.String, java.lang.String)
	 */
	public void setRequestor(String url,String certif){
		requestorURL = url;
		certificateB64 = certif;
	}
	Vector<String> m_vecClaims = new Vector<String>();
	/* (non-Javadoc)
	 * @see com.awl.ws.messages.IRST#addClaims(java.lang.String)
	 */
	public void addClaims(String claims){
		m_vecClaims.add(claims);
	}
	WSCardIdentity cardId = new WSCardIdentity("wsid");
	/* (non-Javadoc)
	 * @see com.awl.ws.messages.IRST#setCardId(java.lang.String)
	 */
	public void setCardId(String strCardId){
	//	strCardId.replace("https", "http");
		cardId.setCardId(strCardId);
	}
	IToken tokenAuthentication;
	/* (non-Javadoc)
	 * @see com.awl.ws.messages.IRST#setAuthenticationHandler(com.awl.ws.messages.authentication.IToken)
	 */
	public void setAuthenticationHandler(IToken token){
		tokenAuthentication = token;
	}
	public String ppid;
	/* (non-Javadoc)
	 * @see com.awl.ws.messages.IRST#setPPID(java.lang.String)
	 */
	public void setPPID(String ppid){
		this.ppid = ppid;
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
	public SOAPMessage createSOAPMessage() throws SOAPException{
		SOAPMessage soapMessage = null;
		soapMessage = MessageFactory.newInstance(curSOAPProtocol).createMessage();
		SOAPPart soapPart = soapMessage.getSOAPPart();
		SOAPEnvelope soapEnvelope = soapPart.getEnvelope();
	    WSAddressing adressing = new WSAddressing("a");
	    WSSecurityUtility security = new WSSecurityUtility("u");
	    security.addNameSpace(soapEnvelope);
	    adressing.addNameSpace(soapEnvelope);
	    
	  //  addNameSpace(soapEnvelope);
	    												   //http://docs.oasis-open.org/ws-sx/ws-trust/200512/RST/Issue
	 //   security.addId(adressing.createAction(soapEnvelope,requestType/*; "http://schemas.xmlsoap.org/ws/2005/02/trust/RST/Issue")*/),"_1"); 
	    security.addId(adressing.createAction(soapEnvelope, "http://docs.oasis-open.org/ws-sx/ws-trust/200512/RST/Issue"),"_1");
	//    security.addId(adressing.createAction(soapEnvelope, REQUEST_TYPE_ISSUING),"_1");
	    //SOAPUtility soap = new SOAPUtility("SOAP-ENV");
	    SOAPUtility soap = new SOAPUtility("env");
	    soap.configureMustUnderstand(adressing.createTo(soapEnvelope, m_strURLEndPoint));
	    
	    //WSSecurityExtention secExt = new WSSecurityExtention("o");
	    //secExt.createSecurity(soapEnvelope);
	    //UserNameToken token = new UserNameToken("o");
	    tokenAuthentication.setSecurityUtility(security);
	    
	    soap.configureMustUnderstand(tokenAuthentication.createToken(soapEnvelope.getHeader()));
	    
	    
	    
	    SOAPBody body = soapEnvelope.getBody();
	    SOAPElement bodyRST = body.addChildElement(soapEnvelope.createName("RequestSecurityToken", prefix, URI_1_3));
	    bodyRST.addNamespaceDeclaration(prefix, URI_1_3);
	    bodyRST.setAttribute("Context", "ProcessRequestSecurityToken");
	    
	    WSPolicy policy = new WSPolicy("p");
	    policy.setWSAdressing(adressing);
//	    policy.setApplyTo(m_strURLEndPoint, certificateB64);
//	    policy.createApplyTo(bodyRST);
	    if(certificateB64 != null){
	    	try {
				CryptoUtils.X509fromB64(certificateB64);
				policy.setApplyTo(m_strURLEndPoint, certificateB64);
				policy.createApplyTo(bodyRST);
			} catch (CryptoException e) {

			}
	    }
	   // adressing.createAdress(bodyRST, this.m_strURLEndPoint);
	   
	    bodyRST.addChildElement(bodyRST.createQName("RequestType", prefix)).addTextNode(this.requestType);
	   
	   
	    
	    cardId.createInformationCardReference(bodyRST);
	    
	    SOAPElement theClaims = createClaimsBlock(bodyRST);//.addChildElement(bodyRST.createQName("Claims", prefix));
	   // WSIdentity id = new WSIdentity("wsid");
	  /*  cardId.addClaims(theClaims, "http://www.fc2consortium.org/ws/2008/10/identity/claims/paymentcardnumber");
	    cardId.addClaims(theClaims, "http://www.fc2consortium.org/ws/2008/10/identity/claims/paymentcardverification");
	    cardId.addClaims(theClaims,"http://www.fc2consortium.org/ws/2008/10/identity/claims/paymentcardexpdatemonth");
	    cardId.addClaims(theClaims,"http://www.fc2consortium.org/ws/2008/10/identity/claims/paymentcardexpdateyear");
	    cardId.addClaims(theClaims,"http://schemas.xmlsoap.org/ws/2005/05/identity/claims/privatepersonalidentifier");
		*/
	    for(String claim : m_vecClaims){
	    	cardId.addClaims(theClaims,claim);
	    }
	    setNoProofKey(bodyRST);
	    cardId.addClientPseudonym(bodyRST,ppid);
	    bodyRST.addChildElement(bodyRST.createQName("TokenType", prefix)).addTextNode(this.tokenType);
	    cardId.addRequestDisplayToken(bodyRST);
	    // theClaims.addAttribute("http://www.fc2consortium.org/ws/2008/10/identity/claims/paymentcardnumber", value)
	    
	    //http://schemas.xmlsoap.org/ws/2005/05/identity/NoProofKey
	    
		return soapMessage;
	}
	
	private void setNoProofKey(SOAPElement elem) throws SOAPException{
	//	elem.addChildElement(elem.createQName("KeyType", prefix)).addTextNode("http://schemas.xmlsoap.org/ws/2005/05/identity/NoProofKey");
		elem.addChildElement(elem.createQName("KeyType", prefix)).addTextNode("http://docs.oasis-open.org/ws-sx/ws-trust/200512/Bearer");
	}
	public SOAPElement createClaimsBlock(SOAPElement elem) throws SOAPException{
		SOAPElement theClaims = elem.addChildElement(elem.createQName("Claims", prefix));
		//theClaims.addNamespaceDeclaration(prefix, URI);
		return theClaims;
	}
	
	/* (non-Javadoc)
	 * @see com.awl.ws.messages.IRST#sendRST()
	 */
	public SOAPMessage sendRST() throws UnsupportedOperationException, SOAPException, IOException{
		 //	String destination = "";
	        //First create the connection
	         SOAPConnectionFactory soapConnFactory = 
	                            SOAPConnectionFactory.newInstance();
	         SOAPConnection connection = 
	                             soapConnFactory.createConnection();

	        writeDebug("RST.xml", createSOAPMessage());
	         
	        //Send the message
		    
		   // m_strURLEndPoint = m_strURLEndPoint.replace("https", "http");
		    System.out.println("sending to  " + m_strURLEndPoint);
		    SOAPMessage initialRST = createSOAPMessage();
		    /*** TRY TO RECONSTRUCT
		     * 
		     * 
		     * 
		     */
		    
		   // String toRet;
		    SOAPMessage reply=null;
			reply = connection.call(initialRST, new URL(m_strURLEndPoint));//"http://localhost:1234/sample/trust/usernamepassword/sts"));
			writeDebug("RSTR.xml", reply);
			

		    return reply;
	}
//	public String sendRSTviaHTTP() throws UnsupportedOperationException, SOAPException, IOException{
//	 	String destination = "";
//        //First create the connection
//         SOAPConnectionFactory soapConnFactory = 
//                            SOAPConnectionFactory.newInstance();
//         SOAPConnection connection = 
//                             soapConnFactory.createConnection();
//
//         writeDebug("RST.xml", createSOAPMessage());
//        
//        //Send the message
//	    
//	   // m_strURLEndPoint = m_strURLEndPoint.replace("https", "http");
//	    System.out.println("sending to  " + m_strURLEndPoint);
//	    SOAPMessage initialRST = createSOAPMessage();
//	    /*** TRY TO RECONSTRUCT
//	     * 
//	     * 
//	     * 
//	     */
//	    
//	    String toRet;
//	    SOAPMessage reply=null;
//		try {
//			//toRet = new String(SOAP2String(initialRST));
//			//SOAPMessage reBuild = MessageFactory.newInstance().createMessage();				
//			//Document doc;
//			//doc = javax.xml.parsers.DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new ByteArrayInputStream(toRet.getBytes()));
//			//reBuild.getSOAPBody().addDocument(doc);
//			//SOAPDoc
//			HTTPTransport transport = new HTTPTransport(m_strURLEndPoint.replace("https", "http"));
//			transport.send(SOAP2String(initialRST));
//			return transport.response;
//			//reply = connection.call(initialRST, new URL(m_strURLEndPoint));
//
//	       
//		} catch (TransformerException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		} catch (SAXException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		} catch (ParserConfigurationException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		} catch (TransportException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
//	
//	    /*
//	     * 
//	     * 
//	     */
//	    
//	    
//	    return null;
//}
	public String SOAP2String(SOAPMessage msg) throws TransformerException, SOAPException, SAXException, IOException, ParserConfigurationException{
		TransformerFactory transformerFactory = 
        TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		//Extract the content of the reply
		Source sourceContent = msg.getSOAPPart().getContent();
		//Set the output for the transformation
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		
		StreamResult result = new StreamResult(bout);
		
		transformer.transform(sourceContent, result);
		
		String toRet = new String(bout.toByteArray());

		return toRet;
	}
	/**
	 * Set the SOAP version (1.1 default).
	 */
	@Override
	public void setSOAPProtocol(String protocol) {
		curSOAPProtocol = protocol;
		
	}
	
}
