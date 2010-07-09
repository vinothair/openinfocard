<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<%@page import="com.utils.*"%><html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>If no error, the sts database should be created</title>
<%
response.getWriter().print("Total intialization of the payment cards for (stef and fj)");
ISTSConfiguration configurator = FactorySTSConfiguration.getInstance();
configurator.configure();
configurator.run();
configurator.test();
//CreateCRD.run();
response.getWriter().print("Enroll succeed");

%>
</head>
<body>

</body>
</html>