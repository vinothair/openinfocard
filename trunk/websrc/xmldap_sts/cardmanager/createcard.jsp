<%@ page import="java.util.List"%>
<%@ page import="java.util.Locale"%>
<%@ page import="org.xmldap.sts.db.ManagedCard"%>
<%@ page import="org.xmldap.sts.db.CardStorage"%>
<%@ page import="org.xmldap.sts.db.DbSupportedClaim"%>
<%@ page import="org.xmldap.sts.db.impl.CardStorageEmbeddedDBImpl"%>
<%@ page import="org.xmldap.util.PropertiesManager"%>
<%@ page import="org.xmldap.sts.db.SupportedClaims"%>
<%@ page import="org.xmldap.util.XSDDateTime"%>
<%

	PropertiesManager properties = new PropertiesManager(PropertiesManager.SECURITY_TOKEN_SERVICE, getServletContext());
	String supportedClaimsClass = properties.getProperty("supportedClaimsClass");
	SupportedClaims supportedClaimsImpl = SupportedClaims.getInstance(supportedClaimsClass);
	CardStorage storage = new CardStorageEmbeddedDBImpl(supportedClaimsImpl);
    String servletPath = properties.getProperty("servletPath");
	response.setContentType("application/xhtml+xml");

%>
<!DOCTYPE html 
     PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
     "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
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


%>

    <script type="text/javascript">
        document.location = <%=servletPath%>+"/cardmanager/";
    </script>


<%
    }  else {

        String action = request.getParameter("action");
        if (action == null ) {

%>
<p style="font-weight:bold">Create a card</p><br/><br/>
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
		 out.println("<tr><td>" + displayTag + ":</td><td><input type=\"text\" name=\"" + key + "\" class=\"forminput\"/></td></tr>");
		}
		if (storage.getVersion() > 1) {
			System.out.println("createcard.jsp: dbVersion=" + storage.getVersion());
			out.println("<tr><td>" + "RequireAppliesTo" + ":</td><td><input type=\"checkbox\" name=\"" + "RequireAppliesTo" + "\" class=\"forminput\"/></td></tr>");
			out.println("<tr><td>" + "RequireStrongRecipientIdentity" + ":</td><td><input type=\"checkbox\" name=\"" + "RequireStrongRecipientIdentity" + "\" class=\"forminput\"/></td></tr>");
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

	    String audit = request.getParameter("RequireAppliesTo");
    	if (audit != null) {
    		if (!"".equals(audit)) {
    			card.setRequireAppliesTo(true);
    		}
    	}
	    String strongCrypto = request.getParameter("RequireStrongRecipientIdentity");
    	if (strongCrypto != null) {
    		if (!"".equals(strongCrypto)) {
	    		card.setRequireStrongRecipientIdentity(true);
    		}
    	}
	    
	    String timeissued = new XSDDateTime().getDateTime();
	    card.setTimeIssued(timeissued);
        storage.addCard(username, card);

        out.println("<script type=\"text/javascript\">document.location = \"/" + servletPath + "/cardmanager/\";</script>");

    }

}
%>
</body>
</html>