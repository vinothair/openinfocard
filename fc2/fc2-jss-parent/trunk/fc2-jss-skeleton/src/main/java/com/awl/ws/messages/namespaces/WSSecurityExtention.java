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
import javax.xml.soap.SOAPException;

import com.awl.ws.messages.utilities.XMLNameSpaceAbstract;



public class WSSecurityExtention extends XMLNameSpaceAbstract {
	protected String prefix;
	final static public String URI = "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd";
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
	public WSSecurityExtention(String prefix) {
		this.prefix = prefix;
	}
	
	public SOAPElement createSecurity(SOAPElement elem) throws SOAPException{
		//SOAPElement toAdd = elem.getHeader().addChildElement(elem.createName("Security", prefix, URI));
	    //elem.setAttribute("SOAP-ENV:mustUnderstand", "1");
	   // toAdd.addTextNode(innerText);
		//SOAPElement Security =  elem.getHeader().addChildElement(elem.createName("Security", prefix, URI));
		elem.addNamespaceDeclaration(prefix, URI);
		SOAPElement Security =  elem.addChildElement(elem.createQName("Security", prefix));
		Security.addNamespaceDeclaration(prefix, URI);
		//toAdd.addChildElement()
	    return Security;
	}
}
