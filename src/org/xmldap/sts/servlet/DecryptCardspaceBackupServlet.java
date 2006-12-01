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

import nu.xom.Document;
import nu.xom.Serializer;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import java.io.*;
import java.util.List;
import java.util.Iterator;

import org.xmldap.infocard.roaming.EncryptedStore;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.FileItem;


public class DecryptCardspaceBackupServlet extends HttpServlet {


    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        InputStream store = null;
        String password = null;

        boolean isMultipart = ServletFileUpload.isMultipartContent(request);

        if ( ! isMultipart ) {
            PrintWriter out = response.getWriter();
            out.println("Invalid form posting - not multipart");
            out.flush();
            out.close();
            return;
        }


        DiskFileItemFactory factory = new DiskFileItemFactory();
        factory.setSizeThreshold(500000);
        ServletFileUpload upload = new ServletFileUpload(factory);
        upload.setSizeMax(1000000);
        try {
            List items = upload.parseRequest(request);
            Iterator iter = items.iterator();
            while (iter.hasNext()) {
                FileItem item = (FileItem) iter.next();

                if (item.isFormField()) {
                    password = item.getString();

                } else {

                    store = item.getInputStream();
                }
            }


        } catch (FileUploadException e) {
            PrintWriter out = response.getWriter();
            out.println("There was an error receiving your backup file.");
            out.flush();
            out.close();
            return;
        }


        if ( store.available() <= 0 ) {
            PrintWriter out = response.getWriter();
            out.println("You must provide a valid backup file");
            out.flush();
            out.close();
            return;
        }



        if ( (password == null) || ( password.equals(""))) {
            PrintWriter out = response.getWriter();
            out.println("You must provide a password");
            out.flush();
            out.close();
            return;
        }

        System.out.println(store);


        EncryptedStore encryptedStore = new EncryptedStore();
        Document roamingStore = null;
        try {
            roamingStore = encryptedStore.decryptStore(store, password);
        } catch (Exception e) {
            PrintWriter out = response.getWriter();
            out.println("There was an error decrypting your backup: " + e.getMessage());
            e.printStackTrace();
            out.flush();
            out.close();
            return;
        }
        
        response.setContentType("text/xml");
        OutputStream out = response.getOutputStream();
        Serializer serializer = null;
        try {
            serializer = new Serializer(out, "UTF8");
            serializer.setIndent(4);
            serializer.setMaxLength(64);
            serializer.setPreserveBaseURI(false);
            serializer.write(roamingStore);
            serializer.flush();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        out.flush();
        out.close();

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        PrintWriter out = response.getWriter();
        out.println("<html><head>");
        out.println("\n" +
                "    <style>\n" +
                "    BODY {background: #FFFFFF;\n" +
                "         color:#000000;\n" +
                "         font-family: verdana, arial, sans-serif;}\n" +
                "\n" +
                "        h2 { color:#000000;\n" +
                "         font-family: verdana, arial, sans-serif;}" +
                "</style>" +
                "<title>CardSpace Backup Decrypt</title>" +
                "</head>" +
                "<body>");
        out.println("<h2>Take a peek inside Cardspace Backups</h2>\n" +
                "\n" +
                "<form action=\"\" method=\"POST\" ENCTYPE=\"multipart/form-data\" >\n" +
                "<table border=0 cellpadding=5><tr>" +
                "<td>Select a Cardspace backup file: </td><td><input name=\"store\" type=\"file\" size=\"50\"><br></td></tr>\n" +
                "<tr><td>Password used to secure the file:</td><td><input name=\"password\" type=\"password\" size=\"50\"><br></td></tr>\n" +
                "<tr><td colspan=2><input type=\"submit\" value=\"Decrypt your backup\"></td></tr></table>\n" +
                "</form>" +
                "</body>");
        out.flush();
        out.close();

    }
}
