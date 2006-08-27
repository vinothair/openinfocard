/** \file
 *
 * Oct 11, 2004
 *
 * Copyright Ian Kaplan 2004, Bear Products International
 *
 * You may use this code for any purpose, without restriction,
 * including in proprietary code for which you charge a fee.
 * In using this code you acknowledge that you understand its
 * function completely and accept all risk in its use.
 *
 * @author Ian Kaplan, www.bearcave.com, iank@bearcave.com
 */
package org.xmldap.rp.servlet;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Calendar;
import java.util.Enumeration;

/**
 * EchoServlet
 * Oct 11, 2004
 *
 * @author Ian Kaplan, www.bearcave.com, iank@bearcave.com
 */
public class EchoServlet extends HttpServlet {

    /**
     * @return a string containing the current date and time, down to the millisecond
     *         The data/time is for the local time zone.
     */
    public String getTimeAndDate() {
        StringBuffer buf = new StringBuffer();
        Calendar cal = Calendar.getInstance();
        int day = cal.get(Calendar.DAY_OF_MONTH);
        int month = cal.get(Calendar.MONTH) + 1;
        int year = cal.get(Calendar.YEAR);
        int hour24 = cal.get(Calendar.HOUR_OF_DAY);
        int min = cal.get(Calendar.MINUTE);
        int sec = cal.get(Calendar.SECOND);
        int msec = cal.get(Calendar.MILLISECOND);

        buf.append(month);
        buf.append('/');
        buf.append(day);
        buf.append('/');
        buf.append(year);
        buf.append(' ');
        buf.append(hour24);
        buf.append(':');
        buf.append(min);
        buf.append(':');
        buf.append(sec);
        buf.append(':');
        buf.append(msec);

        return buf.toString();
    } // getTimeAndDate

    int mCount = 0;

    public synchronized int incrCount() {
        mCount = mCount + 1;
        return mCount;
    }

    public void init(ServletConfig config)
            throws ServletException {
        super.init(config);

        String name = getClass().getName();
        String timeStamp = getTimeAndDate();

        System.out.println(name + "::init() time = " + timeStamp);
        Enumeration ipn = config.getInitParameterNames();
        if (ipn.hasMoreElements()) {
            System.out.println("Config parameters:");
            while (ipn.hasMoreElements()) {
                String s = (String) ipn.nextElement();
                String v = config.getInitParameter(s);
                System.out.println(s + " = " + v);
            } // while
        }
    } // init


    public void doPost(HttpServletRequest request,
                       HttpServletResponse response) {
        int contentLen = request.getContentLength();

        if (contentLen > 0) {

            try {
                DataInputStream inStream = new DataInputStream(request.getInputStream());
                byte[] buf = new byte[contentLen];
                inStream.readFully(buf);
                String inString = new String(buf);
                response.setContentType("text/html");
                PrintWriter writer = response.getWriter();
                Enumeration headerNames = request.getHeaderNames();
                while (headerNames.hasMoreElements()) {

                    String headerName = (String) headerNames.nextElement();
                    Enumeration headers = request.getHeaders(headerName);
                    writer.println("<b>" + headerName + ":</b>");
                    System.out.println(headerName + ":");
                    while (headers.hasMoreElements()) {

                        String thisHeaderVal = (String) headers.nextElement();
                        writer.println(thisHeaderVal + "<br>");
                        System.out.println(thisHeaderVal);

                    }
                    writer.println("<br>");

                }

                writer.println("<hr>");
                writer.println(inString);
                System.out.println(inString);

            } catch (IOException e) {
                System.out.println("EchoServlet::doPost: IOException = " + e.getMessage());
            }
        }
    } // doPost


    private void displayPage(HttpServletResponse response) {
        try {
            response.setContentType("text/html");
            PrintWriter out = response.getWriter();
            out.println("<HTML>");
            out.println("<HEAD>");
            out.println("<TITLE>EchoServlet</TITLE>");
            out.println("</HEAD>");
            out.println("<BODY background=\"http://www.bearcave.com/images/paperbk.gif\">");
            out.println("<br/>");
            out.println("<h1>");
            out.println("<i>");
            out.println("Greetings from the EchoServlet!");
            out.println("</i>");
            out.println("</h1>");
            out.println("</BODY>");
            out.println("</HTML>");
        } catch (IOException e) {
            System.out.println("EchoServlet::doGet: IOException = " + e.getMessage());
        }
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) {
        displayPage(response);
    } // doGet

}
