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
package com.awl.fc2.selector;

import com.awl.fc2.selector.exceptions.Config_Exception_NotDone;
import com.awl.fc2.selector.launcher.Config;
import com.awl.logger.Logger;
import com.utils.XMLParser;
import com.utils.execeptions.XMLParser_Exception_NO_ATTRIBUTE;

/**
 * This class aims to set up the proxy and the trust store for further connection. It is called by the constructor of the {@link Selector}
 * @author Cauchie st�phane
 *
 */
public class AWLProxy {
	static Logger log = new Logger(AWLProxy.class);
	static public void trace(Object msg){
		log.trace(msg);
	}
	static boolean allreadyset = false;
	private AWLProxy() {
	
	}
	/**
	 * Set the proxy, by parsing the XML of the Config_Selector.xml</br>
	 * The following balises are used : </br>
	 * - PROXY/URL : URL of the proxy</br>
	 * - PROXY/PORT : the port of the proxy</br>
	 * - TRUSTSTORE/PATH : The truststore location</br>
	 * - TRUSTSTORE/PWD : The truststore password</br>
	 */
	static public void setProxy(){
		trace("http.proxyHost =" +System.getProperty("http.proxyHost"));
		trace("http.proxyPort =" +System.getProperty("http.proxyPort"));
		trace("https.proxyHost =" +System.getProperty("https.proxyHost"));
		trace("https.proxyPort =" +System.getProperty("https.proxyPort"));
		trace("socksProxyPort =" +System.getProperty("socksProxyPort"));
		trace("socksProxyHost =" +System.getProperty("socksProxyHost"));
		trace("http.proxySet =" +System.getProperty("http.proxySet"));
		trace("https.proxySet =" +System.getProperty("https.proxySet"));
		
		if(!allreadyset){
			try {
				trace("!allreadyset");
				String xml = Config.getInstance().getXML();
				String host = XMLParser.getFirstValue(xml, "PROXY/URL");
				String port = XMLParser.getFirstValue(xml, "PROXY/PORT");
				String tstore = XMLParser.getFirstValue(xml, "TRUSTSTORE/PATH");
				String pwd = XMLParser.getFirstValue(xml, "TRUSTSTORE/PWD");
				System.out.println("setting proxy : " + host +"["+port+"]");
				System.out.println("AVANT =" +System.getProperty("http.proxyHost"));
				System.setProperty("http.proxyHost", host);
				System.setProperty("http.proxyPort", port);
				System.setProperty("socksProxyHost", host);
				System.setProperty("socksProxyPort", port);
				System.setProperty("https.proxyHost", host);
				System.setProperty("https.proxyPort", port);
				System.setProperty("socksProxySet", "true");
				System.setProperty("http.proxySet","true");
				System.setProperty("https.proxySet", "true");
				trace("APRES =" +System.getProperty("http.proxyHost"));
				trace("import trust store");
				//-Djavax.net.ssl.trustStore=mySrvKeystore -Djavax.net.ssl.trustStorePassword=123456 
			
				System.setProperty("javax.net.ssl.trustStore",tstore);
				System.setProperty("javax.net.ssl.trustStorePassword",pwd);

			
			} catch (XMLParser_Exception_NO_ATTRIBUTE e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Config_Exception_NotDone e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}else{
			trace("allreadyset");
			System.setProperty("http.proxyHost", "");
			System.setProperty("http.proxyPort", "");
			System.setProperty("https.proxyHost", "");
			System.setProperty("https.proxyPort", "");
		
		}
		trace("http.proxyHost =" +System.getProperty("http.proxyHost"));
		trace("http.proxyPort =" +System.getProperty("http.proxyPort"));
		trace("https.proxyHost =" +System.getProperty("https.proxyHost"));
		trace("https.proxyPort =" +System.getProperty("https.proxyPort"));
		trace("socksProxyPort =" +System.getProperty("socksProxyPort"));
		trace("socksProxyHost =" +System.getProperty("socksProxyHost"));
		trace("http.proxySet =" +System.getProperty("http.proxySet"));
		trace("https.proxySet =" +System.getProperty("https.proxySet"));
		allreadyset = true;
	}
}
