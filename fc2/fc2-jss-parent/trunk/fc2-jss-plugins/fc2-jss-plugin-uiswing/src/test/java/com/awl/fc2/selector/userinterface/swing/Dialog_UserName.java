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
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.*;

import com.awl.fc2.selector.Selector;
import com.awl.fc2.selector.exceptions.Config_Exception_NotDone;
import com.awl.fc2.selector.exceptions.Config_Exeception_MalFormedConfigFile;
import com.awl.fc2.selector.exceptions.Config_Exeception_UnableToReadConfigFile;
import com.awl.fc2.selector.launcher.Config;
import com.awl.logger.Logger;
import com.utils.XMLParser;
import com.utils.execeptions.XMLParser_Exception_NO_ATTRIBUTE;

/**
 * 
 * @author Maupin Mathieu
 *
 */

public class Dialog_UserName extends JDialog{

	private static final long serialVersionUID = 1L;
	
	protected JDialog jd;
	JLabel profileName = new JLabel(""), userName, error = new JLabel("");
	ImagePanel avatar = new ImagePanel(null), avatarMask;
	JCheckBox manual;
	JTextField textField = new JTextField();
	ImageButton left, right, delete, create;
	boolean check = false;
	String nameBackup;
	JButton valider;
	String response = null;
	String path, imgs;
	
	Dimension size = new Dimension();
	Point point = new Point();
	
	File userlist,profile;
	ArrayList<String> userArray = new ArrayList<String>();
	int index = 0, rename = 1;
	String[] content = new String[4];
	BufferedReader reader = null, listreader = null;
	Writer writer = null;
	
	static Logger log = new Logger(Dialog_UserName.class);
	public static void trace(Object msg){
		log.trace(msg);
	}


	public Dialog_UserName(){
		
		jd = new JDialog(MainWindow.getInstance(), Dialog.ModalityType.DOCUMENT_MODAL);
		
		try {
			path = XMLParser.getFirstValue(Config.getInstance().getXML(),"DEFAULT_PATH");
			imgs = path+"/imgs/interface/";
			path += "/profiles/";
			
			
		} catch (XMLParser_Exception_NO_ATTRIBUTE e) {
			e.printStackTrace();
		} catch (Config_Exception_NotDone e) {
			e.printStackTrace();
		}
		
		fillArray();
		
	}
	
	public void fillArray(){
		
		File userlist = new File(path);

	    FilenameFilter filter = new FilenameFilter() {
	    	public boolean accept(File dir, String name) {
	    	return name.endsWith(".usr");
	    	}
	    	}; 
	    
	    String[] usrFiles = userlist.list(filter);
	    for (int i=0; i<usrFiles.length; i++) {
	    	System.out.println(usrFiles[i]);
	    	userArray.add(usrFiles[i]);
	    }
		
		
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
		profileName.setFont(new Font("Arial", Font.BOLD, 16));
		profileName.setBounds((300-profileName.getPreferredSize().width)/2,30,profileName.getPreferredSize().width,profileName.getPreferredSize().height);
		panel.add(profileName);
		
		final ImagePanel avatarbg = new ImagePanel(new ImageIcon(imgs+"avatarbg.png").getImage());
		avatarbg.setLocation(98,60);
		panel.add(avatarbg);
		
		final JLayeredPane avatarField = new JLayeredPane();
		avatarField.setLayout(null);
		avatarField.setSize(100,100);
		avatarField.setLocation(2,2);
		avatarField.setOpaque(false);
		avatarbg.add(avatarField);
		
		avatar.setLocation(0,0);
		avatarField.add(avatar, new Integer(0));
		
		avatarMask = new ImagePanel(new ImageIcon(imgs+"avatarmask.png").getImage());
		avatarMask.setLocation(0,0);
		avatarMask.setVisible(false);
		avatarField.add(avatarMask, new Integer(1));
		
		left = new ImageButton(new ImageIcon(imgs+"leftb.png"));
		left.setLocation(50,102);
		left.setToolTipText("Previous profile");
		left.setRolloverIcon(new ImageIcon(imgs+"leftb2.png"));
		left.setPressedIcon(new ImageIcon(imgs+"leftb3.png"));
		left.setDisabledIcon(new ImageIcon(imgs+"left4.png"));
		left.setEnabled(false);
		panel.add(left);
		
		right = new ImageButton(new ImageIcon(imgs+"rightb.png"));
		right.setLocation(230,102);
		right.setToolTipText("Next profile");
		right.setRolloverIcon(new ImageIcon(imgs+"rightb2.png"));
		right.setPressedIcon(new ImageIcon(imgs+"rightb3.png"));
		right.setDisabledIcon(new ImageIcon(imgs+"right4.png"));
		if(userArray.size()<2)right.setEnabled(false);
		panel.add(right);
		
		left.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				
				index--;
				checkArrows();
				load(index);
			}
		});
		
		right.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				
				index++;
				checkArrows();
				load(index);
			}
		});
        
		
		delete = new ImageButton(new ImageIcon(imgs+"minus.png"));
		delete.setLocation(131,157);
		delete.setToolTipText("Delete this profile");
		delete.setRolloverIcon(new ImageIcon(imgs+"minus2.png"));
		delete.setPressedIcon(new ImageIcon(imgs+"minus3.png"));
		delete.setDisabledIcon(new ImageIcon(imgs+"minus4.png"));
		panel.add(delete, new Integer(1));
		
		delete.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				
				if(index<0)return;
				//delete(false);
				askDelete();
				
			}
		});
		
//		final ImageLabel neutral = new ImageLabel(new ImageIcon(imgs+"neutral.png"));
//		neutral.setLocation(142,157);
//		panel.add(neutral, new Integer(1));
		
		create = new ImageButton(new ImageIcon(imgs+"plus.png"));
		create.setLocation(150,157);
		create.setToolTipText("Create a new profile");
		create.setRolloverIcon(new ImageIcon(imgs+"plus2.png"));
		create.setPressedIcon(new ImageIcon(imgs+"plus3.png"));
		create.setDisabledIcon(new ImageIcon(imgs+"plus4.png"));
		panel.add(create, new Integer(1));
		
		create.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				
				create("");
			}
		});
		
		
        if(userArray.size() == 0)load(index = -1);
        else load(index);
		//END OF PROFILE SETTINGS//
		
        
        //INPUT SETTINGS
		manual = new JCheckBox(" Manual login");
		manual.setSize(manual.getPreferredSize());
		manual.setLocation(17,195);
		manual.setOpaque(false);
		manual.setSelected(false);
		panel.add(manual);
		
		userName = new JLabel("User name:");
		size = userName.getPreferredSize();
		userName.setBounds(50,225,size.width,size.height);
		userName.setEnabled(check);
		panel.add(userName);
		
		textField.setBounds(60+size.width,222,200-size.width,22);
		textField.setOpaque(false);
		textField.setVisible(check);
		panel.add(textField);
		
		manual.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				
				check = !check;
				if(check) switchToManual();
				else switchToAuto();
				profileName.setBounds((300-profileName.getPreferredSize().width)/2,30,profileName.getPreferredSize().width,profileName.getPreferredSize().height);
			}
		});
		
		error.setForeground(Color.RED);
		size = error.getPreferredSize();
		error.setBounds(20,265,size.width,size.height);
		panel.add(error);
		
		valider = new JButton("Valider");
		size = valider.getPreferredSize();
		valider.setBounds((450-size.width)/2,260,size.width,size.height);
		jd.getRootPane().setDefaultButton(valider);
		panel.add(valider);
		
		valider.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				if (check) {
					if (textField.getText().equals("")){
						error.setText("User name is missing");
						size = error.getPreferredSize();
						error.setBounds(20,265,size.width,size.height);
						error.repaint();
						return;
					} else {
						response = textField.getText();
						jd.dispose();
						return;
					}
				} else {
					if (index >=0) {
						//VERIF EVENTUELLE DE PROFIL CORROMPU
						response = content[2];
						try {
							Selector.getInstance().getUI().getUserNameTokenUI().setProfileUse(true);
							Selector.getInstance().getUI().getUserNameTokenUI().setTempPWD(content[3]);
						} catch (Config_Exeception_UnableToReadConfigFile e1) {
							e1.printStackTrace();
						} catch (Config_Exeception_MalFormedConfigFile e1) {
							e1.printStackTrace();
						} catch (Config_Exception_NotDone e1) {
							e1.printStackTrace();
						}
						jd.dispose();
						return;
					} else {
						error.setText("No profile to load");
						size = error.getPreferredSize();
						error.setBounds(20,265,size.width,size.height);
						error.repaint();
						return;
					}
				}
			}
		});		
        //END OF INPUT SETTINGS
        
        
		jd.setVisible(true);
		jd.requestFocus();
		
	}
	
	public void switchToManual() {
		
		userName.setEnabled(true);
		textField.setVisible(true);
		textField.requestFocusInWindow();
		left.setEnabled(false);
		right.setEnabled(false);
		delete.setEnabled(false);
		create.setEnabled(false);
		avatarMask.setVisible(true);
		nameBackup = profileName.getText();
		profileName.setText("MANUAL LOGIN");
		profileName.setEnabled(false);
		error.setText("");
		error.repaint();
	}
	
	public void switchToAuto() {
		
		userName.setEnabled(false);
		textField.setVisible(false);
		valider.requestFocusInWindow();
		delete.setEnabled(true);
		create.setEnabled(true);
		avatarMask.setVisible(false);
		profileName.setText(nameBackup);
		profileName.setEnabled(true);
		checkArrows();
		error.setText("");
		error.repaint();
	}
	
	public String getResponse(){
		return response;
	}
	
	public void read(String fileName){
		
		profile = new File(path+fileName);
		if(!profile.exists()){
			//SI LE PROFIL N'EXISTE PAS, LE SUPPRIMER DE LA LISTE (QUICK DELETE).
			delete(true);
	    	return;
		}
		

        try
        {
            reader = new BufferedReader(new FileReader(profile));
            String text = null;
            int i = 0;

            while (i < 4)
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
        
        //for(int i=0;i<4;i++)System.out.println(content[i]);
		
	}
	
	
	public void load(int toLoad){
		
		if(toLoad>=0 && toLoad < userArray.size()){
			read(userArray.get(toLoad));
			profileName.setText(content[0]);
			profileName.setEnabled(true);
			avatar.setNewImgAndSize(new ImageIcon(imgs+"avatars/"+content[1]).getImage());
		}
		else{
			profileName.setText("NONE");
			profileName.setEnabled(false);
			avatar.setNewImgAndSize(new ImageIcon(imgs+"avatars/avatar-family.png").getImage());
			index = -1;
		}
		
		//profileName.setFont(new Font("Arial", Font.BOLD, 16));
		profileName.setBounds((300-profileName.getPreferredSize().width)/2,30,profileName.getPreferredSize().width,profileName.getPreferredSize().height);
		
		avatar.setLocation((100-avatar.getWidth())/2,(100-avatar.getHeight())/2);
		avatar.repaint();
		
		checkArrows();
		error.setText("");
		error.repaint();
		
	}
	
	
	public void create(String name){
		
		final Dialog_NewProfile np = new Dialog_NewProfile(jd, name);
		if(np.getResponse()[0].equals("")) return;
		
		for(int i=0;i<4;i++)content[i] = np.getResponse()[i];
		
		if (!content[1].equals("avatar-male.png") && !content[1].equals("avatar-female.png")){
			String backslash = "\\";
			String slash = "/";
			content[1] = content[1].replace(backslash,slash);
			System.out.println("AVATAR URL: "+content[1]);
			
			//PROCESS AVATAR
			
			//ImageIcon imgI = new ImageIcon(content[1]);
			//Image img = imgI.getImage();
			//int height = imgI.getIconHeight();
			//int width = imgI.getIconWidth();
			Image img;
			try {
				URL url = new URL(content[1]);
				img = ImageIO.read(url);
			} catch (IOException e1) {
				e1.printStackTrace();
				//System.out.println("FFAAAAIIIILLLL");
				ImageIcon imgI = new ImageIcon(content[1]);
				img = imgI.getImage();
			}
			int height = img.getHeight(null);
			int width = img.getWidth(null);
			
			if (width>0 && height>0) {
			
				int stringIndex = content[1].lastIndexOf(slash);
				content[1] = content[1].substring(++stringIndex);
				stringIndex = content[1].lastIndexOf(".");
				content[1] = content[1].substring(0,stringIndex)+".png";
				System.out.println("AVATAR NAME: "+content[1]);
				
				BufferedImage bi = new BufferedImage(width,height,BufferedImage.TYPE_INT_RGB);
				Graphics2D g2 = bi.createGraphics();
				// Draw img into bi so we can write it to file.
				g2.drawImage(img, 0, 0, null);
				g2.dispose();
				int newHeight = 100;
				int newWidth = 100;
				Image resized;
				BufferedImage bi2;
				if (height != newHeight && width != newWidth) {
					if (width*newHeight/height>=newWidth){
						resized =  bi.getScaledInstance(width*newHeight/height,newHeight , BufferedImage.SCALE_SMOOTH);
						bi2 = new BufferedImage(width*newHeight/height,newHeight,BufferedImage.TYPE_INT_RGB);
					} else {
						resized =  bi.getScaledInstance(newWidth,height*newWidth/width , BufferedImage.SCALE_SMOOTH);
						bi2 = new BufferedImage(newWidth,height*newWidth/width,BufferedImage.TYPE_INT_RGB);
					}
					Graphics2D g3 = bi2.createGraphics();
					// Draw img into bi so we can write it to file.
					g3.drawImage(resized, 0, 0, null);
					g3.dispose();
					try {
						ImageIO.write(bi2, "png", new File(imgs+"avatars/"+content[1]));
					} catch (IOException e) {
						
					}
				}
				else {
					try {
						ImageIO.write(bi, "png", new File(imgs+"avatars/"+content[1]));
					} catch (IOException e) {
						
					}
				}


			} else content[1] = "avatar-family.png";
			
			
			
			
			
		}
		
		//CREATE NEW USR FILE
		boolean ok = false;
		profile = new File(path+content[0]+".usr");
		if(profile.exists()){
			while(ok==false){
				profile = new File(path+content[0]+rename+".usr");
				if(profile.exists())rename++;
				else ok = true;
			}
		}
		
		
		
	    try {
	    	profile.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		StringBuffer profileInfo = new StringBuffer();
		profileInfo.append(content[0]).append(System.getProperty("line.separator"))
			.append(content[1]).append(System.getProperty("line.separator"))
				.append(content[2]).append(System.getProperty("line.separator"))
					.append(content[3]);
		writeInFile(profile, profileInfo.toString());
		
		//UPDATE USR LIST
		if(ok)userArray.add(content[0]+rename+".usr");
		else userArray.add(content[0]+".usr");
		index = userArray.size()-1;
		
		rename = 1;
		
		//LOAD IT
		load(index);
		
		
	}
	
	public void askDelete(){
		
		if(index <0)return;
		Dialog_YesNo question = new Dialog_YesNo(jd);
		question.settings("Delete?", "Delete this profile?");
		if (question.getResponse()) delete(false);
		
	}
	
	public void delete(boolean quick){
		
		if(index <0)return;
		String toDelete = userArray.get(index);
		userArray.remove(index);
		if (index>0 || (index==0 && userArray.size()==0)){
			index--;	
		}
		load(index);
		if (!quick) removeFile(toDelete);
		
		
	}
	
	
	public void writeInFile(File file, String toWrite){
		
		
		try {
			writer = new BufferedWriter(new FileWriter(file));
			writer.write(toWrite);
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
	
	public void checkArrows(){
		
		if(index<=0) left.setEnabled(false);
		else left.setEnabled(true);
		if (index>=userArray.size()-1) right.setEnabled(false);
		else right.setEnabled(true);
		
	}
	
	public void removeFile(String fileName) {
		
		File f = new File(path+fileName);

	    if (!f.exists())
	      throw new IllegalArgumentException(
	          "Delete: no such file or directory: " +path+fileName);

	    if (!f.canWrite())
	      throw new IllegalArgumentException("Delete: write protected: " +path+fileName);

	    boolean success = f.delete();

	    if (!success)
	      throw new IllegalArgumentException("Delete: deletion failed");
	    
	}
	
}
