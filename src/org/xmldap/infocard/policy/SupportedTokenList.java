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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import nu.xom.Element;
import nu.xom.Elements;

import org.xmldap.exceptions.ParsingException;
import org.xmldap.exceptions.SerializationException;
import org.xmldap.ws.WSConstants;
import org.xmldap.xml.Serializable;


public class SupportedTokenList implements Serializable {


    List<SupportedToken> supportedTokens = null;

    public SupportedTokenList(Element supportedTokenList) throws ParsingException {
    	Elements elts = supportedTokenList.getChildElements("TokenType", WSConstants.TRUST_NAMESPACE_05_02);
    	if (elts.size() == 0) {
    		throw new ParsingException("missing TokenType elements in SupportedTokenTypeList");
    	} else {
    		for (int i=0; i<elts.size(); i++) {
    			Element elt = elts.get(i);
    			SupportedToken supportedToken = new SupportedToken(elt);
    			addSupportedToken(supportedToken);
    		}
    	}
    }
    
    public SupportedTokenList(List<SupportedToken> supportedTokenList) {
    	this.supportedTokens = supportedTokenList;
    }
    
    public void addSupportedToken(SupportedToken token) {
    	if (supportedTokens == null) {
    		supportedTokens = new ArrayList<SupportedToken>();
    	}
        supportedTokens.add(token);

    }

    public List<SupportedToken> getSupportedTokens() {
        return supportedTokens;
    }


    private Element getSupportedTokenList() throws SerializationException {


        Element supportedTokenTypeList = new Element(WSConstants.INFOCARD_PREFIX + ":SupportedTokenTypeList", WSConstants.INFOCARD_NAMESPACE);


        Iterator<SupportedToken> tokens = getSupportedTokens().iterator();
        while (tokens.hasNext()) {

            SupportedToken token = (SupportedToken) tokens.next();
            supportedTokenTypeList.appendChild(token.serialize());

        }

        return supportedTokenTypeList;
    }

    public String toXML() throws SerializationException {

        Element policy = serialize();
        return policy.toXML();

    }

    public Element serialize() throws SerializationException {
        return getSupportedTokenList();
    }
}
