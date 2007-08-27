package org.xmldap.xmldsig;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.xmldap.ws.WSConstants;

import nu.xom.Element;
import nu.xom.Elements;
import nu.xom.canonical.Canonicalizer;

public class ParsedSignedInfo extends SignedInfo {
	String canonicalizationAlgorithm = null;
	String signatureAlgorithm = null;
	List<ParsedReference> parsedReferences = new ArrayList<ParsedReference>();
	byte[] canonicalBytes = null;
	
	public ParsedSignedInfo(Element signedInfo) throws IOException {
		Element canonicalizationMethod = signedInfo.getFirstChildElement("CanonicalizationMethod", WSConstants.DSIG_NAMESPACE);
		canonicalizationAlgorithm = canonicalizationMethod.getAttributeValue("Algorithm");
		Element signatureMethod = signedInfo.getFirstChildElement("SignatureMethod", WSConstants.DSIG_NAMESPACE);
		signatureAlgorithm = signatureMethod.getAttributeValue("Algorithm");
		Elements references = signedInfo.getChildElements("Reference", WSConstants.DSIG_NAMESPACE);
		for (int i=0; i<references.size(); i++) {
			Element reference = references.get(i);
			ParsedReference parsedReference = new ParsedReference(reference);
			parsedReferences.add(parsedReference);
		}
		canonicalBytes = getCanonicalBytes(signedInfo, canonicalizationAlgorithm);
	}
	
	public String getCanonicalizationAlgorithm() {
		return canonicalizationAlgorithm;
	}
	public String getSignatureAlgorithm() {
		return signatureAlgorithm;
	}
	public List<ParsedReference> getReferences() {
		return parsedReferences;
	}
	public byte[] getCanonicalBytes() {
		return canonicalBytes;
	}
	
	/**
	 * @param signedInfo
	 * @return
	 * @throws IOException
	 */
	private byte[] getCanonicalBytes(Element signedInfo, String canonicalizationAlgorithm) throws IOException {
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		Canonicalizer outputter = new Canonicalizer(stream, canonicalizationAlgorithm);
		outputter.write(signedInfo);
		byte[] bytes = stream.toByteArray();
		stream.close();
		return bytes;
	}
}
