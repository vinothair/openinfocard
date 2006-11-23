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

import nu.xom.Attribute;
import nu.xom.Element;
import org.xmldap.exceptions.SerializationException;
import org.xmldap.util.XSDDateTime;
import org.xmldap.ws.WSConstants;
import org.xmldap.xml.Serializable;


public class Conditions implements Serializable {

    private String notBefore;
    private String notOnOrAfter;

    /**
     * @param beforeNow negativ value in minutes before now
     * @param mins		positiv value in minutes after now
     */
    public Conditions(int nowMinus, int nowPlus) {

        XSDDateTime now = new XSDDateTime(nowMinus);
        notBefore = now.getDateTime();

        //TODO - make setable
        XSDDateTime andLater = new XSDDateTime(nowPlus);
        notOnOrAfter = andLater.getDateTime();


    }

    private Element getConditions() {
        Element conditions = new Element(WSConstants.SAML_PREFIX + ":Conditions", WSConstants.SAML11_NAMESPACE);
        Attribute notBeforeAttr = new Attribute("NotBefore", notBefore);
        Attribute notOnOrAfterAttr = new Attribute("NotOnOrAfter", notOnOrAfter);
        conditions.addAttribute(notBeforeAttr);
        conditions.addAttribute(notOnOrAfterAttr);
        return conditions;

    }

    public String toXML() throws SerializationException {

        Element condition = serialize();
        return condition.toXML();

    }

    public Element serialize() throws SerializationException {

        return getConditions();

    }

    public static void main(String[] args) {

        Conditions conditions = new Conditions(-10, 10);
        try {
            System.out.println(conditions.toXML());
        } catch (SerializationException e) {
            e.printStackTrace();
        }
    }
}
