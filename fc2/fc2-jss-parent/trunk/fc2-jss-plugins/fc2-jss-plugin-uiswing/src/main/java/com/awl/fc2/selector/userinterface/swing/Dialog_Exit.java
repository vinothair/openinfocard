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
import java.awt.event.MouseMotionAdapter;

import javax.swing.*;

import com.awl.fc2.selector.exceptions.Config_Exception_NotDone;
import com.awl.fc2.selector.launcher.Config;
import com.utils.XMLParser;
import com.utils.execeptions.XMLParser_Exception_NO_ATTRIBUTE;

/**
 * This dialog box is displayed when the user clicks the close button top right of main JFrame.
 * Locks parent JFrame.
 * Three options given : quit, hide or cancel.
 * Quit will close the application. Hide will minimize it to system tray. Cancel = dispose.
 * @author Maupin Mathieu
 *
 */
public class Dialog_Exit extends JDialog{

	private static final long serialVersionUID = 1L;
	
	protected JDialog ex;
	int choice = 2;
	String imgs;
	
	Dimension size = new Dimension();
	Point point = new Point();
	
	public Dialog_Exit(){
		ex = new JDialog(MainWindow.getInstance(), Dialog.ModalityType.DOCUMENT_MODAL);
		
		try {
			imgs = XMLParser.getFirstValue(Config.getInstance().getXML(),"IMGS");
			imgs += "interface/";
		} catch (XMLParser_Exception_NO_ATTRIBUTE e) {
			e.printStackTrace();
		} catch (Config_Exception_NotDone e) {
			e.printStackTrace();
		}
	
	}
	
	public void settings(){
		ex.setUndecorated(true);
		ex.setTitle("Quit?");
		ex.setSize(300,100);
		ex.setResizable(false);
		ex.setLocationRelativeTo(MainWindow.getInstance());
		
		ImagePanel panel = new ImagePanel(new ImageIcon(imgs+"dialog.png").getImage());
		panel.setLayout(null);
		panel.setLocation(0,0);
		
		JLabel label = new JLabel("Do you really want to quit?");
		size = label.getPreferredSize();
		label.setBounds((300-size.width)/2,20,size.width,size.height);
		panel.add(label);
				
		final JButton quit = new JButton("Quit");
		quit.setToolTipText("Quit JSS now");
		//size = quit.getPreferredSize();
		quit.setBounds(25,55,80,25);
		
		final JButton hide = new JButton("Hide");
		hide.setToolTipText("Minimize JSS to system tray");
		//size = hide.getPreferredSize();
		hide.setBounds(110,55,80,25);
		
		final JButton cancel = new JButton("Cancel");
		cancel.setToolTipText("Return to JSS");
		//size = cancel.getPreferredSize();
		cancel.setBounds(195,55,80,25);
		
		panel.add(quit);
		panel.add(hide);
		panel.add(cancel);
		
		quit.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				choice=0;
				ex.dispose();
			}
		});
		
		hide.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				choice=1;
				ex.dispose();
			}
		});
		
		cancel.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				ex.dispose();
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
					Point p = ex.getLocation();  
					ex.setLocation(p.x + e.getX() - point.x, p.y + e.getY() - point.y); 
				}  
			}  
		});
		
		ex.getRootPane().setDefaultButton(quit);
		ex.setContentPane(panel);
		ex.setVisible(true);
				
	}
	
	public int getChoice(){
		return choice;
	}

}
