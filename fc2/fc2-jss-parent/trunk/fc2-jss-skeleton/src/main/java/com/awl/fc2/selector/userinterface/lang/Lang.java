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
package com.awl.fc2.selector.userinterface.lang;

import java.util.Locale;
import java.util.ResourceBundle;

public class Lang {

	public static String RETRIEVING_ATTRIBUTE="RETRIEVING_ATTRIBUTE";
	public static String WHAT_IS_USERNAME="WHAT_IS_USERNAME";
	public static String AUTHENTICATION_INPROGRESS="AUTHENTICATION_INPROGRESS";
	public static String DT_WE_TRANSMIT_THE_FOLLOWING="DT_WE_TRANSMIT_THE_FOLLOWING";
	public static String ASKPWD="ASKPWD";
	public static String ASK_USERNAME="ASK_USERNAME";
	
	public static String MENU_OPEN ="MENU_OPEN";
	public static String MENU_DIAGNOSTIC = "MENU_DIAGNOSTIC";
	public static String MENU_CLOSE = "MENU_CLOSE";
	public static String MENU_IMPORT = "MENU_IMPORT";
	public static String MENU_DELETE_CARDS ="MENU_DELETE_CARDS";
	public static String MENU_LOG_ON = "MENU_LOG_ON";
	public static String MENU_LOG_OFF = "MENU_LOG_OFF";
	public static String MENU_RESET_SSO_TOKEN = "MENU_RESET_SSO_TOKEN";
	
	public static String ALLREADY_LOG = "ALLREADY_LOG";
	public static String NEW_SESSION="NEW_SESSION";
	public static String NEGOCIATING_WITH_REMOTE_WALLET="NEGOCIATING_WITH_REMOTE_WALLET";
	public static String GET_CARD_I="GET_CARD_I";
	public static String CHECK_REMEMBER_ME ="CHECK_REMEMBER_ME";
	public static String CARD_AUTH_WITH="CARD_AUTH_WITH";
	
	
		
	static ResourceBundle resource = ResourceBundle.getBundle("com.awl.fc2.selector.userinterface.lang.jss_en_US");
	public static String get(String key){
		String response = resource.getString(key);
		if(response == null){
			return key;
		}
		return response;
	}
	
	public static void main(String arg[]){
		System.out.println(get(MENU_DIAGNOSTIC));
	}
}
