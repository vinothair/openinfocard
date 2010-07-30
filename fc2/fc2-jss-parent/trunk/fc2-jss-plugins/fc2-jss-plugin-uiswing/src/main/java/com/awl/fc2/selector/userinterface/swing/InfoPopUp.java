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
import java.awt.GridLayout;
import java.awt.Point;

import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;

public class InfoPopUp extends JDialog{

	private static final long serialVersionUID = 1L;
	
	private static InfoPopUp _instance = null;
	
	//protected JDialog info;
	Point point = new Point();
	//Color bg = new Color(230,233,240);
	Color bg = new Color(222,222,222);
	Border blackline = new CompoundBorder(BorderFactory.createLineBorder(Color.black), new EmptyBorder(10,10,10,22));
	Dimension size = new Dimension();

	
	public InfoPopUp(MainWindow parent){
		
		super(parent);
		//info = new JDialog(parent);
		
	}
	
	public static InfoPopUp getFreshInstance(){
		if(_instance != null)
			_instance.dispose();
			
		_instance = new InfoPopUp(MainWindow.getInstance());
		return _instance;
	}
	
	public static InfoPopUp getInstance(){
		if(_instance == null)
			_instance = new InfoPopUp(MainWindow.getInstance());
			
		return _instance;
	}

	public void settings(Point p, String[] list){
		
		setUndecorated(true);
		setBackground(bg);
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(0,1));
		panel.setBackground(bg);
		panel.setBorder(blackline);
		
		for (int i = 0; i<list.length; i++){
			panel.add(new JLabel("- "+list[i]));
		}
		
//		panel.addMouseListener(new MouseAdapter() {  
//			public void mouseExited(MouseEvent e) {
//				dispose();
//			}  
//		});
		
		setContentPane(panel);
		setMinimumSize(new Dimension(70,70));
		pack();
		
		size = getSize();
		if (p.x > size.width){
			point.x = p.x - size.width;
		} else {
			point.x = p.x + 400;
		}
		point.y = p.y + 400 - size.height;
		setLocation(point);
		
		setVisible(true);
	}
		
	public void hideit(){
		dispose();
	}
}
