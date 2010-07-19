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

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JLabel;

import com.awl.fc2.selector.exceptions.Config_Exception_NotDone;
import com.awl.fc2.selector.launcher.Config;
import com.utils.XMLParser;
import com.utils.execeptions.XMLParser_Exception_NO_ATTRIBUTE;

public class Dialog_YesNo extends JDialog{

	private static final long serialVersionUID = 1L;

	protected JDialog box;
	Window window = null;
	String imgs;
	
	Dimension size = new Dimension();
	Point point = new Point();
	boolean response = false;
	
	public Dialog_YesNo(Window parent){
		box = new JDialog(parent, Dialog.ModalityType.DOCUMENT_MODAL);
		window = parent;
		
		try {
			imgs = XMLParser.getFirstValue(Config.getInstance().getXML(),"IMGS");
			imgs += "interface/";
		} catch (XMLParser_Exception_NO_ATTRIBUTE e) {
			e.printStackTrace();
		} catch (Config_Exception_NotDone e) {
			e.printStackTrace();
		}
	
	}
	
	public void settings(String Title, String caption){
		box.setUndecorated(true);
		box.setTitle(Title);
		box.setResizable(false);
		
		ImagePanel panel = new ImagePanel(new ImageIcon(imgs+"dialog_small.png").getImage());
		panel.setLayout(null);
		panel.setLocation(0,0);
		box.setSize(panel.getSize());
		box.setLocationRelativeTo(window);
		point = box.getLocation();
		box.setLocation(point.x, point.y + 35);
		
		
		JLabel label = new JLabel(caption);
		size = label.getPreferredSize();
		label.setBounds((200-size.width)/2,8,size.width,size.height);
		panel.add(label);
		
		final ImageButton yes = new ImageButton(new ImageIcon(imgs+"yes.png"));
		yes.setRolloverIcon(new ImageIcon(imgs+"yes2.png"));
		yes.setPressedIcon(new ImageIcon(imgs+"yes3.png"));
		yes.setLocation(105,30);
		yes.setToolTipText("Yes");
		panel.add(yes);
		
		yes.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				
				response = true;
				box.dispose();
			}
		});
		
		final ImageButton no = new ImageButton(new ImageIcon(imgs+"no.png"));
		no.setRolloverIcon(new ImageIcon(imgs+"no2.png"));
		no.setPressedIcon(new ImageIcon(imgs+"no3.png"));
		no.setLocation(55,30);
		no.setToolTipText("No");
		panel.add(no);
		
		no.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				
				response = false;
				box.dispose();
			}
		});
		
		panel.addMouseListener(new MouseAdapter() {  
			public void mousePressed(MouseEvent e) {  
				if(!e.isMetaDown()){  
					point.x = e.getX();
					point.y = e.getY();
				}  
			}  
		});  
		
		panel.addMouseMotionListener(new MouseMotionAdapter() {  
			public void mouseDragged(MouseEvent e) {  
				if(!e.isMetaDown()){  
					Point p = box.getLocation();  
					box.setLocation(p.x + e.getX() - point.x, p.y + e.getY() - point.y); 
				}  
			}  
		});
		
		box.getRootPane().setDefaultButton(no);
		box.setContentPane(panel);
		box.setVisible(true);
				
	}
	
	public boolean getResponse(){
		
		return response;
	}
	
}
