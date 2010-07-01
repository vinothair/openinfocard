/*
 * Copyright (c) 2006, Chuck Mortimore - xmldap.org
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

package org.xmldap.infocard.policy;

import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.Elements;

import org.json.JSONException;
import org.json.JSONObject;
import org.xmldap.exceptions.ParsingException;
import org.xmldap.exceptions.SerializationException;
import org.xmldap.ws.WSConstants;


public class SupportedClaim {

    private String displayName;
    private String uri;
    private String description;

    public SupportedClaim(String displayName, String uri, String description) {
        this.displayName = displayName;
        this.uri = uri;
        this.description = description;
    }

// <ic:SupportedClaimType Uri=”xs:anyURI”> 
//  <ic:DisplayTag> xs:string </ic:DisplayTag> ? 
//  <ic:Description> xs:string </ic:Description> ? 
// </ic:SupportedClaimType> 
    public SupportedClaim(Element supportedClaim) throws ParsingException {
    	if ("SupportedClaimType".equals(supportedClaim.getLocalName())) {
    		uri = supportedClaim.getAttributeValue("Uri");
    		Elements displayNameElements = supportedClaim.getChildElements("DisplayTag", WSConstants.INFOCARD_NAMESPACE);
    		if (displayNameElements.size() == 1) {
    			displayName = displayNameElements.get(0).getValue();
    		} else {
    			if (displayNameElements.size() > 0) {
    				throw new ParsingException("Expected one DisplayTag but found " + displayNameElements.size());
    			}
    		}
    		Elements descriptionElements = supportedClaim.getChildElements("Description", WSConstants.INFOCARD_NAMESPACE);
    		if (descriptionElements.size() == 1) {
    			description = descriptionElements.get(0).getValue();
    		} else {
    			if (descriptionElements.size() > 0) { 
    				throw new ParsingException("Expected one Description but found " + descriptionElements.size());
    			}
    		}
    	} else {
    		throw new ParsingException("Expected SupportedClaimType");
    	}
    }
    
    private Element getSupportedClaim() {

        Element supportedClaimType = new Element(WSConstants.INFOCARD_PREFIX + ":SupportedClaimType", WSConstants.INFOCARD_NAMESPACE);
        Attribute URI = new Attribute("Uri", uri);
        supportedClaimType.addAttribute(URI);
        if (displayName != null) {
	        Element displayTag = new Element(WSConstants.INFOCARD_PREFIX + ":DisplayTag", WSConstants.INFOCARD_NAMESPACE);
	        displayTag.appendChild(displayName);
	        supportedClaimType.appendChild(displayTag);
        }
        if (description != null) {
	        Element descriptionE = new Element(WSConstants.INFOCARD_PREFIX + ":Description", WSConstants.INFOCARD_NAMESPACE);
	        descriptionE.appendChild(description);
	        supportedClaimType.appendChild(descriptionE);
        }
        return supportedClaimType;

    }

    public JSONObject toJSON() throws SerializationException {
      try {
        JSONObject json = new JSONObject();
        json.put("Uri", uri);
        if (displayName != null) {
          json.put("DisplayTag", displayName);
        }
        if (description != null) {
          json.put("Description", description);
        }
        return json;
      } catch (JSONException e) {
        throw new SerializationException(e);
      }
    }

    public String toXML() throws SerializationException {
        Element token = serialize();
        return token.toXML();

    }

    public Element serialize() throws SerializationException {
        return getSupportedClaim();
    }


}
