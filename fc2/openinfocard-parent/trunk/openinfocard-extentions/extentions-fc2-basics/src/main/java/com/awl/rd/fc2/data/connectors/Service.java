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
package com.awl.rd.fc2.data.connectors;

import java.util.UUID;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.xmldap.sts.db.DbSupportedClaim;
import org.xmldap.sts.db.SupportedClaims;

import com.awl.rd.fc2.claims.CBSupportedClaims;
import com.awl.rd.fc2.claims.CardsSupportedClaims;
import com.awl.rd.fc2.claims.DriverLicenceSupportedClaims;
import com.awl.rd.fc2.claims.EIDSupportedClaims;
import com.awl.rd.fc2.claims.RACSupportedClaims;
import com.awl.rd.fc2.claims.SDDSupportedClaims;
import com.awl.rd.fc2.claims.TelcosSupportedClaims;
import com.awl.rd.fc2.data.connectors.services.wallet.DBWallet;

public class Service {

	static Logger log = Logger.getLogger(Service.class);
	public static void trace(Object msg){
		log.info(msg);
	}
	String id;
	String m_strInterface;
	Vector<String> m_vecAttributes = new Vector<String>();
	String userIdInThisService ="";
	String xmlParameters;
	public Service(){
		super();
	}
	public Service(String id, String mStrInterface,
			SupportedClaims setOfClaims, String userIdInThisService,
			String xmlParameters) {
		super();
		this.id = id;
		m_strInterface = mStrInterface;		
		this.userIdInThisService = userIdInThisService;
		this.xmlParameters = xmlParameters;
		addSetOfClaims(setOfClaims);
	}
	public void addSetOfClaims(SupportedClaims setOfClaims)
	{
		int size = setOfClaims.dbSupportedClaims().size();//.length;
		for(DbSupportedClaim claim : setOfClaims.dbSupportedClaims()){
			String att2Add = claim.uri;
			trace("Adding the following claim : " + att2Add);
			m_vecAttributes.add(att2Add);  
		}
//		for(int i=0;i<size;i++){
//			String att2Add = setOfClaims.dbSupportedClaims[i].uri;
//			trace("Adding the following claim : " + att2Add);
//			m_vecAttributes.add(att2Add);    			
//		}  
	}
	public boolean isCompatibleWith(String uri){
		for(String cur:m_vecAttributes){
			if(uri.equalsIgnoreCase(cur)){
				return true;
			}
		}
		return false;
	}
	
	public Service createLocalServiceForUser(String userId){
		return null;
	}
	public static Service createSDDServiceForUser(String userId,String svcUserID){
		Service toRet = new Service();
		toRet.id = UUID.randomUUID().toString();
		toRet.m_strInterface = "com.awl.rd.fc2.data.connectors.services.sdd.WSServiceSDD";
		toRet.userIdInThisService = svcUserID;	
		toRet.addSetOfClaims(new SDDSupportedClaims());
		toRet.xmlParameters = "<SPECIFIC><ENDPOINT>http://localhost:8080/ValidationService/validation</ENDPOINT></SPECIFIC>";
		
		return toRet;
		
	}
	
	public static Service createWalletServiceForUser(String userId,String svcUserID){
		Service toRet = new Service();
		toRet.id = UUID.randomUUID().toString();
		toRet.m_strInterface = "com.awl.rd.fc2.data.connectors.services.wallet.WalletService";
		toRet.userIdInThisService = svcUserID;
		DBWallet.getInstance().addUser(svcUserID);
		toRet.addSetOfClaims(new CardsSupportedClaims());
		toRet.xmlParameters = "<SPECIFIC></SPECIFIC>";
		return toRet;
		
	}
	public String toString(){
		return "[userIdInThisService="+userIdInThisService+"]";
	}
	public static Service createPaymentCard(String userId,String svcUserID){
		Service toRet = new Service();
		toRet.id = UUID.randomUUID().toString();
		toRet.m_strInterface = "com.awl.rd.fc2.data.connectors.services.paymentDBBandit.LocalService_PaymentCard";
		toRet.userIdInThisService = svcUserID;
		toRet.addSetOfClaims(new CBSupportedClaims());
		toRet.xmlParameters = "<SPECIFIC></SPECIFIC>";
		return toRet;
	}
	
	public static Service createEIDService(String userId,String svcUserID){
		Service toRet = new Service();
		toRet.id = UUID.randomUUID().toString();
		toRet.m_strInterface = "com.awl.rd.fc2.data.connectors.services.eid.EIDService";
		toRet.userIdInThisService = svcUserID;
		toRet.addSetOfClaims(new EIDSupportedClaims());
		toRet.xmlParameters = "<SPECIFIC></SPECIFIC>";
		return toRet;
	}
	
	public static Service createDriverLicenceService(String userId,String svcUserID){
		Service toRet = new Service();
		toRet.id = UUID.randomUUID().toString();
		toRet.m_strInterface = "com.awl.rd.fc2.data.connectors.services.driverlicence.DriverLicenceService";
		toRet.userIdInThisService = svcUserID;
		toRet.addSetOfClaims(new DriverLicenceSupportedClaims());
		toRet.xmlParameters = "<SPECIFIC></SPECIFIC>";
		return toRet;
	}
	
	public static Service createTelcosService(String userId,String svcUserID){
		Service toRet = new Service();
		toRet.id = UUID.randomUUID().toString();
		toRet.m_strInterface = "com.awl.rd.fc2.data.connectors.services.telcos.TelcosService";
		toRet.userIdInThisService = svcUserID;
		toRet.addSetOfClaims(new TelcosSupportedClaims());
		toRet.xmlParameters = "<SPECIFIC></SPECIFIC>";
		return toRet;
	}
	public static Service createRACService(String userId,String svcUserID){
		Service toRet = new Service();
		toRet.id = UUID.randomUUID().toString();
		toRet.m_strInterface = "com.awl.rd.fc2.data.connectors.services.rac.RACService";
		toRet.userIdInThisService = svcUserID;
		toRet.addSetOfClaims(new RACSupportedClaims());
		toRet.xmlParameters = "<SPECIFIC></SPECIFIC>";
		return toRet;
	}
	public String getId() {
		return id;
	}
	public String getM_strInterface() {
		return m_strInterface;
	}
	public Vector<String> getM_vecAttributes() {
		return m_vecAttributes;
	}
	public String getUserIdInThisService() {
		return userIdInThisService;
	}
	public String getXmlParameters() {
		return xmlParameters;
	}
}
