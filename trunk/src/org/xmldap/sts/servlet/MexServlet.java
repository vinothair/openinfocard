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
import org.xmldap.util.Base64;
import org.xmldap.util.KeystoreUtil;
import org.xmldap.util.ServletUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.text.MessageFormat;


public class MexServlet extends HttpServlet {


    private String cert = null;
    private String mexFile = null;


    public void init() throws ServletException {

        ServletUtil su = new ServletUtil(getServletConfig());

        mexFile = su.getMexFilePathString();

        try {

            KeystoreUtil keystore = su.getKeystore();
            X509Certificate certificate = su.getCertificate();

            try {
                cert = Base64.encodeBytes(certificate.getEncoded());
            } catch (CertificateEncodingException e) {
                throw new ServletException(e);
            }

        } catch (KeyStoreException e) {

            e.printStackTrace();
            throw new ServletException(e);

        }



    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        System.out.println("MEX got a request");


        int contentLen = request.getContentLength();
        DataInputStream inStream = new DataInputStream(request.getInputStream());
        byte[] buf = new byte[contentLen];
        inStream.readFully(buf);
        String mexRequest = new String(buf);

        //let's make a doc
        Builder parser = new Builder();
        Document mex = null;
        try {
            mex = parser.build(mexRequest, "");
        } catch (ParsingException e) {
            throw new ServletException(e);
        } catch (IOException e) {
            throw new ServletException(e);
        }


        XPathContext context = new XPathContext();
        context.addNamespace("s", "http://www.w3.org/2003/05/soap-envelope");
        context.addNamespace("a", "http://www.w3.org/2005/08/addressing");


        Nodes messageIDs = mex.query("//a:MessageID", context);
        Element messageID = (Element) messageIDs.get(0);


        String[] args = {messageID.getValue(), cert};

        //ServletContext application = getServletConfig().getServletContext();
        //InputStream in = application.getResourceAsStream("/mex.xml");
        InputStream in = new FileInputStream(mexFile);



        StringBuffer mexBuff = new StringBuffer();
        BufferedReader ins = new BufferedReader(new InputStreamReader(in));
        try {

            while (in.available() !=0) {
                mexBuff.append(ins.readLine());
            }

            in.close();
            ins.close();

        } catch (IOException e) {
            throw new ServletException(e);
        }

        MessageFormat mexResponse = new MessageFormat(mexBuff.toString());

        String resp = mexResponse.format(args);
        response.setContentLength(resp.length());
        response.setContentType("application/soap+xml; charset=utf-8");

        PrintWriter out = response.getWriter();
        out.println(resp);
        out.flush();
        out.close();
        System.out.println("MEX replied");


    }

}
