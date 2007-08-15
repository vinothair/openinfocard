<!DOCTYPE html 
     PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
     "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<%@ page import="java.util.Iterator"%>
<%@ page import="org.xmldap.sts.db.ManagedCard"%>
<%@ page import="org.xmldap.sts.db.CardStorage"%>
<%@ page import="org.xmldap.sts.db.impl.CardStorageEmbeddedDBImpl"%>
<%@ page import="java.util.List"%>
<%@ page import="org.xmldap.exceptions.StorageException"%>
<%@ page import="org.xmldap.util.PropertiesManager"%>
<%@ page import="org.xmldap.sts.db.SupportedClaims"%>
<%

	PropertiesManager properties = new PropertiesManager(PropertiesManager.SECURITY_TOKEN_SERVICE, getServletContext());
	String supportedClaimsClass = properties.getProperty("supportedClaimsClass");
	SupportedClaims supportedClaimsImpl = SupportedClaims.getInstance(supportedClaimsClass);
	CardStorage storage = new CardStorageEmbeddedDBImpl(supportedClaimsImpl);
	
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
    <script src="https://ssl.google-analytics.com/urchin.js" type="text/javascript">
    </script>
    <script type="text/javascript">
    _uacct = "UA-147402-2";
    urchinTracker();
    </script>



</head>
<body>
<%

    String username = (String)session.getAttribute("username");

    if (username == null) {

        String uid = request.getParameter("uid");
        String password = request.getParameter("password");

        if ((uid != null) && (password != null))  {

            storage.startup();
            boolean isUser = storage.authenticate(uid,password);
            if ( isUser ) {
                username = uid;
                session.setAttribute("username",uid);

            } else {
                try {
                    storage.addAccount(uid,password);
                    session.setAttribute("username",uid);
                    username = uid;
                } catch (StorageException e) {
                    %>

                        <b>Authentication failed.</b><p>

                    <%

                }
            }

        }
    }

    if (username == null) {

%>

<b>Please Login or Create an Account:</b><br><br>
<form action="" method="POST">
    <table border="0" cellpadding="5">
    <tr><td>Username: </td><td><input type="text" name="uid" class="forminput"> </td></tr>
    <tr><td>Password: </td><td><input type="password" name="password" class="forminput"> </td></tr>
    </table>
                                                                          <br>
    <input type="submit" value="Login or Create a New Account">                     <br>
</form>



<%

    }  else {

%>

     <b>Welcome, <%= username %></b>  <br><br>

Here you can create and download managed cards.
<br><br>

<b>Your Cards:</b><br>

<table  border="0" cellpadding="5">
<%

    List cards = storage.getCards(username);
    Iterator iter = cards.iterator();
    while (iter.hasNext()) {

        String cardId = (String)iter.next();
        ManagedCard thisCard = storage.getCard(cardId);
        out.println("<tr><td><a href=\"/sts/card/" + thisCard.getCardId() + ".crd\">" + thisCard.getCardName() + "</a></td></tr>");

    }

    String backupfile = "/sts/backup/" + username + ".crds";


%>

</table>
<br>
<br>
<b>Operations:</b><br>
<blockquote>
<a href="<%= backupfile %>">Download all your cards as a Cardspace Backup file</a><br>
<a href="./createcard.jsp">Create a new card</a><br>
</blockquote>


<%
    }

%>

</body>
</html>