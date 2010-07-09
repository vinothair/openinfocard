package com.utils;

import java.security.InvalidAlgorithmParameterException;
import java.security.Key;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.crypto.MarshalException;
import javax.xml.crypto.dsig.CanonicalizationMethod;
import javax.xml.crypto.dsig.DigestMethod;
import javax.xml.crypto.dsig.Reference;
import javax.xml.crypto.dsig.SignatureMethod;
import javax.xml.crypto.dsig.SignedInfo;
import javax.xml.crypto.dsig.Transform;
import javax.xml.crypto.dsig.XMLSignature;
import javax.xml.crypto.dsig.XMLSignatureException;
import javax.xml.crypto.dsig.XMLSignatureFactory;
import javax.xml.crypto.dsig.dom.DOMSignContext;
import javax.xml.crypto.dsig.dom.DOMValidateContext;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import javax.xml.crypto.dsig.keyinfo.KeyInfoFactory;
import javax.xml.crypto.dsig.keyinfo.X509Data;
import javax.xml.crypto.dsig.spec.C14NMethodParameterSpec;
import javax.xml.crypto.dsig.spec.TransformParameterSpec;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import com.utils.execeptions.XMLSign_Exception;

public class XMLSign {
	static Logger log = new Logger(XMLSign.class);
	public static void trace(Object msg){
		log.trace(msg);
	}
	
	XMLSignatureFactory fac = XMLSignatureFactory.getInstance("DOM");
	KeyInfo ki = null;
	String alias;
	DOMSignContext dsc;
	SignedInfo si;
	Document doc;
	
	public XMLSign() throws XMLSign_Exception {
		initEngine();
	}
	
	protected void initEngine() throws XMLSign_Exception {		
		// Create a Reference to the enveloped document (in this case,
		// you are signing the whole document, so a URI of "" signifies
		// that, and also specify the SHA1 digest algorithm and
		// the ENVELOPED Transform.
		Reference ref;
		try {
			ref = fac.newReference
			 ("", fac.newDigestMethod(DigestMethod.SHA1, null),
			  Collections.singletonList
			   (fac.newTransform
			    (Transform.ENVELOPED, (TransformParameterSpec) null)),
			     null, null);
		} catch (NoSuchAlgorithmException e) {
			throw new XMLSign_Exception(e.getCause());
		} catch (InvalidAlgorithmParameterException e) {
			throw new XMLSign_Exception(e.getCause());
		}

		// Create the SignedInfo.
		try {
			si = fac.newSignedInfo
			 (fac.newCanonicalizationMethod
			  (CanonicalizationMethod.INCLUSIVE,
			   (C14NMethodParameterSpec) null),
			    fac.newSignatureMethod(SignatureMethod.RSA_SHA1, null),
			     Collections.singletonList(ref));
		} catch (NoSuchAlgorithmException e) {
//			throw new WodException("RSA_SHA1", XMLSign.class, e, "initEngine()");
			throw new XMLSign_Exception(e.getCause());
		} catch (InvalidAlgorithmParameterException e) {
//			throw new WodException("RSA_SHA1", XMLSign.class, e, "initEngine()");
			throw new XMLSign_Exception(e.getCause());
		}
	}
	
	public void setCertificate(X509Certificate cert,String alias){
		// = certificates.get(reqAuth.getRequestName());
		// Create the KeyInfo containing the X509Data.
		KeyInfoFactory kif = fac.getKeyInfoFactory();
		List x509Content = new ArrayList();
		x509Content.add(cert.getSubjectX500Principal().getName());
		x509Content.add(cert);
		X509Data xd = kif.newX509Data(x509Content);
		ki = kif.newKeyInfo(Collections.singletonList(xd));
		this.alias = alias;
	}
	
	public void setDocument(Document doc) {
		this.doc = doc;
	}
	
	public boolean verifySignature(Document signedDoc, Key validatingKey) {
//		keySelector.reset();
		NodeList nl = signedDoc.getElementsByTagNameNS(XMLSignature.XMLNS, "Signature");
		if (nl.getLength() == 0) return false;
			
		// Create a DOMValidateContext and specify a KeySelector
		// and document context.
		DOMValidateContext valContext = new DOMValidateContext(validatingKey, nl.item(0));
	
		// Unmarshal the XMLSignature.
		XMLSignature signature2;
		boolean coreValidity = false;
		try {
			signature2 = fac.unmarshalXMLSignature(valContext);
			coreValidity = signature2.validate(valContext);
			
		} catch (MarshalException e) {
			e.printStackTrace();
		} catch (XMLSignatureException e) {
			e.printStackTrace();
		}
		return coreValidity;
	}
//	public WodaSignatureResult verifySignature(X509KeySelector keySelector) throws WodException{
//		keySelector.reset();
//		NodeList nl =
//	    doc.getElementsByTagNameNS(XMLSignature.XMLNS, "Signature");
//		if (nl.getLength() == 0) {
//			trace(WodException.ERR_XML_NOSIGN);
//			throw new WodException(WodException.ERR_XML_NOSIGN, XMLSign.class, null, "verifySignature()");
//		}
//
//		// Create a DOMValidateContext and specify a KeySelector
//		// and document context.
//		DOMValidateContext valContext = new DOMValidateContext
//		    (keySelector, nl.item(0));
//	
//		// Unmarshal the XMLSignature.
//		XMLSignature signature2;
//		boolean coreValidity;
//		try {
//			signature2 = fac.unmarshalXMLSignature(valContext);
//			coreValidity = signature2.validate(valContext);
//			return new WodaSignatureResult(keySelector.getUsedCertificate(), keySelector.getACCertificate(), coreValidity);
//		} catch (MarshalException e) {
//			throw new WodException("unmarshall", XMLSign.class, e, "verifySignature()");
//		} catch (XMLSignatureException e) {
//			throw new WodException("validate", XMLSign.class, e, "verifySignature()");
//		}
//		
//	}
	
	public void sign() throws XMLSign_Exception {
		// Create a DOMSignContext and specify the RSA PrivateKey and
		// location of the resulting XMLSignature's parent element.
		try {
			dsc = new DOMSignContext(CSPController.getPrivateKeyFromAlias(alias), doc.getDocumentElement());
		} catch (UnrecoverableKeyException e) {
			throw new XMLSign_Exception(e.getCause());
		} catch (KeyStoreException e) {
			throw new XMLSign_Exception(e.getCause());
		} catch (NoSuchAlgorithmException e) {
			throw new XMLSign_Exception(e.getCause());
		}
		// Create the XMLSignature, but don't sign it yet.
		XMLSignature signature = fac.newXMLSignature(si, ki);

		// Marshal, generate, and sign the enveloped signature.
		try {
			signature.sign(dsc);
		} catch (MarshalException e) {
			throw new XMLSign_Exception(e.getCause());
		} catch (XMLSignatureException e) {
			throw new XMLSign_Exception(e.getCause());
		}
	}
	
	public Document getDocument(){
		return doc;
	}

}
