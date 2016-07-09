<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!doctype html>
<html>
    <head>
        <title>Book Information</title>
        <link rel="stylesheet" type="text/css" href="${mvc.contextPath}/ozark.css">
    </head>
    <body>
    <h1>Book Information</h1>
    <p>Title: ${book.title}</p>
    <p>Author(s): ${book.author}</p>
    <p>ISBN: ${book.isbn}</p></body>
</html>
