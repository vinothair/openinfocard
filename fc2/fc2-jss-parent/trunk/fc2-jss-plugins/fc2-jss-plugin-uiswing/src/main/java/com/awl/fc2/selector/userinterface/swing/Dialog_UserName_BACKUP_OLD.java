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
import java.io.*;

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

public class Dialog_UserName_BACKUP_OLD extends JDialog{

	private static final long serialVersionUID = 1L;
	
	protected JDialog jd;
	JTextField textField = new JTextField();
	String response = null;
	String path, imgs;
	
	Dimension size = new Dimension();
	Point point = new Point();
	
	File login;
	String[] content = new String[3];
	BufferedReader reader = null;
	Writer writer = null;
	boolean rm;


	public Dialog_UserName_BACKUP_OLD(){
		
		jd = new JDialog(MainWindow.getInstance(), Dialog.ModalityType.DOCUMENT_MODAL);
		
		try {
			path = XMLParser.getFirstValue(Config.getInstance().getXML(),"DEFAULT_PATH");
			imgs = path+"/imgs/interface/";
			
			
		} catch (XMLParser_Exception_NO_ATTRIBUTE e) {
			e.printStackTrace();
		} catch (Config_Exception_NotDone e) {
			e.printStackTrace();
		}
		
		
		login = new File(path+"/login.txt");
		if(!login.exists()){
		      try {
				login.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		

        try
        {
            reader = new BufferedReader(new FileReader(login));
            String text = null;
            int i = 0;

            while (i < 3)
            {
            	text = reader.readLine();
            	if (text == null) text = "";
            	content[i++] = text;
            }
        } catch (FileNotFoundException e)
        {
            e.printStackTrace();
        } catch (IOException e)
        {
            e.printStackTrace();
        } finally
        {
            try
            {
                if (reader != null)
                {
                    reader.close();
                }
            } catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        
        for(int i=0;i<3;i++)System.out.println(content[i]);
        rm = Boolean.valueOf(content[0]);

	
	}
	
	public void settings(String title, String msg){
		
		//GENERAL SETTINGS//
		jd.setUndecorated(true);
		jd.setTitle(title);
		jd.setSize(300,300);
		jd.setResizable(false);
		jd.setLocationRelativeTo(MainWindow.getInstance());
				
		ImagePanel panel = new ImagePanel(new ImageIcon(imgs+"dialog3.png").getImage());
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
		//END OF GENERAL SETTINGS//
		
		
		//PROFILE SETTINGS//
		final JLabel profileName = new JLabel();
		profileName.setFont(new Font("Arial", Font.BOLD, 16));
		profileName.setText("Robert");
		profileName.setBounds((300-profileName.getPreferredSize().width)/2,30,profileName.getPreferredSize().width,profileName.getPreferredSize().height);
		panel.add(profileName);
		
		final ImagePanel avatarbg = new ImagePanel(new ImageIcon(imgs+"avatarbg.png").getImage());
		avatarbg.setLocation(98,60);
		panel.add(avatarbg);
		
		final JPanel avatarField = new JPanel();
		avatarField.setLayout(null);
		avatarField.setSize(100,100);
		avatarField.setLocation(2,2);
		avatarField.setOpaque(false);
		avatarbg.add(avatarField);
		
		final ImagePanel avatar = new ImagePanel(new ImageIcon(imgs+"avatar-maup.png").getImage());
		avatar.setLocation(0,0);
		avatarField.add(avatar);
		
		final ImageButton left = new ImageButton(new ImageIcon(imgs+"leftb.png"));
		left.setLocation(50,102);
		left.setToolTipText("Previous profile");
		left.setRolloverIcon(new ImageIcon(imgs+"leftb2.png"));
		left.setPressedIcon(new ImageIcon(imgs+"leftb3.png"));
		left.setDisabledIcon(new ImageIcon(imgs+"left4.png"));
		//left.setEnabled(false);
		panel.add(left);
		
		
		final ImageButton right = new ImageButton(new ImageIcon(imgs+"rightb.png"));
		right.setLocation(230,102);
		right.setToolTipText("Next profile");
		right.setRolloverIcon(new ImageIcon(imgs+"rightb2.png"));
		right.setPressedIcon(new ImageIcon(imgs+"rightb3.png"));
		right.setDisabledIcon(new ImageIcon(imgs+"right4.png"));
		panel.add(right);
		
		final ImageButton delete = new ImageButton(new ImageIcon(imgs+"minus.png"));
		delete.setLocation(131,157);
		delete.setToolTipText("Delete this profile");
		delete.setRolloverIcon(new ImageIcon(imgs+"minus2.png"));
		delete.setPressedIcon(new ImageIcon(imgs+"minus3.png"));
		panel.add(delete, new Integer(1));
		
//		final ImageLabel neutral = new ImageLabel(new ImageIcon(imgs+"neutral.png"));
//		neutral.setLocation(142,157);
//		panel.add(neutral, new Integer(1));
		
		final ImageButton create = new ImageButton(new ImageIcon(imgs+"plus.png"));
		create.setLocation(151,157);
		create.setToolTipText("Create a new profile");
		create.setRolloverIcon(new ImageIcon(imgs+"plus2.png"));
		create.setPressedIcon(new ImageIcon(imgs+"plus3.png"));
		panel.add(create, new Integer(1));
		//END OF PROFILE SETTINGS//
		
		
		//INPUT SETTINGS//
		JLabel label = new JLabel(msg);
		label.setBounds(18,200,255,22);
		panel.add(label);
		
		
		final JButton valider = new JButton("Valider");
		size = valider.getPreferredSize();
		valider.setBounds((425-size.width)/2,252,size.width,size.height);
		jd.getRootPane().setDefaultButton(valider);
		panel.add(valider);
		
		valider.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				response = textField.getText();
				if(rm != Boolean.valueOf(content[0])){
					//METTRE A JOUR LA VALEUR
					content[0] = String.valueOf(rm);
					if(rm == false){
						//SUPPRIMER LES DONNEES UTILISATEUR
						content[1] = "";
						content[2] = "";
					}
				}
				if(rm == true){
					if(!response.equals(content[1])){
						//METTRE A JOUR LA DONNEE NOM UTILISATEUR ET EFFACER LE PWD
						content[1] = response;
						content[2] = "";
					}
				}
				//login.txt UPDATE
				update();
				
				jd.dispose();
			}
		});
		
		
		textField.setBounds(30,225,200,22);
		textField.setOpaque(false);
		textField.setBorder(null);
		
		if(rm == true){
				textField.setText(content[1]);
				textField.selectAll();
		}

		jd.addWindowFocusListener(new WindowAdapter() {
		    public void windowGainedFocus(WindowEvent e) {
		        textField.requestFocusInWindow();
		    }
		});
		
		panel.add(textField);
		
		final JCheckBox checkbox = new JCheckBox("Remember me");
		checkbox.setLocation(44,255);
		checkbox.setOpaque(false);
		checkbox.setBorder(null);
		checkbox.setSize(checkbox.getPreferredSize());
		checkbox.setSelected(Boolean.valueOf(content[0]));
		panel.add(checkbox);
		checkbox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				rm = !rm;
			}
		});
		//END OF INPUT SETTINGS//
		
		
		jd.setVisible(true);
		jd.requestFocus();
				
	}
	
	public String getResponse(){
		return response;
	}
	
	public void update(){
		
		//login.txt UPDATE
		StringBuffer update = new StringBuffer();
		update.append(content[0]).append(System.getProperty("line.separator"))
			.append(content[1]).append(System.getProperty("line.separator"))
				.append(content[2]);
		try {
			writer = new BufferedWriter(new FileWriter(login));
			writer.write(update.toString());
		} catch (IOException e1) {
			e1.printStackTrace();
		} finally {
			try
            {
                if (writer != null)
                {
                    writer.close();
                }
            } catch (IOException e2)
            {
                e2.printStackTrace();
            }
		}
		
	}
	
}
