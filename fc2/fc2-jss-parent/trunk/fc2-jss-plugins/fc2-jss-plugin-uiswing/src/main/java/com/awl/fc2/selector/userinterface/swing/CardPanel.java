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

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageFilter;
import java.awt.image.ImageProducer;
import java.awt.image.ReplicateScaleFilter;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import com.awl.fc2.selector.exceptions.Config_Exception_NotDone;
import com.awl.fc2.selector.launcher.Config;
import com.utils.XMLParser;
import com.utils.execeptions.XMLParser_Exception_NO_ATTRIBUTE;

/**
 * 
 * @author Maupin Mathieu
 *
 */

public class CardPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	
	String imgs,cardimgs;
	int index = 0;
	JLabel cardName = new JLabel();
	
	final ImagePanel[] cardbg = new ImagePanel[3];
	final ImagePanel[] card = new ImagePanel[3];
	final ImageLabel[] cardmask = new ImageLabel[3];
	
	public CardPanel(final String[] claims, final String[] urls, final String[] labels){
		
		super();
		
		try {
			imgs = XMLParser.getFirstValue(Config.getInstance().getXML(),"IMGS");
			cardimgs = imgs;
			imgs += "interface/";
		} catch (XMLParser_Exception_NO_ATTRIBUTE e) {
			e.printStackTrace();
		} catch (Config_Exception_NotDone e) {
			e.printStackTrace();
		}
		
		
		build(claims, urls, labels);
		
	}
	
	public void build(final String[] claims, final String[] urls, final String[] labels){
		
		setLayout(null);
		setSize(394,290);
		setLocation(0,0);
		setOpaque(false);
		
		
		//TESTING INFO POP UP
		
		final ImageLabel info = new ImageLabel(new ImageIcon(imgs+"info.png"));
		info.setLocation(15,260);
		add(info);
		
		final JLabel infoLabel = new JLabel("Informations recherchées");
		infoLabel.setBounds(50,267,infoLabel.getPreferredSize().width,infoLabel.getPreferredSize().height);
		add(infoLabel);
		
		info.addMouseListener(new MouseAdapter() {  
			public void mouseEntered(MouseEvent e) {
				if (!MainWindow.getInstance().isActive())return;
				Point position = MainWindow.getInstance().getLocation();
				InfoPopUp.getFreshInstance().settings(position,claims);
			}  
		});
		
		info.addMouseListener(new MouseAdapter() {  
			public void mouseExited(MouseEvent e) {
				InfoPopUp.getInstance().dispose();
			}  
		});
		
		//TEST END
		
		
		//TESTING CARD DISPLAY
		
		cardbg[1] = new ImagePanel(new ImageIcon(imgs+"cardbg.png").getImage());
		cardbg[1].setLocation(122,30);
		add(cardbg[1]);
		
		card[1] = new ImagePanel(new ImageIcon(urls[index]).getImage());
		card[1].setLocation((cardbg[1].getWidth()-card[1].getWidth())/2,(cardbg[1].getHeight()-card[1].getHeight())/2);
		cardbg[1].add(card[1]);
		
		if (urls.length>1){
			for(int i=0;i<3;i++) {
				cardmask[i] = new ImageLabel(new ImageIcon(imgs+"cardmask.png"));
				cardmask[i].setLocation(0,-200);
			}
			
			card[1].add(cardmask[1]);
			
			cardbg[0] = new ImagePanel(new ImageIcon(imgs+"cardbgsmall.png").getImage());
			cardbg[0].setLocation(22,45);
			add(cardbg[0]);
			
			card[0] = new ImagePanel(new ImageIcon(imgs+"nocard.png").getImage());
			card[0].setLocation((cardbg[0].getWidth()-card[0].getWidth())/2,(cardbg[0].getHeight()-card[0].getHeight())/2);
			cardbg[0].add(card[0]);
			
			card[0].add(cardmask[0]);
			
			
			cardbg[2] = new ImagePanel(new ImageIcon(imgs+"cardbgsmall.png").getImage());
			cardbg[2].setLocation(297,45);
			add(cardbg[2]);
			
			Image temp = getResized(urls[index+1]);
			card[2] = new ImagePanel(temp);
			card[2].setLocation((cardbg[2].getWidth()-card[2].getWidth())/2,(cardbg[2].getHeight()-card[2].getHeight())/2);
			cardbg[2].add(card[2]);
			
			card[2].add(cardmask[2]);
		}
		
		cardName.setText(labels[index]);
		cardName.setFont(new Font("Arial", Font.BOLD, 16));
		cardName.setBounds(197-cardName.getPreferredSize().width/2,0,cardName.getPreferredSize().width,cardName.getPreferredSize().height);
		add(cardName);
		
		//TEST END
		
		
		//TESTING ARROWS
		
		if (urls.length>1){
			final ImageButton left = new ImageButton(new ImageIcon(imgs+"left.png"));
			left.setLocation(52,105);
			left.setToolTipText("Previous card");
			left.setRolloverIcon(new ImageIcon(imgs+"left2.png"));
			left.setPressedIcon(new ImageIcon(imgs+"left3.png"));
			left.setDisabledIcon(new ImageIcon(imgs+"left4.png"));
			
			left.setEnabled(false);
			
			add(left);
			
			
			final ImageButton right = new ImageButton(new ImageIcon(imgs+"right.png"));
			right.setLocation(322,105);
			right.setToolTipText("Next card");
			right.setRolloverIcon(new ImageIcon(imgs+"right2.png"));
			right.setPressedIcon(new ImageIcon(imgs+"right3.png"));
			right.setDisabledIcon(new ImageIcon(imgs+"right4.png"));
			
			if(labels.length<=1)right.setEnabled(false);
			
			add(right);
	
			
			left.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e){
					
					SwingUtilities.invokeLater(new Runnable() {
						public void run() {
							
							final Timer timer = new Timer(17, null);
					        final int steps = 20;
					        
					        left.setEnabled(false);
					        right.setEnabled(false);
					        
					        index--;
					        cardName.setText(labels[index]);
					        cardName.setBounds(197-cardName.getPreferredSize().width/2,0,cardName.getPreferredSize().width,cardName.getPreferredSize().height);
					        cardName.repaint();
					        
					        timer.addActionListener(new ActionListener() {
					            int count = 0 ;
	
					            public void actionPerformed(ActionEvent e) {
					                if (count <= steps/2) {
					                	for (int i=0; i<3; i++){
					                		cardmask[i].setLocation(0,-200+count*20);
						                	cardmask[i].repaint();
					                	}
					                	
					                	if (count==10){
					                		card[1].setNewImgAndSize(new ImageIcon(urls[index]).getImage());
					                		update(1);
					                		if (index < 1) card[0].setNewImgAndSize(new ImageIcon(imgs+"nocard.png").getImage());
					                		else card[0].setNewImgAndSize(getResized(urls[index-1]));
					                		update(0);
					                		card[2].setNewImgAndSize(getResized(urls[index+1]));
					                		update(2);
					                	}
					            		count++;
					            	} else {
					            		if (count > steps/2 && count <=steps) {
					            			for (int i=0; i<3; i++){
					            				cardmask[i].setLocation(0,200-count*20);
					            				cardmask[i].repaint();
					            			}
						            		count++;
					            		} else {
					            			if(index>0)left.setEnabled(true);
					            			if(index<labels.length-1)right.setEnabled(true);
					            			timer.stop();
					            		}
					            	}
					            }
					        });
					        timer.start();
						}
					});
				}
			});
			
			
			right.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e){
					//traceConsole("RIGHT");
					
					SwingUtilities.invokeLater(new Runnable() {
						public void run() {
							
							final Timer timer = new Timer(17, null);
					        final int steps = 20;
					        
					        left.setEnabled(false);
					        right.setEnabled(false);
					        
					        index++;
					        cardName.setText(labels[index]);
					        cardName.setBounds(197-cardName.getPreferredSize().width/2,0,cardName.getPreferredSize().width,cardName.getPreferredSize().height);
					        cardName.repaint();
					        
					        timer.addActionListener(new ActionListener() {
					            int count = 0 ;
	
					            public void actionPerformed(ActionEvent e) {
					                if (count <= steps/2) {
					                	for (int i=0; i<3; i++){
					                		cardmask[i].setLocation(0,-200+count*20);
					                		cardmask[i].repaint();
					                	}
					                	if (count==10){
					                		card[1].setNewImgAndSize(new ImageIcon(urls[index]).getImage());
					                		update(1);
					                		if (index >= urls.length-1) card[2].setNewImgAndSize(new ImageIcon(imgs+"nocard.png").getImage());
					                		else card[2].setNewImgAndSize(getResized(urls[index+1]));
					                		update(2);
					                		card[0].setNewImgAndSize(getResized(urls[index-1]));
					                		update(0);
					                	}
					            		count++;
					            	} else {
					            		if (count > steps/2 && count <=steps) {
					            			for (int i=0; i<3; i++){
					            				cardmask[i].setLocation(0,200-count*20);
					            				cardmask[i].repaint();
					            			}
						            		count++;
					            		} else {
					            			if(index>0)left.setEnabled(true);
					            			if(index<labels.length-1)right.setEnabled(true);
					            			timer.stop();
					            		}
					            	}
					            }
					        });
					        timer.start();
						}
					});
				}
			});
		
		}
		//TEST END
		
	}
	
	public int getIndex(){
		
		return index;
	}
	
	
	public Image getResized(String url) {
		
		Image temp;
		MediaTracker media = new MediaTracker(this);
		temp = new ImageIcon(url).getImage();
	    media.addImage(temp,0);
	    try {
	      media.waitForID(0);
	      ImageFilter replicate = new ReplicateScaleFilter(temp.getWidth(null)/2, temp.getHeight(null)/2);
	      ImageProducer prod = new FilteredImageSource(temp.getSource(),replicate);
	      temp = createImage(prod);
	      media.addImage(temp,1);
	      media.waitForID(1);

	    } catch(InterruptedException e) {}
	    
	    return temp;
		
	}
	
	public void update(int i) {
		
		if (i>2) return;
		card[i].setLocation((cardbg[i].getWidth()-card[i].getWidth())/2,(cardbg[i].getHeight()-card[i].getHeight())/2);
		card[i].repaint();
		
	}
	
	
}
