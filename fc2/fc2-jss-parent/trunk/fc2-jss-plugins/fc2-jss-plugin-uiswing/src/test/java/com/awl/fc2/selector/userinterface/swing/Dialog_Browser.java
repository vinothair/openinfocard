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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.io.IOException;
import java.io.Reader;

import javax.swing.ImageIcon;
import javax.swing.JDialog;

//import org.lobobrowser.html.*;
//import org.lobobrowser.html.gui.*;
//import org.lobobrowser.html.test.*;
//import org.lobobrowser.html.parser.*;
import org.w3c.dom.*;
import org.xml.sax.SAXException;


import com.awl.fc2.selector.exceptions.Config_Exception_NotDone;
import com.awl.fc2.selector.launcher.Config;
import com.awl.logger.Logger;
import com.utils.XMLParser;
import com.utils.execeptions.XMLParser_Exception_NO_ATTRIBUTE;


/**
 * 
 * @author Maupin Mathieu
 *
 */

public class Dialog_Browser extends JDialog{

	private static final long serialVersionUID = 1L;
	
	private static Dialog_Browser _instance = null;
	
	String path, imgs, profiles;
	
	Dimension size = new Dimension();
	Point point = new Point();
	
	static Logger log = new Logger(Dialog_Browser.class);
	public static void trace(Object msg){
		log.trace(msg);
	}


	public Dialog_Browser(){
		
		super(MainWindow.getInstance(), Dialog.ModalityType.DOCUMENT_MODAL);
		
		try {
			path = XMLParser.getFirstValue(Config.getInstance().getXML(),"DEFAULT_PATH");
			imgs = path+"/imgs/interface/";
			profiles = path+"/profiles/";
			
			
		} catch (XMLParser_Exception_NO_ATTRIBUTE e) {
			e.printStackTrace();
		} catch (Config_Exception_NotDone e) {
			e.printStackTrace();
		}
		
	}
	
	public static Dialog_Browser getFreshInstance(){
		if(_instance != null)
			_instance.dispose();
			
		_instance = new Dialog_Browser();
		return _instance;
	}
	
	public static Dialog_Browser getInstance(){
		if(_instance == null)
			_instance = new Dialog_Browser();
			
		return _instance;
	}
	
	
	public void settings(String title){
		
		//GENERAL SETTINGS//
		setUndecorated(true);
		setTitle(title);
		setSize(300,300);
		setResizable(false);
		setLocationRelativeTo(MainWindow.getInstance());
		
		ImagePanel panel = new ImagePanel(new ImageIcon(imgs+"dialog3.png").getImage());
		panel.setLocation(0,0);
		
		setContentPane(panel);
		
		final ImageButton closeButton = new ImageButton(new ImageIcon(imgs+"closediag.png"));
		closeButton.setLocation(300-24,10);
		closeButton.setToolTipText("Cancel");
		closeButton.setRolloverIcon(new ImageIcon(imgs+"closediag2.png"));
		closeButton.setPressedIcon(new ImageIcon(imgs+"closediag3.png"));
		panel.add(closeButton);
		
		closeButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				dispose();
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
					Point p = getLocation();  
					setLocation(p.x + e.getX() - point.x, p.y + e.getY() - point.y); 
				}  
			}  
		});
		//END OF GENERAL SETTINGS//
		
		
		HtmlPanel htmlpanel = new HtmlPanel();
		// This panel should be added to a JFrame or
		// another Swing component.
		UserAgentContext ucontext = new SimpleUserAgentContext();
		SimpleHtmlRendererContext rcontext = new SimpleHtmlRendererContext(htmlpanel, ucontext);
		// Note that document builder should receive both contexts.
		DocumentBuilderImpl dbi = new DocumentBuilderImpl(ucontext, rcontext);
		String documentURI = "http://lobobrowser.org/cobra/getting-started.jsp";;
		Reader documentReader = null;
		// A documentURI should be provided to resolve relative URIs.
		Document document;
		try {
			document = dbi.parse(new InputSourceImpl(documentReader, documentURI));
			htmlpanel.setDocument(document, rcontext);
		} catch (SAXException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		// Now set document in panel. This is what causes the document to render.
		

		panel.add(htmlpanel);

		
		
		
        //END OF INPUT SETTINGS
		setVisible(true);
		//requestFocus();
		
	}
	
}
