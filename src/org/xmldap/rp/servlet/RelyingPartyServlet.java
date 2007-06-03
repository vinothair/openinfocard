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
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY
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

package org.xmldap.rp.servlet;

import org.xmldap.exceptions.InfoCardProcessingException;
import org.xmldap.exceptions.KeyStoreException;
import org.xmldap.rp.Token;
import org.xmldap.util.KeystoreUtil;
import org.xmldap.util.PropertiesManager;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;


public class RelyingPartyServlet extends HttpServlet {

    private PrivateKey privateKey = null;
    private X509Certificate cert = null;

    public void init(ServletConfig config) throws ServletException {

        super.init(config);

        try {
            
            PropertiesManager properties = new PropertiesManager(PropertiesManager.RELYING_PARTY, config.getServletContext());
            String keystorePath = properties.getProperty("keystore");
            String keystorePassword = properties.getProperty("keystore-password");
            String key = properties.getProperty("key");
            String keyPassword = properties.getProperty("key-password");

            KeystoreUtil keystore = new KeystoreUtil(keystorePath, keystorePassword);
            privateKey = keystore.getPrivateKey(key,keyPassword);
            cert = keystore.getCertificate(key);

        } catch (IOException e) {
            throw new ServletException(e);
        } catch (KeyStoreException e) {
            throw new ServletException(e);
        }

    }


    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        PrintWriter out = response.getWriter();

        out.println("<html><title>Sample Relying Party</title><style>BODY {color:#000;font-family: verdana, arial, sans-serif;}</style>\n" +
                "<body>\n" +
                "<b>Your certificate:</b><br><br>" +
                "<textarea cols=80 rows=20>" + cert + "</textarea>\n" +
                "</body>\n" +
                "</html>");
        out.close();
        return;

    }




    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        PrintWriter out = response.getWriter();

        try {

            out.println("<html><title>Sample Relying Party</title><style>BODY {color:#000;font-family: verdana, arial, sans-serif;}</style><body>");

            String encryptedXML = request.getParameter("xmlToken");
            if ((encryptedXML == null) || (encryptedXML.equals(""))) {
                out.println("Sorry - you did not POST a security token.  Something went wrong with your selector");
                out.close();
                return;
            }

            Token token = new Token(encryptedXML, privateKey);

            out.println("<h2>Here's what you posted:</h2>");
            out.println("<p><textarea rows='10' cols='150'>" + token.getEncryptedToken() + "</textarea></p>");

            out.println("<h2>And here's the decrypted token:</h2>");
            out.println("<p><textarea rows='10' cols='150'>" + token.getDecryptedToken() + "</textarea></p>");

            out.println("<h2>Valid Signature: " + token.isSignatureValid() + "</h2>");
            out.println("<h2>Valid Conditions: " + token.isConditionsValid() + "</h2>");
            try {
            	X509Certificate cert = token.getCertificateOrNull();
            	if (cert != null) {
            		out.println("<h2>Valid Certificate: " + token.isCertificateValid() + "</h2>");
            	} else {
            		out.println("<h2>No Certificate in Token</h2>");
            	}

            } catch (InfoCardProcessingException e) {

               out.println("<h2>Valid Certificate: " + e.getMessage() + "</h2>");

            }
            Map claims = token.getClaims();
            out.println("<h2>You provided the following claims:</h2>");
            Set keys = claims.keySet();
            Iterator keyIter = keys.iterator();
            while (keyIter.hasNext()){
                String name = (String) keyIter.next();
                String value = (String) claims.get(name);
                out.println(name + ": " + value + "<br>");
            }


        } catch (InfoCardProcessingException e) {
            e.printStackTrace();
            out.println(e.getMessage());
        } finally {
            out.close();
        }

    }
}
