<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>MVC Events</title>
</head>
<body>
    <h1>Event Information</h1>
    <p>Request URI: ${event.requestUri}</p>
    <p>Controller Method: ${event.method}</p>
    <p>View: ${event.view}</p>
    <p>View Engine: ${event.engine}</p>
</html>
