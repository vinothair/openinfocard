package org.xmldap.infocard;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import junit.framework.TestCase;

import org.xmldap.crypto.CryptoUtils;
import org.xmldap.exceptions.SerializationException;
import org.xmldap.util.Base64;

/**
 * XSDDateTime Tester.
 *
 * @author <Authors name>
 * @since <pre>03/18/2006</pre>
 * @version 1.0
 */
public class TestTokenServiceReference extends TestCase {

	public void setUp() throws Exception {
		super.setUp();
	}

	public void testUserNamePasswordAuthenticate() throws Exception {
        X509Certificate cert = null;
        try {
            cert = org.xmldap.util.XmldapCertsAndKeys.getXmldapCert();
        } catch (CertificateException e) {
            e.printStackTrace();
            assertFalse(e.getMessage(), true);
        }
        
        String tsURL = "https://xmldap.org/sts/tokenservice"; 
        String mexURL = "https://xmldap.org/sts/mex";
        String userName = "cmort";
        TokenServiceReference tsr = new TokenServiceReference(tsURL, mexURL, cert);
        tsr.setAuthType(UserCredential.USERNAME, userName);
        
        String actual = null;
        String expected = "<ic:TokenServiceList xmlns:ic=\"http://schemas.xmlsoap.org/ws/2005/05/identity\">" + 
        		"<ic:TokenService><wsa:EndpointReference xmlns:wsa=\"http://www.w3.org/2005/08/addressing\">" + 
            	"<wsa:Address>" + tsURL + "</wsa:Address><wsa:Metadata>" + 
        		"<mex:Metadata xmlns:mex=\"http://schemas.xmlsoap.org/ws/2004/09/mex\"><mex:MetadataSection>" + 
            	"<mex:MetadataReference><wsa:Address>" + mexURL + "</wsa:Address>" + 
        		"</mex:MetadataReference></mex:MetadataSection></mex:Metadata></wsa:Metadata>" + 
        		"<wsid:Identity xmlns:wsid=\"http://schemas.xmlsoap.org/ws/2006/02/addressingidentity\">" + 
            	"<ds:KeyInfo xmlns:ds=\"http://www.w3.org/2000/09/xmldsig#\"><ds:X509Data>" + 
                "<ds:X509Certificate>MIIDXTCCAkUCBEQd+4EwDQYJKoZIhvcNAQEEBQAwczELMAkGA1UEBhMCVVMxEzA" + 
        		"RBgNVBAgTCkNhbGlmb3JuaWExFjAUBgNVBAcTDVNhbiBGcmFuY2lzY28xDzANBgNVBAoTBnhtbGRhcDERMA8GA1UECx" + 
        		"MIaW5mb2NhcmQxEzARBgNVBAMTCnhtbGRhcC5vcmcwHhcNMDYwMzIwMDA0NjU3WhcNMDYwNjE4MDA0NjU3WjBzMQswC" + 
        		"QYDVQQGEwJVUzETMBEGA1UECBMKQ2FsaWZvcm5pYTEWMBQGA1UEBxMNU2FuIEZyYW5jaXNjbzEPMA0GA1UEChMGeG1s" + 
        		"ZGFwMREwDwYDVQQLEwhpbmZvY2FyZDETMBEGA1UEAxMKeG1sZGFwLm9yZzCCASIwDQYJKoZIhvcNAQEBBQADggEPADC" + 
        		"CAQoCggEBANMnkVA4xfpG0bLos9FOpNBjHAdFahy2cJ7FUwuXd/IShnG+5qF/z1SdPWzRxTtpFFyodtXlBUEIbiT+Ib" + 
        		"YPZF1vCcBrcFa8Kz/4rBjrpPZgllgA/WSVKjnJvw8q4/tO6CQZSlRlj/ebNK9VyT1kN+MrKV1SGTqaIJ2l+7Rd05WHs" + 
        		"cwZMPdVWBbRrg76YTfy6H/NlQIArNLZanPvE0Vd5QfD4ZyG2hTh3y7ZlJAUndGJ/kfZw8sKuL9QSrh4eOTc280NQUmP" + 
        		"Gz6LP5MXNmu0RxEcomod1+ToKll90yEKFAUKuPYFgm9J+vYm4tzRequLy/njteRIkcfAdcAtt6PCYjUCAwEAATANBgk" + 
        		"qhkiG9w0BAQQFAAOCAQEAURtxiA7qDSq/WlUpWpfWiZ7HvveQrwTaTwV/Fk3l/I9e9WIRN51uFLuiLtZMMwR02BX7Yv" + 
        		"a1KQ/Gl999cm/0b5hptJ+TU29rVPZIlI32c5vjcuSVoEda8+BRj547jlC0rNokyWm+YtBcDOwfHSPFFwVPPVxyQsVEe" + 
        		"bsiB6KazFq6iZ8A0F2HLEnpsdFnGrSwBBbH3I3PH65ofrTTgj1Mjk5kA6EVaeefDCtlkX2ogIFMlcS6ruihX2mlCLUS" + 
        		"rlPs9TH+M4j/R/LV5QWJ93/X9gsxFrxVFGg3b75EKQP8MZ111/jaeKd80mUOAiTO06EtfjXZPrjPN4e2l05i2EGDUA==" + 
        		"</ds:X509Certificate></ds:X509Data></ds:KeyInfo></wsid:Identity></wsa:EndpointReference>" + 
        		"<ic:UserCredential><ic:DisplayCredentialHint>Enter your username and password" + 
        		"</ic:DisplayCredentialHint><ic:UsernamePasswordCredential><ic:Username>" +
        		userName + "</ic:Username>" + 
        		"</ic:UsernamePasswordCredential></ic:UserCredential></ic:TokenService></ic:TokenServiceList>";
        try {
            actual = tsr.toXML();
        } catch (SerializationException e) {
            e.printStackTrace();
            assertFalse(e.getMessage(), true);
        }
        
        assertEquals(expected, actual);
    }
	
	public void testX509V3Authenticate() throws Exception {
        X509Certificate cert = null;
        try {
            cert = org.xmldap.util.XmldapCertsAndKeys.getXmldapCert();
        } catch (CertificateException e) {
            e.printStackTrace();
            assertFalse(e.getMessage(), true);
        }
        if (cert == null) {
        	throw new Exception("oops");
        }
        
        String tsURL = "https://xmldap.org/sts/tokenservice"; 
        String mexURL = "https://xmldap.org/sts/mex";
        X509Certificate userCert = org.xmldap.util.XmldapCertsAndKeys.getXmldapCert1();
        String userCertHash = CryptoUtils.digest(userCert.getEncoded());
        TokenServiceReference tsr = new TokenServiceReference(tsURL, mexURL, cert);
        tsr.setAuthType(UserCredential.X509, userCertHash);
        
        String actual = null;
        String expected = "<ic:TokenServiceList xmlns:ic=\"http://schemas.xmlsoap.org/ws/2005/05/identity\">" +
        		"<ic:TokenService><wsa:EndpointReference xmlns:wsa=\"http://www.w3.org/2005/08/addressing\">" +
        		"<wsa:Address>" + tsURL + "</wsa:Address><wsa:Metadata>" +
        		"<mex:Metadata xmlns:mex=\"http://schemas.xmlsoap.org/ws/2004/09/mex\">" +
        		"<mex:MetadataSection><mex:MetadataReference>" +
        		"<wsa:Address>" + mexURL + "</wsa:Address>" + 
        		"</mex:MetadataReference></mex:MetadataSection></mex:Metadata></wsa:Metadata>" +
        		"<wsid:Identity xmlns:wsid=\"http://schemas.xmlsoap.org/ws/2006/02/addressingidentity\">" +
        		"<ds:KeyInfo xmlns:ds=\"http://www.w3.org/2000/09/xmldsig#\">" +
        		"<ds:X509Data>" + 
        		"<ds:X509Certificate>" + Base64.encodeBytesNoBreaks(cert.getEncoded()) + "</ds:X509Certificate>" +
        		"</ds:X509Data></ds:KeyInfo></wsid:Identity></wsa:EndpointReference>" +
        		"<ic:UserCredential><ic:DisplayCredentialHint>Choose a certificate</ic:DisplayCredentialHint>" +
        		"<ic:X509V3Credential>" +
        		"<ds:X509Data xmlns:ds=\"http://www.w3.org/2000/09/xmldsig#\">" +
        		"<wsse:KeyIdentifier xmlns:wsse=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd\" " +
        		"ValueType=\"http://docs.oasis-open.org/wss/2004/xx/oasis-2004xx-wss-soap-message-security-1.1#ThumbprintSHA1\" " +
        		"EncodingType=\"http://docs.oasis-open.org/wss/2004/xx/oasis-2004xx-wss-soap-message-security-1.1#Base64Binary\">" +
        		userCertHash +
        		"</wsse:KeyIdentifier>" +
        		"</ds:X509Data></ic:X509V3Credential></ic:UserCredential></ic:TokenService></ic:TokenServiceList>";
        try {
            actual = tsr.toXML();
        } catch (SerializationException e) {
            e.printStackTrace();
            assertFalse(e.getMessage(), true);
        }
        
        assertEquals(expected, actual);
    }
}
