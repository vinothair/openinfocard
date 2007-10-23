<%
 String queryString = request.getQueryString();
 if ((queryString != null) && (queryString.indexOf("privacy") != -1)) {
	 String contentType = request.getContentType();
	 System.out.println("privacyStatement request content-Type: " + contentType);
	 if (contentType == null) {
		 contentType = "text/plain";
	 } else if ("*/*".equals(contentType)) {
		 contentType = "text/plain";
	 }
	 org.xmldap.util.PropertiesManager properties = new org.xmldap.util.PropertiesManager(org.xmldap.util.PropertiesManager.RELYING_PARTY, config.getServletContext());
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
//	 java.io.FileInputStream fis = new java.io.FileInputStream(privaceStatement);
	 java.io.BufferedReader ins = new java.io.BufferedReader(new java.io.InputStreamReader(fis));
	 try {
			while (fis.available() != 0) {
				out.println(ins.readLine());
			}
		} catch (java.io.IOException e) {
			throw new ServletException(e);
		}
		finally {
			fis.close();
			ins.close();
		}
 } else {
  out.println("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">");
%>

<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<title>Java Based Relying Party</title>


    <style>
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
    <% 
	 org.xmldap.util.PropertiesManager properties = new org.xmldap.util.PropertiesManager(org.xmldap.util.PropertiesManager.RELYING_PARTY, config.getServletContext());
	 String requiredClaims = properties.getProperty("requiredClaims"); 
	 String optionalClaims = properties.getProperty("optionalClaims"); 

     String servername = request.getServerName();
     if (servername != null) {
    	 if (servername.indexOf("xmldap.org") > 0) {
    %>
    <script src="https://ssl.google-analytics.com/urchin.js" type="text/javascript">
    </script>
    <script type="text/javascript">
    if (!(urchinTracker == undefined)) {
     _uacct = "UA-147402-2";
     urchinTracker();
    }
    </script>
    <%
    	 }
     }
    %>


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
    <table border=0>
        <tr>
            <td>

<%
 if (request.isSecure()) {
%>
<form name='infocard' method='post' action='./infocard' id='infocard' enctype='application/x-www-form-urlencoded'>
<img src="./img/card_off.png"
     onmouseover="this.src='./img/card_on.png';"
     onmouseout="this.src='./img/card_off.png';"
     onclick="infocard.submit()"/>

    <object type="application/x-informationCard" name="xmlToken">
<%
		out.println("<param name=\"privacyUrl\" value=\"" + request.getRequestURL() + "?privacy.txt\"/>");
    	out.println("<param name=\"requiredClaims\" value=\"" + requiredClaims + "\"/>");
    	out.println("<param name=\"optionalClaims\" value=\"" + optionalClaims + "\"/>");
%>
    			  <param name="privacyVersion" value="1"/>
                  <param name="tokenType" value="urn:oasis:names:tc:SAML:1.0:assertion"/>
            </object>
</form>
                    <br/>Click on the image above to login with and Infocard.<br/>
                    <br/><a href="/sts/cardmanager/">Click here to create a managed card.</a>
<%
 } else {
%>
The infocard login will only work if you're on my secure site.  <p><a href="https://xmldap.org/relyingparty/">https://xmldap.org/relyingparty/</a>
<%
 }
%>

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

        The Java Based Relying Party is a simple CardSpace RP implementation, written in 100% in Java and running on Linux. The RP provides the ability to request and accept information cards from Microsoft CardSpace (InfoCard), or other Identity Selectors, and displays information about the card that was submitted. It currently is only tested with self-asserted cards, and SAML 1.0 assertions<p>

        This RP developed from the ground up using protocol documentation, and was the first non-Microsoft affiliated relying party on a non-Windows platform.

    <br/>
    <br/>
    <a href="http://xmldap.blogspot.com/2006/03/how-to-consume-tokens-from-infocard.html">Here's a brief overview of what it's doing.</a>

    </div>
    </div>





</body>
</html>
<%
} 
%>

