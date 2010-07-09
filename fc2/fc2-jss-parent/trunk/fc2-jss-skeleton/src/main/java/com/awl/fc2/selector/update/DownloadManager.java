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
package com.awl.fc2.selector.update;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import javax.swing.JOptionPane;

import org.springframework.util.FileCopyUtils;

import com.awl.fc2.selector.AWLProxy;
import com.awl.fc2.selector.exceptions.Config_Exception_NotDone;
import com.awl.fc2.selector.exceptions.Config_Exeception_UnableToReadConfigFile;
import com.awl.fc2.selector.launcher.Config;

public class DownloadManager {

	/**
	 * @param args
	 * @throws Config_Exeception_UnableToReadConfigFile 
	 * @throws IOException 
	 */
	public static void main(String[] args) throws Config_Exeception_UnableToReadConfigFile, IOException {
		// TODO Auto-generated method stub
		//log.trace("list of arguments : ");
		
		for(int i=0;i<args.length;i++){
			//log.trace(i + " : " + args[i]);
			if(args[i].startsWith("cnf=")){
				String pathXML = args[i].split("=")[1];
				Config.getInstance(pathXML,false);
			}
		}
		AWLProxy.setProxy();
		float srvVersion = ServerVersion();
		
		if(srvVersion>localVersion()){
			int choice = JOptionPane.showOptionDialog(null, "New version of JSS is available, do you want to download it ?", "JSS download Manager",JOptionPane.OK_CANCEL_OPTION, 0, null, null, null);
			if(choice == JOptionPane.OK_OPTION){
				//Updating JAR
				try {
					FileCopyUtils.copy(new File(Config.getInstance().getPath()+"/selector-0.0.1-SNAPSHOT-jar-with-dependencies.jar"),
									   new File(Config.getInstance().getPath()+"/selector-0.0.1-SNAPSHOT-jar-with-dependencies_v"+localVersion()+".jar"));
					downloadJar(Config.getInstance().getPath()+"/selector-0.0.1-SNAPSHOT-jar-with-dependencies.jar");
				} catch (Config_Exception_NotDone e) {
					System.out.println("Should not be here");
				}
				//Update SWF
				try {
					FileCopyUtils.copy(new File(Config.getInstance().getPath()+"/testSelecteurIdentite2/selecteur.swf"),
									   new File(Config.getInstance().getPath()+"/testSelecteurIdentite2/selecteur_v"+localVersion()+".swf"));
					downloadSWF(Config.getInstance().getPath()+"/testSelecteurIdentite2/selecteur.swf");
				} catch (Config_Exception_NotDone e) {
					System.out.println("Should not be here");
				}
				//testSelecteurIdentite2/selecteur.swf
				
			}
			
		}
		
	
	}
	static public float localVersion(){
		return (float) 2.00;
	}
	static public float ServerVersion() throws IOException{
		String strUrl = "https://rentacar.atosworldline.bancaire.test.fc2consortium.org/fj/v2/VERSION.txt";
		
		 URL                url;
		    URLConnection      urlConn;
		    DataInputStream    dis;

		    url = new URL(strUrl);

		    // Note:  a more portable URL:
		    //url = new URL(getCodeBase().toString() + "/ToDoList/ToDoList.txt");

		    urlConn = url.openConnection();
		    urlConn.setDoInput(true);
		    urlConn.setUseCaches(false);

		    InputStream in = urlConn.getInputStream();
		    //dis = new DataInputStream(urlConn.getInputStream());
		    String s;
		   
		    byte [] b = new byte[1024];
		    int length;
		    int total = 24* 1024 ;
		    int cur = 0;
		    //do {		    	
			    length = in.read(b);
			  String version =   (new String(b,0,length));
			   
		    //}while(length!=-1);
			return Float.valueOf(version);
		   
		  
		
	}
	static public void downloadSWF(String whereInto) throws IOException{
		String strUrl = "https://rentacar.atosworldline.bancaire.test.fc2consortium.org/fj/v2/selecteur.swf";
						
		 URL                url;
		    URLConnection      urlConn;
		    DataInputStream    dis;

		    url = new URL(strUrl);

		    // Note:  a more portable URL:
		    //url = new URL(getCodeBase().toString() + "/ToDoList/ToDoList.txt");

		    urlConn = url.openConnection();
		    urlConn.setDoInput(true);
		    urlConn.setUseCaches(false);

		    InputStream in = urlConn.getInputStream();
		    //dis = new DataInputStream(urlConn.getInputStream());
		    String s;
		    FileOutputStream out = new FileOutputStream(whereInto);
		    
		    byte [] b = new byte[1024];
		    int length;
		   // int total = 24* 1024 ;
		    int cur = 0;
		    do {		    	
			    length = in.read(b);
			    if(length>0) {
			    	out.write(b,0,length);
			    	cur += 1;
			    }			   
			    System.out.print(".");
		    }while(length!=-1);
		   
		  out.close();
	}
	static public void downloadJar(String whereInto) throws IOException{
		String strUrl = "https://rentacar.atosworldline.bancaire.test.fc2consortium.org/fj/v2/fc2.jss.build-0.0.1-SNAPSHOT-jar-with-dependencies.jar";
						
		 URL                url;
		    URLConnection      urlConn;
		    DataInputStream    dis;

		    url = new URL(strUrl);

		    // Note:  a more portable URL:
		    //url = new URL(getCodeBase().toString() + "/ToDoList/ToDoList.txt");

		    urlConn = url.openConnection();
		    urlConn.setDoInput(true);
		    urlConn.setUseCaches(false);

		    InputStream in = urlConn.getInputStream();
		    //dis = new DataInputStream(urlConn.getInputStream());
		    String s;
		    FileOutputStream out = new FileOutputStream(whereInto);
		    
		    byte [] b = new byte[1024];
		    int length;
		    int total = 24* 1024 ;
		    int cur = 0;
		    do {		    	
			    length = in.read(b);
			    if(length>0) {
			    	out.write(b,0,length);
			    	cur += 1;
			    }			   
			    System.out.println("Complete " + cur/240);
		    }while(length!=-1);
		   
		  out.close();
	}

}
