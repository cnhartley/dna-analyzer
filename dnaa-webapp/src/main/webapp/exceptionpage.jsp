<%@page isErrorPage="true" %>
<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<% session.setAttribute("pageName", "Exception Caught!"); %>
<!DOCTYPE html>
<html>
<head>
<%@ include file="./pagetemplate/headbase.jsp" %>
<style type="text/css">

section table {
	border: 0;
	padding: 0;
	margin: 0;
	width: 80%;
}
section table tbody tr {
	vertical-align: top;
}
section table tbody tr td {
	padding: 0.5em;
	margin: 0.25em;
	font-weight: normal;
	font-family: monospace;
}
section table tbody tr td:FIRST-CHILD {
	white-space: nowrap;
	font-weight: bold;
	font-family: inherit;
}
section code {
	color: #333366;
	font-family: monospace;
}

</style>
</head>
<body>
<%@ include file="./pagetemplate/header.jsp" %>
<%@ include file="./pagetemplate/nav.jsp" %>

<section>
	<h1><%=Version.getApplicationName() %> - <%=session.getAttribute("pageName") %></h1>
	<h2>Error: ${pageContext.exception}</h2>
	<p>The web application generated an uncaught runtime exception,
	<code>${pageContext.exception}</code>, with the following message:</p>
	<blockquote>${pageContext.exception.message}</blockquote>
	<table>
		<tbody>
			<tr>
				<td>URI:</td>
				<td>${pageContext.errorData.requestURI}</td>
			</tr>
			<tr>
				<td>Status code:</td>
				<td>${pageContext.errorData.statusCode}</td>
			</tr>
			<tr>
				<td>Stack trace:</td>
				<td>
					<c:forEach var="trace" items="${pageContext.exception.stackTrace}">
						<p>${trace}</p>
					</c:forEach>
				</td>
			</tr>
		</tbody>
	</table>
</section>

<%@ include file="./pagetemplate/footer.jsp" %>
</body>
</html>
