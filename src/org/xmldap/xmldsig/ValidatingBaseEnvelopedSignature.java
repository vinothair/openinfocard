package org.xmldap.xmldsig;

import java.io.IOException;
import java.math.BigInteger;
import java.security.PrivateKey;

import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Nodes;
import nu.xom.ParsingException;
import nu.xom.XPathContext;
import nu.xom.XPathException;

import org.xmldap.crypto.CryptoUtils;
import org.xmldap.exceptions.CryptoException;
import org.xmldap.util.Base64;
import org.xmldap.ws.WSConstants;

public class ValidatingBaseEnvelopedSignature extends BaseEnvelopedSignature {

    protected ValidatingBaseEnvelopedSignature(KeyInfo keyInfo, PrivateKey privateKey) {
    	super(keyInfo, privateKey);
    }

	/**
	 * @param xmlDoc
	 * @param signedInfoCanonicalBytes
	 * @param signatureValue
	 * @param mod
	 * @param exp
	 * @param digest
	 * @return
	 * @throws CryptoException
	 */
	public static boolean validateRSA(Element root, byte[] signedInfoCanonicalBytes, String signatureValue, String mod, String exp, String digest) throws CryptoException {
		// WEVE GOT:
		// byte[] signedInfoCanonicalBytes
		// String signatureValue
		// byte[] digestBytes
		// String digest
		// String mod
		// String exp

		// WE now have the digest, and the signing key. Let's validate the
		// REFERENCES:
		String b64EncodedDigest = digestElement(root);

		if (!digest.equals(b64EncodedDigest)) {

			System.out
					.println("Digest of the Reference did not match the provided Digest.  Exiting.");
			return false;

		}

		// WEVE GOT:
		// byte[] signedInfoCanonicalBytes
		// String signatureValue
		// byte[] clearTextKey
		// byte[] digestBytes
		// String digest
		// String mod
		// String exp

		BigInteger modulus = new BigInteger(1, Base64.decode(mod));
		BigInteger exponent = new BigInteger(1, Base64.decode(exp));
		return CryptoUtils.verify(signedInfoCanonicalBytes, Base64
				.decode(signatureValue), modulus, exponent);
	}

	public static boolean validate(String toValidate) throws CryptoException {

		Builder parser = new Builder();
		Document assertion = null;
		try {
			assertion = parser.build(toValidate, "");
			return validate(assertion);
		} catch (ParsingException e) {
			throw new CryptoException(e);
		} catch (IOException e) {
			throw new CryptoException(e);
		}

	}

	public static boolean validate(Document xmlDoc) throws CryptoException {

		XPathContext thisContext = new XPathContext();
		thisContext.addNamespace("dsig", WSConstants.DSIG_NAMESPACE);

		// OK - on to signature validation - we need to get the SignedInfo
		// Element, and the Signature Element
		byte[] signedInfoCanonicalBytes;
		try {
			Element root = xmlDoc.getRootElement();
			signedInfoCanonicalBytes = EnvelopedSignature.getSignedInfoCanonicalBytes(root);
		} catch (IOException e) {
			throw new CryptoException(e);
		}

		String signatureValue = getFirstValue(xmlDoc, thisContext, "//dsig:SignatureValue");
		if (signatureValue == null) {
			return false;
		}


		// GET THE KEY CIPHERTEXT and DECRYPT
		XPathContext encContext = new XPathContext();
		encContext.addNamespace("enc", WSConstants.ENC_NAMESPACE);
		encContext.addNamespace("dsig", WSConstants.DSIG_NAMESPACE);
		String digest = getFirstValue(xmlDoc, encContext, "//dsig:DigestValue");
		if (digest == null) {
			return false;
		}

		Element root = xmlDoc.getRootElement();

		// And we need to fetch the modulus
		// Nodes modVals =
		// assertion.query("/saml:Assertion/dsig:Signature/dsig:KeyInfo/disg:KeyValue/dsig:RSAKeyValue/dsig:Modulus",
		// thisContext);
		String mod = getFirstValue(xmlDoc, thisContext, "//dsig:Modulus");
		if (mod != null) {
			// System.out.println("Modulus: " + mod);
			// And we need to fetch the exponent
			String exp = getFirstValue(xmlDoc, thisContext, "//dsig:Exponent");
			if (exp == null) {
				return false;
			}

			// System.out.println("Exponent: " + exp);

			return validateRSA(root, signedInfoCanonicalBytes, signatureValue, mod, exp, digest);
		} else {
			// mod == null maybe it is AES instead of RSA
			String base64encodedAndEncryptedAESKey = getFirstValue(xmlDoc, thisContext, "//" + WSConstants.ENC_PREFIX + ":CipherValue");
			if (base64encodedAndEncryptedAESKey != null) {
				System.out.println("Can not validate signature: unsupported method!");
//				return validateAES(root, signedInfoCanonicalBytes, signatureValue, base64encodedAndEncryptedAESKey, privateKey, digest );
				return false;
			} else {
				return false;
			}
		}

	}

	private static boolean validateAES(
			Element root, byte[] signedInfoCanonicalBytes, String signatureValue, String base64encodedAndEncryptedAESKey, PrivateKey privateKey, String digest) throws CryptoException {
		// WEVE GOT:
		// byte[] signedInfoCanonicalBytes
		// String signatureValue
		// byte[] digestBytes
		// String digest
		// String mod
		// String exp

		// WE now have the digest, and the signing key. Let's validate the
		// REFERENCES:
		String b64EncodedDigest = digestElement(root);

		if (!digest.equals(b64EncodedDigest)) {

			System.out
					.println("Digest of the Reference did not match the provided Digest.  Exiting.");
			return false;

		}

		byte[] aeskey = CryptoUtils.decryptRSAOAEP(base64encodedAndEncryptedAESKey, privateKey);
		// Axel: the AES key in SymmetricKeyInfo is actually never used
		return false;
	}
	
	/**
	 * @param xmlDoc
	 * @param thisContext
	 * @return
	 */
	private static String getFirstValue(Document xmlDoc, XPathContext thisContext, String query) {
		try {
			Nodes signatureValues = xmlDoc.query(query,
					thisContext);
			if (signatureValues.size() > 0) {
				Element signatureValueElm = (Element) signatureValues.get(0);
				String signatureValue = signatureValueElm.getValue();
				return signatureValue;
			} else {
				System.out.println("Could not find (" + query + ") in...\n" + xmlDoc.toXML() + "\n");
			}
		} catch (XPathException  e) {
			
		}
		return null;
	}

}
