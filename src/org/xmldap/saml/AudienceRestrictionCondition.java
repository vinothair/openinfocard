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
package org.xmldap.saml;

import nu.xom.Element;

import org.xmldap.exceptions.SerializationException;
import org.xmldap.ws.WSConstants;
import org.xmldap.xml.Serializable;

public class AudienceRestrictionCondition implements Serializable {
	//   <saml:AudienceRestrictionCondition>
	//    <saml:Audience>https://w4de3esy0069028.gdc-bln01.t-systems.com:8443/relyingparty/</saml:Audience>
	//   </saml:AudienceRestrictionCondition>
	String restrictedTo = null;
	public AudienceRestrictionCondition(String restrictedTo) {
		this.restrictedTo = restrictedTo;
	}
	public Element serialize() throws SerializationException {
        Element audienceRestrictionCondition = new Element(WSConstants.SAML_PREFIX + ":AudienceRestrictionCondition", WSConstants.SAML11_NAMESPACE);
        Element audience = new Element(WSConstants.SAML_PREFIX + ":Audience", WSConstants.SAML11_NAMESPACE);
        audience.appendChild(restrictedTo);
        audienceRestrictionCondition.appendChild(audience);
		return audienceRestrictionCondition;
	}

	public String toXML() throws SerializationException {
		return serialize().toXML();
	}

}