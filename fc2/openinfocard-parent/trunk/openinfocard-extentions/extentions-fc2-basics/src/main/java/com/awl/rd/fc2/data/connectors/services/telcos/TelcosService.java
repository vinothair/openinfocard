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
package com.awl.rd.fc2.data.connectors.services.telcos;

import org.apache.log4j.Logger;

import com.awl.rd.fc2.data.connectors.services.IServices;
import com.awl.rd.fc2.data.connectors.services.ServiceUtils;
import com.awl.rd.fc2.data.connectors.services.dbutils.DBUtils;
import com.utils.XMLParser;
import com.utils.execeptions.XMLParser_Exception_NO_ATTRIBUTE;
import com.utils.execeptions.XMLParser_Exception_NoNextValue;


public class TelcosService extends ServiceUtils implements IServices {

	static Logger log = Logger.getLogger(TelcosService.class);
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

	public String getValue(String uri,String dynQuery){		
		DBUtils localDB = DBUtils.getInstance();
		TelcosData user = localDB.getUser(TelcosData.class, getSvcUserID());
		if(user == null){
			return "USER NOT FOUND";
		}
		String _uri[] = uri.split("/");		
		return user.get(_uri[_uri.length-1]);	
	}
	static DBUtils localDB = DBUtils.getInstance();//DBUtils<EIDData>.getInstance();

}
