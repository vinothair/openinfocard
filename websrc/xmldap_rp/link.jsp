<%@ page import="org.xmldap.util.PropertiesManager"%>
<%@ page import="org.xmldap.util.KeystoreUtil"%>
<%@ page import="java.security.PrivateKey"%>
<%@ page import="org.xmldap.rp.Token"%>
<%@ page import="java.util.Iterator"%>
<%@ page import="java.util.Map"%>
<%@ page import="java.util.HashMap"%>
<%@ page import="java.util.Set"%>

<%!

	String escapeHtmlEntities(String html) {
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
%>
<%
 org.xmldap.util.PropertiesManager properties = new org.xmldap.util.PropertiesManager(org.xmldap.util.PropertiesManager.RELYING_PARTY, config.getServletContext());
 String requiredClaims = properties.getProperty("requiredClaims"); 
 String optionalClaims = properties.getProperty("optionalClaims"); 

 String key = properties.getProperty("key");
 String keystorePath = properties.getProperty("keystore");
 String keystorePassword = properties.getProperty("keystore-password");
 String keyPassword = properties.getProperty("key-password");

 KeystoreUtil keystore = new KeystoreUtil(keystorePath, keystorePassword);
 PrivateKey privateKey = keystore.getPrivateKey(key,keyPassword);
 String token = null;
 Map allClaims = null;
 
 String queryString = request.getQueryString();
 boolean clearPrivacyData = false;
 
 if (queryString != null) {
	 System.out.println("queryString=" + queryString);
	 if (queryString.indexOf("xmldap_rp.xrds") == 0) {
		 System.out.println("queryString.indexOf(\"xmldap_rp.xrds\") = " + queryString.indexOf("xmldap_rp.xrds"));
		 String xrds = properties.getProperty("xrds"); 
		 if (xrds != null) {
			 response.setContentType("application/xml+xrds");
			 System.out.println("reading xrds : " + xrds);
			 java.io.InputStream fis = getServletContext().getResourceAsStream(xrds);
		//	 java.io.FileInputStream fis = new java.io.FileInputStream(xrds);
			 java.io.BufferedReader ins = new java.io.BufferedReader(new java.io.InputStreamReader(fis));
			 try {
				 String line = ins.readLine();
					while (line != null) {
						out.println(line);
						line = ins.readLine();
					}
				} catch (java.io.IOException e) {
					throw new ServletException(e);
				}
				finally {
					fis.close();
					ins.close();
				}
		} else {
			System.out.println("ERROR: resource not found: " + xrds);
			// TODO return HTTP not found
		}
		 return;
	 } else if (queryString.indexOf("clearPrivacyData") >= 0) {
		 System.out.println("queryString.indexOf(\"clearPrivacyData\") = " + queryString.indexOf("clearPrivacyData"));
		 clearPrivacyData = true;
	 }
 }

 {
	String method = request.getMethod();
	if ("POST".equals(method)) {
    	out.println("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">");
        out.println("<html xmlns=\"http://www.w3.org/1999/xhtml\" xml:lang=\"en\" lang=\"en\"><head><title>XMLDAP Relying Party token digester</title><style type=\"text/css\">BODY {color:#000;font-family: verdana, arial, sans-serif;}</style></head><body>");
        java.io.BufferedReader reader = request.getReader();
        char[] buf; 
        int contentLength = request.getContentLength();
        if (contentLength > 0) {
        	System.out.println("link.jsp: POST contentLength=" + contentLength);
        	buf = new char[contentLength];
        } else {
        	buf = new char[4 * 1024];
        }
        StringBuffer sb = new StringBuffer();
        int len;
        while ((len = reader.read(buf, 0, buf.length)) > 0) {
          sb.append(buf, 0, len);
        }
        String data = sb.toString();
        if (session == null) {
	        session = request.getSession(true);
        }
        session.setAttribute("token", data);
        System.out.println("link.jsp: POST\n" + data);
		return;
	} else {
		if (session != null) {
			token = (String)session.getAttribute("token");
			allClaims = (Map)session.getAttribute("claims");
		}
	}
 String userAgent = request.getHeader("user-agent");
 out.println("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">");
%>

<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<title>XRDS &amp; Java Based Relying Party without object element</title>
	<link rel="xrds.metadata" href="https://w4de3esy0069028.gdc-bln01.t-systems.com:8443/relyingparty/?xmldap_rp.xrds"/>

    <style type="text/css">
    BODY {background: #FFF url(./img/banner.png) repeat-x;
         color:#000;
         font-family: verdana, arial, sans-serif;}

        h2 { color:#185F77;
         font-family: verdana, arial, sans-serif;}

        h4 { color:#000;
         font-family: verdana, arial, sans-serif;}

        .forminput{position:relative;width:300px;background-color: #ffffff;border: 1px solid #666666;}


        A {color: #657485; font-family:verdana, arial, sans-serif; text-decoration: none}
        A:hover {color: #657485; text-decoration: underline}

        .container {
           background-color: #FFFFFF;
           padding: 10px;
           margin: 10px;
           font-family:verdana, arial, sans-serif;
            position:relative;
              left:0px;
              top:25px;
            width: 95%;
           }


        #title {color: #FFF; font:bold 250% arial; text-decoration: none;
            position:relative;
              left:10px;
              top:42px;
        }

        #links {
            position:relative;
              left:-5px;
              top:11px;
        text-align: right;
        }

        #links A {color: #FFF; font-weight:bold; font-family:verdana, arial, sans-serif; text-decoration: none}
        #links A:hover {color: #FFF; text-decoration: underline}

    </style>
    

</head>
<body>
	<div id="title">java based relying party</div>
	<div id="links">
	<a href="../">resources</a>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
	<a href="http://ignisvulpis.blogspot.com">ignisvulpis.blogspot.com</a>
	</div>


	<div>   <br/>
	<div class="container" id="relying_party">

<%
	if (session == null) {
	    session = request.getSession(true);
	}
	if (clearPrivacyData) { // GET not POST
		allClaims = null;
		session.removeAttribute("claims");
		System.out.println("removed attribute claims from session");
		token = null;
		session.removeAttribute("token");
    	out.println("<p>Privacy data was cleared.</p>");
	} else {
		allClaims = (Map)session.getAttribute("claims");
		if (token != null) { // POST not GET
			Token aToken = new Token(token, privateKey);
		    Map claims = aToken.getClaims();
		    Set keys = claims.keySet();
		    Iterator keyIter = keys.iterator();
		    while (keyIter.hasNext()){
		        String name = (String) keyIter.next();
		        String value = (String) claims.get(name);
		        if (allClaims == null) {
		        	allClaims = new HashMap();
		        }
		        allClaims.put(name, value);
		    }
	        if (session == null) {
		        session = request.getSession(true);
	        }
		    session.setAttribute("claims", allClaims);
		    session.removeAttribute("token");
		}
	    if (allClaims != null && allClaims.size() > 0) {
	    	out.println("<h2>You provided the following claims:</h2>");
	    	Set keys = allClaims.keySet();
	    	Iterator keyIter = keys.iterator();
		    while (keyIter.hasNext()){
		        String name = (String) keyIter.next();
		        String value = (String) allClaims.get(name);
		        out.println("<p>" + escapeHtmlEntities(name) + ": " + escapeHtmlEntities(value) + "</p>");
		    }
	    }
	}
%>
<h2>Provide claims with an Information Card</h2>
<%
if (allClaims != null && allClaims.size() > 0) {
%>
	<form id="clearForm" action="link.jsp">
		<input type="submit" name="clearPrivacyData" value="Clear privacy data"/>
	</form>
<%
}
%>
    <br/><br/>
    <h2>Curious about how it works...?</h2>

		This document contains a metadata tag that provides a means to discover the relyingparty policy and endpoints!
    <br/>
    <br/>

<%
	if (userAgent != null) {
		out.println("<p style=\"font-size:xx-small\">Your user agent is: " + escapeHtmlEntities(userAgent) + "</p>");
	}
%>
    </div>
    </div>

</body>
</html>
<%
} 
%>