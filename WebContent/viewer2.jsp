<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<%@page import="java.util.HashMap" %>
<%
final String versionCode = "1.01.001a";
final String pageTitlePrefix = "DNAA";

final String paramId = request.getParameter("id");
final int sequenceId = paramId != null ? Integer.parseInt(paramId) : -1;

// TODO Database request for the sequence information based on the id parameter
//result = getDatabaseInterface().getSequenceInfoById(sequenceId);
///// TEMPORARY FOR TESTING ONLY /////
HashMap<String,String> result = new HashMap<String,String>();
result.put("id", paramId);
result.put("organism", "somesequenceone xbactirium");
result.put("length", "90324857");
result.put("isDNA", "true");
///// END TEMPORARY TESTING STUFF /////

final String sequenceOrganism = result.get("organism");
final long sequenceLength = Long.parseLong(result.get("length"));
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta charset="UTF-8">

<title><%=pageTitlePrefix %> - Sequence Viewer</title>

<link rel="shortcut icon" href="./img/favicon.ico" />
<link rel="apple-touch-icon image_src" href="./img/apple-touch-icon.png" />

<link rel="stylesheet" type="text/css" href="./css/defaults.css" />
<link rel="stylesheet" type="text/css" href="./css/main.css" />
<link rel="stylesheet" type="text/css" href="./css/viewer.css" />

<!-- script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.11.1/jquery.min.js"></script -->
<script type="text/javascript" src="./js/lib/jquery.min.js"></script>
<script type="text/javascript" src="./js/nucleotide.utils.js"></script>
<script type="text/javascript" src="./js/viewer.utils.js"></script>
<script type="text/javascript">

//// start virtual database ////
var seqNames = [
        "somesequenceone xbactirium",
        "somesequencetwo ybactirium"
];
var seqs = [];

function getSequenceLength(id) {
	var len = 0;
	for (var i = 0; i < seqs[id].length; i++)
		len += seqs[id][i].length;
	return len;
}
function generateSequence(blocks) {
	var seq = new Array(blocks);
	var org;//, unpacked;
	for (var i = 0; i < blocks; i++) {
		org = generateSequenceBlock();
		//console.log("pre-pack[" + org + "]")
		seq[i] = pack(org);
		//console.log("post-pack[" + seq[i] + "]")
		//unpacked = unpack(seq[i], ['a','c','g','t']);
		//console.log(" -->unpack[" + unpacked + "]");
		//console.log("test=" + org.toLowerCase());
		//console.log("     " + unpacked + " => " + stringsAreEqual(org.toLowerCase(), unpacked));
	}
	return seq;
}
function stringsAreEqual(a,b) {
	if (a.length != b.length)
		return false;
	for (var c = 0; c < a.length; c++)
		if (a[c] != b[c])
			return false;
	return true;
}
function generateSequenceBlock() {
	var dna = ['a','A','c','C','g','G','t','T'];
	//var rna = ['a','A','c','C','g','G','u','U'];
	var len = 64;
	var block = "";
	
	for (var i = 0; i < len; i++) {
		block = block + dna[Math.floor(Math.random() * dna.length)];
	}
	
	return "" + block;
}//*/

//// end virtual database ////


var seqId = -1;
var seqBlocks = [];
var seqBlockIndex = 0;

var viewer = {};

function init() {
	console.log("initializing...");
	
	viewer = new Viewer(document.getElementById("scrollableViewer"));
	leftSpan = document.getElementById("leftSpan");
	rightSpan = document.getElementById("rightSpan");
	
	seqs = [ generateSequence(5), generateSequence(7) ];

	var nucleotideSequence = new NucleotideSequence(
		"<%=result.get("id") %>",
		"<%=result.get("organism") %>",
		4096, 100);
	nucleotideSequence.get(0); // cause the nucleotide sequence to start caching
	                           // at the begining of the sequence.
	viewer.nucleotideSequence = nucleotideSequence;
}


function scrollViewerLeft(e) {
    if (seqBlockIndex > 0) {
    	seqBlockIndex -= 1;
    	viewer.repaint();
    }
}

function scrollViewerRight(e) {
    if (seqBlockIndex < seqBlocks.length - 1) {
    	seqBlockIndex += 1;
    	viewer.repaint();
    }
}



function requestSequence(id) {
	//TODO: load the correct sequence...
	if (seqId != id) {
		seqId = id;
		seqBlocks = seqs[id] || [];
		seqBlockIndex = 0;
		var info = "<table>"
				+ " <tr><td>Organism name:</td><td>" + seqNames[id] + "</td></tr>"
				+ " <tr><td>Gnome length:</td><td>" + getSequenceLength(id) + "</td></tr>"
				+ "</table>\n";
		
		document.getElementById("sequenceInfoContainer").innerHTML = info;
		console.log("loaded sequence " + (id + 1) + ":");
		for (var i = 0; i < seqs[id].length; i++) {
			var unpacked = unpack(seqs[id][i], ['a','c','g','t']);
			console.log("unpack => [" + seqs[id][i] + "] --> [" + unpacked + "]");
			seqs[id][i] = unpacked;
		}
		viewer.repaint();
	}
}

function setTooltip(title, content) {
	var tooltip = document.getElementById("viewerTooltip");
	var tooltipTitle = document.getElementById("viewerTooltipTitle");
	var tooltipContent = document.getElementById("viewerTooltipContent");
	
	if (tooltip === undefined
			|| tooltipTitle === undefined
			|| tooltipContent === undefined)
		return;
	
	if (tooltip.style.display === "none")
		tooltip.style.display = "block";
	
	tooltipTitle.innerHTML = title;
	tooltipContent.innerHTML = content;
}

function hideTooltip() {
	var tooltip = document.getElementById("viewerTooltip");
	if (tooltip !== undefined)
		tooltip.style.display = "none";
}


//onload events
$(document).ready( function() {
	console.log("document loaded");
	init();
});
 
$(window).load( function() {
	console.log("window loaded");	
	//TODO: call to load the initial sequence...
	
	var ttCloseBtn = document.getElementById("viewerTooltipCloseButton");
	if (ttCloseBtn !== undefined)
		ttCloseBtn.addEventListener("click", hideTooltip, true);
	
	setTimeout(requestSequence(0), 500);
});

</script>
</head>
<body>

<header>
	<div id='versionInfo'>Version <%=versionCode %></div>
	<h1><a href='index.jsp'>DNA Analyzer</a></h1>
	<h2>Sequence Viewer</h2>
</header>

<nav>
	<ol>
		<li><a href="index.jsp" alt="Home page">Home</a></li>
		<li><a href="newSeq.jsp" alt="New nucleotide sequence to evaluate">New Sequence</a></li>
		<li><a href="lookup.jsp" alt="Lookup nucleotide sequences, amino acids, and proteins">Lookup</a></li>
		<li><a href="help.jsp" alt="Find help for this website">Help</a></li>
		
		<li><a id="link1" onclick="requestSequence(0);">Short cut [Sequence 1]</a></li>
		<li><a id="link2" onclick="requestSequence(1);">Short cut [Sequence 2]</a></li>
		
		<span>
		<li><a href="register.php" alt="Register a new user">Register</a></li> 
		<li><a href="signin.php" alt="Sign in as an existing user">Sign in</a></li>
		</span>
	</ol>
</nav>

<details>
	<summary>Sequence Information:</summary>
	<p>Some lame long winded description that probably reads like a Dicken's novel</p>
	<ul>
		<li><b>Sequence ID <%=result.get("id") %></b></li>
		<li>Organism: <em><%=result.get("organism") %></em></li>
		<li>Gnome size <%=Long.parseLong(result.get("length")) %></li>
		<li><em><%=(Boolean.parseBoolean(result.get("isDNA")) ? "DNA (Deoxyr" : "RNA (R") %>ibonucleic Acid)</em></li>
	</ul>
	<div id="sequenceInfoContainer"><em>[No sequence specified]</em></div>
</details>

<div id="scrollableViewerContainer">
	<a id="scrollLeftButton" onclick="scrollViewerLeft(event);">&ltrif;</a>
	<a id="scrollRightButton" onclick="scrollViewerRight(event);">&rtrif;</a>
    <canvas id="scrollableViewer"></canvas>
    <div>
		<span id="leftSpan"><em>[no data]</em></span>
		<span id="rightSpan"><em>[no data]</em></span>
	</div>
</div>

<aside id="viewerTooltip">
	<span id="viewerTooltipCloseButton">&times;<!-- &cross; x X --></span>
	<h1 id="viewerTooltipTitle">Some Very Long Drawn-Out Title</h1>
	<div id="viewerTooltipContent"><em>[no information]</em></div>
</aside>

<footer>
DNA Analyzer Application &amp; Web Service &copy; 2014-2015, Christopher N. Hartley<br />
In Collaboration with California Polytechnic State University as a Senior Project
</footer>

</body>
</html>