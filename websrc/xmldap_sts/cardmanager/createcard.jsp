<?xml version="1.0" encoding="UTF-8"?>
<jsp:root version="1.2" xmlns:f="http://java.sun.com/jsf/core" xmlns:h="http://java.sun.com/jsf/html" xmlns:jsp="http://java.sun.com/JSP/Page">
<jsp:directive.page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"/>

<jsp:output doctype-root-element="html"
    doctype-public="-//W3C//DTD XHTML 1.0 Strict//EN"
    doctype-system="http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd"/>
<%@ page import="java.util.List"%>
<%@ page import="java.util.Locale"%>
<%@ page import="org.xmldap.sts.db.ManagedCard"%>
<%@ page import="org.xmldap.sts.db.CardStorage"%>
<%@ page import="org.xmldap.sts.db.DbSupportedClaim"%>
<%@ page import="org.xmldap.sts.db.impl.CardStorageEmbeddedDBImpl"%>
<%@ page import="org.xmldap.util.PropertiesManager"%>
<%@ page import="org.xmldap.sts.db.SupportedClaims"%>
<%

	PropertiesManager properties = new PropertiesManager(PropertiesManager.SECURITY_TOKEN_SERVICE, getServletContext());
	String supportedClaimsClass = properties.getProperty("supportedClaimsClass");
	SupportedClaims supportedClaimsImpl = SupportedClaims.getInstance(supportedClaimsClass);
	CardStorage storage = new CardStorageEmbeddedDBImpl(supportedClaimsImpl);
	
%>
<h:html 
    xmlns="http://www.w3c.org/1999/xhtml" 
    xmlns:jsp="http://java.sun.com/JSP/Page" 
    xmlns:c="http://java.sun.com/jsp/jstl/core">
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


%>

    <script type="text/javascript">
        document.location = "/sts/cardmanager/";
    </script>


<%
    }  else {

        String action = request.getParameter("action");
        if (action == null ) {

%>
<b>Create a card</b><br/><br/>
<form action="./createcard.jsp" method="post">
    <input type="hidden" name="action" value="createcard"/>
    <table>
    <tr><td>Card Name:</td><td><input type="text" name="cardName" class="forminput"/></td></tr>
<%
		Locale clientLocale = request.getLocale();
		List dbSupportedClaims = supportedClaimsImpl.dbSupportedClaims();
		for (int i=0; i<dbSupportedClaims.size(); i++) {
		 DbSupportedClaim claim = (DbSupportedClaim)dbSupportedClaims.get(i);
		 String key = claim.columnName;
		 String displayTag = claim.getDisplayTag(clientLocale);
		 out.println("<tr><td>" + displayTag + ":</td><td><input type=\"text\" name=\"" + key + "\" class=\"forminput\"></td></tr>");
		}
%>
    <tr><td colspan=2><br/><input type="submit" value="Create a new card"/></td></tr>
    </table>
</form>

<%
    } else {

        storage.startup();

        ManagedCard card = new ManagedCard();
        String cardName = request.getParameter("cardName");
        if (cardName != null ) card.setCardName(cardName);

        List dbSupportedClaims = supportedClaimsImpl.dbSupportedClaims();
	    for (int i=0; i<dbSupportedClaims.size(); i++) {
	    	DbSupportedClaim claim = (DbSupportedClaim)dbSupportedClaims.get(i);
	    	String key = claim.columnName;
	    	String value = request.getParameter(key);
	    	System.out.println("key:"+key+" uri:"+claim.uri+" value:"+value);
	    	if (value != null) {
	    		if (!"".equals(value)) {
		    		card.setClaim(claim.uri, value);
	    		}
	    	}
	    }

        storage.addCard(username, card);

%>

    
    <script type="text/javascript">
        document.location = "/sts/cardmanager/";
    </script>

<%

    }

}
%>
</body>
</h:html>