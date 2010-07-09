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
package com.awl.fc2.selector.storage;

import com.awl.fc2.selector.Selector;
import com.awl.fc2.selector.exceptions.Config_Exception_NotDone;
import com.awl.fc2.selector.exceptions.FC2Authentication_Exeception_AuthenticationFailed;
import com.awl.fc2.selector.launcher.Config;
import com.awl.fc2.selector.session.ISessionElement;
import com.awl.ws.messages.authentication.IToken;

public interface ICredentialsStore extends ISessionElement{
	/**
	 * Create the {@link IToken} object that will be embeded in the RST.<br/>
	 * In the case of a "SSOToken", we look in the hashtable if we have a compatible token.<br/>
	 * compatible means for the good sts and with a good timestamp (the last one is not yet done)<br/>
	 * --------------<br/>
	 * If UserNameToken is selected, then this method call :<br/>
	 * {@link Selector#getUI()}<br/>
	 * {@link com.awl.fc2.selector.userinterface.ISelectorUI#getUserNameTokenUI()}<br/> 
	 * {@link IUserNameTokenUI#getUserName()}  if the username is null<br/> 
	 * {@link IUserNameTokenUI#getPWD()} <br/>
	 * --------------<br/>
	 * if SSOToken is selected, then this method call :<br/>
	 * {@link Selector#getUI()}<br/>
	 * {@link com.awl.fc2.selector.userinterface.ISelectorUI#getMapUI()}<br/> 
	 * {@link com.awl.fc2.selector.userinterface.authentication.IMapAuthenticationUI#setUserName(String)}<br/> 
	 * {@link com.awl.fc2.selector.userinterface.authentication.IMapAuthenticationUI#doAuthentication()}<br/> 
	 * {@link com.awl.fc2.selector.userinterface.authentication.IMapAuthenticationUI#getUserName()}<br/> 
	 * {@link com.awl.fc2.selector.userinterface.authentication.IMapAuthenticationUI#getTicket()}<br/> 
	 * @param supportedToken the requested token : SSOToken or UsernameToken<br/> 
	 * @param username the username (null if not present)
	 * @param param (extra parameters, not yet used, but can embed the Trust link between STS)
	 * @param theSTS the STS url
	 * @return {@link IToken}
	 * @throws Config_Exception_NotDone if the Config is not initialized (call {@link Config#getInstance(String)}
	 * @throws FC2Authentication_Exeception_AuthenticationFailed
	 */
	//public IToken createToken(String cardId,String supportedToken,String username,String param, String theSTS) throws Config_Exception_NotDone, FC2Authentication_Exeception_AuthenticationFailed;
	
	public IToken getToken(String cardId,String supportedToken,String username,String param, String theSTS) throws Config_Exception_NotDone, FC2Authentication_Exeception_AuthenticationFailed;
	
	public void saveCurrentToken();
	
	public void addPwdForCardId(String cardId,String pwd);
	
	public void removePassword();
	
	public void removeSSOToken();
	
	public void addToken(String theSTS,String tokenType,IToken token);
	
	//public PKIHandler getPKIHandlerAssociatedToTheCard(InfoCard card);
	

}
