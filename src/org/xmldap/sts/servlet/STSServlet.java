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

package org.xmldap.sts.servlet;

import nu.xom.*;
import org.xmldap.util.Bag;
import org.xmldap.util.XSDDateTime;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.text.MessageFormat;


public class STSServlet  extends HttpServlet {


    private Bag parseRequest(String requestXML) throws ParsingException{

        //let's make a doc
        Builder parser = new Builder();
        Document req = null;
        try {
            req = parser.build(requestXML, "");
        } catch (ParsingException e) {
            e.printStackTrace();
            throw new ParsingException("Issue parsing request", e);

        } catch (IOException e) {
            e.printStackTrace();
            throw new ParsingException("Issue parsing request", e);
        }


        Bag requestElements = new Bag();

        XPathContext context = new XPathContext();
        context.addNamespace("s","http://www.w3.org/2003/05/soap-envelope");
        context.addNamespace("a", "http://www.w3.org/2005/08/addressing");
        context.addNamespace("wst", "http://schemas.xmlsoap.org/ws/2005/02/trust");
        context.addNamespace("wsid","http://schemas.microsoft.com/ws/2005/05/identity");
        context.addNamespace("o","http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd");
        context.addNamespace("u","http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd");

        Nodes ids = req.query("//a:MessageID",context);
        Element id = (Element) ids.get(0);
        String messageId = id.getValue();
        System.out.println("ID: " + messageId);
        requestElements.put("messageId", messageId);

        Nodes uns = req.query("//o:Username",context);
        Element un = (Element) uns.get(0);
        String userName = un.getValue();
        System.out.println("userName: " + userName);
        requestElements.put("userName", userName);


        Nodes pws = req.query("//o:Password",context);
        Element pw = (Element) pws.get(0);
        String password = pw.getValue();
        System.out.println("password: " + password);
        requestElements.put("password", password);


        Nodes types = req.query("//wst:RequestType",context);
        Element type = (Element) types.get(0);
        String requestType = type.getValue();
        System.out.println("requestType: " + requestType);
        requestElements.put("requestType", requestType);


        Nodes cids = req.query("//wsid:CardId",context);
        Element cid = (Element) cids.get(0);
        String cardId = cid.getValue();
        System.out.println("cardId: " + cardId);
        requestElements.put("cardId", cardId);


        Nodes cvs = req.query("//wsid:CardVersion",context);
        Element cv = (Element) cvs.get(0);
        String cardVersion = cv.getValue();
        System.out.println("CardVersion: " + cardVersion);
        requestElements.put("cardVersion", cardVersion);


        Nodes claims = req.query("//wsid:ClaimType",context);
        for (int i = 0; i < claims.size(); i++ ) {

            Element claimElm = (Element)claims.get(i);
            Attribute uri = claimElm.getAttribute("Uri");
            String claim = uri.getValue();
            System.out.println(claim);
            requestElements.put("claim", claim);

        }

        Nodes kts = req.query("//wst:KeyType",context);
        Element kt = (Element) kts.get(0);
        String keyType = kt.getValue();
        System.out.println("keyType: " + keyType);
        requestElements.put("keyType", keyType);

        Nodes tts = req.query("//wst:TokenType",context);
        Element tt = (Element) tts.get(0);
        String tokenType = tt.getValue();
        System.out.println("tokenType: " + tokenType);
        requestElements.put("tokenType", tokenType);


        /*
        TODO - add i18n
        Nodes rdts = req.query("//wsid:RequestDisplayToken",context);
        Element rd = (Element) rdts.get(0);
        String requestDisplayToken = rd.getValue();
        System.out.println("RequestDisplayToken: " + requestDisplayToken);
        requestElements.put("requestDisplayToken", requestDisplayToken);
        */


        return requestElements;


    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        System.out.println("STS got a request");
        int contentLen = request.getContentLength();

        String reqXML = null;
        if (contentLen > 0) {

            DataInputStream inStream = new DataInputStream(request.getInputStream());
            byte[] buf = new byte[contentLen];
            inStream.readFully(buf);
            reqXML = new String(buf);

            System.out.println("STS Request:");
            System.out.println(reqXML);

        }

        Bag requestElements = null;
        try {
            requestElements = parseRequest(reqXML);
        } catch (ParsingException e) {
            e.printStackTrace();
            //TODO - SOAP Fault
        }

        String type = (String)requestElements.get("requestType");
        String stsResponse = "";
        if ( type.equals("http://schemas.xmlsoap.org/ws/2005/02/trust/Issue")) {

            stsResponse = issue(requestElements);
        }


        response.setContentType("application/soap+xml; charset=\"utf-8\"");
        response.setContentLength(stsResponse.length());
        System.out.println("STS Response:\n " + stsResponse);
        PrintWriter out = response.getWriter();
        out.println(stsResponse);
        out.flush();
        out.close();

    }

    private String issue(Bag requestElements) throws IOException {


        //ServletContext application = getServletConfig().getServletContext();
        //InputStream in = application.getResourceAsStream("/issue.xml");
        InputStream in = new FileInputStream("/home/cmort/issue.xml");

        StringBuffer mexBuff = new StringBuffer();
        DataInputStream ins = new DataInputStream(in);

        while (in.available() !=0) {
            mexBuff.append(ins.readLine());
        }

        in.close();
        ins.close();

        MessageFormat issueResponse = new MessageFormat(mexBuff.toString());

        XSDDateTime now = new XSDDateTime();
        XSDDateTime later = new XSDDateTime(10);   //one week -what's up with window's time???
        String[] args = {(String)requestElements.get("messageId"),now.getDateTime(),later.getDateTime()};

        return issueResponse.format(args);


    }

}
