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

import com.atosworldline.rd.payment.sdd.webservices.FillNSignMandate;

public class TestSDD {
	static Logger log= Logger.getLogger(TestSDD.class);
	public static void trace(Object message){
		log.info(message);
	}
static public String tmplSDD = "<document>"+
 "   <EMndtReqMsgIssg>"+
 "       <GrpHdr>"+
 "           <MsgId>1</MsgId>"+
 "           <CreDtTm>2009-07-13T10:22:50.000+00:15</CreDtTm>"+
 "       </GrpHdr>"+
 "       <Mndt>"+
 "           <MndtId>00XX11YY22ZZ33</MndtId>"+
 "           <PmtTpInf>"+
 "               <SvcLvl>"+
 "                   <Cd>SEPA</Cd>"+
 "               </SvcLvl>"+
 "               <LclInstrm>"+
 "                   <Cd>CORE</Cd>"+
 "               </LclInstrm>"+
 "           </PmtTpInf>"+
 "           <Occurcs>"+
 "               <SeqTp>OOFF</SeqTp>"+
 "               <Frqcy>DAIL</Frqcy>"+
 "           </Occurcs>"+
 "           <CdtrSchmeId>"+
 "               <Id>"+
 "                   <PrvtId>"+
 "                       <Othr>"+
 "                           <SchmeNm>"+
 "                               <Cd>SEPA</Cd>"+
 "                           </SchmeNm>"+
 "                       </Othr>"+
 "                   </PrvtId>"+
 "               </Id>"+
 "           </CdtrSchmeId>"+
 "           <Cdtr>"+
 "               <Nm>Pizz'Atos</Nm>"+
 "               <PstlAdr>"+
 "                   <AdrTp>BIZZ</AdrTp>"+
 "                   <Ctry>FR</Ctry>"+
 "                   <AdrLine>rue de la pointe, Seclin</AdrLine>"+
 "               </PstlAdr>"+
 "           </Cdtr>"+
 "           <UltmtCdtr>"+
 "               <Nm>Pizz'Atos</Nm>"+
 "               <Id>"+
 "                   <OrgId>"+
 "                       <BICOrBEI>PizAtLille</BICOrBEI>"+
 "                   </OrgId>"+
 "               </Id>"+
 "           </UltmtCdtr>"+
 "           <Dbtr>"+
 "               <Nm>1 a</Nm>"+
 "               <PstlAdr>"+
 "                   <AdrTp>HOME</AdrTp>"+
 "                   <Ctry>FR</Ctry>"+
 "                   <AdrLine>1 1 1 1</AdrLine>"+
 "               </PstlAdr>"+
 "           </Dbtr>"+
 "           <DbtrAgt>"+
 "               <FinInstnId>"+
 "                   <BIC>1</BIC>"+
 "               </FinInstnId>"+
 "           </DbtrAgt>"+
 "           <UltmtDbtr>"+
 "               <Nm>a 1</Nm>"+
 "               <Id>"+
 "                   <PrvtId>"+
 "                       <DtAndPlcOfBirth>"+
 "                           <BirthDt>1991-01-01T00:00:00.000+01:00</BirthDt>"+
 "                           <PrvcOfBirth>1</PrvcOfBirth>"+
 "                           <CityOfBirth>1</CityOfBirth>"+
 "                           <CtryOfBirth>FR</CtryOfBirth>"+
 "                       </DtAndPlcOfBirth>"+
 "                   </PrvtId>"+
 "               </Id>"+
 "           </UltmtDbtr>"+
 "           <RfrdDocInf>"+
 "               <Nb>123456789</Nb>"+
 "           </RfrdDocInf>"+
 "       </Mndt>"+
 "   </EMndtReqMsgIssg>"+
"</document>";
	public static void main(String arg[]){
		//String host = "10.26.52.12";
		String host = "proxy";
		String port = "3128";
		System.setProperty("http.proxyHost", host);
//		
		System.setProperty("http.proxyPort", port);
//		System.setProperty("socksProxyHost", host);
//		System.setProperty("socksProxyPort", port);
//		System.setProperty("https.proxyHost", host);
//		System.setProperty("https.proxyPort", port);
//		System.setProperty("socksProxySet", "true");
//		System.setProperty("http.proxySet","true");
//		System.setProperty("https.proxySet", "true");

		//FillNSignMandate svc = FactoryWSSDD.createInstance("http://10.24.238.105:8080/ValidationService/validation");
		
		//FillNSignMandate svc = FactoryWSSDD.createInstance("http://rd-srv-demo.priv.atos.fr:8080/ValidationService/validation");
		FillNSignMandate svc = FactoryWSSDD.createInstance("http://localhost:8080/ValidationService/validation");
		String signedMandate = svc.signMandate("12345", tmplSDD);

//		FillNSignMandate svc = FactoryWSSDD.createInstance("http://10.24.238.105:8080/ValidationService/validation");
//		
//		String signedMandate = svc.signMandate("987654", tmplSDD);
//		String signedMandate = getMandate("987654", tmplSDD);

		trace(signedMandate);
		
		
	}
	
	public static String getMandate(String userId,String template){
		FillNSignMandate svc = FactoryWSSDD.createInstance("http://rd-srv-demo.priv.atos.fr:8080/ValidationService/validation");//"http://10.24.238.105:8080/endpoint.cxf/cxf/validation");
		String signedMandate = svc.signMandate("987654", tmplSDD);
		trace(signedMandate);
		return signedMandate;
	}
}
