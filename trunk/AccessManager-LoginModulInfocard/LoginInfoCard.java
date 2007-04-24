/*
 * Copyright (c) 2006, Axel Nennker - nennker.de
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
 */



package de.nennker.axel;

import java.util.Map;
import java.util.Set;
import java.util.Iterator;
import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.login.LoginException;
import com.sun.identity.authentication.spi.AMLoginModule;
import com.sun.identity.authentication.spi.AuthLoginException;
import com.sun.identity.authentication.AuthContext;
import com.sun.identity.authentication.AuthContext.IndexType;
import javax.security.auth.callback.UnsupportedCallbackException;
//import java.security.NoSuchProviderException;
import sun.misc.BASE64Encoder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import nu.xom.*;
import nu.xom.canonical.Canonicalizer;
import org.xmldap.crypto.CryptoUtils;
//import org.xmldap.exceptions.KeyStoreException;
import org.xmldap.util.Base64;
//import org.xmldap.util.KeystoreUtil;
import org.xmldap.ws.WSConstants;
import org.xmldap.xml.Canonicalizable;
//import org.xmldap.xmlenc.DecryptUtil;

//import javax.servlet.ServletConfig;
//import javax.servlet.ServletException;
//import javax.servlet.ServletOutputStream;
//import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.math.BigInteger;
import java.security.interfaces.RSAPrivateKey;
import java.security.KeyStore;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPrivateKeySpec;
import java.util.Enumeration;

import org.mozilla.jss.CryptoManager;
import org.mozilla.jss.CryptoManager.InitializationValues;
import org.mozilla.jss.CryptoManager.NotInitializedException;
import org.mozilla.jss.crypto.X509Certificate;
import org.mozilla.jss.crypto.ObjectNotFoundException;
import org.mozilla.jss.crypto.TokenException;
import org.mozilla.jss.crypto.CryptoToken;
import org.mozilla.jss.crypto.AlreadyInitializedException;
import org.mozilla.jss.CertDatabaseException;
import org.mozilla.jss.KeyDatabaseException;

import java.util.HashMap;
import org.xmldap.rp.util.ClaimParserUtil;
import org.xmldap.rp.util.DecryptUtil;
import org.xmldap.rp.util.ValidationUtil;
import nu.xom.ParsingException;
import org.xmldap.exceptions.CryptoException;

import java.security.spec.InvalidKeySpecException;
import java.security.NoSuchAlgorithmException;

public class LoginInfoCard extends AMLoginModule {

    private RSAPrivateKey key = null;

    private String userTokenId;
    private java.security.Principal userPrincipal = null;
    private static final String LOGIN_USER = "anonymous";

    private void readKey() throws LoginException {
     try {
	// pkcs8 encoded key
	String fname = "/opt/SUNWwbsvr/alias/" + "Server-Cert.der";
	FileInputStream fis = new FileInputStream(fname);
        DataInputStream dis = new DataInputStream(fis);
        byte[] bytes = new byte[dis.available()];
        dis.readFully(bytes);
        InputStream fl = new ByteArrayInputStream(bytes);
 
        byte[] keyBytes = new byte[fl.available()];
	// this does not work without the BouncyCastleProvider under SUN One WebServer
        KeyFactory kf = KeyFactory.getInstance("RSA", new BouncyCastleProvider());
        fl.read ( keyBytes, 0, fl.available() );
        fl.close();
        PKCS8EncodedKeySpec keysp = new PKCS8EncodedKeySpec ( keyBytes );
        PrivateKey ff = kf.generatePrivate (keysp);
        key = (RSAPrivateKey)ff;
	BigInteger modulus = key.getModulus();
        BigInteger exponent = key.getPrivateExponent();
        //System.out.println( "modulus bitlength=" + modulus.bitLength() );
        //System.out.println( "exponent bitlength=" + exponent.bitLength() );
        //BASE64Encoder b64 = new BASE64Encoder();
        //System.out.println( "modulus=\n" + b64.encode(modulus.toByteArray()));
        //System.out.println( "exponent=\n" + b64.encode(exponent.toByteArray()));
     } catch (InvalidKeySpecException e) {
      throw new LoginException(e.getMessage());
     } catch (NoSuchAlgorithmException e) {
      throw new LoginException(e.getMessage());
     } catch (IOException e) {
      throw new LoginException(e.getMessage());
     //} catch (NoSuchProviderException e) {
      //throw new LoginException(e.getMessage());
     }
    }

    public LoginInfoCard() throws LoginException{
	System.out.println("LoginInfoCard()");
	if (key == null) {
	 readKey();
	}
    }

    private void debugPrintMap(String name, Map map) {
	Set keys = map.keySet();
	for (Iterator iter=keys.iterator(); iter.hasNext(); ) {
	 String key = (String)iter.next();
	 System.out.println(name + "(" + key + ")=" + map.get(key));
	}
    }

    public void init(Subject subject, Map sharedState, Map options) {
	System.out.println("LoginInfoCard initialization");
	if (key == null) {
	 try { readKey(); } catch (LoginException e) {}
	}
	debugPrintMap("sharedState", sharedState);
	debugPrintMap("options", options);
    } 

    private String processXmlToken(String encryptedXML) throws AuthLoginException {
	String ret = null;

        if (key == null) {
	 try { readKey(); } catch (LoginException e) { throw new AuthLoginException(e); }
        }
 
//        try {
            
            //decrypt it.
            DecryptUtil decrypter = new DecryptUtil();
            StringBuffer decryptedXML = decrypter.decryptXML(encryptedXML, key);
            
            
            
            //let's make a doc
            Builder parser = new Builder();
            Document assertion = null;
            try {
                assertion = parser.build(decryptedXML.toString(), "");
            } catch (ParsingException e) {
                System.out.println( e.getMessage() );
                return null;
            } catch (IOException e) {
                System.out.println( e.getMessage() );
                return null;
            }
            
            
            //Validate it
            ValidationUtil validator = new ValidationUtil();
            boolean verified = false;
            try {
                verified = validator.validate(assertion);
            } catch (CryptoException e) {
                System.out.println( e.getMessage() );
                return null;
            }
            
            
           if (verified) {
                System.out.println("Your signature was verified");
            } else {
                System.out.println("Signature validation failed!   Exiting");
                return null;
            }

            //Parse the claims
            ClaimParserUtil claimParser = new ClaimParserUtil();
            HashMap claims = claimParser.parseClaims(assertion);
            
            for (Iterator iter=claims.keySet().iterator(); iter.hasNext(); ) {
                String name = (String)iter.next();
	        System.out.println("claim: " + name + " = " + (String)claims.get(name));
                if (name.equalsIgnoreCase("emailaddress")) { // replace emailaddress if you like something else
                 return (String)claims.get(name);
	        }
            }
            
            
            
//        } catch (IOException e) {
//            throw new AuthLoginException(e);
//        }
        
        System.out.println("required claim emailaddress not found");
        System.out.println(assertion.toString());
        return ret;
    }
    
    private void addLoginCallbackMessage(
     Callback[] callbacks, String user, String pw) throws UnsupportedCallbackException {
        //System.out.println("begin addLoginCallbackMessage()");

        for (int i=0; i < callbacks.length; i++) {

            if (callbacks[i] instanceof NameCallback) {
                //System.out.println("Got NameCallback");
                NameCallback nc = (NameCallback) callbacks[i];
                nc.setName(user);
                //System.out.println(nc.getName());
            }

            if (callbacks[i] instanceof PasswordCallback) {
                //System.out.println("Got PasswordCallback");
                PasswordCallback  pc = (PasswordCallback) callbacks[i];
                pc.setPassword(pw.toCharArray());
                //System.out.println(String.valueOf(pc.getPassword()));
            }

        }

    }   

    private String loginLDAP(String user, String pw) throws AuthLoginException {

	//orgProfile(sunOrganizationAliases)=[e1, n1v1.e1.i3alab.net]

        String strOrgName = "e1.i3alab.net";
        strOrgName = "dc=e1,dc=i3alab,dc=net";
        String in = "LDAP";
        AuthContext ac = null;

        ac = new AuthContext(strOrgName);
        System.out.println("ac.getModuleInstanceNames():" + ac.getModuleInstanceNames());
        ac.login(AuthContext.IndexType.MODULE_INSTANCE, in);

        try {
            Callback[] callbacks = null;
            // get information requested from module
            while (ac.hasMoreRequirements()) {
                callbacks = ac.getRequirements();
                if (callbacks != null) {
                    addLoginCallbackMessage(callbacks, user, pw);
                    ac.submitRequirements(callbacks);
                }
            }
        } catch (Exception e) {
            System.out.println("Login failed!!");
	    e.printStackTrace();
        }

        if (ac.getStatus() == AuthContext.Status.SUCCESS) {
            System.out.println("Login success!!");
	    setLoginSuccessURL("http://n2v1.e1.i3alab.net/mba/demo/loggedin.aspx");
	    return user;
        } else if (ac.getStatus() == AuthContext.Status.FAILED) {
            System.out.println("Login has failed!!");
	    return null;
        } else {
            System.out.println("Unknown status: " + ac.getStatus());
	    return null;
        }
    }

    private void debugOrg() {
	String orgDN = getRequestOrg();
	System.out.println("requestOrg=" + orgDN); 
        try {
	 Map orgProfile = getOrgProfile(orgDN);
	 debugPrintMap("orgProfile", orgProfile);
        } catch (AuthLoginException e) {
	 e.printStackTrace();
        }
    }

    private void debugPrintCallbackData(Callback[] callbacks) {
            for (int i=0; i<callbacks.length; i++) {
                if (callbacks[i] instanceof NameCallback) {
                    System.out.println("Callback Value-> " +
                        ((NameCallback) callbacks[i]).getName());
                } else if (callbacks[i] instanceof PasswordCallback) {
                    System.out.println("Callback Value-> " +
                        ((PasswordCallback) callbacks[i]).getPassword());
                }
	    }

    }

    private void debugPrintRequest(HttpServletRequest hsr) {
      java.util.Enumeration names = hsr.getAttributeNames();
      for (; names.hasMoreElements() ;) {
       System.out.println("attributes: " + (String) names.nextElement());
      }
      names = hsr.getParameterNames();
      for (; names.hasMoreElements() ;) {
       System.out.println("parameter: " + (String) names.nextElement());
      }
    }

    public String getXmlToken() {
     String xmlToken = null;
     HttpServletRequest hsr = getHttpServletRequest();
     if (hsr == null) {
      System.out.println("process: HttpServletRequest is null");
     } else {
      xmlToken = hsr.getParameter("xmlToken");
     }
     return xmlToken;
    }

    private String infocardLogin(String xmlToken) throws AuthLoginException {
     String userTokenId = null;
     System.out.println( "process: xmlToken is\n" + xmlToken );
     userTokenId = processXmlToken( xmlToken );
     if (userTokenId != null) {
      System.out.println( "process: userTokenID is " + userTokenId );
      userTokenId = passwordLogin(userTokenId, "password");
     }
     return userTokenId;
    }

    private String passwordLogin(String username, String password) throws AuthLoginException {
     //System.out.println("passwordLogin: username=" + username + " password=" + password);
     String userTokenId = null;
     if ((username != null) && (password != null) && !username.equals("") && !password.equals("")) {
       userTokenId = loginLDAP(username, password);
     }
     return userTokenId;
    }

    public int process(Callback[] callbacks, int state) throws AuthLoginException {
	int currentState = state;

	if (key == null) {
	 try { 
          readKey(); 
         } catch (LoginException e) { 
           throw new AuthLoginException(e);
         }
	}

	if (currentState != 1) {
         throw new AuthLoginException("illegal state" + currentState);
        }

        String xmlToken = getXmlToken();

	if (xmlToken == null) {
         System.out.println( "process: xmlToken is null" );
	 String username = ((NameCallback) callbacks[0]).getName();
         //System.out.println("XXXhier" + ((PasswordCallback)callbacks[1]).getPrompt());
         //if (callbacks[1] instanceof PasswordCallback) {
           //System.out.println("Got PasswordCallback");
           //PasswordCallback  pc = (PasswordCallback) callbacks[1];
           //System.out.println("password=" + String.valueOf(pc.getPassword()));
         //}

	 String password =  new String( ((PasswordCallback) callbacks[1]).getPassword() );
         userTokenId = passwordLogin(username, password);
	} else {
         userTokenId = infocardLogin(xmlToken);
	}

        if (userTokenId != null){
       	 // return -1 for login successful
	 return -1;
	} else {
         throw new AuthLoginException("login failed");
	}
    }

    public java.security.Principal getPrincipal() {
        if (userPrincipal != null) {
System.out.println( "getPrincipal: getName()=" + userPrincipal.getName() );
            return userPrincipal;
        } else if (userTokenId != null) {
            userPrincipal = new SamplePrincipal(userTokenId);
System.out.println( "getPrincipal: GetName()=" + userPrincipal.getName() );
            return userPrincipal;
        } else {
            return null;
        }
    }
}
