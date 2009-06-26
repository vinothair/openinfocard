<!DOCTYPE html 
     PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
     "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<title>Java Based IPLocation Relying Party</title>


    <style>
    BODY {background: #FFF url(./img/banner.png) repeat-x;
         color:#000;
         font-family: verdana, arial, sans-serif;}

        h2 { color:#185F77;
         font-family: verdana, arial, sans-serif;}

        h4 { color:#000;
         font-family: verdana, arial, sans-serif;}

        .forminput{position:relative;size:300;background-color: #ffffff;border: 1px solid #666666;}


        A {color: #657485; font-family:verdana, arial, sans-serif; text-decoration: none}
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


        #title {color: #FFF; font-weight:bold; font-size:250%; font-family:arial; text-decoration: none;
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

        #links A {color: #FFF; font-weight:bold; font-size:150%; font-family:arial; text-decoration: none}
        #links A:hover {color: #FFF; text-decoration: underline}

    </style>
 </head>
<body>
	<div id="title">ip location relying party</div>
	<div id="links">
	<a href="../">resources</a>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
	<a href="http://ignisvulpis.blogspot.com">ignisvulpis.blogspot.com</a>
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
<img src="./img/card_off.png"
     onmouseover="this.src='./img/card_on.png';"
     onmouseout="this.src='./img/card_off.png';"
     onclick="infocard.submit()"/>

    <OBJECT type="application/x-informationCard" name="xmlToken">
                  <PARAM Name="tokenType" Value="urn:oasis:names:tc:SAML:1.0:assertion">
                  <PARAM Name="requiredClaims" Value="urn:ietf:params:xml:ns:pidf:geopriv10:civicLoc:country">
                  <PARAM Name="optionalClaims" Value="urn:ietf:params:xml:ns:pidf:geopriv10:civicLoc:A2 urn:ietf:params:xml:ns:pidf:geopriv10:civicLoc:A3 urn:ietf:params:xml:ns:pidf:geopriv10:civicLoc:LOC">
    </OBJECT>
</form>
                    <br>Click on the image above to login with and Infocard.<br>
                    <br><a href="/iplocation_sts/cardmanager/">Click here to create a managed card.</a>
<%
 } else {
%>
The infocard login will only work if you're on my secure site.  <p><a href="https://xmldap/iplocation_rp/">https://@domain@/relyingparty/</a>
<%
 }
%>

            </td>
        </tr>
    </table>


    <h2>Or, if you don't yet have CardSpace installed, I can make a security token for you...</h2>

    <form action="./post.jsp" method="post">
        <table border="0">
            <tr><td>PPID:</td><td><input type="text" name="http://schemas.xmlsoap.org/ws/2005/05/identity/claims/privatepersonalidentifier" class="forminput"><br></td></tr>
            <tr><td>Country:</td><td><input type="text" name="urn:ietf:params:xml:ns:pidf:geopriv10:civicLoc:country" class="forminput"><br></td></tr>
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

