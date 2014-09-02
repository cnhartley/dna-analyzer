<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<%
final String versionCode = "1.01.001a";
final String pageTitlePrefix = "DNAA";
%>
<html>
<head>
<meta charset="UTF-8">

<title><%=pageTitlePrefix %> - DNA Analyzer Home Page</title>

<link rel="shortcut icon" href="./img/favicon.ico" />
<link rel="apple-touch-icon image_src" href="./img/apple-touch-icon.png" />

<link rel="stylesheet" type="text/css" href="./css/defaults.css" />
<link rel="stylesheet" type="text/css" href="./css/main.css" />

<!-- script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.11.1/jquery.min.js"></script -->
<script type="text/javascript" src="./js/lib/jquery.min.js"></script>

</head>
<body>

<header>
	<div id='versionInfo'>Version <%=versionCode %></div>
	<h1><a href='index.jsp'>DNA Analyzer</a></h1>
	<h2>Home - HTML5 &amp; CSS3 Application</h2>
</header>

<nav>
	<ol>
		<li><a href="index.jsp" alt="Home page">Home</a></li>
		<li><a href="newSeq.jsp" alt="New nucleotide sequence to evaluate">New Sequence</a></li>
		<li><a href="lookup.jsp" alt="Lookup nucleotide sequences, amino acids, and proteins">Lookup</a></li>
		<li><a href="help.jsp" alt="Find help for this website">Help</a></li>
		
		<span>
		<li><a href="register.jsp" alt="Register a new user">Register</a></li> 
		<li><a href="signin.jsp" alt="Sign in as an existing user">Sign in</a></li>
		</span>
	</ol>
</nav>

<section><img alt="DNA graphic" src="./images/dna_graphic.gif" width="300" height="400" /></section>

<section>
	<h1>DNA Analyzer</h1>
	<h2>Nucleotide Sequence Application and Web Service</h2>
	<p>This software provides a graphical user interface (GUI) for representing Deoxyribonucleic Acid (DNA) or Ribonucleic Acid (RNA) sequences. Modules that are included in this application are; PAM Score analysis and comparison between sequences, Amino Acids and Proteins that the sequence can manufacture, and extended details on those compounds.</p>
</section>

<section>
	<h1>DNA Analyzer Sequence Grabber</h1>
	<ol>
		<li><a href="viewer.jsp?id=1234">DNA Analysis View for test sequence A(1234)...</a></li>
		<li><a href="viewer.jsp?id=4321">DNA Analysis View for test sequence B(4321)...</a></li>
	</ol>
</section>

<footer>
DNA Analyzer Application &amp; Web Service &copy; 2014-2015, Christopher N. Hartley<br />
In Collaboration with California Polytechnic State University as a Senior Project
</footer>

</body>
</html>