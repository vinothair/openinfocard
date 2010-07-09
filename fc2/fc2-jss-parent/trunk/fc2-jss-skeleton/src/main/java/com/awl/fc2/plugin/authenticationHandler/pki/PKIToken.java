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
package com.awl.fc2.plugin.authenticationHandler.pki;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;

import org.apache.xml.security.c14n.CanonicalizationException;
import org.apache.xml.security.c14n.Canonicalizer;
import org.apache.xml.security.c14n.InvalidCanonicalizerException;

import com.awl.fc2.selector.authentication.PKIHandler;
import com.awl.fc2.selector.exceptions.PKIHandler_Exeception;
import com.awl.logger.Logger;
import com.awl.ws.messages.authentication.IToken;
import com.awl.ws.messages.namespaces.WSSecurityExtention;
import com.awl.ws.messages.namespaces.WSSecurityUtility;
import com.utils.Base64;
import com.utils.SUtil;

public class PKIToken extends WSSecurityExtention implements IToken{
	static Logger log = new Logger(PKIToken.class);
	public static void trace(Object msg){
		log.trace(msg);
	}
	final static public String TOKEN_TYPE_PWD = "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-username-token-profile-1.0#PasswordText";
	static{
		org.apache.xml.security.Init.init();
	}
	public PKIToken(String prefix) {
		super(prefix);
		
	}
	String certificate;
	
	/*public void setCertificateB64(String certif){
		this.certificate = certif;
	}*/
	PKIHandler pkiHandler = null;
	public void setPKIHandler(PKIHandler pki) throws PKIHandler_Exeception{
		pkiHandler = pki;
		certificate = pkiHandler.getCertB64();
	}
	
	WSSecurityUtility wsu;
	public void setSecurityUtility(WSSecurityUtility wsu){
		this.wsu = wsu;
	}
	public SOAPElement createToken(SOAPElement env ) throws SOAPException{
		SOAPElement security = createSecurity(env);
		//Add Certificate
		//BinarySecurityToken
		SOAPElement soap_certificate =  security.addChildElement(env.createQName("BinarySecurityToken",prefix));
		//ATTRIBUTES 
		//EncodingType="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-soap-message-security-1.0#Base64Binary" 
		//ValueType="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0#X509v3" 
		//wsu:Id="DBC60B86-4FA6-3C42-B88B-3CDF5D625F56">
		String refCertif = "DBC60B86-4FA6-3C42-B88B-3CDF5D625F56";
		soap_certificate.setAttribute("EncodingType", "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-soap-message-security-1.0#Base64Binary");
		soap_certificate.setAttribute("ValueType", "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0#X509v3");
		soap_certificate.setAttribute("wsu:Id", refCertif);
		soap_certificate.addTextNode(certificate);
		
		
		//Adding namespace WSUtility
		env.addNamespaceDeclaration("wsu", "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd");
		
		
		SOAPElement Timestamp = security.addChildElement(env.createQName("Timestamp","wsu"));
		String timestampID = "566840B4-876C-1E46-A644-EE9E29EB57C9";
		Timestamp.setAttribute("wsu:Id", timestampID);
		Timestamp.addNamespaceDeclaration("wsu", "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd");
		//<wsu:Created>2009-10-28T16:42:12Z</wsu:Created>
		//<wsu:Expires>2009-10-28T16:48:12Z</wsu:Expires>
		SOAPElement created = Timestamp.addChildElement(env.createQName("Created","wsu"));
		SOAPElement expires = Timestamp.addChildElement(env.createQName("Expires","wsu"));
		created.addTextNode("2009-10-28T16:42:12Z");
		expires.addTextNode("2009-10-28T16:48:12Z");
		
		
		
		
		//CREATE SIGNATURE STUFFF
		SOAPElement signature = security.addChildElement("Signature");
		signature.addNamespaceDeclaration("", "http://www.w3.org/2000/09/xmldsig#");
		
		SOAPElement signInfo = signature.addChildElement("SignedInfo");
		SOAPElement canoc = signInfo.addChildElement("CanonicalizationMethod");
		canoc.setAttribute("Algorithm", "http://www.w3.org/2001/10/xml-exc-c14n#");
		
		SOAPElement signMeth = signInfo.addChildElement("SignatureMethod");
		signMeth.setAttribute("Algorithm", "http://www.w3.org/2001/10/xml-exc-c14n#");
		
		SOAPElement reference = signInfo.addChildElement("Reference");
		reference.setAttribute("URI", "#" + timestampID );
		
		SOAPElement Transforms = reference.addChildElement("Transforms");
		SOAPElement Transform = Transforms.addChildElement("Transform");
		Transform.setAttribute("Algorithm", "http://www.w3.org/2001/10/xml-exc-c14n#");
		
		SOAPElement DigestMethod = reference.addChildElement("DigestMethod");
		DigestMethod.setAttribute("Algorithm", "http://www.w3.org/2000/09/xmldsig#sha1");
		
		byte [] tobeSigned = getHash(Timestamp, "http://www.w3.org/2001/10/xml-exc-c14n#", "SHA1");
		SOAPElement DigestValue = reference.addChildElement("DigestValue");
		DigestValue.setValue(Base64.encode(tobeSigned));
		
		SOAPElement SignatureValue = security.addChildElement("SignatureValue");		
		try {
			SignatureValue.setValue(Base64.encode(pkiHandler.signData(tobeSigned)));
		} catch (PKIHandler_Exeception e) {
			throw new SOAPException(e.getMessage());
		}
		
		signature.addNamespaceDeclaration("dsig","http://www.w3.org/2000/09/xmldsig#");
		SOAPElement keyInfo = signature.addChildElement("dsig","KeyInfo","http://www.w3.org/2000/09/xmldsig#");
		SOAPElement SecurityTokenReference = keyInfo.addChildElement("SecurityTokenReference", "o");
		SOAPElement Reference  = SecurityTokenReference.addChildElement("Reference", "o");
		Reference.setAttribute("URI", "#"+refCertif);
		
		//DIGEST 
//		XMLSignatureFactory xmlSignatureFactory = XMLSignatureFactory.getInstance("DOM");
//		
//		CanonicalizationMethod canMeth = xmlSignatureFactory.newCanonicalizationMethod("http://www.w3.org/2001/10/xml-exc-c14n#",
//				(C14NMethodParameterSpec)null);
//		
//		canMeth.transform(new DOMSource(Timestamp), null);
	
		
	

		
		
		
	

		//----
		
//		SOAPElement token = security.addChildElement(env.createQName("SSOToken",prefix));//,URI));
//		wsu.addId(token, "urn:uuid:28420");
//		//<o:Username>fjritaine</o:Username> 
//		SOAPElement username =  token.addChildElement(env.createQName("Username",prefix));//,URI));//.addTextNode(userName);
//		username.addTextNode(userName);
//		
//		SOAPElement pwd = token.addChildElement(env.createQName("Password",prefix));//,URI));
//		pwd.setAttribute(prefix+":Type", TOKEN_TYPE_PWD);
//		pwd.addTextNode(SSOToken);
		return security;
	}
	
	public static byte [] getHash(SOAPElement elem,String algorithmCanocalization,String algoHash){
		Canonicalizer c14n;
        try {
        	 

			c14n = Canonicalizer.getInstance(algorithmCanocalization);
			try {
				byte [] dataBytes = c14n.canonicalizeSubtree(elem);
				String con = new String(dataBytes);
				System.out.println(dataBytes.length);
				System.out.println(con);
				MessageDigest hash;
				try {
					hash = MessageDigest.getInstance(algoHash);
					hash.reset();
					 System.out.println(SUtil.bytes2String(dataBytes));
					 
					 
					byte [] res = hash.digest(dataBytes);
					return res;
				} catch (NoSuchAlgorithmException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} catch (CanonicalizationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (InvalidCanonicalizerException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return null;
	}
//	public byte [] sign(byte [] tobeSigned){
//		trace("Signing the request");
//		return TestCardCertificateBEID.getSignatureFROMBEID(new String(tobeSigned));
//	}

}
