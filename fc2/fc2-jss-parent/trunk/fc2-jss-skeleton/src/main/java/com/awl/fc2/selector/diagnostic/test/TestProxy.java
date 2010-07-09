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
package com.awl.fc2.selector.diagnostic.test;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.security.KeyStore;
import java.security.cert.X509Certificate;
import java.util.Enumeration;
import java.util.Hashtable;

import com.awl.fc2.selector.diagnostic.IFC2Test;
import com.awl.fc2.selector.diagnostic.TestReport;
import com.awl.fc2.selector.exceptions.Config_Exception_NotDone;
import com.awl.fc2.selector.launcher.Config;
import com.awl.logger.Logger;
import com.utils.XMLParser;
import com.utils.execeptions.XMLParser_Exception_NO_ATTRIBUTE;


public class TestProxy implements IFC2Test {
	static Logger log = new Logger(TestProxy.class);
	String tabRequiredCertificate[]={
			"ip-idservices.orange.gouv.test.fc2consortium.org",
			"rentacar.atosworldline.bancaire.test.fc2consortium.org",
			"FC2 subAC gouv Server",
			"FC2 AC telecom",
			"ip-bancaire.atosworldline.bancaire.test.fc2consortium.org",
			"ip-telecom.orange.telecom.test.fc2consortium.org"
	};

	static public void trace(Object msg){
		log.trace(msg);		
	}
	TestReport report = null;
	@Override
	public TestReport getReport() {
		return report;
	}

	@Override
	public void run() {
		report = new TestReport("Proxy");
		if(!testConnectionHTTP()){
			report.addProblem("Cannot connect, the proxy may not be set",
							  "NOTHING WILL WORK", 
							  "SET THE PROXY : ");
		}
		listCertificate();
		
	}
	public void listCertificate(){
		String xml;
		try {
			Hashtable<String, Boolean> m_tab = new Hashtable<String, Boolean>();
			for(int i=0;i<tabRequiredCertificate.length;i++){
				m_tab.put(tabRequiredCertificate[i], Boolean.FALSE);
			}
			xml = Config.getInstance().getXML();
			String tstore = XMLParser.getFirstValue(xml, "TRUSTSTORE/PATH");
			String pwd = XMLParser.getFirstValue(xml, "TRUSTSTORE/PWD");
			KeyStore ks = loadStore(tstore, pwd, "JKS");
			Enumeration<String> it = ks.aliases();
			while(it.hasMoreElements()){
				String alias = it.nextElement();
				//trace("We got : "+ alias );
				X509Certificate cert = (X509Certificate) ks.getCertificate(alias);	
				String host = cert.getSubjectDN().getName();
				int pos = host.indexOf("CN=");
				host = host.substring(pos+3);
				//trace("=> "+host);
				m_tab.put(host, Boolean.TRUE);
				
			}
			for(int i=0;i<tabRequiredCertificate.length;i++){
				if(!m_tab.get(tabRequiredCertificate[i]).booleanValue()){
					//trace("missing " + tabRequiredCertificate[i]);
					report.addProblem("Missing " + tabRequiredCertificate[i],
									  "May not be able to make connection with the STS",
									  "Go to the address, save the certificate,\n" +
									  "Execute : keytool -importcert -alias AnAlias -file TheSavedCertificate.crt -keystore JSSPATH/cert/ipbank.jks");
					
				}
			}
			
		} catch (Config_Exception_NotDone e) {
			trace("Confif error, run diagnostic");
		} catch (XMLParser_Exception_NO_ATTRIBUTE e) {
			trace("Confif error, run diagnostic");
		} catch (Exception e) {
			trace("Could not load the keyStore");
		}
		
		
	}
    KeyStore loadStore(String file, String pass, String type) throws Exception {
        KeyStore ks = KeyStore.getInstance(type);
        FileInputStream is = null;
        if (file != null && !file.equals("NONE")) {
            is = new FileInputStream(file);
        }
        ks.load(is, pass.toCharArray());
        is.close();
        return ks;
    }

	public boolean  testConnectionHTTP(){
		//trace("Connection HTTP");
		String mexURL = "http://www.google.com";//"https://ip-idservices.orange.gouv.test.fc2consortium.org/BanditIdP/services/MetadataUsernameToken";
		 URL                url=null;
		    URLConnection      urlConn;
		   // DataInputStream    dis;

		    try {
				url = new URL(mexURL);
			} catch (MalformedURLException e) {
				trace("MalformedURL - should be here");
				return false;
			}

		    // Note:  a more portable URL:
		    //url = new URL(getCodeBase().toString() + "/ToDoList/ToDoList.txt");

		    try {
				urlConn = url.openConnection();
				urlConn.setConnectTimeout(1000);
				urlConn.setReadTimeout(1000);
				urlConn.connect();
//				 urlConn.setDoInput(true);
//				    urlConn.setUseCaches(false);
//
//				    dis = new DataInputStream(urlConn.getInputStream());
//				    String s;
//				 
//				 
//				 
//				    while ((s = dis.readLine()) != null) {
//					      trace(s);
//					    }
//				    dis.close();
				    return true;
			} catch (IOException e) {
				return false;
			} 
			
		   
	}

}
