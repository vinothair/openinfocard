/*
GNU LESSER GENERAL PUBLIC LICENSE
Copyright (C) 2006 The Lobo Project

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation; either
version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the Free Software
Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA

Contact info: lobochief@users.sourceforge.net
*/
/*
* Created on Oct 22, 2005
*/

package com.awl.fc2.selector.userinterface.swing.cobra;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import java.net.*;
import java.util.logging.*;

import org.lobobrowser.html.gui.*;
import org.lobobrowser.html.test.*;
import org.lobobrowser.html.*;

import com.awl.fc2.selector.exceptions.Config_Exception_NotDone;
import com.awl.fc2.selector.launcher.Config;
import com.awl.fc2.selector.userinterface.swing.*;
import com.utils.XMLParser;
import com.utils.execeptions.XMLParser_Exception_NO_ATTRIBUTE;

/**
* A Swing frame that can be used to test the
* Cobra HTML rendering engine. 
*/
public class BrowserWindow extends JFrame {	

	private static final long serialVersionUID = 1L;

	private static BrowserWindow _instance = null;
	
	String path,imgs,cardimgs;
	ImagePanel panel;
	Point point = new Point();
	
	private static final Logger logger = Logger.getLogger(BrowserWindow.class.getName());
	private SimpleHtmlRendererContext rcontext;
	private HtmlPanel htmlpanel;
	private JTextField addressField;
	
	Color bg = new Color(222,222,222);
	
	public static BrowserWindow getInstance(){
		if(_instance == null)
			_instance = new BrowserWindow("JSS Browser");
		return _instance;
	}
	
	public BrowserWindow() throws HeadlessException {
		this("");
	}
	
	public BrowserWindow(String title) throws HeadlessException {
		
		super(title);
		
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
		
		build();
		
	}
	
	private void build(){
		
		setSize(1024,768);
		setBackground(bg);
		setLocationRelativeTo(null);
		setResizable(false);
		setUndecorated(true);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		setContentPane(buildContentPane());
		
	}
		
	private JLayeredPane buildContentPane(){
	
		
		
		panel = new ImagePanel(new ImageIcon(imgs+"browser.png").getImage());
		panel.setLayout(null);
		panel.setLocation(0,0);
		
		final ImageButton closeButton = new ImageButton(new ImageIcon(imgs+"close.png"));
		closeButton.setLocation(1024-25,10);
		closeButton.setToolTipText("Close...");
		closeButton.setRolloverIcon(new ImageIcon(imgs+"close2.png"));
		closeButton.setPressedIcon(new ImageIcon(imgs+"close3.png"));
		panel.add(closeButton);
		
		closeButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				dispose();
			}
		});
		
		final ImageButton miniButton = new ImageButton(new ImageIcon(imgs+"mini.png"));
		miniButton.setLocation(1024-45,10);
		miniButton.setToolTipText("Minimize to system tray");
		miniButton.setRolloverIcon(new ImageIcon(imgs+"mini2.png"));
		miniButton.setPressedIcon(new ImageIcon(imgs+"mini3.png"));
		panel.add(miniButton);
		
		miniButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				setState(Frame.ICONIFIED );

			}
		});
		
		addressField = new JTextField("http://www.atosworldline.com");
		addressField.setBounds(70,20,800,28);
		panel.add(addressField);
		
		final JButton go = new JButton("GO");
		go.setBounds(880,20,55,28);
		panel.add(go);
		
		go.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				navigate(addressField.getText());
			}
		});
		
		JLabel url = new JLabel("URL:");
		url.setBounds(20,20,40,28);
		panel.add(url);
		
		
		htmlpanel = new HtmlPanel();
		htmlpanel.addSelectionChangeListener(new SelectionChangeListener() {
			public void selectionChanged(SelectionChangeEvent event) {
				if(logger.isLoggable(Level.INFO)) {
					logger.info("selectionChanged(): selection node: " + htmlpanel.getSelectionNode());
				}
			}
		});
		//UserAgentContext ucontext = new SimpleUserAgentContext();
		UserAgentContext ucontext = new AWLUserAgentContext();
		rcontext = new LocalHtmlRendererContext(htmlpanel, ucontext);
		
		htmlpanel.setBounds(20,60,980,680);
		htmlpanel.setBorder(BorderFactory.createLineBorder(Color.black));
		panel.add(htmlpanel);
		
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
					Point p = getLocation();  
					setLocation(p.x + e.getX() - point.x, p.y + e.getY() - point.y); 
				}  
			}  
		});
		
		addWindowFocusListener(new WindowAdapter() {
		    public void windowGainedFocus(WindowEvent e) {
		    	addressField.requestFocusInWindow();
		    	getRootPane().setDefaultButton(go);
		    }
		});
		
		return panel;
	}
	
	public HtmlRendererContext getHtmlRendererContext() {
		return this.rcontext;
	}
	
	public void navigate(String uri) {
		this.addressField.setText(uri);
		this.process(uri);
	}
	
	private void process(final String uri) {
		
		new Thread(new Runnable() {
		      public void run() {
		
		try {
			URL url;
			try {
				url = new URL(uri);
			} catch(java.net.MalformedURLException mfu) {
				int idx = uri.indexOf(':');
				if(idx == -1 || idx == 1) {
					// try file
					url = new URL("file:" + uri);
				}
				else {
					throw mfu;
				}
			}
			// Call SimpleHtmlRendererContext.navigate()
			// which implements incremental rendering.
			    	  
			rcontext.navigate(url, null);
			
		} catch(Exception err) {
			logger.log(Level.SEVERE, "Error trying to load URI=[" + uri + "].", err);
		}
		
		      }
		}).start();
		
	}
		
	public static void main(String[] args) {
		BrowserWindow.getInstance().setVisible(true);
	}
		
		


private class LocalHtmlRendererContext extends SimpleHtmlRendererContext {
	public LocalHtmlRendererContext(HtmlPanel contextComponent, UserAgentContext ucontext) {
		super(contextComponent, ucontext);
	}

	public HtmlRendererContext open(URL url, String windowName, String windowFeatures, boolean replace) {
		HtmlRendererContext ctx = getInstance().getHtmlRendererContext();
		ctx.setOpener(this);
		getInstance().navigate(url.toExternalForm());
		return ctx;
	}
}
}
