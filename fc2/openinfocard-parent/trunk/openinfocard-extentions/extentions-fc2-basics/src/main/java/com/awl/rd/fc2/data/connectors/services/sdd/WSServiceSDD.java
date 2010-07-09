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
package com.awl.rd.fc2.data.connectors.services.sdd;

import org.apache.log4j.Logger;
import org.xmldap.util.PropertiesManager;

import com.atosworldline.rd.payment.sdd.webservices.FillNSignMandate;
import com.awl.rd.fc2.data.connectors.services.IServices;
import com.awl.rd.fc2.data.connectors.services.ServiceUtils;
import com.utils.Base64;
import com.utils.XMLParser;
import com.utils.execeptions.XMLParser_Exception_NO_ATTRIBUTE;
import com.utils.execeptions.XMLParser_Exception_NoNextValue;


public class WSServiceSDD extends ServiceUtils implements IServices {
	static Logger log = Logger.getLogger(WSServiceSDD.class);
	String url;
	public static void trace(Object message){
		log.info(message);
		
	}
	public static void err(Object message){
		log.error(message);
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
				}
				if(dynRequest.length()>0){
					trace("Decode Base 64  = "+ dynRequest);
					String tmplMandat = new String(Base64.decode(dynRequest));
					trace("CALL WEBSERVICE WITH PARAMS : ["+getSvcUserID()+","+tmplMandat+"]");
					//FillNSignMandate mandat = FactoryWSSDD.createInstance("http://localhost:8080/ValidationService/validation");
					FillNSignMandate mandat = FactoryWSSDD.createInstance(PropertiesManager.getInstance().getProperty("sddServiceUrl"));
					
					
					
					
					try {
						
						
						String signedMandat = mandat.signMandate(getSvcUserID(), tmplMandat);					
						trace("Signed mandat => "+signedMandat);
						String encodedMandat = Base64.encode(signedMandat.getBytes());
						String decodedMandat = new String(Base64.decode(encodedMandat));
						trace("Encoded/Decoded mandat => "+decodedMandat);
						response +="<CLAIM><URI>"+uri+"</URI><VALUE>"+encodedMandat+"</VALUE></CLAIM>";
					} catch (Exception e) {
						err("CALLING WEBSERVICE");
					}
					//response +="<CLAIM><URI>"+uri+"</URI><VALUE>"+dynRequest+"</VALUE></CLAIM>";
				}else{
					response +="<CLAIM><URI>"+uri+"</URI><VALUE>"+"NOT HANDLE BY WSServiceSDD"+"</VALUE></CLAIM>";
				}
				
				
			}
		} catch (XMLParser_Exception_NO_ATTRIBUTE e) {
			err("XMLPARSER");
		} catch (XMLParser_Exception_NoNextValue e) {
			err("XMLPARSER");
		}
		trace("REPONSE   " + response); 
		return response +"</RESPONSE>";
	
	}
	/**
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
		//trace("xmlParam : " + xmlParameters);
		constructCommonParameters(xmlParameters);
	/*	try {
			url = XMLParser.getFirstValue(xmlParameters, "ENDPOINT");
		} catch (XMLParser_Exception_NO_ATTRIBUTE e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
