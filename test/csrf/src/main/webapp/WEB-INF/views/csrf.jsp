<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>CSRF Protection Test</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/ozark.css"/>
</head>
<body>
    <h1>CSRF Protection Test</h1>
    <form action="csrf" method="post">
        <input type="submit" value="Click here"/>
        <input type="hidden" name="${_mvc.csrfHeader}" value="${_mvc.csrfToken}"/>
    </form>
</html>
