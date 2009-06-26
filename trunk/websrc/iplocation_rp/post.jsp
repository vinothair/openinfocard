<%@ page import="nu.xom.Element,
                 org.xmldap.exceptions.SerializationException,
                 org.xmldap.infocard.SelfIssuedToken,
                 org.xmldap.util.KeystoreUtil,
                 org.xmldap.xmlenc.EncryptedData"%>
<%@ page import="javax.servlet.ServletException"%>
<%@ page import="java.security.PrivateKey"%>
<%@ page import="java.security.cert.X509Certificate"%>
<%@ page import="org.xmldap.util.PropertiesManager"%>
<%@ page import="java.security.interfaces.RSAPublicKey"%>


<%

   String ppid =   request.getParameter("http://schemas.xmlsoap.org/ws/2005/05/identity/claims/privatepersonalidentifier");
   String country =   request.getParameter("urn:ietf:params:xml:ns:pidf:geopriv10:civicLoc:country");
   String xml =   request.getParameter("token");
   PrivateKey privateKey = null;
   X509Certificate cert = null;


    PropertiesManager properties = new PropertiesManager(PropertiesManager.RELYING_PARTY, config.getServletContext());
    String keystorePath = properties.getProperty("keystore");
    String keystorePassword = properties.getProperty("keystore-password");
    String key = properties.getProperty("key");
    String keyPassword = properties.getProperty("key-password");

    KeystoreUtil keystore = new KeystoreUtil(keystorePath, keystorePassword);
    privateKey = keystore.getPrivateKey(key,keyPassword);
    cert = keystore.getCertificate(key);

    
   String message="";
   EncryptedData encryptor = new EncryptedData(cert);
   SelfIssuedToken token = new SelfIssuedToken((RSAPublicKey)cert.getPublicKey(),privateKey);

   token.setPrivatePersonalIdentifier(ppid);
   token.setSurname(country);
   token.setValidityPeriod(1, 10);
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
    <form name='infocard' method='post' action='/geopriv_rp/infocard'>
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
