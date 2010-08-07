package org.xmldap.xmldsig;

import java.io.IOException;
import java.math.BigInteger;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

import nu.xom.Attribute;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Elements;

import org.xmldap.crypto.CryptoUtils;
import org.xmldap.exceptions.CryptoException;
import org.xmldap.exceptions.ParsingException;
import org.xmldap.ws.WSConstants;

public class ValidatingEnvelopedSignature {
  ParsedSignature parsedSignature = null;
  Element signature = null;
  
  public ValidatingEnvelopedSignature(Document doc) throws ParsingException {
    this(doc.getRootElement());
  }
  
  public ValidatingEnvelopedSignature(Element signature) throws ParsingException {
    String name = signature.getLocalName();
    if ("Signature".equals(name)) {
      if (WSConstants.DSIG_NAMESPACE.equals(signature.getNamespaceURI())) {
        try {
          parsedSignature = new ParsedSignature(signature);
          this.signature = signature;
        } catch (IOException e) {
          throw new ParsingException(e);
        }
      }
    } else {
      throw new ParsingException("ValidatingEnvelopedSignature: localname must be Signature, not " + name + ".\n" + signature.toXML());
    }
  }
  
  /**
   * @return List of signed objects which had a valid signature or null if the signature is not valid
   * @throws CryptoException
   */
  public Element validate() throws CryptoException {
    List<Element> elts = validate(false);
    if (elts == null) return null;
    if (elts.size() != 1) {
      System.out.println("multipleReferencesAllowed == false, but number of list Elements != 1");
      throw new InternalError("multipleReferencesAllowed == false, but number of list Elements != 1");
    }
    return elts.get(0);
  }
  
  /**
   * @param multipleReferencesAllowed
   * @return List of signed objects which had a valid signature or null if one signature is not valid
   *      If multipleReferencesAllowed == false then this should be exactly one Element
   * @throws CryptoException
   */
  public List<Element> validate(boolean multipleReferencesAllowed) throws CryptoException {
    List<Element> validElements = new ArrayList<Element>(1);
    
    ParsedSignedInfo parsedSignedInfo = parsedSignature.getParsedSignedInfo();
    List<ParsedReference> references = parsedSignedInfo.getReferences();
    Elements objects = signature.getChildElements("Object", WSConstants.DSIG_NAMESPACE);

    ParsedKeyInfo keyInfo = parsedSignature.getParsedKeyInfo();
    ParsedX509Data parsedX509Data = keyInfo.getParsedX509Data();
    if (parsedX509Data != null) {
      List<X509Certificate> certList = parsedX509Data.getCertificates();
      for (X509Certificate cert : certList) {
        try {
          System.out.println("checking validity of " + cert.getSubjectX500Principal().getName());
          cert.checkValidity();
        } catch (CertificateExpiredException e) {
          System.out.println("CertificateExpiredException: " + e.getMessage());
          throw new CryptoException(e);
        } catch (CertificateNotYetValidException e) {
          System.out.println("CertificateNotYetValidException: " + e.getMessage());
          throw new CryptoException(e);
        }
      }
    }
    BigInteger modulus = keyInfo.getModulus();
    BigInteger exponent = keyInfo.getExponent();
    
    byte[] signedInfoCanonicalBytes;
    try {
      signedInfoCanonicalBytes = parsedSignature.getSignedInfoCanonicalBytes();
    } catch (IOException e) {
      throw new CryptoException(e);
    }

    String signatureValue = parsedSignature.getSignatureValue();

    if (references.size() == 0) {
      throw new CryptoException("ValidatingEnvelopedSignature: number of Reference elements is 0!");
    }
    
    if ((references.size() > 1) && (multipleReferencesAllowed == false)) {
      throw new CryptoException("ValidatingEnvelopedSignature: multipleReferencesAllowed == false, but number of Reference elements is " + references.size());
    }
    
    for (ParsedReference reference : references) {
      String uri = reference.getUri();
      if (uri.charAt(0) == '#') {
        uri = uri.substring(1);
      }
      for (int index=0; index<objects.size(); index++) {
        Element object = objects.get(index);
        Attribute idAttr = object.getAttribute("Id");
        if (idAttr == null) {
          continue; // oops
        }
        String idValue = idAttr.getValue();
        if (idValue == null) {
          continue; // oops
        }
        if (idValue.equals(uri)) {
          Element root = object;
          String digest = reference.getDigestValue();
          String messageDigestAlgorithm = CryptoUtils.convertMessageDigestAlgorithmUrl(reference.getDigestAlgorithm());
          boolean valid = ValidatingBaseEnvelopedSignature.validateRSA(
              root, signedInfoCanonicalBytes, signatureValue, modulus, exponent, digest, messageDigestAlgorithm);
          if (valid == false) {
            return null;
          }
          validElements.add(object);
          // Don not break here. We must check all objects with that id.
          // Several with the same id is fishy!
        }
      }
    }
    return validElements;
  }
  
  public X509Certificate getCert() {
    ParsedKeyInfo keyInfo = parsedSignature.getParsedKeyInfo();
    if (keyInfo == null) { return null; }
    ParsedX509Data parsedX509data = keyInfo.getParsedX509Data();
    if (parsedX509data == null) { return null; }
    List<X509Certificate> certs = parsedX509data.getCertificates();
    if (certs == null) { return null; }
    if (certs.size() == 0) { return null; }
    return certs.get(0);
  }
}
