/*
 *     Copyright (c) Microsoft. All rights reserved.
 *     This code is licensed under the modified BSD License.
 *     THIS CODE IS PROVIDED *AS IS* WITHOUT WARRANTY OF
 *     ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING ANY
 *     IMPLIED WARRANTIES OF FITNESS FOR A PARTICULAR
 *     PURPOSE, MERCHANTABILITY, OR NON-INFRINGEMENT.
 */
package com.microsoft.uprove;

import java.io.IOException;
import java.util.Arrays;
import com.microsoft.uprove.FieldZq.ZqElement;

/**
 * Class to generate and verify presentation proofs.
 */
public class PresentationProtocol {

    /**
     * Private constructor to prevent instantiation.
     */
    private PresentationProtocol() {
    	super();
    }

    /**
     * Generates a presentation proof.
     * @param ip the issuer parameters under which the U-Prove token was issued.
     * @param disclosed the ordered list of disclosed token attribute indices.
     * @param m the protocol message.
     * @param upkt the U-Prove key and token to use.
     * @param attributes the list of all token attributes.
     * @return a presentation proof.
     * @throws IOException if an argument is malformed.
     */
	public static PresentationProof generatePresentationProof(IssuerParameters ip, int[] disclosed, byte[] m, UProveKeyAndToken upkt, byte[][] attributes) throws IOException {
		return generatePresentationProof(ip, disclosed, m, upkt, attributes, null);
	}

    /**
     * Generates a presentation proof.
     * @param ip the issuer parameters under which the U-Prove token was issued.
     * @param disclosed the ordered list of disclosed token attribute indices.
     * @param m the protocol message.
     * @param upkt the U-Prove key and token to use.
     * @param attributes the list of all token attributes.
	 * @param preGenW a list of pre-generated <code>w</code> values.
     * @return a presentation proof.
     * @throws IOException if an argument is malformed.
     */
	public static PresentationProof generatePresentationProof(IssuerParameters ip, int[] disclosed, byte[] m, UProveKeyAndToken upkt, byte[][] attributes, byte[][] preGenW) throws IOException {
		
			IssuerParametersInternal ipi = IssuerParametersInternal.generate(ip);  
			UProveTokenInternal upti = UProveTokenInternal.generate(ipi, upkt.getToken());
			
			PrimeOrderGroup Gq = ipi.getGroup();
			FieldZq Zq = Gq.getZq();
			ZqElement[] x = ProtocolHelper.computeXArray(ipi, attributes, upti.getTokenInformation());
			int n = ipi.getEncodingBytes().length;
			int nUndisclosed = n - disclosed.length; 
			int[] undisclosed = ProtocolHelper.getUndisclosedIndices(n, disclosed);
			
			ZqElement[] w;
			if (preGenW == null) {
				w = Zq.getRandomElements(nUndisclosed+1, false);
			} else {
				if (preGenW.length != nUndisclosed + 1) {
					throw new IllegalArgumentException("Expected size for preGenW is " + nUndisclosed + ", actual size is " + preGenW.length);
				}
				w = ProtocolHelper.getZqElementArray(Zq, preGenW);
			}

			GroupElement[] bases = new GroupElement[nUndisclosed + 1];
			bases[0] = upti.getPublicKey();
			int bIndex = 1;
			GroupElement[] g = ipi.getPublicKey();
			for (int i=0; i<nUndisclosed; i++) {
				bases[bIndex++] = g[undisclosed[i]];
			}
			
			HashFunction H = ipi.getHashFunction();
			H.update(ProtocolHelper.computeProduct(bases, w));
			byte[] a = H.getByteDigest();
			
			byte[][] disclosedAttributes = new byte[disclosed.length][];
			ZqElement[] disclosedX = new ZqElement[disclosed.length];
			for (int i=0; i<disclosed.length; i++) {
				disclosedAttributes[i] = attributes[disclosed[i]-1]; // attributes array is zero-based
				disclosedX[i] = x[disclosed[i]];
			}
			
			ZqElement c = ProtocolHelper.genChallenge(ipi, upti, a, m, disclosed, disclosedX);
			
			ZqElement r0 = c.multiply(Zq.getPositiveElement(upkt.getTokenPrivateKey())).add(w[0]);
			ZqElement[] r = new ZqElement[nUndisclosed];
			for (int i=0; i<nUndisclosed; i++) {
				r[i] = c.negate().multiply(x[undisclosed[i]]).add(w[i+1]);
			}
			
			return new PresentationProof(disclosedAttributes, a, r0.toByteArray(), ProtocolHelper.getEncodedArray(r));
		}

	/**
	 * Verifies a presentation proof.
	 * @param ip the issuer parameters under which the U-Prove token was issued.
     * @param disclosed the ordered list of disclosed token attribute indices.
     * @param m the protocol message.
	 * @param upt the U-Prove token.
	 * @param pp the presentation proof.
	 * @throws InvalidProofException if the proof is invalid.
	 * @throws IOException if an argument is malformed.
	 */
	public static void verifyPresentationProof(IssuerParameters ip, int[] disclosed, byte[] m, UProveToken upt, PresentationProof pp) throws InvalidProofException, IOException {
		
		IssuerParametersInternal ipi = IssuerParametersInternal.generate(ip);
		UProveTokenInternal upti = UProveTokenInternal.generate(ipi, upt);
		
		// arg validation
		if (disclosed.length != pp.getDisclosedAttributes().length) {
			throw new InvalidProofException("Mismatch in number of disclosed attributes");
		}
		if (!Arrays.equals(upti.getIssuerParametersUID(), ipi.getParametersUID())) {
			throw new IllegalArgumentException("Issuer parameters UID does not match the one referenced in the token.");
		}
		if (!ProtocolHelper.isTokenSignatureValid(ipi, upti)) {
			throw new InvalidProofException("token signature is invalid.");
		}
	
		// [1, x_d_1, ..., x_d_k, x_t]
		ZqElement[] disclosedX = ProtocolHelper.computeXArray(ipi, disclosed, pp.getDisclosedAttributes(), upti.getTokenInformation());
		ZqElement c = ProtocolHelper.genChallenge(ipi, upti, pp.getA(), m, disclosed, (ZqElement[]) Arrays.copyOfRange(disclosedX, 1, disclosedX.length-1));
		
		GroupElement[] g = ipi.getPublicKey();
		GroupElement[] disclosedG = new GroupElement[disclosed.length + 2];
		// g_0
		disclosedG[0] = g[0];
		// g_t
		disclosedG[disclosedG.length-1] = g[g.length-1];
		for (int i=0; i<disclosed.length; i++) {
			disclosedG[i+1] = g[disclosed[i]];
		}
		GroupElement temp1 = ProtocolHelper.computeProduct(disclosedG, disclosedX).exponentiate(c.negate());
		
		PrimeOrderGroup Gq = ipi.getGroup();
		FieldZq Zq = Gq.getZq();
		int[] undisclosed = ProtocolHelper.getUndisclosedIndices(g.length-2, disclosed);
		GroupElement[] bases = new GroupElement[undisclosed.length + 1];
		bases[0] = upti.getPublicKey();
		for (int i=0; i<undisclosed.length; i++) {
			bases[i+1] = g[undisclosed[i]];
		}
		
		ZqElement[] combinedR = new ZqElement[undisclosed.length + 1];
		combinedR[0] = Zq.getPositiveElement(pp.getR0());
		ZqElement r[] = ProtocolHelper.getZqElementArray(Zq, pp.getR());
		for (int i=0; i<r.length; i++) {
			combinedR[i+1] = r[i];
		}
		GroupElement temp2 = ProtocolHelper.computeProduct(bases, combinedR);
		
		HashFunction H = ipi.getHashFunction();
		H.update(temp1.multiply(temp2));
		byte[] a = H.getByteDigest();
		if (!Arrays.equals(pp.getA(), a)) {
			throw new InvalidProofException("proof is invalid.");
		}
	}

}
