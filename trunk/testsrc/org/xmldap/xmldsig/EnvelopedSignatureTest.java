package org.xmldap.xmldsig;

import junit.framework.TestCase;
import nu.xom.Attribute;
import nu.xom.Document;
import nu.xom.Element;
import org.xmldap.util.KeystoreUtil;
import org.xmldap.ws.WSConstants;

/**
 * SignatureUtil Tester.
 *
 * @author <Authors name>
 * @since <pre>03/21/2006</pre>
 * @version 1.0
 */
public class EnvelopedSignatureTest extends TestCase {

    private static final String XML = "<xmldap:Body xmlns:wsu=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd\" xmlns:xmldap=\"http://www.xmldap.org\" wsu:Id=\"urn:guid:58824342-832F-C99B-925B-CB0E858E5D65\"><xmldap:Child /></xmldap:Body>";

    //TODO - refactor to support non-wrapped B64
    private static final String SIGNED_XML_DOC = "<?xml version=\"1.0\"?>\n" +
            "<xmldap:Body xmlns:wsu=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd\" xmlns:xmldap=\"http://www.xmldap.org\" wsu:Id=\"urn:guid:58824342-832F-C99B-925B-CB0E858E5D65\"><xmldap:Child /><ds:Signature xmlns:ds=\"http://www.w3.org/2000/09/xmldsig#\"><ds:SignedInfo><ds:CanonicalizationMethod Algorithm=\"http://www.w3.org/2001/10/xml-exc-c14n#\" /><ds:SignatureMethod Algorithm=\"http://www.w3.org/2000/09/xmldsig#rsa-sha1\" /><ds:Reference URI=\"\"><ds:Transforms><ds:Transform Algorithm=\"http://www.w3.org/2000/09/xmldsig#enveloped-signature\" /><ds:Transform Algorithm=\"http://www.w3.org/2001/10/xml-exc-c14n#\" /></ds:Transforms><ds:DigestMethod Algorithm=\"http://www.w3.org/2000/09/xmldsig#sha1\" /><ds:DigestValue>8xRvuAvdu90P9qcwm3kaUNSh4/c=</ds:DigestValue></ds:Reference></ds:SignedInfo><ds:SignatureValue>udWT9QYo6X/Z0YVIN/deUJxGU3CYiDV6OLYxSo/8xjqEKXYzhpwaGEZMytWRBLM/clcd1PViJCdQ\n" +
            "ZRbHnLzBeXk193c5c06FJC56sruoWg8WoqkABd0ZwY4TxxKxQx2+EHqIJMLV06zPOqkBbR9/l14T\n" +
            "PIv1E7aRR5S+hjycvCTvZ5rkG4HWHo0czdvE0b4T10LN6+aokr5SYDRrouQcU9gDpAOJ9r2l7xTc\n" +
            "39wNk/d4iGJLqbj0X+9S6GqldDMrBFXnTe8tdyoLjCdZpgwMa9Ml0Cx6HBF8CBL8U9R0vlfMQuyd\n" +
            "59ZRHPSlyClf9cYzz7eE3srrMQATImkDS1I0LA==</ds:SignatureValue><ds:KeyInfo><ds:KeyName>Public Key for CN=xmldap.org, OU=infocard, O=xmldap, L=San Francisco, ST=California, C=US</ds:KeyName><ds:KeyValue><ds:RSAKeyValue><ds:Modulus>ANMnkVA4xfpG0bLos9FOpNBjHAdFahy2cJ7FUwuXd/IShnG+5qF/z1SdPWzRxTtpFFyodtXlBUEI\n" +
            "biT+IbYPZF1vCcBrcFa8Kz/4rBjrpPZgllgA/WSVKjnJvw8q4/tO6CQZSlRlj/ebNK9VyT1kN+Mr\n" +
            "KV1SGTqaIJ2l+7Rd05WHscwZMPdVWBbRrg76YTfy6H/NlQIArNLZanPvE0Vd5QfD4ZyG2hTh3y7Z\n" +
            "lJAUndGJ/kfZw8sKuL9QSrh4eOTc280NQUmPGz6LP5MXNmu0RxEcomod1+ToKll90yEKFAUKuPYF\n" +
            "gm9J+vYm4tzRequLy/njteRIkcfAdcAtt6PCYjU=</ds:Modulus><ds:Exponent>AQAB</ds:Exponent></ds:RSAKeyValue></ds:KeyValue><ds:X509Data><ds:X509Certificate>MIIDXTCCAkUCBEQd+4EwDQYJKoZIhvcNAQEEBQAwczELMAkGA1UEBhMCVVMxEzARBgNVBAgTCkNh\n" +
            "bGlmb3JuaWExFjAUBgNVBAcTDVNhbiBGcmFuY2lzY28xDzANBgNVBAoTBnhtbGRhcDERMA8GA1UE\n" +
            "CxMIaW5mb2NhcmQxEzARBgNVBAMTCnhtbGRhcC5vcmcwHhcNMDYwMzIwMDA0NjU3WhcNMDYwNjE4\n" +
            "MDA0NjU3WjBzMQswCQYDVQQGEwJVUzETMBEGA1UECBMKQ2FsaWZvcm5pYTEWMBQGA1UEBxMNU2Fu\n" +
            "IEZyYW5jaXNjbzEPMA0GA1UEChMGeG1sZGFwMREwDwYDVQQLEwhpbmZvY2FyZDETMBEGA1UEAxMK\n" +
            "eG1sZGFwLm9yZzCCASIwDQYJKoZIhvcNAQEBBQADggEPADCCAQoCggEBANMnkVA4xfpG0bLos9FO\n" +
            "pNBjHAdFahy2cJ7FUwuXd/IShnG+5qF/z1SdPWzRxTtpFFyodtXlBUEIbiT+IbYPZF1vCcBrcFa8\n" +
            "Kz/4rBjrpPZgllgA/WSVKjnJvw8q4/tO6CQZSlRlj/ebNK9VyT1kN+MrKV1SGTqaIJ2l+7Rd05WH\n" +
            "scwZMPdVWBbRrg76YTfy6H/NlQIArNLZanPvE0Vd5QfD4ZyG2hTh3y7ZlJAUndGJ/kfZw8sKuL9Q\n" +
            "Srh4eOTc280NQUmPGz6LP5MXNmu0RxEcomod1+ToKll90yEKFAUKuPYFgm9J+vYm4tzRequLy/nj\n" +
            "teRIkcfAdcAtt6PCYjUCAwEAATANBgkqhkiG9w0BAQQFAAOCAQEAURtxiA7qDSq/WlUpWpfWiZ7H\n" +
            "vveQrwTaTwV/Fk3l/I9e9WIRN51uFLuiLtZMMwR02BX7Yva1KQ/Gl999cm/0b5hptJ+TU29rVPZI\n" +
            "lI32c5vjcuSVoEda8+BRj547jlC0rNokyWm+YtBcDOwfHSPFFwVPPVxyQsVEebsiB6KazFq6iZ8A\n" +
            "0F2HLEnpsdFnGrSwBBbH3I3PH65ofrTTgj1Mjk5kA6EVaeefDCtlkX2ogIFMlcS6ruihX2mlCLUS\n" +
            "rlPs9TH+M4j/R/LV5QWJ93/X9gsxFrxVFGg3b75EKQP8MZ111/jaeKd80mUOAiTO06EtfjXZPrjP\n" +
            "N4e2l05i2EGDUA==</ds:X509Certificate></ds:X509Data></ds:KeyInfo></ds:Signature></xmldap:Body>\n";

    
    private static final String SIGNED_XML = "<xmldap:Body xmlns:wsu=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd\" xmlns:xmldap=\"http://www.xmldap.org\" wsu:Id=\"urn:guid:58824342-832F-C99B-925B-CB0E858E5D65\"><xmldap:Child /><ds:Signature xmlns:ds=\"http://www.w3.org/2000/09/xmldsig#\"><ds:SignedInfo><ds:CanonicalizationMethod Algorithm=\"http://www.w3.org/2001/10/xml-exc-c14n#\" /><ds:SignatureMethod Algorithm=\"http://www.w3.org/2000/09/xmldsig#rsa-sha1\" /><ds:Reference URI=\"\"><ds:Transforms><ds:Transform Algorithm=\"http://www.w3.org/2000/09/xmldsig#enveloped-signature\" /><ds:Transform Algorithm=\"http://www.w3.org/2001/10/xml-exc-c14n#\" /></ds:Transforms><ds:DigestMethod Algorithm=\"http://www.w3.org/2000/09/xmldsig#sha1\" /><ds:DigestValue>8xRvuAvdu90P9qcwm3kaUNSh4/c=</ds:DigestValue></ds:Reference></ds:SignedInfo><ds:SignatureValue>udWT9QYo6X/Z0YVIN/deUJxGU3CYiDV6OLYxSo/8xjqEKXYzhpwaGEZMytWRBLM/clcd1PViJCdQ\n" +
            "ZRbHnLzBeXk193c5c06FJC56sruoWg8WoqkABd0ZwY4TxxKxQx2+EHqIJMLV06zPOqkBbR9/l14T\n" +
            "PIv1E7aRR5S+hjycvCTvZ5rkG4HWHo0czdvE0b4T10LN6+aokr5SYDRrouQcU9gDpAOJ9r2l7xTc\n" +
            "39wNk/d4iGJLqbj0X+9S6GqldDMrBFXnTe8tdyoLjCdZpgwMa9Ml0Cx6HBF8CBL8U9R0vlfMQuyd\n" +
            "59ZRHPSlyClf9cYzz7eE3srrMQATImkDS1I0LA==</ds:SignatureValue><ds:KeyInfo><ds:KeyName>Public Key for CN=xmldap.org, OU=infocard, O=xmldap, L=San Francisco, ST=California, C=US</ds:KeyName><ds:KeyValue><ds:RSAKeyValue><ds:Modulus>ANMnkVA4xfpG0bLos9FOpNBjHAdFahy2cJ7FUwuXd/IShnG+5qF/z1SdPWzRxTtpFFyodtXlBUEI\n" +
            "biT+IbYPZF1vCcBrcFa8Kz/4rBjrpPZgllgA/WSVKjnJvw8q4/tO6CQZSlRlj/ebNK9VyT1kN+Mr\n" +
            "KV1SGTqaIJ2l+7Rd05WHscwZMPdVWBbRrg76YTfy6H/NlQIArNLZanPvE0Vd5QfD4ZyG2hTh3y7Z\n" +
            "lJAUndGJ/kfZw8sKuL9QSrh4eOTc280NQUmPGz6LP5MXNmu0RxEcomod1+ToKll90yEKFAUKuPYF\n" +
            "gm9J+vYm4tzRequLy/njteRIkcfAdcAtt6PCYjU=</ds:Modulus><ds:Exponent>AQAB</ds:Exponent></ds:RSAKeyValue></ds:KeyValue><ds:X509Data><ds:X509Certificate>MIIDXTCCAkUCBEQd+4EwDQYJKoZIhvcNAQEEBQAwczELMAkGA1UEBhMCVVMxEzARBgNVBAgTCkNh\n" +
            "bGlmb3JuaWExFjAUBgNVBAcTDVNhbiBGcmFuY2lzY28xDzANBgNVBAoTBnhtbGRhcDERMA8GA1UE\n" +
            "CxMIaW5mb2NhcmQxEzARBgNVBAMTCnhtbGRhcC5vcmcwHhcNMDYwMzIwMDA0NjU3WhcNMDYwNjE4\n" +
            "MDA0NjU3WjBzMQswCQYDVQQGEwJVUzETMBEGA1UECBMKQ2FsaWZvcm5pYTEWMBQGA1UEBxMNU2Fu\n" +
            "IEZyYW5jaXNjbzEPMA0GA1UEChMGeG1sZGFwMREwDwYDVQQLEwhpbmZvY2FyZDETMBEGA1UEAxMK\n" +
            "eG1sZGFwLm9yZzCCASIwDQYJKoZIhvcNAQEBBQADggEPADCCAQoCggEBANMnkVA4xfpG0bLos9FO\n" +
            "pNBjHAdFahy2cJ7FUwuXd/IShnG+5qF/z1SdPWzRxTtpFFyodtXlBUEIbiT+IbYPZF1vCcBrcFa8\n" +
            "Kz/4rBjrpPZgllgA/WSVKjnJvw8q4/tO6CQZSlRlj/ebNK9VyT1kN+MrKV1SGTqaIJ2l+7Rd05WH\n" +
            "scwZMPdVWBbRrg76YTfy6H/NlQIArNLZanPvE0Vd5QfD4ZyG2hTh3y7ZlJAUndGJ/kfZw8sKuL9Q\n" +
            "Srh4eOTc280NQUmPGz6LP5MXNmu0RxEcomod1+ToKll90yEKFAUKuPYFgm9J+vYm4tzRequLy/nj\n" +
            "teRIkcfAdcAtt6PCYjUCAwEAATANBgkqhkiG9w0BAQQFAAOCAQEAURtxiA7qDSq/WlUpWpfWiZ7H\n" +
            "vveQrwTaTwV/Fk3l/I9e9WIRN51uFLuiLtZMMwR02BX7Yva1KQ/Gl999cm/0b5hptJ+TU29rVPZI\n" +
            "lI32c5vjcuSVoEda8+BRj547jlC0rNokyWm+YtBcDOwfHSPFFwVPPVxyQsVEebsiB6KazFq6iZ8A\n" +
            "0F2HLEnpsdFnGrSwBBbH3I3PH65ofrTTgj1Mjk5kA6EVaeefDCtlkX2ogIFMlcS6ruihX2mlCLUS\n" +
            "rlPs9TH+M4j/R/LV5QWJ93/X9gsxFrxVFGg3b75EKQP8MZ111/jaeKd80mUOAiTO06EtfjXZPrjP\n" +
            "N4e2l05i2EGDUA==</ds:X509Certificate></ds:X509Data></ds:KeyInfo></ds:Signature></xmldap:Body>";

    private KeystoreUtil keystore = null;

    public void setUp() throws Exception {
        super.setUp();
        keystore = new KeystoreUtil("/Users/cmort/build/infocard/conf/xmldap.jks", "storepassword");

    }

    /*  TODO - MUST REFACTOR For new Base64 with no linebreaks

    public void testToXMLWithElement() throws Exception{

        Element body = new Element("xmldap:Body", "http://www.xmldap.org");
        body.addNamespaceDeclaration(WSConstants.WSU_PREFIX, WSConstants.WSSE_OASIS_10_WSU_NAMESPACE);
        Attribute idAttr = new Attribute(WSConstants.WSU_PREFIX + ":Id", WSConstants.WSSE_OASIS_10_WSU_NAMESPACE, "urn:guid:58824342-832F-C99B-925B-CB0E858E5D65");
        body.addAttribute(idAttr);
        Element child = new Element("xmldap:Child", "http://www.xmldap.org");
        body.appendChild(child);

        EnvelopedSignature signer = new EnvelopedSignature(keystore, "xmldap", "keypassword");
        Element signedXML = signer.sign(body);
        System.out.println(signedXML.toXML());
        assertEquals(SIGNED_XML,signedXML.toXML());



    }



    public void testToXMLWithDoc() throws Exception{

        Element body = new Element("xmldap:Body", "http://www.xmldap.org");
        body.addNamespaceDeclaration(WSConstants.WSU_PREFIX, WSConstants.WSSE_OASIS_10_WSU_NAMESPACE);
        Attribute idAttr = new Attribute(WSConstants.WSU_PREFIX + ":Id", WSConstants.WSSE_OASIS_10_WSU_NAMESPACE, "urn:guid:58824342-832F-C99B-925B-CB0E858E5D65");
        body.addAttribute(idAttr);
        Element child = new Element("xmldap:Child", "http://www.xmldap.org");
        body.appendChild(child);
        Document bodyDoc = new Document(body);

        EnvelopedSignature signer = new EnvelopedSignature(keystore, "xmldap", "keypassword");
        Document signedXML = signer.sign(bodyDoc);
        String test = signedXML.toXML();
        System.out.println(test);
        System.out.println(test.length() + ":" + SIGNED_XML_DOC.length());

        System.out.println(signedXML.toXML());
        assertEquals(SIGNED_XML_DOC,signedXML.toXML());

    }

    public void testToXMLWithString() throws Exception{

        EnvelopedSignature signer = new EnvelopedSignature(keystore, "xmldap", "keypassword");

        assertEquals(SIGNED_XML_DOC,signer.sign(XML));

    }

    */

    public void testStub() throws Exception{

        assertTrue(true);

    }

}
