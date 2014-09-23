<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<% session.setAttribute("pageName", "User Login"); %>
<!DOCTYPE html>
<html>
<head>
<%@ include file="./pagetemplate/headbase.jsp" %>
	<link rel="stylesheet" type="text/css"  href="./css/dnaa-forms.css"></link>
</head>
<body>
<%@ include file="./pagetemplate/header.jsp" %>
<%@ include file="./pagetemplate/nav.jsp" %>

<section>
	<h1><%=session.getAttribute("pageName") %></h1>
	<form method="post" action="verifylogin.jsp">
	    <fieldset>
	    	<legend>Existing users sign-in with their user name and password below.</legend>
	    	<ul>
	    		<li>
	    			<label for="username">User name:</label>
	    			<input type="text" id="username" name="username" value="" required="required" />
	    		</li>
	    		<li>
	    			<label for="password">Password:</label>
	    			<input type="password" id="password" name="password" value="" required="required"/>
	    		</li>
	    		<li>
	    			<input type="checkbox" id="rememberMe" name="rememberMe" />
	    			<label for="rememberMe">Remember me!</label>
	    		</li>
	    		<li>
	    			<input type="submit" id="submitBtn" name="submitBtn" value="Login" />
	                <input type="reset" id="resetBtn" name="resetBtn" value="Reset" />
	            </li>
	            <li class="nextCategory">Not a registered user? <a href="register.jsp">Register Here!</a></li>
				<li>Forgot your user name or password? <a href="recover.jsp">Recover Account Here!</a></li>
	        </ul>
		</fieldset>
	</form>
</section>

<%@ include file="./pagetemplate/footer.jsp" %>
</body>
</html>