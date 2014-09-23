<%@page import="org.json.JSONObject"%>
<%@page import="com.dnaa.common.dbi.DatabaseInterface"%>
<%
	String errorMessage = null;
	try {
		int id = DatabaseInterface.registerNewUser(request.getParameterMap());
		if (id > 0) {
			session.setAttribute("userid", id);
			response.sendRedirect("welcome.jsp");
		}
	}
	catch (Exception ex) {
		errorMessage = ex.getMessage();
	}
%>
<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<% session.setAttribute("pageName", "New User Registration"); %>
<!DOCTYPE html>
<html>
<head>
<%@ include file="./pagetemplate/headbase.jsp" %>
	<link rel="stylesheet" type="text/css"  href="./css/dnaa-forms.css"></link>
</head>
<body>
<%@ include file="./pagetemplate/header.jsp" %>
<%@ include file="./pagetemplate/nav.jsp" %>

<% if (errorMessage != null) { %>
<section>
	<h1>Registration failed!</h1>
	<p><%=errorMessage %></p>
</section>
<% } %>
<section>
	<h1><%=session.getAttribute("pageName") %></h1>
	<form method="post" action="register.jsp">
		<fieldset>
			<legend>Enter your new user information here to register your account!</legend>
			<ul>
				<li>
					<label for="firstname">First name:</label>
					<input type="text" id="firstname" name="firstname" value="" required="required" />
				</li>
				<li>
					<label for="lastname">Last name:</label>
					<input type="text" id="lastname" name="lastname" value="" required="required" />
				</li>
				<li>
					<label for="email1">Email:</label>
					<input type="email" id="email1" name="email1" value="" required="required" />
				</li>
				<li>
					<label for="email2">Re-type email:</label>
					<input type="email" id="email2" name="email2" value="" required="required" />
	            </li>
	            <li>
	            	<label for="username">User name:</label>
	                <input type="text" id="username" name="username" value="" required="required" />
	            </li>
	            <li>
	            	<label for="password1">Password:</label>
	            	<input type="password" name="password1" value="" required="required" />
				</li>
				<li>
					<label for="password2">Re-type password:</label>
					<input type="password" id="password2" name="password2" value="" required="required" />
				</li>
				<li>
					<label for="subscription">Subscription:</label>
					<select name="subscription" required="required">
	                	<optgroup label="Free Accounts">
	                		<option value="DEFAULT">Basic</option>
	                		<option value="STUDENT">Student</option>
	                	</optgroup>
	                	<optgroup label="Subscriptions">
	                		<option value="EDUCATOR">Educator</option>
	                		<option value="PROFESSIONAL">Professional</option>
	                	</optgroup>
	               	</select>
	            </li>
	            <li>
	            	<input type="checkbox" id="updates" name="updates" />
	            	<label for="updates">Send news and updates to your email address?</label>
	            </li>
	            <li>
	    			<input type="submit" id="submitBtn" name="submitBtn" value="Register" />
	                <input type="reset" id="resetBtn" name="resetBtn" value="Reset" />
	            </li>
	            <li class="nextCategory">Already registered? <a href="signin.jsp">Login Here!</a></li>
				<li>Forgot your user name or password? <a href="recover.jsp">Recover Account Here!</a></li>
	        </ul>
		</fieldset>
	</form>
</section>

<%@ include file="./pagetemplate/footer.jsp" %>
</body>
</html>