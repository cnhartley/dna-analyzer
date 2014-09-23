<!-- Start navigation (nav) -->
<%@page import="com.dnaa.common.dbi.DatabaseInterface"%>
<%@page import="org.json.JSONObject"%>
<nav>
	<ol>
		<li><a href="index.jsp" alt="Home page">Home</a></li>
		<li><a href="newSeq.jsp" alt="New nucleotide sequence to evaluate">New Sequence</a></li>
		<li><a href="search.jsp" alt="Search nucleotide sequences, amino acids, and proteins">Search&hellip;</a></li>
		<li><a href="help.jsp" alt="Find help for this website">Help</a></li>
		
		<span>
<%
int userid = 0;
try {
	userid = Integer.parseInt("" + session.getAttribute("userid"));
	//TODO grab the users name from the account service to display...
	
	JSONObject userInfo = DatabaseInterface.getUserInfo(userid);
	String displayName = userInfo.get("first_name") + " " + userInfo.get("last_name");
%>
		<li><%=displayName %> (<a href="signout.jsp" alt="Sign out from the current web session.">Sign out</a>)</li>
		<li><a href="settings.jsp">&hellip;</a></li>
<%
}
catch (NumberFormatException nfe) {
%>
		<li><a href="register.jsp" alt="Register a new user">Register</a></li> 
		<li><a href="signin.jsp" alt="Sign in as an existing user">Sign in</a></li>
<% } %>
		</span>
	</ol>
</nav>
<!-- End navigation (nav) -->