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
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.*;

import com.awl.fc2.selector.exceptions.Config_Exception_NotDone;
import com.awl.fc2.selector.launcher.Config;
import com.utils.XMLParser;
import com.utils.execeptions.XMLParser_Exception_NO_ATTRIBUTE;

/**
 * 
 * @author Maupin Mathieu
 *
 */

public class Dialog_EditProfile extends JDialog{

	private static final long serialVersionUID = 1L;
	
	protected JDialog jd;
	JLabel profileName;
	JLabel avatar;
	JLabel userName;
	JLabel password1;
	JLabel password2;
	JLabel error;
	JLabel profileNameField;
	JTextField urlField;
	JTextField userNameField;
	JPasswordField passwordField1;
	JPasswordField passwordField2;
	JRadioButton maleButton;
	JRadioButton femaleButton;
	JRadioButton urlButton;
	ButtonGroup group = new ButtonGroup();
	String[] response = new String[4];
	String path, imgs;
	
	Dimension size = new Dimension();
	
	boolean open = false;


	public Dialog_EditProfile(JDialog parent, String[] content){
		
		jd = new JDialog(parent, Dialog.ModalityType.DOCUMENT_MODAL);
		
		try {
			path = XMLParser.getFirstValue(Config.getInstance().getXML(),"DEFAULT_PATH");
			imgs = path+"/imgs/interface/";
			
			
		} catch (XMLParser_Exception_NO_ATTRIBUTE e) {
			e.printStackTrace();
		} catch (Config_Exception_NotDone e) {
			e.printStackTrace();
		}
		
		settings(parent, content);
		
	}
	
	public void settings(JDialog parent, final String[] content){
		
		//GENERAL SETTINGS//
		jd.setUndecorated(true);
		jd.setTitle("New porfile");
		jd.setSize(300,300);
		jd.setResizable(false);
		jd.setLocationRelativeTo(parent);
		
		final ImagePanel panel = new ImagePanel(new ImageIcon(imgs+"dialog3.png").getImage());
		panel.setLocation(0,0);
		
		jd.setContentPane(panel);
		
		final ImageButton closeButton = new ImageButton(new ImageIcon(imgs+"closediag.png"));
		closeButton.setLocation(300-24,10);
		closeButton.setToolTipText("Cancel profile creation");
		closeButton.setRolloverIcon(new ImageIcon(imgs+"closediag2.png"));
		closeButton.setPressedIcon(new ImageIcon(imgs+"closediag3.png"));
		panel.add(closeButton);
		
		closeButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				response[0] = "";
				jd.dispose();
			}
		});
		
		//END OF GENERAL SETTINGS//
		
		
		
		
		//INPUT SETTINGS//
		
		JLabel label = new JLabel("EDIT A PROFILE");
		size = label.getPreferredSize();
		label.setBounds(15,9,size.width,size.height);
		panel.add(label);
		
		profileName = new JLabel("Profile name:");
		size = profileName.getPreferredSize();
		profileName.setBounds(20,40,size.width,size.height);
		panel.add(profileName);
		
		profileNameField = new JLabel(content[0]);
		profileNameField.setBounds(30+size.width,35,230-size.width,26);
		//profileNameField.setOpaque(false);
		//profileNameField.setBorder(null);
		panel.add(profileNameField);
		
		avatar = new JLabel("Avatar:");
		size = avatar.getPreferredSize();
		avatar.setBounds(20,73,size.width,size.height);
		panel.add(avatar);
		
		maleButton = new JRadioButton("Male");
		maleButton.setSize(maleButton.getPreferredSize());
		maleButton.setLocation(30+size.width,70);
		maleButton.setOpaque(false);
		panel.add(maleButton);
		
		maleButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				urlField.setVisible(false);
				//urlField.setEnabled(false);
				userNameField.requestFocusInWindow();
			}
		});
		
		femaleButton = new JRadioButton("Female");
		femaleButton.setSize(femaleButton.getPreferredSize());
		femaleButton.setLocation(30+size.width,90);
		femaleButton.setOpaque(false);
		panel.add(femaleButton);
		
		femaleButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				urlField.setVisible(false);
				//urlField.setEnabled(false);
				userNameField.requestFocusInWindow();
			}
		});
		
		urlButton = new JRadioButton("Import from:");
		urlButton.setSize(urlButton.getPreferredSize());
		urlButton.setLocation(30+size.width,110);
		urlButton.setOpaque(false);
		panel.add(urlButton);
		
		urlButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				urlField.setVisible(true);
				//urlField.setEnabled(true);
				urlField.requestFocusInWindow();
			}
		});
		
	    group.add(maleButton);
	    group.add(femaleButton);
	    group.add(urlButton);
	    
	    urlField = new JTextField();
	    urlField.setBounds(60+size.width,133,210-size.width,26);
		urlField.setOpaque(false);
		urlField.setVisible(false);
		panel.add(urlField);
		
		if (content[1].equals("avatar-male.png")) maleButton.setSelected(true);
		else if (content[1].equals("avatar-female.png")) femaleButton.setSelected(true);
		else {
			urlButton.setSelected(true);
			urlField.setText(content[1]);
			urlField.setVisible(true);
		}
		
		userName = new JLabel("User name:");
		size = userName.getPreferredSize();
		userName.setBounds(20,175,size.width,size.height);
		panel.add(userName);
		
		userNameField = new JTextField(content[2]);
		userNameField.setBounds(30+size.width,170,240-size.width,26);
		userNameField.setOpaque(false);
		//userNameField.setBorder(null);
		panel.add(userNameField);
		
		password1 = new JLabel("Password (if new):");
		size = password1.getPreferredSize();
		password1.setBounds(20,200,size.width,size.height);
		panel.add(password1);
		
		passwordField1 = new JPasswordField();
		passwordField1.setBounds(30+size.width,195,240-size.width,26);
		passwordField1.setOpaque(false);
		//passwordField1.setBorder(null);
		panel.add(passwordField1);
		
		password2 = new JLabel("Confirm password:");
		size = password2.getPreferredSize();
		password2.setBounds(20,225,size.width,size.height);
		panel.add(password2);
		
		passwordField2 = new JPasswordField();
		passwordField2.setBounds(30+size.width,220,240-size.width,26);
		passwordField2.setOpaque(false);
		//passwordField2.setBorder(null);
		panel.add(passwordField2);
		
		error = new JLabel("");
		error.setForeground(Color.RED);
		size = error.getPreferredSize();
		error.setBounds(20,265,size.width,size.height);
		panel.add(error);
		
		final JButton valider = new JButton("Valider");
		size = valider.getPreferredSize();
		valider.setBounds((450-size.width)/2,260,size.width,size.height);
		jd.getRootPane().setDefaultButton(valider);
		panel.add(valider);
		
		valider.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				
				if(userNameField.getText().equals("")){
						error.setText("Missing user name");
						size = error.getPreferredSize();
						error.setBounds(20,265,size.width,size.height);
						error.repaint();
						return;
				}
				
				if(!getPassword(passwordField1).equals(getPassword(passwordField2))){
					error.setText("Passwords do not match");
					size = error.getPreferredSize();
					error.setBounds(20,265,size.width,size.height);
					error.repaint();
					return;
				}
				
				if(urlField.isVisible() && urlField.getText().equals("")){
					error.setText("Missing avatar URL");
					size = error.getPreferredSize();
					error.setBounds(20,265,size.width,size.height);
					error.repaint();
					return;
				}
				
				
				response[0] = content[0];
				if (urlButton.isSelected()) response[1] = urlField.getText();
				else if (femaleButton.isSelected()) response[1] = "avatar-female.png";
				else response[1] = "avatar-male.png";
				response[2] = userNameField.getText();
				if (getPassword(passwordField1).equals("")) response[3] = content[3];
				else response[3] = getPassword(passwordField1);
				for(int i=0;i<4;i++)System.out.println(response[i]);
				
				jd.dispose();
			}
		});
		
		

		jd.addWindowFocusListener(new WindowAdapter() {
		    public void windowGainedFocus(WindowEvent e) {
		    	if(open) return;
		        userNameField.requestFocusInWindow();
		        open = true;
		    }
		});
		
		
		//END OF INPUT SETTINGS//
		
		
		jd.setVisible(true);
		jd.requestFocus();
				
	}
	
	public String getPassword(JPasswordField pf){
		
		char[] temp = pf.getPassword();
		String pwd = "";
		for(int i=0;i<temp.length;i++){
			pwd += temp[i];
		}
		
		return pwd;
		
	}
	
	public String[] getResponse(){
		return response;
	}
	
}
