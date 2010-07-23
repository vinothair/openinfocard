package com.awl.fc2.selector.userinterface.swing;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
public class TestLF extends JFrame{ 
	private static final long serialVersionUID = 1L;
	public TestLF(){ 
		super("Test L&F");
		createJMenuBar();
		createComposants();
		setSize(200,200);
		setLocationRelativeTo(null); 
		setDefaultCloseOperation(EXIT_ON_CLOSE); 
		setVisible(true);
	}
	public Map<String,String> getLookAndFeelsMap(){
		UIManager.LookAndFeelInfo[] info = UIManager.getInstalledLookAndFeels();
		Map<String,String> map = new TreeMap<String,String>();
		for(int i=0; i<info.length;i++){
			String nomLF = info[i].getName();
			String nomClasse = info[i].getClassName();
			map.put(nomLF,nomClasse); 	
		}
		return map;	
	}
	protected void createJMenuBar(){
		JMenuBar bar = new JMenuBar();
		JMenu menu = new JMenu("L&F");
		ButtonGroup bg = new ButtonGroup();
		Map<String,String> map = getLookAndFeelsMap();
		for(String clef : map.keySet()){
			final String classe = map.get(clef);
			System.out.println(classe);
			boolean natif = classe.equals(UIManager.getSystemLookAndFeelClassName());
			JRadioButtonMenuItem item = new JRadioButtonMenuItem(clef,natif);
			item.addActionListener(new ActionListener(){ 
				public void actionPerformed(ActionEvent ae){ 
					try{ 
						UIManager.setLookAndFeel(classe); 
					}catch(Exception e){
						e.printStackTrace();	
					} 
					SwingUtilities.updateComponentTreeUI(TestLF.this); 
				} 
			}); 
			bg.add(item); 
			menu.add(item);  	
		}
		bar.add(menu);
		setJMenuBar(bar);
	}
	protected void createComposants(){
		Container c = getContentPane(); c.setLayout(new GridLayout(3,2)); 
		c.add(new JLabel("JLabel"));
		c.add(new JButton("JButton")); 
		c.add(new JTextField("JTextField"));
		c.add(new JRadioButton("JRadioButton")); 
		c.add(new JComboBox(new String[]{"un","deux","trois","quatre","cinq","six"}));
		JTextArea area = new JTextArea();
		for(int i=0;i<10;i++){area.append("ligne "+i+"\n");}
		c.add(new JScrollPane(area));
		setContentPane(c);	
		try{ 
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); 
		}catch(Exception e){
			e.printStackTrace();	
		} 
		SwingUtilities.updateComponentTreeUI(this);
	}
	public static void main(String[] args){
		new TestLF();	
	}
}