package org.xmldap.xmldsig;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidAlgorithmParameterException;
import java.security.Key;
import java.security.KeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.xml.crypto.AlgorithmMethod;
import javax.xml.crypto.Data;
import javax.xml.crypto.KeySelector;
import javax.xml.crypto.KeySelectorException;
import javax.xml.crypto.KeySelectorResult;
import javax.xml.crypto.MarshalException;
import javax.xml.crypto.NodeSetData;
import javax.xml.crypto.OctetStreamData;
import javax.xml.crypto.URIDereferencer;
import javax.xml.crypto.URIReference;
import javax.xml.crypto.URIReferenceException;
import javax.xml.crypto.XMLCryptoContext;
import javax.xml.crypto.XMLStructure;
import javax.xml.crypto.dom.DOMStructure;
import javax.xml.crypto.dsig.Reference;
import javax.xml.crypto.dsig.SignatureMethod;
import javax.xml.crypto.dsig.SignedInfo;
import javax.xml.crypto.dsig.XMLObject;
import javax.xml.crypto.dsig.XMLSignature;
import javax.xml.crypto.dsig.XMLSignatureException;
import javax.xml.crypto.dsig.XMLSignatureFactory;
import javax.xml.crypto.dsig.dom.DOMSignContext;
import javax.xml.crypto.dsig.dom.DOMValidateContext;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import javax.xml.crypto.dsig.keyinfo.KeyInfoFactory;
import javax.xml.crypto.dsig.keyinfo.KeyValue;
import javax.xml.crypto.dsig.keyinfo.X509Data;
import javax.xml.crypto.dsig.spec.C14NMethodParameterSpec;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xmldap.exceptions.SigningException;
import org.xmldap.ws.WSConstants;


public class Jsr105Signatur {

  static final String providerName = System.getProperty("jsr105Provider",
      "org.jcp.xml.dsig.internal.dom.XMLDSigRI");

  private static class MyUriDereferencer implements URIDereferencer {

    private Data defaultDereference(URIReference uriReference, XMLCryptoContext context)
      throws URIReferenceException {
      URIDereferencer defaultUd = XMLSignatureFactory.getInstance().getURIDereferencer();
      Data result = defaultUd.dereference(uriReference, context);
      printData(result);
      return result;
      
    }

    private void printData(Data result) {
      int count = 1;
      if (result instanceof NodeSetData) {
        Iterator<Node> iter = ((NodeSetData)result).iterator();
        while (iter.hasNext()) {
          Node node = iter.next();
          String localname = node.getLocalName();
          Node parent = node.getParentNode();
          StringBuffer sb = new StringBuffer();
          sb.append(count);
          if (parent != null) {
            sb.append(" parent: " + parent.getLocalName());
          }
          if (localname != null) {
            sb.append(" localname:" + localname);
          }
          String nodeValue = node.getNodeValue();
          if (nodeValue != null) {
            sb.append(" Value:" + nodeValue);
          }
          System.out.println(sb.toString());
          count++;
        }
      } else {
        if (result instanceof OctetStreamData) {
          OctetStreamData data = (OctetStreamData)result;
          InputStream is = data.getOctetStream();
          System.out.println("boink");
        }
      }
    }
    
    @Override
    public Data dereference(URIReference uriReference, XMLCryptoContext context)
        throws URIReferenceException {
      System.out.println("uriReference:" + uriReference.getURI());
      Data result = null;
      
      try {
        result = defaultDereference(uriReference, context);
        return result;
      } catch (URIReferenceException ex) { /* ignore */ }
      
      DOMValidateContext domValidateContext = (DOMValidateContext)context;
      Element node = (Element)domValidateContext.getNode();

      printNode(node);

      Data result1 = createNodeSet(node, uriReference.getURI());
      
      debugCompareNodeSet(result, result1);
      
      return result1;
    }

    private boolean debugCompareNodeSet(Data data1, Data data2) {
      if (data1 == data2) {
        return true;
      }
      if ( !(data1 instanceof NodeSetData) ) return false;
      if ( !(data2 instanceof NodeSetData) ) return false;
      NodeSetData nodesetdata1 = (NodeSetData)data1;
      NodeSetData nodesetdata2 = (NodeSetData)data2;
      Iterator<Node> iter1 = nodesetdata1.iterator();
      Iterator<Node> iter2 = nodesetdata2.iterator();
      int count=0;
      while (iter1.hasNext()) {
        count++;
        Node node1 = iter1.next();
        Node node2 = iter2.next();
        if (!node1.equals(node2)) {
          return false;
        }
      }
      if (iter2.hasNext()) {
        return false;
      }
      return true;
    }
    
    private Data createNodeSet(Element node, String sourceUri) throws URIReferenceException {
      Data result;
            NodeList nL = node.getElementsByTagNameNS(WSConstants.SAML11_NAMESPACE, "Assertion");
           // NodeList nL = node.getElementsByTagNameNS(XMLSignature.XMLNS, "Object");
            if (nL.getLength() == 0) {
              throw new URIReferenceException("to few");
            }
            if (nL.getLength() > 1) {
              throw new URIReferenceException("to many:" + nL.getLength());
            }
            Node signatureNode = nL.item(0);
            result = new MyNodeSetData(signatureNode);
            printData(result);
      return result;
    }

    private void printNode(Element node)
        throws TransformerFactoryConfigurationError {
      try {
        TransformerFactory tfactory = TransformerFactory.newInstance();
        Transformer serializer;
        serializer = tfactory.newTransformer();
        // Setup indenting to "pretty print"
        serializer.setOutputProperty(OutputKeys.INDENT, "yes");
        serializer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount",
            "2");

        serializer.transform(new DOMSource(node), new StreamResult(System.out));
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    
    static class MyNodeSetData implements NodeSetData {
      ArrayList<Node> mSet = null;
//      XMLSignatureInput mXmlSignatureInput = null;
      
      private void add(Node node) {
        mSet.add(node);
        NamedNodeMap attributes = node.getAttributes();
        if (attributes != null) {
          for (int i=0; i<attributes.getLength(); i++) {
            Node attribute = attributes.item(i);
            mSet.add(attribute);
          }
        }
        NodeList children = node.getChildNodes();
        for (int index=0; index<children.getLength(); index++) {
          Node child = children.item(index);
          add(child);
        }
      }
      
      MyNodeSetData(Node node) {
        mSet = new ArrayList<Node>();
        add(node);
      }
      
      @Override
      public Iterator<Node> iterator() {
        return Collections.unmodifiableList(mSet).iterator();
      }
      
    }

    private Data generateOctetStreamData(Node signatureNode)
        throws TransformerFactoryConfigurationError {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      
      TransformerFactory tfactory = TransformerFactory.newInstance();
      

      try {
        Transformer serializer = tfactory.newTransformer();
        serializer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        serializer.transform(new DOMSource(signatureNode), new StreamResult(baos));
System.out.println(new String(baos.toByteArray()));
        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        Data result = new OctetStreamData(bais);
        return result;
      } catch (TransformerConfigurationException e) {
        e.printStackTrace();
        return null;
      } catch (TransformerException e) {
        e.printStackTrace();
        return null;
      }
    }
    
  }
  

//  KeyValueKeySelector.select: start 
//  KeyValueKeySelector.select: sm http://www.w3.org/2001/04/xmldsig-more#rsa-sha256
//  Jsr105Signature::KeyValueKeySelector X509Data length=1
//  Jsr105Signature::KeyValueKeySelector X509Certificate sigAlgName=SHA1withRSA
//  Jsr105Signature::KeyValueKeySelector X509Certificate algs do not match. sm.alg=http://www.w3.org/2001/04/xmldsig-more#rsa-sha256 pk.alg=RSA
//  Jsr105Signatur: XMLSignatureException javax.xml.crypto.dsig.XMLSignatureException: cannot find validation key

  // @@@FIXME: this should also work for key types other than DSA/RSA
  static boolean algEquals(String algURI, String algName) {
    if (algName.equalsIgnoreCase("DSA")
        && (algURI.equalsIgnoreCase(SignatureMethod.DSA_SHA1) ||
            algURI.equalsIgnoreCase("http://www.w3.org/2009/xmldsig11#dsa-sha256"))) {
      return true;
    } else if (algName.equalsIgnoreCase("RSA")
        && (algURI.equalsIgnoreCase(SignatureMethod.RSA_SHA1) || 
        algURI.equalsIgnoreCase("http://www.w3.org/2001/04/xmldsig-more#rsa-sha256") ||
        algURI.equalsIgnoreCase("http://www.w3.org/2001/04/xmldsig-more#rsa-sha384") ||
        algURI.equalsIgnoreCase("http://www.w3.org/2001/04/xmldsig-more#rsa-sha512") ||
        algURI.equalsIgnoreCase("http://www.w3.org/2001/04/xmldsig-more#rsa-ripemd160"))) {
      return true;
    } else {
      return false;
    }
  }

  private static class SimpleKeySelectorResult implements KeySelectorResult {
    private PublicKey pk;

    SimpleKeySelectorResult(PublicKey pk) {
      this.pk = pk;
    }

    public Key getKey() {
      return pk;
    }
  }

  /**
   * KeySelector which retrieves the public key out of the KeyValue element and
   * returns it. NOTE: If the key algorithm doesn't match signature algorithm,
   * then the public key will be ignored.
   */
  private static class KeyValueKeySelector extends KeySelector {
    public KeySelectorResult select(KeyInfo keyInfo,
        KeySelector.Purpose purpose, AlgorithmMethod method,
        XMLCryptoContext context) throws KeySelectorException {
      System.out.println("KeyValueKeySelector.select: start ");
      if (keyInfo == null) {
        throw new KeySelectorException("Null KeyInfo object!");
      }
      SignatureMethod sm = (SignatureMethod) method;
      System.out.println("KeyValueKeySelector.select: sm " + sm.getAlgorithm());
      List<?> list = keyInfo.getContent();

      for (int i = 0; i < list.size(); i++) {
        XMLStructure xmlStructure = (XMLStructure) list.get(i);
        if (xmlStructure instanceof KeyValue) {
          System.out
          .println("Jsr105Signature::KeyValueKeySelector KeyValue");
          PublicKey pk = null;
          try {
            pk = ((KeyValue) xmlStructure).getPublicKey();
          } catch (KeyException ke) {
            throw new KeySelectorException(ke);
          }
          // make sure algorithm is compatible with method
          if (algEquals(sm.getAlgorithm(), pk.getAlgorithm())) {
            return new SimpleKeySelectorResult(pk);
          } else {
            System.out
            .println("Jsr105Signature::KeyValueKeySelector KeyValue: algs do not match. sm.alg=" + sm.getAlgorithm() + 
                " pk.alg=" + pk.getAlgorithm());
          }
        } else if (xmlStructure instanceof X509Data) {
          X509Data x509Data = (X509Data) xmlStructure;
          List<?> x509DataContent = x509Data.getContent();
          System.out
          .println("Jsr105Signature::KeyValueKeySelector X509Data length=" + x509DataContent.size());
          for (int j = 0; j < x509DataContent.size(); j++) {
            Object x509DataContentElement = x509DataContent.get(j);
            if (x509DataContentElement instanceof X509Certificate) {
              X509Certificate cert = (X509Certificate) x509DataContentElement;
              System.out
              .println("Jsr105Signature::KeyValueKeySelector X509Certificate sigAlgName=" + cert.getSigAlgName());
              PublicKey pk = cert.getPublicKey();
              if (algEquals(sm.getAlgorithm(), pk.getAlgorithm())) {
                System.out
                    .println("Jsr105Signature::KeyValueKeySelector using Cert Public Key "
                        + cert.getSubjectDN());
                return new SimpleKeySelectorResult(pk);
              } else {
                System.out
                .println("Jsr105Signature::KeyValueKeySelector X509Certificate algs do not match. sm.alg="
                    + sm.getAlgorithm() + " pk.alg=" + pk.getAlgorithm());
              }
            } else {
              System.out
                  .println("Jsr105Signature::KeyValueKeySelector unsupported Cert"
                      + xmlStructure.getClass().getName());
            }
          }
        } else {
          System.out
              .println("Jsr105Signature::KeyValueKeySelector unsupported "
                  + xmlStructure.getClass().getName());
        }
      }
      throw new KeySelectorException("No KeyValue element found!");
    }
  }

  static private void serialize(Document doc, OutputStream out)
      throws TransformerException {

    TransformerFactory tfactory = TransformerFactory.newInstance();
    Transformer serializer;
    serializer = tfactory.newTransformer();
    // Setup indenting to "pretty print"
    serializer.setOutputProperty(OutputKeys.INDENT, "yes");
    serializer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount",
        "2");

    serializer.transform(new DOMSource(doc), new StreamResult(out));
  }

  static private void printKeyInfo(KeyInfo ki) throws KeyException {
    List<?> list = ki.getContent();

    for (int i = 0; i < list.size(); i++) {
      XMLStructure xmlStructure = (XMLStructure) list.get(i);
      if (xmlStructure instanceof KeyValue) {
        PublicKey pk = null;
        pk = ((KeyValue) xmlStructure).getPublicKey();
        // make sure algorithm is compatible with method
        System.out.println("KeyValue Public Key Alg: " + pk.getAlgorithm());
      } else if (xmlStructure instanceof X509Data) {
        X509Data x509Data = (X509Data) xmlStructure;
        List<?> x509DataContent = x509Data.getContent();
        for (int j = 0; j < x509DataContent.size(); j++) {
          Object x509DataContentElement = x509DataContent.get(j);
          if (x509DataContentElement instanceof X509Certificate) {
            X509Certificate cert = (X509Certificate) x509DataContentElement;
            System.out.println("X509Certificate DN: " + cert.getSubjectDN());
            PublicKey pk = cert.getPublicKey();
            System.out.println("KeyValue X509Certificate Public Key Alg: "
                + pk.getAlgorithm());
          } else {
            System.out
                .println("Jsr105Signature::KeyValueKeySelector unsupported Cert"
                    + xmlStructure.getClass().getName());
          }
        }
      } else {
        System.out.println("Jsr105Signature::KeyValueKeySelector unsupported "
            + xmlStructure.getClass().getName());
      }
    }
  }

  static public boolean validateSignature(InputStream inputStream)
      throws SigningException {
    // Instantiate the document to be validated
    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    dbf.setNamespaceAware(true);
    try {
      Document doc;
      doc = dbf.newDocumentBuilder().parse(inputStream);
      return validateSignature(doc);
    } catch (SAXException e) {
      throw new SigningException(e);
    } catch (IOException e) {
      throw new SigningException(e);
    } catch (ParserConfigurationException e) {
      throw new SigningException(e);
    }
  }

  static public boolean validateSignature(Document doc) throws SigningException {
    try {
      Jsr105Signatur.serialize(doc, System.out);
    } catch (TransformerException e1) {
      throw new SigningException(e1);
    }

    // Find Signature element
    NodeList nl = doc.getElementsByTagNameNS(XMLSignature.XMLNS, "Signature");
    if (nl.getLength() == 0) {
      throw new SigningException("Cannot find Signature element");
    }
    if (nl.getLength() > 1) {
      throw new SigningException("More than one Signature element found");
    }
    return validateSignature(nl.item(0));
  }

  static public boolean validateSignature(Node signatureNode)
      throws SigningException {
    try {
      XMLSignatureFactory fac = XMLSignatureFactory.getInstance("DOM",
          (Provider) Class.forName(providerName).newInstance());
      // Create a DOMValidateContext and specify a KeyValue
      // KeySelector
      // and document context
      DOMValidateContext valContext = new DOMValidateContext(
          new KeyValueKeySelector(), signatureNode);
      MyUriDereferencer myURIDereferencer = new MyUriDereferencer();
      
      valContext.setURIDereferencer(myURIDereferencer);
      
      // unmarshal the XMLSignature
      XMLSignature signature;
      try {
        signature = fac.unmarshalXMLSignature(valContext);
        // Validate the XMLSignature (generated above)
        try {
          boolean coreValidity = signature.validate(valContext);
          if (coreValidity) {
            return true;
          }
          System.out.println("Jsr105Signatur: signatur is not valid");
          KeyInfo ki = signature.getKeyInfo();
          try {
            Jsr105Signatur.printKeyInfo(ki);
          } catch (KeyException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
          }
          boolean sv = signature.getSignatureValue().validate(valContext);
          System.out.println("signature validation status: " + sv);
          // check the validation status of each Reference
          Iterator<?> i = signature.getSignedInfo().getReferences().iterator();
          for (int j = 0; i.hasNext(); j++) {
            boolean refValid = ((Reference) i.next()).validate(valContext);
            System.out.println("ref[" + j + "] validity status: " + refValid);
          }
          return false;
        } catch (XMLSignatureException e) {
          System.out.println("Jsr105Signatur: XMLSignatureException " + e);
          throw new SigningException(e);
        }
      } catch (MarshalException e) {
        System.out.println("Jsr105Signatur: MarshalException " + e);
        throw new SigningException(e);
      }

    } catch (InstantiationException e) {
      throw new SigningException(e);
    } catch (IllegalAccessException e) {
      throw new SigningException(e);
    } catch (ClassNotFoundException e) {
      throw new SigningException(e);
    }
  }

  /**
   * @param streamToSign
   * @param signedStream
   * @param kp
   * @param canonicalizationMethod
   *          e.g.:
   *          javax.xml.crypto.dsig.CanonicalizationMethod.INCLUSIVE_WITH_COMMENTS
   * @param digestMethod
   *          e.g.: javax.xml.crypto.dsig.DigestMethod.SHA1
   * @param signatureMethod
   *          e.g.: SignatureMethod.DSA_SHA1
   * @throws SigningException
   */
  static public void genSignature(InputStream streamToSign,
      OutputStream signedStream, Certificate cert, PrivateKey privateKey,
      String canonicalizationMethod, String digestMethod, String signatureMethod)
      throws SigningException {

    final String objectId = "object";

    XMLSignatureFactory fac;
    try {
      fac = XMLSignatureFactory.getInstance("DOM", (Provider) Class.forName(
          providerName).newInstance());
      Reference ref;
      try {
        // Collections.singletonList(fac.newTransform(Transform.ENVELOPED,
        // (TransformParameterSpec) null))
        ref = fac.newReference("#" + objectId, fac.newDigestMethod(
            digestMethod, null), null, null, null);
      } catch (NoSuchAlgorithmException e) {
        throw new SigningException(e);
      } catch (InvalidAlgorithmParameterException e) {
        throw new SigningException(e);
      }

      SignedInfo si;
      try {
        si = fac.newSignedInfo(fac.newCanonicalizationMethod(
            canonicalizationMethod, (C14NMethodParameterSpec) null), fac
            .newSignatureMethod(signatureMethod, null), Collections
            .singletonList(ref));
      } catch (NoSuchAlgorithmException e) {
        throw new SigningException(e);
      } catch (InvalidAlgorithmParameterException e) {
        throw new SigningException(e);
      }

      KeyInfoFactory kif = fac.getKeyInfoFactory();
      // KeyValue kv;
      // try {
      // kv = kif.newKeyValue(kp.getPublic());
      // } catch (KeyException e) {
      // throw new SigningException(e);
      // }
      //
      // KeyInfo ki = kif.newKeyInfo(Collections.singletonList(kv));
      X509Data x509d = kif.newX509Data(Collections.singletonList(cert));
      KeyInfo ki = kif.newKeyInfo(Collections.singletonList(x509d));

      DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
      dbf.setNamespaceAware(true);
      Document doc;
      try {
        Document dummyDoc = dbf.newDocumentBuilder().newDocument();
        Node text = dummyDoc.createTextNode("some text");

        doc = dbf.newDocumentBuilder().parse(streamToSign);
        Node node = doc.getDocumentElement().cloneNode(true);
        XMLStructure content = new DOMStructure(node);
        XMLObject obj = fac.newXMLObject(Collections.singletonList(content),
            objectId, null, null);

        DOMSignContext dsc = new DOMSignContext(privateKey, dummyDoc);

        XMLSignature signature = fac.newXMLSignature(si, ki, Collections
            .singletonList(obj), null, null);
        try {
          signature.sign(dsc);
        } catch (MarshalException e) {
          throw new SigningException(e);
        } catch (XMLSignatureException e) {
          throw new SigningException(e);
        }

        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer trans;
        try {
          trans = tf.newTransformer();
        } catch (TransformerConfigurationException e) {
          throw new SigningException(e);
        }
        try {
          trans.transform(new DOMSource(dummyDoc), new StreamResult(
              signedStream));
        } catch (TransformerException e) {
          throw new SigningException(e);
        }
      } catch (SAXException e) {
        throw new SigningException(e);
      } catch (IOException e) {
        throw new SigningException(e);
      } catch (ParserConfigurationException e) {
        throw new SigningException(e);
      }
    } catch (InstantiationException e) {
      throw new SigningException(e);
    } catch (IllegalAccessException e) {
      throw new SigningException(e);
    } catch (ClassNotFoundException e) {
      throw new SigningException(e);
    }

  }
}
