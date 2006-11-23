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

package org.xmldap.infocard;

import nu.xom.Element;
import org.xmldap.exceptions.SerializationException;
import org.xmldap.exceptions.SigningException;
import org.xmldap.saml.*;
import org.xmldap.util.RandomGUID;
import org.xmldap.xml.Serializable;
import org.xmldap.xmldsig.AsymmetricKeyInfo;
import org.xmldap.xmldsig.EnvelopedSignature;

import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.Iterator;
import java.util.Vector;


public class ManagedToken implements Serializable {

    public static final String MS_NAMESPACE_PREFIX = "http://schemas.microsoft.com/ws/2005/05/identity/claims/";
	public static final String XS_NAMESPACE_PREFIX = "http://schemas.xmlsoap.org/ws/2005/05/identity/claims/";

	private String namespacePrefix = null;

    private String givenName;
    private String surname;
    private String emailAddress;
    private String streetAddress;
    private String locality;
    private String stateOrProvince;
    private String postalCode;
    private String country;
    private String primaryPhone;
    private String otherPhone;
    private String mobilePhone;
    private String dateOfBirth;
    private String privatePersonalIdentifier;
    private String gender;

    private X509Certificate signingCert;
    private PrivateKey signingKey;


    private int nowPlus = 10; //default to 10 minutes
    private int nowMinus = 10; //default to 5 minutes

    public ManagedToken( X509Certificate signingCert, PrivateKey signingKey ) {
        this.signingCert = signingCert;
        this.signingKey = signingKey;
        namespacePrefix = XS_NAMESPACE_PREFIX; // default is the new (Autumn 2006) namespace
    }


    public void setNamespacePrefix(String namespacePrefix) {
        this.namespacePrefix = namespacePrefix;
    }

    public String getNamespacePrefix() {
    	return namespacePrefix;
    }

//    public int getValidityPeriod() {
//        return nowPlus;
//    }

    public void setValidityPeriod(int nowMinus, int nowPlus) {
        this.nowMinus = nowMinus;
        this.nowPlus = nowPlus;
    }


    public String getGivenName() {
        return givenName;
    }

    public void setGivenName(String givenName) {
        this.givenName = givenName;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getStreetAddress() {
        return streetAddress;
    }

    public void setStreetAddress(String streetAddress) {
        this.streetAddress = streetAddress;
    }

    public String getLocality() {
        return locality;
    }

    public void setLocality(String locality) {
        this.locality = locality;
    }

    public String getStateOrProvince() {
        return stateOrProvince;
    }

    public void setStateOrProvince(String stateOrProvince) {
        this.stateOrProvince = stateOrProvince;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getPrimaryPhone() {
        return primaryPhone;
    }

    public void setPrimaryPhone(String primaryPhone) {
        this.primaryPhone = primaryPhone;
    }

    public String getOtherPhone() {
        return otherPhone;
    }

    public void setOtherPhone(String otherPhone) {
        this.otherPhone = otherPhone;
    }

    public String getMobilePhone() {
        return mobilePhone;
    }

    public void setMobilePhone(String mobilePhone) {
        this.mobilePhone = mobilePhone;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getPrivatePersonalIdentifier() {
        return privatePersonalIdentifier;
    }

    public void setPrivatePersonalIdentifier(String privatePersonalIdentifier) {
        this.privatePersonalIdentifier = privatePersonalIdentifier;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }



    public Element getToken(RandomGUID uuid) throws SerializationException {

        Conditions conditions = new Conditions(nowMinus, nowPlus);


        //SimpleKeyInfo keyInfo = new SimpleKeyInfo(signingCert);
        AsymmetricKeyInfo keyInfo = new AsymmetricKeyInfo(signingCert);

        Subject subject = new Subject(keyInfo);

        Vector attributes = new Vector();

        if (givenName != null) {

            Attribute attr = new Attribute("givenname", namespacePrefix+"givenname", givenName);
            attributes.add(attr);
        }

        if (surname != null) {

            Attribute attr = new Attribute("surname", namespacePrefix+"surname", surname);
            attributes.add(attr);
        }

        if (emailAddress != null) {

            Attribute attr = new Attribute("emailaddress", namespacePrefix+"emailaddress", emailAddress);
            attributes.add(attr);
        }

        if (streetAddress != null) {

            Attribute attr = new Attribute("streetaddress", namespacePrefix+"streetaddress", streetAddress);
            attributes.add(attr);
        }

        if (locality != null) {

            Attribute attr = new Attribute("locality", namespacePrefix+locality, locality);
            attributes.add(attr);
        }

        if (stateOrProvince != null) {

            Attribute attr = new Attribute("stateorprovince", namespacePrefix+"stateorprovince", stateOrProvince);
            attributes.add(attr);
        }

        if (postalCode != null) {

            Attribute attr = new Attribute("postalcode", namespacePrefix+"postalcode", postalCode);
            attributes.add(attr);
        }

        if (country != null) {

            Attribute attr = new Attribute("country", namespacePrefix+"country", country);
            attributes.add(attr);
        }

        if (primaryPhone != null) {

            Attribute attr = new Attribute("primaryphone", namespacePrefix+"primaryphone", primaryPhone);
            attributes.add(attr);
        }

        if (otherPhone != null) {

            Attribute attr = new Attribute("otherphone", namespacePrefix+"otherphone", otherPhone);
            attributes.add(attr);
        }

        if (mobilePhone != null) {

            Attribute attr = new Attribute("mobilephone", namespacePrefix+"mobilephone", mobilePhone);
            attributes.add(attr);
        }

        if (dateOfBirth != null) {

            Attribute attr = new Attribute("dateofbirth", namespacePrefix+"dateofbirth", dateOfBirth);
            attributes.add(attr);
        }

        if (privatePersonalIdentifier != null) {

            Attribute attr = new Attribute("privatepersonalidentifier", namespacePrefix+"privatepersonalidentifier", privatePersonalIdentifier);
            attributes.add(attr);
        }

        if (gender != null) {

            Attribute attr = new Attribute("gender", namespacePrefix+"gender", gender);
            attributes.add(attr);
        }


        AttributeStatement statement = new AttributeStatement();
        statement.setSubject(subject);

        Iterator iter = attributes.iterator();
        while (iter.hasNext()) {

            statement.addAttribute((Attribute) iter.next());

        }

        SAMLAssertion assertion = new SAMLAssertion(uuid);
        assertion.setIssuer("https://xmldap.org/sts/tokenservice");
        assertion.setConditions(conditions);
        assertion.setAttributeStatement(statement);

        //make this support multiple signing modes
        EnvelopedSignature signer = new EnvelopedSignature(signingCert, signingKey);

        Element signedXML = null;
        try {
            signedXML = signer.sign(assertion.serialize());
        } catch (SigningException e) {
            throw new SerializationException("Error signing assertion", e);
        }

        return signedXML;


    }

    public String toXML() throws SerializationException {

        Element sit = serialize();
        return sit.toXML();

    }

    public Element serialize() throws SerializationException {
        //TODO - clean up hack
        return null;


    }

    public Element serialize(RandomGUID uuid) throws SerializationException {
        return getToken(uuid);
    }



}
