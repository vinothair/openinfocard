/*
 * Copyright (c) 2007, Axel Nennker - http://axel.nennker.de/
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

package org.xmldap.xmldsig;

import java.math.BigInteger;
import java.security.interfaces.RSAPublicKey;

import nu.xom.Element;

import org.xmldap.exceptions.SerializationException;
import org.xmldap.util.Base64;
import org.xmldap.ws.WSConstants;

public class RsaPublicKeyInfo implements KeyInfo {
	RSAPublicKey key;
	
	/**
	 * This class is used when we don not want certificate information in the KeyInfo
	 * 
	 * @param key
	 */
	public RsaPublicKeyInfo(RSAPublicKey key) {
		this.key = key;
	}
	
	public Element serialize() throws SerializationException {
        Element keyInfo = new Element("dsig:KeyInfo", WSConstants.DSIG_NAMESPACE);
        Element keyValue = new Element("dsig:KeyValue", WSConstants.DSIG_NAMESPACE);
        Element rsaKeyValue = new Element("dsig:RSAKeyValue", WSConstants.DSIG_NAMESPACE);
        Element modulus = new Element("dsig:Modulus", WSConstants.DSIG_NAMESPACE);
        Element exponent = new Element("dsig:Exponent", WSConstants.DSIG_NAMESPACE);
        BigInteger mod = key.getModulus();
        byte[] modArray = mod.toByteArray();
        modulus.appendChild(Base64.encodeBytesNoBreaks(modArray));
        rsaKeyValue.appendChild(modulus);
        BigInteger exp = key.getPublicExponent();
        byte[] expArray = exp.toByteArray();
        exponent.appendChild(Base64.encodeBytesNoBreaks(expArray));
        rsaKeyValue.appendChild(exponent);
        keyValue.appendChild(rsaKeyValue);
        keyInfo.appendChild(keyValue);
		return keyInfo;
	}

	public String toXML() throws SerializationException {
		return serialize().toXML();
	}

}
