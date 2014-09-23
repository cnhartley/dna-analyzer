<%@page import="com.dnaa.version.Version"%>
<!-- Start header -->
<header>
	<div id='versionInfo'>Version <%=Version.getFullVersion() %></div>
	<h1><a href='index.jsp'><%=Version.getApplicationName() %></a></h1>
	<h2><%=session.getAttribute("pageName") %></h2>
</header>
<!-- End header -->