package org.xmldap.infocard;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import junit.framework.TestCase;
import org.xmldap.exceptions.SerializationException;

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
    
    public void testPad() throws Exception {
        X509Certificate cert = null;
        try {
            cert = org.xmldap.util.XmldapCertsAndKeys.getXmldapCert();
        } catch (CertificateException e) {
            e.printStackTrace();
            assertFalse(e.getMessage(), true);
        }
        
        TokenServiceReference tsr = new TokenServiceReference("http://test", cert);
        tsr.setUserName("cmort");
        
        String actual = null;
        String expected = "<ic:TokenServiceList xmlns:ic=\"http://schemas.microsoft.com/ws/2005/05/identity\"><ic:TokenService><wsa:EndpointReference xmlns:wsa=\"http://schemas.xmlsoap.org/ws/2004/08/addressing\"><wsa:Address>http://test</wsa:Address><wsid:Identity xmlns:wsid=\"http://schemas.microsoft.com/windows/wcf/2005/09/addressingidentityextension\"><ds:KeyInfo xmlns:ds=\"http://www.w3.org/2000/09/xmldsig#\"><ds:KeyName>Public Key for CN=xmldap.org, OU=infocard, O=xmldap, L=San Francisco, ST=California, C=US</ds:KeyName><ds:KeyValue><ds:RSAKeyValue><ds:Modulus>ANMnkVA4xfpG0bLos9FOpNBjHAdFahy2cJ7FUwuXd/IShnG+5qF/z1SdPWzRxTtpFFyodtXlBUEIbiT+IbYPZF1vCcBrcFa8Kz/4rBjrpPZgllgA/WSVKjnJvw8q4/tO6CQZSlRlj/ebNK9VyT1kN+MrKV1SGTqaIJ2l+7Rd05WHscwZMPdVWBbRrg76YTfy6H/NlQIArNLZanPvE0Vd5QfD4ZyG2hTh3y7ZlJAUndGJ/kfZw8sKuL9QSrh4eOTc280NQUmPGz6LP5MXNmu0RxEcomod1+ToKll90yEKFAUKuPYFgm9J+vYm4tzRequLy/njteRIkcfAdcAtt6PCYjU=</ds:Modulus><ds:Exponent>AQAB</ds:Exponent></ds:RSAKeyValue></ds:KeyValue><ds:X509Data><ds:X509Certificate>MIIDXTCCAkUCBEQd+4EwDQYJKoZIhvcNAQEEBQAwczELMAkGA1UEBhMCVVMxEzARBgNVBAgTCkNhbGlmb3JuaWExFjAUBgNVBAcTDVNhbiBGcmFuY2lzY28xDzANBgNVBAoTBnhtbGRhcDERMA8GA1UECxMIaW5mb2NhcmQxEzARBgNVBAMTCnhtbGRhcC5vcmcwHhcNMDYwMzIwMDA0NjU3WhcNMDYwNjE4MDA0NjU3WjBzMQswCQYDVQQGEwJVUzETMBEGA1UECBMKQ2FsaWZvcm5pYTEWMBQGA1UEBxMNU2FuIEZyYW5jaXNjbzEPMA0GA1UEChMGeG1sZGFwMREwDwYDVQQLEwhpbmZvY2FyZDETMBEGA1UEAxMKeG1sZGFwLm9yZzCCASIwDQYJKoZIhvcNAQEBBQADggEPADCCAQoCggEBANMnkVA4xfpG0bLos9FOpNBjHAdFahy2cJ7FUwuXd/IShnG+5qF/z1SdPWzRxTtpFFyodtXlBUEIbiT+IbYPZF1vCcBrcFa8Kz/4rBjrpPZgllgA/WSVKjnJvw8q4/tO6CQZSlRlj/ebNK9VyT1kN+MrKV1SGTqaIJ2l+7Rd05WHscwZMPdVWBbRrg76YTfy6H/NlQIArNLZanPvE0Vd5QfD4ZyG2hTh3y7ZlJAUndGJ/kfZw8sKuL9QSrh4eOTc280NQUmPGz6LP5MXNmu0RxEcomod1+ToKll90yEKFAUKuPYFgm9J+vYm4tzRequLy/njteRIkcfAdcAtt6PCYjUCAwEAATANBgkqhkiG9w0BAQQFAAOCAQEAURtxiA7qDSq/WlUpWpfWiZ7HvveQrwTaTwV/Fk3l/I9e9WIRN51uFLuiLtZMMwR02BX7Yva1KQ/Gl999cm/0b5hptJ+TU29rVPZIlI32c5vjcuSVoEda8+BRj547jlC0rNokyWm+YtBcDOwfHSPFFwVPPVxyQsVEebsiB6KazFq6iZ8A0F2HLEnpsdFnGrSwBBbH3I3PH65ofrTTgj1Mjk5kA6EVaeefDCtlkX2ogIFMlcS6ruihX2mlCLUSrlPs9TH+M4j/R/LV5QWJ93/X9gsxFrxVFGg3b75EKQP8MZ111/jaeKd80mUOAiTO06EtfjXZPrjPN4e2l05i2EGDUA==</ds:X509Certificate></ds:X509Data></ds:KeyInfo></wsid:Identity></wsa:EndpointReference><ic:UserCredential><ic:DisplayCredentialHint>A credential hint</ic:DisplayCredentialHint><ic:UsernamePasswordCredential><ic:Username>cmort</ic:Username></ic:UsernamePasswordCredential></ic:UserCredential></ic:TokenService></ic:TokenServiceList>";
        try {
            actual = tsr.toXML();
        } catch (SerializationException e) {
            e.printStackTrace();
            assertFalse(e.getMessage(), true);
        }
        
        assertEquals(expected, actual);
    }
    
}
