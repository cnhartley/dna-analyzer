<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<% session.setAttribute("pageName", "Account Settings"); %>
<!DOCTYPE html>
<html>
<head>
<%@ include file="./pagetemplate/headbase.jsp" %>
	<link rel="stylesheet" type="text/css"  href="./css/dnaa-forms.css"></link>
	<style type="text/css">
		div
	</style>
</head>
<body>
<%@ include file="./pagetemplate/header.jsp" %>
<%@ include file="./pagetemplate/nav.jsp" %>

<section>
	<h1>Account Type</h1>
	<fieldset>
		<legend>Most recent subscription information</legend>
		<ul>
			<li>
				<label for="subtext">Subscription:</label>
				<input type="text" id="subtext" name="subtext" value="<%="ACCOUNT_TYPE" %>" />
			</li>
			<li>The account description for the specific user account type.</li>
		</ul>
		<h4><font style="color:#339933;">Active</font></h4>
		<ul>
			<li>
				<label for="createdon">Created On:</label>
				<input type="datetime" id="createdon" name="createdon" value="<%="01/01/2013 09:23:34" %>" />
			</li>
			<li>
				<label for="expireson">Expires On:</label>
				<input type="datetime" id="expireson" name="expireson" value="<%="12/31/2014 08:00:00" %>" />
			</li>
		</ul>
	</fieldset>
</section>

<section>
	<h1>Settings</h1>
	<form method="post" action="register.jsp">
		<fieldset>
			<legend>Personal Information</legend>
			<ul>
				<li>
					<label for="firstname">First name:</label>
					<input type="text" id="firstname" name="firstname" value="" required="required" />
				</li>
				<li>
					<label for="lastname">Last name:</label>
					<input type="text" id="lastname" name="lastname" value="" required="required" />
				</li>
			</ul>
		</fieldset>
		<fieldset>
			<legend>User Login Settings</legend>
			<ul>
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
			</ul>
		</fieldset>
		<fieldset>
			<legend>Account Email</legend>
			<ul>
				<li>
					<label for="email1">Email:</label>
					<input type="email" id="email1" name="email1" value="" required="required" />
				</li>
				<li>
					<label for="email2">Re-type email:</label>
					<input type="email" id="email2" name="email2" value="" required="required" />
	            </li>
				<!-- li>
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
	            </li-->
	            <li>
	            	<input type="checkbox" id="updates" name="updates" />
	            	<label for="updates">Send news and updates to your email address?</label>
	            </li>
	            <li>
	    			<input type="submit" id="submitBtn" name="submitBtn" value="Save" />
	                <input type="reset" id="resetBtn" name="resetBtn" value="Reset" />
	            </li>
	        </ul>
		</fieldset>
	</form>
</section>

<%@ include file="./pagetemplate/footer.jsp" %>
</body>
</html>