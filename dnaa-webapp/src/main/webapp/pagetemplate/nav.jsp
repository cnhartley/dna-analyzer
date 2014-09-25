<!-- Start navigation (nav) -->
<%@page import="com.dnaa.common.dbi.DatabaseInterface"%>
<%@page import="org.json.JSONObject"%>
<nav>
	<ol>
		<li><a href="index.jsp" title="Home page">Home</a></li>
		<li><a href="newSeq.jsp" title="New nucleotide sequence to evaluate">New Sequence</a></li>
		<li><a href="search.jsp" title="Search nucleotide sequences, amino acids, and proteins">Search&hellip;</a></li>
		<li><a href="help.jsp" title="Find help for this website">Help</a></li>
		
		<span>
<%
int userid = 0;
try {
	userid = Integer.parseInt("" + session.getAttribute("userid"));
	JSONObject userInfo = DatabaseInterface.getUserInfo(userid);
	String displayName = userInfo.get("first_name") + " " + userInfo.get("last_name");
%>
		<li><%=displayName %> (<a href="signout.jsp" title="Sign out from the current web session.">Sign out</a>)</li>
		<li><a href="settings.jsp">&hellip;</a></li>
<%
}
catch (NumberFormatException nfe) {
%>
		<li><a href="register.jsp" title="Register a new user">Register</a></li> 
		<li><a href="signin.jsp" title="Sign in as an existing user">Sign in</a></li>
<% } %>
		</span>
	</ol>
</nav>
<!-- End navigation (nav) -->