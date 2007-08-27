/*
 * Copyright (c) 2006, Axel Nennker - http://axel.nennker.de/
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

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import org.xmldap.exceptions.SerializationException;
import org.xmldap.xmldsig.AsymmetricKeyInfo;

import junit.framework.TestCase;

public class AttributeStatementTest extends TestCase {

	X509Certificate cert = null;
	
	public void setUp() {
		try {
			cert = org.xmldap.util.XmldapCertsAndKeys.getXmldapCert();
		} catch (CertificateException e) {
			assertTrue(false);
		}
	}

	public void testToXML() {
        
        AsymmetricKeyInfo keyInfo = null;
		keyInfo = new AsymmetricKeyInfo(cert);

		Subject subject = new Subject(keyInfo, Subject.HOLDER_OF_KEY);
        Attribute given = new Attribute("givenname", "http://schemas.microsoft.com/ws/2005/05/identity/claims/GivenName", "Chuck");
        Attribute sur = new Attribute("surname", "http://schemas.microsoft.com/ws/2005/05/identity/claims/SurName", "Mortimore");
        Attribute email = new Attribute("givenname", "http://schemas.microsoft.com/ws/2005/05/identity/claims/EmailAddress", "cmortspam@gmail.com");

        AttributeStatement statement = new AttributeStatement();
        statement.setSubject(subject);
        statement.addAttribute(given);
        statement.addAttribute(sur);
        statement.addAttribute(email);

        try {
			assertEquals( "<saml:AttributeStatement xmlns:saml=\"urn:oasis:names:tc:SAML:1.0:assertion\">" +
					"<saml:Subject><saml:SubjectConfirmation>" +
					"<saml:ConfirmationMethod>urn:oasis:names:tc:SAML:1.0:cm:holder-of-key</saml:ConfirmationMethod>" +
					"<dsig:KeyInfo xmlns:dsig=\"http://www.w3.org/2000/09/xmldsig#\">" +
					"<dsig:KeyName>Public Key for CN=xmldap.org, OU=infocard, O=xmldap, L=San Francisco, ST=California, C=US</dsig:KeyName>" +
					"<dsig:KeyValue><dsig:RSAKeyValue><dsig:Modulus>ANMnkVA4xfpG0bLos9FOpNBjHAdFahy2cJ7FUwuXd/IShnG+5qF/z1SdPWzRxTtpFFyodtX" +
					"lBUEIbiT+IbYPZF1vCcBrcFa8Kz/4rBjrpPZgllgA/WSVKjnJvw8q4/tO6CQZSlRlj/ebNK9VyT1kN+MrKV1SGTqaIJ2l+7Rd05WHscwZMPdVWBbRrg76Y" +
					"Tfy6H/NlQIArNLZanPvE0Vd5QfD4ZyG2hTh3y7ZlJAUndGJ/kfZw8sKuL9QSrh4eOTc280NQUmPGz6LP5MXNmu0RxEcomod1+ToKll90yEKFAUKuPYFgm9" +
					"J+vYm4tzRequLy/njteRIkcfAdcAtt6PCYjU=</dsig:Modulus>" +
					"<dsig:Exponent>AQAB</dsig:Exponent></dsig:RSAKeyValue></dsig:KeyValue>" +
					"<dsig:X509Data><dsig:X509Certificate>MIIDXTCCAkUCBEQd+4EwDQYJKoZIhvcNAQEEBQAwczELMAkGA1UEBhMCVVMxEzARBgNVBAgTCkNhbGlmb" +
					"3JuaWExFjAUBgNVBAcTDVNhbiBGcmFuY2lzY28xDzANBgNVBAoTBnhtbGRhcDERMA8GA1UECxMIaW5mb2NhcmQxEzARBgNVBAMTCnhtbGRhcC5vcmcwHhc" +
					"NMDYwMzIwMDA0NjU3WhcNMDYwNjE4MDA0NjU3WjBzMQswCQYDVQQGEwJVUzETMBEGA1UECBMKQ2FsaWZvcm5pYTEWMBQGA1UEBxMNU2FuIEZyYW5jaXNjb" +
					"zEPMA0GA1UEChMGeG1sZGFwMREwDwYDVQQLEwhpbmZvY2FyZDETMBEGA1UEAxMKeG1sZGFwLm9yZzCCASIwDQYJKoZIhvcNAQEBBQADggEPADCCAQoCggE" +
					"BANMnkVA4xfpG0bLos9FOpNBjHAdFahy2cJ7FUwuXd/IShnG+5qF/z1SdPWzRxTtpFFyodtXlBUEIbiT+IbYPZF1vCcBrcFa8Kz/4rBjrpPZgllgA/WSVK" +
					"jnJvw8q4/tO6CQZSlRlj/ebNK9VyT1kN+MrKV1SGTqaIJ2l+7Rd05WHscwZMPdVWBbRrg76YTfy6H/NlQIArNLZanPvE0Vd5QfD4ZyG2hTh3y7ZlJAUndG" +
					"J/kfZw8sKuL9QSrh4eOTc280NQUmPGz6LP5MXNmu0RxEcomod1+ToKll90yEKFAUKuPYFgm9J+vYm4tzRequLy/njteRIkcfAdcAtt6PCYjUCAwEAATANB" +
					"gkqhkiG9w0BAQQFAAOCAQEAURtxiA7qDSq/WlUpWpfWiZ7HvveQrwTaTwV/Fk3l/I9e9WIRN51uFLuiLtZMMwR02BX7Yva1KQ/Gl999cm/0b5hptJ+TU29" +
					"rVPZIlI32c5vjcuSVoEda8+BRj547jlC0rNokyWm+YtBcDOwfHSPFFwVPPVxyQsVEebsiB6KazFq6iZ8A0F2HLEnpsdFnGrSwBBbH3I3PH65ofrTTgj1Mj" +
					"k5kA6EVaeefDCtlkX2ogIFMlcS6ruihX2mlCLUSrlPs9TH+M4j/R/LV5QWJ93/X9gsxFrxVFGg3b75EKQP8MZ111/jaeKd80mUOAiTO06EtfjXZPrjPN4e" +
					"2l05i2EGDUA==</dsig:X509Certificate></dsig:X509Data></dsig:KeyInfo></saml:SubjectConfirmation></saml:Subject>" +
					"<saml:Attribute " +
					"AttributeName=\"givenname\" " +
					"AttributeNamespace=\"http://schemas.microsoft.com/ws/2005/05/identity/claims/GivenName\">" +
					"<saml:AttributeValue>Chuck</saml:AttributeValue>" +
					"</saml:Attribute>" +
					"<saml:Attribute AttributeName=\"surname\" " +
					"AttributeNamespace=\"http://schemas.microsoft.com/ws/2005/05/identity/claims/SurName\">" +
					"<saml:AttributeValue>Mortimore</saml:AttributeValue></saml:Attribute>" +
					"<saml:Attribute AttributeName=\"givenname\" " +
					"AttributeNamespace=\"http://schemas.microsoft.com/ws/2005/05/identity/claims/EmailAddress\">" +
					"<saml:AttributeValue>cmortspam@gmail.com</saml:AttributeValue></saml:Attribute>" +
					"</saml:AttributeStatement>", statement.toXML());
		} catch (SerializationException e) {
			assertTrue(false);
		}
	}

}
