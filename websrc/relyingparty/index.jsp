<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<title>Java Based Relying Party</title>

<%

    String mayClaims = "<PARAM Name=\"issuer\" Value=\"urn:schemas-microsoft-com:ws:2005:05:identity:issuer:self\"><PARAM Name=\"requiredClaims\" Value=\"http://schemas.microsoft.com/ws/2005/05/identity/claims/givenname, http://schemas.microsoft.com/ws/2005/05/identity/claims/surname, http://schemas.microsoft.com/ws/2005/05/identity/claims/emailaddress\">";

	String septemberClaims = "<PARAM Name=\"requiredClaims\" Value=\"http://schemas.xmlsoap.org/ws/2005/05/identity/claims/givenname http://schemas.xmlsoap.org/ws/2005/05/identity/claims/surname http://schemas.xmlsoap.org/ws/2005/05/identity/claims/emailaddress\">";
		//+ "<PARAM Name=\"optionalClaims\" value=\"http://schemas.xmlsoap.org/ws/2005/05/identity/claims/webpage\">";

    String julyClaims = "<PARAM Name=\"issuer\" Value=\"urn:schemas-microsoft-com:ws:2005:05:identity:issuer:self\"><PARAM Name=\"requiredClaims\" Value=\"http://schemas.xmlsoap.org/ws/2005/05/identity/claims/givenname http://schemas.xmlsoap.org/ws/2005/05/identity/claims/surname http://schemas.xmlsoap.org/ws/2005/05/identity/claims/emailaddress\">";

    String objectClaims = septemberClaims;


    // ONLY GOING TO ALLOW SEPTEMBER OR LATER

    String optionalObjectClaims = "";

    String namespacedOptionalObjectClaims = "<PARAM Name=\"optionalClaims\"\n" +
            "Value=\"http://schemas.xmlsoap.org/ws/2005/05/identity/claims/streetaddress http://schemas.xmlsoap.org/ws/2005/05/identity/claims/locality http://schemas.xmlsoap.org/ws/2005/05/identity/claims/stateorprovince http://schemas.xmlsoap.org/ws/2005/05/identity/claims/postalcode http://schemas.xmlsoap.org/ws/2005/05/identity/claims/country http://schemas.xmlsoap.org/ws/2005/05/identity/claims/homephone http://schemas.xmlsoap.org/ws/2005/05/identity/claims/otherphone http://schemas.xmlsoap.org/ws/2005/05/identity/claims/mobilephone http://schemas.xmlsoap.org/ws/2005/05/identity/claims/dateofbirth http://schemas.xmlsoap.org/ws/2005/05/identity/claims/gender\">";


    
    String userAgent = request.getHeader("User-Agent");
%>
<!-- <%= userAgent %> -->
<%
    if (userAgent == null) {

        objectClaims = septemberClaims;
        optionalObjectClaims = namespacedOptionalObjectClaims;

    } else {

        int index = userAgent.indexOf(".NET CLR 3");
        if (index == -1) {

            objectClaims = septemberClaims;
            optionalObjectClaims = namespacedOptionalObjectClaims;


        } else {

            String agentString = userAgent.substring(index+10,userAgent.length());

            %>

            <%

            if (agentString.compareTo(".0.04308)") > 0){

                objectClaims = septemberClaims;
                optionalObjectClaims = namespacedOptionalObjectClaims;


            } else if (agentString.equals(".04307.00)")){

                objectClaims = julyClaims;

            } else {

                if (agentString.compareTo(".0.04226.00)") >= 0){

                    objectClaims = julyClaims;

                } else {

                    objectClaims = mayClaims;

               }
            }
        }
    }
%>

    <style>
    BODY {background: #FFF url(./img/banner.png) repeat-x;
         color:#000;
         font-family: verdana, arial, sans-serif;}

        h2 { color:#185F77;
         font-family: verdana, arial, sans-serif;}

        h4 { color:#000;
         font-family: verdana, arial, sans-serif;}

        .forminput{position:relative;width:300;background-color: #ffffff;border: 1px solid #666666;}


        A {color: #657485; font:verdana, arial, sans-serif; text-decoration: none}
        A:hover {color: #657485; text-decoration: underline}

        .container {
           background-color: #FFFFFF;
           padding: 10px;
           margin: 10px;
           font-family:verdana, arial, sans-serif;
            position:relative;
              left:0;
              top:25;
            width: 95%;
           }


        #title {color: #FFF; font:bold 250% arial; text-decoration: none;
            position:relative;
              left:10;
              top:42;
        }

        #links {
            position:relative;
              left:-5;
              top:11;
        text-align: right;
        }

        #links A {color: #FFF; font:bold 150% verdana, arial, sans-serif; text-decoration: none}
        #links A:hover {color: #FFF; text-decoration: underline}

    </style>
    <script src="https://ssl.google-analytics.com/urchin.js" type="text/javascript">
    </script>
    <script type="text/javascript">
    _uacct = "UA-147402-2";
    urchinTracker();
    </script>



</head>
<body>
	<div id="title">java based relying party</div>
	<div id="links">
	<a href="../">resources</a>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
	<a href="http://xmldap.blogspot.com">xmldap.blogspot.com</a>
	</div>


	<div>   <br>
	<div class="container" id="relying_party">

<h2>Login with an InfoCard</h2>
    <table border=0>
        <tr>
            <td>

<%
 if (request.isSecure()) {
%>
<form name='infocard' method='post' action='./infocard' id='infocard' enctype='application/x-www-form-urlencoded'>
<img src="img/click_me_card.png"
     onMouseOver="this.src='img/card.png';"
     onMouseOut="this.src='img/click_me_card.png';"
     onClick="infocard.submit()"/>

<OBJECT type="application/x-informationCard" name="xmlToken">
          <PARAM Name="tokenType" Value="urn:oasis:names:tc:SAML:1.0:assertion">
          <%= objectClaims %>
          <%= optionalObjectClaims%>
</OBJECT>
</form>
                    <br>Click on the image above to login with and Infocard.  
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


    <h2>Or, if you don't yet have InfoCard installed, I can make a security token for you...</h2>

    <form action="./post.jsp" method="POST">
        <table border="0">
            <tr><td>First Name:</td><td><input type="text" name="GivenName" class="forminput"><br></td></tr>
            <tr><td>Last Name:</td><td><input type="text" name="Surname" class="forminput"><br></td></tr>
            <tr><td>Email:</td><td><input type="text" name="EmailAddress" class="forminput"><br></td></tr>
            <tr><td colspan="2"><input type="submit" value="Create it for me"></td></tr>
        </table>

    </form>

    <br><br>
    <h2>Curious about how it works...?</h2>

        The Java Based Relying Party is a simple CardSpace RP implementation, written in 100% in Java and running on Linux. The RP provides the ability to request and accept information cards from Microsoft CardSpace (InfoCard), or other Identity Selectors, and displays information about the card that was submitted. It currently is only tested with self-asserted cards, and SAML 1.0 assertions<p>

        This RP developed from the ground up using protocol documentation, and was the first non-Microsoft affiliated relying party on a non-Windows platform.

    <br>
    <br>
    <a href="http://xmldap.blogspot.com/2006/03/how-to-consume-tokens-from-infocard.html">Here's a brief overview of what it's doing.</a>

    </div>
    </div>





</body>
</html>

