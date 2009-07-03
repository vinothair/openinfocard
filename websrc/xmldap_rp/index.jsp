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
 String tokentype = properties.getProperty("tokentype"); 

 String queryString = request.getQueryString();
 if (queryString != null) {
 	if (queryString.indexOf("privacy") == 0) {
	 System.out.println("queryString.indexOf(\"privacy\") = " + queryString.indexOf("privacy"));
	 String contentType = request.getContentType();
	 System.out.println("privacyStatement request content-Type: " + contentType);
	 if (contentType == null) {
		 contentType = "text/plain";
	 } else if ("*/*".equals(contentType)) {
		 contentType = "text/plain";
	 }
	 String privaceStatement = properties.getProperty("privacyStatement." + contentType); 
	 if (privaceStatement == null) {
		 privaceStatement = properties.getProperty("privacyStatement.text/plain"); 
		 if (privaceStatement == null) {
			 response.sendError(500, "could not find privacy statement of content type (" + contentType + ")");
			 return;
		 } else {
			 contentType = "text/plain";
		 }
	 }
	 response.setContentType(contentType);
	 System.out.println("reading : " + privaceStatement);
	 java.io.InputStream fis = getServletContext().getResourceAsStream(privaceStatement);
	 if (fis == null) {
	 	System.out.println("could not find resource: " + privaceStatement);
	 	// TODO send HTTP not found
	 	return;
	 }
//	 java.io.FileInputStream fis = new java.io.FileInputStream(privaceStatement);
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
   } else  if (queryString.indexOf("xmldap_rp.xrds") == 0) {
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
  } else  if (queryString.indexOf("login.xml") == 0) {
		 System.out.println("queryString.indexOf(\"login.xml\") = " + queryString.indexOf("login.xml"));

		 response.setContentType("application/xml");
		 out.println("<object type=\"application/x-informationcard\" name=\"xmlToken\">");
		 
   		 out.print("<param name=\"privacyUrl\" value=\""); out.print(request.getRequestURL()); out.println("?privacy.txt\"/>");
		 out.println("<param name=\"requiredClaims\" value=\"" + requiredClaims + "\"/>");
		 out.println("<param name=\"optionalClaims\" value=\"" + optionalClaims + "\"/>");
		 out.println("<param name=\"privacyVersion\" value=\"1\"/>");
         out.println("<param name=\"tokenType\" value=\"urn:oasis:names:tc:SAML:1.0:assertion\"/>");
		 out.println("</object>");

	  
  }
 } else {
		String userAgent = request.getHeader("user-agent");
		String cardSelectorName = request.getHeader("X-ID-Selector");
		boolean isOpenInfocardSelector = false;
		if (cardSelectorName != null) {
			isOpenInfocardSelector = (cardSelectorName.indexOf("openinfocard") != -1);
		}
	out.println("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">");
%>
<%
 String linkElement = "<link rel=\"xrds.metadata\" href=\"" + request.getServletPath() + "?xmldap_rp.xrds" + "\"/>";
 String metaElement = "<meta http-equiv=\"X-XRDS-Location\" content=\"" + request.getRequestURL() + "?xmldap_rp.xrds" + "\"/>";
%>

<%@page import="java.net.URLEncoder"%><html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<title>Java Based Relying Party</title>
	<%= metaElement %>
    
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

		.droparea:-moz-drag-over {
		  border: 1px solid black;
		}

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
    <table border="0">
        <tr>
            <td>
<% if (request.getHeader("User-Agent").contains("iPhone") || 
		request.getHeader("User-Agent").contains("iPod")) { 
      String policy = "<object type=\"application/x-informationcard\" name=\"xmlToken\">" +
      	"<param name=\"privacyUrl\" value=\"" + request.getRequestURL() + "?privacy.txt\"/>" +
      	"<param name=\"requiredClaims\" value=\"" + requiredClaims + "\"/>" +
      	"<param name=\"optionalClaims\" value=\"" + optionalClaims + "\"/>" +
      	"<param name=\"tokenType\" value=\"" + tokentype + "\"/>" +
      	"<param name=\"privacyVersion\" value=\"1\"/>" +
      	"</object>";
      	String encodedPolicy = URLEncoder.encode(policy, "UTF-8");
      String iPhoneLink = "<a href=\"icard-https://xmldap.org/relyingparty/infocard?_policy=" + 
      	encodedPolicy +
      	"\">Click here to send i-card</a>";
      out.println(iPhoneLink);
      %>
<% } else { %>

<form method='post' action='./infocard' id='infocard' enctype='application/x-www-form-urlencoded'>
<p>
<%
	if (isOpenInfocardSelector) {
%>		
<!--  ondragover="return false" dragenter="return false"  -->
<img id="icDropTarget" class="droparea" src="./img/card_off.png" alt=""
     onmouseover="this.src='./img/card_on.png';"
     onmouseout="this.src='./img/card_off.png';"
     onclick='var pf = document.getElementById("infocard"); pf.submit();'/>
<%
	} else {
%>
<img src="./img/card_off.png" alt=""
     onmouseover="this.src='./img/card_on.png';"
     onmouseout="this.src='./img/card_off.png';"
     onclick='var pf = document.getElementById("infocard"); pf.submit();'/>
<%
	}
%>

    <object type="application/x-informationcard" name="xmlToken">
<%
		out.println("\t<param name=\"privacyUrl\" value=\"" + request.getRequestURL() + "?privacy.txt\"/>");
    	out.println("\t<param name=\"requiredClaims\" value=\"" + requiredClaims + "\"/>");
    	out.println("\t<param name=\"optionalClaims\" value=\"" + optionalClaims + "\"/>");
    	out.println("\t<param name=\"tokenType\" value=\"" + tokentype + "\"/>");
%>
    			  <param name="privacyVersion" value="1"/>
    			  <param name="icDropTargetId" value="icDropTarget"/>
    </object>
</p>
</form>
                    <br/>Click on the image above to login with and Infocard.<br/>
<% } %>
                    <br/><a href="/sts/cardmanager/">Click here to create a managed card.</a>

            </td>
        </tr>
    </table>

	

    <h2>Or, if you don't yet have CardSpace installed, I can make a security token for you...</h2>

    <form action="./post.jsp" method="post">
        <table border="0">
            <tr><td>First Name:</td><td><input type="text" name="GivenName" class="forminput"/><br/></td></tr>
            <tr><td>Last Name:</td><td><input type="text" name="Surname" class="forminput"/><br/></td></tr>
            <tr><td>Email:</td><td><input type="text" name="EmailAddress" class="forminput"/><br/></td></tr>
            <tr><td colspan="2"><input type="submit" value="Create it for me"/></td></tr>
        </table>

    </form>

    <br/><br/>
    <h2>Curious about how it works...?</h2>

        The Java Based Relying Party is a simple CardSpace RP implementation, written in 100% in Java and running on Linux. The RP provides the ability to request and accept information cards from Microsoft CardSpace (InfoCard), or other Identity Selectors, and displays information about the card that was submitted. It currently is only tested with self-asserted cards, and SAML 1.0 assertions<p/>

        This RP developed from the ground up using protocol documentation, and was the first non-Microsoft affiliated relying party on a non-Windows platform.

    <br/>
    <br/>
    <a href="http://xmldap.blogspot.com/2006/03/how-to-consume-tokens-from-infocard.html">Here's a brief overview of what it's doing.</a>

<%
if (userAgent != null) {
	out.println("<p style=\"font-size:xx-small\">Your user agent is: " + escapeHtmlEntities(userAgent) + "</p>");
}
if (cardSelectorName != null) {
	out.println("<p style=\"font-size:xx-small\">Your ID selector is: " + escapeHtmlEntities(cardSelectorName) + "</p>");
}
%>
    </div>
    </div>

</body>
</html>
<%
} 
%>

