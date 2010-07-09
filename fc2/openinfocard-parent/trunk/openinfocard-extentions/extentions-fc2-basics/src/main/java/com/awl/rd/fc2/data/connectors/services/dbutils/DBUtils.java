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
package com.awl.rd.fc2.data.connectors.services.dbutils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.xmldap.util.PropertiesManager;

import com.awl.rd.fc2.data.connectors.DataConnector;
import com.awl.rd.fc2.data.connectors.services.eid.EIDData;
import com.thoughtworks.xstream.XStream;

public class DBUtils {
	static Logger log = Logger.getLogger(DBUtils.class);
	public static void trace(Object msg){
		log.info(msg);
	}
	transient static DBUtils s_this = null;
	Hashtable<String, Container> m_tblClass_Container = null;
	protected DBUtils(){
		trace("Creating a new DBUtils ");
		m_tblClass_Container = new Hashtable<String, Container>();
	}
	
	public static void main(String arg[]){
		DBUtils db = DBUtils.getInstance();
		EIDData stef = new EIDData();
		stef.setGivenname("Cauchie");
		stef.setSurname("Stephan");
		db.addUser("stef", stef);		
		System.out.println(db.getUser(EIDData.class, "stef").getGivenname());
		
	}
	static String getStoragePath(){
		
		String l_strPath2XML = PropertiesManager.getInstance().getProperty("storageFile");
		if(l_strPath2XML==null){
			trace("CONFIG FILE ERROR");
			l_strPath2XML = "c:/DBUtils.xml";
			
		}
		l_strPath2XML= l_strPath2XML.replace(".xml", "_DBUtils.xml");
		return l_strPath2XML;
	}
	public void save(){
		String l_strPath2XML = getStoragePath();
		XStream xstream = new XStream();
		String destFile = null;
		try {

			destFile = l_strPath2XML;
			destFile = destFile.replace("//", "/");
			System.out.println("Writing to : " + destFile);
			BufferedWriter out = new BufferedWriter(new FileWriter(destFile));
			xstream.toXML(this, out);
			
			out.flush();
			out.close(); 
		} catch (IOException e) {
			e.printStackTrace()		;	
		}
	}
	static DBUtils load(DataConnector root){
		String destFile = getStoragePath();//PropertiesManager.getInstance().getProperty("storageFile");//"c:/dataConnector.xml";
		XStream xstream = new XStream();
		try {
			BufferedReader fichier = new BufferedReader(new FileReader(destFile));
			return (DBUtils) xstream.fromXML(fichier,new DBUtils());
		} catch (FileNotFoundException e) {
			trace("File not found, create a new DB");
		}	
		trace("Create a new DB");
		return new DBUtils();
	}
	public static DBUtils getInstance(){
		if(s_this == null){
			s_this = load(null);
		}
		return s_this;
	}
	
	
	// Handle the  Store
	public <T> void addUser(String username, T data){
		String _cls = data.getClass().getCanonicalName();
		Container container = null;
		if(!(m_tblClass_Container.containsKey(_cls))){
			
			
			container = new Container();
			m_tblClass_Container.put(_cls, container);
			//m_tblUserId_Container.put(username,);
			
		}else{
			trace("Finding the container for " + _cls);
			container = m_tblClass_Container.get(_cls);
		}
		
		container.m_tblUserId_T.put(username, data);
		save();
	}
	public <T>  T getUser(Class<T> cls, String username){
		String _cls = cls.getCanonicalName();
		trace("Looking for the container : " + _cls);
		if(m_tblClass_Container.containsKey(_cls)){		
			trace("Container found");
			trace("Getting the user -" + username);
			Container container = m_tblClass_Container.get(_cls);
			trace("Size of the container " + container.m_tblUserId_T.size());
//			if(container.m_tblUserId_T.containsKey(username)){
//				T toRet = (T) container.m_tblUserId_T.get(username);
//				trace(toRet);
//				return toRet;
//			}
			return (T) container.m_tblUserId_T.get(username);
		}
		return null;
	}
	
//	public Vector<String> listUsers(){
//		Vector<String> toRet = new Vector<String>();
//		Set<String> keys = m_tblClass_Container.keySet();
//		Iterator<String> iter = keys.iterator();
//		while (iter.hasNext())
//		{
//			String key = iter.next();
//			toRet.add(key);
//		}
//		return toRet;
//	}
}
class Container
{
	Hashtable<String, Object> m_tblUserId_T = new Hashtable<String, Object>();
}


