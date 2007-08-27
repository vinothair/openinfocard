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

import java.io.IOException;
import java.util.Calendar;

import nu.xom.Attribute;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.ParsingException;
import nu.xom.ValidityException;

import org.xmldap.exceptions.SerializationException;
import org.xmldap.util.XSDDateTime;
import org.xmldap.ws.WSConstants;
import org.xmldap.xml.Serializable;
import org.xmldap.xml.XmlUtils;


public class Conditions implements Serializable {
	//  <saml:Conditions 
	//   NotBefore="2007-08-21T07:18:50.605Z" 
	//   NotOnOrAfter="2007-08-21T08:18:50.605Z">
	//   <saml:AudienceRestrictionCondition>
	//    <saml:Audience>https://w4de3esy0069028.gdc-bln01.t-systems.com:8443/relyingparty/</saml:Audience>
	//   </saml:AudienceRestrictionCondition>
	//  </saml:Conditions>

    private String notBefore;
    private String notOnOrAfter;
    private AudienceRestrictionCondition audienceRestrictionCondition = null;
    
    private void init(Element element) {
        //Get the values
        String notBeforeVal = element.getAttribute("NotBefore").getValue();
        String notOnOrAfterVal = element.getAttribute("NotOnOrAfter").getValue();

        // parse and reconstruct to check the format
        Calendar notBeforeValCalendar  = XSDDateTime.parse(notBeforeVal);
        Calendar notOnOrAfterValCalendar = XSDDateTime.parse(notOnOrAfterVal);
        notBefore = XSDDateTime.getDateTime(notBeforeValCalendar);
        notOnOrAfter = XSDDateTime.getDateTime(notOnOrAfterValCalendar);
    }
    
    public Conditions(String xml) throws ValidityException, ParsingException, IOException {
    	Document doc = XmlUtils.parse(xml);
    	Element element = doc.getRootElement();
    	init(element);
    	audienceRestrictionCondition = null;
    }
    
    public Conditions(Calendar notBeforeCal, Calendar notOnOrAfterCal) {
    	notBefore = XSDDateTime.getDateTime(notBeforeCal);
    	notOnOrAfter = XSDDateTime.getDateTime(notOnOrAfterCal);
    	audienceRestrictionCondition = null;
    }
    
    public boolean validate(Calendar when)
    {
    	Calendar now = null;
    	if (when == null) {
    		now = XSDDateTime.parse(new XSDDateTime().getDateTime());
    	} else {
    		now = when;
    	}
    	
    	Calendar startValidityPeriod = XSDDateTime.parse(notBefore);
    	Calendar endValidityPeriod = XSDDateTime.parse(notOnOrAfter);
    	
    	boolean before = startValidityPeriod.before(now);
    	boolean on = endValidityPeriod.equals(now);
    	boolean after = endValidityPeriod.after(now);
    	boolean onOrAfter = on || after;
    	return before && onOrAfter;
    }
    
    /**
     * @param beforeNow negativ value in minutes before now
     * @param mins		positiv value in minutes after now
     */
    public Conditions(int nowMinus, int nowPlus) {

        XSDDateTime now = new XSDDateTime(nowMinus);
        notBefore = now.getDateTime();

        XSDDateTime andLater = new XSDDateTime(nowPlus);
        notOnOrAfter = andLater.getDateTime();

        audienceRestrictionCondition = null;
    }

    public void setAudienceRestrictionCondition(AudienceRestrictionCondition audienceRestrictionCondition) {
    	this.audienceRestrictionCondition = audienceRestrictionCondition;
    }
    
    public AudienceRestrictionCondition getAudienceRestrictionCondition(AudienceRestrictionCondition audienceRestrictionCondition) {
    	return this.audienceRestrictionCondition;
    }
    
    public Calendar getNotBefore() {
    	return XSDDateTime.parse(notBefore);
    }
    
    public Calendar getNotOnOrAfter() {
    	return XSDDateTime.parse(notOnOrAfter);
    }
    
    private Element getConditions() throws SerializationException {
        Element conditions = new Element(WSConstants.SAML_PREFIX + ":Conditions", WSConstants.SAML11_NAMESPACE);
        Attribute notBeforeAttr = new Attribute("NotBefore", notBefore);
        Attribute notOnOrAfterAttr = new Attribute("NotOnOrAfter", notOnOrAfter);
        conditions.addAttribute(notBeforeAttr);
        conditions.addAttribute(notOnOrAfterAttr);
        if (audienceRestrictionCondition != null) {
        	conditions.appendChild(audienceRestrictionCondition.serialize());
        }
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
