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
package com.awl.fc2.selector.authentication;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;

import test.TestCardCertificateBEID;

import com.awl.fc2.selector.exceptions.PKIHandler_Exeception;
import com.awl.logger.Logger;
import com.utils.Base64;
import com.utils.SUtil;

public class PKIHandler {
	static Logger log = new Logger(PKIHandler.class);
	public static void trace(Object msg){
		log.trace(msg);
	}
	String hashCertificate="";
	public PKIHandler(String hashCertificate) {
		try {
			ks = KeyStore.getInstance("Windows-MY");
			ks.load(null, null) ;
		} catch (KeyStoreException e) {
			trace("Impossible to load the KeyStore");
		} catch (NoSuchAlgorithmException e) {
			trace("Impossible to load the KeyStore");
		} catch (CertificateException e) {
			trace("Impossible to load the KeyStore");
		} catch (IOException e) {
			trace("Impossible to load the KeyStore");
		}		
		this.hashCertificate = hashCertificate;
		findCertificate();
	}
	
	
	
	
	boolean isBEID = false;
	String certB64=null;
	Certificate curCertificate=null;
	String curAlias= null;
	final static String BEID_B64 = "MIIEFTCCAv2gAwIBAgILAQAAAAABIYGgLpMwDQYJKoZIhvcNAQEFBQAwPDELMAkGA1UEBhMCQkUxHDAaBgNVBAMTE1NQRUNJTUVOIENpdGl6ZW4gQ0ExDzANBgNVBAUTBjIwMDUwMTAeFw0wOTA1MjcxMDMxNDdaFw0xMTA1MjcxMDMxNDdaMHgxCzAJBgNVBAYTAkJFMSkwJwYDVQQDEyBSb2JlcnQgU1BFQ0lNRU4gKEF1dGhlbnRpY2F0aW9uKTERMA8GA1UEBBMIU1BFQ0lNRU4xFTATBgNVBCoTDFJvYmVydCBCMzMyNTEUMBIGA1UEBRMLNzE3MTcxMDAwNTIwgZ8wDQYJKoZIhvcNAQEBBQADgY0AMIGJAoGBAIl7i9CpqVOONkU0yZPwUgMPo8/oLG2M0lpt6aaxINDEXrF/3OusEWFEx4AXQc8v4Ese4jQst3ejqZvrG9Maa+gSDwkx489OAbw2bzfowANyBZndrBqySVSH3W7RTVTQafnRDgKC//qJbGXqHTdXWNxCGMPHwiwslILxp37tVgaBAgMBAAGjggFeMIIBWjAfBgNVHSMEGDAWgBTWpf5lJr8obBYV1/p+Pdqfqe59HTB/BggrBgEFBQcBAQRzMHEwPgYIKwYBBQUHMAKGMmh0dHA6Ly9jZXJ0cy5zcGVjaW1lbi1laWQuYmVsZ2l1bS5iZS9iZWxnaXVtcnMuY3J0MC8GCCsGAQUFBzABhiNodHRwOi8vb2NzcC5zcGVjaW1lbi1laWQuYmVsZ2l1bS5iZTBPBgNVHSAESDBGMEQGCQOQDgcBAYMRATA3MDUGCCsGAQUFBwIBFilodHRwOi8vcmVwb3NpdG9yeS5zcGVjaW1lbi1laWQuYmVsZ2l1bS5iZTBCBgNVHR8EOzA5MDegNaAzhjFodHRwOi8vY3JsLnNwZWNpbWVuLWVpZC5iZWxnaXVtLmJlL2VpZGMyMDA1MDEuY3JsMA4GA1UdDwEB/wQEAwIHgDARBglghkgBhvhCAQEEBAMCBaAwDQYJKoZIhvcNAQEFBQADggEBAA7aij5v+b42/D+1SGmWa+MC8xIY20HklLI3YJHKpOx0d56ZQoHqTg339LlUIU2my/4LkG4E20UFdHyAShmKVNs5meb4VG/IAFY/GZekS3oNP3xpl+QgWtMeLJ+B1mTGO4K8KLp9VG2baSrWCXcWXgySL/ovzlJE0pwGWQFgdZKuK99QXGjsBmdY9rY+0AcRs+4F6T2oGwGfclyCAkW+w59PFyrVvp66DRalT97O+pWq60OWiNlOGywnpddqiAHtjPIkDaIByOm4ssbP2hiCN43FyS0wY67ZsgAk9Q+OAWdQ1i09QEVg6zrKT/y70IYnsshrlHIaij3+IIO9iIHYLIY=";
	public void findCertificate(){
		if("tgWxrZ44WQjXfH1+mhnIWCD0G6c=".equalsIgnoreCase(hashCertificate)){
			isBEID = true;
			certB64 = BEID_B64;
		}else{
			hashCertificate = SUtil.bytes2String(Base64.decode(hashCertificate));
			lookForCertificateInWindowsKeyStore();
		}
	}
	public String getCertB64() throws PKIHandler_Exeception {
		if(isBEID){
			return certB64; 
		}else{
			if(curCertificate !=null){
				try {
					return Base64.encode(curCertificate.getEncoded());
				} catch (CertificateEncodingException e) {
					throw new PKIHandler_Exeception(e.getMessage());
				}
			}
		}
		throw new PKIHandler_Exeception("No certificate found");
	}
	public byte [] signData(byte [] data) throws PKIHandler_Exeception{
		if(isBEID){
			return TestCardCertificateBEID.getSignatureFROMBEID(new String(data));			
		}
		trace("    Certificat : " + curCertificate.toString() ) ;
		PrivateKey key;
		try {
			key = (PrivateKey)ks.getKey(curAlias,null);//"0000".toCharArray());
			//SUtil.printBufferBytes(key.getEncoded());
			//Certificate[] chain = ks.getCertificateChain(aliasKey);
			trace("Signature initialization");
			Signature sign = Signature.getInstance("SHA1withRSA");
			sign.initSign(key);		
			sign.update(data);
	      
	      byte [] res = sign.sign();
	      return res;
		} catch (UnrecoverableKeyException e) {
			throw new PKIHandler_Exeception(e.getMessage());
		} catch (KeyStoreException e) {
			throw new PKIHandler_Exeception(e.getMessage());
		} catch (NoSuchAlgorithmException e) {
			throw new PKIHandler_Exeception(e.getMessage());
		} catch (SignatureException e) {
			throw new PKIHandler_Exeception(e.getMessage());
		} catch (InvalidKeyException e) {
			throw new PKIHandler_Exeception(e.getMessage());
		}
		
		
	}
//	/**
//	 * @param args
//	 */
//	public static void main(String[] args) {
//		// TODO Auto-generated method stub
//		String lookFor = "V25rjZKS8s/eNsP9LQr61cPkMTE=";
//		PKIHandler pki = new PKIHandler(lookFor);
//		
//	}

	KeyStore ks = null; 
//	static{		

	public void lookForCertificateInWindowsKeyStore(){
		try 
		{
		int cpt=0;
		
		System.out.println(""+cpt);cpt++;
		java.util.Enumeration<?> en = ks.aliases() ;
		
		System.out.println(""+cpt);cpt++;
		while (en.hasMoreElements()) {
			trace(""+cpt);cpt++;
			String aliasKey = (String)en.nextElement() ;
			Certificate c = ks.getCertificate(aliasKey) ;
			trace("---> alias : " + aliasKey) ;
			trace("BEGIN");
			//trace(Base64.encode(c.getEncoded()));
			MessageDigest hash = MessageDigest.getInstance("SHA1");
			String curHash = SUtil.bytes2String(hash.digest(c.getEncoded()));
			trace("Looking for    : " + hashCertificate);
			trace("Comparing with : " + curHash);
			trace("END");

			if(hashCertificate.equalsIgnoreCase(curHash)){
				trace("FIND IT");
				curCertificate = c;
				curAlias = aliasKey;
				return;
			}
			
//			if(aliasKey.startsWith("AUTH_CLIENT")){
//				System.out.println("    Certificat : " + c.toString() ) ;
//				 
//				
//				      PrivateKey key = (PrivateKey)ks.getKey(aliasKey, "0000".toCharArray());
//				      SUtil.printBufferBytes(key.getEncoded());
//				      Certificate[] chain = ks.getCertificateChain(aliasKey);
//				      System.out.println("Signature initialization");
//				      Signature sign = Signature.getInstance("SHA1withRSA");
//				      sign.initSign(key);
//				      String toSigned = "DataToBeSigned";
//				      sign.update(toSigned.getBytes());
//				      
//				      byte [] res = sign.sign();
//				      for(int i=0;i<res.length;i++){
//				    	  System.out.println(" - " + (int)res[i]);
//				    	  
//				      }
//				      
//				      
//				
//			}
			
		}
	 
	} catch (Exception ioe) {
		System.err.println(ioe.getMessage());
	}
	trace("NOT FOUND");
	}


}
