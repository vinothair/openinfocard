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
package com.awl.fc2.plugin.store.wallet;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import org.xmldap.exceptions.SerializationException;
import org.xmldap.infocard.InfoCard;
import org.xmldap.infocard.SignedInfoCard;
import org.xmldap.infocard.policy.SupportedClaim;

import com.awl.fc2.selector.Selector;
import com.awl.fc2.selector.exceptions.CardStore_Execption_FailedRetrieving;
import com.awl.fc2.selector.exceptions.Config_Exception_NotDone;
import com.awl.fc2.selector.exceptions.Config_Exeception_MalFormedConfigFile;
import com.awl.fc2.selector.exceptions.Config_Exeception_UnableToReadConfigFile;
import com.awl.fc2.selector.exceptions.FC2Authentication_Exeception_AuthenticationFailed;
import com.awl.fc2.selector.launcher.Config;
import com.awl.fc2.selector.query.ClaimsQuery;
import com.awl.fc2.selector.query.CompatibleInfoCards;
import com.awl.fc2.selector.session.SessionSelector;
import com.awl.fc2.selector.storage.ICardStore;
import com.awl.fc2.selector.storage.utils.Utils;
import com.awl.fc2.selector.userinterface.lang.Lang;
import com.awl.logger.Logger;
import com.awl.rd.fc2.claims.CardsSupportedClaims;


public class WalletCardStore_OldVersion  implements ICardStore {

	Vector<InfoCard> m_vecCards = new Vector<InfoCard>();
	static Logger log = new Logger(WalletCardStore_OldVersion.class);
	static public void trace(Object obj){
		log.trace(obj);
	}
	
	
	
	@Override
	public void addInfoCard(InfoCard cardToAdd) {
		for(InfoCard existingCard:m_vecCards){
			if(existingCard.getCardId().equalsIgnoreCase(cardToAdd.getCardId())){
				return;
			}
		}
		trace("Read the current image, and save it");
		byte[] ImgRaw;
		//try {
			ImgRaw = Base64.decode(cardToAdd.getBase64BinaryCardImage());
		
			if(ImgRaw != null){
				ImageIcon imgI = new ImageIcon(ImgRaw);
				Image img = imgI.getImage();
	//			int height = 135;
	//			int width = imgI.getIconWidth()*135/imgI.getIconHeight();
				int height = imgI.getIconHeight();
				int width = imgI.getIconWidth();
				trace("Scaling at  :["+width+","+height+"]");
				//img = img.getScaledInstance(width,height , BufferedImage.SCALE_DEFAULT);
				
				
				BufferedImage bi = new BufferedImage(width,height,BufferedImage.TYPE_INT_RGB);//img.getWidth(null),img.getHeight(null));
				Graphics2D g2 = bi.createGraphics();
				// Draw img into bi so we can write it to file.
				g2.drawImage(img, 0, 0, null);
				g2.dispose();
				int newHeight = 70;
				int newWidth = 110;
				Image resized;
				BufferedImage bi2;
				if (width*newHeight/height<=newWidth){
					resized =  bi.getScaledInstance(width*newHeight/height,newHeight , BufferedImage.SCALE_SMOOTH);
					bi2 = new BufferedImage(width*newHeight/height,newHeight,BufferedImage.TYPE_INT_RGB);//img.getWidth(null),img.getHeight(null));
				} else {
					resized =  bi.getScaledInstance(newWidth,height*newWidth/width , BufferedImage.SCALE_SMOOTH);
					bi2 = new BufferedImage(newWidth,height*newWidth/width,BufferedImage.TYPE_INT_RGB);
				}
				Graphics2D g3 = bi2.createGraphics();
				// Draw img into bi so we can write it to file.
				g3.drawImage(resized, 0, 0, null);
				g3.dispose();
				
				
				
				String filename = cardToAdd.getCardName().replace("/", "_");
				filename = filename.replace("\\", "_");
				try {
					String imgFolder = Config.getInstance().getImgFolder();
					ImageIO.write(bi2, "jpg", new File(imgFolder+filename+".jpg"));
					cardToAdd.setBase64BinaryCardImage(imgFolder+filename+".jpg");
				} catch (Config_Exception_NotDone e) {
					trace("Missing <IMG> folder in the config_selecteur.xml");
				} catch (IOException e) {
					
				} catch (Config_Exeception_MalFormedConfigFile e) {
					
				}
			}
//		} catch (Base64DecodingException e1) {
//			
//		}
		m_vecCards.add(cardToAdd);
		
	}

	@Override
	public void commit() {
		trace("COMMIT NOT DONE");
		for(InfoCard card:m_vecCards){
			if(card instanceof SignedInfoCard){
				SignedInfoCard scard = (SignedInfoCard )card;
				try {
					String xml = scard.toXML();
					String request = Base64.encode(xml.getBytes());
					trace("Sending the following card : " + request);
				} catch (SerializationException e) {
					trace("unable to serialize the card");
				}
				
			}
		}

	}

	@Override
	public Vector<InfoCard> getCompatibleCards(ClaimsQuery query) {
		Vector<InfoCard> res = new Vector<InfoCard>();
		for(InfoCard card : m_vecCards){
						
			boolean tabComp[] = new boolean[query.getTabRequiredClaims().size()];
			for(int s=0;s<tabComp.length;s++){
				tabComp[s] = false;
			}
			List<SupportedClaim> lst = card.getClaimList().getSupportedClaims();
			for(int i=0;i<lst.size();i++){
				for(int s=0;s<tabComp.length;s++){
					if(query.getTabRequiredClaims().get(s).contains(lst.get(i).getURI())){
						tabComp[s] = true;
						break;
					}
				}
				
				
				
			}
			boolean toAdd=true;
			for(int s=0;s<tabComp.length;s++){
				if(tabComp[s]==false){
					toAdd=false;
					break;
				}
			}
			if(toAdd){
				res.add(card);
			}
		}
		
		return res;
	}

	@Override
	public Vector<CompatibleInfoCards> getSetCompatibleCards(
			Vector<ClaimsQuery> setOfQuery) {
		Vector<CompatibleInfoCards> res = new Vector<CompatibleInfoCards>();
		if(setOfQuery.size()!=1){
			trace("getSetCompatibleCards not yet finished");
		}else{
			MatrixAttvsCard look  = new MatrixAttvsCard(setOfQuery.get(0).getTabRequiredClaims(), m_vecCards);
			CompatibleInfoCards oneSet = look.findASet();
			if(oneSet != null) res.add(oneSet);
		}				
		return res;
	}

	@Override
	public Vector<InfoCard> listAllCards(int storeType) {
		return m_vecCards;
	}

	@Override
	public void removeInfoCard(InfoCard cardToRemove) {
		// TODO Auto-generated method stub

	}

	public void traceUI(String msg){
		try {
			Selector.getInstance().getUI().traceConsole(msg);
		} catch (Config_Exeception_UnableToReadConfigFile e) {
			trace("Cannot console");
		} catch (Config_Exeception_MalFormedConfigFile e) {
			trace("Cannot console");
		} catch (Config_Exception_NotDone e) {
			trace("Cannot console");
		}
	}
	
	@Override
	public void update() throws CardStore_Execption_FailedRetrieving {
		Vector<String> lstRequiredClaims = new Vector<String>();
		lstRequiredClaims.add(CardsSupportedClaims.listCardIdO.uri);		
		Map<String, String> response;
		traceUI(Lang.get(Lang.NEGOCIATING_WITH_REMOTE_WALLET));
		try {
			response = Utils.getSTSResponse(theSesison,theSesison.getUsername(),lstRequiredClaims);
//			System.out.println(theSesison.getUsername());
		} catch (FC2Authentication_Exeception_AuthenticationFailed e1) {
			throw(new CardStore_Execption_FailedRetrieving(e1.getMessage()));
		}
		if(response != null){
			String lstCards = (String)response.get(CardsSupportedClaims.listCardIdO.columnName);
			if(lstCards != null){
				String tabCardIds [] = Utils.String2Tab(lstCards);
//				System.out.println("----------------------------");
//				for(int i=0;i<tabCardIds.length;i++){
//					System.out.println("|||||||||||||| "+ tabCardIds[i]);
//				}
//				System.out.println("----------------------------");
				
				for(int i=0;i<tabCardIds.length;i++){
					lstRequiredClaims.clear();
					traceUI(Lang.get(Lang.GET_CARD_I) + " " +(i+1) +"/"+tabCardIds.length);
					String request = CardsSupportedClaims.getCRDO.uri+"?"+tabCardIds[i].trim();
//					trace("Update : "+ request);
//					try {
//						System.in.read();
//					} catch (IOException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
					lstRequiredClaims.add(request);
					try {
						response = Utils.getSTSResponse(theSesison,theSesison.getUsername(),lstRequiredClaims);
						if(response != null){
							String CRDB64 = (String) response.get(CardsSupportedClaims.getCRDO.columnName);
							if(CRDB64 != null){
								InfoCard toAdd = Utils.CRDB64ToInfocar(CRDB64);
								if(toAdd!=null){
									trace("Adding the infocart");
									//m_vecCards.add(toAdd);
									addInfoCard(toAdd);
								}
							}
						}
					} catch (FC2Authentication_Exeception_AuthenticationFailed e) {
						throw(new CardStore_Execption_FailedRetrieving(e.getMessage()));
					}
					
				}
			}
		}
		

	}
	
	
	
	

	@Override
	public void destroy() {
		trace("Destroying the cardstore");
		m_vecCards.clear();

	}

	
	
	@Override
	public void reset() throws CardStore_Execption_FailedRetrieving {
		// TODO Auto-generated method stub
		trace("Reset the CardStore");
		destroy();
//		try {
//			String username =Selector.getInstance().getUI().getBasicInterface().sendQuestion(Lang.get(Lang.NEW_SESSION), "-" + Lang.get(Lang.ASK_USERNAME),false);
//			trace("UserName : "+username);
//			theSesison.setUsername(username);
			
			
			update();
//			
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
		
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		trace("Initialize Config Object");
		try {
			Config.getInstance("c:/tempp/cards/Config_Selecteur.xml",true);
		} catch (Config_Exeception_UnableToReadConfigFile e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

	}

	
	SessionSelector theSesison = null;
	@Override
	public void configure(SessionSelector theSession) {
		this.theSesison = theSession;		
	}

	@Override
	public void addCRD(String path) {
		trace("Import CRD card");
		
			File file = new File(path);
			FileInputStream in;
			try {
				in = new FileInputStream(file);
				//FileReader fin = new FileReader(file);
				StringBuffer buf = new StringBuffer();
				byte[] buffer = new byte[50];
				int read = 1;
				while(read!= -1){
					try {
						read = in.read(buffer);
						
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					if(read != -1){
						String tmp = new String(buffer,0,read);
						//buf.append(buffer,0,read);
						buf.append(tmp);
					}
					
				}
				String toRet = buf.toString();
				
				InfoCard card = Utils.String2InfoCard(toRet);
				if(card != null){
					//m_vecCards.add(card);
					addInfoCard(card);
					commit(card.getCardId(), Base64.encode(toRet.getBytes()));
				}else{
					trace("Importing failed, not a good card");
				}
			} catch (FileNotFoundException e1) {
				trace("Importing failed, file not found");
			}
			

	}

	@Override
	public void commit(String cardId,String B64) {
		trace("Committing the following Card" + cardId);
		for(InfoCard card: this.m_vecCards){
//			if((card instanceof SignedInfoCard)){
//				card.to
//			}
			if(card.getCardId().equalsIgnoreCase(cardId)){
				trace("Find the card to be committed");
				B64 = B64.replaceAll("\n", "");
				B64 = B64.replaceAll("\r", "");
				String request = CardsSupportedClaims.listCardIdO.uri+"?"+B64;
				trace("Request : " + request);
				Vector<String> lstRequiredClaims = new Vector<String>();
				lstRequiredClaims.add(request);
				try {
					Utils.getSTSResponse(theSesison,theSesison.getUsername(),lstRequiredClaims);
				} catch (FC2Authentication_Exeception_AuthenticationFailed e) {
					trace("Unable to commit the card");
				}
//					
				
			}
		}
		
		
	}

	@Override
	public void removeInfoCard(String cardId) {
		// TODO Auto-generated method stub
		for(InfoCard card:this.m_vecCards){
			if(card.getCardId().equalsIgnoreCase(cardId)){
				String request = CardsSupportedClaims.delCRDO.uri+"?"+cardId;
				trace("Request : " + request);
				Vector<String> lstRequiredClaims = new Vector<String>();
				lstRequiredClaims.add(request);
				try {
					Utils.getSTSResponse(theSesison,theSesison.getUsername(),lstRequiredClaims);
				} catch (FC2Authentication_Exeception_AuthenticationFailed e) {
					trace("unable to remove infocard from the wallet");
				}
				m_vecCards.remove(card);
				break;
			}
		}
	}

}
class MatrixAttvsCard{
	Vector<InfoCard> originalSet;
	CompatibleInfoCards aSet = new CompatibleInfoCards();
	boolean matrix[][];
	boolean importCard[];
	int cptCardForAttribute[];
	int nbCards;
	int nbAtt;
	public MatrixAttvsCard(Vector<String> att,Vector<InfoCard> cards) {
		originalSet = cards;
		fillWithData(att, cards);			
	}
	public void selectAllCard(){
		for(int j=0;j<nbCards;j++){
			importCard[j] = true;
		}
	}
	public void initMatrix(){
		matrix = new boolean[nbAtt][nbCards];
		importCard = new boolean[nbCards];
		cptCardForAttribute = new int[nbAtt];
		selectAllCard();
		for(int i = 0;i<nbAtt;i++){
			cptCardForAttribute[i] = 0;
			for(int j=0;j<nbCards;j++){
				matrix[i][j] = false;
			}
		}
	}
	public void fillWithData(Vector<String> att,Vector<InfoCard> cards){
		nbAtt = att.size();
		nbCards = cards.size();
		initMatrix();
		
		for( int l_iatt = 0;l_iatt <nbAtt;l_iatt++){
			String curAtt = att.get(l_iatt);
			for(int l_iCards =0;l_iCards < nbCards; l_iCards++){
				
				List<SupportedClaim> lst = cards.get(l_iCards).getClaimList().getSupportedClaims();
				for(int i=0;i<lst.size();i++){				
					if(curAtt.contains(lst.get(i).getURI())){
						matrix[l_iatt][l_iCards] =true;
						cptCardForAttribute[l_iatt]++;
						break;
					}
				}														
			}								
		}			
	}
	public void computeCPT(){
		for(int i = 0;i<nbAtt;i++){
			cptCardForAttribute[i] = 0;
			for(int j=0;j<nbCards;j++){
				if(matrix[i][j] && importCard[j]){
					cptCardForAttribute[i]++;
				}
			}
		}
	}
	public boolean isSatisifying(){
		
		for(int i = 0;i<nbAtt;i++){
			if(cptCardForAttribute[i] == 0) return false;			
		}
		return true;
	}
	
	public CompatibleInfoCards findASet(){
		selectAllCard();
		computeCPT();
		if(isSatisifying()){
			System.out.println(getMatrix());
			// It exists at least a solution
			// Find the cards we cant remove
			int tabForcedCards[] = new int[nbCards];
			
			for(int i=0;i<nbCards;i++) {tabForcedCards[i]=-1;}
			for(int i=0;i<nbAtt;i++){
				if(cptCardForAttribute[i]==1){
					for(int j=0;j<nbCards;j++){
						if(matrix[i][j]==true){
							tabForcedCards[j] = 1;							
						}
					}
				}
			}
			//
			for(int c=0;c<nbCards;c++){
				System.out.println(getMatrix());
				if(tabForcedCards[c]!=1){
					// Try to remove the c^th cards
					importCard[c] = false;
					computeCPT();
					if(isSatisifying()){
						//we should allways be here
						for(int i=0;i<nbAtt;i++){
							if(cptCardForAttribute[i]==1){
								for(int j=0;j<nbCards;j++){
									if(matrix[i][j]==true && importCard[j] == true){
										tabForcedCards[j] = 1;							
									}
								}
							}
						}						
					}else{
						System.out.println("PAS NORMAL");
						importCard[c] = true;
					}									
				}
			}
			
			for(int i =0;i< nbCards;i++){
				if(tabForcedCards[i] == 1){
					aSet.addInfoCard(originalSet.get(i));
				}
			}
			return aSet;
		}
		return null;
	}
	public String getMatrix(){
		String res= "-----------\n";
		for(int i = 0;i<nbAtt;i++){			
			for(int j=0;j<nbCards;j++){
				//if(matrix[i][j] && importCard[j]){
					res += ((matrix[i][j]&& importCard[j])?"x":"o")+",\t";
				//}
				
			}
			res +="\n";
		}
		
		res +="------------";
		return res;
	}
	
	
}
