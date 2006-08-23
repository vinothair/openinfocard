<%@ page import="org.xmldap.util.KeystoreUtil,
                 org.xmldap.exceptions.KeyStoreException,
                 org.xmldap.xmlenc.EncryptedData,
                 org.xmldap.infocard.SelfIssuedToken,
                 nu.xom.Element,
                 org.xmldap.exceptions.SerializationException"%>


<%

   String givenName =   request.getParameter("GivenName");
   String sureName =   request.getParameter("Surname");
   String email =   request.getParameter("EmailAddress");

    //Get my keystore
   KeystoreUtil keystore = null;
   try {
       //keystore = new KeystoreUtil("/Users/cmort/build/infocard/conf/xmldap.org.jks", "storepassword");
       keystore = new KeystoreUtil("/export/home/cmort/xmldap.org.jks", "storepassword");
   } catch (KeyStoreException e) {
        throw new ServletException(e);
   }

   EncryptedData encryptor = new EncryptedData(keystore, "identityblog");
   SelfIssuedToken token = new SelfIssuedToken(keystore, "identityblog", "Server-Cert", "keypassword");

   token.setGivenName(givenName);
   token.setSurname(sureName);
   token.setEmailAddress(email);
   token.setValidityPeriod(20);

   String xml = "";
   String encryptedXML = "";
   try {

       Element securityToken = token.serialize();
       xml = securityToken.toXML();
       encryptor.setData(xml);
       encryptedXML = encryptor.toXML();

   } catch (SerializationException e) {
       throw new ServletException(e);
   }



%>

<body>

<div style="font-family: Helvetica;">

<h2>Here's the token:</h2>
    <form name='encrypted' method='post' action='https://www.identityblog.com/infocard-post.php'>
       <textarea cols='150' rows='20' name="xmlToken"><%= encryptedXML %></textarea><br>
        <input type=submit value="Login to identityblog">
    </form>
</div>

</body>
