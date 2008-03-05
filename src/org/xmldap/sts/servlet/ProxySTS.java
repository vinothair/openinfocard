/*
 * Copyright (c) 2008 Axel Nennker http://ignisvulpis.blogspot.com/
 * 
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
import org.xmldap.util.*;
import org.xmldap.sts.db.CardStorage;
import org.xmldap.sts.db.SupportedClaims;
import org.xmldap.sts.db.impl.CardStorageEmbeddedDBImpl;

import javax.servlet.ServletException;
import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPrivateKey;
import java.util.logging.Logger;

public class ProxySTS  extends HttpServlet {

	static Logger log = Logger.getLogger("org.xmldap.sts.servlet.proxySTS");

    RSAPrivateKey key = null;
    X509Certificate cert = null;
    String domain = null;
    private String servletPath = null;

    CardStorage storage = null;
    SupportedClaims supportedClaimsImpl = null;

    public void init(ServletConfig config) throws ServletException {

        super.init(config);

        try {

            PropertiesManager properties = new PropertiesManager(PropertiesManager.SECURITY_TOKEN_SERVICE, config.getServletContext());
            String keystorePath = properties.getProperty("keystore");
            if (keystorePath == null) {
            	throw new ServletException("keystorePath is null");
            }
            String keystorePassword = properties.getProperty("keystore.password");
            if (keystorePassword == null) {
            	throw new ServletException("keystorePassword is null");
            }
            String keyname = properties.getProperty("key.name");
            if (keyname == null) {
            	throw new ServletException("cert is null");
            }
            String keypassword = properties.getProperty("key.password");
            if (keypassword == null) {
            	throw new ServletException("keypassword is null");
            }
            domain = properties.getProperty("domain");
            if (domain == null) {
            	throw new ServletException("domainname is null");
            }
            servletPath = properties.getProperty("servletPath");
            if (servletPath == null) {
            	throw new ServletException("servletPath is null");
            }

            String supportedClaimsClass = properties.getProperty("supportedClaimsClass");
            supportedClaimsImpl = SupportedClaims.getInstance(supportedClaimsClass);
            storage = new CardStorageEmbeddedDBImpl(supportedClaimsImpl);
            
            KeystoreUtil keystore = new KeystoreUtil(keystorePath, keystorePassword);
            cert = keystore.getCertificate(keyname);

            key = (RSAPrivateKey) keystore.getPrivateKey(keyname,keypassword);


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

        storage.startup();

    }


    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    	HttpSession session = request.getSession();
    	session.getServletContext().log("proxySTS Servlet got a request");
    	
    	log.finest("proxySTS got a request");
        int contentLen = request.getContentLength();

        String requestXML = null;
        if (contentLen > 0) {

            DataInputStream inStream = new DataInputStream(request.getInputStream());
            byte[] buf = new byte[contentLen];
            inStream.readFully(buf);
            requestXML = new String(buf);

            System.out.println("proxySTS Request:\n" + requestXML);

        }

        //let's make a doc
        Builder parser = new Builder();
        Document req = null;
        try {
            req = parser.build(requestXML, "");
        } catch (nu.xom.ParsingException e) {
            e.printStackTrace();

        } catch (IOException e) {
            e.printStackTrace();
        }

        XPathContext context = Utils.buildRSTcontext();

        log.finest("We have a doc");

        String stsResponse = "not implemented";
        
        response.setContentType("application/soap+xml; charset=\"utf-8\"");
        response.setContentLength(stsResponse.length());
        log.finest("STS Response:\n " + stsResponse);
        PrintWriter out = response.getWriter();
        out.println(stsResponse);
        out.flush();
        out.close();

    }








}
