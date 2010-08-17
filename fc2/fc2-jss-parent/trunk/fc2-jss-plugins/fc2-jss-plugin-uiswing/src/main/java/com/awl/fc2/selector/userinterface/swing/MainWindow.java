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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import com.awl.fc2.selector.exceptions.Config_Exception_NotDone;
import com.awl.fc2.selector.launcher.Config;
import com.awl.fc2.selector.userinterface.swing.cobra.BrowserWindow;
import com.awl.logger.Logger;
import com.utils.XMLParser;
import com.utils.execeptions.XMLParser_Exception_NO_ATTRIBUTE;

/**
 * Used by {@link SelectorUI_Swing}.<br>This is the main frame of JSS user interface.
 * @author Maupin Mathieu
 *
 */
public class MainWindow extends JFrame{
	
	private static final long serialVersionUID = 1L;
	
	private static MainWindow _instance = null;
	
	String path,imgs,cardimgs;
	
	URL url = getClass().getResource("/com/awl/fc2/plugin/session/trayicon/im.png");
	Image icon = Toolkit.getDefaultToolkit().getImage(url);
//	private TrayIcon trayIcon = new TrayIcon(icon, "JSS", null);
	
	Color c1 = new Color(222,122,22);
	Dimension size = new Dimension();
	Point point = new Point();
	
	ImagePanel panel;
	CardPanel cardpanel;
	JPanel content,console;
	int index_y = 0;
	int state = 0;
	
	String response = null;
	
	File titleFile;
	String[] titleContent = new String[2];
	BufferedReader reader = null;
	Writer writer = null;
	
	static Logger log = new Logger(MainWindow.class);
	public static void trace(Object msg){
		log.trace(msg);
	}

	public MainWindow(){
		
		super();

	    try {
	    	UIManager.setLookAndFeel(new JSSLookAndFeel());
	    } catch(UnsupportedLookAndFeelException e) {
	    	
	    }
		
		try {
			path = XMLParser.getFirstValue(Config.getInstance().getXML(),"DEFAULT_PATH")+"/";
			imgs = XMLParser.getFirstValue(Config.getInstance().getXML(),"IMGS");
			cardimgs = imgs;
			imgs += "interface/";
		} catch (XMLParser_Exception_NO_ATTRIBUTE e) {
			e.printStackTrace();
		} catch (Config_Exception_NotDone e) {
			e.printStackTrace();
		}
		
		prepareTitle();
		build();
		hideProc();
		wakeupProc();
		
	}
	
	static public MainWindow getInstance(){
		if(_instance == null)
			_instance = new MainWindow();
		return _instance;
	}

	private void prepareTitle(){
		
		boolean freshlyCreated = false;
		titleFile = new File(path+"title.txt");
		if(!titleFile.exists()){
		    try {
		    	titleFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				titleContent[0] = "J a v a   S m a r t   S e l e c t o r";
				titleContent[1] = "14";
				StringBuffer titleSB = new StringBuffer();
				titleSB.append(titleContent[0]).append(System.getProperty("line.separator"))
					.append(titleContent[1]);
				writer = new BufferedWriter(new FileWriter(titleFile));
				writer.write(titleSB.toString());
				freshlyCreated = true;
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
		
		if(!freshlyCreated){
	        try
	        {
	            reader = new BufferedReader(new FileReader(titleFile));
	            String text = null;
	            int i = 0;
	
	            while (i < 2)
	            {
	            	text = reader.readLine();
	            	if (text == null) text = "";
	            	titleContent[i++] = text;
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
		}
		
//        for(int i=0;i<2;i++)System.out.println(titleContent[i]);
		
	}
	
	private void build(){
		
		setTitle("Java Smart Selector");
		setSize(400,400);
		setBackground(Color.LIGHT_GRAY);
		setLocationRelativeTo(null);
		setResizable(false);
		setUndecorated(true);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		
		addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e){
				exitProc();
			}
			public void windowIconified(WindowEvent e){
				hideProc();
			}
			
		});
		
//		trayIcon.setToolTip("JSS Interface");
//		
//        trayIcon.addMouseListener(new MouseAdapter() {
//            public void mouseClicked(MouseEvent e) {
//            	wakeupProc();
//            }
//        });
		
		this.setIconImage(icon);
		
		setContentPane(buildContentPane());
	}
	
	private JLayeredPane buildContentPane(){
		
		panel = new ImagePanel(new ImageIcon(imgs+"background.png").getImage());
		panel.setLayout(null);
		panel.setLocation(0,0);
		
		content = new JPanel();
		content.setLayout(null);
		content.setSize(394,290);
		content.setLocation(3,80);
		content.setOpaque(false);
		
		panel.add(content);
		
		setConsole();
		content.add(console);
		
		JPanel titlebar = new JPanel();
		titlebar.setLayout(null);
		titlebar.setOpaque(false);
		titlebar.setSize(400,35);
		titlebar.setLocation(0,0);
		panel.add(titlebar);
		
		ImageLabel fish = new ImageLabel(new ImageIcon(imgs+"fish.png"));
		fish.setLocation(10,8);
		panel.add(fish);
		
		JLabel title = new JLabel(titleContent[0]);
		if (containsOnlyNumbers(titleContent[1])) {
			title.setFont(new Font("Trebuchet MS", Font.PLAIN, Integer.valueOf(titleContent[1])));
		} else {
			title.setFont(new Font("Trebuchet MS", Font.PLAIN, 15));
		}
		title.setForeground(c1);
		size = title.getPreferredSize();
		title.setBounds(222-size.width/2,17-size.height/2,size.width,size.height);
		titlebar.add(title);
		
        CopyRight copyright = new CopyRight("Copyright © 2010 Atos Worldline");
        copyright.setLocation(202,380);
        panel.add(copyright);

		
		final ImageButton closeButton = new ImageButton(new ImageIcon(imgs+"close.png"));
		closeButton.setLocation(400-25,10);
		closeButton.setToolTipText("Close...");
		closeButton.setRolloverIcon(new ImageIcon(imgs+"close2.png"));
		closeButton.setPressedIcon(new ImageIcon(imgs+"close3.png"));

		titlebar.add(closeButton);
		
		closeButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				exitProc();
			}
		});
		
		final ImageButton miniButton = new ImageButton(new ImageIcon(imgs+"mini.png"));
		miniButton.setLocation(400-45,10);
		miniButton.setToolTipText("Minimize to system tray");
		miniButton.setRolloverIcon(new ImageIcon(imgs+"mini2.png"));
		miniButton.setPressedIcon(new ImageIcon(imgs+"mini3.png"));
		
		titlebar.add(miniButton);
		
		miniButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				hideProc();
			}
		});
		
		titlebar.addMouseListener(new MouseAdapter() {  
			public void mousePressed(MouseEvent e) {  
				if(!e.isMetaDown()){  
					point.x = e.getX();  
					point.y = e.getY();  
				}  
			}  
		});
		
		titlebar.addMouseMotionListener(new MouseMotionAdapter() {  
			public void mouseDragged(MouseEvent e) {  
				if(!e.isMetaDown()){  
					Point p = getLocation();  
					setLocation(p.x + e.getX() - point.x, p.y + e.getY() - point.y); 
				}  
			}  
		});
		
		
		final JButton browser = new JButton("N");
		browser.setSize(browser.getPreferredSize());
		browser.setLocation(350,40);
		browser.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				BrowserWindow.getInstance().setVisible(true);
			}
		});
		panel.add(browser);
		
		
		return panel;
	}
	
    public boolean containsOnlyNumbers(String str) {
        
        if (str == null || str.length() == 0)
            return false;
        
        for (int i = 0; i < str.length(); i++) {

            if (!Character.isDigit(str.charAt(i)))
                return false;
        }
        
        return true;
    }

	public void exitProc(){
		final Dialog_Exit ex = new Dialog_Exit();
		ex.settings();
		int choice = ex.getChoice();
		
		switch(choice){
			case 0: System.exit(0); break;
			case 1: hideProc(); break;
			default: break;
		}
	}

	public void hideProc(){
		
		setVisible(false);
	}
	
	public void wakeupProc(){
		
		setVisible(true);
		setExtendedState(MainWindow.NORMAL);
		
	}


	public void traceConsole(final String msg){
		
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				
				if(index_y>=270)clearConsole();
				
				final JLabel label = new JLabel(msg);
				label.setFont(new Font("Tahoma", Font.BOLD, 14));
				size = label.getPreferredSize();
				label.setBounds(22,index_y,size.width,size.height);
				index_y += 22;
				console.add(label);
				console.repaint();

				final Timer timer = new Timer(30, null);

		        final int steps = 25;

		        timer.addActionListener(new ActionListener() {
		            int count = 0 ;

		            public void actionPerformed(ActionEvent e) {
		                if (count <= steps) {
		            		float intensity = 1 - (count / (float) steps);
		            		label.setForeground(new Color(intensity, intensity/2, intensity/4));
		            		count++;
		            	} else {
		            		timer.stop();
		            	}
		            }
		        });
		        timer.start();
			}
		});
		
	}
	
	public void setConsole(){
		
//		this.setContentPane(buildContentPane());
//		index_y = 80;
		
		console = new JPanel();
		console.setLayout(null);
		console.setSize(394,290);
		console.setLocation(0,0);
		console.setOpaque(false);
				
	}
	
	public void clearConsole(){
		
		console.removeAll();
		console.repaint();
		index_y = 0;
	}

	
	public void cardTOcons(){
		
		if (state==0)return;
		
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
		
				final Timer timer = new Timer(15, null);
				final int steps = 10;
	        
				console.setLocation(-400,80);
				content.add(console);
        
				timer.addActionListener(new ActionListener() {
					int count = 0 ;

					public void actionPerformed(ActionEvent e) {
						if (count < steps) {
							cardpanel.setLocation((count+1)*40,0);
							console.setLocation((count-9)*40,0);
							content.repaint();
							count++;
						} else {
							content.remove(cardpanel);
							timer.stop();
						}
					}
				});
				timer.start();
        
			}
		});
		
		content.repaint();
		state = 0;
	}
	
	
	public void consTOcard(){
		
		if (state==1)return;
		
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				
				final Timer timer = new Timer(15, null);
		        final int steps = 10;
		        
		        cardpanel.setLocation(400,80);
		        content.add(cardpanel);
		        
		        timer.addActionListener(new ActionListener() {
		            int count = 0 ;

		            public void actionPerformed(ActionEvent e) {
		                if (count < steps) {
		                	console.setLocation(-(count+1)*40,0);
		                	cardpanel.setLocation((9-count)*40,0);
		                	content.repaint();
		            		count++;
		            	} else {
		            		content.remove(console);
		            		timer.stop();
		            	}
		            }
		        });
		        timer.start();
			}
		});
		
		content.repaint();
		state = 1;
	}
	
	public void selectCard(final String[] claims, final String[] urls, final String[] labels){
		
		cardpanel = new CardPanel(claims, urls, labels);
		
		
		ImageButton ok = new ImageButton(new ImageIcon(imgs+"ok.png"));
		ok.setLocation(160,165);
		ok.setPressedIcon(new ImageIcon(imgs+"ok2.png"));
		cardpanel.add(ok);
		cardpanel.repaint();
		
		
		
		ok.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				
				response = String.valueOf(cardpanel.getIndex());
			}
		});
		
		consTOcard();
		
	}
	
	
	public String getResponse(){
		
		return response;
	}
	
	public void resetResponse(){
		
		response = null;
	}

	
	
	@Override
	public void setVisible(final boolean visible) {
	  // make sure that frame is marked as not disposed if it is asked to be visible
//	  if (visible) {
//	      setDisposed(false);
//	  }
	  // let's handle visibility...
	  if (!visible || !isVisible()) { // have to check this condition simply because super.setVisible(true) invokes toFront if frame was already visible
	      super.setVisible(visible);
	  }
	  // ...and bring frame to the front.. in a strange and weird way
	  if (visible) {
	      int state = super.getExtendedState();
	      state &= ~JFrame.ICONIFIED;
	      super.setExtendedState(state);
	      super.setAlwaysOnTop(true);
	      super.toFront();
	      super.requestFocus();
	      super.setAlwaysOnTop(false);
	  }
	}

	@Override
	public void toFront() {
	  super.setVisible(true);
	  int state = super.getExtendedState();
	  state &= ~JFrame.ICONIFIED;
	  super.setExtendedState(state);
	  super.setAlwaysOnTop(true);
	  super.toFront();
	  super.requestFocus();
	  super.setAlwaysOnTop(false);
	}
	
	
}

