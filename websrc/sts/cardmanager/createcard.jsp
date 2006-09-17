<%@ page import="org.xmldap.sts.db.Account"%>
<%@ page import="org.xmldap.sts.db.ManagedCardDB"%>
<%@ page import="java.util.Collection"%>
<%@ page import="java.util.Iterator"%>
<%@ page import="org.xmldap.sts.db.ManagedCard"%>
<%!

    static ManagedCardDB db = ManagedCardDB.getInstance();

%>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<title>XMLDAP Card Manager</title>


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
<%

    Account account = (Account)session.getAttribute("account");

    if (account == null) {


%>

    <jsp:forward page="./" />


<%
    }  else {

        String action = request.getParameter("action");
        if (action == null ) {

%>
<b>Create a card</b><br><br>
<form action="./createcard.jsp" method="POST">
    <input type="hidden" name="action" value="createcard">
    <table>
    <tr><td>Card Name:</td><td><input type="text" name="cardName" class="forminput"></td></tr>
    <tr><td>Given Name:</td><td><input type="text" name="givenName" class="forminput"></td></tr>
    <tr><td>Last Name:</td><td><input type="text" name="surName" class="forminput"></td></tr>
    <tr><td>Email:</td><td><input type="text" name="email" class="forminput"></td></tr>
    <!--
    <tr><td>Address:</td><td><input type="text" name="address" class="forminput"></td></tr>
    <tr><td>City:</td><td><input type="text" name="city" class="forminput"></td></tr>
    <tr><td>State:</td><td><input type="text" name="state" class="forminput"></td></tr>
    <tr><td>Zip:</td><td><input type="text" name="zip" class="forminput"></td></tr>
    <tr><td>Country:</td><td><input type="text" name="country" class="forminput"></td></tr>
    <tr><td>Phone:</td><td><input type="text" name="phone" class="forminput"></td></tr>
    <tr><td>BirthDay:</td><td><input type="text" name="birthday" class="forminput"></td></tr>
    <tr><td>Gender:</td><td><input type="text" name="gender" class="forminput"></td></tr>
    -->

    <tr><td colspan=2><br><input type="submit" value="Create a new card"></td></tr>
    </table>
</form>

<%
    } else {

        ManagedCard card = new ManagedCard();
        String cardName = request.getParameter("cardName");
        if (cardName != null ) card.setCardName(cardName);

        String givenName = request.getParameter("givenName");
        if (givenName != null ) card.setGivenName(givenName);

        String email = request.getParameter("email");
        if (email != null ) card.setEmailAddress(email);

        account.addCard(card);
        db.updateAccount(account);

%>

    <jsp:forward page="./" />

<%

    }

}
%>
</body>
</html>