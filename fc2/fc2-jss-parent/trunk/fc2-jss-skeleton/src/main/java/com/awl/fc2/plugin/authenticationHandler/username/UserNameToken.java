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
package com.awl.fc2.plugin.authenticationHandler.username;

import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;

import com.awl.ws.messages.authentication.IToken;
import com.awl.ws.messages.namespaces.WSSecurityExtention;
import com.awl.ws.messages.namespaces.WSSecurityUtility;

public class UserNameToken extends WSSecurityExtention implements IToken{
	final static public String TOKEN_TYPE_PWD = "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-username-token-profile-1.0#PasswordText";
	
	public UserNameToken(String prefix) {
		super(prefix);
		
	}
	String userName;
	String passWord;
	public void setUserName(String userName){
		this.userName = userName;
	}
	public void setPWD(String pwd){
		passWord = pwd;
	}
	WSSecurityUtility wsu;
	public void setSecurityUtility(WSSecurityUtility wsu){
		this.wsu = wsu;
	}
	public SOAPElement createToken(SOAPElement env ) throws SOAPException{
		SOAPElement security = createSecurity(env);
//		wsu.addTimeStamp(security);
//		wsu.addCreation(security);
//		wsu.addExpires(security);
	
		SOAPElement token = security.addChildElement(env.createQName("UsernameToken",prefix));//,URI));
		
		wsu.addId(token, "urn:uuid:54345546");
		
		//<o:Username>fjritaine</o:Username> 
		SOAPElement username =  token.addChildElement(env.createQName("Username",prefix));//,URI));//.addTextNode(userName);
		username.addTextNode(userName);
		
		SOAPElement pwd = token.addChildElement(env.createQName("Password",prefix));//,URI));
		pwd.setAttribute(prefix+":Type", TOKEN_TYPE_PWD);
		pwd.addTextNode(passWord);
		return security;
	}

}
