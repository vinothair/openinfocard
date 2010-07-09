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
 *     * The names of the contributors may NOT be used to endorse or promote products
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
 * 
 */
package com.awl.rd.fc2.data.connectors.services.paymentDBBandit;
import java.io.IOException;

import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Nodes;
import nu.xom.ParsingException;
import nu.xom.XPathContext;

public class Entity{
	static public void trace(Object message){
		System.out.println(message);
	}
	String username;
	public String getUsername() {
		return username;
	}
	public String getGivenname() {
		return givenname;
	}
	public String getPwd() {
		return pwd;
	}
	public String getPaymentcardnumber() {
		return paymentcardnumber;
	}
	public String getPaymentcardverification() {
		return paymentcardverification;
	}
	public String getPaymentcardexpdatemonth() {
		return paymentcardexpdatemonth;
	}
	public String getPaymentcardexpdateyear() {
		return paymentcardexpdateyear;
	}
	public String getSurname() {
		return surname;
	}
	String givenname;
	String pwd;
	String paymentcardnumber;
	String paymentcardverification;
	String paymentcardexpdatemonth;
	String paymentcardexpdateyear;
	String surname;
	
	public Entity(String username,
	String givenname,
	String surname,
	String pwd,
	String paymentcardnumber,
	String paymentcardverification,
	String paymentcardexpdatemonth,
	String paymentcardexpdateyear) {

		
		this.username = username;
		this.givenname = givenname;
		this.surname= surname;
		this.pwd = pwd;
		this.paymentcardnumber =paymentcardnumber;
		this.paymentcardverification = paymentcardverification;
		this.paymentcardexpdatemonth = paymentcardexpdatemonth;
		this.paymentcardexpdateyear= paymentcardexpdateyear;
	}
	public Entity(String xml) {
		//trace("Construct new one ");
		Builder parser = new Builder();
		Document mex = null;
		try {
		    mex = parser.build(xml, "");
		} catch (ParsingException e) {
		    //throw new ServletException(e);
			e.printStackTrace();			
		} catch (IOException e) {
		   // throw new ServletException(e);
			e.printStackTrace();
		}
		
		XPathContext context = new XPathContext();
//		context.addNamespace("s", WSConstants.SOAP12_NAMESPACE);
//		context.addNamespace("a", "http://www.w3.org/2005/08/addressing");
//		context.addNamespace("wsp", "http://schemas.xmlsoap.org/ws/2004/09/policy");
//		context.addNamespace("wsa10", "http://www.w3.org/2005/08/addressing" );
		Nodes messageIDs = mex.query("//Attribute");//, context);
		for(int i=0;i<messageIDs.size();i++){
			
			Element messageID = (Element) messageIDs.get(i);
		
			String name = messageID.getAttributeValue("AttrID");
			String value = messageID.getChildElements("AttributeValue").get(0).getValue();//getAttributeValue("value");
			//trace("AttrID = "+name + "["+value+"]");
			configure(name, value);
		}
		//trace("createAccountForUserId(\""+username+"\", \""+username+"\", \"SDD."+username+"\", \""+username+"\");");
	}
	public void configure(String uri,String value){
		if("http://www.eclipse.org/higgins/ontologies/2006/higgins#userName".equalsIgnoreCase(uri)){username=value;}
		if("http://www.eclipse.org/higgins/ontologies/2006/higgins#password".equalsIgnoreCase(uri)){pwd=value;}
		if("http://schemas.xmlsoap.org/ws/2005/05/identity/claims/givenname".equalsIgnoreCase(uri)){givenname=value;}
		if("http://schemas.xmlsoap.org/ws/2005/05/identity/claims/surname".equalsIgnoreCase(uri)){surname=value;}
		if("http://www.fc2consortium.org/ws/2008/10/identity/claims/paymentcardnumber".equalsIgnoreCase(uri)){paymentcardnumber=value;}
		if("http://www.fc2consortium.org/ws/2008/10/identity/claims/paymentcardverification".equalsIgnoreCase(uri)){paymentcardverification=value;}
		if("http://www.fc2consortium.org/ws/2008/10/identity/claims/paymentcardexpdatemonth".equalsIgnoreCase(uri)){paymentcardexpdatemonth=value;}
		if("http://www.fc2consortium.org/ws/2008/10/identity/claims/paymentcardexpdateyear".equalsIgnoreCase(uri)){paymentcardexpdateyear=value;}
	}
	
	
}