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

import nu.xom.*;
import org.xmldap.exceptions.KeyStoreException;
import org.xmldap.exceptions.CryptoException;
import org.xmldap.util.KeystoreUtil;
import org.xmldap.ws.WSConstants;
import org.xmldap.rp.util.DecryptUtil;
import org.xmldap.rp.util.ValidationUtil;
import org.xmldap.rp.util.ClaimParserUtil;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Set;
import java.util.Iterator;


public class RelyingPartyServlet extends HttpServlet {

    private KeystoreUtil keystore = null;

    public void init(ServletConfig config) throws ServletException {

        keystore = null;
        try {

            keystore = new KeystoreUtil("/home/cmort/apps/apache-tomcat-5.5.17/conf/xmldap_org.jks", "password");

        } catch (KeyStoreException e) {
            throw new ServletException(e);
        }


    }


    private void processError(String message, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

        RequestDispatcher dispatcher = request.getRequestDispatcher("./error.jsp");
        request.setAttribute("error", message);
        dispatcher.forward(request,response);

    }


    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException {


        try {

            String encryptedXML = request.getParameter("xmlToken");
            if (encryptedXML == null) processError("Sorry - you'll need to POST a security token.", request, response);


            //decrypt it.
            DecryptUtil decrypter = new DecryptUtil(keystore);
            StringBuffer decryptedXML = decrypter.decryptXML(encryptedXML, "xmldap", "password");



            //let's make a doc
            Builder parser = new Builder();
            Document assertion = null;
            try {
                assertion = parser.build(decryptedXML.toString(), "");
            } catch (ParsingException e) {
                processError(e.getMessage(), request, response);
            } catch (IOException e) {
                processError(e.getMessage(), request, response);
            }


            //Validate it
            ValidationUtil validator = new ValidationUtil();
            boolean verified = false;
            try {
                verified = validator.validate(assertion);
            } catch (CryptoException e) {
                processError(e.getMessage(), request, response);
            }


            if (!verified) processError("Signiture Validation Failed!", request, response);



            //Parse the claims
            ClaimParserUtil claimParser = new ClaimParserUtil();
            HashMap claims = claimParser.parseClaims(assertion);


            //Dispatch to UI
            RequestDispatcher dispatcher = request.getRequestDispatcher("./success.jsp");
            request.setAttribute("encryptedXML", encryptedXML);
            request.setAttribute("decryptedXML", decryptedXML.toString());
            if (verified) {
                request.setAttribute("verified", "TRUE");
            } else {
                request.setAttribute("verified", "FALSE");
            }
            request.setAttribute("claims", claims);
            dispatcher.forward(request,response);



        } catch (IOException e) {
            throw new ServletException(e);
        }
    }
}