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
package com.awl.fc2.selector;

import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.swing.JOptionPane;

import org.xmldap.infocard.InfoCard;
import org.xmldap.infocard.TokenServiceReference;
import org.xmldap.infocard.policy.SupportedClaim;


import com.awl.fc2.selector.authentication.AuthenticationConfig;
import com.awl.fc2.selector.authentication.FC2Authentication;
import com.awl.fc2.selector.exceptions.Config_Exception_NotDone;
import com.awl.fc2.selector.exceptions.Config_Exeception_MalFormedConfigFile;
import com.awl.fc2.selector.exceptions.Config_Exeception_UnableToReadConfigFile;
import com.awl.fc2.selector.exceptions.FC2Authentication_Exeception_AuthenticationFailed;
import com.awl.fc2.selector.query.ClaimsQuery;
import com.awl.fc2.selector.query.CompatibleInfoCards;
import com.awl.fc2.selector.session.SessionSelector;
import com.awl.fc2.selector.userinterface.ISelectorUI;
import com.awl.fc2.selector.userinterface.lang.Lang;
import com.awl.logger.Logger;
import com.awl.ws.messages.DisplayTokenElement;

/**
 * This class centralized the functionalities of the selector.
 * It contains the three main methods, getInstance();onQueryClaims();and getTicket();
 * @author Cauchie stéphane
 *
 */
public class Selector {
	String stsTicket="FAILED";
	public SessionSelector session = new SessionSelector();
	//CardStore cardStore = null;
	ISelectorUI selectorUI;
	Vector<Vector<DisplayTokenElement>> setOfDisplayToken = new Vector<Vector<DisplayTokenElement>>(); 
	static Selector s_this=null;
	boolean isMultiSelectionQuery = false;
	Vector<String> vecTickets = new Vector<String>();
	static Logger log = new Logger(Selector.class);
	static public void trace(Object msg){
		log.trace(msg);
	}
	
	/**
	 * 
	 * @return the user interface
	 * @see ISelectorUI
	 */
	public ISelectorUI getUI(){
		return selectorUI;
	}
	/**
	 * create the user interface by calling getUI() on the Config object.
	 * @throws Config_Exeception_MalFormedConfigFile
	 * @throws Config_Exception_NotDone
	 * @see com.awl.fc2.selector.launcher.Config
	 * @see ISelectorUI
	 */
	public void createUI() throws Config_Exeception_MalFormedConfigFile, Config_Exception_NotDone{		
			selectorUI = com.awl.fc2.selector.launcher.Config.getInstance().getUI();
	}
	
	/**
	 * This method create the Selector if it does not exist and set the proxy
	 * @return the unique instance of the selector
	 * @throws Config_Exeception_UnableToReadConfigFile
	 * @throws Config_Exeception_MalFormedConfigFile
	 * @throws Config_Exception_NotDone
	 * @see AWLProxy	 
	 */
	static public Selector getInstance()throws Config_Exeception_UnableToReadConfigFile, Config_Exeception_MalFormedConfigFile, Config_Exception_NotDone {		
		if(s_this ==null){
			s_this = new Selector();
			AWLProxy.setProxy();			
		}
		
		return s_this;
	}
	public void closeSession(){
		session.close();
	}
	public void openSession() throws Config_Exeception_UnableToReadConfigFile, Config_Exeception_MalFormedConfigFile, Config_Exception_NotDone{
		session.open();
	}
	/**
	 * The Selector constructor handles the creation of the UI interface and the cardstore
	 * @throws Config_Exeception_UnableToReadConfigFile
	 * @throws Config_Exeception_MalFormedConfigFile
	 * @throws Config_Exception_NotDone
	 * @see WalletCardStore_OldVersion
	 * @see ISelectorUI
	 */
	private Selector() throws Config_Exeception_UnableToReadConfigFile, Config_Exeception_MalFormedConfigFile, Config_Exception_NotDone {
		
		//cardStore = new CardStore();
		createUI();
		
		session.configure();
		
	}
	
//	/**
//	 * 
//	 * @return the ticket (if the operation has failed the tiket contains {@code "FAILED"})
//	 */
//	public String getTicket(){
//		return stsTicket;
//	}
	public Vector<String> getTickets(){
		return vecTickets;
	}
	
	public void oneCardAuthentication(Vector<String> lstRequiredClaims,
			  Vector<String> lstOptionalClaims,
			  String urlRequestor,
			  String certifRequestorB64,
			  InfoCard choosenCard) throws Config_Exception_NotDone{
		System.gc();
		trace("You have chosen the following card named " + choosenCard.getCardName());				
		trace("looking for supported authentication methods");
		Iterator<TokenServiceReference> it_token = choosenCard.getTokenServiceReference().iterator();
		Vector<AuthenticationConfig> l_vecConfigAuthentication = new Vector<AuthenticationConfig>();
		while(it_token.hasNext()){
			TokenServiceReference tsr = it_token.next();
			String mexAdress = tsr.getMexAddress();
			String authMeth = tsr.getUserCredential().getAuthType();
			String username = tsr.getUserCredential().getUserName();
			trace("username " + username);
			trace("Supported authentication method ["+authMeth+"] on "+ mexAdress  );
			l_vecConfigAuthentication.add(new AuthenticationConfig(username,choosenCard,mexAdress,authMeth));
		}
		
		
		AuthenticationConfig choosenMethod = selectorUI.onChooseAuthenticationConfig(l_vecConfigAuthentication);
		
		
		choosenMethod.setQuery(lstRequiredClaims, lstOptionalClaims, urlRequestor, certifRequestorB64);
		trace("QUERY : " + lstRequiredClaims);
		FC2Authentication authentication = new FC2Authentication(choosenMethod);
		try {
			authentication.doAuthentication(null,this.session);
			//stsTicket = authentication.getTicket();
			vecTickets.add(authentication.getTicket());
			trace("Adding DT : ");
			setOfDisplayToken.add(authentication.getDisplayToken());
		} catch (FC2Authentication_Exeception_AuthenticationFailed e) {			
			e.printStackTrace();
		}
	}
	
	/**
	 * This methods do the following : <br/>
	 * 1) Get the list of compatible cards from the {@link WalletCardStore_OldVersion}<br/> 
	 * 2) Ask to the UI ({@link ISelectorUI}) wich one has to be used ({@code onChooseCard()})<br/>
	 * 3) Create all the {@link AuthenticationConfig} from the selected {@link InfoCard}<br/>
	 * 4) Ask to the UI ({@link ISelectorUI}) wich one has to be used ({@code onChooseAuthenticationConfig()})<br/>
	 * 5) Configure the choosen method with the parameters
	 * 6) Create and use the {@link FC2Authentication} object
	 * @param lstRequiredClaims list of required claims
	 * @param lstOptionalClaims list of optional claims (not used)
	 * @param urlRequestor 	    list of the RP
	 * @param certifRequestorB64 certificate of the RP
	 * @throws Config_Exception_NotDone
	 * @see WalletCardStore_OldVersion
	 */
	public void onQueryClaims(Vector<String> lstRequiredClaims,
							  Vector<String> lstOptionalClaims,
							  String urlRequestor,
							  String certifRequestorB64) throws Config_Exception_NotDone{
		
		if(!session.isOpen())
			try {
				session.open();
			} catch (Config_Exeception_UnableToReadConfigFile e) {
				throw(new Config_Exception_NotDone(e.getMessage()));
			} catch (Config_Exeception_MalFormedConfigFile e) {
				throw(new Config_Exception_NotDone(e.getMessage()));
			}
		
			{
				//Specific behavious of the CardStore and Credential Store
//				String cardId = null;
//				String pwd = null;
//				for(String claim:lstRequiredClaims){
//					trace("RP try to put a card");
//					int dyn = claim.indexOf("?");				
//					String dynamicPart = claim.substring(dyn+1);
//					if(claim.contains(CardsSupportedClaims.pwdCRDO.uri)){
//						String tab[] = dynamicPart.split("--");
//						if(tab.length==2){
//							cardId = tab[0];
//							pwd = tab[1];
//						}						
//					}
//					if(claim.contains(CardsSupportedClaims.listCardIdO.uri)){
//						InfoCard cardToAdd = Utils.CRDB64ToInfocar(dynamicPart);
//						if(cardToAdd != null){
//							//SINCE V2
//							session.getCardStore().addInfoCard(cardToAdd,true);
//							//session.getCardStore().addInfoCard(cardToAdd);
//						}
//						//cardId = cardToAdd.getCardId();						
//					}
//					
//				}
//				if(cardId != null && pwd != null){
//					trace("RP will send the pwd to the credential store");
//					trace("JSS will put be configure with it");
//					trace("CARD ID = "+  cardId);
//					trace("PWD     = "+ pwd);
//					session.getCredentialStore().addPwdForCardId(cardId, pwd);
//				}
			}
		
		
		
		
		vecTickets.clear();
		setOfDisplayToken.clear();
		this.selectorUI.setUserConsentment(lstRequiredClaims, lstOptionalClaims);
		lstRequiredClaims = this.selectorUI.getSelectedClaims(ISelectorUI.CLAIMS_REQUIRED);
		lstOptionalClaims = this.selectorUI.getSelectedClaims(ISelectorUI.CLAIMS_OPTIONAL);
		
		ClaimsQuery query = new ClaimsQuery(lstRequiredClaims, lstOptionalClaims, null);
		//SINCE V2
		Vector<CompatibleInfoCards> compatibleSets = session.getCardStore().getCompatibleInfoCards(query);
		//Vector<InfoCard> lstCards = session.getCardStore().getCompatibleCards(query);
		
		// Clearing the console
		selectorUI.clearConsole();
		//----
		//SINCE V2
		boolean multiSelection = false;
		for(CompatibleInfoCards comp:compatibleSets){
			if(comp.getSet().size()>1){
				multiSelection = true;
			}
		}
		if(multiSelection){
		//if(lstCards.size()== 0){
			trace("No single card found, we found a set");
			Vector<ClaimsQuery> vecQuery = new Vector<ClaimsQuery>();
			vecQuery.add(query);
			//SINCE V2
			Vector<CompatibleInfoCards> set = compatibleSets;
			//Vector<CompatibleInfoCards> set = session.getCardStore().getSetCompatibleCards(vecQuery);
			trace("find : " + set);
			
			if(multiSelection){
				trace("We going to make the authentication on each one");
				selectorUI.onChooseSetsOfCards(set);
				Vector<InfoCard> lstInfoCard = set.get(0).getSet();
				//selectorUI.onChooseCard(lstInfoCard);
				for(InfoCard choosenCard : lstInfoCard)
				{
					Vector<String> restrictedRequiredClaims = new Vector<String>();
					for(String claim : lstRequiredClaims){
						List<SupportedClaim> lst = choosenCard.getClaimList().getSupportedClaims();
						for(int i=0;i<lst.size();i++){				
							if(claim.contains(lst.get(i).getURI())){
								restrictedRequiredClaims.add(claim);
							}
						}
					}
					selectorUI.traceConsole(Lang.get(Lang.CARD_AUTH_WITH)+" " +choosenCard.getCardName() );
					oneCardAuthentication(restrictedRequiredClaims, lstOptionalClaims, urlRequestor, certifRequestorB64,choosenCard);
					Vector<String> newRequiredClaims = new Vector<String>();
					for(String s1 : lstRequiredClaims){
						boolean toAdd = true;
						for( String s2 : restrictedRequiredClaims){
							if(s1.equalsIgnoreCase(s2)){
								toAdd =false;							
								break;
							}
						}
						if(toAdd){
							newRequiredClaims.add(s1);
						}
					}
					lstRequiredClaims =newRequiredClaims;
					
				}
			}else{
				String caption = "No Cards found that works with the current request";
				trace(caption);
				JOptionPane.showConfirmDialog(null, caption);
				//selectorUI.question("Selector", caption);
			}
			
//			if(setOfDisplayToken.size() > 0)				
//				selectorUI.setDisplayTokens(setOfDisplayToken);
		}else{
			if(compatibleSets.size() == 0){
				String caption = "No Cards found that works with the current request";
				trace(caption);
				JOptionPane.showConfirmDialog(null, caption);
				
			}else{
				Vector<InfoCard> lstCards = new Vector<InfoCard>();
				for(CompatibleInfoCards comp:compatibleSets){
					lstCards.addAll(comp.getSet());
				}
				InfoCard choosenCard = selectorUI.onChooseCard(lstCards);
				oneCardAuthentication(lstRequiredClaims, lstOptionalClaims, urlRequestor, certifRequestorB64,choosenCard);
				if(setOfDisplayToken.size() > 0)
				{
					for(DisplayTokenElement dt : setOfDisplayToken.get(0)){
						if(dt.getStrTag().contains("sdd")){
							selectorUI.setDisplayToken(setOfDisplayToken.get(0));
							break;
						}
					}
				}
			}
			
//				selectorUI.setDisplayToken(setOfDisplayToken.get(0));
		}
		
		selectorUI.sleep();
		
	}	
	
	
	
	
	
//	public static void  main(String arf[]){
//		
//		try {
//			Selector selector = Selector.getInstance();
//		} catch (Config_Exeception_UnableToReadConfigFile e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (Config_Exeception_MalFormedConfigFile e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (Config_Exception_NotDone e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
//				
//	}

}
