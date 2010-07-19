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
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.*;

import com.awl.fc2.selector.exceptions.Config_Exception_NotDone;
import com.awl.fc2.selector.launcher.Config;
import com.utils.XMLParser;
import com.utils.execeptions.XMLParser_Exception_NO_ATTRIBUTE;

/**
 * Default dialog box with text input for the Swing UI. Locks parent JFrame.
 * The title and message displayed in the box are given by two String parameters.
 * The boolean parameter "pwd" indicates if the text input is a password or not.
 * @author Maupin Mathieu
 *
 */

public class Dialog_Pwd extends JDialog{

	private static final long serialVersionUID = 1L;
	
	protected JDialog jd;
	JPasswordField  pwdField = new JPasswordField();
	String response = "";
	String path, imgs;
	
	Dimension size = new Dimension();
	Point point = new Point();
	
	public Dialog_Pwd(){
		
		jd = new JDialog(MainWindow.getInstance(), Dialog.ModalityType.DOCUMENT_MODAL);
		
		try {
			path = XMLParser.getFirstValue(Config.getInstance().getXML(),"DEFAULT_PATH");
			imgs = path+"/imgs/interface/";
			
			
		} catch (XMLParser_Exception_NO_ATTRIBUTE e) {
			e.printStackTrace();
		} catch (Config_Exception_NotDone e) {
			e.printStackTrace();
		}
		
	}
	
	public void settings(String title, String msg){
		jd.setUndecorated(true);
		jd.setTitle(title);
		jd.setSize(300,100);
		jd.setResizable(false);
		jd.setLocationRelativeTo(MainWindow.getInstance());
				
		ImagePanel panel = new ImagePanel(new ImageIcon(imgs+"dialog2.png").getImage());
		panel.setLocation(0,0);
		
		jd.setContentPane(panel);
		
		final ImageButton closeButton = new ImageButton(new ImageIcon(imgs+"closediag.png"));
		closeButton.setLocation(300-24,10);
		closeButton.setToolTipText("Cancel");
		closeButton.setRolloverIcon(new ImageIcon(imgs+"closediag2.png"));
		closeButton.setPressedIcon(new ImageIcon(imgs+"closediag3.png"));
		panel.add(closeButton);
		
		closeButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				jd.dispose();
			}
		});
		
		JLabel label = new JLabel(msg);
		label.setBounds(18,10,250,22);
		panel.add(label);
		
		
		final JButton valider = new JButton("Valider");
		size = valider.getPreferredSize();
		valider.setBounds((300-size.width)/2,62,size.width,size.height);
		jd.getRootPane().setDefaultButton(valider);
		panel.add(valider);
		
		valider.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				char[] temp = pwdField.getPassword();
				response = "";
				for(int i=0;i<temp.length;i++){
					response += temp[i];
				}
				jd.dispose();
			}
		});
		
		
		pwdField.setBounds(30,35,200,22);
		pwdField.setOpaque(false);
		pwdField.setBorder(null);
		
		jd.addWindowFocusListener(new WindowAdapter() {
		    public void windowGainedFocus(WindowEvent e) {
		        pwdField.requestFocusInWindow();
		    }
		});
		
		panel.add(pwdField);
			
		
				
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
					Point p = jd.getLocation();  
					jd.setLocation(p.x + e.getX() - point.x, p.y + e.getY() - point.y); 
				}  
			}  
		});
		
		
		jd.setVisible(true);
		jd.requestFocus();
				
	}
	
	public String getResponse(){
		return response;
	}
	
}
