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


public class WSCardIdentity extends XMLNameSpaceAbstract{
/*
 * 
 * <wsid:InformationCardReference xmlns:wsid="http://schemas.xmlsoap.org/ws/2005/05/identity">
  <wsid:CardId>contextid:ip-bancaire.atosworldline.bancaire.test.fc2consortium.org:cfid:card_866511604808013108:cardtype:CB</wsid:CardId> 
  <wsid:CardVersion>1</wsid:CardVersion> 
  </wsid:InformationCardReference>
 */
	
	String prefix;
	final static public String URI = "http://schemas.xmlsoap.org/ws/2005/05/identity";
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
	public WSCardIdentity(String prefix) {
		this.prefix = prefix;
	}
	String cardID= "";//"contextid:ip-bancaire.atosworldline.bancaire.test.fc2consortium.org:cfid:card_866511604808013108:cardtype:CB";
	//String cardID = "https://ip-bancaire.atosworldline.bancaire.test.fc2consortium.org/paymentCard_sts/card/3D24D396-5874-B9EE-5F0B-04BBCB05E00B";
	String cardVersion ="1";
	public void setCardId(String str){
		cardID = str;
	}
	
	public SOAPElement createInformationCardReference(SOAPElement elem) throws SOAPException{
		elem.addNamespaceDeclaration(prefix, URI);
		SOAPElement cardInfo = elem.addChildElement(elem.createQName("InformationCardReference", prefix));
		cardInfo.addNamespaceDeclaration(prefix, URI);
		cardInfo.addChildElement(cardInfo.createQName("CardId", prefix)).addTextNode(cardID);
		cardInfo.addChildElement(cardInfo.createQName("CardVersion", prefix)).addTextNode(cardVersion);
		return cardInfo;
	}
	public void addClaims(SOAPElement elem,String uri) throws SOAPException{
		elem.addNamespaceDeclaration(prefix, URI);
		SOAPElement claims = elem.addChildElement(elem.createQName("ClaimType",prefix));
		claims.addNamespaceDeclaration(prefix, URI);
		claims.setAttribute("Uri", uri);
		
	}
	public void addClientPseudonym(SOAPElement elem,String ppid) throws SOAPException{
		SOAPElement pseudo = elem.addChildElement(elem.addChildElement("ClientPseudonym"));
		pseudo.addNamespaceDeclaration("", URI);
		pseudo.addChildElement("PPID").addTextNode(ppid);
		
	}
	public void addRequestDisplayToken(SOAPElement elem) throws SOAPException{
//		/wsid:RequestDisplayToken xml:lang="en" xmlns:wsid="http://schemas.xmlsoap.org/ws/2005/05/identity" />
		elem.addNamespaceDeclaration(prefix, URI);
		SOAPElement displayToken = elem.addChildElement(elem.createQName("RequestDisplayToken", prefix));
		displayToken.addNamespaceDeclaration(prefix, URI);
		displayToken.setAttribute("xml:lang", "en");
		
	}
	
}
