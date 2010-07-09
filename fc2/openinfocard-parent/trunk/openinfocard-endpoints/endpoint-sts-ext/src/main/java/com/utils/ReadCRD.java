package com.utils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.filechooser.FileFilter;

import nu.xom.Element;
import nu.xom.ParsingException;
import nu.xom.ValidityException;

import org.xmldap.infocard.InfoCard;
import org.xmldap.infocard.SignedInfoCard;
import org.xmldap.util.Base64;
import org.xmldap.util.XmlFileUtil;



public class ReadCRD {

	public static void trace(Object message){
		System.out.println(message);
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		JFileChooser _fileChooser = new JFileChooser();
		 _fileChooser.setDialogTitle("Please choose you CRD file");		 
		 _fileChooser.setFileFilter(new FileFilter(){

			@Override
			public boolean accept(File f) {
				if(f.isDirectory())return true;
				if(f.isFile() && f.getName().contains(".crd"))
					return true;
				return false;
			}

			@Override
			public String getDescription() {
				// TODO Auto-generated method stub
				return "*.CRD";
			}
			 
		 
		});
		 JFrame frm = new JFrame("IO");
		 frm.setVisible(true);
		// System.out.println("ICI");
		 int retval = _fileChooser.showOpenDialog(frm);
		 
        if (retval == JFileChooser.APPROVE_OPTION) {
            //... The user selected a file, get it, use it.
            File file = _fileChooser.getSelectedFile();
            //File file = new File(path);
			FileInputStream in;
			try {
				in = new FileInputStream(file);
				//FileReader fin = new FileReader(file);
				StringBuffer buf = new StringBuffer();
				byte[] buffer = new byte[50];
				int read = 1;
				while(read!= -1){
					try {
						read = in.read(buffer);
						
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					if(read != -1){
						String tmp = new String(buffer,0,read);
						//buf.append(buffer,0,read);
						buf.append(tmp);
					}					
				}
				
				String toRet = buf.toString();
				System.out.print("//");
				InfoCard card = String2InfoCard(toRet);
				
				String toDisplay = "String "+file.getName().replace(".crd", "")+"_cardB64 = \"" + Base64.encodeBytes(toRet.getBytes())+"\";";
				toDisplay = toDisplay.replaceAll("\r\n", "\"+\n\"");
				System.out.println("String "+file.getName().replace(".crd", "")+"_CARID = \""+card.getCardId()+"\";");
				System.out.println(toDisplay);
				in.close();
				
			}catch (Exception e) {
				// TODO: handle exception
			}
			System.exit(0);
        }
	}
	public static InfoCard String2InfoCard(String xml){
		try {
			Element root;
			root = XmlFileUtil.readXml(new ByteArrayInputStream(xml.getBytes())).getRootElement();
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
		}
		return null;
	}

}
