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

import org.xmldap.crypto.CryptoUtils;
import org.xmldap.exceptions.CryptoException;

import com.awl.logger.Logger;
import com.awl.ws.messages.utilities.XMLNameSpaceAbstract;



public class WSIdentity extends XMLNameSpaceAbstract {
	
	String prefix;
	final static public String URI = "http://schemas.xmlsoap.org/ws/2006/02/addressingidentity";
	static Logger log = new com.awl.logger.Logger(WSIdentity.class);
	public static void trace(Object msg){
		log.trace(msg);
	}
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
	public WSIdentity(String prefix) {
		this.prefix = prefix;
	}
	SOAPElement createIdentityX509(SOAPElement elem, String certif) throws SOAPException{
		elem.addNamespaceDeclaration(prefix, URI);
		try {
			CryptoUtils.X509fromB64(certif);
			SOAPElement id = elem.addChildElement(elem.createQName("Identity",prefix));
			id.addNamespaceDeclaration(prefix, URI);
			WSDigitalSignature ds = new WSDigitalSignature("ds");
			ds.createX509Data(ds.createKeyInfo(id),certif);
		} catch (CryptoException e) {
			trace("Certificate data are not well formatted we dont send it in to the RST");
		}
		
		return elem;
	}
	

	
}
