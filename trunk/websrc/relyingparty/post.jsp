<%@ page import="nu.xom.Element,
                 org.xmldap.exceptions.KeyStoreException,
                 org.xmldap.exceptions.SerializationException,
                 org.xmldap.infocard.SelfIssuedToken,
                 org.xmldap.util.KeystoreUtil,
                 org.xmldap.xmlenc.EncryptedData"%>
<%@ page import="javax.servlet.ServletException"%>


<%

   String givenName =   request.getParameter("GivenName");
   String sureName =   request.getParameter("Surname");
   String email =   request.getParameter("EmailAddress");
   String xml =   request.getParameter("token");

    //Get my keystore
   KeystoreUtil keystore = null;
   try {
       //keystore = new KeystoreUtil("/Users/cmort/build/infocard/conf/xmldap.org.jks", "storepassword");
       keystore = new KeystoreUtil("/home/cmort/apps/apache-tomcat-5.5.17/conf/xmldap_org.jks", "password");
   } catch (KeyStoreException e) {
        throw new ServletException(e);
   }

   String message="";
   EncryptedData encryptor = new EncryptedData(keystore, "xmldap");
   SelfIssuedToken token = new SelfIssuedToken(keystore, "xmldap", "xmldap", "password");

   token.setGivenName(givenName);
   token.setSurname(sureName);
   token.setEmailAddress(email);
   token.setValidityPeriod(20);
   Element securityToken = null;
   try {
       securityToken = token.serialize();

       //System.out.println(securityToken.toXML());

       encryptor.setData(securityToken.toXML());
       message = encryptor.toXML();


   } catch (SerializationException e) {
       throw new ServletException(e);
   }

   if (xml == null) {

%>

<body>

<div style="font-family: Helvetica;">

<h2>Here's the token I made for you...</h2>
    <form name='infocard' method='post' action='https://xmldap.org/relyingparty/infocard'>
       <textarea cols='150' rows='20' name="xmlToken"><%= message %></textarea><br>
        <input type=submit value="Login with this token">
    </form>


</div>

</body>

<%
} else {

    response.setContentType("text/xml");
    out.println(message);
    out.flush();
    out.close();
    
}


%>