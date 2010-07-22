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
package com.awl.fc2.selector.storage.utils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Vector;

import nu.xom.Element;
import nu.xom.ParsingException;
import nu.xom.ValidityException;

import org.apache.derby.impl.sql.execute.CardinalityCounter;
import org.xmldap.crypto.CryptoUtils;
import org.xmldap.exceptions.CryptoException;
import org.xmldap.exceptions.InfoCardProcessingException;
import org.xmldap.infocard.InfoCard;
import org.xmldap.infocard.SignedInfoCard;
import org.xmldap.rp.Token;
import org.xmldap.util.XmlFileUtil;

import com.awl.fc2.selector.authentication.AuthenticationConfig;
import com.awl.fc2.selector.authentication.FC2Authentication;
import com.awl.fc2.selector.exceptions.Config_Exception_NotDone;
import com.awl.fc2.selector.exceptions.FC2Authentication_Exeception_AuthenticationFailed;
import com.awl.fc2.selector.launcher.Config;
import com.awl.fc2.selector.session.SessionSelector;
import com.awl.logger.Logger;
import com.awl.rd.fc2.claims.CardsSupportedClaims;
import com.utils.Base64;

public class Utils {
	static String urlSTSWallet=null;
	static CardsSupportedClaims walletClaims = null;
	static String carIDPrefix = "https://ip-bancaire.atosworldline.bancaire.test.fc2consortium.org/sts-wallet/card/";//"http://ip-bancaire.atosworldline.bancaire.test.fc2consortium.org/sts-wallet/";
	static public String getWalletPrefixCardId(){
		return carIDPrefix;
	}
	//static String carIDPrefix = "https://localhost:8080/sts/card/";//"http://ip-bancaire.atosworldline.bancaire.test.fc2consortium.org/sts-wallet/";
	static {
		urlSTSWallet = "http://ip-bancaire.atosworldline.bancaire.test.fc2consortium.org/sts-wallet/mex/UserNamePasswordAuthenticate";
		//urlSTSWallet = "http://localhost:8080/sts/mex/UserNamePasswordAuthenticate";
	}
	static Logger log = new Logger(Utils.class);
	static public void trace(Object obj){
		log.trace(obj);
	}
	@SuppressWarnings("unchecked")
	public static Map<String,String> getSTSResponse(SessionSelector session,
			String username,Vector<String> lstRequiredClaims) throws FC2Authentication_Exeception_AuthenticationFailed{
		
		Vector<String> lstOptionalClaims = new Vector<String>();
		String urlRequestor = "localhost";
		String certifRequestorB64 = "";
		
		AuthenticationConfig choosenMethod = new AuthenticationConfig(username, null, urlSTSWallet, "UserNamePasswordAuthenticate");//selectorUI.onChooseAuthenticationConfig(l_vecConfigAuthentication);		
		choosenMethod.setQuery(lstRequiredClaims, lstOptionalClaims, urlRequestor, certifRequestorB64);

		FC2Authentication authentication = new FC2Authentication(choosenMethod);
		
			try {
				authentication.doAuthentication(carIDPrefix+getCardIDFromUserId(username),session);
			} catch (CryptoException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Config_Exception_NotDone e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			String stsTicket = authentication.getTicket();
			trace("STSTicket : " + stsTicket);
			Token token;
			try {
				token = new Token(stsTicket, null);
				Map<String,String> claims = token.getClaims();
				return claims;
			} catch (InfoCardProcessingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		
		throw(new FC2Authentication_Exeception_AuthenticationFailed("Utils"));
	}
	public static String getCardIDFromUserId(String userID) throws CryptoException{
		return Base64.encode(CryptoUtils.byteDigest(userID.getBytes(),Config.getDigestMethod()));
	}
	public static String [] String2Tab(String l_str){
		l_str = l_str.replace("[", "");
		l_str = l_str.replace("]", "");
		return l_str.split(",");
	}
	public static InfoCard CRDB64ToInfocar(String B64){
		String toRet = new String(Base64.decode(B64));//.toString();
		return String2InfoCard(toRet);
		
	}
	
	public static InfoCard String2InfoCard(String xml){
		try {
			Element root;
			root = XmlFileUtil.readXml(new ByteArrayInputStream(xml.getBytes())).getRootElement();
			SignedInfoCard card = new SignedInfoCard(root);			
			return card;
			
		} catch (ValidityException e) {
			trace("ValidityException");
		} catch (IOException e) {
			trace("IOException");
		} catch (ParsingException e) {
			trace("ParsingException");
		} catch (org.xmldap.exceptions.ParsingException e) {
			trace("org.xmldap.exceptions.ParsingException");
		}
		return null;
	}
}
