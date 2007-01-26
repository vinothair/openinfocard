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

import nu.xom.*;
import org.xmldap.exceptions.KeyStoreException;
import org.xmldap.exceptions.SerializationException;
import org.xmldap.infocard.ManagedToken;
import org.xmldap.util.Bag;
import org.xmldap.util.RandomGUID;
import org.xmldap.util.ServletUtil;
import org.xmldap.ws.WSConstants;
import org.xmldap.sts.db.CardStorage;
import org.xmldap.sts.db.DbSupportedClaim;
import org.xmldap.sts.db.DbSupportedClaims;
import org.xmldap.sts.db.ManagedCard;
import org.xmldap.sts.db.impl.CardStorageEmbeddedDBImpl;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPrivateKey;
import java.util.Locale;
import java.util.Set;


public class STSServlet  extends HttpServlet {


    private boolean DEBUG = false;
    RSAPrivateKey key;
    X509Certificate cert;
    private ServletUtil _su;
    CardStorage storage = new CardStorageEmbeddedDBImpl();

    public void init() throws ServletException {

        //Get my keystore
       try {

	   _su = new ServletUtil(getServletConfig());

           key = (RSAPrivateKey) _su.getPrivateKey();
           cert = _su.getCertificate();

       } catch (KeyStoreException e) {
           e.printStackTrace();
       }

       storage.startup();

    }



    private Bag parseToken(Element tokenXML) throws ParsingException{

        Bag tokenElements = new Bag();

        XPathContext context = new XPathContext();
        context.addNamespace("s","http://www.w3.org/2003/05/soap-envelope");
        context.addNamespace("a", "http://www.w3.org/2005/08/addressing");
        context.addNamespace("wst", "http://schemas.xmlsoap.org/ws/2005/02/trust");
        context.addNamespace("wsid","http://schemas.microsoft.com/ws/2005/05/identity");
        context.addNamespace("o","http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd");
        context.addNamespace("u","http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd");

        Nodes uns = tokenXML.query("//o:Username",context);
        Element un = (Element) uns.get(0);
        String userName = un.getValue();
        if (DEBUG) System.out.println("username: " + userName);
        tokenElements.put("username", userName);


        Nodes pws = tokenXML.query("//o:Password",context);
        Element pw = (Element) pws.get(0);
        String password = pw.getValue();
        tokenElements.put("password", password);

        return tokenElements;

    }



    private Bag parseRequest(Element requestXML) throws ParsingException {

        Bag requestElements = new Bag();


        XPathContext context = new XPathContext();
        context.addNamespace("s","http://www.w3.org/2003/05/soap-envelope");
        context.addNamespace("a", "http://www.w3.org/2005/08/addressing");
        context.addNamespace("wst", "http://schemas.xmlsoap.org/ws/2005/02/trust");
        context.addNamespace("wsid","http://schemas.xmlsoap.org/ws/2005/05/identity");
        context.addNamespace("o","http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd");
        context.addNamespace("u","http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd");

        Nodes cids = requestXML.query("//wsid:CardId",context);
        Element cid = (Element) cids.get(0);
        String cardIdUri = cid.getValue();
        
        String domainname = _su.getDomainName();
        String prefix = "https://" + domainname + "/sts/card/";
        String cardId = null;
        if (cardIdUri.startsWith(prefix)) {
        	cardId = cardIdUri.substring(prefix.length());
        } else {
        	throw new ParsingException("Expected:"+prefix+", but found:"+cardIdUri);
        }
        if (DEBUG) System.out.println("cardId: " + cardId);

        requestElements.put("cardId", cardId);


        Nodes cvs = requestXML.query("//wsid:CardVersion",context);
        Element cv = (Element) cvs.get(0);
        String cardVersion = cv.getValue();
        if (DEBUG) System.out.println("CardVersion: " + cardVersion);
        requestElements.put("cardVersion", cardVersion);


        Nodes claims = requestXML.query("//wsid:ClaimType",context);
        for (int i = 0; i < claims.size(); i++ ) {

            Element claimElm = (Element)claims.get(i);
            Attribute uri = claimElm.getAttribute("Uri");
            String claim = uri.getValue();
            if (DEBUG) System.out.println(claim);
            requestElements.put("claim", claim);

        }

        Nodes kts = requestXML.query("//wst:KeyType",context);
        Element kt = (Element) kts.get(0);
        String keyType = kt.getValue();
        if (DEBUG) System.out.println("keyType: " + keyType);
        requestElements.put("keyType", keyType);

        Nodes tts = requestXML.query("//wst:TokenType",context);
        Element tt = (Element) tts.get(0);
        String tokenType = tt.getValue();
        if (DEBUG) System.out.println("tokenType: " + tokenType);
        requestElements.put("tokenType", tokenType);

        return requestElements;


    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        if (DEBUG) System.out.println("STS got a request");
        int contentLen = request.getContentLength();

        String requestXML = null;
        if (contentLen > 0) {

            DataInputStream inStream = new DataInputStream(request.getInputStream());
            byte[] buf = new byte[contentLen];
            inStream.readFully(buf);
            requestXML = new String(buf);

            if (DEBUG) System.out.println("STS Request:");
            if (DEBUG) System.out.println(requestXML);

        }

        //let's make a doc
        Builder parser = new Builder();
        Document req = null;
        try {
            req = parser.build(requestXML, "");
        } catch (ParsingException e) {
            e.printStackTrace();

        } catch (IOException e) {
            e.printStackTrace();
        }



        if (DEBUG) System.out.println("We have a doc");

        XPathContext context = new XPathContext();
        context.addNamespace("s","http://www.w3.org/2003/05/soap-envelope");
        context.addNamespace("a", "http://www.w3.org/2005/08/addressing");
        context.addNamespace("o","http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd");
        context.addNamespace("u","http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd");
        context.addNamespace("wst", "http://schemas.xmlsoap.org/ws/2005/02/trust");


        Nodes tokenElm = req.query("//o:UsernameToken",context);
        Element token = (Element) tokenElm.get(0);
        if (DEBUG) System.out.println("Token:" + token.toXML());


        Nodes rsts = req.query("//wst:RequestSecurityToken",context);
        Element rst = (Element) rsts.get(0);
        if (DEBUG) System.out.println("RST: " + rst.toXML());


        Bag tokenElements = null;
        try {
            tokenElements = parseToken(token);
        } catch (ParsingException e) {
            e.printStackTrace();
            //TODO - SOAP Fault
        }


        boolean isUser = authenticate(tokenElements);

        //TODO = SOAPFaulr
        if (!isUser) return;


        Bag requestElements = null;
        try {
            requestElements = parseRequest(rst);
        } catch (ParsingException e) {
            e.printStackTrace();
            //TODO - SOAP Fault
        }


        Locale clientLocale = request.getLocale();
        String stsResponse = issue(requestElements, clientLocale);

        response.setContentType("application/soap+xml; charset=\"utf-8\"");
        response.setContentLength(stsResponse.length());
        if (DEBUG) System.out.println("STS Response:\n " + stsResponse);
        PrintWriter out = response.getWriter();
        out.println(stsResponse);
        out.flush();
        out.close();

    }


    private boolean authenticate(Bag tokenElements) {
        String username = (String) tokenElements.get("username");
        String password = (String) tokenElements.get("password");
        boolean isUser = storage.authenticate(username,password);
        System.out.println("STS Authenticated: " + username  + ":" + isUser );
        return isUser;
    }

    private String issue(Bag requestElements,  Locale clientLocale) throws IOException {


        ManagedCard card = storage.getCard((String)requestElements.get("cardId"));
        if (card == null ) throw new IOException("Unable to read card: " + (String)requestElements.get("cardId"));

        System.out.println("STS Issuing Managed Card " + (String)requestElements.get("cardId") + " for " + card.getClaim(org.xmldap.infocard.Constants.IC_NS_EMAILADDRESS));


        Element envelope = new Element(WSConstants.SOAP_PREFIX + ":Envelope", WSConstants.SOAP12_NAMESPACE);
        envelope.addNamespaceDeclaration(WSConstants.WSA_PREFIX, WSConstants.WSA_NAMESPACE_05_08);
        envelope.addNamespaceDeclaration(WSConstants.WSU_PREFIX, WSConstants.WSU_NAMESPACE);
        envelope.addNamespaceDeclaration(WSConstants.WSSE_PREFIX, WSConstants.WSSE_NAMESPACE_OASIS_10);
        envelope.addNamespaceDeclaration(WSConstants.TRUST_PREFIX, WSConstants.TRUST_NAMESPACE_05_02);
        envelope.addNamespaceDeclaration("ic", "http://schemas.xmlsoap.org/ws/2005/05/identity");

        Element header = new Element(WSConstants.SOAP_PREFIX + ":Header", WSConstants.SOAP12_NAMESPACE);
        Element body = new Element(WSConstants.SOAP_PREFIX + ":Body", WSConstants.SOAP12_NAMESPACE);


        envelope.appendChild(header);
        envelope.appendChild(body);



        //Build body
        Element rstr = new Element(WSConstants.TRUST_PREFIX + ":RequestSecurityTokenResponse", WSConstants.TRUST_NAMESPACE_05_02);
        Attribute context = new Attribute("Context","ProcessRequestSecurityToken");
        rstr.addAttribute(context);

        Element tokenType = new Element(WSConstants.TRUST_PREFIX + ":TokenType", WSConstants.TRUST_NAMESPACE_05_02);
        tokenType.appendChild("urn:oasis:names:tc:SAML:1.0:assertion");
        rstr.appendChild(tokenType);

        Element requestType = new Element(WSConstants.TRUST_PREFIX + ":RequestType", WSConstants.TRUST_NAMESPACE_05_02);
        requestType.appendChild("http://schemas.xmlsoap.org/ws/2005/02/trust/Issue");
        rstr.appendChild(requestType);

        Element rst = new Element(WSConstants.TRUST_PREFIX + ":RequestedSecurityToken", WSConstants.TRUST_NAMESPACE_05_02);

        ManagedToken token = new ManagedToken(cert,key);

        Set<String> cardClaims = card.getClaims();
        for (String claim : cardClaims) {
        	token.setClaim(claim, card.getClaim(claim));
        }
        
        token.setPrivatePersonalIdentifier(card.getPrivatePersonalIdentifier());
        token.setValidityPeriod(1, 10);
        String domainname = _su.getDomainName();
        token.setIssuer("https://" + domainname + "/sts/tokenservice");
        
        RandomGUID uuid = new RandomGUID();

        try {
            rst.appendChild(token.serialize(uuid));
        } catch (SerializationException e) {
            e.printStackTrace();
        }

        rstr.appendChild(rst);

        Element requestedAttachedReference = new Element(WSConstants.TRUST_PREFIX + ":RequestedAttachedReference", WSConstants.TRUST_NAMESPACE_05_02);
        Element securityTokenReference = new Element(WSConstants.WSSE_PREFIX + ":SecurityTokenReference", WSConstants.WSSE_NAMESPACE_OASIS_10);
        Element keyIdentifier = new Element(WSConstants.WSSE_PREFIX + ":KeyIdentifier", WSConstants.WSSE_NAMESPACE_OASIS_10);
        Attribute valueType = new Attribute("ValueType","http://docs.oasis-open.org/wss/oasis-wss-saml-token-profile-1.0#SAMLAssertionID");
        keyIdentifier.addAttribute(valueType);
        keyIdentifier.appendChild("uuid-" + uuid.toString());
        securityTokenReference.appendChild(keyIdentifier);
        requestedAttachedReference.appendChild(securityTokenReference);
        rstr.appendChild(requestedAttachedReference);

        Element requestedUnAttachedReference = new Element(WSConstants.TRUST_PREFIX + ":RequestedUnattachedReference", WSConstants.TRUST_NAMESPACE_05_02);
        Element securityTokenReference1 = new Element(WSConstants.WSSE_PREFIX + ":SecurityTokenReference", WSConstants.WSSE_NAMESPACE_OASIS_10);
        Element keyIdentifier1 = new Element(WSConstants.WSSE_PREFIX + ":KeyIdentifier", WSConstants.WSSE_NAMESPACE_OASIS_10);
        Attribute valueType1 = new Attribute("ValueType","http://docs.oasis-open.org/wss/oasis-wss-saml-token-profile-1.0#SAMLAssertionID");
        keyIdentifier1.addAttribute(valueType1);
        keyIdentifier1.appendChild("uuid-" + uuid.toString());
        securityTokenReference1.appendChild(keyIdentifier1);
        requestedUnAttachedReference.appendChild(securityTokenReference1);
        rstr.appendChild(requestedUnAttachedReference);


        Element requestedDisplayToken = new Element(WSConstants.INFOCARD_PREFIX + ":RequestedDisplayToken", WSConstants.INFOCARD_NAMESPACE);
        Element displayToken = new Element(WSConstants.INFOCARD_PREFIX + ":DisplayToken", WSConstants.INFOCARD_NAMESPACE);
        Attribute lang = new Attribute("xml:lang","http://www.w3.org/XML/1998/namespace","en");
        displayToken.addAttribute(lang);
        requestedDisplayToken.appendChild(displayToken);

        for (String uri : cardClaims) {
        	String value = card.getClaim(uri);
        	DbSupportedClaim dbClaim = DbSupportedClaims.getClaimByUri(uri);
        	String displayTag = dbClaim.getDisplayTag(clientLocale);
            addDisplayClaim(
            		displayToken, 
            		uri, 
            		displayTag, 
            		value);
        }

        addDisplayClaim(displayToken, org.xmldap.infocard.Constants.IC_NS_PRIVATEPERSONALIDENTIFIER, "PPID", card.getPrivatePersonalIdentifier());

        rstr.appendChild(requestedDisplayToken);

        body.appendChild(rstr);

        return envelope.toXML();
    }



	/**
	 * @param card
	 * @param displayToken
	 */
	private void addDisplayClaim(Element displayToken, String claimUri, String claimName, String claimValue) {
		Element displayClaim = new Element(WSConstants.INFOCARD_PREFIX + ":DisplayClaim", WSConstants.INFOCARD_NAMESPACE);
        Attribute uri = new Attribute("URI", claimUri);
        displayClaim.addAttribute(uri);
        Element displayTag = new Element(WSConstants.INFOCARD_PREFIX + ":DisplayTag", WSConstants.INFOCARD_NAMESPACE);
        displayTag.appendChild(claimName);
        Element displayValue = new Element(WSConstants.INFOCARD_PREFIX + ":DisplayValue", WSConstants.INFOCARD_NAMESPACE);
        displayValue.appendChild(claimValue);
        displayClaim.appendChild(displayTag);
        displayClaim.appendChild(displayValue);
        displayToken.appendChild(displayClaim);
	}


}
