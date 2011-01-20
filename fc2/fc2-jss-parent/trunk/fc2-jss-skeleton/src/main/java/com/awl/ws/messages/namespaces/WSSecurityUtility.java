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



public class WSSecurityUtility extends XMLNameSpaceAbstract {
	String prefix;
	final static public String URI = "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd";
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
	public WSSecurityUtility(String prefix) {
		this.prefix = prefix;
	}
	
	public void addId(SOAPElement elem,String theId){
		elem.setAttribute(prefix+":Id", theId);
	}
	//
	//<wsu:Created>2009-02-04T14:36:03.515Z</wsu:Created>	
	public void addCreation(SOAPElement elem) throws SOAPException{
		//elem.addChildElement(elem.createQName("Created",prefix)).setTextContent("2009-02-04T14:36:03.515Z");		
	}
	//<wsu:Expires>2009-02-04T14:41:03.515Z</wsu:Expires>
	public void addExpires(SOAPElement elem) throws SOAPException{
		//SOAPElement add= elem.addChildElement(elem.createQName("Expires",prefix));
		//add.setTextContent("2012-02-04T14:36:03.515Z");
	}
	
	
	//<wsu:Timestamp wsu:Id="Timestamp-25511702" xmlns:wsu="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd">
	public void addTimeStamp(SOAPElement elem)throws SOAPException{
		//SOAPElement add = elem.addChildElement(elem.createQName("Timestamp",prefix));
		//add.setAttribute(prefix+":Id", "Timestamp-25511702");
	}
}