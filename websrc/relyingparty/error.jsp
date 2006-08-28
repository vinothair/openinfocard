<%
    String error = (String)request.getAttribute("error");

%>

<html>
<head><title>Error</title></head>

<body>
<b>Error:</b><br>

    <% if ( error != null) {%>

    <%= error %><br>

    <% } %>

</body>
</html>
