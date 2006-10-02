<%@ page import="java.util.HashMap"%>
<%@ page import="java.util.Iterator"%>
<%@ page import="java.util.Set"%>

<%
    HashMap claims = (HashMap)request.getAttribute("claims");
    String verified = (String)request.getAttribute("verified");
    String verifiedCertificate = 
     (String)request.getAttribute("verifiedCertificate");
    String verifiedConditions = 
     (String)request.getAttribute("verifiedConditions");
    String encryptedXML = (String)request.getAttribute("encryptedXML");
    String decryptedXML = (String)request.getAttribute("decryptedXML");


%>


<html><head><title>Success!</title>

    <script src="https://ssl.google-analytics.com/urchin.js" type="text/javascript">
    </script>
    <script type="text/javascript">
    _uacct = "UA-147402-2";
    urchinTracker();
    </script>
</head>

<body>

<div  style="font-family: Helvetica;">

<h2>Here's what you posted:</h2>
<p><textarea rows='10' cols='150'><%= encryptedXML %></textarea></p>

<h2>And here's the decrypted token:</h2>
<p><textarea rows='10' cols='150'><%= decryptedXML %></textarea></p>

<h2>Valid Signature: <%= verified %></h2>
<h2>Conditions: <%= verifiedConditions %></h2>
<h2>Certificate <%= verifiedCertificate %></h2>

<h2>You provided the following claims:</h2>

    <%


        Set keys = claims.keySet();
        Iterator keyIter = keys.iterator();
        while (keyIter.hasNext()){
            String name = (String) keyIter.next();
            String value = (String) claims.get(name);
            out.println(name + ": " + value + "<br>");

        }



    %>


<br><a href='mailto:charliemortimore@gmail.com'>Please drop me a line!</a></div>

</body>
</html>
