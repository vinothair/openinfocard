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
 *     * The names of the contributors may NOT be used to endorse or promote products
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
 * 
 */
package com.awl.rd.fc2.data.connectors.services.paymentDBBandit;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

import org.apache.log4j.Logger;
import org.xmldap.util.PropertiesManager;

import com.utils.XMLParser;
import com.utils.execeptions.XMLParser_Exception_NO_ATTRIBUTE;
import com.utils.execeptions.XMLParser_Exception_NoNextValue;


public class DBBandit {
	static Logger log = Logger.getLogger(DBBandit.class);
	public static void trace(Object message){
		log.info(message);
	}
	static String xmlBDDUsers;
	public DBBandit() {
		try {
			load();
		} catch (XMLParser_Exception_NO_ATTRIBUTE e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	static HashMap<String,Entity> m_tblEntity = null;
	public void initVector(){
		if(m_tblEntity == null){
			m_tblEntity = new HashMap<String,Entity>();
		}		
	}
	public void load() throws XMLParser_Exception_NO_ATTRIBUTE{
		//From property manager
		if(m_tblEntity != null) return ;
		String l_strPath = PropertiesManager.getInstance().getProperty("banditbddxml");
		xmlBDDUsers = getContentFile(l_strPath);
	//	trace("BDD xml : "+xmlBDDUsers);
		trace("Parsing xml");
		//m_vecEntity.clear();
		initVector();
		XMLParser parser = new XMLParser(xmlBDDUsers);
		parser.query("Entity");
		while(parser.hasNext()){
			String xmlEntity;
			try {
				xmlEntity = parser.getNextXML();
			} catch (XMLParser_Exception_NoNextValue e) {
				throw( new XMLParser_Exception_NO_ATTRIBUTE(e.getMessage()));
			}
			//trace("Current entity : " + xmlEntity);
			Entity toAdd = new Entity(xmlEntity);
			m_tblEntity.put(toAdd.getUsername(),toAdd);
		}
	}
	public String getContentFile(String l_strPath){
		trace("getConfigXML() : Loading " + l_strPath);
			
		try {
			trace("Openning : file://"+l_strPath );
			//URL url = new URL("file://"+l_strPath);
		//	InputStream in;	
			//in = url.openStream();
			FileReader fin = new FileReader(new File(l_strPath));
			
			StringBuffer buf = new StringBuffer();
			char[] buffer = new char[50];
			int read = 1;
			while(read!= -1){
				try {
					//read = in.read(buffer);
					read =fin.read(buffer);
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
			return toRet;
		} catch (IOException e1) {
			return null;
		}
		
	}
	public Entity getEntityFromUserID(String userId){
		return m_tblEntity.get(userId);
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
//		DBBandit bdd = new DBBandit();
//	
//		DBBandit bdd2 = new DBBandit();
//		
//		
	}

}

