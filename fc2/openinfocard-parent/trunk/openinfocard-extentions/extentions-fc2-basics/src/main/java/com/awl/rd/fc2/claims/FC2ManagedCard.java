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
package com.awl.rd.fc2.claims;

import java.util.Vector;

import org.apache.log4j.Logger;
import org.xmldap.sts.db.ManagedCard;

import com.awl.rd.fc2.data.connectors.Service;
import com.awl.rd.fc2.data.connectors.services.IServices;
import com.utils.XMLParser;
import com.utils.execeptions.XMLParser_Exception_NO_ATTRIBUTE;

public class FC2ManagedCard extends ManagedCard {
	static Logger log = Logger.getLogger(FC2ManagedCard.class);
	static public void trace(Object message){
		log.info(message);
	}
	public String stsUserId;
	public Vector<Service> m_vecServices;
	public void setSTSUserId(String id){
		stsUserId = id;
	}
	public void setAssociatedServices(Vector<Service> services){
		m_vecServices = services;
	}
	public FC2ManagedCard() {
		super();
	}
	public FC2ManagedCard(String cardId){
		super(cardId);
	}
	public String getClaim(String uri) {
		trace("getClaim("+uri+")");
    	//return supportedClaims.get(uri);
		String dynamicQuery ="";
		uri = uri.replace("%3F", "?");
		int qm = uri.indexOf('?');
    	if (qm > 0) {
    		dynamicQuery = uri.substring(qm+1);
    		uri = uri.substring(0,qm);
    	}
		for(Service svc:m_vecServices){
			if(svc.isCompatibleWith(uri)){
				String strSVC = svc.getM_strInterface();
				try {
					IServices endpoint = (IServices) Class.forName(strSVC).newInstance();
					String xmlParameters = "<SERVICE>"+
					 "<USERID>"+
					 "<SVC>"+svc.getUserIdInThisService()+"</SVC>"+
					 "<STS>"+stsUserId+"</STS>"+
					 "<CARD>"+getCardId()+"</CARD>"+
					 "</USERID>"+
					  svc.getXmlParameters()+
					 "</SERVICE>";
					endpoint.initService(xmlParameters);
					
					String query = "<QUERY>"+
					 "<CLAIM>"+
					 "<URI>"+uri+"</URI>"+
					 "<DYNAMIC>"+dynamicQuery+"</DYNAMIC>"+
					 "</CLAIM>"+
					 "</QUERY>";
					String response = endpoint.execute(query);
					return XMLParser.getFirstValue(response,"VALUE" );
					
				} catch (InstantiationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return null;
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return null;
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return null;
				} catch (XMLParser_Exception_NO_ATTRIBUTE e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return null;
				}

			}
		}
		return null;
    }
    
   
    /*
    public Set<String>getClaims() {
    	
    	//return null;
    }*/
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
