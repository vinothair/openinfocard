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
package com.awl.fc2.plugin.ui.swing;

import java.util.Scanner;
import java.util.Vector;

import org.xmldap.exceptions.SerializationException;
import org.xmldap.infocard.InfoCard;

import com.awl.fc2.selector.authentication.AuthenticationConfig;
import com.awl.fc2.selector.query.CompatibleInfoCards;
import com.awl.fc2.selector.userinterface.ISelectorUI;
import com.awl.fc2.selector.userinterface.swing.MainWindow;

import com.awl.logger.Logger;
import com.awl.rd.protocols.messagehandler.IUI_BasicInterface;
import com.awl.ws.messages.DisplayTokenElement;

/**
 * Specification of the {@link ISelectorUI}. It implements the different methods for a console interaction. (just for test)
 * @author Cauchie stéphane
 *
 */
public class SelectorUI_Swing implements ISelectorUI {

//	IUserNameTokenUI userNameTokenUI;
//	IMapAuthenticationUI mapclientUI;
	
	IUI_BasicInterface basicUI;
	
	Vector<String> vecRequiredClaims = new Vector<String>();
	Vector<String> vecOptionalClaims = new Vector<String>();
	Vector<DisplayTokenElement> vecDisplayToken = new Vector<DisplayTokenElement>();
	
	static Logger log = new Logger(SelectorUI_Swing.class);
	public static void trace(Object msg){
		log.trace(msg);
	}
	
	/**
	 * Construct the UI for the console, including :<br/>
	 * {@link UserNameTokenUI_Console}<br/>
	 * {@link MapAuthenticationUI_Console}
	 */
	public SelectorUI_Swing() {
		
		basicUI = new BasicUI_Swing();
		
		MainWindow.getInstance().hideProc();
		
//		userNameTokenUI = new UserNameTokenUI_Console();
//		mapclientUI = new MapAuthenticationUI_Console();
	}
//	/**
//	 * return the initialized {@link IUserNameTokenUI}
//	 */
//	public IUserNameTokenUI getUserNameTokenUI(){
//		return userNameTokenUI;
//	}
//	
//	/**
//	 * return the initialized {@link IMapAuthenticationUI}
//	 */
//	public IMapAuthenticationUI getMapUI(){
//		return mapclientUI;
//	}
	
	/**
	 * Propose the different configs and let the user chooses
	 * @param {@link Vector}<{@link AuthenticationConfig }> the list of possible authentication config
	 * @return {@link AuthenticationConfig}
	 */
	public AuthenticationConfig onChooseAuthenticationConfig(Vector<AuthenticationConfig> vecConfig){
		trace("---- onChooseAuthenticationConfig ----");
		if(vecConfig.size()==1){
			
			trace("Only one method, we return it directly");
			trace("----------------");
			return vecConfig.get(0);
		}
		trace("Here is the authentication methods");
		
		for(int i=0;i<vecConfig.size();i++){
			System.out.println(i + ": " + vecConfig.get(i));
		}				
		trace("Please choose the right authentication method : ");
		Scanner in = new Scanner(System.in);
		String response = in.nextLine();
		
		trace("-----");
		return vecConfig.get(Integer.valueOf(response));
	}
	
	/**
	 * Propose the different infocard and let the user chooses
	 * @param {@link Vector}<{@link InfoCard}> the list of possible InfoCard	
	 * @return {@link InfoCard}
	 */
	public InfoCard onChooseCard(Vector<InfoCard> lstCards){
//		trace("We are looking for the following claims :");
//		for(String claim : vecRequiredClaims){
//			
//			try {
//				String desc = lstCards.get(0).getClaimList().getClaimsbyURI(claim).toXML();
//				trace("find : " +desc);
//				String BEGIN = "DisplayTag>";
//				int pos_beg = desc.indexOf(BEGIN);
//				if(pos_beg > 0){
//					
//					
//					int pos_end = desc.indexOf("</", pos_beg);
//					if(pos_end > pos_beg){
//						desc = desc.substring(pos_beg+BEGIN.length(),pos_end);
//						claim = desc;
//					}
//						
//					trace("> " + claim);
//				}
//				
//			} catch (SerializationException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			
//
//		}
//		System.out.println("---- onChooseCard ----");
//		System.out.println("Here is the compatible cards");
//		for(int i=0;i<lstCards.size();i++){
//			System.out.println(i + ": " + lstCards.get(i).getCardName());
//		}				
//		System.out.print("Please choose the right card : ");
//		Scanner in = new Scanner(System.in);
//		String response = in.nextLine();
//		
//		System.out.println("-----");
//		
//		return lstCards.get(Integer.valueOf(response));
		
		trace("---- onChooseCard ----");
		trace("Here is the compatible cards");
	
		String claims[] = new String[vecRequiredClaims.size()];
		String urls[] = new String[lstCards.size()];
		String labels[] = new String[lstCards.size()];
		
		for(int i=0;i<lstCards.size();i++){
			trace(i + ": " + lstCards.get(i).getCardName());			
			urls[i] = lstCards.get(i).getBase64BinaryCardImage();
			trace("Setting img to " + urls[i]);
			
			//lstCards.get(i).getIssuer()
			labels[i] = lstCards.get(i).getCardName();
		}	
		
		int i = 0;
		
		for(String claim : vecRequiredClaims){
			
			try {
				String desc = lstCards.get(0).getClaimList().getClaimsbyURI(claim).toXML();
				trace("find : " +desc);
				String BEGIN = "DisplayTag>";
				int pos_beg = desc.indexOf(BEGIN);
				if(pos_beg > 0){
					
					
					int pos_end = desc.indexOf("</", pos_beg);
					if(pos_end > pos_beg){
						desc = desc.substring(pos_beg+BEGIN.length(),pos_end);
						claim = desc;
					}
					
					trace("> " + claim);
					
					claims[i++] = claim;
				}
				
			} catch (SerializationException e) {
				e.printStackTrace();
			}
		}
			
		
		wakeup();
		MainWindow.getInstance().selectCard(claims, urls, labels);
		
		System.out.println("-----");
		
		while(MainWindow.getInstance().getResponse()==null){};
		
		String response = MainWindow.getInstance().getResponse();
		MainWindow.getInstance().resetResponse();
		
		MainWindow.getInstance().cardTOcons();
		
		System.out.println("-----");
		
		return lstCards.get(Integer.valueOf(response));
		
		
	}
	/**
	 * sleep : hide the main window.
	 */
	@Override
	public void sleep() {
		trace("Entering sleep mode");
		MainWindow.getInstance().hideProc();
	}

	@Override
	public void setUserConsentment(Vector<String> lStrRequiredClaims,
			Vector<String> lStrOptionalClaims) {
		vecRequiredClaims = lStrRequiredClaims;
		vecOptionalClaims = lStrOptionalClaims;
		trace("Set User Consemtment with \n");					
		trace("Required = "+ vecRequiredClaims);
		trace("Optional = "+ vecOptionalClaims);
		
		
	}

	@Override
	public void setDisplayToken(Vector<DisplayTokenElement> lStrDisplaysClaims) {
		vecDisplayToken = lStrDisplaysClaims;
		
		trace("Set STS display token with \n");					
		trace("DT = "+ vecDisplayToken);
		
	}

	@Override
	public void setDisplayTokens(Vector<Vector<DisplayTokenElement>> vecvecDTElements) {
		trace("Set STS display token with \n");					
		trace("DT = "+ vecvecDTElements);
		
	}

	@Override
	public Vector<String> getSelectedClaims(int type) {
		trace("getSelectedClaims");
		Vector<String> selected = type==CLAIMS_OPTIONAL?vecOptionalClaims:vecRequiredClaims;		
		return selected;
	}

	@Override
	public CompatibleInfoCards onChooseSetsOfCards(
			Vector<CompatibleInfoCards> lstSetsOfCards) {
		trace("Here are the compatible sets of cards : " + lstSetsOfCards);
		if(lstSetsOfCards.size()>0){
			return lstSetsOfCards.get(0);
		}
		return null;
	}

//	@Override
//	public void inform(String Title, String caption, boolean modal) {
//		trace("["+Title+"]="+caption);
//		if(modal)
//		{
//			System.out.println("Press a key.");
//			Scanner in = new Scanner(System.in);
//			String response = in.nextLine();
//		}
//	}
//
//	@Override
//	public String question(String Title, String question) {
//		System.out.println("["+Title+"]"+question);
//		Scanner in = new Scanner(System.in);
//		String response = in.nextLine();
//		return response;
//	}

	@Override
	public void wakeup() {
		trace("Waking up");
		MainWindow.getInstance().wakeupProc();
	}

	@Override
	public void clearConsole() {
		trace("Clearing console...");
		MainWindow.getInstance().clearConsole();
	}

	@Override
	public void traceConsole(String msg) {
		trace("traceConsole("+msg+")");
		MainWindow.getInstance().traceConsole(msg);
	}

	@Override
	public IUI_BasicInterface getBasicInterface() {
		return basicUI;
	}

	@Override
	public void kill() {
		
	}

	

	
}
