/*
 * Copyright (c) 2006, Chuck Mortimore - xmldap.org
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
 *     * Neither the names xmldap, xmldap.org, xmldap.com nor the
 *       names of its contributors may be used to endorse or promote products
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
 */


package org.xmldap.sts.servlet;

import org.xmldap.exceptions.KeyStoreException;
import org.xmldap.exceptions.SerializationException;
import org.xmldap.infocard.InfoCard;
import org.xmldap.infocard.TokenServiceReference;
import org.xmldap.infocard.policy.SupportedClaim;
import org.xmldap.infocard.policy.SupportedClaimList;
import org.xmldap.infocard.policy.SupportedToken;
import org.xmldap.infocard.policy.SupportedTokenList;
import org.xmldap.sts.db.DbSupportedClaim;
import org.xmldap.sts.db.ManagedCard;
import org.xmldap.sts.db.CardStorage;
import org.xmldap.sts.db.SupportedClaims;
import org.xmldap.sts.db.impl.CardStorageEmbeddedDBImpl;
import org.xmldap.util.*;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.List;


public class CardServlet extends HttpServlet {

    private static CardStorage storage = null;
    private String base64ImageFile = null;

    private X509Certificate cert = null;
    private PrivateKey privateKey = null;
    private String domainname = null;
    private String servletPath = null;

    private SupportedClaims supportedClaimsImpl = null;


    public void init(ServletConfig config) throws ServletException {

        super.init(config);

        try {
            PropertiesManager properties = new PropertiesManager(PropertiesManager.SECURITY_TOKEN_SERVICE, config.getServletContext());
            String keystorePath = properties.getProperty("keystore");
            String keystorePassword = properties.getProperty("keystore.password");
            String key = properties.getProperty("key.name");
            String keyPassword = properties.getProperty("key.password");
            String imageFilePathString = properties.getProperty("image.file");
            String supportedClaimsClass = properties.getProperty("supportedClaimsClass");
            supportedClaimsImpl = SupportedClaims.getInstance(supportedClaimsClass);
            storage = new CardStorageEmbeddedDBImpl(supportedClaimsImpl);
            
            KeystoreUtil keystore = new KeystoreUtil(keystorePath, keystorePassword);
            privateKey = keystore.getPrivateKey(key,keyPassword);
            if (privateKey == null) {
            	throw new ServletException("privateKey is null");
            }
            cert = keystore.getCertificate(key);
            if (cert == null) {
            	throw new ServletException("cert is null");
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
            	java.io.InputStream fis = getServletContext().getResourceAsStream(imageFilePathString);
                base64ImageFile = getImageFileEncodedAsBase64(fis);
            }
            
        } catch (IOException e) {
            throw new ServletException(e);
        } catch (KeyStoreException e) {
            throw new ServletException(e);
        } catch (InstantiationException e) {
        	throw new ServletException(e);
		} catch (IllegalAccessException e) {
			throw new ServletException(e);
		} catch (ClassNotFoundException e) {
			throw new ServletException(e);
    }

    }


    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        HttpSession session = request.getSession(true);
        String username = getUsername(session);
        if (username == null) {
            dispatchUnauthenticatedRequest(request, response); // no username means invalid state . . .
            return;
        }

        String cardId = extractCardIdFromRequest(request);


        System.out.println(cardId);
        ManagedCard managedCard = storage.getCard(cardId);
        if (managedCard == null) {
            /* log */
        	System.out.println("CardServlet: could not find card:" + cardId);
            return;
        }
        String userCredential = (String)session.getAttribute("UserCredential");
        if (userCredential == null) {
        	userCredential = TokenServiceReference.USERNAME;
        	System.out.println("Warn: UserCredentialType is null. Using default: " + TokenServiceReference.USERNAME);
        }
        
        String tokenServiceEndpoint = "https://" + domainname + "/" + servletPath + "/" + "tokenservice";
        String mexEndpoint = "https://" + domainname + "/" + servletPath + "/" + "mex" + "/" + userCredential;

        InfoCard card = new InfoCard(cert, privateKey);
        card.setCardId("https://" + domainname + "/" + servletPath + "/" + "card/" + managedCard.getCardId());
        card.setCardName(managedCard.getCardName());
        card.setCardVersion(1);
        card.setIssuerName(domainname);
        card.setIssuer(tokenServiceEndpoint);

        // set card logo/image if available . . . if not available it will default to Milo :-)
        if (base64ImageFile != null) {
            card.setBase64BinaryCardImage(base64ImageFile);
        }


        XSDDateTime issued = new XSDDateTime();
        XSDDateTime expires = new XSDDateTime(525600);

        card.setTimeIssued(issued.getDateTime());
        card.setTimeExpires(expires.getDateTime());


        TokenServiceReference tsr = getTokenServiceReference(tokenServiceEndpoint, mexEndpoint, cert);
        tsr.setAuthType(userCredential, username);
        card.setTokenServiceReference(tsr);
        

        SupportedTokenList tokenList = new SupportedTokenList();
        SupportedToken token = new SupportedToken(SupportedToken.SAML11);
        tokenList.addSupportedToken(token);
        card.setTokenList(tokenList);

        SupportedClaimList claimList = getSupportedClaimList();
        card.setClaimList(claimList);

        card.setPrivacyPolicy(getPrivacyPolicyReference(domainname));


        PrintWriter out = response.getWriter();
        response.setContentType("application/soap+xml; charset=utf-8");

        try {
            out.println(card.toXML());
        } catch (SerializationException e) {
            throw new ServletException(e);
        }

        out.flush();
        out.close();
        return;


    }

    protected String getPrivacyPolicyReference(String domainname) {
        return "https://" + domainname + "/" + servletPath + "/PrivacyPolicy.xml";
    }

    protected TokenServiceReference getTokenServiceReference(String tokenServiceEndpoint, String mexEndpoint, X509Certificate cert) {
        TokenServiceReference tsr = new TokenServiceReference(tokenServiceEndpoint, mexEndpoint, cert);
        return tsr;
    }

    protected SupportedClaimList getSupportedClaimList() {
    	List<DbSupportedClaim> supportedClaims = supportedClaimsImpl.dbSupportedClaims();
        SupportedClaimList claimList = new SupportedClaimList();
        SupportedClaim supportedClaim = new SupportedClaim("PPID", org.xmldap.infocard.Constants.IC_NS_PRIVATEPERSONALIDENTIFIER, "your personal private identitfier");
        claimList.addSupportedClaim(supportedClaim);
    	for (DbSupportedClaim claim : supportedClaims) {
    		// TODO: support description. Axel
    		supportedClaim = new SupportedClaim(claim.displayTags[0].displayTag, claim.uri, "A Description");
    		claimList.addSupportedClaim(supportedClaim);
    	}
//        SupportedClaimList claimList = new SupportedClaimList();
//        SupportedClaim given = new SupportedClaim("GivenName", org.xmldap.infocard.Constants.IC_NS_GIVENNAME);
//        SupportedClaim sur = new SupportedClaim("Surname", org.xmldap.infocard.Constants.IC_NS_SURNAME);
//        SupportedClaim email = new SupportedClaim("EmailAddress", org.xmldap.infocard.Constants.IC_NS_EMAILADDRESS);
//        SupportedClaim ppid = new SupportedClaim("PPID", org.xmldap.infocard.Constants.IC_NS_PRIVATEPERSONALIDENTIFIER);
//        claimList.addSupportedClaim(given);
//        claimList.addSupportedClaim(sur);
//        claimList.addSupportedClaim(email);
//        claimList.addSupportedClaim(ppid);
        return claimList;
    }

    protected void dispatchUnauthenticatedRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        RequestDispatcher dispatcher = request.getRequestDispatcher("/cardmanager/");
        dispatcher.forward(request, response);
    }

    protected String getUsername(HttpSession session) {
        String username = (String) session.getAttribute("username");
        return username;
    }

    /**
     * Get the card identifier from the GET request URL (i.e. http://sts.xmldap.org/card/<b>MY-CARD-IDENTIFIER</b>.crd
     *
     * @param request
     * @return a card identifier that can be used to retrieve the actual card object from the card store
     */
    protected static String extractCardIdFromRequest(HttpServletRequest request) {
        String url = request.getRequestURL().toString();
        return extractCardIdFromUrl(url);
    }

    /**
     * Get the card identifier from the GET request URL (i.e. http://sts.xmldap.org/card/<b>MY-CARD-IDENTIFIER</b>.crd
     *
     * @param url
     * @return a card identifier that can be used to retrieve the actual card object from the card store
     */

    protected static String extractCardIdFromUrl(String url) {
        int index = url.lastIndexOf("/");
        index++;
        String cardID = url.substring(index, url.length() - 4);
        return cardID;
    }

    /**
     * Gets the file referenced by path and returns it's data as a Base64 string.
     * @param imageFilePathString path to the file
     * @return Base64 encoded image data (usaully a PNG)
     */
    protected String getImageFileEncodedAsBase64(String imageFilePathString) {
        String encodedFile;
        encodedFile = Base64.encodeFromFile(imageFilePathString);
        return encodedFile;
    }
    protected String getImageFileEncodedAsBase64(InputStream ins) {
        String encodedFile;
        encodedFile = Base64.encodeFromInputStream(ins, 0);
        return encodedFile;
    }
}
