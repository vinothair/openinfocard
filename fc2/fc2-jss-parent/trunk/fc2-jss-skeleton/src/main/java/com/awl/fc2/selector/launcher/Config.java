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
package com.awl.fc2.selector.launcher;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Vector;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;

import com.awl.fc2.plugin.PlugInLauncher;
import com.awl.fc2.selector.connector.IConnector;
import com.awl.fc2.selector.exceptions.Config_Exception_NotDone;
import com.awl.fc2.selector.exceptions.Config_Exeception_MalFormedConfigFile;
import com.awl.fc2.selector.exceptions.Config_Exeception_UnableToReadConfigFile;
import com.awl.fc2.selector.storage.ICardStoreV2;
import com.awl.fc2.selector.userinterface.ISelectorUI;
import com.awl.logger.Logger;
import com.utils.XMLParser;
import com.utils.execeptions.XMLParser_Exception_NO_ATTRIBUTE;
/**
 * This class is used to centralized the access the config file. 
 * @see Logger
 * @author Cauchie stephane
 *
 */
public class Config {
	static Logger log = new Logger(Config.class);	
	static public void trace(Object msg){
		log.trace(msg);
	}
	String xmlConfig = null;
	static protected Config s_this = null;
	boolean tryToFindManually = false;
	boolean tryToFindAutomatically = false;
	private String path = null;
	
	PlugInLauncher plugInDB = new PlugInLauncher();
	
	static String defaultDigestMethod="SHA";
	static public String getDigestMethod(){
		return defaultDigestMethod;
	}
	public String getPath(){
		return path;
	}
	/**
	 * @return the XML in String format contained in the Config_Selector.xml file.
	 * @throws Config_Exception_NotDone if you did not call getInstance(path2ConfigFile) first
	 * @Remark The XML file is read only once, the returned string is stored.
	 */
	public String getXML() throws Config_Exception_NotDone{
		if(xmlConfig == null) throw(new Config_Exception_NotDone("Please initialize properly the config object (getInstance(path))"));
		return xmlConfig;
		
	}
	
	/**
	 * Read the file located by {@code pathXML}.<br/>
	 * If an error occurs the exception is throwed<br/>
	 * This function can only be called internaly, the constructor is calling it. We do that to replace all the {@code %PATH%} by the real path 
	 * @param pathXML
	 * @throws Config_Exeception_UnableToReadConfigFile
	 */
	protected void loadXML(String pathXML)throws Config_Exeception_UnableToReadConfigFile{	
		trace("loadXML("+pathXML+")");		
		try {								
			File file = new File(pathXML);
			FileReader fin = new FileReader(file);
			StringBuffer buf = new StringBuffer();
			char[] buffer = new char[50];
			int read = 1;
			while(read!= -1){
				try {
					read = fin.read(buffer);
				} catch (IOException e) {
					throw(new Config_Exeception_UnableToReadConfigFile(e.getMessage()));
				}
				if(read != -1){
					String tmp = new String(buffer,0,read);
					//buf.append(buffer,0,read);
					buf.append(tmp);
				}
				
			}								
			String toRet = buf.toString().replace("$PATH$", this.path);;			
			xmlConfig = toRet;
		}catch (Exception e) {
			boolean succeed = false;
			if(!tryToFindAutomatically){
				succeed = automaticallyFindConfigXML();
				if(succeed) return;
			}
			if(!tryToFindManually)
				manuallyFindConfigXML();
			else{
				throw(new Config_Exeception_UnableToReadConfigFile(e.getMessage()));
			}
			//throw(new Config_Exeception_UnableToReadConfigFile(e.getMessage()));
			
		}		
	}
	protected boolean automaticallyFindConfigXML(){
		JOptionPane.showConfirmDialog(null, "JSS is not launch properly, we try to find Config_Selector.xml,");
		return false;
	}
	protected void manuallyFindConfigXML() throws Config_Exeception_UnableToReadConfigFile{
		tryToFindManually = true; 
		JOptionPane.showConfirmDialog(null, "Could not find the Config_Selector.xml, find it manually");
		JFileChooser _fileChooser = new JFileChooser();
		 _fileChooser.setDialogTitle("Please choose you Config file");
		 _fileChooser.setFileFilter(new FileFilter(){

			@Override
			public boolean accept(File f) {
				if(f.isDirectory()) return true;
				if(f.isFile() && f.getName().contains(".xml"))
					return true;
				return false;
			}

			@Override
			public String getDescription() {
				// TODO Auto-generated method stub
				return "*.xml";
			}
			 
		 
		});
		 
		 int retval = _fileChooser.showOpenDialog(null);
		 
         if (retval == JFileChooser.APPROVE_OPTION) {
             //... The user selected a file, get it, use it.
             File file = _fileChooser.getSelectedFile();
             String pathXML = file.getAbsolutePath();
             trace("PATH_XML : " + pathXML);
             
             this.path=extractPath(pathXML);
             loadXML(pathXML);//.replaceAll("\\", "/"));
         }
	}
	
	protected String extractPath(String pathXML){
		try {
			return pathXML.substring(0, pathXML.lastIndexOf("/"));
		} catch (java.lang.StringIndexOutOfBoundsException e) {
			return pathXML.substring(0, pathXML.lastIndexOf("\\"));
		}
	}
	/**
	 * Construct the Config object. It extracts the real path and then call {@code loadXML(this.path);}
	 * @param pathXML
	 * @throws Config_Exeception_UnableToReadConfigFile
	 */
	protected  Config(String pathXML, boolean loadPlugins) throws Config_Exeception_UnableToReadConfigFile {
		log.trace("Configuring with the following file : " + pathXML);
		//this.path = pathXML.substring(0, pathXML.lastIndexOf("/"));
		this.path = extractPath(pathXML);
		loadXML(pathXML);
		s_this = this;
		if(loadPlugins) loadPluginDB();
		
	}
	/**
	 * get the singleton instance of the Config class. By calling this getInstance(String), a new one is creating (meaning a new xml parse is created).
	 * @param pathXML
	 * @return the Config singleton object
	 * @throws Config_Exeception_UnableToReadConfigFile
	 */
	static public Config getInstance(String pathXML, boolean loadPlugin) throws Config_Exeception_UnableToReadConfigFile{
		if(s_this == null){
			s_this = new Config(pathXML,loadPlugin);
			
		}
		return s_this;
	}
	void loadPluginDB(){
		plugInDB.launch(this);		
	}
	public PlugInLauncher getPlugInDB(){
		return plugInDB;
	}
	
	/**
	 * 
	 * @return the Config singleton object
	 * @throws Config_Exception_NotDone if the Config is not well initialized (call getInstance(String) first).
	 */
	static public Config getInstance() throws Config_Exception_NotDone{
		if(s_this == null){
			throw(new Config_Exception_NotDone("The Configurator is not initiatilize, you may not have call the launcher"));
		}
		return s_this;
	}
	
	/**
	 * 
	 * @return the class that can handle the UserInterface job
	 * @throws Config_Exeception_MalFormedConfigFile if the XML does not contains the balise {@code <UI_CLASS>}
	 * @see ISelectorUI
	 */
	public String getClassForUI() throws Config_Exeception_MalFormedConfigFile{
		try {
			return XMLParser.getFirstValue(xmlConfig, "UI_CLASS");
		} catch (XMLParser_Exception_NO_ATTRIBUTE e) {
			throw(new Config_Exeception_MalFormedConfigFile("UI_CLASS not found"));
		}
	}
	
	/**
	 * 
	 * @return the class that can handle the PluginConnection job
	 * @throws Config_Exeception_MalFormedConfigFile if the XML does not contains the balise {@code <STARTER_CLASS>}
	 * @see IPluginConnection
	 */
	public String getClassForPlugin() throws Config_Exeception_MalFormedConfigFile{
		try {
			return XMLParser.getFirstValue(xmlConfig, "STARTER_CLASS");
		} catch (XMLParser_Exception_NO_ATTRIBUTE e) {
			throw(new Config_Exeception_MalFormedConfigFile("STARTER_CLASS not found"));
		}
	}
	/**
	 * 
	 * @return the class that can handle the Connector job
	 * @throws Config_Exeception_MalFormedConfigFile if the XML does not contains the balise {@code <CONNECTOR_CLASS>}
	 * @see IConnector
	 */
	public String getClassForConnector()throws Config_Exeception_MalFormedConfigFile{
		try {
			return XMLParser.getFirstValue(xmlConfig, "CONNECTOR_CLASS");
		} catch (XMLParser_Exception_NO_ATTRIBUTE e) {
			throw(new Config_Exeception_MalFormedConfigFile("CONNECTOR_CLASS not found"));
		}
	}
	/**
	 * 
	 * @return the interface containing the selector user interface object
	 * @throws Config_Exeception_MalFormedConfigFile
	 * @throws Config_Exception_NotDone
	 */
	public ISelectorUI getUI() throws Config_Exeception_MalFormedConfigFile, Config_Exception_NotDone{
//		String cls = com.awl.fc2.selector.launcher.Config.getInstance().getClassForUI();		
//		return (ISelectorUI) createObject(cls);
		return plugInDB.getUI();
	}
	
	/**
	 * 
	 * @return return the IConnector instantiate object
	 * @throws Config_Exeception_MalFormedConfigFile
	 * @throws Config_Exception_NotDone
	 */
	public Vector<IConnector> getConnectors() throws Config_Exeception_MalFormedConfigFile, Config_Exception_NotDone{
//		String cls = com.awl.fc2.selector.launcher.Config.getInstance().getClassForConnector();		
//		return (IConnector) createObject(cls);
		return plugInDB.getConnectors();
	}	
	
	/**
	 * 
	 * @return the IPluginConnection instantiate object
	 * @throws Config_Exeception_MalFormedConfigFile
	 * @throws Config_Exception_NotDone
	 */
//	public IPluginConnection getPlugin() throws Config_Exeception_MalFormedConfigFile, Config_Exception_NotDone{
//		String cls = com.awl.fc2.selector.launcher.Config.getInstance().getClassForPlugin();		
//		return (IPluginConnection) createObject(cls);
//	}
	/**
	 * Method that instantiate a new object 
	 * @param cls the class to be instantiate
	 * @return the desired object
	 * @throws Config_Exeception_MalFormedConfigFile if something goes wrong (IllegalAccess,ClassNotFound,...)
	 */
	protected Object createObject(String cls) throws Config_Exeception_MalFormedConfigFile{
		log.trace("createObject("+cls+")");
		try{						
			return (Object) Class.forName(cls).newInstance();
		} catch (InstantiationException e) {
			throw(new Config_Exeception_MalFormedConfigFile("Unable to instanciate"));
		} catch (IllegalAccessException e) {
			throw(new Config_Exeception_MalFormedConfigFile("Unable to instanciate"));
		} catch (ClassNotFoundException e) {
			throw(new Config_Exeception_MalFormedConfigFile("Unable to instanciate"));		}
		
	}
	/**
	 * 
	 * @return the directory where all debug files will be created
	 * @throws Config_Exeception_MalFormedConfigFile
	 */
	public String getDebugFolder() throws Config_Exeception_MalFormedConfigFile{
		try {
			return XMLParser.getFirstValue(xmlConfig, "DEBUG").replace("$PATH$", path);
		} catch (XMLParser_Exception_NO_ATTRIBUTE e) {
			throw(new Config_Exeception_MalFormedConfigFile("DEBUG not found"));
		}
	}
	
	public String getImgFolder() throws Config_Exeception_MalFormedConfigFile{
		try {
			return XMLParser.getFirstValue(xmlConfig, "IMGS").replace("$PATH$", path);
		} catch (XMLParser_Exception_NO_ATTRIBUTE e) {
			throw(new Config_Exeception_MalFormedConfigFile("DEBUG not found"));
		}
	}
	
	
	public ICardStoreV2  getCardStorage(){ //throws Config_Exeception_MalFormedConfigFile, Config_Exception_NotDone{
//		String cls = com.awl.fc2.selector.launcher.Config.getInstance().getClassForCardStorage();		
//		return  (ICardStoreV2) createObject(cls);
		return plugInDB.getCardStore();
	}
	
	public String getClassForCardStorage()throws Config_Exeception_MalFormedConfigFile{
		try {
			return XMLParser.getFirstValue(xmlConfig, "CARD_STORAGE_CLASS");
		} catch (XMLParser_Exception_NO_ATTRIBUTE e) {
			throw(new Config_Exeception_MalFormedConfigFile("CARD_STORAGE_CLASS not found"));
		}
	}
	public boolean distributedWeakCredentials(){
		try {
			String tmp =  XMLParser.getFirstValue(xmlConfig, "DISTRIBUTED_WEAK_AUTHENTICATION");
			if("TRUE".equalsIgnoreCase(tmp)){
				return true;
			}else{
				return false;
			}
		} catch (XMLParser_Exception_NO_ATTRIBUTE e) {
			return true;
		}
	}
	
	
//	public String getUseWallet() throws Config_Exeception_MalFormedConfigFile {
//		try {
//			return XMLParser.getFirstValue(xmlConfig, "USE_WALLET");
//		} catch (XMLParser_Exception_NO_ATTRIBUTE e) {
//			throw new Config_Exeception_MalFormedConfigFile("CARDSTORE_TYPE not found");
//		}
//	}
	
}
