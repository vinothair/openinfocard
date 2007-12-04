<%
 String price = request.getParameter("price");
 if (price == null) {
         price = "2700";
 } else {
         try {
                 // make sure that price is an integer
                 int p = Integer.parseInt(price);
                 price = String.valueOf(p);
         } catch (NumberFormatException e) {
                 price = "0";
         }
 }
 String requiredClaims="http://schemas.xmlsoap.org/PaymentCard/account http://schemas.xmlsoap.org/PaymentCard/VV http://schemas.xmlsoap.org/PaymentCard/expiry http://schemas.xmlsoap.org/PaymentCard/trandata?price=" + price + "EUR";
%>
<%
 String queryString = request.getQueryString();
 if ((queryString != null) && (queryString.indexOf("privacy") != -1)) {
         String contentType = request.getContentType();
         log("privacyStatement request content-Type: " + contentType);
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
         java.io.FileInputStream fis = new java.io.FileInputStream(privaceStatement);
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
  out.println("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.1//EN\" \"http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd\">");
%>

<html xmlns="http://www.w3.org/1999/xhtml">
<head>
        <title>PaymentCards are accepted at this Java Based Relying Party</title>


    <style type="text/css">
    BODY {background: #FFF url(./img/banner.png) repeat-x;
         color:#000;
         font-family: verdana, arial, sans-serif;}

        h2 { color:#185F77;
         font-family: verdana, arial, sans-serif;}

        h4 { color:#000;
         font-family: verdana, arial, sans-serif;}

        .forminput{position:relative;width:300px;background-color:#ffffff;border: 1px solid #666666;}


        A {color: #657485; font-family:verdana, arial, sans-serif;text-decoration: none}
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


        #title {color: #FFF; font:bold 250% arial; text-decoration:none;
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

        #links A {color: #FFF; font-weight:bold; font-family:verdana,arial, sans-serif; text-decoration: none}
        #links A:hover {color: #FFF; text-decoration: underline}

    </style>
    <%
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
        <a href="http://ignisvulpis.blogspot.com">ignisvulpis.blogspot.com</a>
        </div>


        <div>   <br/>
        <div class="container" id="relying_party">

<h2>Login with an InfoCard</h2>
    <table border="0">
        <tr>
            <td>

<%
 if (request.isSecure()) {
%>

<form method='post' action='' enctype='application/x-www-form-urlencoded'>
 <p>Price:&nbsp;<input name="price" type="text" size="30" maxlength="30"/>&nbsp;EUR</p>
</form>
<form method='post' action='./infocard' id='infocard' enctype='application/x-www-form-urlencoded'>
 <table><tr><td><%
                   out.println("Buy for just " + price + " ,00 EUR");
                  %>
            </td></tr>
        <tr><td><img alt="mastercard" src="./img/MasterCard.PNG" onclick="var f=document.getElementById('infocard'); f.submit()"/></td></tr>
 </table>
 <p>
    <object type="application/x-informationcard" name="xmlToken">
<%
                              out.println("<param name=\"privacyUrl\" value=\"" + request.getRequestURL() + "?privacy.txt\"/>");
%>
                              <param name="privacyVersion" value="1"/>
                  <param name="tokenType" value="urn:oasis:names:tc:SAML:1.0:assertion"/>
                  <%
                   out.println("<param id=\"requiredClaims\" name=\"requiredClaims\" value=\"" + requiredClaims + "\"/>");
                  %>
                  <param name="optionalClaims" value="http://schemas.xmlsoap.org/ws/2005/05/identity/claims/privatepersonalidentifier http://schemas.xmlsoap.org/ws/2005/05/identity/claims/givenname http://schemas.xmlsoap.org/ws/2005/05/identity/claims/surname http://schemas.xmlsoap.org/ws/2005/05/identity/claims/emailaddress http://schemas.xmlsoap.org/ws/2005/05/identity/claims/streetaddress http://schemas.xmlsoap.org/ws/2005/05/identity/claims/locality http://schemas.xmlsoap.org/ws/2005/05/identity/claims/stateorprovince http://schemas.xmlsoap.org/ws/2005/05/identity/claims/postalcode http://schemas.xmlsoap.org/ws/2005/05/identity/claims/country http://schemas.xmlsoap.org/ws/2005/05/identity/claims/homephone http://schemas.xmlsoap.org/ws/2005/05/identity/claims/otherphone http://schemas.xmlsoap.org/ws/2005/05/identity/claims/mobilephone http://schemas.xmlsoap.org/ws/2005/05/identity/claims/dateofbirth http://schemas.xmlsoap.org/ws/2005/05/identity/claims/gender"/>
    </object>
 </p>
</form>
                    <br/>Click on the image above to buy with and Infocard.<br/>
                    <br/><a href="/paymentCard_sts/cardmanager/">Click here to create a managed card.</a>
<%
 } else {
%>
The infocard login will only work if you're on my secure site.  <p><a href="https://xmldap.org/relyingparty/">https://xmldap.org/relyingparty/
</a>
<%
 }
%>

            </td>
        </tr>
    </table>

    </div>
    </div>

<br/>
<p>
    <a href="http://validator.w3.org/check?uri=referer"><img
        src="http://www.w3.org/Icons/valid-xhtml11"
        alt="Valid XHTML 1.1" height="31" width="88" /></a>
  </p>


</body>
</html>
<%
}
%>
