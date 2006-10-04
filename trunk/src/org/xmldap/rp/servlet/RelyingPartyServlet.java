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
 *     * Neither the name of the University of California, Berkeley nor the
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

package org.xmldap.rp.servlet;

import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.ParsingException;
import org.xmldap.exceptions.CryptoException;
import org.xmldap.exceptions.KeyStoreException;
import org.xmldap.rp.util.ClaimParserUtil;
import org.xmldap.rp.util.DecryptUtil;
import org.xmldap.rp.util.ValidationUtil;
import org.xmldap.util.KeystoreUtil;
import org.xmldap.util.ServletUtil;
import org.xmldap.util.Base64;
import org.xmldap.xmldsig.EnvelopedSignature;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.PrivateKey;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.security.cert.CertificateEncodingException;
import java.util.HashMap;


public class RelyingPartyServlet extends HttpServlet {


    private PrivateKey privateKey = null;
    private String cert = null;

    public void init(ServletConfig config) throws ServletException {
	super.init(config);

        try {
            ServletUtil su = new ServletUtil(config);
            KeystoreUtil keystore = su.getKeystore();
            privateKey = su.getPrivateKey();

            X509Certificate certificate = su.getCertificate();
            StringBuffer sb = new StringBuffer("-----BEGIN CERTIFICATE-----\n");
            sb.append(Base64.encodeBytes(certificate.getEncoded()));
            sb.append("\n-----END CERTIFICATE-----\n");
            cert = sb.toString();

        } catch (KeyStoreException e) {

            e.printStackTrace();
            throw new ServletException(e);

        } catch (CertificateEncodingException e) {
            throw new ServletException(e);
        }

    }


    private void processError(String message, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

        RequestDispatcher dispatcher = request.getRequestDispatcher("./error.jsp");
        request.setAttribute("error", message);
        dispatcher.forward(request,response);

    }


    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        PrintWriter out = response.getWriter();

        out.println("<html>\n" +
                "<body>\n" +
                "<b>The xmldap certificate:</b><br><br>" +
                "<textarea cols=80 rows=20>" + cert + "</textarea>\n" +
                "</body>\n" +
                "</html>");
        out.close();
        return;


    }


    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException {


        try {

            String encryptedXML = request.getParameter("xmlToken");
            if ((encryptedXML == null) || (encryptedXML.equals(""))) {
                processError("Sorry - you'll need to POST a security token.", request, response);
                return;
            }


            //decrypt it.
            DecryptUtil decrypter = new DecryptUtil();
            StringBuffer decryptedXML = decrypter.decryptXML(encryptedXML, privateKey);

	    if (decryptedXML == null) {
                processError("Sorry - could not decrypt your XML (perhaps the web server is using a different key than this servlet)?", request, response);
		return;
	    }

            //let's make a doc
            Builder parser = new Builder();
            Document assertion = null;
            try {
                assertion = parser.build(decryptedXML.toString(), "");
            } catch (ParsingException e) {
                processError(e.getMessage(), request, response);
                return;
            } catch (IOException e) {
                processError(e.getMessage(), request, response);
                return;
            }


            // no processError for the assertion contitions for now
    	    // if conditions are not met
        	// This has to be done before the signature validation
        	// because signature validation changes the assertion
            String verifiedConditions = ValidationUtil.validateConditions(assertion);
            try {
				String verifiedCertificate = ValidationUtil.validateCertificate(assertion);
                request.setAttribute("verifiedCertificate", verifiedCertificate);
			} catch (CertificateException e) {
				request.setAttribute("verifiedCertificate", e.getMessage());
			}

            boolean verified = false;
            try {
                verified = EnvelopedSignature.validate(assertion);
            } catch (CryptoException e) {
                processError(e.getMessage(), request, response);
                return;
            }


            if (!verified) {
                processError("Signature Validation Failed!", request, response);
                return;
            }


            //Parse the claims
            ClaimParserUtil claimParser = new ClaimParserUtil();
            HashMap claims = claimParser.parseClaims(assertion);

            if (claims.containsKey("emailaddress")) System.out.println("Login from: " + claims.get("emailaddress"));

            //Dispatch to UI
            RequestDispatcher dispatcher = request.getRequestDispatcher("./success.jsp");
            request.setAttribute("encryptedXML", encryptedXML);
            request.setAttribute("decryptedXML", decryptedXML.toString());
            if (verified) {
                request.setAttribute("verified", "TRUE");
            } else {
                request.setAttribute("verified", "FALSE");
            }
            request.setAttribute("verifiedConditions", verifiedConditions);
            request.setAttribute("claims", claims);
            dispatcher.forward(request,response);



        } catch (IOException e) {
            throw new ServletException(e);
        }
    }
}
