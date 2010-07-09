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
package com.awl.fc2.selector.diagnostic.test;

import java.io.File;

import javax.swing.JOptionPane;

import com.awl.fc2.selector.diagnostic.IFC2Test;
import com.awl.fc2.selector.diagnostic.TestReport;
import com.awl.fc2.selector.exceptions.Config_Exception_NotDone;
import com.awl.fc2.selector.exceptions.Config_Exeception_MalFormedConfigFile;
import com.awl.fc2.selector.launcher.Config;
import com.awl.logger.Logger;


public class TestConfig implements IFC2Test{
	static Logger log = new Logger(TestConfig.class);
	static public void trace(Object msg){
		log.trace(msg);		
	}
	TestReport report = null;
	public void run(){
		report = new TestReport("Config");
		trace("running Test of Config");
		try {
			Config cnf = Config.getInstance();
			String path = cnf.getPath();
			if(path == null){
				report.addProblem("Could not load extract the path from the xml config file",
							  "No image, Flex UI may froze", 
							  "in the ini file, put cnf=ABSOLUTE PATH to .xml\n" +
							  "in the xml, put <IMGS>$PATH$/imgs</IMGS>\n" +
							  "Create the imgs folder into the directory");
			}
			{
				File f = new File(path);
				if(!f.isAbsolute()){
					report.addProblem("PATH is not absolute",
							  "No image, Flex UI may froze", 
							  "in the ini file, put cnf=ABSOLUTE PATH to .xml");
				}
			}
			{
				try {
					String ImgFolder = cnf.getImgFolder();
					File f = new File(ImgFolder);
					if(!f.isAbsolute()){
						report.addProblem("Image Folder does not exist or you forget to put $PATH$",
							  "No image, Flex UI may froze", 
							  "in the Config_Selector.xml : put <IMGS>$PATH$/imgs/</IMGS>\n" +
							  "And create the directory imgs");
					}
					if(!f.exists()){
						report.addProblem("Image Folder does not exist or you forget to put $PATH$",
								  "No image, Flex UI may froze", 
								  "in the Config_Selector.xml : put <IMGS>$PATH$/imgs/</IMGS>\n" +
								  "And create the directory imgs : " + f.getAbsolutePath());
						JOptionPane.showConfirmDialog(null, "the image directory does not exsit, we try to create it");
						f.mkdirs();
					}
					
				} catch (Config_Exeception_MalFormedConfigFile e) {
					report.addProblem("Image Folder does not exist",
							  "No image, Flex UI may froze", 
							  "in the Config_Selector.xml : put <IMGS>$PATH$/imgs/</IMGS>\n" +
							  "And create the directory imgs");
				}
				
			}
		} catch (Config_Exception_NotDone e) {
			report.addProblem("Could not load Config_Selecteur.xml",
							  "NOTHING WILL WORK", 
							  "in the ini file, put cnf=ABSOLUTE PATH to .xml");
		}
		
	}
	@Override
	public TestReport getReport() {
		// TODO Auto-generated method stub
		return report;
	}
}
