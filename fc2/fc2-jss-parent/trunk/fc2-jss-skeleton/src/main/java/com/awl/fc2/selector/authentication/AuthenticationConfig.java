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

import java.util.Vector;

import org.xmldap.infocard.InfoCard;

//import com.awl.fc2.selector.session.SessionSelector;

/**
 * This class embeds all the data needed to perform an Claim request to an STS. <br/>
 * - The url for doing the MetaData Exchange <br/>
 * - The URI of the authentication methods <br/>
 * - The vector of required Claims <br/>
 * - The vector of optional Claims - not yet handle <br/>
 * - The url of the requestor (RP) <br/>
 * - The certificate in Base64 of the requestor (RP) <br/>
 * - The Infocard <br/>
 * @author Cauchie stephane
 *
 */
public class AuthenticationConfig {
	public String urlMEX;
	public String uriAuthentication;
	public Vector<String> requiredClaims;
	public Vector<String> optionalClaims;
	public String urlRequestor;
	public String certifRequestor;
	public InfoCard card;
	public String userName;
	/*SessionSelector theSession;*/
	
	
	
	/**
	 * Set the parameters requested by the RP.
	 * @param rClaims the vector of required claims (string)
	 * @param oClaims the vector of optional claims (String)
	 * @param urlReq the url of the RP 
	 * @param certif the certificate of the RP
	 */
	public void setQuery(Vector<String> rClaims,
						 Vector<String> oClaims,
						 String urlReq,
						 String certif/*,
						 SessionSelector theSession*/){
		requiredClaims = rClaims;
		optionalClaims = oClaims;
		urlRequestor = urlReq;
		certifRequestor = certif;
		/*this.theSession = theSession;*/
	
	}
	
	
	/**
	 * This object should be constructed when the user has selected the infocard and the authentication method
	 * @param userName  the username
	 * @param choosenCard the {@link InfoCard} 
	 * @param mex the URL where to perform the MetaData exchange
	 * @param auth the URI of the choosen authentication method
	 */
	public AuthenticationConfig(String userName,InfoCard choosenCard,String mex,String auth) {
		urlMEX = mex;
		uriAuthentication = auth;
		card = choosenCard;
		this.userName = userName;
	
	}
	
	/**
	 * Returns a string representation of an Authentication Config.
	 */
	public String toString(){
		return "AuthenticationConfig(meth:"+uriAuthentication+"mex:"+urlMEX+")";
	}
}
