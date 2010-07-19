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
package com.awl.fc2.plugin.session.trayicon;

import java.awt.AWTException;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.Image;
import java.awt.Menu;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Vector;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import org.xmldap.infocard.InfoCard;
import org.xmldap.infocard.SignedInfoCard;

import com.awl.fc2.selector.Selector;
import com.awl.fc2.selector.diagnostic.Diagnostic;
import com.awl.fc2.selector.exceptions.Config_Exception_NotDone;
import com.awl.fc2.selector.exceptions.Config_Exeception_MalFormedConfigFile;
import com.awl.fc2.selector.exceptions.Config_Exeception_UnableToReadConfigFile;
import com.awl.fc2.selector.session.ISessionElement;
import com.awl.fc2.selector.session.SessionSelector;
import com.awl.fc2.selector.userinterface.lang.Lang;
import com.awl.logger.Logger;

public class FC2TrayIcon implements ISessionElement {
	static Logger log = new Logger(FC2TrayIcon.class);

	static public void trace(Object msg) {
		log.trace(msg);
		System.out.println(msg);
	}

	static class ShowMessageListener implements ActionListener {
		TrayIcon trayIcon;
		String title;
		String message;
		TrayIcon.MessageType messageType;

		ShowMessageListener(TrayIcon trayIcon, String title, String message,
				TrayIcon.MessageType messageType) {
			this.trayIcon = trayIcon;
			this.title = title;
			this.message = message;
			this.messageType = messageType;
		}

		public void actionPerformed(ActionEvent e) {
			trayIcon.displayMessage(title, message, messageType);
		}
	}

	public void doImportCard() {
		JFileChooser _fileChooser = new JFileChooser();
		_fileChooser.setDialogTitle("Please choose you CRD file");
		_fileChooser.setFileFilter(new FileFilter() {

			@Override
			public boolean accept(File f) {
				if (f.isDirectory())
					return true;
				if (f.isFile() && f.getName().contains(".crd"))
					return true;
				return false;
			}

			@Override
			public String getDescription() {
				// TODO Auto-generated method stub
				return "*.CRD";
			}

		});

		int retval = _fileChooser.showOpenDialog(null);

		if (retval == JFileChooser.APPROVE_OPTION) {
			// ... The user selected a file, get it, use it.
			File file = _fileChooser.getSelectedFile();
			FileInputStream in;
			try {
				in = new FileInputStream(file);
				// FileReader fin = new FileReader(file);
				StringBuffer buf = new StringBuffer();
				byte[] buffer = new byte[50];
				int read = 1;
				while (read != -1) {
					try {
						read = in.read(buffer);

					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					if (read != -1) {
						String tmp = new String(buffer, 0, read);
						// buf.append(buffer,0,read);
						buf.append(tmp);
					}

				}

				String toRet = buf.toString();
				System.out.print("//");
				InfoCard card = String2InfoCard(toRet);

				// String toDisplay = "String "+file.getName().replace(".crd",
				// "")+"_cardB64 = \"" + Base64.encode(toRet.getBytes())+"\";";
				// toDisplay = toDisplay.replaceAll("\r\n", "\"+\n\"");
				// System.out.println("String "+file.getName().replace(".crd",
				// "")+"_CARID = \""+card.getCardId()+"\";");
				// System.out.println(toDisplay);
				in.close();
				//SINCE V2
				theSession.getCardStore().addInfoCard(card,false);
//				theSession.getCardStore().commit(card.getCardId(),
//						Base64.encode(toRet.getBytes()));

			} catch (Exception e) {
				// TODO: handle exception
			}

		}

	}

	public static InfoCard String2InfoCard(String xml) {
		/*try {
			Element root;
			root = XmlFileUtil
					.readXml(new ByteArrayInputStream(xml.getBytes()))
					.getRootElement();
			SignedInfoCard card = new SignedInfoCard(root);
			return card;

		} catch (ValidityException e) {
			trace("ValidityException");
		} catch (IOException e) {
			trace("IOException");
		} catch (ParsingException e) {
			trace("ParsingException");
		} catch (org.xmldap.exceptions.ParsingException e) {
			trace("org.xmldap.exceptions.ParsingException");
		}*/
		try {
			return new SignedInfoCard(xml);
		} catch (org.xmldap.exceptions.ParsingException e) {
			trace("Unable to parse the xml");
			return null;
		}
		
	}

	public FC2TrayIcon() {

	}

	Menu menuDel;
	SessionSelector theSession;
	FC2PopupMenu popup;
	TrayIcon trayIcon;
	SystemTray tray;

	@Override
	public void configure(SessionSelector theSession) {
		this.theSession = theSession;
		popup = new FC2PopupMenu(this);
		Runnable runner = new Runnable() {
			public void run() {
				if (SystemTray.isSupported()) {
					tray = SystemTray.getSystemTray();
					// String [] tfile = new File("./").list();
					// for(int i=0;i<tfile.length;i++){
					// System.out.println(">> " + tfile[i]);
					// }
					URL url = getClass()
							.getResource(
									"/com/awl/fc2/plugin/session/trayicon/infocard_17x12.png");
					Image image = Toolkit.getDefaultToolkit().getImage(url);

					trayIcon = new TrayIcon(image, "JSS Controller", popup);
					trayIcon.addMouseListener(new PopupMouseListener());
					trayIcon.setImageAutoSize(true);

					try {
						tray.add(trayIcon);
					} catch (AWTException e) {
						System.err.println("Can't add to tray");
					}
				} else {
					System.err.println("Tray unavailable");
				}
			}
		};
		EventQueue.invokeLater(runner);
	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub

	}

	@Override
	public void reset() {
		// TODO Auto-generated method stub

	}

	public void update_Deletemenu() {
		menuDel.removeAll();
		if (theSession == null) {
			MenuItem item = new MenuItem("You are not logged");
			item.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					System.out.println(((MenuItem) e.getSource()).getLabel());
				}
			});
			menuDel.add(item);
			return;
		}
		trace("Updating Deletemanu");
		{
			//SINCE V2
			Vector<InfoCard> vecCard = theSession.getCardStore().getAllInfoCards();
//			Vector<InfoCard> vecCard = theSession.getCardStore()
//					.listAllCards(0);
			for (InfoCard toAdd : vecCard) {
				MenuItem item = new MenuItem(toAdd.getCardName());
				item.setName(toAdd.getCardId());
				item.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						trace("Deleting the following card : "
								+ ((MenuItem) e.getSource()).getName());
						theSession.getCardStore().removeInfoCard(
								((MenuItem) e.getSource()).getName());
					}
				});
				menuDel.add(item);
			}

		}
	}

	public static void main(String args[]) {
		new FC2TrayIcon().configure(null);
	}

	// /**
	// * @param args
	// */
	// public static void main(String[] args) {
	// // TODO Auto-generated method stub
	//
	// }
	class FC2PopupMenu extends PopupMenu {

		/**
				 * 
				 */
		private static final long serialVersionUID = -7402537868485864128L;
		FC2TrayIcon fc2tray;

		public FC2PopupMenu(FC2TrayIcon fc2tray) {
			super();
			trace("Create FC2PopupMenu");
			this.fc2tray = fc2tray;
			createMenu();

		}

		public void createMenu() {

			// Adding Diagnostic menu
			{
				MenuItem item = new MenuItem(Lang.get(Lang.MENU_DIAGNOSTIC));
				item.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						Diagnostic diag = new Diagnostic();
						diag.doTest();
					}
				});
				add(item);
			}
			// Adding Close menu
			{
				MenuItem item = new MenuItem(Lang.get(Lang.MENU_CLOSE));
				item.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						fc2tray.tray.remove(fc2tray.trayIcon);
						//AirAppControler.getInstance().killFlex();
						try {
							Selector.getInstance().getUI().kill();
						} catch (Config_Exeception_UnableToReadConfigFile e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch (Config_Exeception_MalFormedConfigFile e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch (Config_Exception_NotDone e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						System.exit(0);
					}
				});
				add(item);
			}

			// Adding Import menu
			{
				MenuItem item = new MenuItem(Lang.get(Lang.MENU_IMPORT));
				item.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						fc2tray.doImportCard();
					}
				});
				add(item);
			}

			{// Adding Delete card menu
				fc2tray.menuDel = new Menu(Lang.get(Lang.MENU_DELETE_CARDS));
				add(fc2tray.menuDel);
			}
			// LogOff meny
			{
				MenuItem item = new MenuItem(Lang.get(Lang.MENU_LOG_OFF));
				item.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						theSession.close();
					}
				});
				add(item);
			}
			// LogOn menu
			{
				MenuItem item = new MenuItem(Lang.get(Lang.MENU_LOG_ON));
				item.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						
						new Thread(new Runnable() {
						      public void run() {
						    	  
						    	  try {
						    		  theSession.open();
						    	  } catch (Config_Exeception_UnableToReadConfigFile e1) {
						    		  trace("Configuration not done");
						    	  } catch (Config_Exeception_MalFormedConfigFile e1) {
						    		  trace("Configuration not done");
						    	  } catch (Config_Exception_NotDone e1) {
						    		  trace("Configuration not done");
						    	  }
						    	  
						      }
						}).start();
						
					}
				});
				add(item);
			}
			// Reset credentials
			{
				MenuItem item = new MenuItem(Lang.get(Lang.MENU_RESET_SSO_TOKEN));
				item.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						theSession.getCredentialStore().removeSSOToken();
					}
				});
				add(item);
			}
		}

		@Override
		public void show(Component origin, int x, int y) {
			trace("do somthing");
			super.show(origin, x, y);
		}
	}

	class PopupMouseListener implements MouseListener {

		public void mouseClicked(MouseEvent e) {
			System.out.println("Recompute the menu");
			update_Deletemenu();
		}

		public void mouseEntered(MouseEvent e) {
			// TODO Auto-generated method stub

		}

		public void mouseExited(MouseEvent e) {
			// TODO Auto-generated method stub

		}

		public void mousePressed(MouseEvent e) {
			// TODO Auto-generated method stub

		}

		public void mouseReleased(MouseEvent e) {
			// TODO Auto-generated method stub

		}
	}
}
