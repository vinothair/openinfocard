/*
 * Copyright (c) 2007, Axel Nennker - axel () nennker.de
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
package org.xmldap.wsse;

import nu.xom.Attribute;
import nu.xom.Element;

import org.xmldap.exceptions.SerializationException;
import org.xmldap.ws.WSConstants;
import org.xmldap.xml.Serializable;

public class KeyIdentifier implements Serializable {

	String valueType = null;
	String encodingType = null;
	String value = null;
	/**
	 * @param valueType
	 * @param encodingType
	 * @param value
	 */
	public KeyIdentifier(String valueType, String encodingType, String value) {
		this.valueType = valueType;
		this.encodingType = encodingType; 
		this.value = value;
	}
	/**
	 * @param valueType
	 * @param value
	 */
	public KeyIdentifier(String valueType, String value) {
		this.valueType = valueType;
		this.encodingType = null; 
		this.value = value;
	}
	public Element serialize() throws SerializationException {
		Element keyIdentifier = new Element(WSConstants.WSSE_PREFIX + ":KeyIdentifier", WSConstants.WSSE_NAMESPACE_OASIS_10);
    	keyIdentifier.addAttribute(new Attribute("ValueType", valueType));
    	if (encodingType != null) {
    		keyIdentifier.addAttribute(new Attribute("EncodingType", encodingType));
    	}
    	keyIdentifier.appendChild(value);
		return keyIdentifier;
	}

	public String toXML() throws SerializationException {
		return serialize().toXML();
	}

}
