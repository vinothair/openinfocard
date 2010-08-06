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

package org.xmldap.saml;

import java.util.Iterator;
import java.util.Vector;

import nu.xom.Element;

import org.xmldap.exceptions.SerializationException;
import org.xmldap.ws.WSConstants;
import org.xmldap.xml.Serializable;


public class AttributeStatement implements Serializable {

    private Subject subject;
    private Vector attributes = new Vector();

    public void setSubject(Subject subject) {
        this.subject = subject;
    }

    public void addAttribute(Attribute attribute) {

        attributes.add(attribute);

    }

    private Element getAttributeStatement() throws SerializationException {

        Element attrStatement = new Element(WSConstants.SAML_PREFIX + ":AttributeStatement", WSConstants.SAML11_NAMESPACE);
        attrStatement.appendChild(subject.serialize());
        Iterator attrs = attributes.iterator();
        while (attrs.hasNext()) {

            Attribute attribute = (Attribute) attrs.next();
            attrStatement.appendChild(attribute.serialize());

        }

        return attrStatement;

    }

    public String toXML() throws SerializationException {

        Element attrStatement = serialize();
        return attrStatement.toXML();

    }

    public Element serialize() throws SerializationException {

        return getAttributeStatement();

    }

}
