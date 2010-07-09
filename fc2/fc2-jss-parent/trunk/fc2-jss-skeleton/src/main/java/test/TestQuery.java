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
package test;

import java.util.Vector;

import com.awl.fc2.selector.Selector;
import com.awl.fc2.selector.exceptions.Config_Exception_NotDone;
import com.awl.fc2.selector.exceptions.Config_Exeception_MalFormedConfigFile;
import com.awl.fc2.selector.exceptions.Config_Exeception_UnableToReadConfigFile;
import com.awl.fc2.selector.launcher.Config;
//import com.awl.id.selector.TestAuthentication;

public class TestQuery {
	public static String certifRPB64="MIIFhTCCBG2gAwIBAgIIID4hfF0RrEswDQYJKoZIhvcNAQEFBQAwXDEiMCAGA1UEAwwZRkMyIHN1"+
	"YkFDIGJhbmNhaXJlIFNlcnZlcjENMAsGA1UECwwEdGVzdDEaMBgGA1UECgwRZmMyY29uc29ydGl1"+
	"bS5vcmcxCzAJBgNVBAYTAkZSMB4XDTA5MDQxNTE2MDkxNFoXDTEwMTEwNjE2MDkxNFowejE7MDkG"+
	"A1UEAwwyc2lwcy5hdG9zd29ybGRsaW5lLmJhbmNhaXJlLnRlc3QuZmMyY29uc29ydGl1bS5vcmcx"+
	"FjAUBgNVBAsMDWF0b3N3b3JsZGxpbmUxFjAUBgNVBAoMDWZjMmNvbnNvcnRpdW0xCzAJBgNVBAYT"+
	"AkZSMIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCCaZxuQbYNH0/j8IfXYjqtSfsWsDVyQE1b"+
	"wOkUY2Jup/M5a7hrB5D5fUDIKDyIQXwsgs8zTwCBgLKXqoer0V25tMU95KWG8zivIBOe1drp1POc"+
	"AIpbwDMZlondXbV8UMgeTNw7YklTZ8ULNic4tB8KIpLxBrcrQ0JHsBkDdnHjVwIDAQABo4ICrzCC"+
	"AqswgbgGCCsGAQUFBwEBBIGrMIGoME0GCCsGAQUFBzAChkEvaG9tZS9mYzIvY2VydGlmaWNhdHMv"+
	"ZmMyL3N1YkFDL0ZDMnN1YkFDYmFuY2FpcmVTZXJ2ZXIuY2FjZXJ0LnBlbTBXBggrBgEFBQcwAYZL"+
	"aHR0cDovL2FjLmRzLmNvbW11bi50ZXN0LmZjMmNvbnNvcnRpdW0ub3JnOjgwODAvZWpiY2EvcHVi"+
	"bGljd2ViL3N0YXR1cy9vY3NwMB0GA1UdDgQWBBRhtNsdbXK/Q8RdgmK01A5zHpYYjjAMBgNVHRMB"+
	"Af8EAjAAMB8GA1UdIwQYMBaAFBbVz0SMhSviHXbhilO74KsZsRMQMIIBHAYDVR0fBIIBEzCCAQ8w"+
	"ggELoIGmoIGjhoGgaHR0cDovL2FjLmRzLmNvbW11bi50ZXN0LmZjMmNvbnNvcnRpdW0ub3JnOjgw"+
	"ODAvZWpiY2EvcHVibGljd2ViL3dlYmRpc3QvY2VydGRpc3Q/Y21kPWNybCZpc3N1ZXI9Q049RkMy"+
	"IHN1YkFDIGJhbmNhaXJlIFNlcnZlciwgTz1mYzJjb25zb3J0aXVtLm9yZywgT1U9dGVzdCwgQz1G"+
	"UqJgpF4wXDEiMCAGA1UEAwwZRkMyIHN1YkFDIGJhbmNhaXJlIFNlcnZlcjEaMBgGA1UECgwRZmMy"+
	"Y29uc29ydGl1bS5vcmcxDTALBgNVBAsMBHRlc3QxCzAJBgNVBAYTAkZSMA4GA1UdDwEB/wQEAwIE"+
	"8DAxBgNVHSUEKjAoBggrBgEFBQcDAQYIKwYBBQUHAwMGCCsGAQUFBwMEBggrBgEFBQcDCDA9BgNV"+
	"HREENjA0gjJzaXBzLmF0b3N3b3JsZGxpbmUuYmFuY2FpcmUudGVzdC5mYzJjb25zb3J0aXVtLm9y"+
	"ZzANBgkqhkiG9w0BAQUFAAOCAQEAJyd19ufbDgi4yh/vClvtgoHa3mbXTXUWs3Dz62xgYgYiOZBa"+
	"gSFjJHw74/snCjdMBe3/R0z89sLgBQEvlfOJkuDYyJZ/vodeERRtdIcLshlLUCg85w2XUqjTt40W"+
	"iCPffCPgSbFP33IMniGoGT6NwnfEtICgtKn2CgvnQSrPMeOzenelmuCp3s1zcKwIQFLr0JDzehgZ"+
	"STYzoRL6irnql/9oxoIteYELSY4JB7hDq+6CugLHiNm5OQ9aaGqEx6AaJV+67BG1qrOFrUyQPwQ0"+
	"iHGvMroaHdBasE0wC7JbdKvwTCpTL98E6l7hMn+Cwgf9IVrRP2ISIskIDVAdOwQHbg==";
	/**
	 * @param args
	 * @throws Config_Exception_NotDone 
	 * @throws Config_Exeception_UnableToReadConfigFile 
	 */
	public static void main(String[] args) throws Config_Exception_NotDone, Config_Exeception_UnableToReadConfigFile {
		
		Config.getInstance("C:/tempp/cards/Config_Selecteur.xml",true);
		Selector selector;
		try {
			
				selector =Selector.getInstance();
				selector.openSession();
				System.out.println("Press a key");
			//	System.in.read();
			// Emulate the onQueryClaims()
			
			// The required Claims
			
			/* String requiredClaims[] = {            "http://www.fc2consortium.org/ws/2008/10/identity/claims/paymentcardnumber",
							"								http://www.fc2consortium.org/ws/2008/10/identity/claims/paymentcardverification",
							"								http://www.fc2consortium.org/ws/2008/10/identity/claims/paymentcardexpdatemonth",
							"								http://schemas.xmlsoap.org/ws/2005/05/identity/claims/privatepersonalidentifier",
							"								http://www.fc2consortium.org/ws/2008/10/identity/claims/paymentcardexpdateyear"
							};*/
			
			String requiredClaims[]={//"http://www.fc2consortium.org/ws/2008/10/identity/claims/civility", 
//					"http://schemas.xmlsoap.org/ws/2005/05/identity/claims/givenname",
					"http://schemas.xmlsoap.org/ws/2005/05/identity/claims/surname",
//					"http://schemas.xmlsoap.org/ws/2005/05/identity/claims/dateofbirth",
//					"https://idp.ds.interieur.test.fc2consortium.org/ws/2005/05/identity/claims/qualite",
					
					//PAYMENT
//					"http://www.fc2consortium.org/ws/2008/10/identity/claims/paymentcardnumber",
//					"http://www.fc2consortium.org/ws/2008/10/identity/claims/paymentcardverification",					
//					"http://www.fc2consortium.org/ws/2008/10/identity/claims/payment-amount?123",
					//eID
//					"http://www.fc2consortium.org/ws/2008/10/identity/claims/cnienumber",
//					"http://www.fc2consortium.org/ws/2008/10/identity/claims/civility",
//					"http://schemas.xmlsoap.org/ws/2005/05/identity/claims/givenname",
//					"http://schemas.xmlsoap.org/ws/2005/05/identity/claims/surname",
//					"http://schemas.xmlsoap.org/ws/2005/05/identity/claims/dateofbirth",
//					//Oragne
//					"http://schemas.xmlsoap.org/ws/2005/05/identity/claims/emailaddress",
//					"http://schemas.xmlsoap.org/ws/2005/05/identity/claims/homephone",
//					"http://schemas.xmlsoap.org/ws/2005/05/identity/claims/streetaddress",
//					"http://schemas.xmlsoap.org/ws/2005/05/identity/claims/postalcode",
//					"http://schemas.xmlsoap.org/ws/2005/05/identity/claims/locality",
//					"http://schemas.xmlsoap.org/ws/2005/05/identity/claims/country",
//					//PERMIS
//					"http://www.fc2consortium.org/ws/2008/10/identity/claims/drivinglicencenumber",
//					"http://www.fc2consortium.org/ws/2008/10/identity/claims/drivinglicenceissuingdate"
					
					
			
			};
			 
			 String urlRequestor = "https://sips.atosworldline.bancaire.test.fc2consortium.org/callpayment";
			 String certifRequestor = certifRPB64;
			 
			 Vector<String> lstRequiredClaims = new Vector<String>();
			 Vector<String> lstOptionalClaims = new Vector<String>();
			 
			 for(int i=0;i<requiredClaims.length;i++){
				 lstRequiredClaims.add(requiredClaims[i].trim());
			 }
			 
			 selector.onQueryClaims(lstRequiredClaims, lstOptionalClaims, urlRequestor, certifRequestor);
			System.out.println("Sortie");
			
		} catch (Config_Exeception_UnableToReadConfigFile e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Config_Exeception_MalFormedConfigFile e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}/* catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		
		
		
		
		

	}
}
