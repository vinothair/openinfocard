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
package com.awl.fc2.plugin.connector.hbxsocket;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.Vector;

import javax.swing.JOptionPane;

import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Nodes;
import nu.xom.ParsingException;
import nu.xom.XPathContext;

import org.xmldap.crypto.CryptoUtils;
import org.xmldap.exceptions.CryptoException;
import org.xmldap.exceptions.SerializationException;
import org.xmldap.ws.WSConstants;
import org.xmldap.xmlenc.EncryptedData;

import com.awl.fc2.selector.Selector;
import com.awl.fc2.selector.connector.IConnector;
import com.awl.fc2.selector.exceptions.Config_Exception_NotDone;
import com.awl.fc2.selector.exceptions.Config_Exeception_MalFormedConfigFile;
import com.awl.fc2.selector.exceptions.Config_Exeception_UnableToReadConfigFile;
import com.awl.logger.Logger;
//import com.sun.org.apache.xml.internal.security.utils.Base64;
/**
 *  This class is the specific connector for azigo.
 *   
 * @author Cauchie stéphane
 * @see IConnector
 */
public class HBXSocketConnector implements IConnector  {
	String m_strCertRP_Base64;
	String m_strCertRP ="MIIFjTCCBHWgAwIBAgIIZ/RR+bJxWDYwDQYJKoZIhvcNAQEFBQAwXDEiMCAGA1UEAwwZRkMyIHN1YkFDIGJhbmNhaXJlIFNlcnZlcjENMAsGA1UECwwEdGVzdDEaMBgGA1UECgwRZmMyY29uc29ydGl1bS5vcmcxCzAJBgNVBAYTAkZSMB4XDTA5MDQxNTE2MDkxNFoXDTEwMTEwNjE2MDkxNFowfjE/MD0GA1UEAww2cmVudGFjYXIuYXRvc3dvcmxkbGluZS5iYW5jYWlyZS50ZXN0LmZjMmNvbnNvcnRpdW0ub3JnMRYwFAYDVQQLDA1hdG9zd29ybGRsaW5lMRYwFAYDVQQKDA1mYzJjb25zb3J0aXVtMQswCQYDVQQGEwJGUjCBnzANBgkqhkiG9w0BAQEFAAOBjQAwgYkCgYEAgpLllQHP4dm9TLECa3FMhOjAxucVvLSQayP3vyrkU4HtP4TuG0dK4cNmLkeGQHiDp0XZPtPqa3kYUACkIa6DsG1sNVXnfSO7P+URHPwZVWUia0/7vQx5fmC6UuMOHfhDpZ7WDfTLrfZDXQah+TTNeLOFgzp7hAjbi7+mollKFIUCAwEAAaOCArMwggKvMIG4BggrBgEFBQcBAQSBqzCBqDBNBggrBgEFBQcwAoZBL2hvbWUvZmMyL2NlcnRpZmljYXRzL2ZjMi9zdWJBQy9GQzJzdWJBQ2JhbmNhaXJlU2VydmVyLmNhY2VydC5wZW0wVwYIKwYBBQUHMAGGS2h0dHA6Ly9hYy5kcy5jb21tdW4udGVzdC5mYzJjb25zb3J0aXVtLm9yZzo4MDgwL2VqYmNhL3B1YmxpY3dlYi9zdGF0dXMvb2NzcDAdBgNVHQ4EFgQUtZGZUl2qIPoJQ4J99ruGeF7yJHIwDAYDVR0TAQH/BAIwADAfBgNVHSMEGDAWgBQW1c9EjIUr4h124YpTu+CrGbETEDCCARwGA1UdHwSCARMwggEPMIIBC6CBpqCBo4aBoGh0dHA6Ly9hYy5kcy5jb21tdW4udGVzdC5mYzJjb25zb3J0aXVtLm9yZzo4MDgwL2VqYmNhL3B1YmxpY3dlYi93ZWJkaXN0L2NlcnRkaXN0P2NtZD1jcmwmaXNzdWVyPUNOPUZDMiBzdWJBQyBiYW5jYWlyZSBTZXJ2ZXIsIE89ZmMyY29uc29ydGl1bS5vcmcsIE9VPXRlc3QsIEM9RlKiYKReMFwxIjAgBgNVBAMMGUZDMiBzdWJBQyBiYW5jYWlyZSBTZXJ2ZXIxGjAYBgNVBAoMEWZjMmNvbnNvcnRpdW0ub3JnMQ0wCwYDVQQLDAR0ZXN0MQswCQYDVQQGEwJGUjAOBgNVHQ8BAf8EBAMCBPAwMQYDVR0lBCowKAYIKwYBBQUHAwEGCCsGAQUFBwMDBggrBgEFBQcDBAYIKwYBBQUHAwgwQQYDVR0RBDowOII2cmVudGFjYXIuYXRvc3dvcmxkbGluZS5iYW5jYWlyZS50ZXN0LmZjMmNvbnNvcnRpdW0ub3JnMA0GCSqGSIb3DQEBBQUAA4IBAQCxBgJB57BH0e3tRqGcNstiEhwjsAUEa7VeLDnUzAZzpwHqg6wMyakbR0r5X86b/o/T60tMo1zRu7Lkd4MDSN90Clu0FKC9MW7/F0bg2J0Tc6rA9/KU4nwl08v+HSOr9WqyzauA5RxKH8vh44gCCEwCbp+ESzcXI0vTdimrnYTCtPTqOp3YzlKJAykkwtRc1c/7wLx77BTtuRccimWGGPKJQcDA6JSgq8OemTyfK0ODn2k1x8lE//fAGWnmQSuURldq/tUIvSscq+MA/fpYT1o+nbOooK6BgNVvRZFuuNMHp+oms7AonTuzg/LWhIaHTpGA5ZwZ9XSyIuyZHlyB3wk9MIIFCTCCA/GgAwIBAgIICadDQ8Rzx0cwDQYJKoZIhvcNAQEFBQAwUjEYMBYGA1UEAwwPRkMyIEFDIGJhbmNhaXJlMQ0wCwYDVQQLDAR0ZXN0MRowGAYDVQQKDBFmYzJjb25zb3J0aXVtLm9yZzELMAkGA1UEBhMCRlIwHhcNMDkwMzE4MDk1NjI3WhcNMTcwNjA0MDk1NjI3WjBcMSIwIAYDVQQDDBlGQzIgc3ViQUMgYmFuY2FpcmUgU2VydmVyMQ0wCwYDVQQLDAR0ZXN0MRowGAYDVQQKDBFmYzJjb25zb3J0aXVtLm9yZzELMAkGA1UEBhMCRlIwggEiMA0GCSqGSIb3DQEBAQUAA4IBDwAwggEKAoIBAQDQMsX3uhSOU3ildoQqUyvYhjVyd4bVgoOnshDBw1kNnvLWxFbjPCXjCdvZdadWniaQjxb1qOxhjBP7SV6eKUvIIoSWkMZ6VxPW+ZWxGmpMdxOi4r1DI+Iq3ejlOjkJCY11kk8h9wzUJNPlrarCkJNu1KoaKsiH2ULlomt5qz7E/CuHnDllOH/MGVBcnHDiqDjs5nQlp9tTIrdIrk+pstBLzMVwUe84jxZBd0V/5QPqh43U2wvd/JcjwEsXo9yx9b1p6GrUmLF4Zz8zkFi9k23DL4Xi6lQ60C4hVlqvf2TDtL3ecNtAzWfYARzvF4iodODMQSrwFJiCmB6r0oT6G6kbAgMBAAGjggHXMIIB0zBnBggrBgEFBQcBAQRbMFkwVwYIKwYBBQUHMAGGS2h0dHA6Ly9hYy5kcy5jb21tdW4udGVzdC5mYzJjb25zb3J0aXVtLm9yZzo4MDgwL2VqYmNhL3B1YmxpY3dlYi9zdGF0dXMvb2NzcDAdBgNVHQ4EFgQUFtXPRIyFK+IdduGKU7vgqxmxExAwDwYDVR0TAQH/BAUwAwEB/zAfBgNVHSMEGDAWgBRRm17ugbPUpQleeTT0UY/SuD56BzCCAQUGA1UdHwSB/TCB+jCB96CBnKCBmYaBlmh0dHA6Ly9hYy5kcy5jb21tdW4udGVzdC5mYzJjb25zb3J0aXVtLm9yZzo4MDgwL2VqYmNhL3B1YmxpY3dlYi93ZWJkaXN0L2NlcnRkaXN0P2NtZD1jcmwmaXNzdWVyPUNOPUZDMiBBQyBiYW5jYWlyZSwgTz1mYzJjb25zb3J0aXVtLm9yZywgT1U9dGVzdCwgQz1GUqJWpFQwUjEYMBYGA1UEAwwPRkMyIEFDIGJhbmNhaXJlMRowGAYDVQQKDBFmYzJjb25zb3J0aXVtLm9yZzENMAsGA1UECwwEdGVzdDELMAkGA1UEBhMCRlIwDgYDVR0PAQH/BAQDAgFGMA0GCSqGSIb3DQEBBQUAA4IBAQBrsN3R9JYBF4MrcTeIsdUNUeGXaTlQFDu81Xbe9IXqoGYU0kWxT5sixyqsj3Xn9XM44F53T5VEN4oReLnRPTM41RXKLdltj1ZnVZ9XVuyvHR8kVrdQq8eSn5lrib57YhJ2s2XDrznJQq/Og1RTKk/KH1Y8sQWDrPe9fCgbJbcc0IOtGv7vPgsYT87lto95sEPKDTad1dCrGDSns7djhY6uWRD6iwx7RxSQu5gAU/lwqewqYEcA/6WOgm4pmVs3/B3KvpMQ0DQLIqI1Yq2YrkiWjIfyPZlWdnKVZdsumCsMKJ8Pf5uSC6P5sbTEjB1VZjYsjBkLmalVD27V3gt0fAtHMIIE/zCCA+egAwIBAgIIUF00GwUssRUwDQYJKoZIhvcNAQEFBQAwUjEYMBYGA1UEAwwPRkMyIEFDIGJhbmNhaXJlMQ0wCwYDVQQLDAR0ZXN0MRowGAYDVQQKDBFmYzJjb25zb3J0aXVtLm9yZzELMAkGA1UEBhMCRlIwHhcNMDkwMzE4MDk1MjAxWhcNMTkwMzE2MDk1MjAxWjBSMRgwFgYDVQQDDA9GQzIgQUMgYmFuY2FpcmUxDTALBgNVBAsMBHRlc3QxGjAYBgNVBAoMEWZjMmNvbnNvcnRpdW0ub3JnMQswCQYDVQQGEwJGUjCCASIwDQYJKoZIhvcNAQEBBQADggEPADCCAQoCggEBAIJGwkfYVRNkWuqnpdZYAqldiAbkwr7qTImbaRBo0DSDNYCiCIlJJYPoBfjijoY0ciJmcLdUeTzqKvkfvtX3flal1+ABwKYPkYU+YMwkspeQxavLPbtiB3M+Tylo6et+LdLp44r5GNO1+6pqHutuRUdKH8fx3t3MaqsTcMuNewYVCDgpnMZ+eW1RbN/ZpJsgLIMToVfN0VRCEURfcPky8ix2f3/VmL5uJdX9K+EgJIu13+xnO93VUfiHRZrIMl/ZdD2b7TXW+Z5UWSgjWDjQWec5wpHi81EJP42OU3DgTV4/V/JeSVFf0/5JyXNFhXblQwzkCXsdM/2UfQo9eqGf6LcCAwEAAaOCAdcwggHTMGcGCCsGAQUFBwEBBFswWTBXBggrBgEFBQcwAYZLaHR0cDovL2FjLmRzLmNvbW11bi50ZXN0LmZjMmNvbnNvcnRpdW0ub3JnOjgwODAvZWpiY2EvcHVibGljd2ViL3N0YXR1cy9vY3NwMB0GA1UdDgQWBBRRm17ugbPUpQleeTT0UY/SuD56BzAPBgNVHRMBAf8EBTADAQH/MB8GA1UdIwQYMBaAFFGbXu6Bs9SlCV55NPRRj9K4PnoHMIIBBQYDVR0fBIH9MIH6MIH3oIGcoIGZhoGWaHR0cDovL2FjLmRzLmNvbW11bi50ZXN0LmZjMmNvbnNvcnRpdW0ub3JnOjgwODAvZWpiY2EvcHVibGljd2ViL3dlYmRpc3QvY2VydGRpc3Q/Y21kPWNybCZpc3N1ZXI9Q049RkMyIEFDIGJhbmNhaXJlLCBPPWZjMmNvbnNvcnRpdW0ub3JnLCBPVT10ZXN0LCBDPUZSolakVDBSMRgwFgYDVQQDDA9GQzIgQUMgYmFuY2FpcmUxGjAYBgNVBAoMEWZjMmNvbnNvcnRpdW0ub3JnMQ0wCwYDVQQLDAR0ZXN0MQswCQYDVQQGEwJGUjAOBgNVHQ8BAf8EBAMCAUYwDQYJKoZIhvcNAQEFBQADggEBAH+367ot70fcTB5hJIWyyUIzJj74cCNRZ0uwrOj3LcGk3aP4O4nXa5JBOwl6diiADzGx13bpe5KXtQV7HwvWCFOfEGjwGI1lpE2qNWSOHJxkpEDHitVDYlc5bAEBwuDhsXW48bps1d1jbhBXpzBbyBItcvB8BqNgwCj9d3jvXHtEP9XitulCvPvFv6tLzepKsBBc9icfFnlBbpXcWxqBZrDL8Xr0msmxB7jSOfue0YLjMXWgkCUp/ptecUjphGzA2JmzQLzGkSGSZebNaDeu23lp2NjB/7bbuxh0BmxiBzk9qfohDUk+4/aXO+PIN257nQPX3pzOupAB+hEoqjP7gjY=";
	Vector<String> m_tabRequiredClaims = new Vector<String>();
	String m_strTokenType;
	X509Certificate m_certRP;
	String m_strURLRP;;
	static Logger log = new Logger(HBXSocketConnector.class);
	
	/**
	 * Internal function used to log
	 * @param obj
	 */
	public static void trace(Object obj){
		log.trace(obj);
		
	}
	
	/**
	 * Build a X509Certificate from the azigo request (Base64 formatted).
	 * As the chain certificates is concatenated in the azigo request, we call {@code getCertificateFromRequest } that build the last certificate
	 * 
	 * @param certifBase64 the certificate in base 64 (it can be a certification chain)
	 * @see X509Certificate
	 */
	public void setCertificate(String certifBase64){
		certifBase64 = certifBase64.replace(" ", "");
	//	int pos = certifBase64.indexOf("==");		
		
		trace("SetCertificate");
		trace("--- recu ----");
		trace(certifBase64);
		trace(m_strCertRP);

		
		m_strCertRP_Base64 = certifBase64;
		m_certRP = getCertificateFromRequest(m_strCertRP_Base64);
		if(m_certRP != null){
			try {
			m_strCertRP_Base64 = Base64.encode(m_certRP.getEncoded());
			} catch (CertificateEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		
	}
	/**
	 * fill the list of required claims
	 * @param theClaims the list of claim in a String. (each claim is space separated). 
	 */
	public void setRequiredClaims(String theClaims){
		String [] claims = theClaims.split(" ");
		m_tabRequiredClaims.clear();
		for(int i=0;i<claims.length;i++){
			if(claims[i].length()!=0){
				trace(claims[i]);
				m_tabRequiredClaims.add(claims[i]);
			}
			
		}
		trace(m_tabRequiredClaims);
	}
	
	
	/**
	 * Parse the Azigo request in order to prepare all the needed parameters (claims, certificate)
	 * @param inputFromAzigo the global azigo request {@code <hbx_request>...</hbx_request>}
	 */
	public void setInputFromRequestor(String inputFromAzigo){
		m_certRP = null;
		trace("ORORO : " + inputFromAzigo);
		inputFromAzigo = inputFromAzigo.replace("\n", "");
		inputFromAzigo = inputFromAzigo.replace("&", "&amp;");
		trace("Input from azigo");
		inputFromAzigo = inputFromAzigo.replace("</document_URL>", ";</document_URL>");
		if(inputFromAzigo.indexOf("</hbx_request>")==-1)
		{
			inputFromAzigo = inputFromAzigo + ">";
		}
		trace(">> : " + inputFromAzigo);
		Builder parser = new Builder();
		Document mex = null;
		try {
		    mex = parser.build(inputFromAzigo, "");
		} catch (ParsingException e) {
		    //throw new ServletException(e);
			e.printStackTrace();	
			return;
		} catch (IOException e) {
		   // throw new ServletException(e);
			return;
		}
		//XPathContext context = new XPathContext("")
		XPathContext context = new XPathContext();
		context.addNamespace("s", WSConstants.SOAP12_NAMESPACE);
		context.addNamespace("a", "http://www.w3.org/2005/08/addressing");
		context.addNamespace("wsp", "http://schemas.xmlsoap.org/ws/2004/09/policy");
		context.addNamespace("wsa10", "http://www.w3.org/2005/08/addressing" );
		Nodes messageIDs = mex.query("//parameter", context);
		for(int i=0;i<messageIDs.size();i++){
			
			Element messageID = (Element) messageIDs.get(i);
		
			String name = messageID.getAttributeValue("name");
			String value = messageID.getAttributeValue("value");
			trace("Parsing azigo request ("+i+") name = " + name);
			if("TokenType".equalsIgnoreCase(name)){
				m_strTokenType=value;
			}
			if("certificate".equalsIgnoreCase(name)){
				setCertificate(value);
			}
			if("requiredClaims".equalsIgnoreCase(name)){
				setRequiredClaims(value);
				
			}
		}
		messageIDs = mex.query("//document_URL", context);
		Element messageID = (Element) messageIDs.get(0);
		m_strURLRP = messageID.getValue();
		
		
		//document_URL*/
	}
	
	/**
	 * Call the {@code onQueryClaims()} with the prepared requests. Finaly it calls {@code encryptToken}.
	 * @see Selector
	 */
	public String getToken() throws Config_Exeception_UnableToReadConfigFile, Config_Exeception_MalFormedConfigFile, Config_Exception_NotDone{
		Selector selector =Selector.getInstance();		
		// Vector<String> lstRequiredClaims = new Vector<String>();
		 Vector<String> lstOptionalClaims = new Vector<String>();
		 
		 
		 selector.onQueryClaims(this.m_tabRequiredClaims, lstOptionalClaims, this.m_strURLRP, this.m_strCertRP_Base64);
		Vector<String> tickets = selector.getTickets();
		if(tickets.size()==0){
			return "FAILED";
		}
		if(tickets.size()==1){
			return encryptToken(tickets.get(0));
		}	
		String xmlRes = "<TOKENS>";
		for(String ticket : tickets){
			
			xmlRes += "<TOKEN>"+encryptToken(ticket)+"</TOKEN>";
			//xmlRes += "<TOKEN>"+ticket+"</TOKEN>";
		}
		xmlRes+="</TOKENS>";
		
		
		return xmlRes;//tickets.get(0);
	}
	
	/**
	 * When the token from the sts is correctly received we encrypted it with the RP public key.
	 * @param stsToken
	 * @return
	 */
	public String encryptToken(String stsToken){

		if(stsToken.contains("EncryptedData")) return stsToken;
		if(m_certRP == null) return stsToken;
		EncryptedData encrypted = null;
		encrypted = new EncryptedData(m_certRP);       
        encrypted.setData(stsToken);
        try {
        	trace("Try to encrypt");
        	String encryptedToken = encrypted.toXML();
        	trace("Encryption : " + encryptedToken);
           return encryptedToken;
        } catch (SerializationException e) {
        	trace("Failed in token encryption");
        	return stsToken;
        }
	}

	/**
	 * Azigo concat all the certificate without any separator. Due to this, we need to find the split in order to construct the proper X509Certificate
	 * @param multiCert
	 * @return the final certificate (should be the RP one)
	 */
	public X509Certificate getCertificateFromRequest(String multiCert){
		X509Certificate toAdd = null;
		toAdd = getCertifFromB64(multiCert);
		if(toAdd != null){
			return toAdd;
		}
		String tab[] = multiCert.split("MII");
		for(int i=0;i<tab.length;i++){
			trace("Actual : " + tab[i]);
			tab[i] = "MII" + tab[i];
			                     
		}
		if(tab.length<2) return null;
		String construct = tab[1]+tab[2];
		Vector<X509Certificate> l_vecChain = new Vector<X509Certificate>();
		int curPos = 1;
		
		construct = tab[curPos];
		toAdd = getCertifFromB64(construct);
		if(toAdd == null){
			for(int i = curPos+1;curPos<tab.length;curPos++){
				construct += tab[i];
				trace("Trying the cert : " + construct);
				toAdd = getCertifFromB64(construct);
				if(toAdd !=null){
					l_vecChain.add(toAdd);
					m_strCertRP_Base64 = construct;
					break;
				}
			}
		}
		if(l_vecChain.size()==0){
			trace("No constructed certificate");
			return null;
		}
		
		return l_vecChain.get(0);
	}
	/**
	 * Try to construct a X509Certificate
	 * @param construct
	 * @return the certificate represented by {@code construct}, null otherwize
	 * @see X509Certificate
	 */
	public static X509Certificate getCertifFromB64(String construct){
		try {
			return   CryptoUtils.X509fromB64(construct);
			
			
			} catch (CryptoException e) {
			return null;
			}
	}

	@Override
	public void run() throws IOException,
			Config_Exeception_UnableToReadConfigFile,
			Config_Exeception_MalFormedConfigFile, Config_Exception_NotDone {
		Selector.getInstance();
    	int localPort = 31001;
    	String remoteHost = "localhost";    	           	  
    	trace("Listening on  " + localPort);
    	//Create a listening socket at proxy

    	ServerSocket server = null;        
    	try
    	{
    		server = new ServerSocket(localPort);
    	}

    	catch(IOException e) 
    	{
    		trace("Error: " + e.getMessage());
    		JOptionPane.showConfirmDialog(null, "An instance of JSS is still running," +
    											"Kill the process,");
    		System.exit(-1);
    	}
    	catch (Exception e) {
    		trace("HERE");
		}
    	//Loop to listen for incoming connection,
    	//and accept if there is one

    	Socket incoming = null; 
    	//Socket outgoing = null;
    	
    	while(true)
    	{
    		try
    		{    			
    			incoming = server.accept();    		
    			ProxyThread thread1 = new ProxyThread(this,incoming,"Incoming Traffic");    			
    			thread1.start();
    		
    		} 
    		catch (UnknownHostException e) 
    		{
    			System.err.println("Error: Unknown Host " + remoteHost);
    			System.exit(-1);
    		} 
    		catch(IOException e)
    		{
    			throw(e);
    		}
    	}
		
	}


}




