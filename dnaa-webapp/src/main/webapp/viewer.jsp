<%@page errorPage="exceptionpage.jsp" %>
<%@page import="org.json.JSONObject"%>
<%@page import="com.dnaa.common.dbi.DatabaseInterface"%>
<%

final String paramId = request.getParameter("id");
final int sequenceId = paramId != null ? Integer.parseInt(paramId) : -1;
final JSONObject sequenceInfo = DatabaseInterface.getSequenceInfo(sequenceId);

if (sequenceInfo == null) {
	//TODO redirect to page for not found exception in sequence information...
	//response.sendError(HttpServletResponse.SC_NOT_FOUND, "Sequence not found!");
	System.err.println("sequence grabber returned null for " + paramId);
}

%>
<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<% session.setAttribute("pageName", "Sequence Viewer"); %>
<!DOCTYPE html>
<html>
<head>
<%@ include file="./pagetemplate/headbase.jsp" %>
	<link rel="stylesheet" type="text/css"  href="./css/dnaa-viewer.css"></link>
	<script type="text/javascript" src="./js/viewer-lib.js"></script>
	<script type="text/javascript" src="./js/nucleotide-lib.js"></script>
	<script type="text/javascript" src="./js/amino-acid-lib.js"></script>
	<script type="text/javascript">

function dumpSequence(sequence) {
	console.log("sequence={");
    console.log("  id=" + sequence.id);
    console.log("  organism=" + sequence.organism);
    console.log("  isDNA=" + sequence.isDNA);
    console.log("  length=" + sequence.length);
    console.log("  blockBuffer=" + sequence.blockBuffer);
    for (var block = 0; block < sequence.blockBuffer.length; block++) {
        dumpBlockBuffer(sequence.blockBuffer[block]);
    }
    console.log("}");
}
function dumpBlockBuffer(blockBuffer) {
	console.log("    block=" + blockBuffer);
    for (var prop in blockBuffer) {
        console.log("      prop=" + prop + " -> " + blockBuffer[prop]);
    }
}

var viewer1 = {},
    leftSpan = {},
    rightSpan = {};

function init() {
	var viewer1CanvasId = "viewer1",
	    leftSpanId = "leftSpan",
	    rightSpanId = "rightSpan",
	    sequence = Object.create(NucleotideSequence);
	    sequence.init(
			"<%=sequenceInfo.getInt("id") %>",
			"<%=sequenceInfo.getString("organism") %>",
			<%=sequenceInfo.getBoolean("isDNA") ? "true" : "false" %>,
		    <%=sequenceInfo.getLong("length") %>
		);
	
	sequence.get(0); // cause the nucleotide sequence to start caching
		             // at the begining of the sequence.
	dumpSequence(sequence);
	
	view1 = Object.create(Viewer);
	view1.init(viewer1CanvasId);
    view1.sequence = sequence;
    view1.background = Object.create(FloatingBubblesBackground);
    view1.background.init();
    
    leftSpan = document.getElementById(leftSpanId);
    rightSpan = document.getElementById(rightSpanId);
    
    console.log("view1.sequence=[" + view1.sequence.blockBuffer[0] + "]");
    
    $('#viewerTooltipCloseButton').click( function() {
    	$('#viewerTooltip').fadeOut("fast");
    });

    $('#viewerTooltip').hide();
}

//onload events
$(document).ready( function() {
	console.log("document loaded!");
	init();
});

$(window).ready( function () {
	console.log("window loaded!");
	view1.repaint();
	
	AminoAcidLib.load("./xml/aminoacids.xml");//, completedFn, errorFn, statusFn);
});

	</script>
</head>

<body>
<%@ include file="./pagetemplate/header.jsp" %>
<%@ include file="./pagetemplate/nav.jsp" %>

<details>
	<summary>Sequence Information:</summary>
	<p>Some lame long winded description that probably reads like a Dicken's novel</p>
	<ul>
		<li><b>Sequence ID <%=sequenceInfo.getInt("id") %></b></li>
		<li>Organism: <em><%=sequenceInfo.getString("organism") %></em></li>
		<li>Gnome size <%=sequenceInfo.getLong("length") %></li>
		<li><em><%=sequenceInfo.getBoolean("isDNA") ? "DNA (Deoxyr" : "RNA (R" %>ibonucleic Acid)</em></li>
	</ul>
	<div id="sequenceInfoContainer"><em>[No sequence specified]</em></div>
</details>

<canvas id="viewer1" class="viewer"></canvas>
<div>
    <span id="leftSpan"><em>[no data]</em></span>
    <span id="rightSpan"><em>[no data]</em></span>
</div>

<aside id="viewerTooltip">
	<span id="viewerTooltipCloseButton">&times;<!-- &cross; x X --></span>
	<h1 id="viewerTooltipTitle">Some Very Long Drawn-Out Title</h1>
	<div id="viewerTooltipContent"><em>[no information]</em></div>
</aside>

<%@ include file="./pagetemplate/footer.jsp" %>
</body>
</html>