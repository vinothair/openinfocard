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
package com.awl.ws.messages;

import java.io.IOException;

import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;

import com.awl.ws.messages.authentication.IToken;

public interface IRequestSecurityToken {

	final static public String URI = "http://schemas.xmlsoap.org/ws/2005/02/trust";
	
	
	final static public String URI_1_3 = "http://docs.oasis-open.org/ws-sx/ws-trust/200512";
	
	final static public String REQUEST_TYPE_ISSUING_1_2 = "http://schemas.xmlsoap.org/ws/2005/02/trust/Issue";
    final static public String REQUEST_TYPE_ISSUING_1_3 = "http://docs.oasis-open.org/ws-sx/ws-trust/200512/Issue";
	final static public String TOKEN_TYPE_SAML10 = "urn:oasis:names:tc:SAML:1.0:assertion";
	final static public String TOKEN_TYPE_SAML11 = "http://docs.oasis-open.org/wss/oasis-wss-saml-token-profile-1.1#SAMLV1.1";
	public abstract void setTokenType(String tt);

	public abstract void setSOAPProtocol(String protocol);
	
	public abstract void setEndPoint(String urlEndPoint);

	public abstract void setRequestor(String url, String certif);

	public abstract void addClaims(String claims);

	public abstract void setCardId(String strCardId);

	public abstract void setAuthenticationHandler(IToken token);

	public abstract void setPPID(String ppid);

	public abstract SOAPMessage sendRST() throws UnsupportedOperationException,
			SOAPException, IOException;

	
}