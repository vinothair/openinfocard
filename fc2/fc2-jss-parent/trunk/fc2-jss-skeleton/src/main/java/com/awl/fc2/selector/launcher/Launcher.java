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

import java.io.IOException;

import com.awl.fc2.selector.connector.IConnector;
import com.awl.fc2.selector.exceptions.Config_Exception_NotDone;
import com.awl.fc2.selector.exceptions.Config_Exeception_MalFormedConfigFile;
import com.awl.fc2.selector.exceptions.Config_Exeception_UnableToReadConfigFile;
import com.awl.logger.Logger;
/**
 * The Launcher class handles the starting of the selector. You can either use the constructor in your own projet or use the static main function (the argument for both is cnf=../somewhere/Config_Selector.xml)
 * 
 * 
 * 
 *  @see Logger com.awl.logger.Logger
 *  
 * @author Cauchie stéphane<br/> 
 */
public class Launcher {
	/**
	 * the instance of the logger of this class
	 * @see Logger
	 * 
	 */
	com.awl.logger.Logger log = new Logger(Launcher.class);
	
	/**
	 * Main constructor of the launcher (the constructor is in charge of constructing the Config object)
	 * @param args the argument has to be the property {@code cnf} assigned to the path where file {@code Config_Selecteur.xml} is located
	 * @see Config Config
	 */
	public Launcher(String[] args) throws Config_Exeception_UnableToReadConfigFile {
		log.trace("list of arguments : ");
		
		for(int i=0;i<args.length;i++){
			log.trace(i + " : " + args[i]);
			if(args[i].startsWith("cnf=")){
				String pathXML = args[i].split("=")[1];
				Config.getInstance(pathXML,true);
			}
		}
	}
	/**
	 * The launch method get the plugin object defined in the Config object.<br/>
	 * It then calls the {@code run()} methods.
	 * @see IPluginConnection
	 * @see Config
	 * @throws IOException
	 * @throws Config_Exeception_UnableToReadConfigFile
	 * @throws Config_Exeception_MalFormedConfigFile
	 * @throws Config_Exception_NotDone
	 */
	public void launch() throws IOException, Config_Exeception_UnableToReadConfigFile, Config_Exeception_MalFormedConfigFile, Config_Exception_NotDone{
		log.trace("Launching the Selector");
		//Selector.getInstance().openSession();
		for(IConnector connector : Config.getInstance().getConnectors()){
			log.trace("-> found connector ("+connector.getClass().getName()+")");
			connector.run();
		}
		
	}
	/**
	 * Entry point of our selector
	 * the argument has to be the property {@code cnf} assigned to thepath to the file {@code Config_Selecteur.xml}<br/>
	 * {@code ex: java -cp .... com.awl.fc2.selector.launcher.Launcher cnf=c:/Config_Selecteur.xml}
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			Launcher launcher = new Launcher(args);
			launcher.launch();
		} catch (Config_Exeception_UnableToReadConfigFile e) {			
			e.printStackTrace();
		} catch (IOException e) { 
			e.printStackTrace();
		} catch (Config_Exeception_MalFormedConfigFile e) {
			e.printStackTrace();
		} catch (Config_Exception_NotDone e) {
			e.printStackTrace();
		}

	}

}
