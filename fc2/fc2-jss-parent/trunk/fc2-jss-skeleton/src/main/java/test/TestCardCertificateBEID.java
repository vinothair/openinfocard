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

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Vector;

import com.awl.logger.Logger;
import com.awl.rd.smartcard.CReaders_JRE_Embedded;
import com.awl.rd.smartcard.IAPDU_Bridge;
import com.awl.rd.smartcard.exception.SmartCard_Exception_NO_CARD;
import com.awl.rd.smartcard.exception.SmartCard_Exception_NO_READER;
import com.utils.SUtil;

public class TestCardCertificateBEID {
	public static Logger log = new Logger(TestCardCertificateBEID.class);
	public static void trace(Object msg){
		log.trace(msg);
	}
	public static byte [] getSignatureFROMBEID(String challenge){
		CReaders_JRE_Embedded reader = new CReaders_JRE_Embedded();
		Vector<String> lstReaders=null;
		try {
			lstReaders = reader.getVectorOfAccessibleReaders();
			reader.selectTerminal(lstReaders.get(0));
			try {
				IAPDU_Bridge apdu = reader.createCompatibleAPDUBridge(true);
				
				try {
					apdu.sendAPDUString("002241B6050480028482");
					verifyPIN(apdu, "1234");
					
					return signChallenge(apdu, challenge);
				} catch (NoSuchAlgorithmException e) {
					trace("NIMP");
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			} catch (SmartCard_Exception_NO_CARD e) {
				trace("No card");
			}
			
		} catch (SmartCard_Exception_NO_READER e) {
			trace("No Reader");
		}
		return null;
	}
	public static void main(String arg[]) throws NoSuchAlgorithmException{
		getSignatureFROMBEID("cococo");
//		String certif = "MIIEFTCCAv2gAwIBAgILAQAAAAABIYGgLpMwDQYJKoZIhvcNAQEFBQAwPDELMAkGA1UEBhMCQkUxHDAaBgNVBAMTE1NQRUNJTUVOIENpdGl6ZW4gQ0ExDzANBgNVBAUTBjIwMDUwMTAeFw0wOTA1MjcxMDMxNDdaFw0xMTA1MjcxMDMxNDdaMHgxCzAJBgNVBAYTAkJFMSkwJwYDVQQDEyBSb2JlcnQgU1BFQ0lNRU4gKEF1dGhlbnRpY2F0aW9uKTERMA8GA1UEBBMIU1BFQ0lNRU4xFTATBgNVBCoTDFJvYmVydCBCMzMyNTEUMBIGA1UEBRMLNzE3MTcxMDAwNTIwgZ8wDQYJKoZIhvcNAQEBBQADgY0AMIGJAoGBAIl7i9CpqVOONkU0yZPwUgMPo8/oLG2M0lpt6aaxINDEXrF/3OusEWFEx4AXQc8v4Ese4jQst3ejqZvrG9Maa+gSDwkx489OAbw2bzfowANyBZndrBqySVSH3W7RTVTQafnRDgKC//qJbGXqHTdXWNxCGMPHwiwslILxp37tVgaBAgMBAAGjggFeMIIBWjAfBgNVHSMEGDAWgBTWpf5lJr8obBYV1/p+Pdqfqe59HTB/BggrBgEFBQcBAQRzMHEwPgYIKwYBBQUHMAKGMmh0dHA6Ly9jZXJ0cy5zcGVjaW1lbi1laWQuYmVsZ2l1bS5iZS9iZWxnaXVtcnMuY3J0MC8GCCsGAQUFBzABhiNodHRwOi8vb2NzcC5zcGVjaW1lbi1laWQuYmVsZ2l1bS5iZTBPBgNVHSAESDBGMEQGCQOQDgcBAYMRATA3MDUGCCsGAQUFBwIBFilodHRwOi8vcmVwb3NpdG9yeS5zcGVjaW1lbi1laWQuYmVsZ2l1bS5iZTBCBgNVHR8EOzA5MDegNaAzhjFodHRwOi8vY3JsLnNwZWNpbWVuLWVpZC5iZWxnaXVtLmJlL2VpZGMyMDA1MDEuY3JsMA4GA1UdDwEB/wQEAwIHgDARBglghkgBhvhCAQEEBAMCBaAwDQYJKoZIhvcNAQEFBQADggEBAA7aij5v+b42/D+1SGmWa+MC8xIY20HklLI3YJHKpOx0d56ZQoHqTg339LlUIU2my/4LkG4E20UFdHyAShmKVNs5meb4VG/IAFY/GZekS3oNP3xpl+QgWtMeLJ+B1mTGO4K8KLp9VG2baSrWCXcWXgySL/ovzlJE0pwGWQFgdZKuK99QXGjsBmdY9rY+0AcRs+4F6T2oGwGfclyCAkW+w59PFyrVvp66DRalT97O+pWq60OWiNlOGywnpddqiAHtjPIkDaIByOm4ssbP2hiCN43FyS0wY67ZsgAk9Q+OAWdQ1i09QEVg6zrKT/y70IYnsshrlHIaij3+IIO9iIHYLIY=";
//		MessageDigest hash = MessageDigest.getInstance("SHA1");
//		hash.reset();
//		byte [] res = hash.digest(certif.getBytes());
//		System.out.println(Base64.encode(res));
//		//getSignatureFROMBEID("CHALLENGE");
//		byte decode []= Base64.decode("tgWxrZ44WQjXfH1+mhnIWCD0G6c=");
//		System.out.println(SUtil.bytes2String(decode));
		
		
		
	}

	public static byte [] signChallenge(IAPDU_Bridge apdu,String inputchallenge) throws NoSuchAlgorithmException{
		String challenge = inputchallenge;
		byte [] data = challenge.getBytes();
//		apdu.sendAPDUString("002241B6050480028482");
//		apdu.transmitControlCommand(3224864, new byte[0]);
//		apdu.transmitControlCommand(16606712, SUtil.string2Bytes("0A008947040C0402000A0C000000000D00000000200001082FFFFFFFFFFFFFFF"));
		String hash ="";
		{
			MessageDigest md = MessageDigest.getInstance("SHA1");
			md.update(data);
			hash = SUtil.bytes2String(md.digest());
			hash = SUtil.bytes2String(new byte[]{(byte) (hash.length()/2)})+hash;
		}
		//System.out.println("Should  send 002A9E9A14E9166B5B8A91B29A913F6295A07B4CD96AEC9D2F");
		//							     002A9E9A  E9166B5B8A91B29A913F6295A07B4CD96AEC9D2F
		try {
			apdu.sendAPDUString("002A9E9A"+hash);
		} catch (Exception e) {
			trace("Pb in sending the apdu");
		}
		//return SUtil.bytes2String(apdu.getResData());
		byte sw[] = apdu.getResStatus();
		if(sw[0]==(byte)0x90 && sw[1] == 0x00){
			trace("We get the Signature");
			byte [] signature = apdu.getResData();
			return signature;
		}else{
			trace("finished");
			return null;
		}
	}
	public static boolean verifyPIN(IAPDU_Bridge apdu,String code) {
		// Code should have even number of characters
		String evenLengthCode = code;
		if (2 * ((int) (evenLengthCode.length() / 2)) != evenLengthCode
				.length()) {
			evenLengthCode = evenLengthCode + "F";
		}

		// Handle the case when no connection has yet been made
//		if (!isConnected()) {
//			throw new CardNotFoundException(
//					CardNotFoundException.CardNotFoundType.NOT_CONNECTED);
//		}

		// Write protect the verification of the PIN
//		this.beginTransaction();

		// Insert PIN in APDU field
		byte[] pin = new byte[] { (byte) 0x2F, (byte) 0xFF, (byte) 0xFF,
				(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF };
		pin[0] = (byte) (2 * 16 + evenLengthCode.length());
		for (int i = 0; i < evenLengthCode.length(); i += 2) {
			pin[(i / 2) + 1] = (byte) (Integer.parseInt(evenLengthCode
					.substring(i, i + 2), 16));
		}

		// Send command
		apdu.SendAPDU(0x00,
				      0x20,
				      0x00,
				      0x01,
				      pin.length,
				      pin,
				      pin.length,
				      0,
				      -1);
//		ResponseAPDU rAPDU = this.transmitAPDU(new CommandAPDU(0x00, 0x20,
//				0x00, 0x01 /* hardcoded reference */, pin));
		
		// End lock
//		this.endTransaction();

		// Check whether correct
		if ((apdu.getResStatus()[0] == 0x90) && (apdu.getResStatus()[1]== 0x00)) {
			trace("PIN OK");
			return true;
		} else{
			trace("PIN KO");
		}
		return false;
	}
}
