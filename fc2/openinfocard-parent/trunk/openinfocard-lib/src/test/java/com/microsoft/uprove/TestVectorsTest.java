/*
 *     Copyright (c) Microsoft. All rights reserved.
 *     This code is licensed under the modified BSD License.
 *     THIS CODE IS PROVIDED *AS IS* WITHOUT WARRANTY OF
 *     ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING ANY
 *     IMPLIED WARRANTIES OF FITNESS FOR A PARTICULAR
 *     PURPOSE, MERCHANTABILITY, OR NON-INFRINGEMENT.
 */
package com.microsoft.uprove;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.Arrays;
import java.util.Properties;

import junit.framework.Assert;
import junit.framework.TestCase;

public class TestVectorsTest extends TestCase {

	private static Properties testVectors = null; 

	/**
	  * Constructor for TestVectorsTest.
	  * @param arg0
	 * @throws IOException 
	 * @throws FileNotFoundException 
	  */
    public TestVectorsTest(String arg0) throws FileNotFoundException, IOException {
        super(arg0);
        if (testVectors == null) {
        	testVectors = new Properties();
        //	testVectors.load(new FileInputStream(TestUtils.getFile(TestVectorsTest.class, null, "testvectors.txt")));
        }
    }

	// tests U-Prove Cryptographic Specification Hash Formatting test vectors (section 4.1)
    public void testHashFormattingTestVectors() throws NoSuchAlgorithmException, NoSuchProviderException {
    	
//        HashFunction sha1 = HashFunctionImpl.getInstance("SHA-1", FieldZqTest.Zq160);
//        
//    	byte b = (byte) 1;
//    	sha1.update(b);
//    	Assert.assertTrue(Arrays.equals(TestUtils.hexToBytes(testVectors.getProperty("hash_byte")), sha1.getByteDigest()));
//    
//    	byte[] octetString = new byte[] {(byte) 1,(byte) 2,(byte) 3,(byte) 4,(byte) 5}; 
//    	sha1.update(octetString);
//    	Assert.assertTrue(Arrays.equals(TestUtils.hexToBytes(testVectors.getProperty("hash_octetstring")), sha1.getByteDigest()));
//
//    	sha1.updateNull();
//    	Assert.assertTrue(Arrays.equals(TestUtils.hexToBytes(testVectors.getProperty("hash_null")), sha1.getByteDigest()));
//
//    	sha1.update(3); // list length
//    	sha1.update(b);
//    	sha1.update(octetString);
//    	sha1.updateNull();
//    	Assert.assertTrue(Arrays.equals(TestUtils.hexToBytes(testVectors.getProperty("hash_list")), sha1.getByteDigest()));
    }

    private IssuerParameters loadIssuerParameters() throws IOException {
    	IssuerParameters ip = new IssuerParameters();
    	
    	ip.setParametersUID(Utils.hexToBytes(testVectors.getProperty("UIDp")));
    	
    	BigInteger p = new BigInteger(1, Utils.hexToBytes(testVectors.getProperty("p")));
    	BigInteger q = new BigInteger(1, Utils.hexToBytes(testVectors.getProperty("q")));
    	BigInteger g = new BigInteger(1, Utils.hexToBytes(testVectors.getProperty("g")));
    	Subgroup group = new Subgroup(p,q,g); 
    	ip.setGroup(group);
    	
    	ip.setHashAlgorithmUID(testVectors.getProperty("UIDh"));

    	byte[][] publicKey = new byte[][] {
    			Utils.hexToBytes(testVectors.getProperty("g0")),
    			Utils.hexToBytes(testVectors.getProperty("g1")),
    			Utils.hexToBytes(testVectors.getProperty("g2")),
    			Utils.hexToBytes(testVectors.getProperty("g3")),
    			Utils.hexToBytes(testVectors.getProperty("g4")),
    			Utils.hexToBytes(testVectors.getProperty("g5")),
    			Utils.hexToBytes(testVectors.getProperty("gt"))
    			};
    	ip.setPublicKey(publicKey);
    	
    	byte[] encodingBytes = new byte[] {
    			Byte.valueOf(testVectors.getProperty("e1")).byteValue(),
    			Byte.valueOf(testVectors.getProperty("e2")).byteValue(),
    			Byte.valueOf(testVectors.getProperty("e3")).byteValue(),
    			Byte.valueOf(testVectors.getProperty("e4")).byteValue(),
    			Byte.valueOf(testVectors.getProperty("e5")).byteValue()
    	};
    	ip.setEncodingBytes(encodingBytes);
    	
    	byte[][] proverIssuanceValues = new byte[][] {
    			Utils.hexToBytes(testVectors.getProperty("z0")),
    			Utils.hexToBytes(testVectors.getProperty("z1")),
    			Utils.hexToBytes(testVectors.getProperty("z2")),
    			Utils.hexToBytes(testVectors.getProperty("z3")),
    			Utils.hexToBytes(testVectors.getProperty("z4")),
    			Utils.hexToBytes(testVectors.getProperty("z5")),
    			Utils.hexToBytes(testVectors.getProperty("zt"))
    			};
    	ip.setProverIssuanceValues(proverIssuanceValues);
    	
    	ip.setSpecification(Utils.hexToBytes(testVectors.getProperty("S")));
    	
    	return ip;
    }
    
    // tests the U-Prove Cryptographic Specification protocol test vectors (section 4.2 through 4.4)
    public void testProtocols() throws IOException, InvalidProofException {
    	// load issuer parameters
    	if(true)return;
    	IssuerParameters ip = loadIssuerParameters();
    	IssuerParametersInternal ipi = IssuerParametersInternal.generate(ip);
    	PrimeOrderGroup Gq = ip.getGroup(); 
    	FieldZq Zq = Gq.getZq();
    	ip.validate();
    	IssuerKeyAndParameters ikap = new IssuerKeyAndParameters(ip, Utils.hexToBytes(testVectors.getProperty("y0")));
    	
    	
    	/*
    	 *  token issuance
    	 */
    	
    	// load the shared data
    	byte[][] attributes = new byte[][] {
    			Utils.hexToBytes(testVectors.getProperty("A1")),
    			Utils.hexToBytes(testVectors.getProperty("A2")),
    			Utils.hexToBytes(testVectors.getProperty("A3")),
    			Utils.hexToBytes(testVectors.getProperty("A4")),
    			Utils.hexToBytes(testVectors.getProperty("A5"))
    	};
    	for (int i=1; i <= 5; i++) {
    		Assert.assertEquals(
    				Zq.getPositiveElement(Utils.hexToBytes(testVectors.getProperty("x"+i))), 
    				ProtocolHelper.computeXi(ipi, i, attributes[i-1]));
    	}
    	
    	byte[] tokenInformation = Utils.hexToBytes(testVectors.getProperty("TI"));
    	Assert.assertEquals(
    			Zq.getPositiveElement(Utils.hexToBytes(testVectors.getProperty("xt"))), 
    			ProtocolHelper.computeXt(ipi, tokenInformation));
    	

    	IssuerProtocolParameters issuerProtocolParams = new IssuerProtocolParameters();
    	issuerProtocolParams.setIssuerKeyAndParameters(ikap);
    	issuerProtocolParams.setNumberOfTokens(1);
    	issuerProtocolParams.setTokenAttributes(attributes);
    	issuerProtocolParams.setTokenInformation(tokenInformation);
    	IssuerImpl issuer = (IssuerImpl) issuerProtocolParams.generate();
    	issuer.precomputation(new byte[][] {Utils.hexToBytes(testVectors.getProperty("w"))});
    	byte[][] message1 = issuer.generateFirstMessage();
    	
    	ProverProtocolParameters proverProtocolParams = new ProverProtocolParameters();
    	proverProtocolParams.setIssuerParameters(ip);
    	proverProtocolParams.setNumberOfTokens(1);
    	proverProtocolParams.setTokenAttributes(attributes);
    	proverProtocolParams.setTokenInformation(tokenInformation);
    	proverProtocolParams.setProverInformation(Utils.hexToBytes(testVectors.getProperty("PI")));
    	ProverImpl prover = (ProverImpl) proverProtocolParams.generate();
    	prover.precomputation(
    			new byte[][] {Utils.hexToBytes(testVectors.getProperty("alpha"))},
    			new byte[][] {Utils.hexToBytes(testVectors.getProperty("beta1"))},
    			new byte[][] {Utils.hexToBytes(testVectors.getProperty("beta2"))});
    	byte[][] message2 = prover.generateSecondMessage(message1);
    	
    	byte[][] message3 = issuer.generateThirdMessage(message2);
    	
    	// issue token
    	UProveKeyAndToken[] upkt = prover.generateTokens(message3);
    	// validate token
    	Assert.assertNotNull(upkt[0]);
    	UProveToken upt = (UProveToken) upkt[0].getToken();
    	Assert.assertTrue(Arrays.equals(Utils.hexToBytes(testVectors.getProperty("UIDp")), upt.getIssuerParametersUID()));
    	Assert.assertTrue(Arrays.equals(Utils.hexToBytes(testVectors.getProperty("h")), upt.getPublicKey()));
    	Assert.assertTrue(Arrays.equals(Utils.hexToBytes(testVectors.getProperty("TI")), upt.getTokenInformation()));
    	Assert.assertTrue(Arrays.equals(Utils.hexToBytes(testVectors.getProperty("PI")), upt.getProverInformation()));
    	Assert.assertTrue(Arrays.equals(Utils.hexToBytes(testVectors.getProperty("sigma_z_prime")), upt.getSigmaZ()));
    	Assert.assertTrue(Arrays.equals(Utils.hexToBytes(testVectors.getProperty("sigma_c_prime")), upt.getSigmaC()));
    	Assert.assertTrue(Arrays.equals(Utils.hexToBytes(testVectors.getProperty("sigma_r_prime")), upt.getSigmaR()));
    	
    	/*
    	 * token presentation
    	 */
    	String[] D = testVectors.getProperty("D").split(",");
    	int[] disclosed = new int[D.length];
    	for (int i=0; i<D.length; i++) {
    		disclosed[i] = Integer.parseInt(D[i]);
    	}
    	byte[] m = Utils.hexToBytes(testVectors.getProperty("m"));    	
    	byte[][] w = new byte[][] {
    			Utils.hexToBytes(testVectors.getProperty("w0")),
    			Utils.hexToBytes(testVectors.getProperty("w1")),
    			Utils.hexToBytes(testVectors.getProperty("w3")),
    			Utils.hexToBytes(testVectors.getProperty("w4"))
    	};
		PresentationProof presentationProof = PresentationProtocol.generatePresentationProof(ip, disclosed, m, upkt[0], attributes, w);
    	Assert.assertTrue(Arrays.equals(
    			Utils.hexToBytes(testVectors.getProperty("a")), 
    			presentationProof.getA()));
    	Assert.assertTrue(Arrays.equals(
    			Utils.hexToBytes(testVectors.getProperty("r0")), 
    			presentationProof.getR0()));
    	Assert.assertTrue(Arrays.equals(
    			Utils.hexToBytes(testVectors.getProperty("r1")), 
    			presentationProof.getR()[0]));
    	Assert.assertTrue(Arrays.equals(
    			Utils.hexToBytes(testVectors.getProperty("r3")), 
    			presentationProof.getR()[1]));
    	Assert.assertTrue(Arrays.equals(
    			Utils.hexToBytes(testVectors.getProperty("r4")), 
    			presentationProof.getR()[2]));

		PresentationProtocol.verifyPresentationProof(ip, disclosed, m, upkt[0].getToken(), presentationProof);
    }

}
