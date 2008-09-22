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

 String queryString = request.getQueryString();
 if (queryString != null) {
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
	 }
 } else {

 String userAgent = request.getHeader("user-agent");
 out.println("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">");
%>

<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<title>XRDS &amp; Java Based Relying Party</title>
	<link rel="xrds.metadata" href="?xmldap_rp.xrds"/>

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
	<a href="http://xmldap.blogspot.com">xmldap.blogspot.com</a>
	</div>


	<div>   <br/>
	<div class="container" id="relying_party">

<h2>Login with an InfoCard</h2>
	<div id="infocardStuffGoesHere" />
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