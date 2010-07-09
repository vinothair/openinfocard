package com.awl.rd.fc2;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;

import nu.xom.Element;
import nu.xom.ValidityException;

import org.apache.log4j.Logger;
import org.xmldap.exceptions.KeyStoreException;
import org.xmldap.exceptions.ParsingException;
import org.xmldap.exceptions.SerializationException;
import org.xmldap.infocard.InfoCard;
import org.xmldap.infocard.SignedInfoCard;
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
import org.xmldap.util.XmlFileUtil;
import org.xmldap.ws.WSConstants;

import com.awl.rd.fc2.data.connectors.Card;
import com.awl.rd.fc2.data.connectors.DataConnector;
import com.awl.rd.fc2.data.connectors.exceptions.CardNotFoundExecption;
import com.awl.rd.fc2.plugin.infocard.usercredentials.cas.UserCredentialExt_CAS;
import com.awl.rd.fc2.plugin.infocard.usercredentials.map.UserCredentialExt_MAP;

public class UpdateInfoCard {

	static Logger log = Logger.getLogger(UpdateInfoCard.class);
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
//					base64ImageFile = com.utils.Base64.encode(getFileContent("D:/Documents and Settings/A171258/Bureau/eid.png"));
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
	    
	        System.out.println("\n\n" + managedCard.getCardName());
	        
	           
	        if (managedCard == null) {
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
//	        card.setCardName("CNIE");
	        
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
		        
		        
//		        UserCredential usercredential_map = new UserCredential(UserCredential.MAP,"URLMAP");
//		        usercredential_map.setUserName(usercredential.getUserName());
//		       // String mexEndpoint_MAP = protocolInUse+"://" + domainname + "/" + servletPath + "/" + "mex" + "/" + userCredentialType;
//		        TokenServiceReference tsr2 = getTokenServiceReference(tokenServiceEndpoint, mexEndpoint, certChain[0], usercredential_map);
//		        
//		        
//		        UserCredential usercredential_CAS = new UserCredential(UserCredential.CAS,"CAP");
//		        usercredential_CAS.setUserName(usercredential.getUserName());
//		        TokenServiceReference tsr3 = getTokenServiceReference(tokenServiceEndpoint, mexEndpoint, certChain[0], usercredential_CAS);
//		        
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
	    
	    static public InfoCard getCardFromCRD(String path) {	    	
			trace("Loading on " + path);
			SignedInfoCard card = null;
			try {
				File file = new File(path);
				FileInputStream in = new FileInputStream(file);
				//FileReader fin = new FileReader(file);
				StringBuffer buf = new StringBuffer();
				byte[] buffer = new byte[50];
				int read = 1;
				while(read!= -1){
					try {
						read = in.read(buffer);
						
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					if(read != -1){
						String tmp = new String(buffer,0,read);
						//buf.append(buffer,0,read);
						buf.append(tmp);
					}
					
				}
				String toRet = buf.toString();
				Element root = XmlFileUtil.readXml(new ByteArrayInputStream(toRet.getBytes())).getRootElement();
				card = new SignedInfoCard(root);
				//trace("READ IMG = " + card.getBase64BinaryCardImage());
//				trace("Read the current image, and save it");
//				byte [] ImgRaw = com.utils.Base64.decode(card.getBase64BinaryCardImage());
//				if(ImgRaw != null){
//					ImageIcon imgI = new ImageIcon(ImgRaw);
//					Image img = imgI.getImage();
////					int height = 135;
////					int width = imgI.getIconWidth()*135/imgI.getIconHeight();
//					int height = imgI.getIconHeight();
//					int width = imgI.getIconWidth();
//					trace("Scaling at  :["+width+","+height+"]");
//					//img = img.getScaledInstance(width,height , BufferedImage.SCALE_DEFAULT);
//					
//					
//					BufferedImage bi = new BufferedImage(width,height,BufferedImage.TYPE_INT_RGB);//img.getWidth(null),img.getHeight(null));
//					Graphics2D g2 = bi.createGraphics();
//					// Draw img into bi so we can write it to file.
//					g2.drawImage(img, 0, 0, null);
//					g2.dispose();
//					int newHeight = 70;
//					Image Resized =  bi.getScaledInstance(width*newHeight/height,newHeight , BufferedImage.SCALE_DEFAULT);
//									
//						BufferedImage bi2 = new BufferedImage(width*newHeight/height,newHeight,BufferedImage.TYPE_INT_RGB);//img.getWidth(null),img.getHeight(null));
//						Graphics2D g3 = bi2.createGraphics();
//						// Draw img into bi so we can write it to file.
//						g3.drawImage(Resized, 0, 0, null);
//						g3.dispose();
//					
//					
//					
//					String filename = card.getCardName().replace("/", "_");
//					filename = filename.replace("\\", "_");
//					try {
//						String imgFolder = Config.getInstance().getImgFolder();
//						ImageIO.write(bi2, "jpg", new File(imgFolder+filename+".jpg"));
//						card.setBase64BinaryCardImage(imgFolder+filename+".jpg");
//					} catch (Config_Exception_NotDone e) {
//						trace("Missing <IMG> folder in the config_selecteur.xml");
//					}
//					
//				}
				
				
			} catch (ValidityException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ParsingException e) {
				e.printStackTrace();		
			} catch (nu.xom.ParsingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return card;
		}
	    
	    public static String signCard(InfoCard toSign) {
	    	PropertiesManager properties = PropertiesManager.getInstance();

	    	String keystorePath = properties.getProperty("keystore");
	    	String keystorePassword = properties.getProperty("keystore.password");
	    	String key = properties.getProperty("key.name");
	    	String keyPassword = properties.getProperty("key.password");

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
	    		
	    	} catch (KeyStoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ServletException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			//*****//
			toSign.setCertChainAndPrivateKey(certChain, privateKey);
			
			try {
				return toSign.toXML();
			} catch (SerializationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return "erreur";
	    }
	    
	    public static void main(String[] args) throws CardNotFoundExecption, IOException, SerializationException {
//	    	System.out.println("pouet");
//	    	String card = getCRD("robert", 0,"SSO");
//	    	saveInFile("D:/Documents and Settings/A171258/Bureau/azertyuiop.crd", card);
	    	
	    	InfoCard card = UpdateInfoCard.getCardFromCRD("D:/Documents and Settings/A171258/Bureau/asp/SF_01_Gerard_Mandarine_Bancaire_SSO-JSS_CA.crd");
	    	System.out.println("Load successfull : " + card.getCardName());
	    	
//	    	System.out.println("Modyfing img");
//	    	String image = com.utils.Base64.encode(getFileContent("D:/Documents and Settings/A171258/Bureau/asp/CB.png"));
//	    	card.setBase64BinaryCardImage(image);
//	    	System.out.println("img modified");
	    	
	    	System.out.println("Modifying name");
	    	card.setCardName("CB Credit Agricole");
	    	System.out.println("name modified");
	    	
	    	System.out.println("exporting card");
	    	saveInFile("D:/Documents and Settings/A171258/Bureau/asp/SF_01_Gerard_Mandarine_Bancaire_SSO-JSS_CA2.crd", signCard(card));
	    	System.out.println("done.");
	    }
	    
	    private static void saveInFile(String strFilename,String xmlCard){
			File file = new File(strFilename);
			FileOutputStream out;
			try {
				out = new FileOutputStream(file);
				out.write(xmlCard.getBytes());
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			trace("Card saved in " + strFilename);
		}
}
