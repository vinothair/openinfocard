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

import com.awl.fc2.selector.exceptions.FC2Authentication_Exeception_AuthenticationFailed;
import com.awl.logger.Logger;
import com.awl.rd.applications.common.exceptions.APP_Exception_InternalError;
import com.awl.rd.applications.map.orchestror.IAPP_Orchestror_ExportedFunctions;
import com.utils.execeptions.XMLParser_Exception_NO_ATTRIBUTE;

/**
 * This class implements the behaviour of a Map Authentication, using Swing components concerning the user interactions
 * @see IMapAuthenticationUI
 * @author Maupin Mathieu
 *
 */
public class MapAuthenticationUI_Swing implements IMapAuthenticationUI{
	
	String userName = null;
	String ticket;
	
	static Logger log = new Logger(MapAuthenticationUI_Swing.class);
	/**
	 * Use our {@link Logger} to trace
	 * @param msg message to log at TRACE level
	 */
	static public void trace(Object msg){
		log.trace(msg);
	}
	
	
//	/**
//	 * The authentication is proceed as follow : <br/>
//	 * 1) if {@code userName} is null then we ask it, (use : {@link AirAppControler#sendQuestion(String)})<br/>
//	 * 2) create the orchestror that embeds the procedure ({@link com.awl.fc2.selector.userinterface.flex.map.ClientFlex}</br>
//	 * 3) create the object that discuss with the MAP server {@link  com.awl.fc2.selector.authentication.map.ClientPost}<br/>
//	 * 4) set the orchestor  <br/>
//	 * 4-- {@link com.awl.rd.applications.map.orchestror.Client#setOrchestror(IAPP_Orchestror_ExportedFunctions)<br/>
//	 * 5) run the authentication and try to get the ticket<br/>
//	 * 5-- {@link com.awl.rd.applications.map.orchestror.Client#run(String)}
//	 */
	@Override
	public void doAuthentication() throws FC2Authentication_Exeception_AuthenticationFailed  {
		System.out.println("DOAUTHENTICAYTION USENRAME = " + userName);		
		MainWindow.getInstance().traceConsole(Lang.get(Lang.RETRIEVING_ATTRIBUTE));
		if(userName == null){
			trace("---- MapAuthenticationUI_Swing.doAuthentication ----");	
			final Dialog_UserName jd = new Dialog_UserName();
			jd.settings("User name?",Lang.get(Lang.WHAT_IS_USERNAME));
			userName = jd.getResponse();
		}
			ClientSwing client = new ClientSwing();
			try {
				IAPP_Orchestror_ExportedFunctions map = new ClientPost();
				client.setOrchestror(map);
				ticket = client.run(userName);
			} catch (APP_Exception_InternalError e) {
				throw(new FC2Authentication_Exeception_AuthenticationFailed(e.getMessage()));
			} catch (XMLParser_Exception_NO_ATTRIBUTE e) {
				throw(new FC2Authentication_Exeception_AuthenticationFailed(e.getMessage()));
			}
		//}
	}

	/**
	 * 
	 * @return The MAP SAML Authentication ticket
	 * @throws FC2Authentication_Exeception_AuthenticationFailed if the authentication does not succeed
	 */
	@Override
	public String getTicket() throws FC2Authentication_Exeception_AuthenticationFailed {		
		if(ticket == null) throw new FC2Authentication_Exeception_AuthenticationFailed("No ticket found");
		
		return ticket;
	}


	/**
	 * @return the username
	 */
	@Override
	public String getUserName() {
		return userName;
	}

	
	/**
	 * set the username (optional)
	 * @param userName
	 */
	@Override
	public void setUserName(String userName) {
		if(userName == null) return;
		this.userName = userName;
	}

}