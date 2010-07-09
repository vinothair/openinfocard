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
package com.awl.rd.fc2.data.connectors.services.wallet;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import nu.xom.Element;
import nu.xom.ParsingException;
import nu.xom.ValidityException;

import org.apache.log4j.Logger;
import org.xmldap.infocard.SignedInfoCard;
import org.xmldap.util.XmlFileUtil;

import com.awl.rd.fc2.claims.CardsSupportedClaims;
import com.awl.rd.fc2.data.connectors.services.IServices;
import com.awl.rd.fc2.data.connectors.services.ServiceUtils;
import com.utils.Base64;
import com.utils.XMLParser;
import com.utils.execeptions.XMLParser_Exception_NO_ATTRIBUTE;
import com.utils.execeptions.XMLParser_Exception_NoNextValue;


public class WalletService extends ServiceUtils implements IServices {

	static Logger log = Logger.getLogger(WalletService.class);
	public static void trace(Object msg){
		log.info(msg);
	}
	/**
	 * <QUERY>
	 * <CLAIM>
	 * <URI></URI>
	 * <DYNAMIC></DYNAMIC>
	 * </CLAIM>
	 * </QUERY>
	 * 
	 * <RESPONSE>
	 * <CLAIM>
	 * <URI></URI>
	 * <VALUE></VALUE>
	 * </CLAIM>
	 * </RESPONSE>
	 * 
	 * @param query
	 * @return
	 */
	@Override
	public String execute(String query) {
		trace("execute("+query+")");
		XMLParser parser = new XMLParser(query);
		String response = "<RESPONSE>";
		try {
			parser.query("CLAIM");
			while(parser.hasNext()){
				XMLParser claim =  new XMLParser(parser.getNextXML());
				String uri = claim.getFirstValue("URI");
				String dynRequest = "";
				try{
					dynRequest = claim.getFirstValue("DYNAMIC");
				}catch (XMLParser_Exception_NO_ATTRIBUTE e) {
					// NO dynamic queyr for this claim
					trace("No dynamic part");
				}
				String value = getValue(uri, dynRequest);
				response +="<CLAIM><URI>"+uri+"</URI><VALUE>"+value+"</VALUE></CLAIM>";
			}
		} catch (XMLParser_Exception_NO_ATTRIBUTE e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (XMLParser_Exception_NoNextValue e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return response +"</RESPONSE>";
	}

	/*
	 * <SERVICE>
	 * <USERID>
	 * <SVC>ID_SERVICE</SVC>
	 * <STS>ID_STS</STS>
	 * <CARD>CARDID</CARD>
	 * </USERID>
	 * ... SERVICE SPECIFIQUE ...
	 * </SERVICE>
	 */
	@Override
	public void initService(String xmlParameters) {
		
		constructCommonParameters(xmlParameters);
		trace("Initialization with " + svcUserID);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	static private final String prefix = "http://www.fc2consortium.org/ws/2008/10/identity/claims/";
	public String getValue(String uri,String dynQuery){
//		String aCRD = "<dsig:Signature xmlns:dsig=\"http://www.w3.org/2000/09/xmldsig#\"><dsig:SignedInfo><dsig:CanonicalizationMethod Algorithm=\"http://www.w3.org/2001/10/xml-exc-c14n#\" /><dsig:SignatureMethod Algorithm=\"http://www.w3.org/2000/09/xmldsig#rsa-sha1\" /><dsig:Reference URI=\"#_Object_InfoCard\"><dsig:Transforms><dsig:Transform Algorithm=\"http://www.w3.org/2001/10/xml-exc-c14n#\" /></dsig:Transforms><dsig:DigestMethod Algorithm=\"http://www.w3.org/2000/09/xmldsig#sha1\" /><dsig:DigestValue>JvFaPtE/tdIaMtDDyy2G5ol5YdA=</dsig:DigestValue></dsig:Reference></dsig:SignedInfo><dsig:SignatureValue>b7eek1EOvVLB3YhZ7cTs06oGgaDVqJdI3pKba+uv39m0L4MXeOgT0lGPYJDzOCnOhidc1thW7HKA668wcA9O3iWCB0Rlfiwdse0sLUoMj7tqf30U6SsCVAAhzAwdDWCczxHdAgbk1lnAqC4TmMC1ctygBUJuwi+4mBouShI7BVY=</dsig:SignatureValue><dsig:KeyInfo><dsig:X509Data><dsig:X509Certificate>MIIFlDCCBHygAwIBAgIIBDKvM4Sz7lgwDQYJKoZIhvcNAQEFBQAwXDEiMCAGA1UEAwwZRkMyIHN1YkFDIGJhbmNhaXJlIFNlcnZlcjENMAsGA1UECwwEdGVzdDEaMBgGA1UECgwRZmMyY29uc29ydGl1bS5vcmcxCzAJBgNVBAYTAkZSMB4XDTA5MDQxNTE2MDkxNVoXDTEwMTEwNjE2MDkxNVowgYExQjBABgNVBAMMOWlwLWJhbmNhaXJlLmF0b3N3b3JsZGxpbmUuYmFuY2FpcmUudGVzdC5mYzJjb25zb3J0aXVtLm9yZzEWMBQGA1UECwwNYXRvc3dvcmxkbGluZTEWMBQGA1UECgwNZmMyY29uc29ydGl1bTELMAkGA1UEBhMCRlIwgZ8wDQYJKoZIhvcNAQEBBQADgY0AMIGJAoGBAJESrpckw23Y09vIMaOgj2n8fYD6g3nZEZrsoFPoRDgi9OAOLejEWnOlN2NVpuQhngSd4Z5qWr9Pj+ZmTgLD+A6FXdCFYluLzZizVFS+ouKQCsF18KAIFiTGPbqIk0XCYuLj9QANpJxHrW0EPReCHt6idhQJAiqKxICjrRLHMEwFAgMBAAGjggK2MIICsjCBuAYIKwYBBQUHAQEEgaswgagwTQYIKwYBBQUHMAKGQS9ob21lL2ZjMi9jZXJ0aWZpY2F0cy9mYzIvc3ViQUMvRkMyc3ViQUNiYW5jYWlyZVNlcnZlci5jYWNlcnQucGVtMFcGCCsGAQUFBzABhktodHRwOi8vYWMuZHMuY29tbXVuLnRlc3QuZmMyY29uc29ydGl1bS5vcmc6ODA4MC9lamJjYS9wdWJsaWN3ZWIvc3RhdHVzL29jc3AwHQYDVR0OBBYEFJa9z75H3NbtSQ6GsEZnMwX1hVaQMAwGA1UdEwEB/wQCMAAwHwYDVR0jBBgwFoAUFtXPRIyFK+IdduGKU7vgqxmxExAwggEcBgNVHR8EggETMIIBDzCCAQuggaaggaOGgaBodHRwOi8vYWMuZHMuY29tbXVuLnRlc3QuZmMyY29uc29ydGl1bS5vcmc6ODA4MC9lamJjYS9wdWJsaWN3ZWIvd2ViZGlzdC9jZXJ0ZGlzdD9jbWQ9Y3JsJmlzc3Vlcj1DTj1GQzIgc3ViQUMgYmFuY2FpcmUgU2VydmVyLCBPPWZjMmNvbnNvcnRpdW0ub3JnLCBPVT10ZXN0LCBDPUZSomCkXjBcMSIwIAYDVQQDDBlGQzIgc3ViQUMgYmFuY2FpcmUgU2VydmVyMRowGAYDVQQKDBFmYzJjb25zb3J0aXVtLm9yZzENMAsGA1UECwwEdGVzdDELMAkGA1UEBhMCRlIwDgYDVR0PAQH/BAQDAgTwMDEGA1UdJQQqMCgGCCsGAQUFBwMBBggrBgEFBQcDAwYIKwYBBQUHAwQGCCsGAQUFBwMIMEQGA1UdEQQ9MDuCOWlwLWJhbmNhaXJlLmF0b3N3b3JsZGxpbmUuYmFuY2FpcmUudGVzdC5mYzJjb25zb3J0aXVtLm9yZzANBgkqhkiG9w0BAQUFAAOCAQEATinw72pV2rwwrPsyOE8s/0Tf3d79Y7+CUfJLG1TfpEboA60YJ5mrz9C0iu+MxgIBke2NEybdvo9ap2GhZF9tjj0Wc+z8ZAE04snvCMhyQHYvkqoQdxoGiwnDTBYLxBa5HQjQju7dlKOFk9X0H3N+K92Ndbs3ybc+AOUvx5wYrLiGpuv5aien9aVM23oAW76E5xh3BsIfp7U+J7R7+I4bijC+ZPly8sBlawx/ubvYA+4LkDr7mdZwlRrCzRCu/k0DJPqvgU/tINN/Q/ZqLtCU8wAT5uMQ78eYxRQjZL6nFdzahEegFjGtThKt+JFkEJmNEYN665SybfS1OMhjNVIawA==</dsig:X509Certificate></dsig:X509Data></dsig:KeyInfo><dsig:Object Id=\"_Object_InfoCard\"><ic:InformationCard xmlns:ds=\"http://www.w3.org/2000/09/xmldsig#\" xmlns:ic=\"http://schemas.xmlsoap.org/ws/2005/05/identity\" xmlns:mex=\"http://schemas.xmlsoap.org/ws/2004/09/mex\" xmlns:wsa=\"http://www.w3.org/2005/08/addressing\" xmlns:wsid=\"http://schemas.xmlsoap.org/ws/2006/02/addressingidentity\" xmlns:wst=\"http://schemas.xmlsoap.org/ws/2005/02/trust\" xml:lang=\"en-us\"><ic:InformationCardReference><ic:CardId>https://ip-bancaire.atosworldline.bancaire.test.fc2consortium.org/eIDCard_sts/card/A1BA2F5D-0CC2-6B9C-E693-07B4D3430205</ic:CardId><ic:CardVersion>1</ic:CardVersion></ic:InformationCardReference><ic:CardName>eID5</ic:CardName><ic:Issuer>https://ip-bancaire.atosworldline.bancaire.test.fc2consortium.org/eIDCard_sts/tokenservice</ic:Issuer><ic:TimeIssued>2009-07-15T12:55:20Z</ic:TimeIssued><ic:TokenServiceList><ic:TokenService><wsa:EndpointReference><wsa:Address>https://ip-bancaire.atosworldline.bancaire.test.fc2consortium.org/eIDCard_sts/tokenservice</wsa:Address><wsa:Metadata><mex:Metadata><mex:MetadataSection><mex:MetadataReference><wsa:Address>https://ip-bancaire.atosworldline.bancaire.test.fc2consortium.org/eIDCard_sts/mex/UserNamePasswordAuthenticate</wsa:Address></mex:MetadataReference></mex:MetadataSection></mex:Metadata></wsa:Metadata><wsid:Identity><ds:KeyInfo><ds:X509Data><ds:X509Certificate>MIIFlDCCBHygAwIBAgIIBDKvM4Sz7lgwDQYJKoZIhvcNAQEFBQAwXDEiMCAGA1UEAwwZRkMyIHN1YkFDIGJhbmNhaXJlIFNlcnZlcjENMAsGA1UECwwEdGVzdDEaMBgGA1UECgwRZmMyY29uc29ydGl1bS5vcmcxCzAJBgNVBAYTAkZSMB4XDTA5MDQxNTE2MDkxNVoXDTEwMTEwNjE2MDkxNVowgYExQjBABgNVBAMMOWlwLWJhbmNhaXJlLmF0b3N3b3JsZGxpbmUuYmFuY2FpcmUudGVzdC5mYzJjb25zb3J0aXVtLm9yZzEWMBQGA1UECwwNYXRvc3dvcmxkbGluZTEWMBQGA1UECgwNZmMyY29uc29ydGl1bTELMAkGA1UEBhMCRlIwgZ8wDQYJKoZIhvcNAQEBBQADgY0AMIGJAoGBAJESrpckw23Y09vIMaOgj2n8fYD6g3nZEZrsoFPoRDgi9OAOLejEWnOlN2NVpuQhngSd4Z5qWr9Pj+ZmTgLD+A6FXdCFYluLzZizVFS+ouKQCsF18KAIFiTGPbqIk0XCYuLj9QANpJxHrW0EPReCHt6idhQJAiqKxICjrRLHMEwFAgMBAAGjggK2MIICsjCBuAYIKwYBBQUHAQEEgaswgagwTQYIKwYBBQUHMAKGQS9ob21lL2ZjMi9jZXJ0aWZpY2F0cy9mYzIvc3ViQUMvRkMyc3ViQUNiYW5jYWlyZVNlcnZlci5jYWNlcnQucGVtMFcGCCsGAQUFBzABhktodHRwOi8vYWMuZHMuY29tbXVuLnRlc3QuZmMyY29uc29ydGl1bS5vcmc6ODA4MC9lamJjYS9wdWJsaWN3ZWIvc3RhdHVzL29jc3AwHQYDVR0OBBYEFJa9z75H3NbtSQ6GsEZnMwX1hVaQMAwGA1UdEwEB/wQCMAAwHwYDVR0jBBgwFoAUFtXPRIyFK+IdduGKU7vgqxmxExAwggEcBgNVHR8EggETMIIBDzCCAQuggaaggaOGgaBodHRwOi8vYWMuZHMuY29tbXVuLnRlc3QuZmMyY29uc29ydGl1bS5vcmc6ODA4MC9lamJjYS9wdWJsaWN3ZWIvd2ViZGlzdC9jZXJ0ZGlzdD9jbWQ9Y3JsJmlzc3Vlcj1DTj1GQzIgc3ViQUMgYmFuY2FpcmUgU2VydmVyLCBPPWZjMmNvbnNvcnRpdW0ub3JnLCBPVT10ZXN0LCBDPUZSomCkXjBcMSIwIAYDVQQDDBlGQzIgc3ViQUMgYmFuY2FpcmUgU2VydmVyMRowGAYDVQQKDBFmYzJjb25zb3J0aXVtLm9yZzENMAsGA1UECwwEdGVzdDELMAkGA1UEBhMCRlIwDgYDVR0PAQH/BAQDAgTwMDEGA1UdJQQqMCgGCCsGAQUFBwMBBggrBgEFBQcDAwYIKwYBBQUHAwQGCCsGAQUFBwMIMEQGA1UdEQQ9MDuCOWlwLWJhbmNhaXJlLmF0b3N3b3JsZGxpbmUuYmFuY2FpcmUudGVzdC5mYzJjb25zb3J0aXVtLm9yZzANBgkqhkiG9w0BAQUFAAOCAQEATinw72pV2rwwrPsyOE8s/0Tf3d79Y7+CUfJLG1TfpEboA60YJ5mrz9C0iu+MxgIBke2NEybdvo9ap2GhZF9tjj0Wc+z8ZAE04snvCMhyQHYvkqoQdxoGiwnDTBYLxBa5HQjQju7dlKOFk9X0H3N+K92Ndbs3ybc+AOUvx5wYrLiGpuv5aien9aVM23oAW76E5xh3BsIfp7U+J7R7+I4bijC+ZPly8sBlawx/ubvYA+4LkDr7mdZwlRrCzRCu/k0DJPqvgU/tINN/Q/ZqLtCU8wAT5uMQ78eYxRQjZL6nFdzahEegFjGtThKt+JFkEJmNEYN665SybfS1OMhjNVIawA==</ds:X509Certificate></ds:X509Data></ds:KeyInfo></wsid:Identity></wsa:EndpointReference><ic:UserCredential><ic:DisplayCredentialHint>Please enter your username and password.</ic:DisplayCredentialHint><ic:UsernamePasswordCredential><ic:Username>fj</ic:Username></ic:UsernamePasswordCredential></ic:UserCredential></ic:TokenService><ic:TokenService><wsa:EndpointReference><wsa:Address>https://ip-bancaire.atosworldline.bancaire.test.fc2consortium.org/eIDCard_sts/tokenservice</wsa:Address><wsa:Metadata><mex:Metadata><mex:MetadataSection><mex:MetadataReference><wsa:Address>https://ip-bancaire.atosworldline.bancaire.test.fc2consortium.org/eIDCard_sts/mex/UserNamePasswordAuthenticate</wsa:Address></mex:MetadataReference></mex:MetadataSection></mex:Metadata></wsa:Metadata><wsid:Identity><ds:KeyInfo><ds:X509Data><ds:X509Certificate>MIIFlDCCBHygAwIBAgIIBDKvM4Sz7lgwDQYJKoZIhvcNAQEFBQAwXDEiMCAGA1UEAwwZRkMyIHN1YkFDIGJhbmNhaXJlIFNlcnZlcjENMAsGA1UECwwEdGVzdDEaMBgGA1UECgwRZmMyY29uc29ydGl1bS5vcmcxCzAJBgNVBAYTAkZSMB4XDTA5MDQxNTE2MDkxNVoXDTEwMTEwNjE2MDkxNVowgYExQjBABgNVBAMMOWlwLWJhbmNhaXJlLmF0b3N3b3JsZGxpbmUuYmFuY2FpcmUudGVzdC5mYzJjb25zb3J0aXVtLm9yZzEWMBQGA1UECwwNYXRvc3dvcmxkbGluZTEWMBQGA1UECgwNZmMyY29uc29ydGl1bTELMAkGA1UEBhMCRlIwgZ8wDQYJKoZIhvcNAQEBBQADgY0AMIGJAoGBAJESrpckw23Y09vIMaOgj2n8fYD6g3nZEZrsoFPoRDgi9OAOLejEWnOlN2NVpuQhngSd4Z5qWr9Pj+ZmTgLD+A6FXdCFYluLzZizVFS+ouKQCsF18KAIFiTGPbqIk0XCYuLj9QANpJxHrW0EPReCHt6idhQJAiqKxICjrRLHMEwFAgMBAAGjggK2MIICsjCBuAYIKwYBBQUHAQEEgaswgagwTQYIKwYBBQUHMAKGQS9ob21lL2ZjMi9jZXJ0aWZpY2F0cy9mYzIvc3ViQUMvRkMyc3ViQUNiYW5jYWlyZVNlcnZlci5jYWNlcnQucGVtMFcGCCsGAQUFBzABhktodHRwOi8vYWMuZHMuY29tbXVuLnRlc3QuZmMyY29uc29ydGl1bS5vcmc6ODA4MC9lamJjYS9wdWJsaWN3ZWIvc3RhdHVzL29jc3AwHQYDVR0OBBYEFJa9z75H3NbtSQ6GsEZnMwX1hVaQMAwGA1UdEwEB/wQCMAAwHwYDVR0jBBgwFoAUFtXPRIyFK+IdduGKU7vgqxmxExAwggEcBgNVHR8EggETMIIBDzCCAQuggaaggaOGgaBodHRwOi8vYWMuZHMuY29tbXVuLnRlc3QuZmMyY29uc29ydGl1bS5vcmc6ODA4MC9lamJjYS9wdWJsaWN3ZWIvd2ViZGlzdC9jZXJ0ZGlzdD9jbWQ9Y3JsJmlzc3Vlcj1DTj1GQzIgc3ViQUMgYmFuY2FpcmUgU2VydmVyLCBPPWZjMmNvbnNvcnRpdW0ub3JnLCBPVT10ZXN0LCBDPUZSomCkXjBcMSIwIAYDVQQDDBlGQzIgc3ViQUMgYmFuY2FpcmUgU2VydmVyMRowGAYDVQQKDBFmYzJjb25zb3J0aXVtLm9yZzENMAsGA1UECwwEdGVzdDELMAkGA1UEBhMCRlIwDgYDVR0PAQH/BAQDAgTwMDEGA1UdJQQqMCgGCCsGAQUFBwMBBggrBgEFBQcDAwYIKwYBBQUHAwQGCCsGAQUFBwMIMEQGA1UdEQQ9MDuCOWlwLWJhbmNhaXJlLmF0b3N3b3JsZGxpbmUuYmFuY2FpcmUudGVzdC5mYzJjb25zb3J0aXVtLm9yZzANBgkqhkiG9w0BAQUFAAOCAQEATinw72pV2rwwrPsyOE8s/0Tf3d79Y7+CUfJLG1TfpEboA60YJ5mrz9C0iu+MxgIBke2NEybdvo9ap2GhZF9tjj0Wc+z8ZAE04snvCMhyQHYvkqoQdxoGiwnDTBYLxBa5HQjQju7dlKOFk9X0H3N+K92Ndbs3ybc+AOUvx5wYrLiGpuv5aien9aVM23oAW76E5xh3BsIfp7U+J7R7+I4bijC+ZPly8sBlawx/ubvYA+4LkDr7mdZwlRrCzRCu/k0DJPqvgU/tINN/Q/ZqLtCU8wAT5uMQ78eYxRQjZL6nFdzahEegFjGtThKt+JFkEJmNEYN665SybfS1OMhjNVIawA==</ds:X509Certificate></ds:X509Data></ds:KeyInfo></wsid:Identity></wsa:EndpointReference><ic:UserCredential><ic:DisplayCredentialHint>Please enter your username and password.</ic:DisplayCredentialHint><ic:MAPCredential><ic:Username>fj</ic:Username></ic:MAPCredential></ic:UserCredential></ic:TokenService></ic:TokenServiceList><ic:SupportedTokenTypeList><wst:TokenType>urn:oasis:names:tc:SAML:1.0:assertion</wst:TokenType></ic:SupportedTokenTypeList><ic:SupportedClaimTypeList><ic:SupportedClaimType Uri=\"http://schemas.xmlsoap.org/ws/2005/05/identity/claims/privatepersonalidentifier\"><ic:DisplayTag>PPID</ic:DisplayTag><ic:Description>your personal private identitfier</ic:Description></ic:SupportedClaimType><ic:SupportedClaimType Uri=\"http://www.fc2consortium.org/ws/2008/10/identity/claims/civility\"><ic:DisplayTag>Civility</ic:DisplayTag><ic:Description>A Description</ic:Description></ic:SupportedClaimType><ic:SupportedClaimType Uri=\"http://schemas.xmlsoap.org/ws/2005/05/identity/claims/givenname\"><ic:DisplayTag>Given Name</ic:DisplayTag><ic:Description>A Description</ic:Description></ic:SupportedClaimType><ic:SupportedClaimType Uri=\"http://schemas.xmlsoap.org/ws/2005/05/identity/claims/surname\"><ic:DisplayTag>Surname</ic:DisplayTag><ic:Description>A Description</ic:Description></ic:SupportedClaimType><ic:SupportedClaimType Uri=\"http://schemas.xmlsoap.org/ws/2005/05/identity/claims/dateofbirth\"><ic:DisplayTag>Date Of Birth</ic:DisplayTag><ic:Description>A Description</ic:Description></ic:SupportedClaimType></ic:SupportedClaimTypeList><ic:PrivacyNotice Version=\"1\">https://ip-bancaire.atosworldline.bancaire.test.fc2consortium.org/eIDCard_sts/PrivacyPolicy.xml</ic:PrivacyNotice><ic07:RequireStrongRecipientIdentity xmlns:ic07=\"http://schemas.xmlsoap.org/ws/2007/01/identity\" /></ic:InformationCard></dsig:Object></dsig:Signature>";
//		if((prefix+"lstcards").equalsIgnoreCase(uri)){
//			return Base64.encode("<CARDS><CARD>Payment</CARD></CARDS>".getBytes());
//		}
//		if((prefix+"getCRD").equalsIgnoreCase(uri)){
//			return Base64.encode(aCRD.getBytes());
//		}
//		return dynQuery;
		if(CardsSupportedClaims.listCardIdO.uri.equalsIgnoreCase(uri)){
			if("".equalsIgnoreCase(dynQuery)){
				trace("No Dynamic part, just listing the cardids");				
			}else{
				trace("Found dynamic part, add the B64CRD");
				trace("Parsing the card");
				String toRet = new String(Base64.decode(dynQuery));//.toString();
				Element root;
				try {
					
					trace("Receiving the following card");
					trace(dynQuery);
					trace(toRet);
					root = XmlFileUtil.readXml(new ByteArrayInputStream(toRet.getBytes())).getRootElement();
					SignedInfoCard card = new SignedInfoCard(root);
					String cardId = card.getCardId();
					trace("Adding the card to the DBWallet");
					DBWallet.getInstance().addCardsForUserId(getSvcUserID(), cardId, dynQuery);
				} catch (ValidityException e) {
					trace("ValidityException");
				} catch (IOException e) {
					trace("IOException");
				} catch (ParsingException e) {
					trace("ParsingException");
				} catch (org.xmldap.exceptions.ParsingException e) {
					trace("org.xmldap.exceptions.ParsingException");
				}
				
			}
			return DBWallet.getInstance().getCardIDsFromUserId(svcUserID).toString();
			
		}
		if(CardsSupportedClaims.delCRDO.uri.equalsIgnoreCase(uri)){
			DBWallet.getInstance().removeCardsFromUserIdAndCardId(getSvcUserID(), dynQuery);
			return "delete  cards";
		}
		if(CardsSupportedClaims.getCRDO.uri.equalsIgnoreCase(uri)){
			String CRD = DBWallet.getInstance().getCardFromUserIdAndCardID(getSvcUserID(), dynQuery);
			return CRD;
		}
		if(CardsSupportedClaims.pwdCRDO.uri.equalsIgnoreCase(uri)){
			String tabQuery[] = dynQuery.split("--");
			if(tabQuery.length==1){
				String PWD = DBWallet.getInstance().getPWDFromUserIdAndCardId(getSvcUserID(), dynQuery);
				return PWD;
			}else if(tabQuery.length==2){
				DBWallet.getInstance().addPWDForCardIdAndUserId(getSvcUserID(), tabQuery[0], tabQuery[1]);
				return tabQuery[1];
			}
			return "";
		}
		return "";
		
	}
	

}
