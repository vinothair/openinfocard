package org.xmldap.sts.servlet;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.util.Iterator;
import java.util.Locale;

import javax.xml.crypto.Data;
import javax.xml.crypto.URIDereferencer;
import javax.xml.crypto.URIReference;
import javax.xml.crypto.URIReferenceException;
import javax.xml.crypto.XMLCryptoContext;
import javax.xml.crypto.dsig.XMLSignature;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import junit.framework.TestCase;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xmldap.exceptions.CryptoException;
import org.xmldap.exceptions.SigningException;
import org.xmldap.exceptions.TokenIssuanceException;
import org.xmldap.sts.db.DbSupportedClaim;
import org.xmldap.sts.db.ManagedCard;
import org.xmldap.sts.db.SupportedClaims;
import org.xmldap.util.Bag;
import org.xmldap.util.XmldapCertsAndKeys;
import org.xmldap.ws.WSConstants;
import org.xmldap.xmldsig.Jsr105Signatur;

public class UtilsTest extends TestCase {

  public void testIssue() throws IOException, CryptoException,
      TokenIssuanceException, InvalidKeySpecException,
      NoSuchAlgorithmException, CertificateException, InstantiationException,
      IllegalAccessException, ClassNotFoundException, SAXException,
      ParserConfigurationException, SigningException {
    ManagedCard card = new ManagedCard("cardid");
    card.setCardName("cardname");
    card.setCardVersion(1);
    card.setPrivatePersonalIdentifier("privatePersonalIdentifier");
    card.setRequireStrongRecipientIdentity(true);
    card.setRequireAppliesTo(true);
    // card.setTimeExpires(timeExpires);
    // card.setTimeIssued(timeIssued);
    Bag requestElements = new Bag();
    Locale clientLocale = null;
    X509Certificate cert = XmldapCertsAndKeys.getXmldapCert1();
    RSAPrivateKey key = XmldapCertsAndKeys.getXmldapPrivateKey();
    String issuerName = "JUNIT Test";
    String supportedClaimsClass = "org.xmldap.sts.db.DbSupportedClaims";
    SupportedClaims supportedClaimsImpl = SupportedClaims
        .getInstance(supportedClaimsClass);
    Iterator<DbSupportedClaim> iter = supportedClaimsImpl.iterator();
    while (iter.hasNext()) {
      DbSupportedClaim claim = iter.next();
      String uri = claim.uri;
      card.setClaim(uri, "value");
    }

    String relyingPartyURL = "https://restrictedToMe/";
    String relyingPartyCertB64 = XmldapCertsAndKeys.getXmldapCert1String();
    String messageDigestAlgorithm = "SHA1";

    String stsResponse = Utils.issue(card, requestElements, clientLocale, cert,
        key, issuerName, supportedClaimsImpl, relyingPartyURL,
        relyingPartyCertB64, messageDigestAlgorithm);
    System.out.println("STSResponse : " + stsResponse);
    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    dbf.setNamespaceAware(true);
    ByteArrayInputStream is = new ByteArrayInputStream(stsResponse.getBytes());
    Document stsResponseDoc = dbf.newDocumentBuilder().parse(is);

    NodeList assertions = stsResponseDoc.getElementsByTagNameNS(
        WSConstants.SAML11_NAMESPACE, "Assertion");
    assertEquals(1, assertions.getLength());
    Node assertion = assertions.item(0);

    NodeList signatures = stsResponseDoc.getElementsByTagNameNS(
        XMLSignature.XMLNS, "Signature");
    assertEquals(1, signatures.getLength());
    Node signatureNode = signatures.item(0);
    
    Document dummyDoc = dbf.newDocumentBuilder().newDocument();
    signatureNode = dummyDoc.importNode(signatureNode, true);
    dummyDoc.appendChild(signatureNode);
    System.out.println("length: " + dummyDoc.getChildNodes().getLength());
    System.out.println("localname: " + signatureNode.getLocalName());
    Element documentElement = dummyDoc.getDocumentElement();
    if (documentElement  == null) {
      System.out.println("documentelement is null");
    }
    System.out.println("length: " + dummyDoc.getChildNodes().getLength());
    signatures = dummyDoc.getElementsByTagNameNS(
        XMLSignature.XMLNS, "Signature");
    assertEquals(1, signatures.getLength());
    signatureNode = signatures.item(0);
    assertion = dummyDoc.importNode(assertion, true);
    NamedNodeMap attributes = assertion.getAttributes();
    Node assertionId = attributes.getNamedItem("AssertionID");
    System.out.println("AssertionID=" + assertionId.getNodeValue());
    signatureNode.appendChild(assertion);
    
    try {
      TransformerFactory tfactory = TransformerFactory.newInstance();
      Transformer serializer;
      serializer = tfactory.newTransformer();
      // Setup indenting to "pretty print"
      serializer.setOutputProperty(OutputKeys.INDENT, "yes");
      serializer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount",
          "2");

      serializer.transform(new DOMSource(signatureNode.getOwnerDocument()), new StreamResult(System.out));
    } catch (Exception e) {
      e.printStackTrace();
    }

    
    boolean valid = Jsr105Signatur.validateSignature(signatureNode);

    //assertTrue(valid);
  }
}
