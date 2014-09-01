<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<%

final String serviceServerURL = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();//"http://localhost:8080";
final String seqGrabberServiceURL = serviceServerURL
                                  + "/dnaa/services/sequence-grabber-service/";
final int blockSize = 4095;

// Info from database...
final String organism = "Somecrazylatinname Bactierium";
final Integer organismId = 324789;
final Integer organismSequenceLength = 1000000;

// initial sequence block to load from...
int blockIndex = 100;
int numOfBlocks = organismSequenceLength / blockSize;

%>
<html>
<head>
<title>DNA Analyzer - DNA Expanded View (All-In-One)</title>
<link rel="shortcut icon" href="./img/favicon.ico" />
<link rel="apple-touch-icon image_src" href="./img/apple-touch-icon.png" />

<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">

<link rel="stylesheet" type="text/css" href="./css/dnaView.css" />

<!-- script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.11.1/jquery.min.js"></script -->
<script type="text/javascript" src="./js/lib/jquery.min.js"></script>
<script type="text/javascript" src="./js/nucleotide.utils.js"></script>
<script type="text/javascript" src="./js/lib/compression/zip.js"></script>
<script type="text/javascript">

zip.workerScriptsPath = "./js/";

var serviceServerURL = "<%=serviceServerURL %>";
var seqGrabberServiceURL = "<%=seqGrabberServiceURL %>";
var loadedSequenceBlocks = new Array(7);

var currentBlockIndex = 100;

function prevSequenceBlock() {
	sendBlockRequest(--currentBlockIndex);
}

function nextSequenceBlock() {
	sendBlockRequest(++currentBlockIndex);
}


function xmlDocGetFirstNodeValue(xmlDoc, tagName) {
	var elements = xmlDoc.getElementsByTagName(tagName);
	if (elements.length > 0)
	return elements.length > 0 ? elements[0].childNodes[0].nodeValue : null;
}

function processServiceResponse(data) {
	var ns = new NucleotideSequence(
			xmlDocGetFirstNodeValue(data, "sequence.id") || -1,
			xmlDocGetFirstNodeValue(data, "sequence.organism-name") || "[unknown]",
			1024,
			200
	);
	window.alert("new NucleotideSequence:[" + ns.id + "]");
	var result = "<p>Block ID: <em>" + ns.id + "</em><br />\n"
				+ "<code>" + ns.blockcount + "</code></p>\n";
	console.log(result);
	//document.getElementById("resultDiv").innerHTML = result;
	
	loadedSequenceBlocks[2] = xmlDocGetFirstNodeValue(data, "sequence.block");
	
	paintCanvas();
}
var sequenceId = 0;
function sendBlockRequest(blockIndex) {
	$.get(seqGrabberServiceURL + sequenceId + "," + blockIndex,
		function(data, status) {
			if (status === "success")
				processServiceResponse(data);
			else
				window.alert("Data sequence block was not returned successfully!");
		},
		"xml"
	);
}


// Coordinates...
function cnvs_getCoordinates(e) {
	src = e.target;
	x=e.clientX - src.offsetLeft;
	y=e.clientY - src.offsetTop;
	document.getElementById("xycoordinates").innerHTML = "Coordinates: (" + x + "," + y + ")";
}

function cnvs_clearCoordinates() {
	document.getElementById("xycoordinates").innerHTML = "[out of canvas]";
}


// Painting canvas...
function paintCanvas() {
	console.log("--> paintCanvas()");
	if (typeof canvas !== 'undefined') {
		var t = new Date().getTime();
		_paintCanvas(canvas.getContext("2d"), canvas.width, canvas.height);
		console.log("<-- paintCanvas() completed in " + (new Date().getTime() - t) + "ms");
	}
	else {
		window.alert("Unable to get referrence to the DNA view canvas!");
		init();
	}
}

function _paintCanvas(ctx, w, h) {
	ctx.clearRect(0, 0, w, h);
	ctx.font = "12px 'Courier New',Courier,monospace";
	ctx.lineWidth = 1;

	ctx.moveTo(0,0);
	ctx.lineTo(w, h);
	ctx.strokeStyle = '#555555';
	ctx.stroke();
	
	var b = 12;
	var seq = "" + loadedSequenceBlocks[2];
	var seqWidth = seq.length * (b + 1);

	var x = (w - seqWidth) / 2;
	var y = (h - b) / 2;
	
	drawSequence(seq, ctx, x, y);
}

var unknownFillColor = '#555555';
var unknownLineColor = '#333333';
var nucleotideTextColor = '#111111';

function drawSequence(seq, ctx, x, y) {
	var b = 12;
	
	for (var i = 0; i < seq.length; i++) {
		if (x > -b) {
			ctx.fillStyle = nucleotideColorFill[seq[i]] || unknownFillColor;
			ctx.fillRect(x, y, b, b);
			
			ctx.strokeStyle = nucleotideColorLine[seq[i]] || unknownLineColor;
			ctx.strokeRect(x, y, b, b);
			
			ctx.fillStyle = nucleotideTextColor;
			ctx.fillText(seq[i], x + 2, y + 10);
		}
		x = x + b + 1;
	}
}

function updateSizes() {
	if (typeof canvas === 'undefined')
		return;
	
	if (canvas.width < window.innerWidth) {
        canvas.width = window.innerWidth;
        paintCanvas();
    }
}


// onload events
$(document).ready( function() {
	console.log("document loaded");
	init();
});
 
$(window).load( function() {
	console.log("window loaded");	
	//TODO: call to load the initial sequence...
	
	//setTimeout(paintCanvas, 500);
});

var canvas;

var nucleotideColorLine = new Array();
var nucleotideColorFill = new Array();

function init() {
	if (typeof document === 'undefined')
		window.alert("document is undefined!");
	
	// line color for nucleotides.
	nucleotideColorLine["a"] = nucleotideColorLine["A"] = '#009900';
	nucleotideColorLine["t"] = nucleotideColorLine["T"] = '#990000';
	nucleotideColorLine["u"] = nucleotideColorLine["U"] = '#990033';
	nucleotideColorLine["g"] = nucleotideColorLine["G"] = '#996666';
	nucleotideColorLine["c"] = nucleotideColorLine["C"] = '#333399';
	
	// fill color for nucleotides.
	nucleotideColorFill["a"] = nucleotideColorFill["A"] = '#22BB22';
	nucleotideColorFill["t"] = nucleotideColorFill["T"] = '#BB2222';
	nucleotideColorFill["u"] = nucleotideColorFill["U"] = '#BB2255';
	nucleotideColorFill["g"] = nucleotideColorFill["G"] = '#BB8888';
	nucleotideColorFill["c"] = nucleotideColorFill["C"] = '#5555BB';
	
	canvas = document.getElementById("dnaViewCanvas");
	
	window.addEventListener('resize', updateSizes, false);
	window.addEventListener('orientationchange', updateSizes, false);
	updateSizes();
}

</script>
</head>
<body>

<h1>DNA Analyzer Viewer</h1>
<ol>
<li>Organism: <span class="organismName"><%=organism != null ? organism : "[unknwon]" %></span></li>
<li>Sequence Length: <%=String.format("%,d nucleotides", organismSequenceLength) %>
</ol>

<div id="dnaExpViewDiv"
     onmousemove="cnvs_getCoordinates(event);"
     onmouseout="cnvs_clearCoordinates()">
     
	<span id="xycoordinates"></span>
	
	<a id="dnaViewScrollLeftButton"
	   onclick="prevSequenceBlock();">&ltrif;</a>
	   
	<a id="dnaViewScrollRightButton"
	   onclick="nextSequenceBlock();">&rtrif;</a>
	
	<canvas id="dnaViewCanvas"></canvas>
</div>

<footer>Some footer content</footer>

</body>
</html>