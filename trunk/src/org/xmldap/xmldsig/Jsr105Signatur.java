package org.xmldap.xmldsig;

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
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.xml.crypto.AlgorithmMethod;
import javax.xml.crypto.KeySelector;
import javax.xml.crypto.KeySelectorException;
import javax.xml.crypto.KeySelectorResult;
import javax.xml.crypto.MarshalException;
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
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xmldap.exceptions.SigningException;

public class Jsr105Signatur {

	static final String providerName = System.getProperty("jsr105Provider",
			"org.jcp.xml.dsig.internal.dom.XMLDSigRI");

	// @@@FIXME: this should also work for key types other than DSA/RSA
	static boolean algEquals(String algURI, String algName) {
		if (algName.equalsIgnoreCase("DSA")
				&& algURI.equalsIgnoreCase(SignatureMethod.DSA_SHA1)) {
			return true;
		} else if (algName.equalsIgnoreCase("RSA")
				&& algURI.equalsIgnoreCase(SignatureMethod.RSA_SHA1)) {
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
	 * KeySelector which retrieves the public key out of the KeyValue element
	 * and returns it. NOTE: If the key algorithm doesn't match signature
	 * algorithm, then the public key will be ignored.
	 */
	private static class KeyValueKeySelector extends KeySelector {
		public KeySelectorResult select(KeyInfo keyInfo,
				KeySelector.Purpose purpose, AlgorithmMethod method,
				XMLCryptoContext context) throws KeySelectorException {
			if (keyInfo == null) {
				throw new KeySelectorException("Null KeyInfo object!");
			}
			SignatureMethod sm = (SignatureMethod) method;
			List list = keyInfo.getContent();

			for (int i = 0; i < list.size(); i++) {
				XMLStructure xmlStructure = (XMLStructure) list.get(i);
				if (xmlStructure instanceof KeyValue) {
					PublicKey pk = null;
					try {
						pk = ((KeyValue) xmlStructure).getPublicKey();
					} catch (KeyException ke) {
						throw new KeySelectorException(ke);
					}
					// make sure algorithm is compatible with method
					if (algEquals(sm.getAlgorithm(), pk.getAlgorithm())) {
						return new SimpleKeySelectorResult(pk);
					}
				} else if (xmlStructure instanceof X509Data) {
					X509Data x509Data = (X509Data) xmlStructure;
					List x509DataContent = x509Data.getContent();
					for (int j = 0; j < x509DataContent.size(); j++) {
						Object x509DataContentElement = x509DataContent.get(j);
						if (x509DataContentElement instanceof X509Certificate) {
							X509Certificate cert = (X509Certificate)x509DataContentElement;
							PublicKey pk = cert.getPublicKey();
							if (algEquals(sm.getAlgorithm(), pk.getAlgorithm())) {
								return new SimpleKeySelectorResult(pk);
							}
						}
					}
				}
			}
			throw new KeySelectorException("No KeyValue element found!");
		}
	}

	static public boolean validateSignature(InputStream inputStream)
			throws SigningException {
		// Instantiate the document to be validated
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setNamespaceAware(true);
		try {
			Document doc = dbf.newDocumentBuilder().parse(inputStream);
			// Find Signature element
			NodeList nl = doc.getElementsByTagNameNS(XMLSignature.XMLNS,
					"Signature");
			if (nl.getLength() == 0) {
				throw new SigningException("Cannot find Signature element");
			}
			if (nl.getLength() > 1) {
				throw new SigningException(
						"More than one Signature element found");
			}
			try {
				XMLSignatureFactory fac = XMLSignatureFactory.getInstance(
						"DOM", (Provider) Class.forName(providerName)
								.newInstance());
				// Create a DOMValidateContext and specify a KeyValue
				// KeySelector
				// and document context
				DOMValidateContext valContext = new DOMValidateContext(
						new KeyValueKeySelector(), nl.item(0));

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
						boolean sv = signature.getSignatureValue().validate(valContext);
					    System.out.println("signature validation status: " + sv);
					    // check the validation status of each Reference
					    Iterator i = signature.getSignedInfo().getReferences().iterator();
					    for (int j=0; i.hasNext(); j++) {
						boolean refValid = 
						    ((Reference) i.next()).validate(valContext);
						System.out.println("ref["+j+"] validity status: " + refValid);
					    }
					    return false;
					} catch (XMLSignatureException e) {
						throw new SigningException(e);
					} 
				} catch (MarshalException e) {
					throw new SigningException(e);
				}

				
				
			} catch (InstantiationException e) {
				throw new SigningException(e);
			} catch (IllegalAccessException e) {
				throw new SigningException(e);
			} catch (ClassNotFoundException e) {
				throw new SigningException(e);
			}
		} catch (SAXException e) {
			throw new SigningException(e);
		} catch (IOException e) {
			throw new SigningException(e);
		} catch (ParserConfigurationException e) {
			throw new SigningException(e);
		}
	}

	/**
	 * @param streamToSign
	 * @param signedStream
	 * @param kp
	 * @param canonicalizationMethod
	 *            e.g.:
	 *            javax.xml.crypto.dsig.CanonicalizationMethod.INCLUSIVE_WITH_COMMENTS
	 * @param digestMethod
	 *            e.g.: javax.xml.crypto.dsig.DigestMethod.SHA1
	 * @param signatureMethod
	 *            e.g.: SignatureMethod.DSA_SHA1
	 * @throws SigningException
	 */
	static public void genSignature(InputStream streamToSign,
			OutputStream signedStream, Certificate cert, PrivateKey privateKey,
			String canonicalizationMethod, String digestMethod,
			String signatureMethod) throws SigningException {

		final String objectId = "object";

		XMLSignatureFactory fac;
		try {
			fac = XMLSignatureFactory.getInstance("DOM", (Provider) Class
					.forName(providerName).newInstance());
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
				si = fac.newSignedInfo(
						fac.newCanonicalizationMethod(canonicalizationMethod,
								(C14NMethodParameterSpec) null), fac
								.newSignatureMethod(signatureMethod, null),
						Collections.singletonList(ref));
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
				XMLObject obj = fac.newXMLObject(Collections
						.singletonList(content), objectId, null, null);

				DOMSignContext dsc = new DOMSignContext(privateKey, dummyDoc);

				XMLSignature signature = fac.newXMLSignature(si, ki,
						Collections.singletonList(obj), null, null);
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
