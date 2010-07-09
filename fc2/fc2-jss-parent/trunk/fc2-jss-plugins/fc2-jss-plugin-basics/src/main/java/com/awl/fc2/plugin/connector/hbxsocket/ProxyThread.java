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
package com.awl.fc2.plugin.connector.hbxsocket;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import com.awl.fc2.selector.connector.IConnector;
import com.awl.fc2.selector.exceptions.Config_Exception_NotDone;
import com.awl.fc2.selector.exceptions.Config_Exeception_MalFormedConfigFile;
import com.awl.fc2.selector.exceptions.Config_Exeception_UnableToReadConfigFile;
import com.awl.logger.Logger;

/**
 * This class is a thread that is constructed when a request from the external application is produced.
 * 
 * @author Cauchie stéphane
 * @see com.awl.fc2.selector.connector.IPluginConnection
 * @see com.awl.fc2.selector.connector.PluginConnection
 * 
 */
public class ProxyThread extends Thread 
{
	Socket incoming;
	String entete = "ProxyThread : ";
	IConnector azigoOut;
	static Logger log = new Logger(ProxyThread.class);
	static public void trace(Object msg){
		log.trace(msg);
	}	
	//public boolean isAbleToRelay=true;
	//public boolean isInChargeOfDecideWorkFlow=false;
	/**
	 * @param in the client socket.
	 * @param l a string that will be added in the log (in order to differientiate the differente threads)
	 */
	public ProxyThread(IConnector connector,Socket in,String l)
	{
		incoming = in;
		entete = l;
		azigoOut = connector;
	}

//	synchronized public void wakeup(){
//		notify();
//	}
	// Overwritten run() method of thread,
	// does the data transfers
	/**
	 * The thread. It is in charge of read all the request from the client socket.<br/>
	 * It then as the IConnector interface to the Config class.</br>
	 * {@code IConnector azigoOut;	}<br/>			
	 * {@code azigoOut = Config.getInstance().getConnector();		}<br/>
	 * Then it gives the input resquest and get the computed token. <br/>If something goes wrong, the socket is closed.
	 */
	synchronized public void run()
	{
		byte[] buffer = new byte[60];
		int numberRead = 0;
		OutputStream toClient;
		InputStream fromClient;
		
		try{
			toClient = incoming.getOutputStream();      
			fromClient = incoming.getInputStream();
			String m_strGlobalMessage="";
			while(true)
			{
				
				
				numberRead = fromClient.read(buffer, 0, 50);
				
				if(numberRead == -1)
				{
					incoming.close();				
				}
				m_strGlobalMessage += new String(buffer).substring(0, numberRead);
				if(m_strGlobalMessage.contains("</hbx_request>")){				
					System.out.println(m_strGlobalMessage);
					
					try {
											
						//azigoOut = Config.getInstance().getConnector();					
						azigoOut.setInputFromRequestor(m_strGlobalMessage);
						String responseToAzigo = "";		
						responseToAzigo = azigoOut.getToken();
						toClient.write(responseToAzigo.getBytes());
						toClient.close();
						
					} catch (Config_Exeception_UnableToReadConfigFile e) {						
						e.printStackTrace();
						incoming.close();	
					} catch (Config_Exeception_MalFormedConfigFile e) {
						e.printStackTrace();
						incoming.close();	
					} catch (Config_Exception_NotDone e1) {
						e1.printStackTrace();
						incoming.close();	
					}
					return;					
				}
				

			}
		}
		catch(IOException e) 
		{
			e.printStackTrace();
				
		}
		catch(ArrayIndexOutOfBoundsException e)
		{
			e.printStackTrace();
			
		}
	}
}
