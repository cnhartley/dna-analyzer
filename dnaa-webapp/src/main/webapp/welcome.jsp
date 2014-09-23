<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<% session.setAttribute("pageName", "Registration Successful!"); %>
<!DOCTYPE html>
<html>
<head>
<%@ include file="./pagetemplate/headbase.jsp" %>
</head>
<body>
<%@ include file="./pagetemplate/header.jsp" %>
<%@ include file="./pagetemplate/nav.jsp" %>

<section>
	<h1><%=session.getAttribute("pageName") %></h1>
	<p>You have successfully completed the account registration.
		You will receive an email shortly to verify your email account and 
		active your subscription.</p>
	<p><strong>Note:</strong> Until you have verified your account by clicking
		on the link contained in the email, you will only have the Basic FREE
		account permissions. <em>(for details of the Basic FREE account 
		<a href="info.jsp?account=default">click here!</a>)</em>
</section>

<%@ include file="./pagetemplate/footer.jsp" %>
</body>
</html>