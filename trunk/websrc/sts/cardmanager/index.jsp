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
	<title>Java Based Relying Party</title>


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

        String uid = request.getParameter("uid");
        String password = request.getParameter("password");

        if ((uid != null) && (password != null))  {

            account = db.authenticate(uid,password);
            if ( account == null ) {
                account = new Account(uid,password);
                db.addAccount(account);
            }

            session.setAttribute("account",account);

        }

    }

    if (account == null) {

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

     <b>Welcome, <%= account.getUid() %></b>  <br><br>

Here you can create and download managed cards.   Please note that your cards may disappear at anytime, as this is quite alpha, and I may need to wipe things out.
<br><br>

<table  border="0" cellpadding="5">
<%

    Collection cards = account.getCards();
    Iterator iter = cards.iterator();
    while (iter.hasNext()) {

        ManagedCard thisCard = (ManagedCard)iter.next();
        out.println("<tr><td>" + thisCard.getCardName() + "</td><td><a href=\"" + thisCard.getCardId() + ".crd\">" + thisCard.getCardId() + ".crd</a></td></tr>");

    }


%>

</table>
<br>
<a href="./createcard.jsp">Create a new card</a><br><br>



<%
    }

%>

</body>
</html>