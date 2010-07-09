/*
 * Copyright (c) 2010, Atos Worldline - http://www.atosworldline.com/
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * The names of the contributors may NOT be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE REGENTS AND CONTRIBUTORS ``AS IS'' AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE REGENTS AND CONTRIBUTORS BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * 
 */
package com.awl.rd.fc2;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;

import org.apache.log4j.Logger;
import org.xmldap.exceptions.KeyStoreException;
import org.xmldap.exceptions.SerializationException;
import org.xmldap.infocard.InfoCard;
import org.xmldap.infocard.TokenServiceReference;
import org.xmldap.infocard.UserCredential;
import org.xmldap.infocard.policy.SupportedClaim;
import org.xmldap.infocard.policy.SupportedClaimTypeList;
import org.xmldap.infocard.policy.SupportedToken;
import org.xmldap.infocard.policy.SupportedTokenList;
import org.xmldap.sts.db.CardStorage;
import org.xmldap.sts.db.DbSupportedClaim;
import org.xmldap.sts.db.ManagedCard;
import org.xmldap.sts.db.SupportedClaims;
import org.xmldap.util.Base64;
import org.xmldap.util.KeystoreUtil;
import org.xmldap.util.PropertiesManager;
import org.xmldap.ws.WSConstants;

import com.awl.rd.fc2.data.connectors.Card;
import com.awl.rd.fc2.data.connectors.DataConnector;
import com.awl.rd.fc2.data.connectors.exceptions.CardNotFoundExecption;
import com.awl.rd.fc2.plugin.infocard.usercredentials.cas.UserCredentialExt_CAS;
import com.awl.rd.fc2.plugin.infocard.usercredentials.map.UserCredentialExt_MAP;

public class CreateInfoCard {

	static Logger log = Logger.getLogger(CreateInfoCard.class);
	static public void trace(Object msg){
		log.info(msg);
	}
	static CardStorage storage = null;
	static String base64ImageFile = null;

	static X509Certificate[] certChain = null;
	static  PrivateKey privateKey = null;
	static  String domainname = null;
	static  String servletPath = null;
	//static SupportedClaims supportedClaimsImpl = null;
	/**
	 * @param args
	 * @throws CardNotFoundExecption 
	 */
	public final static String METHOD_SSO = "SSO";
	public final static String METHOD_PWD = "PWD";
	public final static String METHOD_CAS = "CAS";
	public final static String METHOD_BOTH = "BOTH";
	public static String getCRD(String userId, int idCard,String method) throws CardNotFoundExecption {
		
		// TODO Auto-generated method stub
		 PropertiesManager properties = PropertiesManager.getInstance();
		 String keystorePath = properties.getProperty("keystore");
         String keystorePassword = properties.getProperty("keystore.password");
         String key = properties.getProperty("key.name");
         String keyPassword = properties.getProperty("key.password");
         String imageFilePathString = properties.getProperty("image-file");
         String supportedClaimsClass = properties.getProperty("supportedClaimsClass");
         String protocolInUse   = properties.getProperty("PROTOCOL_IN_USE");
         trace("Image path  = " + imageFilePathString);
         
        
         try {
			//supportedClaimsImpl = SupportedClaims.getInstance(supportedClaimsClass);
			KeystoreUtil keystore = new KeystoreUtil(keystorePath, keystorePassword);
			trace("Keystore path : " + keystorePath);
            privateKey = keystore.getPrivateKey(key,keyPassword);
            if (privateKey == null) {
            	throw new ServletException("privateKey is null");
            }
            certChain = keystore.getCertificateChain(key);
            if (certChain == null) {
            	throw new ServletException("certChain is null");
            }
            if (certChain.length == 0) {
            	throw new ServletException("certChain.length is zero");
            }
            domainname = properties.getProperty("domain");
            if (domainname == null) {
            	throw new ServletException("domainname is null");
            }
            servletPath = properties.getProperty("servletPath");
            if (servletPath == null) {
            	throw new ServletException("servletPath is null");
            }
            
            {
            //	FileInputStream in;
				//					trace("Loading card image ["+imageFilePathString+"]");
//					in = new FileInputStream(new File(imageFilePathString));
//					
//	                base64ImageFile = getImageFileEncodedAsBase64(fis);
				try {
					base64ImageFile = com.utils.Base64.encode(getFileContent(imageFilePathString));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

            	
            }
		} catch (KeyStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ServletException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		// END INIT
		
		DataConnector connect = DataConnector.getInstance();
		Card fc2card = null;
		fc2card = connect.getCardsByUserId(userId).get(idCard);
		String cardId = fc2card.getCardId();
		// System.out.println(cardId);
	        ManagedCard managedCard = fc2card.getManagedCard();//storage.getCard(cardId);
	        
	        if (managedCard == null) {
	            /* log */
	        	System.out.println("CardServlet: could not find card:" + cardId);
	            return "";
	        }
	        String userCredentialType = (String) UserCredential.USERNAME;
	        if (userCredentialType == null) {
	        	userCredentialType = UserCredential.USERNAME;
	        	System.out.println("Warn: UserCredentialType is null. Using default: " + UserCredential.USERNAME);
	        }
	        
	        UserCredential usercredential = null;
	        if (UserCredential.USERNAME.equals(userCredentialType)) {
	        	usercredential = new UserCredential(UserCredential.USERNAME, userId);
	        } else {
	        	System.out.println("only username password credentials are currently supported");
	        	return "";
	        }
	        String tokenServiceEndpoint = protocolInUse+"://" + domainname + "/" + servletPath + "/" + "tokenservice";
	        String mexEndpoint = protocolInUse+"://" + domainname + "/" + servletPath + "/" + "mex" + "/" + userCredentialType;

	    //  x509Certificate2.appendChild("MIIEQTCCA6qgAwIBAgICAQQwDQYJKoZIhvcNAQEFBQAwgbsxJDAiBgNVBAcTG1ZhbGlDZXJ0IFZhbGlkYXRpb24gTmV0d29yazEXMBUGA1UEChMOVmFsaUNlcnQsIEluYy4xNTAzBgNVBAsTLFZhbGlDZXJ0IENsYXNzIDIgUG9saWN5IFZhbGlkYXRpb24gQXV0aG9yaXR5MSEwHwYDVQQDExhodHRwOi8vd3d3LnZhbGljZXJ0LmNvbS8xIDAeBgkqhkiG9w0BCQEWEWluZm9AdmFsaWNlcnQuY29tMB4XDTA0MDExNDIxMDUyMVoXDTI0MDEwOTIxMDUyMVowgewxCzAJBgNVBAYTAlVTMRAwDgYDVQQIEwdBcml6b25hMRMwEQYDVQQHEwpTY290dHNkYWxlMSUwIwYDVQQKExxTdGFyZmllbGQgVGVjaG5vbG9naWVzLCBJbmMuMTAwLgYDVQQLEydodHRwOi8vd3d3LnN0YXJmaWVsZHRlY2guY29tL3JlcG9zaXRvcnkxMTAvBgNVBAMTKFN0YXJmaWVsZCBTZWN1cmUgQ2VydGlmaWNhdGlvbiBBdXRob3JpdHkxKjAoBgkqhkiG9w0BCQEWG3ByYWN0aWNlc0BzdGFyZmllbGR0ZWNoLmNvbTCBnTANBgkqhkiG9w0BAQEFAAOBiwAwgYcCgYEA2xFDa9zRaXhZSehudBQIdBFsfrcqqCLYQjx6z59QskaupmcaIyK+D7M0+6yskKpbKMJw9raKgCrgm5xS4JGocqAW4cROfREJs5651POyUMRtSAi9vCqXDG2jimo8ms9KNNwe3upaJsChooKpSvuGIhKQOrKC1JKRn6lFn8Ok2/sCAQOjggEhMIIBHTAMBgNVHRMEBTADAQH/MAsGA1UdDwQEAwIBBjBKBgNVHR8EQzBBMD+gPaA7hjlodHRwOi8vY2VydGlmaWNhdGVzLnN0YXJmaWVsZHRlY2guY29tL3JlcG9zaXRvcnkvcm9vdC5jcmwwTwYDVR0gBEgwRjBEBgtghkgBhvhFAQcXAzA1MDMGCCsGAQUFBwIBFidodHRwOi8vd3d3LnN0YXJmaWVsZHRlY2guY29tL3JlcG9zaXRvcnkwOQYIKwYBBQUHAQEELTArMCkGCCsGAQUFBzABhh1odHRwOi8vb2NzcC5zdGFyZmllbGR0ZWNoLmNvbTAdBgNVHQ4EFgQUrFXet+oT6/yYaOJTYB7xJT6M7ucwCQYDVR0jBAIwADANBgkqhkiG9w0BAQUFAAOBgQB+HJi+rQONJYXufJCIIiv+J/RCsux/tfxyaAWkfZHvKNF9IDk7eQg3aBhS1Y8D0olPHhHR6aV0S/xfZ2WEcYR4WbfWydfXkzXmE6uUPI6TQImMwNfy5wdS0XCPmIzroG3RNlOQoI8WMB7ew79/RqWVKvnI3jvbd/TyMrEzYaIwNQ==");
	    //  x509Certificate3.appendChild("MIIC5zCCAlACAQEwDQYJKoZIhvcNAQEFBQAwgbsxJDAiBgNVBAcTG1ZhbGlDZXJ0IFZhbGlkYXRpb24gTmV0d29yazEXMBUGA1UEChMOVmFsaUNlcnQsIEluYy4xNTAzBgNVBAsTLFZhbGlDZXJ0IENsYXNzIDIgUG9saWN5IFZhbGlkYXRpb24gQXV0aG9yaXR5MSEwHwYDVQQDExhodHRwOi8vd3d3LnZhbGljZXJ0LmNvbS8xIDAeBgkqhkiG9w0BCQEWEWluZm9AdmFsaWNlcnQuY29tMB4XDTk5MDYyNjAwMTk1NFoXDTE5MDYyNjAwMTk1NFowgbsxJDAiBgNVBAcTG1ZhbGlDZXJ0IFZhbGlkYXRpb24gTmV0d29yazEXMBUGA1UEChMOVmFsaUNlcnQsIEluYy4xNTAzBgNVBAsTLFZhbGlDZXJ0IENsYXNzIDIgUG9saWN5IFZhbGlkYXRpb24gQXV0aG9yaXR5MSEwHwYDVQQDExhodHRwOi8vd3d3LnZhbGljZXJ0LmNvbS8xIDAeBgkqhkiG9w0BCQEWEWluZm9AdmFsaWNlcnQuY29tMIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDOOnHK5avIWZJV16vYdA757tn2VUdZZUcOBVXc65g2PFxTXdMwzzjsvUGJ7SVCCSRrCl6zfN1SLUzm1NZ9WlmpZdRJEy0kTRxQb7XBhVQ7/nHk01xC+YDgkRoKWzk2Z/M/VXwbP7RfZHM047QSv4dk+NoS/zcnwbNDu+97bi5p9wIDAQABMA0GCSqGSIb3DQEBBQUAA4GBADt/UG9vUJSZSWI4OB9L+KXIPqeCgfYrx+jFzug6EILLGACOTb2oWH+heQC1u+mNr0HZDzTuIYEZoDJJKPTEjlbVUjP9UNV+mWwD5MlM/Mtsq2azSiGM5bUMMj4QssxsodyamEwCW/POuZ6lcg5Ktz885hZo+L7tdEy8W9ViH0Pd");

	        InfoCard card = new InfoCard(certChain, privateKey);
	        card.setCardId("https://" + domainname + "/" + servletPath + "/" + "card/" + managedCard.getCardId(), 1);
	        card.setCardName(managedCard.getCardName());
	        
	        card.setIssuer(tokenServiceEndpoint);

	        if (managedCard.getRequireAppliesTo()) {
	        	// optional
	        	card.setRequireAppliesTo();  // optional = false
	        }
	        if (managedCard.getRequireStrongRecipientIdentity()) {
	        	// optional
	        	card.setRequireStrongRecipientIdentity(true);
	        }
	        
	        // set card logo/image if available . . . if not available it will default to Milo :-)
	        if (base64ImageFile != null) {
	            card.setBase64BinaryCardImage(base64ImageFile);
	            card.setMimeType("image/png");
	        }

//			The next line made no sense since the dates are in the database and 
//	        are in the card already
//	        XSDDateTime issued = new XSDDateTime();
//	        XSDDateTime expires = new XSDDateTime(525600);
	//
//	        card.setTimeIssued(issued.getDateTime());
//	        card.setTimeExpires(expires.getDateTime());
	        card.setTimeIssued(managedCard.getTimeIssued());
	        String timeexpired = managedCard.getTimeExpires();
	        if (timeexpired != null) {
	        	card.setTimeExpires(timeexpired);
	        }
	        
	        {
		        TokenServiceReference tsr = getTokenServiceReference(tokenServiceEndpoint, mexEndpoint, certChain[0], usercredential);
		        List<TokenServiceReference> list = new ArrayList<TokenServiceReference>();
		       
		    //    card.setTokenServiceReference(list);
		        
		        UserCredentialExt_MAP extMap = new UserCredentialExt_MAP("URLMAP");
		        extMap.setUsername(usercredential.getUserName());
		        UserCredential usercredential_map = new UserCredential(extMap);
		        usercredential_map.setUserName(usercredential.getUserName());
		       // String mexEndpoint_MAP = protocolInUse+"://" + domainname + "/" + servletPath + "/" + "mex" + "/" + userCredentialType;
		        TokenServiceReference tsr2 = getTokenServiceReference(tokenServiceEndpoint, mexEndpoint, certChain[0], usercredential_map);
		        
		        
		        UserCredentialExt_CAS extCas = new UserCredentialExt_CAS("CAS");
		        extCas.setUsername(usercredential.getUserName());
		        UserCredential usercredential_CAS = new UserCredential(extCas);//UserCredential.CAS,"CAP");
		        usercredential_CAS.setUserName(usercredential.getUserName());
		        TokenServiceReference tsr3 = getTokenServiceReference(tokenServiceEndpoint, mexEndpoint, certChain[0], usercredential_CAS);
		        
		        
		        if(METHOD_BOTH.equalsIgnoreCase(method)){
		        	list.add(tsr);//PWD
			        list.add(tsr2);//SSO
		        }else{
		        	if(METHOD_SSO.equalsIgnoreCase(method)){
		        		trace("Setting SSO Authentication method");
		        		list.add(tsr2);//SSO
		        	}else if(METHOD_PWD.equalsIgnoreCase(method)){
		        		trace("Setting PWD Authentication method");
		        		list.add(tsr);//PWD
		        	}else if(METHOD_CAS.equalsIgnoreCase(method)){
		        		trace("Setting CAS Authentication method");
		        		list.add(tsr3);
		        	}
		        }
		        
		       
		        card.setTokenServiceReference(list);
		        
	        }
	        
	        {
		        SupportedToken token = new SupportedToken(WSConstants.SAML11_NAMESPACE);
		        List<SupportedToken> list = new ArrayList<SupportedToken>();
		        SupportedTokenList tokenList = new SupportedTokenList(list);
		        tokenList.addSupportedToken(token);
		        card.setTokenList(tokenList);
	        }
	        SupportedClaimTypeList claimList = getSupportedClaimList(managedCard,fc2card.getSupportedClaims());
	        card.setClaimList(claimList);

	        try {
				card.setPrivacyPolicy(getPrivacyPolicyReference(domainname), 1);
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}
			try {
				System.out.println(card.toXML());
			} catch (SerializationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				return card.toXML();
			} catch (SerializationException e) {
				return "";
			}
         
	}
	 protected static String getImageFileEncodedAsBase64(InputStream ins) {
	        String encodedFile;
	        encodedFile = Base64.encodeFromInputStream(ins, 0);
	        return encodedFile;
	    }

	  protected static String getPrivacyPolicyReference(String domainname) {
	        return "https://" + domainname + "/" + servletPath + "/PrivacyPolicy.xml";
	    }

	    protected static TokenServiceReference getTokenServiceReference(String tokenServiceEndpoint, String mexEndpoint, X509Certificate cert, UserCredential usercredential) {
	    	TokenServiceReference tsr = new TokenServiceReference(tokenServiceEndpoint, mexEndpoint, cert, usercredential);
	        return tsr;
	    }

	    protected static SupportedClaimTypeList getSupportedClaimList(ManagedCard managedCard,SupportedClaims supportedClaims2) {
	    	List<DbSupportedClaim> supportedClaims = supportedClaims2.dbSupportedClaims();
	        SupportedClaim supportedClaim = new SupportedClaim("PPID", org.xmldap.infocard.Constants.IC_NS_PRIVATEPERSONALIDENTIFIER, "your personal private identitfier");
	        ArrayList<SupportedClaim> cl = new ArrayList<SupportedClaim>();
	        cl.add(supportedClaim);
	    	for (DbSupportedClaim claim : supportedClaims) {
	    		String value = managedCard.getClaim(claim.uri);
	    		//if ((value != null) && !("".equals(value))) {
		    		// TODO: support description. Axel
		    		supportedClaim = new SupportedClaim(claim.displayTags[0].displayTag, claim.uri, "A Description");
		    		cl.add(supportedClaim);
	    		//}
	    	}
	        SupportedClaimTypeList claimList = new SupportedClaimTypeList(cl);
//	        SupportedClaimList claimList = new SupportedClaimList();
//	        SupportedClaim given = new SupportedClaim("GivenName", org.xmldap.infocard.Constants.IC_NS_GIVENNAME);
//	        SupportedClaim sur = new SupportedClaim("Surname", org.xmldap.infocard.Constants.IC_NS_SURNAME);
//	        SupportedClaim email = new SupportedClaim("EmailAddress", org.xmldap.infocard.Constants.IC_NS_EMAILADDRESS);
//	        SupportedClaim ppid = new SupportedClaim("PPID", org.xmldap.infocard.Constants.IC_NS_PRIVATEPERSONALIDENTIFIER);
//	        claimList.addSupportedClaim(given);
//	        claimList.addSupportedClaim(sur);
//	        claimList.addSupportedClaim(email);
//	        claimList.addSupportedClaim(ppid);
	        return claimList;
	    }
	    public static byte [] getFileContent(String strFilePath) throws IOException{
	    	try {
	    	File file = new File(strFilePath);
	    	InputStream is;
			
				is = new FileInputStream(file);
			
	        
	        // Get the size of the file
	        long length = file.length();
	    
	        // You cannot create an array using a long type.
	        // It needs to be an int type.
	        // Before converting to an int type, check
	        // to ensure that file is not larger than Integer.MAX_VALUE.
	        if (length > Integer.MAX_VALUE) {
	            // File is too large
	        }
	    
	        // Create the byte array to hold the data
	        byte[] bytes = new byte[(int)length];
	    
	        // Read in the bytes
	        int offset = 0;
	        int numRead = 0;
	        while (offset < bytes.length
	               && (numRead=is.read(bytes, offset, bytes.length-offset)) >= 0) {
	            offset += numRead;
	        }
	    
	        // Ensure all the bytes have been read in
	        if (offset < bytes.length) {
	            throw new IOException("Could not completely read file "+file.getName());
	        }
	    
	        // Close the input stream and return bytes
	        is.close();
	        return bytes;
	    	} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    	return null;

	    }
}
