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
package com.awl.fc2.plugin.store.local;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import nu.xom.Element;
import nu.xom.ParsingException;
import nu.xom.ValidityException;

import org.xmldap.exceptions.SerializationException;
import org.xmldap.infocard.InfoCard;
import org.xmldap.infocard.SignedInfoCard;
import org.xmldap.infocard.policy.SupportedClaim;
import org.xmldap.util.XmlFileUtil;

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
import com.awl.logger.Logger;
import com.awl.rd.fc2.claims.CardsSupportedClaims;

import com.utils.XMLParser;
import com.utils.execeptions.XMLParser_Exception_NO_ATTRIBUTE;
import com.utils.execeptions.XMLParser_Exception_NoNextValue;

public class LocalCardStore_OldVersion  implements ICardStore {

	Vector<InfoCard> m_vecCards = new Vector<InfoCard>();
	static Logger log = new Logger(LocalCardStore_OldVersion.class);
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
//		try {
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
				Image Resized =  bi.getScaledInstance(width*newHeight/height,newHeight , BufferedImage.SCALE_DEFAULT);
								
					BufferedImage bi2 = new BufferedImage(width*newHeight/height,newHeight,BufferedImage.TYPE_INT_RGB);//img.getWidth(null),img.getHeight(null));
					Graphics2D g3 = bi2.createGraphics();
					// Draw img into bi so we can write it to file.
					g3.drawImage(Resized, 0, 0, null);
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
		try {
			loadLocalStore();
		} catch (Config_Exeception_MalFormedConfigFile e) {
			trace("Config malformed");
		} catch (Config_Exception_NotDone e) {
			trace("Config not done");
		}
		

	}
	
	public void loadLocalStore() throws  Config_Exeception_MalFormedConfigFile, Config_Exception_NotDone{
		String xml;
		try {
			xml = com.awl.fc2.selector.launcher.Config.getInstance().getXML();
			XMLParser parser = new XMLParser(xml);
			
			try {
				parser.query("CRD");
				while(parser.hasNext()){
					String crdToLoad;
					try {
						crdToLoad = parser.getNextValue();
					} catch (XMLParser_Exception_NoNextValue e) {
						throw(new Config_Exeception_MalFormedConfigFile(e.getMessage()));
					}
					m_vecCards.add(getCardFromCRD(crdToLoad));
				}
				
			} catch (XMLParser_Exception_NO_ATTRIBUTE e) {
				throw(new Config_Exeception_MalFormedConfigFile(e.getMessage()));
			}
		} catch (Config_Exception_NotDone e1) {
			throw(e1);
		}
		
		
		
		
	}
	
	/**
	 * Load a CRD file, check the signature and create an {@link InfoCard} object
	 * @param path where is located the crd file
	 * @return the {@link InfoCard} contained in the crd file
	 * @throws Config_Exeception_MalFormedConfigFile
	 */
	public InfoCard getCardFromCRD(String path) throws Config_Exeception_MalFormedConfigFile{
		trace("Loading on " + path);
		try {
			File file = new File(path);
			FileInputStream in = new FileInputStream(file);
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
			Element root = XmlFileUtil.readXml(new ByteArrayInputStream(toRet.getBytes())).getRootElement();
			SignedInfoCard card = new SignedInfoCard(root);
			//trace("READ IMG = " + card.getBase64BinaryCardImage());
			trace("Read the current image, and save it");
			byte [] ImgRaw = com.utils.Base64.decode(card.getBase64BinaryCardImage());
			if(ImgRaw != null){
				ImageIcon imgI = new ImageIcon(ImgRaw);
				Image img = imgI.getImage();
//				int height = 135;
//				int width = imgI.getIconWidth()*135/imgI.getIconHeight();
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
				Image Resized =  bi.getScaledInstance(width*newHeight/height,newHeight , BufferedImage.SCALE_DEFAULT);
								
					BufferedImage bi2 = new BufferedImage(width*newHeight/height,newHeight,BufferedImage.TYPE_INT_RGB);//img.getWidth(null),img.getHeight(null));
					Graphics2D g3 = bi2.createGraphics();
					// Draw img into bi so we can write it to file.
					g3.drawImage(Resized, 0, 0, null);
					g3.dispose();
				
				
				
				String filename = card.getCardName().replace("/", "_");
				filename = filename.replace("\\", "_");
				try {
					String imgFolder = Config.getInstance().getImgFolder();
					ImageIO.write(bi2, "jpg", new File(imgFolder+filename+".jpg"));
					card.setBase64BinaryCardImage(imgFolder+filename+".jpg");
				} catch (Config_Exception_NotDone e) {
					trace("Missing <IMG> folder in the config_selecteur.xml");
				}
				
			}
			
			return card;
		} catch (ValidityException e) {
			throw(new Config_Exeception_MalFormedConfigFile(e.getMessage()));
		} catch (IOException e) {
			throw(new Config_Exeception_MalFormedConfigFile(e.getMessage()));
		} catch (ParsingException e) {
			throw(new Config_Exeception_MalFormedConfigFile(e.getMessage()));		}
		catch (org.xmldap.exceptions.ParsingException e) {	
			throw(new Config_Exeception_MalFormedConfigFile(e.getMessage()));		
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
		//try {
//			String username =Selector.getInstance().getUI().question("New Session", "-What's your username ?");
//			trace("UserName : "+username);
			theSesison.setUsername("noname");
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
	
	

