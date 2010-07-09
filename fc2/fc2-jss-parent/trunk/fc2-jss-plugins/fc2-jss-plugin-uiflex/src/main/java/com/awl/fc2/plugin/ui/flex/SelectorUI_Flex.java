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
package com.awl.fc2.plugin.ui.flex;

import java.util.Vector;

import org.xmldap.exceptions.SerializationException;
import org.xmldap.infocard.InfoCard;
import org.xmldap.infocard.policy.SupportedClaim;

import com.awl.fc2.selector.authentication.AuthenticationConfig;
import com.awl.fc2.selector.query.CompatibleInfoCards;
import com.awl.fc2.selector.userinterface.ISelectorUI;
import com.awl.fc2.selector.userinterface.lang.Lang;
import com.awl.logger.Logger;
import com.awl.rd.protocols.messagehandler.IUI_BasicInterface;
import com.awl.ws.messages.DisplayTokenElement;


/**
 * Specification of the {@link ISelectorUI}. It implements the different methods for user interaction. (using {@link AirAppControler})
 * @author Cauchie st�phane
 *
 */
public class SelectorUI_Flex implements ISelectorUI {
//	IUserNameTokenUI userNameTokenUI;
//	IMapAuthenticationUI mapclientUI;
	BasicUI_Flex basicUI;
	Vector<String> vecRequiredClaims = new Vector<String>();
	Vector<String> vecOptionalClaims = new Vector<String>();
	static Logger log = new Logger(SelectorUI_Flex.class);
	/**
	 * trace the message into our {@link Logger} 
	 * @param msg message to be log at trace level
	 */
	public static void trace(Object msg){
		log.trace(msg);
	}
	
	/**
	 * Construct the UI for the external program (Air), including :<br/>
	 * {@link UserNameTokenUI_Flex}<br/>
	 * {@link MapAuthenticationUI_Flex}<br/>
	 * it also initialize the AirAppControler by calling {@link AirAppControler#initControler()}
	 */
	public SelectorUI_Flex() {
		AirAppControler.getInstance().initControler();
		basicUI = new BasicUI_Flex();
//		userNameTokenUI = new UserNameTokenUI_Flex();
//		mapclientUI = new MapAuthenticationUI_Flex();
	}
	
//	/**
//	 * @return {@link IUserNameTokenUI} the actual configured UserNameToken interface ({@link UserNameTokenUI_Flex})
//	 */
//	public IUserNameTokenUI getUserNameTokenUI(){
//		return userNameTokenUI;
//	}
//	/**
//	 * @return {@link IMapAuthenticationUI} the actual configured UserNameToken interface ({@link MapAuthenticationUI_Flex})
//	 */
//	public IMapAuthenticationUI getMapUI(){
//		return mapclientUI;
//	}
	
	/**
	 * Propose the different configs and let the user chooses
	 * @param {@link Vector}<{@link AuthenticationConfig }> the list of possible authentication config
	 * @return {@link AuthenticationConfig}
	 * return the first one
	 */
	public AuthenticationConfig onChooseAuthenticationConfig(Vector<AuthenticationConfig> vecConfig){
		System.out.println("---- onChooseAuthenticationConfig ----");
		System.out.println("Here is the compatible cards");
		for(int i=0;i<vecConfig.size();i++){
			System.out.println(i + ": " + vecConfig.get(i));
		}				
		System.out.print("Please choose the right authentication method : ");
		/*Scanner in = new Scanner(System.in);
		String response = in.nextLine();*/
		
		
		System.out.println("-----");
		return vecConfig.get(Integer.valueOf(vecConfig.size()-1));

	}
	
	
	/**
	 * Propose the different infocard and let the user chooses. (create the xml to be send to the AirAppControler)
	 * @param {@link Vector}<{@link InfoCard}> the list of possible InfoCard	
	 * @return {@link InfoCard}<br/>
	 * it uses : <br/>
	 * {@link AirAppControler#sendOpen()}<br/>
	 * {@link AirAppControler#sendSelectCard(String)}
	 */
	public InfoCard onChooseCard(Vector<InfoCard> lstCards){
		trace("---- onChooseCard ----");
		trace("Here is the compatible cards");
	
		String xmlCards =  "<?xml version=\"1.0\"?>"+
							"<methods>";
		
		
		for(int i=0;i<lstCards.size();i++){
			trace(i + ": " + lstCards.get(i).getCardName());			
			String url = lstCards.get(i).getBase64BinaryCardImage();
			trace("Setting img to " + url);
			
			String tmplCard = 	"<item>"+
			"<url>"+url+"</url>"+
			"<info>"+lstCards.get(i).getIssuer()+"</info>"+
			"<title> "+lstCards.get(i).getCardName()+" </title>"+
			"<sendBack>"+i+"</sendBack>"+
			"</item>";		
			xmlCards += tmplCard;
		}		
		
		String xmlConsent = "<claims>";
		
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
				}
				
			} catch (SerializationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			xmlConsent+="<claim>" +
						 "<ATT_NAME>"+claim+"</ATT_NAME>"+
						"<ATT_VALUE></ATT_VALUE>"+"</claim>";
		}
		xmlConsent += "</claims>";
		//String response = AirAppControler.getInstance().sendSelectCard(xmlConsent);		
		//trace("----- " + response);
		
		xmlCards += xmlConsent + "</methods>";	
		AirAppControler.getInstance().sendOpen();
		String response = AirAppControler.getInstance().sendSelectCard(xmlCards);
		
		System.out.println("-----");
		return lstCards.get(Integer.valueOf(response));
		
		
	}
	
	
	/**
	 * Send the close command to the AirAppControler:<br/>
	 * {@link AirAppControler#sendClose()}
	 */
	@Override
	public void sleep() {
		AirAppControler.getInstance().sendClose();
		
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
		
		String DT = Lang.get(Lang.DT_WE_TRANSMIT_THE_FOLLOWING);//"Nous allons transmettre les informations suivantes : \n";
		for(DisplayTokenElement dte : lStrDisplaysClaims){
			DT += dte.getStrTag()+" : " + dte.getStrValue() +"\n";
		}
		AirAppControler.getInstance().sendDisplayToken();// //sendModalNotification(DT);
	}

	@Override
	public void setDisplayTokens(Vector<Vector<DisplayTokenElement>> lStrDisplaysClaims) {
		/**
		 * <claims>"+
							                    "<claim>"+
							                        "<ATT_NAME>Nom</ATT_NAME>"+
							                        "<ATT_VALUE>Cauchie</ATT_VALUE>"+                        
							                    "</claim>"+
							                    "<claim>"+
							                        "<ATT_NAME>prenom</ATT_NAME>"+
							                        "<ATT_VALUE>Stephane</ATT_VALUE>"+                        
							                    "</claim>"+
							                 "</claims>"
		 * 
		 */
		
		
	}
	@Override
	public Vector<String> getSelectedClaims(int type) {
		trace("getSelectedClaims");
		Vector<String> selected = type==CLAIMS_OPTIONAL?vecOptionalClaims:vecRequiredClaims;		
		return selected;
	}
	
	public String genXMLConsentElementFromSupportedClaim(String claim,InfoCard card){		
			String desc = "";//		
			//lstCards.get(0).getClaimList().getClaimsbyURI(claim).toXML();
			SupportedClaim sc = card.getClaimList().getClaimsbyURI(claim);
			if(sc != null){
				try {
					desc = sc.toXML();
				} catch (SerializationException e) {
					return null;
				}				
			}else{
				return null;
			}
		
			
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
			}
			return "<claim>" +
			 "<ATT_NAME>"+claim+"</ATT_NAME>"+
				"<ATT_VALUE></ATT_VALUE>"+"</claim>";
	}

	@Override
	public CompatibleInfoCards onChooseSetsOfCards(
			Vector<CompatibleInfoCards> lstSetsOfCards) {
		trace("---- onChooseCard ----");
		trace("Here is the compatible cards");
	
		String xmlCards =  "<?xml version=\"1.0\"?>"+
							"<methods>";
		if(lstSetsOfCards.size()==0)return null;
		Vector<InfoCard> lstCards = lstSetsOfCards.get(0).getSet();
		Vector<String> tmpClaims = new Vector<String>();//vecRequiredClaims.clone();
		for(String copyClaim : vecRequiredClaims){
			tmpClaims.add(copyClaim);
		}
		for(int i=0;i<lstCards.size();i++){
			trace(i + ": " + lstCards.get(i).getCardName());			
			String url = lstCards.get(i).getBase64BinaryCardImage();
			trace("Setting img to " + url);
			
			String tmplCard = 	"<item>"+
			"<url>"+url+"</url>"+
			"<info>"+lstCards.get(i).getIssuer()+"</info>"+
			"<title> "+lstCards.get(i).getCardName()+" </title>"+
			"<sendBack>"+i+"</sendBack>";
			
			String xmlConsent = "<claims>";
			Vector<String> futureClaims = new Vector<String>();
			for(String claim : tmpClaims){
				String toAdd = genXMLConsentElementFromSupportedClaim(claim, lstCards.get(i));
				if(toAdd != null){
					xmlConsent += toAdd;					
				}else{
					futureClaims.add(claim);
				}
			}
			tmpClaims = futureClaims;
			
			tmplCard += xmlConsent+ "</claims></item>";		
			xmlCards += tmplCard;
		}		
		
		
		
	
		//String response = AirAppControler.getInstance().sendSelectCard(xmlConsent);		
		//trace("----- " + response);
		
		xmlCards += "</methods>";	
		AirAppControler.getInstance().sendOpen();
		AirAppControler.getInstance().sendSelectSetOfCards(xmlCards);
		
		System.out.println("-----");
		if(lstSetsOfCards.size()>0){
			return lstSetsOfCards.get(0);
		}
		return null;
	}

//	@Override
//	public void inform(String Title, String caption, boolean modal) {
//		trace("inform("+Title+", "+caption+", "+modal+")");
//		if(modal){
//			AirAppControler.getInstance().sendNotification(caption);
//		}else{
//			AirAppControler.getInstance().sendModalNotification(caption);
//		}
//		
//	}
//
//	@Override
//	public String question(String Title, String question) {
//		trace("question("+Title+", "+question+")");
//		String response = AirAppControler.getInstance().sendQuestion(question);
//		return response;
//	}

	@Override
	public void wakeup() {
		trace("Waking up");
		AirAppControler.getInstance().sendOpen();
		
	}

	@Override
	public void clearConsole() {
		AirAppControler.getInstance().clearConsole();
		
	}

	@Override
	public void traceConsole(String msg) {
		AirAppControler.getInstance().traceConsole(msg);
		
	}

	@Override
	public IUI_BasicInterface getBasicInterface() {
		// TODO Auto-generated method stub
		return basicUI;
	}

	@Override
	public void kill() {
		AirAppControler.getInstance().killFlex();
		
		
	}
}
