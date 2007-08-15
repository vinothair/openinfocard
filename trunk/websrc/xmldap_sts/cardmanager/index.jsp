<! DOCTYPE root PUBLIC 
 "-//Sun Microsystems Inc.//DTD JavaServer Pages Version 1.0//EN" 
 "http://java.sun.com/products/jsp/dtd/jspcore_1_0.dtd">
<jsp:root xmlns:jsp="http://java.sun.com/products/jsp/dtd/jsp_1_0.dtd">
<%-- http://xmleverywhere.com/wrox/2858/28581002.htm --%>

<%@ page import="java.util.Iterator"%>
<%@ page import="org.xmldap.sts.db.ManagedCard"%>
<%@ page import="org.xmldap.sts.db.CardStorage"%>
<%@ page import="org.xmldap.sts.db.impl.CardStorageEmbeddedDBImpl"%>
<%@ page import="java.util.List"%>
<%@ page import="org.xmldap.exceptions.StorageException"%>
<%@ page import="org.xmldap.util.PropertiesManager"%>
<%@ page import="org.xmldap.sts.db.SupportedClaims"%>
<%@ page import="org.xmldap.util.KeystoreUtil"%>
<%@ page import="java.security.PrivateKey"%>
<%@ page import="org.xmldap.exceptions.KeyStoreException"%>
<%@ page import="org.xmldap.rp.Token"%>
<%@ page import="org.xmldap.infocard.TokenServiceReference"%>
<%@ page import="java.util.Map"%>
<%@ page import="java.util.Set"%>

<%
			PropertiesManager properties = new PropertiesManager(
			PropertiesManager.SECURITY_TOKEN_SERVICE,
			getServletContext());
	String supportedClaimsClass = properties
			.getProperty("supportedClaimsClass");
	SupportedClaims supportedClaimsImpl = SupportedClaims
			.getInstance(supportedClaimsClass);
	CardStorage storage = new CardStorageEmbeddedDBImpl(
			supportedClaimsImpl);
	PrivateKey privateKey = null;

	try {

		String keystorePath = properties.getProperty("keystore");
		String keystorePassword = properties
		.getProperty("keystore.password");
		String key = properties.getProperty("key.name");
		String keyPassword = properties.getProperty("key.password");

		KeystoreUtil keystore = new KeystoreUtil(keystorePath,
		keystorePassword);
		privateKey = keystore.getPrivateKey(key, keyPassword);

	} catch (KeyStoreException e) {
		throw new ServletException(e);
	}
%>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>XMLDAP Card Manager</title>


<style>
    BODY {background: #FFFFFF;
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


</head>
<body>
<%
	String username = (String) session.getAttribute("username");

	if (username == null) {

		String uid = request.getParameter("uid");
		String password = request.getParameter("password");

		if ((uid != null) && (uid != "") && (password != null) && (password != "")) {

			storage.startup();
			boolean isUser = storage.authenticate(uid, password);
			if (isUser) {
		username = uid;
		session.setAttribute("username", uid);
		session.setAttribute("UserCredential",
				TokenServiceReference.USERNAME);
			} else {
		try {
			storage.addAccount(uid, password);
			session.setAttribute("username", uid);
			username = uid;
			session.setAttribute("UserCredential",
			TokenServiceReference.USERNAME);
		} catch (StorageException e) {
%>

<b>Authentication failed.</b>
<p />

<%
			}
			}

		}
	}

	String encryptedXML = request.getParameter("xmlToken");

	if ((encryptedXML != null) && (encryptedXML != "")) {
		session.getServletContext().log("got xmlToken");
		Token token = new Token(encryptedXML, privateKey);
		
		session.getServletContext().log("You provided the following claims:");
        Map claims = token.getClaims();
        Set keys = claims.keySet();
        Iterator keyIter = keys.iterator();
        while (keyIter.hasNext()){
            String name = (String) keyIter.next();
            String value = (String) claims.get(name);
            session.getServletContext().log(name + ": " + value);
        }

        boolean signatureIsValid = token.isSignatureValid();
        if (signatureIsValid) {
        	session.getServletContext().log("signature is valid");
        } else {
	    	session.getServletContext().log("signature is not valid");
    	}
        boolean conditionsAreValid = token.isConditionsValid();
        if (conditionsAreValid) {
        	session.getServletContext().log("conditions are valid");
        } else {
	    	session.getServletContext().log("conditions are not valid");
    	}
        
		if (signatureIsValid && conditionsAreValid) {
			username = (String) claims.get("privatepersonalidentifier");
			session.getServletContext().log("ppid=" + username);
			if ((username != null) && (username != "")) {
				String password = "openinfocard";
				session.getServletContext().log("username="+username);
				storage.startup();
				boolean isUser = storage.authenticate(username,
						password);
				if (isUser) {
					session.setAttribute("username", username);
					session.setAttribute("UserCredential",
					TokenServiceReference.SELF_ISSUED);
					session.getServletContext().log("username="+username+" authenticated");
				} else {
					try {
						storage.addAccount(username, password);
						session.setAttribute("username", username);
						session.setAttribute("UserCredential",
						TokenServiceReference.SELF_ISSUED);
						session.getServletContext().log("username="+username+" created");
					} catch (StorageException e) {
%>

				<b>Authentication failed.</b>
				<p />

<%
					}
				}
			}
		} else {
			session.getServletContext().log("conditions or signature invalid");
		}
	}

	if (username == null) {
%>

<b>Please Login or Create an Account:</b>
<br />
<br />
<form name='infocard' method='post' action='' id='infocard'
	enctype='application/x-www-form-urlencoded'>
<table border="0" cellpadding="5">
	<tr>
		<td>Username:</td>
		<td><input type="text" name="uid" class="forminput" /></td>
	</tr>
	<tr>
		<td>Password:</td>
		<td><input type="password" name="password" class="forminput" /></td>
	</tr>
</table>
<br />
<input type="submit" value="Login or Create a New Account" />
<br />
<img src="../img/card.png"
	onMouseOver="this.src='../img/card.png';"
	onMouseOut="this.src='../img/click_me_card.png';" onClick="infocard.submit()" />
<br>Click on the image above to login with and Infocard.
<br>
<OBJECT type="application/x-informationCard" name="xmlToken">
	<PARAM name="tokenType" Value="urn:oasis:names:tc:SAML:1.0:assertion">
	<PARAM Name="requiredClaims"
		Value="http://schemas.xmlsoap.org/ws/2005/05/identity/claims/privatepersonalidentifier">
</OBJECT>
</form>



<%
} else {
%>

<b>Welcome, <%=username%></b>
<br>
<br> Here you can create and download managed cards. 
<br>
<br>
<b>Your Cards:</b>
<br>
<table border="0" cellpadding="5">
	<%
			List cards = storage.getCards(username);
			Iterator iter = cards.iterator();
			while (iter.hasNext()) {

				String cardId = (String) iter.next();
				ManagedCard thisCard = storage.getCard(cardId);
				out.println("<tr><td><a href=\"/sts/card/"
				+ thisCard.getCardId() + ".crd\">"
				+ thisCard.getCardName() + "</a></td></tr>");

			}

			String backupfile = "/sts/backup/" + username + ".crds";
	%>

</table>
<br>
<br>
<b>Operations:</b>
<br>
<blockquote>
<a href="<%= backupfile %>">Download all your cards as a Cardspace
Backup file</a>
<br>
<a href="./createcard.jsp">Create a new card</a>
<br>
</blockquote>


<%
}
%>

</body>
</html>
