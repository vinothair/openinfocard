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
import java.util.Vector;

import javax.xml.soap.SOAPMessage;

import com.awl.fc2.selector.exceptions.FC2Authentication_Exeception_AuthenticationFailed;
import com.awl.logger.Logger;
//import com.awl.rd.fc2.cas.CASResponse;
import com.awl.ws.messages.DisplayTokenElement;
import com.awl.ws.messages.IRequestSecurityTokenResponse;
import com.utils.XMLParser;
import com.utils.execeptions.XMLParser_Exception_NO_ATTRIBUTE;

public class RequestSecurityTokenResponse implements
		    IRequestSecurityTokenResponse {

	static Logger log = new Logger(RequestSecurityTokenResponse.class);
	public void trace(Object msg){
		log.trace(msg);
	}
	final static public int STATUS_FAILED =0;
	final static public int STATUS_SUCCEED=1;
	final static public int STATUS_PROGRESS=2;
	int status= STATUS_PROGRESS;
	String stsSAMLAssertion;
	Vector<DisplayTokenElement> vecDisplayToken;
	@Override
	public Vector<DisplayTokenElement> getDisplayToken() {
		// TODO Auto-generated method stub
		return vecDisplayToken;
	}

	@Override
	public String getSAMLAssertionXML() {
		// TODO Auto-generated method stub
		return stsSAMLAssertion;
	}

//	@Override
//	public void setRSTR(SOAPMessage rstr) throws FC2Authentication_Exeception_AuthenticationFailed {
//		try{
////			//Check the output
////	        System.out.println("\nRESPONSE:\n");
//	        //Create the transformer
//	        TransformerFactory transformerFactory = 
//	                           TransformerFactory.newInstance();
//	        Transformer transformer = 
//	                        transformerFactory.newTransformer();
//	        //Extract the content of the reply
//	        Source sourceContent = rstr.getSOAPPart().getContent();
//	        //Set the output for the transformation
//	        ByteArrayOutputStream bout = new ByteArrayOutputStream();
//	        trace("Attempting to get a String representation");
//	        //
//	       
//	        //
//			String rstr_str ="";
//			try {
////				StreamResult result = new StreamResult(bout);
////		        transformer.transform(sourceContent, result);
//				rstr.writeTo(bout);
//		        rstr_str = new String(bout.toByteArray());
//		        if(rstr_str.endsWith("</soap:Envelop"))
//		        	{
//		        	trace("Fixing the pb of SOAPElement");
//		        	rstr_str = rstr_str +"e>";
//		        	}
//		       
//			} catch (Exception e) {
//				
//				//try {
////					 rstr_str +="e>";
////					 trace(rstr_str);
//					// rstr.writeTo(bout);
////				} catch (IOException e1) {
////					trace("FAILED WRITING IN BOUT");
////				}
//			}
//	        
//	        String resp;
//	        trace("Attempting to get the SAML_ASSERTION");
//			try {
//				resp = XMLParser.getFirstXML(rstr_str, "saml:Assertion","saml","urn:oasis:names:tc:SAML:1.0:assertion");
//				System.out.println(resp);
//			    stsSAMLAssertion =resp;
//			    status = STATUS_SUCCEED;
//			    
//			} catch (XMLParser_Exception_NO_ATTRIBUTE e) {
//				String response =rstr_str;
//				trace("processRSTR " + response);
//				String TMP_FIN = "</xenc:EncryptedData>";
//				int deb = response.indexOf("<xenc:EncryptedData");
//				int fin = response.indexOf(TMP_FIN);
//				stsSAMLAssertion = response.substring(deb, fin + TMP_FIN.length());
//				
//				trace(stsSAMLAssertion);
//				status = STATUS_SUCCEED;
//			}
//			
//			/*{
//				trace("Try to extract the display token");
//				NodeList lst = rstr.getSOAPBody().getElementsByTagName("*");//,"http://schemas.xmlsoap.org/ws/2005/05/identity");
//				vecDisplayToken = new Vector<DisplayTokenElement>();
//				for(int i=0;i<lst.getLength();i++){
//					
//					String name = lst.item(i).getNodeName();
//					if(name.contains("DisplayClaim")){
//						trace("Find one : ");
//						NodeList nodeDT =lst.item(i).getChildNodes();
//						String tag="";
//						String value="";
//						String desc = "";
//						for(int t = 0;t<nodeDT.getLength();t++){
//							String att = nodeDT.item(t).getLocalName();
//							
//							//trace(att + "=" + nodeDT.item(t).getFirstChild().getNodeValue());
//							if(att.equalsIgnoreCase("DisplayTag")){
//								tag = nodeDT.item(t).getFirstChild().getNodeValue();
//							}
//							if(att.equalsIgnoreCase("DisplayValue")){
//								value = nodeDT.item(t).getFirstChild().getNodeValue();
//							}
//							if(att.equalsIgnoreCase("Description")){
//								desc = nodeDT.item(t).getFirstChild().getNodeValue();
//							}							
//							
//						}
//						vecDisplayToken.add(new DisplayTokenElement(tag, desc, value));
//					}
//				}
//
//				
//			}*/
//			XMLParser parser = new XMLParser(rstr_str);
//			try {
//				parser.query("ic:RequestedDisplayToken","ic", "http://schemas.xmlsoap.org/ws/2005/05/identity");
//				while(parser.hasNext()){
//					trace("Find one : " + parser.getNextXML());
//				}
//			} catch (XMLParser_Exception_NO_ATTRIBUTE e) {
//				trace("unable to find the display token");
//			} catch (XMLParser_Exception_NoNextValue e) {
//				trace("unable to find the display token");
//			}
//	       
//
//		} catch (UnsupportedOperationException e) {
//			status = STATUS_FAILED;
//			throw(new FC2Authentication_Exeception_AuthenticationFailed(e.getMessage()));
//		} catch (SOAPException e) {
//			status = STATUS_FAILED;
//			throw(new FC2Authentication_Exeception_AuthenticationFailed(e.getMessage()));
////		} catch (IOException e) {
////			status = STATUS_FAILED;
////			throw(new FC2Authentication_Exeception_AuthenticationFailed(e.getMessage()));
//		} catch (TransformerConfigurationException e) {
//			status = STATUS_FAILED;
//			throw(new FC2Authentication_Exeception_AuthenticationFailed(e.getMessage()));
//		} catch (TransformerException e) {
//			status = STATUS_FAILED;
//			throw(new FC2Authentication_Exeception_AuthenticationFailed(e.getMessage()));
//		}
//		
//
//	}
	boolean bcontainCE = false;
//	CASResponse casResponse = null;
//	public boolean containCustomExchange(){
//		return bcontainCE;
//	}
//	public CASResponse getCE(){
//		return casResponse;
//	}
	
	@Override
	public void setRSTR(SOAPMessage rstr) throws FC2Authentication_Exeception_AuthenticationFailed {
		try{

	        ByteArrayOutputStream bout = new ByteArrayOutputStream();
	        trace("Attempting to get a String representation");
			String rstr_str ="";
			try {
				rstr.writeTo(bout);
		        rstr_str = new String(bout.toByteArray());
		        if(!(rstr_str.endsWith("Envelope>"))){
		        	trace("Fixing the pb of SOAPElement");
		        	if(rstr_str.endsWith("</soap:Envelop"))
		        	{		        		
		        		rstr_str = rstr_str +"e>";
		        	}
		        	if(rstr_str.endsWith("</soap:Envelo"))
		        	{		        		
		        		rstr_str = rstr_str +"pe>";
		        	}
		        	if(rstr_str.endsWith("</soap:Envel"))
		        	{		        		
		        		rstr_str = rstr_str +"ope>";
		        	}
		        	if(rstr_str.endsWith("</soap:Enve"))
		        	{		        		
		        		rstr_str = rstr_str +"lope>";
		        	}
		        	if(rstr_str.endsWith("</soap:Env"))
		        	{		        		
		        		rstr_str = rstr_str +"elope>";
		        	}
		        	if(rstr_str.endsWith("</soap:En"))
		        	{		        		
		        		rstr_str = rstr_str +"velope>";
		        	}
		        	if(rstr_str.endsWith("</soap:E"))
		        	{		        		
		        		rstr_str = rstr_str +"nvelope>";
		        	}
		        	if(rstr_str.endsWith("</soap:E"))
		        	{		        		
		        		rstr_str = rstr_str +"nvelope>";
		        	}
		        	if(rstr_str.endsWith("</soap:"))
		        	{		        		
		        		rstr_str = rstr_str +"Envelope>";
		        	}
		        	if(rstr_str.endsWith("</soap"))
		        	{		        		
		        		rstr_str = rstr_str +":Envelope>";
		        	}
		        }
		      
		       
			} catch (Exception e) {
				
			}
	        
	        String resp;
	        if(rstr_str.contains("CustomExchange")){
	        	trace("Get a CustomExchange");
	        	String xmlCustomExchange ="";
	        	String TMP_FIN = "CustomExchange>";
	        	String TMP_DEB = "<CustomExchange";
 				int deb = rstr_str.indexOf(TMP_DEB);
 				int fin = rstr_str.indexOf(TMP_FIN,deb+TMP_DEB.length());
 				xmlCustomExchange = rstr_str.substring(deb, fin + TMP_FIN.length());
 				trace("CE : " + xmlCustomExchange);
 				String SessionIdentifier;
 				String Frame;
 				String DisplayMessage;
 				XMLParser parser = new XMLParser(xmlCustomExchange);
 				try {
					SessionIdentifier = parser.getFirstValue("SessionIdentifier");
				} catch (XMLParser_Exception_NO_ATTRIBUTE e) {
					SessionIdentifier = "";					
				}
				try {
					Frame = parser.getFirstValue("Frame");
				} catch (XMLParser_Exception_NO_ATTRIBUTE e) {
					Frame = "";					
				}
				try {
					DisplayMessage = parser.getFirstValue("DisplayMessage");
				} catch (XMLParser_Exception_NO_ATTRIBUTE e) {
					DisplayMessage = "";					
				}
//				casResponse = new CASResponse(SessionIdentifier, Frame, DisplayMessage);
				throw new FC2Authentication_Exeception_AuthenticationFailed(this.getClass().getCanonicalName() + "Does not handle Custom Exchange");
 				//bcontainCE = true;
	        }else{
	        	 trace("Attempting to get the SAML_ASSERTION");
	 			try {
	 				resp = XMLParser.getFirstXML(rstr_str, "saml:Assertion","saml","urn:oasis:names:tc:SAML:1.0:assertion");
	 				System.out.println(resp);
	 			    stsSAMLAssertion =resp;
	 			    status = STATUS_SUCCEED;
	 			    
	 			} catch (XMLParser_Exception_NO_ATTRIBUTE e) {
	 				String response =rstr_str;
	 				trace("processRSTR " + response);
	 				String TMP_FIN = "</xenc:EncryptedData>";
	 				int deb = response.indexOf("<xenc:EncryptedData");
	 				int fin = response.indexOf(TMP_FIN);
	 				if(deb==-1){
	 					throw new FC2Authentication_Exeception_AuthenticationFailed("NOT A FINAL RSTR");
	 				}
	 				stsSAMLAssertion = response.substring(deb, fin + TMP_FIN.length());
	 				
	 				trace(stsSAMLAssertion);
	 				status = STATUS_SUCCEED;
	 			}
	 			
	 		
	 			fillDisplayToken(rstr_str);
	        }
	       
	
			
	       

		} catch (UnsupportedOperationException e) {
			status = STATUS_FAILED;
			throw(new FC2Authentication_Exeception_AuthenticationFailed(e.getMessage()));
		}
		

	}
	public String getPart(String xml,String strDeb,String strFin){
		String TMP_FIN = strFin;
		int deb = xml.indexOf(strDeb);
		int fin = xml.indexOf(TMP_FIN);
		if(fin>deb){
			String DT = xml.substring(deb+strDeb.length(), fin);// + TMP_FIN.length());
			return DT;
		}
		return "";
	}
	public void fillDisplayToken(String rstr){
		{
		trace("Try to extract the display token");
		//NodeList lst = rstr.getSOAPBody().getElementsByTagName("*");//,"http://schemas.xmlsoap.org/ws/2005/05/identity");
		vecDisplayToken = new Vector<DisplayTokenElement>();
		String TMP_FIN = "</ic:DisplayToken>";
		int deb = rstr.indexOf("<ic:DisplayToken");
		int fin = rstr.indexOf(TMP_FIN);
		if(fin>deb){
			String DT = rstr.substring(deb, fin + TMP_FIN.length());
			String DC[] = DT.split("<ic:DisplayClaim");
			for(int i=0;i<DC.length;i++){
				//trace(DC[i]);
				String tag=getPart(DC[i],"<ic:DisplayTag>","</ic:DisplayTag>");
				String value=getPart(DC[i],"<ic:DisplayValue>","</ic:DisplayValue>");
				String desc = getPart(DC[i],"<ic:Description>","</ic:Description>");
				if(tag.equalsIgnoreCase("") && 
				   value.equalsIgnoreCase("") &&
				   desc.equalsIgnoreCase(""))
				{
					continue;
				}
				trace("Adding : (tag="+tag+",value="+value+",desc="+desc+")");
				vecDisplayToken.add(new DisplayTokenElement(tag, desc, value));
			}
			
			
		}else{
			trace("NO DT FOUND");
		}
		
		
//		for(int i=0;i<lst.getLength();i++){
//			
//			String name = lst.item(i).getNodeName();
//			if(name.contains("DisplayClaim")){
//				trace("Find one : ");
//				NodeList nodeDT =lst.item(i).getChildNodes();
//				String tag="";
//				String value="";
//				String desc = "";
//				for(int t = 0;t<nodeDT.getLength();t++){
//					String att = nodeDT.item(t).getLocalName();
//					
//					//trace(att + "=" + nodeDT.item(t).getFirstChild().getNodeValue());
//					if(att.equalsIgnoreCase("DisplayTag")){
//						tag = nodeDT.item(t).getFirstChild().getNodeValue();
//					}
//					if(att.equalsIgnoreCase("DisplayValue")){
//						value = nodeDT.item(t).getFirstChild().getNodeValue();
//					}
//					if(att.equalsIgnoreCase("Description")){
//						desc = nodeDT.item(t).getFirstChild().getNodeValue();
//					}							
//					
//				}
//				//vecDisplayToken.add(new DisplayTokenElement(tag, desc, value));
//			}
//		}

		
		}
	}

	@Override
	public int getStatus() {
		return status;
	}
	
	public static void main(String arg[]){
		String RSTR = "<soap:Envelope xmlns:ic=\"http://schemas.xmlsoap.org/ws/2005/05/identity\" xmlns:soap=\"http://www.w3.org/2003/05/soap-envelope\" xmlns:wsa=\"http://www.w3.org/2005/08/addressing\" xmlns:wsse=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd\" xmlns:wst=\"http://schemas.xmlsoap.org/ws/2005/02/trust\" xmlns:wsu=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd\"><soap:Header /><soap:Body><wst:RequestSecurityTokenResponse Context=\"ProcessRequestSecurityToken\"><wst:TokenType>urn:oasis:names:tc:SAML:1.0:assertion</wst:TokenType><wst:RequestType>http://schemas.xmlsoap.org/ws/2005/02/trust/Issue</wst:RequestType><wst:RequestedSecurityToken><saml:Assertion xmlns:saml=\"urn:oasis:names:tc:SAML:1.0:assertion\" MajorVersion=\"1\" MinorVersion=\"1\" AssertionID=\"uuid-3D837FC4-A285-5814-93FA-3D915DF1C5AA\" Issuer=\"https://ip-bancaire.atosworldline.bancaire.test.fc2consortium.orgsts-eid\" IssueInstant=\"2009-10-30T17:51:27Z\"><saml:Conditions NotBefore=\"2009-10-30T17:48:27Z\" NotOnOrAfter=\"2009-10-30T18:01:27Z\"><saml:AudienceRestrictionCondition><saml:Audience>http://ip-bancaire.atosworldline.bancaire.test.fc2consortium.org/sts-eid/tokenservice</saml:Audience></saml:AudienceRestrictionCondition></saml:Conditions><saml:AttributeStatement><saml:Subject><saml:SubjectConfirmation><saml:ConfirmationMethod>urn:oasis:names:tc:SAML:1.0:cm:bearer</saml:ConfirmationMethod><dsig:KeyInfo xmlns:dsig=\"http://www.w3.org/2000/09/xmldsig#\"><dsig:KeyName>Public Key for C=FR, O=fc2consortium, OU=atosworldline, CN=ip-bancaire.atosworldline.bancaire.test.fc2consortium.org</dsig:KeyName><dsig:KeyValue><dsig:RSAKeyValue><dsig:Modulus>AJESrpckw23Y09vIMaOgj2n8fYD6g3nZEZrsoFPoRDgi9OAOLejEWnOlN2NVpuQhngSd4Z5qWr9Pj+ZmTgLD+A6FXdCFYluLzZizVFS+ouKQCsF18KAIFiTGPbqIk0XCYuLj9QANpJxHrW0EPReCHt6idhQJAiqKxICjrRLHMEwF</dsig:Modulus><dsig:Exponent>AQAB</dsig:Exponent></dsig:RSAKeyValue></dsig:KeyValue><dsig:X509Data><dsig:X509Certificate>MIIFlDCCBHygAwIBAgIIBDKvM4Sz7lgwDQYJKoZIhvcNAQEFBQAwXDEiMCAGA1UEAwwZRkMyIHN1YkFDIGJhbmNhaXJlIFNlcnZlcjENMAsGA1UECwwEdGVzdDEaMBgGA1UECgwRZmMyY29uc29ydGl1bS5vcmcxCzAJBgNVBAYTAkZSMB4XDTA5MDQxNTE2MDkxNVoXDTEwMTEwNjE2MDkxNVowgYExQjBABgNVBAMMOWlwLWJhbmNhaXJlLmF0b3N3b3JsZGxpbmUuYmFuY2FpcmUudGVzdC5mYzJjb25zb3J0aXVtLm9yZzEWMBQGA1UECwwNYXRvc3dvcmxkbGluZTEWMBQGA1UECgwNZmMyY29uc29ydGl1bTELMAkGA1UEBhMCRlIwgZ8wDQYJKoZIhvcNAQEBBQADgY0AMIGJAoGBAJESrpckw23Y09vIMaOgj2n8fYD6g3nZEZrsoFPoRDgi9OAOLejEWnOlN2NVpuQhngSd4Z5qWr9Pj+ZmTgLD+A6FXdCFYluLzZizVFS+ouKQCsF18KAIFiTGPbqIk0XCYuLj9QANpJxHrW0EPReCHt6idhQJAiqKxICjrRLHMEwFAgMBAAGjggK2MIICsjCBuAYIKwYBBQUHAQEEgaswgagwTQYIKwYBBQUHMAKGQS9ob21lL2ZjMi9jZXJ0aWZpY2F0cy9mYzIvc3ViQUMvRkMyc3ViQUNiYW5jYWlyZVNlcnZlci5jYWNlcnQucGVtMFcGCCsGAQUFBzABhktodHRwOi8vYWMuZHMuY29tbXVuLnRlc3QuZmMyY29uc29ydGl1bS5vcmc6ODA4MC9lamJjYS9wdWJsaWN3ZWIvc3RhdHVzL29jc3AwHQYDVR0OBBYEFJa9z75H3NbtSQ6GsEZnMwX1hVaQMAwGA1UdEwEB/wQCMAAwHwYDVR0jBBgwFoAUFtXPRIyFK+IdduGKU7vgqxmxExAwggEcBgNVHR8EggETMIIBDzCCAQuggaaggaOGgaBodHRwOi8vYWMuZHMuY29tbXVuLnRlc3QuZmMyY29uc29ydGl1bS5vcmc6ODA4MC9lamJjYS9wdWJsaWN3ZWIvd2ViZGlzdC9jZXJ0ZGlzdD9jbWQ9Y3JsJmlzc3Vlcj1DTj1GQzIgc3ViQUMgYmFuY2FpcmUgU2VydmVyLCBPPWZjMmNvbnNvcnRpdW0ub3JnLCBPVT10ZXN0LCBDPUZSomCkXjBcMSIwIAYDVQQDDBlGQzIgc3ViQUMgYmFuY2FpcmUgU2VydmVyMRowGAYDVQQKDBFmYzJjb25zb3J0aXVtLm9yZzENMAsGA1UECwwEdGVzdDELMAkGA1UEBhMCRlIwDgYDVR0PAQH/BAQDAgTwMDEGA1UdJQQqMCgGCCsGAQUFBwMBBggrBgEFBQcDAwYIKwYBBQUHAwQGCCsGAQUFBwMIMEQGA1UdEQQ9MDuCOWlwLWJhbmNhaXJlLmF0b3N3b3JsZGxpbmUuYmFuY2FpcmUudGVzdC5mYzJjb25zb3J0aXVtLm9yZzANBgkqhkiG9w0BAQUFAAOCAQEATinw72pV2rwwrPsyOE8s/0Tf3d79Y7+CUfJLG1TfpEboA60YJ5mrz9C0iu+MxgIBke2NEybdvo9ap2GhZF9tjj0Wc+z8ZAE04snvCMhyQHYvkqoQdxoGiwnDTBYLxBa5HQjQju7dlKOFk9X0H3N+K92Ndbs3ybc+AOUvx5wYrLiGpuv5aien9aVM23oAW76E5xh3BsIfp7U+J7R7+I4bijC+ZPly8sBlawx/ubvYA+4LkDr7mdZwlRrCzRCu/k0DJPqvgU/tINN/Q/ZqLtCU8wAT5uMQ78eYxRQjZL6nFdzahEegFjGtThKt+JFkEJmNEYN665SybfS1OMhjNVIawA==</dsig:X509Certificate></dsig:X509Data></dsig:KeyInfo></saml:SubjectConfirmation></saml:Subject><saml:Attribute AttributeName=\"privatepersonalidentifier\" AttributeNamespace=\"http://schemas.xmlsoap.org/ws/2005/05/identity/claims\"><saml:AttributeValue>kt//TXrG5ZjfCrQsOXWQjk05t1g=</saml:AttributeValue></saml:Attribute><saml:Attribute AttributeName=\"dateofbirth\" AttributeNamespace=\"http://schemas.xmlsoap.org/ws/2005/05/identity/claims/dateofbirth\"><saml:AttributeValue>21/01/1971</saml:AttributeValue></saml:Attribute><saml:Attribute AttributeName=\"civility\" AttributeNamespace=\"http://www.fc2consortium.org/ws/2008/10/identity/claims/civility\"><saml:AttributeValue>M.</saml:AttributeValue></saml:Attribute><saml:Attribute AttributeName=\"cnienumber\" AttributeNamespace=\"http://www.fc2consortium.org/ws/2008/10/identity/claims/cnienumber\"><saml:AttributeValue>IDBEL000000332</saml:AttributeValue></saml:Attribute><saml:Attribute AttributeName=\"givenname\" AttributeNamespace=\"http://schemas.xmlsoap.org/ws/2005/05/identity/claims/givenname\"><saml:AttributeValue>Vandenbergh</saml:AttributeValue></saml:Attribute><saml:Attribute AttributeName=\"country\" AttributeNamespace=\"http://schemas.xmlsoap.org/ws/2005/05/identity/claims/country\"><saml:AttributeValue>BEL</saml:AttributeValue></saml:Attribute><saml:Attribute AttributeName=\"streetaddress\" AttributeNamespace=\"http://schemas.xmlsoap.org/ws/2005/05/identity/claims/streetaddress\"><saml:AttributeValue>42 Avenue Théodore Schwann</saml:AttributeValue></saml:Attribute><saml:Attribute AttributeName=\"locality\" AttributeNamespace=\"http://schemas.xmlsoap.org/ws/2005/05/identity/claims/locality\"><saml:AttributeValue>BE</saml:AttributeValue></saml:Attribute><saml:Attribute AttributeName=\"surname\" AttributeNamespace=\"http://schemas.xmlsoap.org/ws/2005/05/identity/claims/surname\"><saml:AttributeValue>Robert</saml:AttributeValue></saml:Attribute><saml:Attribute AttributeName=\"postalcode\" AttributeNamespace=\"http://schemas.xmlsoap.org/ws/2005/05/identity/claims/postalcode\"><saml:AttributeValue>1348</saml:AttributeValue></saml:Attribute></saml:AttributeStatement><dsig:Signature xmlns:dsig=\"http://www.w3.org/2000/09/xmldsig#\"><dsig:SignedInfo><dsig:CanonicalizationMethod Algorithm=\"http://www.w3.org/2001/10/xml-exc-c14n#\" /><dsig:SignatureMethod Algorithm=\"http://www.w3.org/2000/09/xmldsig#rsa-sha1\" /><dsig:Reference URI=\"#uuid-3D837FC4-A285-5814-93FA-3D915DF1C5AA\"><dsig:Transforms><dsig:Transform Algorithm=\"http://www.w3.org/2000/09/xmldsig#enveloped-signature\" /><dsig:Transform Algorithm=\"http://www.w3.org/2001/10/xml-exc-c14n#\"><ec:InclusiveNamespaces xmlns:ec=\"http://www.w3.org/2001/10/xml-exc-c14n#\" PrefixList=\"saml\" /></dsig:Transform></dsig:Transforms><dsig:DigestMethod Algorithm=\"http://www.w3.org/2000/09/xmldsig#sha1\" /><dsig:DigestValue>S1/3VKP/7yZ3Uy90FaA0dT31kck=</dsig:DigestValue></dsig:Reference></dsig:SignedInfo><dsig:SignatureValue>jFNH8t1ucJfXZtZuT6o6gAQGxRjSASkWVqiGs9EzRw9nV7YIefS7guqDCr5iAyyGBij+NNwKAp08a70ww5dnGlnyQqRFCU77nexTz4RKSF/fkVlnDaRWCKmev+3rG+jECpmtW+qCIWU0kfhVdiY0KwYNLXesDB2WFad8B/D+yKs=</dsig:SignatureValue><dsig:KeyInfo><dsig:KeyName>Public Key for C=FR, O=fc2consortium, OU=atosworldline, CN=ip-bancaire.atosworldline.bancaire.test.fc2consortium.org</dsig:KeyName><dsig:KeyValue><dsig:RSAKeyValue><dsig:Modulus>AJESrpckw23Y09vIMaOgj2n8fYD6g3nZEZrsoFPoRDgi9OAOLejEWnOlN2NVpuQhngSd4Z5qWr9Pj+ZmTgLD+A6FXdCFYluLzZizVFS+ouKQCsF18KAIFiTGPbqIk0XCYuLj9QANpJxHrW0EPReCHt6idhQJAiqKxICjrRLHMEwF</dsig:Modulus><dsig:Exponent>AQAB</dsig:Exponent></dsig:RSAKeyValue></dsig:KeyValue><dsig:X509Data><dsig:X509Certificate>MIIFlDCCBHygAwIBAgIIBDKvM4Sz7lgwDQYJKoZIhvcNAQEFBQAwXDEiMCAGA1UEAwwZRkMyIHN1YkFDIGJhbmNhaXJlIFNlcnZlcjENMAsGA1UECwwEdGVzdDEaMBgGA1UECgwRZmMyY29uc29ydGl1bS5vcmcxCzAJBgNVBAYTAkZSMB4XDTA5MDQxNTE2MDkxNVoXDTEwMTEwNjE2MDkxNVowgYExQjBABgNVBAMMOWlwLWJhbmNhaXJlLmF0b3N3b3JsZGxpbmUuYmFuY2FpcmUudGVzdC5mYzJjb25zb3J0aXVtLm9yZzEWMBQGA1UECwwNYXRvc3dvcmxkbGluZTEWMBQGA1UECgwNZmMyY29uc29ydGl1bTELMAkGA1UEBhMCRlIwgZ8wDQYJKoZIhvcNAQEBBQADgY0AMIGJAoGBAJESrpckw23Y09vIMaOgj2n8fYD6g3nZEZrsoFPoRDgi9OAOLejEWnOlN2NVpuQhngSd4Z5qWr9Pj+ZmTgLD+A6FXdCFYluLzZizVFS+ouKQCsF18KAIFiTGPbqIk0XCYuLj9QANpJxHrW0EPReCHt6idhQJAiqKxICjrRLHMEwFAgMBAAGjggK2MIICsjCBuAYIKwYBBQUHAQEEgaswgagwTQYIKwYBBQUHMAKGQS9ob21lL2ZjMi9jZXJ0aWZpY2F0cy9mYzIvc3ViQUMvRkMyc3ViQUNiYW5jYWlyZVNlcnZlci5jYWNlcnQucGVtMFcGCCsGAQUFBzABhktodHRwOi8vYWMuZHMuY29tbXVuLnRlc3QuZmMyY29uc29ydGl1bS5vcmc6ODA4MC9lamJjYS9wdWJsaWN3ZWIvc3RhdHVzL29jc3AwHQYDVR0OBBYEFJa9z75H3NbtSQ6GsEZnMwX1hVaQMAwGA1UdEwEB/wQCMAAwHwYDVR0jBBgwFoAUFtXPRIyFK+IdduGKU7vgqxmxExAwggEcBgNVHR8EggETMIIBDzCCAQuggaaggaOGgaBodHRwOi8vYWMuZHMuY29tbXVuLnRlc3QuZmMyY29uc29ydGl1bS5vcmc6ODA4MC9lamJjYS9wdWJsaWN3ZWIvd2ViZGlzdC9jZXJ0ZGlzdD9jbWQ9Y3JsJmlzc3Vlcj1DTj1GQzIgc3ViQUMgYmFuY2FpcmUgU2VydmVyLCBPPWZjMmNvbnNvcnRpdW0ub3JnLCBPVT10ZXN0LCBDPUZSomCkXjBcMSIwIAYDVQQDDBlGQzIgc3ViQUMgYmFuY2FpcmUgU2VydmVyMRowGAYDVQQKDBFmYzJjb25zb3J0aXVtLm9yZzENMAsGA1UECwwEdGVzdDELMAkGA1UEBhMCRlIwDgYDVR0PAQH/BAQDAgTwMDEGA1UdJQQqMCgGCCsGAQUFBwMBBggrBgEFBQcDAwYIKwYBBQUHAwQGCCsGAQUFBwMIMEQGA1UdEQQ9MDuCOWlwLWJhbmNhaXJlLmF0b3N3b3JsZGxpbmUuYmFuY2FpcmUudGVzdC5mYzJjb25zb3J0aXVtLm9yZzANBgkqhkiG9w0BAQUFAAOCAQEATinw72pV2rwwrPsyOE8s/0Tf3d79Y7+CUfJLG1TfpEboA60YJ5mrz9C0iu+MxgIBke2NEybdvo9ap2GhZF9tjj0Wc+z8ZAE04snvCMhyQHYvkqoQdxoGiwnDTBYLxBa5HQjQju7dlKOFk9X0H3N+K92Ndbs3ybc+AOUvx5wYrLiGpuv5aien9aVM23oAW76E5xh3BsIfp7U+J7R7+I4bijC+ZPly8sBlawx/ubvYA+4LkDr7mdZwlRrCzRCu/k0DJPqvgU/tINN/Q/ZqLtCU8wAT5uMQ78eYxRQjZL6nFdzahEegFjGtThKt+JFkEJmNEYN665SybfS1OMhjNVIawA==</dsig:X509Certificate></dsig:X509Data></dsig:KeyInfo></dsig:Signature></saml:Assertion></wst:RequestedSecurityToken><wst:RequestedAttachedReference><wsse:SecurityTokenReference><wsse:KeyIdentifier ValueType=\"http://docs.oasis-open.org/wss/oasis-wss-saml-token-profile-1.0#SAMLAssertionID\">uuid-3D837FC4-A285-5814-93FA-3D915DF1C5AA</wsse:KeyIdentifier></wsse:SecurityTokenReference></wst:RequestedAttachedReference><wst:RequestedUnattachedReference><wsse:SecurityTokenReference><wsse:KeyIdentifier ValueType=\"http://docs.oasis-open.org/wss/oasis-wss-saml-token-profile-1.0#SAMLAssertionID\">uuid-3D837FC4-A285-5814-93FA-3D915DF1C5AA</wsse:KeyIdentifier></wsse:SecurityTokenReference></wst:RequestedUnattachedReference><ic:RequestedDisplayToken><ic:DisplayToken xml:lang=\"en\"><ic:DisplayClaim Uri=\"http://www.fc2consortium.org/ws/2008/10/identity/claims/cnienumber\"><ic:DisplayTag>cnienumber</ic:DisplayTag><ic:DisplayValue>IDBEL000000332</ic:DisplayValue></ic:DisplayClaim><ic:DisplayClaim Uri=\"http://schemas.xmlsoap.org/ws/2005/05/identity/claims/locality\"><ic:DisplayTag>locality</ic:DisplayTag><ic:DisplayValue>BE</ic:DisplayValue></ic:DisplayClaim><ic:DisplayClaim Uri=\"http://schemas.xmlsoap.org/ws/2005/05/identity/claims/dateofbirth\"><ic:DisplayTag>dateofbirth</ic:DisplayTag><ic:DisplayValue>21/01/1971</ic:DisplayValue></ic:DisplayClaim><ic:DisplayClaim Uri=\"http://www.fc2consortium.org/ws/2008/10/identity/claims/civility\"><ic:DisplayTag>civility</ic:DisplayTag><ic:DisplayValue>M.</ic:DisplayValue></ic:DisplayClaim><ic:DisplayClaim Uri=\"http://schemas.xmlsoap.org/ws/2005/05/identity/claims/givenname\"><ic:DisplayTag>surname</ic:DisplayTag><ic:DisplayValue>Vandenbergh</ic:DisplayValue></ic:DisplayClaim><ic:DisplayClaim Uri=\"http://schemas.xmlsoap.org/ws/2005/05/identity/claims/country\"><ic:DisplayTag>country</ic:DisplayTag><ic:DisplayValue>BEL</ic:DisplayValue></ic:DisplayClaim><ic:DisplayClaim Uri=\"http://schemas.xmlsoap.org/ws/2005/05/identity/claims/streetaddress\"><ic:DisplayTag>streetAdress</ic:DisplayTag><ic:DisplayValue>42 Avenue Théodore Schwann</ic:DisplayValue></ic:DisplayClaim><ic:DisplayClaim Uri=\"http://schemas.xmlsoap.org/ws/2005/05/identity/claims/surname\"><ic:DisplayTag>surname</ic:DisplayTag><ic:DisplayValue>Robert</ic:DisplayValue></ic:DisplayClaim><ic:DisplayClaim Uri=\"http://schemas.xmlsoap.org/ws/2005/05/identity/claims/postalcode\"><ic:DisplayTag>postalCode</ic:DisplayTag><ic:DisplayValue>1348</ic:DisplayValue></ic:DisplayClaim><ic:DisplayClaim Uri=\"http://schemas.xmlsoap.org/ws/2005/05/identity/claims\"><ic:DisplayTag>PPID</ic:DisplayTag><ic:DisplayValue>EukpPsazDH+ooJJq9CgH6SnBaE8=</ic:DisplayValue></ic:DisplayClaim></ic:DisplayToken></ic:RequestedDisplayToken></wst:RequestSecurityTokenResponse></soap:Body></soap:Envelop";
		new RequestSecurityTokenResponse().fillDisplayToken(RSTR);
	}

}
