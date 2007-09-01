package org.xmldap.firefox;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;

import org.xmldap.exceptions.SerializationException;
import org.xmldap.rp.Token;
import org.xmldap.saml.Subject;
import org.xmldap.xml.Canonicalizable;
import org.xmldap.xmldsig.AsymmetricKeyInfo;
import org.xmldap.xmldsig.KeyInfo;
import org.xmldap.xmldsig.SAMLTokenKeyInfo;

import nu.xom.Attribute;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.canonical.Canonicalizer;

import junit.framework.TestCase;

public class RequestSecurityTokenTest extends TestCase {

	protected void setUp() throws Exception {
		super.setUp();
	}

    private static byte[] canonicalize(Element xml) throws SerializationException {
        return canonicalize(xml, Canonicalizable.EXCLUSIVE_CANONICAL_XML);  //To change body of implemented methods use File | Settings | File Templates.
    }

    private static byte[] canonicalize(Element xml, String canonicalizationAlgorithm) throws SerializationException {


        byte[] dataBytes = null;

        try {

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            Canonicalizer outputter = new Canonicalizer(stream, canonicalizationAlgorithm);
            //TODO - support prefix list for exclusive!
            //outputter.setInclusiveNamespacePrefixList("");
            //Canonicalizer outputter = new Canonicalizer(stream, Canonicalizer.CANONICAL_XML);
            outputter.write(xml);
            dataBytes = stream.toByteArray();
        } catch (IOException ioe) {

            throw new SerializationException("IO Exception during canonicalization of SignedInfo");
        }

        return dataBytes;

    }

	public void test() throws Exception {
		String card =
			 "<infocard>" +
			 "   <name>nessus</name>" +
			 "   <type>selfAsserted</type>" +
			 "   <version>1</version>" +
			 "   <id>45925</id>" +
			 "   <privatepersonalidentifier>5601ad61a8d29f0c464f45c9fc1ebb014acd060e</privatepersonalidentifier>" +
			 "   <supportedclaim>givenname</supportedclaim>" +
			 "   <supportedclaim>surname</supportedclaim>" +
			 "   <supportedclaim>emailaddress</supportedclaim>" +
			 "   <supportedclaim>imgurl</supportedclaim>" +
			 "   <carddata>" +
			 "     <selfasserted>" +
			 "       <givenname>Axel</givenname>" +
			 "       <surname>Nennker</surname>" +
			 "       <emailaddress>axel@nennker.de</emailaddress>" +
			 "       <imgurl>file:///D:/Dokumente/nessus.png</imgurl>" +
			 "     </selfasserted>" +
			 "   </carddata>" +
			 " </infocard>";
			        X509Certificate cert = null;
			        cert = org.xmldap.util.XmldapCertsAndKeys.getXmldapCert1();
			        PrivateKey privateKey = null;
			        privateKey = org.xmldap.util.XmldapCertsAndKeys.getXmldapPrivateKey1();

			        X509Certificate relyingPartyCert = cert;
					X509Certificate[] chain = new X509Certificate[1];
					chain[0] = cert;
					String requiredClaims = "http://schemas.xmlsoap.org/ws/2005/05/identity/claims/privatepersonalidentifier http://schemas.xmlsoap.org/ws/2005/05/identity/claims/givenname http://schemas.xmlsoap.org/ws/2005/05/identity/claims/surname http://schemas.xmlsoap.org/ws/2005/05/identity/claims/emailaddress"; 
					String optionalClaims = "http://schemas.xmlsoap.org/ws/2005/05/identity/claims/streetaddress http://schemas.xmlsoap.org/ws/2005/05/identity/claims/locality http://schemas.xmlsoap.org/ws/2005/05/identity/claims/stateorprovince http://schemas.xmlsoap.org/ws/2005/05/identity/claims/postalcode http://schemas.xmlsoap.org/ws/2005/05/identity/claims/country http://schemas.xmlsoap.org/ws/2005/05/identity/claims/homephone http://schemas.xmlsoap.org/ws/2005/05/identity/claims/otherphone http://schemas.xmlsoap.org/ws/2005/05/identity/claims/mobilephone http://schemas.xmlsoap.org/ws/2005/05/identity/claims/dateofbirth http://schemas.xmlsoap.org/ws/2005/05/identity/claims/gender";
					X509Certificate signingCert = cert;
					PrivateKey signingKey = privateKey;
					String encryptedXML = 	TokenIssuer.getSelfAssertedToken(
							card, 
							relyingPartyCert,
							chain,
							requiredClaims,
							optionalClaims,
							signingCert,
							signingKey,
							"https://relyingparty.example.com/AudienceRestriction",
							Subject.HOLDER_OF_KEY);
					Token token = new Token(encryptedXML, privateKey);
        
        String sts = "http://contoso.com/tokenservice";
        Element encryptedSamlAssertion = token.getDoc().getRootElement();
        Element x = (Element)encryptedSamlAssertion.copy();
        byte[] conan = canonicalize(x);
        Document doc = org.xmldap.xml.XmlUtils.parse(new String(conan));
        Element y = doc.getRootElement();
        Element z = (Element)y.copy();
        AsymmetricKeyInfo asymmetricKeyInfo = new AsymmetricKeyInfo(cert);
        String samlAssertionId = "";
        SAMLTokenKeyInfo samlTokenKeyInfo = new SAMLTokenKeyInfo(samlAssertionId);
        Attribute assertionID = z.getAttribute("AssertionID");
        if (assertionID != null) {
        	samlAssertionId = assertionID.getValue();
        }

        RequestSecurityToken rst = new RequestSecurityToken(sts, z, asymmetricKeyInfo, privateKey, samlTokenKeyInfo);
        assertEquals(rst.toXML(), "");
	}
	
}
