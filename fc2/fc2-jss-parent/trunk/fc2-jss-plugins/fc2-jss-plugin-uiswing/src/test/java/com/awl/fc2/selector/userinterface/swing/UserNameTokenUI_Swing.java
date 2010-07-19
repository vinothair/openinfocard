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
package com.awl.fc2.selector.userinterface.swing;


import com.awl.fc2.selector.userinterface.authentication.IUserNameTokenUI;
import com.awl.fc2.selector.userinterface.swing.lang.Lang;
import com.awl.logger.Logger;


public class UserNameTokenUI_Swing implements IUserNameTokenUI{

	String title = "Opening a session.", tempPWD="";
	boolean profileUse = false;
	
	static Logger log = new Logger(UserNameTokenUI_Swing.class);
	public static void trace(Object msg){
		log.trace(msg);
	}
	
	
	@Override
	public String getPWD() {
		String response;
		if (profileUse) {
			response = tempPWD;
			tempPWD = "";
			setProfileUse(false);
		} else {
			trace("question("+title+", "+Lang.get(Lang.ASKPWD)+")");
			final Dialog_Pwd jd = new Dialog_Pwd();
			jd.settings(title,Lang.get(Lang.ASKPWD));
			response = jd.getResponse();
		}
		return response;

	}

	@Override
	public String getUserName() {
		trace("question("+title+", "+Lang.get(Lang.ASK_USERNAME)+")");
		final Dialog_UserName jd = new Dialog_UserName();
		jd.settings(title,Lang.get(Lang.ASK_USERNAME));
		String response = jd.getResponse();
		return response;
	}

	public void setProfileUse(boolean newValue) {
		profileUse = newValue;
	}
	
	public void setTempPWD(String newValue) {
		tempPWD = newValue;
	}
}
