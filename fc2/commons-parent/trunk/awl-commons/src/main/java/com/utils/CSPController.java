package com.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Enumeration;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.utils.execeptions.XMLSign_Exception;


public class CSPController {
	
	static Logger log=  new Logger(CSPController.class);
	
	public static void trace(Object msg){
		log.trace(msg);
	}
	
	//keyusages
	public static final int USAGE_DIGITAL_SIGNATURE = 0;
	public static final int USAGE_NON_REPUDIATION = 1;
	public static final int USAGE_KEY_ENCIPHERMENT = 2;
	public static final int USAGE_DATA_ENCIPHERMENT = 3;
	public static final int USAGE_KEY_AGREEMENT = 4;
	public static final int USAGE_KEY_CERT_SIGN = 5;
	public static final int USAGE_CRL_SIGN = 6;
	public static final int USAGE_ENCIPHER_ONLY = 7;
	public static final int USAGE_DECIPHER_ONLY = 8;
	
	public static final String CSP_USER_CERTIFICATES = "Windows-MY";
	public static final String CSP_ALL_TRUSTED_CERTIFICATES = "Windows-ROOT";
	
	private static String whichCertificates = CSP_USER_CERTIFICATES;
	
	static KeyStore ks = null;
	
	private Certificate currentCertificate = null;
	
	static{		
		resetStore();
	}
	
	public CSPController(String whichCertificates) {
		this.whichCertificates = whichCertificates;
	}
	
	public static void resetStore(){
		try {
			ks = null;
			System.gc();
			ks = KeyStore.getInstance(CSPController.whichCertificates);
			ks.load(null, null) ;
		} catch (KeyStoreException e) {
			trace("Impossible to load the KeyStore");
		} catch (NoSuchAlgorithmException e) {
			trace("Impossible to load the KeyStore");
		} catch (CertificateException e) {
			trace("Impossible to load the KeyStore");
		} catch (IOException e) {
			trace("Impossible to load the KeyStore");
		}		
	}
		
	public CSPController() {}

	public void reset(){
		trace("reset called");
		try {
			ks = null;
			currentCertificate = null;
			System.gc();
			ks = KeyStore.getInstance(this.whichCertificates);
			ks.load(null, null) ;
		} catch (KeyStoreException e) {
			trace("Impossible to load the KeyStore");
		} catch (NoSuchAlgorithmException e) {
			trace("Impossible to load the KeyStore");
		} catch (CertificateException e) {
			trace("Impossible to load the KeyStore");
		} catch (IOException e) {
			trace("Impossible to load the KeyStore");
		}		
	}
	
	public X509Certificate getFirstSatisfyingCertificate(String userDN, String issuerDN, int[] usages) throws KeyStoreException {
		trace("Requested certificate : userDN="+userDN+", issuerDN="+issuerDN+", usages ="+usages);
		Enumeration en = ks.aliases() ;

		while (en.hasMoreElements()) {
			String aliasKey = (String)en.nextElement() ;
			
			X509Certificate currentCert = (X509Certificate) ks.getCertificate(aliasKey);
			if(isCertificateSatisfyingRequest(currentCert, userDN, issuerDN, usages)) {
				System.out.println("found satisfying certificate (alias = "+aliasKey+")");
				this.currentCertificate = currentCert;
				return currentCert;
			}
		}
		
		return null;
	}
	
	private boolean isCertificateSatisfyingRequest(X509Certificate certificate, String requestedUserDN, String requestedIssuerDN, int[] requestedUsages) {
		if(!certificate.getIssuerDN().getName().contains(requestedIssuerDN)) return false;
		if(!certificate.getSubjectDN().getName().contains(requestedUserDN)) return false;
		
		if(requestedUsages != null) {
			boolean[] keyUsage = certificate.getKeyUsage();
			for(int i=0; i<requestedUsages.length; i++){
				if(!keyUsage[requestedUsages[i]]){
					return false;
				}
			}
		}
		return true;
	}
	
	public static void main(String[] args) {
//		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
//		dbf.setNamespaceAware(true);
//		
//		String str ="<a>pouet</a>";
//		InputStream in = new ByteArrayInputStream(str.getBytes());	
//		
//		
//		Document doc;
//		try {
//			doc = dbf.newDocumentBuilder().parse(in);
//			System.out.println(doc);
//		} catch (SAXException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (ParserConfigurationException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		
		
		
		CSPController csp = new CSPController();
		csp.reset();
		try {
			
			X509Certificate cert = csp.getFirstSatisfyingCertificate("robert", "", new int[]{USAGE_DIGITAL_SIGNATURE, USAGE_NON_REPUDIATION});//CN=FC2 subAC bancaire Client
//			System.out.println(cert.getSubjectDN().getName());
//			csp.getCertificate(, "", new int[]{RequestCertificate.USAGE_AUTHENTICATION});
			System.out.println("certif = "+Base64.encode(cert.getEncoded()));
			
//			String toSign = "pouet";
//			
//			byte[] signedData = csp.signData(ks.getCertificateAlias(cert), toSign);
//			System.out.println("signed data : " + csp.bytes2String(signedData));
//			
//			Signature sign = Signature.getInstance("SHA1withRSA");
//			sign.initVerify(cert.getPublicKey());
//			sign.update(toSign.getBytes());
//			System.out.println("sign = "+sign.verify(signedData));
		} catch (KeyStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		
//		CSPController csp = new CSPController();
//		csp.reset();
//		
//		try {
//			
//			X509Certificate cert = csp.getFirstSatisfyingCertificate("C=FR, O=fc2consortium, OU=atosorigin, CN=François-Julien Ritaine", "", new int[]{USAGE_DIGITAL_SIGNATURE, USAGE_NON_REPUDIATION});//CN=FC2 subAC bancaire Client
//			String toSign = "<test>pouet</test>";
//			Document signed = csp.signXml(ks.getCertificateAlias(cert), toSign);
//			System.out.println("signed xml = " + xml2String(signed));
//
//			//verif
//			XMLSign signer = new XMLSign();
//			boolean check = signer.verifySignature(signed, cert.getPublicKey());
//			System.out.println("check = "+check);
//		} catch (KeyStoreException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}
	
	
	static public PrivateKey getPrivateKeyFromAlias(String alias) throws UnrecoverableKeyException, KeyStoreException, NoSuchAlgorithmException{
//		trace("getPrivateKey");
		return (PrivateKey)ks.getKey(alias,null);
	}
	
	public Document signXml(String xmlData) throws Exception {
		if(currentCertificate == null) throw new Exception("No certificate found to sign the data");
		else {
			return signXml(ks.getCertificateAlias(this.currentCertificate), xmlData);
		}
	}
	
	public Document signXml(String certificateAlias, String xmlData) {
		trace("Data Signing request : data = " + xmlData + ", with certificate alias = " + certificateAlias);
		
		Document toSign = null;
		try {
			InputStream in = new ByteArrayInputStream(xmlData.getBytes());				
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			dbf.setNamespaceAware(true);
			toSign = dbf.newDocumentBuilder().parse(in);
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
		
		XMLSign xmlSign = null;
		try {
			xmlSign = new XMLSign();
			xmlSign.setCertificate((X509Certificate) ks.getCertificate(certificateAlias), certificateAlias);
			xmlSign.setDocument(toSign);
			xmlSign.sign();
			
			
		} catch (XMLSign_Exception e1) {
			e1.printStackTrace();
		} catch (KeyStoreException e) {
			e.printStackTrace();
		}
		
		
		trace("Computed signature: " + xml2String(xmlSign.getDocument()));
		Document signedDocument = xmlSign.getDocument();
		return signedDocument;
	}
	
	public byte[] signData(String b64Data) throws Exception {
		if(currentCertificate == null) throw new Exception("No certificate found to sign the data");
		else {
			return signData(ks.getCertificateAlias(this.currentCertificate), b64Data);
		}
	}
	
	public byte[] signData(String certificateAlias, String b64Data) {
		trace("Data Signing request : data = " + b64Data
				+ ", with certificate alias = " + certificateAlias);
		byte[] signedData = null;
		try {
			PrivateKey pKey = (PrivateKey) ks.getKey(certificateAlias, null);

			Signature signature = Signature.getInstance("SHA1withRSA");
			
			signature.initSign(pKey);
			signature.update(Base64.decode(b64Data));//.getBytes());

			signedData = signature.sign();
		} catch (UnrecoverableKeyException e) {
			e.printStackTrace();
		} catch (KeyStoreException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (SignatureException e) {
			e.printStackTrace();
		}
		trace(signedData.hashCode());
		return signedData;
	}
	
//	
//	public byte[] signData(byte [] data) throws PKIHandler_Exeception{
//		
//		//trace("    Certificat : " + curCertificate.toString() ) ;
//		PrivateKey key;
//		try {
//			key = (PrivateKey)ks.getKey(curAlias,null);//"0000".toCharArray());
//			//SUtil.printBufferBytes(key.getEncoded());
//			//Certificate[] chain = ks.getCertificateChain(aliasKey);
//			trace("Signature initialization");
//			Signature sign = Signature.getInstance("SHA1withRSA");
//			sign.initSign(key);		
//			sign.update(data);
//	      
//	      byte [] res = sign.sign();
//	      return res;
//		} catch (UnrecoverableKeyException e) {
//			throw new PKIHandler_Exeception(e.getMessage());
//		} catch (KeyStoreException e) {
//			throw new PKIHandler_Exeception(e.getMessage());
//		} catch (NoSuchAlgorithmException e) {
//			throw new PKIHandler_Exeception(e.getMessage());
//		} catch (SignatureException e) {
//			throw new PKIHandler_Exeception(e.getMessage());
//		} catch (InvalidKeyException e) {
//			throw new PKIHandler_Exeception(e.getMessage());
//		}
//		
//		
//	}
	
	public String getWhichCertificates() {
		return whichCertificates;
	}

	public void setWhichCertificates(String whichCertificates) {
		this.whichCertificates = whichCertificates;
		this.reset();
	}

	public Certificate getCurrentCertificate() {
		return currentCertificate;
	}

	/** UTILS **/
	static String bytes2String(byte[] b) {
		String result = "";
		for (int i = 0; i < b.length; i++) {
			String s = Integer.toHexString(b[i] & (0xff)).toUpperCase();
			if (s.length() == 1)
				s = "0" + s;
			result += s;
		}
		return result;
	}


	static byte[] string2Bytes(String s) {
		byte b[] = new byte[s.length() / 2];
		for (int i = 0; i < b.length; i++) {
			int j = i * 2;
			b[i] = Integer.valueOf(s.substring(j, j + 2), 16).byteValue();
		}
		return b;
	}
	
	public static Document String2XML(String xml) {
		InputStream in = new ByteArrayInputStream(xml.getBytes());				
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setNamespaceAware(true);
		
		try {
			return dbf.newDocumentBuilder().parse(in);
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public static String xml2String(Document doc) {
		OutputStream os = new ByteArrayOutputStream();
		TransformerFactory tf = TransformerFactory.newInstance();
		Transformer trans;
		
		try {
			trans = tf.newTransformer();
			trans.transform(new DOMSource(doc), new StreamResult(os));
		} catch (TransformerConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return os.toString();
	}
	
	public static X509Certificate X509fromB64(String b64EncodedX509Certificate){// throws CryptoException {
        StringBuffer sb = new StringBuffer("-----BEGIN CERTIFICATE-----\n");
        sb.append(b64EncodedX509Certificate);
        sb.append("\n-----END CERTIFICATE-----\n");

        ByteArrayInputStream bis = new ByteArrayInputStream(sb.toString().getBytes());
        CertificateFactory cf;
        try {
            cf = CertificateFactory.getInstance("X509");
        } catch (CertificateException e) {
        	System.out.println("Error creating X509 CertificateFactory");	
        	return null;
        }
        try {
            X509Certificate certificate = (X509Certificate)cf.generateCertificate(bis);
            return certificate;
        } catch (CertificateException e) {
        	// in case that the base64 coding is not compliant
        	byte[] decodedBase64 = Base64.decode(b64EncodedX509Certificate);
        	String b64 = Base64.encode(decodedBase64);
            X509Certificate certificate = null;
			try {
		        sb = new StringBuffer("-----BEGIN CERTIFICATE-----\n");
		        sb.append(b64);
		        sb.append("\n-----END CERTIFICATE-----\n");

		        bis = new ByteArrayInputStream(sb.toString().getBytes());
				certificate = (X509Certificate)cf.generateCertificate(bis);
			} catch (CertificateException e1) {
	            System.out.println("Error creating X509Certificate from base64-encoded String");			
	           }
           return certificate;
			
			
        }
	}
	
}
