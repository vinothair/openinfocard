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
package com.awl.fc2.selector.connector;

import java.io.IOException;

import com.awl.fc2.selector.exceptions.Config_Exception_NotDone;
import com.awl.fc2.selector.exceptions.Config_Exeception_MalFormedConfigFile;
import com.awl.fc2.selector.exceptions.Config_Exeception_UnableToReadConfigFile;

/**
 * Interface générique permettant l'échange de données entre un browser (firefox, ie,...) et le sélecteur.
 * 
 * @author A168594
 *
 */
public interface IConnector {

	/**
	 * Set the RP certificate
	 * @param certifBase64
	 */
	public abstract void setCertificate(String certifBase64);

	/**
	 * set the RP requested claims
	 * @param theClaims
	 */
	public abstract void setRequiredClaims(String theClaims);

	/**
	 * set the global request 
	 * @param inputFromAzigo
	 */
	public abstract void setInputFromRequestor(String inputFromAzigo);

	/**
	 * @return the signed assertions - if the authentication succeeds
	 * @throws Config_Exeception_UnableToReadConfigFile
	 * @throws Config_Exeception_MalFormedConfigFile
	 * @throws Config_Exception_NotDone
	 */
	public abstract String getToken()
			throws Config_Exeception_UnableToReadConfigFile,
			Config_Exeception_MalFormedConfigFile, Config_Exception_NotDone;
	
	public void run() throws IOException, Config_Exeception_UnableToReadConfigFile, Config_Exeception_MalFormedConfigFile, Config_Exception_NotDone;
	

}