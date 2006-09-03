<%@ page import="nu.xom.Element,
                 org.xmldap.exceptions.KeyStoreException,
                 org.xmldap.exceptions.SerializationException,
                 org.xmldap.infocard.SelfIssuedToken,
                 org.xmldap.util.KeystoreUtil,
                 org.xmldap.xmlenc.EncryptedData"%>
<%@ page import="javax.servlet.ServletException"%>
<%@ page import="java.security.PrivateKey"%>
<%@ page import="java.security.cert.X509Certificate"%>


<%

   String givenName =   request.getParameter("GivenName");
   String sureName =   request.getParameter("Surname");
   String email =   request.getParameter("EmailAddress");
   String xml =   request.getParameter("token");
   PrivateKey privateKey = null;
   X509Certificate cert = null;

   try {

        KeystoreUtil keystore = new KeystoreUtil("/home/cmort/apps/apache-tomcat-5.5.17/conf/xmldap_org.jks", "password");
        privateKey = keystore.getPrivateKey("xmldap", "password");
        cert = keystore.getCertificate("xmldap");

   } catch (KeyStoreException e) {
        throw new ServletException(e);
   }

   String message="";
   EncryptedData encryptor = new EncryptedData(cert);
   SelfIssuedToken token = new SelfIssuedToken(cert,cert,privateKey);

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