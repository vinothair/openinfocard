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
package com.awl.ws.messages.namespaces;

import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;

import com.awl.ws.messages.utilities.XMLNameSpaceAbstract;



public class WSAddressing extends XMLNameSpaceAbstract {
	String prefix;
	final static public String URI = "http://www.w3.org/2005/08/addressing";
	@Override
	public String getPrefix() {
		// TODO Auto-generated method stub
		return prefix;
	}
	@Override
	public String getURI() {
		// TODO Auto-generated method stub
		return URI;
	} 
	public WSAddressing(String prefix) {
		this.prefix = prefix;
	}
	
	public SOAPElement createAction(SOAPEnvelope elem,String innerText) throws SOAPException{
		
		SOAPElement toAdd = elem.getHeader().addChildElement(elem.createName("Action", prefix, URI));
	    //elem.setAttribute("SOAP-ENV:mustUnderstand", "1");
	    toAdd.addTextNode(innerText);
	    return toAdd;
	}
	public SOAPElement createTo(SOAPEnvelope elem,String innerText) throws SOAPException{
		// <a:To s:mustUnderstand="1">https://ip-bancaire.atosworldline.bancaire.test.fc2consortium.org/BanditIdP/services/Trust</a:To> 
		SOAPElement toAdd = elem.getHeader().addChildElement(elem.createName("To", prefix, URI));
	    //elem.setAttribute("SOAP-ENV:mustUnderstand", "1");
	    toAdd.addTextNode(innerText);
	    return toAdd;

	}
	public SOAPElement createEndPoint(SOAPElement soapBody) throws SOAPException{
		SOAPElement elem = soapBody.addChildElement(soapBody.createQName("EndpointReference", prefix));
		return elem;
	}
	public SOAPElement createAdress(SOAPElement soapBody,String url) throws SOAPException{
		SOAPElement elem = soapBody.addChildElement(soapBody.createQName("Address", prefix));
		//elem.setTextContent(url);//"https://ip-telecom.orange.telecom.test.fc2consortium.org/CustomUserNameCardStsHostFactory/Service.svc");//TextContent(url);
		elem.setValue(url);
		return elem;
		
	}
}
