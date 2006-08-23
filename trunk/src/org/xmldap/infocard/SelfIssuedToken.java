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
 *     * Neither the name of the University of California, Berkeley nor the
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
import org.xmldap.crypto.CryptoUtils;
import org.xmldap.exceptions.KeyStoreException;
import org.xmldap.exceptions.SerializationException;
import org.xmldap.exceptions.SigningException;
import org.xmldap.saml.*;
import org.xmldap.util.KeystoreUtil;
import org.xmldap.xml.Serializable;
import org.xmldap.xmldsig.AysmmetricKeyInfo;
import org.xmldap.xmldsig.EnvelopedSignature;
import org.xmldap.xmldsig.KeyInfo;
import org.xmldap.xmldsig.SymmetricKeyInfo;

import java.util.Iterator;
import java.util.Vector;
import java.security.cert.X509Certificate;

/**
 * SelfIssuedToken allows you to create Self issued tokens for passing to an RP
 */
public class SelfIssuedToken implements Serializable {

    private static final String GivenName_NAMESPACE = "http://schemas.microsoft.com/ws/2005/05/identity/claims/givenname";
    private static final String Surname_NAMESPACE = "http://schemas.microsoft.com/ws/2005/05/identity/claims/surname";
    private static final String EmailAddress_NAMESPACE = "http://schemas.microsoft.com/ws/2005/05/identity/claims/emailaddress";
    private static final String StreetAddress_NAMESPACE = "http://schemas.microsoft.com/ws/2005/05/identity/claims/streetaddress";
    private static final String Locality_NAMESPACE = "http://schemas.microsoft.com/ws/2005/05/identity/claims/locality";
    private static final String StateOrProvince_NAMESPACE = "http://schemas.microsoft.com/ws/2005/05/identity/claims/stateorprovince";
    private static final String PostalCode_NAMESPACE = "http://schemas.microsoft.com/ws/2005/05/identity/claims/postalcode";
    private static final String Country_NAMESPACE = "http://schemas.microsoft.com/ws/2005/05/identity/claims/country";
    private static final String PrimaryPhone_NAMESPACE = "http://schemas.microsoft.com/ws/2005/05/identity/claims/homephone";
    private static final String OtherPhone_NAMESPACE = "http://schemas.microsoft.com/ws/2005/05/identity/claims/otherphone";
    private static final String MobilePhone_NAMESPACE = "http://schemas.microsoft.com/ws/2005/05/identity/claims/mobilephone";
    private static final String DateOfBirth_NAMESPACE = "http://schemas.microsoft.com/ws/2005/05/identity/claims/dateofbirth";
    private static final String PrivatePersonalIdentifier_NAMESPACE = "http://schemas.microsoft.com/ws/2005/05/identity/claims/privatepersonalidentifier";
    private static final String Gender_NAMESPACE = "http://schemas.microsoft.com/ws/2005/05/identity/claims/gender";


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

    private KeystoreUtil keystore;
    private String signingAlias;
    private String signingAliasPassword;
    private String relyingPartyAlias;
    private X509Certificate relyingPartyCert;


    private int validityPeriod = 10; //default to 10 minutes
    private boolean asymmetric = false; //default to symmetric key

    public SelfIssuedToken(KeystoreUtil keystore, X509Certificate relyingPartyCert, String signingAlias, String signingAliasPassword) {
        this.keystore = keystore;
        this.relyingPartyCert = relyingPartyCert;
        this.signingAlias = signingAlias;
        this.signingAliasPassword = signingAliasPassword;
    }

    public SelfIssuedToken(KeystoreUtil keystore, String relyingPartyAlias, String signingAlias, String signingAliasPassword) {
        this.keystore = keystore;
        this.relyingPartyAlias = relyingPartyAlias;
        this.signingAlias = signingAlias;
        this.signingAliasPassword = signingAliasPassword;
    }

    public int getValidityPeriod() {
        return validityPeriod;
    }

    public void setValidityPeriod(int validityPeriod) {
        this.validityPeriod = validityPeriod;
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

    public void useAsymmetricKey(String signingAlias, String signingAliasPassword) {

        this.asymmetric = true;
        this.signingAliasPassword = signingAliasPassword;
        this.signingAlias = signingAlias;

    }


    public Element getSelfIssuedToken() throws SerializationException {

        Conditions conditions = new Conditions(validityPeriod);
        KeyInfo keyInfo = null;
        if (asymmetric) {

            keyInfo = new AysmmetricKeyInfo(keystore, signingAlias);

        } else {
            byte[] secretKey;
            try {
                secretKey = CryptoUtils.genKey(128);
            } catch (org.xmldap.exceptions.CryptoException e) {
                throw new SerializationException(e);
            }
            //System.out.println(keystore + ":" + relyingPartyAlias + ":" + secretKey);

            if (relyingPartyCert != null) {
                keyInfo = new SymmetricKeyInfo(relyingPartyCert, secretKey);
            } else {
                keyInfo = new SymmetricKeyInfo(keystore, relyingPartyAlias, secretKey);
            }



        }

        Subject subject = new Subject(keyInfo);

        Vector attributes = new Vector();

        if (givenName != null) {

            Attribute attr = new Attribute("givenname", GivenName_NAMESPACE, givenName);
            attributes.add(attr);
        }

        if (surname != null) {

            Attribute attr = new Attribute("surname", Surname_NAMESPACE, surname);
            attributes.add(attr);
        }

        if (emailAddress != null) {

            Attribute attr = new Attribute("emailaddress", EmailAddress_NAMESPACE, emailAddress);
            attributes.add(attr);
        }

        if (streetAddress != null) {

            Attribute attr = new Attribute("streetaddress", StreetAddress_NAMESPACE, streetAddress);
            attributes.add(attr);
        }

        if (locality != null) {

            Attribute attr = new Attribute("locality", Locality_NAMESPACE, locality);
            attributes.add(attr);
        }

        if (stateOrProvince != null) {

            Attribute attr = new Attribute("stateorprovince", StateOrProvince_NAMESPACE, stateOrProvince);
            attributes.add(attr);
        }

        if (postalCode != null) {

            Attribute attr = new Attribute("postalcode", PostalCode_NAMESPACE, postalCode);
            attributes.add(attr);
        }

        if (country != null) {

            Attribute attr = new Attribute("country", Country_NAMESPACE, country);
            attributes.add(attr);
        }

        if (primaryPhone != null) {

            Attribute attr = new Attribute("primaryphone", PrimaryPhone_NAMESPACE, primaryPhone);
            attributes.add(attr);
        }

        if (otherPhone != null) {

            Attribute attr = new Attribute("otherphone", OtherPhone_NAMESPACE, otherPhone);
            attributes.add(attr);
        }

        if (mobilePhone != null) {

            Attribute attr = new Attribute("mobilephone", MobilePhone_NAMESPACE, mobilePhone);
            attributes.add(attr);
        }

        if (dateOfBirth != null) {

            Attribute attr = new Attribute("dateofbirth", DateOfBirth_NAMESPACE, dateOfBirth);
            attributes.add(attr);
        }

        if (privatePersonalIdentifier != null) {

            Attribute attr = new Attribute("privatepersonalidentifier", PrivatePersonalIdentifier_NAMESPACE, privatePersonalIdentifier);
            attributes.add(attr);
        }

        if (gender != null) {

            Attribute attr = new Attribute("gender", Gender_NAMESPACE, gender);
            attributes.add(attr);
        }


        AttributeStatement statement = new AttributeStatement();
        statement.setSubject(subject);

        Iterator iter = attributes.iterator();
        while (iter.hasNext()) {

            statement.addAttribute((Attribute) iter.next());

        }

        SAMLAssertion assertion = new SAMLAssertion();
        assertion.setConditions(conditions);
        assertion.setAttributeStatement(statement);

        //make this support multiple signing modes
        EnvelopedSignature signer = new EnvelopedSignature(keystore, signingAlias, signingAliasPassword);

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
        return getSelfIssuedToken();
    }


    public static void main(String[] args) {

        KeystoreUtil keystore = null;
        try {
            keystore = new KeystoreUtil("/Users/cmort/build/infocard/conf/xmldap.jks", "storepassword");
        } catch (KeyStoreException e) {
            e.printStackTrace();
        }

        SelfIssuedToken token = new SelfIssuedToken(keystore, "identityblog", "xmldap", "keypassword");
        token.useAsymmetricKey("xmldap", "keypassword");
        token.setGivenName("Chuck");
        token.setSurname("Mortimore");
        token.setEmailAddress("cmortspam@gmail.com");
        token.setValidityPeriod(20);
        Element securityToken = null;
        try {
            securityToken = token.serialize();
        } catch (SerializationException e) {
            e.printStackTrace();
        }
        System.out.println(securityToken.toXML());


    }


}
