package com.utils;

import java.io.ByteArrayInputStream;
import java.io.StringReader;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PublicKey;
import java.util.Date;

import org.eclipse.higgins.saml2idp.saml2.SAMLAssertion;
import org.eclipse.higgins.saml2idp.saml2.SAMLAuthnRequest;
import org.eclipse.higgins.saml2idp.saml2.SAMLAuthnStatement;
import org.eclipse.higgins.saml2idp.saml2.SAMLConditions;
import org.eclipse.higgins.saml2idp.saml2.SAMLConstants;
import org.eclipse.higgins.saml2idp.saml2.SAMLResponse;
import org.eclipse.higgins.saml2idp.saml2.SAMLSubject;


import com.utils.KeyTool;
import com.utils.execeptions.Utils_Exception_Unable_ToRead_File;

public class SAML2Helpers {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		KeyPair keys;
		try {
			keys = KeyTool.getFromKeyStore_KeyPair();
			String Assertion = createResponse_WithUserSpec("stef", "WLBank", keys);
			System.out.println(Assertion);
			String response = verifyResponse(Assertion, true, keys.getPublic());
			System.out.println(response);
		} catch (Utils_Exception_Unable_ToRead_File e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static KeyPair initKeys(){
		KeyPair keys = new KeyTool().getKeyPairFromKeyStore("verystef", "bigsecret");
		if(keys == null){
			 KeyPairGenerator  keyGen = null;
			    try {
			    	  keyGen = KeyPairGenerator.getInstance("RSA");  
				} catch (Exception e) {
					// TODO: handle exception
				}
			
			    
			 keyGen.initialize(1024);  
			    
			 keys = keyGen.generateKeyPair();
			 System.out.println("ERROR");
		}
		return keys;
		 
		 
	}
	
	public static String verifyResponse(String samlResponse_Base64Encoded, boolean isEncoded, PublicKey pubKey){
		System.out.println("VERIFY RESPONSE");
		//String response="";
		byte [] BASE64String = null;
		if(isEncoded) 
			BASE64String = Base64.decode(samlResponse_Base64Encoded);
		else
			BASE64String = samlResponse_Base64Encoded.getBytes();
		
		try {
			SAMLResponse samlResponse = new SAMLResponse(new ByteArrayInputStream(BASE64String));
			try {
				samlResponse.verify(pubKey);
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
			System.out.println("VERIFICATION OK");;
			
			String statusCodeValue = samlResponse.getStatusCodeValue();
			System.out.println("SAML2 Response StatusCode: " + statusCodeValue);

			SAMLAssertion samlAssertion = samlResponse.getSAMLAssertion();
			SAMLSubject samlSubject = samlAssertion.getSubject(); 
			String nameId = samlSubject.getNameID();

			System.out.println("SAML2 Response NameID: " + nameId);

			if (! statusCodeValue.equals(SAMLConstants.STATUSCCODE_SUCCESS)) {

				System.out.println("User NOT successfully logged in.");
				
				return "FAILED";
			}

			// Log in.
			System.out.println("User successfully logged in.");
			return nameId;
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
		return "FAILED";
	}
	
	public static String createResponse(String request, KeyPair key) {

		{
			byte[] BASE64String = ZIP.decompress(Base64.decode(request));

			try {
				SAMLAuthnRequest samlRequest = new SAMLAuthnRequest(
						new ByteArrayInputStream(BASE64String));
				System.out.println("THE ISSUER : " + samlRequest.getIssuer());
			} catch (Exception e) {
				// TODO: handle exception
				System.out.println("CREATE RESPONSE : IMPOSSIBLE LIRE REQUETE");
			}

		}

		SAMLResponse samlResponse = new SAMLResponse();
		samlResponse.setStatusCodeValue(SAMLConstants.STATUSCCODE_SUCCESS);
		samlResponse.setDestination("RSPDestination");
		// samlResponse.setIssuer(Init.getSAML2Issuer());

		SAMLAssertion samlAssertion = new SAMLAssertion(samlResponse
				.getDocument());
		samlAssertion.setIssuer("Issuer");

		samlResponse.setSAMLAssertion(samlAssertion);

		SAMLSubject samlSubject = new SAMLSubject(samlAssertion.getDocument());
		samlSubject.setNameIDFormat(SAMLConstants.NAMEIDFORMAT_EMAILADDRESS);
		samlSubject.setNameID("NameID");
		samlSubject
				.setSubjectConfirmationMethod(SAMLConstants.SUBJECTCONFIRMATIONMETHOD_BEARER);
		samlAssertion.setSAMLSubject(samlSubject);

		SAMLConditions samlConditions = new SAMLConditions(samlAssertion
				.getDocument());
		samlConditions.setNotBefore(new Date(samlAssertion.getIssueInstant()
				.getTime()/* - Init.getSAML2AssertionValidityMillis()) */));
		samlConditions.setNotOnOrAfter(new Date(samlAssertion.getIssueInstant()
				.getTime()/* + Init.getSAML2AssertionValidityMillis() */));
		samlAssertion.setSAMLConditions(samlConditions);

		SAMLAuthnStatement samlAuthnStatement = new SAMLAuthnStatement(
				samlAssertion.getDocument());
		samlAuthnStatement
				.setAuthnContextClassRef(SAMLConstants.AUTHNCONTEXTCLASSREF_PASSWORD);
		samlAssertion.setSAMLAuthnStatement(samlAuthnStatement);

		// Sign it.

		try {

			samlResponse = new SAMLResponse(new StringReader(samlResponse
					.dump()));
			samlResponse.sign(key.getPrivate(), key.getPublic());
		} catch (Exception ex) {

			ex.printStackTrace();

		}
		String response = "";
		// We need to convert the SAML message to Base64.
		try {
			String samlString = samlResponse.dump();
			/*
			 * Map attributes = new HashMap ();
			 * 
			 * attributes.put("SAMLResponse", samlString);
			 */
			response = samlString;

			// Display the auto-redirect form.

			System.out.println(samlString);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return Base64.encode(response.getBytes());
	}
	
	public static String createResponse_WithUserSpec(String userId,
												     String l_strAudience,
													 KeyPair key) {

		

		SAMLResponse samlResponse = new SAMLResponse();
		samlResponse.setStatusCodeValue(SAMLConstants.STATUSCCODE_SUCCESS);
		samlResponse.setDestination("RSPDestination");
		// samlResponse.setIssuer(Init.getSAML2Issuer());

		SAMLAssertion samlAssertion = new SAMLAssertion(samlResponse
				.getDocument());
		samlAssertion.setIssuer("MAP_v0.1");

		samlResponse.setSAMLAssertion(samlAssertion);

		SAMLSubject samlSubject = new SAMLSubject(samlAssertion.getDocument());
		samlSubject.setNameIDFormat(SAMLConstants.NAMEIDFORMAT_EMAILADDRESS);
		samlSubject.setNameID(userId);		
		samlSubject.setSubjectConfirmationMethod(SAMLConstants.SUBJECTCONFIRMATIONMETHOD_BEARER);
		samlAssertion.setSAMLSubject(samlSubject);

		SAMLConditions samlConditions = new SAMLConditions(samlAssertion
				.getDocument());
		samlConditions.setNotBefore(new Date(samlAssertion.getIssueInstant()
				.getTime()/* - Init.getSAML2AssertionValidityMillis()) */));
		samlConditions.setNotOnOrAfter(new Date(samlAssertion.getIssueInstant()
				.getTime()/* + Init.getSAML2AssertionValidityMillis() */));
		samlAssertion.setSAMLConditions(samlConditions);

		SAMLAuthnStatement samlAuthnStatement = new SAMLAuthnStatement(
				samlAssertion.getDocument());
		samlAuthnStatement
				.setAuthnContextClassRef(SAMLConstants.AUTHNCONTEXTCLASSREF_PASSWORD);
		samlAssertion.setSAMLAuthnStatement(samlAuthnStatement);

		// Sign it.

		try {

			samlResponse = new SAMLResponse(new StringReader(samlResponse
					.dump()));
			samlResponse.sign(key.getPrivate(), key.getPublic());
		} catch (Exception ex) {

			ex.printStackTrace();

		}
		String response = "";
		// We need to convert the SAML message to Base64.
		try {
			String samlString = samlResponse.dump();
			/*
			 * Map attributes = new HashMap ();
			 * 
			 * attributes.put("SAMLResponse", samlString);
			 */
			response = samlString;

			// Display the auto-redirect form.

			//System.out.println(samlString);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return Base64.encode(response.getBytes());
	}
}
