<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<% session.setAttribute("pageName", "Home - HTML5 &amp; CSS3 Application"); %>
<!DOCTYPE html>
<html>
<head>
<%@ include file="./pagetemplate/headbase.jsp" %>
</head>
<body>
<%@ include file="./pagetemplate/header.jsp" %>
<%@ include file="./pagetemplate/nav.jsp" %>

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

<%@ include file="./pagetemplate/footer.jsp" %>
</body>
</html>