package org.xmldap.rp.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class OpenIdConsumerServlet extends HttpServlet {

    private static String escapeHtmlEntities(String html) {
		StringBuffer result = new StringBuffer();
		for (int i = 0; i < html.length(); i++) {
			char ch = html.charAt(i);
			if (ch == '<') {
				result.append("&lt;");
			} else if (ch == '>') {
				result.append("&gt;");
			} else if (ch == '\"') {
				result.append("&quot;");
			} else if (ch == '\'') {
				result.append("&#039;");
			} else if (ch == '&') {
				result.append("&amp;");
			} else {
				result.append(ch);
			}
		}
		return result.toString();
	}

	public void init(ServletConfig config) throws ServletException {

        super.init(config);
	}
	
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	PrintWriter out = null;
    	try {
    		String AcceptHeaderValue = request.getHeader("Accept");
    		if ((AcceptHeaderValue != null) && (AcceptHeaderValue.indexOf("application/xhtml+xml") >= 0)) {
    			response.setContentType("application/xhtml+xml");
    		}
	        out = response.getWriter();
	    	out.println("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">");
	
	        out.println("<html xmlns=\"http://www.w3.org/1999/xhtml\" xml:lang=\"en\" lang=\"en\"><head><title>Sample Relying Party</title><style>BODY {color:#000;font-family: verdana, arial, sans-serif;}</style></head>\n");
	        out.println("<body>\n");
	        out.println("<b>get:</b><br/><br/>" +
	        		"<p>" + escapeHtmlEntities(request.getRequestURL().toString()) + "</p>");
	        if (request.getQueryString() != null) {
	        	String[] nvps = request.getQueryString().split("&");
	        	for (int i=0; i<nvps.length; i++) {
	        		String nvp = nvps[i];
	        		String[] nameValue = nvp.split("=",2);
	        		if (nameValue.length == 2) {
	        			out.println("<p>" + escapeHtmlEntities(nameValue[0]) + "=" + escapeHtmlEntities(nameValue[1]) + "</p>");
	        		} else {
	        			out.println("<p>" + escapeHtmlEntities(nameValue[0]) + "</p>");
	        		}
	        	}
	        }
	        out.println("</body>\n" +
	                "</html>");
    	} finally {
    		if (out != null) {
    			out.close();
    		}
    	}
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	PrintWriter out = null;
    	try {
    		String xmlTokenParam = request.getParameter("xmlToken");
            if ((xmlTokenParam == null) || (xmlTokenParam.equals(""))) {
        		String AcceptHeaderValue = request.getHeader("Accept");
        		if ((AcceptHeaderValue != null) && (AcceptHeaderValue.indexOf("application/xhtml+xml") >= 0)) {
        			response.setContentType("application/xhtml+xml");
        		}
    	        out = response.getWriter();
            	out.println("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">");

    	        out.println("<html xmlns=\"http://www.w3.org/1999/xhtml\" xml:lang=\"en\" lang=\"en\"><head><title>Sample Relying Party</title><style>BODY {color:#000;font-family: verdana, arial, sans-serif;}</style></head>\n");
    	        out.println("<body>\n");
                out.println("Sorry - you did not POST a security token.  Something went wrong with your selector");
    	        out.println("<b>POST:</b><br/><br/>" +
    	                request.getRequestURL().toString() +
    	                ((request.getQueryString() != null) ? request.getQueryString() : ""));
    	        out.println("</body>\n" +
    	                "</html>");
            } else {
            	System.out.println("OpenIdConsumerServlet: xmlToken=" + xmlTokenParam);
            	response.sendRedirect(xmlTokenParam);
            }
	} finally {
    		if (out != null) {
    			out.close();
    		}
    	}
    }
}
