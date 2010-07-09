package com.utils;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;


import com.utils.execeptions.Utils_Exception_Unable_ToRead_File;
import com.utils.execeptions.XMLParser_Exception_NO_ATTRIBUTE;

public class KeyTool {
	public String s_PATH_KEYSTORE = "...";
	public KeyTool() {
		// TODO Auto-generated constructor stub
	}
	public KeyTool(String path){
		s_PATH_KEYSTORE = path;
	}
	public static void exec(String cmd){
		try {
			
			Process p = Runtime.getRuntime().exec(cmd);  
			BufferedReader br = new BufferedReader(new InputStreamReader(p.getErrorStream()));  
			StringBuffer sb = new StringBuffer();  
			String line;  
			while ((line = br.readLine()) != null) {  
			  sb.append(line).append("\n");  				  
			}  
			
		} catch (Exception t) 
		{
			t.printStackTrace();
		} 
	}
	public void genCertificate(String alias,String DN){
		String cmd= "keytool.exe -genkey -alias "+alias+" -keyalg RSA -storepass bigsecret   -keypass bigsecret -dname \""+DN +"\"  -keystore " + s_PATH_KEYSTORE;
		exec(cmd);
	}
	private KeyStore getKeyStore(){
		 try {
		        // Load the keystore in the user's home directory
		        FileInputStream is = new FileInputStream(s_PATH_KEYSTORE);
		            
		    
		        KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());
		        keystore.load(is, "bigsecret".toCharArray());
		    
		       
		        return keystore;
		    } catch (KeyStoreException e) {
		    } catch (java.security.cert.CertificateException e) {
		    } catch (NoSuchAlgorithmException e) {
		    } catch (java.io.IOException e) {
		    }
		    return null;
	}
	public Certificate getCertificateFromKeyStore(String alias){
		   try {
		        // Load the keystore in the user's home directory
		     
		    
		        // Get certificate
		        java.security.cert.Certificate cert = getKeyStore().getCertificate(alias);
		        return cert;
		    } catch (KeyStoreException e) {
		    
		    }
		    return null;

	}
	public String getCertificateB64FromKeyStore(String alias){
		try {
			return Base64.encode(getCertificateFromKeyStore(alias).getEncoded());
		} catch (CertificateEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	public KeyPair getKeyPairFromKeyStore(String alias,String password){
		return getPrivateKey(getKeyStore(), alias, password.toCharArray());
	}
	
	 public KeyPair getPrivateKey(KeyStore keystore, String alias, char[] password) {
	        try {
	            // Get private key
	            Key key = keystore.getKey(alias, password);
	            if (key instanceof PrivateKey) {
	                // Get certificate of public key
	                java.security.cert.Certificate cert = keystore.getCertificate(alias);
	    
	                // Get public key
	                PublicKey publicKey = cert.getPublicKey();
	    
	                // Return a key pair
	                return new KeyPair(publicKey, (PrivateKey)key);
	            }
	        } catch (UnrecoverableKeyException e) {
	        } catch (NoSuchAlgorithmException e) {
	        } catch (KeyStoreException e) {
	        }
	        return null;
	    }
	  public static PublicKey getFromKeyStore_PublicKey() throws Utils_Exception_Unable_ToRead_File{
	    	 String path;
			try {
				path = Config.getKeyStore();
				KeyPair keys = new KeyTool(path).getKeyPairFromKeyStore(Config.getKeyStoreAlias(Constant_XML.XML_BALISE_KEYSTORE_ALIAS_SAML2),
					 	 Config.getKeyStoreMdp());
				return keys.getPublic(); 
			} catch (XMLParser_Exception_NO_ATTRIBUTE e) {
				throw(new Utils_Exception_Unable_ToRead_File(e.getMessage()));
			} catch (Utils_Exception_Unable_ToRead_File e) {
				throw(e);
			}
	    	 
	     }
	     public static KeyPair getFromKeyStore_KeyPair() throws Utils_Exception_Unable_ToRead_File{
	    	 try{
	    		 String path = Config.getKeyStore();
		    	 KeyPair keys = new KeyTool(path).getKeyPairFromKeyStore(Config.getKeyStoreAlias(Constant_XML.XML_BALISE_KEYSTORE_ALIAS_SAML2),
		    			 												 Config.getKeyStoreMdp());
		    	 
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
	    	 } catch (XMLParser_Exception_NO_ATTRIBUTE e) {
					throw(new Utils_Exception_Unable_ToRead_File(e.getMessage()));
				} catch (Utils_Exception_Unable_ToRead_File e) {
					throw(e);
				}
	    	
	 		
	     }
	public static void main(String arg[]){
		KeyTool kt = new KeyTool();
		kt.genCertificate("VeryStef", "cn=localhost");
		System.out.println(kt.getCertificateB64FromKeyStore("verystef"));
		
		 //sun.security.tools.KeyTool jkt = new sun.security.tools.KeyTool();
		 
	}
}
