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
import org.xmldap.exceptions.SerializationException;
import org.xmldap.ws.WSConstants;


public class SupportedClaim {

    private String displayName;
    private String uri;

    public SupportedClaim(String displayName, String uri) {
        this.displayName = displayName;
        this.uri = uri;
    }


    private Element getSupportedClaim() {

        Element supportedClaimType = new Element(WSConstants.INFOCARD_PREFIX + ":SupportedClaimType", WSConstants.INFOCARD_NAMESPACE);
        Attribute URI = new Attribute("Uri", uri);
        supportedClaimType.addAttribute(URI);
        Element displayTag = new Element(WSConstants.INFOCARD_PREFIX + ":DisplayTag", WSConstants.INFOCARD_NAMESPACE);
        displayTag.appendChild(displayName);
        supportedClaimType.appendChild(displayTag);

        Element description = new Element(WSConstants.INFOCARD_PREFIX + ":Description", WSConstants.INFOCARD_NAMESPACE);
        description.appendChild("A description");
        supportedClaimType.appendChild(description);

        return supportedClaimType;

    }

    public String toXML() throws SerializationException {
        Element token = serialize();
        return token.toXML();

    }

    public Element serialize() throws SerializationException {
        return getSupportedClaim();
    }


}