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



public class WSPolicy extends XMLNameSpaceAbstract {
	String prefix;
	final static public String URI = "http://schemas.xmlsoap.org/ws/2004/09/policy";
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
	public WSPolicy(String prefix) {
		this.prefix = prefix;
	}
	String urlApplyTo;
	String certificateB64;
	public void setApplyTo(String url,String certif){
		urlApplyTo=url;
		certificateB64 = certif;
	}
	WSAddressing wsa;
	public void setWSAdressing(WSAddressing wsa){
		this.wsa = wsa;
	}
	public SOAPElement createApplyTo(SOAPElement elem) throws SOAPException{
		elem.addNamespaceDeclaration(prefix, URI);
		SOAPElement applyto = elem.addChildElement(elem.createQName("AppliesTo", prefix));
		applyto.addNamespaceDeclaration(prefix, URI);
		SOAPElement endPoint = wsa.createEndPoint(applyto);
		
		wsa.createAdress(endPoint,urlApplyTo);
		WSIdentity identity = new WSIdentity("i");
		identity.createIdentityX509(endPoint, certificateB64);
		
		
		return applyto;
	}
	
	
}
