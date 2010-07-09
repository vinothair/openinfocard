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
package com.awl.fc2.selector.userinterface;

import java.util.Vector;

import org.xmldap.infocard.InfoCard;

//import com.awl.fc2.selector.Selector;
//import com.awl.fc2.selector.authentication.AuthenticationConfig;
//import com.awl.fc2.selector.authentication.FC2Authentication;
//import com.awl.fc2.selector.query.CompatibleInfoCards;
//import com.awl.fc2.selector.userinterface.authentication.IMapAuthenticationUI;
//import com.awl.fc2.selector.userinterface.authentication.IUserNameTokenUI;
import com.awl.fc2.selector.authentication.AuthenticationConfig;
import com.awl.fc2.selector.query.CompatibleInfoCards;

import com.awl.rd.protocols.messagehandler.IUI_BasicInterface;
import com.awl.ws.messages.DisplayTokenElement;

/**
 * Interface that describe the methods called by the {@link Selector} and {@link FC2Authentication} objects when an user interaction is neeeded
 * @author Cauchie stephane
 */
public interface ISelectorUI {
	
	public final int CLAIMS_REQUIRED = 0;
	public final int CLAIMS_OPTIONAL = 1;
	
	public void setUserConsentment(Vector<String> l_strRequiredClaims,
								   Vector<String> l_strOptionalClaims);
	
	public Vector<String> getSelectedClaims(int type);
	
//	public void inform(String Title,String caption,boolean modal);
//	public String question(String Title,String question);
	
	public void setDisplayToken(Vector<DisplayTokenElement> l_strDisplaysClaims);
	
	public void setDisplayTokens(Vector<Vector<DisplayTokenElement>> vecvecDTElement);
	/**
	 * Ask the user to select one of the presented cards
	 * @param lstCards the list of {@link InfoCard}
	 * @return the choosen {@link InfoCard}
	 */
	public InfoCard onChooseCard(Vector<InfoCard> lstCards);
	
	public CompatibleInfoCards onChooseSetsOfCards(Vector<CompatibleInfoCards> lstSetsOfCards);
	
	/**
	 * More than one Authentication methods can be avaible (present in the infocard):<br/>
	 * 1) UserName/PassWord<br/>
	 * 2) Map Authentication<br/> 
	 * This methods serves to ask the user to choose which one to perform.
	 * @param vecConfig the list of possible authentication methods
	 * @return {@link AuthenticationConfig}
	 * 
	 */
	public AuthenticationConfig onChooseAuthenticationConfig(Vector<AuthenticationConfig> vecConfig);
//	
//	/**
//	 * If the choosen method is UserNameToken then one can use this method to get the object that handle the user interaction for it.  
//	 * @return {@link IUserNameTokenUI}
//	 */
//	public IUserNameTokenUI getUserNameTokenUI();

	/**
	 * If the choosen method is MapAuthentication then one can use this method to get the object that handle the user interaction for it.  
	 * @return {@link IMapAuthenticationUI}
	 */
	//public IMapAuthenticationUI getMapUI();
	public IUI_BasicInterface getBasicInterface();
	
	/**
	 * this method is used to close the user interface. The selector should call it, when a Infocard request is finished.
	 */
	public void sleep();
	
	public void wakeup();
	
	public void kill();
	
	public void clearConsole();
	public void traceConsole(String msg);
}
